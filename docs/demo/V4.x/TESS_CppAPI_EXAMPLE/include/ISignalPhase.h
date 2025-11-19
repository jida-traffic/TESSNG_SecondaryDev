#ifndef __ISignalPhase__
#define __ISignalPhase__

#include "tessinterfaces_global.h"

#include "Plugin/_datastruct.h"

#include <QObject>

//class ISignalGroup;
class ISignalLamp;
class ISignalPlan;

class TESSINTERFACES_EXPORT ISignalPhase
{
public:
	virtual ~ISignalPhase() = default;
	/* ID */
	virtual long id();
	///* 序号 */
	//virtual int number();
	/* 相位名称 */
	virtual QString phaseName();
	/* 相位灯色列表 */
	virtual QList<Online::ColorInterval> listColor();
	/* 设置灯色列表 */
	virtual void setColorList(QList<Online::ColorInterval> lColor);
	/* 周期，单位秒 */
	virtual int cycleTime();
	/*当前灯色*/
	virtual Online::SignalPhaseColor phaseColor();
	///* 信号灯组 */
	//virtual ISignalGroup* signalGroup();
	/*信控方案*/
	virtual ISignalPlan* signalPlan();
	/* 信号灯 */
	virtual QList<ISignalLamp*> signalLamps();
    /*添加关联的信号灯*/


    /* 信号灯id组 */
    virtual QString signalLampIds();

	///* 设置相位序号 */
	//virtual void setNumber(int number);
	/* 设置相位名称 */
	virtual void setPhaseName(QString name);

};

#endif