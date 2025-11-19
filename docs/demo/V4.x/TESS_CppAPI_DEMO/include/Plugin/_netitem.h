/********************************************************************
* 路网元素对应数据库表名
*********************************************************************/
#ifndef __NetItem__
#define __NetItem__

#define NET_ITEM_Vertex "Vertex"
#define NET_ITEM_Link "Link"
#define NET_ITEM_Lane "Lane"
#define NET_ITEM_Connector "Connector"
#define NET_ITEM_DecisionPoint "DecisionPoint"
#define NET_ITEM_DeparturePoint "DeparturePoint"
#define NET_ITEM_DepartureInterval "DepartureInterval" 
#define NET_ITEM_Routing "Routing"
#define NET_ITEM_RoutingFLowRatio "RoutingFLowRatio" 
#define NET_ITEM_BusLine "BusLine"
#define NET_ITEM_BusStation "BusStation"
#define NET_ITEM_BusStationLine "BusStationLine"

//路网元素属性名的枚举
enum GraphicsItemPropName {None, Id, Name };


#endif