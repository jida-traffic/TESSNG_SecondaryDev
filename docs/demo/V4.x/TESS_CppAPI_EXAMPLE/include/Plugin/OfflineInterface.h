/********************************************************************************
* 准在线仿真接口，用户实现全部或部分方法实现TESS NG准在线仿真功能
*********************************************************************************/

#ifndef __OfflineInterface__
#define __OfflineInterface__

#include "tessinterfaces_global.h"

#include "_datastruct.h"

#include <QJsonObject>

class ILink;
class IVehicle;

namespace Online
{
	class TESSINTERFACES_EXPORT OfflineInterface
	{
	public:
		OfflineInterface();
		virtual ~OfflineInterface();

		/*
		初始化窗体
		*/
		virtual void initGui();

		/*
		初始化路网
		*/
		virtual void initNet();

		/*
		初始化仿真
		*/
		virtual void initSimu();

		/*
		此方法接收传入的参数bOnline，确定是否为在线仿真
		*/
		virtual void setOnline(bool bOnline);

		/*
		此方法接收传入的参数workspace，这是插件所在路径
		*/
		virtual void setWorkspace(QString workspace);

		/*
		此方法传入配置文件完整信息，方便用户对运用进行参数配置
		*/
		virtual void setConfig(QJsonObject jsonConfig);

		/*
T		启动仿真前的操作
		*/
		virtual void beforeStartSimu();

		/*
		仿真开始的现实时间, time: milliseconds since 1970-01-01T00:00:00 Universal Coordinated Time
		*/
		//virtual void setStartMSecsSinceEpoch(qint64 time);

		/*
		由车道确定的车辆最大限速（公里/小时), laneNumber:车道序号，从0开始
		*/
		virtual bool calcSpeedLimitByLane(ILink *pLink, int laneNumber, qreal &outSpeed);

		//根据车辆类型及路段ID计算限行车道：如管制、危险等，最右侧编号为0
		virtual bool calcLimitedLaneByVehiType(long vehiType, ILink *pLink, QList<int> &lNumber);

		/*
		根据当前仿真时间获取所有发车点未来一段时间发车数及组成， simuTime：当前仿真时间
		*/
		virtual DispatchWithSource calcDispatchInterval(qint64 simuTime);

		/*
		根据当前仿真时间获取所有决策点未来一段时间各路径的流量比，simuTime：当前仿真时间
		*/
		virtual FlowRatioWithSource calcFlowRatioInterval(qint64 simuTime);

		/*
		根据当前仿真时间获取信号灯组当前或未来一段时间的相位灯色，simuTime：当前仿真时间
		*/
		virtual SignalContralWithSource calcSignalContral(qint64 simuTime);

		/*
		计算创建动态车辆的参数，插件通过此方法得到该参数后调用TESSNG的api创建车辆
		*/
		virtual QList<Online::DynaVehiParam> calcDynaVehisParam(qint64 simuTime);

		/*
		计算创建动态车辆的参数，参数附带客户端时间戳，插件通过此方法得到该参数后调用TESSNG的api创建车辆
		*/
		virtual DynsVehiWithTimestamp calcDynaVehisWithTsParam(qint64 simuTime);

		/*
		计算动态创建施工区的参数
		*/
		virtual QList<Online::DynaRoadWorkZoneParam> calcDynaRoadWorkZoneParam();

		/*
		计算动态创建事故区的参数
		*/
		virtual QList<Online::DynaAccidentZoneParam> calcDynaAcciZoneParam();

		/*
		计算跟驰参数
		*/
		virtual QList<Online::FollowingModelParam> calcFollowingParam();

		/*
		是否输出车辆轨迹
		*/
		virtual bool isPutVehisStatus();

		/*
		是否输出每一个计算周期结束时采集器采集到的信息
		*/
		virtual bool isPutVehisInfoCollected();

		/*
		是否输出每一个排队计数器每一仿真秒的排队信息
		*/
		virtual bool isPutVehisQueueCounted();

		/*
		是否输出每一个计算周期结束时行程时间检测器检测到的数据(车辆完成从起始点到结束点的行程)
		*/
		virtual bool isPutVehisTravelDetected();

		/*
		插件每一个计算周期结束时put车辆状态到lVs，包含车辆位置等信息，运用方可从lVs中获取车辆状态信息
		*/
		virtual void putVehisStatus(qint64 simuTime, QList<Online::VehicleStatus>& lVs);

		/*
		将指定车辆轨迹put到lVt
		*/
		virtual void putVehisTrace(long vehiId, QList<Online::VehiclePosition>& lVt);

		/*
		将当前信号灯组相位颜色信息保存到lSp
		*/
		virtual void putSignalPhasesColor(QList<Online::SignalPhaseColor>& lSp);

		/*
		插件每一个计算周期结束时put采集器采集到的信息，这些信息是当前周期完成采集的所有车辆信息。运用方可以从lVInfo中获取采集到的车辆信息
		*/
		virtual void putVehisInfoCollected(qint64 simuTime, QList<Online::VehiInfoCollected>& lVInfo);

		/*
		插件每一个计算周期结束时计算采集器是否有新的集计数据时间段，如果有则计算该时间段内的集计数据，put采集器的集计数据。运用方可以从lVInfo中获取集计数据
		*/
		virtual void putVehisInfoAggregated(qint64 simuTime, QList<Online::VehiInfoAggregated>& lVInfo);

		/*
		插件每一个仿真秒put排队计数器计数的车辆排队信息，运用方可以从lVQueue中获取车辆排队信息
		*/
		virtual void putVehisQueueCounted(qint64 simuTime, QList<Online::VehiQueueCounted>& lVQueue);

		/*
		插件每一个计算周期结束时计算排队计数器是否有新的集计数据时间段，如果有则计算该时间段内的集计数据，put排队计数器的集计数据。运用方可从lVQueueAggregated获取集计数据
		*/
		virtual void putVehisQueueAggregated(qint64 simuTime, QList<Online::VehiQueueAggregated>& lVQueueAggregated);

		/*
		插件每一个计算周期结束时put行程时间检测器检测到的信息，这些信息是正在完成行程时间检测器的检测要求的信息（经过行程时间检测器的起点和终点）。运用方可从lVTravel获取数据
		*/
		virtual void putVehisTravelDetected(qint64 simuTime, QList<Online::VehiTravelDetected>& lVTravel);

		/*
		插件每一个计算周期结束时计算行程时间检测器是否有新的集计数据时间段产生，如果有则计算该时间段内的集计数据，put行程时间集计数据。运用方可从lVTravel获取行程时间集计数据
		*/
		virtual void putVehisTravelAggregated(qint64 simuTime, QList<Online::VehiTravelAggregated>& lVTravelAggregated);

		/*
		一个计算周期后的操作, simuTime:当前计算周期的仿真时间
		*/
		virtual void afterOneStep(qint64 simuTime);

		/*
		停止仿真后操作
		*/
		virtual void afterStopSimu();

		/*
		将仿真结果写入dataDir文件夹，dataDir：文件夹
		*/
		virtual void writeSimuResult(QString dataDir);

	};
}

#endif