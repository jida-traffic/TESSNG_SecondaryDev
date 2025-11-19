/********************************************************************************
* 车道连接接口，由TESS NG实现，用户可以借此调用接口方法，获取车道连接一些基本属性
*********************************************************************************/

#ifndef __ILaneConnector__
#define __ILaneConnector__

#include "tessinterfaces_global.h"
#include "ILaneObject.h"
#include "UnitChange.h"

#include <QObject>
#include <QJsonObject>

class ILane;
class IConnector;

class TESSINTERFACES_EXPORT ILaneConnector : public ILaneObject
{
public:
	virtual ~ILaneConnector() = default;
	//============继承自ILaneObject的方法============
	//GLaneConnectorType
	virtual int gtype() = 0;
	//车道连接ID
	virtual long id() = 0;
	//所属Section
	virtual ISection* section() = 0;
	//车道长度，单位：米
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 根据ID获取上游 LaneObject。
	id 为 0 返回上游车道，否则返回空指针。
	*/
	virtual ILaneObject* fromLaneObject(long id = 0) = 0;
	/* 根据ID获取下游 LaneObject。
	id 为 0 返回下游车道，否则返回空指针。
	*/
	virtual ILaneObject* toLaneObject(long id = 0) = 0;
	//"车道连接"断点集
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
	//"车道连接"右侧部分断点(三维)集，fromPoint：中心线上某一点作为起点，toPoint：中心线上某一点作为终点
	virtual QList<QVector3D> rightBreak3DsPartly(QPointF fromPoint, QPointF toPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离
	virtual qreal distToStartPoint(const QPointF p, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离，附加条件是该点所在车道上的分段序号
	virtual qreal distToStartPointWithSegmIndex(const QPointF p, int segmIndex = 0, bool bOnCentLine = true, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起始point向前延伸dist距离后所在点及分段序号, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointAndIndexByDist(qreal dist, QPointF& outPoint, int& outIndex, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起始point向前延伸dist距离后所在点, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointByDist(qreal dist, QPointF& outPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置车道连接其它属性
	virtual void setOtherAttr(QJsonObject attr) = 0;


	//============车道连接特有方法============
	//所在连接段
	virtual IConnector* connector() = 0;
	//上游车道
	virtual ILane* fromLane() = 0;
	//下游车道
	virtual ILane* toLane() = 0;

};

#endif
