#pragma once

#include <QObject>
#include <QGraphicsEllipseItem>

class IHelpModifyPedestrianRegionSizeDotItem : public QObject, public QGraphicsEllipseItem
{
public:
    virtual ~IHelpModifyPedestrianRegionSizeDotItem() = default;

    // 设置位置, pos: 坐标点，所属面域的局部坐标系坐标
    virtual void setPosition(QPointF pos) = 0;

    // 获取位置, 返回坐标点
    // useSceneCoords 标识是否返回基于场景坐标系的坐标，
    // 默认返回所属面域的局部坐标系坐标
    virtual QPointF getPosition(bool useSceneCoords = false) = 0;
};