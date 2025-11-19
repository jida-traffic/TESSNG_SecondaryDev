#ifndef __ISignalGroup__
#define __ISignalGroup__

#include "tessinterfaces_global.h"

#include <QObject>

class ISignalPhase;

class TESSINTERFACES_EXPORT ISignalGroup
{
public:
	virtual ~ISignalGroup() = default;
	/* ID */
	virtual long id();
	/* 灯组名 */
	virtual QString groupName();
	/*信号周期 单位：秒*/
	virtual int periodTime();
	/* 起始时间，单位：秒 */
	virtual long fromTime();
	/* 结束时间 单位：秒 */
	virtual long toTime();
	/* 相位列表 */
	virtual QList<ISignalPhase*> phases();

	/* 设置信号灯组名称 */
	virtual void setName(QString name);
	/* 设置信号周期 单位：秒 */
	virtual void setPeriodTime(int period);
	/* 设置起始时间 单位：秒*/
	virtual void setFromTime(long time);
	/* 设置结束时间 单位：秒 */
	virtual void setToTime(long time);

};

#endif