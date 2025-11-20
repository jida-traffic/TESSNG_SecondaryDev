#include "MySimulator.h"

#include <QDebug>
#include <QFile>
#include <QJsonDocument>
#include <QJsonObject>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "UnitChange.h"

MySimulator::MySimulator() {
    QString jsonPath = QString("./JsonData/DecisionPoint3801_FlowRation.json");
    QFile f(jsonPath);
    if (f.open(QIODevice::ReadOnly)) {
        QJsonDocument doc = QJsonDocument::fromJson(f.readAll());
        f.close();
        if (doc.isObject()) {
            QJsonObject root = doc.object();
            QStringList keys = root.keys();
            if (!keys.isEmpty()) {
                mIntervalKey = keys.first();
                QJsonObject flows = root.value(mIntervalKey).toObject();
                for (auto it = flows.begin(); it != flows.end(); ++it) {
                    mRoutingInitFlow.insert(it.key().toLongLong(), it.value().toInt());
                }
            }
        }
    }
    if (mRoutingInitFlow.isEmpty()) {
        mIntervalKey = "1-99999";
        mRoutingInitFlow.insert(3802, 21);
        mRoutingInitFlow.insert(3901, 5);
        mRoutingInitFlow.insert(3902, 5);
        mRoutingInitFlow.insert(3903, 2);
        mRoutingInitFlow.insert(3904, 7);
        mRoutingInitFlow.insert(3905, 6);
        mRoutingInitFlow.insert(3907, 10);
        mRoutingInitFlow.insert(3908, 4);
        mRoutingInitFlow.insert(6109, 20);
        mRoutingInitFlow.insert(6110, 20);
    }
}

QList<Online::DecipointFlowRatioByInterval> MySimulator::calcDynaFlowRatioParameters() {
    QList<Online::DecipointFlowRatioByInterval> result;
    if (mVehiQueueAggregateFlag) {
        long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
        qDebug() << "L21起点路段路口直行车道排队过长，降低直行流量比！" << simuTime;

        Online::DecipointFlowRatioByInterval dfi;
        dfi.deciPointID = 3801;
        QStringList ts = mIntervalKey.split('-');
        dfi.startDateTime = ts.size() > 0 ? ts[0].toLongLong() : 1;
        dfi.endDateTime = ts.size() > 1 ? ts[1].toLongLong() : 99999;

        QList<long> order;
        order << 3802 << 3901 << 3902 << 3903 << 3904 << 3905 << 3907 << 3908 << 6109 << 6110;

        QList<int> updated;
        for (int i = 0; i < order.size(); ++i) {
            long routingId = order[i];
            int base = mRoutingInitFlow.value(routingId, 0);
            int current = base;
            if (i <= 0) {
                current = base + 3;
            } else if (i > 0 && i <= 4) {
                current = base;
            } else if (i > 4 && i < 8) {
                current = base + 3;
            } else {
                current = base < 6 ? base : base - 6;
            }
            dfi.mlRoutingFlowRatio.append(Online::RoutingFlowRatio(routingId, current));
            updated.append(current);
        }

        qreal rightRatio = 0;
        qreal leftRatio = 0;
        qreal straightRatio = 0;
        int sum = 0;
        for (int v : updated) sum += v;
        if (sum > 0) {
            rightRatio = qRound((updated[0] * 1000.0) / sum) / 10.0;
            int leftSum = 0; for (int j = 1; j <= 4; ++j) leftSum += updated[j];
            leftRatio = qRound((leftSum * 1000.0) / sum) / 10.0;
            int straightSum = 0; for (int j = 6; j <= 9; ++j) straightSum += updated[j];
            straightRatio = qRound((straightSum * 1000.0) / sum) / 10.0;
        }

        qDebug() << "L21起点路段路口直行车道排队过长，动态调整左转、右转和直行的流量比！";
        qDebug() << "修改后右转比例为:" << rightRatio;
        qDebug() << "左转比例为:" << leftRatio;
        qDebug() << "直行比例为:" << straightRatio;
        mDecisionPointFlowRatio.append(QList<qreal>() << rightRatio << leftRatio << straightRatio);

        mVehiQueueAggregateFlag = false;
        result.append(dfi);
    }
    return result;
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    long simuTime = gpTessInterface->simuInterface()->simuTimeIntervalWithAcceMutiples();
    QList<Online::VehiQueueAggregated> lVehiQueueAggr = gpTessInterface->simuInterface()->getVehisQueueAggregated();
    if (!lVehiQueueAggr.isEmpty() && simuTime > (300 + 20) * 1000) {
        mAggrIntervals.append(simuTime / 1000.0 - 300);
        for (const auto& vqAggr : lVehiQueueAggr) {
            long id = vqAggr.counterId;
            if (id == 4501 || id == 4502 || id == 4503 || id == 4504) {
                if (!mVehiQueueAggregationDict.contains(id)) {
                    mVehiQueueAggregationDict[id] = QList<qreal>();
                }
                mVehiQueueAggregationDict[id].append(vqAggr.avgQueueLength);
                qDebug() << "车辆排队集计数据：" << vqAggr.counterId << vqAggr.avgQueueLength << vqAggr.maxQueueLength;
                mVehiQueueAggregateFlag = true;
            }
        }
    }
}

void MySimulator::afterStop() {
    qDebug() << mDecisionPointFlowRatio;
    qDebug() << mVehiQueueAggregationDict;
    qDebug() << mAggrIntervals;
}

MySimulator::~MySimulator() {
}