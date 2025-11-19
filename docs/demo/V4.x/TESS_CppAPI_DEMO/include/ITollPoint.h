/********************************************************************************
* 收费点接口，由TESS NG实现，用户可以借此调用接口方法，获取路径一些基本属性
*********************************************************************************/

#ifndef __TOLL_POINT_H__
#define __TOLL_POINT_H__

#include "tessinterfaces_global.h"
#include <QGraphicsItem>

class ILink;
class ISection;
class ITollLane;

class TESSINTERFACES_EXPORT ITollPoint
{
public:
	virtual ~ITollPoint() = default;
	/*ID*/
	virtual long id() = 0;

	/// @brief 距路段起始位置（米)
	virtual qreal distance() const = 0;

	/*所属收费车道ID*/
	virtual long tollLaneId() = 0;

	virtual ITollLane* tollLane() = 0;

	// 是否启用
	virtual bool isEnabled() = 0;

	// 设置启用状态
	virtual bool setEnabled(bool enabled) = 0;

	// 收费类型
	virtual int tollType() = 0;

	// 设置收费类型
	virtual bool setTollType(int tollType) = 0;

	// 停车时间分布ID
	virtual int timeDisId() = 0;

	virtual bool setTimeDisId(int timeDisId) = 0;
};

#endif