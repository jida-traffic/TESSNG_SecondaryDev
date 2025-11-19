project(TESS_CppAPI_EXAMPLE VERSION 1.0.0 LANGUAGES CXX)

set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

if(MSVC)
    add_compile_options(/utf-8)
    add_compile_options(/wd4819)
endif()

file(GLOB SOURCES 
    "*.cpp"
    "*.h"
    "*.hpp"
    "*.ui"
    "Resource/*.qrc"
    "Resource/*.rc"
)

add_executable(${PROJECT_NAME} ${SOURCES})

set_target_properties(${PROJECT_NAME} PROPERTIES AUTOMOC_PATH_PREFIX ON)

set_target_properties(${PROJECT_NAME} PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY_DEBUG "${CMAKE_SOURCE_DIR}/x64/Debug"
    RUNTIME_OUTPUT_DIRECTORY_RELEASE "${CMAKE_SOURCE_DIR}/x64/Release"
)

target_link_libraries(${PROJECT_NAME} PRIVATE
    Qt5::Widgets
    Qt5::Core
    Qt5::Gui
    Qt5::Sql
    Qt5::PrintSupport
    Qt5::Charts
    Qt5::WebSockets
    Qt5::Concurrent
    Qt5::Xml
    Qt5::Network
    Qt5::Svg
)

target_include_directories(${PROJECT_NAME} PRIVATE
    ${CMAKE_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}
)

target_link_libraries(${PROJECT_NAME} PRIVATE 
    optimized ${CMAKE_SOURCE_DIR}/lib/release/TessInterfaces.lib
    debug ${CMAKE_SOURCE_DIR}/lib/debug/TessInterfaces.lib
)

