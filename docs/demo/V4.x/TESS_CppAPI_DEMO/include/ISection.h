#ifndef __ISection__
#define __ISection__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QPolygonF>

class ILaneObject;
class ILink;
class IConnector;

class TESSINTERFACES_EXPORT ISection
{
public:
	virtual ~ISection() = default;
	//获取ID，如果是Link，id是Link的ID，如果是连接段，id是连接段ID+10000000，从而区分路段与连接段
	virtual long sectionId() = 0;
	//如果是Link，id是Link的ID， 如果是连接段，id是连接段ID
	virtual long id() = 0;
	//类型，GLinkType 或 GConnectorType
	virtual int gtype() = 0;
	//是否路段
	virtual bool isLink() = 0;
	//Section名
	virtual QString name() = 0;
	//设置Section名
	virtual void setName(QString name) = 0;
	//高程
	virtual qreal v3z(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*
	长度，单位米
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
	//设置路段或连接段其它属性
	virtual void setOtherAttr(QJsonObject otherAttr) = 0;
	//转换成ILink，如果当前为连接段则返回空指针
	virtual ILink* castToLink() = 0;
	//转换成IConnector* 如果当前为路段则返回空指针
	virtual IConnector* castToConnector() = 0;
	//多边型轮廓
	virtual QPolygonF polygon() = 0;
	//Worker节点标识
	virtual int workerKey() = 0;
	//设置Worker节点
	virtual void setWorkerKey(int key) = 0;
	//上游Worker节点标识
	virtual int fromWorkerKey() = 0;
	//设置上游Worker节点标识
	virtual void setFromWorkerKey(int key) = 0;
};

#endif