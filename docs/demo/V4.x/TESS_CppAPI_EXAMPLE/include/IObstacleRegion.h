#pragma once

class IObstacleRegion
{
public:
    virtual ~IObstacleRegion() = default;
    virtual bool isObstacle() const = 0; //获取面域是否为障碍物
    virtual void setObstacle(bool b) = 0; //设置面域是否为障碍物
};