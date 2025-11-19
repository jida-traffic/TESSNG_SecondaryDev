#ifndef __IModelSimu__
#define __IModelSimu__

#include "tessinterfaces_global.h"

#include <QObject>

class IVehicle;
class ILaneConnector;
class IBusStationLine;
class ILaneObject;
class IDecisionPoint;
class IRouting;

class TESSINTERFACES_EXPORT IModelSimu
{
public:
	IModelSimu() {}
	virtual ~IModelSimu() {}

	//仿真前的准备
	virtual void beforeStart(bool& keepOn);

	//仿真结束后的处理
	virtual void afterStop();

	//一个批次计算后的处理
	virtual void afterOneStep();

	virtual void afterStep(IVehicle* pIVehicle);

	//计算加速度
	virtual bool calcAcce(IVehicle* pIVehicle, qreal& acce);

	//重新设置加速度
	virtual bool reSetAcce(IVehicle* pIVehicle, qreal& inOutAcce);

	// 重新设置速度
	virtual bool reSetSpeed(IVehicle* pIVehicle, qreal& inOutSpeed);

	virtual void initVehicle(IVehicle* pIVehicle);

	virtual void beforeNextPoint(IVehicle* pIVehicle, bool& keepOn);

	// virtual void afterOneStep();
	/**
	* 计算车辆后续“车道连接”，此时车辆正跨出当前路段，驶到pPreLaneConnector，可以通过此方法改变车辆后续方向。
	如果返回的“车道连接”不在原有路径上，车辆在下一个路段尽头会消失。为了解决这个问题可以在返回新的“车道连接”
	前重新设置路径（经过返回的“车道连接”），或将路径设为空
	*/
	virtual ILaneConnector* candidateLaneConnector(IVehicle* pIVehicle, ILaneConnector* pPreLaneConnector);

	////自由左变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由左变道
	virtual void beforeToLeftFreely(IVehicle* pIVehicle, bool& bKeepOn);

	////自由右变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由右变道
	virtual void beforeToRightFreely(IVehicle* pIVehicle, bool& bKeepOn);

	// 计算限制车道序号：如管制、危险等，最右侧编号为0
	virtual QList<int> calcLimitedLaneNumber(IVehicle* pIVehicle);

	// 计算安全变道距离
	virtual bool calcChangeLaneSafeDist(IVehicle* pIVehicle, qreal& dist);

	//仿真结束后插件写文件方法
	virtual void writeSimuResult(QString dataDir);

	// 离开轨迹
	virtual bool leaveOffChangingTrace(IVehicle* pIVehicle, qreal differ, qreal& s) ;


	//公交到站 pIVehicle:公交车, pStationLine:站点线路，alightingCount:下客人数，alightingTime:下客所需总时间，boardingCount：上客人数，boardingTime:上客所需总时间
	virtual void busArriving(IVehicle* pIVehicle, IBusStationLine* pStationLine, int alightingCount, int alightingTime, int boardingCount, int boardingTime);

	//公交离站
	virtual void busOnLeaving(IVehicle* pIVehicle, IBusStationLine* pStationLine);

	virtual bool reCalcAngle(IVehicle* pIVehicle, qreal& outAngle);

	//是否应该变道接客 针对营运车辆 返回是否 targetLaneNumber返回应该变道的目标车道序号
	virtual bool shouldHookUpPedestrian(IVehicle* pIVehicle, long& laneNumber);

	//离开当前ILaneObject去另一ILaneObject时调用，如果pAnotherLaneObj为空，表明车辆正驰了路网
	virtual void leaveForNextLaneObj(IVehicle* pIVehicle, ILaneObject* pCurrLaneObj, ILaneObject* pNextLaneObj);

	virtual bool beforeLeaveCurrRoad(IVehicle* pIVehicle, bool& keepOn);

	//分配决策路径后的处理
	virtual void afterDistriRouting(IVehicle* pIVehicle, IDecisionPoint* pIDeciPoint, IRouting* pIRouting);
};

#endif