#ifndef __objtype__
#define __objtype__

#include "../tessinterfaces_global.h"
#include "../UnitChange.h"

#include <QObject>

class TESSINTERFACES_EXPORT objint {
public:
	int value;
public:
	objint()
	{
		value = 0;
	}
	objint(int v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objint64 {
public:
	qint64 value;
public:
	objint64()
	{
		value = 0;
	}
	objint64(qint64 v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objlong {
public:
	long value;
public:
	objlong()
	{
		value = 0;
	}
	objlong(long v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objdouble {
public:
	double value;
public:
	objdouble()
	{
		value = 0.0;
	}
	objdouble(double v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objreal {
public:
	qreal value;
public:
	objreal()
	{
		value = 0.0;
	}
	objreal(qreal v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objbool {
public:
	bool value;
public:
	objbool()
	{
		value = false;
	}
	objbool(bool v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objstring {
public:
	QString value;
public:
	objstring()
	{
		value = QString();
	}
	objstring(QString v)
	{
		value = v;
	}
};

class TESSINTERFACES_EXPORT objUnitOfMeasure {
public:
	UnitOfMeasure value;
public:
	objUnitOfMeasure()
	{
		value = UnitOfMeasure::Metric;
	}
	objUnitOfMeasure(UnitOfMeasure* v)
	{
		value = *v;
	}
};

#endif