#pragma once

#include "tessinterfaces_global.h"

class IPedestrianRegion;
class IPedestrianQueue;

class IPedestrian
{
public:
    virtual ~IPedestrian() = default;
    //行人id
    virtual long getId() const = 0;
    //半径 单位：米 m
    virtual qreal getRadius() const = 0;
    //质量 单位：千克 kg
    virtual qreal getWeight() const = 0;
    //颜色：RGB，举例："#EE0000"
    virtual QString getColor() const = 0;
    //当前点位 单位：米 m
    virtual QPointF getPos() const = 0;
    //当前角度 Qt坐标系下，x轴正方向为0，逆时针为正
    virtual qreal getAngle() const = 0;
    //当前二维方向向量
    virtual QVector2D getDirection() const = 0;
    //当前高程 单位：米 m
    virtual qreal getElevation() const = 0;
    //当前速度 单位：米 m/s
    virtual QVector2D getSpeed() const = 0;
    //期望速度 单位：米 m/s
    virtual qreal getDesiredSpeed() const = 0;
    //最大速度限制 单位：米 m/s
    virtual qreal getMaxSpeed() const = 0;
    //当前加速度 单位：米 m/s^2
    virtual QVector2D getAcce() const = 0;
    //最大加速度限制 单位：米 m/s^2
    virtual qreal getMaxAcce() const = 0;
    //欧拉角
    virtual QVector3D getEuler() const = 0;
    //速度欧拉角
    virtual QVector3D getSpeedEuler() const = 0;
    //墙壁方向单位向量
    virtual QVector2D getWallFDirection() const = 0;
    //当前所在面域
    virtual IPedestrianRegion* getRegion() const = 0;
    //行人类型id
    virtual long getPedestrianTypeId() const = 0;
    //设置行人轨迹
    virtual void setPedestrianTrajectory(QPainterPath path) = 0;
    //移动到指定位置 pos: 位置 单位米，goalPoint: 朝向目标点 米
    virtual void moveTo(QPointF pos, QPointF goalPoint) = 0;
    //设置临时目标位置 pos: 位置 单位米
    virtual void setTempTargetPos(QPointF pos) = 0;
    //获取目标距离 单位：米 m
    virtual qreal getTargetDist() const = 0;
    //获取队列通过状态 0表示还未接近排队点，1表示到达排队点，2正在向排队点移动，3表示正在排队
    virtual int getQueuePassStatus() const = 0;
    //设置队列通过状态
    virtual void setQueuePassStatus(int status) = 0;
    //获取当前正在通过的队列
    virtual IPedestrianQueue* getCurQueue() const = 0;
    //设置当前正在通过的队列
    virtual void setCurQueue(IPedestrianQueue* queue) = 0;
    //停止仿真，会在下一个仿真批次移除当前行人，释放资源
    virtual void stop() = 0;
};