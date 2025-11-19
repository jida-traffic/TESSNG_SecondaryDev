/********************************************************************************
* 连接段接口，由TESS NG实现，用户可以借此调用接口方法，获取连接段一些基本属性
*********************************************************************************/

#ifndef __IConnector__
#define __IConnector__

#include "tessinterfaces_global.h"

#include "ISection.h"

#include <QJsonObject>
#include <QPolygonF>

class ILink;
class ILaneConnector;

class TESSINTERFACES_EXPORT IConnector : public ISection
{
public:
	virtual ~IConnector() = default;
	//============继承自ISection============
	//获取sectionID，由于当前是连接段，sectionID是连接段ID+10000000
	virtual long sectionId() = 0;
	//连接段ID
	virtual long id() = 0;
	//类型，GLinkType 或 GConnectorType
	virtual int gtype() = 0;
	//连接段长度
	virtual qreal length(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//连接段名
	virtual QString name() = 0;
	//设置连接段名
	virtual void setName(QString name) = 0;
	//高程
	virtual qreal v3z(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
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
	//设置连接段其它属性
	virtual void setOtherAttr(QJsonObject otherAttr) = 0;
	//多边型轮廓
	virtual QPolygonF polygon() = 0;


	//==========连接段特有属性============
	//高程
	virtual qreal z(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//上游路段
	virtual ILink* fromLink() = 0;
	//下游路段
	virtual ILink* toLink() = 0;
	//限速 千米/小时
	virtual qreal limitSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//最小速度 千米/小时
	virtual qreal minSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	//车道连接列表
	virtual QList<ILaneConnector*> laneConnectors() = 0;
	//设置车道连接其它属性
	virtual void setLaneConnectorOtherAtrrs(QList<QJsonObject> lAttrs) = 0;

};

#endif