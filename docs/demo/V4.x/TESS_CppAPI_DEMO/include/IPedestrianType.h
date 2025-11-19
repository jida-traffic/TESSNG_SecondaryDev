#pragma once

class IPedestrianType
{
public:
    long pedestrianTypeCode; //行人类型代码
    QString pedestrianTypeName; //行人类型名称
    qreal radius; //行人半径
    qreal radiusStandardDeviation; //行人半径标准差
    qreal desiredSpeed; //行人期望速度
    qreal desiredSpeedStandardDeviation; //行人期望速度标准差
    qreal weight; //行人重量
    qreal weightStandardDeviation; //行人重量标准差
};