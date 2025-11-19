#pragma once

#include <QObject>

#include "tessinterfaces_global.h"

#include "Plugin/_datastruct.h"

#include <QObject>

class ISignalPhase;

class TESSINTERFACES_EXPORT ISignalPlan  : public QObject
{
	Q_OBJECT

public:
	virtual ~ISignalPlan() = default;
	/* ID */
	virtual long id();
	/* 灯组名 */
	virtual QString name();
	/* 信号机名称 */
	virtual QString trafficName();
	/*信号周期 单位：秒*/
	virtual int cycleTime();
	/* 起始时间，单位：秒 */
	virtual long fromTime();
	/* 结束时间 单位：秒 */
	virtual long toTime();
	/*相位差*/
	virtual int phaseDifference();
	/* 相位列表 */
	virtual QList<ISignalPhase*> phases();

	/* 设置信号灯组/信控方案名称 */
	virtual void setName(QString name);
	/* 设置信号周期 单位：秒 */
	virtual void setCycleTime(int period);
	/* 设置起始时间 单位：秒*/
	virtual void setFromTime(long time);
	/* 设置结束时间 单位：秒 */
	virtual void setToTime(long time);
	/* 设置相位差 单位：秒 */
	virtual void setPhaseDifference(long time);

//	ISignalPlan(QObject *parent);
//	~ISignalPlan();
};
