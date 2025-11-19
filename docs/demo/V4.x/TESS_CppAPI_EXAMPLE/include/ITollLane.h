/********************************************************************************
* 收费车道接口，由TESS NG实现，用户可以借此调用接口方法，获取路径一些基本属性
*********************************************************************************/

#ifndef __TOLL_LANE_H_
#define __TOLL_LANE_H_

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QGraphicsItem>
#include <QObject>

class ILink;
class ISection;
class ITollPoint;

class TESSINTERFACES_EXPORT ITollLane
{
public:
	virtual ~ITollLane() = default;
	// 返回收费车道ID
	virtual long id() const;

	// 返回收费车道名称
	virtual QString name() const;

	/// @brief 距路段起始位置（米)
	virtual qreal distance() const;

	/// @brief 设置收费车道名称
	/// @param name 收费车道新名称
	virtual void setName(const QString& name);
	
	/// @brief 设置工作时间，工作时间与仿真时间对应
	/// @param startTime 开始时间(秒)
	/// @param endTime 结束时间(秒)
	virtual void setWorkTime(long startTime, long endTime);

	virtual Online::TollStation::DynaTollLane dynaTollLane();

	// 收费车道所有收费点
	virtual QList<ITollPoint*> tollPoints() const;
};

#endif
