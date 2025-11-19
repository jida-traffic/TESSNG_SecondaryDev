/*******************************************************************************
* 路网元素类型常量定义
********************************************************************************/

#ifndef __NetItemType__
#define __NetItemType__

#include "../tessinterfaces_global.h"

#include <QGraphicsItem>

class TESSINTERFACES_EXPORT NetItemType {
public:
	static const int GVertexType;
	static const int GLinkType;
	static const int GLaneType;
	static const int GConnectorType;
	static const int GLaneConnectorType;
	static const int GDecisionType;
	static const int GDeparturePointType;
	static const int GVehicleType;
	static const int GSignalLampType;
	static const int GVehicleDrivInfoCollecterType;
	static const int GVehicleQueueCounterType;
	static const int GVehicleTravelDetectorType;
	static const int GReduceSpeedAreaType;
	static const int GBusStationType;
	static const int GBusLineType;
	static const int GGuideArrowType;
	static const int GConnectorAreaType;
	static const int GLinkCenterType;
	static const int GVehicleDetectorType;
	
	static const int GRectPedestrianAreaType;
	static const int GEllipsePedestrianAreaType;
	static const int GTrianglePedestrianAreaType;
	static const int GFanShapePedestrianAreaType;
	static const int GPolygonPedestrianAreaType;
	static const int GSideWalkPedestrianAreaType;
	static const int GCrossWalkPedestrianAreaType;
	static const int GStairRegionAreaType;
	static const int GPedestrianGateGroupType;
	static const int GPedestrianGateType;
	static const int GPedestrianPathPointType;
	static const int GPedestrianPathStartPointType;
	static const int GPedestrianPathEndPointType;
	static const int GPedestrianPathVirtualPointType;
	static const int GPedestrianPathType;
	static const int GCrossWalkTrafficLightType;
	static const int GPedestrianDecisionPointType;
	static const int GPedestrianTravelDetectorType;

	static const int LaneType;
	static const int LaneConnectorType;

	static const int GRsuType;
	static const int GRoadWorkZoneType;
	static const int GAccidentZoneType;
	static const int GReconstructionType;
	static const int GLimitedZoneType;

	// toll station
	static const int GTollLane;
	static const int GTollPoint;
	static const int GTollDeciPoint;
	static const int GTollAddRouting;
	static const int GTollDelRouting;

	static const int GEvaluateAreaType;
	static const int GJunctionType;

	static const int GParkingGarageOffroad ;
	static const int GParkingRegion ;
	static const int GParkingStall ;
	static const int GParkingDeciPoint ;
	static const int GAddParkingDeciRouter;
	static const int GDelParkingDeciRouter;

	static const int GLimitLane;
	static const int GNetText;
	static const int GNetImage;

	static const int GFlowerfedType;
	static const int G3DpointType;
	static const int GBaseSurfaceType;
};

#endif