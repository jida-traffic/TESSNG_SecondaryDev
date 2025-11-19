#include "MyNet.h"
#include <QList>
#include <QMap>
#include <QDebug>
#include "tessinterface.h"
#include "netinterface.h"
#include "UnitChange.h"
#include "IPedestrianRectRegion.h"
#include "IPedestrianRegion.h"
#include "IPedestrianPathPoint.h"
#include "IPedestrianPath.h"
#include "IPedestrianSideWalkRegion.h"
#include "IPedestrianCrossWalkRegion.h"
#include "ISignalPhase.h"
#include "ilink.h"
#include "ICrosswalkSignalLamp.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    setupPedestrianDemo();
}

MyNet::~MyNet(){
}

void MyNet::setupPedestrianDemo()
{
    auto ni = gpTessInterface->netInterface();
    qreal offset = 3;
    QMap<QString, QPair<long, long>> linkPairs;
    linkPairs.insert("西进口", QPair<long, long>(1, 6));
    linkPairs.insert("南进口", QPair<long, long>(5, 2));
    linkPairs.insert("东进口", QPair<long, long>(3, 7));
    linkPairs.insert("北进口", QPair<long, long>(8, 4));

    QList<IPedestrianPathPoint*> pathStartPoints;
    QList<IPedestrianPathPoint*> pathEndPoints;
    QList<IPedestrianCrossWalkRegion*> crossWalks;
    IPedestrianPathPoint* pedestrianStartPoint = nullptr;

    for (auto it = linkPairs.constBegin(); it != linkPairs.constEnd(); ++it) {
        QString direction = it.key();
        QPair<long, long> pair = it.value();
        QList<QPointF> update_vertexs;
        QList<long> ids; ids << pair.first << pair.second;
        for (long linkId : ids) {
            ILink* link = ni->findLink(linkId);
            if (!link) continue;
            QList<QPointF> rightPts = link->rightBreakPoints();
            if (rightPts.isEmpty()) continue;
            QPointF startPoint = rightPts.first();
            QPointF endPoint = rightPts.last();
            qreal delta_x = p2m(endPoint.x()) - p2m(startPoint.x());
            qreal delta_y = p2m(endPoint.y()) - p2m(startPoint.y());
            QList<QPointF> vertexs;
            for (const QPointF& pt : rightPts) {
                vertexs.append(QPointF(p2m(pt.x()), p2m(pt.y())));
            }
            if (qAbs(delta_x) > qAbs(delta_y)) {
                if (delta_x > 0) {
                    for (const QPointF& p : vertexs) update_vertexs.append(QPointF(p.x(), p.y() + offset));
                } else {
                    for (const QPointF& p : vertexs) update_vertexs.append(QPointF(p.x(), p.y() - offset));
                }
            } else {
                if (delta_y > 0) {
                    for (const QPointF& p : vertexs) update_vertexs.append(QPointF(p.x() - offset, p.y()));
                } else {
                    for (const QPointF& p : vertexs) update_vertexs.append(QPointF(p.x() + offset, p.y()));
                }
            }
        }
        if (update_vertexs.size() < 2) {
            qDebug() << "方向" << direction << "人行道顶点不足，跳过创建，点数:" << update_vertexs.size();
            continue;
        }
        IPedestrianSideWalkRegion* sideWalk = ni->createPedestrianSideWalkRegion(update_vertexs);
        QPointF pathStartPoint((update_vertexs[0].x() * 2 + update_vertexs[1].x()) / 3.0,
                               (update_vertexs[0].y() * 2 + update_vertexs[1].y()) / 3.0);
        IPedestrianPathPoint* newStart = ni->createPedestrianPathStartPoint(pathStartPoint);
        if (newStart) pathStartPoints.append(newStart);
        QPointF pathEndPoint((update_vertexs.last().x() * 2 + update_vertexs[update_vertexs.size() - 2].x()) / 3.0,
                             (update_vertexs.last().y() * 2 + update_vertexs[update_vertexs.size() - 2].y()) / 3.0);
        IPedestrianPathPoint* newEnd = ni->createPedestrianPathEndPoint(pathEndPoint);
        if (newEnd) pathEndPoints.append(newEnd);
        if (direction == "西进口") {
            pedestrianStartPoint = newStart;
        }
        qDebug() << "创建" << direction << "人行道" << (sideWalk ? sideWalk->getId() : -1)
                 << ",行人发生点" << (newStart ? newStart->getId() : -1)
                 << ",行人结束点" << (newEnd ? newEnd->getId() : -1);
    }

    QMap<QString, QPair<QPointF, QPointF>> crossWalkPoints;
    crossWalkPoints.insert("西进口", QPair<QPointF, QPointF>(QPointF(-32, -35), QPointF(-32, 2)));
    crossWalkPoints.insert("北进口", QPair<QPointF, QPointF>(QPointF(-21, 10), QPointF(35, 10)));
    crossWalkPoints.insert("南进口", QPair<QPointF, QPointF>(QPointF(-24, -39), QPointF(33, -39)));
    crossWalkPoints.insert("东进口", QPair<QPointF, QPointF>(QPointF(45, -29), QPointF(45, 4)));
    QList<QPointF> pathMiddlePoints;
    for (auto it = crossWalkPoints.constBegin(); it != crossWalkPoints.constEnd(); ++it) {
        QString direction = it.key();
        QPointF startPointCoor = it.value().first;
        QPointF endPointCoor = it.value().second;
        QPointF startPoint(startPointCoor.x(), -startPointCoor.y());
        QPointF endPoint(endPointCoor.x(), -endPointCoor.y());
        QPointF mid((startPointCoor.x() + endPointCoor.x()) / 2.0,
                    -((startPointCoor.y() + endPointCoor.y()) / 2.0));
        IPedestrianCrossWalkRegion* crossWalk = ni->createPedestrianCrossWalkRegion(startPoint, endPoint);
        if (crossWalk) crossWalk->setName(QString("%1人行横道").arg(direction));
        crossWalks.append(crossWalk);
        pathMiddlePoints.append(mid);
        qDebug() << "创建" << direction << "人行横道" << (crossWalk ? crossWalk->getId() : -1);
    }

    IPedestrianPath* straightPath = nullptr;
    IPedestrianPath* leftPath = nullptr;
    if (pathStartPoints.size() >= 4 && pathEndPoints.size() >= 4 && pathMiddlePoints.size() >= 4) {
        straightPath = ni->createPedestrianPath(pathStartPoints[0], pathEndPoints[1], QList<QPointF>() << pathMiddlePoints[2]);
        qDebug() << "创建西进口直行过街路径";
        leftPath = ni->createPedestrianPath(pathStartPoints[0], pathEndPoints[3], QList<QPointF>() << pathMiddlePoints[0]);
        qDebug() << "创建西进口左转过街路径";
        ni->createPedestrianPath(pathStartPoints[1], pathEndPoints[2], QList<QPointF>() << pathMiddlePoints[3]);
        qDebug() << "创建南进口直行过街路径";
        ni->createPedestrianPath(pathStartPoints[2], pathEndPoints[3], QList<QPointF>() << pathMiddlePoints[1]);
        qDebug() << "创建东进口直行过街路径";
        ni->createPedestrianPath(pathStartPoints[3], pathEndPoints[0], QList<QPointF>() << pathMiddlePoints[0]);
        qDebug() << "创建北进口直行过街路径";
    } else {
        qDebug() << "路径创建失败，起止点或中间点数量不足: start" << pathStartPoints.size() << "end" << pathEndPoints.size() << "middle" << pathMiddlePoints.size();
    }

    QMap<QString, int> crossWalkPhaseId; crossWalkPhaseId.insert("西进口", 5); crossWalkPhaseId.insert("东进口", 5); crossWalkPhaseId.insert("南进口", 4); crossWalkPhaseId.insert("北进口", 4);
    ISignalController* trafficController = ni->findSignalControllerById(1);
    for (IPedestrianCrossWalkRegion* crossWalk : crossWalks) {
        if (!crossWalk) continue;
        long crossWalkId = crossWalk->getId();
        QString crossWalkName = crossWalk->getName();
        QString crossWalkDirection = crossWalkName;
        crossWalkDirection.replace("人行横道", "");
        auto pair = crossWalkPoints.value(crossWalkDirection);
        QPointF s = pair.first; QPointF e = pair.second;
        QString name = QString("%1信号灯").arg(crossWalkName);
        bool isPositive = true;
        QPointF pos1((s.x() * 9 + e.x()) / 10.0, -((s.y() * 9 + e.y()) / 10.0));
        auto lampPos = ni->createCrossWalkSignalLamp(trafficController, name, crossWalkId, pos1, isPositive);
        isPositive = false;
        QPointF pos2((s.x() + e.x() * 9) / 10.0, -((s.y() + e.y() * 9) / 10.0));
        auto lampNeg = ni->createCrossWalkSignalLamp(trafficController, name, crossWalkId, pos2, isPositive);
        ISignalPhase* phase = ni->findSignalPhase(crossWalkPhaseId.value(crossWalkDirection));
        if (phase) {
            ni->addCrossWalkSignalPhaseToLamp(phase->id(), lampPos);
            ni->addCrossWalkSignalPhaseToLamp(phase->id(), lampNeg);
        }
        qDebug() << "为" << crossWalkDirection << "人行横道创建双向信号灯" << (lampPos ? lampPos->id() : -1) << "," << (lampNeg ? lampNeg->id() : -1) << "; 并分配相位" << (phase ? phase->id() : -1);
    }

    if (pedestrianStartPoint && straightPath && leftPath) {
        auto configInfo = ni->findPedestrianStartPointConfigInfo(pedestrianStartPoint->getId());
        Online::Pedestrian::PedestrianPathStartPointConfigInfo updateConfig;
        Online::Pedestrian::GenPedestrianInfo gen;
        gen.pedestrianCount = 6;
        gen.timeInterval = configInfo.genPedestrianConfigInfo.isEmpty() ? 600 : configInfo.genPedestrianConfigInfo.first().timeInterval;
        updateConfig.genPedestrianConfigInfo = QList<Online::Pedestrian::GenPedestrianInfo>() << gen;
        updateConfig.id = configInfo.id;
        Online::Pedestrian::PedestrianTrafficDistributionInfo dis;
        dis.timeInterval = configInfo.pedestrianTrafficDistributionConfigInfo.isEmpty() ? 600 : configInfo.pedestrianTrafficDistributionConfigInfo.first().timeInterval;
        dis.trafficRatio.insert((int)(straightPath ? straightPath->getId() : -1), 2);
        dis.trafficRatio.insert((int)(leftPath ? leftPath->getId() : -1), 1);
        updateConfig.pedestrianTrafficDistributionConfigInfo = QList<Online::Pedestrian::PedestrianTrafficDistributionInfo>() << dis;
        bool result = ni->updatePedestrianStartPointConfigInfo(updateConfig);
        qDebug() << "更新西进口行人发生点流量:" << gen.pedestrianCount << "人/" << gen.timeInterval << "秒,result:" << result;
        qDebug() << "更新西进口行人发生点路径分配:" << dis.trafficRatio << ",result:" << result;
    }
}