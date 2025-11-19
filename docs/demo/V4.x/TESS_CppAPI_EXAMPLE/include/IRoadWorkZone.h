#ifndef __IRoadWorkZone__
#define __IRoadWorkZone__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"
#include "UnitChange.h"
#include <QObject>

class ILaneObject;

class TESSINTERFACES_EXPORT IRoadWorkZone
{
public:
	virtual ~IRoadWorkZone() = default;
	/* 施工区ID */
	virtual long id() = 0;
	//名称
	virtual QString name() = 0;
	/* 距起点距离，单位：像素 */
	virtual qreal location(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//施工区长度，单位：像素
	virtual qreal zoneLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//施工区限速（最大车速:像素（km/h））
	virtual qreal limitSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 路段或连接段ID */
	virtual long sectionId() = 0;
	//Section名称
	virtual QString sectionName() = 0;
	//道路类型 link:路段, connector:连接段
	virtual QString sectionType() = 0;
	//相关车道或“车道连接”
	virtual QList<ILaneObject*> laneObjects() = 0;
	//相关车道或“车道连接”ID
	virtual QList<long> laneObjectIds() = 0;
	//上游警示区长度，单位：像素
	virtual qreal upCautionLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//上游过渡区长度，单位：像素
	virtual qreal upTransitionLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//上游缓冲区长度，单位：像素
	virtual qreal upBufferLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//下游过渡区长度，单位：像素
	virtual qreal downTransitionLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//下游终止区长度，单位：像素
	virtual qreal downTerminationLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//施工区管控标志序
	virtual QList<int> logoIndexs() = 0;
	//初始创建时间，单位毫秒
	virtual qint64 createCPUTime() = 0;

	//======以下是施工区管理相关属性
	//施工持续时间，单位秒，自仿真过程创建后，持续时间大于此值，则删除
	virtual long duration() = 0;
	//被借道
	virtual bool isBorrowed() = 0;
	//设置借道
	virtual void setBorrowed(bool b) = 0;
};

#endif