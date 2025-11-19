/********************************************************************************
* 停车区域接口，由TESS NG实现，用户可以借此调用接口方法，获取路径一些基本属性
*********************************************************************************/

#ifndef __PARKINGREGION_STALL__
#define __PARKINGREGION_STALL__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QGraphicsItem>

class ILink;
class IParkingStall;

class TESSINTERFACES_EXPORT IParkingRegion
{
public:
	virtual ~IParkingRegion() = default;
	/*停车区域ID*/
	virtual long id() const;

	// 停车区域名称
	virtual QString name() const;

	/// @brief 设置停车区域名称
	/// @param name 新名称
	virtual void setName(const QString& name);

	// 所有停车位
	virtual QList<IParkingStall*> parkingStalls() const;

	virtual Online::ParkingLot::DynaParkingRegion dynaParkingRegion();

    //停车场polygon
    virtual QPolygonF polygon();
};

#endif