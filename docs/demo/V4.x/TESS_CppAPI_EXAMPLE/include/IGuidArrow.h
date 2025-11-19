#ifndef __IGuidArrow__
#define __IGuidArrow__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"
#include "UnitChange.h"
#include <QObject>
#include <QPolygonF>

class ILane;

class TESSINTERFACES_EXPORT IGuidArrow
{
public:
	virtual ~IGuidArrow() = default;
	//导向箭头ID
	virtual long id();
	//车道
	virtual ILane* lane();
	//长度，单位像素
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default);
	//到终点距离，单位像素
	virtual qreal distToTerminal(UnitOfMeasure unit = UnitOfMeasure::Default); 
	//导向箭头类型
	virtual Online::GuideArrowType arrowType();
	//多边型轮廓
	virtual QPolygonF polygon();
};

#endif

