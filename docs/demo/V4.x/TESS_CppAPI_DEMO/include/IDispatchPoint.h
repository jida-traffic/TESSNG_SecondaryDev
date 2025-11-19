/********************************************************************************
* 发车点接口，由TESS NG实现，用户可以借此调用接口方法，获取发车点一些基本属性，增加发车间隔
*********************************************************************************/

#ifndef __IDispatchPoint__
#define __IDispatchPoint__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"

#include <QPolygonF>

class ILink;

class TESSINTERFACES_EXPORT IDispatchPoint {
public:
	virtual ~IDispatchPoint() = default;
	/*发车点ID	*/
	virtual long id();
	//发车点名称
	virtual QString name();
	//发车点所在路段
	virtual ILink *link();
	//设置是否被动态修改，默认情况下发车信息被动态修改后，整个文件不能保存，以免破坏原有发车设置
	virtual void setDynaModified(bool bModified);
	//增加发车间隔
	virtual long addDispatchInterval(long vehiCompId, int interval, int vehiCount);
	//修改发车间隔，只能修改当前正在仿真的间隔内容，及后续间隔内容，当前正在仿真的隔间只能修改发车数，且不能导致设定的发车数小于已发车辆数，后续间隔内容可以修改车型组成、发车数
	virtual bool updateDispatchInterval(long departureIntervald, int vehiCount, long vehiCompId = 0);
	//删除发车信息
	virtual void removeDispatchIntervals();
	//发车间隔
	virtual QList<Online::DispatchInterval> dispatchIntervals();

	//多边型轮廓
	virtual QPolygonF polygon();

};

#endif