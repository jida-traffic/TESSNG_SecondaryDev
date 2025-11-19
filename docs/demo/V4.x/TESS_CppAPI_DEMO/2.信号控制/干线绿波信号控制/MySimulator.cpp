#include "MySimulator.h"
#include "tessinterface.h"
#include "netinterface.h"
#include "simuinterface.h"
#include <QFile>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>

MySimulator::MySimulator(){}
MySimulator::~MySimulator(){}

QList<Online::SignalContralParam> MySimulator::calcDynaSignalContralParameters()
{
    // 读取参数
    QList<Online::SignalContralParam> out;
    QFile f(QString("%1/param.json").arg(QCoreApplication::applicationDirPath()));
    if (!f.open(QIODevice::ReadOnly)) return out;
    QJsonDocument doc = QJsonDocument::fromJson(f.readAll());
    f.close();
    if (!doc.isObject()) return out;
    QJsonObject p = doc.object();

    int N = p["N"].toInt();
    int Cmax = p["Cmax"].toInt();
    int Cmin = p["Cmin"].toInt();
    QJsonArray L = p["outbound_Li"].toArray();
    QJsonArray V = p["outbound_vi"].toArray();
    QJsonArray R = p["outbound_ri"].toArray();
    QJsonArray groups = p["groupIdLst"].toArray();

    // 简化计算：周期取中间值，偏移按累计行程时间归一化
    int C = (Cmax + Cmin) / 2;
    double z = 1.0 / double(C);

    QList<double> t; for (int i = 0; i < N - 1; ++i) t.append(L[i].toDouble() / V[i].toDouble() * z);
    QList<int> offset;
    double acc = 0.0;
    for (int i = 0; i < N; ++i)
    {
        if (i > 0) acc += t[i - 1];
        offset.append(int(std::round(acc * C)) % C);
    }

    for (int i = 0; i < groups.size() && i < N; ++i)
    {
        Online::SignalContralParam scp;
        scp.signalGroupId = groups[i].toInt();
        scp.period = C;
        scp.yellowInterval = 3;
        scp.redInterval = 2;
        scp.crdinatedPhaseNum = 1;
        scp.crdinatedPhaseDiff = offset[i];
        // 简化：用 R[i] 分配协调相位绿时，其余平均
        int coordGreen = int(std::round((1.0 - R[i].toDouble()) * C)) - scp.yellowInterval;
        int otherGreen = (C - coordGreen - scp.yellowInterval - scp.redInterval);
        int phases = 4;
        scp.mlPhaseGreen << coordGreen;
        for (int k = 1; k < phases; ++k) scp.mlPhaseGreen << qMax(5, otherGreen / (phases - 1));
        out << scp;
    }

    return out;
}