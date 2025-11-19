/********************************************************************************
* TESS NG仿真运算层接口，此接口由TESS NG实现，用户可以通过此接口启动停止仿真，
* 在仿真过程获取所有车辆运行状态、指定车辆运行轨迹、几种检测器检测数据等
*********************************************************************************/

#ifndef SIMUINTERFACE_H
#define SIMUINTERFACE_H

#include <QObject>
#include <QPolygonF>

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"
#include "UnitChange.h"

class CustomerSimulator;
class IVehicleSupervisor;
class IVehicleDrivingManagerTask;
class IVehicleDrivingTask;
class IVehicle;
class IBusLine;
class IPedestrian;

class TESSINTERFACES_EXPORT SimuInterface
{
public:
	virtual IVehicleDrivingManagerTask* vehicleDrivingManagerTask();
	virtual QList<IVehicleDrivingTask*> vehicleDrivingTasks();
	//仿真时间是否由现实时间确定
	virtual bool byCpuTime();
	//设置是否由现实时间(处理器运算时间)确定仿真时间,如果正在仿真计算则不进行设置并返回false，否则设置参数并返回true
	virtual bool setByCpuTime(bool bByCpuTime);
	//启动仿真
	virtual void startSimu();
	//单步仿真
	virtual void stepSimu();
	//暂停仿真
	virtual void pauseSimu();
	//结束仿真
	virtual void stopSimu();
	//仿真是否在进行
	virtual bool isRunning();
	//仿真是否处于暂停状态
	virtual bool isPausing();
	//是否记录车辆轨迹
	virtual bool isRecordTrace();
	//设置是否记录车辆轨迹
	virtual void setIsRecordTrace(bool bRecord);
	//是否记录行人轨迹
	virtual bool isRecordPedestrianTrace();
	//设置是否记录行人轨迹
	virtual void setIsRecordPedestrianTrace(bool bRecord);
	//是否记录信号灯灯态
	virtual bool isRecordSignalLampStatus();
	//设置是否记录信号灯灯态
	virtual void setIsRecordSignalLampStatus(bool bRecord);
	//预期仿真时长
	virtual long simuIntervalScheming();
	//设置预期仿真时长
	virtual void setSimuIntervalScheming(long interval);
	//仿真精度
	virtual int simuAccuracy();
	//设置仿真精度
	virtual void setSimuAccuracy(int accuracy);
	//加速倍数
	virtual int acceMultiples();
	//设置加速倍数
	virtual void setAcceMultiples(int multiples);	
	//设置工作线程数
	virtual void setThreadCount(int count);
	//随机数种子
	virtual int randSeed();
	//设置随机数种子
	virtual void setRandSeed(int seed);
	//实际加速倍数
	virtual qreal acceMultiplesReally();
	//当前批次
	virtual long batchNumber();
	//指定批次的实时时间t
	virtual qint64 timeOnBatchNumber(long batchNumber);
	//当前批次实际时间
	virtual qreal batchIntervalReally();
	//仿真开始的现实时间
	virtual qint64 startMSecsSinceEpoch();
	//仿真结束的现实时间
	virtual qint64 stopMSecsSinceEpoch();
	//当前已仿真时间
	virtual long simuTimeIntervalWithAcceMutiples();
	//仿真到指定批次时总延误(毫秒)
	//virtual long delayTimeOnBatchNumber(long batchNumber);
	//车辆总数
	virtual long vehiCountTotal();
	//路网上运行的车辆数
	virtual long vehiCountRunning();
	//根据车辆ID获取车辆对象
	virtual IVehicle* getVehicle(long vehiId);
	//在运行所有车辆
	virtual QList<IVehicle*> allVehiStarted();
	//所有车辆
	virtual QList<IVehicle*> allVehicle();
	//获取所有在运行车辆状态，包括轨迹
	virtual QList<Online::VehicleStatus> getVehisStatus(long batchNumber = 0, UnitOfMeasure unit = UnitOfMeasure::Default);
	//获取指定车辆运行轨迹
	virtual QList<Online::VehiclePosition> getVehiTrace(long vehiId, UnitOfMeasure unit = UnitOfMeasure::Default);
	//获取当前所有信号灯组相位颜色
	virtual QList<Online::SignalPhaseColor> getSignalPhasesColor();
	//获取当前仿真时间完成穿越采集器的所有车辆信息
	virtual QList<Online::VehiInfoCollected> getVehisInfoCollected();
	//获取最近集计时间段内采集器采集的所有车辆集计信息
	virtual QList<Online::VehiInfoAggregated> getVehisInfoAggregated();
	//获取当前仿真时间排队计数器计数的车辆排队信息
	virtual QList<Online::VehiQueueCounted> getVehisQueueCounted();
	//获取最近集计时间段内排队计数器集计数据
	virtual QList<Online::VehiQueueAggregated> getVehisQueueAggregated();
	//获取当前仿真时间行程时间检测器完成的行程时间检测信息
	virtual QList<Online::VehiTravelDetected> getVehisTravelDetected();
	//获取最近集计时间段内行程时间检测器集计数据
	virtual QList<Online::VehiTravelAggregated> getVehisTravelAggregated();
	//动态创建车辆, bInitLaneObject:是否初始化车道或车道连接，如果false则从车道或车道连接起点发车
	virtual IVehicle* createGVehicle(Online::DynaVehiParam dynaVehi, bool bStarted = true, bool bInitLaneObject = true);
	//动态创建公交车
	virtual IVehicle* createBus(IBusLine* pBusLine, qreal startSimuDateTime);
	//创建快照，需要TESSNG分布式组件支持
	virtual QString catchSnapshotAsString();
	//加载快照，需要TESSNG分布式组件支持
	virtual bool loadSnapshotFromString(QString data);
	//停止车辆运行
	virtual void stopVehicleDriving(IVehicle* pVehicle);

	//主界面工具栏按钮功能
	//暂停或恢复仿真
	virtual void pauseSimuOrNot();

	virtual QList<IVehicle*> vehisInLink(long linkId);
	virtual QList<IVehicle*> vehisInLane(long laneId);
	virtual QList<IVehicle*> vehisInConnector(long connectorId);
	virtual QList<IVehicle*> vehisInLaneConnector(long connectorId, long fromLaneId, long toLaneId);

	//排队计数器最近一次排队信息
	virtual bool queueRecently(long queueCounterId, qreal& queueLength, int& vehiCount, UnitOfMeasure unit = UnitOfMeasure::Default);

	//行人相关
	//根据行人面域id获取当前时间面域上所有行人的状态信息
	virtual QList<Online::Pedestrian::PedestrianStatus> getPedestriansStatusByRegionId(long regionId);
	//在运行所有行人
	virtual QList<IPedestrian*> allPedestrianStarted();
	//已经生成的行人数量
	virtual int getGeneratedPedestrianCount();
	//正在运行的行人数量
	virtual int	getActivePedestrianCount();
};

#endif // SIMUINTERFACE_H