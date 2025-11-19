/********************************************************************************
* 工作线程接口，由TESS NG实现，用户可以借此调用接口方法，获取仿真过程一些数据，如线程ID、是否正在运行、信号量等
*********************************************************************************/

#ifndef IVEHICLEDRIVINGTASK_H
#define IVEHICLEDRIVINGTASK_H

#include "tessinterfaces_global.h"

#include <QObject>

class TESSINTERFACES_EXPORT IVehicleDrivingTask
{
public:
	virtual ~IVehicleDrivingTask() = default;
	virtual long id();
	virtual bool finished();
	virtual bool running();
	virtual bool outTime();
	virtual int semaphoreAvailable();
	virtual long currBatchNumber();
	virtual void startTask(long currBatchNumber = 0);
	virtual void setStopped(bool stopped);
};

#endif // IVEHICLEDRIVINGTASK_H