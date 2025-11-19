#include "MySimulator.h"
#include <QtMath>
#include <algorithm>
#include <QDebug>
#include <QTextStream>

#include "tessinterface.h"
#include "netinterface.h"
#include "simuinterface.h"
#include "ISignalPlan.h"
#include "ISignalPhase.h"
#include "Plugin/_datastruct.h"

MySimulator::MySimulator() {

}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    // 获取接口
    auto* simuiface = gpTessInterface->simuInterface();
    auto* netiface = gpTessInterface->netInterface();

    // 当前已仿真时间(毫秒)
    long simuTime = simuiface->simuTimeIntervalWithAcceMutiples();

    if (simuTime / (mAggregateTime * 1000) > 1 && simuTime % (mAggregateTime * 1000) == 1000) {
        QTextStream out(stdout);
        out.setCodec("GBK");
        out << "simuTime: " << simuTime << "\n";

        // 采集器集计信息 -> 每个采集器小时流量
        QMap<long, double> collectorFlow;
        const auto lAgg = simuiface->getVehisInfoAggregated();
        for (const auto& item : lAgg) {
            const long collectorId = item.collectorId;
            const int vehCount = item.vehiCount;
            const long aggInterval = item.toTime - item.fromTime; // 秒
            const double flowPerHour = aggInterval > 0 ? (vehCount * 3600.0 / aggInterval) : 0.0;
            collectorFlow[collectorId] = flowPerHour;
        }

        // 计算每相位单车道流量(取相位内采集器平均值)
        QMap<long, double> phaseFlow;
        for (auto it = mPhase2Collector.constBegin(); it != mPhase2Collector.constEnd(); ++it) {
            const long phaseId = it.key();
            const QList<long>& collectors = it.value();
            double sum = 0.0;
            int cnt = 0;
            for (long cid : collectors) {
                if (collectorFlow.contains(cid)) {
                    sum += collectorFlow[cid];
                    cnt++;
                }
            }
            phaseFlow[phaseId] = cnt > 0 ? (sum / cnt) : 0.0;
        }

        // 打印phase_flow_dict
        out << "{";
        bool first = true;
        for (auto it = phaseFlow.constBegin(); it != phaseFlow.constEnd(); ++it) {
            if (!first) out << ", ";
            first = false;
            out << it.key() << ": " << QString::number(it.value(), 'f', 2);
        }
        out << "}" << "\n\n";

        // 打印collector_flow_dict
        out << "{";
        first = true;
        for (auto it = collectorFlow.constBegin(); it != collectorFlow.constEnd(); ++it) {
            if (!first) out << ", ";
            first = false;
            out << it.key() << ": " << QString::number(it.value(), 'f', 2);
        }
        out << "}" << "\n";

        // 获取信控方案并解析当前各相位灯色时长
        ISignalPlan* plan = netiface->findSignalPlanById(201);
        if (!plan) return;

        const int cycleTime = plan->cycleTime();
        QMap<long, QMap<QString, int>> phaseInterval; // phaseId -> {start_time, green_interval, yellow_interval, all_red_interval}
        const auto phases = plan->phases();
        for (auto* phase : phases) {
            int startTime = 0;
            int greenInterval = 0;
            int yellowInterval = 0;
            // 遍历灯色序列
            const auto colors = phase->listColor();
            for (const auto& ci : colors) {
                if (ci.color == QStringLiteral("红")) {
                    startTime += ci.interval;
                } else if (ci.color == QStringLiteral("绿")) {
                    greenInterval = ci.interval;
                } else if (ci.color == QStringLiteral("黄")) {
                    yellowInterval = ci.interval;
                }
            }
            QMap<QString, int> one;
            one[QStringLiteral("phase_id")] = static_cast<int>(phase->id());
            one[QStringLiteral("start_time")] = startTime;
            one[QStringLiteral("green_interval")] = greenInterval;
            one[QStringLiteral("yellow_interval")] = yellowInterval;
            one[QStringLiteral("all_red_interval")] = 0;
            phaseInterval[phase->id()] = one;
        }

        // 计算韦伯斯特配时
        int newPeriod = cycleTime;
        QMap<long, QMap<QString, int>> newPhaseInterval;
        webster(cycleTime, phaseInterval, phaseFlow, newPeriod, newPhaseInterval);

        // 打印new_period_time与new_phase_interval_dict
        out << newPeriod << ", {";
        first = true;
        for (auto it = newPhaseInterval.constBegin(); it != newPhaseInterval.constEnd(); ++it) {
            if (!first) out << ", ";
            first = false;
            const auto& mp = it.value();
            out << it.key() << ": {phase_id: " << mp.value("phase_id")
                << ", start_time: " << mp.value("start_time")
                << ", green_interval: " << mp.value("green_interval")
                << ", yellow_interval: " << mp.value("yellow_interval")
                << ", all_red_interval: " << mp.value("all_red_interval") << "}";
        }
        out << "}" << "\n";

        // 应用到仿真中的信控方案
        plan->setCycleTime(newPeriod);
        for (auto* phase : phases) {
            const auto np = newPhaseInterval[phase->id()];
            const int st = np.value("start_time");
            const int g = np.value("green_interval");
            const int y = np.value("yellow_interval");
            const int r2 = std::max(newPeriod - st - g - y, 0);

            QList<Online::ColorInterval> newColors;
            newColors.append(Online::ColorInterval(QStringLiteral("红"), st));
            newColors.append(Online::ColorInterval(QStringLiteral("绿"), g));
            newColors.append(Online::ColorInterval(QStringLiteral("黄"), y));
            newColors.append(Online::ColorInterval(QStringLiteral("红"), r2));
            phase->setColorList(newColors);
        }
        out << "set new signal group successfully" << "\n";
    }
}

MySimulator::~MySimulator() {
}

// 计算韦伯斯特配时
void MySimulator::webster(int periodTime,
                          const QMap<long, QMap<QString, int>>& phaseInterval,
                          const QMap<long, double>& phaseFlow,
                          int& outNewPeriod,
                          QMap<long, QMap<QString, int>>& outNewPhaseInterval) {
    const int k = phaseInterval.size();
    // 全红时间与黄灯时间
    int L_allred = 0;
    int L_yellow = 0;
    for (auto it = phaseInterval.constBegin(); it != phaseInterval.constEnd(); ++it) {
        const auto& v = it.value();
        L_allred += v.value(QStringLiteral("all_red_interval"));
        L_yellow += v.value(QStringLiteral("yellow_interval"));
    }
    const int L = L_allred + L_yellow;

    // 流量比
    double totalY = 0.0;
    QList<long> keys = phaseInterval.keys();
    QMap<long, double> ymap;
    for (long pid : keys) {
        const double flow = phaseFlow.contains(pid) ? phaseFlow[pid] : 0.0;
        const double y = flow / mCapacity;
        ymap[pid] = y;
        totalY += y;
    }

    // 周期
    const int c0 = periodTime;
    int c = static_cast<int>(std::ceil(L / std::max(1e-6, (1 - totalY))));
    c = std::min(std::max(c, static_cast<int>(c0 * 0.5)), static_cast<int>(c0 * 2));
    outNewPeriod = c;

    // 总有效绿灯时间
    const int Ge = c - static_cast<int>(std::floor(static_cast<double>(L)));

    // 有效绿灯时间分配
    QMap<long, int> gmap;
    int sumg = 0;
    for (int i = 0; i < k; ++i) {
        long pid = keys[i];
        int gi = static_cast<int>(std::floor((ymap[pid] / std::max(1e-6, totalY)) * Ge));
        gmap[pid] = gi;
        sumg += gi;
    }
    // 调整保证总和等于Ge
    if (k > 0) {
        long lastPid = keys.last();
        gmap[lastPid] += Ge - sumg;
    }

    // 新的相位灯色
    outNewPhaseInterval.clear();
    for (int i = 0; i < k; ++i) {
        long pid = keys[i];
        int startSumG = 0;
        int startSumY = 0;
        for (int j = 0; j < i; ++j) {
            long pj = keys[j];
            startSumG += gmap[pj];
            startSumY += phaseInterval[pj].value(QStringLiteral("yellow_interval"));
        }
        QMap<QString, int> m;
        m[QStringLiteral("phase_id")] = static_cast<int>(pid);
        m[QStringLiteral("start_time")] = startSumG + startSumY;
        m[QStringLiteral("green_interval")] = gmap[pid];
        m[QStringLiteral("yellow_interval")] = phaseInterval[pid].value(QStringLiteral("yellow_interval"));
        m[QStringLiteral("all_red_interval")] = phaseInterval[pid].value(QStringLiteral("all_red_interval"));
        outNewPhaseInterval[pid] = m;
    }
}