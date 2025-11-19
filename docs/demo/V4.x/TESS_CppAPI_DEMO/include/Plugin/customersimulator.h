/********************************************************************************
* 用户插件参于在仿真与TESS NG交互，用户实现全部或部分方法扩展TESS NG功能
*********************************************************************************/

#ifndef CUSTOMERSIMULATOR_H
#define CUSTOMERSIMULATOR_H

#include <unordered_map>

#include <QObject>
#include <QPainter>
#include <QGraphicsItem>
#include <QVector3D>

#include "../tessinterfaces_global.h"
#include "_datastruct.h"
#include "../UnitChange.h"

class ISignalPhase;
class ISignalLamp;
class IVehicle;
class ILink;
class GraphicsObject;
class ILaneConnector;
class IBusLine;
class IBusStationLine;
class IDecisionPoint;
class IRouting;

class TESSINTERFACES_EXPORT CustomerSimulator
{
public:
	CustomerSimulator() {}
	virtual ~CustomerSimulator();

	//仿真前的准备
	virtual void beforeStart(bool& keepOn);
	//仿真起动后的处理
	virtual void afterStart();
	//仿真暂停后处理
	virtual void afterPause();
	//仿真结束后的处理
	virtual void afterStop();
	//计算仿真参数
	virtual bool calcSimuConfig(Online::SimuConfig& config);
	//计算动态发车信息
	virtual QList<Online::DispatchInterval> calcDynaDispatchParameters();
	//一个或一次数据来源里保存的所有决策点在一个时间间隔的路径流量分配信息
	virtual QList<Online::DecipointFlowRatioByInterval> calcDynaFlowRatioParameters();
	//一个或一次数据来源里保存的所有信号灯组的信号控制信息 
	virtual QList<Online::SignalContralParam> calcDynaSignalContralParameters();
	//初始车辆
	virtual void initVehicle(IVehicle* pIVehicle);
	//创建公交车辆前的预处理
	virtual void beforeCreateGVehiclesForBusLine(IBusLine* pBusLine, bool& keepOn);
	//计算信号灯色前的预处理
	virtual bool beforeCalcLampColor(bool& keepOn);
	//计算信号灯颜色
	virtual bool calcLampColor(ISignalLamp* pSignalLamp);
	//信号相位同期开始
	virtual void signalPhaseOnPeriodBegin(ISignalPhase* pIPhase);
	//重新计算是否要左变道
	virtual bool reCalcToLeftLane(IVehicle* pIVehicle);
	//重新计算是否要右变道
	virtual bool reCalcToRightLane(IVehicle* pIVehicle);
	//自由左变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由左变道
	virtual void beforeToLeftFreely(IVehicle* pIVehicle, bool &bKeepOn);
	//自由右变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由右变道
	virtual void beforeToRightFreely(IVehicle* pIVehicle, bool& bKeepOn);
	//重新计算是否自由左变道
	virtual bool reCalcToLeftFreely(IVehicle* pIVehicle);
	//重新计算是否自由右变道
	virtual bool reCalcToRightFreely(IVehicle* pIVehicle);
	//重新计算是否撤销变道
	virtual bool reCalcDismissChangeLane(IVehicle* pIVehicle);
	//重新计算期望速度
	virtual bool reCalcdesirSpeed(IVehicle* pIVehicle, qreal& inOutDesirSpeed);
	//重新计算期望速度，支持单位参数
	virtual bool reCalcdesirSpeed(IVehicle* pIVehicle, qreal& inOutDesirSpeed, UnitOfMeasure* unit);
	//重新设置跟驰类型，在计算加速度的过程中被调用
	virtual bool reSetFollowingType(IVehicle* pIVehicle, int& outTypeValue);
	//重新设置跟驰的安全时距及安全距离
	virtual bool reSetFollowingParam(IVehicle* pIVehicle, qreal& inOutSafeInterval, qreal& inOutSafeDistance);	
	//重新设置跟驰的安全时距及安全距离，支持单位参数
	virtual bool reSetFollowingParam(IVehicle* pIVehicle, qreal& inOutSafeInterval, qreal& inOutSafeDistance, UnitOfMeasure* unit);
	//按机动车或非机动车重新设置跟驰模型参数，对所有车辆产生影响，支持仿真过程设置
	virtual QList<Online::FollowingModelParam> reSetFollowingParams();
	//按车辆类型设置跟驰参数，支持仿真过程设置
	virtual QList<Online::FollowingParamByVehiType> reSetFollowingParamsByVehiType();
	//重新设置前车距
	virtual bool reSetVehicleFront(IVehicle* pIVehicle, IVehicle*& pVehiFront);
	//重新设置前车距及安全跟车距离
	virtual bool reSetDistanceFront(IVehicle* pIVehicle, qreal& distance, qreal& s0);	
	//重新设置前车距及安全跟车距离，支持单位参数
	virtual bool reSetDistanceFront(IVehicle* pIVehicle, qreal& distance, qreal& s0, UnitOfMeasure* unit);
	//重新设置速度
	virtual bool reSetSpeed(IVehicle* pIVehicle, qreal& inOutSpeed);
	//重新设置速度，支持单位参数
	virtual bool reSetSpeed(IVehicle* pIVehicle, qreal& inOutSpeed, UnitOfMeasure* unit);
	//汇入前可以由插件处理
	virtual void beforeMergingToLane(IVehicle* pIVehicle, bool& keepOn);
	//一个批次计算后的处理
	virtual void afterOneStep();
	//与工作线程同步，不安全，但效率高
	virtual void duringOneStep();
	//计算下一道路前的处理
	virtual void beforeNextRoad(IVehicle* pIVehicle, QGraphicsItem*& pRoad, bool& keepOn);
	//计算当车辆离开路段时后续可经过的车道连接, 此方法在计算后续连接段时时被调用，此时车辆大多数情况没有跨出路段。 lLaneConnector是已计算出的当前车道可达的所有车道连接，用户可以从中筛选或重新计算
	virtual QList<ILaneConnector*> candidateLaneConnectors(IVehicle* pIVehicle, QList<ILaneConnector*> lInLC);
	/*
	计算车辆后续“车道连接”，此时车辆正跨出当前路段，将驶到pCurrLaneConnector。此方法可以改变后续“车道连接”。
	如果返回的“车道连接”为空，TESSNG会忽略此方法的调用。
	如果返回的“车道连接”不在原有路径上，或者此方法设置了新路径且新路径不经过返回的“车道连接”，TESSNG调用此方法后会将路径设为空。
	*/
	virtual ILaneConnector* candidateLaneConnector(IVehicle* pIVehicle, ILaneConnector* pCurrLaneConnector);
	//计算下一点位置前处理
	virtual void beforeNextPoint(IVehicle* pIVehicle, bool& keepOn);
	//计算下一点
	virtual bool nextPoint(IVehicle* pIVehicle, bool& bResult);
	//计算限制车道序号：如管制、危险等，最右侧编号为0
	virtual QList<int> calcLimitedLaneNumber(IVehicle* pIVehicle);
	//由车道确定的限制车速（最高速度, 公里/小时）,laneNumber:车道序号，最右侧编号为0
	virtual bool calcSpeedLimitByLane(ILink* pILink, int laneNumber, qreal& outSpeed);
	//由车道确定的限制车速（最高速度, 公里/小时）,laneNumber:车道序号，最右侧编号为0，支持单位参数
	virtual bool calcSpeedLimitByLane(ILink* pILink, int laneNumber, qreal& outSpeed, UnitOfMeasure* unit);
	//计算当前最大限速
	virtual bool calcMaxLimitedSpeed(IVehicle* pIVehicle, qreal& inOutLimitedSpeed);
	//计算当前最大限速，支持单位参数
	virtual bool calcMaxLimitedSpeed(IVehicle* pIVehicle, qreal& inOutLimitedSpeed, UnitOfMeasure* unit);
	//计算到事件对象距离，如到事故区、施工区的距离
	virtual bool calcDistToEventObj(IVehicle* pIVehicle, qreal& dist);
	//计算到事件对象距离，如到事故区、施工区的距离，支持单位参数
	virtual bool calcDistToEventObj(IVehicle* pIVehicle, qreal& dist, UnitOfMeasure* unit);
	//计算安全变道距离
	virtual bool calcChangeLaneSafeDist(IVehicle* pIVehicle, qreal& dist);
	//计算安全变道距离，支持单位参数
	virtual bool calcChangeLaneSafeDist(IVehicle* pIVehicle, qreal& dist, UnitOfMeasure* unit);
	//分配决策路径后的处理
	virtual void afterDistriRouting(IVehicle* pIVehicle, IDecisionPoint* pIDeciPoint, IRouting* pIRouting);
	//计算下一点后的处理
	virtual void afterStep(IVehicle* pIVehicle);
	/*
	重新核实车辆是否存在错误，包括车间关系，TESSNG判断车间关系存在错误时会调用此方法，如果此方法返回false表明开发者不进行判断，返回true表明开发者有判断，判断结果保存在bError。
	如果返回false，或者返回true且bError为true，TESSNG移除问题车辆
	*/
	virtual bool reCheckIfError(IVehicle* pIVehicle, bool& bError);
	/*
	判断当前车辆及其它车辆(主要是相邻车辆)是否存在问题，如果存在错误则返问题车辆，TESSNG将移除问题车辆，否则返回空指针
	*/
	virtual IVehicle* checkIfErrorForSelfOrOthers(IVehicle* pIVehicle);
	//停车前
	virtual void beforeParking(IVehicle* pIVehicle);
	//公交车到站停稳，还没有开门调用一次
	virtual void busOnArriving(IVehicle* pIVehicle, IBusStationLine* pStationLine);
	/*
	  公交到站 pIVehicle : 公交车, pStationLine : 站点线路，alightingCount : 下客人数，alightingTime : 下客所需总时间，boardingCount：上客人数，boardingTime : 上客所需总时间
	  公交车到站后被调用，经过停车、上下客，这个过程一直被调用，传入的下客人数和下客时间不变，上客人数和上客时间可能变化，因为在下客的过程中会有新的乘客到站并上车
	*/
	virtual void busArriving(IVehicle* pIVehicle, IBusStationLine* pStationLine, int alightingCount, int alightingTime, int boardingCount, int boardingTime);
	/*
	  公交车完成上下客，关门准备离开时被调用
	  参数 pIVehicle : 公交车, pStationLine : 站点线路，alightingCount : 下客人数，alightingTime : 下客所需总时间，boardingCount：上客人数，boardingTime : 上客所需总时间, passengerCount:上下客后车上的乘客数
	*/
	virtual void busOnLeaving(IVehicle* pIVehicle, IBusStationLine* pStationLine, int alightingCount, int alightingTime, int boardingCount, int boardingTime, int passengerCount);
	//计算加速度
	virtual bool calcAcce(IVehicle* pIVehicle, qreal& acce);
	//计算加速度，支持单位参数
	virtual bool calcAcce(IVehicle* pIVehicle, qreal& acce, UnitOfMeasure* unit);
	//是否计算车辆3D属性，如欧拉角等
	virtual bool isCalcVehicleVector3D();
	//是否输出检测器数据
	virtual bool isExportCollecterInfo();
	//计算欧拉角, bPosiDire:车头方向是否正向计算，如果bPosiDire为false则反向计算
	virtual QVector3D calcVehicleEuler(IVehicle* pIVehicle, bool bPosiDire = true);
	//重新设置加速度
	virtual bool reSetAcce(IVehicle* pIVehicle, qreal& inOutAcce);
	//重新设置加速度，支持单位参数
	virtual bool reSetAcce(IVehicle* pIVehicle, qreal& inOutAcce, UnitOfMeasure* unit);
	//计算跟驰类型后处理
	virtual void afterCalcTracingType(IVehicle* pIVehicle);
	//在变轨迹上
	virtual bool travelOnChangingTrace(IVehicle* pIVehicle);
	//离开变轨
	virtual bool leaveOffChangingTrace(IVehicle* pIVehicle, qreal differ, qreal& s);
	//离开当前ILaneObject去另一ILaneObject时调用，如果pAnotherLaneObj为空，表明车辆正驰了路网
	virtual void leaveForNextLaneObj(IVehicle* pIVehicle, ILaneObject* pCurrLaneObj, ILaneObject* pNextLaneObj);
	virtual bool boundingRect(IVehicle* pIVehicle, QRectF& outRect) const;
	virtual bool shape(IVehicle* pIVehicle, QPainterPath& outShape) const;
	//绘制车辆
	virtual bool paintVehicle(IVehicle* pIVehicle, QPainter* painter);
	//绘制车辆，绘制前将车辆对象旋转指定角度
	virtual bool paintVehicleWithRotation(IVehicle* pIVehicle, QPainter* painter, qreal& inOutRotation);
	//绘制车辆后的再绘制，客户可在此方法增加绘制内容
	virtual void rePaintVehicle(IVehicle* pIVehicle, QPainter* painter);
	//计算角度后的处理，如重新计算，可修改已算得角度
	virtual bool reCalcAngle(IVehicle* pIVehicle, qreal& outAngle);
	//是否停车运行，TESSNG在计算下一点位置后调用，判断是否要停止车辆pIVehicle的运行
	virtual bool isStopDriving(IVehicle* pIVehicle);
	//是否计算距离限速区距离
	virtual bool isCalcDistToReduceArea();
	//记录车辆速度和位置的最近时间,单位：秒
	virtual bool recentTimeOfSpeedAndPos(int& timeInterval);
	//输出车辆轨迹到指定路径
	virtual void exportVehiTrace(QString dirPath);
	//车辆停止运行前处理
	virtual void beforeStopVehicle(IVehicle* pIVehicle);
	//车辆停车运行后处理
	virtual void afterStopVehicle(IVehicle* pIVehicle);
	//重新设置仿真结果保存的路径
	virtual bool reSetSimuResultDir(QString& inOutResultDir);
	//仿真结束后插件写文件方法
	virtual void writeSimuResult(QString dataDir);
	//车辆的运行信息
	virtual QString vehiRunInfo(IVehicle* pIVehicle);
	//用快照恢复仿真场景
	virtual bool recoveredSnapshot();
	virtual void catchCustomConfigSnapshot(std::unordered_map<std::string, std::string>& configMap);
	virtual void recoverCustomConfigSnapshot(const std::unordered_map<std::string, std::string>& configMap);
	// 更新在线指令
	virtual void updateOnlineCmds(qint64 ts, long simuTime);
	// 获取批在线指令
	virtual Online::CmdBatch* getCmdBatch();
};

#endif // CUSTOMERSIMULATOR_H