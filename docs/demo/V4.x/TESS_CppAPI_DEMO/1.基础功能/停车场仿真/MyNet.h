#ifndef __MyNet__
#define __MyNet__

#include "Plugin/customernet.h"
#include "Plugin/_netitem.h"

class MyNet : public CustomerNet
{
public:
    MyNet();
    ~MyNet() override;

    //==========以下是接口方法重新实现==========
    //加载完路网后的行为
    void afterLoadNet() override;
private:
    void createParkingRegionsAndRouting();
};

#endif