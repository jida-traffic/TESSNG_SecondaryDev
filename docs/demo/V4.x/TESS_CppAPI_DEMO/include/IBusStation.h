/********************************************************************************
* 公交站点接口，由TESS NG实现，用户可以借此访问公交站点对象方法，获取公交线路一些基本信息
*********************************************************************************/

#ifndef __IBusStation__
#define __IBusStation__

#include "tessinterfaces_global.h"

#include "IBusStationLine.h"
#include "UnitChange.h"
#include <QObject>
#include <QList>
#include <QPolygonF>

class ILink;
class ILane;
class IBusLine;

class TESSINTERFACES_EXPORT IBusStation
{
public:
	virtual ~IBusStation() = default;
	// 线路ID
	virtual long id();
	//线路名称
	virtual QString name();
	/* 公交站点所在车道序号 */
	virtual int laneNumber();
	/* 公交站点的起始位置距路段起点距离，单位像素*/
	virtual qreal distance(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 位置X */
	virtual qreal x(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 位置Y */
	virtual qreal y(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 长度 */
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 站点类型 1：路边式、2：港湾式 */
	virtual int stationType();
	/* 公交站点所在路段 */
	virtual ILink* link();
	/*公交站点所在车道*/
	virtual ILane* lane();

	/*设置站点名称*/
	virtual void setName(QString name);
	/* 设置站点起始点距车道起点距离 dist:距车道起点距离，单位像素*/
	virtual void setDistToStart(qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 设置长度，单位像素 */
	virtual void setLength(qreal length, UnitOfMeasure unit = UnitOfMeasure::Default);
	/* 设置站点类型 type:1 路侧式、2 港湾式*/
	virtual void setType(int type);

	//多边型轮廓
	virtual QPolygonF polygon();

	//方向线，从起点到终点的线段
	virtual QLineF directionLine();
};

#endif