/********************************************************************************
* 停车区域接口，由TESS NG实现，用户可以借此调用接口方法，获取路径一些基本属性
*********************************************************************************/

#ifndef __IJUNCTION_H__
#define __IJUNCTION_H__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QGraphicsItem>

class ILink;
class IConnector;

class TESSINTERFACES_EXPORT IJunction
{
public:
	virtual ~IJunction() = default;
	/*节点ID*/
	virtual long getId() const = 0;

	// 停车区域名称
	virtual QString name() const = 0;

	/// @brief 设置节点新名称
	/// @param name 新名称
	virtual void setName(const QString& strName) const = 0;

	//获取节点内的路段 
	virtual QList<ILink*> getJunctionLinks() const = 0;

	//获取节点内的连接段
	virtual QList<IConnector*> getJunctionConnectors() const = 0;

	//获取节点内的流向信息
	virtual QList<Online::Junction::TurnningBaseInfo> getAllTurningInfo() const = 0;

	//获取节点内指定的流向信息
	virtual Online::Junction::TurnningBaseInfo getTurningInfo(long turningId)const = 0;

};

#endif