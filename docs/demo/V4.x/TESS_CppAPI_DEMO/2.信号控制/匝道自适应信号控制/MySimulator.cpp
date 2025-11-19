#include "MySimulator.h"

#include <QList>
#include <QDebug>
#include <numeric>
#include <cmath>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "isignallamp.h"
#include "ISignalPhase.h"
#include "Plugin/_datastruct.h"

MySimulator::MySimulator() {
}

// 一个批次计算后的处理
void MySimulator::afterOneStep() {
    // TESSNG 顶层接口
    SimuInterface* simuiface = gpTessInterface->simuInterface();
    NetInterface* netiface = gpTessInterface->netInterface();

    // 当前已仿真时间，单位：毫秒
    long simuTime = simuiface->simuTimeIntervalWithAcceMutiples();

    // 获取最近集计时间段内采集器采集的所有车辆集计信息
    QList<Online::VehiInfoAggregated> lVehiInfo = simuiface->getVehisInfoAggregated();

    QList<qreal> lVehiInfoFromPartly; // 上游初始占有率，固定
    QList<qreal> lVehiInfoToPartly;   // 下游占有率，实时每周期60s计算
    qreal O_k_1 = 0;                  // 下游初始饱和平均占有率

    if (!lVehiInfo.isEmpty()) {
        if (simuTime > 90 * 1000 && simuTime < 110 * 1000) {
            for (const auto& vehiInfo : lVehiInfo) {
                long vehiCollectorId = vehiInfo.collectorId;
                if (vehiCollectorId == 10 || vehiCollectorId == 11 || vehiCollectorId == 12) {
                    lVehiInfoFromPartly.append(vehiInfo.occupancy);
                    qDebug() << "上游初始饱和占有率：" << vehiInfo.collectorId << vehiInfo.occupancy;
                }
            }
            if (!lVehiInfoFromPartly.isEmpty()) {
                mOBar = std::accumulate(lVehiInfoFromPartly.begin(), lVehiInfoFromPartly.end(), 0.0) / lVehiInfoFromPartly.size();
                qDebug() << "上游初始饱和平均占有率" << mOBar;
            }
        } else if (simuTime > 110 * 1000) {
            for (const auto& vinfo : lVehiInfo) {
                long vehiCollectorId = vinfo.collectorId;
                if (vehiCollectorId == 7 || vehiCollectorId == 8 || vehiCollectorId == 9) {
                    lVehiInfoToPartly.append(vinfo.occupancy);
                    qDebug() << "下游初始饱和占有率：" << vinfo.collectorId << vinfo.occupancy;
                }
                if (vehiCollectorId == 1) {
                    mVehiCount = vinfo.vehiCount;
                    qDebug() << "采集器1过车数：" << vinfo.vehiCount;
                }
            }
            if (!lVehiInfoToPartly.isEmpty()) {
                O_k_1 = std::accumulate(lVehiInfoToPartly.begin(), lVehiInfoToPartly.end(), 0.0) / lVehiInfoToPartly.size();
                qDebug() << "下游初始饱和平均占有率" << O_k_1;
                lVehiInfoToPartly.clear();

                qDebug() << "vehiCount" << mVehiCount;
                qreal r_k = mVehiCount * 60 + mKr * ((mOBar - O_k_1) / 100.0);
                // T = 饱和车头时距
                qreal T = 3600.0 / 1800.0; // 2
                int calcGreen = static_cast<int>(std::round((r_k / 60.0) * T));
                mGreenTime = calcGreen > 0 ? calcGreen : 0;
                qDebug() << "greenTime" << mGreenTime;
                qDebug() << "r_k" << r_k;

                // 根据id获取信号灯
                ISignalLamp* signalLamp = netiface->findSignalLamp(1);
                qDebug() << signalLamp;
                if (signalLamp) {
                    // 根据信号灯获取信控方案相位
                    ISignalPhase* signalPhase = signalLamp->signalPhase();
                    qDebug() << signalPhase;
                    if (signalPhase) {
                        QList<Online::ColorInterval> color_list; // 按照红灯、绿灯顺序计算
                        color_list.append(Online::ColorInterval(QStringLiteral("红"), 60 - mGreenTime));
                        color_list.append(Online::ColorInterval(QStringLiteral("绿"), mGreenTime));
                        signalPhase->setColorList(color_list);
                    }
                }
            }
        }
    }
}

MySimulator::~MySimulator() {
}