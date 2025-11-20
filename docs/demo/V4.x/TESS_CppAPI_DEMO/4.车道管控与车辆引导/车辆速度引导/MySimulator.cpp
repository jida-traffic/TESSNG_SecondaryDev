#include "MySimulator.h"

#include <QDebug>
#include <QFile>
#include <QTextStream>
#include <QtMath>
#include <QDir>

#include "tessinterface.h"
#include "simuinterface.h"
#include "netinterface.h"
#include "UnitChange.h"
#include "ivehicle.h"
#include "ivehicle.h"
#include "ivehicledriving.h"

MySimulator::MySimulator() {
    // 计算每个目标路段的引导点
    GuidancePoints.clear();
    for (int i = 0; i < LinksID.size(); ++i) {
        GuidancePoints.append(QList<qreal>());
        qreal signalHeadPos = SignalHeadsPos[i];
        QList<qreal> &gp = GuidancePoints[i];
        qreal l = signalHeadPos - Guidance_Length - 2.0; // 预留2米停车线
        while (l > 0) {
            gp.append(qRound(l * 100.0) / 100.0);
            l -= Guidance_Length;
        }
    }
}

void MySimulator::beforeStart(bool &keepOn) {
    gpTessInterface->simuInterface()->setAcceMultiples(100);
    keepOn = true;
}

//每步计算后
void MySimulator::afterOneStep() {
    auto simuiface = gpTessInterface->simuInterface();
    auto netiface = gpTessInterface->netInterface();

    long simuTime = simuiface->simuTimeIntervalWithAcceMutiples();
    qreal currentTime = qRound(simuTime / 100.0) / 10.0; // s, 保留1位

    if (simuTime >= SimTime * 1000) {
        emit forStopSimu();
        return;
    }

    QList<IVehicle*> lAllVehi = simuiface->allVehiStarted();
    if (lAllVehi.isEmpty()) return;

    QList<qreal> QL_list = GetQueueLength(lAllVehi, LinksID);

    // 遍历车辆，计算引导速度
    for (IVehicle* vehi : lAllVehi) {
        long vehID = vehi->id() % 100000;
        int vehType = vehi->vehicleTypeCode();
        qreal vehPos = p2m(vehi->vehicleDriving()->currDistanceInRoad());
        qreal vehSpeed = p2m(vehi->currSpeed()); // m/s
        bool isLink = vehi->roadIsLink();
        int vehLink = vehi->roadId();
        if (!isLink) vehLink += 100;
        qreal vehDes_Speed = p2m(vehi->vehicleDriving()->desirSpeed()) * 3.6; // km/h

        // 记录数据
        qreal realPos = GetRealVehPos(vehLink, vehPos);
        datas.append(QList<qreal>{ currentTime, (qreal)vehID, (qreal)vehType, vehSpeed, (qreal)vehLink, vehPos, vehDes_Speed, realPos });

        // 初始路段颜色
        if (vehPos < 5 && vehLink == 1) {
            if (vehType == 2) vehi->setColor("#FFA500"); else vehi->setColor("#FFFFFF");
        }

        if (!Is_Guidance) continue;
        if (vehType == 2) continue; // 不引导普通车
        if (!LinksID.contains(vehLink)) continue;

        qreal signalHeadPos = 0;
        QList<qreal> guidancePoint; int guidancePointCount = 0; qreal QL = 0;
        for (int j = 0; j < LinksID.size(); ++j) {
            if (vehLink == LinksID[j]) {
                signalHeadPos = SignalHeadsPos[j];
                guidancePoint = GuidancePoints[j];
                guidancePointCount = guidancePoint.size();
                QL = QL_list[j];
                break;
            }
        }

        // 已越过信号灯，恢复颜色与期望速度
        if (vehPos > signalHeadPos) {
            vehi->setColor("#FFFFFF");
            Speed_Guidance[vehID] = defaultDesSpeed / 3.6;
            continue;
        }

        // 引导点附近
        for (int j = 0; j < guidancePointCount; ++j) {
            qreal gp = guidancePoint[j];
            if (qAbs(vehPos - gp) < 1.0) { // ±1m
                int rest_GP_Count = j + 1; // 剩余引导次数
                qreal guidanceSpeed = FindOptimalSpeed(vehSpeed, currentTime, rest_GP_Count, vehLink, QL);
                Speed_Guidance[vehID] = guidanceSpeed;

                if (qFuzzyCompare(guidanceSpeed * 3.6, vehDes_Speed)) break;
                if (guidanceSpeed >= vehSpeed || guidanceSpeed * 3.6 >= 80.0) vehi->setColor("#D92626"); else vehi->setColor("#00FF07");
                break;
            }
        }
    }

    // 实时信息输出
    QString qlStr;
    for (int i = 0; i < QL_list.size(); ++i) {
        qlStr.append(QString::number(QL_list[i], 'f', 2));
        if (i < QL_list.size() - 1) qlStr.append(", ");
    }
    QString runInfo = QString("运行车辆数：%1\n仿真时间：%2(秒)\n\n[%3]")
        .arg(gpTessInterface->simuInterface()->allVehiStarted().size())
        .arg(currentTime)
        .arg(qlStr);
    qDebug() << runInfo;
}

bool MySimulator::reCalcdesirSpeed(IVehicle *pIVehicle, qreal &inOutDesirSpeed) {
    long vehID = pIVehicle->id() % 100000;
    if (Speed_Guidance.contains(vehID)) {
        inOutDesirSpeed = m2p(Speed_Guidance[vehID]);
        return true;
    }
    return false;
}

void MySimulator::afterStop() {
    // 保存Data.csv
    QDir().mkpath("./Data");
    QFile f("./Data/Data.csv");
    if (f.open(QIODevice::WriteOnly | QIODevice::Text)) {
        QTextStream out(&f);
        out.setCodec("GBK");
        out << "currentTime,vehID,vehType,vehSpeed,vehLink,vehPos,desSpeed,realVehPos\n";
        for (const auto &row : datas) {
            out << QString::number(row[0]) << "," << QString::number(row[1]) << "," << QString::number(row[2])
                << "," << QString::number(row[3]) << "," << QString::number(row[4]) << "," << QString::number(row[5])
                << "," << QString::number(row[6]) << "," << QString::number(row[7]) << "\n";
        }
        f.close();
    }
    // 统计输出与结果文件
    CookDatas();
}

//================ 工具函数 =================
qreal MySimulator::CalcFU(qreal v, qreal d) {
    if (v > 0) return d * (0.0411 * (0.132 * v + 0.000302 * v * v * v) + 0.4629) / v;
    return 0;
}

qreal MySimulator::CalcAccFU(qreal v0, qreal vt) {
    qreal fu = 0; qreal tt = 0;
    if (v0 < vt) { // 加速
        qreal a = acceleration; qreal t = (vt - v0) / a;
        while (tt < t) { tt += 0.1; qreal v = v0 + a * tt; qreal d = (v + v0) / 2.0 * 0.1; fu += CalcFU((v + v0) / 2.0, d); }
    } else {
        qreal a = deceleration; qreal t = (v0 - vt) / a;
        while (tt < t) { tt += 0.1; qreal v = v0 - a * tt; qreal d = (v + v0) / 2.0 * 0.1; fu += CalcFU((v + v0) / 2.0, d); }
    }
    return fu;
}

qreal MySimulator::CalcStopFU(qreal finalSpeed) {
    return CalcAccFU(0, finalSpeed) * stop_weight;
}

qreal MySimulator::CalcWholeFU(const QList<qreal> &speedList) {
    qreal FU = 0.0; for (qreal v : speedList) { qreal d = v * 0.1; FU += CalcFU(v, d); } return FU;
}

QPair<qreal, QPair<qreal, qreal>> MySimulator::CalcSegmentFU(qreal currentSpeed, qreal targetSpeed, qreal QL) {
    qreal gl = Guidance_Length - QL; qreal finalSpeed = targetSpeed; qreal FU = 999.9; qreal travelTime = 999.9;
    if (currentSpeed > targetSpeed) { // 减速
        qreal t = (currentSpeed - targetSpeed) / deceleration; qreal s = (currentSpeed + targetSpeed) * t / 2;
        if (s <= gl) { travelTime = t + (gl - s) / finalSpeed; qreal fu1 = CalcAccFU(currentSpeed, finalSpeed); qreal fu2 = CalcFU(finalSpeed, gl - s); FU = fu1 + fu2; }
        else { finalSpeed = qSqrt(currentSpeed * currentSpeed - 2 * deceleration * gl); travelTime = (currentSpeed - finalSpeed) / deceleration; FU = CalcAccFU(currentSpeed, finalSpeed); }
    } else if (currentSpeed < targetSpeed) { // 加速
        qreal t = (targetSpeed - currentSpeed) / acceleration; qreal s = (currentSpeed + targetSpeed) * t / 2;
        if (s <= gl) { travelTime = t + (gl - s) / finalSpeed; qreal fu1 = CalcAccFU(currentSpeed, finalSpeed); qreal fu2 = CalcFU(finalSpeed, gl - s); FU = fu1 + fu2; }
        else { finalSpeed = qSqrt(currentSpeed * currentSpeed + 2 * acceleration * gl); travelTime = (finalSpeed - currentSpeed) / acceleration; FU = CalcAccFU(currentSpeed, finalSpeed); }
    } else { travelTime = gl / finalSpeed; FU = CalcFU(finalSpeed, gl); }
    return qMakePair(FU, qMakePair(finalSpeed, travelTime));
}

bool MySimulator::CheckSignalHeadRed(qreal currentTime, qreal t, int linkid) {
    return ((int)(currentTime + t + SignalCycle - SignalHead_offset.value(linkid)) % SignalCycle) <= SignalRed;
}

qreal MySimulator::GetRealVehPos(int linkid, qreal vehpos) {
    if (allLinksID.contains(linkid)) {
        // 预构建累计长度表
        static QMap<int, qreal> links_Length_Added; static bool inited = false; if (!inited) {
            QList<QPair<int, qreal>> links_Length{{1, 921.4},{101,28.2},{2,1912.7},{102,30.8},{3,987.3},{103,85.4},{4,386.7}};
            qreal length = 0; for (auto &it : links_Length) { links_Length_Added[it.first] = length; length += it.second; } inited = true;
        }
        return links_Length_Added.value(linkid) + vehpos;
    }
    return 0;
}

QList<qreal> MySimulator::GetQueueLength(const QList<IVehicle*> &vehs_info, const QList<int> &target_linksIDs) {
    QList<qreal> QL_list; for (int link : target_linksIDs) { int l = 0; for (IVehicle* vehi : vehs_info) {
            qreal vehSpeed_kmh = p2m(vehi->currSpeed()) * 3.6; bool isLink = vehi->roadIsLink(); int vehLink = vehi->roadId(); if (!isLink) vehLink += 100;
            if (vehSpeed_kmh < 7.2 && vehLink == link) l += 1; }
        QL_list.append(l * (4.5 + 1.0) / 4 * 2); }
    return QL_list;
}

qreal MySimulator::FindOptimalSpeed(qreal currentSpeed, qreal currentTime, int guidancePointCount, int linkid, qreal QL) {
    if (guidancePointCount > 3) return defaultDesSpeed / 3.6;
    int ranges = int((toSpeed - fromSpeed) / desGap) + 1; qreal optimalSpeed = defaultDesSpeed / 3.6; qreal minFU = 9999999.99;
    if (guidancePointCount == 1) {
        for (int i = 0; i < ranges; ++i) { qreal targetSpeed = (fromSpeed + i * desGap) / 3.6; auto seg = CalcSegmentFU(currentSpeed, targetSpeed, QL); qreal FU = seg.first; qreal t = seg.second.second; if (CheckSignalHeadRed(currentTime, t, linkid)) FU += CalcStopFU(); if (FU <= minFU) { minFU = FU; optimalSpeed = targetSpeed; } }
        return optimalSpeed;
    } else if (guidancePointCount == 2) {
        for (int i = 0; i < ranges; ++i) { qreal targetSpeed1 = (fromSpeed + i * desGap) / 3.6; auto seg1 = CalcSegmentFU(currentSpeed, targetSpeed1); qreal FU1 = seg1.first; qreal finalSpeed = seg1.second.first; qreal t1 = seg1.second.second; for (int j = 0; j < ranges; ++j) { qreal targetSpeed2 = (fromSpeed + j * desGap) / 3.6; auto seg2 = CalcSegmentFU(finalSpeed, targetSpeed2); qreal FU2 = seg2.first; qreal t2 = seg2.second.second; qreal FU = FU1 + FU2; if (CheckSignalHeadRed(currentTime, t1 + t2, linkid)) FU += CalcStopFU(); if (FU <= minFU) { minFU = FU; optimalSpeed = targetSpeed1; } } }
        return optimalSpeed;
    } else if (guidancePointCount == 3) {
        for (int i = 0; i < ranges; ++i) { qreal targetSpeed1 = (fromSpeed + i * desGap) / 3.6; auto seg1 = CalcSegmentFU(currentSpeed, targetSpeed1); qreal FU1 = seg1.first; qreal finalSpeed1 = seg1.second.first; qreal t1 = seg1.second.second; for (int j = 0; j < ranges; ++j) { qreal targetSpeed2 = (fromSpeed + j * desGap) / 3.6; auto seg2 = CalcSegmentFU(finalSpeed1, targetSpeed2); qreal FU2 = seg2.first; qreal finalSpeed2 = seg2.second.first; qreal t2 = seg2.second.second; for (int k = 0; k < ranges; ++k) { qreal targetSpeed3 = (fromSpeed + k * desGap) / 3.6; auto seg3 = CalcSegmentFU(finalSpeed2, targetSpeed3); qreal FU3 = seg3.first; qreal t3 = seg3.second.second; qreal FU = FU1 + FU2 + FU3; if (CheckSignalHeadRed(currentTime, t1 + t2 + t3, linkid)) FU += CalcStopFU(); if (FU <= minFU) { minFU = FU; optimalSpeed = targetSpeed1; } } } }
        return optimalSpeed;
    }
    return defaultDesSpeed / 3.6;
}

void MySimulator::CookDatas() {
    // 读取生成的Data.csv并做简要统计，输出结果到两个CSV
    QFile inFile("./Data/Data.csv"); if (!inFile.open(QIODevice::ReadOnly | QIODevice::Text)) return; QTextStream in(&inFile); in.setCodec("GBK");
    QList<QList<qreal>> rows; QString header = in.readLine(); while (!in.atEnd()) { QString line = in.readLine(); auto parts = line.split(','); if (parts.size() != 8) continue; QList<qreal> r; for (const auto &p : parts) r.append(p.toDouble()); rows.append(r); }
    inFile.close();

    // 统计每辆车
    QMap<int, QList<QList<qreal>>> perVeh; for (const auto &r : rows) { int vehId = (int)r[1]; perVeh[vehId].append(r); }
    QList<QList<qreal>> outputALL; QList<QList<qreal>> outputCV;
    qreal total_fu_ALL = 0, total_fu_stop_ALL = 0, total_tt_ALL = 0; int total_stop_ALL = 0;
    qreal total_fu_CV = 0, total_fu_stop_CV = 0, total_tt_CV = 0; int total_stop_CV = 0;

    auto processVeh = [&](int vehId, const QList<QList<qreal>> &vr, bool isCV) {
        // 是否走完全程（存在vehLink==4）
        bool finished = false; for (const auto &row : vr) { if ((int)row[4] == 4) { finished = true; break; } }
        if (!finished) return;
        // 排除初始路段 vehLink>=5
        QList<qreal> speedList; qreal tmin = 1e9, tmax = -1e9; int stopTimes = 0;
        for (const auto &row : vr) {
            int vehLink = (int)row[4]; if (vehLink >= 5) continue; qreal ct = row[0]; tmin = qMin(tmin, ct); tmax = qMax(tmax, ct); speedList.append(row[3]); }
        qreal travelTime = tmax - tmin; qreal fu = CalcWholeFU(speedList);
        // 简化统计停车次数：在信号灯路段上速度最小<0.1
        QList<int> targetLinks{1,2,3}; for (int link : targetLinks) { qreal minv = 1e9; for (const auto &row : vr) { if ((int)row[4] == link) minv = qMin(minv, row[3]); } if (minv < 0.1) stopTimes++; }
        qreal fu_with_stop = fu + stopTimes * (stop_weight - 1.0) * CalcStopFU();
        QList<qreal> out{ (qreal)vehId, fu, fu_with_stop, travelTime, (qreal)stopTimes };
        if (isCV) { outputCV.append(out); total_fu_CV += fu; total_fu_stop_CV += fu_with_stop; total_tt_CV += travelTime; total_stop_CV += stopTimes; }
        else { outputALL.append(out); total_fu_ALL += fu; total_fu_stop_ALL += fu_with_stop; total_tt_ALL += travelTime; total_stop_ALL += stopTimes; }
    };

    for (auto it = perVeh.begin(); it != perVeh.end(); ++it) {
        int vehId = it.key(); const auto &vr = it.value(); int vehType = 2; for (const auto &row : vr) { vehType = (int)row[2]; break; }
        if (vehType == 1) processVeh(vehId, vr, true); // CV
        processVeh(vehId, vr, false); // ALL
    }

    // 写CSV
    auto writeCsv = [](const QString &path, const QList<QList<qreal>> &rows) {
        QFile f(path); if (!f.open(QIODevice::WriteOnly | QIODevice::Text)) return; QTextStream out(&f); out << "vehID,FU,FUwithStop,travelTime,stopTimes\n"; for (const auto &r : rows) { out << r[0] << "," << r[1] << "," << r[2] << "," << r[3] << "," << r[4] << "\n"; } f.close(); };
    // 执行目录下的Data文件夹，具体实施时可由用户自由更改
    writeCsv("./Data/result_ALL.csv", outputALL);
    writeCsv("./Data/result_CV.csv", outputCV);

    if (!outputALL.isEmpty()) {
        qDebug() << "统计车辆数：" << outputALL.size();
        qDebug() << "车均油耗：" << (total_fu_ALL / outputALL.size()) << "ml";
        qDebug() << "车均油耗（含停车惩罚）：" << (total_fu_stop_ALL / outputALL.size()) << "ml";
        qDebug() << "车均行程时间：" << (total_tt_ALL / outputALL.size()) << "s";
        qDebug() << "车均停车次数：" << (total_stop_ALL / outputALL.size());
        qDebug() << "";
    }
    if (!outputCV.isEmpty()) {
        qDebug() << "CV统计车辆数：" << outputCV.size();
        qDebug() << "CV车均油耗：" << (total_fu_CV / outputCV.size()) << "ml";
        qDebug() << "CV车均油耗（含停车惩罚）：" << (total_fu_stop_CV / outputCV.size()) << "ml";
        qDebug() << "CV车均行程时间：" << (total_tt_CV / outputCV.size()) << "s";
        qDebug() << "CV车均停车次数：" << (total_stop_CV / outputCV.size());
    }
}

MySimulator::~MySimulator() {
}