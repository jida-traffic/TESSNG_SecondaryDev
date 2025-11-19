/********************************************************************************
* 停车决策点接口，由TESS NG实现，用户可以借此调用接口方法，获取决策点一些基本属性，进行一些操作
*********************************************************************************/

#ifndef __IPARKINGDECISIONPOINT_H__
#define __IPARKINGDECISIONPOINT_H__

#include "tessinterfaces_global.h"
#include "_tables.h"
#include "Plugin/_datastruct.h"

#include <QPolygonF>

class ILink;
class IParkingRouting;
class IRouting;


class TESSINTERFACES_EXPORT IParkingDecisionPoint
{
public:
	virtual ~IParkingDecisionPoint() = default;
	/*决策点ID	*/
	virtual long id();
	//决策点名称
	virtual QString name();
	//决策点所在路段
	virtual ILink* link();
	//距路段起点距离，单位米
	virtual qreal distance();
	//相关停车路径
	virtual QList<IParkingRouting*> routings();
	virtual bool updateParkDisInfo(const QList<Online::ParkingLot::DynaParkDisInfo>& tollDisInfoList);

	//决策流量比
	// virtual QList<_RoutingFLowRatio> routingFLowRatios() = 0;
	//设置是否被动态修改，默认情况下发车信息被动态修改后，整个文件不能保存，以免破坏原有发车设置
	// virtual void setDynaModified(bool bModified) = 0;

	virtual QList<Online::ParkingLot::DynaParkDisInfo> parkDisInfoList();

	//多边型轮廓
	virtual QPolygonF polygon();
};

#endif