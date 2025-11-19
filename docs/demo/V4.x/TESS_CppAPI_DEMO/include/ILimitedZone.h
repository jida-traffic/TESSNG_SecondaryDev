#ifndef __ILimitedZone__
#define __ILimitedZone__

#include <QObject>
#include "tessinterfaces_global.h"
#include "UnitChange.h"

class ILaneObject;
class ISection;

class TESSINTERFACES_EXPORT ILimitedZone
{
public:
	virtual ~ILimitedZone() = default;
	/* 施工区ID */
	virtual long id() = 0;
	//名称
	virtual QString name() = 0;
	/* 距起点距离，单位：像素 */
	virtual qreal location(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//施工区长度，单位：像素
	virtual qreal zoneLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//施工区限速（最大车速:像素/秒）
	virtual qreal limitSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 路段或连接段ID */
	virtual long sectionId() = 0;
	//Section名称
	virtual QString sectionName() = 0;
	//道路类型 link:路段, connector:连接段
	virtual QString sectionType() = 0;

	virtual QList<ILaneObject*> laneObjects() = 0;

	//======以下是限行区管理相关属性======
	//限行持续时间，单位秒，自仿真过程创建后，持续时间大于此值，则删除
	virtual long duration() = 0;
};

#endif