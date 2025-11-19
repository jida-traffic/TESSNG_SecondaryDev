/********************************************************************************
* 车道接口，由TESS NG实现，用户可以借此调用接口方法，获取车道一些基本属性
*********************************************************************************/

#ifndef __ILane__
#define __ILane__

#include "tessinterfaces_global.h"
#include "ILaneObject.h"
#include "UnitChange.h"

#include <QObject>
#include <QList>
#include <QPointF>

class ILink;
class ILaneObject;
class _LaneLimitChange;
class ILaneConnector;

class TESSINTERFACES_EXPORT ILane : public ILaneObject
{
public:
	virtual ~ILane() = default;
	//============继承自ILaneObject的方法============
	//GLaneType
	virtual int gtype() = 0;
	//所属Section
	virtual ISection* section() = 0;
	//车道ID
	virtual long id() = 0;
	//车道长度，单位：米
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/* 根据ID获取上游 LaneObject。
	id 为 0 返回空指针，否则返回上游指定ID的“车道连接”；
	*/
	virtual ILaneObject* fromLaneObject(long id = 0) = 0;
	/* 根据ID获取下游 LaneObject。
	id 为 0 返回空指针，否则返回下游指定ID的“车道连接”；
	*/
	virtual ILaneObject* toLaneObject(long id = 0) = 0;
	//车道中心线点集
	virtual QList<QPointF> centerBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道左侧线断点集
	virtual QList<QPointF> leftBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道右侧线断点集
	virtual QList<QPointF> rightBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道中心线断点(三维)集
	virtual QList<QVector3D> centerBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道左侧线断点(三维)集
	virtual QList<QVector3D> leftBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道右侧线断点(三维)集
	virtual QList<QVector3D> rightBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道左侧部分断点(三维)集，fromPoint：中心线上某一点作为起点，toPoint：中心线上某一点作为终点
	virtual QList<QVector3D> leftBreak3DsPartly(QPointF fromPoint, QPointF toPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道右侧部分断点(三维)集，fromPoint：中心线上某一点作为起点，toPoint：中心线上某一点作为终点
	virtual QList<QVector3D> rightBreak3DsPartly(QPointF fromPoint, QPointF toPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离
	virtual qreal distToStartPoint(const QPointF p, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//中心线上一点到起点距离，附加条件是该点所在车道上的分段序号
	virtual qreal distToStartPointWithSegmIndex(const QPointF p, int segmIndex = 0, bool bOnCentLine = true, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点及分段序号, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointAndIndexByDist(qreal dist, QPointF& outPoint, int& outIndex, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointByDist(qreal dist, QPointF& outPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置车道其它属性
	virtual void setOtherAttr(QJsonObject attr) = 0;
	//多边型轮廓
	virtual QPolygonF polygon() = 0;

	//============车道特有方法============
	//获取车道所在路段
	virtual ILink* link() = 0;
	//车道宽度，单位：米
	virtual qreal width(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//序号，从0开始，自外侧往内侧
	virtual int number() = 0;
	//车道行为类型：“机动车道”、“非机动车道”、“公交专用道”等
	virtual QString actionType() = 0;

	//设置车道类型
	virtual void setLaneType(QString type) = 0;

	// 是否应急车道
	virtual bool emergencyLane() = 0;
	virtual void setEmergencyLane(bool value) = 0;

	// 新增限制变道 laneLimitChange ID设置为默认值，如果设置大于0需要自己判断唯一性避免保存出错
	virtual _LaneLimitChange addLimitChange(const _LaneLimitChange& laneLimitChange) = 0;
	// 更新限制变道信息
	virtual bool updateLimitChange(const _LaneLimitChange& laneLimitChange) = 0;
	// 删除限制变道 根据参数属性ID删除
	virtual bool delLimitChange(long limitChangeID) = 0;
	// 查询限制变道信息
	virtual QList<_LaneLimitChange> limitChanges() = 0;
    //
    virtual QVector<QList<QPointF>>  getLimitChangeBreakPointVec() = 0;
  
	virtual QList<ILaneConnector*> fromLaneConnectors() = 0;
	virtual QList<ILaneConnector*> toLaneConnectors() = 0;

};

#endif 
