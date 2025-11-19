/********************************************************************************
* 面域接口，由TESS NG实现，用户可以借此调用接口方法，获取面域一些基本属性
*********************************************************************************/

#ifndef __IConnectorArea__
#define __IConnectorArea__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

class IConnector;

class TESSINTERFACES_EXPORT IConnectorArea
{
public:
	virtual ~IConnectorArea() = default;
	virtual long id()= 0;
	virtual QPointF centerPoint(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	virtual QList<IConnector*> allConnector() = 0;
	//获取 Worker 标识符
	virtual int workerKey() = 0;
	//设置 Worker 标识符
	virtual void setWorkerKey(int key) = 0;
};

#endif