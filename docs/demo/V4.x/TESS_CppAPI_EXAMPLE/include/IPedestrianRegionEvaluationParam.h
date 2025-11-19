#pragma once

#include <QMap>
#include <QColor>

class IPedestrianRegionEvaluationParam
{
public:
    virtual ~IPedestrianRegionEvaluationParam() = default;
    /* 仿真起始记录时间 */
    long startRecordTime{ 1 };
    /* 仿真结束记录时间 */
    long endRecordTime{ 3600 };
    /* 聚合间隔时间 */
    long dataIntervalTime{ 300 };
    /* 是否评价所有面域 */
    bool enableGlobalEvaluation{ false };
    /* 是否记录交通流量数据 */
    bool recordTrafficFlow{ false };
    /* 是否记录交通密度数据 */
    bool recordTrafficDensity{ false };
    /* 是否记录交通速度数据 */
    bool recordTrafficSpeed{ false };
};