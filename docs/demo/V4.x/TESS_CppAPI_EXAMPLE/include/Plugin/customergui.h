/********************************************************************************
* 用户插件参于用户交互的抽象类，用户实现全部或部分方法扩展TESS NG的运用
********************************************************************************/

#ifndef __CustomerGui__
#define __CustomerGui__

#include "../tessinterfaces_global.h"

#include <QObject>
#include <QToolBar>
#include <QMenuBar>
#include <QAction>

class TESSINTERFACES_EXPORT CustomerGui
{
public:
	virtual ~CustomerGui();

	virtual QList<QMenu*> menus();
	virtual QList<QToolBar*> toolBars();
	virtual QList<QAction*> actions();
};

#endif