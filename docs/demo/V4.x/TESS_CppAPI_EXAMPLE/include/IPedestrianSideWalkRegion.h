#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsPathItem>

class IHelpModifyPedestrianRegionSizeDotItem;

class IPedestrianSideWalkRegion :public QGraphicsPathItem, virtual public IPedestrianRegion
{
public:
    virtual ~IPedestrianSideWalkRegion() = default;
    virtual qreal getWidth() const = 0; //获取人行道宽度
    virtual void setWidth(qreal width) = 0; //设置人行道宽度
    virtual QList<IHelpModifyPedestrianRegionSizeDotItem*> getVetexs() const = 0; //获取人行道顶点，即初始折线顶点, 是当前面域的子项
    virtual QList<IHelpModifyPedestrianRegionSizeDotItem*> getControl1Vetexs() const = 0; //获取人行道贝塞尔曲线控制点P1, 是当前面域的子项
    virtual QList<IHelpModifyPedestrianRegionSizeDotItem*> getControl2Vetexs() const = 0; //获取人行道贝塞尔曲线控制点P2, 是当前面域的子项
    virtual QList<IHelpModifyPedestrianRegionSizeDotItem*> getCandidateVetexs() const = 0; //获取候选顶点, 是当前面域的子项
    virtual void removeVetex(int index) = 0; //删除第index个顶点
    virtual void insertVetex(QPointF pos, int index) = 0; //在第index个位置插入顶点，初始位置为pos
};