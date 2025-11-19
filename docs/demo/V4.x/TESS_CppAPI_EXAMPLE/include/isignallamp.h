/********************************************************************************
* 信号灯接口，由TESS NG实现，用户可以借此调用接口方法，获取信号灯一些基本属性
*********************************************************************************/

#ifndef ISIGNALLAMP_H
#define ISIGNALLAMP_H

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QColor>
#include <QPolygonF>

//class ISignalGroup;
class ISignalPhase;
class ILaneObject;
class ISignalPlan;
class ISignalPhase;

class TESSINTERFACES_EXPORT ISignalLamp
{
public:
	virtual ~ISignalLamp() = default;
	//获取信号灯ID
	virtual long id();
	//设置相位，所设相位可以是其它信号灯组的相位
	virtual void setSignalPhase(ISignalPhase* pPhase);
	//设置相位序号，序号从1开始，如果num序号大于相位总数不进行设置
	virtual void setPhaseNumber(int num);
	//设置灯色，colorStr为"红"、"绿"、"黄"、"灰"，或者"R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"
	virtual void setLampColor(QString colorStr);
	//获取信号灯色，"R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"
	virtual QString color();
	//获取信号灯名
	virtual QString name();
	//设置信号灯名称
	virtual void setName(QString name);
	//设置信号灯距车道起点（或“车道连接”起点）距离，单位像素
	virtual void setDistToStart(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//获取信控方案
	virtual ISignalPlan* signalPlan();
	/*获取当前相位*/
	virtual ISignalPhase* signalPhase();
    /*获取相位列表*/
    virtual QList< ISignalPhase*> lsignalPhases();
	/*获取所在车道或车道连接*/
	virtual ILaneObject* laneObject();
	//多边型轮廓 
	virtual QPolygonF polygon();
	//角度
	virtual qreal angle();
private:
	
};

#endif // ISIGNALLAMP_H