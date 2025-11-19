#pragma once

#include "IPedestrianRegion.h"

#include <QGraphicsPathItem>

class ICrosswalkSignalLamp;
class IHelpModifyPedestrianRegionSizeDotItem;

class IPedestrianCrossWalkRegion :public QGraphicsPathItem, virtual public IPedestrianRegion
{
public:
    virtual ~IPedestrianCrossWalkRegion() = default;
    virtual qreal getWidth() const = 0; //返回人行横道的宽度, 单位：米
    virtual void setWidth(qreal width) = 0; //设置人行横道的宽度，单位：米
    virtual QLineF getSceneLine(UnitOfMeasure unit = UnitOfMeasure::Default) const = 0; //返回人行横道起点到终点的线段，场景坐标系下, 默认单位为像素
    virtual qreal getAngle() const = 0; //返回人行横道倾斜角度，单位：度，人行横道自身坐标系下，以垂直于起终点连线为0度，大于0度向左倾斜，小于0度向右倾斜
    virtual void setAngle(qreal angle) = 0; //设置人行横道倾斜角度，单位：度，人行横道自身坐标系下，以垂直于起终点连线为0度，大于0度向左倾斜，小于0度向右倾斜
    virtual qreal getRedLightSpeedFactor() const = 0; //返回红灯清尾速度系数
    virtual void setRedLightSpeedFactor(qreal factor) = 0; //设置红灯清尾速度系数
    virtual QVector2D getUnitDirectionFromStartToEnd() const = 0; //返回场景坐标系下从起点到终点的单位方向向量
    virtual QVector2D getLocalUnitDirectionFromStartToEnd() const = 0; //返回人行横道本身坐标系下从起点到终点的单位方向向量
    virtual IHelpModifyPedestrianRegionSizeDotItem* getStartControlPoint() const = 0; //获取起点控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getEndControlPoint() const = 0; //获取终点控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getLeftControlPoint() const = 0; //获取左侧控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getRightControlPoint() const = 0; //获取右侧控制点
    virtual ICrosswalkSignalLamp* getPositiveDirectionSignalLamp() const = 0; //获取管控正向通行的信号灯, 是当前面域的子项
    virtual ICrosswalkSignalLamp* getNegativeDirectionSignalLamp() const = 0; //获取管控反向通行的信号灯, 是当前面域的子项
    virtual bool isPositiveTrafficLightAdded() const = 0; //返回是否添加了管控正向通行的信号灯
    virtual bool isReverseTrafficLightAdded() const = 0; //返回是否添加了管控反向通行的信号灯
    virtual QList<QPolygonF> getWhiteBlockPolygons() const = 0; //获取斑马线白色区块的多边形, 返回Qt场景坐标系下的一组多边形，米制单位
};