/***********************************************************************
 * 车辆监视接口，TESS NG实现，用户借此接口可以获取车辆与路段、连接段、车道、“车道连接”之间关系等
 ***********************************************************************/

#ifndef IVEHICLESUPERVISOR_H
#define IVEHICLESUPERVISOR_H

#include "tessinterfaces_global.h"

#include <QObject>

class IVehicle;

class TESSINTERFACES_EXPORT IVehicleSupervisor
{
public:
	virtual ~IVehicleSupervisor() = default;
	virtual QList<IVehicle*> vehisInLink(long linkId);
	virtual QList<IVehicle*> vehisInLane(long laneId);
	virtual QList<IVehicle*> vehisInConnector(long connectorId);
	virtual QList<IVehicle*> vehisInLaneConnector(long connectorId, long fromLaneId, long toLaneId);

	/*两车多边型间的最近距离，单位：像素*/
	virtual qreal distanceTwoVehicle(IVehicle *pV1, IVehicle *pV2);

private:
	
};

#endif // IVEHICLESUPERVISOR_H