/********************************************************************************
* 停车决策路径接口，由TESS NG实现，用户可以借此调用接口方法，获取决策点一些基本属性，进行一些操作
*********************************************************************************/

#ifndef __IPARKINGROUTING_H__
#define __IPARKINGROUTING_H__

#include "tessinterfaces_global.h"
#include "_tables.h"

#include <QPolygonF>

class ILink;
class ISection;

class TESSINTERFACES_EXPORT IParkingRouting
{
public:
	virtual ~IParkingRouting() = default;
	/*路径ID*/
	virtual long id();

	/*所属决策点ID*/
	virtual long parkingDeciPointId();

	/*路径到达的停车区域ID*/
	virtual long parkingRegionId();

	/*计算路径长度*/
	virtual qreal calcuLength();

	/* 根据所给道路判断是否在当前路径上*/
	virtual bool contain(ISection* pRoad);

	/* 根据所给道路求下一条道路, pRoad是路段或连接段*/
	virtual ISection* nextRoad(ISection* pRoad);

	/* 获取路段序列*/
	virtual QList<ILink*> getLinks();
};

#endif