#ifndef __IEvaluateArea_H__
#define __IEvaluateArea_H__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QGraphicsItem>

class ILink;
class IConnector;

class TESSINTERFACES_EXPORT IEvaluateArea
{
public:
	virtual ~IEvaluateArea() = default;
	/*节点ID*/
	virtual long getId() = 0;

	// 停车区域名称
	virtual QString name() = 0;

	/// @brief 设置评价区域新名称
	/// @param name 新名称
	virtual void setName(const QString& strName) = 0;
};

#endif