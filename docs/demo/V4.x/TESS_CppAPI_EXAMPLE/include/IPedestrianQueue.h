#pragma once

class IPedestrian;

class IPedestrianQueue
{
public:
    virtual ~IPedestrianQueue() = default;

    virtual void moveQueue(qreal stepSecondTime) = 0; //移动队列
    virtual void addPedestrian(IPedestrian* pedestrian) = 0; //添加行人到队列
    virtual QPointF getEnqueuePos() const = 0; //获取排队点位置 米
    virtual QPointF getStartPos() const = 0; //获取队列起始位置 米
    virtual QPointF getEndPos() const = 0; //获取队列结束位置 米
    virtual QPointF getMiddlePos() const = 0; //获取队列线中间位置 米
    virtual void setQueuePath(const QPainterPath& path) = 0; //设置队列路径
};