cmake_minimum_required(VERSION 3.16)
project(TESS_CppAPI_DEMO VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_AUTOMOC_PATH_PREFIX ON)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

if(MSVC)
    add_compile_options(/utf-8)
    add_compile_options(/wd4819)
endif()

find_package(Qt5 REQUIRED COMPONENTS widgets core gui sql printsupport charts websockets concurrent xml network svg)

set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_DEBUG "${CMAKE_SOURCE_DIR}/x64/Debug")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/x64/Release")

# 1. 基础功能模块
add_subdirectory(1.基础功能/基础样例 TESS_CppAPI_EXAMPLE)
add_subdirectory(1.基础功能/停车场仿真 ParkingSimulation)
add_subdirectory(1.基础功能/收费站仿真 TollSimulation)
add_subdirectory(1.基础功能/节点评价 NodeEvaluation)
add_subdirectory(1.基础功能/行人仿真 PedestrianSimulation)

# 2. 信号控制模块
add_subdirectory(2.信号控制/单点信号控制 SingleSignalControl)
add_subdirectory(2.信号控制/多时段多方案信号控制 MultiPeriodSignalControl)
add_subdirectory(2.信号控制/干线绿波信号控制 GreenWaveControl)
add_subdirectory(2.信号控制/公交优先信号控制 BusPriorityControl)
add_subdirectory(2.信号控制/匝道自适应信号控制 RampAdaptiveControl)
add_subdirectory(2.信号控制/行人交叉口信号控制 PedestrianCrossingControl)

# 3. 交通事故与施工改扩建模块
add_subdirectory(3.交通事故与施工改扩建/交通事故（创建抛锚车辆） BrokenVehicleCreation)
add_subdirectory(3.交通事故与施工改扩建/交通事故（创建事故区域） AccidentZoneCreation)
add_subdirectory(3.交通事故与施工改扩建/施工改扩建 RoadConstruction)

# 4. 车道管控与车辆引导模块
add_subdirectory(4.车道管控与车辆引导/车辆路径诱导 VehicleRoutingGuidance)
add_subdirectory(4.车道管控与车辆引导/车辆速度引导 VehicleSpeedGuidance)
# add_subdirectory(4.车道管控与车辆引导/可变车道管控 VariableLaneControl)
# add_subdirectory(4.车道管控与车辆引导/可变限速管控（基于强化学习） VariableSpeedControl)
add_subdirectory(4.车道管控与车辆引导/应急车道开放管控 EmergencyLaneControl)
add_subdirectory(4.车道管控与车辆引导/优先车道管控 PriorityLaneControl)

# 5. 智能网联与自动驾驶模块
add_subdirectory(5.智能网联与自动驾驶/智能网联车辆编队行驶 CVPlatooning)
add_subdirectory(5.智能网联与自动驾驶/智能网联车辆汇入决策 CVMergeDecision)
add_subdirectory(5.智能网联与自动驾驶/智能网联车辆事故避让 CVCollisionAvoidance)
add_subdirectory(5.智能网联与自动驾驶/自动驾驶仿真 AutonomousDriving)

# 6. 模型参数标定模块
# add_subdirectory(6.模型参数标定/交叉口模型参数标定 IntersectionCalibration)
# add_subdirectory(6.模型参数标定/快速路模型参数标定 ExpresswayCalibration)
