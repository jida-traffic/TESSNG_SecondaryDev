#pragma once

class IPassengerRegion
{
public:
    virtual ~IPassengerRegion() = default;
    virtual bool isBoardingArea() const = 0; //获取面域是否为上客区域
    virtual void setIsBoardingArea(bool b) = 0; //设置面域是否为上客区域
    virtual bool isAlightingArea() const = 0; //获取面域是否为下客区域
    virtual void setIsAlightingArea(bool b) = 0; //设置面域是否为下客区域
};