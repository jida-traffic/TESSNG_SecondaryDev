#ifndef __IReduceSpeedArea__
#define __IReduceSpeedArea__

#include "tessinterfaces_global.h"
#include "UnitChange.h"
#include "Plugin/_datastruct.h"

#include <QObject>

class IReduceSpeedInterval;

class TESSINTERFACES_EXPORT IReduceSpeedArea
{
public:
	virtual ~IReduceSpeedArea() = default;
	/*限速区ID*/
	virtual long id() = 0;
	/*限速区名称*/
	virtual QString name() = 0;
	/* 距起点距离，单位：像素 */
	virtual qreal location(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*限速区长度，单位：像素*/
	virtual qreal areaLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 路段或连接段ID */
	virtual long sectionId() = 0;
	/* 车道序号 */
	virtual int laneNumber() = 0;
	/* 目标车道序号 */
	virtual int toLaneNumber() = 0;

	/* 添加限速时段 */
	virtual IReduceSpeedInterval* addReduceSpeedInterval(Online::DynaReduceSpeedIntervalParam param) = 0;
	/* 移除限速时段 */
	virtual void removeReduceSpeedInterval(long id) = 0;
	/* 更新限速时段 */
	virtual bool updateReduceSpeedInterval(Online::DynaReduceSpeedIntervalParam param) = 0;
	/* 限速时段 */
	virtual QList<IReduceSpeedInterval*> reduceSpeedIntervals() = 0;
	/*根据ID获取限速时段*/
	virtual IReduceSpeedInterval* findReduceSpeedIntervalById(long id) = 0;
	/*根据起始时间获取限速时段*/
	virtual IReduceSpeedInterval* findReduceSpeedIntervalByStartTime(long startTime) = 0;

	//多边型轮廓
	virtual QPolygonF polygon() = 0;

};

#endif