/********************************************************************************
* 路段接口，由TESS NG实现，用户可以借此调用接口方法，获取路段一些基本属性
*********************************************************************************/

#ifndef __ILink__
#define __ILink__

#include "tessinterfaces_global.h"

#include "ISection.h"
#include "ILane.h"
#include "UnitChange.h"

#include <QVector3D>
#include <QJsonObject>

class IRouting;
class IConnector;

class TESSINTERFACES_EXPORT  ILink : public ISection
{
public:
	virtual ~ILink() = default;
	//============继承自ISection============
	//获取sectionID，由于当前是路段，sectionID即为路段的ID
	virtual long sectionId() = 0;
	//路段ID
	virtual long id() = 0;
	//类型，GLinkType
	virtual int gtype() = 0;
	//Section名
	virtual QString name() = 0;
	//设置Section名
	virtual void setName(QString name) = 0;
	//高程
	virtual qreal v3z(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*
	长度，单位：米
	*/
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*
	ILaneObject列表
	*/
	virtual QList<ILaneObject*> laneObjects() = 0;
	/* 根据ID获取上游 Sction。
	如果当前是路段, id 为 0 返回空指针，否则返回上游指定ID的连接段；
	如果当前是连接段，id 为 0 返回上游路段，否则返回空指针。
	*/
	virtual ISection* fromSection(long id = 0) = 0;
	/* 根据ID获取下游 Sction。
	如果当前是路段, id 为 0 返回空指针，否则返回下游指定ID的连接段；
	如果当前是连接段，id 为 0 返回下游路段，否则返回空指针。
	*/
	virtual ISection* toSection(long id = 0) = 0;
	//路段其它属性
	virtual QJsonObject otherAttr() = 0;
	//设置路段其它属性
	virtual void setOtherAttr(QJsonObject otherAttr) = 0;
	//多边型轮廓
	virtual QPolygonF polygon() = 0;


	//============路段特有属性============
	//路段宽度，单位：米
	virtual qreal width(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//高程
	virtual qreal z(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道数
	virtual int laneCount() = 0;
	//限速 千米/小时
	virtual double limitSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段类型
	virtual QString linkType() = 0;
	//设置最高限速
	virtual void setLimitSpeed(qreal speed, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置路段类型
	virtual void setType(QString type) = 0;
	//最小速度 千米/小时
	virtual qreal minSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置最小限速
	virtual void setMinSpeed(qreal speed, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道接口列表
	virtual QList<ILane*> lanes() = 0;
	//上游连接段列表
	virtual QList<IConnector*> fromConnectors() = 0;
	//下游连接段列表
	virtual QList<IConnector*> toConnectors() = 0;
	//路段中心线断点集
	virtual QList<QPointF> centerBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段左侧线断点集
	virtual QList<QPointF> leftBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段右侧线断点集
	virtual QList<QPointF> rightBreakPoints(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段中心线断点(三维)集
	virtual QList<QVector3D> centerBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段左侧线断点(三维)集
	virtual QList<QVector3D> leftBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//路段右侧线断点(三维)集
	virtual QList<QVector3D> rightBreakPoint3Ds(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//设置车道属性，属性类型包括：
	virtual void setLaneTypes(QList<QString> lType) = 0;
	//设置车道其它属性
	virtual void setLaneOtherAtrrs(QList<QJsonObject> lAttrs) = 0;
	//中心线上一点到起点距离
	virtual qreal distToStartPoint(const QPointF p, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点及分段序号, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointAndIndexByDist(qreal dist, QPointF& outPoint, int& outIndex, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//求中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回false，否则返回true   
	virtual bool getPointByDist(qreal dist, QPointF& outPoint, UnitOfMeasure unit = UnitOfMeasure::Default) = 0;

};

#endif