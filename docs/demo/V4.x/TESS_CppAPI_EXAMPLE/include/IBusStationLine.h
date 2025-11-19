#ifndef __IBusStationLine__
#define __IBusStationLine__

#include "tessinterfaces_global.h"

#include <QObject>

class TESSINTERFACES_EXPORT IBusStationLine
{
public:
	virtual ~IBusStationLine() = default;
	/* 站点线路ID */
	virtual long id();
	/* 公交站点ID*/
	virtual long stationId();
	/* 公交线路ID*/
	virtual long lineId();
	/* 车辆停靠时间(秒) */
	virtual int busParkingTime();
	/* 下客百分比*/
	virtual qreal getOutPercent();
	/* 平均每位乘客上车时间*/
	virtual qreal getOnTimePerPerson();
	/* 平均每位乘客下车时间*/
	virtual qreal getOutTimePerPerson();

	/* 设置车辆停靠时间(秒) */
	virtual void setBusParkingTime(int time);
	/* 设置下客百分比*/
	virtual void setGetOutPercent(qreal percent);
	/* 设置平均每位乘客上车时间*/
	virtual void setGetOnTimePerPerson(qreal time);
	/* 设置平均每位乘客下车时间*/
	virtual void setGetOutTimePerPerson(qreal time);

};

#endif