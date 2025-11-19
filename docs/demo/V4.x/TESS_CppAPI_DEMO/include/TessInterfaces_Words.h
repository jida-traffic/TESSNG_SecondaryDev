#ifndef __TessInterfaces_Words__
#define __TessInterfaces_Words__
#include <QObject>
class TessInterfaces_Words{
public:
    QString w_ILane_C_1_w = QObject::tr("机动车道");
    QString w_ivehicle_C_1_w = QObject::tr("小客车");
    QString w_tessinterface_C_1_w = QObject::tr("创建工厂方法指针失败");
    QString w_tessinterface_C_2_w = QObject::tr("加载动态库失败");
};

TessInterfaces_Words& _Get_TessInterfaces_Words_Instance();
#define ___TessInterfaces_Words _Get_TessInterfaces_Words_Instance()

#endif