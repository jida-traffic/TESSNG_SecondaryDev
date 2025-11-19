#pragma once

/**
 * @author 孙文光
 * @date 2024-12-18
 * @class IPedestrianStairRegion
 * @brief 楼梯接口类
 * 该类可能代表楼梯、扶梯、传送带的任何一种，具体类型根据getStairType返回的值来决定
 * 此类型具有两个首尾衔接区域以连接普通的行人面域，衔接区域可以看作是普通的面域，真正的楼梯主体是getMainQueueRegion返回的区域
 * 楼梯所属的层级以起始层级为准
 *
 */

#include <QGraphicsPathItem>
#include "IPedestrianRegion.h"
#include "tessinterfaces_global.h"

class IHelpModifyPedestrianRegionSizeDotItem;

enum class StairType
{
    escalator = 0, //扶梯
    staircase, //楼梯
    conveyer //传送带
};

class IPedestrianStairRegion : public QGraphicsPathItem, virtual public IPedestrianPathRegionBase
{
public:
    TESSINTERFACES_EXPORT IPedestrianStairRegion(QGraphicsItem* parent = nullptr);
    virtual ~IPedestrianStairRegion() = default;
    virtual qreal getWidth() const = 0; //返回楼梯的宽度, 单位：米
    virtual void setWidth(qreal width) = 0; //设置楼梯的宽度，单位：米
    virtual QPointF getStartPoint() const = 0; //获取起始点，scene场景坐标系下
    virtual QPointF getEndPoint() const = 0; //获取终止点，scene场景坐标系下
    virtual qreal getStartConnectionAreaLength() const = 0; //获取起始衔接区域长度 单位米
    virtual qreal getEndConnectionAreaLength() const = 0; //获取终止衔接区域长度 单位米
    virtual QPointF getStartRegionCenterPoint() const = 0; //获取起始衔接区域中心，scene场景坐标系下
    virtual QPointF getEndRegionCenterPoint() const = 0; //获取终止衔接区域中心，scene场景坐标系下
    virtual QPainterPath getStartSceneRegion() const = 0; //获取起始衔接区域形状，scene场景坐标系下
    virtual QPainterPath getEndSceneRegion() const = 0; //获取终止衔接区域形状，scene场景坐标系下
    virtual QPainterPath getMainStairRegion() const = 0; //获取楼梯主体形状，scene场景坐标系下
    virtual QPainterPath getFullStairRegion() const = 0; //获取楼梯整体形状，scene场景坐标系下
    virtual QPolygonF getStartSceneRegionPolygon() const = 0; //获取起始衔接区域多边形，scene场景坐标系下
    virtual QPolygonF getEndSceneRegionPolygon() const = 0; //获取终止衔接区域多边形，scene场景坐标系下
    virtual QPolygonF getFullStairRegionPolygon() const = 0; //获取楼梯整体多边形，scene场景坐标系下
    virtual QPolygonF getMainStairPolygon() const = 0; //获取楼梯主体多边形，scene场景坐标系下
    virtual StairType getStairType() const = 0; //获取楼梯类型
    virtual void setStairType(StairType type) = 0; //设置楼梯类型
    virtual long getStartLayerId() const = 0; //获取起始层级
    virtual void setStartLayerId(long id) = 0; //设置起始层级
    virtual long getEndLayerId() const = 0; //获取终止层级
    virtual void setEndLayerId(long id) = 0; //设置终止层级
    virtual qreal getTransmissionSpeed() const = 0; //获取传送速度 单位米/秒
    virtual void setTransmissionSpeed(qreal speed) = 0; //设置传送速度 speed单位米/秒
    virtual qreal getHeadroom() const = 0; //获取楼梯净高
    virtual void setHeadroom(qreal headroom) = 0; //设置楼梯净高
    virtual IHelpModifyPedestrianRegionSizeDotItem* getStartControlPoint() const = 0; //获取起点控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getEndControlPoint() const = 0; //获取终点控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getLeftControlPoint() const = 0; //获取左侧控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getRightControlPoint() const = 0; //获取右侧控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getStartConnectionAreaControlPoint() const = 0; //获取起始衔接区域长度控制点
    virtual IHelpModifyPedestrianRegionSizeDotItem* getEndConnectionAreaControlPoint() const = 0; //获取终止衔接区域长度控制点

};