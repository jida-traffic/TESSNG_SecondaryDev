/********************************************************************************
* 用户插件参于在路网层与TESS NG交互，用户实现全部或部分方法扩展TESS NG的运用
*********************************************************************************/

#ifndef __CustomerNet__
#define __CustomerNet__

#include "../tessinterfaces_global.h"
#include "_netitem.h"
#include "tessplugin.h"

#include <QObject>
#include <QPainter>
#include <QStyleOptionGraphicsItem>
#include <QWidget>
//#include <QSqlQuery>

class ILink;
class CustomerGraphicsObject;
class ILaneObject;

class TESSINTERFACES_EXPORT  CustomerNet
{
public:
	CustomerNet() {}
	virtual ~CustomerNet();
	//路网文件后缀
	virtual bool netFileSuffix(QString &suffix);
	//添加用户设计的表
	virtual QMap<QString, QString> customerTableDDL();
	/*插入用户插件创建的表数据*/
	virtual void insertCustomerData();
	//删除用户插件创建的表数据
	virtual void deleteCustomerData();
	//加载路网前的准备
	virtual void beforeLoadNet();
	//加载完路网后的行为
	virtual void afterLoadNet();
	//从场景中移除插件创建的对象
	virtual bool removeItemFromScene(CustomerGraphicsObject *pItem);
	//路段类型
	virtual bool linkType(QList<QString> &lType);
	//车道类型
	virtual bool laneType(QList<QString> &lType);
	//创建车道
	virtual bool linkBuildGLanes(ILink *pILink);
	//曲化精度
	virtual bool curvaturePrecision(int itemType, long itemId, qreal &extend1, qreal &extend2, qreal &precision);
	//曲化最短距离, itemType：GLinkType或GConnectorType，itemId：路段或连接段ID，minDist：最短距离，单位米
	virtual bool curvatureMinDist(int itemType, long itemId, qreal& minDist);
	//是否允许调用客户绘制，默认为true。本方法的目的是：在python环境减少不必要的对python方法调用，消除对运行效率的负面影响
	virtual bool isPermitForCustDraw();
	//绘制路网元素
	virtual bool paint(int itemType, long itemId, QPainter *painter);
	//绘制车道或车道连接
	virtual bool paintLaneObject(ILaneObject* pILaneObj, QPainter* painter);
	//路段brush颜色
	virtual bool linkBrushColor(long linkId, QColor& color);
	//车道brush颜色
	virtual bool laneBrushAndPen(long laneId, QBrush &brush, QPen &pen);
	//面域brush颜色
	virtual bool connectorAreaBrushColor(long connAreaId, QColor& color);
	//写标签，按照给定的属性名和字体大小（米）,outPropName是牧举类型GraphicsItemPropName
	virtual void labelNameAndFont(int itemType, long itemId, int &outPropName, qreal &outFontSize);
	//是否绘标签
	//virtual bool isDrawLabel(int itemType, long itemId);
	//是否画路段中心线
	virtual bool isDrawLinkCenterLine(long linkId);
	//是否绘路段四个拐角
	virtual bool isDrawLinkCorner(long linkId);
	//是否画车道中心线
	virtual bool isDrawLaneCenterLine(long laneId);

	//QGraphicsView的keyPressEvent事件后的行为
	virtual void afterViewKeyPressEvent(QKeyEvent* event);
	//QGraphicsView的keyReleaseEvent事件后的行为
	virtual void afterViewKeyReleaseEvent(QKeyEvent *event);
	//QGraphicsView的mouseDoubleClickEvent事件后的行为
	virtual void afterViewMouseDoubleClickEvent(QMouseEvent * event);
	//QGraphicsView的mouseMoveEvent事件后的行为
	virtual void afterViewMouseMoveEvent(QMouseEvent *event);
	//QGraphicsView的mousePressEvent事件后的行为
	virtual void afterViewMousePressEvent(QMouseEvent *event);
	//QGraphicsView的mouseReleaseEvent事件后的行为
	virtual void afterViewMouseReleaseEvent(QMouseEvent *event);
	//QGraphicsView的resizeEvent事件后的行为
	virtual void afterViewResizeEvent(QResizeEvent *event);
	//鼠标滚动事件响应
	virtual void afterViewWheelEvent(QWheelEvent *event);
	//滚动条移动事件后的行为
	virtual void afterViewScrollContentsBy(int dx, int dy);

	//移动路段断点后调用 参数 pILink:路段, vertexIndex:断点序号, pressPoint:断点移动前位置, releasePoint:断点移动后位置
	virtual void afterLinkVertexMove(ILink* pILink, int vertexIndex, QPointF pressPoint, QPointF releasePoint);
};

#endif