#pragma once

class IPedestrianPathPoint;

class IPedestrianPath
{
public:
    virtual ~IPedestrianPath() = default;
    virtual long getId() const = 0; //获取行人路径ID
    virtual IPedestrianPathPoint* getPathStartPoint() const = 0; //获取行人路径起始点
    virtual IPedestrianPathPoint* getPathEndPoint() const = 0; //获取行人路径终点
    virtual QList<IPedestrianPathPoint*> getPathMiddlePoints() const = 0; //获取行人路径中间点
    virtual bool isLocalPath() const = 0; //是否是局部路径
};