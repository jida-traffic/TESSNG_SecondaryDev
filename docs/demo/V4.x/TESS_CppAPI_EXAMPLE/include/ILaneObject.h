#ifndef __ILaneObject__
#define __ILaneObject__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

class ISection;
class ILane;
class ILaneConnector;

class TESSINTERFACES_EXPORT ILaneObject
{
public:
	virtual ~ILaneObject() = default;
	//GLaneType或GLaneConnectorType
	virtual int gtype() = 0;
	//是否车道
	virtual bool isLane() = 0;
	//sectionId，如果是Lane，id是Lane的ID， 如果是车道连接，id是车道连接ID
	virtual long id() = 0;
	//长度，单位：米
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//所属Section
	virtual ISection* section() = 0;
	/* 根据ID获取上游 LaneObject。
	如果当前是车道, id 为 0 返回空指针，否则返回上游指定ID的“车道连接”；
	如果当前是连接段，id 为 0 返回上游车道，否则返回空指针。
	*/
	virtual ILaneObject* fromLaneObject(long id = 0) = 0;
	/* 根据ID获取下游 LaneObject。
	如果当前是车道, id 为 0 返回空指针，否则返回下游指定ID的“车道连接”；
	如果当前是连接段，id 为 0 返回下游车道，否则返回空指针。
	*/
	virtual ILaneObject* toLaneObject(long id = 0) = 0;
	//车道中心线点集
	virtual QList<QPointF> centerBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"左侧线断点集
	virtual QList<QPointF> leftBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"右侧线断点集
	virtual QList<QPointF> rightBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"中心线断点(三维)集
	virtual QList<QVector3D> centerBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"左侧线断点(三维)集
	virtual QList<QVector3D> leftBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"右侧线断点(三维)集
	virtual QList<QVector3D> rightBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//"车道连接"左侧部分断点(三维)集，fromPoint：中心线上某一点作为起点，toPoint：中心线上某一点作为终点
	virtual QList<QVector3D> leftBreak3DsPartly(QPointF fromPoint, QPointF toPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//“车道连接”右侧部分断点(三维)集，fromPoint：中心线上某一点作为起点，toPoint：中心线上某一点作为终点
	virtual QList<QVector3D> rightBreak3DsPartly(QPointF fromPoint, QPointF toPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离
	virtual qreal distToStartPoint(const QPointF p, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离，附加条件是该点所在车道上的分段序号
	virtual qreal distToStartPointWithSegmIndex(const QPointF p, int segmIndex = 0, bool bOnCentLine = true, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点及分段序号, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointAndIndexByDist(qreal dist, QPointF& outPoint, int& outIndex, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointByDist(qreal dist, QPointF& outPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置车道或"车道连接"其它属性
	virtual void setOtherAttr(QJsonObject attr) = 0;
	//将ILaneObject转换为ILane，如果当前ILaneObject是“车道连接”则返回空指针
	virtual ILane* castToLane() = 0;
	//将ILaneObject转换为ILaneConnector，如果当前ILaneObject是车道则返回空指针
	virtual ILaneConnector* castToLaneConnector() = 0;

};

#endif