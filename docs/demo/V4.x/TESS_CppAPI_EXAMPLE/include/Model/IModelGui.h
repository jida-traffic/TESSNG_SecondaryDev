#ifndef __IModelGui__
#define __IModelGui__

#include "tessinterfaces_global.h"

class QAction;
class TESSINTERFACES_EXPORT IModelGui
{
public:
	IModelGui() {}
	virtual void init(bool isCerted) {}
	virtual ~IModelGui() {}
	virtual QList<QAction*> actions() { return {}; };

};

#endif