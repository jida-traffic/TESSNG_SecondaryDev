#ifndef __IRoadNet__
#define __IRoadNet__

#include "tessinterfaces_global.h"
#include "UnitChange.h"

#include <QObject>
#include <QJsonObject>

class TESSINTERFACES_EXPORT IRoadNet
{
public:
	virtual ~IRoadNet() = default;
	/* 路网ID */
	virtual long id();
	/* 路网名称 */
	virtual QString netName();
	/* 源数据路径，可以是本地文件，可以是网络地址 */
	virtual QString url();
	/* 来源分类：TESSNG，表示TESSNG自建、OpenDrive，表示由OpenDrive数据导入，geojson，表示由geojson数据导入 */
	virtual QString type();
	/* 底图数据路网，可以是本地文件，也可以是网络地址 */
	virtual QString bkgUrl();
	/* 说明 */
	virtual QString explain();
	/* 其它属性json字数据 */
	virtual QJsonObject otherAttrs();
	/* 中心点位置 */
	virtual QPointF centerPoint(UnitOfMeasure unit = UnitOfMeasure::Default);

};

#endif