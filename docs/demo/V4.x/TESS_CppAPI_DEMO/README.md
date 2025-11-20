# TESSNG C++ API Demo 使用指南

## 项目简介

本项目是基于 TESSNG C++ SDK 开发的交通仿真Demo集合，涵盖基础功能、信号控制、交通事故处理、车道管控、智能网联和模型参数标定等功能。

## 环境要求

### 必需软件
- **CMake 3.16 或更高版本** - 项目构建工具
- **Visual Studio Code/2019/2022** - C++ 编译器（推荐 VS Code）
- **Qt 5.15 ** - GUI 框架和依赖库
- **Windows 10/11** - 操作系统

### VSCode 插件推荐
安装以下插件以获得更好的开发体验：
- **CMake Tools** - CMake 项目支持
- **CMake** - CMake 语法高亮
- **C/C++** - C++ 代码智能提示
- **Qt Configure** - Qt 项目支持（可选）

## 快速开始

### 1. 安装 CMake

#### 方法一：安装包安装
1. 访问 [CMake 官网](https://cmake.org/download/)
2. 下载 Windows x64 安装包（如 cmake-3.27.0-windows-x86_64.msi）
3. 运行安装程序，确保勾选 **Add CMake to system PATH**

#### 方法二：VSCode 扩展安装
1. 在 VSCode 扩展商店搜索 **CMake Tools**
2. 点击安装，扩展会自动安装 CMake

### 2. 配置开发环境

#### 安装 Qt
1. 访问 [Qt 官网](https://www.qt.io/download)
2. 下载 Qt Online Installer
3. 安装 Qt 5.15 或更高版本，确保包含以下组件：
   - Qt 5.15.x → MinGW 或 MSVC 2019 64-bit
   - Qt Charts
   - Qt WebSockets

#### 设置环境变量
将 Qt 的 bin 目录添加到系统 PATH：
```
C:\Qt\5.15.2\msvc2019_64\bin
```

### 3. 项目结构解析

```
TESS_CppAPI_DEMO/
├── CMakeLists.txt              # 主 CMake 配置文件
├── include/                    # TESSNG SDK 头文件
├── lib/                        # TESSNG SDK 库文件
│   ├── debug/TessInterfaces.lib    # Debug 版本库
│   └── release/TessInterfaces.lib  # Release 版本库
├── x64/                        # 编译输出目录
│   ├── Debug/                  # Debug 版本可执行文件
│   └── Release/                # Release 版本可执行文件
├── 1.基础功能/                  # 基础功能模块
│   ├── 基础样例/               # 基础 API 使用示例
│   ├── 停车场仿真/             # 停车场仿真案例
│   ├── 收费站仿真/             # 收费站仿真案例
│   ├── 节点评价/               # 交通节点评价
│   └── 行人仿真/               # 行人交通仿真
├── 2.信号控制/                  # 信号控制模块
│   ├── 单点信号控制/           # 单点信号控制
│   ├── 多时段多方案信号控制/   # 多时段信号控制
│   ├── 干线绿波信号控制/       # 绿波带控制
│   ├── 公交优先信号控制/       # 公交优先控制
│   ├── 匝道自适应信号控制/     # 匝道信号控制
│   └── 行人交叉口信号控制/     # 行人信号控制
├── 3.交通事故与施工改扩建/       # 事故与施工模块
│   ├── 交通事故（创建抛锚车辆）/
│   ├── 交通事故（创建事故区域）/
│   └── 施工改扩建/
├── 4.车道管控与车辆引导/        # 车道管控模块
│   ├── 车辆路径诱导/           # 路径诱导
│   ├── 车辆速度引导/           # 速度引导
│   ├── 可变车道管控/           # 车道变换控制
│   ├── 可变限速管控（基于强化学习）/
│   ├── 应急车道开放管控/       # 应急车道管理
│   └── 优先车道管控/           # 优先车道管理
├── 5.智能网联与自动驾驶/        # 智能网联模块
│   ├── 智能网联车辆编队行驶/     # 车辆编队
│   ├── 智能网联车辆汇入决策/   # 汇入决策
│   ├── 智能网联车辆事故避让/   # 事故避让
│   └── 自动驾驶仿真/           # 自动驾驶仿真
└── 6.模型参数标定/              # 参数标定模块
    ├── 交叉口模型参数标定/       # 交叉口参数优化
    └── 快速路模型参数标定/       # 快速路参数优化
```

### 4. 编译项目

#### 方法一：使用 CMake GUI（推荐新手）
1. 打开 **CMake GUI**
2. 设置源代码路径：`项目根目录`
3. 设置构建路径：`项目根目录/build`
4. 点击 **Configure**，选择 **Visual Studio 16 2019**
5. 点击 **Generate** 生成解决方案
6. 点击 **Open Project** 打开 Visual Studio
7. 在 Visual Studio 中选择 **Release** 或 **Debug** 模式
8. 点击 **生成 → 生成解决方案**（或按 Ctrl+Shift+B）

#### 方法二：使用命令行
```bash
# 创建构建目录
mkdir build
cd build

# 生成解决方案（VS2019）
cmake .. -G "Visual Studio 16 2019" -A x64

# 或 VS2022
cmake .. -G "Visual Studio 17 2022" -A x64

# 编译所有项目（Release 版本）
cmake --build . --config Release

# 或 Debug 版本
cmake --build . --config Debug
```

#### 方法三：使用 VSCode
1. 打开 VSCode，点击左侧 **CMake 工具栏**
2. 点击 **Configure**，选择编译工具包
3. 选择 **Release** 或 **Debug** 构建类型
4. 点击 **Build** 按钮进行编译

### 5. 运行 Demo

#### 理解输出目录结构
编译完成后，可执行文件位于：
```
x64/
├── Debug/                    # Debug 版本
│   ├── TESS_CppAPI_EXAMPLE.exe
│   ├── ParkingSimulation.exe
│   ├── TollSimulation.exe
│   └── ...（其他 demo 可执行文件）
└── Release/                  # Release 版本
    ├── TESS_CppAPI_EXAMPLE.exe
    ├── ParkingSimulation.exe
    ├── TollSimulation.exe
    └── ...（其他 demo 可执行文件）
```

#### 运行方式
1. **直接双击运行**：在文件资源管理器中双击 `.exe` 文件
2. **命令行运行**：打开命令提示符，进入相应目录：
   ```bash
   cd x64\Release
   TESS_CppAPI_EXAMPLE.exe
   ```
3. **VSCode 调试**：在 VSCode 中设置调试配置，按 F5 运行

#### 运行注意事项
- 首次运行可能需要管理员权限
- 确保 TESSNG 软件已正确安装并激活
- 某些 demo 需要特定的路网文件（`.tess` 文件）

### 6. 开发自己的 Demo

#### 创建新项目
1. 在相应模块目录下创建新文件夹
2. 复制任意现有 demo 的 `CMakeLists.txt` 和源文件作为模板
3. 修改 `CMakeLists.txt` 中的项目名称：
   ```cmake
   project(YourDemoName VERSION 1.0.0 LANGUAGES CXX)
   ```
4. 在主 `CMakeLists.txt` 中添加子目录：
   ```cmake
   add_subdirectory(7.新模块/YourDemoName YourDemoName)
   ```

#### 基本代码结构
每个 demo 通常包含以下文件：
- `main.cpp` - 程序入口点
- `MyPlugin.h/cpp` - 插件实现
- `MyNet.h/cpp` - 路网相关功能
- `MySimulator.h/cpp` - 仿真相关功能
- `CMakeLists.txt` - 构建配置

## 常见问题解决

### 1. CMake 配置失败
**问题**：找不到 Qt 或编译器
**解决**：
- 确保 Qt 和 Visual Studio 正确安装
- 设置 Qt 环境变量：`CMAKE_PREFIX_PATH=C:\Qt\5.15.2\msvc2019_64`
- 重新启动 CMake 或 VSCode

### 2. 编译错误：找不到 TESSNG 库
**问题**：链接错误 LNK2019
**解决**：
- 检查 `lib/debug` 和 `lib/release` 目录是否存在 `TessInterfaces.lib`
- 确保头文件在 `include` 目录中
- 检查 CMakeLists.txt 中的库路径是否正确

### 3. 运行时错误：缺少 DLL
**问题**：提示缺少 Qt5Core.dll 等
**解决**：
- 将 Qt 的 bin 目录添加到 PATH
- 或将所需 DLL 复制到可执行文件目录
- 使用 Qt 的 windeployqt 工具部署依赖

### 4. 插件加载失败
**问题**：提示没有权限加载插件
**解决**：
- 确保 TESSNG 软件已激活
- 检查试用版是否过期
- 以管理员身份运行程序

### 学习建议
1. **从简单 demo 开始**：先研究 `基础样例`，理解基本框架
2. **逐步深入**：按模块顺序学习，从基础功能到高级应用
3. **修改现有代码**：在现有 demo 基础上进行修改和扩展
4. **阅读文档**：参考 TESSNG SDK 官方文档了解 API 详情
5. **加入社区**：参与相关技术论坛和讨论组

## 技术支持

如遇到问题，请检查：
1. 所有软件版本是否符合要求
2. 环境变量是否正确设置
3. 项目文件是否完整
4. TESSNG 软件是否正常运行

建议先在 `基础样例` 上测试环境配置是否正确，再尝试其他复杂 demo。

---

**祝使用愉快！** 🚦🚗