#include "MyNet.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "IDecisionPoint.h"
#include "IRouting.h"
#include "IJunction.h"
#include "Plugin/_datastruct.h"
#include "UnitChange.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    // 若当前路网为空则不进行演示逻辑
    if (gpTessInterface->netInterface()->linkCount() == 0) {
        return;
    }

    // step1：创建节点区域
    QPointF startPoint = QPointF(m2p(-1000), m2p(-40));
    QPointF endPoint   = QPointF(m2p(-800),  m2p(180));
    QString junctionName = QString("newJunction");
    IJunction* pJunction = gpTessInterface->netInterface()->createJunction(startPoint, endPoint, junctionName);
    if (pJunction) {
        qDebug() << "createJunction:" << junctionName << "done!";
    }

    // step2：构建并应用静态路径，然后优化决策位置与车道连接
    gpTessInterface->netInterface()->buildAndApplyPaths(3);
    gpTessInterface->netInterface()->reSetDeciPoint();
    QList<IDecisionPoint*> lDp = gpTessInterface->netInterface()->decisionPoints();
    for (IDecisionPoint* dp : lDp) {
        for (IRouting* r : dp->routings()) {
            gpTessInterface->netInterface()->reSetLaneConnector(r);
        }
    }
    qDebug() << "buildAndApplyPaths done!";

    // step3：设置节点各转向的输入流量
    // 3-1 添加并设置流量时间段
    Online::Junction::FlowTimeInterval interval = gpTessInterface->netInterface()->addFlowTimeInterval();
    long timeId = interval.timeId;
    gpTessInterface->netInterface()->updateFlowTimeInterval(timeId, 0, 3600);

    // 3-2 遍历所有节点并按转向类型赋初始流量
    QHash<QString, long> turnVolumeMap;
    turnVolumeMap.insert(QString("左转"), 400);
    turnVolumeMap.insert(QString("直行"), 1200);
    turnVolumeMap.insert(QString("右转"), 200);
    turnVolumeMap.insert(QString("掉头"), 0);

    QList<IJunction*> junctions = gpTessInterface->netInterface()->getAllJunctions();
    for (IJunction* j : junctions) {
        long junctionId = j->getId();
        QList<Online::Junction::TurnningBaseInfo> turnings = j->getAllTurningInfo();
        for (const auto& t : turnings) {
            long turningId = t.turningId;
            QString turnType = t.strTurnType;
            long inputVolume = turnVolumeMap.value(turnType, 0);
            gpTessInterface->netInterface()->updateFlow(timeId, junctionId, turningId, inputVolume);
        }
    }
    qDebug() << "updateFlow done!";

    // step4：设置算法参数并进行流量计算
    gpTessInterface->netInterface()->updateFlowAlgorithmParams(0.1, 0.15, 4.0, 300);
    QHash<long, QList<Online::Junction::FlowTurning>> result = gpTessInterface->netInterface()->calculateFlows();
    qDebug() << "calculateFlows done!";

    // 打印流量分配结果
    for (auto it = result.constBegin(); it != result.constEnd(); ++it) {
        const QList<Online::Junction::FlowTurning>& turningFlow = it.value();
        for (const auto& i : turningFlow) {
            long junctionId = i.pJunction ? i.pJunction->getId() : -1;
            QString turning = QString("%1-%2").arg(i.turningBaseInfo.strDirection).arg(i.turningBaseInfo.strTurnType);
            long inputVolume = i.inputFlowValue;
            long realVolume = i.realFlow;
            double relativeError = i.relativeError;
            long startTime = i.flowTimeInterval.startTime;
            long endTime   = i.flowTimeInterval.endTime;
            QString msg = QString("%1-%2: {节点:%3, 转向:%4, 输入流量:%5, 分配流量:%6, 相对误差:%7}")
                              .arg(startTime)
                              .arg(endTime)
                              .arg(junctionId)
                              .arg(turning)
                              .arg(inputVolume)
                              .arg(realVolume)
                              .arg(relativeError);
            qDebug().noquote() << msg;
        }
    }
}

MyNet::~MyNet(){
}