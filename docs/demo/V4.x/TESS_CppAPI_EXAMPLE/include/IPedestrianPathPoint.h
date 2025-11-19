#pragma once

/**
 * @brief 行人路径点
 * 通常作为面域的子项使用，位置坐标为所属面域坐标系下的点位
 * @author 孙文光
 */

#include <QGraphicsPathItem>

#include "tessinterfaces_global.h"
#include "UnitChange.h"

class TESSINTERFACES_EXPORT IPedestrianPathPoint: public QGraphicsPathItem
{
public:
    IPedestrianPathPoint(QGraphicsItem* parent = nullptr);
    virtual ~IPedestrianPathPoint() = default;
    virtual long getId() const = 0; //获取行人路径点ID
    virtual QPointF getScenePos(UnitOfMeasure unit = UnitOfMeasure::Default) const = 0; //获取行人路径点场景坐标系下的位置
    virtual qreal getRadius() const = 0; //获取行人路径点的半径 单位：米
    virtual bool verifyNewPosition() = 0; //验证新位置是否合法, 可能会导致面域切换
};