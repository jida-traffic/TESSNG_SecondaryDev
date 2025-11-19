/*****************************************************************************
* 客户自定义路网元素基础类
* 定义一些抽象方法，用户借此创建可视化元素并实现这些方法后，可以在插件环境下与TESS NG进行交互
******************************************************************************/

#ifndef __CustomerGraphicsObject__
#define __CustomerGraphicsObject__

#include "tessinterfaces_global.h"

#include <QGraphicsObject>
#include <QSqlQuery>
#include <QSqlError>

class GraphicsScene;
class RoadNetScene;
class ISection;

class TESSINTERFACES_EXPORT CustomerGraphicsObject : public QGraphicsObject
{
	Q_OBJECT

public:
	CustomerGraphicsObject(QGraphicsItem* parent = nullptr);
	//CustomerGraphicsObject(GraphicsScene* pGraphicsScene, QGraphicsItem* parent = nullptr);
	virtual ~CustomerGraphicsObject();

	virtual CustomerGraphicsObject* copyThis(qlonglong linkId, bool addToScene) { return nullptr; };
	virtual long identification() { return -1; };

	virtual int type() const override { return QGraphicsItem::UserType + 1000; }
	virtual QString tableName() { return QString(); }
	virtual qreal distToStartPoint() { return 0; }
	virtual qreal length() { return 0; }
	virtual bool insertRecord(QSqlDatabase& sqlDb) { return true; };
	virtual ISection* road() { return nullptr; };
	virtual void adjust() {}
	virtual void detect() {}
	qreal calcFontSize(qreal fsize) const;
	virtual void clearSubItem() {}
	virtual void restoreData(void* oldData, int oper) {}
	virtual QVariant extension(const QVariant& variant) const { return {}; };

public:
	QRectF boundingRect() const;
	QPainterPath shape() const;
	void paint(QPainter* painter, const QStyleOptionGraphicsItem* option, QWidget* widget);

};

#endif
