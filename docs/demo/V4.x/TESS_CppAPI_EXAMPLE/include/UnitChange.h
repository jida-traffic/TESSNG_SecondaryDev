/********************************************************************************
* 度量单位接口，此接口由TESS NG实现，用户可以通过此接口设置度量单位、进行不同度量单位之间数据转换
*********************************************************************************/

#ifndef __UnitChange__
#define __UnitChange__

#include "tessinterfaces_global.h"

#include <QObject>
#include <QPointF>
#include <QMainWindow>

//度量单位
enum TESSINTERFACES_EXPORT UnitOfMeasure { Default, Pixel, Metric };

// extern bool TESSINTERFACES_EXPORT gbIsMetric;

//长度单位制，米制或像素，默认为米制；影响二次开发输入的长度单位，默认为米制是因为客户不习惯用像素作为长度单位
// void TESSINTERFACES_EXPORT setUnitOfMeasure(UnitOfMeasure unit);

//将以米为度量单位的数据转换成以像素为度量单位的数据
qreal TESSINTERFACES_EXPORT m2p(qreal value);

//将以像素为度量单位的数据转换成以米为度量单位的数据
qreal TESSINTERFACES_EXPORT p2m(qreal value);

#endif