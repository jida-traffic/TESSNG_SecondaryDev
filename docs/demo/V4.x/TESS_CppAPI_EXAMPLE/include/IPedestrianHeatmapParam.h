#pragma once

#include <QMap>
#include <QColor>

class IPedestrianHeatmapParam
{
public:
    virtual ~IPedestrianHeatmapParam() = default;
    /* 仿真起始记录时间 */
    long startRecordTime{ 1 };
    /* 仿真结束记录时间 */
    long endRecordTime{ 3600 };
    /* 聚合间隔时间 */
    long dataIntervalTime{ 300 };
    /* 是否启用热力图 */
    bool enableHeatmap{ false };
    /* 是否启用全局热力图  开启后全部面域都会有热力图*/
    bool enableGlobalHeatmap{ false };
    /* 热力图聚合方式 true表示最新 false表示累积 */
    bool useLatestAggregation{ true };
    /* 热力图可视化类型 0：流量 1：密度 2：速度*/
    int heatmapVisualizationType{ 0 };
    /* 是否自动计算热力图颜色 */
    bool autoCalcHeatmapColor{ true };
    /* 流量热力图颜色配置 */
    QMap<qreal, QColor> trafficHeatmapColorConfig;
    /* 密度热力图颜色配置 */
    QMap<qreal, QColor> densityHeatmapColorConfig;
    /* 速度热力图颜色配置 */
    QMap<qreal, QColor> speedHeatmapColorConfig;
};