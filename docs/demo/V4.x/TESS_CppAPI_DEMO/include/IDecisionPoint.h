/********************************************************************************
* 决策点接口，由TESS NG实现，用户可以借此调用接口方法，获取决策点一些基本属性，进行一些操作
*********************************************************************************/

#ifndef __IDecisionPoint__
#define __IDecisionPoint__

#include "tessinterfaces_global.h"
#include "_tables.h"
#include "UnitChange.h"

#include <QPolygonF>

class ILink;
class IRouting;

class TESSINTERFACES_EXPORT IDecisionPoint
{
public:
	virtual ~IDecisionPoint() = default;
	/*决策点ID	*/
	virtual long id();
	//决策点名称
	virtual QString name();
	//决策点所在路段
	virtual ILink *link();
	//距路段起点距离，单位像素
	virtual qreal distance(UnitOfMeasure unit = UnitOfMeasure::Default);
	//相关决策路径
	virtual QList<IRouting*> routings();
	//决策流量比
	virtual QList<_RoutingFLowRatio> routingFLowRatios();
	//随机分配比例
	virtual qreal randomRatio();
	//设置是否被动态修改，默认情况下发车信息被动态修改后，整个文件不能保存，以免破坏原有发车设置
	virtual void setDynaModified(bool bModified);
	//多边型轮廓
	virtual QPolygonF polygon();
};

#endif