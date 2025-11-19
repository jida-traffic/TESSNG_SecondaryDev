#include "MyNet.h"
#include <QFile>
#include <QJsonDocument>
#include <QJsonObject>
#include <QJsonArray>
#include <QTextStream>

#include "tessinterface.h"
#include "netinterface.h"
#include "ITrafficLight.h"
#include "ILink.h"
#include "ILane.h"
#include "isignallamp.h"
#include "ISignalPlan.h"
#include "ISignalPhase.h"
#include "UnitChange.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    // 读取JSON配置并创建信号机/方案/相位/灯头
    QTextStream out(stdout);

    const QString jsonPath = QString("./light_data.json");
    QFile f(jsonPath);
    if (!f.open(QIODevice::ReadOnly)) {
        out << "无法打开配置文件: " << jsonPath << "\n";
        return;
    }
    const QByteArray data = f.readAll();
    f.close();

    QJsonParseError parseErr;
    const QJsonDocument doc = QJsonDocument::fromJson(data, &parseErr);
    if (parseErr.error != QJsonParseError::NoError || !doc.isObject()) {
        out << "配置解析失败: " << parseErr.errorString() << "\n";
        return;
    }

    auto* netiface = gpTessInterface->netInterface();
    const QJsonObject root = doc.object();

    QMap<int, long> signalControllerIdMap; // 原始ID -> TESS ID
    QMap<int, long> signalPhaseIdMap;      // 原始ID -> TESS ID

    // 创建信号机与方案相位
    const QJsonArray controllers = root.value("signalControllers").toArray();
    for (const auto& cVal : controllers) {
        const QJsonObject cObj = cVal.toObject();
        const int ctrlId = cObj.value("id").toInt();
        const QString ctrlName = cObj.value("name").toString();
        auto* controller = netiface->createSignalController(ctrlName);
        if (!controller) continue;
        signalControllerIdMap[ctrlId] = controller->id();
        out << "create signal controller id=" << controller->id() << "\n";

        const QJsonArray plans = cObj.value("signalPlans").toArray();
        for (const auto& pVal : plans) {
            const QJsonObject pObj = pVal.toObject();
            const int planId = pObj.value("id").toInt();
            const QString planName = pObj.value("name").toString();
            const int cycleTime = pObj.value("cycleTime").toInt();
            const int startTime = pObj.value("startTime").toInt();
            const int endTime = pObj.value("endTime").toInt();
            const int offset = pObj.value("offset").toInt();

            auto* plan = netiface->createSignalPlan(controller, planName, cycleTime, offset, startTime, endTime);
            if (!plan) continue;
            out << "create signal plan id=" << plan->id() << "\n";

            const QJsonArray phases = pObj.value("signalPhases").toArray();
            for (const auto& phVal : phases) {
                const QJsonObject phObj = phVal.toObject();
                const int phaseId = phObj.value("id").toInt();
                const QString phaseName = phObj.value("name").toString();
                const QJsonArray colors = phObj.value("colors").toArray();
                const QJsonArray durations = phObj.value("durations").toArray();

                QList<Online::ColorInterval> lColor;
                const int n = qMin(colors.size(), durations.size());
                for (int i = 0; i < n; ++i) {
                    const QString color = colors.at(i).toString();
                    const int dur = durations.at(i).toInt();
                    lColor.append(Online::ColorInterval(color, dur));
                }
                auto* phase = netiface->createSignalPlanSignalPhase(plan, phaseName, lColor);
                if (!phase) continue;
                signalPhaseIdMap[phaseId] = phase->id();
                out << "create signal phase id=" << phase->id() << "\n";
            }
        }
    }
    out << "\n";

    // 创建信号灯头并绑定相位
    const QJsonArray lamps = root.value("signalLamps").toArray();
    for (const auto& lVal : lamps) {
        const QJsonObject lObj = lVal.toObject();
        const QString lampName = lObj.value("name").toString();
        const int controllerOriginId = lObj.value("signalControllerId").toInt();
        const long controllerTessId = signalControllerIdMap.value(controllerOriginId, -1);
        auto* controller = netiface->findSignalControllerById(controllerTessId);
        if (!controller) continue;

        // 相位ID映射
        QList<long> phaseTessIds;
        const QJsonArray phaseIds = lObj.value("signalPhaseIds").toArray();
        for (const auto& pidVal : phaseIds) {
            const int originPid = pidVal.toInt();
            const long tessPid = signalPhaseIdMap.value(originPid, -1);
            if (tessPid > 0) phaseTessIds.append(tessPid);
        }

        // 对每个路段的每条车道创建灯头
        const QJsonArray linkIds = lObj.value("linkIds").toArray();
        for (const auto& lidVal : linkIds) {
            const long linkId = lidVal.toInt();
            auto* link = netiface->findLink(linkId);
            if (!link) continue;
            const auto lanes = link->lanes();
            for (auto* lane : lanes) {
                const long laneId = lane->id();
                const qreal laneLenM = p2m(lane->length());
                const qreal distPx = m2p(laneLenM - 1);
                auto* lamp = netiface->createTrafficSignalLamp(controller, lampName, laneId, -1, distPx);
                if (!lamp) continue;
                out << "create traffic signal lamp id=" << lamp->id() << "\n";
                for (long tessPid : phaseTessIds) {
                    netiface->addSignalPhaseToLamp((int)tessPid, lamp);
                }
            }
        }
    }
}

MyNet::~MyNet(){
}