/********************************************************************************
* 停车位接口，由TESS NG实现，用户可以借此调用接口方法，获取路径一些基本属性
*********************************************************************************/

#ifndef __PARKING_STALL__
#define __PARKING_STALL__

#include "tessinterfaces_global.h"

#include <QGraphicsItem>

class ILink;
class ISection;
class IParkingRegion;

class TESSINTERFACES_EXPORT IParkingStall
{
public:
	virtual ~IParkingStall() = default;
	/*停车位ID*/
	virtual long id();

	/*所属停车区域ID*/
	virtual long parkingRegionId();

	virtual IParkingRegion* parkingRegion() = 0;

	/// @brief 距路段起始位置（米)
	virtual qreal distance() const = 0;

	// 车位类型 与车辆类型编码一致
	virtual int stallType() = 0;
	// 车位是否预分配使用
	virtual bool isPreUsed() = 0;
	// 车位是否使用
	virtual bool isUsed() = 0;
};

#endif