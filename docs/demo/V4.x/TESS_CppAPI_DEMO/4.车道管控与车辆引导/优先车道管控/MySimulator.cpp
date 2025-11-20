#include "MySimulator.h"

#include <QDebug>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "ivehicle.h"
#include "ivehicledriving.h"

MySimulator::MySimulator() {

}

void MySimulator::initVehicle(IVehicle *pIVehicle) {
    long tmpId = pIVehicle->id() % 100000;
    if (pIVehicle->vehicleTypeCode() == 13) {
        pIVehicle->setColor("Green");
    }
}

QList<int> MySimulator::calcLimitedLaneNumber(IVehicle *pIVehicle) {
    auto simuiface = gpTessInterface->simuInterface();
    long batchNum = simuiface->batchNumber();
    QList<int> GL_list{0, 1, 2};
    QList<int> ML_list{3};
    qreal dist = pIVehicle->vehicleDriving()->getVehiDrivDistance(); // 米
    qDebug() << "vehicle=" << pIVehicle->id() << " dist=" << dist;

    // 进入管控区域前：普通车在管控车道则右移，EV车保持左移进入管控车道
    if (dist <= 800 && pIVehicle->vehicleTypeCode() != 13 && pIVehicle->vehicleDriving()->laneNumber() == 3) {
        pIVehicle->vehicleDriving()->toRightLane();
        return ML_list;
    }
    if (pIVehicle->vehicleTypeCode() == 13) {
        if (pIVehicle->vehicleDriving()->laneNumber() != 3) {
            pIVehicle->vehicleDriving()->toLeftLane();
        }
        return GL_list;
    }

    // 管控区域内控制
    if (ML2GL) {
        if (pIVehicle->vehicleDriving()->laneNumber() == 3) {
            if (count * 60 < AllowVehsFlow) {
                pIVehicle->setColor("Red");
                count += 1;
                qDebug() << "count:" << count;
                return ML_list;
            } else {
                return QList<int>();
            }
        } else {
            return ML_list;
        }
    } else if (GL2ML) {
        if (count * 60 <= AllowVehsFlow && pIVehicle->vehicleDriving()->laneNumber() == 2 && dist <= 1227) {
            pIVehicle->setColor("Red");
            Cars.append(pIVehicle->id());
            pIVehicle->vehicleDriving()->toLeftLane();
            managedCars.insert(pIVehicle->id(), true);
            count += 1;
            qDebug() << "count:" << count;
            return GL_list;
        } else if (managedCars.contains(pIVehicle->id())) {
            return GL_list;
        } else {
            return ML_list;
        }
    } else {
        return ML_list;
    }
}

// 一个批次计算后的处理
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    long batchNum = simuiface->batchNumber();

    QList<Online::VehiInfoAggregated> lAgg = simuiface->getVehisInfoAggregated();
    if (!lAgg.isEmpty()) {
        for (const auto &vinfo : lAgg) {
            if (vinfo.collectorId == 17) {
                Q_ML = vinfo.vehiCount * 60; // veh/h
            } else if (vinfo.collectorId == 18) {
                Q6 = vinfo.vehiCount * 60; V6 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 19) {
                Q7 = vinfo.vehiCount * 60; V7 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 20) {
                Q8 = vinfo.vehiCount * 60; V8 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 25) {
                V_ML = vinfo.avgSpeed; qDebug() << "speed_ML:" << V_ML;
            } else if (vinfo.collectorId == 24) {
                V_GL1 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 23) {
                V_GL2 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 22) {
                V_GL3 = vinfo.avgSpeed;
            } else if (vinfo.collectorId == 21) {
                V_ML_out = vinfo.avgSpeed;
            }
        }
        Q_GL = (Q6 + Q7 + Q8) / 3.0;
        V_GL = (V6 + V7 + V8) / 3.0;
        qDebug() << "speed_GL:" << V_GL;
        qDebug() << "speed_ML_out:" << V_ML_out;
        qDebug() << "speed_GL_out:" << (V_GL1 + V_GL2 + V_GL3) / 3.0;
    }

    // 2分钟时记录一次
    if (batchNum == (20 * 60 * 2)) {
        MLFlow = Q_ML;
        GLFlow = Q_GL;
    }

    // 每分钟刷新一次流量并清零计数
    if (batchNum % (20 * 60) == 0) {
        MLFlow = Q_ML;
        GLFlow = Q_GL;
        count = 0;
        qDebug() << "管控车道的流量" << MLFlow;
        qDebug() << "普通车道的流量" << GLFlow;
    }

    // 每3分钟计算一次变道信号
    if (batchNum % (20 * 60 * 3) == 0) {
        ML2GL = 0; GL2ML = 0;
        if (V_GL != 0 && MLFlow != 0 && GLFlow != 0) {
            speedDiffRate = (V_ML - V_GL) / V_GL;
            if ((V_ML - V_GL) / V_GL > 0.1) {
                while (MLFlow <= 3600 && GLFlow >= 0) {
                    MLFlow += 90; GLFlow -= 30;
                    MLSpeed = CalcSpeedbyFlow(MLFlow, false, true);
                    GLSpeed = CalcSpeedbyFlow(GLFlow, true, true);
                    tempSpeedDiffRate = (MLSpeed - GLSpeed) / GLSpeed;
                    if (tempSpeedDiffRate <= 0.1) {
                        solutionMLFlow = (int)MLFlow - 90;
                        solutionGLFlow = (int)GLFlow + 30;
                        solutionMLSpeed = CalcSpeedbyFlow(solutionMLFlow, false);
                        solutionGLSpeed = CalcSpeedbyFlow(solutionGLFlow, true);
                        solutionSpeedDiffRate = (solutionMLSpeed - solutionGLSpeed) / solutionGLSpeed;
                        solutionProductivity = solutionMLFlow * solutionMLSpeed + solutionGLFlow * solutionGLSpeed * 3;
                        AllowVehsFlow = qAbs(solutionMLFlow - (int)Q_ML);
                        GL2ML = 1;
                        qDebug() << "需要有" << AllowVehsFlow << "辆车从普通车道进入到管控车道";
                        break;
                    } else {
                        GL2ML = 0;
                    }
                }
            } else {
                while (MLFlow >= 0 && GLFlow <= 3600) {
                    MLFlow -= 30; GLFlow += 10;
                    MLSpeed = CalcSpeedbyFlow(MLFlow, false, true);
                    GLSpeed = CalcSpeedbyFlow(GLFlow, true, true);
                    tempSpeedDiffRate = (MLSpeed - GLSpeed) / GLSpeed;
                    if (tempSpeedDiffRate >= 0.1) {
                        solutionMLFlow = (int)MLFlow;
                        solutionGLFlow = (int)GLFlow;
                        solutionMLSpeed = CalcSpeedbyFlow(solutionMLFlow, false);
                        solutionGLSpeed = CalcSpeedbyFlow(solutionGLFlow, true);
                        solutionSpeedDiffRate = (solutionMLSpeed - solutionGLSpeed) / solutionGLSpeed;
                        solutionProductivity = solutionMLFlow * solutionMLSpeed + solutionGLFlow * solutionGLSpeed * 3;
                        AllowVehsFlow = qAbs(solutionMLFlow - (int)Q_ML);
                        ML2GL = 1;
                        qDebug() << "需要有" << AllowVehsFlow << "辆车从管控车道进入到普通车道";
                        break;
                    } else {
                        ML2GL = 0;
                    }
                }
            }
        }
    }
}

void MySimulator::beforeToLeftFreely(IVehicle *pIVehicle, bool &bKeepOn) {
    if ((double)qrand() / RAND_MAX > 0.3) bKeepOn = false;
    if (pIVehicle->vehicleDriving()->laneNumber() == 2 || pIVehicle->vehicleDriving()->laneNumber() == 3) bKeepOn = true;
}

void MySimulator::beforeToRightFreely(IVehicle *pIVehicle, bool &bKeepOn) {
    if ((double)qrand() / RAND_MAX > 0.3) bKeepOn = false;
}

bool MySimulator::reSetSpeed(IVehicle *pIVehicle, qreal &inOutSpeed) {
    long tmpId = pIVehicle->id();
    if (Cars.contains(tmpId)) {
        inOutSpeed = pIVehicle->vehicleDriving()->desirSpeed();
        Cars.removeOne(tmpId);
        return true;
    }
    return false;
}

qreal MySimulator::SpeedFlowFunction(qreal x, qreal kj, qreal qc, qreal vf, qreal vm) {
    qreal C1 = vf / (kj * vm * vm) * (2.0 * vm - vf);
    qreal C2 = vf / (kj * vm * vm) * (vf - vm) * (vf - vm);
    qreal C3 = (1.0 / qc) - vf / (kj * vm * vm);
    return x / (C1 + C2 / (qAbs(vf - x) + 0.1) + C3 * x);
}

qreal MySimulator::CalcSpeedbyFlow(qreal Y, bool isGL, bool getBigX) {
    qreal Kj = isGL ? Kj_GL : Kj_ML;
    qreal Qc = isGL ? Qc_GL : Qc_ML;
    qreal Vf = isGL ? Vf_GL : Vf_ML;
    qreal Vm = isGL ? Vm_GL : Vm_ML;
    int start = 55, end = 75;
    if (getBigX) {
        for (int x = end; x >= start; --x) {
            qreal y = SpeedFlowFunction(x, Kj, Qc, Vf, Vm);
            if (y >= Y) return x;
        }
    } else {
        for (int x = start; x <= end; ++x) {
            qreal y = SpeedFlowFunction(x, Kj, Qc, Vf, Vm);
            if (y >= Y) return x;
        }
    }
    int topX = 0;
    for (int x = start; x <= end; ++x) {
        if (SpeedFlowFunction(x, Kj, Qc, Vf, Vm) >= SpeedFlowFunction(topX, Kj, Qc, Vf, Vm)) topX = x;
    }
    return topX;
}

MySimulator::~MySimulator() {
}