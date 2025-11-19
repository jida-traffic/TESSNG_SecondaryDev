#ifndef __StepsPerCall__
#define __StepsPerCall__

#include "tessinterfaces_global.h"

class TESSINTERFACES_EXPORT StepsPerCall
{
public:
	StepsPerCall();
	virtual ~StepsPerCall() = default;
public:
	bool IsPermitForVehicleDraw;

	int Steps_beforeNextPoint;
	int Steps_nextPoint;
	int Steps_afterStep;
	int Steps_isStopDriving;

	int Steps_reCalcdesirSpeed;
	int Steps_calcMaxLimitedSpeed;
	int Steps_calcLimitedLaneNumber;
	int Steps_calcSpeedLimitByLane;
	int Steps_calcChangeLaneSafeDist;
	int Steps_reCalcToLeftLane;
	int Steps_reCalcToRightLane;
	int Steps_reCalcToLeftFreely;
	int Steps_reCalcToRightFreely;
	int Steps_afterCalcTracingType;
	int Steps_beforeMergingToLane;
	int Steps_reSetFollowingType;
	int Steps_reCalcDismissChangeLane;
	int Steps_calcAcce;
	int Steps_reSetAcce;
	int Steps_reSetSpeed;
	int Steps_reCalcAngle;
	int Steps_recentTimeOfSpeedAndPos;
	int Steps_travelOnChangingTrace;
	int Steps_leaveOffChangingTrace;
	int Steps_beforeNextRoad;
};

extern StepsPerCall TESSINTERFACES_EXPORT gStepsPerCall;

#endif