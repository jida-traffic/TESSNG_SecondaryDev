#include "MySimulator.h"

#include <QDebug>
#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "ISignalPhase.h"

MySimulator::MySimulator() {

}

bool MySimulator::calcLampColor(ISignalLamp *pSignalLamp) {
    auto simuiface = gpTessInterface->simuInterface();
    long simuTimeMs = simuiface->simuTimeIntervalWithAcceMutiples();
    long t = simuTimeMs / 1000;
    const long tChange = 8;

    long signalePhaseId = 0;
    if (pSignalLamp && pSignalLamp->signalPhase()) {
        signalePhaseId = pSignalLamp->signalPhase()->id();
    }

    if (mPrevSec != t) {
        mPrevSec = t;
        if (!mOutVehiInfo.isEmpty()) {
            if (mOutVehiInfo.first().avgSpeed < 200) {
                mBusArrivalSec = t;
            }
        }
        mOutVehiInfo.clear();
    }

    if (mBusArrivalSec > (mTg2r - tChange) && mBusArrivalSec < mTg2r) {
        mTg2r += tChange;
        mPhaseChange += tChange;
        mTr2g = 90 + mTg2r;
    } else if (mPrevSec >= mTr2g && mTg2r >= mPrevSec) {
        mTg2r = mTg2r;
        mTr2g = 90 + mTg2r;
    }

    if (mBusArrivalSec > mTr2g - 35 && mBusArrivalSec < mTr2g - tChange) {
        mTr2g = mPrevSec + tChange;
        mPhaseChange = mPrevSec + tChange;
        mTg2r = 30 + mTr2g;
    } else if ((mTg2r < mPrevSec && mPrevSec < mTr2g - 35) || (mPrevSec >= mTr2g - tChange && mPrevSec < mTr2g)) {
        mTr2g = mTr2g;
        mTg2r = 30 + mTr2g;
    }

    long w2GandN2R = (0 + mPhaseChange) % 120;
    long s2GandW2R = (30 + mPhaseChange) % 120;
    long e2GandS2R = (60 + mPhaseChange) % 120;
    long n2GandE2R = (90 + mPhaseChange) % 120;

    if (t == 0) {
        if (signalePhaseId == 418) {
            pSignalLamp->setLampColor("红");
        }
        if (signalePhaseId == 415) {
            pSignalLamp->setLampColor("红");
        }
    }

    if (t % 120 == w2GandN2R - 3) {
        if (signalePhaseId == 417) {
            pSignalLamp->setLampColor("黄");
        }
    } else if (t % 120 == s2GandW2R - 3) {
        if (signalePhaseId == 416) {
            pSignalLamp->setLampColor("黄");
        }
    } else if (t % 120 == e2GandS2R - 3) {
        if (signalePhaseId == 418) {
            pSignalLamp->setLampColor("黄");
        }
    } else if (t % 120 == n2GandE2R - 3) {
        if (signalePhaseId == 415) {
            pSignalLamp->setLampColor("黄");
        }
    }

    if (t % 120 == w2GandN2R) {
        if (signalePhaseId == 416) {
            pSignalLamp->setLampColor("绿");
        }
        if (signalePhaseId == 417) {
            pSignalLamp->setLampColor("红");
        }
    } else if (t % 120 == s2GandW2R) {
        if (signalePhaseId == 418) {
            pSignalLamp->setLampColor("绿");
        }
        if (signalePhaseId == 416) {
            pSignalLamp->setLampColor("红");
        }
    } else if (t % 120 == e2GandS2R) {
        if (signalePhaseId == 415) {
            pSignalLamp->setLampColor("绿");
        }
        if (signalePhaseId == 418) {
            pSignalLamp->setLampColor("红");
        }
    } else if (t % 120 == n2GandE2R) {
        if (signalePhaseId == 417) {
            pSignalLamp->setLampColor("绿");
        }
        if (signalePhaseId == 415) {
            pSignalLamp->setLampColor("红");
        }
    }

    return true;
}

//一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    long batchNum = simuiface->batchNumber();
    long simuTimeMs = simuiface->simuTimeIntervalWithAcceMutiples();

    QList<Online::SignalPhaseColor> lPhoneColor = simuiface->getSignalPhasesColor();
    qDebug() << "Signal phase colors";
    QStringList items;
    for (const auto &pcolor : lPhoneColor) {
        items << QString("(%1, %2, %3, %4, %5)")
                    .arg(pcolor.signalGroupId)
                    .arg(pcolor.phaseNumber)
                    .arg(pcolor.color)
                    .arg(pcolor.mrIntervalSetted)
                    .arg(pcolor.mrIntervalByNow);
    }
    qDebug() << "Signal phase colors" << QString("[") + items.join(", ") + "]";

    QList<Online::VehiInfoCollected> lVehiInfo = simuiface->getVehisInfoCollected();
    if (!lVehiInfo.isEmpty() && lVehiInfo.first().vehiType == 2) {
        mOutVehiInfo.append(lVehiInfo.first());
        QStringList vitems;
        for (const auto &vinfo : lVehiInfo) {
            vitems << QString("(%1, %2)").arg(vinfo.collectorId).arg(vinfo.vehiId);
        }
        qDebug() << "Vehicle info collected:" << QString("[") + vitems.join(", ") + "]";
    }
}

MySimulator::~MySimulator() {
}