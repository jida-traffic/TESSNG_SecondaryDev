#pragma once
#include <qobject.h>

#include "tessinterfaces_global.h"

class TESSINTERFACES_EXPORT IControlCentre :
    public QObject
{
	Q_OBJECT
public:
	virtual ~IControlCentre() = default;
	virtual void startSimu();
	virtual void pauseSimu();
	virtual void stopSimu(bool stopDS = true);
	virtual void setStopSimuFlags();

	virtual void loadPlugins(QString tessDirPath = QString());
	virtual void loadPlugin(QString tessLibPath);

	virtual void openFile();

	virtual void cleanInStopSimu();

	virtual bool activeSoftware(bool judgeFileExsit) { return false; }
	virtual bool activeSoftwareWithTrue() { return false; }

public slots:
	virtual void doStartSimu();
	virtual void doStopSimu(bool stopDS = true);
	// 先清理，后停止
	virtual void doStopSimuAfterClean(bool stopDS = true);

signals:
	void startedSimu();
	void pausedSimu();
	void stoppedSimu();
};

extern TESSINTERFACES_EXPORT IControlCentre* gpControlCentre;