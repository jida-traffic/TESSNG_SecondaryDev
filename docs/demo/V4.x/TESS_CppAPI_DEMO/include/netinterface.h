/********************************************************************************
* TESS NG路网层接口，此接口由TESS NG实现，用户可以通过此接口访问路网，获取路网数据，创建路段、发车点等
*********************************************************************************/

#ifndef NETINTERFACE_H
#define NETINTERFACE_H

#include <QObject>

#include <QGraphicsScene>
#include <QPointF>
#include <QList>
#include <QMap>

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"
#include "_tables.h"
#include "IPedestrianType.h"
#include "UnitChange.h"

namespace Online {
	struct CrossPoint;
}

class CustomerNet;
class IRoadNet;
class ISection;
class ILink;
class Link;
class IConnector;
class Connector;
class IConnectorArea;
class IDispatchPoint;
class IDecisionPoint;
class CustomerGraphicsObject;
class IConnectorArea;
class IBusLine;
class IBusStation;
class IBusStationLine;
class IRouting;
class ISignalLamp;
class ISignalPhase;
//class ISignalGroup;
class IVehicleDrivInfoCollector;
class IVehicleQueueCounter;
class IVehicleTravelDetector;
class ILaneConnector;
class ILane;
class IGuidArrow;
class IRoadWorkZone;
class IAccidentZone;
class ILimitedZone;
class IReduceSpeedArea;
class IReconstruction;
class IPedestrianRegion;
class IPedestrianRectRegion;
class IPedestrianEllipseRegion;
class IPedestrianTriangleRegion;
class IPedestrianFanShapeRegion;
class IPedestrianPolygonRegion;
class IPedestrianSideWalkRegion;
class IPedestrianCrossWalkRegion;
class IPedestrianStairRegion;
class IPedestrianPathPoint;
class IPedestrianPath;
class ICrosswalkSignalLamp;

class ISignalController;
class ISignalPlan;
class IJunction;
class ITollLane;
class ITollPoint;
class ITollRouting;
class ITollDecisionPoint;
class IParkingStall;
class IParkingRegion;
class IParkingRouting;
class IParkingDecisionPoint;
class IEvaluateArea;

/*
访问、控制路网的接口
*/
class TESSINTERFACES_EXPORT NetInterface
{
public:
	//打开路网文件
	virtual void openNetFle(QString filePath);
	//从专业数据库加载路网
	virtual void openNetByNetId(long netId);
	//保存路网
	virtual bool saveRoadNet();
	//路网文件路径，如果是专业数据保存的路网，返回的是路网ID
	virtual QString netFilePath();
	//路网对象
	virtual IRoadNet* roadNet();
	//路段面积，单位平方米
	virtual qreal roadNetArea();
	//场景
	virtual QGraphicsScene* graphicsScene();
	//视图
	virtual QGraphicsView* graphicsView();
	//场景中的比例尺
	virtual qreal sceneScale();
	//设置场景大小
	virtual void setSceneSize(qreal w, qreal h, UnitOfMeasure unit = UnitOfMeasure::Default);
	//场景宽度
	virtual qreal sceneWidth(UnitOfMeasure unit = UnitOfMeasure::Default);
	//场景高度
	virtual qreal sceneHeigth(UnitOfMeasure unit = UnitOfMeasure::Default);
	//背景图
	virtual QByteArray backgroundMap();
	//设置路网像素比
	virtual void setPixelRatio(qreal newRatio);
	//开始数据导入，与endImportData()配合使用，可以提高效率。此方法在开始导入数据前调用
	virtual void beginImportData();
	//结束数据导入，与beginImportData()配合使用。此方法在完成导入数据后调用
	virtual void endImportData();
	//所有Section
	virtual QList<ISection*> sections();
	//路段ID集
	virtual QList<long> linkIds();
	//路段数
	virtual int linkCount();
	//路段集
	virtual QList<ILink*> links();
	//车型组成集
	virtual QList<Online::VehicleCompositionList> vehicleCompositions();
	//根据路段ID查找路段
	virtual ILink* findLink(long id);
	//根据车道ID查找车道
	virtual ILane* findLane(long id);
	//根据“车道连接”ID查找“车道连接”
	virtual ILaneConnector* findLaneConnector(long id);
	//连接段ID集
	virtual QList<long> connectorIds();
	//连接段数
	virtual int connectorCount();
	//连接段集
	virtual QList<IConnector*> connectors();
	//根据连接段ID查找连接段
	virtual IConnector* findConnector(long id);
	//根据需域ID查找需域
	virtual IConnectorArea* findConnectorArea(long id);
	//根据起始路段ID及目标路段ID查找连接段
	virtual IConnector* findConnectorByLinkIds(long fromLinkId, long toLinkId);
	//根据起始车道ID及目标车道ID查找“车道连接”
	virtual ILaneConnector* findLaneConnector(long fromLaneId, long toLaneId);
	//导向箭头数
	virtual int guidArrowCount();
	//导向箭头ID集
	virtual QList<long> guidArrowIds();
	//导向箭头集
	virtual QList<IGuidArrow*> guidArrows();
	//根据ID查询导向箭头
	virtual IGuidArrow* findGuidArrow(long id);

	//信号灯数
	virtual int signalLampCount();
	//信号灯ID集
	virtual QList<long> signalLampIds();
	//信号灯集
	virtual QList<ISignalLamp*> signalLamps();
	//根据信号灯ID查找信号灯
	virtual ISignalLamp* findSignalLamp(long id);
	//根据信号相位ID查找信号相位
	virtual ISignalPhase* findSignalPhase(long id);
	////信号灯组数
	//virtual int signalGroupCount();
	////信号灯组ID集
	//virtual QList<long> signalGroupIds();
	//信号灯组集
	//virtual QList<ISignalGroup*> signalGroups();
	//根据信号灯组ID查找信号灯组
	//virtual ISignalGroup* findSignalGroup(long id);

	//信号机数
	virtual int signalControllerCount();
	//信号机ID集
	virtual QList<long> signalControllerIds();
	//信号机集
	virtual QList<ISignalController*> signalControllers();
	//根据id查询信号机
	virtual ISignalController* findSignalControllerById(long id);
	//根据名称查询信号机(如果同名返回第一个)
	virtual ISignalController* findSignalControllerByName(QString name);


	//信控方案组数
	virtual int signalPlanCount();
	//信控方案ID集
	virtual QList<long> signalPlanIds();
	//信控方案集
	virtual QList<ISignalPlan*> signalPlans();
	//根据信控方案ID查找信控方案
	virtual ISignalPlan* findSignalPlanById(long id);
	//根据信控方案名称查找信控方案
	virtual ISignalPlan* findSignalPlanByName(QString name);



	//发车点集
	virtual QList<IDispatchPoint*> dispatchPoints();
	//根据发车点ID查找发车点
	virtual IDispatchPoint* findDispatchPoint(long id);
	//决策点集
	virtual QList<IDecisionPoint*> decisionPoints();
	//根据决策点ID查找决策点
	virtual IDecisionPoint* findDecisionPoint(long id);
	//所有车辆检测器
	virtual QList<IVehicleDrivInfoCollector*> vehiInfoCollectors();
	//根据ID查询车辆检测器
	virtual IVehicleDrivInfoCollector* findVehiInfoCollector(long id);
	//所有排队计数器
	virtual QList<IVehicleQueueCounter*> vehiQueueCounters();
	//根据ID查询车辆排队计数器
	virtual IVehicleQueueCounter* findVehiQueueCounter(long id);
	//所有车辆行程时间检测器，返回列表中的每一个元素是一对行程时间检测器的起始检测器
	virtual QList<IVehicleTravelDetector*> vehiTravelDetectors();
	//根据ID查询车辆行程时间检测器，返回一对行程时间检测器中起始检测器
	virtual IVehicleTravelDetector* findVehiTravelDetector(long id);
	//根据路径ID查找路径
	virtual IRouting* findRouting(long id);
	//公交线路集
	virtual QList<IBusLine*> buslines();
	//根据公交线路ID查询公交线路
	virtual IBusLine* findBusline(long buslineId); 
	//根据公交线路起始路段ID查询公交线路
	virtual IBusLine* findBuslineByFirstLinkId(long linkId);
	//公交站点集
	virtual QList<IBusStation*> busStations();
	//根据公交站点ID查询公交站点
	virtual IBusStation* findBusStation(long stationId);
	//根据公交站点ID查询相关BusLineStation
	virtual QList<IBusStationLine*> findBusStationLineByStationId(long stationId);
	//面域集
	virtual QList<IConnectorArea*> allConnectorArea();
	//车道中心线断点集
	virtual QList<QPointF> laneCenterPoints(long laneId, UnitOfMeasure unit = UnitOfMeasure::Default);
	//路段中心线断点集
	virtual QList<QPointF> linkCenterPoints(long linkId, UnitOfMeasure unit = UnitOfMeasure::Default);
	//判断路段去向是否进入交叉口， 以面域是否存在多连接段以及当前路段与后续路段之间的角度为依据
	virtual bool judgeLinkToCross(long linkId);
	//当前“车道连接”穿过其它“车道连接”形成的交叉点列表
	virtual QList<Online::CrossPoint> crossPoints(ILaneConnector* pLaneConnector);

	//根据路网元素名获取自增ID
	virtual long getIDByItemName(QString name);

	//路网基本属性
	virtual IRoadNet* netAttrs();

	/*
	设置路网基本属性
	name:路网名称
	centerPoint:中心点坐标，默认为(0, 0)，用户也可以将中心点坐标保存到otherAttrsJson字段里
	sourceType : 数据g来源分类
	otherAttrsJson : 保存在json对象中的其它属性
	*/
	virtual IRoadNet* setNetAttrs(QString name, QString sourceType = QString("TESSNG"), QPointF centerPoint = QPointF(), QString backgroundUrl = QString(), QJsonObject otherAttrsJson = QJsonObject(), UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建空白路网，filePath：空白路网全路径名
	virtual bool createEmptyNetFile(QString filePath, long dbver = 5);

	//创建路段，lCenterPoint:路段中心线，laneCount:车道数
	virtual ILink* createLink(QList<QPointF> lCenterPoint, int laneCount, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterV3:路段中心线，laneCount:车道数
	virtual ILink* createLink3D(QList<QVector3D> lCenterV3, int laneCount, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterPoint:路段中心线，lLaneWidth:车道宽度列表
	virtual ILink* createLinkWithLaneWidth(QList<QPointF> lCenterPoint, QList<qreal> lLaneWidth, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterPoint:路段中心线，lLaneWidth:车道宽度列表 OSM用
	virtual ILink* createLinkWithLanesWidth(QList<QPointF> lCenterPoint, QList<qreal> lLaneWidth, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterV3:路段中心线，lLaneWidth:车道宽度列表
	virtual ILink* createLink3DWithLanesWidth(QList<QVector3D> lCenterV3, QList<qreal> lLaneWidth, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterV3:路段中心线，lLaneWidth:车道宽度列表
	virtual ILink* createLink3DWithLaneWidth(QList<QVector3D> lCenterV3, QList<qreal> lLaneWidth, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterLineV3：路段中心点集(对应TESSNG路段中心点)，lanesWithPoints：车道点集的集合，linkName：路段名，默认为路段ID，bAddToScene：是否加入路网，默认true表示加入
	virtual ILink* createLink3DWithLanePoints(QList<QVector3D> lCenterLineV3, QList<QMap<QString, QList<QVector3D>>> lanesWithPoints, QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建路段，lCenterLineV3：路段中心点集(对应TESSNG路段中心点)，lanesWithPoints：车道点集的集合，lLaneType:车道类型集，lAttr:车道附加属性集，linkName：路段名，默认为路段ID, bAddToScene：是否加入路网，默认true表示加入
	virtual ILink* createLink3DWithLanePointsAndAttrs(QList<QVector3D> lCenterLineV3, QList<QMap<QString, QList<QVector3D>>> lanesWithPoints, QList<QString> lLaneType, QList<QJsonObject> lAttr = QList<QJsonObject>(), QString linkName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除路段，从场景中移除pLink，但不从路网文件或数据库中删除，保存路网后才会从路网文件中删除
	virtual void removeLink(ILink* pLink);
	//更新路段，更新后返回路段对象
	virtual ILink* updateLink(_Link link, QList<_Lane> lLane = QList<_Lane>(), QList<QPointF> lPoint = QList<QPointF>(), UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新路段断点高程，车道断点高程也被更新，参数lV3z是断点高程列表，长度必须与路段断点数相同
	virtual void updateLinkV3z(ILink* pILink, QList<qreal> lV3z, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新路段断点集，车道断点将被自动计算，更新后的断点数可以与原先断点数不同
	virtual void updateLink3DWithPoints(ILink* pILink, QList<QVector3D> lV3d, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新路段及车道断点集，更新后的路段断点数与车道的断点数必须一致，但可以与原先断点数不同
	virtual void updateLinkAndLane3DWithPoints(ILink* pILink, QList<QVector3D> lCenterLineV3, QList<QMap<QString, QList<QVector3D>>> lanesWithPoints, UnitOfMeasure unit = UnitOfMeasure::Default);
	//创建连接段, fromLinkId:起始路段ID，toLinkId：目标车道ID， lFromLaneNumber:起始路段车道序号，最外侧为1，lToLaneNumber：目标路段车道序号，最外侧为1
	virtual IConnector* createConnector(long fromLinkId, long toLinkId, QList<int> lFromLaneNumber, QList<int> lToLaneNumber, QString connName = QString(), bool bAddToScene = true);
	//创建连接段，创建连接段后将laneConnector中自动计算的断点集用参数laneConnectorWithPoints断点替换
	virtual IConnector* createConnector3DWithPoints(long fromLinkId, long toLinkId, QList<int> lFromLaneNumber, QList<int> lToLaneNumber, QList<QMap<QString, QList<QVector3D>>> laneConnectorWithPoints, QString connName = QString(), bool bAddToScene = true, UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除连接段，从场景中移除pConnector，但不从路网文件或数据库中删除，保存路网后才会从路网文件中删除
	virtual void removeConnector(IConnector* pConnector);
	//更新连接段，更新后返回连接段对象
	virtual IConnector* updateConnector(_Connector connector);
	//创建“车道连接”
	virtual ILaneConnector* createLaneConnector(IConnector* pIConnector, long fromLaneId, long toLaneId);
	//移除“车道连接”
	virtual bool removeLaneConnector(ILaneConnector* pILC);
	//创建导向箭头。 pLane:车道；length:长度，单位像素；distToTerminal：到车道终点距离，单位像素；arrowType：箭头类型
	virtual IGuidArrow* createGuidArrow(ILane* pLane, qreal length, qreal distToTerminal, Online::GuideArrowType arrowType, UnitOfMeasure unit = UnitOfMeasure::Default);
	//删除导向箭头
	virtual void removeGuidArrow(IGuidArrow* pArrow);
	//创建发车点
	virtual IDispatchPoint* createDispatchPoint(ILink* pLink, QString dpName = QString(), bool bAddToScene = true);
	//删除发车点
	virtual bool removeDispatchPoint(IDispatchPoint* pDispPoint);
	//车辆类型列表
	virtual QList<_VehicleType> vehicleTypes();
	//创建车型
	virtual bool createVehicleType(_VehicleType _vt);
	//创建车型组成
	virtual long createVehicleComposition(QString name, QList<Online::VehiComposition> lVehiComp);
	//更新车型组成
	virtual bool updateVehicleComposition(long vehiCompId, QList<Online::VehiComposition> lVehiComp);
	//移除车型组成
	virtual bool removeVehicleComposition(long vehiCompId);

	//创建决策点
	virtual IDecisionPoint* createDecisionPoint(ILink* pLink, qreal distance, QString name = QString(), UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除决策点
	virtual bool removeDecisionPoint(IDecisionPoint* pDeciPoint);

	//创建决策路径
	virtual IRouting* createDeciRouting(IDecisionPoint* pDeciPoint, QList<ILink*> lILink);
	//删除决策路径
	virtual bool removeDeciRouting(IDecisionPoint* pDeciPoint, IRouting* pRouting);
	//更新决策点及其各路径不同时间段流量比
	virtual IDecisionPoint* updateDecipointPoint(_DecisionPoint deciPoint, QList<_RoutingFLowRatio> lFlowRatio = QList<_RoutingFLowRatio>());
	//更新决策点下的决策路径
	virtual bool updateRouting(IRouting* pIRouting, QList<ILink*> lILink);
	/*
	更新决策路径上一个连接段的车道连接
		参数 pRouting：路径，pConnector：连接段，lFromLaneId：连接段上游车道ID列表，lToLaneId：连接段下游车道ID列表
		返回 bool类型
	*/
	virtual bool updateRoutingLaneConnector(IRouting* pRouting, IConnector* pConnector, QList<long> lFromLaneId, QList<long> lToLaneId);
	//在路段的车道上创建车辆采集器，dist单位像素
	virtual IVehicleDrivInfoCollector* createVehiCollectorOnLink(ILane* pLane, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//在连接段的“车道连接”上创建采集器，dist单位像素
	virtual IVehicleDrivInfoCollector* createVehiCollectorOnConnector(ILaneConnector* pLaneConnector, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新采集器
	virtual IVehicleDrivInfoCollector* updateVehiCollector(_VehicleDrivInfoCollector collecter);
	//移除车辆检测器
	virtual bool removeVehiCollector(IVehicleDrivInfoCollector* pCollector);

	//在路段的车道上创建车辆排队计数器，dist单位像素
	virtual IVehicleQueueCounter* createVehiQueueCounterOnLink(ILane* pLane, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//在连接段的车道连接上创建车辆排队计数器，dist单位像素
	virtual IVehicleQueueCounter* createVehiQueueCounterOnConnector(ILaneConnector* pLaneConnector, qreal dist, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新排队计数器
	virtual IVehicleQueueCounter* updateVehiQueueCounter(_VehicleQueueCounter counter);

	//创建行程时间检测器，起点和终点都在路段上，dist1为检测器起点距路所在路段起始点距离，单位像素，dist2为检测器终点距所在路段起始点距离，单位像素，
	virtual QList<IVehicleTravelDetector*> createVehicleTravelDetector_link2link(ILink* pStartLink, ILink* pEndLink, qreal dist1, qreal dist2, UnitOfMeasure unit = UnitOfMeasure::Default);
	//创建行程时间检测器，起点在路段上，终点都在连接段的“车道连接”上，dist1为检测器起点距路所在段起始点距离，单位像素，dist2为检测器终点距所在“车道连接”起始点距离，单位像素，
	virtual QList<IVehicleTravelDetector*> createVehicleTravelDetector_link2conn(ILink* pStartLink, ILaneConnector* pEndLaneConnector, qreal dist1, qreal dist2, UnitOfMeasure unit = UnitOfMeasure::Default);
	//创建行程时间检测器，起点在连接段的“车道连接”上，终点在路段上，dist1为检测器起点距路所在"车道连接"起始点距离，单位像素，dist2为检测器终点距所在路段起始点距离，单位像素，
	virtual QList<IVehicleTravelDetector*> createVehicleTravelDetector_conn2link(ILaneConnector* pStartLaneConnector, ILink* pEndLink, qreal dist1, qreal dist2, UnitOfMeasure unit = UnitOfMeasure::Default);
	//创建行程时间检测器，起点和终点都在连接段的“车道连接”上，dist1为检测器起点距路所在"车道连接"起始点距离，单位像素，dist2为检测器终点距所在“车道连接”起始点距离，单位像素，
	virtual QList<IVehicleTravelDetector*> createVehicleTravelDetector_conn2conn(ILaneConnector* pStartLaneConnector, ILaneConnector* pEndLaneConnector, qreal dist1, qreal dist2, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建信号灯组， 参数 name:灯组名称， period:周期，单位秒，fromTime:起始时间，单位秒，toTime:结束时间,单位秒
	//virtual ISignalGroup* createSignalGroup(QString name, int period, long fromTime, long toTime);
	//创建相位， 参数 pGroup：信号灯组， name：相位名称，lColor：相位灯色序列，新建相位排在已有相位序列的最后
	//virtual ISignalPhase* createSignalPhase(ISignalGroup* pGroup, QString name, QList<Online::ColorInterval> lColor); 
	

	//创建相位， 参数 SignalPlan：信控方案， name：相位名称，lColor：相位灯色序列，新建相位排在已有相位序列的最后
	 virtual ISignalPhase* createSignalPlanSignalPhase(ISignalPlan* SignalPlan, QString name, QList<Online::ColorInterval> lColor);
	////移除已有相位，相位移除后，原相位序列自然重排, 参数 pGroup：信号灯组对象，phaseId：将要移除的相位ID:
	virtual void removeSignalPhase(ISignalPlan* pPlan, long phaseId);
	//创建信号灯 参数 pPhase：相位对象，name：信号灯名称，laneId：信号灯所在车道ID，或所在“车道连接”上游车道ID，toLaneId：信号灯所在“车道连接”下游道ID，distance：信号灯距车道或“车道连接”起点距离，单位像素
	virtual ISignalLamp* createSignalLamp(ISignalPhase* pPhase, QString name, long laneId, long toLaneId, qreal distance);
	//创建信号灯 参数 pTrafficLight：信号机，name：信号灯名称，laneId：信号灯所在车道ID，或所在“车道连接”上游车道ID，toLaneId：信号灯所在“车道连接”下游道ID，distance：信号灯距车道或“车道连接”起点距离，单位像素
	virtual ISignalLamp* createTrafficSignalLamp(ISignalController* pTrafficLight, QString name, long laneId, long toLaneId, qreal distance);

	//创建信号机
	virtual ISignalController* createSignalController(QString name);
	//创建信控方案
	virtual ISignalPlan* createSignalPlan(ISignalController* pITrafficLight, QString name, int cycle, int offset, int startTime, int endTime);
	//信号灯移除莫个绑定的相位(如果相位列表只存在一个相位则将关联的相位设置为null)
	virtual void removeSignalPhaseFromLamp(int SignalPhaseId, ISignalLamp* signalLamp);
	//信号灯添加绑定的相位
	virtual void addSignalPhaseToLamp(int SignalPhaseId, ISignalLamp* signalLamp);
	//人行道横道信号灯添加绑定的相位
	virtual void addCrossWalkSignalPhaseToLamp(int SignalPhaseId, ICrosswalkSignalLamp* signalLamp);
	//信号灯更换绑定的相位(不允许跨越信号机)
	virtual void transferSignalPhase(ISignalPhase* pFromISignalPhase, ISignalPhase* pToISignalPhase, ISignalLamp* signalLamp);

	//创建公交线路，lLink列表中相邻两路段可以是路网上相邻两路段，也可以不相邻，如果不相邻，TESSNG会在它们之间创建一条最短路径。
	//如果lLink列表中相邻路段在路网上不相邻并且二者之间不存在最短路径，则相邻的第二条路段及后续路段无效。
	virtual IBusLine* createBusLine(QList<ILink*> lLink);
	//移除公交线路
	virtual bool removeBusLine(IBusLine* pBusLine);
	//创建公交站点。pLane:车道，length:站点长度(单位像素), dist:站点起始点距车道起点距离(单位像素)
	virtual IBusStation* createBusStation(ILane* pLane, qreal length, qreal dist, QString name = QString(), UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除公交站点
	virtual bool removeBusStation(IBusStation* pStation);
	/* 将公交站点关联到公交线路上 */
	virtual bool addBusStationToLine(IBusLine* pBusLine, IBusStation* pStation);
	/* 将公交站点与公交线路的关联关系解除*/
	virtual bool removeBusStationFromLine(IBusLine* pBusLine, IBusStation* pStation);
	//编辑公交站点基础信息(参数: 公交站点，当前公交线路，基本停靠时间 ，下客百分比，上车时间，下车时间)
	virtual bool editBusStationBasicInformation(IBusLine* pBusLine, IBusStation* pStation,int parkingTime,double leavingPercent,double getOnTimePerson ,double getOutTimePerson);
	//编辑公交站点关联线路信息
	virtual bool editBusStation(IBusLine* pBusLine, IBusStation* pStation, QList<Online::PassengerArrivings> lPassengerArrivings);

	/*创建限速区*/
	virtual IReduceSpeedArea* createReduceSpeedArea(Online::DynaReduceSpeedAreaParam param);
	/*移除限速区*/
	virtual void removeReduceSpeedArea(IReduceSpeedArea* pIReduceSpeedArea);
	/*更新限速区*/
	virtual bool updateReduceSpeedArea(Online::DynaReduceSpeedAreaParam param);
	//获取所有限速区
	virtual QList<IReduceSpeedArea*> reduceSpeedAreas();
	//查询指定ID的限速区
	virtual IReduceSpeedArea* findReduceSpeedArea(long id);

	//初始化数据库序列
	virtual bool initSequence(QString schemaName = QString());

	//计算最短路径，返回路径上路段序列，pFromLink:起始路段，pToLink：目标路段
	virtual IRouting* shortestRouting(ILink* pFromLink, ILink* pToLink);
	//用连续通达的路段序列创建路径
	virtual IRouting* createRouting(QList<ILink*> lILink);

	//场景角度转换到图元角度
	virtual qreal angleToItem(qreal angle);
	//增加客户路网元素到场景
	virtual void addCustomerItem(CustomerGraphicsObject* pCustObj);
	//从场景移除客户路网元素
	virtual void removeCustomerItem(CustomerGraphicsObject* pCustObj);

	//路网的网格化，参数width单元格宽度，单位米
	virtual void buildNetGrid(qreal width = 25, UnitOfMeasure unit = UnitOfMeasure::Default);
	//根据point查询所在单元格所有Section
	virtual QList<ISection*> findSectionOn1Cell(QPointF point, UnitOfMeasure unit = UnitOfMeasure::Default);
	//根据point查询最近4个单元格所有Section
	virtual QList<ISection*> findSectionOn4Cell(QPointF point, UnitOfMeasure unit = UnitOfMeasure::Default);
	//根据point查询最近9个单元格所有Section
	virtual QList<ISection*> findSectionOn9Cell(QPointF point, UnitOfMeasure unit = UnitOfMeasure::Default);
	/*
	根据point对lSection列表中每一个Section所有LaneObject求最短距离，返回Location列表，列表安最短距离排序，从小到大，
	referDistance：LaneObject上与point最近的点到LaneObject起点距离，单位像素，是大约数，只为提高计算效率，默认值为0;
	*/
	virtual QList<Online::Location> locateOnSections(QPointF point, QList<ISection*> lSection, qreal referDistance = 0, UnitOfMeasure unit = UnitOfMeasure::Default);
	/*
	point周围若干个单元格里查询LaneObject，cellCount：单元格数，小于1时默认为1，大于1小于4时默认为4，大于4时默认为9
	*/
	virtual QList<Online::Location> locateOnCrid(QPointF point, int cellCount = 1, UnitOfMeasure unit = UnitOfMeasure::Default);
	//路网外围Rect，用以获取路网边界
	virtual QRectF boundingRect();

	//创建施工区
	virtual IRoadWorkZone* createRoadWorkZone(Online::DynaRoadWorkZoneParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新施工区
	virtual bool updateRoadWorkZone(Online::DynaRoadWorkZoneParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除施工区
	virtual void removeRoadWorkZone(IRoadWorkZone* pIRoadWorkZone);
	//获取所有施工区
	virtual QList<IRoadWorkZone*> roadWorkZones();
	//根据ID查询施工区
	virtual IRoadWorkZone* findRoadWorkZone(long roadWorkZoneId);
	//创建事故区
	virtual IAccidentZone* createAccidentZone(Online::DynaAccidentZoneParam param);
	//移除事故区
	virtual void removeAccidentZone(IAccidentZone* pIAccidentZone);
	//更新事故区
	virtual bool updateAccidentZone(Online::DynaAccidentZoneParam param);
	//获取所有事故区
	virtual QList<IAccidentZone*> accidentZones();
	//根据ID查询事故区
	virtual IAccidentZone* findAccidentZone(long accidentZoneId);
	//创建限行区
	virtual ILimitedZone* createLimitedZone(Online::DynaLimitedZoneParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新限行区
	virtual bool updateLimitedZone(Online::DynaLimitedZoneParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除限行区
	virtual void removeLimitedZone(ILimitedZone* pILimitedZone);
	//获取所有限行区
	virtual QList<ILimitedZone*> limitedZones();
	//根据ID查询限行区
	virtual ILimitedZone* findLimitedZone(long limitedZoneId);
	//移动路段及相关连接段
	virtual void moveLinks(QList<ILink*> lLink, QPointF offset, UnitOfMeasure unit = UnitOfMeasure::Default);

	//创建改扩建
	virtual IReconstruction* createReconstruction(Online::DynaReconstructionParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//更新改扩建
	virtual bool updateReconStruction(Online::DynaReconstructionParam param, UnitOfMeasure unit = UnitOfMeasure::Default);
	//移除改扩建
	virtual void removeReconstruction(IReconstruction* pIReconstruction);
	//获取所有改扩建
	virtual QList<IReconstruction*> reconstructions();
	//根据ID查询改扩建
	virtual IReconstruction* findReconstruction(long reconstructionId);
	//重新计算保通开口长度
	virtual qreal reCalcPassagewayLength(Online::DynaReconstructionParam param, UnitOfMeasure unit = UnitOfMeasure::Default);


	//行人相关接口
	//所有行人类型
	virtual QList<IPedestrianType> pedestrianTypes();
	//所有行人组成
	virtual QList<Online::Pedestrian::PedestrianComposition> pedestrianCompositions();
	//所有层级信息
	virtual QList<Online::Pedestrian::LayerInfo> layerInfos();
	//所有行人面域
	virtual QList<IPedestrianRegion*> pedestrianRegions();
	//所有矩形面域
	virtual QList<IPedestrianRectRegion*> pedestrianRectRegions();
	//所有椭圆形面域
	virtual QList<IPedestrianEllipseRegion*> pedestrianEllipseRegions();
	//所有三角形面域
	virtual QList<IPedestrianTriangleRegion*> pedestrianTriangleRegions();
	//所有扇形面域
	virtual QList<IPedestrianFanShapeRegion*> pedestrianFanShapeRegions();
	//所有多边形面域
	virtual QList<IPedestrianPolygonRegion*> pedestrianPolygonRegions();
	//所有人行道
	virtual QList<IPedestrianSideWalkRegion*> pedestrianSideWalkRegions();
	//所有人行横道
	virtual QList<IPedestrianCrossWalkRegion*> pedestrianCrossWalkRegions();
	//所有楼梯
	virtual QList<IPedestrianStairRegion*> pedestrianStairRegions();
	//所有行人发生点
	virtual QList<IPedestrianPathPoint*> pedestrianPathStartPoints();
	//所有行人结束点
	virtual QList<IPedestrianPathPoint*> pedestrianPathEndPoints();
	//所有行人决策点
	virtual QList<IPedestrianPathPoint*> pedestrianPathDecisionPoints();
	//所有行人路径，包括局部路径
	virtual QList<IPedestrianPath*> pedestrianPaths();
	//所有人行横道红绿灯
	virtual QList<ICrosswalkSignalLamp*> crosswalkSignalLamps();
	//根据id获取行人面域
	virtual IPedestrianRegion* findPedestrianRegion(long id);
	//根据id获取矩形面域
	virtual IPedestrianRectRegion* findPedestrianRectRegion(long id);
	//根据id获取椭圆形面域
	virtual IPedestrianEllipseRegion* findPedestrianEllipseRegion(long id);
	//根据id获取三角形面域
	virtual IPedestrianTriangleRegion* findPedestrianTriangleRegion(long id);
	//根据id获取扇形面域
	virtual IPedestrianFanShapeRegion* findPedestrianFanShapeRegion(long id);
	//根据id获取多边形面域
	virtual IPedestrianPolygonRegion* findPedestrianPolygonRegion(long id);
	//根据id获取人行道
	virtual IPedestrianSideWalkRegion* findPedestrianSideWalkRegion(long id);
	//根据id获取人行横道
	virtual IPedestrianCrossWalkRegion* findPedestrianCrossWalkRegion(long id);
	//根据id获取楼梯
	virtual IPedestrianStairRegion* findPedestrianStairRegion(long id);
	//根据id获取行人发生点	
	virtual IPedestrianPathPoint* findPedestrianPathStartPoint(long id);
	//根据id获取行人结束点
	virtual IPedestrianPathPoint* findPedestrianPathEndPoint(long id);
	//根据id获取行人决策点
	virtual IPedestrianPathPoint* findPedestrianDecisionPoint(long id);
	//根据id获取行人路径中间点
	virtual IPedestrianPathPoint* findPedestrianPathMiddlePoint(long id);
	//根据id获取行人路径，包括局部路径
	virtual IPedestrianPath* findPedestrianPath(long id);
	//根据id获取人行横道红绿灯
	virtual ICrosswalkSignalLamp* findCrosswalkSignalLamp(long id);
	//根据id获取行人发生点配置信息，id为行人发生点ID
	virtual Online::Pedestrian::PedestrianPathStartPointConfigInfo findPedestrianStartPointConfigInfo(long id);
	//根据id获取行人决策点配置信息，id为行人决策点ID
	virtual Online::Pedestrian::PedestrianDecisionPointConfigInfo findPedestrianDecisionPointConfigInfo(long id);
	//创建行人组成，name:组成名称，mpCompositionRatio:组成明细(key:行人类型编码,value:比重)，返回组成ID, 如果创建失败返回-1
	virtual long createPedestrianComposition(QString name, QMap<int, qreal> mpCompositionRatio);
	//更新行人组成，返回是否更新成功
	virtual bool updatePedestrianComposition(long compositionId, QMap<int, qreal> mpCompositionRatio);
	//移除行人组成
	virtual bool removePedestrianComposition(long compositionId);
	//新增层级，返回新增的层级信息，name:层级名称，height:层级高度，visible:是否可见，locked:是否锁定,锁定后面域不可以修改
	virtual Online::Pedestrian::LayerInfo addLayerInfo(QString name, qreal height, bool visible, bool locked);
	//删除某个层级，会删除层级当中的所有元素
	virtual void removeLayerInfo(long layerId);
	//更新层级信息, 如果layerId非法返回失败，name:层级名称，height:层级高度，visible:是否可见，locked:是否锁定,锁定后面域不可以修改
	virtual bool updateLayerInfo(long layerId, QString name, qreal height, bool visible, bool locked);
	//更新行人发生点配置信息
	virtual bool updatePedestrianStartPointConfigInfo(Online::Pedestrian::PedestrianPathStartPointConfigInfo info);
	//更新行人决策点配置信息
	virtual bool updatePedestrianDecisionPointConfigInfo(Online::Pedestrian::PedestrianDecisionPointConfigInfo info);
	//创建矩形行人面域, 传入矩形对角线的两个点，startPoint为左上角，endPoint为右下角
	virtual IPedestrianRectRegion* createPedestrianRectRegion(QPointF startPoint, QPointF endPoint);
	//删除矩形行人面域
	virtual void removePedestrianRectRegion(IPedestrianRectRegion* pIPedestrianRectRegion);
	//创建椭圆行人面域, 传入椭圆外接矩形对角线的两个点，startPoint为左上角，endPoint为右下角
	virtual IPedestrianEllipseRegion* createPedestrianEllipseRegion(QPointF startPoint, QPointF endPoint);
	//删除椭圆行人面域
	virtual void removePedestrianEllipseRegion(IPedestrianEllipseRegion* pIPedestrianEllipseRegion);
	//创建三角形行人面域, 三角形为矩形内接等腰三角形，传入矩形对角线的两个点，startPoint为左上角，endPoint为右下角
	virtual IPedestrianTriangleRegion* createPedestrianTriangleRegion(QPointF startPoint, QPointF endPoint);
	//删除三角形行人面域
	virtual void removePedestrianTriangleRegion(IPedestrianTriangleRegion* pIPedestrianTriangleRegion);
	//创建扇形行人面域, 传入扇形顶点，startPoint圆心，endPoint外半径终点
	virtual IPedestrianFanShapeRegion* createPedestrianFanShapeRegion(QPointF startPoint, QPointF endPoint);
	//删除扇形行人面域
	virtual void removePedestrianFanShapeRegion(IPedestrianFanShapeRegion* pIPedestrianFanShapeRegion);
	//创建多边形行人面域, 传入多边形顶点
	virtual IPedestrianPolygonRegion* createPedestrianPolygonRegion(QPolygonF polygon);
	//删除多边形行人面域
	virtual void removePedestrianPolygonRegion(IPedestrianPolygonRegion* pIPedestrianPolygonRegion);
	//创建人行道
	virtual IPedestrianSideWalkRegion* createPedestrianSideWalkRegion(QList<QPointF> vertexs);
	//删除人行道
	virtual void removePedestrianSideWalkRegion(IPedestrianSideWalkRegion* pIPedestrianSideWalkRegion);
	//创建人行横道
	virtual IPedestrianCrossWalkRegion* createPedestrianCrossWalkRegion(QPointF startPoint, QPointF endPoint);
	//删除人行横道
	virtual void removePedestrianCrossWalkRegion(IPedestrianCrossWalkRegion* pIPedestrianCrossWalkRegion);
	//创建楼梯
	virtual IPedestrianStairRegion* createPedestrianStairRegion(QPointF startPoint, QPointF endPoint);
	//删除楼梯
	virtual void removePedestrianStairRegion(IPedestrianStairRegion* pIPedestrianStairRegion);
	//创建行人发生点，scenePos:场景坐标，必须在现有的面域内，否则创建失败返回nullptr
	virtual IPedestrianPathPoint* createPedestrianPathStartPoint(QPointF scenePos);
	//删除行人发生点
	virtual void removePedestrianPathStartPoint(IPedestrianPathPoint* pIPedestrianPathStartPoint);
	//创建行人结束点，scenePos:场景坐标，必须在现有的面域内，否则创建失败返回nullptr
	virtual IPedestrianPathPoint* createPedestrianPathEndPoint(QPointF scenePos);
	//删除行人结束点
	virtual void removePedestrianPathEndPoint(IPedestrianPathPoint* pIPedestrianPathEndPoint);
	//创建行人决策点，scenePos:场景坐标，必须在现有的面域内，否则创建失败返回nullptr
	virtual IPedestrianPathPoint* createPedestrianDecisionPoint(QPointF scenePos);
	//删除行人决策点
	virtual void removePedestrianDecisionPoint(IPedestrianPathPoint* pIPedestrianDecisionPoint);
	//创建行人路径（或行人局部路径），pStartPoint:行人发生点（或行人决策点）, pEndPoint:行人结束点, middlePoints:一组中间必经点，必经点必须在现有的面域内且从起点到中间点再到终点必须依次面域内连通，否则创建失败返回nullptr
	virtual IPedestrianPath* createPedestrianPath(IPedestrianPathPoint* pStartPoint, IPedestrianPathPoint* pEndPoint, QList<QPointF> middlePoints);
	//删除行人路径
	virtual void removePedestrianPath(IPedestrianPath* pIPedestrianPath);
	//创建人行横道信号灯，pTrafficLight:信号机, name:名称, crosswalkId:人行横道ID，scenePos:位于人行横道内的场景坐标, isPositive:信号灯管控防线是否为正向，否则为反向，如果该方向已经添加过信号灯则返回nullptr
	virtual ICrosswalkSignalLamp* createCrossWalkSignalLamp(ISignalController* pTrafficLight, QString name, long crosswalkId, QPointF scenePos, bool isPositive);
	//删除人行横道信号灯
	virtual void removeCrossWalkSignalLamp(ICrosswalkSignalLamp* pICrosswalkSignalLamp);

	///所有冲突区参数查询设置接口
	//获取所有所有冲突点的冲突设置参数
	virtual QList<Online::Intersection::ConnectorAreaIntersectionParam> getIntersectionRelationshipParam();
	//设置所有冲突点的冲突设置参数
	virtual bool setIntersectionRelationshipParam(QHash<QString, Online::Intersection::IntersectionParam> relationshipParam);
	//给定冲突点id计算初始的优先权类型, crosspointId格式不正确返回-1， 正常返回值：路权优先级, 0-3, 0 根据组合后车道连接id，id较小的具有优先权；1 根据组合后的车道连接id, id较大的具有优先权；2 忽略优先权，直接通行；3 同等路权，按照既有的算法判断谁先通行
	virtual int getInitialPriorityType(QString crosspointId);
	//给定连接段id获取所在的面域id，没有找到的情况下返回-1
	virtual long getAreaIdByConnectorId(long connectorId);

	//////////////// 收费站相关接口
	// 收费车道列表
	virtual QList<ITollLane*> tollLanes();
	virtual QList<ITollDecisionPoint*> tollDecisionPoints();
	virtual QList<IParkingRegion*> parkingRegions();
	virtual QList<IParkingDecisionPoint*> parkingDecisionPoints();

	virtual ITollLane* findTollLane(long id);
	virtual ITollDecisionPoint* findTollDecisionPoint(long id);
	virtual IParkingRegion* findParkingRegion(long id);
	virtual IParkingDecisionPoint* findParkingDecisionPoint(long id);

	virtual ITollRouting* findTollRouing(long id);
	virtual IParkingRouting* findParkingRouing(long id);

	// findParkingStall
	// findTollPoint

	virtual void removeTollLane(ITollLane* pITollLane);
	//移除收费决策点
	virtual void removeTollDecisionPoint(ITollDecisionPoint* pITollDecisionPoint);
	virtual void removeParkingRegion(IParkingRegion* pIParkingRegion);
	virtual void removeParkingDecisionPoint(IParkingDecisionPoint* pIParkingDecisionPoint);
	virtual void removeTollRouting(ITollRouting* pITollRouting);
	virtual void removeParkingRouting(IParkingRouting* pIParkingRouting);

	virtual void removeTollLaneById(long id);
	virtual void removeTollDecisionPointById(long id);
	virtual void removeParkingRegionById(long id);
	virtual void removeParkingDecisionPointById(long id);
	virtual void removeTollRoutingById(long id);
	virtual void removeParkingRoutingById(long id);

	virtual ITollLane* createTollLane(const Online::TollStation::DynaTollLane& param);
	virtual IParkingRegion* createParkingRegion(const Online::ParkingLot::DynaParkingRegion& param);

	virtual ITollLane* updateTollLane(const Online::TollStation::DynaTollLane& param);
	virtual IParkingRegion* updateParkingRegion(const Online::ParkingLot::DynaParkingRegion& param);

	// 创建收费决策点
	virtual ITollDecisionPoint* createTollDecisionPoint(ILink* pLink, qreal distance, QString name = QString());
	virtual ITollRouting* createTollRouting(ITollDecisionPoint* pDeciPoint, ITollLane* pITollLane);
	// 创建停车决策点
	virtual IParkingDecisionPoint* createParkingDecisionPoint(ILink* pLink, qreal distance, QString name = QString());
	virtual IParkingRouting* createParkingRouting(IParkingDecisionPoint* pDeciPoint, IParkingRegion* pIParkingRegion);

	// 收费停车时间分布列表
	virtual QList<Online::TollStation::DynaTollParkingTimeDis> tollParkingTimeDis();
	// 创建收费停车时间分布
	virtual Online::TollStation::DynaTollParkingTimeDis createTollParkingTimeDis(const Online::TollStation::DynaTollParkingTimeDis& param);
	virtual void removeTollParkingTimeDis(long id);
	virtual Online::TollStation::DynaTollParkingTimeDis updateTollParkingTimeDis(const Online::TollStation::DynaTollParkingTimeDis& param);

	// 所有停车事件分布信息
	virtual QList<Online::ParkingLot::DynaParkingTimeDis> parkingTimeDis();
	// 创建停车时间分布信息
	virtual Online::ParkingLot::DynaParkingTimeDis createParkingTimeDis(const Online::ParkingLot::DynaParkingTimeDis& param);
	// 移除停车时间分布信息
	virtual void removeParkingTimeDis(long id);
	// 更新停车时间分布信息
	virtual Online::ParkingLot::DynaParkingTimeDis updateParkingTimeDis(const Online::ParkingLot::DynaParkingTimeDis& param);

	/************************ 节点基础操作 ************************/
	// 创建节点 startPoint:左上角起始点坐标 endPoint:右下角终点坐标 name:节点名称
	virtual IJunction* createJunction(QPointF startPoint, QPointF endPoint, QString name);

	// 查找节点 junctionId:节点ID
	virtual IJunction* findJunction(long junctionId);

	// 获取所有节点
	virtual QList<IJunction*> getAllJunctions();

	// 删除节点 junctionId:节点ID
	virtual bool removeJunction(long junctionId);

	// 更新节点名称 junctionId:节点ID name:新的节点名称
	virtual bool updateJunctionName(long junctionId, QString name);

	/************************ 排放基础操作 ************************/
	// 创建评价区域 startPoint:左上角起始点坐标 endPoint:右下角终点坐标 name:评价区域名称
	virtual IEvaluateArea* createEvaluateArea(QPointF startPoint, QPointF endPoint, QString name);

	// 查找评价区域 areaId:评价区域ID
	virtual IEvaluateArea* findEvaluateArea(long areaId);

	// 获取所有节点
	virtual QList<IEvaluateArea*> getAllEvaluateAreas();

	// 删除节点 areaId:评价区域ID
	virtual bool removeEvaluateArea(long areaId);

	// 更新评价区域名称 areaId:评价区域ID name:新的评价区域名称
	virtual bool updateEvaluateAreaName(long areaId, QString name);

	// 获取排放参数
	virtual QJsonObject getEmissionsConfig();

	// 设置排放参数
	virtual bool updateEmissionsConfig(QJsonObject obj);

	// 获取排放
	virtual QHash<QString, QHash<long, QVector<QPointF>>> getEmissions(QString emType);

	// 获取排放热力图数据
	virtual QJsonObject getEmissionsHeatMapData(long simuTime);

	/************************ 时间段管理 ************************/
	// 获取所有时间段
	virtual QList<Online::Junction::FlowTimeInterval> getFlowTimeIntervals();

	// 添加时间段,返回新时间段ID,失败返回-1
	virtual Online::Junction::FlowTimeInterval addFlowTimeInterval();

	// 删除时间段 timeId:时间段ID
	virtual bool deleteFlowTimeInterval(long timeId);

	// 更新时间段 timeId:时间段ID startTime:开始时间(秒) endTime:结束时间(秒)
	virtual Online::Junction::FlowTimeInterval updateFlowTimeInterval(long timeId, long startTime, long endTime);

	/************************ 流量管理 ************************/
	// 获取节点流向信息 junctionId:节点ID
	virtual QList<Online::Junction::FlowTurning> getJunctionFlows(long junctionId);

	// 更新流向流量 timeId:时间段ID junctionId:节点ID turningId:转向ID inputFlowValue:输入流量值(辆/小时)
	virtual bool updateFlow(long timeId, long junctionId, long turningId, long inputFlowValue);

	/************************ 算法参数设置 ************************/
	// 更新流量算法参数 theta:参数θ(0.01-1) bpra:BPR路阻参数A(0.05-0.5) bprb:BPR路阻参数B(1-10) maxIterateNum:最大迭代次数(1-5000)
	virtual bool updateFlowAlgorithmParams(double theta, double bpra, double bprb, long maxIterateNum);

	/************************ 路径构建 ************************/
	// 构建并应用路径,返回路径结果映射:<起始路段ID,终点路段ID> -> 可行路径列表
	virtual QMap<QPair<long, long>, QVector<QList<ILink*>>> buildAndApplyPaths(long minPathNum = 3);

	//优化决策点位置
	virtual void reSetDeciPoint();

	//优化连接段
	virtual void reSetLaneConnector(IRouting* pITollRouting);

	/************************ 流量计算 ************************/
	// 计算并应用流量结果,返回时间段ID到流量计算结果的映射
	virtual QHash<long, QList<Online::Junction::FlowTurning>> calculateFlows();

	/**************用于录制视频****************/
	virtual QString Recordvideo(QString path);
	//停止录屏
	virtual QString stopScreenRecording();

};

#endif // NETINTERFACE_H
