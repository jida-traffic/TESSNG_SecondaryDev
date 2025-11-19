#ifndef __IReconstruction__
#define __IReconstruction__

#include "tessinterfaces_global.h"
#include "Plugin/_datastruct.h"
#include "UnitChange.h"
#include <QObject>

class TESSINTERFACES_EXPORT IReconstruction
{
public:
	virtual ~IReconstruction() = default;
	/*改扩建ID*/
	virtual long id() = 0;
	/*改扩建起始施工区ID*/
	virtual long roadWorkZoneId() = 0;
	/*被借道限行区ID*/
	virtual long limitedZoneId() = 0;
	/*保通长度，单位米*/
	virtual qreal passagewayLength(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*改扩建持续时间*/
	virtual long duration() = 0;
	/*借道数量*/
	virtual int borrowedNum() = 0;
	/*保通开口限速，单位像素（km/h）*/
	virtual qreal passagewayLimitedSpeed(UnitOfMeasure unit = UnitOfMeasure::Default) = 0;
	/*初始创建CPU时间，单位毫秒*/
	virtual qint64 createCPUTime() = 0;
	/*改扩建动态参数 --返回参数为米制*/
	virtual Online::DynaReconstructionParam dynaReconstructionParam() = 0;
};

#endif