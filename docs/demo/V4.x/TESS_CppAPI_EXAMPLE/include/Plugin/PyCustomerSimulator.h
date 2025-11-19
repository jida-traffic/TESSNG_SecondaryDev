#ifndef __PyCustomerSimulator__
#define __PyCustomerSimulator__

#include "customersimulator.h"
#include "_objtypes.h"

class TESSINTERFACES_EXPORT PyCustomerSimulator : public CustomerSimulator
{
public:
	PyCustomerSimulator() {}
	virtual ~PyCustomerSimulator() = default;
	//仿真前的准备
	void beforeStart(bool& keepOn) override;
	//仿真前的准备
	virtual void ref_beforeStart(objbool& ref_keepOn);
	//仿真起动后的处理
	virtual void afterStart() override;
	//仿真暂停后处理
	virtual void afterPause() override;
	//仿真结束后的处理
	virtual void afterStop() override;
	//计算动态发车信息
	virtual QList<Online::DispatchInterval> calcDynaDispatchParameters() override;
	//一个或一次数据来源里保存的所有决策点在一个时间间隔的路径流量分配信息
	virtual QList<Online::DecipointFlowRatioByInterval> calcDynaFlowRatioParameters() override;
	//一个或一次数据来源里保存的所有信号灯组的信号控制信息 
	virtual QList<Online::SignalContralParam> calcDynaSignalContralParameters() override;
	//初始车辆
	virtual void initVehicle(IVehicle* pIVehicle) override;
	//创建公交车辆前的预处理
	void beforeCreateGVehiclesForBusLine(IBusLine* pBusLine, bool& keepOn) override;
	//创建公交车辆前的预处理
	virtual void ref_beforeCreateGVehiclesForBusLine(IBusLine* pBusLine, objbool& ref_keepOn);
	//计算信号灯色前的预处理
	bool beforeCalcLampColor(bool& keepOn) override;
	//计算信号灯色前的预处理
	virtual bool ref_beforeCalcLampColor(objbool& ref_keepOn);
	//计算信号灯颜色
	virtual bool calcLampColor(ISignalLamp* pSignalLamp) override;
	//信号相位同期开始
	virtual void signalPhaseOnPeriodBegin(ISignalPhase* pIPhase) override;
	//自由左变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由左变道
	void beforeToLeftFreely(IVehicle* pIVehicle, bool &bKeepOn) override;
	//自由左变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由左变道
	virtual void ref_beforeToLeftFreely(IVehicle* pIVehicle, objbool& ref_keepOn);
	//自由右变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由右变道
	void beforeToRightFreely(IVehicle* pIVehicle, bool& bKeepOn) override;
	//自由右变道前处理，如果bKeepOn被赋值为false，TESSNG不再计算是否自由右变道
	virtual void ref_beforeToRightFreely(IVehicle* pIVehicle, objbool& ref_keepOn);
	//重新计算是否要左变道
	virtual bool reCalcToLeftLane(IVehicle* pIVehicle) override;
	//重新计算是否要右变道
	virtual bool reCalcToRightLane(IVehicle* pIVehicle) override;
	//重新计算是否自由左变道
	virtual bool reCalcToLeftFreely(IVehicle* pIVehicle) override;
	//重新计算是否自由右变道
	virtual bool reCalcToRightFreely(IVehicle* pIVehicle) override;
	//重新计算是否撤销变道
	virtual bool reCalcDismissChangeLane(IVehicle* pIVehicle) override;
	//重新计算期望速度
	bool reCalcdesirSpeed(IVehicle* pIVehicle, qreal& inOutDesirSpeed) override;
	//重新计算期望速度
	virtual bool ref_reCalcdesirSpeed(IVehicle* pIVehicle, objreal& ref_desirSpeed);
	//重新设置跟驰类型，在计算加速度的过程中被调用
	bool reSetFollowingType(IVehicle* pIVehicle, int& outTypeValue) override;
	//重新设置跟驰类型，在计算加速度的过程中被调用
	virtual bool ref_reSetFollowingType(IVehicle* pIVehicle, objint& ref_outTypeValue);
	//按机动或非机动车重新设置跟驰模型参数，对所有车辆产生影响，支持仿真过程设置
	virtual QList<Online::FollowingModelParam> reSetFollowingParams() override;
	//按车辆类型设置跟驰参数，支持仿真过程设置
	virtual QList<Online::FollowingParamByVehiType> reSetFollowingParamsByVehiType() override;
	//重新设置跟驰的安全时距及安全距离
	bool reSetFollowingParam(IVehicle* pIVehicle, qreal& inOutSafeInterval, qreal& inOutSafeDistance) override;
	//重新设置跟驰的安全时距及安全距离
	virtual bool ref_reSetFollowingParam(IVehicle* pIVehicle, objreal& ref_inOutSafeInterval, objreal& ref_inOutSafeDistance);
	//重新设置前车
	bool reSetVehicleFront(IVehicle* pIVehicle, IVehicle*& pVehiFront) override;
	//重新设置前车距及安全跟车距离
	bool reSetDistanceFront(IVehicle* pIVehicle, qreal& distance, qreal& s0) override;
	//重新设置前车距及安全跟车距离
	virtual bool ref_reSetDistanceFront(IVehicle* pIVehicle, objreal& distance, objreal& s0);
	//重新设置速度
	bool reSetSpeed(IVehicle* pIVehicle, qreal& inOutSpeed) override;
	//重新设置速度
	virtual bool ref_reSetSpeed(IVehicle* pIVehicle, objreal& ref_inOutSpeed);
	//汇入前可以由插件处理
	virtual void beforeMergingToLane(IVehicle* pIVehicle, bool& keepOn) override;
	//汇入前可以由插件处理
	virtual void ref_beforeMergingToLane(IVehicle* pIVehicle, objbool& ref_keepOn);
	//一个批次计算后的处理
	virtual void afterOneStep() override;
	//与工作线程同步，不安全，但效率高
	virtual void duringOneStep() override;
	//计算下一道路前的处理
	void beforeNextRoad(IVehicle* pIVehicle, QGraphicsItem*& pRoad, bool& keepOn) override;
	//计算下一道路前的处理
	virtual void ref_beforeNextRoad(IVehicle* pIVehicle, QGraphicsItem* pRoad, objbool& ref_keepOn);
	//计算当车辆离开路段时后续可经过的车道连接, lLaneConnector是已计算出的当前车道可达的所有车道连接，用户可以从中筛选或重新计算
	virtual QList<ILaneConnector*> candidateLaneConnectors(IVehicle* pIVehicle, QList<ILaneConnector*> lInLC) override;
	/*
	计算车辆后续“车道连接”，此时车辆正跨出当前路段，将驶到pCurrLaneConnector。此方法可以改变后续“车道连接”。
	如果返回的“车道连接”为空，TESSNG会忽略此方法的调用。
	如果返回的“车道连接”不在原有路径上，或者此方法设置了新路径且新路径不经过返回的“车道连接”，TESSNG调用此方法后会将路径设为空。
	*/
	virtual ILaneConnector* candidateLaneConnector(IVehicle* pIVehicle, ILaneConnector* pCurrLaneConnector) override;
	//计算下一点位置前处理
	void beforeNextPoint(IVehicle* pIVehicle, bool& keepOn) override;
	//计算下一点位置前处理
	virtual void ref_beforeNextPoint(IVehicle* pIVehicle, objbool& ref_keepOn);
	//计算下一点
	bool nextPoint(IVehicle* pIVehicle, bool& bResult) override;
	//计算下一点
	virtual bool ref_nextPoint(IVehicle* pIVehicle, objbool& ref_bResult);

	//计算限制车道序号：如管制、危险等，最右侧编号为0
	virtual QList<int> calcLimitedLaneNumber(IVehicle* pIVehicle) override;
	//由车道确定的限制车速（最高速度, 公里/小时）,laneNumber:车道序号，最右侧编号为0
	bool calcSpeedLimitByLane(ILink* pILink, int laneNumber, qreal& outSpeed) override;
	//由车道确定的限制车速（最高速度, 公里/小时）,laneNumber:车道序号，最右侧编号为0
	virtual bool ref_calcSpeedLimitByLane(ILink* pILink, int laneNumber, objreal& ref_outSpeed);
	//计算当前最大限速
	bool calcMaxLimitedSpeed(IVehicle* pIVehicle, qreal& inOutLimitedSpeed) override;
	//计算当前最大限速
	virtual bool ref_calcMaxLimitedSpeed(IVehicle* pIVehicle, objreal& ref_inOutLimitedSpeed);
	//计算到事件对象距离，如到事故区、施工区的距离
	bool calcDistToEventObj(IVehicle* pIVehicle, qreal& dist) override;
	//计算到事件对象距离，如到事故区、施工区的距离
	virtual bool ref_calcDistToEventObj(IVehicle* pIVehicle, objreal& ref_dist);
	//计算安全变道距离
	bool calcChangeLaneSafeDist(IVehicle* pIVehicle, qreal& dist) override;
	//计算安全变道距离
	virtual bool ref_calcChangeLaneSafeDist(IVehicle* pIVehicle, objreal& ref_dist);
	//分配决策路径后的处理
	virtual void afterDistriRouting(IVehicle* pIVehicle, IDecisionPoint* pIDeciPoint, IRouting* pIRouting) override;
	//计算下一点后的处理
	virtual void afterStep(IVehicle* pIVehicle) override;
	/*
	重新核实车辆是否存在错误，包括车间关系，TESSNG判断车间关系存在错误时会调用此方法，如果此方法返回false表明开发者不进行判断，返回true表明开发者有判断，判断结果保存在bError。
	如果返回false，或者返回true且bError为true，TESSNG移除问题车辆
	*/
	bool reCheckIfError(IVehicle* pIVehicle, bool& bError) override;
	/*
	重新核实车辆是否存在错误，包括车间关系，TESSNG判断车间关系存在错误时会调用此方法，如果此方法返回false表明开发者不进行判断，返回true表明开发者有判断，判断结果保存在bError。
	如果返回false，或者返回true且bError为true，TESSNG移除问题车辆
	*/
	virtual bool ref_reCheckIfError(IVehicle* pIVehicle, objbool& ref_inOutError);
	/*
	判断当前车辆及其它车辆(主要是相邻车辆)是否存在问题，如果存在问题则返问题车辆，TESSNG将移除问题车辆，否则返回空指针
	*/
	virtual IVehicle* checkIfErrorForSelfOrOthers(IVehicle* pIVehicle) override;
	//停车前
	virtual void beforeParking(IVehicle* pIVehicle) override;
	//公交车到站停稳，还没有开门调用一次
    virtual void busOnArriving(IVehicle* pIVehicle, IBusStationLine* pStationLine) override;
	/*
	  公交到站 pIVehicle : 公交车, pStationLine : 站点线路，alightingCount : 下客人数，alightingTime : 下客所需总时间，boardingCount：上客人数，boardingTime : 上客所需总时间
	  公交车到站后被调用，经过停车、上下客，这个过程一直被调用，传入的下客人数和下客时间不变，上客人数和上客时间可能变化，因为在下客的过程中会有新的乘客到站并上车
	*/
	virtual void busArriving(IVehicle* pIVehicle, IBusStationLine* pStationLine, int alightingCount, int alightingTime, int boardingCount, int boardingTime) override;
	/*
	  公交车完成上下客，关门准备离开时被调用
	  参数 pIVehicle : 公交车, pStationLine : 站点线路，alightingCount : 下客人数，alightingTime : 下客所需总时间，boardingCount：上客人数，boardingTime : 上客所需总时间, passengerCount:上下客后车上的乘客数
	*/
	virtual void busOnLeaving(IVehicle* pIVehicle, IBusStationLine* pStationLine, int alightingCount, int alightingTime, int boardingCount, int boardingTime, int passengerCount) override;
	//计算加速度
	bool calcAcce(IVehicle* pIVehicle, qreal& acce) override;
	//计算加速度
	virtual bool ref_calcAcce(IVehicle* pIVehicle, objreal& acce);
	//是否计算车辆3D属性，如欧拉角等
	virtual bool isCalcVehicleVector3D() override;
	//是否输出检测器数据
	virtual bool isExportCollecterInfo() override;
	//计算欧拉角, bPosiDire:车头方向是否正向计算，如果bPosiDire为false则反向计算
	virtual QVector3D calcVehicleEuler(IVehicle* pIVehicle, bool bPosiDire = true) override;
	//重新设置加速度
	bool reSetAcce(IVehicle* pIVehicle, qreal& inOutAcce) override;
	//重新设置加速度
	virtual bool ref_reSetAcce(IVehicle* pIVehicle, objreal& ref_inOutAcce);
	//计算跟驰类型后处理
	virtual void afterCalcTracingType(IVehicle* pIVehicle) override;
	//在变轨迹上
	virtual bool travelOnChangingTrace(IVehicle* pIVehicle) override;
	//离开变轨
	virtual bool leaveOffChangingTrace(IVehicle* pIVehicle, qreal differ, qreal& s) override;
	//离开当前ILaneObject去另一ILaneObject时调用，如果pAnotherLaneObj为空，表明车辆正驰了路网
	virtual void leaveForNextLaneObj(IVehicle* pIVehicle, ILaneObject* pCurrLaneObj, ILaneObject* pNextLaneObj) override;
	//离开变轨
	virtual bool ref_leaveOffChangingTrace(IVehicle* pIVehicle, qreal differ, objreal& ref_s);
	virtual bool boundingRect(IVehicle* pIVehicle, QRectF& outRect) const override;
	virtual bool shape(IVehicle* pIVehicle, QPainterPath& outShape) const override;
	//绘制车辆
	virtual bool paintVehicle(IVehicle* pIVehicle, QPainter* painter) override;
	//绘制车辆，绘制前将车辆对象旋转指定角度
	bool paintVehicleWithRotation(IVehicle* pIVehicle, QPainter* painter, qreal& inOutRotation) override;
	//绘制车辆，绘制前将车辆对象旋转指定角度
	virtual bool ref_paintVehicleWithRotation(IVehicle* pIVehicle, QPainter* painter, objreal& ref_inOutRotation);
	//绘制车辆后的再绘制，客户可在此方法增加绘制内容
	virtual void rePaintVehicle(IVehicle* pIVehicle, QPainter* painter) override;
	//计算角度后的处理，如重新计算，或修改已算得角度
	bool reCalcAngle(IVehicle* pIVehicle, qreal& outAngle) override;
	//计算角度后的处理，如重新计算，或修改已算得角度
	virtual bool ref_reCalcAngle(IVehicle* pIVehicle, objreal& ref_outAngle);
	//是否停车运行，TESSNG在计算下一点位置后调用，判断是否要停止车辆pIVehicle的运行
	virtual bool isStopDriving(IVehicle* pIVehicle) override;
	//是否计算距离限速区距离
	virtual bool isCalcDistToReduceArea() override;
	//记录车辆速度和位置的最近时间,单位：秒
	bool recentTimeOfSpeedAndPos(int& timeInterval) override;
	//记录车辆速度和位置的最近时间,单位：秒
	virtual bool ref_recentTimeOfSpeedAndPos(objint& timeInterval);
	//输出车辆轨迹到指定路径
	virtual void exportVehiTrace(QString dirPath) override;
	//车辆停车运行前处理
	virtual void beforeStopVehicle(IVehicle* pIVehicle) override;
	//车辆停车运行后处理
	virtual void afterStopVehicle(IVehicle* pIVehicle) override;
	//重新设置仿真结果保存的路径
	bool reSetSimuResultDir(QString& inOutResultDir) override;
	//重新设置仿真结果保存的路径
	virtual bool ref_reSetSimuResultDir(objstring& inOutResultDir);
	//仿真结束后插件写文件方法
	virtual void writeSimuResult(QString dataDir) override;
	//车辆的运行信息
	virtual QString vehiRunInfo(IVehicle* pIVehicle) override;

	//重新计算期望速度,支持单位参数
	bool reCalcdesirSpeed(IVehicle* pIVehicle, qreal& inOutDesirSpeed, UnitOfMeasure* unit) override;
	virtual bool ref_reCalcdesirSpeed_unit(IVehicle* pIVehicle, objreal& ref_inOutDesirSpeed, objUnitOfMeasure& ref_unit);
	//重新设置跟驰的安全时距及安全距离,支持单位参数
	bool reSetFollowingParam(IVehicle* pIVehicle, qreal& inOutSafeInterval, qreal& inOutSafeDistance, UnitOfMeasure* unit) override;
	virtual bool ref_reSetFollowingParam_unit(IVehicle* pIVehicle, objreal& ref_inOutSafeInterval, objreal& ref_inOutSafeDistance, objUnitOfMeasure& ref_unit);
	//重新设置前车距及安全跟车距离,支持单位参数
	bool reSetDistanceFront(IVehicle* pIVehicle, qreal& distance, qreal& s0, UnitOfMeasure* unit) override;
	virtual bool ref_reSetDistanceFront_unit(IVehicle* pIVehicle, objreal& ref_distance, objreal& ref_s0, objUnitOfMeasure& ref_unit);
	//重新设置速度,支持单位参数
	bool reSetSpeed(IVehicle* pIVehicle, qreal& inOutSpeed, UnitOfMeasure* unit) override;
	virtual bool ref_reSetSpeed_unit(IVehicle* pIVehicle, objreal& ref_inOutSpeed, objUnitOfMeasure& ref_unit);
	//由车道确定的限制车速,支持单位参数
	bool calcSpeedLimitByLane(ILink* pILink, int laneNumber, qreal& outSpeed, UnitOfMeasure* unit) override;
	virtual bool ref_calcSpeedLimitByLane_unit(ILink* pILink, int laneNumber, objreal& ref_outSpeed, objUnitOfMeasure& ref_unit);
	//计算当前最大限速,支持单位参数
	bool calcMaxLimitedSpeed(IVehicle* pIVehicle, qreal& inOutLimitedSpeed, UnitOfMeasure* unit) override;
	virtual bool ref_calcMaxLimitedSpeed_unit(IVehicle* pIVehicle, objreal& ref_inOutLimitedSpeed, objUnitOfMeasure& ref_unit);
	//计算到事件对象距离,支持单位参数
	bool calcDistToEventObj(IVehicle* pIVehicle, qreal& dist, UnitOfMeasure* unit) override;
	virtual bool ref_calcDistToEventObj_unit(IVehicle* pIVehicle, objreal& ref_dist, objUnitOfMeasure& ref_unit);
	//计算安全变道距离,支持单位参数
	bool calcChangeLaneSafeDist(IVehicle* pIVehicle, qreal& dist, UnitOfMeasure* unit) override;
	virtual bool ref_calcChangeLaneSafeDist_unit(IVehicle* pIVehicle, objreal& ref_dist, objUnitOfMeasure& ref_unit);
	//计算加速度,支持单位参数
	bool calcAcce(IVehicle* pIVehicle, qreal& acce, UnitOfMeasure* unit) override;
	virtual bool ref_calcAcce_unit(IVehicle* pIVehicle, objreal& ref_acce, objUnitOfMeasure& ref_unit);
	//重新设置加速度,支持单位参数
	bool reSetAcce(IVehicle* pIVehicle, qreal& inOutAcce, UnitOfMeasure* unit) override;
	virtual bool ref_reSetAcce_unit(IVehicle* pIVehicle, objreal& ref_inOutAcce, objUnitOfMeasure& ref_unit);
};

#endif
