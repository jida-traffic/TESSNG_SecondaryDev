#ifndef __IModelNet__
#define __IModelNet__

#include "tessinterfaces_global.h"

#include <QPainter>

class ILaneObject;
class QGraphicsItem;

class TESSINTERFACES_EXPORT IModelNet
{
public:
	IModelNet() {}
	virtual ~IModelNet() {}

	//加载完路网后的行为
	virtual void afterLoadNet();

	virtual bool removeItemFromScene(QGraphicsItem* pItem);

	virtual void deleteCustomerData();

	//绘制车道或车道连接
	virtual bool paintLaneObject(ILaneObject* pILaneObj, QPainter* painter);

};

#endif