[# 接口详解

## 1. 配置文件 config.json 及插件方法调用频次

### 1.1. 配置文件config.json属性详解
在构建 TESS NG 工厂类实例之前，应首先准备好 config.json 文件，该文件中包含了启动仿真所需的各类配置参数, 说明如下: 
```java
{
  "__workspace": "xxxxxx",
  "__netfilepath":"xxxxxx"
  "__httpserverport": 7777,
  "__language": "zh_CN",
  "__showonlinemap": true,
  "__showtoolbar_emission": true,
  "__showtoolbar_file": true,
  "__showtoolbar_netconfig": true,
  "__showtoolbar_netedit": true,
  "__showtoolbar_oper": true,
  "__showtoolbar_operinfo": true,
  "__showtoolbar_pedestrian": true,
  "__showtoolbar_simu": true,
  "__todb6": true,
  "__enablenewaccecalculation": true
}

```
"__workspace": 指定“当前工作路径”, TESS NG会在“当前工作路径”的Cert子文件夹下读取认证文件, 在”SimuResult”子文件夹下保存仿真结果等。

"__netfilepath": 指定TESSNG启动后加载的路网文件全路径名; 

"__simuafterload": 指定TESSNG加载路网文件（指定的路网文件或临时空白路网文件）后是否启动仿真; 

"__httpserverport": 设置 TESS NG 内置 HTTP 服务监听的端口号，用于外部接口通信。

"__language": 设置 TESS NG 界面语言，如 "zh_CN" 表示中文界面。

"__showonlinemap": 控制是否在仿真界面中显示在线地图图层（如百度地图）。

"__showtoolbar_emission": 控制是否显示“排放分析”相关的工具栏按钮。

"__showtoolbar_file": 控制是否显示“文件操作”相关的工具栏按钮（如新建、打开、保存）。

"__showtoolbar_netconfig": 控制是否显示“路网配置”相关的工具栏按钮（如车道宽度、限速设置）。

"__showtoolbar_oper": 控制是否显示“操作工具”相关的工具栏按钮（如移动、缩放）。

"__showtoolbar_operinfo": 控制是否显示“操作信息”相关的工具栏按钮（如对象属性查看）。

"__showtoolbar_pedestrian": 控制是否显示“行人仿真”相关的工具栏按钮。

"__showtoolbar_simu": 控制是否显示“仿真控制”相关的工具栏按钮（如开始、暂停、停止仿真）。

"__todb6": 控制是否将仿真结果写入 DB6 数据库文件，用于后续分析。

"__enablenewaccecalculation": 控制是否启用新的加速度计算模型，提升车辆动力学模拟精度。

### 1.2. 插件方法调用频次控制

TESSNG调用插件方法的频次是指对插件实现的CustomerSimulator接口方法调用频次。

当"__custsimubysteps"设置为True时, **默认调用频次比较低, 很多低到毫无意义, 只为减少调用次数, 不至于影响仿真运行效率**。如果某方法被实现, 需要对该方法调用频次进行调整。可参见范例。

假设仿真精度是steps, 即每秒计算steps次, 各方法默认调用频次如下: 

1）、车辆相关方法调用频次

计算下一位置前处理方法beforeNextPoint被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次; 

具体车辆一个步长计算完成后的处理方法afterStep被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次; 

确定是否停止车辆运行并移出路网方法isStopDriving调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次; 

2）、驾驶行为相关方法调用频次

重新设置期望速度方法reCalcdesirSpeed被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次, 如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

计算最大限速方法calcMaxLimitedSpeed被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

计算限制车道方法calcLimitedLaneNumber被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

计算车道限速方法calcSpeedLimitByLane被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

计算安全变道方法calcChangeLaneSafeDist被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以左强制变道方法reCalcToLeftLane被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以右强制变道方法reCalcToRightLane被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以左自由变道方法reCalcToLeftFreely被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以右自由变道方法reCalcToRightFreely被调用频次: 每steps个仿真周期调用一次, 即每秒调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

计算跟驰类型后处理方法afterCalcTracingType被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为20个计算周期调用1次或更小。

连接段上汇入到车道前处理方法beforeMergingToLane被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算跟驰状态参数方法reSetFollowingType被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

计算加速度方法calcAcce被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算加速度方法reSetAcce被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

重置车速方法reSetSpeed被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算角度方法reCalcAngle被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

计算后续道路前处理方法beforeNextRoad被调用频次: 每steps * 300个仿真周期调用一次, 即5分钟调用一次。如果该方法被实现, 建议将该方法调用频次设为1个计算周期调用1次或更大。

### 1.3. 仿真过程中gui界面车辆重绘控制

可以在CustomerSimulator的initVehicle(IVehicle pIVehicle)方法里通过pIVehicle设置TESSNG对CustomerSimulator不同方法调用频次及是否允许插件重绘车辆。

是否允许对车辆重绘方法的调用: 默认为False, 如果允许, 可以传入True, 如: pIVehicle.setIsPermitForVehicleDraw(True)。可以通过pIVehicle得到该车辆类型及ID等信息来确定是否允许对该车辆重绘。


## 2. 路网基本元素

### 2.1. IRoadNet

路网基本信息接口, 设计此接口的目的是为了TESS NG在导入外源路网时能够保存这些路网的属性, 如路网中心点坐标、空间参考等。

接口方法: 

** int id();**

获取路网ID, 即路网编辑弹窗中的编号

举例: 

```java
# 获取路网ID
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网ID=" + netAttrs.id());
```

 ** String netName();**

获取路网名称

举例: 

```java
// 获取路网名称
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网名称=" + netAttrs.netName());
```

 ** String url();**

获取源数据路径, 可以是本地文件, 可以是网络地址

举例: 

```java
// 获取源数据路径
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("源数据路径=" + netAttrs.url());
```

 ** String type();**

获取来源分类: "TESSNG"表示TESSNG自建; "OpenDrive"表示由OpenDrive数据导入; "GeoJson"表示由geojson数据导入

举例: 

```java
// 获取来源分类
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("来源分类=" + netAttrs.type());
```

 ** String bkgUrl();**

获取背景路径

举例: 

```java
// 获取背景路径
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("背景路径=" + netAttrs.bkgUrl());
```

 **Map<String, Object> otherAttrs();**

获取其它属性数据, json 数据

举例: 

```java
// 获取其它属性数据
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("其它属性数据=" + netAttrs.otherAttrs());
```

 **String explain();**

获取路网说明

举例: 

```java
// 获取路网说明
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网说明=" + netAttrs.explain());
```

 **Point centerPoint(UnitOfMeasure unit);**

获取路网中心点位置, 默认单位: 像素, 可通过可选参数: unit设置单位, （用户也可以根据需求通过m2p转成米制单位坐标, 并注意y轴的正负号）

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位, 返回接口默认的单位

注: 米制与像素制仅在比例尺(像素比)不为1时生效。

举例: 

```java
// 获取路网中心点位置
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网中心点位置=" + netAttrs.centerPoint());
System.out.println("路网中心点位置(米制)=" + netAttrs.centerPoint(UnitOfMeasure.Metric));
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
// 代表TESS NG的路网子接口
NetInterface netiface = iface.netInterface();
showRoadNetAttr(netiface);

public void showRoadNetAttr(NetInterface netiface) {
    String netpath = netiface.netFilePath();
    System.out.println("===保存路网, 文件路径=" + netpath);
    IRoadNet roadNet = netiface.roadNet();
    System.out.println("路网属性netAttrs=" + netiface.netAttrs());
    System.out.println("===获取当前路网基本信息: id=" + roadNet.id() + ", netName=" + roadNet.netName() + ", url=" + roadNet.url() + ", "
            + "路网来源type=" + roadNet.type() + ", 背景图片路径=" + roadNet.bkgUrl() + ", 其他属性=" + roadNet.otherAttrs() + ", "
            + "路网说明=" + roadNet.explain() + ", 路网中心点位置(像素)=" + roadNet.centerPoint() + ", 路网中心点位置(米制)=" + roadNet.centerPoint(UnitOfMeasure.Metric) + " ");

    System.out.println("===获取场景信息");
    Object graphicScene = netiface.graphicsScene();
    Object graphicsView = netiface.graphicsView();
    double sceneScale = netiface.sceneScale();
    System.out.println("像素比=" + sceneScale + ", 场景宽度=" + netiface.sceneWidth() + ", "
            // + "高度=" + netiface.sceneHeight() + ", "
            + "背景图=" + netiface.backgroundMap() + ", ");
}
```





### 2.2. ISection

路段与连接段的父类接口, 方法如下: 

 **int gtype();**

获取Section类型, GLinkType 或 GConnectorType。在Tessng.pyi / NetItemType类中可见, 定义了一批枚举, 每一个数值代表路网上一种元素类型。如: GLinkType代表路段、GConnectorType代表连接段。

举例: 

```java
# 获取Section类型
// 获取Section类型
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的类型是" + section.gtype());
}
```

 **boolean isLink();**

是否是路段; TESSNG中基础路网由路段Link和连接段connector构成

举例: 

```java
// 获取Section类型
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    if (section.isLink()) {
        System.out.println("id为" + section.id() + "的Section是路段");
    } else {
        System.out.println("id为" + section.id() + "的Section是连接段");
    }
}
```

 **int id();**

获取ID: 如果当前对象是Link, 则id是Link的ID; 如果是连接段, 则id是连接段ID

举例: 

```java
// 获取Section的ID
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("Section的ID是" + section.id());
}
```

 **int sectionId();**

获取ID, 如果当前Isection对象是Link, 则id是Link的ID; 
如果是连接段, 则id是连接段ID+10000000（TESSNG内部通过加常数的方式来区分路段与连接段）

举例: 

```java
// 获取Section的ID
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("Section的ID是" + section.id());
}
```

 **String name();**

获取Section名称: 路段名或连接段名

举例: 

```java
// 获取Section名称
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的名称是" + section.name());
}
```

 **void setName(String name);**

设置Section名称

举例: 

```java
# 设置Section名称
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    section.setName(section.name() + section.id());
    System.out.println("id为" +section.id()+"的Section的名称是"+ section.name());
}
```

 **double v3z(UnitOfMeasure unit);**

获取Section高程, 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取Section高程
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的高程是" + section.v3z());
    System.out.println("id为" + section.id() + "的Section的高程(米制)是" + section.v3z(UnitOfMeasure.Metric));
}
```

 **double length(UnitOfMeasure unit);**

获取Section长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取Section长度
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的长度是" + section.length());
    System.out.println("id为" + section.id() + "的Section的长度(米制)是" + section.length(UnitOfMeasure.Metric));
}
```

 **List<ILaneObject> laneObjects();**

车道与“车道连接”的父类接口列表

举例: 

```java
// 获取Section的车道与“车道连接”的父类接口列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    List< ILaneObject> laneObjects = section.laneObjects();
    for (ILaneObject laneObject : laneObjects) {
        System.out.println("id为" + section.id() + "的Section包含id为" + laneObject.id() + "的laneObject");
    }
}
```

 **ISection fromSection(int id);**

根据ID获取上游Section。如果当前Section是路段且id 为 0 则返回空; 否则返回上游指定ID的连接段; 如果当前Section是连接段且id 为 0 返回上游路段, 否则返回空。

举例: 

```java
// 获取Section的上游Section
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    if (section.gtype() == NetItemType.getGConnectorType()) {
        System.out.println("id为" + section.id() + "的Section的上游Section是" + section.fromSection(0));
    }
}
```

 **ISection toSection(int id);**

根据ID获取下游 Section。如果当前section是路段且 id 为 0 则返回空, 否则返回下游指定ID的连接段; 
如果当前section是连接段且id 为 0 则返回下游路段, 否则返回空。

举例: 

```java
// 获取Section的下游Section
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    if (section.gtype() == NetItemType.getGConnectorType()) {
        System.out.println("id为" + section.id() + "的Section的下游Section是" + section.toSection(0));
    }
}
```

 **void setOtherAttr(JsonObject otherAttr);**

设置路段或连接段其它属性; 这些属性可以用户自定义, 类型为字典, 方便用户做二次开发时扩充属性

举例: 

```java
// 设置Section的其它属性
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("newAttr", "add a new attr");  // 添加键值对
    JsonObject newAttr = builder.build();      // 构建不可变的 JsonObject
    section.setOtherAttr(newAttr);
}
```

 ** ILink castToLink();**

将当前Section转换成其子类ILink, 如果当前Section是连接段则返回空

举例: 

```java
// 将当前Section转换成其子类ILink
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section转换成ILink后是" + section.castToLink());
}
```

 **IConnector castToConnector();**

将当前Section转换成其子类转换成IConnector, 如果当前Section为路段Link则返回空

举例: 

```java
// 将当前Section转换成其子类IConnector
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section转换成IConnector后是" + section.castToConnector());
}
```

 **Vector<Point> polygon();**

获取Section的轮廓, 轮廓由section的一系列顶点组成

举例: 

```java
// 获取Section的轮廓
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的轮廓是" + section.polygon());
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
// 代表TESS NG的路网子接口
NetInterface netiface = iface.netInterface();
showSectionAttr(netiface);

public void showSectionAttr(NetInterface netiface) {
    System.out.println("===场景中的section个数（路段与连接段的父类对象）=" + netiface.sections().size() + ", 第一个section的属性=" + netiface.sections().get(0).id());
    ISection section = netiface.sections().get(0);
    for (ISection s : netiface.sections()) {
        if (s.id() == 135 && s.isLink()) {
            System.out.println("link=" + s.id() + ", v3z=" + s.v3z() + ", m2p_v3z=" + m2p(s.v3z()) + ", p2m_v3z=" + p2m(s.v3z()) + "v3z_m=" + s.v3z(UnitOfMeasure.Metric));
            System.out.println("link=" + s.id() + ", v3z=" + s.v3z() + ", m2p_v3z=" + m2p(s.v3z()) + ", p2m_v3z=" + p2m(s.v3z()) + "v3z_m=" + s.v3z(UnitOfMeasure.Metric));

            System.out.println("该section的属性: id(linkid or connectorid)=" + section.id() + ", 类型gtype=" + section.gtype() + ", "
                    + "是否为link=" + section.isLink() + ", sectionId=" + section.sectionId() + ", name=" + section.name() + ", 设置新的name=" + section.setName(section.name() + "1") + ", "
                    + "v3z(像素制)=" + section.v3z() + ", v3z(米制)=" + section.v3z(UnitOfMeasure.Metric) + ", 长度length（像素制）=" + section.length() + ", 米制=" + section.length(UnitOfMeasure.Metric)
                    + "section下包含的laneObject（lane和lanconnector的父类）=" + section.laneObjects() + ", fromSection=" + section.fromSection() + ", toSection=" + section.toSection() + ", "
                    + "设置自定义属性setOtherAttr=" + section.setOtherAttr(new HashMap<String, Object>() {{ put("newAttr", "add a new attr"); }}) + ", 将section强转为子类link=" + section.castToLink() + ", "
                    + "将section强转为子类Iconnector=" + section.castToLink() + ", 获取section外轮廓=" + section.polygon());
        }
    }
    System.out.println();
    System.out.println("该section的属性: id(linkid or connectorid)=" + section.id() + ", 类型gtype=" + section.gtype() + ", "
            + "是否为link=" + section.isLink() + ", sectionId=" + section.sectionId() + ", name=" + section.name() + ", 设置新的name=" + section.setName(section.name() + "1") + ", "
            + "v3z(像素制)=" + section.v3z() + ", v3z(米制)=" + section.v3z(UnitOfMeasure.Metric) + ", 长度length（像素制）=" + section.length() + ", 米制=" + section.length(UnitOfMeasure.Metric)
            + "section下包含的laneObject（lane和lanconnector的父类）=" + section.laneObjects() + ", fromSection=" + section.fromSection() + ", toSection=" + section.toSection() + ", "
            + "设置自定义属性setOtherAttr=" + section.setOtherAttr(new HashMap<String, Object>() {{ put("newAttr", "add a new attr"); }}) + ", 将section强转为子类link=" + section.castToLink() + ", "
            + "将section强转为子类Iconnector=" + section.castToLink() + ", 获取section外轮廓=" + section.polygon());
}

// 假设存在m2p和p2m方法
private double m2p(double value) {
    // 实现米转像素的逻辑
    return 0;
}

private double p2m(double value) {
    // 实现像素转米的逻辑
    return 0;
}
```





### 2.3 ILaneObject 

车道与车道连接的父类接口, 方法如下: 

 **int gtype();**

类型, GLaneType或GLaneConnectorType

举例: 

```java
// 获取ILaneObject的类型
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    // 获取路网中的所有ILaneObject
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        System.out.println("id为" + laneObject.id() + "的ILaneObject的类型是" + laneObject.gtype());
    }
}
```

 **boolean isLane();**

当前laneObject是否为车道, 因为也有可能是车道连接

举例: 

```java
// 判断ILaneObject是否是车道
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List<ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List<ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        System.out.println("id为" + laneObject.id() + "的ILaneObject是否是车道: " + laneObject.isLane());
    }
}
```

 **int id();**

获取ID, 如果当前对象是Lane则id是Lane的ID, 如果是车道连接, 则id是“车道连接”ID

举例: 

```java
// 获取ILaneObject的ID
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List<ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        System.out.println("ILaneObject的ID是" + laneObject.id());
    }
}
```

 **double length( UnitOfMeasure unit);**

获取车道或“车道连接”长度, 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的长度
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        System.out.println("id为" + laneObject.id() + "的ILaneObject的长度是" + laneObject.length());
        System.out.println("id为" + laneObject.id() + "的ILaneObject的长度(米制单位)是" + laneObject.length(UnitOfMeasure.Metric));
    }
}
```

 **ISection section();**

获取所属的ISection

举例: 

```java
// 获取ILaneObject所属的ISection
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        System.out.println("id为" + laneObject.id() + "的ILaneObject所属的ISection是" + laneObject.section());
    }
}
```

 **ILaneObject fromLaneObject(int id);**

根据laneObject ID获取其上游的 LaneObject对象。如果当前laneObject对象是车道, 则且id 为 0 表示未传入laneObject ID信息, 则 返回空; 否则返回其上游的“车道连接”; 
如果当前laneObject对象是车道连接且id 为 0, 那么 返回其上游车道对象, 否则返回空。

举例: 

```java
// 获取ILaneObject的上游LaneObject
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        if (laneObject.gtype() == NetItemType.GLaneConnectorType) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的上游LaneObject是" + laneObject.fromLaneObject(0));
        }
    }
}
```

 **ILaneObject toLaneObject(int id);**

根据ID获取下游 LaneObject。如果当前是车道, id 为 0 返回空, 否则返回下游指定ID的“车道连接”; 如果当前是连接段, id 为 0 返回下游车道, 否则返回空。

举例: 

```java
// 获取ILaneObject的下游LaneObject
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        if (laneObject.gtype() == NetItemType.GLaneConnectorType) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的下游LaneObject是" + laneObject.toLaneObject(0));
        }
    }
}
```

 **ArrayList<Point> centerBreakPoints(UnitOfMeasure unit);**

获取laneObject的中心线断点列表, 即车道或“车道连接”中心线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的中心线断点列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        List<Point> lCenterBreakPoints = laneObject.centerBreakPoints();
        for (Object centerBreakPoint : lCenterBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表是" + centerBreakPoint);
        }
        List<Point> lCenterBreakPoints1 = laneObject.centerBreakPoints(UnitOfMeasure.Metric);
        for (Object centerBreakPoint : lCenterBreakPoints1) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表(米制)是" + centerBreakPoint);
        }
        
    }
}
```

 **ArrayList<Point>  leftBreakPoints(UnitOfMeasure unit);**

获取laneObject的左侧边线断点列表; 即车道或“车道连接”左侧线断点集; 断点均为像素坐标下的点 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的左侧边线断点列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        List<Point> lLeftBreakPoints = laneObject.leftBreakPoints();
        for (Object leftBreakPoint : lLeftBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表是" + leftBreakPoint);
        }
        List<Point> lLeftBreakPoints1 = laneObject.leftBreakPoints(UnitOfMeasure.Metric);
        for (Object leftBreakPoint : lLeftBreakPoints1) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(米制)是" + leftBreakPoint);
        }
        
    }
}
```

 **ArrayList<Point> rightBreakPoints(UnitOfMeasure unit);**

获取laneObject的右侧边线断点列表; 车道或“车道连接”右侧线断点集; 断点均为像素坐标下的点 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的右侧边线断点列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        List<?> lRightBreakPoints = laneObject.rightBreakPoints();
        for (Object rightBreakPoint : lRightBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表是" + rightBreakPoint);
        }
        lRightBreakPoints = laneObject.rightBreakPoints(UnitOfMeasure.Metric);
        for (Object rightBreakPoint : lRightBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(米制)是" + rightBreakPoint);
        }
    }
}
```

 **ArrayList<Point3D> centerBreakPoint3Ds(UnitOfMeasure unit);**

获取laneObject的右侧边线断点列表; 车道或“车道连接”中心线断点(三维)集（包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的中心线断点列表
TessInterface iface = TESSNG.tessngIFace();
if(iface!=null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<Point3D> lCenterBreakPoint3Ds = laneObject.centerBreakPoint3Ds();
                for (Object centerBreakPoint3D : lCenterBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表(三维)是" + centerBreakPoint3D);
                }
                lCenterBreakPoint3Ds = laneObject.centerBreakPoint3Ds(UnitOfMeasure.Metric);
                for (Object centerBreakPoint3D : lCenterBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表(三维，米制)是" + centerBreakPoint3D);
                }
                
            }
        }
    }
}
```

 **ArrayList<Point3D> leftBreakPoint3Ds( UnitOfMeasure unit);**

获取laneObject的左侧边线断点列表; 车道或“车道连接”左侧线断点(三维)集; （包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的左侧边线断点列表 
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<Point3D> lLeftBreakPoint3Ds = laneObject.leftBreakPoint3Ds();
                for (Object leftBreakPoint3D : lLeftBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(三维)是" + leftBreakPoint3D);
                }
                lLeftBreakPoint3Ds = laneObject.leftBreakPoint3Ds(UnitOfMeasure.Metric);
                for (Object leftBreakPoint3D : lLeftBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(三维,米制)是" + leftBreakPoint3D);
                }
                
            }
        }
    }
}
```

 **ArrayList<Point3D> rightBreakPoint3Ds( UnitOfMeasure unit); **  

获取laneObject的右侧边线断点列表; 车道或“车道连接”右侧线断点(三维)集; （包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject的右侧边线断点列表 
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<?> lRightBreakPoint3Ds = laneObject.rightBreakPoint3Ds();
                for (Object rightBreakPoint3D : lRightBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(三维)是" + rightBreakPoint3D);
                }
                lRightBreakPoint3Ds = laneObject.rightBreakPoint3Ds(UnitOfMeasure.Metric);
                for (Object rightBreakPoint3D : lRightBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(三维, 米制)是" + rightBreakPoint3D);
                }
            }
        }
    }
}
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint, UnitOfMeasure unit);**

通过起终止断点, 获取该范围内laneObject的左侧边线断点集; 即车道或“车道连接”左侧部分断点(三维)集; 入参出参均为像素单位 

参数: 
[ in ] fromPoint: 中心线上某一点作为起点  
[ in ] toPoint: 中心线上某一点作为终点  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位    

举例: 

```java
// 获取ILaneObject的左侧边线部分断点列表(三维)
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<?> lLeftBreakPoints = laneObject.leftBreakPoints();
                if (lLeftBreakPoints.size() > 2) {
                    List<?> lLeftBreak3Ds = laneObject.leftBreak3DsPartly(new Point(TESSNG.m2p(-300), -100), new Point(TESSNG.m2p(300), -100));
                    for (Object leftBreak3D : lLeftBreak3Ds) {
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(三维)是" + leftBreak3D);
                    }
                    lLeftBreak3Ds = laneObject.leftBreak3DsPartly(
                            laneObject.leftBreakPoints(UnitOfMeasure.Metric).get(0),
                            laneObject.leftBreakPoints(UnitOfMeasure.Metric).get(2),
                            UnitOfMeasure.Metric
                    );
                    for (Object leftBreak3D : lLeftBreak3Ds) {
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(三维, 米制)是" + leftBreak3D);
                    }
                }
            }
        }
    }
}
```

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint, UnitOfMeasure unit);**

通过起终止断点, 获取该范围内laneObject的右侧边线断点集; 即车道或“车道连接”右侧部分断点(三维)集; 入参出参均为像素单位  

参数: 
[ in ] fromPoint: 中心线上某一点作为起点; Point类型, 且是像素坐标  
[ in ] toPoint: 中心线上某一点作为终点; Point类型, 且是像素坐标  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
// 获取ILaneObject的右侧边线部分断点列表(三维)
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<?> lRightBreakPoints = laneObject.rightBreakPoints();
                if (lRightBreakPoints.size() > 2) {
                    List<?> lRightBreak3Ds = laneObject.rightBreak3DsPartly(new Point(TESSNG.m2p(-300), -100), new Point(TESSNG.m2p(300), -100));
                    for (Object rightBreak3D : lRightBreak3Ds) {
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(三维)是" + rightBreak3D);
                    }
                    lRightBreak3Ds = laneObject.rightBreak3DsPartly(
                            laneObject.rightBreakPoints(UnitOfMeasure.Metric).get(0),
                            laneObject.rightBreakPoints(UnitOfMeasure.Metric).get(2),
                            UnitOfMeasure.Metric
                    );
                    for (Object rightBreak3D : lRightBreak3Ds) {
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(三维, 米制)是" + rightBreak3D);
                    }
                }
            }
        }
    }
}
```

 **double distToStartPoint(Point p, UnitOfMeasure unit);**

中心线上一点到laneObject对象起点的距离; 默认单位: 像素  

参数: 
[ in ] p: 当前点坐标  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILaneObject距中心线起点距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                List<?> centerBreakPoints = laneObject.centerBreakPoints();
                if (!centerBreakPoints.isEmpty() && centerBreakPoints.size() > 1) {
                    double dist = laneObject.distToStartPoint(new Point(TESSNG.m2p(-300), -100));
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的距中心线起点距离为" + dist);
                }
                centerBreakPoints = laneObject.centerBreakPoints(UnitOfMeasure.Metric);
                if (!centerBreakPoints.isEmpty() && centerBreakPoints.size() > 1) {
                    double dist = laneObject.distToStartPoint(new Point(TESSNG.m2p(-300), -100));
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的距中心线起点距离(米制)为" + dist);
                }
                
            }
        }
    }
}
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex) ;**

laneObject中心线上一点到起点的距离, 默认单位: 像素, 附加条件是该点所在车道上的分段序号; 其中分段是指两个断点之间的部分。往往可以根据当前车辆所在的segmIndex信息, 调用该函数, 这样比distToStartPoint函数效率要高一些 

参数: 
[ in ] p: 当前中心线上点或附近点的坐标; Point类型, 且是像素坐标  
[ in ] segmIndex: 参数p点所在车道上的分段序号; 两个断点组成一个分段, 分段序号从0开始, 沿着道路方向递增  
[ in ] bOnCentLine: 参数p点是否在中心线上  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

注: 如传入米制参数, 请勿遗忘传入segmIndex与bOnCentLine参数。

举例: 

```java
// 获取ILaneObject带分段索引的距中心线起点距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    // 获取路网中的所有ILaneObject
    List<ISection> lSections = netiface.sections();
    for (ISection section : lSections) {
        List<ILaneObject> lLaneObjects = section.laneObjects();
        for (ILaneObject laneObject : lLaneObjects) {
            List<?> centerBreakPoints = laneObject.centerBreakPoints();
            if (!centerBreakPoints.isEmpty() && centerBreakPoints.size() > 1) {
                double dist = laneObject.distToStartPointWithSegmIndex(new Point(TESSNG.m2p(-300), -100), 1);
                System.out.println("id为" + laneObject.id() + "的ILaneObject的距中心线起点距离为" + dist);
                double dist1 = laneObject.distToStartPointWithSegmIndex(new Point(TESSNG.m2p(-300), -100), 1,true,UnitOfMeasure.Metric);
                System.out.println("id为" + laneObject.id() + "的ILaneObject的距中心线起点距离(米制)为" + dist);
            

            }
        }
    }
}
}
```

 **boolean getPointAndIndexByDist(double dist, Point outPoint, SWIGTYPE_p_int outIndex);**

获取沿着行驶方向, 距laneObject起点dist距离处的点和该点所属的分段序号; 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 中心线起点向下游延伸的距离 
[ out ] outPoint: 中心线起点向下游延伸dist距离后所在点, 默认单位: 像素单位, 具体返回单位受unit参数控制  
[ out ] outIndex: 中心线起点向下游延伸dist距离后所在分段序号, 默认单位: 像素单位, 具体返回单位受unit参数控制  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 像素制
                ILink link = netiface.findLink(5);
                if (link != null) {
                    List<ILaneObject> laneObjects = link.laneObjects();
                    if (!laneObjects.isEmpty()) {
                        ILaneObject laneObjLeft = laneObjects.get(laneObjects.size() - 1); // 最左侧车道
                        Point outPoint = new Point();
                        ObjInt outIndex = new ObjInt(0);
                        double dist = m2p(140);
                        if (laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex)) {
                            System.out.println("路段5最左侧车道向前延伸140米后所在点坐标为: (" + outPoint.getX() + ", " + outPoint.getY() + "), 分段序号为: " + outIndex);
                        }
                    }
                }
                //# 路段5最左侧车道向前延伸140米后所在点及分段序号, 返回米制
                link = netiface.findLink(5);
                if (link != null) {
                    List<ILaneObject> laneObjects = link.laneObjects();
                    if (!laneObjects.isEmpty()) {
                        ILaneObject laneObjLeft = laneObjects.get(laneObjects.size() - 1); // 最左侧车道
                        Point outPoint = new Point();
                        ObjInt outIndex = new ObjInt(0);
                        double dist = m2p(140);
                        if (laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex, UnitOfMeasure.Metric)) {
                            System.out.println("路段5最左侧车道向前延伸140米后所在点坐标为: (" + outPoint.getX() + ", " + outPoint.getY() + "), 分段序号为: " + outIndex);
                        }
                    }
                }
            }
        }

```

 

 **boolean getPointByDist(double dist, Point outPoint, UnitOfMeasure unit);**

获取距离中心线起点向下游延伸dist处的点, 如果目标点不在中心线上返回False, 否则返回True; 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ] dist: 中心线起点向下游延伸的距离, 默认单位: 像素  
[ out ] outPoint: 中心线起点向下游延伸dist距离后所在点, 默认单位: 像素单位, 具体返回单位受unit参数控制 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
// 获取ILaneObject的中心线断点列表
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILaneObject
                List<ISection> lSections = netiface.sections();
                for (ISection section : lSections) {
                    List<ILaneObject> lLaneObjects = section.laneObjects();
                    for (ILaneObject laneObject : lLaneObjects) {
                        Point outPoint = new Point(0,0);
                        boolean dist = laneObject.getPointByDist(50, outPoint);
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的距离中心线起点向下游延伸的点为" + outPoint);

                        Point metricOutPoint = new Point();
                        dist = laneObject.getPointByDist(50, metricOutPoint, UnitOfMeasure.Metric);
                        System.out.println("id为" + laneObject.id() + "的ILaneObject的距离中心线起点向下游延伸的点(米制)为" + metricOutPoint);
                    }
                }
            }
        }

```

 **void setOtherAttr(JsonObject attr);**

设置车道或“车道连接”其它属性; 字典类型

举例: 

```java
// 设置ILaneObject的其它属性
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILaneObject
                List<ISection> lSections = netiface.sections();
                for (ISection section : lSections) {
                    List<ILaneObject> lLaneObjects = section.laneObjects();
                    for (ILaneObject laneObject : lLaneObjects) {
                        JsonObjectBuilder builder = Json.createObjectBuilder();
                        builder.add("newAttr", "add a new attr");  // 添加键值对
                        JsonObject newAttr = builder.build();
                        laneObject.setOtherAttr(newAttr);
                    }
                }
            }
        }
```

 **ILane castToLane();**

将ILaneObject转换为子类ILane, 但如果当前ILaneObject是“车道连接”则转化失败, 返回空

举例: 

```java
// 将ILaneObject转换为子类ILane
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILaneObject
        List<ISection> lSections = netiface.sections();
        for (ISection section : lSections) {
            List<ILaneObject> lLaneObjects = section.laneObjects();
            for (ILaneObject laneObject : lLaneObjects) {
                ILane lane = laneObject.castToLane();
                System.out.println("id为" + laneObject.id() + "的ILaneObject转换为子类ILane为" + lane);
            }
        }
    }
}
```

 **ILaneConnector castToLaneConnector();**

将ILaneObject转换为ILaneConnector子类, 但如果当前ILaneObject是车道则转化失败, 返回空

举例: 

```java
private static void showLaneObjectAttr(NetInterface netiface) {
        // 获取第一个路段
        ILink section = netiface.findLink(netiface.sections().get(0).id());
        ILaneObject laneObject = section.laneObjects().get(0);

//         查找车道序号为0的车道对象
        for (ILaneObject obj : section.laneObjects()) {
            if (obj.getNumber() == 0) {
                laneObject = obj;
                break;
            }
        }

        if (laneObject == null && !section.laneObjects().isEmpty()) {
            laneObject = section.laneObjects().get(0);
        }

        if (laneObject != null) {
            // 处理leftBreak3DsPartly方法
            List<Point> leftBreakPointsMetric = laneObject.leftBreakPoints();
            if (leftBreakPointsMetric.size() > 1) {
                System.out.println(laneObject.leftBreak3DsPartly(
                        leftBreakPointsMetric.get(1),
                        leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1)
                        
                ));
            }

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("newAttr", "add a new attr");  // 添加键值对
            JsonObject newAttr = builder.build();	     // 构建不可变的 JsonObject

            // 处理void方法：先执行设置操作，存储执行状态
            laneObject.setOtherAttr(newAttr);
            String setOtherAttrResult = "执行成功";


            Point outPoint2 = new Point();
            laneObject.getPointByDist(2.0, outPoint2);

            Point outPoint3 = new Point();
            laneObject.getPointByDist(2.0, outPoint3);

            // 输出属性信息
            System.out.println(
                    "===section中的第一个laneObject(最右侧) id(linkid or connectorid)=" + laneObject.id() + ", 类型gtype=" + laneObject.gtype() + ", "
                            + "是否为link=" + laneObject.isLane() + ", 所属section=" + laneObject.section() + ", 长度length（像素制）=" + laneObject.length() + ", 米制=" + laneObject.length() + ", "
                            + "fromLaneObject=" + laneObject.fromLaneObject() + ", toLaneObject=" + laneObject.toLaneObject() + ", "
                            + "centerBreakPoints(像素制)=" + laneObject.centerBreakPoints() + ", centerBreakPoints(米制)=" + laneObject.centerBreakPoints() + ", "
                            + "leftBreakPoints(像素制)=" + laneObject.leftBreakPoints() + ", leftBreakPoints(米制)=" + laneObject.leftBreakPoints() + ", "
                            + "rightBreakPoints(像素制)=" + laneObject.rightBreakPoints() + ", rightBreakPoints(米制)=" + laneObject.rightBreakPoints() + ", "
                            + "centerBreakPoint3Ds(像素制)=" + laneObject.centerBreakPoint3Ds() + ", centerBreakPoint3Ds(米制)=" + laneObject.centerBreakPoint3Ds() + ", "
                            + "leftBreakPoint3Ds(像素制)=" + laneObject.leftBreakPoint3Ds() + ", leftBreakPoint3Ds(米制)=" + laneObject.leftBreakPoint3Ds() + ", "
                            + "rightBreakPoint3Ds(像素制)=" + laneObject.rightBreakPoint3Ds() + ", rightBreakPoint3Ds(米制)=" + laneObject.rightBreakPoint3Ds() + ", "
                            + "leftBreak3DsPartly(像素制)=" + laneObject.leftBreak3DsPartly(
                            laneObject.leftBreakPoints().get(1),
                            laneObject.leftBreakPoints().get(laneObject.leftBreakPoints().size() - 1)
                    ) + ", "
                            + "leftBreak3DsPartly(米制)=" + laneObject.leftBreak3DsPartly(
                            laneObject.leftBreakPoints().get(1),
                            laneObject.leftBreakPoints().get(laneObject.leftBreakPoints().size() - 1)
                            
                    ) + ", "
                            + "rightBreak3DsPartly(像素制)=" + laneObject.rightBreak3DsPartly(
                            laneObject.rightBreakPoints().get(1),
                            laneObject.rightBreakPoints().get(laneObject.rightBreakPoints().size() - 1)
                    ) + ", "
                            + "rightBreak3DsPartly(米制)=" + laneObject.rightBreak3DsPartly(
                            laneObject.rightBreakPoints().get(1),
                            laneObject.rightBreakPoints().get(laneObject.rightBreakPoints().size() - 1)
                            
                    ) + ", "
                            + "distToStartPoint(像素制)=" + laneObject.distToStartPoint(laneObject.centerBreakPoints().get(0)) + ", "
                            + "distToStartPoint(米制)=" + laneObject.distToStartPoint(laneObject.centerBreakPoints().get(0)) + ", "
                            + "设置自定义属性setOtherAttr=" + setOtherAttrResult + ", "
                            + "将section强转为子类link=" + laneObject.castToLane() + ", "
                            + "将section强转为子类Iconnector=" + laneObject.castToLaneConnector()
            );

//            // 输出点和索引信息
//            System.out.println(
//                    "getPointAndIndexByDist(像素制)=[" + outPoint + ", " + outIndex[0] + "], " +
//                            "getPointAndIndexByDist(米制)=[" + outPoint1 + ", " + outIndex1[0] + "]"
//            );

            // 输出点信息
            System.out.println(
                    "getPointByDist(像素制)=" + outPoint2 + ", " +
                            "getPointByDist(米制)=" + outPoint3
            );
        }
    }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    // 代表TESS NG的路网子接口
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showLaneObjectAttr(netiface);
    }
}

private static void showLaneObjectAttr(NetInterface netiface) {
        // 获取第一个路段
        ILink section = netiface.findLink(netiface.sections().get(0).id());
        ILaneObject laneObject = section.laneObjects().get(0);

//         查找车道序号为0的车道对象
        for (ILaneObject obj : section.laneObjects()) {
            if (obj.getNumber() == 0) {
                laneObject = obj;
                break;
            }
        }

        if (laneObject == null && !section.laneObjects().isEmpty()) {
            laneObject = section.laneObjects().get(0);
        }

        if (laneObject != null) {
            // 处理leftBreak3DsPartly方法
            List<Point> leftBreakPointsMetric = laneObject.leftBreakPoints();
            if (leftBreakPointsMetric.size() > 1) {
                System.out.println(laneObject.leftBreak3DsPartly(
                        leftBreakPointsMetric.get(1),
                        leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1)
                        
                ));
            }

            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("newAttr", "add a new attr");  // 添加键值对
            JsonObject newAttr = builder.build();	     // 构建不可变的 JsonObject

            // 处理void方法：先执行设置操作，存储执行状态
            laneObject.setOtherAttr(newAttr);
            String setOtherAttrResult = "执行成功";


            Point outPoint2 = new Point();
            laneObject.getPointByDist(2.0, outPoint2);

            Point outPoint3 = new Point();
            laneObject.getPointByDist(2.0, outPoint3);

            // 输出属性信息
            System.out.println(
                    "===section中的第一个laneObject(最右侧) id(linkid or connectorid)=" + laneObject.id() + ", 类型gtype=" + laneObject.gtype() + ", "
                            + "是否为link=" + laneObject.isLane() + ", 所属section=" + laneObject.section() + ", 长度length（像素制）=" + laneObject.length() + ", 米制=" + laneObject.length() + ", "
                            + "fromLaneObject=" + laneObject.fromLaneObject() + ", toLaneObject=" + laneObject.toLaneObject() + ", "
                            + "centerBreakPoints(像素制)=" + laneObject.centerBreakPoints() + ", centerBreakPoints(米制)=" + laneObject.centerBreakPoints() + ", "
                            + "leftBreakPoints(像素制)=" + laneObject.leftBreakPoints() + ", leftBreakPoints(米制)=" + laneObject.leftBreakPoints() + ", "
                            + "rightBreakPoints(像素制)=" + laneObject.rightBreakPoints() + ", rightBreakPoints(米制)=" + laneObject.rightBreakPoints() + ", "
                            + "centerBreakPoint3Ds(像素制)=" + laneObject.centerBreakPoint3Ds() + ", centerBreakPoint3Ds(米制)=" + laneObject.centerBreakPoint3Ds() + ", "
                            + "leftBreakPoint3Ds(像素制)=" + laneObject.leftBreakPoint3Ds() + ", leftBreakPoint3Ds(米制)=" + laneObject.leftBreakPoint3Ds() + ", "
                            + "rightBreakPoint3Ds(像素制)=" + laneObject.rightBreakPoint3Ds() + ", rightBreakPoint3Ds(米制)=" + laneObject.rightBreakPoint3Ds() + ", "
                            + "leftBreak3DsPartly(像素制)=" + laneObject.leftBreak3DsPartly(
                            laneObject.leftBreakPoints().get(1),
                            laneObject.leftBreakPoints().get(laneObject.leftBreakPoints().size() - 1)
                    ) + ", "
                            + "leftBreak3DsPartly(米制)=" + laneObject.leftBreak3DsPartly(
                            laneObject.leftBreakPoints().get(1),
                            laneObject.leftBreakPoints().get(laneObject.leftBreakPoints().size() - 1)
                            
                    ) + ", "
                            + "rightBreak3DsPartly(像素制)=" + laneObject.rightBreak3DsPartly(
                            laneObject.rightBreakPoints().get(1),
                            laneObject.rightBreakPoints().get(laneObject.rightBreakPoints().size() - 1)
                    ) + ", "
                            + "rightBreak3DsPartly(米制)=" + laneObject.rightBreak3DsPartly(
                            laneObject.rightBreakPoints().get(1),
                            laneObject.rightBreakPoints().get(laneObject.rightBreakPoints().size() - 1)
                            
                    ) + ", "
                            + "distToStartPoint(像素制)=" + laneObject.distToStartPoint(laneObject.centerBreakPoints().get(0)) + ", "
                            + "distToStartPoint(米制)=" + laneObject.distToStartPoint(laneObject.centerBreakPoints().get(0)) + ", "
                            + "设置自定义属性setOtherAttr=" + setOtherAttrResult + ", "
                            + "将section强转为子类link=" + laneObject.castToLane() + ", "
                            + "将section强转为子类Iconnector=" + laneObject.castToLaneConnector()
            );

//            // 输出点和索引信息
//            System.out.println(
//                    "getPointAndIndexByDist(像素制)=[" + outPoint + ", " + outIndex[0] + "], " +
//                            "getPointAndIndexByDist(米制)=[" + outPoint1 + ", " + outIndex1[0] + "]"
//            );

            // 输出点信息
            System.out.println(
                    "getPointByDist(像素制)=" + outPoint2 + ", " +
                            "getPointByDist(米制)=" + outPoint3
            );
        }
    }
```





### 2.4. ILink

路段接口, 方法如下: 

 **int gtype();**

类型, 返回GLinkType

举例: 

```java
// 获取ILink的类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的类型为" + link.gtype());
        }
    }
}
```

 **int id();**

获取路段ID

举例: 

```java
// 获取ILink的ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("ILink的ID为" + link.id());
        }
    }
}
```

 **double length(UnitOfMeasure unit);**

获取路段长度, 默认单位: 像素, 可通过可选参数unit设置单位 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILink的长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的长度为" + link.length());
            System.out.println("id为" + link.id() + "的ILink的长度(米制)为" + link.length(UnitOfMeasure.Metric));
        }
    }
}
```

 **double width(UnitOfMeasure unit);**

获取路段宽度, 默认单位: 像素; 可通过可选参数unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取ILink的宽度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的长度为" + link.width());
            System.out.println("id为" + link.id() + "的ILink的长度(米制)为" + link.width(UnitOfMeasure.Metric));
        }
    }
}
```

 **double z(UnitOfMeasure unit);**

获取路段高程, 默认单位: 像素, 可通过可选参数unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    System.out.println("id为" + link.id() + "的ILink的高程为" + link.z());
                    System.out.println("id为" + link.id() + "的ILink的高程(米制)为" + link.z(UnitOfMeasure.Metric));
                }
            }
        }
```

 **double v3z(UnitOfMeasure unit);**

获取路段高程, 过载ISection的方法  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的高程为" + link.v3z());
            System.out.println("id为" + link.id() + "的ILink的高程(米制)为" + link.v3z(UnitOfMeasure.Metric));
        }
    }
}
```

 **String name();** 

获取路段名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的名称" + link.name());
        }
    }
}
```

 **void setName(String name);**

设置路段名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            link.setName("test_name");
            System.out.println("id为" + link.id() + "的ILink的名称" + link.name());
        }
    }
}
```

 **String linkType();**

获取路段类型, 出参为字符串枚举: 城市主干道、城市次干道、人行道。

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的类型为" + link.linkType());
        }
    }
}
```

 **void setType(String type);**

设置路段类型, 路段类型有10种, 入参可以为: 高速路、城市快速路、匝道、城市主要干道、次要干道、地方街道、非机动车道、人行道、公交专用道、机非共享; 其中的任意一个, 其他类型暂不支持

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            link.setType("机非共享");
            System.out.println("id为" + link.id() + "的ILink的类型为" + link.linkType());
        }
    }
}
```

 **int laneCount();**

获取车道数

举例: 

```java
// 获取ILink的车道数
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的车道数为" + link.laneCount());
        }
    }
}
```

 **double limitSpeed(UnitOfMeasure unit);**

获取路段最高限速, 默认单位: 千米/小时  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink的最高限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的最高限速为" + link.limitSpeed());
            System.out.println("id为" + link.id() + "的ILink的最高限速(米制)为" + link.limitSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **void setLimitSpeed(double speed,UnitOfMeasure unit);**

设置最高限速, 默认单位: 千米/小时 

参数: 
[ in ] speed: 最高限速, 单位: 千米/小时  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
# 设置ILink的最高限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            link.setLimitSpeed(link.limitSpeed() * 1.2);
            System.out.println("id为" + link.id() + "的ILink的最高限速为" + link.limitSpeed());
            link.setLimitSpeed(link.limitSpeed(UnitOfMeasure.Metric) * 1.2);
            System.out.println("id为" + link.id() + "的ILink的最高限速(米制)为" + link.limitSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **double minSpeed(UnitOfMeasure unit);**

获取最低限速, 单位: 千米/小时 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
# 获取ILink的最低限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("id为" + link.id() + "的ILink的最低限速为" + link.minSpeed());
            System.out.println("id为" + link.id() + "的ILink的最低限速(米制)为" + link.minSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **ArrayList<ILane> lanes();**

获取ILink上的车道列表, 列表按照从右到左的顺序排列; 列表元素为ILane对象

举例: 

```java
// 获取ILink上的车道列表
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lanes = link.lanes();
            for (ILane lane : lanes) {
                System.out.println("id为" + lane.id() + "的车道对象为" + lane);
            }
        }
    }
}
```

 **ArrayList<ILaneObject> laneObjects();**

获取ILink下所有LaneObject对象, 列表类型, LaneObject可以是车道, 也可以是“车道连接”的父对象

举例: 

```java
# 获取ILink下的所有LaneObject对象
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILaneObject> laneObjects = link.laneObjects();
            for (ILaneObject laneObject : laneObjects) {
                System.out.println("id为" + laneObject.id() + "的LaneObject对象为" + laneObject);
            }
        }
    }
}
```

 **ArrayList<Point> centerBreakPoints(UnitOfMeasure unit);**

获取路段中心线断点集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java

// 获取ILink的中心线断点集)
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<Point> centerBreakPoints = link.centerBreakPoints();
            for (Object centerBreakPoint : centerBreakPoints) {
                System.out.println("路段id为" + link.id() + "的中心线断点为" + centerBreakPoint);
            }
            centerBreakPoints = link.centerBreakPoints(UnitOfMeasure.Metric);
            for (Object centerBreakPoint : centerBreakPoints) {
                System.out.println("路段id为" + link.id() + "的中心线断点（米制）为" + centerBreakPoint);
            }

        }
    }
}
```

 **ArrayList<Point> leftBreakPoints(UnitOfMeasure unit);**

获取路段左侧线断点集, 默认单位: 像素   

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
                //获取ILink的左侧线断点集
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<Point> leftBreakPoints = link.leftBreakPoints();
                    for (Object leftBreakPoint : leftBreakPoints) {
                        System.out.println("路段id为" + link.id() + "的左侧线断点为" + leftBreakPoint);
                    }
                    leftBreakPoints = link.leftBreakPoints(UnitOfMeasure.Metric);
                    for (Object leftBreakPoint : leftBreakPoints) {
                        System.out.println("路段id为" + link.id() + "的左侧线断点（米制）为" + leftBreakPoint);
                    }

                }
            }
        }
```

 **ArrayList<Point> rightBreakPoints(UnitOfMeasure unit);**

获取路段右侧线断点集, 默认单位: 像素  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink的右侧线断点集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    // 获取路网中的所有ILink
    List<ILink> lLinks = netiface.links();
    for (ILink link : lLinks) {
        List<?> rightBreakPoints = link.rightBreakPoints();
        for (Object rightBreakPoint : rightBreakPoints) {
            System.out.println("路段id为" + link.id() + "的右侧线断点为" + rightBreakPoint);
        }
        rightBreakPoints = link.rightBreakPoints(UnitOfMeasure.Metric);
        for (Object rightBreakPoint : rightBreakPoints) {
            System.out.println("路段id为" + link.id() + "的右侧线断点（米制）为" + rightBreakPoint);
        }

    }
}
}
```

 **ArrayList<Point3D> centerBreakPoint3Ds(UnitOfMeasure unit);**

获取路段中心线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink的中心线断点(三维)集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<?> centerBreakPoint3Ds = link.centerBreakPoint3Ds();
                    for (Object centerBreakPoint3D : centerBreakPoint3Ds) {
                        System.out.println("路段id为" + link.id() + "的中心线断点(三维)为" + centerBreakPoint3D);
                    }
                    centerBreakPoint3Ds = link.centerBreakPoint3Ds(UnitOfMeasure.Metric);
                    for (Object centerBreakPoint3D : centerBreakPoint3Ds) {
                        System.out.println("路段id为" + link.id() + "的中心线断点(三维，米制)为" + centerBreakPoint3D);
                    }

                }
            }
        }
```

 **ArrayList<Point3D> leftBreakPoint3Ds(UnitOfMeasure unit);**

获取路段左侧线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink的左侧线断点(三维)集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> leftBreakPoint3Ds = link.leftBreakPoint3Ds();
            for (Object leftBreakPoint3D : leftBreakPoint3Ds) {
                System.out.println("路段id为" + link.id() + "的左侧线断点(三维)为" + leftBreakPoint3D);
            }
            leftBreakPoint3Ds = link.leftBreakPoint3Ds(UnitOfMeasure.Metric);
            for (Object leftBreakPoint3D : leftBreakPoint3Ds) {
                System.out.println("路段id为" + link.id() + "的左侧线断点(三维，米制)为" + leftBreakPoint3D);
            }
            
        }
    }
}
```

 **ArrayList<Point3D> rightBreakPoint3Ds();**

获取路段右侧线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink的右侧线断点(三维)集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> rightBreakPoint3Ds = link.rightBreakPoint3Ds();
            for (Object rightBreakPoint3D : rightBreakPoint3Ds) {
                System.out.println("路段id为" + link.id() + "的右侧线断点(三维)为" + rightBreakPoint3D);
            }
            rightBreakPoint3Ds = link.rightBreakPoint3Ds(UnitOfMeasure.Metric);
            for (Object rightBreakPoint3D : rightBreakPoint3Ds) {
                System.out.println("路段id为" + link.id() + "的右侧线断点(三维,米制)为" + rightBreakPoint3D);
            }
            
        }
    }
}
```

 **ArrayList<IConnector> fromConnectors();**

获取ILink的上游连接段, 其可能有多个, 返回类型为列表, 列表元素为IConnector对象

举例: 

```java
// 获取ILink的上游连接段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> fromConnectors = link.fromConnectors();
            for (Object fromConnector : fromConnectors) {
                // 假设fromConnector为IConnector类型，包含id()方法
                System.out.println("路段id为" + link.id() + "的上游连接段之一为" + ((IConnector)fromConnector).id());
            }
        }
    }
}
```

 **ArrayList<IConnector> toConnectors();**

获取ILink的下游连接段, 其可能有多个, 返回类型为列表, 列表元素为IConnector对象

举例: 

```java
// 获取ILink的下游连接段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> toConnectors = link.toConnectors();
            for (Object toConnector : toConnectors) {
                // 假设toConnector为IConnector类型，包含id()方法
                System.out.println("路段id为" + link.id() + "的下游连接段之一为" + ((IConnector)toConnector).id());
            }
        }
    }
}
```

 **void setOtherAttr(JsonObject otherAttr);**

设置路段的其它属性, TESSNG仿真过程中仅记录拓展的属性, 方便用户拓展, 并自定义使用

举例: 

```java
// 设置ILink的其它属性
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("newAttr", "add a new attr");
                    JsonObject newAttr = builder.build();
                    link.setOtherAttr(newAttr);
                    System.out.println("路段id为" + link.id() + "的其它属性为" + link.otherAttr());
                }
            }
        }
```

 **JsonObject otherAttr();**

获取路段的其它属性, TESSNG仿真过程中仅记录拓展的属性, 方便用户拓展, 并自定义使用

举例: 

```java
// 获取ILink的其它属性
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            System.out.println("路段id为" + link.id() + "的其它属性为" + link.otherAttr());
        }
    }
}
```

 **void setLaneTypes(ArrayList<String> lType);**

依次为ILink下所有车道设置车道属性（列表顺序为从右到左的车道顺序）, 入参为序列类型（列表, 元组等）, 其中元素的类型从这四种常量字符串中获取: "机动车道"、"机非共享"、"非机动车道"、"公交专用道"

举例: 

```java
// 依次为ILink下所有车道设置车道属性
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    if (link.laneCount() == 3) {
                        ArrayList<String> laneTypes = new ArrayList<>();
                        laneTypes.add("机动车道");
                        laneTypes.add("非机动车道");
                        laneTypes.add("公交专用道");
                        link.setLaneTypes(laneTypes);
                    }
                }
            }
        }
```

 **void setLaneOtherAtrrs(ArrayList<JsonObject> lAttrs);**

依次为ILink下所有车道设置车道其它属性

举例: 

```java
# 依次为ILink下所有车道设置车道其它属性
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for(ILink link:lLinks){
            if(link.laneCount() == 3){
                link.setLaneOtherAtrrs(new ArrayList<>(Arrays.asList(
                        Json.createObjectBuilder().add("new_name", "自定义机动车道").build(),
                        Json.createObjectBuilder().add("new_name", "自定义非机动车道").build(),
                        Json.createObjectBuilder().add("new_name", "自定义公交专用道").build()
                )));
            }
        }
    }
}

```

 **double distToStartPoint(Point p, UnitOfMeasure unit);**

ILink中心线上任意一点到ILink起点的距离, 默认单位: 像素  

参数: 
[ in ] p: 当前点坐标
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILink中心线上任意一点到ILink起点的距离
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<Point> centerBreakPoints = link.centerBreakPoints();
                    if (centerBreakPoints.size() > 1) {
                        System.out.println("id为" + link.id() + "的路段的中心线到起点的距离为" + link.distToStartPoint(centerBreakPoints.get(1)));
                    }
                    centerBreakPoints = link.centerBreakPoints();
                    if (centerBreakPoints.size() > 1) {
                        System.out.println("id为" + link.id() + "的路段的中心线到起点的距离为" + link.distToStartPoint(centerBreakPoints.get(1),UnitOfMeasure.Metric));
                    }

                }
            }
        }
```

 **boolean getPointAndIndexByDist(double dist, Point outPoint, ObjInt outIndex);**

获取ILink中心线起点下游dist距离处的点及其所属分段序号, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向下游延伸的距离  
[ out ] outPoint: 中心线起点向下游延伸dist距离后所在点  
[ out ] outIndex: 中心线起点向下游延伸dist处的点所属分段序号
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            Point outPoint = new Point();
            ObjInt outIndex = new ObjInt(0);
            boolean result = link.getPointAndIndexByDist(50, outPoint, outIndex);
            if (result) {
                System.out.printf("id为%d的路段的中心线起点向下游延伸50像素处的点为(%f, %f), 所属分段序号为%d%n",
                        link.id(), outPoint.getX(), outPoint.getY(), outIndex.getValue());
            }
            result = link.getPointAndIndexByDist(50, outPoint, outIndex, UnitOfMeasure.Metric);
            if (result) {
                System.out.printf("id为%d的路段的中心线起点向下游延伸50像素处的点为(%f, %f), 所属分段序号为%d%n",
                        link.id(), outPoint.getX(), outPoint.getY(), outIndex.getValue());
            }
        }
    }
}
```

 **boolean getPointByDist(double dist, Point outPoint, UnitOfMeasure unit);**

求ILink中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
# 获取ILink中心线起点向前延伸dist距离后所在点
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    Point outPoint1 = new Point();
                    boolean result1 = link.getPointByDist(50, outPoint1 );
                    if (result1) {
                        System.out.printf("id为%d的路段的中心线起点向下游延伸50像素处的点为(%f, %f), 所属分段序号为%d%n",
                                link.id(), outPoint1.getX(), outPoint1.getY() );
                    }
                    result1 = link.getPointByDist(50, outPoint1, UnitOfMeasure.Metric );
                    if (result1) {
                        System.out.printf("id为%d的路段的中心线起点向下游延伸50米处的点为(%f, %f), 所属分段序号为%d%n",
                                link.id(), outPoint1.getX(), outPoint1.getY() );
                    }
                }
            }
        }
```

 **Vector<Point> polygon();**

获取路段的多边型轮廓, 返回值类型 Vector<Point> , 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    System.out.println("id为" + link.id() + "的路段的多边型轮廓为" + link.polygon());
                }
            }
        }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showLinkAttr(netiface)
    }
}

public static void showLinkAttr(NetInterface netiface) {
        System.out.printf("===场景中的link总数=%d, 第一个link的id=%d%n",
                netiface.linkCount(), netiface.linkIds().get(0));

        ILink link = netiface.findLink(netiface.linkIds().get(0));
        ILink link1 = netiface.links().get(0);
        System.out.println(link1);

        System.out.printf("该link的属性: id=%d, %n", link.id());
        System.out.printf("link.fromConnectors()=%s%n", link.fromConnectors());

        // 构建其他属性的JSON对象
        JsonObjectBuilder otherAttrBuilder = Json.createObjectBuilder();
        otherAttrBuilder.add("new_msg", "this is a av car");
        JsonObject otherAttr = otherAttrBuilder.build();

        // 设置车道类型和属性
        ArrayList<String> laneTypes = new ArrayList<>();
        laneTypes.add("公交专用道");
        laneTypes.add("机动车道");

        // 输出路段属性信息
        System.out.printf(
                "该link: id=%d, 其属性为: 路段类型=%s, 路段长度=%.2f, "
                        + "宽度=%.2f, 高程=%.2f, 高程v3z=%.2f, "
                        + "名称=%s, linkType=%s, 车道数=%d, "
                        + "路段最高限速=%.2f, 路段最低限速=%.2f, "
                        + "更新后最高限速=%.2f, "
                        + "路段包含的车道对象=%s, "
                        + "路段包含的laneObject对象=%s, "
                        + "路段中心线=%s, "
                        + "路段左侧线=%s, "
                        + "路段右侧线=%s, "
                        + "路段中心线3D=%s, "
                        + "路段左侧线3D=%s, "
                        + "路段右侧线3D=%s, "
                        + "自定义其他属性=%s, "
                        + "车道类型设置=%s "
                        + "polygon=%s%n",

                link.id(),                  // %d（id）
                link.gtype(),               // %s（路段类型）
                link.length(),              // %.2f（长度）
                link.width(),               // %.2f（宽度）
                link.z(),                   // %.2f（高程）
                link.v3z(),                 // %.2f（高程v3z）
                link.name(),                // %s（名称）
                link.linkType(),            // %s（linkType）
                link.laneCount(),           // %d（车道数）
                link.limitSpeed(),          // %.2f（最高限速）
                link.minSpeed(),            // %.2f（最低限速）
                link.limitSpeed(),          // %.2f（更新后最高限速）
                link.lanes(),               // %s（车道对象）
                link.laneObjects(),         // %s（laneObject对象）
                link.centerBreakPoints(),   // %s（中心线）
                link.leftBreakPoints(),     // %s（左侧线）
                link.rightBreakPoints(),    // %s（右侧线）
                link.centerBreakPoint3Ds(), // %s（中心线3D）
                link.leftBreakPoint3Ds(),   // %s（左侧线3D）
                link.rightBreakPoint3Ds(),  // %s（右侧线3D）
                
                otherAttr,                  // %s（自定义其他属性）
                laneTypes,                  // %s（车道类型设置）
                link.polygon()              // %s（polygon）
        );
        // 获取指定距离处的点和分段序号
        Point outPoint = new Point();
        ObjInt outIndex = new ObjInt(0);
        link.getPointAndIndexByDist(2.0, outPoint, outIndex);
        System.out.printf("getPointAndIndexByDist=%s, %d%n", outPoint, outIndex.getValue());

        // 获取指定距离处的点
        Point outPoint2 = new Point( );
        link.getPointByDist(2.0, outPoint2);
        System.out.printf("getPointByDist=%s%n", outPoint2);
    }
```






### 2.5. ILane

车道接口, 方法如下: 

 **int gtype();**

类型, 车道类型为GLaneType

举例: 

```java
// 获取ILane的类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILane
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的类型为" + lane.gtype());
            }
        }
    }
}
```

 **int id();**

获取车道ID

举例: 

```java
// 获取ILane的ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILane
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id());
            }
        }
    }
}
```

 **ILink link();**

获取车道所属路段, 返回路段对象

举例: 

```java
// 获取ILane所属路段
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道所属路段为" + lane.link());
                    }
                }
            }
        }
```

 **ISection section();**

获取车道所属Section, 返回Section对象, 其为ILink的父对象

举例: 

```java
// 获取ILane所属Section
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道所属Section为" + lane.section());
            }
        }
    }
}
```

 **double length(UnitOfMeasure unit);**

获取车道长度, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILane
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道长度为" + lane.length());
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道长度(米制单位)为" + lane.length(UnitOfMeasure.Metric));
            }
        }
    }
}
```

 **double width(UnitOfMeasure unit);**

获取车道宽度, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的宽度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道宽度为" + lane.width());
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道宽度（米制）为" + lane.width(UnitOfMeasure.Metric));
            }
        }
    }
}
```

 **int number();**

获取车道序号, 从0开始（自外侧往内侧, 即自右向左依次编号）

举例: 

```java
// 获取ILane的序号
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道序号为" + lane.number());
                    }
                }
            }
        }
```

 **String actionType();**

获取车道的行为类型, 返回的为行为类型常量字符串, 包括: "机动车道"、“非机动车道”、“公交专用道”

举例: 

```java
// 获取ILane的行为类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道行为类型为" + lane.actionType());
            }
        }
    }
}
```

**ArrayList<ILaneConnector> fromLaneConnectors();**

获取上游车道连接列表

举例: 

```java
// 获取ILane的上游车道连接列表
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                List<?> lLaneConnectors = lane.fromLaneConnectors();
                for (Object laneConnector : lLaneConnectors) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道上车道连接列表为" + ((ILaneConnector)laneConnector).id());
                }
            }
        }
    }
}
```

**ArrayList<ILaneConnector> toLaneConnectors();**

获取下游车道连接列表

举例: 

```java
// 获取ILane的下游车道连接列表
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                List<?> lLaneConnectors = lane.toLaneConnectors();
                for (Object laneConnector : lLaneConnectors) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道下游车道连接列表为" + ((ILaneConnector)laneConnector).id());
                }
            }
        }
    }
}
```

 **ArrayList<Point> centerBreakPoints(UnitOfMeasure unit);**

获取车道中心点断点集, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILane的中心点断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有ILink
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> lCenterBreakPoints = lane.centerBreakPoints();
                        for (Point centerBreakPoint : lCenterBreakPoints) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心点断点集为" + "(" +  centerBreakPoint.getX() + "," +centerBreakPoint.getY() +")" );
                        }
                        lCenterBreakPoints = lane.centerBreakPoints(UnitOfMeasure.Metric);
                        for (Point centerBreakPoint : lCenterBreakPoints) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心点断点集为" + "(" +  centerBreakPoint.getX() + "," +centerBreakPoint.getY() +")" );
                        }

                    }
                }
            }
        }
```

 **ArrayList<Point> leftBreakPoints(UnitOfMeasure unit);**

获取车道左侧线断点集, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的左侧线断点集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                List<Point> lLeftBreakPoints = lane.leftBreakPoints();
                for (Point leftBreakPoint : lLeftBreakPoints) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点集为" + "(" +  leftBreakPoint.getX() + "," +leftBreakPoint.getY() +")" );
                }
                lLeftBreakPoints = lane.leftBreakPoints(UnitOfMeasure.Metric);
                for (Point leftBreakPoint : lLeftBreakPoints) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点集(米制)为" + "(" +  leftBreakPoint.getX() + "," +leftBreakPoint.getY() +")" );
                }
            }
        }
    }
}
```

 **ArrayList<Point> rightBreakPoints(UnitOfMeasure unit);**

获取车道右侧线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的右侧线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> lRightBreakPoints = lane.rightBreakPoints();
                        for (Point rightBreakPoint : lRightBreakPoints) {
                            System.out.println("id为" + lane.id() + "的车道的右侧线断点集为" + "(" +  rightBreakPoint.getX() + "," +rightBreakPoint.getY() +")");
                        }
                        lRightBreakPoints = lane.rightBreakPoints(UnitOfMeasure.Metric);
                        for (Point rightBreakPoint : lRightBreakPoints) {
                            System.out.println("id为" + lane.id() + "的车道的右侧线断点集(米制)为" + "(" +  rightBreakPoint.getX() + "," +rightBreakPoint.getY() +")");
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point3D> centerBreakPoint3Ds(UnitOfMeasure unit);**

获取车道中心线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILane的中心线断点(三维)集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point3D> lCenterBreakPoint3Ds = lane.centerBreakPoint3Ds();
                        for (Point3D centerBreakPoint3D : lCenterBreakPoint3Ds) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线断点(三维)集为" + "(" +  centerBreakPoint3D.getX() + "," +centerBreakPoint3D.getY() + ","  + centerBreakPoint3D.getZ() + ")");
                        }
                        lCenterBreakPoint3Ds = lane.centerBreakPoint3Ds(UnitOfMeasure.Metric);
                        for (Point3D centerBreakPoint3D : lCenterBreakPoint3Ds) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线断点(三维,米制)集为" + "(" +  centerBreakPoint3D.getX() + "," +centerBreakPoint3D.getY() + ","  + centerBreakPoint3D.getZ() + ")");
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreakPoint3Ds(UnitOfMeasure unit);**

获取车道左侧线断点(三维)集, 默认单位: 像素,  可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的左侧线断点(三维)集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point3D> lLeftBreakPoint3Ds = lane.leftBreakPoint3Ds();
                        for (Point3D leftBreakPoint3D : lLeftBreakPoint3Ds) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点(三维)集为" + "(" +  leftBreakPoint3D.getX() + "," +leftBreakPoint3D.getY() + ","  + leftBreakPoint3D.getZ() + ")");
                        }
                        lLeftBreakPoint3Ds = lane.leftBreakPoint3Ds(UnitOfMeasure.Metric);
                        for (Point3D leftBreakPoint3D : lLeftBreakPoint3Ds) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点(三维,米制)集为" + "(" +  leftBreakPoint3D.getX() + "," +leftBreakPoint3D.getY() + ","  + leftBreakPoint3D.getZ() + ")");
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreakPoint3Ds(UnitOfMeasure unit);**

获取车道右侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的右侧线断点(三维)集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                List<Point3D> lRightBreakPoint3Ds = lane.rightBreakPoint3Ds();
                for (Point3D rightBreakPoint3D : lRightBreakPoint3Ds) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点(三维)集为" + "(" +  rightBreakPoint3D.getX() + "," +rightBreakPoint3D.getY() + ","  + rightBreakPoint3D.getZ() + ")");
                }
                lRightBreakPoint3Ds = lane.rightBreakPoint3Ds(UnitOfMeasure.Metric);
                for (Point3D rightBreakPoint3D : lRightBreakPoint3Ds) {
                    System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧线断点(三维,米制)集为" + "(" +  rightBreakPoint3D.getX() + "," +rightBreakPoint3D.getY() + ","  + rightBreakPoint3D.getZ() + ")");
                }
            }
        }
    }
}
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint,UnitOfMeasure unit);**

根据指定起终点断点, 获取车道左侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点, 默认单位: 像素,    
[ in ] toPoint: 中心线上某一点作为终点, 默认单位: 像素,   
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILane的左侧部分断点(三维)集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> lLeftBreakPoints = lane.leftBreakPoints();
                        if (lLeftBreakPoints.size() > 2) {
                            List<Point3D> lLeftBreak3DsPartly = lane.leftBreak3DsPartly(lLeftBreakPoints.get(0), lLeftBreakPoints.get(2));
                            for (Point3D leftBreak3DPartly : lLeftBreak3DsPartly) {
                                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧部分断点(三维)集为" + "(" + leftBreak3DPartly.getX() + "," + leftBreak3DPartly.getY() + "," + leftBreak3DPartly.getZ() + ")");
                            }
                            
                             lLeftBreak3DsPartly = lane.leftBreak3DsPartly(lLeftBreakPoints.get(0), lLeftBreakPoints.get(2),UnitOfMeasure.Metric);
                            for (Point3D leftBreak3DPartly : lLeftBreak3DsPartly) {
                                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧部分断点(三维，米制)集为" + "(" + leftBreak3DPartly.getX() + "," + leftBreak3DPartly.getY() + "," + leftBreak3DPartly.getZ() + ")");
                            }
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint, UnitOfMeasure unit);**

根据指定起终点断点, 获取车道右侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点, 像素坐标,   
[ in ] toPoint: 中心线上某一点作为终点, 像素坐标,   
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 获取ILane的右侧部分断点(三维)集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> lRightBreakPoints = lane.rightBreakPoints();
                        if (lRightBreakPoints.size() > 2) {
                            List<Point3D> lRightBreak3DsPartly = lane.rightBreak3DsPartly(lRightBreakPoints.get(0), lRightBreakPoints.get(2));
                            for (Point3D RightBreak3DPartly : lRightBreak3DsPartly) {
                                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧部分断点(三维)集为" + "(" + RightBreak3DPartly.getX() + "," + RightBreak3DPartly.getY() + "," + RightBreak3DPartly.getZ() + ")");
                            }
                            lRightBreak3DsPartly = lane.rightBreak3DsPartly(lRightBreakPoints.get(0), lRightBreakPoints.get(2),UnitOfMeasure.Metric);
                            for (Point3D RightBreak3DPartly : lRightBreak3DsPartly) {
                                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的左侧部分断点(三维,米制)集为" + "(" + RightBreak3DPartly.getX() + "," + RightBreak3DPartly.getY() + "," + RightBreak3DPartly.getZ() + ")");
                            }
                        }
                    }
                }
            }
        }
```

 **double distToStartPoint(Point p，UnitOfMeasure unit);**

获取中心线上一点到起点的距离, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] p: 当前点坐标
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILane的中心线上一点到起点的距离
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links(); 
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> centerBreakPoints = lane.centerBreakPoints();
                        if (centerBreakPoints.size() > 1) {
                            double lDistToStartPoint = lane.distToStartPoint(centerBreakPoints.get(1));
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线上一点到起点的距离为" + lDistToStartPoint);
                              lDistToStartPoint = lane.distToStartPoint(centerBreakPoints.get(1),UnitOfMeasure.Metric);
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线上一点到起点的距离(米制)为" + lDistToStartPoint);
                        }


                    }
                }
            }
        }
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex, UnitOfMeasure unit);**

根据中心线上任意点所处的车道分段号和该点本身信息, 计算该点到车道起点的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] p: 当前中心线上的点坐标, 像素单位  
[ in ] segmIndex: 该点所在车道上的分段序号  
[ in ] bOnCentLine: 该点是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取ILane的中心线上一点到起点的距离（带分段索引）
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> centerBreakPoints = lane.centerBreakPoints();
                        if (centerBreakPoints.size() > 1) {
                            double lDistToStartPoint = lane.distToStartPointWithSegmIndex(centerBreakPoints.get(1), 1);
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线上一点到起点的距离为" + lDistToStartPoint);
                            lDistToStartPoint = lane.distToStartPointWithSegmIndex(centerBreakPoints.get(1), 1, true,UnitOfMeasure.Metric);
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心线上一点到起点的距离（米制）为" + lDistToStartPoint);
                        }

                    }
                }
            }
        }
```

 **boolean getPointAndIndexByDist(double dist, Point outPoint, ObjInt outIndex,UnitOfMeasure unit);**

获取车道中心线起点下游dist距离处的点及其所属分段序号; 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点  
[ out ] outIndex: 中心线起点向前延伸dist距离后所在分段序号  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  


举例: 

```java
# 获取ILane的中心线上一点到起点的距离
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        // 创建用于存储结果的Point对象
                        Point outPoint = new Point();
                        ObjInt outIndex = new ObjInt(0);
                        boolean result = lane.getPointAndIndexByDist(50, outPoint,outIndex);
                        if (result) {
                            System.out.printf("路段id为%d的车道id为%d的车道中心线起点下游50像素处的点为(%f, %f)%n",
                                    link.id(), lane.id(), outPoint.getX(), outPoint.getY());
                        }
                        result = lane.getPointAndIndexByDist(50, outPoint,outIndex,UnitOfMeasure.Metric);
                        if (result) {
                            System.out.printf("路段id为%d的车道id为%d的车道中心线起点下游50米处的点为(%f, %f)%n",
                                    link.id(), lane.id(), outPoint.getX(), outPoint.getY());
                        }
                    }
                }
            }
        }
                }
```

 **boolean getPointByDist(double dist, Point outPoint, UnitOfMeasure unit);**

获取车道中心线起点下游dist距离处的点; 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
# 获取ILane的中心线上一点到起点的距离
if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
List<ILink> lLinks = netiface.links();
for (ILink link : lLinks) {
List<ILane> lLanes = link.lanes();
for (ILane lane : lLanes) {
    // 创建用于存储结果的Point对象
    Point outPoint = new Point();
    boolean result = lane.getPointByDist(50, outPoint);
    if (result) {
        System.out.printf("路段id为%d的车道id为%d的车道中心线起点下游dist距离处的点为(%f, %f)%n",
                link.id(), lane.id(), outPoint.getX(), outPoint.getY());
    }
    result = lane.getPointByDist(50, outPoint, UnitOfMeasure.Metric);
    if (result) {
        System.out.printf("路段id为%d的车道id为%d的车道中心线起点下游dist米距离处的点为(%f, %f)%n",
                link.id(), lane.id(), outPoint.getX(), outPoint.getY());
    }
}
}
}
}
```

 **void setOtherAttr(JsonObject attr);**

设置车道的其它属性, 方便用户拓展车道属性; 类型: 字典形式

举例: 

```java
// 设置ILane的其它属性
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        JsonObjectBuilder builder = Json.createObjectBuilder();
                        builder.add("newAttr", "add a new attr");
                        JsonObject otherAttr = builder.build();
                        
                        lane.setOtherAttr(otherAttr);
                    }
                }
            }
        }
```

 **void setLaneType(String type);**

设置车道的类型; 车道类型常量范围: "机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

参数: 

[ in ] type: 车道类型, 选下列几种类型其中一种: "机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

举例: 

```java
// 设置ILane的类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                lane.setLaneType("机动车道");
            }
        }
    }
}
```

 **Vector<Point> polygon();**

获取车道的多边型轮廓顶点, 默认单位: 像素

举例: 

```java
// 获取ILane的多边型轮廓顶点
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<ILane> lLanes = link.lanes();
            for (ILane lane : lLanes) {
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的多边型轮廓顶点为" + lane.polygon());
            }
        }
    }
}
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showLaneAttr(netiface);
    }
}
public static void showLaneAttr(NetInterface netiface) {
        // 获取第一个路段
        ILink link = netiface.links().get(0);
        // 初始化目标车道（默认取第一个车道）
        ILane lane = link.lanes().get(0);

        // 查找序号为0的车道（最右侧车道）
        List<ILane> lanes = link.lanes();
        for (ILane currentLane : lanes) {
            if (currentLane.number() == 0) {
                lane = currentLane;
                break;
            }
        }

        JsonObject otherAttr = Json.createObjectBuilder()
                .add("newAttr", "add a new attr")
                .build();

        lane.setOtherAttr(otherAttr);
        lane.setLaneType("机动车道");

        List<Point> centerBreakPointsPixel = lane.centerBreakPoints();
        List<Point> centerBreakPointsMetric = lane.centerBreakPoints(UnitOfMeasure.Pixel);
        List<Point> leftBreakPointsPixel = lane.leftBreakPoints();
        List<Point> leftBreakPointsMetric = lane.leftBreakPoints(UnitOfMeasure.Metric);

        // 输出车道基础属性
        System.out.printf(
                "===link中的第一个lane(最右侧) id=%d, 类型gtype=%s, %n" +
                        "所属link=%s, 所属section=%s, %n" +
                        "长度length（像素制）=%.2f, 米制=%.2f, %n" +
                        "宽度width（像素制）=%.2f, 米制=%.2f, %n" +
                        "车道序号number=%d, 行为类型=%s, %n" +
                        "fromLaneObject=%s, toLaneObject=%s, %n" +
                        "centerBreakPoints(像素制)=%s, centerBreakPoints(米制)=%s, %n" +
                        "leftBreakPoints(像素制)=%s, leftBreakPoints(米制)=%s, %n" +
                        "rightBreakPoints(像素制)=%s, rightBreakPoints(米制)=%s, %n" +
                        "centerBreakPoint3Ds(像素制)=%s, centerBreakPoint3Ds(米制)=%s, %n" +
                        "leftBreakPoint3Ds(像素制)=%s, leftBreakPoint3Ds(米制)=%s, %n" +
                        "rightBreakPoint3Ds(像素制)=%s, rightBreakPoint3Ds(米制)=%s, %n",
                lane.id(), lane.gtype(),
                lane.link(), lane.section(),
                lane.length(), lane.length(),
                lane.width(), lane.width(UnitOfMeasure.Metric),
                lane.number(), lane.actionType(),
                lane.fromLaneObject(), lane.toLaneObject(),
                centerBreakPointsPixel, centerBreakPointsMetric,
                leftBreakPointsPixel, leftBreakPointsMetric,
                lane.rightBreakPoints(), lane.rightBreakPoints(UnitOfMeasure.Metric),
                lane.centerBreakPoint3Ds(), lane.centerBreakPoint3Ds(UnitOfMeasure.Metric),
                lane.leftBreakPoint3Ds(), lane.leftBreakPoint3Ds(UnitOfMeasure.Metric),
                lane.rightBreakPoint3Ds(), lane.rightBreakPoint3Ds(UnitOfMeasure.Metric)
        );

        // 输出部分断点集和距离信息
        System.out.printf(
                "leftBreak3DsPartly(像素制)=%s, %n" +
                        "leftBreak3DsPartly(米制)=%s, %n" +
                        "rightBreak3DsPartly(像素制)=%s, %n" +
                        "rightBreak3DsPartly(米制)=%s, %n" +
                        "distToStartPoint(像素制)=%.2f, distToStartPoint(米制)=%.2f, %n" +
                        "设置自定义属性结果=成功, 设置车道类型结果=成功, 行为类型=%s, polygon=%s%n",
                (leftBreakPointsPixel.size() >= 2) ? lane.leftBreak3DsPartly(leftBreakPointsPixel.get(1), leftBreakPointsPixel.get(leftBreakPointsPixel.size() - 1)) : "无有效断点",
                (leftBreakPointsMetric.size() >= 2) ? lane.leftBreak3DsPartly(leftBreakPointsMetric.get(1), leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1), UnitOfMeasure.Metric) : "无有效断点",
                (leftBreakPointsPixel.size() >= 2) ? lane.rightBreak3DsPartly(leftBreakPointsPixel.get(1), leftBreakPointsPixel.get(leftBreakPointsPixel.size() - 1)) : "无有效断点",
                (leftBreakPointsMetric.size() >= 2) ? lane.rightBreak3DsPartly(leftBreakPointsMetric.get(1), leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1), UnitOfMeasure.Metric) : "无有效断点",
                (!centerBreakPointsPixel.isEmpty()) ? lane.distToStartPoint(centerBreakPointsPixel.get(0)) : 0.0,
                (!centerBreakPointsMetric.isEmpty()) ? lane.distToStartPoint(centerBreakPointsMetric.get(0), UnitOfMeasure.Metric) : 0.0,
                lane.actionType(),
                lane.polygon()
        );

        // 调用getPointAndIndexByDist方法（像素制）
        Point outPoint = new Point();
        ObjInt outIndex = new ObjInt(0);
        boolean isSuccessPixel = lane.getPointAndIndexByDist(2.0, outPoint, outIndex);
        // 米制
        Point outPoint1 = new Point( );
        ObjInt outIndex1 = new ObjInt(0);
        boolean isSuccessMetric = lane.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric);

        // 输出getPointAndIndexByDist结果
        if (isSuccessPixel) {
            System.out.printf("getPointAndIndexByDist(像素制)=(%f, %f), 分段序号=%d%n",
                    outPoint.getX(), outPoint.getY(), outIndex.getValue());
        }
        if (isSuccessMetric) {
            System.out.printf("getPointAndIndexByDist(米制)=(%f, %f), 分段序号=%d%n",
                    outPoint1.getX(), outPoint1.getY(), outIndex1.getValue());
        }

        // 调用getPointByDist方法（像素制）
        Point outPoint2 = new Point();
        boolean isSuccessByDistPixel = lane.getPointByDist(2.0, outPoint2);
        // 米制
        Point outPoint3 = new Point();
        boolean isSuccessByDistMetric = lane.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric);

        // 输出getPointByDist结果
        if (isSuccessByDistPixel) {
            System.out.printf("getPointByDist(像素制)=(%f, %f)%n", outPoint2.getX(), outPoint2.getY());
        }
        if (isSuccessByDistMetric) {
            System.out.printf("getPointByDist(米制)=(%f, %f)%n", outPoint3.getX(), outPoint3.getY());
        }
    }
```





### 2.6. IConnector

连接段接口, 方法如下: 

 **int gtype();**

类型, 连接段类型为GConnectorType, GConnectorType是一种整数型常量。

举例: 

```java
// 获取IConnector的类型
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    System.out.println("id为" + connector.id() + "的连接段的类型为" + connector.gtype());
                }
            }
        }
```

 **int id();**

获取连接段ID; 因为连接段ID和路段ID是相互独立的, 所以可能两者的ID之间会有重复

举例: 

```java
// 获取IConnector的ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("连接段的ID为" + connector.id());
        }
    }
}
```

 **double length(UnitOfMeasure unit);**

获取连接段长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取IConnector的长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的长度为" + connector.length());
            System.out.println("id为" + connector.id() + "的连接段的长度（米）为" + connector.length(UnitOfMeasure.Metric));
        }
        
    }
}
```

 **double z(UnitOfMeasure unit);**

获取连接段高程, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取IConnector的高程
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的高程为" + connector.z());
            System.out.println("id为" + connector.id() + "的连接段的高程(米)为" + connector.z(UnitOfMeasure.Metric));
        }
    }
}
```

 **double v3z(UnitOfMeasure unit);**

获取连接段高程, 过载自ISection的方法, 与z()方法作用相同, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取IConnector的高程
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    System.out.println("id为" + connector.id() + "的连接段的高程为" + connector.v3z());
                    System.out.println("id为" + connector.id() + "的连接段的高程(米)为" + connector.v3z(UnitOfMeasure.Metric));
                    
                }
            }
        }
```

 **String name();**

获取连接段名称

举例: 

```java
// 获取IConnector的名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的名称" + connector.name());
        }
    }
}
```

 **void setName(String name);**

设置连接段名称

举例: 

```java
// 设置IConnector的名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            connector.setName("new connector");
        }
    }
}
```

 **ILink fromLink();**

获取当前connector的起始路段, 返回路段对象

举例: 

```java
// 获取IConnector的起始路段
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    System.out.println("id为" + connector.id() + "的连接段的起始路段为" + connector.fromLink());
                }
            }
        }
```

 **ILink toLink();**

获取当前connector的目标路段（出口路段）, 返回路段对象

举例: 

```java
// 获取IConnector的目标路段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的目标路段为" + connector.toLink());
        }
    }
}
```

 **double limitSpeed(UnitOfMeasure unit);**

获取连接器的最高限速, 因为连接器没有最高限速这一属性, 因此该函数返回连接器的起始路段最高限速作为连接段的最高限速, 默认单位: 千米/小时, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取IConnector的最高限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的最高限速为" + connector.limitSpeed());
            System.out.println("id为" + connector.id() + "的连接段的最高限速(米)为" + connector.limitSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **double minSpeed(UnitOfMeasure unit);**

获取连接器的最低限速, 因为连接器没有最低限速这一属性, 因此返回连接器起始路段的最低限速作为连接段的最低限速, 默认单位: 千米/小时, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取IConnector的最低限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的最低限速为" + connector.minSpeed());
            System.out.println("id为" + connector.id() + "的连接段的最低限速（米）为" + connector.minSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **ArrayList<ILaneConnector> laneConnectors();**

获取连接器下的所有“车道连接”对象, 列表形式, 列表元素为ILaneConnector对象

举例: 

```java
// 获取IConnector的车道连接
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的车道连接为" + connector.laneConnectors());
        }
    }
}
```

 **ArrayList<ILaneObject> laneObjects();**

车道及“车道连接”的接口列表

举例: 

```java
// 获取IConnector的车道连接
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的车道连接为" + connector.laneObjects());
        }
    }
}
```

 **void setLaneConnectorOtherAtrrs(ArrayList<JsonObject> lAttrs);**

设置包含的“车道连接”其它属性

举例: 

```java
// 设置IConnector的其它属性
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            int laneCount = connector.laneConnectors().size();
            ArrayList<JsonObject> attributes = new ArrayList<>();
            for (int i = 0; i < laneCount; i++) {
                JsonObject attribute = Json.createObjectBuilder()
                        .add("newAttr", i)
                        .build();
                attributes.add(attribute);
            }
            connector.setLaneConnectorOtherAtrrs(attributes);
        }
    }
}
```

 **void setOtherAttr(JsonObject otherAttr);**

设置连接段其它属性

举例: 

```java
// 获取ILane的多边型轮廓顶点
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {

                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("newAttr", "add a new attr");
                    JsonObject otherAttr = builder.build();
                    connector.setOtherAttr(otherAttr);
                }
            }
        }
```

 **Vector<Point> polygon();**

获取连接段的多边型轮廓顶点

举例: 

```java
// 获取ILane的多边型轮廓顶点
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector lConnector : lConnectors) {
                System.out.println("id为" + lConnector.id() + "的连接段的多边型轮廓顶点为" + lConnector.polygon());

        }
    }
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    // 获取路网子接口
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 调用方法展示连接段属性
        showConnectorAttr(netiface);
    }
}
public static void showConnectorAttr(NetInterface netiface) {
        List<IConnector> connectors = netiface.connectors();
        int connectorCount = netiface.connectorCount();
        List<Long> connectorIds = netiface.connectorIds();
        long firstConnectorId = (connectors != null && !connectors.isEmpty()) ? connectors.get(0).id() : 0;

        System.out.printf(
                "===场景中的connector个数（连接段对象）=%d, %d, %d, " +
                        "第一个connector的属性=%d%n",
                (connectors != null ? connectors.size() : 0),  
                connectorCount,                                
                (connectorIds != null ? connectorIds.size() : 0),  
                firstConnectorId
        );

        IConnector connector = null;
        IConnector connector1 = null;
        if (connectors != null && !connectors.isEmpty()) {
            connector = connectors.get(0);
        }
        if (connectorIds != null && !connectorIds.isEmpty()) {
            connector1 = netiface.findConnector(connectorIds.get(0));
        }

        if (connector == null) {
            System.out.println("未找到有效的connector对象");
            return;
        }

        System.out.println("connector类型: " + connector.getClass().getName());
        System.out.println("connector可用方法/属性: " + java.util.Arrays.toString(connector.getClass().getMethods()));

        connector.setName("new connector");  

        List<ILaneConnector> laneConnectors = connector.laneConnectors();
        JsonArrayBuilder laneConnectorAttrsBuilder = Json.createArrayBuilder();
        if (laneConnectors != null) {
            for (int i = 0; i < laneConnectors.size(); i++) {
                laneConnectorAttrsBuilder.add(Json.createObjectBuilder().add("newAttr", i));
            }
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("newAttr", "add a new attr");
        JsonObject otherAttr = builder.build();
        connector.setOtherAttr(otherAttr);  // 设置连接段其他属性

        // 输出连接段详细属性
        System.out.printf(
                "该connectors的属性: id(连接段和路段ID是独立的, 因此两者ID可能会重复)=%d, 类型gtype=%s, %n" +
                        "name=%s, %n" +
                        "长度length（像素制）=%.2f, 米制=%.2f, %n" +
                        "高程=%.2f, v3z=%.2f, %n" +
                        "fromLink=%s, toLink=%s, fromSection=%s, toSection=%s, %n" +
                        "最高限速(像素制)=%.2f, 最高限速(米制)=%.2f, %n" +
                        "最低限速(像素制)=%.2f, 最低限速(米制)=%.2f, %n" +
                        "laneConnectors=%s, laneObjects=%s, %n" +
                        "polygon=%s%n",
                connector.id(),
                connector.gtype(),
                connector.name(),  // 已通过setName设置为"new connector"
                connector.length(),
                connector.length(UnitOfMeasure.Metric),  // 米制
                connector.z(),
                connector.v3z(),
                connector.fromLink(),
                connector.toLink(),
                connector.fromSection(0),  // id=0
                connector.toSection(0),    // id=0
                connector.limitSpeed(),
                connector.limitSpeed(UnitOfMeasure.Metric),  // 米制
                connector.minSpeed(),
                connector.minSpeed(UnitOfMeasure.Metric),    // 米制
                connector.laneConnectors(),
                connector.laneObjects(),
                connector.polygon()
        );
    }
```





### 2.7. ILaneConnector

“车道连接”接口, 方法如下: 

 **int gtype();**

类型, GLaneType或GLaneConnectorType, 车道连接段为GLaneConnectorType , 这里的返回值只可能是GLaneConnectorType

举例: 

```java
// 获取IConnector的类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            List<ILaneConnector> laneConnectors = connector.laneConnectors();
            for (ILaneConnector laneConnector : laneConnectors) {
                System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的类型为" + laneConnector.gtype());
            }
        }
    }
}
```

 **int id();**

获取车道连接ID

举例: 

```java
// 获取车道连接ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            List<ILaneConnector> laneConnectors = connector.laneConnectors();
            for (ILaneConnector laneConnector : laneConnectors) {
                System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的ID为" + laneConnector.id());
            }
        }
    }
}
```

 **IConnector connector();**

获取车道连接所属的连接段Connector对象, 返回类型IConnector

举例: 

```java
// 获取车道连接所属的连接段Connector对象
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "所属的连接段Connector对象为" + laneConnector.connector().name());
                    }
                }
            }
        }
```

 **ISection section();**

获取车道所属Section, Section为 IConnector的父类

举例: 

```java
// 获取车道连接所属的Section
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "所属的Section为" + laneConnector.section().name());
                    }
                }
            }
        }
```

 **ILane fromLane();**

获取当前车道链接的上游车道对象

举例: 

```java
// 获取车道连接的上游车道对象
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的上游车道对象为" + laneConnector.fromLane().id());
                    }
                }
            }
        }
```

 **ILane toLane();**

获取当前车道链接的下游车道对象

举例: 

```java
// 获取车道连接的下游车道对象
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的下游车道对象为" + laneConnector.toLane().number());
                    }
                }
            }
        }
```

 **double length(UnitOfMeasure unit);**

获取“车道连接”的长度, 是指中心线的长度, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的长度
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的长度为" + laneConnector.length());
                        System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的长度(米)为" + laneConnector.length(UnitOfMeasure.Metric));
                    }
                }
            }
        }
```

 **ArrayList<Point> centerBreakPoints(UnitOfMeasure unit);**

获取“车道连接”的中心线断点集, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point>  centerBPs= laneConnector.centerBreakPoints();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线断点集为"  );
                        for(Point point:centerBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();
                        centerBPs= laneConnector.centerBreakPoints(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线断点集（米制）为"  );
                        for(Point point:centerBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point> leftBreakPoints(UnitOfMeasure unit);**

获取“车道连接”左侧线断点集, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point>  leftBPs= laneConnector.leftBreakPoints();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的左侧线断点集为"  );
                        for(Point point:leftBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();
                        leftBPs= laneConnector.leftBreakPoints(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的左侧线断点集（米）为"  );
                        for(Point point:leftBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point> rightBreakPoints(UnitOfMeasure unit);**

获取“车道连接”右侧线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point>  rightBPs= laneConnector.rightBreakPoints();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的右侧线断点集为"  );
                        for(Point point:rightBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();
                        rightBPs= laneConnector.rightBreakPoints(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的右侧线断点集（米）为"  );
                        for(Point point:rightBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point3D> centerBreakPoint3Ds(UnitOfMeasure unit);**

获取“车道连接”中心线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point3D>  centerBPs= laneConnector.centerBreakPoint3Ds();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线断点(三维)集为"  );
                        for(Point3D point:centerBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();
                        centerBPs= laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线断点(三维，米)集为"  );
                        for(Point3D point:centerBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreakPoint3Ds(UnitOfMeasure unit);**

获取“车道连接”左侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point3D>  leftBPs= laneConnector.leftBreakPoint3Ds();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的左侧线断点(三维)集为"  );
                        for(Point3D point:leftBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();
                        leftBPs= laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的左侧线断点(三维,米)集为"  );
                        for(Point3D point:leftBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreakPoint3Ds(UnitOfMeasure unit);**

获取“车道连接”右侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的中心线断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point3D>  rightBPs= laneConnector.rightBreakPoint3Ds();
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的右侧线断点(三维)集为"  );
                        for(Point3D point:rightBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();
                        rightBPs= laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric);
                        System.out.print("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的右侧线断点(三维，米)集为"  );
                        for(Point3D point:rightBPs){
                            System.out.print("(" +  point.getX() + "," +point.getY() + "," +point.getZ() + ")," );
                        }
                        System.out.println();

                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint,UnitOfMeasure unit);**

根据指定的起终止点获取“车道连接”左侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点
[ in ] toPoint: 中心线上某一点作为终点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 获取车道连接的左侧部分断点(三维)集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point> leftBreakPoints = laneConnector.leftBreakPoints();
                        if (leftBreakPoints.size() > 0) {
                            Point startPoint = leftBreakPoints.get(0);
                            Point endPoint = leftBreakPoints.get(leftBreakPoints.size() - 1);
                            System.out.println("id为" + connector.id() + "的连接段的车道连接的左侧部分断点(三维)集为" + laneConnector.leftBreak3DsPartly(startPoint, endPoint));
                            System.out.println("id为" + connector.id() + "的连接段的车道连接的左侧部分断点(三维,米)集为" + laneConnector.leftBreak3DsPartly(startPoint, endPoint, UnitOfMeasure.Metric));
                        }

                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint, UnitOfMeasure unit);**

根据指定的起终止点获取“车道连接”右侧部分断点(三维)集, 默认单位: 像素, 高程Z默认单位: 像素

参数: 
[ in ] fromPoint: 中心线上某一点作为起点
[ in ] toPoint: 中心线上某一点作为终点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制 

举例: 

```java
// 获取车道连接的右侧部分断点(三维)集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            List<ILaneConnector> laneConnectors = connector.laneConnectors();
            for (ILaneConnector laneConnector : laneConnectors) {
                List<Point> leftBreakPoints = laneConnector.leftBreakPoints();
                if (leftBreakPoints.size() > 0) {
                    Point startPoint = leftBreakPoints.get(0);
                    Point endPoint = leftBreakPoints.get(leftBreakPoints.size() - 1);
                    System.out.println("id为" + connector.id() + "的连接段的车道连接的右侧部分断点(三维)集为" + laneConnector.rightBreak3DsPartly(startPoint, endPoint));
                    System.out.println("id为" + connector.id() + "的连接段的车道连接的右侧部分断点(三维，米)集为" + laneConnector.rightBreak3DsPartly(startPoint, endPoint,UnitOfMeasure.Metric));
                }

            }
        }
    }
}
```

 **double distToStartPoint(Point p, UnitOfMeasure unit);**

计算车道链接中心线上任意点到起点的距离, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 计算车道连接中心线上任意点到起点的距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            List<ILaneConnector> laneConnectors = connector.laneConnectors();
            for (ILaneConnector laneConnector : laneConnectors) {
                List<Point> centerBreakPoints = laneConnector.centerBreakPoints();
                if (centerBreakPoints.size() > 1) {
                    double distance = laneConnector.distToStartPoint(centerBreakPoints.get(1));
                    System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线上任意点到起点的距离为" + distance);
                    distance = laneConnector.distToStartPoint(centerBreakPoints.get(1),UnitOfMeasure.Metric);
                    System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线上任意点到起点的距离（米）为" + distance);
                    
                }

            }
        }
    }
}
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex, boolean bOnCentLine, UnitOfMeasure unit);**

计算中心线上任意点到起点的距离, 附加条件是该点所在车道上的分段序号, 默认单位为像素; 可通过unit参数设置单位  

参数: 
[ in ] p: 当前中心线上该点坐标, 像素坐标   
[ in ] segmIndex: 该点所在车道上的分段序号  
[ in ] bOnCentLine: 是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  
注: 如传入米制参数, 请勿遗忘传入segmIndex与bOnCentLine参数。

举例: 

```java
// 计算中心线上任意点到起点的距离
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        List<Point> centerBreakPoints = laneConnector.centerBreakPoints();
                        if (centerBreakPoints.size() > 1) {
                            System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线上任意点到起点的距离为" + laneConnector.distToStartPointWithSegmIndex(centerBreakPoints.get(1), 1));
                            System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线上任意点到起点的距离为" + laneConnector.distToStartPointWithSegmIndex(centerBreakPoints.get(1), 1,true,UnitOfMeasure.Metric));
                        }

                    }
                }
            }
        }
```

 **boolean getPointAndIndexByDist(double dist, Point outPoint, ObjInt outIndex, UnitOfMeasure unit);**

求中心线起点下游dist距离处的点及分段序号, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离, 像素单位  
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点, 默认单位: 像素  
[ out ] outIndex: 中心线起点向前延伸dist距离后所在分段序号
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  


举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            // 获取路网子接口
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    // 获取当前连接段的所有车道连接
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        // 创建用于存储结果的Point对象和分段序号引用
                        Point outPoint = new Point();
                        ObjInt outIndex = new ObjInt(0);

                        // 调用getPointAndIndexByDist方法（像素单位）
                        boolean result = laneConnector.getPointAndIndexByDist(50, outPoint, outIndex);
                        if (result) {
                            System.out.printf("id为%d的连接段的车道连接%d的中心线起点向前延伸50像素后所在点为(%f, %f), 分段序号为%d%n",
                                    connector.id(), laneConnector.id(), outPoint.getX(), outPoint.getY(), outIndex.getValue());
                        }

                        // 调用getPointAndIndexByDist方法（米制单位）
                        Point outPointMeter = new Point();
                        ObjInt outIndexMeter = new ObjInt();
                        boolean resultMeter = laneConnector.getPointAndIndexByDist(50, outPointMeter, outIndexMeter, UnitOfMeasure.Metric);
                        if (resultMeter) {
                            System.out.printf("id为%d的连接段的车道连接%d的中心线起点向前延伸50米后所在点为(%f, %f), 分段序号为%d%n",
                                    connector.id(), laneConnector.id(), outPointMeter.getX(), outPointMeter.getY(), outIndexMeter.getValue());
                        }
                    }
                }
            }
        }
```



 **boolean getPointByDist(double dist, Point outPoint, UnitOfMeasure unit);**

求中心线起始点下游dist距离处的点, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向前延伸的距离, 像素单位  
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点, 默认单位: 像素 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```java
// 求中心线起始点下游dist距离处的点
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    List<ILaneConnector> laneConnectors = connector.laneConnectors();
                    for (ILaneConnector laneConnector : laneConnectors) {
                        Point outPoint = new Point();
                        if (laneConnector.getPointByDist(50, outPoint)) {
                            System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线起点向前延伸dist距离后所在点为" + outPoint);
                        }
                        if (laneConnector.getPointByDist(50, outPoint,UnitOfMeasure.Metric)) {
                            System.out.println("id为" + connector.id() + "的连接段的车道连接" + laneConnector.id() + "的中心线起点向前延伸dist米距离后所在点为" + outPoint);
                        }

                    }
                }
            }
        }
```



 **void setOtherAttr(JsonObject attr);**

设置车道连接其它属性, 方便二次开发过程中使用

举例: 

```java
// 设置车道连接其它属性
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            List<ILaneConnector> laneConnectors = connector.laneConnectors();
            for (ILaneConnector laneConnector : laneConnectors) {
                JsonObjectBuilder builder = Json.createObjectBuilder();
                builder.add("newAttr", "add a new attr");
                JsonObject otherAttr = builder.build();
               
                // 调用设置属性的方法
                laneConnector.setOtherAttr(otherAttr);
            }
        }
    }
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    // 获取路网子接口
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 调用方法展示连接段属性
        showConnectorAttr(netiface);
    }
}
 
public static void showLaneConnectorAttr(NetInterface netiface) {
        // 获取第一个连接段（通过ID查找）
        List<Long> connectorIds = netiface.connectorIds();
        if (connectorIds == null || connectorIds.isEmpty()) {
            System.out.println("未找到有效的连接段ID");
            return;
        }
        IConnector connector = netiface.findConnector(connectorIds.get(0));
        if (connector == null) {
            System.out.println("未找到ID对应的连接段");
            return;
        }

        // 获取第一个车道连接段
        List<ILaneConnector> laneConnectors = connector.laneConnectors();
        if (laneConnectors == null || laneConnectors.isEmpty()) {
            System.out.println("当前连接段无车道连接段");
            return;
        }
        ILaneConnector laneConnector = laneConnectors.get(0);

        // 构建自定义属性（JSON格式）
        JsonObject otherAttr = Json.createObjectBuilder()
                .add("newAttr", "add a new attr")
                .build();
        // 执行设置操作（返回void的方法单独调用）
        laneConnector.setOtherAttr(otherAttr);

        // 预获取断点集（避免重复调用，提升效率）
        List<Point> leftBreakPointsPixel = laneConnector.leftBreakPoints();
        List<Point> leftBreakPointsMetric = laneConnector.leftBreakPoints(UnitOfMeasure.Metric);
        List<Point> centerBreakPointsPixel = laneConnector.centerBreakPoints();
        List<Point> centerBreakPointsMetric = laneConnector.centerBreakPoints(UnitOfMeasure.Metric);

        // 输出车道连接段基础属性
        System.out.printf(
                "===laneConnector id=%d, 类型gtype=%s, %n" +
                        "其所属的连接段=%s, 所属section=%s, %n" +
                        "fromLane=%s, toLane=%s, %n" +
                        "fromLaneObject=%s, toLaneObject=%s, %n" +
                        "长度length（像素制）=%.2f, 米制=%.2f, %n" +
                        "centerBreakPoints(像素制)=%s, centerBreakPoints(米制)=%s, %n" +
                        "leftBreakPoints(像素制)=%s, leftBreakPoints(米制)=%s, %n" +
                        "rightBreakPoints(像素制)=%s, rightBreakPoints(米制)=%s, %n" +
                        "centerBreakPoint3Ds(像素制)=%s, centerBreakPoint3Ds(米制)=%s, %n" +
                        "leftBreakPoint3Ds(像素制)=%s, leftBreakPoint3Ds(米制)=%s, %n" +
                        "rightBreakPoint3Ds(像素制)=%s, rightBreakPoint3Ds(米制)=%s, %n",
                laneConnector.id(),
                laneConnector.gtype(),
                laneConnector.connector(),
                laneConnector.section(),
                laneConnector.fromLane(),
                laneConnector.toLane(),
                laneConnector.fromLaneObject(),
                laneConnector.toLaneObject(),
                laneConnector.length(),
                laneConnector.length(UnitOfMeasure.Metric),  // 米制
                centerBreakPointsPixel,
                centerBreakPointsMetric,
                leftBreakPointsPixel,
                leftBreakPointsMetric,
                laneConnector.rightBreakPoints(),
                laneConnector.rightBreakPoints(UnitOfMeasure.Metric),  // 米制
                laneConnector.centerBreakPoint3Ds(),
                laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric),  // 米制
                laneConnector.leftBreakPoint3Ds(),
                laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric),  // 米制
                laneConnector.rightBreakPoint3Ds(),
                laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric)   // 米制
        );

        // 输出部分断点集及距离信息（处理可能的空集合）
        System.out.printf(
                "leftBreak3DsPartly(像素制)=%s, %n" +
                        "leftBreak3DsPartly(米制)=%s, %n" +
                        "rightBreak3DsPartly(像素制)=%s, %n" +
                        "rightBreak3DsPartly(米制)=%s, %n" +
                        "distToStartPoint(像素制)=%.2f, distToStartPoint(米制)=%.2f, %n" +
                        "设置自定义属性setOtherAttr=成功%n",
                // 左侧部分断点（像素制）：需至少2个断点
                (leftBreakPointsPixel.size() >= 2) ? laneConnector.leftBreak3DsPartly(leftBreakPointsPixel.get(1), leftBreakPointsPixel.get(leftBreakPointsPixel.size() - 1)) : "断点不足",
                // 左侧部分断点（米制）
                (leftBreakPointsMetric.size() >= 2) ? laneConnector.leftBreak3DsPartly(leftBreakPointsMetric.get(1), leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1), UnitOfMeasure.Metric) : "断点不足",
                // 右侧部分断点（像素制）
                (leftBreakPointsPixel.size() >= 2) ? laneConnector.rightBreak3DsPartly(leftBreakPointsPixel.get(1), leftBreakPointsPixel.get(leftBreakPointsPixel.size() - 1)) : "断点不足",
                // 右侧部分断点（米制）
                (leftBreakPointsMetric.size() >= 2) ? laneConnector.rightBreak3DsPartly(leftBreakPointsMetric.get(1), leftBreakPointsMetric.get(leftBreakPointsMetric.size() - 1), UnitOfMeasure.Metric) : "断点不足",
                // 到起点的距离（像素制）：需至少1个断点
                (centerBreakPointsPixel.size() >= 1) ? laneConnector.distToStartPoint(centerBreakPointsPixel.get(0)) : 0.0,
                // 到起点的距离（米制）
                (centerBreakPointsMetric.size() >= 1) ? laneConnector.distToStartPoint(centerBreakPointsMetric.get(0), UnitOfMeasure.Metric) : 0.0
        );

        // 1. 调用getPointAndIndexByDist方法（获取指定距离的点及分段序号）
        // 像素制
        Point outPoint = new Point();
        ObjInt outIndex = new ObjInt(0);
        laneConnector.getPointAndIndexByDist(2.0, outPoint, outIndex);
        // 米制
        Point outPoint1 = new Point();
        ObjInt outIndex1 = new ObjInt(0);
        laneConnector.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric);
        // 输出结果
        System.out.printf(
                "getPointAndIndexByDist(像素制)=(%d, %d), 分段序号=%d; getPointAndIndexByDist(米制)=(%d, %d), 分段序号=%d%n",
                outPoint.getX(), outPoint.getY(), outIndex.getValue(),
                outPoint1.getX(), outPoint1.getY(), outIndex1.getValue()
        );

        // 2. 调用getPointByDist方法（获取指定距离的点）
        // 像素制
        Point outPoint2 = new Point();
        laneConnector.getPointByDist(2.0, outPoint2);
        // 米制
        Point outPoint3 = new Point();
        laneConnector.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric);
        // 输出结果
        System.out.printf(
                "getPointByDist(像素制)=(%d, %d); getPointByDist(米制)=(%d, %d)%n",
                outPoint2.getX(), outPoint2.getY(),
                outPoint3.getX(), outPoint3.getY()
        );

        // 3. 调用distToStartPointWithSegmIndex方法（获取点到起点的距离及分段序号）
        // 像素制
        double dist = laneConnector.distToStartPointWithSegmIndex(outPoint, 0);
        // 米制
        double dist1 = laneConnector.distToStartPointWithSegmIndex(outPoint1, 0,false);
        // 输出结果
        System.out.printf(
                "distToStartPointWithSegmIndex(像素制)=%.2f; distToStartPointWithSegmIndex(米制)=%.2f%n",
                dist, dist1
        );
    }
```





### 2.8. IConnectorArea

面域接口, 方法如下: 

 **int id();**

获取面域ID, 面域是指若干Connector重叠形成的区域

举例: 

```java
// 获取面域ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnectorArea
        List<IConnectorArea> lConnectorAreas = netiface.allConnectorArea();
        for (IConnectorArea connectorArea : lConnectorAreas) {
            System.out.println("面域ID为" + connectorArea.id());
        }
    }
}
```

 **ArrayList<IConnector> allConnector();**

获取当前面域包含的所有连接段, 返回类型列表, 元素为IConnector对象

举例: 

```java
// 获取当前面域包含的所有连接段
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnectorArea
                List<IConnectorArea> lConnectorAreas = netiface.allConnectorArea();
                for (IConnectorArea connectorArea : lConnectorAreas) {
                    List<IConnector> lConnectors = connectorArea.allConnector();
                    for (IConnector connector : lConnectors) {
                        System.out.println("id为" + connector.id() + "的连接段的面域ID为" + connectorArea.id());
                    }
                }
            }
        }
```

 **Point centerPoint(UnitOfMeasure unit);**

获取面域中心点, 默认单位: 像素; 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 举例: 

```java
// 获取面域中心点
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnectorArea
                List<IConnectorArea> lConnectorAreas = netiface.allConnectorArea();
                for (IConnectorArea connectorArea : lConnectorAreas) {
                    System.out.println("id为" + connectorArea.id() + "的面域中心点为" + connectorArea.centerPoint());
                    System.out.println("id为" + connectorArea.id() + "的面域中心点为" + connectorArea.centerPoint(UnitOfMeasure.Metric));
                }
            }
        }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            // 代表TESS NG的路网子接口
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                showConnectorAreaAttr(netiface);
            }
        }

private static void showConnectorAreaAttr(NetInterface netiface) {
        List<IConnectorArea> allConnectorArea = netiface.allConnectorArea();
        if (allConnectorArea.isEmpty()) {
            return;
        }
        IConnectorArea connectorArea = allConnectorArea.get(0);
        IConnectorArea connectorArea1 = netiface.findConnectorArea(connectorArea.id());
        System.out.println(
                "===场景中的connectorArea个数=" + allConnectorArea.size() + ", 第一个connectorArea的属性=" + connectorArea.id()
        );

    }
```





### 2.9. IDispatchPoint

发车点接口, 方法如下: 

 **int id();**

获取发车点ID

举例: 

```java
// 获取发车点ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDispatchPoint
        List<IDispatchPoint> lDispatchPoints = netiface.dispatchPoints();
        for (IDispatchPoint dispatchPoint : lDispatchPoints) {
            System.out.println("发车点ID为" + dispatchPoint.id());
        }
    }
}
```

 **String name();**

获取发车名称

举例: 

```java
// 获取发车名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDispatchPoint
        List<IDispatchPoint> lDispatchPoints = netiface.dispatchPoints();
        for (IDispatchPoint dispatchPoint : lDispatchPoints) {
            System.out.println("发车点名称=" + dispatchPoint.name());
        }
    }
}
```

 **ILink link();**

获取发车点所在路段

举例: 

```java
// 获取发车点所在路段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDispatchPoint
        List<IDispatchPoint> lDispatchPoints = netiface.dispatchPoints();
        for (IDispatchPoint dispatchPoint : lDispatchPoints) {
            ILink link = dispatchPoint.link();
            if (link != null) {
                System.out.println("发车点所在路段ID=" + link.id());
            }
        }
    }
}
```

 **int addDispatchInterval(int vehiCompId, int interval, int vehiCount);**

为发车点增加发点间隔

参数: 
[ in ] vehiCompId: 车型组成ID
[ in ] interval: 时间段, 单位: 秒
[ in ] vehiCount: 发车数

返回值: 

返回发车间隔ID

举例: 

```java
// 新建发车点, 车型组成ID为动态创建的, 600秒发300辆车

    if (link != null) {
        IDispatchPoint dp = netiface.createDispatchPoint(link );
        if (dp != null) {
            // vehiCompositionID为动态创建的车型组成ID
            dp.addDispatchInterval(vehiCompositionID, 600, 300);
        }
    }

```

 **Vector<Point> polygon();**

获取发车点多边型轮廓

举例: 

```java
// 获取发车点多边型轮廓
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IDispatchPoint
                List<IDispatchPoint> lDispatchPoints = netiface.dispatchPoints();
                for (IDispatchPoint dispatchPoint : lDispatchPoints) {
                    System.out.print("发车点多边型轮廓=[" );
                    dispatchPoint.polygon().forEach(s->System.out.print("("+s.getX()+","+s.getY()+")"));
                    System.out.println("]");
                }
            }
        }
```


**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    // 代表TESS NG的路网子接口
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showDispatchPointAttr(netiface);
    }
}

private static void showDispatchPointAttr(NetInterface netiface) {
    List<IDispatchPoint> dispatchPoints = netiface.dispatchPoints();
    if (dispatchPoints.isEmpty()) {
        return;
    }
    IDispatchPoint dispatchPoint = dispatchPoints.get(0);
    IDispatchPoint connectorArea1 = netiface.findDispatchPoint(dispatchPoint.id());
    System.out.println(
        "===场景中的dispatchPoint个数=" + dispatchPoints.size() + ", 第一个dispatchPoint的属性=" + dispatchPoint.id()
    );
    ILink link = dispatchPoint.link();
    String linkName = (link != null) ? link.name() : "未知路段";
    System.out.println(
        "该dispatchPoint的属性: id=" + dispatchPoint.id() + ", dispatchPoint name =" + dispatchPoint.name() + ", "
        + "所在路段名称=" + linkName + ", polygon=" + dispatchPoint.polygon()
    );
}
```





### 2.10. IDecisionPoint

决策点接口, 方法如下: 

 **int id();**

获取决策点ID

举例: 

```java
// 获取决策点ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDecisionPoint
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            System.out.println("决策点ID为" + decisionPoint.id());
        }
    }
}
```

 **String name( );**

获取决策点名称

举例: 

```java
// 获取决策点名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDecisionPoint
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            System.out.println("决策点" + decisionPoint.id() + "的名称=" + decisionPoint.name());
        }
    }
}
```

 **ILink link();**

获取决策点所在路段

举例: 

```java
// 获取决策点所在路段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDecisionPoint
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            ILink link = decisionPoint.link();
            if (link != null) {
                System.out.println("决策点" + decisionPoint.id() + "所在路段ID=" + link.id());
            }
        }
    }
}
```

 **double distance(UnitOfMeasure unit);**

获取距路段起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取距路段起点距离
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IDecisionPoint
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    System.out.println("决策点" + decisionPoint.id() + "距路段起点距离=" + decisionPoint.distance());
                    System.out.println("决策点" + decisionPoint.id() + "距路段起点距离(米)=" + decisionPoint.distance(UnitOfMeasure.Metric));
                }
            }
        }
```

 **ArrayList<IRouting> routings();**

获取决策点控制的所有决策路径, 返回类型列表, 元素为IRouting对象

举例: 

```java
// 获取决策点控制的所有决策路径
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDecisionPoint
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            List<IRouting> lRoutings = decisionPoint.routings();
            for (IRouting routing : lRoutings) {
                System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id());
            }
        }
    }
}
```

 **Vector<Point> polygon();**

获取决策点多边型轮廓

举例: 

```java
// 获取决策点多边型轮廓
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IDecisionPoint
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            System.out.println("决策点" + decisionPoint.id() + "的多边型轮廓=" + decisionPoint.polygon());
        }
    }
}
```


**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            // 代表TESS NG的路网子接口
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                showDecisionPointAttr(netiface);
            }
        }

private static void showDecisionPointAttr(NetInterface netiface) {
        List<IDecisionPoint> decisionPoints = netiface.decisionPoints();
        if (decisionPoints.isEmpty()) {
            return;
        }
        IDecisionPoint decisionPoint = decisionPoints.get(0);
        IDispatchPoint decisionPoint1 = netiface.findDispatchPoint(decisionPoint.id());

        System.out.println(
                "===场景中的decisionPoint个数=" + decisionPoints.size() + ", 第一个decisionPoint的属性=" + decisionPoint.id()
        );

        ILink link = decisionPoint.link();
        String linkName = (link != null) ? link.name() : "未知路段";

        // 构建决策路径ID及包含路段名称的字符串
        StringBuilder routingInfo = new StringBuilder();
        List<IRouting> routings = decisionPoint.routings();
        for (IRouting routing : routings) {
            routingInfo.append("(").append(routing.id()).append(", [");
            List<ILink> routingLinks = routing.getLinks();
            for (int i = 0; i < routingLinks.size(); i++) {
                routingInfo.append(routingLinks.get(i).name());
                if (i < routingLinks.size() - 1) {
                    routingInfo.append(", ");
                }
            }
            routingInfo.append("]), ");
        }
        if (routingInfo.length() > 0) {
            routingInfo.setLength(routingInfo.length() - 2); // 移除末尾多余的逗号和空格
        }

        System.out.println(
                "该decisionPoint的属性: id=" + decisionPoint.id() + ", dispatchPoint name =" + decisionPoint.name() + ", "
                        + "所在路段名称=" + linkName + ", 距离路段起点距离distance(像素制)=" + decisionPoint.distance() + ", 米制=" + decisionPoint.distance(UnitOfMeasure.Metric) + ", "
                        + "控制的决策路径id=" + routingInfo + ", "
                        + "polygon=" + decisionPoint.polygon() 
        );
    }
```





### 2.11. IRouting

路径接口, 方法如下: 

 **int id();**

获取路径ID

举例: 

```java
// 获取路径ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            // 获取决策点控制的所有决策路径
            List<IRouting> lRoutings = decisionPoint.routings();
            for (IRouting routing : lRoutings) {
                System.out.println("决策点" + decisionPoint.id() + "的决策路径ID为" + routing.id());
            }
        }
    }
}
```

 **double calcuLength(UnitOfMeasure unit);**

计算路径长度, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    List<IRouting> lRoutings = decisionPoint.routings();
                    for (IRouting routing : lRoutings) {
                        System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "长度=" + routing.calcuLength());
                        System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "长度(米)=" + routing.calcuLength(UnitOfMeasure.Metric));
                    }
                }
            }
        }
```

 **ArrayList<ILink> getLinks();**

获取当前路径的路段序列, 不包含连接段

举例: 

```java
// 获取当前路径的路段序列
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    List<IRouting> lRoutings = decisionPoint.routings();
                    for (IRouting routing : lRoutings) {
                        List<ILink> links = routing.getLinks();
                        for (ILink link : links) {
                            System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "的路段序列=" + link.name());
                        }
                    }
                }
            }
        }
```

 **int deciPointId();**

获取当前路径所属的决策点ID

举例: 

```java
// 获取当前路径所属的决策点ID
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    List<IRouting> lRoutings = decisionPoint.routings();
                    for (IRouting routing : lRoutings) {
                        System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "所属的决策点ID=" + routing.deciPointId());
                    }
                }
            }
        }
```

 **boolean contain(ISection pRoad);**

判定道路是否在当前路径上, 入参需是ISection对象

参数：
[ in ] pRoad: 路段或连接段

举例: 

```java
// 判定道路是否在当前路径上
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            List<IRouting> lRoutings = decisionPoint.routings();
            for (IRouting routing : lRoutings) {
                List<ILink> links = routing.getLinks();
                if (!links.isEmpty()) {
                    System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "判断道路是否在当前路径上=" + routing.contain(links.get(0).toSection()));
                }
            }
        }
    }
}
```

 **ISection nextRoad(ISection pRoad);**

根据当前路径, 获取所给道路的下一条道路, 返回类型为ISection, 即下一条路段可能是Link也可能是Connector

参数: 

[ in ] pRoad: 路段或连接段

举例: 

```java
// 根据当前路径, 获取所给道路的下一条道路
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
        for (IDecisionPoint decisionPoint : lDecisionPoints) {
            List<IRouting> lRoutings = decisionPoint.routings();
            for (IRouting routing : lRoutings) {
                List<ILink> links = routing.getLinks();
                if (!links.isEmpty()) {
                    System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "根据当前路径, 获取所给道路的下一条道路=" + routing.nextRoad(links.get(0).toSection()));
                }
            }
        }
    }
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    // 代表TESS NG的路网子接口
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showRoutingAttr(netiface);
    }
}

private static void showRoutingAttr(NetInterface netiface) {
        List<IDecisionPoint> decisionPoints = netiface.decisionPoints();
        if (decisionPoints.isEmpty()) {
            return;
        }
        IDecisionPoint decisionPoint = decisionPoints.get(0);
        List<IRouting> routes = decisionPoint.routings();
        if (routes.isEmpty()) {
            return;
        }
        IRouting route = routes.get(0);
        IRouting route1 = netiface.findRouting(route.id());

        // 构建路径包含路段名称的字符串
        StringBuilder linkNames = new StringBuilder();
        List<ILink> links = route.getLinks();
        for (int i = 0; i < links.size(); i++) {
            linkNames.append(links.get(i).name());
            if (i < links.size() - 1) {
                linkNames.append(", ");
            }
        }

        System.out.println(
                "该route的属性: id=" + route.id() + ", "
                        + "route 长度 calcuLength (像素制)=" + route.calcuLength() +   ", "
                        + "所属决策点id=" + route.deciPointId() + ", 路径上的links=[" + linkNames + "]"
        );

        if (!links.isEmpty()) {
            System.out.println("判断link是否在路径上=" + route.contain(links.get(0).toSection()));
            System.out.println("获取路径上, 指定link的下游道路, 返回值可能是link也可能是connector=" + route.nextRoad(links.get(0).toSection()));
        }

        List<ILink> links1 = netiface.links();
        if (!links1.isEmpty()) {
            System.out.println("判断link是否在路径上=" + route.contain(links1.get(0).toSection()));
            if (route.contain(links1.get(0).toSection())) {
                System.out.println("获取路径上, 指定link的下游道路, 返回值可能是link也可能是connector=" + route.nextRoad(links1.get(0).toSection()));
            }
        }
    }
```



### 2.12. ISignalController

信号机接口

 **ArrayList<Long> signalControllerIds();**

获取信控机ID 

举例: 

```java
# 获取信控机ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<Long> lSignalControllerIds= netiface.signalControllerIds();
        for(long signalControllerId : lSignalControllerIds){
            System.out.println("信控机ID为"+signalControllerId);
        }
    }
}
```

 **String name();**

获取信控机名称

举例: 

```java
# 获取信控机名称
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<Long> lSignalControllerIds= netiface.signalControllerIds();
        for(long signalControllerId : lSignalControllerIds){
            System.out.println("信控机 "+signalControllerId+"名称="+netiface.findSignalControllerById(signalControllerId).name());
        }
    }
}
```

 **void setName(String name);**

设置信控机名称  

参数: 
[ in ] name: 信号机名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<Long> lSignalControllerIds= netiface.signalControllerIds();
            for(long signalControllerId : lSignalControllerIds){
                ISignalController signalController =  netiface.findSignalControllerById(signalControllerId);
                signalController.setName("new_name");
                System.out.println("设置信控机"+signalControllerId+"名称="+signalController.name());
            }
        }
    }
```

 **void addPlan(ISignalPlan plan);**

 为信号机添加信控方案  

参数: 
[ in ] plan : 信控方案, 可循环调用设置多时段信控方案

举例: 

```java
# 为信号机添加信控方案
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<Long> lSignalControllerIds= netiface.signalControllerIds();
            for(long signalControllerId : lSignalControllerIds){
                ISignalController signalController =  netiface.findSignalControllerById(signalControllerId);
                ArrayList<ISignalPlan>  signalPlans= signalController.plans();
                System.out.println("为信号机"+signalControllerId+"添加信控方案" );
                signalController.addPlan(signalPlans.get(0));
            }
        }
    }
```

 **void removePlan(ISignalPlan plan);**

 移除/删除信号机的信控方案  

参数: 
[ in ] plan : 信控方案, 

举例: 

```java
//移除/删除信号机的信控方案
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    List<Long> lSignalControllerIds= netiface.signalControllerIds();
    for(long signalControllerId : lSignalControllerIds){
        ISignalController signalController =  netiface.findSignalControllerById(signalControllerId);
        ArrayList<ISignalPlan>  signalPlans= signalController.plans();
        System.out.println("移除/删除信号机"+signalControllerId+"的信控方案" );
        signalController.removePlan(signalPlans.get(0));
    }
}
}
```

 **ArrayList<ISignalPlan> plans();**

 获取当前信号机中所有的信控方案  

举例: 

```java
// 获取当前信号机中所有的信控方案
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<Long> lSignalControllerIds= netiface.signalControllerIds();
        for(long signalControllerId : lSignalControllerIds){
            ISignalController signalController =  netiface.findSignalControllerById(signalControllerId);
            System.out.println("获取当前信号机"+signalControllerId+"中所有的信控方案"+signalController.plans() );
        }
    }
}
```


**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    ISignalController signalController = netiface.createSignalController("交叉口1");

    ISignalPlan signalPlan = netiface.createSignalPlan(signalController, "早高峰", 150, 50, 0, 1800);

    ColorInterval green = new ColorInterval("绿", 50);
    ColorInterval yellow = new ColorInterval("黄", 3);
    ColorInterval red = new ColorInterval("红", 97);
    ArrayList<ColorInterval>   w_e_straight_phasecolor = new ArrayList<>
        (Arrays.asList(green, yellow, red));
    ISignalPhase w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor);
    ISignalPhase we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor);

    Point startPoint = new Point(-300, 6);
    Point endPoint = new Point(-25, 6);
    ArrayList<Point> lPoint = new ArrayList<>(Arrays.asList(startPoint, endPoint));

    ILink w_approach = netiface.createLink(lPoint,3,"西进口",true,UnitOfMeasure.Metric);

    Point3D startPoint1 = new Point3D(300, -6, 0);
    Point3D endPoint1 = new Point3D(25, -6, 0);
    ArrayList<Point3D> lPoint1 = new ArrayList<>(Arrays.asList(startPoint1, endPoint1));
    ILink e_approach = netiface.createLink3D(lPoint1,3,"西进口",true,UnitOfMeasure.Metric);

    java.util.ArrayList<ISignalLamp> w_e_straight_lamps = new java.util.ArrayList<>();
    for (ILane lane : w_approach.lanes()) {
        if (lane.number() < w_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_straight_lamps.add(signalLamp);
        }
    }
    for (ILane lane : e_approach.lanes()) {
        if (lane.number() < e_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_straight_lamps.add(signalLamp);
        }
    }

    // 展示信号控制器属性
    showSignalControllerAttr(netiface);
}
}
private static void showSignalControllerAttr(NetInterface netiface) {
        int controllerCount = netiface.signalControllerCount();
        
        List<Long> signalControllerIds = netiface.signalControllerIds();
        ISignalController controller = netiface.findSignalControllerById(signalControllerIds.get(0));
        System.out.println("路网中的信号机总数=" + controllerCount + ", 所有的信号机id列表=" + signalControllerIds + ", 信号机编号=" + signalControllerIds.get(0) + "的具体信息: "
                + "编号=" + controller.id() + ", 名称=" + controller.name());
        controller.setName("new_name");
        System.out.println(", 设置新名字=new_" + controller.name()+ ", "+ "获取信号机的信控方案=" + controller.plans() );
        List<Long> lsignalPlanIds= netiface.signalPlanIds();
        for(long signalControllerId : signalControllerIds){
            ISignalController signalController =  netiface.findSignalControllerById(signalControllerId);
            System.out.println("移除/删除信号机"+signalControllerId+"的信控方案" );
            signalController.removePlan(signalController.plans().get(0));
        }
    }
```



### 2.13. ISignalPlan

信号控制方案接口

 **long id();**

获取信控方案ID 

举例: 

```java
// 获取当前信号机中所有的信控方案
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<ISignalController> lSignalControllers = netiface.signalControllers();
            for (ISignalController signalController : lSignalControllers) {
                List<ISignalPlan> plans = signalController.plans();
                for (ISignalPlan signalPlan : plans) {
                    System.out.println("信控方案ID=" + signalPlan.id());
                }
            }
        }
    }
```

 **String name();**

获取信控方案名称（V3版本的信号灯组名称） 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    List<ISignalController> lSignalControllers = netiface.signalControllers();
    for (ISignalController signalController : lSignalControllers) {
        List<ISignalPlan> plans = signalController.plans();
        for (ISignalPlan signalPlan : plans) {
            System.out.println("信控方案名称=" + signalPlan.name());
        }
    }
}
}
```

 **String trafficName();**

获取信号机名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                System.out.println("信号机名称=" + signalPlan.trafficName());
            }
        }
    }
}
```

 **int cycleTime();**

获取获取信号周期, 单位: 秒 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    List<ISignalController> lSignalControllers = netiface.signalControllers();
    for (ISignalController signalController : lSignalControllers) {
        List<ISignalPlan> plans = signalController.plans();
        for (ISignalPlan signalPlan : plans) {
            System.out.println("信控方案周期=" + signalPlan.cycleTime());
        }
    }
}
}
```

 **long fromTime();**

获取信控方案起始时间, 单位: 秒 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                        for (ISignalController signalController : lSignalControllers) {
                            List<ISignalPlan> plans = signalController.plans();
                            for (ISignalPlan signalPlan : plans) {
                                System.out.println("信控方案起始时间=" + signalPlan.fromTime());
                            }
                        }
                    }
                }
```

 **long toTime();**

获取信控方案结束时间, 单位: 秒 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                System.out.println("信控方案结束时间=" + signalPlan.toTime());
            }
        }
    }
}
```

 **ArrayList<ISignalPhase> phases();**

获取信控方案中的相位列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                System.out.println("信控方案中的相位列表=" + signalPlan.phases());
            }
        }
    }
}
```

 **void setName(String name);**

设置信控方案（V3版本的信号灯组）名称 

参数: 

[ in ] name: 信控方案名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                signalPlan.setName("new_" + signalPlan.name());
                System.out.println("获取信控方案名称=" + signalPlan.name());
            }
        }
    }
}
```

 **void setCycleTime(int period);**

设置信控方案（V3版本的信号灯组）的信号周期, 单位: 秒 

参数: 

[ in ] period: 信号周期, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                signalPlan.setCycleTime(100); // 执行设置
                System.out.println("获取信控方案周期=" + signalPlan.cycleTime());
            }
        }
    }
}
```

 **void setFromTime(long time);**

设置信控方案（V3版本的信号灯组）起作用时段的起始时间, 单位: 秒 

参数: 

[ in ] time: 起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                signalPlan.setFromTime(100); // 执行设置
                System.out.println("获取信控方案起始时间=" + signalPlan.fromTime());
            }
        }
    }
}
```

 **void setToTime(long time);**

设置信控方案（V3版本的信号灯组）起作用时段的结束时间, 单位: 秒

参数: 

[ in ] time: 结束时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                signalPlan.setToTime(100); // 执行设置
                System.out.println("获取信控方案结束时间=" + signalPlan.toTime());
            }
        }
    }
}
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
    // 1. 获取信号控制器（此处取第一个信号控制器作为示例）
    List<ISignalController> signalControllers = netiface.signalControllers();
    if (signalControllers == null || signalControllers.isEmpty()) {
        System.out.println("无可用信号控制器，无法创建信控方案");
        break;
    }
    ISignalController signalController = signalControllers.get(0);
    
    // 2. 创建信控方案（参数对应：信号控制器、名称、周期、基础绿灯时长、偏移量、生效时长）
    ISignalPlan signalPlan = netiface.createSignalPlan(
            signalController,
            "早高峰",  // 信控方案名称
            150,       // 周期（150秒）
            50,        // 基础绿灯时长（50秒）
            0,         // 偏移量
            1800       // 生效时长（1800秒）
    );
    if (signalPlan == null) {
        System.out.println("信控方案创建失败");
        break;
    }
    System.out.println("信控方案创建成功：ID=" + signalPlan.id() + "，名称=" + signalPlan.name());
    
    // 3. 创建灯色周期（绿-黄-红）
    ColorInterval green = new ColorInterval("绿", 50);   // 绿灯50秒
    ColorInterval yellow = new ColorInterval("黄", 3);  // 黄灯3秒
    ColorInterval red = new ColorInterval("红", 97);     // 红灯97秒
    ArrayList<ColorInterval> w_e_straight_phasecolor = new ArrayList<>(Arrays.asList(green, yellow, red));  // 灯色序列
    
    // 4. 创建相位（东西直行机动车相位）
    ISignalPhase w_e_straight_phase = netiface.createSignalPlanSignalPhase(
            signalPlan,        // 所属信控方案
            "东西直行",        // 相位名称
            w_e_straight_phasecolor  // 关联灯色周期
    );
    // 创建行人相位（东西直行行人相位，复用灯色周期）
    ISignalPhase we_ped_phase = netiface.createSignalPlanSignalPhase(
            signalPlan,
            "东西直行行人",
            w_e_straight_phasecolor
    );
    
    // 输出创建结果
    if (w_e_straight_phase != null && we_ped_phase != null) {
        System.out.println("相位创建成功：");
        System.out.println(" - 东西直行相位：ID=" + w_e_straight_phase.id() + "，名称=" + w_e_straight_phase.phaseName());
        System.out.println(" - 东西直行行人相位：ID=" + we_ped_phase.id() + "，名称=" + we_ped_phase.phaseName());
    } else {
        System.out.println("相位创建失败");
    }
    showSignalPlanAttr(netiface);
    
    }
    }
    private  static  void showSignalPlanAttr(NetInterface netiface){
        List<ISignalPlan> signalPlans = netiface.signalPlans();
        int signalPlanCount = netiface.signalPlanCount();
        List<Long> signalPlanIds = netiface.signalPlanIds();

        if (signalPlanIds != null && !signalPlanIds.isEmpty()) {
            ISignalPlan signalPlan = netiface.findSignalPlanById(signalPlanIds.get(0));
            if (signalPlans != null && !signalPlans.isEmpty()) {
                signalPlan = netiface.findSignalPlanByName(signalPlans.get(0).name());
            }

            if (signalPlan != null) {
                // 单独执行名称设置
                String originalName = signalPlan.name();
                signalPlan.setName("new_" + originalName);

                // 打印详细属性
                System.out.printf(
                        "路网中的信控方案总数=%d, 所有信控方案列表=%s, 信控方案编号=%d的具体信息: " +
                                "编号=%d, 名称=%s, 所属信号机名称=%s, " +
                                "获取信控方案信控周期=%d, 开始时间-结束时间=%d-%d, 所有相位信息=%s%n",
                        signalPlanCount,
                        signalPlanIds,
                        signalPlanIds.get(0),
                        signalPlan.id(),
                        signalPlan.name(),
                        signalPlan.trafficName(),
                        signalPlan.cycleTime(),
                        signalPlan.fromTime(),
                        signalPlan.toTime(),
                        signalPlan.phases()
                );
            }
        }
    }
```



### 2.14. ISignalPhase

信号灯相位, 接口方法: 

 **long id();**

获取当前相位的相位ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<ISignalController> lSignalControllers = netiface.signalControllers();
            for (ISignalController signalController : lSignalControllers) {
                List<ISignalPlan> plans = signalController.plans();

                for (ISignalPlan signalPlan : plans) {
                    List<ISignalPhase> phases = signalPlan.phases();
                    for (ISignalPhase signalPhase : phases) {
                        System.out.println("相位ID=" + signalPhase.id());
                    }
                        

                }
            }
        }
    }
```

 **String phaseName();**

获取当前相位的相位名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                        for (ISignalController signalController : lSignalControllers) {
                            List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                List<ISignalPhase> phases = signalPlan.phases();
                                for (ISignalPhase signalPhase : phases) {
                                    System.out.println("相位名称=" + signalPhase.phaseName());
                                }
                            }
                        }
                    }
                }
```

 **ArrayList<ISignalLamp> signalLamps();**

获取本相位下的信号灯列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                        for (ISignalController signalController : lSignalControllers) {
                            List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                List<ISignalPhase> phases = signalPlan.phases();
                                for (ISignalPhase signalPhase : phases) {
                                    System.out.println("本相位下的信号灯列表=" + signalPhase.signalLamps());
                                }
                            }
                        }
                    }
                }
```

 **ArrayList<ColorInterval> listColor();**

获取本相位的相位灯色列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                        for (ISignalController signalController : lSignalControllers) {
                            List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                List<ISignalPhase> phases = signalPlan.phases();
                                for (ISignalPhase signalPhase : phases) {
                                    System.out.println("本相位的相位灯色列表=" + signalPhase.listColor());
                                }
                            }

                        }
                    }
                }
```

 **void setColorList(ArrayList<ColorInterval> lColor);**

设置当前相位的信号灯色信息列表

参数: 

[ in ] lColor: 灯色时长信息, 包含信号灯颜色和信号灯色时长
举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 查找ID为7的相位
        ISignalPhase signalPhaseL127 = netiface.findSignalPhase(7);
        if (signalPhaseL127 != null) {
            // 创建新的灯色列表（红→绿→黄→红）
            ArrayList<ColorInterval> colorList = new java.util.ArrayList<>();
            colorList.add(new ColorInterval("红", 10));
            colorList.add(new ColorInterval("绿", 110));
            colorList.add(new ColorInterval("黄", 3));
            colorList.add(new ColorInterval("红", 28));
            // 设置灯色列表
            signalPhaseL127.setColorList(colorList);
            System.out.println("相位ID=7的灯色列表已修改为：" + signalPhaseL127.listColor());
        } else {
            System.out.println("未找到ID=7的相位");
        }
    }
}

```

 **void setPhaseName(String name);**

设置当前相位名称

参数: 

[ in ] name: 相位名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
NetInterface netiface = iface.netInterface();
if (netiface != null) {
    List<ISignalController> lSignalControllers = netiface.signalControllers();
        for (ISignalController signalController : lSignalControllers) {
            List<ISignalPlan> plans = signalController.plans();
            for (ISignalPlan signalPlan : plans) {
                List<ISignalPhase> phases = signalPlan.phases();
                for (ISignalPhase signalPhase : phases) {
                    // 先设置新名称（setPhaseName返回void，单独执行）
                    signalPhase.setPhaseName("new_" + signalPhase.phaseName());
                    // 再打印结果
                    System.out.println("获取当前相位名称=" + signalPhase.phaseName());
                }
            }
        }
    }
}
```

 **int cycleTime();**

获取相位周期, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                            for (ISignalController signalController : lSignalControllers) {
                                List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                    List<ISignalPhase> phases = signalPlan.phases();
                                    for (ISignalPhase signalPhase : phases) {
                                        System.out.println("相位周期=" + signalPhase.cycleTime());
                                    }
                                }
                            }
                    }
                }
```

 **SignalPhaseColor phaseColor();**

获取当前相位灯色, Online.SignalPhaseColor

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                        
                            for (ISignalController signalController : lSignalControllers) {
                                List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                    List<ISignalPhase> phases = signalPlan.phases();
                                    for (ISignalPhase signalPhase : phases) {
                                        System.out.println("当前相位灯色=" + signalPhase.phaseColor());
                                    }
                                }
                        }
                    }
                }
```

 **ISignalPlan signalPlan();**

获取相位所在信控方案

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> lSignalControllers = netiface.signalControllers();
                            for (ISignalController signalController : lSignalControllers) {
                                List<ISignalPlan> plans = signalController.plans();
                                for (ISignalPlan signalPlan : plans) {
                                    List<ISignalPhase> phases = signalPlan.phases();

                                    for (ISignalPhase signalPhase : phases) {
                                        System.out.println("相位所在信控方案=" + signalPhase.signalPlan());
                                    }
                                }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ISignalController> signalControllers = netiface.signalControllers();
                        if (signalControllers == null || signalControllers.isEmpty()) {
                            System.out.println("无可用信号控制器，无法创建信控方案");
                            break;
                        }
                        ISignalController signalController = signalControllers.get(0);

                        ISignalPlan signalPlan = netiface.createSignalPlan(
                                signalController,
                                "早高峰",  
                                150,      
                                50,       
                                0,     
                                1800       
                        );
                        if (signalPlan == null) {
                            System.out.println("信控方案创建失败");
                            break;
                        }
                        System.out.println("信控方案创建成功：ID=" + signalPlan.id());

                        ColorInterval green = new ColorInterval("绿", 50);  
                        ColorInterval yellow = new ColorInterval("黄", 3);  
                        ColorInterval red = new ColorInterval("红", 97);     
                        ArrayList<ColorInterval> w_e_straight_phasecolor = new ArrayList<>(Arrays.asList(green, yellow, red));  

                        ISignalPhase w_e_straight_phase = netiface.createSignalPlanSignalPhase(
                                signalPlan,
                                "东西直行",
                                w_e_straight_phasecolor
                        );
                        ISignalPhase we_ped_phase = netiface.createSignalPlanSignalPhase(
                                signalPlan,
                                "东西直行行人",
                                w_e_straight_phasecolor
                        );
                        if (w_e_straight_phase != null && we_ped_phase != null) {
                            System.out.println("相位创建成功：东西直行（ID=" + w_e_straight_phase.id() + "）、东西直行行人（ID=" + we_ped_phase.id() + "）");
                        } else {
                            System.out.println("相位创建失败");
                        }
                        showSignalPhaseAttr(netiface);

                    }
                }

private static void showSignalPhaseAttr(NetInterface netiface){
        List<ISignalPlan> signalPlans = netiface.signalPlans();
        if (signalPlans != null && !signalPlans.isEmpty()) {
            ISignalPlan signalPlan = signalPlans.get(0);
            List<ISignalPhase> signalPhases = signalPlan.phases();
            if (signalPhases != null && !signalPhases.isEmpty()) {
                ISignalPhase signalPhase = signalPhases.get(0);
                System.out.printf(
                        "信控方案=%s, 的所有相位列表=%s, 第一相位=%d的具体信息: %n" +
                                "编号=%d, 名称=%s, 本相位下的信号灯列表=%s, %n" +
                                "相位灯色列表=%s, 相位周期(秒)=%d, 当前相位灯色=%s, %n" +
                                "所在信控方案=%s%n",
                        signalPlan.name(),
                        signalPhases,
                        signalPhase.id(),
                        signalPhase.id(),
                        signalPhase.phaseName(),
                        signalPhase.signalLamps(),
                        signalPhase.listColor(),
                        signalPhase.cycleTime(),
                        signalPhase.phaseColor(),
                        signalPhase.signalPlan()
                );
            } else {
                System.out.println("当前信控方案无相位数据");
            }
        } else {
            System.out.println("无信控方案数据");
        }
    }
```





### 2.15. ISignalLamp

信号灯接口, 方法如下: 

 **int id();**

获取信号灯ID

举例: 

```java
// 获取信号灯ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("信号灯ID=" + signalLamp.id());
        }
    }
}
```

 **String color();**

获取信号灯当前信号灯色, "R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例: 

```java
// 获取信号灯当前信号灯色
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("信号灯当前信号灯色=" + signalLamp.color());
        }
    }
}
```

 **String name();**

获取信号灯名称

举例: 

```java
// 获取信号灯名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("信号灯名称=" + signalLamp.name());
        }
    }
}
```

 **void setName(String name);**

设置信号灯名称

举例: 

```java
// 设置并获取信号灯名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            // 处理void方法：先执行设置操作
            String newName = "new_" + signalLamp.name();
            signalLamp.setName(newName);
            System.out.println("设置信号灯名称=执行成功");
            System.out.println("获取信号灯名称=" + signalLamp.name());
        }
    }
}
```

 **void setLampColor(String colorStr);**

设置信号灯颜色

参数: 

[ in ] colorStr: 字符串表达的颜色, 有四种可选, 支持汉字: "红"、"绿"、"黄"、"灰", 也支持字符: "R"、"G"、"Y"、"gray"。

举例: 

```java
// 设置并获取信号灯颜色
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            // 处理void方法：先执行设置操作
            signalLamp.setLampColor("R");
            System.out.println("设置信号灯颜色=执行成功");
            System.out.println("获取信号灯颜色=" + signalLamp.color());
        }
    }
}
```

 **ISignalPhase signalPhase();**

获取当前信号灯所在的相位

举例: 

```java
// 获取当前信号灯所在的相位
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("当前信号灯所在的相位=" + signalLamp.signalPhase());
        }
    }
}
```

 **void setSignalPhase(ISignalPhase pPhase);**

设置信号灯相位

参数: 

[ in ] signalPhase: 信号灯相位

举例: 

```java
// 为信号灯设置相位
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        ISignalPhase signalPhase = netiface.findSignalPhase(2);
        for (ISignalLamp signalLamp : lSignalLamps) {
            if (signalPhase != null) {
                // 处理void方法：先执行设置操作
                signalLamp.setSignalPhase(signalPhase);
                System.out.println("为信号灯设置相位=执行成功");
            }
        }
    }
}
```

 **ISignalPlan signalPlan();**

获取当前信号灯所在的灯组, 这里灯组类似于一个信号机种的某个信控方案

举例: 

```java
// 获取当前信号灯所在的灯组
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("当前信号灯所在的灯组=" + signalLamp.signalPlan());
        }
    }
}
```

 **void setDistToStart(double dist,UnitOfMeasure unit);**

设置信号灯距路段起点距离, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 距离值  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
// 设置并获取信号灯距路段起点距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            signalLamp.setDistToStart(100);
            System.out.println("设置信号灯距路段起点距离=执行成功");
            System.out.println("获取信号灯距路段起点距离=" + signalLamp);
            
            signalLamp.setDistToStart(100,UnitOfMeasure.Metric);
            System.out.println("设置信号灯距路段起点距离=执行成功");
            System.out.println("获取信号灯距路段起点距离(米)=" + signalLamp);
        }
    }
}
```

 **ILaneObject laneObject();**

获取所在车道或车道连接

举例: 

```java
// 获取信号灯所在车道或车道连接
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("所在车道或车道连接=" + signalLamp.laneObject());
        }
    }
}
```

 **Vector<Point> polygon();**

获取信号灯多边型轮廓

举例: 

```java
// 获取信号灯多边型轮廓
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("信号灯多边型轮廓=" + signalLamp.polygon());
        }
    }
}
```

**double angle();**

获取信号灯角度, 正北为0顺时针方向

举例: 

```java
// 获取信号灯角度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            System.out.println("信号灯角度=" + signalLamp.angle());
        }
    }
}
```

**案例代码**

```java
if (iface == null) {
                    System.err.println("无法获取TessInterface实例");
                    return;
                }
                NetInterface netiface = iface.netInterface();
                if (netiface == null) {
                    System.err.println("无法获取NetInterface实例");
                    return;
                }

                ISignalController signalController = netiface.createSignalController("交叉口信号控制器");

                ISignalPlan signalPlan = netiface.createSignalPlan(
                        signalController,
                        "早高峰",
                        150,
                        50,
                        0,
                        1800
                );

                ColorInterval green = new ColorInterval("绿", 50);
                ColorInterval yellow = new ColorInterval("黄", 3);
                ColorInterval red = new ColorInterval("红", 97);
                ArrayList<ColorInterval> weStraightPhaseColors = new ArrayList<>(Arrays.asList(green, yellow, red));

                ISignalPhase weStraightPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "东西直行", weStraightPhaseColors
                );
                ISignalPhase wePedPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "东西直行行人", weStraightPhaseColors
                );

                ColorInterval red1 = new ColorInterval("红", 53);
                ColorInterval greenLeft = new ColorInterval("绿", 30);
                ColorInterval yellowLeft = new ColorInterval("黄", 3);
                ColorInterval red2 = new ColorInterval("红", 64);
                ArrayList<ColorInterval> weLeftPhaseColors = new ArrayList<>(Arrays.asList(red1, greenLeft, yellowLeft, red2));
                ISignalPhase weLeftPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "东西左转", weLeftPhaseColors
                );

                ColorInterval red3 = new ColorInterval("红", 86);
                ColorInterval greenNs = new ColorInterval("绿", 30);
                ColorInterval yellowNs = new ColorInterval("黄", 3);
                ColorInterval red4 = new ColorInterval("红", 31);
                ArrayList<ColorInterval> nsStraightPhaseColors = new ArrayList<>(Arrays.asList(red3, greenNs, yellowNs, red4));
                ISignalPhase nsStraightPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "南北直行", nsStraightPhaseColors
                );
                ISignalPhase nsPedPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "南北直行行人", nsStraightPhaseColors
                );

                ColorInterval red5 = new ColorInterval("红", 119);
                ColorInterval greenNsLeft = new ColorInterval("绿", 29);
                ColorInterval yellowNsLeft = new ColorInterval("黄", 3);
                ArrayList<ColorInterval> nsLeftPhaseColors = new ArrayList<>(Arrays.asList(red5, greenNsLeft, yellowNsLeft));
                ISignalPhase nsLeftPhase = netiface.createSignalPlanSignalPhase(
                        signalPlan, "南北左转", nsLeftPhaseColors
                );

                Point startPoint = new Point(-300, 6);
                Point endPoint = new Point(-25, 6);
                ArrayList<Point> lPoint = new ArrayList<>(Arrays.asList(startPoint, endPoint));
                ILink wApproach = netiface.createLink(lPoint, 3, "西进口道", true, UnitOfMeasure.Metric);

                Point3D startPoint1 = new Point3D(300, -6, 0);
                Point3D endPoint1 = new Point3D(25, -6, 0);
                ArrayList<Point3D> lPoint1 = new ArrayList<>(Arrays.asList(startPoint1, endPoint1));
                ILink eApproach = netiface.createLink3D(lPoint1, 3, "东进口道", true, UnitOfMeasure.Metric);

                Point3D startPoint2 = new Point3D(-6, -300, 0);
                Point3D endPoint2 = new Point3D(-6, -25, 0);
                ArrayList<Point3D> lPoint2 = new ArrayList<>(Arrays.asList(startPoint2, endPoint2));
                ILink nApproach = netiface.createLink3D(lPoint2, 3, "北进口道", true, UnitOfMeasure.Metric);

                Point3D startPoint3 = new Point3D(6, 300, 0);
                Point3D endPoint3 = new Point3D(6, 25, 0);
                ArrayList<Point3D> lPoint3 = new ArrayList<>(Arrays.asList(startPoint3, endPoint3));
                ILink sApproach = netiface.createLink3D(lPoint3, 3, "南进口道", true, UnitOfMeasure.Metric);


                List<ISignalLamp> weStraightLamps = new java.util.ArrayList<>();
                if (wApproach != null) {
                    for (ILane lane : wApproach.lanes()) {
                        if (lane.number() < wApproach.laneCount() - 1 && lane.number() > 0) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    weStraightPhase,
                                    "东西直行信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            weStraightLamps.add(signalLamp);
                        }
                    }
                }
                if (eApproach != null) {
                    for (ILane lane : eApproach.lanes()) {
                        if (lane.number() < eApproach.laneCount() - 1 && lane.number() > 0) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    weStraightPhase,
                                    "东西直行信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            weStraightLamps.add(signalLamp);
                        }
                    }
                }

                List<ISignalLamp> weLeftLamps = new java.util.ArrayList<>();
                if (wApproach != null) {
                    for (ILane lane : wApproach.lanes()) {
                        if (lane.number() == wApproach.laneCount() - 1) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    weLeftPhase,
                                    "东西左转信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            weLeftLamps.add(signalLamp);
                        }
                    }
                }
                if (eApproach != null) {
                    for (ILane lane : eApproach.lanes()) {
                        if (lane.number() == eApproach.laneCount() - 1) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    weLeftPhase,
                                    "东西左转信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            weLeftLamps.add(signalLamp);
                        }
                    }
                }

                List<ISignalLamp> nsStraightLamps = new java.util.ArrayList<>();
                if (nApproach != null) {
                    for (ILane lane : nApproach.lanes()) {
                        if (lane.number() < nApproach.laneCount() - 1 && lane.number() > 0) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    nsStraightPhase,
                                    "南北直行信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            nsStraightLamps.add(signalLamp);
                        }
                    }
                }
                if (sApproach != null) {
                    for (ILane lane : sApproach.lanes()) {
                        if (lane.number() < sApproach.laneCount() - 1 && lane.number() > 0) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    nsStraightPhase,
                                    "南北直行信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            nsStraightLamps.add(signalLamp);
                        }
                    }
                }

                List<ISignalLamp> nsLeftLamps = new java.util.ArrayList<>();
                if (nApproach != null) {
                    for (ILane lane : nApproach.lanes()) {
                        if (lane.number() == nApproach.laneCount() - 1) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    nsLeftPhase,
                                    "南北左转信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            nsLeftLamps.add(signalLamp);
                        }
                    }
                }
                if (sApproach != null) {
                    for (ILane lane : sApproach.lanes()) {
                        if (lane.number() == sApproach.laneCount() - 1) {
                            ISignalLamp signalLamp = netiface.createSignalLamp(
                                    nsLeftPhase,
                                    "南北左转信号灯",
                                    lane.id(),
                                    -1,
                                    lane.length() - 0.5
                            );
                            nsLeftLamps.add(signalLamp);
                        }
                    }
                }

                IPedestrianCrossWalkRegion nCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(14, -22), new Point(-14, -22));
                IPedestrianCrossWalkRegion sCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(14, 22), new Point(-14, 22));
                IPedestrianCrossWalkRegion wCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(-22, -14), new Point(-22, 14));
                IPedestrianCrossWalkRegion eCrosswalk = netiface.createPedestrianCrossWalkRegion(new Point(22, -14), new Point(22, 14));

                if (sCrosswalk != null) {
                    ISignalLamp signalLamp1Positive = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "南斑马线信号灯",
                            sCrosswalk.getId(),
                            new Point(13, 22),
                            true
                    );
                    ISignalLamp signalLamp1Negative = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "南斑马线信号灯",
                            sCrosswalk.getId(),
                            new Point(-13, 22),
                            false
                    );
                    signalLamp1Positive.setSignalPhase(wePedPhase);
                    signalLamp1Negative.setSignalPhase(wePedPhase);
                }

                if (nCrosswalk != null) {
                    ISignalLamp signalLamp2Positive = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "北斑马线信号灯",
                            nCrosswalk.getId(),
                            new Point(13, -22),
                            true
                    );
                    ISignalLamp signalLamp2Negative = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "北斑马线信号灯",
                            nCrosswalk.getId(),
                            new Point(-13, -22),
                            false
                    );
                    signalLamp2Positive.setSignalPhase(wePedPhase);
                    signalLamp2Negative.setSignalPhase(wePedPhase);
                }

                if (eCrosswalk != null) {
                    ISignalLamp signalLamp3Positive = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "东斑马线信号灯",
                            eCrosswalk.getId(),
                            new Point(22, -13),
                            true
                    );
                    ISignalLamp signalLamp3Negative = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "东斑马线信号灯",
                            eCrosswalk.getId(),
                            new Point(22, 13),
                            false
                    );
                    signalLamp3Positive.setSignalPhase(nsPedPhase);
                    signalLamp3Negative.setSignalPhase(nsPedPhase);
                }

                if (wCrosswalk != null) {
                    ISignalLamp signalLamp4Positive = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "西斑马线信号灯",
                            wCrosswalk.getId(),
                            new Point(-22, -13),
                            true
                    );
                    ISignalLamp signalLamp4Negative = netiface.createCrossWalkSignalLamp(
                            signalController,
                            "西斑马线信号灯",
                            wCrosswalk.getId(),
                            new Point(-22, 13),
                            false
                    );
                    signalLamp4Positive.setSignalPhase(nsPedPhase);
                    signalLamp4Negative.setSignalPhase(nsPedPhase);
                }

                showSignalLampAttr(netiface);

private static void showSignalLampAttr(NetInterface netiface){
        int signalLampCount = netiface.signalLampCount();
        List<Long> signalLampIds = netiface.signalLampIds();
        List<ISignalLamp> signalLamps = netiface.signalLamps();

        if (signalLampIds.isEmpty()) {
            System.out.println("无信号灯数据");
            return;
        }

        ISignalLamp signalLamp = netiface.findSignalLamp(signalLampIds.get(0));
        if (signalLamp == null) {
            System.out.println("未找到指定ID的信号灯");
            return;
        }

        String originalName = signalLamp.name();
        signalLamp.setName("new_name");
        String newName = signalLamp.name();

        System.out.printf(
                "机动车信号灯总数=%d, 编号列表=%s, %d的具体信息: %n" +
                        "编号=%d, 获取信号灯当前灯色=%s, 原名称=%s, %n" +
                        "新名称=%s, 获取当前信号灯所在的相位=%s, %n" +
                        "获取当前信号灯所在的灯组=%s, 获取所在车道或车道连接=%s, %n" +
                        "获取信号灯多边形轮廓=%s, 获取信号灯角度(正北为0顺时针)=%f%n",
                signalLampCount,
                signalLampIds,
                signalLampIds.get(0),
                signalLamp.id(),
                signalLamp.color(),
                originalName,  // 原名称
                newName,       // 设置后的新名称
                signalLamp.signalPhase(),
                signalLamp.signalPlan(),
                signalLamp.laneObject(),
                signalLamp.polygon(),
                signalLamp.angle()
        );
    }
```



### 2.16. IBusLine

公交线路接口, 接口方法: 

 **int id();**

获取当前公交线路的ID

举例: 

```java
// 获取当前公交线路的ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路ID=" + busLine.id());
        }
    }
}

```

 **String name();**

获取当前公交线路的名称

举例: 

```java
// 获取当前公交线路的ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路名称=" + busLine.name());
        }
    }
}
```

 **double length(UnitOfMeasure unit);**

获取当前公交线路长度, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取公交线路长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路长度=" + busLine.length());
            System.out.println("公交线路长度=" + busLine.length(UnitOfMeasure.Metric));
        }
    }
}
```

 **int dispatchFreq();**

获取当前公交线路的发车间隔, 单位: 秒

举例: 

```java
// 获取公交线路发车间隔
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IBusLine
                List<IBusLine> lBusLines = netiface.buslines();
                for (IBusLine busLine : lBusLines) {
                    System.out.println("公交线路发车间隔=" + busLine.dispatchFreq());
                }
            }
        }
```

 **int dispatchStartTime();**

获取当前公交线路的发车开始时间, 单位: 秒

举例: 

```java
// 获取公交线路发车开始时间
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路发车开始时间=" + busLine.dispatchStartTime());
        }
    }
}
```

 **int dispatchEndTime();**

获取当前公交线路的发车结束时间, 单位: 秒, 即当前线路的公交调度表的结束时刻

举例: 

```java
// 获取公交线路发车结束时间
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路发车结束时间=" + busLine.dispatchEndTime());
        }
    }
}
```

 **double desirSpeed(UnitOfMeasure unit);**

获取当前公交线路的期望速度, 默认单位: 像素/秒, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取公交线路期望速度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路期望速度=" + busLine.desirSpeed());
            System.out.println("公交线路期望速度=" + busLine.desirSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **int passCountAtStartTime();**

公交线路中公交车的起始载客人数

举例: 

```java
// 获取公交线路中公交车的起始载客人数
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路中公交车的起始载客人数=" + busLine.passCountAtStartTime());
        }
    }
}
```

 **ArrayList<ILink> links();**

获取公交线路经过的路段序列

举例: 

```java
// 获取公交线路经过的路段序列
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路经过的路段序列=" + busLine.links());
        }
    }
}
```

 **ArrayList<IBusStation> stations();**

获取公交线路上的所有站点 

举例: 

```java
// 获取公交线路上的所有站点
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路上的所有站点=" + busLine.stations());
        }
    }
}
```

 **SWIGTYPE_p_QListT_IBusStationLine_p_t stationLines();**

公交站点线路, 当前线路相关站点的上下客等参数, 所有参数的列表

举例: 

```java
// 获取公交站点线路相关参数列表
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IBusLine
                List<IBusLine> lBusLines = netiface.buslines();
                for (IBusLine busLine : lBusLines) {
                    System.out.println("公交站点线路, 当前线路相关站点的上下客等参数, 所有参数的列表=" + busLine.stationLines());
                }
            }
        }
```

 **void setName(String name);**

设置当前公交线路的名称

举例: 

```java
// 设置并获取公交线路名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("设置当前公交线路的名称为"+ "new name");
            busLine.setName("new name");
            System.out.println("获取当前公交线路的名称=" + busLine.name());
        }
    }
}
```

 **void setDispatchFreq(int freq);**

设置当前公交线路的发车间隔, 单位: 秒

参数: 
[ in ] freq: 发车间隔, 单位: 秒

举例: 

```java
// 设置并获取公交线路发车间隔
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IBusLine
                List<IBusLine> lBusLines = netiface.buslines();
                for (IBusLine busLine : lBusLines) {
                    System.out.println("设置当前公交线路的发车间隔(秒)=" + "20");
                    busLine.setDispatchFreq(20);
                    System.out.println("获取当前公交线路的发车间隔(秒)=" + busLine.dispatchFreq());
                }
            }
        }
```

 **void setDispatchStartTime(int startTime);**

设置当前公交线路上的公交首班车辆的开始发车时间

参数: 
[ in ] startTime: 开始发车时间, 单位: 秒

举例: 

```java
// 设置并获取公交线路首班车发车时间
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("设置当前公交线路首班车发车时间=" + "0");
            busLine.setDispatchStartTime(0);
            System.out.println("获取当前公交线路首班车发车时间=" + busLine.dispatchStartTime());
        }
    }
}
```

 **void setDispatchEndTime(int endTime);**

设置当前公交线路上的公交末班车的发车时间

参数: 
[ in ] endTime: 结束发车时间, 单位: 秒

举例: 

```java
// 设置并获取公交线路末班车发车时间
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IBusLine
                List<IBusLine> lBusLines = netiface.buslines();
                for (IBusLine busLine : lBusLines) {
                    System.out.println("设置当前公交线路末班车发车时间=" + "300");
                    busLine.setDispatchEndTime(300);
                    System.out.println("获取当前公交线路末班车发车时间=" + busLine.dispatchEndTime());
                }
            }
        }
```

 **void setDesirSpeed(double desirSpeed,UnitOfMeasure unit);**

设置当前公交线路的期望速度, 默认单位：像素, 可通过unit参数设置单位

参数: 
[ in ]  desirSpeed: 期望速度, 默认单位: 像素/秒 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
// 设置并获取公交线路期望速度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            busLine.setDesirSpeed(40);
            System.out.println("获取当前公交线路期望速度(像素)=" + busLine.desirSpeed());
            busLine.setDesirSpeed(40,UnitOfMeasure.Metric);
            System.out.println("获取当前公交线路期望速度(米制)=" + busLine.desirSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **void setPassCountAtStartTime(int count);**

设置当前公交线路的起始载客人数

举例: 

```java
// 设置并获取公交线路起始载客人数
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("设置当前公交线路起始载客人数=" + "60");
            busLine.setPassCountAtStartTime(60);
            System.out.println("获取当前公交线路起始载客人数=" + busLine.passCountAtStartTime());
        }
    }
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                showBusLineAttr(netiface);
            }
        }

private static void showBusLineAttr(NetInterface netiface) {
    List<IBusLine> busLines = netiface.buslines();
    if (busLines.isEmpty()) {
        return;
    }
    IBusLine path = busLines.get(0);
    IBusLine path2 = netiface.findBusline(path.id());
    IBusLine path3 = (path.links() != null && !path.links().isEmpty()) 
        ? netiface.findBuslineByFirstLinkId(path.links().get(0).id()) 
        : null;

    // 构建路段序列名称字符串
    StringBuilder linkNames = new StringBuilder();
    if (path.links() != null) {
        for (int i = 0; i < path.links().size(); i++) {
            linkNames.append(path.links().get(i).name());
            if (i < path.links().size() - 1) {
                linkNames.append(", ");
            }
        }
    }

    // 处理无返回值（void）的方法：先执行方法，再拼接提示文本
    String setNameResult = "执行成功";
    path.setName("new name"); // 先执行设置名称操作

    String setDispatchFreqResult = "执行成功";
    path.setDispatchFreq(20); // 先执行设置发车间隔操作

    String setDispatchStartTimeResult = "执行成功";
    path.setDispatchStartTime(0); // 先执行设置首班车时间操作

    String setDispatchEndTimeResult = "执行成功";
    path.setDispatchEndTime(300); // 先执行设置末班车时间操作

    String setDesirSpeedResult = "执行成功";
    path.setDesirSpeed(40); // 先执行设置期望速度操作

    String setPassCountResult = "执行成功";
    path.setPassCountAtStartTime(60); // 先执行设置起始载客人数操作

    // 拼接输出内容（仅包含有返回值的结果和操作提示）
    System.out.println(
        "获取当前公交线路的ID=" + path.id() + ", 获取当前公交线路的名称=" + path.name() + ", "
        + "获取当前公交线路长度(像素)=" + path.length() + ", 单位: 米=" + path.length() + ", "
        + "获取当前公交线路的发车间隔(秒)=" + path.dispatchFreq() + ", 获取当前公交线路的发车开始时间(秒)=" + path.dispatchStartTime() + ", "
        + "获取当前公交线路的发车结束时间(秒)=" + path.dispatchEndTime() + ", "
        + "获取当前公交线路的期望速度(像素制)=" + path.desirSpeed() + ", 米制km/h=" + path.desirSpeed() + ", "
        + "公交线路中公交车的起始载客人数=" + path.passCountAtStartTime() + ", 获取公交线路经过的路段序列=[" + linkNames + "], "
        + "获取公交线路上的所有站点=" + path.stations() + ", 公交站点线路相关参数列表=" + path.stationLines() + ", "
        + "设置当前公交线路的名称=" + setNameResult + ", 设置当前公交线路的发车间隔(秒)=" + setDispatchFreqResult + ", "
        + "设置当前公交线路首班车发车时间=" + setDispatchStartTimeResult + ", 设置当前公交线路末班车发车时间=" + setDispatchEndTimeResult + ", "
        + "设置当前公交线路期望速度(米制)=" + setDesirSpeedResult + ", "
        + "设置当前公交线路起始载客人数=" + setPassCountResult
    );
}
```





### 2.17. IBusStation

公交站点接口, 接口方法: 

 **int id();**

获取当前公交站点ID

举例: 

```java
// 获取当前公交站点ID
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点ID=" + busStation.id());
        }
    }
}
```

 **String name();**

获取当前公交站点名称

举例: 

```java
// 获取当前公交站点名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点名称=" + busStation.name());
        }
    }
}
```

 **int laneNumber();**

获取当前公交站点所在车道序号

举例: 

```java
// 获取当前公交站点所在车道序号
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点所在车道序号=" + busStation.laneNumber());
        }
    }
}
```

 **double x(UnitOfMeasure unit);**

获取当前公交站点的中心点的位置, X坐标, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取当前公交站点的中心点X坐标
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的中心点的位置, X坐标=" + busStation.x());
            System.out.println("获取当前公交站点的中心点的位置, X坐标(米制)=" + busStation.x(UnitOfMeasure.Metric));
        }
    }
}
```

 **double y(UnitOfMeasure unit);**

获取当前公交站点的中心点的位置, Y坐标, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取当前公交站点的中心点X坐标
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的中心点的位置, Y坐标=" + busStation.y());
            System.out.println("获取当前公交站点的中心点的位置, Y坐标(米)=" + busStation.y(UnitOfMeasure.Metric));
        }
    }
}
```

 **double length(UnitOfMeasure unit);**

获取当前公交站点的长度, 默认单位: 像素, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取当前公交站点的长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的长度(像素)=" + busStation.length());
            System.out.println("获取当前公交站点的长度(米)=" + busStation.length(UnitOfMeasure.Metric));
        }
    }
}
```

 **int stationType();**

获取当前公交站点的类型: 站点类型 1: 路边式、2: 港湾式

举例: 

```java
// 获取当前公交站点的类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的类型: 站点类型 1: 路边式、2: 港湾式=" + busStation.stationType());
        }
    }
}
```

 **ILink link();**

获取当前公交站点所在路段

举例: 

```java
// 获取当前公交站点所在路段
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点所在路段=" + busStation.link());
        }
    }
}
```

 **ILane lane();**

获取当前公交站点所在车道

举例: 

```java
// 获取当前公交站点所在车道
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点所在车道=" + busStation.lane());
        }
    }
}
```

 **double distance(UnitOfMeasure unit);**

获取当前公交站点的起始位置距路段起点的距离, 默认单位: 像素, 可通过unit参数设置单位 
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 获取当前公交站点的起始位置距路段起点的距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的起始位置距路段起点的距离(像素)=" + busStation.distance());
            System.out.println("获取当前公交站点的起始位置距路段起点的距离(米)=" + busStation.distance(UnitOfMeasure.Metric));
        }
    }
}
```

 **void setName(String name);**

设置当前公交站点名称

参数: 
[ in ] name: 新名称

举例: 

```java
// 设置并获取公交站点名称
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            // 处理void方法：先执行设置操作，再拼接提示文本
            busStation.setName("new name");
            System.out.println("设置站点名称=执行成功");
            System.out.println("获取站点名称=" + busStation.name());
        }
    }
}
```

 **void setDistToStart(double dist,UnitOfMeasure unit);**

设置站点起始点距车道起点距离, 默认单位: 像素, 可通过unit参数设置单位 
参数: 
[ in ] dist: 距车道起点距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例: 

```java
// 设置并获取公交站点起始点距车道起点距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            // 处理void方法：先执行设置操作，再拼接提示文本
            busStation.setDistToStart(100);
            System.out.println("设置站点起始点距车道起点距离(像素)=执行成功");
            System.out.println("获取当前公交站点的起始位置距路段起点的距离(像素)=" + busStation.distance());
            
            busStation.setDistToStart(100,UnitOfMeasure.Metric);
            System.out.println("设置站点起始点距车道起点距离(米)=执行成功");
            System.out.println("获取当前公交站点的起始位置距路段起点的距离(米)=" + busStation.distance(UnitOfMeasure.Metric));
        }
    }
}
```

 **void setLength(double length,UnitOfMeasure unit);**

设置当前公交站点的长度, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
// 设置并获取公交站点长度
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IBusStation
                List<IBusStation> lBusStations = netiface.busStations();
                for (IBusStation busStation : lBusStations) {
                    // 处理void方法：先执行设置操作，再拼接提示文本
                    busStation.setLength(100);
                    System.out.println("设置当前公交站点的长度(像素)=执行成功");
                    System.out.println("获取当前公交站点的长度(像素)=" + busStation.length());
                    
                    busStation.setLength(100,UnitOfMeasure.Metric);
                    System.out.println("设置当前公交站点的长度(米)=执行成功");
                    System.out.println("获取当前公交站点的长度(米)=" + busStation.length(UnitOfMeasure.Metric));
                }
            }
        }
```

 **void setType(int type);**

设置当前公交站点类型

参数: 

[ in ] type: 站点类型, 1 路侧式、2 港湾式

举例: 

```java
// 设置并获取公交站点类型
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            // 处理void方法：先执行设置操作，再拼接提示文本
            busStation.setType(2);
            System.out.println("设置当前公交站点类型=执行成功");
            System.out.println("获取当前公交站点类型=" + busStation.stationType());
        }
    }
}
```

 **Vector<Point> polygon();**

获取 公交站点多边型轮廓的顶点

举例: 

```java
// 获取公交站点多边型轮廓的顶点
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取 公交站点多边型轮廓的顶点=" + busStation.polygon());
        }
    }
}
```



**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showBusStationAttr(netiface);
    }
}

private static void showBusStationAttr(NetInterface netiface) {
        List<IBusStation> busStations = netiface.busStations();
        if (busStations.isEmpty()) {
            return;
        }
        IBusStation busStation = busStations.get(0);
        IBusStation busStation1 = netiface.findBusStation(busStation.id());

        // 处理void方法：先执行设置操作，存储执行状态
        busStation.setName("new trans station");
        String setNameResult = "执行成功";

        double originalDistPixel = busStation.distance();
        busStation.setDistToStart(originalDistPixel + 1);
        String setDistPixelResult = "执行成功";

        double originalLengthPixel = busStation.length();
        busStation.setLength(originalLengthPixel + 1);
        String setLengthPixelResult = "执行成功";

        busStation.setType(2);
        String setType2Result = "执行成功";
        busStation.setType(1);
        String setType1Result = "执行成功";

        // 拼接输出内容（移除了被删除的属性）
        System.out.println(
                "获取当前公交站点ID=" + busStation.id() + ", 获取当前公交线路名称=" + busStation.name() + ", "
                        + "获取当前公交站点所在车道序号=" + busStation.laneNumber() + ", "
                        + "获取当前公交站点的中心点的位置, X坐标 像素制=" + busStation.x() +  ", "
                        + "获取当前公交站点的中心点的位置, Y坐标 像素制=" + busStation.y() +  ", "
                        + "获取当前公交站点的长度, 像素制=" + busStation.length() +   ", "
                        + "获取当前公交站点所在路段=" + busStation.link() + ", "
                        + "获取当前公交站点所在车道=" + busStation.lane() + ", "
                        + "设置站点名称=" + setNameResult + ", "
                        + "获取当前公交站点的起始位置距路段起点的距离, 像素制=" + busStation.distance() + ", "
                        + "设置站点起始点距车道起点距离(像素)=" + setDistPixelResult + ", "
                        + "设置站点长度(像素)=" + setLengthPixelResult + ", "
                        + "设置当前公交站点类型(2)=" + setType2Result + ", 设置当前公交站点类型(1)=" + setType1Result + ", "
                        + "获取 公交站点多边型轮廓的顶点=" + busStation.polygon()
        );
    }
```





### 2.18. IBusStationLine

公交站点-线路接口, 通过此接口可以获取指定线路某站点运行参数, 如靠站时间、下客百分比等, 还可以设置这些参数。

接口方法: 

 **long id();**

获取公交“站点-线路”ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IBusLine> buslines = netiface.buslines();
            if (buslines != null) {
                for (IBusLine busLine : buslines) {
                    List<IBusStationLine> busStationLines = busLine.stationLines();
                    if (busStationLines != null) {
                        for (IBusStationLine busStationLine : busStationLines) {
                            System.out.println("获取公交“站点-线路”ID=" + busStationLine.id());
                        }
                    }
                }
            }
        }
    }
```

 **long stationId();**

获取当前公交站点的ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IBusLine> buslines = netiface.buslines();
                if (buslines != null) {
                    for (IBusLine busLine : buslines) {
                        List<IBusStationLine> busStationLines = busLine.stationLines();
                        if (busStationLines != null) {
                            for (IBusStationLine busStationLine : busStationLines) {
                                System.out.println("获取当前公交站点的ID=" + busStationLine.stationId());
                            }
                        }
                    }
                }
            }
        }
```

 **long lineId();**

获取当前公交站台所属的公交线路ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        System.out.println("获取当前公交站台所属的公交线路ID=" + busStationLine.lineId());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **int busParkingTime();**

获取当前公交线路下该站台的公交车辆停靠时间(秒)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        System.out.println("获取当前公交线路下该站台的公交车辆停靠时间(秒)=" + busStationLine.busParkingTime());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **double getOutPercent();**

获取当前公交线路下该站台的下客百分比

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        System.out.println("获取当前公交线路下该站台的下客百分比=" + busStationLine.getOutPercent());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **double getOnTimePerPerson();**

获取当前公交线路下该站台下的平均每位乘客上车时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        System.out.println("获取当前公交线路下该站台的平均每位乘客上车时间(秒)=" + busStationLine.getOnTimePerPerson());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **double getOutTimePerPerson();**

获取当前公交线路下该站台下的平均每位乘客下车时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        System.out.println("获取当前公交线路下该站台的平均每位乘客下车时间(秒)=" + busStationLine.getOutTimePerPerson());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **void setBusParkingTime(int time);**

设置当前公交线路下该站台下的车辆停靠时间(秒)

参数: 
[ in ] time: 车辆停靠时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        busStationLine.setBusParkingTime(20);
                                        System.out.println("获取当前公交线路下该站台的车辆停靠时间(秒)=" + busStationLine.busParkingTime());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **void setGetOutPercent(double percent);**

设置当前公交线路下的该站台的下客百分比

参数: 
[ in ] percent: 下客百分比

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        busStationLine.setGetOutPercent(20);
                                        System.out.println("获取当前公交线路下该站台的下客百分比=" + busStationLine.getOutPercent());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **void setGetOnTimePerPerson(double time);**

设置当前公交线路下的该站台的平均每位乘客上车时间

参数: 
[ in ] time: 平均每位乘客上车时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IBusLine> buslines = netiface.buslines();
                        if (buslines != null) {
                            for (IBusLine busLine : buslines) {
                                List<IBusStationLine> busStationLines = busLine.stationLines();
                                if (busStationLines != null) {
                                    for (IBusStationLine busStationLine : busStationLines) {
                                        busStationLine.setGetOnTimePerPerson(5.0);
                                        System.out.println("获取当前公交线路下该站台的平均每位乘客上车时间=" + busStationLine.getOnTimePerPerson());
                                    }
                                }
                            }
                        }
                    }
                }
```

 **void setGetOutTimePerPerson(double time);**

设置当前公交线路下的该站台的平均每位乘客下车时间

参数: 
[ in ] time: 平均每位乘客下车时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IBusLine> buslines = netiface.buslines();
            if (buslines != null) {
                for (IBusLine busLine : buslines) {
                    List<IBusStationLine> busStationLines = busLine.stationLines();
                    if (busStationLines != null) {
                        for (IBusStationLine busStationLine : busStationLines) {
                            busStationLine.setGetOutTimePerPerson(1.0);
                            System.out.println("获取当前公交线路下该站台的平均每位乘客下车时间=" + busStationLine.getOutTimePerPerson());
                        }
                    }
                }
            }
        }
    }
```

**案例代码**

```java
netTessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            showBusStationLineAttr(netiface);
        }
    }

private static void showBusStationLineAttr(NetInterface netiface) {
        List<IBusStation> busStations = netiface.busStations();
        if (busStations == null || busStations.isEmpty()) {
            System.out.println("无公交站点数据");
            return;
        }

        IBusStation busStation = busStations.get(0);
        List<IBusStationLine> busStationLines = netiface.findBusStationLineByStationId(busStation.id());
        if (busStationLines == null || busStationLines.isEmpty()) {
            System.out.println("当前公交站点无关联的“站点-线路”数据");
            return;
        }

        IBusStationLine busStationLine = busStationLines.get(0);

        busStationLine.setBusParkingTime(20);
        busStationLine.setGetOutPercent(0.60);
        busStationLine.setGetOnTimePerPerson(2.0);
        busStationLine.setGetOutTimePerPerson(1.0);

        System.out.printf(
                "获取公交“站点-线路”ID=%d, 获取当前公交站点的ID=%d, %n" +
                        "获取当前公交站台所属的公交线路ID=%d, %n" +
                        "获取当前公交线路下该站台的公交车辆停靠时间(秒)=%d, %n" +
                        "获取当前公交线路下该站台的下客百分比=%.2f, %n" +
                        "获取当前公交线路下该站台的平均每位乘客上车时间(秒)=%.1f, %n" +
                        "获取当前公交线路下该站台的平均每位乘客下车时间(秒)=%.1f%n",
                busStationLine.id(),
                busStationLine.stationId(),
                busStationLine.lineId(),
                busStationLine.busParkingTime(),
                busStationLine.getOutPercent(),
                busStationLine.getOnTimePerPerson(),
                busStationLine.getOutTimePerPerson()
        );
    }
```





### 2.19. IVehicleDrivInfoCollector

数据采集器接口, 方法如下: 

 **long id();**

获取采集器ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
        if (collectors != null) {
            for (IVehicleDrivInfoCollector collector : collectors) {
                System.out.println("采集器ID: " + collector.id());
            }
        }
    }
}
```

 **String collName();**

获取采集器名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
        if (collectors != null) {
            for (IVehicleDrivInfoCollector collector : collectors) {
                System.out.printf("采集器%d名称: %s%n", collector.id(), collector.collName());
            }
        }
    }
}
```

 **boolean onLink();**

判断当前数据采集器是否在路段上, 返回值为True表示检测器在路段上, 返回值False则表示在connector上

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d是否在路段上: %b%n", collector.id(), collector.onLink());
                }
            }
        }
    }
```

 **ILink link();**

获取采集器所在的路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                        if (collectors != null) {
                            for (IVehicleDrivInfoCollector collector : collectors) {
                                System.out.printf("采集器%d所在的路段: %s%n", collector.id(), collector.link());
                            }
                        }
                    }
                }
```

 **IConnector connector();**

获取采集器所在的连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d所在的连接段: %s%n", collector.id(), collector.connector());
                }
            }
        }
    }
```

 **ILane lane();**

如果采集器在路段上, 则返回ILane对象, 否则范围None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d所在的车道: %s%n", collector.id(), collector.lane());
                }
            }
        }
    }
```

 **ILaneConnector laneConnector();**

如果采集器在连接段上, 则返回laneConnector“车道连接”对象, 否则返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d所在的车道连接: %s%n", collector.id(), collector.laneConnector());
                }
            }
        }
    }
```

 **double distToStart(UnitOfMeasure unit);**

获取采集器距离路段或连接段起点的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
        if (collectors != null) {
            for (IVehicleDrivInfoCollector collector : collectors) {
                System.out.printf("采集器%d距离路段|连接段起点的距离为%s%n",
                        collector.id(), collector.distToStart());
                System.out.printf("采集器%d距离路段|连接段起点的距离（米制）为%s%n",
                        collector.id(), collector.distToStart(UnitOfMeasure.Metric));
            }
        }
    }
}
```

 **Point point(UnitOfMeasure unit);**

获取采集器所在点, 像素坐标, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d所在点, 坐标为%s%n",
                            collector.id(), collector.point());
                    System.out.printf("采集器%d所在点, 米制坐标为%s%n",
                            collector.id(), collector.point(UnitOfMeasure.Metric));
                }
            }
        }
    }
```

 **long fromTime();**

获取采集器的工作起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                NetInterface netiface = iface.netInterface();
                if (netiface != null) {
                    List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                    if (collectors != null) {
                        for (IVehicleDrivInfoCollector collector : collectors) {
                            System.out.printf("采集器%d的工作起始时间, 为%d秒%n",
                                    collector.id(), collector.fromTime());
                        }
                    }
                }
            }
```

 **long toTime();**

获取采集器的工作停止时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    System.out.printf("采集器%d的工作停止时间, 为%d秒%n",
                            collector.id(), collector.toTime());
                }
            }
        }
    }
```

 **long aggregateInterval();**

获取数据集计的时间间隔, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                NetInterface netiface = iface.netInterface();
                if (netiface != null) {
                    List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                    if (collectors != null) {
                        for (IVehicleDrivInfoCollector collector : collectors) {
                            System.out.printf("采集器%d数据集计的时间间隔, 为%d秒%n",
                                    collector.id(), collector.aggregateInterval());
                        }
                    }
                }
            }
```

 **void setName(String name);**

设置采集器名称

参数: 
[ in ] name: 新名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                        if (collectors != null) {
                            for (IVehicleDrivInfoCollector collector : collectors) {
                                collector.setName("采集器名称");
                                System.out.printf("采集器%d的名称, 为%s%n",
                                        collector.id(), collector.collName());
                            }
                        }
                    }
                }
```

 **void setDistToStart(double dist, UnitOfMeasure unit);**

设置采集器距车道起点（或“车道连接”起点）的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 采集器距离车道起点（或“车道连接”起点）的距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
            if (collectors != null) {
                for (IVehicleDrivInfoCollector collector : collectors) {
                    collector.setDistToStart(400);
                    System.out.printf("采集器%d距车道起点的距离, 为%s像素%n",
                            collector.id(), collector.distToStart());

                    collector.setDistToStart(400, UnitOfMeasure.Metric);
                    System.out.printf("采集器%d距车道起点的距离（米制）, 为%s米%n",
                            collector.id(), collector.distToStart(UnitOfMeasure.Metric));
                }
            }
        }
    }
```

 **void setFromTime(long time);**

设置工作起始时间(秒)

参数: 
[ in ] time: 工作起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                        if (collectors != null) {
                            for (IVehicleDrivInfoCollector collector : collectors) {
                                collector.setFromTime(10);
                                System.out.printf("采集器%d的工作起始时间, 为%d秒%n",
                                        collector.id(), collector.fromTime());
                            }
                        }
                    }
                }
```

 **void setToTime(long time);**

设置工作结束时间(秒)

参数: 
[ in ] time: 工作结束时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                if (collectors != null) {
                    for (IVehicleDrivInfoCollector collector : collectors) {
                        collector.setToTime(60);
                        System.out.printf("采集器%d的工作停止时间, 为%d秒%n",
                                collector.id(), collector.toTime());
                    }
                }
            }
        }
```

 **void setAggregateInterval(int interval);**

设置集计数据时间间隔(秒)

参数: 
[ in ] interval: 集计数据时间间隔, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                        if (collectors != null) {
                            for (IVehicleDrivInfoCollector collector : collectors) {
                                collector.setAggregateInterval(10);
                                System.out.printf("采集器%d数据集计的时间间隔, 为%d秒%n",
                                        collector.id(), collector.aggregateInterval());
                            }
                        }
                    }
                }
```

 **Vector<Point> polygon();**

获取采集器的多边型轮廓顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
                        if (collectors != null) {
                            for (IVehicleDrivInfoCollector collector : collectors) {
                                System.out.printf("采集器%d的多边型轮廓顶点为%s%n",
                                        collector.id(), collector.polygon());
                            }
                        }
                    }
                }
```



**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showVehicleDrivInfoCounter(netiface);
                    }
                }
                private static void showVehicleDrivInfoCounter(NetInterface netiface) {
        List<IVehicleDrivInfoCollector> collectors = netiface.vehiInfoCollectors();
        if (collectors == null || collectors.isEmpty()) {
            System.out.println("无车辆行驶信息采集器数据");
            return;
        }

        // 获取第一个采集器
        IVehicleDrivInfoCollector collector = netiface.findVehiInfoCollector(collectors.get(0).id());
        if (collector == null) {
            System.out.println("未找到目标采集器");
            return;
        }

        // 打印详细属性
        System.out.printf(
                "获取采集器ID=%d, 获取采集器名称=%s, %n" +
                        "判断当前数据采集器是否在路段上（True=路段, False=连接段）=%b, %n" +
                        "获取采集器所在的路段=%s, 获取采集器所在的连接段=%s, %n" +
                        "采集器所在车道（路段上有效）=%s, 采集器所在车道连接（连接段上有效）=%s, %n" +
                        "采集器工作时间范围=%d-%d秒, 所在点像素坐标=%s, %n" +
                        "采集器距离路段|连接段起点的距离=%s%n",
                collector.id(),
                collector.collName(),
                collector.onLink(),
                collector.link(),
                collector.connector(),
                collector.lane(),
                collector.laneConnector(),
                collector.fromTime(),
                collector.toTime(),
                collector.point(),
                collector.distToStart()
        );
    }
```





### 2.20. IVehicleQueueCounter

排队计数器接口, 方法如下: 

 **long id();**

获取当前排队计数器ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
            for (IVehicleQueueCounter counter : counters) {
                System.out.printf("排队计数器%s的ID为%s%n", counter.id(), counter.id());
            }
        }
    }
```

 **String counterName();**

获取当前排队计数器名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s的名称为%s%n", counter.id(), counter.counterName());
                        }
                    }
                }
```

 **boolean onLink();**

是否在路段上, 如果True则connector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s是否在路段上为%b%n", counter.id(), counter.onLink());
                        }
                    }
                }
```

 **ILink link();**

获取当前排队计数器所在路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s所在路段为%s%n", counter.id(), counter.link());
                        }
                    }
                }
```

 **IConnector connector();**

获取当前计数器所在连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s所在连接段为%s%n", counter.id(), counter.connector());
                        }
                    }
                }
```

 **ILane lane();**

如果计数器在路段上则lane()返回所在车道, laneConnector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s所在车道为%s%n", counter.id(), counter.lane());
                        }
                    }
                }
```

 **ILaneConnector laneConnector();**

如果计数器在连接段上则laneConnector返回“车道连接”, lane()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
            for (IVehicleQueueCounter counter : counters) {
                System.out.printf("排队计数器%s所在车道连接为%s%n", counter.id(), counter.laneConnector());
            }
        }
    }
```

 **double distToStart(UnitOfMeasure unit);**

计数器距离起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s距离起点距离为%s%n", counter.id(), counter.distToStart());
                            System.out.printf("排队计数器%s距离起点距离（米制）为%s%n", counter.id(), counter.distToStart(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **Point point(UnitOfMeasure unit);**

计数器所在点, 像素坐标, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s所在点坐标为%s%n", counter.id(), counter.point());
                            System.out.printf("排队计数器%s所在点坐标（米制）为%s%n", counter.id(), counter.point(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long fromTime();**

获取当前计数器工作起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s的工作起始时间, 为%d秒%n", counter.id(), counter.fromTime());
                        }
                    }
                }
```

 **long toTime();**

获取当前计数器工作停止时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                for (IVehicleQueueCounter counter : counters) {
                    System.out.printf("排队计数器%s的工作停止时间, 为%d秒%n", counter.id(), counter.toTime());
                }
            }
        }
```

 **long aggregateInterval();**

计数集计数据时间间隔, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
            for (IVehicleQueueCounter counter : counters) {
                System.out.printf("排队计数器%s数据集计的时间间隔, 为%d秒%n", counter.id(), counter.aggregateInterval());
            }
        }
    }
```

 **void setName(String name);**

设置计数器名称

参数: 
[ in ] name: 计数器名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            counter.setName("计数器名称");
                            System.out.printf("排队计数器%s的名称, 为%s%n", counter.id(), counter.counterName());
                        }
                    }
                }
```

 **void setDistToStart(double dist, UnitOfMeasure unit);**

设置当前计数器距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 计数器距离车道起点（或“车道连接”起点）的距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            counter.setDistToStart(100);
                            System.out.printf("排队计数器%s距离起点距离, 为%s%n", counter.id(), counter.distToStart());

                            counter.setDistToStart(100, UnitOfMeasure.Metric);
                            System.out.printf("排队计数器%s距离起点距离（米制）, 为%s米%n", counter.id(), counter.distToStart(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setFromTime(long time);**

设置工作起始时间(秒)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            counter.setFromTime(10);
                            System.out.printf("排队计数器%s的工作起始时间, 为%d秒%n", counter.id(), counter.fromTime());
                        }
                    }
                }
```

 **void setToTime(long time);**

设置工作结束时间(秒)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            counter.setToTime(60);
                            System.out.printf("排队计数器%s的工作结束时间, 为%d秒%n", counter.id(), counter.toTime());
                        }
                    }
                }
```

 **void setAggregateInterval(int interval);**

设置集计数据时间间隔(秒)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            counter.setAggregateInterval(10);
                            System.out.printf("排队计数器%s数据集计的时间间隔, 为%d秒%n", counter.id(), counter.aggregateInterval());
                        }
                    }
                }
```

 **Vector<Point> polygon();**

获取计数器的多边型轮廓顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
                        for (IVehicleQueueCounter counter : counters) {
                            System.out.printf("排队计数器%s的多边型轮廓顶点为%s%n", counter.id(), counter.polygon());
                        }
                    }
                }
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showVehicleQueueCounter(netiface);
    }
}

private static void showVehicleQueueCounter(NetInterface netiface) {
        List<IVehicleQueueCounter> counters = netiface.vehiQueueCounters();
        IVehicleQueueCounter counter = netiface.findVehiQueueCounter(counters.get(0).id());

        System.out.printf(
                "获取当前排队计数器ID=%s, 获取当前排队计数器名称=%s, %n" +
                        "判断当前数据采集器是否在路段上（True=路段, False=连接段）=%b, %n" +
                        "获取当前排队计数器所在路段=%s, 获取当前排队计数器所在连接段=%s, %n" +
                        "计数器所在车道（路段上有效）=%s, 计数器所在车道连接（连接段上有效）=%s, %n" +
                        "计数器工作时间范围=%d-%d秒, 所在点像素坐标=%s, %n" +
                        "计数集计数据时间间隔=%d秒, 计数器距离起点距离（像素）=%s%n",
                counter.id(),
                counter.counterName(),
                counter.onLink(),
                counter.link(),
                counter.connector(),
                counter.lane(),
                counter.laneConnector(),
                counter.fromTime(),
                counter.toTime(),
                counter.point(),
                counter.aggregateInterval(),
                counter.distToStart()
        );
    }
```




### 2.21. IVehicleTravelDetector

行程时间检测器接口, 方法如下: 

 **long id();**

获取检测器ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的ID为%s%n", detector.id(), detector.id());
            }
        }
    }
```

 **String detectorName();**

获取检测器名称

举例: 

```java
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的名称, 为%s%n", detector.id(), detector.detectorName());
            }
        }
    }
```

 **boolean isStartDetector();**

是否检测器起始点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            System.out.printf("行程时间检测器%s是否为起始点, 为%b%n", detector.id(), detector.isStartDetector());
                        }
                    }
                }
```

 **boolean isOnLink_startDetector();**

检测器起点是否在路段上, 如果否, 则起点在连接段上

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
        for (IVehicleTravelDetector detector : detectors) {
            System.out.printf("行程时间检测器起点%s是否在路段上, 为%b%n", detector.id(), detector.isOnLink_startDetector());
        }
    }
}
```

 **boolean isOnLink_endDetector();**

检测器终点是否在路段上, 如果否, 则终点在连接段上

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器终点%s是否在路段上, 为%b%n", detector.id(), detector.isOnLink_endDetector());
            }
        }
    }
```

 **ILink link_startDetector();**

如果检测器起点在路段上则link_startDetector()返回起点所在路段, laneConnector_startDetector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器起点%s所在路段, 为%s%n", detector.id(), detector.link_startDetector());
            }
        }
    }
```

 **ILaneConnector laneConnector_startDetector();**

如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”, link_startDetector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                for (IVehicleTravelDetector detector : detectors) {
                    System.out.printf("行程时间检测器起点%s所在连接段, 为%s%n", detector.id(), detector.laneConnector_startDetector());
                }
            }
        }
```

 **ILink link_endDetector();**

如果检测器终点在路段上则link_endDetector()返回终点所在路段, laneConnector_endDetector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            System.out.printf("行程时间检测器终点%s所在路段, 为%s%n", detector.id(), detector.link_endDetector());
                        }
                    }
                }
```

 **ILaneConnector laneConnector_endDetector();**

如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”, link_endDetector()返回None

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器终点%s所在连接段, 为%s%n", detector.id(), detector.laneConnector_endDetector());
            }
        }
    }
```

 **double distance_startDetector(UnitOfMeasure unit);**

检测器起点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的起点距离所在车道起点距离, 为%s%n", detector.id(), detector.distance_startDetector());
                System.out.printf("行程时间检测器%s的起点距离（米制）, 为%s米%n", detector.id(), detector.distance_startDetector(UnitOfMeasure.Metric));
            }
        }
    }
```

 **double distance_endDetector(UnitOfMeasure unit);**

检测器终点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的终点距离所在车道起点距离, 为%s%n", detector.id(), detector.distance_endDetector());
                System.out.printf("行程时间检测器%s的终点距离（米制）, 为%s米%n", detector.id(), detector.distance_endDetector(UnitOfMeasure.Metric));
            }
        }
    }
```

 **Point point_startDetector(UnitOfMeasure unit);**

检测器起点位置, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的起点位置, 为%s%n", detector.id(), detector.point_startDetector());
                System.out.printf("行程时间检测器%s的起点位置（米制）, 为%s米%n", detector.id(), detector.point_startDetector(UnitOfMeasure.Metric));
            }
        }
    }
```

 **Point point_endDetector(UnitOfMeasure unit);**

检测器终点位置, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            System.out.printf("行程时间检测器%s的终点位置, 为%s%n", detector.id(), detector.point_endDetector());
                            System.out.printf("行程时间检测器%s的终点位置（米制）, 为%s米%n", detector.id(), detector.point_endDetector(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long fromTime();**

获取检测器工作起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            System.out.printf("行程时间检测器%s的工作起始时间, 为%s%n", detector.id(), detector.fromTime());
                        }
                    }
                }
```

 **long toTime();**

检测器工作停止时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            System.out.printf("行程时间检测器%s的工作停止时间, 为%s%n", detector.id(), detector.toTime());
                        }
                    }
                }

```

 **long aggregateInterval();**

集计数据时间间隔, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的集计数据时间间隔, 为%s%n", detector.id(), detector.aggregateInterval());
            }
        }
    }
}
                }
```

 **void setName(String name);**

设置检测器名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            detector.setName("检测器名称");
                            System.out.printf("行程时间检测器%s的名称, 为%s%n", detector.id(), detector.detectorName());
                        }
                    
```

 **void setDistance_startDetector**

设置检测器起点距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            detector.setDistance_startDetector(100);
                            System.out.printf("行程时间检测器%s的起点距离, 为%s%n", detector.id(), detector.distance_startDetector());
                            System.out.printf("行程时间检测器%s的起点距离（米制）, 为%s米%n", detector.id(), detector.distance_startDetector(UnitOfMeasure.Metric));
                        }
                    }
                }

```

 **void setDistance_startDetector(double dist, UnitOfMeasure unit);**

设置检测器终点距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            detector.setDistance_endDetector(100);
                            System.out.printf("行程时间检测器%s的终点距离, 为%s%n", detector.id(), detector.distance_endDetector());
                            System.out.printf("行程时间检测器%s的终点距离（米制）, 为%s米%n", detector.id(), detector.distance_endDetector(UnitOfMeasure.Metric));
                        }
                    }
                }

```

 **void setFromTime(long time);**

设置检测器工作起始时间, 单位: 秒

参数: 
[ in ] time: 工作起始时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            detector.setFromTime(10);
                            detector.setToTime(60);
                            System.out.printf("行程时间检测器%s的工作起始时间, 为%d秒%n", detector.id(), detector.fromTime());
                            System.out.printf("行程时间检测器%s的工作结束时间, 为%d秒%n", detector.id(), detector.toTime());
                        }
                    }
                }
```

 

 **void setToTime(long time);**

设置检测器工作结束时间, 单位: 秒

参数: 
[ in ] time: 工作结束时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
                        for (IVehicleTravelDetector detector : detectors) {
                            detector.setFromTime(10);
                            detector.setToTime(60);
                            System.out.printf("行程时间检测器%s的工作起始时间, 为%d秒%n", detector.id(), detector.fromTime());
                            System.out.printf("行程时间检测器%s的工作结束时间, 为%d秒%n", detector.id(), detector.toTime());
                        }
                    }
                }
```

 **void setAggregateInterval(int interval);**

设置检测器集计数据时间间隔, 单位: 秒

参数: 
[ in ] interval: 集计数据时间间隔, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
        for (IVehicleTravelDetector detector : detectors) {
            detector.setAggregateInterval(10);
            System.out.printf("行程时间检测器%s的集计数据时间间隔, 为%s%n", detector.id(), detector.aggregateInterval());
        }
    }
}
```

 **Vector<Point> polygon_startDetector();**

获取行程时间检测器起始点多边型轮廓的顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
        for (IVehicleTravelDetector detector : detectors) {
            System.out.printf("行程时间检测器%s的起点多边型轮廓的顶点, 为%s%n", detector.id(), detector.polygon_startDetector());
        }
    }
}
```

 **Vector<Point> polygon_endDetector();**

获取行程时间检测器终止点多边型轮廓的顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
            for (IVehicleTravelDetector detector : detectors) {
                System.out.printf("行程时间检测器%s的终点多边型轮廓的顶点, 为%s%n", detector.id(), detector.polygon_endDetector());
            }
        }
    }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showVehicleTravelDetector(netiface);
    }
}

private static void showVehicleTravelDetector(NetInterface netiface) {
        List<IVehicleTravelDetector> detectors = netiface.vehiTravelDetectors();
        IVehicleTravelDetector detector = netiface.findVehiTravelDetector(detectors.get(0).id());

        System.out.printf(
                "获取检测器ID=%s, 获取检测器名称=%s, 是否检测器起始点=%b, %n" +
//                        "判断当前数据采集器是否在路段上（True=路段, False=连接段）=%b, %n" +
                        "检测器起点是否在路段上=%b, 检测器终点是否在路段上=%b, %n" +
                        "起点所在路段（路段上有效）=%s, 起点所在车道连接（连接段上有效）=%s, %n" +
                        "终点所在路段（路段上有效）=%s, 终点所在车道连接（连接段上有效）=%s, %n" +
                        "起点距离（像素）=%s, 终点距离（像素）=%s, %n" +
                        "起点位置（像素）=%s, 终点位置（像素）=%s, %n" +
                        "工作起始时间（秒）=%d, 工作停止时间（秒）=%d, %n" +
                        "集计数据时间间隔（秒）=%s, %n" +
                        "起点多边形轮廓顶点=%s, 终点多边形轮廓顶点=%s%n",
                detector.id(),
                detector.detectorName(),
//                detector.onLink(),
                detector.isStartDetector(),
                detector.isOnLink_startDetector(),
                detector.isOnLink_endDetector(),
                detector.link_startDetector(),
                detector.laneConnector_startDetector(),
                detector.link_endDetector(),
                detector.laneConnector_endDetector(),
                detector.distance_startDetector(),
                detector.distance_endDetector(),
                detector.point_startDetector(),
                detector.point_endDetector(),
                detector.fromTime(),
                detector.toTime(),
                detector.aggregateInterval(),
                detector.polygon_startDetector(),
                detector.polygon_endDetector()
        );
    }
```






### 2.22. IGuidArrow

导向箭头接口, 方法如下: 

 **long id();**

获取导向箭头ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IGuidArrow> lGuidArrows = netiface.guidArrows();
        for (IGuidArrow guidArrow : lGuidArrows) {
            System.out.printf("导向箭头的ID为%s%n", guidArrow.id());
        }
    }
}
```

 **ILane lane();**

获取导向箭头所在的车道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IGuidArrow> lGuidArrows = netiface.guidArrows();
        for (IGuidArrow guidArrow : lGuidArrows) {
            System.out.printf("导向箭头%s所在的车道为%s%n", guidArrow.id(), guidArrow.lane());
        }
    }
    }
```

 **double length(UnitOfMeasure unit);**

获取导向箭头的长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IGuidArrow> lGuidArrows = netiface.guidArrows();
                        for (IGuidArrow guidArrow : lGuidArrows) {
                            System.out.printf("导向箭头%s的长度, 为%s%n", guidArrow.id(), guidArrow.length());
                            System.out.printf("导向箭头%s的长度（米制）, 为%s米%n", guidArrow.id(), guidArrow.length(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double distToTerminal(UnitOfMeasure unit);**

获取导向箭头到的终点距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IGuidArrow> lGuidArrows = netiface.guidArrows();
            for (IGuidArrow guidArrow : lGuidArrows) {
                System.out.printf("导向箭头%s的终点距离, 为%s%n", guidArrow.id(), guidArrow.distToTerminal());
                System.out.printf("导向箭头%s的终点距离（米制）, 为%s米%n", guidArrow.id(), guidArrow.distToTerminal(UnitOfMeasure.Metric));
            }
        }
    }
```
 **GuideArrowType arrowType();**

获取导向箭头的类型, 导向箭头的类型分为: 直行、左转、右转、直行或左转、直行或右转、直行左转或右转、左转或右转、掉头、直行或掉头和左转或掉头

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IGuidArrow> lGuidArrows = netiface.guidArrows();
        for (IGuidArrow guidArrow : lGuidArrows) {
            System.out.printf("导向箭头%s的类型, 为%s%n", guidArrow.id(), guidArrow.arrowType());
        }
    }
}
```

 **Vector<Point> polygon();**

获取导向箭头的多边型轮廓的顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IGuidArrow> lGuidArrows = netiface.guidArrows();
            for (IGuidArrow guidArrow : lGuidArrows) {
                System.out.printf("导向箭头%s的多边型轮廓的顶点, 为%s%n", guidArrow.id(), guidArrow.polygon());
            }
        }
    }
```

**案例代码**

```java
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            showGuidArrowAttr(netiface);
        }
    }
private static void showGuidArrowAttr(NetInterface netiface) {
        List<IGuidArrow> guidArrows = netiface.guidArrows();
        IGuidArrow guidArrow = netiface.findGuidArrow(guidArrows.get(0).id());

        System.out.printf(
                "导向箭头数=%d, 导向箭头集=%s, %n" +
                        "导向箭头ID=%s, %n" +
                        "获取导向箭头所在的车道=%s, %n" +
                        "获取导向箭头长度（像素制）=%s, 米制=%s米, %n" +
                        "获取导向箭头到终点距离（像素制）=%s, 米制=%s米, %n" +
                        "获取导向箭头的类型（直行/左转/右转等）=%s, %n" +
                        "获取导向箭头的多边型轮廓的顶点=%s%n",
                netiface.guidArrowCount(),
                guidArrows,
                guidArrow.id(),
                guidArrow.lane(),
                guidArrow.length(),
                guidArrow.length(UnitOfMeasure.Metric),
                guidArrow.distToTerminal(),
                guidArrow.distToTerminal(UnitOfMeasure.Metric),
                guidArrow.arrowType(),
                guidArrow.polygon()
        );
    }
```





### 2.23. IAccidentZone

事故区接口, 方法如下: 

 **long id();**

获取事故区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区的ID为%s%n", accidentZone.id());
                        }
                    }
                }
```

 **String name();**

获取事故区名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区的名称为%s%n", accidentZone.name());
                        }
                    }
                }
```

 **double location(UnitOfMeasure unit);**

获取事故区距所在路段起点的距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s距所在路段起点的距离, 为%s%n", accidentZone.id(), accidentZone.location());
                            System.out.printf("事故区%s距所在路段起点的距离（米制）, 为%s米%n", accidentZone.id(), accidentZone.location(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double zoneLength(UnitOfMeasure unit);**

获取事故区长度, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
        for (IAccidentZone accidentZone : lAccidentZones) {
            System.out.printf("事故区%s的长度, 为%s%n", accidentZone.id(), accidentZone.zoneLength());
            System.out.printf("事故区%s的长度（米制）, 为%s米%n", accidentZone.id(), accidentZone.zoneLength(UnitOfMeasure.Metric));
        }
    }
}
```

 **double limitedSpeed(UnitOfMeasure unit);**

获取事故区当前时段限速, 默认单位: 像素(km/h), 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位(km/h), Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s的限速, 为%s%n", accidentZone.id(), accidentZone.limitedSpeed());
                            System.out.printf("事故区%s的限速（米制）, 为%skm/h%n", accidentZone.id(), accidentZone.limitedSpeed(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **ISection section();**

获取事故区所在的路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s所在的路段或连接段, 为%s%n", accidentZone.id(), accidentZone.section());
                        }
                    }
                }
```

 **long roadId();**

获取事故区所在路段的ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s所在路段的ID为%s%n", accidentZone.id(), accidentZone.roadId());
                        }
                    }
                }
```

 **String roadType();**

获取事故区所在的道路类型(路段或连接段)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s所在的道路类型为%s%n", accidentZone.id(), accidentZone.roadType());
                        }
                    }
                }
```

 **ArrayList<ILaneObject> laneObjects();**

获取事故区当前时段占用的车道列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s当前时段占用的车道列表为%s%n", accidentZone.id(), accidentZone.laneObjects());
                        }
                    }
                }
```

 **double controlLength(UnitOfMeasure unit);**

获取事故区当前时段控制距离（车辆距离事故区起点该距离内, 强制变道）, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s当前时段控制距离, 为%s%n", accidentZone.id(), accidentZone.controlLength());
                            System.out.printf("事故区%s当前时段控制距离（米制）, 为%s米%n", accidentZone.id(), accidentZone.controlLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **IAccidentZoneInterval addAccidentZoneInterval(DynaAccidentZoneIntervalParam param);**

添加事故时段

参数: 
[ in ]   param: 事故时段参数, 入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> accidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            if (!accidentZoneIntervals.isEmpty()) {
                                IAccidentZoneInterval param = accidentZoneIntervals.get(accidentZoneIntervals.size() - 1);
                                DynaAccidentZoneIntervalParam accidentZoneIntervalParam = new DynaAccidentZoneIntervalParam();
                                accidentZoneIntervalParam.setAccidentZoneId(param.accidentZoneId()) ;
                                accidentZoneIntervalParam.setStartTime(param.endTime()) ;
                                accidentZoneIntervalParam.setEndTime(param.endTime() + 300);
                                accidentZoneIntervalParam.setLength(param.length());
                                accidentZoneIntervalParam.setLocation(param.location());
                                accidentZoneIntervalParam.setLimitedSpeed(param.limitedSpeed());
                                accidentZoneIntervalParam.setControlLength(param.controlLength()) ;

                                ILink link = netiface.findLink(accidentZone.roadId());
                                ArrayList<Integer> laneNumbers = new java.util.ArrayList<>();
                                for (ILane lane : link.lanes()) {
                                    laneNumbers.add(lane.number());
                                }
                                ArrayList<Integer> paramLaneNumbers = param.laneNumbers();
                                laneNumbers.removeAll(paramLaneNumbers);
                                accidentZoneIntervalParam.setMlFromLaneNumber(laneNumbers);

                                System.out.printf("添加前事故时段列表%s%n", accidentZone.accidentZoneIntervals());
                                IAccidentZoneInterval accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam);
                                System.out.printf("添加后事故时段列表%s%n", accidentZone.accidentZoneIntervals());
                            }
                        }
                    }
                }
```

 **void removeAccidentZoneInterval(long accidentZoneIntervalId);**

移除事故时段 

参数: 
[ in ] accidentZoneIntervalId: 事故时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> accidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            if (!accidentZoneIntervals.isEmpty()) {
                                IAccidentZoneInterval param = accidentZoneIntervals.get(accidentZoneIntervals.size() - 1);
                                DynaAccidentZoneIntervalParam accidentZoneIntervalParam = new DynaAccidentZoneIntervalParam();
                                accidentZoneIntervalParam.setAccidentZoneId(param.accidentZoneId()) ;
                                accidentZoneIntervalParam.setStartTime(param.endTime()) ;
                                accidentZoneIntervalParam.setEndTime(param.endTime() + 300);
                                accidentZoneIntervalParam.setLength(param.length());
                                accidentZoneIntervalParam.setLocation(param.location());
                                accidentZoneIntervalParam.setLimitedSpeed(param.limitedSpeed());
                                accidentZoneIntervalParam.setControlLength(param.controlLength()) ;

                                ILink link = netiface.findLink(accidentZone.roadId());
                                ArrayList<Integer> laneNumbers = new java.util.ArrayList<>();
                                for (ILane lane : link.lanes()) {
                                    laneNumbers.add(lane.number());
                                }
                                ArrayList<Integer> paramLaneNumbers = param.laneNumbers();
                                laneNumbers.removeAll(paramLaneNumbers);
                                accidentZoneIntervalParam.setMlFromLaneNumber(laneNumbers);

                                // 添加两个事故时段
                                IAccidentZoneInterval accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam);
                                DynaAccidentZoneIntervalParam accidentZoneIntervalParam1 = accidentZoneIntervalParam;
                                accidentZoneIntervalParam1.setStartTime(accidentZoneIntervalParam1.getEndTime());
                                accidentZoneIntervalParam1.setEndTime(accidentZoneIntervalParam1.getEndTime() + 300);
                                IAccidentZoneInterval accidentZoneInterval1 = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam);

                                // 移除刚添加的第二个时段
                                System.out.printf("移除前事故时段列表%s%n", accidentZone.accidentZoneIntervals());
                                accidentZone.removeAccidentZoneInterval(accidentZoneInterval1.intervalId());
                                System.out.printf("移除后事故时段列表%s%n", accidentZone.accidentZoneIntervals());
                            }
                        }
                    }
                }
```

 **boolean updateAccidentZoneInterval(DynaAccidentZoneIntervalParam param);**

更新事故时段

参数: 
[ in ] param: 事故时段参数, 入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> accidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            if (!accidentZoneIntervals.isEmpty()) {
                                IAccidentZoneInterval param = accidentZoneIntervals.get(accidentZoneIntervals.size() - 1);
                                DynaAccidentZoneIntervalParam accidentZoneIntervalParam = new DynaAccidentZoneIntervalParam();
                                accidentZoneIntervalParam.setAccidentZoneId(param.accidentZoneId()) ;
                                accidentZoneIntervalParam.setStartTime(param.endTime()) ;
                                accidentZoneIntervalParam.setEndTime(param.endTime() + 300);
                                accidentZoneIntervalParam.setLength(param.length());
                                accidentZoneIntervalParam.setLocation(param.location());
                                accidentZoneIntervalParam.setLimitedSpeed(param.limitedSpeed());
                                accidentZoneIntervalParam.setControlLength(param.controlLength()) ;

                                ILink link = netiface.findLink(accidentZone.roadId());
                                ArrayList<Integer> laneNumbers = new java.util.ArrayList<>();
                                for (ILane lane : link.lanes()) {
                                    laneNumbers.add(lane.number());
                                }
                                ArrayList<Integer> paramLaneNumbers = param.laneNumbers();
                                laneNumbers.removeAll(paramLaneNumbers);
                                accidentZoneIntervalParam.setMlFromLaneNumber(laneNumbers);

                                IAccidentZoneInterval accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam);
                                accidentZoneIntervalParam.setControlLength(param.controlLength() + 10);
                                accidentZone.updateAccidentZoneInterval(accidentZoneIntervalParam);
                                System.out.printf("更新后事故时段参数（控制距离）：%s%n", accidentZone.findAccidentZoneIntervalById(accidentZoneInterval.intervalId()).controlLength());
                            }
                        }
                    }
                }
```

 **ArrayList<IAccidentZoneInterval> accidentZoneIntervals();**

获取所有事故时段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            System.out.printf("事故区%s的事故时段, 为%s%n", accidentZone.id(), accidentZone.accidentZoneIntervals());
                        }
                    }
                }
```

 **IAccidentZoneInterval findAccidentZoneIntervalById(long accidentZoneIntervalId);**

根据ID查询事故时段

参数: 
[ in ] accidentZoneIntervalId: 事故时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> accidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : accidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段（ID查询）, 为%s%n", accidentZone.id(), accidentZone.findAccidentZoneIntervalById(interval.intervalId()));
                            }
                        }
                    }
                }
```

**IAccidentZoneInterval findAccidentZoneIntervalByStartTime(long startTime);**

根据开始时间查询事故时段

参数: 
[ in ] startTime: 事故时段开始时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> accidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : accidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段（开始时间查询）, 为%s%n", accidentZone.id(), accidentZone.findAccidentZoneIntervalByStartTime(interval.startTime()));
                            }
                        }
                    }
                }
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showAccidentZoneAttr(netiface);
    }
}

private static void showAccidentZoneAttr(NetInterface netiface) {
        List<IAccidentZone> accZones = netiface.accidentZones();
        if (accZones.isEmpty()) {
            System.out.println("没有找到事故区");
            return;
        }

        IAccidentZone accZone = netiface.findAccidentZone(accZones.get(0).id());

        System.out.printf("获取事故区ID=%s, 获取事故区名称=%s, " +
                        "获取事故区当前时段距所在路段起点的距离, 像素制=%s, 米制=%s, " +
                        "获取事故区当前时段长度, 像素制=%s, 米制=%s, " +
                        "获取事故区当前时段限速, 像素制 km/h=%s, 米制=%s, " +
                        "获取事故区所在的路段或连接段=%s, 获取事故区所在路段的ID=%s, " +
                        "获取事故区所在的道路类型(路段或连接段)=%s, " +
                        "获取事故区当前时段占用的车道列表=%s, " +
                        "获取事故区当前时段控制距离（车辆距离事故区起点该距离内, 强制变道, 像素制=%s, 米制=%s%n",
                accZone.id(), accZone.name(),
                accZone.location(), accZone.location(UnitOfMeasure.Metric),
                accZone.zoneLength(), accZone.zoneLength(UnitOfMeasure.Metric),
                accZone.limitedSpeed(), accZone.limitedSpeed(UnitOfMeasure.Metric),
                accZone.section(), accZone.roadId(),
                accZone.roadType(),
                accZone.laneObjects(),
                accZone.controlLength(), accZone.controlLength(UnitOfMeasure.Metric));

        System.out.println("添加事故时段");

        List<IAccidentZoneInterval> accidentZoneIntervals = accZone.accidentZoneIntervals();
        if (accidentZoneIntervals.isEmpty()) {
            System.out.println("没有找到事故时段");
            return;
        }

        IAccidentZoneInterval lastInterval = accidentZoneIntervals.get(accidentZoneIntervals.size() - 1);
        DynaAccidentZoneIntervalParam accidentZoneIntervalParam = new DynaAccidentZoneIntervalParam();

        accidentZoneIntervalParam.setAccidentZoneId(lastInterval.accidentZoneId());
        accidentZoneIntervalParam.setStartTime(lastInterval.endTime());
        accidentZoneIntervalParam.setEndTime(lastInterval.endTime() + 300);
        accidentZoneIntervalParam.setLength(lastInterval.length());
        accidentZoneIntervalParam.setLocation(lastInterval.location());
        accidentZoneIntervalParam.setLimitedSpeed(lastInterval.limitedSpeed());
        accidentZoneIntervalParam.setControlLength(lastInterval.controlLength());

        ILink link = netiface.findLink(accZone.roadId());
        List<Integer> laneNumbers = link.lanes().stream()
                .map(ILane::number)
                .collect(Collectors.toList());

        Set<Integer> allLanes = new HashSet<>(laneNumbers);
        Set<Integer> occupiedLanes = new HashSet<>(lastInterval.laneNumbers());
        allLanes.removeAll(occupiedLanes);

        accidentZoneIntervalParam.setMlFromLaneNumber(new ArrayList<>(allLanes));

        IAccidentZoneInterval accidentZoneInterval = accZone.addAccidentZoneInterval(accidentZoneIntervalParam);

        DynaAccidentZoneIntervalParam accidentZoneIntervalParam1 = new DynaAccidentZoneIntervalParam();
        accidentZoneIntervalParam1.setAccidentZoneId(accidentZoneIntervalParam.getAccidentZoneId());
        accidentZoneIntervalParam1.setStartTime(accidentZoneIntervalParam.getEndTime());
        accidentZoneIntervalParam1.setEndTime(accidentZoneIntervalParam.getEndTime() + 300);
        accidentZoneIntervalParam1.setLength(accidentZoneIntervalParam.getLength());
        accidentZoneIntervalParam1.setLocation(accidentZoneIntervalParam.getLocation());
        accidentZoneIntervalParam1.setLimitedSpeed(accidentZoneIntervalParam.getLimitedSpeed());
        accidentZoneIntervalParam1.setControlLength(accidentZoneIntervalParam.getControlLength());
        accidentZoneIntervalParam1.setMlFromLaneNumber(accidentZoneIntervalParam.getMlFromLaneNumber());

        IAccidentZoneInterval accidentZoneInterval1 = accZone.addAccidentZoneInterval(accidentZoneIntervalParam1);

        accidentZoneIntervalParam.setControlLength(lastInterval.controlLength() + 10);
        accZone.updateAccidentZoneInterval(accidentZoneIntervalParam);

        System.out.printf("获取所有事故时段=%s, " +
                        "根据开始时间查询事故时段=%s%n",
                accZone.accidentZoneIntervals(),
                accZone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime()));


    }

```



### 2.24. IAccidentZoneInterval

事故时段接口, 方法如下: 

 **long intervalId();**

获取事故时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段ID为%s%n", accidentZone.id(), interval.intervalId());
                            }
                        }
                    }
                }
```

 **long accidentZoneId();**

获取所属事故区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段所属事故区ID为%s%n", accidentZone.id(), interval.accidentZoneId());
                            }
                        }
                    }
                }
```

 **long startTime();**

获取事故区开始时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段开始时间为%s%n", accidentZone.id(), interval.startTime());
                            }
                        }
                    }
                }
```

 **long endTime();**

获取事故区结束时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段结束时间为%s%n", accidentZone.id(), interval.endTime());
                            }
                        }
                    }
                }
```

 **double length(UnitOfMeasure unit);**

获取事故区在该时段的长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段长度为%s%n", accidentZone.id(), interval.length());
                                System.out.printf("事故区%s的事故时段长度（米制）为%s%n", accidentZone.id(), interval.length(UnitOfMeasure.Metric));
                            }
                        }
                    }
                }
```

 **double location(UnitOfMeasure unit);**

获取事故区在该时段的距起点距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段距起点距离为%s%n", accidentZone.id(), interval.location());
                                System.out.printf("事故区%s的事故时段距起点距离（米制）为%s%n", accidentZone.id(), interval.location(UnitOfMeasure.Metric));
                            }
                        }
                    }
                }
```

 **double limitedSpeed(UnitOfMeasure unit);**

获取事故区在该时段的限速, 默认单位: 像素(km/h), 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位(km/h), Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段限速为%s%n", accidentZone.id(), interval.limitedSpeed());
                                System.out.printf("事故区%s的事故时段限速（米制）为%s%n", accidentZone.id(), interval.limitedSpeed(UnitOfMeasure.Metric));
                            }
                        }
                    }
                }
```

 **double controlLength(UnitOfMeasure unit);**

获取事故区在该时段的控制距离（车辆距离事故区起点该距离内, 强制变道）, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段控制距离为%s%n", accidentZone.id(), interval.controlLength());
                                System.out.printf("事故区%s的事故时段控制距离（米制）为%s%n", accidentZone.id(), interval.controlLength(UnitOfMeasure.Metric));
                            }
                        }
                    }
                }
```

 **ArrayList<Integer> laneNumbers();**

获取事故区在该时段的占用车道序号

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> lAccidentZones = netiface.accidentZones();
                        for (IAccidentZone accidentZone : lAccidentZones) {
                            List<IAccidentZoneInterval> lAccidentZoneIntervals = accidentZone.accidentZoneIntervals();
                            for (IAccidentZoneInterval interval : lAccidentZoneIntervals) {
                                System.out.printf("事故区%s的事故时段占用车道序号为%s%n", accidentZone.id(), interval.laneNumbers());
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace(); 
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IAccidentZone> acczones = netiface.accidentZones();
                        if (!acczones.isEmpty()) {
                            IAccidentZone acczone = netiface.findAccidentZone(acczones.get(0).id());
                            showAccidentZoneIntervalAttr(acczone);
                        }
                    }
                }
private static void showAccidentZoneIntervalAttr(IAccidentZone acczone) {
        List<IAccidentZoneInterval> intervals = acczone.accidentZoneIntervals();
        if (!intervals.isEmpty()) {
            IAccidentZoneInterval interval = intervals.get(0);
            System.out.printf(
                    "获取所属事故区ID=%s, %n" +
                            "获取事故时段开始时间=%s, 获取事故时段结束时间=%s, %n" +
                            "获取事故区该时段长度（像素制）=%s, 米制=%s, %n" +
                            "获取事故区该时段限速（像素制）=%s km/h, 米制=%s, %n" +
                            "获取事故区该时段控制距离（像素制）=%s, 米制=%s, %n" +
                            "获取事故区该时段占用车道序号=%s%n",
                    interval.accidentZoneId(),
                    interval.startTime(),
                    interval.endTime(),
                    interval.location(),
                    interval.location(UnitOfMeasure.Metric),
                    interval.limitedSpeed(),
                    interval.limitedSpeed(UnitOfMeasure.Metric),
                    interval.controlLength(),
                    interval.controlLength(UnitOfMeasure.Metric),
                    interval.laneNumbers()
            );
        } else {
            System.out.println("当前事故区无事故时段数据");
        }
    }
```





### 2.25. IRoadWorkZone

施工区接口, 方法如下: 

 **long id();**

获取当前施工区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
            System.out.printf("施工区ID为%s%n", roadWorkZone.id());
        }
    }
}
```

 **String name();**

获取施工区名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区名称=%s%n", roadWorkZone.name());
                        }
                    }
                }
```

 **double location(UnitOfMeasure unit);**

获取施工区距所在路段起点的距离, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区距所在路段起点的距离=%s%n", roadWorkZone.location());
                            System.out.printf("施工区距所在路段起点的距离（米制）=%s%n", roadWorkZone.location(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double zoneLength(UnitOfMeasure unit);**

获取施工区长度, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区长度=%s%n", roadWorkZone.zoneLength());
                            System.out.printf("施工区长度（米制）=%s%n", roadWorkZone.zoneLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double limitSpeed(UnitOfMeasure unit);**

施工区限速 , 默认单位: 像素（km/h）, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区限速=%s%n", roadWorkZone.limitSpeed());
                            System.out.printf("施工区限速（米制）=%s%n", roadWorkZone.limitSpeed(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long sectionId();**

获取施工区所在路段或连接段的ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区所在路段或连接段ID为%s%n", roadWorkZone.sectionId());
                        }
                    }
                }
```

 **String sectionName();**

获取施工区所在路段或连接段的名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区所在路段或连接段名称=%s%n", roadWorkZone.sectionName());
                        }
                    }
                }
```

 **String sectionType();**

获取施工区所在道路的道路类型, link: 路段, connector: 连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区所在道路的道路类型=%s%n", roadWorkZone.sectionType());
                        }
                    }
                }
```

 ** ArrayList<ILaneObject> laneObjects();**

获取施工区所占的车道列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区所占的车道列表=%s%n", roadWorkZone.laneObjects());
                        }
                    }
                }
```

 **ArrayList<Long> laneObjectIds();**

获取施工区所占的车道ID列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区所占的车道ID列表=%s%n", roadWorkZone.laneObjectIds());
                        }
                    }
                }
```

 **double upCautionLength(UnitOfMeasure unit);**

获取施工区上游警示区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区上游警示区长度=%s%n", roadWorkZone.upCautionLength());
                            System.out.printf("施工区上游警示区长度（米制）=%s%n", roadWorkZone.upCautionLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double upTransitionLength(UnitOfMeasure unit);**

获取施工区上游过渡区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区上游过渡区长度=%s%n", roadWorkZone.upTransitionLength());
                            System.out.printf("施工区上游过渡区长度（米制）=%s%n", roadWorkZone.upTransitionLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double upBufferLength(UnitOfMeasure unit);**

获取施工区上游缓冲区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区上游缓冲区长度=%s%n", roadWorkZone.upBufferLength());
                            System.out.printf("施工区上游缓冲区长度（米制）=%s%n", roadWorkZone.upBufferLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double downTransitionLength(UnitOfMeasure unit);**

获取施工区下游过渡区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区下游过渡区长度=%s%n", roadWorkZone.downTransitionLength());
                            System.out.printf("施工区下游过渡区长度（米制）=%s%n", roadWorkZone.downTransitionLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double downTerminationLength(UnitOfMeasure unit);**

获取施工区下游终止区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区下游终止区长度=%s%n", roadWorkZone.downTerminationLength());
                            System.out.printf("施工区下游终止区长度（米制）=%s%n", roadWorkZone.downTerminationLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long duration();**

施工持续时间, 单位: 秒。自仿真过程创建后, 持续时间大于此值, 则移除

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工持续时间=%s%n", roadWorkZone.duration());
                        }
                    }
                }
```

 **boolean isBorrowed();**

获取施工区是否被借道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IRoadWorkZone> lRoadWorkZones = netiface.roadWorkZones();
                        for (IRoadWorkZone roadWorkZone : lRoadWorkZones) {
                            System.out.printf("施工区是否被借道=%b%n", roadWorkZone.isBorrowed());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showRoadWorkZoneAttr(netiface);
    }
}
private static void showRoadWorkZoneAttr(NetInterface netiface) {
        List<IRoadWorkZone> roadworkzones = netiface.roadWorkZones();
        if (!roadworkzones.isEmpty()) {
            IRoadWorkZone roadworkzone = roadworkzones.get(0);
            System.out.printf(
                    "施工区ID=%s, 获取施工区名称=%s, %n" +
                            "获取施工区距路段起点距离（像素制）=%s, 米制=%s, %n" +
                            "获取施工区长度（像素制）=%s, 米制=%s, %n" +
                            "获取施工区限速（像素制）=%s, 米制=%s, %n" +
                            "获取施工区所在路段/连接段ID=%s, 名称=%s, %n" +
                            "获取施工区所在道路类型=%s, %n" +
                            "获取施工区所占车道列表=%s, 车道ID列表=%s, %n" +
                            "获取上游警示区长度（像素制）=%s, 米制=%s, %n" +
                            "获取上游过渡区长度（像素制）=%s, 米制=%s, %n" +
                            "获取上游缓冲区长度（像素制）=%s, 米制=%s, %n" +
                            "获取下游过渡区长度（像素制）=%s, 米制=%s, %n" +
                            "施工持续时间（秒）=%s, 获取施工区是否被借道=%b%n",
                    roadworkzone.id(),
                    roadworkzone.name(),
                    roadworkzone.location(),
                    roadworkzone.location(UnitOfMeasure.Metric),
                    roadworkzone.zoneLength(),
                    roadworkzone.zoneLength(UnitOfMeasure.Metric),
                    roadworkzone.limitSpeed(),
                    roadworkzone.limitSpeed(UnitOfMeasure.Metric),
                    roadworkzone.sectionId(),
                    roadworkzone.sectionName(),
                    roadworkzone.sectionType(),
                    roadworkzone.laneObjects(),
                    roadworkzone.laneObjectIds(),
                    roadworkzone.upCautionLength(),
                    roadworkzone.upCautionLength(UnitOfMeasure.Metric),
                    roadworkzone.upTransitionLength(),
                    roadworkzone.upTransitionLength(UnitOfMeasure.Metric),
                    roadworkzone.upBufferLength(),
                    roadworkzone.upBufferLength(UnitOfMeasure.Metric),
                    roadworkzone.downTransitionLength(),
                    roadworkzone.downTransitionLength(UnitOfMeasure.Metric),
                    roadworkzone.duration(),
                    roadworkzone.isBorrowed()
            );
        } else {
            System.out.println("无施工区数据");
        }
    }
```





### 2.26. ILimitedZone

限行区接口（借道施工的被借车道, 限制对向车辆行走的区域）, 方法如下: 

 **long id();**

获取限行区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区ID=%s%n", limitedZone.id());
                        }
                    }
                }
```

 **String name();**

获取限行区名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区名称=%s%n", limitedZone.name());
                        }
                    }
                }
```

 **double location(UnitOfMeasure unit);**

获取距起点距离, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区距起点距离=%s%n", limitedZone.location());
                            System.out.printf("限行区距起点距离（米制）=%s%n", limitedZone.location(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double zoneLength(UnitOfMeasure unit);**

获取限行区长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区长度=%s%n", limitedZone.zoneLength());
                            System.out.printf("限行区长度（米制）=%s%n", limitedZone.zoneLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double limitSpeed(UnitOfMeasure unit);**

获取限行区限速, 默认单位: 像素(km/h), 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区限速=%s%n", limitedZone.limitSpeed());
                            System.out.printf("限行区限速（米制）=%s%n", limitedZone.limitSpeed(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long sectionId();**

获取限行区所在路段或连接段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区所在路段或连接段ID=%s%n", limitedZone.sectionId());
                        }
                    }
                }
```

 **String sectionName();**

获取限行区所在路段或连接段的名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区所在路段或连接段名称=%s%n", limitedZone.sectionName());
                        }
                    }
                }
```

 **String sectionType();**

获取限行区所在道路的类型: "link"表示路段, "connector"表示连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区所在道路的类型=%s%n", limitedZone.sectionType());
                        }
                    }
                }
```

 **ArrayList<ILaneObject> laneObjects();**

获取限行区所在车道对象列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区所在车道对象列表=%s%n", limitedZone.laneObjects());
                        }
                    }
                }
```

 **long duration();**

获取限行区的持续时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ILimitedZone> lLimitedZones = netiface.limitedZones();
                        for (ILimitedZone limitedZone : lLimitedZones) {
                            System.out.printf("限行区持续时间=%s%n", limitedZone.duration());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showLimitZoneAttr(netiface);
                    }
                }

private static void showLimitZoneAttr(NetInterface netiface) {
        List<ILimitedZone> limitedZones = netiface.limitedZones();
        ILimitedZone limitedZone = limitedZones.get(0);
        ILimitedZone limitedZone1 = netiface.findLimitedZone(limitedZone.id()); // 按ID查找限行区

        // 打印详细属性
        System.out.printf(
                "获取限行区ID=%s, 获取限行区名称=%s, %n" +
                        "获取限行区距路段起点距离（像素制）=%s, 米制=%s, %n" +
                        "获取限行区长度（像素制）=%s, 米制=%s, %n" +
                        "获取限行区限速（像素制）=%s, 米制=%s, %n" +
                        "获取路段或连接段ID=%s, 名称=%s, %n" +
                        "获取道路类型（link=路段, connector=连接段）=%s, %n" +
                        "获取相关车道对象列表=%s, %n" +
                        "获取限行持续时间（秒）=%s%n",
                limitedZone.id(),
                limitedZone.name(),
                limitedZone.location(),
                limitedZone.location(UnitOfMeasure.Metric),
                limitedZone.zoneLength(),
                limitedZone.zoneLength(UnitOfMeasure.Metric),
                limitedZone.limitSpeed(),
                limitedZone.limitSpeed(UnitOfMeasure.Metric),
                limitedZone.sectionId(),
                limitedZone.sectionName(),
                limitedZone.sectionType(),
                limitedZone.laneObjects(),
                limitedZone.duration()
        );
    }
```







### 2.27. IReconstruction

改扩建接口, 此接口最好是在构造路网的最后调用, 避免后续其他接口调用原因导致创建施工区的路段线性被更改

 **long id();**

获取改扩建对象ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建对象ID=%s%n", reconstruction.id());
                        }
                    }
                }
```

 **long roadWorkZoneId();**

获取改扩建对象的起始施工区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建对象的起始施工区ID=%s%n", reconstruction.roadWorkZoneId());
                        }
                    }
                }
```

 **long limitedZoneId();**

获取改扩建对象的被借道限行区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建对象的被借道限行区ID=%s%n", reconstruction.limitedZoneId());
                        }
                    }
                }
```

 **double passagewayLength(UnitOfMeasure unit);**

获取改扩建对象的保通长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建对象的保通长度=%s%n", reconstruction.passagewayLength());
                            System.out.printf("改扩建对象的保通长度（米制）=%s%n", reconstruction.passagewayLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long duration();**

获取改扩建的持续时间, 单位: 秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建的持续时间=%s%n", reconstruction.duration());
                        }
                    }
                }
```

 **int borrowedNum();**

获取改扩建的借道车道数量

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("改扩建的借道车道数量=%s%n", reconstruction.borrowedNum());
                        }
                    }
                }
```

 **double passagewayLimitedSpeed(UnitOfMeasure unit);**

获取保通开口限速, 默认单位: 像素（km/h）, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("保通开口限速=%s%n", reconstruction.passagewayLimitedSpeed());
                            System.out.printf("保通开口限速（米制）=%s%n", reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **DynaReconstructionParam dynaReconstructionParam();**

获取改扩建动态参数; 入参数据结构见pyi文件的 Online.DynaReconstructionParam类 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReconstruction> lReconstructions = netiface.reconstructions();
                        for (IReconstruction reconstruction : lReconstructions) {
                            System.out.printf("获取改扩建动态参数=%s%n", reconstruction.dynaReconstructionParam());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showReconstructionAttr(netiface);
                    }
                }
private static void showReconstructionAttr(NetInterface netiface) {
        List<IReconstruction> reconstructions = netiface.reconstructions();
        if (!reconstructions.isEmpty()) {
            IReconstruction reconstruction = reconstructions.get(0);
            IReconstruction reconstruction1 = netiface.findReconstruction(reconstruction.id()); // 按ID查找改扩建对象

            System.out.printf(
                    "获取改扩建ID=%s, 获取改扩建起始施工区ID=%s, %n" +
                            "获取被借道限行区ID=%s, %n" +
                            "获取保通长度（像素制）=%s, 米制=%s, %n" +
                            "获取保通开口限速（像素制）=%s, 米制=%s, %n" +
                            "获取改扩建持续时间=%s, 获取借道数量=%s, %n" +
                            "获取改扩建动态参数（米制）=%s%n",
                    reconstruction.id(),
                    reconstruction.roadWorkZoneId(),
                    reconstruction.limitedZoneId(),
                    reconstruction.passagewayLength(),
                    reconstruction.passagewayLength(UnitOfMeasure.Metric),
                    reconstruction.passagewayLimitedSpeed(),
                    reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric),
                    reconstruction.duration(),
                    reconstruction.borrowedNum(),
                    reconstruction.dynaReconstructionParam()
            );
        } else {
            System.out.println("无改扩建对象数据");
        }
    }
```



### 2.28. IReduceSpeedArea

限速区接口

 **long id()**

获取限速区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区ID=%s%n", reduceSpeedArea.id());
                        }
                    }
                }
```

 **String name()**

获取限速区名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区名称=%s%n", reduceSpeedArea.name());
                        }
                    }
                }
```

 **double location(UnitOfMeasure unit)**

获取距起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区距起点距离=%s%n", reduceSpeedArea.location());
                            System.out.printf("限速区距起点距离（米制）=%s%n", reduceSpeedArea.location(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double areaLength(UnitOfMeasure unit)**

获取限速区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区长度=%s%n", reduceSpeedArea.areaLength());
                            System.out.printf("限速区长度（米制）=%s%n", reduceSpeedArea.areaLength(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **long sectionId()**

获取限速区所在路段或连接段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区所在路段或连接段ID=%s%n", reduceSpeedArea.sectionId());
                        }
                    }
                }
```

 **int laneNumber()**

获取限速区车道序号

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区车道序号=%s%n", reduceSpeedArea.laneNumber());
                        }
                    }
                }
```

 **int toLaneNumber()**

获取限速区获取目标车道序号（当限速区设置在连接段时, 返回值非空）

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("限速区获取目标车道序号=%s%n", reduceSpeedArea.toLaneNumber());
                        }
                    }
                }
```

 **IReduceSpeedInterval addReduceSpeedInterval(DynaReduceSpeedIntervalParam param)**

添加限速时段

参数: 
[ in ]  param: 限速时段参数, 入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            DynaReduceSpeedIntervalParam param = new DynaReduceSpeedIntervalParam();
                            param.setStartTime(3600);
                            param.setEndTime (7200);

                            DynaReduceSpeedVehiTypeParam type1 = new DynaReduceSpeedVehiTypeParam();
                            type1.setVehicleTypeCode(2);
                            type1.setAvgSpeed(10);
                            type1.setSpeedSD(5);
                            param.setReduceSpeedVehicleTypeParams(new ArrayList<>(Arrays.asList(type1)));

                            System.out.printf("添加前限速时段列表=%s%n", reduceSpeedArea.reduceSpeedIntervals());
                            IReduceSpeedInterval interval = reduceSpeedArea.addReduceSpeedInterval(param);
                            System.out.printf("添加后限速时段列表=%s%n", reduceSpeedArea.reduceSpeedIntervals());
                        }
                    }
                }
```

 **void removeReduceSpeedInterval(long id)**

移除限速时段

参数: 
[ in ] id: 限速时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            DynaReduceSpeedIntervalParam param = new DynaReduceSpeedIntervalParam();

                            param.setStartTime(3601);
                            param.setEndTime (7200);

                            DynaReduceSpeedVehiTypeParam type1 = new DynaReduceSpeedVehiTypeParam();
                            type1.setVehicleTypeCode(2);
                            type1.setAvgSpeed(10);
                            type1.setSpeedSD(5);

                            param.setReduceSpeedVehicleTypeParams(new ArrayList<>(Arrays.asList(type1)));

                            IReduceSpeedInterval interval = reduceSpeedArea.addReduceSpeedInterval(param);
                            System.out.printf("移除前限速时段列表=%s%n", reduceSpeedArea.reduceSpeedIntervals());
                            // 移除刚添加的时段
                            reduceSpeedArea.removeReduceSpeedInterval(interval.id());
                            System.out.printf("移除后限速时段列表=%s%n", reduceSpeedArea.reduceSpeedIntervals());
                        }
                    }
                }
```

 **boolean updateReduceSpeedInterval(DynaReduceSpeedIntervalParam param)**

更新限速时段

参数: 
[ in ]  param: 限速时段参数, 入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            DynaReduceSpeedIntervalParam param = new DynaReduceSpeedIntervalParam();

                            param.setStartTime(3601);
                            param.setEndTime (7200);

                            DynaReduceSpeedVehiTypeParam type1 = new DynaReduceSpeedVehiTypeParam();
                            type1.setVehicleTypeCode(2);
                            type1.setAvgSpeed(10);
                            type1.setSpeedSD(5);

                            param.setReduceSpeedVehicleTypeParams(new ArrayList<>(Arrays.asList(type1)));

                            // 添加并移除时段
                            IReduceSpeedInterval interval = reduceSpeedArea.addReduceSpeedInterval(param);
                            reduceSpeedArea.removeReduceSpeedInterval(interval.id());
                            // 重新添加时段
                            IReduceSpeedInterval interval1 = reduceSpeedArea.addReduceSpeedInterval(param);
                            System.out.printf("reduceSpeedArea.addReduceSpeedInterval(param) 添加成功=%s%n", interval1 != null);

                            param.setId(interval1.id());
                            param.setReduceSpeedAreaId(reduceSpeedArea.id());
                            param.setStartTime(7200);
                            param.setEndTime(10000);
                            boolean flag = reduceSpeedArea.updateReduceSpeedInterval(param);
                            System.out.printf("reduceSpeedArea.updateReduceSpeedInterval(param) 更新成功=%b%n", flag);
                        }
                    }
                }
```

 **ArrayList<IReduceSpeedInterval> reduceSpeedIntervals()**

获取限速时段列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("获取限速时段列表=%s%n", reduceSpeedArea.reduceSpeedIntervals());
                        }
                    }
                }
```

 **IReduceSpeedInterval findReduceSpeedIntervalById(long id)**

根据ID获取限速时段

参数: 
[ in ] id: 限速时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> intervals = reduceSpeedArea.reduceSpeedIntervals();
                            if (!intervals.isEmpty()) {
                                System.out.printf("根据ID获取限速时段=%s%n", reduceSpeedArea.findReduceSpeedIntervalById(intervals.get(0).id()));
                            }
                        }
                    }
                }
```

 **IReduceSpeedInterval findReduceSpeedIntervalByStartTime(long startTime)**

根据起始时间获取限速时段

参数: 
[ in ] startTime: 起始时间


举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> intervals = reduceSpeedArea.reduceSpeedIntervals();
                            if (!intervals.isEmpty()) {
                                System.out.printf("根据起始时间获取限速时段=%s%n", reduceSpeedArea.findReduceSpeedIntervalByStartTime(intervals.get(0).intervalStartTime()));
                            }
                        }
                    }
                }
```

 **Vector<Point> polygon()**

获取限速区获取多边型轮廓

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            System.out.printf("获取限速区获取多边型轮廓=%s%n", reduceSpeedArea.polygon());
                        }
                    }
                }
```

### 2.29. IReduceSpeedInterval

限速时段接口

 **long id()**

获取限速时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                System.out.printf("获取限速时段ID=%s%n", reduceSpeedInterval.id());
                            }
                        }
                    }
                }
```

 **long reduceSpeedAreaId()**

获取所属限速区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                System.out.printf("获取所属限速区ID=%s%n", reduceSpeedInterval.reduceSpeedAreaId());
                            }
                        }
                    }
                }
```

 **long intervalStartTime()**

获取开始时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                System.out.printf("获取开始时间=%s%n", reduceSpeedInterval.intervalStartTime());
                            }
                        }
                    }
                }
```

 **long intervalEndTime()**

获取结束时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                System.out.printf("获取结束时间=%s%n", reduceSpeedInterval.intervalEndTime());
                            }
                        }
                    }
                }
```

 **IReduceSpeedVehiType addReduceSpeedVehiType(DynaReduceSpeedVehiTypeParam param)**

添加限速车型

参数: 
[ in ] param: 限速车型参数, 数据结构见Online.DynaReduceSpeedVehiTypeParam

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                DynaReduceSpeedVehiTypeParam param = new DynaReduceSpeedVehiTypeParam();
                                param.setVehicleTypeCode(13);
                                param.setAvgSpeed(10);
                                param.setSpeedSD(5);
                                IReduceSpeedVehiType interval = reduceSpeedInterval.addReduceSpeedVehiType(param);
                                System.out.printf("添加限速车型成功=%s%n", interval != null);
                            }
                        }
                    }
                }
```

 **void removeReduceSpeedVehiType(long id)**

移除限速车型

参数: 
[ in ]  id: 限速车型ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType reduceSpeedVehiType : lReduceSpeedVehiTypes) {
                                    System.out.printf("移除限速车型%s%n", reduceSpeedVehiType.id());
                                    reduceSpeedInterval.removeReduceSpeedVehiType(reduceSpeedVehiType.id());
                                }
                            }
                        }
                    }
                }
```

 **boolean updateReduceSpeedVehiType(DynaReduceSpeedVehiTypeParam param)**

更新限速车型

参数: 
[ in ] param: 限速车型参数, 数据结构见Online.DynaReduceSpeedVehiTypeParam

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes();
                                if (!vehiTypes.isEmpty()) {
                                    IReduceSpeedVehiType vehiType = vehiTypes.get(vehiTypes.size() - 1);

                                    DynaReduceSpeedVehiTypeParam param = new DynaReduceSpeedVehiTypeParam();
                                    param.setVehicleTypeCode(vehiType.vehiTypeCode());
                                    param.setAvgSpeed(vehiType.averageSpeed() + 10);
                                    param.setSpeedSD(vehiType.speedStandardDeviation() + 5);
                                    param.setReduceSpeedAreaId(reduceSpeedArea.id());
                                    param.setReduceSpeedIntervalId(reduceSpeedInterval.id());
                                    param.setId(vehiType.id());

                                    boolean b = reduceSpeedInterval.updateReduceSpeedVehiType(param);
                                    System.out.printf("更新限速车型成功=%b%n", b);
                                }
                            }
                        }
                    }
                }
```

 **ArrayList<IReduceSpeedVehiType> reduceSpeedVehiTypes()**

获取本时段限速车型及限速参数列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                System.out.printf("获取本时段限速车型及限速参数列表=%s%n", reduceSpeedInterval.reduceSpeedVehiTypes());
                            }
                        }
                    }
                }
```

 **IReduceSpeedVehiType findReduceSpeedVehiTypeById(long id)**

根据车型代码获取限速车型

参数: 
[ in ] id: 限速车型ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> lReduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : lReduceSpeedAreas) {
                            List<IReduceSpeedInterval> lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval reduceSpeedInterval : lReduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType reduceSpeedVehiType : lReduceSpeedVehiTypes) {
                                    System.out.printf("根据ID获取限速车型=%s%n", reduceSpeedInterval.findReduceSpeedVehiTypeById(reduceSpeedVehiType.id()));
                                }
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showReduceSpeedAreaAttr(netiface);
                    }
                }
private static void showReduceSpeedAreaAttr(NetInterface netiface) {
        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
        if (!reduceSpeedAreas.isEmpty()) {
            IReduceSpeedArea reduceSpeedArea = reduceSpeedAreas.get(0);
            IReduceSpeedArea reduceSpeedArea1 = netiface.findReduceSpeedArea(reduceSpeedAreas.get(0).id());

            // 打印限速区基础属性
            System.out.printf(
                    "获取限速区ID=%s, 获取限速区名称=%s, %n" +
                            "获取距起点距离=%s, 米制=%s, %n" +
                            "获取限速区长度（像素制）=%s, 米制=%s, %n" +
                            "获取路段或连接段ID=%s, %n" +
                            "获取车道序号=%s, 获取目标车道序号=%s, %n" +
                            "获取限速时段列表=%s, %n" +
                            "获取多边型轮廓=%s%n",
                    reduceSpeedArea.id(),
                    reduceSpeedArea.name(),
                    reduceSpeedArea.location(),
                    reduceSpeedArea.location(UnitOfMeasure.Metric),
                    reduceSpeedArea.areaLength(),
                    reduceSpeedArea.areaLength(UnitOfMeasure.Metric),
                    reduceSpeedArea.sectionId(),
                    reduceSpeedArea.laneNumber(),
                    reduceSpeedArea.toLaneNumber(),
                    reduceSpeedArea.reduceSpeedIntervals(),
                    reduceSpeedArea.polygon()
            );

            System.out.println("添加限速时段时段");
            List<IReduceSpeedInterval> intervals = reduceSpeedArea.reduceSpeedIntervals();
            if (!intervals.isEmpty()) {
                System.out.printf(
                        "获取所有限速时段=%s, %n" +
                                "根据ID获取限速时段=%s, %n" +
                                "根据开始时间查询限速时段=%s%n",
                        intervals,
                        reduceSpeedArea.findReduceSpeedIntervalById(intervals.get(0).id()),
                        reduceSpeedArea.findReduceSpeedIntervalByStartTime(intervals.get(0).intervalStartTime())
                );
            }

            // 演示限速时段添加、移除、更新操作
            DynaReduceSpeedIntervalParam param = new DynaReduceSpeedIntervalParam();
            param.setStartTime(100); // 新增时段需与已有时段不冲突
            param.setEndTime(500);

            DynaReduceSpeedVehiTypeParam type1 = new DynaReduceSpeedVehiTypeParam();
            type1.setVehicleTypeCode(2);
            type1.setAvgSpeed(10);
            type1.setSpeedSD(5);

            param.setReduceSpeedVehicleTypeParams(new java.util.ArrayList<>(Arrays.asList(type1)));

            // 添加并移除时段
            IReduceSpeedInterval interval = reduceSpeedArea.addReduceSpeedInterval(param);
            reduceSpeedArea.removeReduceSpeedInterval(interval.id());
            // 重新添加时段
            IReduceSpeedInterval interval1 = reduceSpeedArea.addReduceSpeedInterval(param);
            System.out.printf("reduceSpeedArea.addReduceSpeedInterval(param) 添加成功=%s%n", interval1 != null);

            // 更新时段
            boolean flag = reduceSpeedArea.updateReduceSpeedInterval(param);
            System.out.printf("reduceSpeedArea.updateReduceSpeedInterval(param) 更新成功=%b%n", flag);
        } else {
            System.out.println("无限速区数据");
        }
    }            
```





### 2.30. IReduceSpeedVehiType

限速车型接口

 **long id();**

获取限速车型ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取限速车型ID=%s%n", vehiType.id());
                                }
                            }
                        }
                    }
                }
```

 **long intervalId();**

获取所属限速时段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取所属限速时段ID=%s%n", vehiType.intervalId());
                                }
                            }
                        }
                    }
                }
```

 **long reduceSpeedAreaId();**

获取所属限速区ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取所属限速区ID=%s%n", vehiType.reduceSpeedAreaId());
                                }
                            }
                        }
                    }
                }
```

 **long vehiTypeCode();**

获取车型编码

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取车型编码=%s%n", vehiType.vehiTypeCode());
                                }
                            }
                        }
                    }
                }
```

 **double averageSpeed(UnitOfMeasure unit);**

获取平均车速, 默认单位: 像素/秒, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取平均车速=%s%n", vehiType.averageSpeed());
                                    System.out.printf("获取平均车速, 单位: 米/秒=%s%n", vehiType.averageSpeed(UnitOfMeasure.Metric));
                                }
                            }
                        }
                    }
                }
```

 **double speedStandardDeviation(UnitOfMeasure unit);**

获取车速标准差, 默认单位: 像素/秒, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        for (IReduceSpeedArea reduceSpeedArea : reduceSpeedAreas) {
                            List<IReduceSpeedInterval> reduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals();
                            for (IReduceSpeedInterval interval : reduceSpeedIntervals) {
                                List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
                                for (IReduceSpeedVehiType vehiType : vehiTypes) {
                                    System.out.printf("获取车速标准差=%s%n", vehiType.speedStandardDeviation());
                                    System.out.printf("获取车速标准差, 单位: 米/秒=%s%n", vehiType.speedStandardDeviation(UnitOfMeasure.Metric));
                                }
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IReduceSpeedArea> reduceSpeedAreas = netiface.reduceSpeedAreas();
                        if (!reduceSpeedAreas.isEmpty()) {
                            IReduceSpeedArea reduceSpeedArea = reduceSpeedAreas.get(0);
                            showReduceSpeedAreaIntervalAttr(reduceSpeedArea);
                        } else {
                            System.out.println("无可用限速区数据");
                        }
                    }
                }
private static void showReduceSpeedAreaIntervalAttr(IReduceSpeedArea reduceSpeedArea) {
        List<IReduceSpeedInterval> intervals = reduceSpeedArea.reduceSpeedIntervals();
        if (!intervals.isEmpty()) {
            IReduceSpeedInterval interval = intervals.get(0);
            System.out.printf("获取限速时段ID=%s, 获取所属限速区ID=%s, " +
                            "获取开始时间=%s, 获取结束时间=%s%n",
                    interval.id(), interval.reduceSpeedAreaId(),
                    interval.intervalStartTime(), interval.intervalEndTime());

            List<IReduceSpeedVehiType> vehiTypes = interval.reduceSpeedVehiTypes();
            if (!vehiTypes.isEmpty()) {
                IReduceSpeedVehiType firstVehiType = vehiTypes.get(0);
                IReduceSpeedVehiType vehiTypeById = interval.findReduceSpeedVehiTypeById(firstVehiType.id());
                IReduceSpeedVehiType vehiTypeByCode = interval.findReduceSpeedVehiTypeByCode(firstVehiType.vehiTypeCode());

                System.out.printf("按ID查询到的限速车型: %s, 按编码查询到的限速车型: %s%n",
                        vehiTypeById != null ? vehiTypeById.vehiTypeCode() : "未找到",
                        vehiTypeByCode != null ? vehiTypeByCode.id() : "未找到");
            } else {
                System.out.println("当前限速时段无关联限速车型");
            }
        } else {
            System.out.println("当前限速区无可用时段数据");
        }
    }               
```





### 2.31. ITollLane

收费车道接口

 **long id()**

获取收费车道ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<ITollLane> tollLanes = netiface.tollLanes();
            for (ITollLane tollLane : tollLanes) {
                System.out.printf("获取收费车道ID=%s%n", tollLane.id());
            }
        }
    }
```

 **String name()**

获取收费车道名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            System.out.printf("获取收费车道名称=%s%n", tollLane.name());
                        }
                    }
                }
```

 **double distance()**

获取收费车道起点距当前所在路段起始位置的距离。单位: 米 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            System.out.printf("获取收费车道起点距当前所在路段起始位置的距离=%s%n", tollLane.distance());
                        }
                    }
                }
```

 **void setName(String name)**

设置收费车道名称 

参数: 
[ in ] name : 收费车道名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            // 先执行设置名称（setName返回void，单独调用）
                            tollLane.setName("test lane toll");
                            // 再打印结果
                            System.out.printf("获取收费车道名称=%s%n", tollLane.name());
                        }
                    }
                }
```

 **void setWorkTime(long startTime, long endTime)**

设置收费车道的工作时间, 不设置时, 默认与仿真时间对应 

参数: 
[ in ] startTime 开始时间（秒）  
[ in ] endTime 结束时间（秒）

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            tollLane.setWorkTime(0, 3000);
                            System.out.printf("收费车道%s工作时间已设置为0-3000秒%n", tollLane.id());
                        }
                    }
                }
```

 **DynaTollLane dynaTollLane()**

获取动态收费车道信息, 具体数据结构见Online.TollStation.DynaTollLane  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            System.out.printf("获取动态收费车道信息=%s%n", tollLane.dynaTollLane());
                        }
                    }
                }
```

 **ArrayList<ITollPoint> tollPoints()**

获取收费车道所有收费点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            System.out.printf("获取收费车道所有收费点=%s%n", tollLane.tollPoints());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showTollLaneAttr(netiface);
                    }
                }
private static void showTollLaneAttr(NetInterface netiface) {
        List<ITollLane> rs = netiface.tollLanes();
        if (!rs.isEmpty()) {
            ITollLane r = rs.get(0);
            ITollLane r1 = netiface.findTollLane(rs.get(0).id()); // 按ID查找收费车道

            // 执行设置操作（set方法返回void，单独调用）
            r.setName("test lane toll");
            r.setWorkTime(0, 3000);

            // 打印详细属性
            System.out.printf(
                    "获取收费车道ID=%s, 获取收费车道名称=%s, %n" +
                            "获取距路段起始位置（米）=%s, %n" +
                            "获取动态收费车道信息=%s, %n" +
                            "获取收费车道所有收费点=%s%n",
                    r.id(),
                    r.name(), // 打印设置后的名称
                    r.distance(),
                    r.dynaTollLane(),
                    r.tollPoints()
            );
        } else {
            System.out.println("无收费车道数据");
        }
    }
```






### 2.32. ITollDecisionPoint

收费决策点接口

 **long id();**

获取收费决策点ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点ID=%s%n", tollDecisionPoint.id());
                        }
                    }
                }
```

 **String name();**

获取收费决策点名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点名称=%s%n", tollDecisionPoint.name());
                        }
                    }
                }
```

 **ILink link();**

获取收费决策点所在路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点所在路段=%s%n", tollDecisionPoint.link());
                        }
                    }
                }
```

 **double distance();**

获取收费决策点距离所在路段起点的距离, 默认单位为米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点距离所在路段起点的距离=%s%n", tollDecisionPoint.distance());
                        }
                    }
                }
```

 **ArrayList<ITollRouting> routings();**

获取收费决策点的所有收费路径

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点的所有收费路径=%s%n", tollDecisionPoint.routings());
                        }
                    }
                }
```

 **ArrayList<DynaTollDisInfo> tollDisInfoList();**

获取收费决策点收费路径分配信息列表, 返回值为 TollDisInfo; 数据结构见Online.TollStation.TollDisInfo

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点收费路径分配信息列表=%s%n", tollDecisionPoint.tollDisInfoList());
                        }
                    }
                }
```

 **void updateTollDisInfoList(ArrayList<DynaTollDisInfo> arg0);**

更新收费分配信息列表, 先创建决策点, 再更新决策点的车道分配信息 

参数: 
[ in ] Online.TollStation.DynaTollDisInfo: 收费分配信息列表 数据结构见Online.TollStation.DynaTollDisInfo

举例: 

```java
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            tollDecisionPoint.updateTollDisInfoList(tollDecisionPoint.tollDisInfoList());
                            System.out.print("更新收费分配信息列表 ");
                        }
                    }
                }
```

 **Vector<Point> polygon();**

获取收费决策点多边形轮廓

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            System.out.printf("获取收费决策点多边形轮廓=%s%n", tollDecisionPoint.polygon());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showTollDecisionPointAttr(netiface);
    }
}
private static void showTollDecisionPointAttr(NetInterface netiface) {
        List<ITollDecisionPoint> rs = netiface.tollDecisionPoints();
        if (!rs.isEmpty()) {
            ITollDecisionPoint r = rs.get(0);
            IDecisionPoint r1 = netiface.findDecisionPoint(rs.get(0).id()); // 按ID查找收费决策点

            // 更新收费分配信息列表（原逻辑中直接使用现有列表更新）
              r.updateTollDisInfoList(r.tollDisInfoList());

            // 打印详细属性
            System.out.printf(
                    "获取收费决策点ID=%s, 获取收费决策点名称=%s, %n" +
                            "获取收费决策点所在路段=%s, %n" +
                            "获取距路段起始位置（米）=%s, %n" +
                            "获取相关收费路径=%s, %n" +
                            "获取收费分配信息列表=%s, %n" +
                            "获取收费决策点多边型轮廓=%s%n",
                    r.id(),
                    r.name(),
                    r.link(),
                    r.distance(),
                    r.routings(),
                    r.tollDisInfoList(),
                    r.polygon()
            );
        } else {
            System.out.println("无收费决策点数据");
        }
    }
```





### 2.33. ITollRouting

收费路径接口

 **long id();** 

获取收费路径ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取收费路径ID=%s%n", tollRouting.id());
                            }
                        }
                    }
                }
```

 **long tollDeciPointId();**

获取收费路径所属收费决策点ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取收费路径所属收费决策点ID=%s%n", tollRouting.tollDeciPointId());
                            }
                        }
                    }
                }
```

 **long tollLaneId();**

获取路径到达的收费区域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取路径到达的收费区域id=%s%n", tollRouting.tollLaneId());
                            }
                        }
                    }
                }
```

 **double calcuLength();**

获取收费决策路径长度, 单位: 米; 收费路径长度是指: 收费决策点到收费车道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取收费决策路径长度, 单位: 米=%s%n", tollRouting.calcuLength());
                            }
                        }
                    }
                }
```

 **boolean contain(ISection pRoad);**

判断输入的路段是否在当前路径上 

参数: 
[ in ] pRoad : 路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                boolean isContained = tollRouting.contain(tollDecisionPoint.link().toSection());
                                System.out.printf("判断输入的路段是否在当前路径上=%b%n", isContained);
                            }
                        }
                    }
                }
```

 **ISection nextRoad(ISection pRoad);**

获取输入路段的紧邻下游道路 

参数: 
[ in ] pRoad : 路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取输入路段的紧邻下游道路=%s%n", tollRouting.nextRoad(tollDecisionPoint.link().toSection()));
                            }
                        }
                    }
                }
```

 **ArrayList<ILink> getLinks();**

获取当前收费路径的有序路段序列

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
                        for (ITollDecisionPoint tollDecisionPoint : tollDecisionPoints) {
                            List<ITollRouting> tollRoutings = tollDecisionPoint.routings();
                            for (ITollRouting tollRouting : tollRoutings) {
                                System.out.printf("获取当前收费路径的有序路段序列=%s%n", tollRouting.getLinks());
                            }
                        }
                    }
                }
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showTollRoutingAttr(netiface);
    }
}
private static void showTollRoutingAttr(NetInterface netiface) {
        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
        if (!tollDecisionPoints.isEmpty()) {
            ITollDecisionPoint tollDecisionPoint = tollDecisionPoints.get(0);
            List<ITollRouting> routes = tollDecisionPoint.routings();
            if (!routes.isEmpty()) {
                ITollRouting r = routes.get(0);

                // 打印详细属性
                System.out.printf(
                        "获取路径ID=%s, 获取所属收费决策点ID=%s, %n" +
                                "计算路径长度（米）=%s, %n" +
                                "根据所给道路判断是否在当前路径上=%b, %n" +
                                "根据所给道路求下一条道路=%s, %n" +
                                "获取路段序列=%s%n",
                        r.id(),
                        r.tollDeciPointId(),
                        r.calcuLength(),
                        r.contain(tollDecisionPoint.link().toSection()),
                        r.nextRoad(tollDecisionPoint.link().toSection()),
                        r.getLinks()
                );
            } else {
                System.out.println("当前收费决策点无收费路径数据");
            }
        } else {
            System.out.println("无收费决策点数据");
        }
    }
```





### 2.34. ITollPoint

收费站停车点接口

 **long id()**

获取收费站停车点位ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取收费站停车点位ID=%s%n", tollPoint.id());
                            }
                        }
                    }
                }
```

 **double distance()**

获取收费站停车点距离路段起始位置的距离, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取收费站停车点距离路段起始位置的距离, 单位: 米=%s%n", tollPoint.distance());
                            }
                        }
                    }
                }
```

 **long tollLaneId()**

获取收费站停车点所在的车道ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取收费站停车点所在的车道ID=%s%n", tollPoint.tollLaneId());
                            }
                        }
                    }
                }
```

 **ITollLane tollLane()**

获取所属收费车道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取所属收费车道=%s%n", tollPoint.tollLane());
                            }
                        }
                    }
                }
```

 **boolean isEnabled()**

获取是否启用的状态

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取是否启用的状态=%s%n", tollPoint.isEnabled());
                            }
                        }
                    }
                }
```

 **boolean setEnabled(boolean enabled)**

设置当前收费站停车点是否启用, 返回是否设置成功的标签 

参数: 
[ in ] enabled : 默认为True表示启用, 若传入False则表明禁用该收费站点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("设置当前收费站停车点是否启用=%s%n", tollPoint.setEnabled(false));
                            }
                        }
                    }
                }
```

 **int tollType()**

获取收费类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取收费类型=%s%n", tollPoint.tollType());
                            }
                        }
                    }
                }
```

 **boolean setTollType(int tollType)**

设置收费类型 

参数: 
[ in ] tollType: 收费类型, 数据结构 见OnLine.TollType

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("设置收费类型=%s%n", tollPoint.setTollType(tollPoint.tollType()));
                            }
                        }
                    }
                }
```

 **int timeDisId()**

获取停车时间分布ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("获取停车时间分布ID=%s%n", tollPoint.timeDisId());
                            }
                        }
                    }
                }
```

 **boolean setTimeDisId(int timeDisId)**

设置停车时间分布ID 

参数: 
[ in ] timeDisId: 时间分布ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ITollLane> tollLanes = netiface.tollLanes();
                        for (ITollLane tollLane : tollLanes) {
                            ArrayList<ITollPoint> tollPoints = tollLane.tollPoints();
                            for (ITollPoint tollPoint : tollPoints) {
                                System.out.printf("设置停车时间分布IDID=%s%n", tollPoint.setTimeDisId(tollPoint.timeDisId()));
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showTollRoutingAttr(netiface);
                    }
                }
private static void showTollRoutingAttr(NetInterface netiface) {
        List<ITollDecisionPoint> tollDecisionPoints = netiface.tollDecisionPoints();
        if (!tollDecisionPoints.isEmpty()) {
            ITollDecisionPoint tollDecisionPoint = tollDecisionPoints.get(0);
            List<ITollRouting> routes = tollDecisionPoint.routings();
            if (!routes.isEmpty()) {
                ITollRouting r = routes.get(0);

                // 打印详细属性
                System.out.printf(
                        "获取路径ID=%s, 获取所属收费决策点ID=%s, %n" +
                                "计算路径长度（米）=%s, %n" +
                                "根据所给道路判断是否在当前路径上=%b, %n" +
                                "根据所给道路求下一条道路=%s, %n" +
                                "获取路段序列=%s%n",
                        r.id(),
                        r.tollDeciPointId(),
                        r.calcuLength(),
                        r.contain(tollDecisionPoint.link().toSection()),
                        r.nextRoad(tollDecisionPoint.link().toSection()),
                        r.getLinks()
                );
            } else {
                System.out.println("当前收费决策点无收费路径数据");
            }
        } else {
            System.out.println("无收费决策点数据");
        }
    }
```



### 2.35. IParkingStall

停车位接口

 **long id();**

获取停车位ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
                            for (IParkingStall parkingStall : parkingStalls) {
                                System.out.printf("获取停车位ID=%s%n", parkingStall.id());
                            }
                        }
                    }
                }
```

 **long parkingRegionId();**

获取所属停车区域ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
                            for (IParkingStall parkingStall : parkingStalls) {
                                System.out.printf("获取所属停车区域ID=%s%n", parkingStall.parkingRegionId());
                            }
                        }
                    }
                }
```

 **IParkingRegion parkingRegion();**

获取所属停车区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
                            for (IParkingStall parkingStall : parkingStalls) {
                                // 假设IParkingRegion的toString()方法已实现，可输出区域标识
                                System.out.printf("获取所属停车区域=%s%n", parkingStall.parkingRegion());
                            }
                        }
                    }
                }
```

 **double distance();**

获取距路段起始位置, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
                            for (IParkingStall parkingStall : parkingStalls) {
                                System.out.printf("获取距路段起始位置, 单位: 米=%s%n", parkingStall.distance());
                            }
                        }
                    }
                }
```

 **int stallType();**

获取车位类型, 与车辆类型编码一致

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
                            for (IParkingStall parkingStall : parkingStalls) {
                                System.out.printf("获取车位类型, 与车辆类型编码一致=%s%n", parkingStall.stallType());
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showParkingStallAttr(netiface);
                    }
                }
private static void showParkingStallAttr(NetInterface netiface) {
        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
        if (!parkingRegions.isEmpty()) {
            IParkingRegion parkingRegion = parkingRegions.get(0);
            List<IParkingStall> parkingStalls = parkingRegion.parkingStalls();
            if (!parkingStalls.isEmpty()) {
                IParkingStall parkingStall = parkingStalls.get(0);
                System.out.printf("获取停车位ID=%s, 获取所属停车区域ID=%s, " +
                                "获取距路段起始位置, 单位: 米=%s, " +
                                "获取车位类型, 与车辆类型编码一致=%s%n",
                        parkingStall.id(),
                        parkingStall.parkingRegionId(),
                        parkingStall.distance(),
                        parkingStall.stallType());
            } else {
                System.out.println("当前停车区域无停车位数据");
            }
        } else {
            System.out.println("无停车区域数据");
        }
    }
```




### 2.36. IParkingRegion

停车区域接口

 **long id();**

获取停车区域ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IParkingRegion> parkingRegions = netiface.parkingRegions();
            for (IParkingRegion parkingRegion : parkingRegions) {
                System.out.printf("获取停车区域ID=%s%n", parkingRegion.id());
            }
        }
    }
```

 **String name();**

获取所属停车区域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IParkingRegion> parkingRegions = netiface.parkingRegions();
            for (IParkingRegion parkingRegion : parkingRegions) {
                System.out.printf("获取停车区域名称=%s%n", parkingRegion.name());
            }
        }
    }
```

 **void setName(String name);**

设置停车区域名称 

参数: 
[ in ] name : 停车区域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            // 先执行设置名称操作（setName返回void）
                            parkingRegion.setName("test parking name");
                            // 再打印设置后的名称
                            System.out.printf("获取停车区域名称=%s%n", parkingRegion.name());
                        }
                    }
                }
```

 **ArrayList<IParkingStall> parkingStalls();**

获取所有停车位, 返回列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            System.out.printf("获取所有停车位, 返回列表=%s%n", parkingRegion.parkingStalls());
                        }
                    }
                }
```

 **DynaParkingRegion dynaParkingRegion();**

获取动态停车区域信息

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
                        for (IParkingRegion parkingRegion : parkingRegions) {
                            System.out.printf("获取动态停车区域信息=%s%n", parkingRegion.dynaParkingRegion());
                        }
                    }
                }
```

 数据结构见jar文件的com.jidatraffic.tessng.DynaParkingRegion 

**案例代码**

```java
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            showParkingRegionAttr(netiface);
        }
    }
private static void showParkingRegionAttr(NetInterface netiface) {
        List<IParkingRegion> parkingRegions = netiface.parkingRegions();
        if (!parkingRegions.isEmpty()) {
            IParkingRegion parkingRegion = parkingRegions.get(0);
            IParkingRegion r1 = netiface.findParkingRegion(parkingRegion.id()); // 按ID查找停车区域

            // 执行设置名称操作
            parkingRegion.setName("test parking name");

            // 打印详细属性
            System.out.printf(
                    "获取停车区域ID=%s, 获取停车区域名称=%s, %n" +
                            "获取所有停车位=%s, %n" +
                            "获取动态停车区域信息=%s%n",
                    parkingRegion.id(),
                    parkingRegion.name(), // 打印设置后的名称
                    parkingRegion.parkingStalls(),
                    parkingRegion.dynaParkingRegion()
            );
        } else {
            System.out.println("无停车区域数据");
        }
    }
```




### 2.37. IParkingDecisionPoint

停车决策点接口

 **long id();**

获取停车决策点ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
            for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                System.out.printf("获取停车决策点ID=%s%n", parkingDecisionPoint.id());
            }
        }
    }
```

 **String name();**

获取停车决策点名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
            for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                System.out.printf("获取停车决策点名称=%s%n", parkingDecisionPoint.name());
            }
        }
    }
```

 **ILink link();**

获取停车决策点所在路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
            for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                System.out.printf("获取停车决策点所在路段=%s%n", parkingDecisionPoint.link());
            }
        }
    }
```

 **double distance();**

获取停车决策点距离所在路段起点的距离, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            System.out.printf("获取停车决策点距离所在路段起点的距离, 单位: 米=%s%n", parkingDecisionPoint.distance());
                        }
                    }
                }
```

 **ArrayList<IParkingRouting> routings();**

获取当前停车决策点对应的所有停车路径

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            System.out.printf("获取当前停车决策点对应的所有停车路径=%s%n", parkingDecisionPoint.routings());
                        }
                    }
                }
```

**boolean updateParkDisInfo(ArrayList<DynaParkDisInfo> tollDisInfoList)**

更新停车分配信息, 先构建停车决策点, 再通过此更新方法补充完善停车分配信息

参数: 
[ in ] tollDisInfoList: 停车分配信息列表, 见pyi文件的Online.ParkingLot.DynaParkDisInfo

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            boolean updateResult = parkingDecisionPoint.updateParkDisInfo(parkingDecisionPoint.parkDisInfoList());
                            System.out.printf("更新停车分配信息=%b%n", updateResult);
                        }
                    }
                }
```

**ArrayList<DynaParkDisInfo> parkDisInfoList()**

获取停车分配信息列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            System.out.printf("获取停车分配信息列表=%s%n", parkingDecisionPoint.parkDisInfoList());
                        }
                    }
                }
```

 **Vector<Point> polygon();**

获取当前停车决策点多边形轮廓

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            System.out.printf("获取当前停车决策点多边形轮廓=%s%n", parkingDecisionPoint.polygon());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showParkingDecisionPointAttr(netiface);
    }
}
private static void showParkingDecisionPointAttr(NetInterface netiface) {
        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
        if (!parkingDecisionPoints.isEmpty()) {
            IParkingDecisionPoint parkingDecisionPoint = parkingDecisionPoints.get(0);
            IParkingDecisionPoint r1 = netiface.findParkingDecisionPoint(parkingDecisionPoint.id()); // 按ID查找停车决策点

            // 执行停车分配信息更新操作
            boolean updateResult = parkingDecisionPoint.updateParkDisInfo(parkingDecisionPoint.parkDisInfoList());

            // 打印详细属性
            System.out.printf(
                    "获取停车决策点ID=%s, 获取停车决策点名称=%s, %n" +
                            "获取停车决策点所在路段=%s, %n" +
                            "获取停车决策点距路段起点距离（米）=%s, %n" +
                            "获取停车分配信息列表=%s, %n" +
                            "获取相关停车路径=%s, %n" +
                            "更新停车分配信息=%b, %n" +
                            "获取停车决策点多边型轮廓=%s%n",
                    parkingDecisionPoint.id(),
                    parkingDecisionPoint.name(),
                    parkingDecisionPoint.link(),
                    parkingDecisionPoint.distance(),
                    parkingDecisionPoint.parkDisInfoList(),
                    parkingDecisionPoint.routings(),
                    updateResult,
                    parkingDecisionPoint.polygon()
            );
        } else {
            System.out.println("无停车决策点数据");
        }
    }
```




### 2.38. IParkingRouting

停车决策路径接口

 **long id()**

获取停车决策路径ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取停车决策路径ID=%s%n", parkingRouting.id());
                            }
                        }
                    }
                }
```

 **long parkingDeciPointId()**

获取停车决策路径所属停车决策点的ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取停车决策路径所属停车决策点的ID=%s%n", parkingRouting.parkingDeciPointId());
                            }
                        }
                    }
                }
```

 **long parkingRegionId()**

获取路径到达的停车区域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取路径到达的停车区域id=%s%n", parkingRouting.parkingRegionId());
                            }
                        }
                    }
                }
```

 **double calcuLength(**

获取停车决策路径的长度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取停车决策路径的长度, 单位: 米=%s%n", parkingRouting.calcuLength());
                            }
                        }
                    }
                }
```

 **boolean contain(ISection pRoad)**

判断输入的道路（ 路段或连接段）是否在当前停车决策路径上 

参数: 
[ in ] pRoad : 道路对象, 类型为Tessng.ISection

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                boolean isContained = parkingRouting.contain(parkingDecisionPoint.link().toSection());
                                System.out.printf("判断输入的道路是否在当前停车决策路径上=%b%n", isContained);
                            }
                        }
                    }
                }
```

 **ISection nextRoad(ISection pRoad)**

获取输入道路的紧邻下游道路 

参数: 
[ in ] pRoad : 道路对象, 类型为Tessng.ISection

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取输入道路的紧邻下游道路=%s%n", parkingRouting.nextRoad(parkingDecisionPoint.link().toSection()));
                            }
                        }
                    }
                }
```

 **ArrayList<ILink> getLinks()**

获取当前停车路径的有序路段序列

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
                        for (IParkingDecisionPoint parkingDecisionPoint : parkingDecisionPoints) {
                            List<IParkingRouting> parkingRoutings = parkingDecisionPoint.routings();
                            for (IParkingRouting parkingRouting : parkingRoutings) {
                                System.out.printf("获取当前停车路径的有序路段序列=%s%n", parkingRouting.getLinks());
                            }
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showParkingRoutingAttr(netiface);
                    }
                }
private static void showParkingRoutingAttr(NetInterface netiface) {
        List<IParkingDecisionPoint> parkingDecisionPoints = netiface.parkingDecisionPoints();
        if (!parkingDecisionPoints.isEmpty()) {
            IParkingDecisionPoint parkingDecisionPoint = parkingDecisionPoints.get(0);
            List<IParkingRouting> r1 = parkingDecisionPoint.routings();
            if (!r1.isEmpty()) {
                IParkingRouting r = r1.get(0);

                // 打印详细属性
                System.out.printf(
                        "获取路径ID=%s, 获取所属决策点ID=%s, %n" +
                                "计算路径长度（米）=%s, %n" +
                                "根据所给道路判断是否在当前路径上=%b, %n" +
                                "根据所给道路求下一条道路=%s, %n" +
                                "获取路段序列=%s%n",
                        r.id(),
                        r.parkingDeciPointId(),
                        r.calcuLength(),
                        r.contain(parkingDecisionPoint.link().toSection()),
                        r.nextRoad(parkingDecisionPoint.link().toSection()),
                        r.getLinks()
                );
            } else {
                System.out.println("当前停车决策点无停车路径数据");
            }
        } else {
            System.out.println("无停车决策点数据");
        }
    }
```



### 2.39. IJunction

节点接口

 **long getId()**

获取节点ID   

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IJunction> nodes = netiface.getAllJunctions();
        for (IJunction node : nodes) {
            System.out.printf("获取节点ID=%s%n", node.getId());
        }
    }
}
```

 **String name()**

获取节点名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            System.out.printf("获取节点名称=%s%n", node.name());
                        }
                    }
                }
```

 **void setName(String strName)**

设置节点名称  
[ in ] strName : 节点名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            node.setName("new_" + node.name());
                            System.out.printf("获取节点名称=%s%n", node.name());
                        }
                    }
                }
```

 **ArrayList<ILink> getJunctionLinks()**

获取节点内的路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            System.out.printf("获取节点内的路段=%s%n", node.getJunctionLinks());
                        }
                    }
                }
```

 **ArrayList<IConnector> getJunctionConnectors()**

获取节点内的连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            System.out.printf("获取节点内的连接段=%s%n", node.getJunctionConnectors());
                        }
                    }
                }
```

 **ArrayList<TurnningBaseInfo> getAllTurningInfo()**

获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            System.out.printf("获取节点内的流向信息=%s%n", node.getAllTurningInfo());
                        }
                    }
                }
```

 **TurnningBaseInfo getTurningInfo(long turningId)**

参数: 
[ in ] turningId: 转向编号

根据转向编号获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IJunction> nodes = netiface.getAllJunctions();
                        for (IJunction node : nodes) {
                            System.out.printf("根据转向编号获取节点内的流向信息=%s%n", node.getTurningInfo(node.getId()));
                        }
                    }
                }
```

**案例代码**

```java
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showJunctionAttr(netiface);
    }
}
private static void showJunctionAttr(NetInterface netiface) {
        List<IJunction> nodes = netiface.getAllJunctions();
        if (!nodes.isEmpty()) {
            IJunction node = netiface.findJunction(nodes.get(0).getId());
            // 原逻辑中通过名称查找信号控制器，此处保留调用
            ISignalController signalController = netiface.findSignalControllerByName(node.name());

            // 执行设置名称操作
            String originalName = node.name();
            node.setName("new_" + originalName);

            // 打印详细属性
            System.out.printf(
                    "路网中的节点总数=%d, 节点编号=%s的具体信息: %n" +
                            "获取节点ID=%s, 名称=%s, %n" +
                            "获取节点内的路段=%s, %n" +
                            "获取节点内的连接段=%s, %n" +
                            "获取节点内的流向信息（TurnningBaseInfo）=%s, %n" +
                            "根据转向编号获取节点内的流向信息（TurnningBaseInfo）=%s%n",
                    nodes.size(),
                    nodes.get(0).getId(),
                    node.getId(),
                    node.name(),
                    node.getJunctionLinks(),
                    node.getJunctionConnectors(),
                    node.getAllTurningInfo(),
                    node.getTurningInfo(nodes.get(0).getId())
            );
        } else {
            System.out.println("无节点数据");
        }
    }
```






### 2.40. IPedestrian

行人接口

 **long getId();**

获取行人ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人ID=%s%n", ped.getId());
            }
        }
    }
```

 **double getRadius();**

获取行人半径大小, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    SimuInterface simuiface = iface.simuInterface();
                    if (simuiface != null) {
                        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
                        for (IPedestrian ped : allPedestrian) {
                            System.out.printf("获取行人半径大小, 单位: 米=%s%n", ped.getRadius());
                        }
                    }
                }
```

 **double getWeight();**

获取行人质量, 单位: 千克

举例: 

```java
if (iface != null) {
                    SimuInterface simuiface = iface.simuInterface();
                    if (simuiface != null) {
                        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
                        for (IPedestrian ped : allPedestrian) {
                            System.out.printf("获取行人质量, 单位: 千克=%s%n", ped.getWeight());
                        }
                    }
                }
```

 **String getColor();**

获取行人颜色, 十六进制颜色代码, 如"#EE0000"

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人颜色=%s%n", ped.getColor());
        }
    }
}
```

 **Point getPos();**

获取行人当前位置（瞬时位置）, 像素坐标系下的坐标点, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人当前位置（瞬时位置）, 像素坐标系下的坐标点, 单位: 米=%s%n", ped.getPos());
        }
    }
}
```

 **double getAngle();**

获取行人当前角度, QT像素坐标系下, X轴正方向为0, 逆时针为正, 单位: 度 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人当前角度, QT像素坐标系下, X轴正方向为0, 逆时针为正, 单位: 度=%s%n", ped.getAngle());
        }
    }
}
```

 **Vector2D getDirection()**

获取行人当前方向向量, 二维向量

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人当前方向向量, 二维向量=%s%n", ped.getDirection());
        }
    }
}
```

 **double getElevation();**

获取行人当前位置的高程, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人当前位置的高程, 单位: 米=%s%n", ped.getElevation());
        }
    }
}
```

 **Vector2D getSpeed()**

获取行人当前速度, 单位: 米/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人当前速度, 单位: 米/秒=%s%n", ped.getSpeed());
        }
    }
}
```

 **double getDesiredSpeed();**

获取行人期望速度, 单位: 米/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            System.out.printf("获取行人期望速度, 单位: 米/秒=%s%n", ped.getDesiredSpeed());
        }
    }
}
```

 **double getMaxSpeed();**

获取行人最大速度限制, 单位: 米/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人最大速度限制, 单位: 米/秒=%s%n", ped.getMaxSpeed());
            }
        }
    }
```

 **Vector2D getAcce()**

获取行人当前加速度, 单位: 米/秒²

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人当前加速度, 单位: 米/秒²=%s%n", ped.getAcce());
            }
        }
    }
```

 **double getMaxAcce();**

获取行人最大加速度限制, 单位: 米/秒²

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人最大加速度限制, 单位: 米/秒²=%s%n", ped.getMaxAcce());
            }
        }
    }
```

 **Point3D getEuler();**

获取行人欧拉角, 单位: 度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人欧拉角, 用于三维的信息展示和计算, 单位: 度=%s%n", ped.getEuler());
            }
        }
    }
```

 **Point3D getSpeedEuler();**

获取行人速度欧拉角, 单位: 度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        SimuInterface simuiface = iface.simuInterface();
        if (simuiface != null) {
            List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
            for (IPedestrian ped : allPedestrian) {
                System.out.printf("获取行人速度欧拉角, 用于三维的信息展示和计算, 单位: 度=%s%n", ped.getSpeedEuler());
            }
        }
    }
```

 **Vector2D getWallFDirection()**

获取墙壁方向单位向量

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    SimuInterface simuiface = iface.simuInterface();
                    if (simuiface != null) {
                        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
                        for (IPedestrian ped : allPedestrian) {
                            System.out.printf("获取墙壁方向单位向量=%s%n", ped.getWallFDirection());
                        }
                    }
                }
```

 **IPedestrianRegion getRegion();**

获取行人当前所在面域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    SimuInterface simuiface = iface.simuInterface();
                    if (simuiface != null) {
                        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
                        for (IPedestrian ped : allPedestrian) {
                            System.out.printf("获取行人当前所在面域=%s%n", ped.getRegion());
                        }
                    }
                }
```

 **long getPedestrianTypeId();**

获取行人类型ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    SimuInterface simuiface = iface.simuInterface();
                    if (simuiface != null) {
                        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
                        for (IPedestrian ped : allPedestrian) {
                            System.out.printf("获取行人类型ID=%s%n", ped.getPedestrianTypeId());
                        }
                    }
                }
```

 **void stop();**

停止仿真, 会在下一个仿真批次移除当前行人, 释放资源

举例: 

```java
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        for (IPedestrian ped : allPedestrian) {
            ped.stop();
            System.out.println("停止仿真, 会在下一个仿真批次移除当前行人, 释放资源");
        }
    }
}
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace(); 
if (iface != null) {
    SimuInterface simuiface = iface.simuInterface();
    if (simuiface != null) {
        showPedestrianAttr(simuiface);
    }
}
private static void showPedestrianAttr(SimuInterface simuiface) {
        List<IPedestrian> allPedestrian = simuiface.allPedestrianStarted();
        if (!allPedestrian.isEmpty()) {
            IPedestrian ped = allPedestrian.get(0);

            // 执行设置面域颜色和停止操作
//            ped.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            ped.stop();

            // 打印详细属性
            System.out.printf(
                    "获取行人ID=%s, %n" +
                            "获取行人半径大小（米）=%s, %n" +
                            "获取行人质量（千克）=%s, %n" +
                            "获取行人颜色（十六进制）=%s, %n" +
                            "获取行人当前位置（像素坐标系，米）=%s, %n" +
                            "获取行人当前角度（度）=%s, %n" +
                            "获取行人当前方向向量=%s, %n" +
                            "获取行人当前位置高程（米）=%s, %n" +
                            "获取行人当前速度（米/秒）=%s, %n" +
                            "获取行人期望速度（米/秒）=%s, %n" +
                            "获取行人最大速度限制（米/秒）=%s, %n" +
                            "获取行人当前加速度（米/秒²）=%s, %n" +
                            "获取行人最大加速度限制（米/秒²）=%s, %n" +
                            "获取行人欧拉角（度）=%s, %n" +
                            "获取行人速度欧拉角（度）=%s, %n" +
                            "获取墙壁方向单位向量=%s, %n" +
                            "获取行人当前所在面域=%s, %n" +
                            "获取行人类型ID=%s, %n" +
                            "停止当前行人仿真运动（下一批次移除）",
                    ped.getId(),
                    ped.getRadius(),
                    ped.getWeight(),
                    ped.getColor(),
                    ped.getPos(),
                    ped.getAngle(),
                    ped.getDirection(),
                    ped.getElevation(),
                    ped.getSpeed(),
                    ped.getDesiredSpeed(),
                    ped.getMaxSpeed(),
                    ped.getAcce(),
                    ped.getMaxAcce(),
                    ped.getEuler(),
                    ped.getSpeedEuler(),
                    ped.getWallFDirection(),
                    ped.getRegion(),
                    ped.getPedestrianTypeId()
//                    ,stopResult
            );
        } else {
            System.out.println("无已启动的行人数据");
        }
    }
```




### 2.41. IPedestrianPathRegionBase

行人可通行路径面域基类接口, 用例见下文子类

 **int getId();**

获取面域id

 **String getName();**

获取面域名称

 **void setName(String name);**

设置面域名称

[ in ] name: 面域名称

 **Color getRegionColor();**

获取面域颜色, 返回pyside2的QColor类型

 **void setRegionColor(Color color);**

获取面域颜色, 

[ in ] color: 面域颜色

 **Point getPosition(UnitOfMeasure unit);**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 **void setPosition(Point scenePos, UnitOfMeasure unit);**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 **int getGType();**

获取面域类型, 面域类型见pyi文件NetItemType类





### 2.42. IObstacleRegion

障碍物面域基类接口, 用例见下文子类

 **boolean isObstacleRegion();**

获取面域是否为障碍物

 **void setObstacleRegion(boolean b);**

设置面域是否为障碍物

[ in ] b: True表示设置为障碍物, False表示设置为非障碍物





### 2.43. IPassengerRegion

乘客面域基类接口, 用例见下文子类

 **boolean isBoardingArea();**

获取面域是否为上客区域

 **void setIsBoardingArea(boolean b);**

设置面域是否为上客区域

参数: 
[ in ] b: True表示设置为上客区域, False表示设置为非上客区域

 **boolean isAlightingArea();**

获取面域是否为下客区域

 **void setIsAlightingArea(boolean b);**

设置面域是否为下客区域 

参数: 
[ in ] b: True表示设置为下客区域, False表示设置为非下客区域





### 2.44. IPedestrianRegion 

行人区域（面域）接口, 用例见下文子类

 **int getId();**

获取行人区域(面域)ID

 **String getName();**

获取行人区域(面域)名称

 **void setName(String name);**

设置行人区域(面域)名称

 **void setRegionColor(QColor color);**

设置行人区域(面域)的颜色

 **Point getPosition(UnitOfMeasure unit);**

获取面域位置, 默认单位: 像素, 可通过unit参数设置单位, 这里范围的面域中心点的位置, QT像素坐标系  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 **void setPosition(Point scenePos, UnitOfMeasure unit);**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] scenePos: 场景坐标系下的位置   
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 **int getGType();**

获取行人区域(面域)类型, 面域类型见pyi文件NetItemType类

 **int getExpectSpeedFactor();**

获取行人区域(面域)的期望速度系数

 **void setExpectSpeedFactor(double factor);**

设置行人区域(面域)的期望速度系数

参数: 
[ in ] factor: 期望速度系数

 **void getElevation();**

获取面域高程, 单位: 米

 **void setElevation(double elevation);**

设置面域高程, 单位: 米

参数: 
[ in ] elevation: 高程

 **void getPolygon();**

获取面域多边形

 **int getLayerId();**

获取面域所在图层ID

 **void setLayerId(int id);**

将面域图层设置为图层id, 如果图层id非法, 则不做任何改变

参数: 
[ in ] id: 图层ID





### 2.45. IPedestrianSideWalkRegion

人行道区域（面域）接口

**long getId();**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
        for (IPedestrianSideWalkRegion region : allRegion) {
            System.out.printf("获取面域ID=%s%n", region.getId());
        }
    }
}
```

 **String getName();**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
        for (IPedestrianSideWalkRegion region : allRegion) {
            System.out.printf("获取面域名称=%s%n", region.getName());
        }
    }
}
```

 **void setName(String name);**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
            for (IPedestrianSideWalkRegion region : allRegion) {
                region.setName("new_" + region.getName());
                System.out.printf("获取面域名称=%s%n", region.getName());
            }
        }
    }
```

 **Color getRegionColor();**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color);**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0));
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit);**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit);**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setPosition(region.getPosition());
                            region.setPosition(region.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
                            System.out.printf("面域位置（像素+米制）已更新%n");
                        }
                    }
                }
```

 **int getGType();**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor();**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val);**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation();**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation);**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon();**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId();**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id);**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setLayerId(region.getLayerId());
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **double getWidth();**

获取人行道(面域)宽度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取人行道宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **void setWidth(double width);**

设置人行道(面域)宽度, 单位: 米

参数: 
[ in ] width: 宽度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.setWidth(region.getWidth() + 0.1);
                            System.out.printf("获取人行道宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **ArrayList<IHelpModifyPedestrianRegionSizeDotItem> getVetexs()**

获取人行道(面域)顶点, 即初始折线顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取人行道初始折线顶点=%s%n", region.getVetexs());
                        }
                    }
                }
```

 **ArrayList<IHelpModifyPedestrianRegionSizeDotItem> getControl1Vetexs()**

获取人行道(面域)贝塞尔曲线控制点P1

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取人行道贝塞尔曲线控制点P1=%s%n", region.getControl1Vetexs());
                        }
                    }
                }
```

 **ArrayList<IHelpModifyPedestrianRegionSizeDotItem> getControl2Vetexs()**

获取人行道(面域)贝塞尔曲线控制点P2

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取人行道贝塞尔曲线控制点P2=%s%n", region.getControl2Vetexs());
                        }
                    }
                }
```

 **ArrayList<IHelpModifyPedestrianRegionSizeDotItem> getCandidateVetexs()**

获取人行道(面域)候选顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            System.out.printf("获取人行道候选顶点=%s%n", region.getCandidateVetexs());
                        }
                    }
                }
```

 **void removeVetex(int index)**

删除人行道(面域)的第index个顶点: 顺序: 按照人行横道的绘制顺序排列

参数: 
[ in ] index: 顶点索引

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianSideWalkRegion> allRegion = netiface.pedestrianSideWalkRegions();
                        for (IPedestrianSideWalkRegion region : allRegion) {
                            region.removeVetex(1);
                            System.out.printf("已删除人行道第1个顶点%n");
                        }
                    }
                }
```

 **def insertVetex(Point pos, int index)**

在人行道(面域)的第index的位置插入顶点, 初始位置为pos: 顺序: 按照人行横道的绘制顺序排列

参数: 
[ in ] pos: 顶点位置
[ in ] index: 顶点索引

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"在第index个位置插入顶点, 初始位置为pos={region.insertVetex(Point(region.getCandidateVetexs()[0].pos().x()+0.1, region.getCandidateVetexs()[0].pos().y()+0.1), 0)}")
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
showPedestrianSideWalkRegionAttr(netiface)
def showPedestrianSideWalkRegionAttr(netiface): 
    regions = netiface.pedestrianSideWalkRegions()
    if len(regions) > 0: 
        r = regions[0]
        print(
              f"获取面域ID={r.getId()}, "
              f"获取面域名称={r.getName()}, "
              f"设置面域名称={r.setName('test_area')}, " 
              f"获取面域颜色={r.getRegionColor()}, "
              f"设置面域颜色={r.setRegionColor(QColor('red'))}, "
              f"获取面域位置, 默认单位: 像素={r.getPosition()}, "
              f"获取面域位置, 米制={r.getPosition(UnitOfMeasure.Metric)}, "
              f"设置面域位置, 像素制={r.setPosition(r.getPosition())}, "
              f"设置面域位置, 米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)}, "
              f"获取面域类型={r.getGType()}, "
              f"获取期望速度系数={r.getExpectSpeedFactor()}, "
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)}, "
              f"获取面域高程={r. getElevation() }, "
              f"设置面域高程={r. setElevation(0.1)}, "
              f"获取面域多边形={r.getPolygon()}, " 
              f"获取面域所在图层ID={r.getLayerId() }, 设置面域所在图层, 如果图层ID非法, 则不做任何改变={r.setLayerId(r.getLayerId())}")
        print(f"仿真路网中人行道区域总数={len(regions)}, "
              f"获取人行道宽度={r.getWidth()}, 设置人行道宽度={r.setWidth(r.getWidth()+0.5)}, "
              f"获取人行道顶点, 即初始折线顶点={r.getVetexs()}, 获取人行道贝塞尔曲线控制点P1={r.getControl1Vetexs()}, "
              f"获取人行道贝塞尔曲线控制点P2={r.getControl2Vetexs()}, 获取候选顶点={r.getCandidateVetexs()}")
        print(f"在第index个位置插入顶点, 初始位置为pos={r.insertVetex(Point(r.getCandidateVetexs()[0].pos().x()+0.1, r.getCandidateVetexs()[0].pos().y()+0.1), 0)}, "
              f"删除第index个顶点={r.removeVetex(1)}")
        #
        print(f"在第index个位置插入顶点, 初始位置为pos={r.insertVetex(Point(100, 100), 0)}, "
              f"删除第index个顶点={r.removeVetex(1)}")

```






### 2.46. IPedestrianCrossWalkRegion

人行横道区域接口

**long getId();**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
        for (IPedestrianCrossWalkRegion region : allRegion) {
            System.out.printf("获取面域id=%s%n", region.getId());
        }
    }
}
```

 **String getName();**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name);**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor();**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color);**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit);**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit);**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType();**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor();**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val);**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation();**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation);**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon();**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
        for (IPedestrianCrossWalkRegion region : allRegion) {
            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
        }
    }
}
```

 **long getLayerId();**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id);**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **double getWidth();**

获取人行横道宽度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **void setWidth(double width);**

设置行人横道宽度, 单位: 米

参数: 
[ in ] width: 宽度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setWidth(1.5);
                            System.out.printf("获取人行横道宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **SWIGTYPE_p_QLineF getSceneLine();**  

获取人行横道起点到终点的线段, QT场景坐标系, 场景坐标系下, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道起点到终点线段（像素制）=%s%n", region.getSceneLine());
                            System.out.printf("获取人行横道起点到终点线段（米制）=%s%n", region.getSceneLine(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double getAngle();**  

获取人行横道倾斜角度, 单位: 度, QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道倾斜角度（度）=%s%n", region.getAngle());
                        }
                    }
                }
```

 **void setAngle(double angle);**

设置人行横道倾斜角度, 单位: 度, QT像素坐标系下, X轴正方向为0, 逆时针为正

参数: 
[ in ] angle: 角度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
 if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setAngle(5);
                            System.out.printf("获取人行横道倾斜角度（度）=%s%n", region.getAngle());
                        }
                    }
                }
```

 **double getRedLightSpeedFactor();**

获取人行横道上红灯清尾速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道上红灯清尾速度系数=%s%n", region.getRedLightSpeedFactor());
                        }
                    }
                }
```

 **void setRedLightSpeedFactor(double factor);**

设置人行横道上红灯清尾速度系数  

参数: 
[ in ] factor : 红灯清尾速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            region.setRedLightSpeedFactor(1.5);
                            System.out.printf("获取人行横道上红灯清尾速度系数=%s%n", region.getRedLightSpeedFactor());
                        }
                    }
                }
```

 **Vector2D getUnitDirectionFromStartToEnd()**

获取人行横道起点到终点的在场景坐标系下的单位方向向量, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取场景坐标系下起点到终点单位方向向量=%s%n", region.getUnitDirectionFromStartToEnd());
                        }
                    }
                }
```

 **Vector2D getLocalUnitDirectionFromStartToEnd()**

获取人行横道本身坐标系下从起点到终点的单位方向

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取自身坐标系下起点到终点单位方向=%s%n", region.getLocalUnitDirectionFromStartToEnd());
                        }
                    }
                }
```

 **IHelpModifyPedestrianRegionSizeDotItem getStartControlPoint();**

获取人行横道起点控制点, 场景坐标系

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道起点控制点=%s%n", region.getStartControlPoint());
                        }
                    }
                }
```

 **IHelpModifyPedestrianRegionSizeDotItem getEndControlPoint();**

获取人行横道终点控制点, 场景坐标系

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道终点控制点=%s%n", region.getEndControlPoint());
                        }
                    }
                }
```

 **IHelpModifyPedestrianRegionSizeDotItem getLeftControlPoint();**

获取人行横道左侧控制点, 场景坐标系

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道左侧控制点=%s%n", region.getLeftControlPoint());
                        }
                    }
                }
```

 **IHelpModifyPedestrianRegionSizeDotItem getRightControlPoint();**

获取人行横道右侧控制点, 场景坐标系

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取人行横道右侧控制点=%s%n", region.getRightControlPoint());
                        }
                    }
                }
```

 **ICrosswalkSignalLamp getPositiveDirectionSignalLamp();**

获取人行横道上管控正向通行的信号灯对象

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("获取正向通行信号灯=%s%n", region.getPositiveDirectionSignalLamp());
                        }
                    }
                }
```

 **ICrosswalkSignalLamp getNegativeDirectionSignalLamp();**

获取人行横道上管控反向通行的信号灯对象

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();

if (iface != null) {
        NetInterface netiface = iface.netInterface();
        if (netiface != null) {
            List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
            for (IPedestrianCrossWalkRegion region : allRegion) {
                System.out.printf("获取人行横道上管控反向通行的信号灯对象=%s%n", region.getNegativeDirectionSignalLamp());
            }
        }
    }
```

 **boolean isPositiveTrafficLightAdded();**

判断人行横道上是否存在管控正向通行的信号灯

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
        for (IPedestrianCrossWalkRegion region : allRegion) {
            System.out.printf("判断人行横道上是否存在管控正向通行的信号灯=%b%n", region.isPositiveTrafficLightAdded());
        }
    }
}
```

 **boolean isReverseTrafficLightAdded();**

判断人行横道上是否存在管控反向通行的信号灯

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianCrossWalkRegion> allRegion = netiface.pedestrianCrossWalkRegions();
                        for (IPedestrianCrossWalkRegion region : allRegion) {
                            System.out.printf("判断人行横道上是否存在管控反向通行的信号灯=%b%n", region.isReverseTrafficLightAdded());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianCrossWalkRegionAttr(netiface);
                    }
                }
private static void showPedestrianCrossWalkRegionAttr(NetInterface netiface) {
        List<IPedestrianCrossWalkRegion> allCrossWalkRegion = netiface.pedestrianCrossWalkRegions();
        if (!allCrossWalkRegion.isEmpty()) {
            IPedestrianCrossWalkRegion crossWalkRegion = allCrossWalkRegion.get(0);

            // 执行设置操作
            crossWalkRegion.setName("test_area");
            crossWalkRegion.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            crossWalkRegion.setPosition(crossWalkRegion.getPosition());
            crossWalkRegion.setPosition(crossWalkRegion.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            crossWalkRegion.setExpectSpeedFactor(1.5);
            crossWalkRegion.setElevation(0.1);
            crossWalkRegion.setLayerId(crossWalkRegion.getLayerId());
            crossWalkRegion.setWidth(crossWalkRegion.getWidth() + 0.1);
            crossWalkRegion.setAngle(5);
            crossWalkRegion.setRedLightSpeedFactor(1.5);

            // 打印详细属性（分两部分保持格式清晰）
            System.out.printf(
                    "获取面域ID=%s, " +
                            "获取面域名称=%s, " +
                            "获取面域颜色=%s, " +
                            "获取面域位置（像素制）=%s, " +
                            "获取面域位置（米制）=%s, " +
                            "获取面域类型=%s, " +
                            "获取期望速度系数=%s, " +
                            "获取面域高程=%s, " +
                            "获取面域多边形=%s, " +
                            "获取面域所在图层ID=%s%n",
                    crossWalkRegion.getId(),
                    crossWalkRegion.getName(),
                    crossWalkRegion.getRegionColor(),
                    crossWalkRegion.getPosition(),
                    crossWalkRegion.getPosition(UnitOfMeasure.Metric),
                    crossWalkRegion.getGType(),
                    crossWalkRegion.getExpectSpeedFactor(),
                    crossWalkRegion.getElevation(),
                    crossWalkRegion.getPolygon(),
                    crossWalkRegion.getLayerId()
            );

            System.out.printf(
                    "仿真路网中人行横道区域总数=%d, " +
                            "获取人行横道宽度（米）=%s, " +
                            "获取人行横道起点到终点线段=%s, " +
                            "获取人行横道倾斜角度（度）=%s, " +
                            "获取红灯清尾速度系数=%s, " +
                            "获取场景坐标系起点到终点单位方向向量=%s, " +
                            "获取自身坐标系起点到终点单位方向=%s, " +
                            "获取起点控制点=%s, " +
                            "获取终点控制点=%s, " +
                            "获取左侧控制点=%s, " +
                            "获取右侧控制点=%s, " +
                            "是否存在正向通行信号灯=%b, " +
                            "是否存在反向通行信号灯=%b, " +
                            "获取正向通行信号灯=%s, " +
                            "获取反向通行信号灯=%s%n",
                    allCrossWalkRegion.size(),
                    crossWalkRegion.getWidth(),
                    crossWalkRegion.getSceneLine(),
                    crossWalkRegion.getAngle(),
                    crossWalkRegion.getRedLightSpeedFactor(),
                    crossWalkRegion.getUnitDirectionFromStartToEnd(),
                    crossWalkRegion.getLocalUnitDirectionFromStartToEnd(),
                    crossWalkRegion.getStartControlPoint(),
                    crossWalkRegion.getEndControlPoint(),
                    crossWalkRegion.getLeftControlPoint(),
                    crossWalkRegion.getRightControlPoint(),
                    crossWalkRegion.isPositiveTrafficLightAdded(),
                    crossWalkRegion.isReverseTrafficLightAdded(),
                    crossWalkRegion.getPositiveDirectionSignalLamp(),
                    crossWalkRegion.getNegativeDirectionSignalLamp()
            );
        } else {
            System.out.println("无人行横道区域数据");
        }
    }              
```




### 2.47. IPedestrianEllipseRegion

行人椭圆面域接口

 **long getId()**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
        for (IPedestrianEllipseRegion region : allRegion) {
            System.out.printf("获取面域id=%s%n", region.getId());
        }
    }
}
```

 **String getName()**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name)**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor()**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color)**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit)**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit)**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType()**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor()**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val)**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation()**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation)**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon()**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId()    **

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id)**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **boolean isObstacle()**

获取面域是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();

                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

 **void setObstacle(boolean b)**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            iObstacleRegion.setObstacle(true);
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

**boolean isBoardingArea()**

获取面域是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **void setIsBoardingArea(boolean b)**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            iPassengerRegion.setIsBoardingArea(true);
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **boolean isAlightingArea()**

获取面域是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

 **void setIsAlightingArea(boolean b)**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianEllipseRegion> allRegion = netiface.pedestrianEllipseRegions();
                        for (IPedestrianEllipseRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            iPedestrianRegion.setIsAlightingArea(true);
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianEllipseRegionAttr(netiface);
                    }
                }
private static void showPedestrianEllipseRegionAttr(NetInterface netiface) {
        List<IPedestrianEllipseRegion> areas = netiface.pedestrianEllipseRegions();
        if (!areas.isEmpty()) {
            IPedestrianEllipseRegion r = areas.get(0);

            IPedestrianRegion iPedestrianRegion = r.pedestrianRegion();
            IPassengerRegion iPassengerRegion = r.passengerRegion();
            IObstacleRegion iObstacleRegion = r.obstacleRegion();

            // 执行设置操作
            r.setName("test_area");
            r.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            r.setPosition(r.getPosition());
            r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            r.setExpectSpeedFactor(1.5);
            r.setElevation(0.1);
            r.setLayerId(r.getLayerId());
            iObstacleRegion.setObstacle(true);
            iPassengerRegion.setIsBoardingArea(true);
            iPassengerRegion.setIsAlightingArea(true);

            // 打印详细属性
            System.out.printf(
                    "获取面域ID=%s, %n" +
                            "获取面域名称=%s, %n" +
                            "获取面域颜色=%s, %n" +
                            "获取面域位置（像素制）=%s, 米制=%s, %n" +
                            "获取面域类型=%s, %n" +
                            "获取期望速度系数=%s, %n" +
                            "获取面域高程=%s, %n" +
                            "获取面域多边形=%s, %n" +
                            "获取面域所在图层ID=%s, %n" +
                            "获取面域是否为障碍物=%b, %n" +
                            "获取面域是否为上客区域=%b, %n" +
                            "获取面域是否为下客区域=%b, %n" +
                            "仿真路网中pedestrianEllipseRegions总数=%d%n",
                    r.getId(),
                    r.getName(),
                    r.getRegionColor(),
                    r.getPosition(),
                    r.getPosition(UnitOfMeasure.Metric),
                    r.getGType(),
                    r.getExpectSpeedFactor(),
                    r.getElevation(),
                    r.getPolygon(),
                    r.getLayerId(),
                    iObstacleRegion.isObstacle(),
                    iPassengerRegion.isBoardingArea(),
                    iPassengerRegion.isAlightingArea(),
                    areas.size()
            );
        } else {
            System.out.println("无椭圆面域数据");
        }
    }
```






### 2.48. IPedestrianFanShapeRegion

行人扇形面域接口

 **long getId()**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域id=%s%n", region.getId());
                        }
                    }
                }
```

 **String getName()**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name)**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor()**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color)**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit)**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit)**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType()**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor()**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val)**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation()**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation)**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon()**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId()**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id)**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **boolean isObstacle()**

获取面域是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

 **void setObstacle(boolean b)**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            iObstacleRegion.setObstacle(true);
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

**boolean isBoardingArea()**

获取面域是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **void setIsBoardingArea(boolean b)**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            iPassengerRegion.setIsBoardingArea(true);
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **boolean isAlightingArea()**

获取面域是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

 **void setIsAlightingArea(boolean b)**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            iPedestrianRegion.setIsAlightingArea(true);
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

**double getInnerRadius()**

获取扇形面域内半径, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取扇形面域内半径=%s%n", region.getInnerRadius());
                        }
                    }
                }
```

 **double getOuterRadius()**

获取扇形面域外半径, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取扇形面域外半径=%s%n", region.getOuterRadius());
                        }
                    }
                }
```

 **double getStartAngle()**

获取扇形面域起始角度, 单位: 度  QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取扇形面域起始角度=%s%n", region.getStartAngle());
                        }
                    }
                }
```

 **double getSweepAngle()**

获取扇形面域扫过角度, 单位: 度  QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianFanShapeRegion> allRegion = netiface.pedestrianFanShapeRegions();
                        for (IPedestrianFanShapeRegion region : allRegion) {
                            System.out.printf("获取扇形面域扫过角度=%s%n", region.getSweepAngle());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianFanShapRegionAttr(netiface);
                    }
                }
private static void showPedestrianFanShapRegionAttr(NetInterface netiface) {
        List<IPedestrianFanShapeRegion> areas = netiface.pedestrianFanShapeRegions();
        if (!areas.isEmpty()) {
            IPedestrianFanShapeRegion r = areas.get(0);
            IPedestrianRegion iPedestrianRegion = r.pedestrianRegion();
            IPassengerRegion iPassengerRegion = r.passengerRegion();
            IObstacleRegion iObstacleRegion = r.obstacleRegion();
            // 执行设置操作
            r.setName("test_area");
            r.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            r.setPosition(r.getPosition());
            r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            r.setExpectSpeedFactor(1.5);
            r.setElevation(0.1);
            r.setLayerId(r.getLayerId());
            iObstacleRegion.setObstacle(true);
            iPassengerRegion.setIsBoardingArea(true);
            iPassengerRegion.setIsAlightingArea(true);

            // 打印详细属性
            System.out.printf(
                    "获取面域ID=%s, %n" +
                            "获取面域名称=%s, %n" +
                            "获取面域颜色=%s, %n" +
                            "获取面域位置（像素制）=%s, 米制=%s, %n" +
                            "获取面域类型=%s, %n" +
                            "获取期望速度系数=%s, %n" +
                            "获取面域高程=%s, %n" +
                            "获取面域多边形=%s, %n" +
                            "获取面域所在图层ID=%s, %n" +
                            "获取面域是否为障碍物=%b, %n" +
                            "获取面域是否为上客区域=%b, %n" +
                            "获取面域是否为下客区域=%b, %n" +
                            "获取扇形面域内半径=%s, %n" +
                            "获取扇形面域外半径=%s, %n" +
                            "获取扇形面域起始角度=%s, %n" +
                            "获取扇形面域扫过角度=%s, %n" +
                            "仿真路网中pedestrianFanShapeRegions总数=%d%n",
                    r.getId(),
                    r.getName(),
                    r.getRegionColor(),
                    r.getPosition(),
                    r.getPosition(UnitOfMeasure.Metric),
                    r.getGType(),
                    r.getExpectSpeedFactor(),
                    r.getElevation(),
                    r.getPolygon(),
                    r.getLayerId(),
                    iObstacleRegion.isObstacle(),
                    iPassengerRegion.isBoardingArea(),
                    iPassengerRegion.isAlightingArea(),
                    areas.size()
            );
        } else {
            System.out.println("无扇形面域数据");
        }
    }
```




### 2.49.IPedestrianPolygonRegion

行人多边形面域接口

 **long getId()**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域id=%s%n", region.getId());
                        }
                    }
                }
```

 **String getName()**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name)**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor()**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color)**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit)**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit)**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType()**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor()**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val)**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation()**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation)**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon()**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId()**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id)**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **boolean isObstacle()**

获取面域是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

 **void setObstacle(boolean b)**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            iObstacleRegion.setObstacle(true);
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

**boolean isBoardingArea()**

获取面域是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **void setIsBoardingArea(boolean b)**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            iPassengerRegion.setIsBoardingArea(true);
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **boolean isAlightingArea()**

获取面域是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

 **void setIsAlightingArea(boolean b)**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPolygonRegion> allRegion = netiface.pedestrianPolygonRegions();
                        for (IPedestrianPolygonRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            iPedestrianRegion.setIsAlightingArea(true);
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianPolygonRegionAttr(netiface);
                    }
                }
private static void showPedestrianPolygonRegionAttr(NetInterface netiface) {
        List<IPedestrianPolygonRegion> areas = netiface.pedestrianPolygonRegions();
        if (!areas.isEmpty()) {
            IPedestrianPolygonRegion r = areas.get(0);
            IPedestrianRegion iPedestrianRegion = r.pedestrianRegion();
            IPassengerRegion iPassengerRegion = r.passengerRegion();
            IObstacleRegion iObstacleRegion = r.obstacleRegion();
            // 执行设置操作
            r.setName("test_area");
            r.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            r.setPosition(r.getPosition());
            r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            r.setExpectSpeedFactor(1.5);
            r.setElevation(0.1);
            r.setLayerId(r.getLayerId());
            iObstacleRegion.setObstacle(true);
            iPassengerRegion.setIsBoardingArea(true);
            iPassengerRegion.setIsAlightingArea(true);
            // 打印详细属性
            System.out.printf(
                    "获取面域ID=%s, %n" +
                            "获取面域名称=%s, %n" +
                            "获取面域颜色=%s, %n" +
                            "获取面域位置（像素制）=%s, 米制=%s, %n" +
                            "获取面域类型=%s, %n" +
                            "获取期望速度系数=%s, %n" +
                            "获取面域高程=%s, %n" +
                            "获取面域多边形=%s, %n" +
                            "获取面域所在图层ID=%s, %n" +
                            "获取面域是否为障碍物=%b, %n" +
                            "获取面域是否为上客区域=%b, %n" +
                            "获取面域是否为下客区域=%b, %n" +
                            "仿真路网中pedestrianPolygonRegions总数=%d%n",
                    r.getId(),
                    r.getName(),
                    r.getRegionColor(),
                    r.getPosition(),
                    r.getPosition(UnitOfMeasure.Metric),
                    r.getGType(),
                    r.getExpectSpeedFactor(),
                    r.getElevation(),
                    r.getPolygon(),
                    r.getLayerId(),
                    iObstacleRegion.isObstacle(),
                    iPassengerRegion.isBoardingArea(),
                    iPassengerRegion.isAlightingArea(),
                    areas.size()
            );
        } else {
            System.out.println("无多边形面域数据");
        }
    }
```





### 2.50. IPedestrianRectRegion

行人矩形面域接口

 **long getId()**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域id=%s%n", region.getId());
                        }
                    }
                }
```

 **String getName()**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name)**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor()**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color)**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit)**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit)**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType()**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor()**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val)**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation()**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation)**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon()**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId()**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id)**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **boolean isObstacle()**

获取面域是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

 **void setObstacle(boolean b)**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            iObstacleRegion.setObstacle(true);
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

**boolean isBoardingArea()**

获取面域是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **void setIsBoardingArea(boolean b)**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            iPassengerRegion.setIsBoardingArea(true);
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **boolean isAlightingArea()**

获取面域是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

 **void setIsAlightingArea(boolean b)**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianRectRegion> allRegion = netiface.pedestrianRectRegions();
                        for (IPedestrianRectRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            iPedestrianRegion.setIsAlightingArea(true);
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianRectRegionAttr(netiface);
                    }
                }
private static void showPedestrianRectRegionAttr(NetInterface netiface) {
        List<IPedestrianRectRegion> areas = netiface.pedestrianRectRegions();
        if (!areas.isEmpty()) {
            IPedestrianRectRegion r = areas.get(0);

            // 执行设置操作
            r.setName("test_area");
            r.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            r.setPosition(r.getPosition());
            r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            r.setExpectSpeedFactor(1.5);
            r.setElevation(0.1);
            r.setLayerId(r.getLayerId());
            IPedestrianRegion iPedestrianRegion = r.pedestrianRegion();
            IPassengerRegion iPassengerRegion = r.passengerRegion();
            IObstacleRegion iObstacleRegion = r.obstacleRegion();
            iObstacleRegion.setObstacle(true);
            iPassengerRegion.setIsBoardingArea(true);
            iPassengerRegion.setIsAlightingArea(true);

            // 打印详细属性
            System.out.printf(
                    "获取面域ID=%s, %n" +
                            "获取面域名称=%s, %n" +
                            "获取面域颜色=%s, %n" +
                            "获取面域位置（像素制）=%s, 米制=%s, %n" +
                            "获取面域类型=%s, %n" +
                            "获取期望速度系数=%s, %n" +
                            "获取面域高程=%s, %n" +
                            "获取面域多边形=%s, %n" +
                            "获取面域所在图层ID=%s, %n" +
                            "获取面域是否为障碍物=%b, %n" +
                            "获取面域是否为上客区域=%b, %n" +
                            "获取面域是否为下客区域=%b, %n" +
                            "仿真路网中pedestrianRectRegions总数=%d%n",
                    r.getId(),
                    r.getName(),
                    r.getRegionColor(),
                    r.getPosition(),
                    r.getPosition(UnitOfMeasure.Metric),
                    r.getGType(),
                    r.getExpectSpeedFactor(),
                    r.getElevation(),
                    r.getPolygon(),
                    r.getLayerId(),
                    iObstacleRegion.isObstacle(),
                    iPassengerRegion.isBoardingArea(),
                    iPassengerRegion.isAlightingArea(),
                    areas.size()
            );
        } else {
            System.out.println("无矩形面域数据");
        }
    }
```

### 2.51. IPedestrianTriangleRegion

行人三角形面域接口

 **long getId()**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域id=%s%n", region.getId());
                        }
                    }
                }
```

 **String getName()**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name)**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor()**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color)**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit)**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos, UnitOfMeasure unit)**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType()**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getExpectSpeedFactor()**

获取期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **void setExpectSpeedFactor(double val)**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setExpectSpeedFactor(1.5);
                            System.out.printf("获取期望速度系数=%s%n", region.getExpectSpeedFactor());
                        }
                    }
                }
```

 **double getElevation()**

获取面域高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **void setElevation(double elevation)**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setElevation(0.1);
                            System.out.printf("获取面域高程=%s%n", region.getElevation());
                        }
                    }
                }
```

 **Vector<Point> getPolygon()**

获取面域多边形

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域多边形=%s%n", region.getPolygon());
                        }
                    }
                }
```

 **long getLayerId()**

获取面域所在图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **void setLayerId(long id)**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            region.setLayerId(1);
                            System.out.printf("获取面域所在图层ID=%s%n", region.getLayerId());
                        }
                    }
                }
```

 **boolean isObstacle()**

获取面域是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();

                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

 **void setObstacle(boolean b)**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IObstacleRegion iObstacleRegion = region.obstacleRegion();
                            iObstacleRegion.setObstacle(true);
                            System.out.printf("获取面域是否为障碍物=%b%n", iObstacleRegion.isObstacle());
                        }
                    }
                }
```

**boolean isBoardingArea()**

获取面域是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **void setIsBoardingArea(boolean b)**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IPassengerRegion iPassengerRegion = region.passengerRegion();
                            iPassengerRegion.setIsBoardingArea(true);
                            System.out.printf("获取面域是否为上客区域=%b%n", iPassengerRegion.isBoardingArea());
                        }
                    }
                }
```

 **boolean isAlightingArea()**

获取面域是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

 **void setIsAlightingArea(boolean b)**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianTriangleRegion> allRegion = netiface.pedestrianTriangleRegions();
                        for (IPedestrianTriangleRegion region : allRegion) {
                            IPassengerRegion iPedestrianRegion = region.passengerRegion();
                            iPedestrianRegion.setIsAlightingArea(true);
                            System.out.printf("获取面域是否为下客区域=%b%n", iPedestrianRegion.isAlightingArea());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianTriangleRegionAttr(netiface);
                    }
                }
private static void showPedestrianTriangleRegionAttr(NetInterface netiface) {
        List<IPedestrianTriangleRegion> areas = netiface.pedestrianTriangleRegions();
        if (!areas.isEmpty()) {
            IPedestrianTriangleRegion r = areas.get(0);

            IPedestrianRegion iPedestrianRegion = r.pedestrianRegion();
            IPassengerRegion iPassengerRegion = r.passengerRegion();
            IObstacleRegion iObstacleRegion = r.obstacleRegion();

            // 执行设置操作
            r.setName("test_area");
            r.setRegionColor(new Color(255, 0, 0)); // 对应QColor('red')
            r.setPosition(r.getPosition());
            r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
            r.setExpectSpeedFactor(1.5);
            r.setElevation(0.1);
            r.setLayerId(r.getLayerId());
            iObstacleRegion.setObstacle(true);
            iPassengerRegion.setIsBoardingArea(true);
            iPassengerRegion.setIsAlightingArea(true);

            // 打印详细属性
            System.out.printf(
                    "获取面域ID=%s, %n" +
                            "获取面域名称=%s, %n" +
                            "获取面域颜色=%s, %n" +
                            "获取面域位置（像素制）=%s, 米制=%s, %n" +
                            "获取面域类型=%s, %n" +
                            "获取期望速度系数=%s, %n" +
                            "获取面域高程=%s, %n" +
                            "获取面域多边形=%s, %n" +
                            "获取面域所在图层ID=%s, %n" +
                            "获取面域是否为障碍物=%b, %n" +
                            "获取面域是否为上客区域=%b, %n" +
                            "获取面域是否为下客区域=%b, %n" +
                            "仿真路网中pedestrianTriangleRegions总数=%d%n",
                    r.getId(),
                    r.getName(),
                    r.getRegionColor(),
                    r.getPosition(),
                    r.getPosition(UnitOfMeasure.Metric),
                    r.getGType(),
                    r.getExpectSpeedFactor(),
                    r.getElevation(),
                    r.getPolygon(),
                    r.getLayerId(),
                    iObstacleRegion.isObstacle(),
                    iPassengerRegion.isBoardingArea(),
                    iPassengerRegion.isAlightingArea(),
                    areas.size()
            );
        } else {
            System.out.println("无三角形面域数据");
        }
    }

```





### 2.52. IPedestrianStairRegion

楼梯区域接口

 **long getId();**

获取面域id

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取面域id=%s%n", region.getId());
                        }
                    }
                }
```

 **String getName();**

获取面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **void setName(String name);**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            region.setName("test_area");
                            System.out.printf("获取面域名称=%s%n", region.getName());
                        }
                    }
                }
```

 **Color getRegionColor();**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **void setRegionColor(Color color);**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            region.setRegionColor(new Color(255, 0, 0)); 
                            System.out.printf("获取面域颜色=%s%n", region.getRegionColor());
                        }
                    }
                }
```

 **Point getPosition(UnitOfMeasure unit);**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **void setPosition(Point scenePos);**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            region.setPosition(new Point(100, 100));
                            System.out.printf("获取面域位置（像素制）=%s%n", region.getPosition());
                            region.setPosition(new Point(100, 100), UnitOfMeasure.Metric);
                            System.out.printf("获取面域位置（米制）=%s%n", region.getPosition(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **int getGType();**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取面域类型=%s%n", region.getGType());
                        }
                    }
                }
```

 **double getWidth();**

获取楼梯宽度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **void setWidth(double width);**

设置楼梯(面域)宽度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            region.setWidth(region.getWidth() + 0.2);
                            System.out.printf("获取楼梯宽度（米）=%s%n", region.getWidth());
                        }
                    }
                }
```

 **Point getStartPoint();**

获取楼梯起始点, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯起始点（场景坐标系）=%s%n", region.getStartPoint());
                        }
                    }
                }
```

 **Point getEndPoint();**

获取楼梯终止点, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯终止点（场景坐标系）=%s%n", region.getEndPoint());
                        }
                    }
                }
```

 **double getStartConnectionAreaLength();**

获取起始衔接区域长度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取起始衔接区域长度（米）=%s%n", region.getStartConnectionAreaLength());
                        }
                    }
                }
```

 **double getEndConnectionAreaLength();**

获取终止衔接区域长度, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取终止衔接区域长度（米）=%s%n", region.getEndConnectionAreaLength());
                        }
                    }
                }
```

 **Point getStartRegionCenterPoint();**

获取起始衔接区域中心, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取起始衔接区域中心（场景坐标系）=%s%n", region.getStartRegionCenterPoint());
                        }
                    }
                }
```

 **Point getEndRegionCenterPoint();**

获取终止衔接区域中心, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取终止衔接区域中心（场景坐标系）=%s%n", region.getEndRegionCenterPoint());
                        }
                    }
                }
```



 **Vector<Point> getMainQueuePolygon();**

获取楼梯主体多边形, 场景坐标系下

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯主体多边形（场景坐标系）=%s%n", region.getMainQueuePolygon());
                        }
                    }
                }
```

 **StairType getStairType();**

获取楼梯类型, 类型枚举说明, 参见pyi的 StariType类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯类型=%s%n", region.getStairType());
                        }
                    }
                }
```

 **void setStairType(StairType type);**

设置楼梯类型, 类型枚举说明, 参见pyi的 StariType类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            // 保持原有类型不变（使用当前类型重新设置）
                            region.setStairType(region.getStairType());
                            System.out.printf("设置后楼梯类型=%s%n", region.getStairType());
                        }
                    }
                }
```

 **long getStartLayerId();**

获取楼梯的起始层级

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯的起始层级=%s%n", region.getStartLayerId());
                        }
                    }
                }
```

 **void setStartLayerId(long id);**

设置楼梯的起始层级

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            // 保持原有起始层级不变
                            region.setStartLayerId(region.getStartLayerId());
                            System.out.printf("设置后楼梯的起始层级=%s%n", region.getStartLayerId());
                        }
                    }
                }
```

 **long getEndLayerId();**

获取楼梯的终止层级

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯的终止层级=%s%n", region.getEndLayerId());
                        }
                    }
                }
```

 **void setEndLayerId(long id);**

设置楼梯的终止层级

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            region.setEndLayerId(region.getEndLayerId());
                            System.out.printf("设置后楼梯的终止层级=%s%n", region.getEndLayerId());
                        }
                    }
                }
```

 **double getTransmissionSpeed();**

获取楼梯传输速度, 单位米/秒, 如果是步行楼梯, 则返回值应该是0

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯传输速度（米/秒）=%s%n", region.getTransmissionSpeed());
                        }
                    }
                }
```

 **void setTransmissionSpeed(double speed);**

设置楼梯传输速度, 单位米/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            // 保持原有传输速度不变
                            region.setTransmissionSpeed(region.getTransmissionSpeed());
                            System.out.printf("设置后楼梯传输速度（米/秒）=%s%n", region.getTransmissionSpeed());
                        }
                    }
                }
```

 **double getHeadroom();**

获取楼梯净高, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            System.out.printf("获取楼梯净高（米）=%s%n", region.getHeadroom());
                        }
                    }
                }
```

 **void setHeadroom(double headroom);**

设置楼梯净高, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianStairRegion> allRegion = netiface.pedestrianStairRegions();
                        for (IPedestrianStairRegion region : allRegion) {
                            // 在原有净高基础上增加0.2米
                            region.setHeadroom(region.getHeadroom() + 0.2);
                            System.out.printf("设置后楼梯净高（米）=%s%n", region.getHeadroom());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianStairRegionAttr(netiface);
                    }
                }
private static void showPedestrianStairRegionAttr(NetInterface netiface) {
    List<IPedestrianStairRegion> stairRegions = netiface.pedestrianStairRegions();
    if (!stairRegions.isEmpty()) {
        IPedestrianStairRegion r = stairRegions.get(0);

        // 执行所有设置操作
        r.setName("test_area");
        r.setRegionColor(new Color(255, 0, 0)); // 红色
        r.setPosition(r.getPosition());
        r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric);
        r.setWidth(r.getWidth() + 0.2);
        r.setStairType(r.getStairType());
        r.setStartLayerId(r.getStartLayerId());
        r.setEndLayerId(r.getEndLayerId());
        r.setTransmissionSpeed(r.getTransmissionSpeed());
        r.setHeadroom(r.getHeadroom());

        // 打印完整属性（分两部分避免单行过长）
        System.out.printf(
                "获取面域ID=%s, 获取面域名称=%s, %n" +
                        "获取面域颜色=%s, %n" +
                        "获取面域位置（像素制）=%s, 米制=%s, %n" +
                        "获取面域类型=%s, %n" +
                        "仿真路网中楼梯区域总数=%d, %n" +
                        "获取楼梯宽度（米）=%s, %n" +
                        "获取起始点（场景坐标系）=%s, 获取终止点（场景坐标系）=%s, %n" +
                        "获取起始衔接区域长度（米）=%s, 获取终止衔接区域长度（米）=%s%n",
                r.getId(),
                r.getName(),
                r.getRegionColor(),
                r.getPosition(),
                r.getPosition(UnitOfMeasure.Metric),
                r.getGType(),
                stairRegions.size(),
                r.getWidth(),
                r.getStartPoint(),
                r.getEndPoint(),
                r.getStartConnectionAreaLength(),
                r.getEndConnectionAreaLength()
        );

        System.out.printf(
                "获取起始衔接区域中心（场景坐标系）=%s, 获取终止衔接区域中心（场景坐标系）=%s, %n" +

                        "获取楼梯主体多边形（场景坐标系）=%s, %n" +
                        "获取楼梯类型=%s, %n" +
                        "获取起始层级=%s, 获取终止层级=%s, %n" +
                        "获取传送速度（米/秒）=%s, 获取楼梯净高（米）=%s, %n" ,
                r.getStartRegionCenterPoint(),
                r.getEndRegionCenterPoint(),

                r.getMainQueuePolygon(),
                r.getStairType(),
                r.getStartLayerId(),
                r.getEndLayerId(),
                r.getTransmissionSpeed(),
                r.getHeadroom()
        );
    } else {
        System.out.println("无楼梯区域数据");
    }
}

```


### 2.53. ICrosswalkSignalLamp

人行横道信号灯接口

 **long id();**

获取行人信号灯ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取行人信号灯ID=%s%n", lamp.id());
                        }
                    }
                }
```

 **void setSignalPhase(ISignalPhase pPhase);**

设置相位, 所设相位可以是其它信号灯组的相位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            // 使用当前相位重新设置（原逻辑保持相位不变）
                            lamp.setSignalPhase(lamp.signalPhase());
                            System.out.printf("设置后信号灯相位=%s%n", lamp.signalPhase());
                        }
                    }
                }
```

 **void setLampColor(String colorStr);**

设置信号灯颜色    

参数: 

colorStr: 字符串表达的颜色, 有四种可选, 分别是"红"、"绿"、"黄"、"灰", , 或者是"R"、"G"、"Y"、"gray"。

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            lamp.setLampColor("gray");
                            System.out.printf("设置后信号灯颜色=%s%n", lamp.color());
                        }
                    }
                }
```

 **String color();**

获取信号灯色, "R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取信号灯色=%s%n", lamp.color());
                        }
                    }
                }
```

**String name();**

获取信号灯名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取信号灯名称=%s%n", lamp.name());
                        }
                    }
                }
```

**void setName(String name);**

设置信号灯名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            lamp.setName("new_" + lamp.name());
                            System.out.printf("设置后信号灯名称=%s%n", lamp.name());
                        }
                    }
                }
```

 **ISignalPlan signalPlan();**

获取信控方案

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取信控方案=%s%n", lamp.signalPlan());
                        }
                    }
                }
```

 **ISignalPhase signalPhase();**

获取相位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取相位=%s%n", lamp.signalPhase());
                        }
                    }
                }
```

 **Vector<Point> polygon();**

获取信号灯多边型轮廓的顶点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取信号灯多边型轮廓的顶点=%s%n", lamp.polygon());
                        }
                    }
                }
```

 **double angle();**

获取信号灯角度, 正北为0, 顺时针

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取信号灯角度（正北为0，顺时针）=%s%n", lamp.angle());
                        }
                    }
                }
```

 **IPedestrianCrossWalkRegion getICrossWalk();**

获取行人信号灯所属人行横道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<ICrosswalkSignalLamp> allLamps = netiface.crosswalkSignalLamps();
                        for (ICrosswalkSignalLamp lamp : allLamps) {
                            System.out.printf("获取行人信号灯所属人行横道=%s%n", lamp.getICrossWalk());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showCrossWalkSignalLampAttr(netiface);
    }
}
private static void showCrossWalkSignalLampAttr(NetInterface netiface) {
        List<ICrosswalkSignalLamp> crosswalkSignalLamps = netiface.crosswalkSignalLamps();
        if (!crosswalkSignalLamps.isEmpty()) {
            ICrosswalkSignalLamp crosswalkSignalLamp = netiface.findCrosswalkSignalLamp(crosswalkSignalLamps.get(0).id());

            // 执行设置操作
            crosswalkSignalLamp.setName("new_" + crosswalkSignalLamp.name());

            // 打印详细属性
            System.out.printf(
                    "行人信号灯列表=%s, %n" +
                            "行人信号灯%s的具体信息: %n" +
                            "编号=%s, 当前信号灯色=%s, 名称=%s, %n" +
                            "当前所在相位=%s, 当前所在灯组=%s, %n" +
                            "所属人行横道=%s, 多边形轮廓=%s, %n" +
                            "信号灯角度（正北为0顺时针）=%s%n",
                    crosswalkSignalLamps,
                    crosswalkSignalLamp.id(),
                    crosswalkSignalLamp.id(),
                    crosswalkSignalLamp.color(),
                    crosswalkSignalLamp.name(),
                    crosswalkSignalLamp.signalPhase(),
                    crosswalkSignalLamp.signalPlan(),
                    crosswalkSignalLamp.getICrossWalk(),
                    crosswalkSignalLamp.polygon(),
                    crosswalkSignalLamp.angle()
            );
        } else {
            System.out.println("无行人信号灯数据");
        }
    }
```

 


### 2.54 IPedestrianPath

行人路径接口

 **long getId();**

获取行人路径ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPath> allPaths = netiface.pedestrianPaths();
                        for (IPedestrianPath path : allPaths) {
                            System.out.printf("获取行人路径ID=%s%n", path.getId());
                        }
                    }
                }
```

 **IPedestrianPathPoint getPathStartPoint();**

获取行人路径起点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPath> allPaths = netiface.pedestrianPaths();
                        for (IPedestrianPath path : allPaths) {
                            System.out.printf("获取行人路径起点=%s%n", path.getPathStartPoint());
                        }
                    }
                }
```

 **IPedestrianPathPoint getPathEndPoint();**

获取行人路径终点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPath> allPaths = netiface.pedestrianPaths();
                        for (IPedestrianPath path : allPaths) {
                            System.out.printf("获取行人路径终点=%s%n", path.getPathEndPoint());
                        }
                    }
                }
```

 **ArrayList<IPedestrianPathPoint> getPathMiddlePoints();**

获取行人路径的中间点集合, 有序集合

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPath> allPaths = netiface.pedestrianPaths();
                        for (IPedestrianPath path : allPaths) {
                            System.out.printf("获取行人路径的中间点集合（有序）=%s%n", path.getPathMiddlePoints());
                        }
                    }
                }
```

 **boolean isLocalPath();**

判断当前行人路径是否为行人局部路径

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPath> allPaths = netiface.pedestrianPaths();
                        for (IPedestrianPath path : allPaths) {
                            System.out.printf("判断当前行人路径是否为行人局部路径=%b%n", path.isLocalPath());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        showPedestrianPathAttr(netiface);
    }
}
private static void showPedestrianPathAttr(NetInterface netiface) {
        List<IPedestrianPath> paths = netiface.pedestrianPaths();
        if (!paths.isEmpty()) {
            IPedestrianPath path = paths.get(0);

            // 打印详细属性
            System.out.printf(
                    "仿真路网中行人路径总数=%d, %n" +
                            "获取行人路径起始点=%s, %n" +
                            "获取行人路径终点=%s, %n" +
                            "获取行人路径中间点（有序集合）=%s, %n" +
                            "判断是否是局部路径=%b%n",
                    paths.size(),
                    path.getPathStartPoint(),
                    path.getPathEndPoint(),
                    path.getPathMiddlePoints(),
                    path.isLocalPath()
            );
        } else {
            System.out.println("无行人路径数据");
        }
    }
```




### 2.55. IPedestrianPathPoint

行人路径点（起点, 终点, 途经点）接口

 **long getId();**

获取行人路径点ID 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPathPoint> allPoints = netiface.pedestrianPathDecisionPoints();
                        for (IPedestrianPathPoint point : allPoints) {
                            System.out.printf("获取行人路径点ID=%s%n", point.getId());
                        }
                    }
                }
```

 **Point getScenePos(UnitOfMeasure unit);**

获取行人路径点场景坐标系下的位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPathPoint> allPoints = netiface.pedestrianPathDecisionPoints();
                        for (IPedestrianPathPoint point : allPoints) {
                            System.out.printf("获取行人路径点场景坐标（像素）=%s%n", point.getScenePos());
                            System.out.printf("获取行人路径点场景坐标（米）=%s%n", point.getScenePos(UnitOfMeasure.Metric));
                        }
                    }
                }
```

 **double getRadius();**

获取行人路径点的半径, 单位: 米

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        List<IPedestrianPathPoint> allPoints = netiface.pedestrianPathDecisionPoints();
                        for (IPedestrianPathPoint point : allPoints) {
                            System.out.printf("获取行人路径点的半径（米）=%s%n", point.getRadius());
                        }
                    }
                }
```

**案例代码**

```java
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
                    NetInterface netiface = iface.netInterface();
                    if (netiface != null) {
                        showPedestrianPathPointAttr(netiface);
                    }
                }
private static void showPedestrianPathPointAttr(NetInterface netiface) {
        List<IPedestrianPath> paths = netiface.pedestrianPaths();
        if (!paths.isEmpty()) {
            IPedestrianPath path = paths.get(0);
            IPedestrianPathPoint sp = path.getPathStartPoint(); // 获取路径起点（作为路径点示例）

            if (sp != null) {
                // 打印详细属性
                System.out.printf(
                        "获取行人路径点ID=%s, %n" +
                                "获取行人路径点场景坐标（像素）=%s, %n" +
                                "获取行人路径点场景坐标（米）=%s, %n" +
                                "获取行人路径点的半径（米）=%s%n",
                        sp.getId(),
                        sp.getScenePos(),
                        sp.getScenePos(UnitOfMeasure.Metric),
                        sp.getRadius()
                );
            } else {
                System.out.println("当前行人路径无起点数据");
            }
        } else {
            System.out.println("无行人路径数据");
        }
    }
```


## 3. 车辆及驾驶行为

### 3.1. IVehicle

车辆接口, 用于访问、控制车辆。通过此接口可以读取车辆属性, 初始化时设置车辆部分属性, 仿真过程读取当前道路情况、车辆前后左右相邻车辆及与它们的距离, 可以在车辆未驰出路网时停止车辆运行等。



接口方法: 

 **int id()**

车辆 ID 由 “x*100000 + y” 构成，其中 x 为发车点编号（从 1 开始递增，每个发车点对应唯一 x 值），y 为该发车点的车辆序号（从 1 开始递增），如第 1 个发车点的车辆 ID 从 100001 开始递增，第 2 个发车点的车辆 ID 从 200001 开始递增，后续以此类推。

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆ID为" + vehi.id());
}
```

 **ILink startLink() ;**

车辆进入路网时起始路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    String startLinkName = vehi.startLink().name();
    System.out.println("车辆进入路网时起始路段=" + startLinkName);
}
```

 **int startSimuTime();**

车辆进入路网时起始时间

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆进入路网时起始时间=" + vehi.startSimuTime());
}
```

 **int roadId();**

车辆所在路段link或connector连接段ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在路段link或connector连接段ID=" + vehi.roadId());
}
```

 **ILink/IConnector road();**

道路, 如果在路段上返回ILink对象, 如果在连接段上返回IConnector对象

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在道路对象=" + vehi.road());
}
```

 **ISection section();**

车辆所在的Section对象, 即路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在的Section, 即路段或连接段=" + vehi.section().name());
}
```

 **ILaneObject laneObj();**

车辆所在的车道或“车道连接”对象

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在的车道或“车道连接”ID=" + vehi.laneObj());
}
```

 **int segmIndex();**

车辆在当前车道上的分段序号

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆在当前车道上的分段序号=" + vehi.segmIndex());
}
```

 **boolean roadIsLink();**

车辆所在道路是否路段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在道路是否路段=" + vehi.roadIsLink());
}
```

 **String roadName();**

道路名

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("道路名=" + vehi.roadName());
}
```

 **double initSpeed(double speed, UnitOfMeasure unit);**

初始化车速, 默认单位: 像素/秒

参数: 
[ in ] speed: 车速, 如果大于0, 车辆以指定的速度从发车点出发, 默认单位: 像素/秒  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认单位  
返回: 初始化车速, 默认单位: 像素/秒

举例: 

```java
void initVehicle(IVehicle vehi){
    return vehi.initSpeed(5,UnitOfMeasure.Metric);
}
```

 **void initLane(int laneNumber, double dist, double speed, UnitOfMeasure unit);**

在路段上初始化车辆, 默认单位: 像素

参数: 
[ in ] laneNumber: 车道序号, 从0开始  
[ in ] dist: 距离路段起点距离, 默认单位: 像素  
[ in ] speed: 起动时的速度, 默认单位: 像素/秒  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
void initVehicle(IVehicle vehi){
    if (vehi.id() == 100001){
        vehi.initLane(3, TESSNG.m2p(105), 0, UnitOfMeasure.Metric);
    }
}
```

 **void initLaneConnector(int laneNumber, int toLaneNumber, double dist, double speed, UnitOfMeasure unit) ;**

初始化车辆, laneNumber: “车道连接”起始车道在所在路段的序号, 从0开始自右往左; toLaneNumber: “车道连接”目标车道在所在路段的序号, 从0开始自右往左, dist, 距起点距离, 默认单位: 像素; speed: 车速, 像素/秒, 默认单位: 像素
参数: 

[ in ] laneNumber: 车道序号, 从0开始自右侧至左侧  
[ in ] toLaneNumber: 车道序号, 从0开始自右侧至左侧  
[ in ] dist: 距离路段起点距离, 单位: : 像素
[ in ] speed: 起动时的速度, 默认单位: 像素/秒)  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```java
void initVehicle(IVehicle vehi){
    if (vehi.id() == 100001){
        vehi.initLaneConnector(0, 0, 5, 0, UnitOfMeasure.Metric);
    }
}
```

 **void setVehiType(int code);**

设置车辆类型, 车辆被创建时已确定了类型, 通过此方法可以改变车辆类型

参数: 

[ in ] code: 车辆类型编码

举例: 

```java
void initVehicle(IVehicle vehi){
    if (vehi.id() == 100001){
        vehi.setVehiType(12);
    }
}
```

 **void setColor(String color) ;**

设置车辆颜色  

参数: 
[ in ] color: 颜色RGB, 如: "#EE0000"

举例: 

```java
void initVehicle(IVehicle vehi){
    if (vehi.roadId() == 2){
        vehi.setColor("#EE0000");
    }
}
```

 **double length(UnitOfMeasure unit);**

获取车辆长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 车辆长度, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆长度="+vehi.length());
    System.out.println("车辆长度, 单位: 米="+vehi.length(UnitOfMeasure.Metric));
}
```

 **void setLength(double len, boolean bRestWidth, UnitOfMeasure unit) ;**

设置车辆长度

参数: 
[ in ] len: 车辆长度, 默认单位: 像素  
[ in ] bRestWidth: 是否同比例约束宽度, 默认为False  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```java
void initVehicle(IVehicle vehi){
    if (vehi.roadId() == 100001){
        vehi.setLength(10, false, UnitOfMeasure.Metric);
    }
}
```

 **int laneId();**

如果toLaneId() 小于等于0, 那么laneId()获取的是当前所在车道ID, 如果toLaneId()大于0, 则车辆在“车道连接”上, laneId()获取的是上游车道ID

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在车道ID="+vehi.laneId());
}
```

 **int toLaneId();**

下游车道ID。如果小于等于0, 车辆在路段的车道上, 否则车辆在连接段的“车道连接”上

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆下游车道ID="+vehi.toLaneId());
}
```

 **ILane lane();**

获取当前车道, 如果车辆在“车道连接”上, 获取的是“车道连接”的上游车道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆当前车道="+vehi.lane());
}
```

 **ILane toLane();**

如果车辆在“车道连接”上, 返回“车道连接”的下游车道, 如果当前不在“车道连接”上, 返回对象为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆下游车道="+vehi.toLane());
}
```

 **ILaneConnector laneConnector() ;**

获取当前“车道连接”, 如果在车道上, 返回空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆当前车道连接="+vehi.laneConnector());
}
```

**int currBatchNumber();**

当前仿真计算批次

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前仿真计算批次="+vehi.currBatchNumber());
}
```

 **int roadType();**

车辆所在道路类型。包NetItemType中定义了一批常量, 每一个数值代表路网上一种元素类型。如: GLinkType代表路段、GConnectorType代表连接段。

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在道路类型="+vehi.roadType());
}
```

 **double limitMaxSpeed(UnitOfMeasure unit);**

车辆所在路段或连接段最大限速, 兼顾到车辆的期望速度, 默认单位: 像素/秒  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 最大限速, 默认单位: 像素/秒


举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在路段或连接段最大限速="+vehi.limitMaxSpeed());
    System.out.println("车辆所在路段或连接段最大限速, 单位: 米/秒="+vehi.limitMaxSpeed(UnitOfMeasure.Metric));
}
```

 **double limitMinSpeed(UnitOfMeasure unit);**

车辆所在路段或连接段最小限速, 兼顾到车辆的期望速度, 默认单位: 像素/秒  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 最小限速, 默认单位: 像素/秒


举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface =iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆所在路段或连接段最小限速="+vehi.limitMinSpeed());
    System.out.println("车辆所在路段或连接段最小限速, 单位: 米/秒="+vehi.limitMaxSpeed(UnitOfMeasure.Metric));
}
```

 **int vehicleTypeCode();**

车辆类型编码。打开TESSNG, 通过菜单“车辆”->“车辆类型”打开车辆类型编辑窗体, 可以看到不同类型车辆的编码

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.printf("车辆类型编码=%s%n", vehi.vehicleTypeCode());
}
```

 **String vehicleTypeName();**

获取车辆类型名, 如“小客车”

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuiface = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuiface.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.printf("车辆类型名=%s%n", vehi.vehicleTypeName());
}
```

 **String name();**

获取车辆名称

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆名称=" + vehi.name());
}
```

 **IVehicleDriving vehicleDriving();**

获取车辆驾驶行为接口

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆驾驶行为接口=" + vehi.vehicleDriving());
}
```

 **void driving()**

驱动车辆。在每个运算周期, 每个在运行的车辆被调用一次该方法; 
如果用户使用该函数驱动车辆, 那后续整个仿真生命周期均需要用户控制该辆车。即TESSNG将此车辆的控制权移交给用户。

 **Point pos(UnitOfMeasure unit)**

获取当前位置, 默认单位: 像素。

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 当前位置, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆当前位置=" + vehi.pos());
    System.out.println("车辆当前位置, 单位: 米制="+vehi.pos(UnitOfMeasure.Metric));
}
```

 **doule zValue(UnitOfMeasure unit) **

当前高程, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 当前加速度, 默认单位: 像素/秒^2

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆当前高程=" + vehi.zValue());
    System.out.println("车辆当前高程=" + vehi.zValue(UnitOfMeasure.Metric));
}
```

 **double acce(UnitOfMeasure unit)**

当前加速度, 默认单位: 像素/秒^2 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 当前加速度, 默认单位: 像素/秒^2

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    double acceleration = vehi.acce();
    System.out.println("车辆当前加速度=" + acceleration);
    double metricAcceleration = vehi.acce(UnitOfMeasure.Metric);
    System.out.println("车辆当前加速度, 单位: 米制=" + metricAcceleration);
}
```

 **double currSpeed(UnitOfMeasure unit)**

当前速度, 默认单位: 像素/秒

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 当前速度, 默认单位: 像素/秒

举例: 

```java
        TessInterface iface = TESSNG.tessngIFace();
        SimuInterface simuIFace = iface.simuInterface();
        ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
        for (IVehicle vehi : allVehicleStarted) {
            double speed = vehi.currSpeed();
            System.out.println("车辆当前速度=" + speed);
            double metricSpeed = vehi.currSpeed(UnitOfMeasure.Metric);
            System.out.println("车辆当前速度, 单位: 米制=" + metricSpeed);
        }
```

 **double angle()**

当前角度, 北向0度顺时针  

返回: 当前角度, 单位: 度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    double angle = vehi.angle();
    System.out.println("车辆当前角度=" + angle);
}
```

 **boolean isStarted()**

是否在运行, 如果返回False, 表明车辆已驰出路网或尚未上路

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    boolean isStarted = vehi.isStarted();
    System.out.println("车辆是否在运行=" + isStarted);
}
```

 **IVehicle vehicleFront()**

获取前车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle frontVehicle = vehi.vehicleFront();
    System.out.println("前车=" + (frontVehicle != null ? frontVehicle.id() : "null"));
}
```

 **IVehicle vehicleRear()**

后车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle rearVehicle = vehi.vehicleRear();
    System.out.println("后车=" + (rearVehicle != null ? rearVehicle.id() : "null"));
}
```

 **IVehicle vehicleLFront()**

左前车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle leftFrontVehicle = vehi.vehicleLFront();
    System.out.println("左前车=" + (leftFrontVehicle != null ? leftFrontVehicle.id() : "null"));
}
```

 **IVehicle vehicleLRear()**

左后车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle leftRearVehicle = vehi.vehicleLRear();
    System.out.println("左后车=" + (leftRearVehicle != null ? leftRearVehicle.id() : "null"));
}
```

 **IVehicle vehicleRFront()**

右前车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle rightFrontVehicle = vehi.vehicleRFront();
    System.out.println("右前车=" + (rightFrontVehicle != null ? rightFrontVehicle.id() : "null"));
}
```

 **IVehicle vehicleRRear()**

右后车, 可能为空

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle rightRearVehicle = vehi.vehicleRRear();
    System.out.println("右后车=" + (rightRearVehicle != null ? rightRearVehicle.id() : "null"));
}
```

 **double vehiDistFront(UnitOfMeasure unit)**

前车间距, 默认单位: 像素; 若无前车, 则范围固定的常量, 默认单位: 像素

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 前车间距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("前车间距=" + vehi.vehiDistFront());
    System.out.println("前车间距, 单位: 米制=" + vehi.vehiDistFront(UnitOfMeasure.Metric));
}
```

 **double vehiSpeedFront(UnitOfMeasure unit)**

前车速度, 默认单位: 像素/秒; 若无前车, 则范围固定的常量, 默认单位: 像素

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 前车速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("前车速度=" + vehi.vehiSpeedFront());
    System.out.println("前车速度, 单位: 米/秒=" + vehi.vehiSpeedFront(UnitOfMeasure.Metric));
}
```

 **double vehiHeadwayFront(UnitOfMeasure unit)**

距前车时距, 若无前车, 则范围固定的常量  默认单位: 像素 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 距前车时距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("距前车时距=" + vehi.vehiHeadwayFront());
    System.out.println("距前车时距, 单位: 米制=" + vehi.vehiHeadwayFront(UnitOfMeasure.Metric));
}
```

 **double vehiDistRear(UnitOfMeasure unit)**

后车间距, 默认单位: 像素, 若无后车, 则范围固定的常量  默认单位: 像素 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 后车间距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    double rearDistance = vehi.vehiDistRear();
    System.out.println("后车间距=" + rearDistance);
    double metricRearDistance = vehi.vehiDistRear(UnitOfMeasure.Metric);
    System.out.println("后车间距, 单位: 米制=" + metricRearDistance);
}
```

 **double vehiSpeedRear(UnitOfMeasure unit)**

后车速度, 默认单位: 像素/秒  若无后车, 则范围固定的常量  默认单位: 像素 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 后车速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("后车速度=" + vehi.vehiSpeedRear());
    System.out.println("后车速度, 单位: 米/秒=" + vehi.vehiSpeedRear(UnitOfMeasure.Metric));
}
```

 **double vehiHeadwaytoRear(UnitOfMeasure unit)**

距后车时距, 默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 距后车时距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("距后车时距=" + vehi.vehiHeadwaytoRear());
    System.out.println("距后车时距, 单位: 米制=" + vehi.vehiHeadwaytoRear(UnitOfMeasure.Metric));
}
```

 **double vehiDistLLaneFront(UnitOfMeasure unit)**

相邻左车道前车间距, 默认单位: 像素; 若无目标车, 则返回固定的常量  默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻左车道前车间距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻左车道前车间距=" + vehi.vehiDistLLaneFront());
    System.out.println("相邻左车道前车间距, 单位: 米制=" + vehi.vehiDistLLaneFront(UnitOfMeasure.Metric));
}
```

 **double vehiSpeedLLaneFront(UnitOfMeasure unit)**

相邻左车道前车速度, 默认单位: 像素/秒; 若无目标车, 则返回固定的常量  默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻左车道前车速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻左车道前车速度=" + vehi.vehiSpeedLLaneFront());
    System.out.println("相邻左车道前车速度, 单位: 米/秒=" + vehi.vehiSpeedLLaneFront(UnitOfMeasure.Metric));
}
```

 **double vehiDistLLaneRear(UnitOfMeasure unit)**

相邻左车道后车间距, 默认单位: 像素; 若无目标车, 则返回固定的常量  默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻左车道后车间距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻左车道后车间距=" + vehi.vehiDistLLaneRear());
    System.out.println("相邻左车道后车间距, 单位: 米制=" + vehi.vehiDistLLaneRear(UnitOfMeasure.Metric));
}
```

 **double vehiSpeedLLaneRear(UnitOfMeasure unit)**

相邻左车道后车速度, 默认单位: 像素/秒; 若无目标车, 则返回固定的常量  默认单位: 像素

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻左车道后车速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻左车道后车速度=" + vehi.vehiSpeedLLaneRear());
    System.out.println("相邻左车道前车速度, 单位: 米/秒=" + vehi.vehiSpeedLLaneRear(UnitOfMeasure.Metric));
}
```

 **double vehiDistRLaneFront(UnitOfMeasure unit)**

相邻右车道前车间距, 默认单位: 像素; 若无目标车, 则返回固定的常量  默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻右车道前车间距, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻右车道前车间距=" + vehi.vehiDistRLaneFront();
    System.out.println("相邻右车道前车间距, 单位: 米制=" + vehi.vehiDistRLaneFront(UnitOfMeasure.Metric);
}
```

 **double vehiSpeedRLaneFront(UnitOfMeasure unit)**

相邻右车道前车速度, 默认单位: 像素/秒; 若无目标车, 则返回固定的常量  默认单位: 像素

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻右车道前车速度, 默认单位: 像素/秒  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻右车道前车速度=" + vehi.vehiSpeedRLaneFront());
    System.out.println("相邻右车道前车速度, 单位: 米/秒=" + vehi.vehiSpeedRLaneFront(UnitOfMeasure.Metric));
}
```

 **double vehiDistRLaneRear(UnitOfMeasure unit)**

相邻右车道后车间距, 默认单位: 像素; 若无目标车, 则返回固定的常量  默认单位: 像素

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻右车道后车间距, 默认单位: 像素  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻右车道后车间距=" + vehi.vehiDistRLaneRear());
    System.out.println("相邻右车道后车间距, 单位: 米制=" + vehi.vehiDistRLaneRear(UnitOfMeasure.Metric));
}
```

 **double vehiSpeedRLaneRear(UnitOfMeasure unit)**

相邻右车道后车速度, 默认单位: 像素/秒; 若无目标车, 则返回固定的常量  默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 相邻右车道后车速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("相邻右车道后车速度=" + vehi.vehiSpeedRLaneRear());
    System.out.println("相邻右车道后车速度, 单位: 米/秒=" + vehi.vehiSpeedRLaneRear(UnitOfMeasure.Metric));
}
```

 **void setIsPermitForVehicleDraw()**

设置是否允许插件绘制车辆

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    vehi.setIsPermitForVehicleDraw(true);
}
```

 **ArrayList<Point> lLaneObjectVertex(UnitOfMeasure unit) **

车道或车道连接中心线内点集, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 车道或车道连接中心线内点集

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车道或车道连接中心线内点集=" + vehi.lLaneObjectVertex());
    System.out.println("车道或车道连接中心线内点集, 单位: 米制=" + vehi.lLaneObjectVertex(UnitOfMeasure.Metric));
}
```

 **IRouting routing()**

获取车辆当前路径; 返回的是当前车辆的全局路径, 包括已经行驶过大的路段序列

返回: 车辆当前路径

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IRouting routing = vehi.routing();
    System.out.println("车辆当前路径=" + (routing != null ? routing.toString() : "null"));
}
```

 **SWIGTYPE_p_QPicture picture(self)**

获取车辆图片

返回: 车辆图片

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    String picture = vehi.picture().toString();
    System.out.println("车辆图片=" + (picture != null ? picture : "null"));
}
```

 **Vector<Point> boundingPolygon()**

获取车辆由方向和长度决定的四个拐角构成的多边型

返回: 车辆由方向和长度决定的四个拐角构成的多边型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    Vector<Point> boundingPolygon = vehi.boundingPolygon();
    System.out.println("车辆由方向和长度决定的四个拐角构成的多边型=" +
                       (boundingPolygon != null ? boundingPolygon.toString() : "null"));
}
```

 **void setTag(int tag)**

设置标签表示的状态

参数: 
[ in ] tag: 标签表示的状态

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    vehi.setTag(1);
}
```

 **int tag()**

获取标签表示的状态

返回: 标签表示的状态

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    int tag = vehi.tag();
    System.out.println("标签表示的状态=" + tag);
}
```

 **void setTextTag(String text)**

设置文本信息, 用于在运行过程保存临时信息, 方便开发

参数: 
[ in ] text: 文本信息

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    vehi.setTextTag("test");
}
```

 **String textTag()**

文本信息, 运行过程临时保存的信息, 方便开发

返回: 文本信息

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    String textTag = vehi.textTag();
    System.out.println("文本信息=" + (textTag != null ? textTag : "null"));
}
```

 **void setJsonInfo(JsonObject info)**

设置json格式数据

参数: 
[ in ] info: json格式数据

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

JsonObject json = Json.createObjectBuilder()
.add("test", "test")
.build();

for (IVehicle vehi : allVehicleStarted) {
    vehi.setJsonInfo(json);
}
```

 **JsonObject jsonInfo()**

返回json格式数据

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

for (IVehicle vehi : allVehicleStarted) {
    JsonObject jsonInfo = vehi.jsonInfo();
    System.out.println("json格式数据=" + (jsonInfo != null ? jsonInfo.toString() : "null"));
}
```

 **Object jsonProperty(String propName)**

返回json字段值

参数: 
[ in ] propName: json字段名

返回: json字段值

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

for (IVehicle vehi : allVehicleStarted) {
    Object jsonProperty = vehi.jsonProperty("test");
    System.out.println("json字段值=" + (jsonProperty != null ? jsonProperty.toString() : "null"));
}
```

 **void setJsonProperty(String key, Object value)**

设置json数据属性

参数: 
[ in ] key: json字段名  
[ in ] value: json字段值

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
ArrayList<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

for (IVehicle vehi : allVehicleStarted) {
    vehi.setJsonProperty("test", "test");
}
```





### 3.2. IVehicleDriving

驾驶行为接口, 通过此接口可以控制车辆的左右变道、设置车辆角度, 对车辆速度、坐标位置等进行控制, 可以在路网中间停止车辆运行, 将车辆移出路网, 等等。

接口方法: 

 **IVehicle vehicle()**

当前驾驶车辆

返回: 当前驾驶车辆对象

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    IVehicle drivingVehicle = vehi.vehicleDriving().vehicle();
    System.out.println("当前驾驶车辆=" + (drivingVehicle != null ? drivingVehicle.id() : "null"));
}
```

 **int getRandomNumber(self)**

获取车辆被赋予的随机数

返回: 车辆被赋予的随机数

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    double randomNumber = vehi.vehicleDriving().getRandomNumber();
    System.out.printf("车辆ID=%d 的随机数=%f%n", vehi.id(), randomNumber);
}
```

 **boolean nextPoint(self)**

计算下一点位置, 过程包括计算车辆邻车关系、公交车是否进站是否出站、是否变道、加速度、车速、移动距离、跟驰类型、轨迹类型等

返回: 计算下一点位置成功与否

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

for (IVehicle vehi : allVehicleStarted) {
    System.out.println("计算下一点位置=" + vehi.vehicleDriving().nextPoint());
}
```

 **int zeroSpeedInterval()**

当前车速为零持续时间(毫秒)

返回: 当前车速为零持续时间(毫秒)

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前车速为零持续时间(毫秒)=" + vehi.vehicleDriving().zeroSpeedInterval());
}
```

 **boolean isHavingDeciPointOnLink()**

当前是否在路段上且有决策点

返回: 当前是否在路段上且有决策点

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前是否在路段上且有决策点=" + vehi.vehicleDriving().isHavingDeciPointOnLink());
}
```

 **int followingType()**

车辆的跟驰类型, 分为: 0: 停车, 1: 正常, 5: 急减速, 6: 急加速, 7: 汇入, 8: 穿越, 9: 协作减速, 10: 协作加速, 11: 减速待转, 12: 加速待转

返回: 车辆的跟驰类型

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆的跟驰类型=" + vehi.vehicleDriving().followingType());
}
```

 **boolean isOnRouting(self)**

当前是否在路径上

返回: 当前是否在路径上

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前是否在路径上=" + vehi.vehicleDriving().isOnRouting());
}
```

 **void stopVehicle()**

停止运行, 车辆移出路网

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
if (simuTime == 20 * 1000) {
    for (IVehicle vehi : allVehicleStarted) {
        vehi.vehicleDriving().stopVehicle();
    }
}
```

 **double angle()**

旋转角, 北向0度顺时针

返回: 旋转角, 北向0度顺时针

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();

for (IVehicle vehi : allVehicleStarted) {
    System.out.println("旋转角=" + vehi.vehicleDriving().angle());
}
```

 **void setAngle(double angle)**

设置车辆旋转角; 

参数: 
[ in ] angle: 旋转角, 一周360度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
if (vehi.roadId() == 5) {
    double vehi_currentDistToEnd = vehi.vehicleDriving().distToEndpoint(true);
    if (TESSNG.p2m(vehi_currentDistToEnd) < 50) {
        vehi.vehicleDriving().setAngle(vehi.angle() + 45.0);
    }
}
}
```

 **Point3D euler()**

返回车辆欧拉角

返回: 车辆欧拉角

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("车辆欧拉角=" + vehi.vehicleDriving().euler());
}
```

 **double desirSpeed(UnitOfMeasure unit)**

当前期望速度, 与车辆自身期望速度和道路限速有关, 不大于道路限速, 默认单位: 像素/秒  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前期望速度, 默认单位: 像素/秒

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前期望速度=" + vehi.vehicleDriving().desirSpeed());
    System.out.println("当前期望速度, 单位: 米/秒=" + vehi.vehicleDriving().desirSpeed());
}
```

 **ISection getCurrRoad()**

返回当前所在路段或连接段

返回: 当前所在路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前所在路段或连接段=" + vehi.vehicleDriving().getCurrRoad());
}
```

 **ISection getNextRoad()**

下一路段或连接段

返回: 下一路段或连接段

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("下一路段或连接段=" + vehi.vehicleDriving().getNextRoad());
}
```

 **int differToTargetLaneNumber()**

与目标车道序号的差值, 不等于0表示有强制变道意图, 大于0有左变道意图, 小于0有右变道意图, 绝对值大于0表示需要强制变道次数

返回: 与目标车道序号的差值

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("与目标车道序号的差值=" + vehi.vehicleDriving().differToTargetLaneNumber());
}
```

 **void toLeftLane()**

左变道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    vehi.vehicleDriving().toLeftLane();
}
```

 **void toRightLane()**

右变道

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    vehi.vehicleDriving().toRightLane();
}
```

 **int laneNumber()**

当前车道序号, 最右侧序号为0

返回: 当前车道序号, 最右侧序号为0

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前车道序号=" + vehi.vehicleDriving().laneNumber());
}
```

 **void initTrace()**

初始化轨迹

 **void setTrace(ArrayList<Point> lPoint, UnitOfMeasure unit)**

设置轨迹, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] lPoint: 轨迹点坐标集合  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void calcTraceLength()**

计算轨迹长度; 前提是: TESSNG开启车辆轨迹记录|输出 功能  

 **int tracingType()**

返回轨迹类型, 分为: 0: 跟驰, 1: 左变道, 2: 右变道, 3: 左虚拟変道, 4: 右虚拟变道, 5: 左转待转, 6: 右转待转, 7: 入湾, 8: 出湾

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("轨迹类型=" + vehi.vehicleDriving().tracingType());
}
```

 **void setTracingType(int type)**

设置轨迹类型; TESSNG车辆后续运动轨迹按照此轨迹类型的动机产生动作, 但因为阈值条件有可能环境不满足, 因此动机并不一定能执行

参数: 
[ in ] type: 轨迹类型

 **void setLaneNumber(int number)**

设置当前车道序号

参数: 
[ in ] number: 车道序号

 **double currDistance(UnitOfMeasure unit)**

当前计算周期移动距离, 默认单位: 像素, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前计算周期移动距离, 默认单位: 像素

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前计算周期移动距离=" + vehi.vehicleDriving().currDistance());
    System.out.println("当前计算周期移动距离, 米制=" + vehi.vehicleDriving().currDistance(UnitOfMeasure.Metric));
}
```

 **double currDistanceInRoad(UnitOfMeasure unit)**

当前路段或连接上已行驶距离, 默认单位: 像素  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前路段或连接上已行驶距离, 默认单位: 像素, 可通过unit参数设置单位

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前路段或连接上已行驶距离=" + vehi.vehicleDriving().currDistanceInRoad());
    System.out.println("当前路段或连接上已行驶距离, 米制=" + vehi.vehicleDriving().currDistanceInRoad(UnitOfMeasure.Metric));
```

 **void setCurrDistanceInRoad(double dist, UnitOfMeasure unit)**

设置当前路段已行驶距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void setVehiDrivDistance(double dist, UnitOfMeasure unit)**

设置当前已行驶总里程, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 总里程, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **double getVehiDrivDistance(UnitOfMeasure unit)**

已行驶总里程, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 已行驶总里程

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("已行驶总里程=" + vehi.vehicleDriving().getVehiDrivDistance());
    System.out.println("已行驶总里程, 米制=" + vehi.vehicleDriving().getVehiDrivDistance(UnitOfMeasure.Metric));
```

 **double currDistanceInSegment(UnitOfMeasure unit)**

当前分段已行驶距离 , 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前分段已行驶距离

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前分段已行驶距离=" + vehi.vehicleDriving().currDistanceInSegment());
    System.out.println("当前分段已行驶距离, 米制=" + vehi.vehicleDriving().currDistanceInSegment(UnitOfMeasure.Metric));
}
```

 **void setCurrDistanceInSegment(double dist, UnitOfMeasure unit)**

设置当前分段已行驶的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void setSegmentIndex(int index)**

设置分段序号

参数: 
[ in ] index: 分段序号

 **void setCurrDistanceInTrace(double dist, UnitOfMeasure unit)**

设置曲化轨迹上行驶的距离, 默认单位: 像素

参数:

[ in ] dist: 距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void setIndexOfSegmInTrace(int index)**

设置曲化轨迹上的分段序号

参数: 
[ in ] index: 分段序号

 **void setChangingTracingType(boolen B)**

设置是否改变轨迹, 当设为True时会对轨迹初始化

 **double currDistance(UnitOfMeasure unit)**

当前时间段移动距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前时间段移动距离

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前时间段移动距离=" + vehi.vehicleDriving().currDistance());
    System.out.println("当前时间段移动距离, 米制=" + vehi.vehicleDriving().currDistance(UnitOfMeasure.Metric));
}
```

 **boolean setRouting(IRouting pRouting)**

设置路径, 外界设置的路径不一定有决策点, 可能是临时创建的, 如果车辆不在此路径上则设置不成功并返回False

参数: 
[ in ] pRouting: 路径

返回: 是否设置成功

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
NetInterface netIFace = iface.netInterface();
List<IVehicle> allVehiStarted_lst = simuIFace.allVehiStarted();
List<IDecisionPoint> decisionPoints_lst = netIFace.decisionPoints();

for (IVehicle vehi : allVehiStarted_lst) {
    if (vehi.roadId() == 1) {
        IDecisionPoint decisionPoint_link1 = null;
        for (IDecisionPoint decisionPoint : decisionPoints_lst) {
            if (decisionPoint.link().id() == 1) {
                decisionPoint_link1 = decisionPoint;
                break;
            }
        }

        List<IRouting> decisionPoint_link1_routings_lst = null;
        if (decisionPoint_link1 != null) {
            decisionPoint_link1_routings_lst = decisionPoint_link1.routings();
        }

        if (decisionPoint_link1_routings_lst != null && !decisionPoint_link1_routings_lst.isEmpty()) {
            IRouting lastRouting = decisionPoint_link1_routings_lst.get(decisionPoint_link1_routings_lst.size() - 1);
            if (!vehi.routing().equals(lastRouting)) {
                if (vehi.vehicleDriving().setRouting(lastRouting)) {
                    System.out.println(vehi.id() + "车辆修改路径成功。");
                }
            }
        }
    }
}

```

 **void setSegmentIndex(int index)**

设置分段序号
[ in ] index: 分段序号

 **double currDistanceInSegment(UnitOfMeasure unit))**

当前在分段上已行驶距离 , 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 当前在分段上已行驶距离

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("当前在分段上已行驶距离=" + vehi.vehicleDriving().currDistanceInSegment());
    System.out.println("当前在分段上已行驶距离, 米制=" + vehi.vehicleDriving().currDistanceInSegment(UnitOfMeasure.Metric));
}
```

 **void setCurrDistanceInSegment(double dist, UnitOfMeasure unit)**

设置在分段上已行驶距离, 默认单位: 像素, 可通过unit参数设置单位

参数:

[ in ] dist: 距离, 默认单位: 像素

[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void setX(double dist, UnitOfMeasure unit)**

设置横坐标, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] posX: 横坐标: 单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

 **void setY(double dist, UnitOfMeasure unit)**

设置纵坐标, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] posY: 纵坐标: 单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **void setV3z(double v3z, UnitOfMeasure unit)**

设置高程坐标, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] v3z: 高程坐标: 单位: 像素

[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **ArrayList<Point> changingTrace()**

变轨点集, 车辆不在车道中心线或“车道连接”中心线上时的轨迹, 如变道过程的轨迹点集 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("变轨点集=" + vehi.vehicleDriving().changingTrace());
}
```

 **double changingTraceLength(UnitOfMeasure unit)**

变轨长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 变轨长度

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("变轨长度=" + vehi.vehicleDriving().changingTraceLength());
    System.out.println("变轨长度, 米制=" + vehi.vehicleDriving().changingTraceLength(UnitOfMeasure.Metric));
}
```

 **double distToStartPoint(boolean fromVehiHead, boolean bOnCentLine, UnitOfMeasure unit)**

在车道或车道连接上到起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromVehiHead: 是否从车头计算, 如果为False, 从车辆中心点计算, 默认值为False  
[ in ] bOnCentLine: 当前是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

返回: 在车道或车道连接上到起点距离

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("在车道或车道连接上到起点距离=" + vehi.vehicleDriving().distToStartPoint());
    System.out.println("在车道或车道连接上到起点距离, 米制=" + vehi.vehicleDriving().distToStartPoint(false, true, UnitOfMeasure.Metric));
}
```

 **double distToEndpoint(boolean fromVehiHead, boolean bOnCentLine, UnitOfMeasure unit)**

在车道或“车道连接”上车辆到终点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromVehiHead: 是否从车头计算, 如果为False, 从车辆中心点计算, 默认值为False  
[ in ] bOnCentLine: 当前是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

返回: 在车道或“车道连接”上车辆到终点距离

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIFace = iface.simuInterface();
List<IVehicle> allVehicleStarted = simuIFace.allVehiStarted();
for (IVehicle vehi : allVehicleStarted) {
    System.out.println("在车道或“车道连接”上车辆到终点距离=" + vehi.vehicleDriving().distToEndpoint());
    System.out.println("在车道或“车道连接”上车辆到终点距离, 米制=" + vehi.vehicleDriving().distToEndpoint(false, true, UnitOfMeasure.Metric));
}
```

 **boolean moveToLane(ILane pLane, double dist, UnitOfMeasure unit)**

将车辆移到另一条车道上; 车辆会瞬间从当前车道移动到目标车道及指定的距离出, 后续TESSNG接管车辆继续行驶   

参数: 
[ in ] pLane: 目标车道  
[ in ] dist: 到目标车道起点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

```java
TessInterface tessngIFace = TESSNG.tessngIFace();
SimuInterface simuInterface = tessngIFace.simuInterface();
NetInterface netInterface = tessngIFace.netInterface();
IVehicle plane = simuInterface.getVehicle(100001);
ILane lane = netInterface.findLane(1);
if (plane.vehicleDriving().moveToLane(lane, 400)) {
    System.out.println("移动飞机成功");
}
```
 **boolean moveToLaneConnector(ILaneConnector pLaneConnector, double dist, UnitOfMeasure unit)**

将车辆移到另一条车道连接上; 车辆会瞬间从当前位置移动到目标车道连接及指定的距离处, 后续TESSNG接管车辆继续行驶 

参数: 
[ in ] pLaneConnector: 目标车道  
[ in ] dist: 到目标车道起点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuIface = iface.simuInterface();
NetInterface netIface = iface.netInterface();
IVehicle vehicle = simuIface.getVehicle(1001);
ILaneConnector laneConnector = netIface.findLaneConnector(5);
if (vehicle.vehicleDriving().moveToLaneConnector(laneConnector, 100)) {
    System.out.println("车辆成功移动到目标车道连接");
}
```

 **boolean move(ILaneObject pILaneObject, double dist, UnitOfMeasure unit)**

移动车辆到到另一条车道或“车道连接”; 使用该函数后, 车辆脱离TESSNG管控, 需要用户维护后期车辆运动

参数: 

[ in ] pILaneObject: 目标车道或“车道连接”  
[ in ] dist: 到目标车道起点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
举例: 

```java
if (currentSimulationTime == 10 * 1000) {
    TessInterface tessngInterface = TESSNG.tessngIFace();
    SimuInterface simuInterface = tessngInterface.simuInterface();
    NetInterface networkInterface = tessngInterface.netInterface();
    // 获取所有已启动的车辆列表
    List<IVehicle> allStartedVehicles = simuInterface.allVehiStarted();
    // 遍历所有已启动的车辆
    for (int vehicleIndex = 0; vehicleIndex < allStartedVehicles.size(); vehicleIndex++) {
        IVehicle currentVehicle = allStartedVehicles.get(vehicleIndex);
        System.out.println(vehicleIndex + " " + currentVehicle.id());
        // 筛选出L5路段上的车辆（roadId为5）
        if (currentVehicle.roadId() == 5) {
            // 查找目标Link（id为9）
            ILink targetLink = networkInterface.findLink(9);
            List<ILaneObject> targetLaneObjects = targetLink.laneObjects();
            // 计算目标车道和距离
            int targetLaneIndex = vehicleIndex % targetLaneObjects.size();
            ILaneObject targetLane = targetLaneObjects.get(targetLaneIndex);
            double distanceOnLane = vehicleIndex % 100;
            // 执行车辆位置移动
            if (currentVehicle.vehicleDriving().move(targetLane, distanceOnLane)) {
                System.out.println(currentVehicle.id() + "车辆移动成功。");
            }
        }
    }
}
```



## 4. 自定义窗口组件TessInterface

TessInterface 是TESSN对外暴露的顶级接口, 下面有三个子接口: NetInterface、SimuInterface、GuiInterface, 分别用于访问或控制路网、仿真过程和用户交互界面。

获取顶层接口的方法是: tessngIFace()。

下面是几个接口方法的说明: 

 **JsonObject config()**

获取json对象, 其中保存了config.json配置文件中的信息。

每次加载路网时会重新加载配置信息, 上次通过setConfigProperty()方法设置的属性会在重新加载路网后丢失。

```java
TessInterface iface = TESSNG.tessngIFace();
JsonObject configInformation = iface.config();
System.out.println("config.json配置文件中的信息: " + configInformation);
```

 **void setConfigProperty(String key, Object value)**

设置配置属性

```java
TessInterface iface = TESSNG.tessngIFace();
iface.setConfigProperty("__httpserverport",8080);
System.out.println("config.json配置文件中的信息: " + configInformation);
```

 **void releasePlugins()**

卸载并释放插件

 **NetInterface netInterface()**

返回用于访问控制路网的接口NetInterface

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netIface = iface.netInterface();
System.out.println("用于访问控制路网的接口NetInterface: " + netIface);
```

 **def simuInterface(self) -> Tessng.SimuInterface: ...**

返回用于控制仿真过程的接口SimuInterface

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
System.out.println("用于控制仿真过程的接口SimuInterface: " + simuInterface);
```

 **def guiInterface(self) -> Tessng.GuiInterface: ...**

返回用于访问控制用户介面的接口GuiInterface

```java
TessInterface iface = TESSNG.tessngIFace();
GuiInterface guiIface = iface.guiInterface();
System.out.println("用于访问控制用户介面的接口GuiInterface: " + guiIface);
```

 **boolean loadPluginFromMem(TessPlugin pPlugin)**

从内存加载插件, 此方法便于用户基于API进行二次开发。

下面对三个子接口进行详解: 

### 4.1. NetInterface

 terface是TessInterface的子接口, 用于访问、控制路网的接口, 通过这个接口可以从文件加载路网、创建路段、连接段、发车点等。

下面对NetInterface接口方法作详细解释。

 **void openNetFle(string filePath)**

打开保存在文件中的路网

参数: 
[ in ] filePath: 路网文件全路径名

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netIFace = iface.netInterface();
netIFace.openNetFle("C: /TESSNG/Example/杭州武林门区域路网公交优先方案.tess")
```

 **void openNetByNetId(int netId)**

从专业数据库加载路网

 **boolean saveRoadNet()**

保存路网, 打开另存为窗口, 但无法覆盖已保存的路网文件,保存成功返回true,否则返回false。

 **String netFilePath()**

获取路网文件全路径名, 如果是专业数据保存的路网, 返回的是路网ID

 **IRoadNet roadNet()**

获取路网对象

 **IRoadNet netAttrs()**

获取路网对象, 如果路网是从opendrive导入的, 此路网对象可能保存了路网中心点所在的经纬度坐标, 以及大地坐标等信息

 **IRoadNet setNetAttrs(String name, String sourceType, Point centerPoint, String backgroundUrl, JsonObject otherAttrsJson)**

设置路网基本信息

参数:
[ in ] name: 路网名称
[ in ] sourceType: 数据来源分类, 默认为 “TESSNG”, 表示路网由TESSNG软件直接创建。取值“OPENDRIVE”, 表示路网是经过opendrive路网导入而来
[ in ] centerPoint: 中心点坐标所在路网, 默认为(0, 0) , 用户也可以将中心点坐标保存到otherAttrsJson字段里
[ in ] backgroundUrl: 底图路径
[ in ] otherAttrsJson: 保存在json对象中的其它属性, 如大地坐标等信息。

 **SWIGTYPE_p_QGraphicsScene graphicsScene()**

获取场景对象

 **SWIGTYPE_p_QGraphicsView graphicsView()**

获取视图对象

 **double sceneScale()**

场景中的像素比, 单位: 米/像素

 **double sceneWidth(UnitOfMeasure unit)**

场景宽度, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **double sceneHeigth(UnitOfMeasure unit)**

场景高度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **SWIGTYPE_p_QByteArray backgroundMap()**

获取路网背景图

 **ArrayList<ISection> sections()**

获取所有Section

 **ArrayList<Long> linkIds()**

获取路网中所有路段的ID列表

 **int linkCount()**

获取路网中的路段数

 **ArrayList<ILink> links()**

获取路网中的路段对象集

 **ArrayList<Long> connectorIds()**

获取路网中的连接段ID集

 **int connectorCount()**

获取路网中的连接段数

 **ArrayList<IConnector> connectors()**

获取路网中的连接段对象集

 **ArrayList<IConnectorArea> allConnectorArea()**

获取路网中的连接面域集

 **int signalLampCount()**

获取路网中的信号灯数

 **ArrayList<Long> signalLampIds()**

获取路网中的信号灯ID集

 **ArrayList<ISignalLamp> signalLamps()**

获取路网中的信号灯集

 **int guidArrowCount()**

获取路网中的导向箭头数

 **ArrayList<Long> guidArrowIds()**

获取路网中的导向箭头ID集

 **ArrayList<IDispatchPoint> dispatchPoints()**

获取路网中的发车点集

 **ArrayList<IBusLine> buslines()**

获取路网中的公交线路集

**ArrayList<IBusStation> busStations()**

获取路网中的公交站点集

 **ArrayList<IDecisionPoint> decisionPoints()**

获取路网中的决策点列表

 **ArrayList<IVehicleDrivInfoCollector> vehiInfoCollectors()**

获取路网中的所有车辆检测器

 **ArrayList<IVehicleQueueCounter> vehiQueueCounters()**

获取路网中的所有排队计数器

 **ArrayList<IVehicleTravelDetector> vehiTravelDetectors()**

获取路网中的所有车辆行程时间检测器, 返回列表中的每一个元素是一对行程时间检测器的起始检测器

 **ArrayList<CrossPoint> crossPoints(ILaneConnector pLaneConnector)**

获取路网中当前“车道连接”穿过其它“车道连接”形成的交叉点列表; 

参数: 
[ in ] pLaneConnector: “车道连接”对象

返回: 交叉点列表

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netIFace = iface.netInterface();
// 获取 ID 为 6 的 Connector
IConnector connector = netIFace.findConnector(6);
if (connector != null) {
    // 获取该 Connector 的所有车道连接（ILaneConnector）
    List<ILaneConnector> laneConnectors = connector.laneConnectors();
    if (laneConnectors != null && !laneConnectors.isEmpty()) {
        for (ILaneConnector laneConnector : laneConnectors) {
            // 获取当前车道连接的交叉点列表
            ArrayList<CrossPoint> crossPoints = netIFace.crossPoints(laneConnector);
            if (crossPoints != null && !crossPoints.isEmpty()) {
                for (CrossPoint crossPoint : crossPoints) {
                    // 获取主车道连接（即被交叉的车道连接）
                    ILaneConnector mainLaneConnector = crossPoint.getMpLaneConnector();
                    // 获取交叉点坐标
                    Point crossPointCoord = crossPoint.getMCrossPoint();
                    // 输出信息
                    System.out.printf("主车道连接, 即被交叉的“车道连接”: %d%n", mainLaneConnector.id());
                    System.out.printf("交叉点坐标为: (%.2f, %.2f)%n",
                                      crossPointCoord.getX(), crossPointCoord.getY());
                }
            } 
        }
    } 
} 
```

  **int signalControllerCount()**

获取路网中信号机总数

 **ArrayList<Long> signalControllerIds()**

获取信号机编号列表

 **ArrayList<ISignalController> signalControllers()**

获取信号机对象列表

 **int signalPlanCount()**

获取路网中信控方案总数

 **ArrayList<Long> signalPlanIds()**

获取信控方案ID集合

 **ArrayList<ISignalPlan> signalPlans()**

获取信控方案对象列表

 **def signalPhases(self) -> typing.List<Tess.ISignalPhase>: ...**

获取所有信控方案的相位信息

 **ArrayList<IRoadWorkZone> roadWorkZones()**

获取所有施工区

 **ArrayList<IAccidentZone> accidentZones()**

获取所有事故区

 **IAccidentZone findAccidentZone(int accidentZoneId)**

根据ID查询事故区

参数: 
[ in ] accidentZoneId: 事故区ID

 **ArrayList<ILimitedZone> limitedZones()**

获取所有限行区

 **ArrayList<IReconstruction> reconstructions()**

获取所有改扩建

 **ArrayList<IReduceSpeedArea> reduceSpeedAreas()**

获取所有限速区 

 **ArrayList<ITollLane> tollLanes()**

获取所有收费车道列表

 **ArrayList<ITollDecisionPoint> tollDecisionPoints()**

获取所有收费决策点列表

 **ArrayList<IParkingRegion> parkingRegions()**

获取所有停车区列表

 **ArrayList<IParkingDecisionPoint> parkingDecisionPoints()**

获取所有停车决策点列表

 **ArrayList<DynaParkingTimeDis> parkingTimeDis()**

获取停车场停车时距分布列表

 **SWIGTYPE_p_QListT_Online__TollStation__DynaTollParkingTimeDis_t tollParkingTimeDis()**

获取收费站停车时距分布列表

 **ArrayList<IJunction> getAllJunctions()**

获得所有节点, 返回类型为字典

 **ArrayList<FlowTimeInterval> getFlowTimeIntervals()**

获取所有时间段

 **FlowTimeInterval addFlowTimeInterval()**

添加时间段, 返回新时间段ID, 失败返回-1; 调用这个接口后会根据上一个时间段计算当前时间段


 **ArrayList<FlowTurning> getJunctionFlows(long junctionId)**

获取节点流向信息  
[ in ]junctionId: 节点ID

 **SWIGTYPE_p_QMapT_QPairT_long_long_t_QVectorT_QListT_ILink_p_t_t_t buildAndApplyPaths()**

构建并应用路径, 返回路径结果映射: < 起始路段ID, 终点路段ID > - > 可行路径列表

 **SWIGTYPE_p_QHashT_long_QListT_Online__Junction__FlowTurning_t_t calculateFlows()**

计算并应用流量结果, 返回时间段ID到流量计算结果的映射

 **ArrayList<IPedestrianType> pedestrianTypes()**

获取所有行人类型

 **SWIGTYPE_p_QListT_Online__Pedestrian__PedestrianComposition_t pedestrianCompositions()**

获取所有行人组成

 **SWIGTYPE_p_QListT_Online__Pedestrian__LayerInfo_t layerInfos()**

获取所有层级信息

 **SWIGTYPE_p_QListT_IPedestrianRectRegion_p_t pedestrianRectRegions()**

获取所有矩形面域

 **SWIGTYPE_p_QListT_IPedestrianEllipseRegion_p_t pedestrianEllipseRegions()**

获取所有椭圆形面域

 **SWIGTYPE_p_QListT_IPedestrianTriangleRegion_p_t pedestrianTriangleRegions()**

获取所有三角形面域

 **SWIGTYPE_p_QListT_IPedestrianFanShapeRegion_p_t pedestrianFanShapeRegions()**

获取所有扇形面域

 **SWIGTYPE_p_QListT_IPedestrianPolygonRegion_p_t pedestrianPolygonRegions()**

获取所有多边形面域

 **SWIGTYPE_p_QListT_IPedestrianSideWalkRegion_p_t pedestrianSideWalkRegions()**

获取所有人行道

 **SWIGTYPE_p_QListT_IPedestrianCrossWalkRegion_p_t pedestrianCrossWalkRegions()**

获取所有人行横道

 **SWIGTYPE_p_QListT_IPedestrianPathPoint_p_t pedestrianPathStartPoints()**

获取所有行人发生点

 **SWIGTYPE_p_QListT_IPedestrianPathPoint_p_t pedestrianPathEndPoints()**

获取所有行人结束点

 **SWIGTYPE_p_QListT_IPedestrianPathPoint_p_t pedestrianPathDecisionPoints()**

获取所有行人决策点

 **SWIGTYPE_p_QListT_IPedestrianPath_p_t pedestrianPaths()**

获取所有行人路径, 包括局部路径

 **SWIGTYPE_p_QListT_ICrosswalkSignalLamp_p_t crosswalkSignalLamps()**

获取所有人行横道红绿灯

 **ILink findLink(int id)**

根据路段ID查找路段

参数: 
[ in ] id: 路段ID

 **ILane findLane(int id)**

根据车道ID查找车道

参数: 
[ in ] id: 车道ID

 **ILaneConnector findLaneConnector(int id)**

根据“车道连接”ID查找“车道连接”

参数: 
[ in ] connectorId: “车道连接”ID

 **ILaneConnector findLaneConnector(int fromLaneId, int toLaneId)**

根据起始车道ID及目标车道ID查找“车道连接”

参数: 
[ in ] fromLaneId: 起始车道ID
[ in ] toLaneId: 目标车道ID

 **IConnector findConnector(int id)**

根据连接段ID查找连接段

参数: 
[ in ] id: 连接段ID

 **IConnectorArea findConnectorArea(int id)**

根据面域ID查找面域

 **IConnector findConnectorByLinkIds(int fromLinkId, int toLinkId)**

参数: 
[ in ] fromLinkId: 起始路段ID
[ in ] toLinkId: 目标路段ID

根据起始路段ID及目标路段ID查找连接段

 **ISignalLamp findSignalLamp(int id)**

参数: 
[ in ] id: 信号灯ID

根据信号灯ID查找信号灯

 **ISignalPhase findSignalPhase(int id)**

根据信号相位ID查找信号相位, 目前的接口有点问题, 返回的是none

参数: 
[ in ] id: 信号相位ID

 **IDispatchPoint findDispatchPoint(int id)**

根据发车点ID查找发车点

参数: 
[ in ] id: 发车点ID

 **IBusLine findBusline(int buslineId)**

根据公交线路ID查找公交线路

参数: 
[ in ] buslineId: 公交线路ID

 **IBusLine findBuslineByFirstLinkId(int linkId)**

根据公交线路起始路段ID查找公交线路

参数: 
[ in ] linkId: 公交线路起始段ID

 **IBusStation findBusStation(int stationId)**

根据公交站点ID查询公交站点

参数: 
[ in ] stationId: 公交站点ID

 **ArrayList<IBusStationLine> findBusStationLineByStationId(long stationId)**

根据公交站点ID查询相关BusLineStation

参数: 
[ in ] stationId: 公交站点ID

 **IDecisionPoint findDecisionPoint(int id)**

根据ID查找决策点

参数: 
[ in ] id: 决策点ID

返回: 决策点对象

 **IVehicleDrivInfoCollector findVehiInfoCollector(int id)**

根据ID查询车辆检测器

参数: 
[ in ] id: 车辆检测器ID

返回: 车辆检测器对象

 **IVehicleQueueCounter findVehiQueueCounter(int id)**

根据ID查询车辆排队计数器

参数: 
[ in ] id: 排队计数器ID

返回: 排队计数器对象

 **IRouting findRouting(int id)**

根据路径ID查找路径

参数: 
[ in ] id: 路径ID

 **IVehicleTravelDetector findVehiTravelDetector(int id)**

根据ID查询车辆行程时间检测器, 返回一对行程时间检测器中起始检测器

参数: 
[ in ] id: 行程时间检测器ID

返回: 行程时间检测器对象

 **ISignalController findSignalControllerById(int id)**

获取指定id的信号机对象

参数: 
[ in ] id: 信号机ID

 **ISignalController findSignalControllerByName(String name)**

根据名称查询信号机(如果同名返回第一个)

参数: 
[ in ] name: 信号机名称

 **ISignalPlan findSignalPlanById(int id)**

获取指定id的信号机对象

参数: 
[ in ] id: 信号机ID

 **ISignalPlan findSignalPlanByName(String name)**

根据名称查询信号机(如果同名返回第一个)

参数: 
[ in ] name: 信号机名称

 **IRoadWorkZone findRoadWorkZone(int roadWorkZoneId)**

根据ID查询施工区

参数: 
[ in ] roadWorkZoneId: 施工区ID

返回: 施工区对象

 **ILimitedZone findLimitedZone(int limitedZoneId)**

根据ID获取指定的限行区

参数: 
[ in ] limitedZoneId: 限行区ID

 **IReconstruction findReconstruction(int reconstructionId)**

根据ID获取指定的改扩建对象

参数: 
[ in ] reconstructionId: 改扩建ID

 **IReduceSpeedArea findReduceSpeedArea(int id)**

查询指定ID的限速区 

参数: 
[ in ] id: 限速区ID

 **ITollLane findTollLane(int id)**

通过id查询收费车道

参数: 
[ in ] id: 收费车道ID

 **ITollDecisionPoint findTollDecisionPoint(int id)**

通过id查询收费决策点

参数: 
[ in ] id: 收费决策点ID

 **IParkingRegion findParkingRegion(int id)**

通过id查询停车区域

参数: 
[ in ] id: 停车区域ID

 **IParkingDecisionPoint findParkingDecisionPoint(int id)**

通过id查询停车决策点

参数: 
[ in ] id: 停车决策点ID

 **IJunction findJunction(int junctionId)**

根据路径ID查找节点 

参数: 
[ in ] id: 节点ID

 **IPedestrianRegion findPedestrianRegion(int id)**

根据id获取行人面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianRectRegion findPedestrianRectRegion(int id)**

根据id获取矩形面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianEllipseRegion findPedestrianEllipseRegion(int id)**

根据id获取椭圆形面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianTriangleRegion findPedestrianTriangleRegion(int id)**

根据id获取三角形面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianFanShapeRegion findPedestrianFanShapeRegion(int id)**

根据id获取扇形面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianPolygonRegion findPedestrianPolygonRegion(int id)**

根据id获取多边形面域

参数: 
[ in ] id: 行人面域ID

 **IPedestrianSideWalkRegion findPedestrianSideWalkRegion(int id)**

根据id获取人行道

参数: 
[ in ] id: 人行道ID

 **IPedestrianCrossWalkRegion findPedestrianCrossWalkRegion(int id)**

根据id获取人行横道

参数: 
[ in ] id: 人行横道ID

 **IPedestrianPathPoint findPedestrianPathStartPoint(int id)**

根据id获取行人发生点

参数: 
[ in ] id: 行人发生点ID

 **IPedestrianPathPoint findPedestrianPathEndPoint(int id)**

根据id获取行人结束点

参数: 
[ in ] id: 行人结束点ID

 **IPedestrianPathPoint findPedestrianDecisionPoint(int id)**

根据id获取行人决策点

参数: 
[ in ] id: 行人决策点ID

 **IPedestrianPath findPedestrianPath(int id)**

根据id获取行人路径, 包括局部路径

参数: 
[ in ] id: 行人路径ID

 **ICrosswalkSignalLamp findCrosswalkSignalLamp(int id)**

根据id获取人行横道红绿灯

参数: 
[ in ] id: 人行横道红绿灯ID

 **PedestrianPathStartPointConfigInfo findPedestrianStartPointConfigInfo(int id)**

根据id获取行人发生点配置信息, id为行人发生点ID

参数: 
[ in ] id: 行人发生点ID

 **PedestrianDecisionPointConfigInfo findPedestrianDecisionPointConfigInfo(int id)**

根据id获取行人决策点配置信息, id为行人决策点ID

参数: 
[ in ] id: 行人决策点ID

 **ArrayList<Point> linkCenterPoints(long linkId, UnitOfMeasure unit)**

获取指定路段的中心线断点集

参数: 
[ in ] linkId: 指定路段ID  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **ArrayList<Point> laneCenterPoints(long laneId, UnitOfMeasure unit)**

**获取指定车道的中心线断点集**

参数: 
[ in ] laneId: 指定车道ID  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **boolean judgeLinkToCross(int linkId)**

判断路段去向是否进入交叉口, 以面域是否存在多连接段以及当前路段与后续路段之间的角度为依据; 和节点Ijunction没啥关系

 **ILink createLink(ArrayList<Point> lCenterPoint, int laneCount, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建路段

参数: 
[ in ] lCenterPoint: 路段中心线断点集  
[ in ] laneCount: 车道数  
[ in ] linkName: 路段名, 默认为空, 将以路段ID作为路段名  
[ in ] bAddToScene: 创建后是否放入路网场景, 默认为True  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
注: 如传入米制参数, 请勿遗忘传入linkName与bAddToScene参数。  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
Point startPoint = new Point(TESSNG.m2p(-300), 0);
Point endPoint = new Point(TESSNG.m2p(300), 0);
ArrayList<Point> lPoint = new ArrayList<>();
lPoint.add(startPoint);
lPoint.add(endPoint);
ILink link1 = netiface.createLink(lPoint, 7, "曹安公路");
```

返回: 路段对象。

 **ILink createLink3D(ArrayList<Point3D> lCenterV3, int laneCount, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建路段, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] lCenterV3: 路段中心线断点序列, 每个断点均为三维空间的点  
[ in ] laneCount: 车道数  
[ in ] linkName: 路段名  
[ in ] bAddToScene: 创建后是否放入路网场景, 默认为True  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 路段对象。  
注: 如传入米制参数, 请勿遗忘传入linkName与bAddToScene参数。 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
ArrayList<Vector3D> centerPoints1 = new ArrayList<>();
Vector3D startPoint1 = new Vector3D(300, -6, 0); // 起点
Vector3D endPoint1 = new Vector3D(25, -6, 5);     // 终点
centerPoints1.add(startPoint1);
centerPoints1.add(endPoint1);
int laneCount = 3;                          // 车道数量
String linkName = "东进口creatlink3d";       // 路段名称
boolean bAddToScene = true;                 // 是否加入场景
// 创建3D道路链接
ILink e_approach = netiface.createLink3D(centerPoints1, laneCount, linkName, bAddToScene,UnitOfMeasure.Metric);
```

 **ILink createLinkWithLaneWidth(ArrayList<Point> lCenterPoint, ArrayList<Double> lLaneWidth, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建路段, 默认单位: 像素, 可通过unit参数设置单位

参数: 

[ in ] lCenterPoint: 路段中心线断点序列  
[ in ] lLaneWidth: 车道宽度列表  
[ in ] linkName: 路段名  
[ in ] bAddToScene: 是否加入场景, 默认为True  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 路段对象。  
注: 如传入米制参数, 请勿遗忘传入linkName与bAddToScene参数。  

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
ArrayList<Point> centerPoints1 = new ArrayList<>();
Point startPoint1 = new Point(6, 300); // 起点
Point endPoint1 = new Point(6, 25);     // 终点
centerPoints1.add(startPoint1);
centerPoints1.add(endPoint1);
// 设置每条车道的宽度（从左到右）
ArrayList<Double> laneWidths = new ArrayList<>();
laneWidths.add(3.5);
laneWidths.add(3.0);
laneWidths.add(3.0);
String linkName = "南进口creatlinkWithLaneWidth"; // 路段名称
boolean bAddToScene = true;                       // 是否添加到场景
ILink s_approach = netiface.createLinkWithLaneWidth(centerPoints1, laneWidths, linkName, bAddToScene,UnitOfMeasure.Metric);
```

 **ILink createLink3DWithLaneWidth(ArrayList<Point3D> lCenterV3, ArrayList<Double> lLaneWidth, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建指定车道宽度的3D路段, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] lCenterV3: 路段中心线断点序列, 每个断点均为三维空间的点  
[ in ] lLaneWidth: 车道宽度列表  
[ in ] linkName: 路段名  
[ in ] bAddToScene: 是否加入场景, 默认为True  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 路段对象。  
注: 如传入米制参数, 请勿遗忘传入linkName与bAddToScene参数。   

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
ArrayList<Point3D> centerPoints3D = new ArrayList<>();
Point3D startPoint3D = new Point3D(6, 300, 0); // 起点 (x, y, z)
Point3D endPoint3D = new Point3D(6, 25, 5);    // 终点 (x, y, z)
centerPoints3D.add(startPoint3D);
centerPoints3D.add(endPoint3D);
// 设置每条车道的宽度（从左到右）
ArrayList<Double> laneWidths = new ArrayList<>();
laneWidths.add(3.5);
laneWidths.add(3.0);
laneWidths.add(3.0);
String linkName = "南进口creatlink3dWithLaneWidth"; // 路段名称
boolean bAddToScene = true;                       // 是否添加到场景
// 创建带有自定义车道宽度的3D路段
ILink s_approach = netiface.createLink3DWithLaneWidth(centerPoints3D, laneWidths, linkName, bAddToScene, UnitOfMeasure.Metric);
if (s_approach != null) {
    System.out.println("3D路段创建成功，ID: " + s_approach.id());
} else {
    System.out.println("3D路段创建失败");
}
```

 **ILink createLink3DWithLanePoints(ArrayList<Point3D> lCenterLineV3, ArrayList<TreeMap<String, ArrayList<Point3D>>> lanesWithPoints, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建指定车道断点的3D路段, 默认单位: 像素

参数: 
[ in ] lCenterLineV3: 路段中心点集(对应TESSNG路段中心点), 每个点均为三维空间的  
[ in ] lanesWithPoints: 车道数据集合, 每个成员是QMap< QString, QList< QVector3D>>类型数据, 有三个key, 分别是“left”、“center”、“right”、分别表示一条车道左、中、右侧断点序列。  
[ in ] linkName: 路段名, 默认为路段ID  
[ in ] bAddToScene: 是否加入路网, 默认True表示加入  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 路段对象  
注: 如传入米制参数, 请勿遗忘传入linkName与bAddToScene参数。

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 定义起点和终点
Point3D startPoint = new Point3D(-6, -300, 0);
Point3D endPoint = new Point3D(-6, -25, 5);
ArrayList<Point3D> lPoint = new ArrayList<>();
lPoint.add(startPoint);
lPoint.add(endPoint);

// 定义每条车道的左右和中心线点集
ArrayList<Point3D> lane3_left = new ArrayList<>();
lane3_left.add(new Point3D(-3, -300, 0));
lane3_left.add(new Point3D(-3, -25, 0));

ArrayList<Point3D> lane3_mid = new ArrayList<>();
lane3_mid.add(new Point3D(-1.5, -300, 0));
lane3_mid.add(new Point3D(-1.5, -25, 0));

ArrayList<Point3D> lane3_right = new ArrayList<>();
lane3_right.add(new Point3D(0, -300, 0));
lane3_right.add(new Point3D(0, -25, 0));

ArrayList<Point3D> lane2_left = new ArrayList<>();
lane2_left.add(new Point3D(-6, -300, 0));
lane2_left.add(new Point3D(-6, -25, 0));

ArrayList<Point3D> lane2_mid = new ArrayList<>();
lane2_mid.add(new Point3D(-4.5, -300, 0));
lane2_mid.add(new Point3D(-4.5, -25, 0));

ArrayList<Point3D> lane2_right = new ArrayList<>();
lane2_right.add(new Point3D(-3, -300, 0));
lane2_right.add(new Point3D(-3, -25, 0));

ArrayList<Point3D> lane1_left = new ArrayList<>();
lane1_left.add(new Point3D(-9, -300, 0));
lane1_left.add(new Point3D(-9, -25, 0));

ArrayList<Point3D> lane1_mid = new ArrayList<>();
lane1_mid.add(new Point3D(-7.5, -300, 0));
lane1_mid.add(new Point3D(-7.5, -25, 0));

ArrayList<Point3D> lane1_right = new ArrayList<>();
lane1_right.add(new Point3D(-6, -300, 0));
lane1_right.add(new Point3D(-6, -25, 0));
ArrayList<TreeMap<String, ArrayList<Point3D>>> lanes = new ArrayList<>();
// 车道1
TreeMap<String, ArrayList<Point3D>> lane1 = new TreeMap<>();
lane1.put("left", lane1_left);
lane1.put("center", lane1_mid);
lane1.put("right", lane1_right);
// 车道2
TreeMap<String, ArrayList<Point3D>> lane2 = new TreeMap<>();
lane2.put("left", lane2_left);
lane2.put("center", lane2_mid);
lane2.put("right", lane2_right);
// 车道3
TreeMap<String, ArrayList<Point3D>> lane3 = new TreeMap<>();
lane3.put("left", lane3_left);
lane3.put("center", lane3_mid);
lane3.put("right", lane3_right);
// 添加到列表
lanes.add(lane1);
lanes.add(lane2);
lanes.add(lane3);
// 创建带有自定义车道点集的3D路段
String linkName = "北进口createLink3DWithLanePoints";
boolean bAddToScene = true;
ILink n_approach = netiface.createLink3DWithLanePoints(lPoint, lanes, linkName, bAddToScene, UnitOfMeasure.Metric);
```

 **IConnector createConnector(int fromLinkId, int toLinkId, ArrayList<Integer> lFromLaneNumber, ArrayList<Integer> lToLaneNumber, String connName, boolean bAddToScene)**

创建连接段

参数: 

[ in ] fromLinkId: 起始路段ID  
[ in ] toLinkId: 目标路段ID  
[ in ] lFromLaneNumber: 连接段起始车道序号集  
[ in ] lToLaneNumber: 连接段目标车道序号集   
[ in ] connName: 连接段名, 默认为空, 以两条路段的ID连接起来作为名称   
[ in ] bAddToScene: 创建后是否放入路网场景, 默认为True  

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 创建西进口路段
// 定义起点和终点
Point startPoint = new Point(-300, 6);
Point endPoint = new Point(-25, 6);
ArrayList<Point> lPoint = new ArrayList<>();
lPoint.add(startPoint);
lPoint.add(endPoint);
int laneCount = 3;
String linkName1 = "西进口";
boolean bAddToScene1 = true;
ILink w_approach = netiface.createLink(lPoint, laneCount, linkName1, bAddToScene1);
// 创建东进口 3D 路段
// 定义 3D 起点和终点
Vector3D startPoint3D = new Vector3D(300, -6, 0);
Vector3D endPoint3D = new Vector3D(25, -6, 5);
ArrayList<Vector3D> lPoint3D = new ArrayList<>();
lPoint3D.add(startPoint3D);
lPoint3D.add(endPoint3D);
String linkName2 = "东进口creatlink3d";
boolean bAddToScene2 = true;
ILink e_approach = netiface.createLink3D(lPoint3D, laneCount, linkName2, bAddToScene2);
// 定义车道编号列表
ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
lFromLaneNumber.add(2);
ArrayList<Integer> lToLaneNumber = new ArrayList<>();
lToLaneNumber.add(2);
// 创建连接段
String connectorName = "东西直行";
boolean bAddToScene3 = true;
IConnector w_e_straight_connector = netiface.createConnector(w_approach.id(), e_approach.id(), lFromLaneNumber, lToLaneNumber, connectorName, bAddToScene3);
```

 **IConnector createConnector3DWithPoints(long fromLinkId, long toLinkId, ArrayList<Integer> lFromLaneNumber, ArrayList<Integer> lToLaneNumber, ArrayList<TreeMap<String, ArrayList<Point3D>>> laneConnectorWithPoints, String connName, boolean bAddToScene, UnitOfMeasure unit)**

创建连接段, 创建连接段后将“车道连接”中自动计算的断点集用参数laneConnectorWithPoints断点替换; 即创建指定断点的3D连接段, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] fromLinkId: 起始路段ID  
[ in ] toLinkId: 目标路段ID  
[ in ] lFromLaneNumber: 起始路段参于连接的车道序号  
[ in ] lToLaneNumber: 目标路段参于连接的车道序号  
[ in ] laneConnectorWithPoints: “车道连接”数据列表, 成员是TreeMap<String, ArrayList<Point3D>>类型数据, 有三种key, 分别是“left”、“center”、“right”, 表示一条“车道连接”左、中、右侧断点序列   
[ in ] connName: 连接段名, 默认将起始路段ID和目标路段ID用“_”连接表示连接段名, 如“100_101”。  
[ in ] bAddToScene: 是否加入到场景, 默认为True  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位
返回: 连接段对象  

注: 如传入米制参数, 请勿遗忘传入connName与bAddToScene参数。  

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 创建西进口路段
// 定义起点和终点
Point startPoint = new Point(-300, 6);
Point endPoint = new Point(-25, 6);
ArrayList<Point> lPoint = new ArrayList<>();
lPoint.add(startPoint);
lPoint.add(endPoint);
int laneCount = 3;
String linkName1 = "西进口";
boolean bAddToScene1 = true;
ILink w_approach = netiface.createLink(lPoint, laneCount, linkName1, bAddToScene1);
// 创建东进口 3D 路段
// 定义 3D 起点和终点
Vector3D startPoint3D = new Vector3D(300, -6, 0);
Vector3D endPoint3D = new Vector3D(25, -6, 5);
ArrayList<Vector3D> lPoint3D = new ArrayList<>();
lPoint3D.add(startPoint3D);
lPoint3D.add(endPoint3D);
String linkName2 = "东进口creatlink3d";
boolean bAddToScene2 = true;
ILink e_approach = netiface.createLink3D(lPoint3D, laneCount, linkName2, bAddToScene2);
// 定义车道编号列表
ArrayList<Integer> lFromLaneNumber = new ArrayList<>();
lFromLaneNumber.add(2);
ArrayList<Integer> lToLaneNumber = new ArrayList<>();
lToLaneNumber.add(2);
// 定义车道点集，这里假设每个车道有一个点集
ArrayList<Point3D> lane1Points = new ArrayList<>();
lane1Points.add(new Point3D(0, 0, 0));
lane1Points.add(new Point3D(1, 1, 1));
// 创建车道结构
TreeMap<String, ArrayList<Point3D>> laneMap = new TreeMap<>();
laneMap.put("center", lane1Points);
// 创建车道列表
ArrayList<TreeMap<String, ArrayList<Point3D>>> laneConnectorWithPoints = new ArrayList<>();
laneConnectorWithPoints.add(laneMap);
String connectorName = "直行";
boolean bAddToScene3 = true;
IConnector connector = netiface.createConnector3DWithPoints(w_approach.id(), e_approach.id(), lFromLaneNumber, lToLaneNumber, laneConnectorWithPoints, connectorName, bAddToScene3, UnitOfMeasure.Metric);
```

 **IDispatchPoint createDispatchPoint(ILink pLink, String dpName, boolean bAddToScene)**

创建发车点

参数: 
[ in ] pLink: 路段, 在其上创建发车点  
[ in ] dpName: 发车点名称, 默认为空, 将以发车点ID作为名称  
[ in ] bAddToScene: 创建后是否放入路网场景, 默认为True  

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 定义起点和终点
Point startPoint1 = new Point(TESSNG.m2p(-300), TESSNG.m2p(-200));
Point endPoint1 = new Point(TESSNG.m2p(-50), TESSNG.m2p(-200));
ArrayList<Point> lPoint1 = new ArrayList<>();
lPoint1.add(startPoint1);
lPoint1.add(endPoint1);
int laneCount = 3;
String linkName = "信控编辑路段1";
// 创建路段
ILink link1 = netiface.createLink(lPoint1, laneCount, linkName);
if (link1 != null) {
    System.out.println("路段创建成功，ID: " + link1.id());
    // 创建发车点
    IDispatchPoint dp = netiface.createDispatchPoint(link1);
    if (dp != null) {
        System.out.println("发车点创建成功");
        // 设置发车间隔，含车型组成、时间间隔、发车数
        dp.addDispatchInterval(1, 3600, 3600);
    } else {
        System.out.println("发车点创建失败");
    }
} else {
    System.out.println("路段创建失败");
}
```

 **int createVehicleComposition(String name, ArrayList<VehiComposition> lVehiComp)**

创建车型组成, 如果车型组成名已存在或相关车型编码不存在或相关车型占比小于0则返回-1, 否则新建车型组成, 并返回车型组成编码  

参数: 
[ in ] name: 车型组成名  
[ in ] lVehiComp: 不同车型占比列表  

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 创建车辆组成及指定车辆类型
ArrayList<VehiComposition> vehiType_proportion_lst = new ArrayList<>();
// 车型组成: 小客车 0.3, 大客车 0.2, 公交车 0.1, 货车 0.4
vehiType_proportion_lst.add(new VehiComposition(1, 0.3));
vehiType_proportion_lst.add(new VehiComposition(2, 0.2));
vehiType_proportion_lst.add(new VehiComposition(3, 0.1));
vehiType_proportion_lst.add(new VehiComposition(4, 0.4));
String compositionName = "动态创建车型组成";
int vehiCompositionID = netiface.createVehicleComposition(compositionName, vehiType_proportion_lst);
System.out.println("车辆组成 ID: " + vehiCompositionID);
```

 **IRouting shortestRouting(ILink pFromLink, ILink pToLink)**

计算最短路径

参数: 
[ in ] pFromLink: 起始路段  
[ in ] pToLink: 目标路段  
返回: 最短路径对象, 包含经过的路段对象序列

 **IRouting createRouting(ArrayList<ILink> lILink)**

用连续通达的路段序列创建路径

参数: 
[ in ] lILink: 路段对象序列  
返回: 路径对象  

 **ILink createLink3DWithLanePointsAndAttrs(ArrayList<Point3D> lCenterLineV3, ArrayList<TreeMap<String, ArrayList<Point3D>>> lanesWithPoints, ArrayList<String> lLaneType, SWIGTYPE_p_QListT_QJsonObject_t lAttr, String linkName, boolean bAddToScene, UnitOfMeasure unit)**

创建路段

参数: 
[ in ] lCenterLineV3: 路段中心点集(对应TESSNG路段中心点)  
[ in ] lanesWithPoints: 车道点集的集合  
[ in ] lLaneType: 车道类型集  
[ in ] lAttr: 车道附加属性集  
[ in ] linkName: 路段名, 默认为路段ID, 
[ in ] bAddToScene: 是否加入路网, 默认True表示加入  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 路段对象 

注: 如传入米制参数, 请勿遗忘传入connName与bAddToScene参数。


 **IGuidArrow createGuidArrow(ILane pLane, double length, double distToTerminal, GuideArrowType arrowType, UnitOfMeasure unit)**

创建导向箭头, 默认单位: 像素

参数: 
[ in ] pLane: 车道  
[ in ] length: 长度, 默认默认单位: 像素, 可通过制定unit更改为米制  
[ in ] distToTerminal: 到车道终点距离, 默认默认单位: 像素, 可通过制定unit更改为米制  
[ in ] arrowType: 箭头类型, Online.GuideArrowType 枚举值  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 创建车道箭头    

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
ILink w_approach = netiface.findLink(1001);
GuideArrowType arrowType = GuideArrowType.StraightRight;
// 获取路段 w_approach 的第一条车道
ILane firstLane = w_approach.lanes().get(0);
// 创建导向箭头
IGuidArrow straitghtRightArrow = netiface.createGuidArrow(firstLane, 4, 10, arrowType);
```

**boolean createVehicleType(_VehicleType _vt)**

创建车型, 如果创建成功, 会将新创建的车辆类型存放到全局数据里供使用

参数: 
[ in ] _vt: 车辆类型数据

**IDecisionPoint createDecisionPoint(ILink pLink, double distance, String name, UnitOfMeasure unit)**

创建决策点, 默认单位: 像素

参数: 
[ in ] pLink: 决策点所在的路段  
[ in ] distance: 决策点距离路段起点的距离, 默认单位: 像素  
[ in ] name: 决策点的名称  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 决策点对象  
注: 如传入米制参数, 请勿遗忘传入name参数。  

举例: 

```java
IDecisionPoint createDecisionPoint(link3, 30, "Decesion Point", UnitOfMeasure.Metric);
```

 **IRouting createDeciRouting(IDecisionPoint pDeciPoint, ArrayList<ILink> lILink)**

创建决策路径

参数: 
[ in ] pDeciPoint: 决策点
[ in ] lILink: 决策路径所包含的路段集合

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 创建决策点（参数：路段，距离路段起点的位置）
IDecisionPoint decisionPoint = netiface.createDecisionPoint(link3, m2p(30));
// 创建路径(左, 直, 右)
ArrayList<ILink> route1Links = new ArrayList<>(Arrays.asList(link3, link5, link6));
IRouting decisionRouting1 = netiface.createDeciRouting(decisionPoint, route1Links);
ArrayList<ILink> route2Links = new ArrayList<>(Arrays.asList(link3, link5, link8));
IRouting decisionRouting2 = netiface.createDeciRouting(decisionPoint, route2Links);
ArrayList<ILink> route3Links = new ArrayList<>(Arrays.asList(link3, link5, link7));
IRouting decisionRouting3 = netiface.createDeciRouting(decisionPoint, route3Links);
System.out.println("创建决策路径成功");
```

 **IVehicleDrivInfoCollector createVehiCollectorOnLink(ILane pLane, double dist, UnitOfMeasure unit)**

在路段的车道上创建车辆采集器, 默认单位: 像素

参数: 
[ in ] pLane: 车道对象  
[ in ] dist: 路车道起点距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 车辆采集器    

举例: 

```java
// 获取TESSNG接口
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 查找路段9
ILink link = netiface.findLink(9);
// 获取最左侧车道（Java中列表索引从0开始，最后一个元素为 lanes().size()-1）
ILane leftLane = link.lanes().get(link.lanes().size() - 1);
// 初始位置（可能不需要，后续会设置到400米处）
double initialDist = m2p(100);
// 创建车辆采集器
IVehicleDrivInfoCollector collector = netiface.createVehiCollectorOnLink(leftLane, initialDist);
// 将采集器设置到距路段起点400米处
collector.setDistToStart(m2p(400));
```

 **IVehicleDrivInfoCollector createVehiCollectorOnConnector(ILaneConnector pLaneConnector, double dist, UnitOfMeasure unit)**

在连接段的“车道连接”上创建采集器

参数: 
[ in ] pLaneConnector: “车道连接”对象  
[ in ] dist: 距“车道连接”起点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位     

 **IVehicleQueueCounter createVehiQueueCounterOnLink(ILane pLane, double dist, UnitOfMeasure unit)**

在路段的车道上创建车辆排队计数器

参数: 
[ in ] pLane: 车道对象  
[ in ] dist: 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 
返回: 排队计数器对象   

举例: 

```java
// 获取TESSNG接口并查找路段9
ILink link = TESSNG.tessngIFace().netInterface().findLink(9);
// 获取最左侧车道（假设路段存在且至少有1个车道）
ILane leftLane = link.lanes().get(link.lanes().size() - 1);
// 创建排队计数器（假设方法存在且参数正确）
IVehicleQueueCounter counter = TESSNG.tessngIFace().netInterface().createVehiQueueCounterOnLink(leftLane, m2p(100));
// 输出计数器位置（假设counter不为null）
Point point = counter.point();
System.out.printf("计数器所在点坐标为: (%.2f, %.2f)%n", point.getX(), point.getY());
```

 **IVehicleQueueCounter createVehiQueueCounterOnConnector(ILaneConnector pLaneConnector, double dist, UnitOfMeasure unit)**

在连接段的车道连接上创建车辆排队计数器, 默认单位: 像素

参数: 
[ in ] pLaneConnector: “车道连接”对象  
[ in ] dist: 距“车道连接”起点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 排队计数器对象  

 **ArrayList<IVehicleTravelDetector> createVehicleTravelDetector_link2link(ILink pStartLink, ILink pEndLink, double dist1, double dist2, UnitOfMeasure unit)**

创建行程时间检测器, 起点和终点均在路段上; 默认单位: 像素

参数: 
[ in ] pStartLink: 检测器起点所在路段对象  
[ in ] pEndLaneConnector: 检测器终点所在“车道连接”对象  
[ in ] dist1: 检测器起点距所在路段起始点距离, 默认单位: 像素  
[ in ] dist2: 检测器终点距所在“车道连接”起始点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位    

举例: 

```java
// 获取TESSNG接口并查找路段9
ILink link = TESSNG.tessngIFace().netInterface().findLink(9);
// 创建行程检测器
ArrayList<IVehicleTravelDetector> detectors =
    TESSNG.tessngIFace().netInterface().createVehicleTravelDetector_link2link(link, link, m2p(50), m2p(550));
// 设置检测时间范围
for (IVehicleTravelDetector detector : detectors) {
    detector.setFromTime(10);
    detector.setToTime(60);
}
System.out.println("成功创建行程检测器");
```

 **ArrayList<IVehicleTravelDetector> createVehicleTravelDetector_link2conn(ILink pStartLink, ILaneConnector pEndLaneConnector, double dist1, double dist2, UnitOfMeasure unit)**

创建路段到连接段的行程时间检测器, 起点在路段上, 终点均在连接段的“车道连接”上; 默认单位: 像素 

参数: 
[ in ] pStartLink: 检测器起点所在路段对象  
[ in ] pEndLaneConnector: 检测器终点所在“车道连接”对象  
[ in ] dist1: 检测器起点距所在路段起始点距离, 默认单位: 像素  
[ in ] dist2: 检测器终点距所在“车道连接”起始点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 行程时间检测器对象  

 **ArrayList<IVehicleTravelDetector> createVehicleTravelDetector_conn2link(ILaneConnector pStartLaneConnector, ILink pEndLink, double dist1, double dist2, UnitOfMeasure unit)**

创建连接段到路段的行程时间检测器, 起点在连接段的“车道连接”上, 终点在路段上

参数: 
[ in ] pStartLaneConnector: 检测器起点所在“车道连接”对象  
[ in ] pEndLink: 检测器终点所在路段对象  
[ in ] dist1: 检测器起点距所在"车道连接”起始点距离, 默认单位: 像素  
[ in ] dist2: 检测器终点距所在路段起始点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 行程时间检测器对象    

 **ArrayList<IVehicleTravelDetector> createVehicleTravelDetector_conn2conn(ILaneConnector pStartLaneConnector, ILaneConnector pEndLaneConnector, double dist1, double dist2, UnitOfMeasure unit)**

创建连接段到连接段的行程时间检测器, , 起点和终点均在连接段的“车道连接”上; 默认单位: 像素

参数: 
[ in ] pStartLaneConnector: 检测器起点所在“车道连接”对象  
[ in ] pEndLaneConnector: 检测器终点所在“车道连接”对象  
[ in ] dist1: 检测器起点距所在"车道连接”起始点距离, 默认单位: 像素  
[ in ] dist2: 检测器终点距所在“车道连接”起始点距离, 默认单位: 像素  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 行程时间检测器对象    

 **ISignalPhase createSignalPlanSignalPhase(ISignalPlan SignalPlan, String name, ArrayList<ColorInterval> lColor)**

创建相位, 参数 SignalPlan: 信控方案, name: 相位名称, lColor: 相位灯色序列, 新建相位排在已有相位序列的最后

参数: 
[ in ] SignalPlan: 信号方案  
[ in ] name: 相位名称  
[ in ] lColor: 相位灯色序列, 新建相位排在已有相位序列的最后  

返回: 信号相位对象

举例: 

```java
public void createSignalControl(NetInterface netInterface, ILink w_approach, ILink e_approach,
                                ILink n_approach, ILink s_approach, IPedestrianCrossWalkRegion n_crosswalk,
                                IPedestrianCrossWalkRegion s_crosswalk, IPedestrianCrossWalkRegion w_crosswalk, IPedestrianCrossWalkRegion e_crosswalk) {

    // 创建信号机
    ISignalController signalController = netInterface.createSignalController("交叉口1");
    System.out.println("信号机创建成功，ID: " + signalController.id());
    // 创建信控方案（周期150秒，绿信比50%，相位差0，开始时间1800秒）
    ISignalPlan signalPlan = netInterface.createSignalPlan(
        signalController, "早高峰", 150, 50, 0, 1800);

    // 创建东西直行相位
    ArrayList<ColorInterval> w_e_straight_phasecolor = new ArrayList<>();
    w_e_straight_phasecolor.add(new ColorInterval("绿", 50));
    w_e_straight_phasecolor.add(new ColorInterval("黄", 3));
    w_e_straight_phasecolor.add(new ColorInterval("红", 97));
    ISignalPhase w_e_straight_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "东西直行", w_e_straight_phasecolor);
    System.out.println("东西直行相位创建成功，ID: " + w_e_straight_phase.id());
    // 创建东西直行行人相位
    ISignalPhase we_ped_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "东西直行行人", w_e_straight_phasecolor);

    // 创建东西左转相位
    ArrayList<ColorInterval> w_e_left_phasecolor = new ArrayList<>();
    w_e_left_phasecolor.add(new ColorInterval("红", 53));
    w_e_left_phasecolor.add(new ColorInterval("绿", 30));
    w_e_left_phasecolor.add(new ColorInterval("黄", 3));
    w_e_left_phasecolor.add(new ColorInterval("红", 64));
    ISignalPhase w_e_left_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "东西左转", w_e_left_phasecolor);

    // 创建南北直行相位
    ArrayList<ColorInterval> s_n_straight_phasecolor = new ArrayList<>();
    s_n_straight_phasecolor.add(new ColorInterval("红", 86));
    s_n_straight_phasecolor.add(new ColorInterval("绿", 30));
    s_n_straight_phasecolor.add(new ColorInterval("黄", 3));
    s_n_straight_phasecolor.add(new ColorInterval("红", 31));
    ISignalPhase s_n_straight_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "南北直行", s_n_straight_phasecolor);

    // 创建南北直行行人相位
    ISignalPhase ns_ped_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "南北直行行人", s_n_straight_phasecolor);
    System.out.println("s_n_straight_phase:" + s_n_straight_phase.id());
    // 创建南北左转相位
    ArrayList<ColorInterval> s_n_left_phasecolor = new ArrayList<>();
    s_n_left_phasecolor.add(new ColorInterval("红", 119));
    s_n_left_phasecolor.add(new ColorInterval("绿", 29));
    s_n_left_phasecolor.add(new ColorInterval("黄", 3));
    ISignalPhase s_n_left_phase = netInterface.createSignalPlanSignalPhase(
        signalPlan, "南北左转", s_n_left_phasecolor);
    System.out.println("s_n_left_phase:" + s_n_left_phase.id());
    // 创建东西直行机动车信号灯
    ArrayList<ISignalLamp> w_e_straight_lamps = new ArrayList<>();
    for (ILane lane : w_approach.lanes()) {
        if (lane.number() < w_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_straight_lamps.add(signalLamp);
        }
    }
    for (ILane lane : e_approach.lanes()) {
        if (lane.number() < e_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_straight_lamps.add(signalLamp);
        }
    }

    // 创建东西左转机动车信号灯
    ArrayList<ISignalLamp> w_e_left_lamps = new ArrayList<>();
    for (ILane lane : w_approach.lanes()) {
        if (lane.number() == w_approach.laneCount() - 1) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                w_e_left_phase, "东西左转信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_left_lamps.add(signalLamp);
        }
    }
    for (ILane lane : e_approach.lanes()) {
        if (lane.number() == e_approach.laneCount() - 1) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                w_e_left_phase, "东西左转信号灯", lane.id(), -1, lane.length() - 0.5);
            w_e_left_lamps.add(signalLamp);
        }
    }

    // 创建南北直行机动车信号灯
    ArrayList<ISignalLamp> n_s_straight_lamps = new ArrayList<>();
    for (ILane lane : n_approach.lanes()) {
        if (lane.number() < n_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                s_n_straight_phase, "南北直行信号灯", lane.id(), -1, lane.length() - 0.5);
            n_s_straight_lamps.add(signalLamp);
        }
    }
    for (ILane lane : s_approach.lanes()) {
        if (lane.number() < s_approach.laneCount() - 1 && lane.number() > 0) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                s_n_straight_phase, "南北直行信号灯", lane.id(), -1, lane.length() - 0.5);
            n_s_straight_lamps.add(signalLamp);
        }
    }

    // 创建南北左转机动车信号灯
    ArrayList<ISignalLamp> n_s_left_lamps = new ArrayList<>();
    for (ILane lane : n_approach.lanes()) {
        if (lane.number() == n_approach.laneCount() - 1) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                s_n_left_phase, "南北左转信号灯", lane.id(), -1, lane.length() - 0.5);
            n_s_left_lamps.add(signalLamp);
        }
    }
    for (ILane lane : s_approach.lanes()) {
        if (lane.number() == s_approach.laneCount() - 1) {
            ISignalLamp signalLamp = netInterface.createSignalLamp(
                s_n_left_phase, "南北左转信号灯", lane.id(), -1, lane.length() - 0.5);
            n_s_left_lamps.add(signalLamp);
        }
    }
    System.out.println("w_e_straight_phase:" + w_e_straight_phase.id());
    // 创建行人信号灯并关联相位
    // 南斑马线信号灯
    ICrosswalkSignalLamp signalLamp1_positive = netInterface.createCrossWalkSignalLamp(
        signalController, "南斑马线信号灯", s_crosswalk.getId(), new Point(m2p(0), m2p(0)), true);
    ICrosswalkSignalLamp signalLamp1_negetive = netInterface.createCrossWalkSignalLamp(
        signalController, "南斑马线信号灯", s_crosswalk.getId(), new Point(m2p(0), m2p(0)), false);
    signalLamp1_positive.setSignalPhase(we_ped_phase);
    signalLamp1_negetive.setSignalPhase(we_ped_phase);
    System.out.println("we_ped_phase:" + we_ped_phase.id());
    // 北斑马线信号灯
    ICrosswalkSignalLamp signalLamp2_positive = netInterface.createCrossWalkSignalLamp(
        signalController, "北斑马线信号灯", n_crosswalk.getId(), new Point(m2p(0), m2p(0)), true);
    ICrosswalkSignalLamp signalLamp2_negetive = netInterface.createCrossWalkSignalLamp(
        signalController, "北斑马线信号灯", n_crosswalk.getId(), new Point(m2p(0), m2p(0)), false);
    signalLamp2_positive.setSignalPhase(we_ped_phase);
    signalLamp2_negetive.setSignalPhase(we_ped_phase);

    // 东斑马线信号灯
    ICrosswalkSignalLamp signalLamp3_positive = netInterface.createCrossWalkSignalLamp(
        signalController, "东斑马线信号灯", e_crosswalk.getId(), new Point(m2p(0), m2p(0)), true);
    ICrosswalkSignalLamp signalLamp3_negetive = netInterface.createCrossWalkSignalLamp(
        signalController, "东斑马线信号灯", e_crosswalk.getId(), new Point(m2p(0), m2p(0)), false);
    signalLamp3_positive.setSignalPhase(ns_ped_phase);
    signalLamp3_negetive.setSignalPhase(ns_ped_phase);

    // 西斑马线信号灯
    ICrosswalkSignalLamp signalLamp4_positive = netInterface.createCrossWalkSignalLamp(
        signalController, "西斑马线信号灯", w_crosswalk.getId(), new Point(m2p(0), m2p(0)), true);
    ICrosswalkSignalLamp signalLamp4_negetive = netInterface.createCrossWalkSignalLamp(
        signalController, "西斑马线信号灯", w_crosswalk.getId(), new Point(m2p(0), m2p(0)), false);
    signalLamp4_positive.setSignalPhase(ns_ped_phase);
    signalLamp4_negetive.setSignalPhase(ns_ped_phase);}
```

 **ISignalLamp createSignalLamp(ISignalPhase pPhase, String name, int laneId, int toLaneId, double distance)**

创建信号灯

参数: 
[ in ] pPhase: 相位对象  
[ in ] name: 信号灯名称  
[ in ] laneId: 信号灯所在车道ID, 或所在“车道连接”上游车道ID  
[ in ] toLaneId: 信号灯所在“车道连接”下游车道ID, 如果放到连接器上需要该参数, 如果在非连接器上, 此值赋值为-1  
[ in ] distance: 信号灯距车道或“车道连接”起点距离, 默认单位: 像素  

返回: 信号灯对象

举例: 

```java
for (int index = 0; index < lLaneObjects.size(); index++) {
    ILaneObject laneObj = lLaneObjects.get(index);
    // 创建信号灯
    ISignalLamp signalLamp = netInterface.createSignalLamp(
        signalPhase,                     // 关联信号相位
        "信号灯" + (index + 1),         // 名称
        laneObj.fromLaneObject().id(),        // 来源车道ID
        laneObj.toLaneObject().id(),          // 目标车道ID
        m2p(2.0)                      // 位置
    );
}
```

 **ISignalLamp createSignalLamp(ISignalPhase pPhase, String name, int laneId, int toLaneId, double distance)**

创建信号灯

参数: 
[ in ] ISignalController: 信号控制机
[ in ] pPhase: 相位对象  
[ in ] name: 信号灯名称  
[ in ] laneId: 信号灯所在车道ID, 或所在“车道连接”上游车道ID  
[ in ] toLaneId: 信号灯所在“车道连接”下游车道ID, 如果放到连接器上需要该参数, 如果在非连接器上, 此值赋值为-1  
[ in ] distance: 信号灯距车道或“车道连接”起点距离, 默认单位: 像素  

返回: 信号灯对象

举例: 

```java
for (int index = 0; index < lLaneObjects.size(); index++) {
    ILaneObject laneObj = lLaneObjects.get(index);
    // 创建信号灯
    ISignalLamp signalLamp = netInterface.createSignalLamp(
        signalController,                     // 信号控制器
        "信号灯" + (index + 1),         // 名称
        laneObj.fromLaneObject().id(),        // 来源车道ID
        laneObj.toLaneObject().id(),          // 目标车道ID
        m2p(2.0)                      // 位置
    );
}
```

 **ISignalController createSignalController(String name)**

创建信号机

参数: 
[ in ] name: 信号灯名称  

 **ISignalPlan createSignalPlan(ISignalController pITrafficLight, String name, int cycle, int offset, int startTime, int endTime)**

创建信控方案

参数: 
[ in ] signalController: 信号控制器
[ in ] name: 信控方案名称
[ in ] cycleTime: 信控 方案周期, 秒
[ in ] offset: 信控方案相位差  
[ in ] startTime: 信控方案开始时间, 秒  
[ in ] endTime: 信控方案结束时间, 秒   

 **addSignalPhaseToLamp(int SignalPhaseId, ISignalLamp signalLamp)**

将信号灯与相位绑定, 不允许跨越信号机绑定

参数: 
[ in ] signalPhaseId: 相位id
[ in ] signalLamp: 信号灯对象

 **void addCrossWalkSignalPhaseToLamp(int SignalPhaseId, ICrosswalkSignalLamp signalLamp)**

将人行横道与相位绑定, 不允许跨越信号机绑定

参数: 
[ in ] signalPhaseId: 相位id
[ in ] signalLamp: 信号灯对象

 **void transferSignalPhase(ISignalPhase pFromISignalPhase, ISignalPhase pToISignalPhase, ISignalLamp signalLamp)**

更换信号灯绑定的信控相位, 不允许跨越信号机

参数: 
[ in ] pFromISignalPhase: 原相位
[ in ] pToISignalPhase: 新相位
[ in ] signalLamp: 信号灯对象

 **IBusLine createBusLine(ArrayList<ILink> lLink)**

创建公交线路, lLink列表中相邻两路段可以是路网上相邻两路段, 也可以不相邻, 如果不相邻, TESSNG会在它们之间创建一条最短路径。如果lLink列表中相邻路段在路网上不相邻并且二者之间不存在最短路径, 则相邻的第二条路段及后续路段无效。

参数: 
[ in ] lLink, 公交线路经过的路段对象集

返回: 公交线路对象

举例: 

```java
// 获取TESSNG核心接口
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
// 创建两个路段
ArrayList<Point> points1 = new ArrayList<>();
points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
ILink link10 = netInterface.createLink(points1, 3, "路段10");
ArrayList<Point> points2 = new ArrayList<>();
points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
ILink link11 = netInterface.createLink(points2, 3, "路段11");
if (link10 != null && link11 != null) {
    ArrayList<ILink> linksForBusLine = new ArrayList<>();
    linksForBusLine.add(link10);
    linksForBusLine.add(link11);
    IBusLine busLine = netInterface.createBusLine(linksForBusLine);
    if (busLine != null) {
        busLine.setDesirSpeed(TESSNG.m2p(60));
        System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
    }
}
```

  **IBusStation createBusStation(ILane pLane, double length, double dist, String name, UnitOfMeasure unit)**

创建公交站点, 默认单位: 像素

参数: 
[ in ] pLane: 车道  
[ in ] length: 站点长度(默认单位: 像素)  
[ in ] dist: 站点起始点距车道起点距离(默认单位: 像素)  
[ in ] name: 站点名称  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 公交站点对象  

举例: 

```java
// 获取 TESS NG 接口
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
// 创建两个路段
ArrayList<Point> points1 = new ArrayList<>();
points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
ILink link10 = netInterface.createLink(points1, 3, "路段10");
ArrayList<Point> points2 = new ArrayList<>();
points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
ILink link11 = netInterface.createLink(points2, 3, "路段11");
if (link10 == null || link11 == null) {
    System.out.println("路段 link10 或 link11 创建失败");
    return;
}
// 创建公交线路
ArrayList<ILink> linksForBusLine = new ArrayList<>();
linksForBusLine.add(link10);
linksForBusLine.add(link11);
IBusLine busLine = netInterface.createBusLine(linksForBusLine);
if (busLine != null) {
    // 设置公交线路期望速度为 60 km/h，转换为 TESS NG 内部单位
    busLine.setDesirSpeed(TESSNG.m2p(60));
    System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
    // 获取路段的车道
    List<ILane> lanes10 = link10.lanes();
    List<ILane> lanes11 = link11.lanes();
    if (lanes10 != null && !lanes10.isEmpty() &&
        lanes11 != null && !lanes11.isEmpty()) {
        ILane lane10 = lanes10.get(0);  // 第一个车道
        ILane lane11 = lanes11.get(0);  // 第一个车道
        // 创建公交站点
        IBusStation busStation1 = netInterface.createBusStation(
            lane10,
            TESSNG.m2p(30),  // 站点位置在车道上 30 米处
            TESSNG.m2p(50),  // 停靠点距离车道中心线 50 米
            "公交站1"
        );
        IBusStation busStation2 = netInterface.createBusStation(
            lane11,
            TESSNG.m2p(15),  // 站点位置在车道上 15 米处
            TESSNG.m2p(50),  // 停靠点距离车道中心线 50 米
            "公交站2"
        );
        if (busStation1 != null && busStation2 != null) {
            System.out.println("公交站点创建成功");
        } else {
            System.out.println("公交站点创建失败");
        }
    } else {
        System.out.println("路段车道为空");
    }
} else {
    System.out.println("公交线路创建失败");
}
```

 **boolean addBusStationToLine(IBusLine pBusLine, IBusStation pStation)**

将公交站点关联到公交线路上

参数: 
[ in ] pBusLine: 公交线路
[ in ] pStation: 公交站点

举例: 

```java
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
ArrayList<Point> points1 = new ArrayList<>();
points1.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
points1.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
ILink link10 = netInterface.createLink(points1, 3, "路段10");
ArrayList<Point> points2 = new ArrayList<>();
points2.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
points2.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
ILink link11 = netInterface.createLink(points2, 3, "路段11");
if (link10 == null || link11 == null) {
    System.out.println("路段 link10 或 link11 创建失败");
    return;
}
ArrayList<ILink> linksForBusLine = new ArrayList<>();
linksForBusLine.add(link10);
linksForBusLine.add(link11);
IBusLine busLine = netInterface.createBusLine(linksForBusLine);
if (busLine != null) {
    busLine.setDesirSpeed(TESSNG.m2p(60));
    System.out.println("公交线路创建成功，期望速度设置为: " + busLine.desirSpeed());
    List<ILane> lanes10 = link10.lanes();
    List<ILane> lanes11 = link11.lanes();
    if (lanes10 != null && !lanes10.isEmpty() &&
        lanes11 != null && !lanes11.isEmpty()) {
        ILane lane10 = lanes10.get(0);  
        ILane lane11 = lanes11.get(0); 
        IBusStation busStation1 = netInterface.createBusStation(
            lane10,
            TESSNG.m2p(30),  
            TESSNG.m2p(50),  
            "公交站1"
        );
        IBusStation busStation2 = netInterface.createBusStation(
            lane11,
            TESSNG.m2p(15),  
            TESSNG.m2p(50),  
            "公交站2"
        );
        if (busStation1 != null && busStation2 != null) {
            System.out.println("公交站点创建成功");
            if (netInterface.addBusStationToLine(busLine, busStation1)) {
                busStation1.setType(2);  // 设置为上客站
                System.out.println("公交站1已关联到公交线路");
            }
            if (netInterface.addBusStationToLine(busLine, busStation2)) {
                System.out.println("公交站2已关联到公交线路");
            }

        } else {
            System.out.println("公交站点创建失败");
        }
    } else {
        System.out.println("路段车道为空");
    }
}
```

  **IRoadWorkZone createRoadWorkZone(DynaRoadWorkZoneParam param, UnitOfMeasure unit)**

创建施工区, 默认单位: 像素

参数: 
[ in ] param: 动态施工区信息  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
举例: 

```java
public IRoadWorkZone createRoadWorkZone(NetInterface netInterface, ILink upstreamLink) {
    // 创建施工区参数对象
    DynaRoadWorkZoneParam param = new DynaRoadWorkZoneParam();
    // 设置施工区参数
    param.setName("施工区");
    param.setRoadId(upstreamLink.id());
    param.setLocation(TESSNG.m2p(0));           // 位置
    param.setLength(TESSNG.m2p(100));             // 长度
    param.setUpCautionLength(TESSNG.m2p(70));     // 提前警示长度
    param.setUpTransitionLength(TESSNG.m2p(50));  // 过渡区长度
    param.setUpBufferLength(TESSNG.m2p(50));      // 缓冲区长度
    param.setDownTransitionLength(TESSNG.m2p(40));// 下游过渡区长度
    param.setDownTerminationLength(TESSNG.m2p(40));// 终止区长度
    // 设置车道编号
    ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
    fromLaneNumbers.add(1);
    param.setMlFromLaneNumber(fromLaneNumbers);  // 设置施工影响的车道编号
    param.setStartTime(0);                       // 开始时间（秒）
    param.setDuration(3600);                    // 持续时间（秒）
    param.setLimitSpeed(TESSNG.m2p(42));         // 限速，转换为 TESS NG 内部单位
    // 创建施工区
    IRoadWorkZone workZone = netInterface.createRoadWorkZone(param, UnitOfMeasure.Metric); // Metric 单位制
    if (workZone != null) {
        System.out.println("施工区创建成功: " + workZone.name());
    } else {
        System.out.println("施工区创建失败");
    }
    return workZone;
}
```

  **IAccidentZone createAccidentZone(DynaAccidentZoneParam param)**

创建事故区

参数: 
[ in ] param: 动态事故区信息, 用于封装创建事故区所需的参数

举例: 

```java
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
// 创建路段
ArrayList<Point> points = new ArrayList<>();
points.add(new Point(TESSNG.m2p(-300), TESSNG.m2p(25)));
points.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
ILink link = netInterface.createLink(points, 4, "主干道");
// 创建事故区参数对象
DynaAccidentZoneParam accidentZoneParam = new DynaAccidentZoneParam();
// 设置事故区参数
accidentZoneParam.setRoadId(link.id());               // 使用创建的 link 的 ID
accidentZoneParam.setName("最左侧车道发生事故");
accidentZoneParam.setLocation(TESSNG.m2p(700));       // 位置
accidentZoneParam.setLength(TESSNG.m2p(50));         // 事故区长度
accidentZoneParam.setStartTime(0);                   // 开始时间（秒）
accidentZoneParam.setDuration(500);                  // 持续时间（秒）
accidentZoneParam.setNeedStayed(true);               // 是否需要停留
accidentZoneParam.setLimitSpeed(TESSNG.m2p(55));      // 限速
accidentZoneParam.setControlLength(TESSNG.m2p(100));   // 控制区长度
ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
fromLaneNumbers.add(2);
accidentZoneParam.setMlFromLaneNumber(fromLaneNumbers);
// 创建事故区
IAccidentZone accidentZone = netInterface.createAccidentZone(accidentZoneParam);
// 创建事故区时间段参数
DynaAccidentZoneIntervalParam intervalParam = new DynaAccidentZoneIntervalParam();
intervalParam.setAccidentZoneId(accidentZone.id());
intervalParam.setStartTime(501);
intervalParam.setEndTime(1000);
intervalParam.setLocation(TESSNG.m2p(200));
intervalParam.setLength(TESSNG.m2p(50));
intervalParam.setLimitedSpeed(TESSNG.m2p(10));
intervalParam.setControlLength(TESSNG.m2p(100));
ArrayList<Integer> intervalLaneNumbers = new ArrayList<>();
intervalLaneNumbers.add(0);
intervalLaneNumbers.add(1);
intervalLaneNumbers.add(3);
intervalParam.setMlFromLaneNumber(intervalLaneNumbers);
IAccidentZoneInterval added = accidentZone.addAccidentZoneInterval(intervalParam);
if (added != null) {
    System.out.println("事故区时间段信息添加成功");
} else {
    System.out.println("事故区时间段信息添加失败");
}
```

 **ILimitedZone createLimitedZone(DynaLimitedZoneParam param, UnitOfMeasure unit)**

创建限行区, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] param: 动态限行区信息, 用于封装创建限行区所需的参数  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 


```java
// 获取 TESS NG 接口
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
// 创建限行区参数对象
DynaLimitedZoneParam param = new DynaLimitedZoneParam();
// 设置限行区参数
param.setName("限行区测试");                // 名称
param.setRoadId(1);                         // 道路ID
param.setLocation(m2p(50));                 // 限行区起始位置
param.setLength(m2p(100));                  // 限行区长度
param.setLimitSpeed(m2p(40));               // 限速
param.setDuration(3600);                    // 持续时间
// 设置限行车道编号
ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
fromLaneNumbers.add(0);  // 本例限行右侧第一车道
param.setMlFromLaneNumber(fromLaneNumbers);
// 创建限行区
ILimitedZone limitedZone = netInterface.createLimitedZone(param);
if (limitedZone != null) {
    System.out.println("限行区创建成功: " + limitedZone.name());
} else {
    System.out.println("限行区创建失败");
}
```

 **IReconstruction createReconstruction(DynaReconstructionParam param, UnitOfMeasure unit)**

创建改扩建, 默认单位: 像素
[ in ] param: 动态改扩建信息, 用于封装创建改扩建所需的参数  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例:

```java
// 获取 TESS NG 接口
TessInterface tessInterface = TESSNG.tessngIFace();
NetInterface netInterface = tessInterface.netInterface();
// 创建上游路段（upstreamLink）
ArrayList<Point> upstreamPoints = new ArrayList<>();
upstreamPoints.add(new Point(TESSNG.m2p(-100), TESSNG.m2p(25)));
upstreamPoints.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
ILink upstreamLink = netInterface.createLink(upstreamPoints, 3, "上游路段");
// 创建下游路段（downstreamLink）
ArrayList<Point> downstreamPoints = new ArrayList<>();
downstreamPoints.add(new Point(TESSNG.m2p(100), TESSNG.m2p(25)));
downstreamPoints.add(new Point(TESSNG.m2p(300), TESSNG.m2p(25)));
ILink downstreamLink = netInterface.createLink(downstreamPoints, 3, "下游路段");
// 创建施工区参数对象
DynaRoadWorkZoneParam param = new DynaRoadWorkZoneParam();
// 设置施工区参数
param.setName("施工区");
param.setRoadId(upstreamLink.id());
param.setLocation(TESSNG.m2p(200));           // 位置
param.setLength(TESSNG.m2p(100));             // 长度
param.setUpCautionLength(TESSNG.m2p(70));     // 提前警示长度
param.setUpTransitionLength(TESSNG.m2p(50));  // 过渡区长度
param.setUpBufferLength(TESSNG.m2p(50));      // 缓冲区长度
param.setDownTransitionLength(TESSNG.m2p(40));// 下游过渡区长度
param.setDownTerminationLength(TESSNG.m2p(40));// 终止区长度
// 设置车道编号（注意：Java 中使用 List<Integer>）
ArrayList<Integer> fromLaneNumbers = new ArrayList<>();
fromLaneNumbers.add(3);
param.setMlFromLaneNumber(fromLaneNumbers);  // 设置施工影响的车道编号
param.setStartTime(0);                       // 开始时间（秒）
param.setDuration(3600);                    // 持续时间（秒）
param.setLimitSpeed(TESSNG.m2p(42));         // 限速，转换为 TESS NG 内部单位
// 创建施工区
IRoadWorkZone workZone = netInterface.createRoadWorkZone(param, UnitOfMeasure.Metric); // Metric 单位制
if (workZone != null) {
    System.out.println("施工区创建成功: " + workZone.name());
} else {
    System.out.println("施工区创建失败");
}
```

 **double reCalcPassagewayLength(DynaReconstructionParam param, UnitOfMeasure unit)**

重新计算保通开口长度, 可根据保通详细参数计算出保通开口长度, 默认单位: 像素

参数: 
[ in ] param:用于描述重建区的参数类，定义了重建区所需的各类配置信息  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

 **IReduceSpeedArea createReduceSpeedArea(DynaReduceSpeedAreaParam param)**

创建限速区 

参数: 
[ in ] param: 用于描述限速区的参数类，定义了限速区所需的各类配置信息, 其属性有: 

name: 限速区名称
location: 距起点距离, 默认单位: 像素
areaLength: 限速区长度, 默认单位: 像素
roadId: 路段或连接段ID
laneNumber: 车道序号, 从0开始
toLaneNumber: 目标车道序号, 如果大于等于0, roadID是连接段ID, 否则是路段ID
fromTime: 起始时间
toTime: 结束时间
lSpeedVehiType: 限速车型列表

```python
print("创建限速区")
type1 = Online.DynaReduceSpeedVehiTypeParam()
type1.vehicleTypeCode = 1
type1.avgSpeed = 10
type1.speedSD = 5
type2 = Online.DynaReduceSpeedVehiTypeParam()
type2.vehicleTypeCode = 2
type2.avgSpeed = 20
type2.speedSD = 0

reduceSpeedIntervalParam = Online.DynaReduceSpeedIntervalParam()
reduceSpeedIntervalParam.startTime = 0
reduceSpeedIntervalParam.endTime = 50
reduceSpeedIntervalParam.mlReduceSpeedVehicleTypeParam = [type1, type2]
#
#
param1 = Online.DynaReduceSpeedAreaParam()
param1.name="限速区"
param1.location = 2500
param1.areaLength =100
param1.roadId=upstream.id()
param1.laneNumber = 0
param1.toLaneNumber = -1
param1.mlReduceSpeedIntervalParam = [reduceSpeedIntervalParam]
reduceSpeedArea = netiface.createReduceSpeedArea(param)
print(reduceSpeedArea)
```

 **def createTollLane(param: Online.TollStation.DynaTollLaneg) ->Tessng.ITollLane: ...**

创建收费车道  

参数: 
[ in ]  param: 动态收费车道信息, 数据类型在文件 Plugin/_datastruct.h中定义, python初始化  Online.TollStation.DynaTollLane的示例代码如下: 

```python
def create_toll_lane(netiface, link_id: int, location: float, length: float, lane_number: int, start_time: float, end_time: float, toll_point_list: list, toll_point_length: float = 8): 
    """创建收费车道"""
    toll_lane_param = Online.TollStation.DynaTollLane()
    # toll_lane_param.name = "123"
    # 路段ID
    toll_lane_param.roadId = link_id
    # 距离路段起点的距离, 单位: m
    toll_lane_param.location = location
    # 收费车道长度, 单位: m
    toll_lane_param.length = length
    # 车道序号, 从右向左, 从0开始
    toll_lane_param.laneNumber = lane_number
    # 开始时间, 单位: 秒
    toll_lane_param.startTime = start_time
    # 结束时间, 单位: 秒
    toll_lane_param.endTime = end_time
    # 收费区域列表
    toll_lane_param.tollPoint = toll_point_list
    # 收费区域长度
    toll_lane_param.tollPointLen = toll_point_length
    # 创建收费车道
    toll_lane = netiface.createTollLane(toll_lane_param)
    return toll_lane
```

 **def createParkingRegion(param: Online.ParkingLot.DynaParkingRegion) ->Tessng.IParkingRegion: ...**

创建停车区

参数: 
[ in ]  param: 动态停车区信息, 数据类型在文件 Plugin/_datastruct.h中定义, python初始化

Online.ParkingLot.DynaParkingRegion的示例代码如下: 

```python

def create_parking(netiface, downstream): 

    print(f"创建停车时间分布")
    new_pt = Online.ParkingLot.DynaParkingParkTime()
    new_pt.time = 3
    new_pt.prop = 100
    new_pt1 = Online.ParkingLot.DynaParkingParkTime()
    new_pt1.time = 5
    new_pt1.prop = 100
    new_ptd = Online.ParkingLot.DynaParkingTimeDis()
    new_ptd.name = "新增停车时间分布"
    new_ptd.parkingTimeList = [new_pt, new_pt1]
    netiface.createParkingTimeDis(new_ptd)

    print(f"创建停车区域")
    dynaParkingRegion = Online.ParkingLot.DynaParkingRegion()
    dynaParkingRegion.name = "test Parking region"
    dynaParkingRegion.location = 7800
    dynaParkingRegion.length = 100
    dynaParkingRegion.roadId = downstream.id()
    dynaParkingRegion.laneNumber = 0
    dynaParkingRegion.findParkingStallStrategy = 1
    # 0 - 车道内; 1 - 车道右侧; 2 - 车道左侧
    dynaParkingRegion.parkingStallPos = 1
    dynaParkingRegion.arrangeType = 1
    # 车位吸引力
    dynaParkingRegion.firstParkingStallAttract = 1
    dynaParkingRegion.middleParkingStallAttract = 1
    dynaParkingRegion.lastParkingStallAttract = 1
    # 停车运动参数
    dynaParkingRegion.menaValue = 0 #  均值
    dynaParkingRegion.variance = 1.0 # 方差
    dynaParkingRegion.parkingSpeed = 5.0 # 泊车速度
    dynaParkingRegion.joinGap = 5.0 # 汇入间隙
    # 0 - 前进->前进; 1 - 前进->后退; 2 - 后退->前进
    dynaParkingRegion.parkingType = 1
    # 运营参数
    dynaParkingRegion.attract = 0 #吸引力
    dynaParkingRegion.startTime = 0 # 开始时间
    dynaParkingRegion.endTime = 999999 #结束时间
    dynaParkingRegion.stallLength = 6 # 车位长度单位米
    dynaParkingRegion.stallWidth = 3 # 车位宽度单位米
    # 根据区域长度和车位长度, 创建停车位
    stallLen = 6
    parkingStalls= []
    for i in range(int(dynaParkingRegion.length/stallLen)-1): 
        parkingStall = Online.ParkingLot.DynaParkingStall()
        parkingStall.location = dynaParkingRegion.location + stallLen * i
        parkingStall.linkID = dynaParkingRegion.roadId
        parkingStall.laneNumber = dynaParkingRegion.laneNumber
        # 0 - 车道内; 1 - 车道右侧; 2 - 车道左侧
        parkingStall.parkingStallPos = 1
        # 0 - 垂直式; 1 - 倾斜式 - 30°; 2 - 倾斜式 - 45°; 3 - 倾斜式 - 60°; 4 - 平行式
        parkingStall.arrangeType = 0
        # 停车位类型
        parkingStall.parkingStallType = 1 # 小客车 / 大客车 / 其他
        parkingStalls.append(parkingStall)
    dynaParkingRegion.parkingStalls = parkingStalls

    parkingRegion = netiface.createParkingRegion(dynaParkingRegion)

    distance = 7500
    new_pdp = netiface.createParkingDecisionPoint(downstream, distance, "上游路段停车场")
    new_pr = netiface.createParkingRouting(new_pdp, parkingRegion)


    # 更新停车时间分布
    origin_ptd = netiface.parkingTimeDis()[-1]
    origin_ptl = origin_ptd.parkingTimeList
    print(origin_ptd.id, [(i.parkingTimeDisId, i.time, i.prop) for i in origin_ptl])
    update_ptd = Online.ParkingLot.DynaParkingTimeDis()
    update_ptd.id = origin_ptd.id
    pt = Online.ParkingLot.DynaParkingParkTime()
    pt.time = 60
    pt.prop = 0.5
    new_ptl = origin_ptl + [pt]
    update_ptd.parkingTimeList = new_ptl
    netiface.updateParkingTimeDis(update_ptd)

    # 更新停车决策分配
    pdp = netiface.parkingDecisionPoints()[-1]
    pdi = pdp.parkDisInfoList()[-1]
    print(f'len(pdi.disVehiclsInfoList): {len(pdi.disVehiclsInfoList)}')
    dvi = pdi.disVehiclsInfoList[-1]
    dvi.startTime = 0
    dvi.endTime = 1800
    pdi.disVehiclsInfoList = [dvi]
    print(f"len(pdi.disVehiclsInfoList): {len(pdi.disVehiclsInfoList)}")
    for i in pdi.disVehiclsInfoList: 
        print(f"{i.startTime}-{i.endTime}")
    print(f"{pdi.disVehiclsInfoList[0].startTime}-{pdi.disVehiclsInfoList[0].endTime}")
    pdp.updateParkDisInfo([pdi])

    # 移除
    # last_pr = netiface.parkingDecisionPoints()[-1].routings()[-1]
    # netiface.removeParkingRouting(last_pr)
    # pr = netiface.findParkingRegion(2)
    # netiface.removeParkingRegion(pr)    # 停车区相关的路径会被同步移除
    # last_ptd = netiface.parkingTimeDis()[-1]
    # netiface.removeParkingTimeDis(last_ptd.id)

    # 停车测试
    parkingRegions = netiface.parkingRegions()
    print(netiface.vehicleTypes())
    for pr in parkingRegions: 
        print(f"id: {pr.id()}")
        print(f"name: {pr.name()}")
        print(f"parkingStalls: {pr.parkingStalls()}")
        pr.setName(f'停车区{pr.name()+"_new"}')
        parkingStalls = pr.parkingStalls()
        for ps in parkingStalls: 
            print(dir(ps))
            print(f"id: {ps.id()}")
            print(f"distance: {ps.distance()}")
            print(f"parkingRegion: {ps.parkingRegionId()}")
            print(f"type: {ps.stallType()}")

    parkingDecisionPoints = netiface.parkingDecisionPoints()
    # 停车决策点
    for pdp in parkingDecisionPoints: 
        print(dir(pdp))
        print(f"id: {pdp.id()}")
        print(f"distance: {pdp.distance()}")
        print(f"link: {pdp.link().id()}")
        print(f"name: {pdp.name()}")
        print(f"parkDisInfoList: {pdp.parkDisInfoList()}")
        # 静态决策路径
        for pdi in pdp.parkDisInfoList(): 
            print(type(pdi), dir(pdi))
            print(f"disVehiclsInfoList: {pdi.disVehiclsInfoList}")
            # 静态路径时间段
            for dvi in pdi.disVehiclsInfoList: 
                # print(type(dvi), dir(dvi))
                print(f"startTime: {dvi.startTime}")
                print(f"endTime: {dvi.endTime}")
                print(f"vehicleDisDetailList: {dvi.vehicleDisDetailList}")
                # 停车分配详细信息
                for vdd in dvi.vehicleDisDetailList: 
                    print(type(vdd), dir(vdd))
                    print(f"parkingRegionID: {vdd.parkingRegionID}")
                    print(f"parkingRoutingID: {vdd.parkingRoutingID}")
                    print(f"parkingSelection: {vdd.parkingSelection}")
                    print(f"parkingTimeDisId: {vdd.parkingTimeDisId}")
                    print(f"prop: {vdd.prop}")
                    print(f"vehicleType: {vdd.vehicleType}")
            print(f"pIRouting: {pdi.pIRouting}")
        print(f"polygon: {pdp.polygon()}")
        print(f"routings: {pdp.routings()}")
    for routing in pdp.routings(): 
        # print(type(routing), dir(routing))
        print(f"routingId: {routing.id()}")
        print(f"routingLinks: {routing.getLinks()}")
        link = netiface.findLink(21)
        print(f"nextRoad: {routing.nextRoad(link)}")
        print(f"deciPoint: {routing.parkingDeciPointId()}")
        print(f"contain: {routing.contain(link)}")
        print(f"length: {routing.calcuLength()}")

    parkingTimeDisArray = netiface.parkingTimeDis()
```


 **ITollDecisionPoint createTollDecisionPoint(ILink pLink, double distance, String name)**

创建收费决策点  

参数: 
[ in ]  pLink: 收费决策点所在的路段  
[ in ]  distance: 收费决策点距离路段起点的距离, 默认单位: 像素  
[ in ]  pLink: 收费决策点的名称, 可选参数

```java
public ITollDecisionPoint createTollDecisionPoint(
    NetInterface netInterface,
    int linkId,
    double location,
    String name) {

    // 查找路段
    ILink link = netInterface.findLink(linkId);
    if (link == null) {
        System.out.println("路段未找到，ID: " + linkId);
        return null;
    }

    // 创建收费路径决策点
    ITollDecisionPoint tollDecisionPoint = netInterface.createTollDecisionPoint(link, m2p(location), name);

    if (tollDecisionPoint != null) {
        System.out.println("收费路径决策点创建成功: " + tollDecisionPoint.name());
    } else {
        System.out.println("收费路径决策点创建失败");
    }

    return tollDecisionPoint;
}
```

 **ITollRouting createTollRouting(ITollDecisionPoint pDeciPoint, ITollLane pITollLane)**

创建收费路径  

参数: 
[ in ] pDeciPoint: 收费决策点  
[ in ] pITollLane: 收费车道  

```java
public ITollRouting createTollRouting(
    NetInterface netInterface,
    ITollDecisionPoint tollDecisionPoint,
    ITollLane tollLane) {

    // 创建收费路径
    ITollRouting tollRouting = netInterface.createTollRouting(tollDecisionPoint, tollLane);
    if (tollRouting != null) {
        System.out.println("收费路径创建成功");
    } else {
        System.out.println("收费路径创建失败");
    }
    return tollRouting;
}
```

 **IParkingDecisionPoint createParkingDecisionPoint(ILink pLink, double distance, String name)**

创建停车决策点  

参数: 
[ in ] pLink: 停车决策点所在的路tollDisInfoList段  
[ in ] distance: 停车决策点距离路段起点的距离, 默认单位: 米  
[ in ] name: 停车决策点的名称, 可选参数  

 **IParkingRouting createParkingRouting(IParkingDecisionPoint pDeciPoint, IParkingRegion pIParkingRegion)**

创建停车路径  

参数: 
[ in ] pDeciPoint: 停车决策点  
[ in ] pIParkingRegion: 停车区  

 **DynaTollParkingTimeDis createTollParkingTimeDis(DynaTollParkingTimeDis param)**

创建收费站停车时距分布

参数: 
[ in ]  param: 停车时距分布参数

 **DynaParkingTimeDis createParkingTimeDis(DynaParkingTimeDis param)**

更新收费站停车时距分布  

参数: 
[ in ]  param: 停车时距分布参数

```java
// 创建停车时间分布对象
DynaParkingTimeDis new_ptd = new DynaParkingTimeDis();
new_ptd.setName("新增停车时间分布");
// 创建第一个停车时间对象
DynaParkingParkTime new_pt = new DynaParkingParkTime();
new_pt.setTime(3);   // 设置时间为 3 秒
new_pt.setProp(100); // 设置比例为 100%
// 创建第二个停车时间对象
DynaParkingParkTime new_pt1 = new DynaParkingParkTime();
new_pt1.setTime(5);   // 设置时间为 5 秒
new_pt1.setProp(100); // 设置比例为 100%
// 设置停车时间列表
ArrayList<DynaParkingParkTime> parkingTimeList = new ArrayList<>();
parkingTimeList.add(new_pt);
parkingTimeList.add(new_pt1);
// 将列表设置到停车时间分布对象中
new_ptd.setParkingTimeList(parkingTimeList);
// 调用接口创建停车时间分布
netiface.createParkingTimeDis(new_ptd);
```


 **def createJunction (startPoint: QPointF, endPoint: QPointF, name: str) ->Tessng.IJunction: ...**

创建节点  

参数: 
[ in ] startPoint: 左上角起始点坐标  
[ in ] endPoint: 右下角起始点坐标  
[ in ] name: 节点名字  

```python
def createJunctionNode(netiface): 
    # # step1: 创建节点
    x1 = -500
    y1 = 500
    x2 = 500
    y2 = -500
    junctionName = 'newJunction'
    netiface.createJunction(QPointF(m2p(x1), m2p(y1)), QPointF(m2p(x2), m2p(y2)), junctionName)

    # # step2: 创建静态路径
    netiface.buildAndApplyPaths(3)    # 设置每个OD最多搜索3条路径
    netiface.reSetDeciPoint()    # 优化决策点位置
    for dp in netiface.decisionPoints(): 
        for routing in dp.routings(): 
            netiface.reSetLaneConnector(routing)    # 优化路径中的车道连接


    # # step3: 为节点中每个转向设置流量
    # 由于没有已知值, 这里针对不同转向类型为其赋流量初值
    turnVolumeReduct = {'左转': 400, '直行': 1200, '右转': 200, '掉头': 0}
    # # step3-1: 添加流量时间段
    timeInterval = netiface.addFlowTimeInterval()
    timeId = timeInterval.timeId
    startTime = 0
    endTime = 3600
    netiface.updateFlowTimeInterval(timeId, startTime, endTime)
    # # step3-2: 遍历转向, 为其设置流量
    junctions = netiface.getAllJunctions()
    for junction in junctions: 
        junctionId = junction.getId()
        for turning in junction.getAllTurnningInfo(): 
            turningId = turning.turningId
            turnType = turning.strTurnType
            inputVolume = turnVolumeReduct.get(turnType, 0)
            # 为该转向设置输入流量
            netiface.updateFlow(timeId, junctionId, turningId, inputVolume)


    # # step4: 进行流量分配计算
    # 设置BPR路阻函数参数, 流量分配算法参数
    theta = 0.1
    bpra = 0.15
    bprb = 4
    maxIterateNum = 300
    netiface.updateFlowAlgorithmParams(theta, bpra, bprb, maxIterateNum)    # 更新计算参数
    result = netiface.calculateFlows()    # 计算路径流量分配并应用, 返回分配结果

    # 取流量分配结果
    resultJson = collections.defaultdict(list)
    for timeId, turningFlow in result.items(): 
        for i in turningFlow: 
            junction = i.pJunction.getId()
            turning = f"{i.turningBaseInfo.strDirection}-{i.turningBaseInfo.strTurnType}"
            inputVolume = i.inputFlowValue    # 该转向输入流量
            realVolume = i.realFlow    # 该转向实际分配到的流量
            relativeError = i.relativeError    # 分配的相对误差
            interval = i.flowTimeInterval
            startTime = interval.startTime
            endTime = interval.endTime
            resultJson[f"{startTime}-{endTime}"].append({'junction': junction, 'turning': turning, 'inputVolume': inputVolume, 'realVolume': realVolume, 'relativerror': relativeError})
    print(f"result: {resultJson}")


```

 **long createPedestrianComposition(String name, SWIGTYPE_p_QMapT_int_qreal_t mpCompositionRatio)**

创建行人组成  

参数: 
[ in ] name: 组成名称  
[ in ] mpCompositionRatio: 组成明细, key为行人类型编码, value为行人类型占比 , 
[ out ] 返回: 组成ID, 如果创建失败返回-1

```java
// 创建行人组成
Map<Integer, Double> compostion = new HashMap<>();
compostion.put(1, 0.8);  // 类型 1，占比 0.8
compostion.put(2, 0.2);  // 类型 2，占比 0.2
// 调用接口创建行人组成
long pedComposition = netiface.createPedestrianComposition("自定义1", compostion);
```


 **LayerInfo addLayerInfo(String name, double height, boolean visible, boolean locked)**

新增层级, 返回新增的层级信息  

参数: 
[ in ] name: 层级名称  
[ in ] height: 层级高度  
[ in ] visible: 是否可见  
[ in ] locked: 是否锁定, 锁定后面域不可以修改  
[ out ] 返回: 图层对象  

```java
LayerInfo pedLayer = netInterface.addLayerInfo("行人图层", 0.0, true, false);
System.out.println("图层创建成功: " + pedLayer.getId());
```

 **IPedestrianRectRegion createPedestrianRectRegion(Point startPoint, Point endPoint)**

创建矩形行人面域   

参数: 
[ in ] startPoint: 左上角  
[ in ] endPoint: 右下角  
[ out ] 矩形行人面域对象  

```java
// 创建第一个行人矩形面域
Point point1 = new Point(-300, -300);
Point point2 = new Point(-400, -400);
IPedestrianRectRegion leftupArea = netInterface.createPedestrianRectRegion(point1, point2);
// 创建第二个行人矩形面域
Point point3 = new Point(-400, -400);
Point point4 = new Point(-500, -500);
IPedestrianRectRegion leftupArea1 = netInterface.createPedestrianRectRegion(point3, point4);
// 删除第二个面域
netInterface.removePedestrianRectRegion(leftupArea1);
```

 **IPedestrianEllipseRegion createPedestrianEllipseRegion(Point startPoint, Point endPoint)** 

创建椭圆行人面域  

参数: 
[ in ] startPoint: 左上角  
[ in ] endPoint: 右下角  
[ out ] 椭圆行人面域对象

 **IPedestrianTriangleRegion createPedestrianTriangleRegion(Point startPoint, Point endPoint)**

创建三角形行人面域  

参数: 
[ in ] startPoint: 左上角  
[ in ] endPoint: 右下角  
[ out ] 三角形行人面域对象  


 **IPedestrianFanShapeRegion createPedestrianFanShapeRegion(Point startPoint, Point endPoint)**

创建扇形行人面域  

参数: 
[ in ] startPoint: 左上角  
[ in ] endPoint: 右下角  
[ out ] 扇形行人面域对象  


 **IPedestrianPolygonRegion createPedestrianPolygonRegion(Vector<Point> polygon)**

创建多边形行人面域  

参数: 
[ in ] polygon: 多边形顶点  
[ out ]多边形行人面域对象


 **IPedestrianSideWalkRegion createPedestrianSideWalkRegion(ArrayList<Point> vertexs)**

创建人行道  

参数: 
[ in ] vertexs: 顶点列表  
[ out ] 人行道对象


 **IPedestrianCrossWalkRegion createPedestrianCrossWalkRegion(Point startPoint, Point endPoint)**

创建人行横道  

参数: 
[ in ] startPoint: 左上角  
[ in ] endPoint: 右下角  
[ out ] 人行横道对象


 **IPedestrianStairRegion createPedestrianStairRegion(Point startPoint, Point endPoint)**

创建人行横道  

参数: 
[ in ] startPoint: 起点  
[ in ] endPoint: 终点  
[ out ] 楼梯对象


 **IPedestrianPathPoint createPedestrianPathStartPoint(Point scenePos)**

创建行人发生点  

参数: 
[ in ] scenePos: 场景坐标, 
[ out ] 行人发生点对象


 **IPedestrianPathPoint createPedestrianPathEndPoint(Point scenePos)**

创建行人结束点  

参数: 
[ in ] scenePos: 场景坐标  
[ out ] 行人结束点对象


 **IPedestrianPathPoint createPedestrianDecisionPoint(Point scenePos)**

创建行人决策点  

参数: 
[ in ] scenePos: 场景坐标  
[ out ] 创建行人决策点  


 **IPedestrianPath createPedestrianPath(IPedestrianPathPoint pStartPoint, IPedestrianPathPoint pEndPoint, ArrayList<Point> middlePoints)**

创建行人路径（或行人局部路径）  

参数: 
[ in ] pStartPoint: 行人发生点（或行人决策点）  
[ in ] pEndPoint: 行人结束点  
[ in ] middlePoints: 一组中间必经点  
[ out ] 行人路径对象


 **def createCrossWalkSignalLamp(psignalController: Tessng.ISignalController, name: str, crosswalkid: str, scenePos: QPointF, isPositive: bool) -> Tessng.ICrosswalkSignalLamp: ...**

创建人行横道信号灯   

参数: 
[ in ]  psignalController: 信号机  
[ in ] name: 名称  
[ in ] crosswalkId: 人行横道ID  
[ in ] scenePos: 位于人行横道内的场景坐标  
[ in ] isPositive: 信号灯管控方向是否为正向  
[ out ]人行横道信号灯对象  

```python
signalLamp1_positive = netiface.createCrossWalkSignalLamp(signalController, "南斑马线信号灯", s_crosswalk.getId() , QPointF(13, 22), True)
```

 **ILink updateLink(_Link link, ArrayList<_Lane> lLane, ArrayList<Point> lPoint)**

更新路段, 更新后返回路段对象 

参数: 
[ in ] link: 更新的路段数据  
[ in ] lLink: 更新的车道列表数据  
[ in ] lPoint: 更新的断点集合  

 **IConnector updateConnector(_Connector connector)**

更新连接段, 更新后返回连接段对象

参数: 
[ in ] connector: 连接段数据

返回: 更新后的连接段对象

 **IDecisionPoint updateDecipointPoint(_DecisionPoint deciPoint, ArrayList<_RoutingFLowRatio> lFlowRatio)**

更新决策点及其各路径不同时间段流量比

参数: 
[ in ] deciPoint: 决策点数据
[ in ] lFlowRatio: 各路径按时间段流量比的数据集合

返回: 更新后的决策点

举例: 

```java
public IDecisionPoint updateDecisionPointWithFlowRatio(
    NetInterface netInterface,
    IDecisionPoint decisionPoint,
    IRouting decisionRouting1,
    IRouting decisionRouting2,
    IRouting decisionRouting3) {

    // 分配左、直、右流量比
    List<_RoutingFLowRatio> flowRatios = new ArrayList<>();

    _RoutingFLowRatio flowRatioLeft = new _RoutingFLowRatio();
    flowRatioLeft.setRoutingFLowRatioID(1);
    flowRatioLeft.setRoutingID(decisionRouting1.id());
    flowRatioLeft.setStartDateTime(0);
    flowRatioLeft.setEndDateTime(999999);
    flowRatioLeft.setRatio(2.0);
    flowRatios.add(flowRatioLeft);

    _RoutingFLowRatio flowRatioStraight = new _RoutingFLowRatio();
    flowRatioStraight.setRoutingFLowRatioID(2);
    flowRatioStraight.setRoutingID(decisionRouting2.id());
    flowRatioStraight.setStartDateTime(0);
    flowRatioStraight.setEndDateTime(999999);
    flowRatioStraight.setRatio(3.0);
    flowRatios.add(flowRatioStraight);

    _RoutingFLowRatio flowRatioRight = new _RoutingFLowRatio();
    flowRatioRight.setRoutingFLowRatioID(3);
    flowRatioRight.setRoutingID(decisionRouting3.id());
    flowRatioRight.setStartDateTime(0);
    flowRatioRight.setEndDateTime(999999);
    flowRatioRight.setRatio(1.0);
    flowRatios.add(flowRatioRight);

    // 构建决策点数据
    _DecisionPoint decisionPointData = new _DecisionPoint();
    decisionPointData.setDeciPointID(decisionPoint.id());
    decisionPointData.setDeciPointName(decisionPoint.name());

    // 获取决策点坐标
    QPointF decisionPointPos = new QPointF();
    boolean gotPoint = decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos);

    if (gotPoint) {
        decisionPointData.setX(decisionPointPos.x());
        decisionPointData.setY(decisionPointPos.y());
        decisionPointData.setZ(decisionPoint.link().z());
    } else {
        System.out.println("无法获取决策点坐标");
        return null;
    }

    // 更新决策点及其路径流量比
    IDecisionPoint updatedDecisionPoint = netInterface.updateDecipointPoint(decisionPointData, flowRatios);

    if (updatedDecisionPoint != null) {
        System.out.println("决策点更新成功: " + updatedDecisionPoint.name());
    } else {
        System.out.println("决策点更新失败");
    }

    return updatedDecisionPoint;
}
```

  **boolean updateRoadWorkZone(DynaRoadWorkZoneParam param, UnitOfMeasure unit)**

更新施工区, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] pIRoadWorkZone: 将要移除的施工区对象  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

 **boolean updateLimitedZone(DynaLimitedZoneParam param)**

更新限行区

参数: 
[ in ] param: 限行区信息, 用于封装更新限行区所需的参数

 **boolean updateReconStruction(DynaReconstructionParam param, UnitOfMeasure unit)**

更新改扩建

参数: 
[ in ] param: 扩建区信息, 用于封装更新扩建区所需的参数  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位    

 **void removeReconstruction(IReconstruction pIReconstruction)**

移除改扩建

参数: 
[ in ] pIReconstruction: 将要移除的改扩建对象引用

 **boolean updateReduceSpeedArea(DynaReduceSpeedAreaParam param)**

更新限速区  

参数: 
[ in ] param: 限速区信息, 用于封装更新限速区所需的参数  

 **ITollLane updateTollLane(DynaTollLane param)**

更新收费车道  

参数: 
[ in ]  param: 动态收费车道信息, 用于封装更新收费车道所需的参数  

 **IParkingRegion updateParkingRegion(DynaParkingRegion param)**

更新停车区  

参数: 
[ in ]  param: 动态停车区信息, 用于封装更新停车区所需的参数  

 **DynaTollParkingTimeDis updateTollParkingTimeDis(DynaTollParkingTimeDis param)**

更新收费站停车时距分布  

参数: 
[ in ]  param: 停车时距分布参数

 **DynaParkingTimeDis updateParkingTimeDis(DynaParkingTimeDis param)**

更新停车场停车时距分布  

参数: 
[ in ]  param: 停车时距分布参数

 **boolean updateJunctionName(int junctionId, String name)**

更新节点名字  

参数: 
[ in ] id: 节点ID  
[ in ] name: 节点名字

 **FlowTimeInterval updateFlowTimeInterval(int timeId, int startTime, int endTime)**

更新时间段(节点的流量时间段)  

参数: 
[ in ]timeId: 时间段ID  
[ in ]startTime: 开始时间(秒)  
[ in ]endTime: 结束时间(秒)

 **boolean updateFlow(int timeId, int junctionId, int turningId, int inputFlowValue)**

更新节点流向流量  

参数: 
[ in ]timeId: 时间段ID  
[ in ]junctionId: 节点ID  
[ in ]turningId: 转向ID  
[ in ]inputFlowValue: 输入流量值（辆/小时）

 **boolean updateFlowAlgorithmParams(double theta, double bpra, double bprb, int maxIterateNum)**

更新流量算法参数  

参数: 
[ in ]theta: 参数θ(0.01-1)  
[ in ]BPR路阻参数A(0.05-0.5)   
[ in ]bprb: BPR路阻参数B(1-10)  
[ in ]maxIterateNum: 最大迭代次数(1-5000)  

 **boolean updatePedestrianComposition(int compositionId, SWIGTYPE_p_QMapT_int_qreal_t mpCompositionRatio)**

创建行人组成  

参数: 
[ in ] compositionId: 组成Id  
[ in ] mpCompositionRatio: 组成明细, key为行人类型编码, value为行人类型占比 , 
[ out ] 返回: True表示更新成功, False表示更新失败

 **boolean updateLayerInfo(int layerId, String name, double height, boolean visible, boolean locked)**

更新层级信息  

参数: 
[ in ] id: 层级ID  
[ in ] name: 层级名称  
[ in ] height: 层级高度  
[ in ] visible: 是否可见  
[ in ] locked: 是否锁定, 锁定后面域不可以修改  
[ out ] 返回: 是否更新成功  

 **boolean updatePedestrianStartPointConfigInfo(PedestrianPathStartPointConfigInfo info)**

更新行人发生点配置信息  

参数: 
[ in ] info: 行人发生点配置信息  
[ out ] 返回: 是否更新成功  

 **boolean updatePedestrianDecisionPointConfigInfo(PedestrianDecisionPointConfigInfo info)**

更新行人决策点配置信息  

参数: 
[ in ] info: 行人决策点配置信息  
[ out ] 返回: 是否更新成功  

 **void removeLink(ILink pLink)**

移除路段, 从场景中移除pLink, 但不从文件中删除, 保存路网后才会从路网文件中删除 

参数: 
[ in ] pLink: 将要移除的路段

 **void removeConnector(IConnector pConnector)**

移除连接段, 从场景中移除pLink, 但不从文件中删除, 保存路网后才会从路网文件中删除

参数: 
[ in ] pConnector: 连接段对象

 **void removeGuidArrow(IGuidArrow pArrow)**

移除导向箭头

参数: 
[ in ] pArrow: 导向箭头对象


 **boolean removeDispatchPoint(IDispatchPoint pDispPoint)**

移除发车点

参数: 
[ in ] pDispPoint: 发车点对象

 **boolean removeVehicleComposition(int vehiCompId)**

移除车型组成

参数: 
[ in ] vehiCompId: 车型组成ID

 **boolean removeDeciRouting(IDecisionPoint pDeciPoint, IRouting pRouting)**

删除决策路径

参数: 
[ in ] pDeciPoint: 决策点
[ in ] pRouting: 将要删除的路径

举例: 

```java
if (netiface.removeDeciRouting(decisionPoint, decisionRouting3)) {
    System.out.println("删除右转路径成功。");
}
```

 **boolean removeVehiCollector(IVehicleDrivInfoCollector pCollector)**

移除车辆信息采集器

参数: 
[ in ] pCollector: 车辆信息采集器

 **void removeSignalPhase(ISignalPlan pPlan, int phaseId)**

移除已有相位, 相位移除后, 原相位序列自动重排, 

参数: 
[ in ] pPlan: 信控方案
[ in ] phaseId: 将要移除的相位ID

 **void removeSignalPhaseFromLamp(int SignalPhaseId, ISignalLamp signalLamp)**

为信号灯移除指定的（已绑定的）相位(如果相位列表只存在一个相位则将关联的相位设置为nu11)

 **boolean removeBusLine(IBusLine pBusLine)**

移除公交线路

参数: 
[ in ] pBusLine: 将要移除的公交线路对象

 **boolean removeBusStation(IBusStation pStation)**

移除公交站点

参数: 
[ in ] pStation: 公交站点对象

 **boolean removeBusStationFromLine(IBusLine pBusLine, IBusStation pStation)**

将公交站点与公交线路的关联关系解除

参数: 
[ in ] pBusLine: 公交线路
[ in ] pStation: 公交站点

```java
public void createAndManageBusLine(NetInterface netiface, ILink w_approach, ILink e_outgoing, ILink s_outgoing) {
    // 创建公交线路
    ArrayList<ILink> busLineLinks1 = new ArrayList<>();
    busLineLinks1.add(w_approach);
    busLineLinks1.add(e_outgoing);
    IBusLine busline = netiface.createBusLine(busLineLinks1);
    ArrayList<ILink> busLineLinks2 = new ArrayList<>();
    busLineLinks2.add(w_approach);
    busLineLinks2.add(s_outgoing);
    IBusLine busline1 = netiface.createBusLine(busLineLinks2);
    // 删除公交线路 busline1
    netiface.removeBusLine(busline1);

    // 设置公交线路期望速度
    if (busline != null) {
        busline.setDesirSpeed(TESSNG.m2p(60));
    }
    // 创建公交站点
    ILane lane_w_approach = w_approach.lanes().get(0);
    IBusStation busstation1 = netiface.createBusStation(lane_w_approach, TESSNG.m2p(30), TESSNG.m2p(100), "西进口公交站点1", UnitOfMeasure.Metric);
    IBusStation busstation2 = netiface.createBusStation(lane_w_approach, TESSNG.m2p(30), TESSNG.m2p(200), "西进口公交站点2", UnitOfMeasure.Metric);
    ILane lane_e_outgoing = e_outgoing.lanes().get(0);
    IBusStation busstation3 = netiface.createBusStation(lane_e_outgoing, TESSNG.m2p(30), TESSNG.m2p(200), "东出口公交站点1", UnitOfMeasure.Metric);
    // 将公交站点关联到公交线路上
    if (busline != null && busstation1 != null) {
        netiface.addBusStationToLine(busline, busstation1);
    }
    if (busline != null && busstation2 != null) {
        netiface.addBusStationToLine(busline, busstation2);
    }
    if (busline != null && busstation3 != null) {
        netiface.addBusStationToLine(busline, busstation3);
    }
    // 删除公交线路中的一个站点
    if (busline != null && busstation2 != null) {
        netiface.removeBusStationFromLine(busline, busstation2);
    }
}
```

 **void removeAccidentZone(IAccidentZone pIAccidentZone)**

移除事故区

 **void removeRoadWorkZone(IRoadWorkZone pIRoadWorkZone)**

移除施工区

参数: 
[ in ] pIRoadWorkZone: 将要移除的施工区对象

 **void removeLimitedZone(ILimitedZone pILimitedZone)**

移除限行区 Tessng.ILimitedZone 还是 Online.ILimitedZone

参数: 
[ in ] pILimitedZone: 将要移除的限行区对象, 数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数 Online.DynaLimitedZoneParam的案例见createLimitedZone

 **void removeReduceSpeedArea(IReduceSpeedArea pIReduceSpeedArea)**

移除限速区

参数: 
[ in ] pIReduceSpeedArea: 限速区对象

 **void removeTollLane(ITollLane pITollLane)**

移除收费车道

参数: 
[ in ] pITollLane: 收费车道对象

 **void removeTollDecisionPoint(ITollDecisionPoint pITollDecisionPoint)**

移除收费决策点

参数: 
[ in ] pITollDecisionPoint: 收费决策点对象

 **void removeParkingRegion(IParkingRegion pIParkingRegion)**

移除停车区

参数: 
[ in ] pIParkingRegion: 停车区对象

 **void removeParkingDecisionPoint(IParkingDecisionPoint pIParkingDecisionPoint)**

移除收费决策点

参数: 
[ in ] pIParkingDecisionPoint: 收费决策点对象

 **void removeTollRouting(ITollRouting pITollRouting)**

移除收费路径

参数: 
[ in ] pITollRouting: 收费路径对象

 **void removeParkingRouting(IParkingRouting pIParkingRouting)**

移除停车路径

参数: 
[ in ] pIParkingRouting: 停车路径对象

 **void removeTollLaneById(int id)**

通过ID移除收费车道

参数: 
[ in ] id: 收费车道ID

 **void removeTollDecisionPointById(int id)**

通过ID移除收费决策点

参数: 
[ in ] id: 收费决策点ID

 **void removeParkingRegionById(int id)**

通过ID移除停车区

参数: 
[ in ] id: 停车区ID

 **void removeParkingDecisionPointById(int id)**

通过ID移除停车决策点

参数: 
[ in ] id: 停车决策点ID

 **void removeTollRoutingById(int id)**

通过ID移除收费路径

参数: 
[ in ] id: 收费路径ID

 **void removeParkingRoutingById(int id)**

通过ID移除停车路径

参数: 
[ in ] id: 停车路径ID

 **void removeTollParkingTimeDis(int id)**

移除收费站停车时距分布  

参数: 
[ in ]  id : 停车时距分布参数的Id

 **void removeParkingTimeDis(int id)**

移除停车场停车时距分布  

参数: 
[ in ]  id : 停车时距分布ID

 **boolean removeJunction(int junctionId)**

删除节点  

参数: 
[ in ] id: 节点ID

 **boolean deleteFlowTimeInterval(int timeId)**

删除时间段(节点的流量时间段)  

参数: 
[ in ] timeId: 时间段ID

 **boolean removePedestrianComposition(int compositionId)**

移除行人组成  

参数: 
[ in ] compositionId: 组成Id  
[ out ] 返回: True表示成功, False表示失败

 **void removeLayerInfo(int layerId)**

删除某个层级, 会删除层级当中的所有元素 

参数: 
[ in ] layerId: 层级Id

 **void removePedestrianEllipseRegion(IPedestrianEllipseRegion pIPedestrianEllipseRegion)**

删除椭圆行人面域  

参数: 
[ in ] pIPedestrianEllipseRegion: 椭圆行人面域对象

 **void removePedestrianRectRegion(IPedestrianRectRegion pIPedestrianRectRegion)** 

删除矩形行人面域  

参数: 
[ in ] pIPedestrianRectRegion: 矩形行人面域对象

 **void removePedestrianTriangleRegion(IPedestrianTriangleRegion pIPedestrianTriangleRegion)**

删除三角形行人面域  

参数: 
[ in ] pIPedestrianEllipseRegion: 三角形行人面域对象  

 **void removePedestrianFanShapeRegion(IPedestrianFanShapeRegion pIPedestrianFanShapeRegion)**  

删除扇形行人面域  

参数: 
[ in ] IPedestrianFanRegion: 扇形行人面域对象

 **void removePedestrianPolygonRegion(IPedestrianPolygonRegion pIPedestrianPolygonRegion)**

删除多边形行人面域  

参数: 
[ in ] pIPedestrianPolygonRegion: 多边形行人面域对象

 **void removePedestrianSideWalkRegion(IPedestrianSideWalkRegion pIPedestrianSideWalkRegion)**

删除人行道  

参数: 
[ in ]  pIPedestrianSideWalkRegion: 人行道对象

 **void removePedestrianStairRegion(IPedestrianStairRegion pIPedestrianStairRegion)**

删除楼梯  

参数: 
[ in ] pIPedestrianStairRegion: 楼梯对象  

 **void removePedestrianPathStartPoint(IPedestrianPathPoint pIPedestrianPathStartPoint)**

删除行人发生点 

参数: 
[ in ] pIPedestrianPathStartPoint: 行人发生点对象

 **void removePedestrianPathEndPoint(IPedestrianPathPoint pIPedestrianPathEndPoint)**

删除行人结束点 

参数: 
[ in ] pIPedestrianPathStartPoint: 删除行人结束点

 **void removePedestrianDecisionPoint(IPedestrianPathPoint pIPedestrianDecisionPoint)**

删除行人决策点 

参数: 
[ in ] pIPedestrianPathStartPoint: 行人决策点对象 

 **void removePedestrianPath(IPedestrianPath pIPedestrianPath)**

删除行人路径 

参数: 
[ in ] pIPedestrianPath: 行人路径对象

 **void removeCrossWalkSignalLamp(ICrosswalkSignalLamp pICrosswalkSignalLamp)**

删除人行横道信号灯 

参数: 
[ in ]  pICrosswalkSignalLamp: 人行横道信号灯对象

 **void removePedestrianCrossWalkRegion(IPedestrianCrossWalkRegion pIPedestrianCrossWalkRegion)**

删除人行横道 

参数: 
[ in ] pIPedestrianCrossWalkRegion: 人行横道对象

 **boolean createEmptyNetFile(String filePath, int dbver)**

创建空白路网

参数: 
[ in ] filePath: 空白路网全路径名
[ in ] dbver: : 数据库版本

 **boolean initSequence(String schemaName)**

初始化数据库序列, 对保存路网的专业数据库序列进行初始化, 目前支持PostgreSql

参数: 
[ in ] schemaName: 数据库的schema名称

 **void buildNetGrid(double width, UnitOfMeasure unit)**

路网网格化, 默认单位: 像素

参数: 
[ in ] width: 单元格宽度, 默认单位: 米  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位    

```java
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
netiface.buildNetGrid(10, UnitOfMeasure.Metric);
```

 **ArrayList<ISection> findSectionOn1Cell(Point point, UnitOfMeasure unit)**

根据point查询所在单元格所有Section, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] point: 路网场景中的点  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: ISection列表   

 **ArrayList<ISection> findSectionOn4Cell(Point point, UnitOfMeasure unit)**

根据point查询最近4个单元格所有经过的ISection, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] point: 路网场景中的一个点  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: ISection列表

 **ArrayList<ISection> findSectionOn9Cell(Point point, UnitOfMeasure unit)**

根据point查询最近9个单元格所有经过的ISection, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] point: 路网场景中的一个点  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: ISection列表  

 **ArrayList<Location> locateOnSections(Point point, ArrayList<ISection> lSection, double referDistance, UnitOfMeasure unit)**

根据point对lSection列表中每一个Section所有LaneObject求最短距离, 返回Location列表, 列表按最短距离排序, 从小到大, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] point: 路网场景中的一个点  
[ in ] lSection: section列表  
[ in ] referDistance: LaneObject上与point最近的点到LaneObject起点距离, 默认单位: 像素, 是大约数, 只为提高计算效率, 默认值为0  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: Online.Location列表  

举例: 

```java
// 获取 TESS NG 接口
TessInterface tessng = TESSNG.tessngIFace();
NetInterface netInterface = tessng.netInterface();
// 获取所有分段
ArrayList<ISection> sections = netInterface.sections();
// 创建一个点对象
Point point = new Point(0, 0);
// 在所有分段上定位该点，获取位置信息
ArrayList<Location> locations = netInterface.locateOnSections(point, sections);
// 遍历结果并输出信息
if (locations != null && !locations.isEmpty()) {
    for (Location location : locations) {
        // 输出相关车道或车道连接的 ID
        System.out.println("相关车道或车道连接为: " + location.getPLaneObject().id());
        // 输出最近点的坐标
        Point nearestPoint = location.getPoint();
        System.out.println("最近点坐标: (" + nearestPoint.getX() + ", " + nearestPoint.getY() + ")");
        // 输出到最近点的距离
        System.out.println("到最近点的最短距离: " + location.getLeastDist());
        // 输出最近点到起点的里程
        System.out.println("最近点到起点的里程: " + location.getDistToStart());
        // 输出最近点所在分段序号
        System.out.println("最近点所在分段序号: " + location.getSegmIndex());
        System.out.println(); // 换行
    }
} else {
    System.out.println("未找到任何匹配的车道或车道连接。");
}
```

 **ArrayList<Location> locateOnCrid(Point point, int cellCount, UnitOfMeasure unit)**

point周围若干个单元格里查询LaneObject, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] point: 路网场景中的一个点  
[ in ] cellCount: 单元格数, 小于1时默认为1, 大于1小于4时默认为4, 大于4时默认为9  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: Location列表

 **RectF boundingRect()**

路网外围Rect, 用以获取路网边界

 **int getIDByItemName(String name)**

根据路网元素名获取自增ID

参数: 
[ in ] name: 路网元素名

**void moveLinks(ArrayList<ILink> lLink, Point offset, UnitOfMeasure unit)**

移动路段及相关连接段, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] lLink: 要移动的路段列表  
[ in ] offset: 移动的偏移量, 即沿着坐标系向X向Y轴各自移动的偏移量  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

```java
// 获取 TESS NG 接口
TessInterface tessng = TESSNG.tessngIFace();
NetInterface netiface = tessng.netInterface();
// 创建起点和终点坐标
Point startPoint = new Point(-300, 6);
Point endPoint = new Point(-25, 6);
// 创建点列表
ArrayList<Point> lPoint = new ArrayList<>();
lPoint.add(startPoint);
lPoint.add(endPoint);
// 创建路段
ILink w_approach = netiface.createLink(lPoint, 3, "西进口", true, UnitOfMeasure.Metric);
if (w_approach != null) {
    System.out.println("路段创建成功: " + w_approach.name());
    // 创建要移动的路段列表
    ArrayList<ILink> linksToMove = new ArrayList<>();
    linksToMove.add(w_approach);
    // 移动路段
    Point offset = new Point(0, 12);
    netiface.moveLinks(linksToMove, offset, UnitOfMeasure.Metric);
}
```







### 4.2. SimuInterface

SimuInterface是TessInterface的子接口, 通过此接口可以启动、暂停、停止仿真, 可以设置仿真精度, 获取仿真过程车辆对象、车辆状态（包括位置信息）, 获取几种检测器检测的样本数据和集计数据, 等等。

下面对SimuInterface接口方法作详细解释。

 **boolean byCpuTime()**

仿真时间是否由现实时间确定。

一个计算周期存在两种时间, 一种是现实经历的时间, 另一种是由仿真精度决定的仿真时间, 如果仿真精度为每秒20次, 仿真一次相当于仿真了50毫秒。默认情况下, 一个计算周期的仿真时间是由仿真精度决定的。在线仿真时如果算力不够, 按仿真精度确定的仿真时间会与现实时间存在时差。

 **boolean setByCpuTime(boolean bByCpuTime)**

设置是否由现实时间确定仿真时间, 如果设为True, 每个仿真周期现实经历的时间作为仿真时间, 这样仿真时间与现实时间相吻合。

参数: 
[ in ] bByCpuTime: 是否由现实时间确定仿真时间

 **void startSimu()**

启动仿真

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
simuInterface.startSimu();
```

 **void pauseSimu()**

暂停仿真

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
simuInterface.pauseSimu();
```

 **void stopSimu()**

停止仿真运行 

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
simuInterface.stopSimu();
```

 **void pauseSimuOrNot()**

暂停或恢复仿真。如果当前处于仿真运行状态, 此方法暂停仿真, 如果当前处于暂停状态, 此方法继续仿真

举例: 

```java
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
simuInterface.pauseSimuOrNot();
```

 **boolean isRunning()**

仿真是否在进行

 **boolean isPausing()**

仿真是否处于暂停状态

 **boolean isRecordTrace()**

仿真是否记录车辆轨迹

 **void setIsRecordTrace(boolean bRecord)**

设置是否记录车辆轨迹

参数: 
[ in ] bRecord: 是否记录车辆轨迹

 **long simuIntervalScheming()**

预期仿真时长, 即仿真设置窗口设置的仿真时间

 **long simuIntervalScheming()**

设置预期仿真时长

参数: 
[ in ] interval: 预期仿真时长, 默认单位: 秒

 **int simuAccuracy()**

获取仿真精度

 **void setSimuAccuracy(int accuracy)**

设置仿真精度, 即每秒计算次数

参数: 
[ in ] accuracy: 每秒计算次数

 **int acceMultiples()**

获取加速倍数

 **void setAcceMultiples(int multiples)**

设置加速倍数

参数: 
[ in ] multiples 加速倍数

 **void setThreadCount(int count)**

设置工作线程数

 **long batchNumber()**

当前仿真批次

 **double batchIntervalReally()**

当前批次实际时间

 **SWIGTYPE_p_qint64 startMSecsSinceEpoch()**

获取仿真开始的现实时间

 **SWIGTYPE_p_qint64 stopMSecsSinceEpoch()**

仿真结束的现实时间

 **long simuTimeIntervalWithAcceMutiples()**

获取当前已仿真时间

 **long vehiCountTotal()**

车辆总数, 包括已创建尚未进入路网的车辆、正在运行的车辆、已驶出路网的车辆

 **long vehiCountRunning()**

正在运行车辆数

 **IVehicle getVehicle(long vehiId)**

根据车辆ID获取车辆对象

参数: 
[ in ] vehiId: 车辆ID

 **ArrayList<IVehicle> allVehiStarted()**

所有正在运行车辆

```java
// 获取 TESS NG 顶层接口实例
TessInterface iface = TessInterface.getInstance();
// 获取仿真子接口
SimuInterface simuInterface = iface.simuInterface();
// 获取所有已启动的车辆列表
List<IVehicle> startedVehicles = simuInterface.allVehiStarted();
```

 **ArrayList<IVehicle> allVehicle()**

所有车辆, 包括已创建尚未进入路网的车辆、正在运行的车辆、已驶出路网的车辆

 **ArrayList<VehicleStatus> getVehisStatus(long batchNumber, UnitOfMeasure unit)**

获取所有正在运行的车辆状态, 包括轨迹

参数: 
[ in ] batchNumber: 批次号  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 车辆状态（包括轨迹）VehicleStatus列表  
注: 如使用米制单位, 请勿遗忘传入unit参数  

 **ArrayList<VehiclePosition> getVehiTrace(long vehiId, UnitOfMeasure unit)**

获取指定车辆运行轨迹, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] vehiId: 车辆ID  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
返回: 车辆运行轨迹, 即VehiclePosition列表  

 **ArrayList<SignalPhaseColor> getSignalPhasesColor()**

获取当前所有信号灯组相位颜色

返回: 当前相位颜色SignalPhaseColor列表, 包括各相位当前颜色设置的时间和已持续时间。 

 **ArrayList<VehiInfoCollected> getVehisInfoCollected()**

获取当前完成穿越车辆数据采集器的所有车辆信息

返回: 采集的车辆信息列表。

举例: 

```java
// 获取 TESS NG 顶层接口实例
TessInterface tessng = TessInterface.getInstance();
// 获取仿真子接口
SimuInterface simuInterface = tessng.simuInterface();
// 获取当前仿真时间完成穿越采集器的所有车辆信息
ArrayList<VehiInfoCollected> collectedVehicles = simuInterface.getVehisInfoCollected();
```


 **ArrayList<VehiInfoAggregated> getVehisInfoAggregated()**

获取最近集计时间段内采集器采集的所有车辆集计信息

返回: 采集器集计数据VehiInfoAggregated列表

 **ArrayList<VehiQueueCounted> getVehisQueueCounted()**

获取当前排队计数器计数的车辆排队信息

返回: 车辆排队信息VehiQueueCounted列表

 **ArrayList<VehiQueueAggregated> getVehisQueueAggregated()**

获取最近集计时间段内排队计数器集计数据

返回: 排队计数器集计数据VehiQueueAggregated列表

 **ArrayList<VehiTravelDetected> getVehisTravelDetected()**

获取当前行程时间检测器完成的行程时间检测信息

返回: 行程时间检测器数据VehiTravelDetected列表

 **ArrayList<VehiTravelAggregated> getVehisTravelAggregated()**

获取最近集计时间段内行程时间检测器集计数据

返回: 行程时间集计数据VehiTravelAggregated列表

 **IVehicle createGVehicle(DynaVehiParam dynaVehi)**

动态创建车辆

参数: 
[ in ] dynaVehi: 动态车辆信息

举例: 

```java
// 获取 TESS NG 接口
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
// 示例 link ID，实际应使用真实路段对象或 ID
long link3Id = 3; // 示例路段 ID，替换为你实际创建的路段 ID
long link4Id = 4;
// 创建车道 0 的车辆参数
DynaVehiParam dvp_lane0 = new DynaVehiParam();
dvp_lane0.setVehiTypeCode(1);           // 设置车辆类型代码
dvp_lane0.setRoadId(link3Id);           // 设置路段 ID
dvp_lane0.setLaneNumber(0);             // 设置车道编号
dvp_lane0.setDist(TESSNG.m2p(50));      // 设置距离路段起点位置（转换为像素）
dvp_lane0.setSpeed(TESSNG.m2p(20));     // 设置速度（转换为像素/秒）
dvp_lane0.setColor("#FF0000");          // 设置颜色：红色
// 创建车道 1 的车辆参数
DynaVehiParam dvp_lane1 = new DynaVehiParam();
dvp_lane1.setVehiTypeCode(2);           // 设置车辆类型代码
dvp_lane1.setRoadId(link3Id);           // 设置路段 ID
dvp_lane1.setLaneNumber(1);             // 设置车道编号
dvp_lane1.setDist(TESSNG.m2p(100));     // 设置距离路段起点位置
dvp_lane1.setSpeed(TESSNG.m2p(30));     // 设置速度
dvp_lane1.setColor("#008000");          // 设置颜色：绿色
// 创建车道 2 的车辆参数
DynaVehiParam dvp_lane2 = new DynaVehiParam();
dvp_lane2.setVehiTypeCode(3);           // 设置车辆类型代码
dvp_lane2.setRoadId(link4Id);           // 设置路段 ID
dvp_lane2.setLaneNumber(2);             // 设置车道编号
dvp_lane2.setDist(TESSNG.m2p(50));      // 设置距离路段起点位置
dvp_lane2.setSpeed(TESSNG.m2p(40));     // 设置速度
dvp_lane2.setColor("#0000FF");          // 设置颜色：蓝色
// 动态创建车辆
IVehicle vehi_lane0 = simuInterface.createGVehicle(dvp_lane0);
IVehicle vehi_lane1 = simuInterface.createGVehicle(dvp_lane1);
IVehicle vehi_lane2 = simuInterface.createGVehicle(dvp_lane2);
// 检查是否创建成功
if (vehi_lane0 != null) {
    System.out.println("车道0车辆创建成功，ID: " + vehi_lane0.id());
} else {
    System.out.println("车道0车辆创建失败");
}
if (vehi_lane1 != null) {
    System.out.println("车道1车辆创建成功，ID: " + vehi_lane1.id());
} else {
    System.out.println("车道1车辆创建失败");
}
if (vehi_lane2 != null) {
    System.out.println("车道2车辆创建成功，ID: " + vehi_lane2.id());
} else {
    System.out.println("车道2车辆创建失败");
}
```

 **IVehicle createBus(IBusLine pBusLine, double startSimuDateTime)**

动态创建公交车

参数: 
[ in ] pBusLine: 公交线路
[ in ] startSimuDateTime: 发车时间, 单位毫秒

举例: 

```java
// 获取 TESS NG 接口
TessInterface iface = TESSNG.tessngIFace();
SimuInterface simuInterface = iface.simuInterface();
// 假设 busLine 已经创建成功
// 动态创建公交车，延迟 10 秒（单位：毫秒）
long delayMs = 10 * 1000; // 10 秒后发车
IVehicle bus = simuInterface.createBus(busLine, delayMs);
```

 **ArrayList<IVehicle> vehisInLink(long linkId)**

指定ID路段上的车辆

参数

[ in ] linkId: 路段ID

返回: 车辆列表

举例: 

```java
public List<IVehicle> getVehiclesInLink(long linkId) {
    // 获取 TESS NG 顶层接口实例
    TessInterface iface = TessInterface.getInstance();
    // 获取仿真子接口
    SimuInterface simuInterface = iface.simuInterface();
    // 获取指定路段上的车辆列表
    List<IVehicle> vehicleList = simuInterface.vehisInLink(linkId);
    return vehicleList;
}
```

 **ArrayList<IVehicle> vehisInLane(long laneId)**

指定ID车道上的车辆

参数: 
[ in ] laneId: 车道ID

返回: 车辆列表

 **ArrayList<IVehicle> vehisInConnector(long connectorId)**

指定ID连接段上的车辆

参数: 
[ in ] connectorId: 连接段ID

返回: 车辆列表

 **ArrayList<IVehicle> vehisInLaneConnector(long connectorId, long fromLaneId, long toLaneId)**

指定连接段ID及上游车道ID和下游车道ID相关“车道连接”上的车辆

参数: 
[ in ] connectorId: 连接段ID
[ in ] fromLaneId: 上游车道ID
[ in ] toLaneId: 下游车道ID

 **boolean queueRecently(long queueCounterId, ObjReal queueLength, ObjInt vehiCount, UnitOfMeasure unit)**

获取排队计数器最近一次排队信息, 返回值为是否成功的标签, 具体的排队和流量信息见入参ref_queueLength, ref_vehiCount。  
该函数的入参为引用, 函数直接修改入参数据  
参数: 
[ in ] queueCounterId: 排队计数器ID  
\[in & out\] queueLength: 排队长度, 默认单位: 像素, 可通过unit参数设置单位  
\[in & out\] vehiCount: 排队车辆数, 默认单位: 像素, 可通过unit参数设置单位  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
[ out ] 是否获取成功  

 **SWIGTYPE_p_QListT_Online__Pedestrian__PedestrianStatus_t getPedestriansStatusByRegionId(long regionId)**

根据行人面域id获取当前时间面域上所有行人的状态信息  
参数: 
[ in ]  regionId: 面域ID  
[ out ] 行人状态信息列表

 **ArrayList<IPedestrian> allPedestrianStarted()**

获取所有正在运行的行人; , 因为此函数可能会影响仿真效率, 仿真默认关闭, 如需要可在config配置里添加配置开启  
参数: 
[ out ] 行人对象列表





### 4.3.  GuiInterface

GuiInterface是TessInterface的子接口, 通过此接口可以访问控制TESSNG主窗体, 在主窗体上创建菜单、自定义窗体等。

 **SWIGTYPE_p_QMainWindow mainWindow()**

获取TESS NG主窗体






## 5. 自定义插件TessPlugin

TessPlugin是用户开发的插件顶级接口, 下面有三个子接口: PyCustomerNet、PyCustomerSimulator、CustomerGui。TESS NG通过这三个子接口分别在路网、仿真过程、窗体这三个方面与用户插件进行交互。

获取插件顶层接口的方法: tessngPlugin()。

虽然用户可以通过接口TessInterface下的三个子接口访问控制TESS NG的路网、仿真过程及窗体, 但用户只能调用TESS NG接口方法, 不能深入接口方法内部改变运行逻辑。通过实现接口TessPlugin子接口的方法, 用户可以在TESS NG的方法内部施加影响, 改变运行逻辑。

TessPlugin下的子接口PyCustomerNet、PyCustomerSimulator可以让用户较多地参于加载路网及仿真过程, 改变TESSNG内部运行逻辑。比如, 通过实现PyCustomerNet、PyCustomerSimulator接口方法可以让用户加载路网后进行必要的处理, 点击仿真按钮后根据需要确定是否继续仿真或者放弃, 还可以在仿真过程对部分或全部车辆的速度施加影响, 主动干预车辆的自由变道, 等等。

插件的三个子接口PyCustomerNet、PyCustomerSimulator、CustomerGui的所有方法均有默认实现, 用户可以根据需要实现其中部分方法或全部方法, 这些方法均由TESSNG在加载并初始化插件、打开路网前后、仿真前、仿真过程中、仿真结束后进行调用, 正是通过TESS NG对这些接口方法的调用达到控制或影响TESS NG运行的目的。

由于插件接口方法调用的场景、目的均不一样, 为了尽可尽可能统一对插件接口方法理解, 很多方法采用如下结构形式: 

```java
boolean method(type outParam)
```

TESS NG在调用这些方法时作以下理解: 如果返加值为False, 视为用户没有反应, 忽略。如果返回值为True, 表明用户有反应, 这时再视参数outParam值进行处理。举范例中的一个例子, 曹安路上的车辆排成方正, 飞机后的车辆速度重新设置, 保持与飞机相同的速度。PyCustomerSimulator的子类MySimulator实现了reSetSpeed方法如下: 

```java
public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
    long tmpId = vehi.id() % 100000;
    String roadName = vehi.roadName();
    if ("曹安公路".equals(roadName)) {
        if (tmpId == 1) {
            this.mrSpeedOfPlane = vehi.currSpeed();
        } else if (tmpId >= 2 && tmpId <= this.mrSquareVehiCount) {
            ref_inOutSpeed.setValue(this.mrSpeedOfPlane);
        }
        return true;
    }
    return false;
}
```

TESS NG在计算车辆的速度后会调用插件的reSetSpeed方法, 如果该方法返回True, 视插件对此方法作出响应, 这时再用outSpeed值取代原先计算的车速。

下面对PyCustomerNet、PyCustomerSimulator两个子接口进行说明

### 5.1. JCustomerNet

PyCustomerNet是TessPlugin子接口, 用户实现这个接口, TESSNG在加载路网前后会调用用户实现的接口方法。范例在加载临时路网后创建路段、连接段和发车点。TESSNG在绘制部分路网元素时也会调用PyCustomerNet实现类相关方法。范例通过实现方法labelNameAndFont让部分路段和连接段用路段名（默认为ID）绘制标签。

下面对PyCustomerNet接口方法作详细解释。



 **boolean ref_netFileSuffix(ObjString ref_suffix)**

路网文件后缀, 由用户通过参数suffix设置

参数: 
[ out ] suffix: 路网文件后缀名

 **TreeMap<String, String> customerTableDDL()**

添加用户设计的表

返回: 用户数据库的表定义TreeMap, key为表名, value为表的定义

 **void insertCustomerData()**

插入用户插件创建的表数据

 **void deleteCustomerData()**

删除用户插件创建的表数据

 **void beforeLoadNet()**

打开路网前调用, 用户可以通过此方法在加载路网前作必要的初始化准备工作

 **void afterLoadNet()**

加载路网后调用。

举例: 

加载路网后读路段数, 如果路段数为0则创建路段、连接段和发车点: 

```java
public void afterLoadNet(){
    //代表TESS NG的接口
    TessInterface iface = TESSNG.tessngIFace();
    //代表TESS NG的路网子接口
    NetInterface netiface = iface.netInterface();
    int count = netiface.linkCount();
    if (count == 0) {
        createNet();
    }
}
```

 **boolean linkType(SWIGTYPE_p_QListT_QString_t lType)**

路段类型

参数: 

[ out ] lType: 用户定义的路段类型列表

 **def laneType(self, lType: typing.Sequence) -> bool: ...**

车道类型

参数: 

[ out ] lType: 用户定义的车道类型列表

 **boolean linkBuildGLanes(ILink pILink)**

创建车道

参数: 

[ in ] pILink: 路段对象

返回: 如果返回True, 表示用户已创建了车道, TESSNG不再创建

 **boolean isPermitForCustDraw()**

在绘制路网过程中是否允许调用客户绘制逻辑, 默认为False。本方法的目的是在python环境减少不必要的对python代码调用, 消除对运行效率的负面影响。可参数范例。

 **void ref_labelNameAndFont(int itemType, long itemId, ObjInt ref_outPropName, ObjReal ref_outFontSize)**

根据路网元素类型及ID确定用标签用ID或名称作为绘制内容。

参数: 

[ in ] itemType: 路段元素类型; 

[ in ] itemId: 路网元素ID; 

[ out ] outPropName: 枚举值, 选择用ID或路网元素名作为绘制内容; 

[ out ] outFontSize: 字体大小, 单位: 米。假设车道宽度是3米, 如果赋给outFontSize的值是6, 绘出的文字将占用两个车道的宽度。

返回: False 忽略, True 则根据设定的outPropName 值确定用ID或名称绘制标签, 并且用指定大小绘制。

举例: 

范例中的路段和连接段的标签内容部分是名称, 部分是ID。

```java
public void ref_labelNameAndFont(int itemType, long itemId, ObjInt ref_outPropName, ObjReal ref_outFontSize) {
    // 代表TESS NG的接口
    TessInterface iface = TESSNG.tessngIFace();
    // 代表TESS NG仿真子接口
    SimuInterface simuiface = iface.simuInterface();
    // 如果仿真正在进行，设置 ref_outPropName 为 None，路段和车道均不绘制标签
    if (simuiface.isRunning()) {
        ref_outPropName.setValue(GraphicsItemPropName.None.swigValue());
    }
    // 默认绘制 ID
    ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
    // 标签大小为 6 米
    ref_outFontSize.setValue(6);
    // 如果是连接段，一律绘制名称
    if (itemType == NetItemType.getGConnectorType()) {
        ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
    } else if (itemType == NetItemType.getGLinkType()) {
        if (itemId == 1 || itemId == 5 || itemId == 6) {
            ref_outPropName.setValue(GraphicsItemPropName.Name.swigValue());
        }
    }
}
```

 **boolean isDrawLinkCenterLine(long linkId)**

是否绘制路段中心线

参数: 

[ in ] linkId: 路段ID; 

返回值: True绘制, False不绘制。

 **boolean isDrawLinkCorner(long linkId)**

是否绘制路段四个拐角的圆形和正方型。

参数: 

[ in ] linkId: 路段ID; 

返回值: True绘制, False不绘制。

 **boolean isDrawLaneCenterLine(long laneId)**

是否绘制车道中心线。

参数: 

[ in ] laneId: 车道ID; 

返回值: True绘制, False不绘制。

 **void afterViewKeyReleaseEvent(SWIGTYPE_p_QKeyEvent event)**

QGraphicsView的keyReleaseEvent事件后行为, 用户可以根据自己的需要接入键盘事件, 实现自身业务逻辑。

 **void afterViewMouseDoubleClickEvent(SWIGTYPE_p_QMouseEvent event)**

QGraphicsView的mouseDoubleClickEvent事件后的行为, 用户可以根据自己的需要编写鼠标双击事件响应代码。

 **void afterViewMouseMoveEvent(SWIGTYPE_p_QMouseEvent event)**

QGraphicsView的mouseMoveEvent事件后的行为, 用户可以根据自己的需要编写鼠标移动事件响应代码。

 **void afterViewMousePressEvent(SWIGTYPE_p_QMouseEvent event)**

QGraphicsView的mousePressEvent事件后的行为, 用户可以根据自己的需要编写鼠标点击事件响应代码。

 **void afterViewMouseReleaseEvent(SWIGTYPE_p_QMouseEvent event)**

QGraphicsView的mouseReleaseEvent事件后的行为, 用户可以根据自己的需要编写鼠标释放事件响应代码。

 **void afterViewResizeEvent(SWIGTYPE_p_QResizeEvent event)**

QGraphicsView的resizeEvent事件后的行为, 用户可以根据自己的需要编写屏幕缩放事件响应代码。

 **void afterViewWheelEvent(SWIGTYPE_p_QWheelEvent event)**

QGraphicsView的鼠标滚动事件后的行为, 用户可以根据自己的需要编写鼠标滚动事件后响应代码。

 **void afterViewScrollContentsBy(int dx, int dy)**

QGraphicsView滚动条移动事件后的行为, 用户可以根据自己的需要实现视窗滚动条移动后响应代码。





### 5.2. JCustomerSimulator

PyCustomerSimulator是TessPlugin子接口, 用户实现这个接口。TESS NG在仿真前后以及仿真过程中调用这个接口实现的方法, 达到与插件交互的目的, 用户可以通过这个接口的实现在仿真前后以及仿真运算过程中对TESS NG的仿真进行干预, 大到可以控制仿真是否进行, 小到干预某一车辆的驾驶行为。

用户对车辆驾驶行为的干预主要通过车速和变道来实现。对车速的干预主要有以下几个方法: 

1）重新计算车速; 

2）修改路段限速; 

3）重新计算加速度; 

4）修改跟驰安全距离和安全时距、重新设置前车距

以上几个方法的优先级依次降低。在没有插件干预的情况下, 车辆行驶的最高速度受到道路的最高速度限制; 在有插件的干预下, 如果直接修改了车速, 则不受道路最高限速的限制。

下面对PyCustomerSimulator接口方法作详细解释。

 **void ref_beforeStart(ObjBool ref_keepOn)**

仿真前的准备。如果需要, 用户可通过设置keepOn为False来放弃仿真。

参数: 

[ out ] ref_keepOn: 是否继续, 默认为True; 

 **void afterStart()**

启动仿真后的操作。这个方法的处理时间尽量短, 否则影响仿真时长的计算, 因为调用这个方法的过程仿真已经计时。仿真前的操作尽可能放到beforeStart方法中处理。

 **void afterStop()**

 仿真结束后的操作, 如果需要, 用户可以在此方法释放资源。

**ArrayList<DispatchInterval> calcDynaDispatchParameters()**

计算动态发车信息, 用来修改发车点相关参数, 此方法可以用来实现实时动态仿真。

返回: 动态发车信息DispatchInterval列表。

举例: 

```java
public ArrayList<DispatchInterval> calcDynaDispatchParameters() {
    // 获取 TESSNG 顶层接口
    TessInterface iface = TESSNG.tessngIFace();
    SimuInterface simuInterface = iface.simuInterface();
    // 获取当前仿真时间（单位：毫秒）
    long currSimuTime = simuInterface.simuTimeIntervalWithAcceMutiples();
    // 每10秒执行一次，且仿真时间小于60秒
    if (currSimuTime % (10 * 1000) == 0 && currSimuTime < 60 * 1000) {
        // 获取 ID 等于 5 的路段上的车辆
        List<IVehicle> lVehi = simuInterface.vehisInLink(5);
        if (currSimuTime < 1000 * 30 || lVehi.size() > 0) {
            return new ArrayList<>();
        } else {
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();
            // 当前时间换算为秒
            int currSecs = now.getHour() * 3600 + now.getMinute() * 60 + now.getSecond();
            // 创建发车间隔对象
            DispatchInterval di = new DispatchInterval();
            // 动作控制案例 - 机动车交叉口 L5 路段发车点 ID 为 11
            di.setDispatchId(11);
            di.setFromTime(currSecs);
            di.setToTime(currSecs + 300 - 1);
            di.setVehiCount(300);
            // 设置车辆组成详情
            List<VehiComposition> vehiConsDetail = new ArrayList<>();
            vehiConsDetail.add(new VehiComposition(1, 60)); // 小客车 60%
            vehiConsDetail.add(new VehiComposition(2, 40)); // 大客车 40%
            di.setMlVehicleConsDetail(vehiConsDetail);
            System.out.println("流量修改完成，当前时间为" + currSimuTime);
            ArrayList<DispatchInterval> result = new ArrayList<>();
            result.add(di);
            return result;
        }
    }

    return new ArrayList<>();
}
```

 **ArrayList<DecipointFlowRatioByInterval> calcDynaFlowRatioParameters()**

一个或一次数据来源里保存的所有决策点在一个时间间隔的路径流量分配信息, 此方法可以用来实现实时动态仿真。

返回: 决策点流量分配信息DecipointFlowRatioByInterval列表。

 **ArrayList<SignalContralParam> calcDynaSignalContralParameters()**

一个或一次数据来源里保存的所有信号灯组的信号控制信息。

返回: 信号灯组控制参数SignalContralParam列表。

 **void initVehicle(IVehicle pIVehicle)**

初始化车辆, 此方法在车辆起动加入路网时被调用, 用户可以在这个方法里调用IVehicle的setVehiType方法重新设置类型, 调用initLane或initLaneConnector方法对车辆的车道序号、起始位置、车辆大小进行初始化。

参数: 

[ in ] pIVehicle: 车辆对象

举例: 

```java
public void initVehicle(IVehicle vehi){
    // 车辆 ID，不含首位数，首位数与车辆来源有关，如发车点、公交线路
    int tmpId = (int)(vehi.id() % 100000);
    // 车辆所在路段名或连接段名
    String roadName = vehi.roadName();
    // 车辆所在路段 ID 或连接段 ID
    int roadId = (int)(vehi.roadId());
    if ("曹安公路".equals(roadName)) {
        // 飞机
        if (tmpId == 1) {
            vehi.setVehiType(12);
            vehi.initLane(3, TESSNG.m2p(105), 0);
        }
        // 工程车
        else if (tmpId >= 2 && tmpId <= 8) {
            vehi.setVehiType(8);
            vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(80), 0);
        }
        // 消防车
        else if (tmpId >= 9 && tmpId <= 15) {
            vehi.setVehiType(9);
            vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(65), 0);
        }
        // 消防车
        else if (tmpId >= 16 && tmpId <= 22) {
            vehi.setVehiType(10);
            vehi.initLane((tmpId - 2) % 7, TESSNG.m2p(50), 0);
        }
        // 最后两队列小车
        else if (tmpId == 23) {
            vehi.setVehiType(1);
            vehi.initLane(1, TESSNG.m2p(35), 0);
        } else if (tmpId == 24) {
            vehi.setVehiType(1);
            vehi.initLane(5, TESSNG.m2p(35), 0);
        } else if (tmpId == 25) {
            vehi.setVehiType(1);
            vehi.initLane(1, TESSNG.m2p(20), 0);
        } else if (tmpId == 26) {
            vehi.setVehiType(1);
            vehi.initLane(5, TESSNG.m2p(20), 0);
        } else if (tmpId == 27) {
            vehi.setVehiType(1);
            vehi.initLane(1, TESSNG.m2p(5), 0);
        } else if (tmpId == 28) {
            vehi.setVehiType(1);
            vehi.initLane(5, TESSNG.m2p(5), 0);
        }
        // 最后两列小车的长度设为一样长，这个很重要，如果车长不一样长，加上导致的前车距就不一样，会使它们变道轨迹长度不一样，就会乱掉
        if (tmpId >= 23 && tmpId <= 28) {
            vehi.setLength(TESSNG.m2p(4.5), true);
        }
    }
}
```

 **void ref_beforeCreateGVehiclesForBusLine(IBusLine pBusLine, ObjBool ref_keepOn)**

创建公交车辆前的预处理

参数: 

[ in ] pBusLine: 公交线路

[ in、out ] ref_keepOn: 是否继续执行创建公交车辆, 如果ref_keepOn被赋值为False, TESSNG不再创建公交车辆

 **boolean shape(IVehicle pIVehicle, SWIGTYPE_p_QPainterPath outShape)**

车辆外型, 用户可以用此方法改变车辆外观

参数: 

[ in ] pIVehicle: 车辆对象

[ in、out ] outShape: 车辆外形

返回: 如果返回False, 则忽略

 **boolean ref_beforeCalcLampColor(ObjBool ref_keepOn)**

计算信号灯色前的预处理。

参数: 

[ in、out ] 是否断续计算

返回: 如果返回 True, 且keepOn等于False, TESS NG不再计算信号灯色。

 **boolean calcLampColor(ISignalLamp pSignalLamp)**

计算信号灯的灯色。ISignalLamp有设置信号灯颜色方法。

参数: 

[ in ] pSignalLamp: 信号灯对象; 

返回值: 

如果返回True, 表明用户已修改了信号灯颜色, TESS NG不再计算灯色。

 **boolean reCalcToLeftLane(IVehicle pIVehicle)**

计算是否要左强制变道, TESS NG在移动车辆时计算强制左变道的条件, 当条件不足时让插件计算, 如果返回值为True, 强制左变道。

参数: 

[ in ] pIVehicle: 车辆对象。

返回: False: 忽略, True: 强制左变道

 **boolean reCalcToRightLane(IVehicle pIVehicle)**

计算是否要右强制变道, TESS NG在先移动车辆时计算强制右变道的条件, 当条件不足时让插件计算, 如果返回值为True, 强制右变道。 
用户通过此函数设置是车辆是否有强制右边道的动机, 但是否变道还要看是否满足变道条件。

参数: 

[ in ] pIVehicle: 车辆对象

返回: False: 忽略, True: 强制右变道

 **void ref_beforeToLeftFreely(IVehicle pIVehicle, ObjBool ref_keepOn)**

自由左变道前处理, 如果ref_keepOn被赋值为False, TESSNG不再计算是否自由左变道
用户通过此函数设置车辆是否在后续的仿真中屏蔽自由左变道的动机生成

参数: 

[ in ] pIVehicle: 车辆

[ in、out ] ref_keepOn: 是否继续, 如果设为False, 不再计算是否可以左自由变道

 **void ref_beforeToRightFreely(IVehicle pIVehicle, ObjBool ref_keepOn)**

自由右变道前处理, 如果ref_keepOn被赋值为False, TESSNG不再计算是否自由右变道
用户通过此函数设置车辆是否在后续的仿真中屏蔽自由右变道的动机生成

参数: 

[ in ] pIVehicle: 车辆

[ in、out ] ref_keepOn: 是否继续, 如果设为False, 不再计算是否可以右自由变道

举例: 

```java
// 自由左变道前预处理
public void ref_beforeToLeftFreely(IVehicle pIVehicle, ObjBool ref_keepOn) {
    if (pIVehicle.roadId() == 9) {
        pIVehicle.setColor("#0000FF"); // 蓝色
    }
}
// 自由右变道前预处理
public void ref_beforeToRightFreely(IVehicle pIVehicle, ObjBool ref_keepOn) {
    if (pIVehicle.roadId() == 9) {
        pIVehicle.setColor("#EE0000"); // 红色
    }
}
```

 **boolean reCalcToLeftFreely(IVehicle pIVehicle)**

重新计算是否要自由左变道。TESS NG在移动车辆时计算自由左变道条件, 当条件不足时让插件计算, 如果返回值为True, 自由左变道。
用户可以调用此函数在需要的时候让TESSNG再次计算自由左变道的判断逻辑; 但不保证计算结果满足变道条件

[ in ] pIVehicle: 车辆

返回: False: 忽略, True: 左自由変道, 但在一些特殊场景也会放弃变道, 如危险

 **boolean reCalcToRightFreely(IVehicle pIVehicle)**

重新计算是否要自由右变道。TESS NG在移动车辆时计算自由右变道条件, 当条件不足时让插件计算, 如果返回值为True, 自由右变道。
用户可以调用此函数在需要的时候让TESSNG再次计算自由右变道的判断逻辑; 但不保证计算结果满足变道条件
参数: 

[ in ] pIVehicle: 车辆对象

返回: False: 忽略, True: 右自由変道, 但在一些特殊场景也会放弃变道, 如危险

 **boolean reCalcDismissChangeLane(IVehicle pIVehicle)**

重新计算是否撤销变道, 通过pIVehicle获取到自身条件数据及当前周边环境条件数据, 判断是否要撤销正在进行的变道。

参数: 

[ in ] pIVehicle: 车辆

返回: True 如果当前变道完成度不超过三分之一, 则撤销当前变道行为; False 忽略。

 **boolean ref_reCalcdesirSpeed(IVehicle pIVehicle, ObjReal ref_desirSpeed)**

重新计算期望速度, TESS NG调用此方法时将车辆当前期望速度赋给inOutDesirSpeed, 如果需要, 用户可在此方法重新计算期望速度, 并赋给inOutDesirSpeed。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_desirSpeed: 重新设置前后的车辆期望速度, 默认单位: 像素/秒; 

举例: 

```java
public boolean ref_reCalcdesirSpeed(IVehicle vehi, ObjReal ref_desirSpeed) {
    long tmpId = vehi.id() % 100000;
    String roadName = vehi.roadName();
    if ("曹安公路".equals(roadName)) {
        if (tmpId <= this.mrSquareVehiCount) {
            TessInterface iface = TESSNG.tessngIFace();
            SimuInterface simuIFace = iface.simuInterface();
            long simuTime = simuIFace.simuTimeIntervalWithAcceMutiples();
            if (simuTime < 5 * 1000) {
                ref_desirSpeed.setValue(0);
            } else if (simuTime < 10 * 1000) {
                ref_desirSpeed.setValue(TESSNG.m2p(20 / 3.6));
            } else {
                ref_desirSpeed.setValue(TESSNG.m2p(40 / 3.6));
            }
            return true;
        }
    }
    return false;
}
```

  **boolean ref_reCalcdesirSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutDesirSpeed, objUnitOfMeasure ref_unit)**

重新计算期望速度, TESS NG调用此方法时将车辆当前期望速度赋给inOutDesirSpeed, 如果需要, 用户可在此方法重新计算期望速度, 并赋给inOutDesirSpeed。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] inOutDesirSpeed: 重新设置前后的车辆期望速度, 默认单位: 像素/秒; 

[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例：

```java
public boolean ref_reCalcdesirSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutDesirSpeed, objUnitOfMeasure ref_unit) {
    // 设置单位为米（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 打印调试信息
    System.out.println("test: " + ref_inOutDesirSpeed.getValue() + ", " + pIVehicle.currSpeed() + ", " + pIVehicle.currSpeed(UnitOfMeasure.Metric));
    // 设置期望速度为 20 m/s
    ref_inOutDesirSpeed.setValue(20);
    // 返回 true 表示采用修改后的值
    return true;
}
```

**boolean ref_reSetFollowingType(IVehicle pIVehicle, ObjInt ref_outTypeValue)**

重新设置跟驰类型, 在计算加速度的过程中被调用

参数: 

[ in ] pIVehicle: 车辆对象

[ out ] ref_outTypeValue: 跟驰类型, 0: 停车, 1: 正常, 5: 急减速, 6: 急加速, 7: 汇入, 8: 穿越, 9: 协作减速, 10: 协作加速, 11: 减速待转, 12: 加速待转

返回: False: 忽略, True: 用ref_outTypeValue设置车辆驾驶行为的跟驰类型

 **boolean ref_reSetFollowingParam(IVehicle pIVehicle, ObjReal ref_inOutSafeInterval, ObjReal ref_inOutSafeDistance)**

重新设置跟驰模型的安全间距和安全时距。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_inOutSafeInterval: 安全时距, 单位: 秒; 

[ in、out ] ref_inOutSafeDistance: 安全间距: 单位: 像素; 

举例: 

```java
public boolean ref_reSetFollowingParam(IVehicle vehi, ObjReal ref_inOutSi, ObjReal ref_inOutSd) {
    String roadName = vehi.roadName();
    if ("连接段2".equals(roadName)) {
        ref_inOutSd.setValue(TESSNG.m2p(30));
        return true;
    }
    return false;
}
```

**boolean ref_reSetFollowingParam_unit(IVehicle pIVehicle, ObjReal ref_inOutSafeInterval, ObjReal ref_inOutSafeDistance, objUnitOfMeasure ref_unit)**

重新设置跟驰模型的安全间距和安全时距（支持单位参数）。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_inOutSafeInterval: 安全时距, 单位: 秒; 

[ in、out ] ref_inOutSafeDistance: 安全间距: 单位: 像素; 

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: false: 忽略, true: 用ref_inOutSafeInterval设置安全时距, 用ref_inOutSafeDistance设置安全间距

举例：

```java
public boolean ref_reSetFollowingParam_unit(IVehicle pIVehicle, ObjReal ref_inOutSafeInterval, ObjReal ref_inOutSafeDistance, objUnitOfMeasure ref_unit) {
    // 设置单位为米制
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置安全距离为 10 米
    ref_inOutSafeDistance.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **ArrayList<FollowingModelParam> reSetFollowingParams()**

重新设置跟驰模型参数, 影响所有车辆。此方法被TESS NG调用, 用返回的跟驰模型取代当前仿真正在采用的跟驰模型。

返回: 跟驰参数列表, 可对机动车和非机车的跟驰参数重新设置, 设置以后会被采用, 直到被新的参数所代替。

 **boolean ref_reSetDistanceFront(IVehicle pIVehicle, ObjReal distance, ObjReal s0)**

重新设置前车距及安全跟车距离

参数: 

[ in ] pIVehicle: 车辆对象

[ in、out ] distance: 当前车辆与前车的距离, 默认单位: 像素

[ in、out ] s0: 安全跟车距离, 默认单位: 像素

返回: False: 忽略, True: 用distance设置前车距, 用s0设置安全跟车距离

 **boolean ref_reSetDistanceFront_unit(IVehicle pIVehicle, ObjReal ref_distance, ObjReal ref_s0, objUnitOfMeasure ref_unit)**

重新设置前车距及安全跟车距离

参数: 

[ in ] pIVehicle: 车辆对象

[ in、out ] ref_distance: 当前车辆与前车的距离, 默认单位: 像素

[ in、out ] ref_s0: 安全跟车距离, 默认单位: 像素

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: False: 忽略, True: 用ref_distance设置前车距, 用ref_s0设置安全跟车距离

举例：
```java
public boolean ref_reSetDistanceFront_unit(IVehicle pIVehicle, ObjReal ref_distance, ObjReal ref_s0, objUnitOfMeasure ref_unit){
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置前车间距为 10 米
    ref_distance.setValue(10);
    // 设置 s0（静止时最小间距）为 10 米
    ref_s0.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **boolean ref_reSetSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutSpeed)**

重新设置车速。TESS NG调用此方法时将当前计算所得车速赋给ref_inOutSpeed的value, 如果需要, 用户可以在此方法重新计算车速并赋给ref_inOutSpeed的value。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_inOutSpeed: 重新计算前后的车速, 默认单位: 像素/秒。

返回: False: 忽略, True: 用ref_inOutSpeed设置车辆当前速度

举例: 

```java
public boolean ref_reSetSpeed(IVehicle vehi, ObjReal ref_inOutSpeed) {
    long tmpId = vehi.id() % 100000;
    String roadName = vehi.roadName();
    if ("曹安公路".equals(roadName)) {
        if (tmpId == 1) {
            this.mrSpeedOfPlane = vehi.currSpeed();
        } else if (tmpId >= 2 && tmpId <= this.mrSquareVehiCount) {
            ref_inOutSpeed.setValue(this.mrSpeedOfPlane);
        }
        return true;
    }
    return false;
}
```

 **boolean ref_reSetSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutSpeed, objUnitOfMeasure ref_unit)**

重新设置车速。TESS NG调用此方法时将当前计算所得车速赋给ref_inOutSpeed的value, 如果需要, 用户可以在此方法重新计算车速并赋给ref_inOutSpeed的value。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_inOutSpeed: 重新计算前后的车速, 默认单位: 像素/秒。

[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: False: 忽略, True: 用ref_inOutSpeed设置车辆当前速度

举例：
```java
public boolean ref_reSetSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutSpeed, objUnitOfMeasure ref_unit){
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置车辆速度为 20 m/s
    ref_inOutSpeed.setValue(20);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **void ref_beforeMergingToLane(IVehicle pIVehicle, ObjBool ref_keepOn)**

在“车道连接”上汇入车道前的计算, 可以让TESS NG放弃汇入计算, 以便于用户实现自己的汇入逻辑。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ out ] ref_keepOn: 是否放弃, 默认为True。赋值ref_keepOn.value为False, TESSNG则放弃汇入。

 **def afterOneStep(self) -> None: ...**

一个计算批次后的计算, 这个时候所有车辆均完成同一个批次的计算。通常在这个方法中获取所有车辆轨迹、检测器数据、进行必要的小计等。在这个方法中进行的计算基本不影响仿真结果的一致性, 但效率不高, 如果计算量大对仿真效率会有影响。

举例: 

```java
public void afterOneStep() {
    // 获取 TESSNG 顶层接口
    TessInterface iface = TESSNG.tessngIFace();
    // 获取 TESSNG 仿真子接口
    SimuInterface simuiface = iface.simuInterface();
    // 获取 TESSNG 路网子接口
    NetInterface netiface = iface.netInterface();
    // 当前仿真计算批次
    long batchNum = simuiface.batchNumber();
    // 当前已仿真时间，单位：毫秒
    long simuTime = simuiface.simuTimeIntervalWithAcceMutiples();
    // 获取当前正在运行的车辆列表
    List<IVehicle> vehis = simuiface.allVehiStarted();
    System.out.println("仿真批次: " + batchNum);
    System.out.println("当前仿真时间: " + simuTime + " ms");
    System.out.println("当前运行车辆数量: " + vehis.size());
}
```


 **void duringOneStep()**

该方法在各个线程进行同一批次的计算过程中调用, 这时存在部分车辆计算完成, 部分车辆仍在计算过程中。这个方法中的计算不够安全, 但效率较高。

 **void ref_beforeNextRoad(IVehicle pIVehicle, SWIGTYPE_p_QGraphicsItem pRoad, ObjBool ref_keepOn)**

计算下一道路前的处理

参数: 

[ in ] pIVehicle: 车辆

[ in ] pRoad: 暂不使用

[ in、out ] ref_keepOn: 是否继续计算, False: TESSNG不再计算后续道路, True: 继续计算

 **def candidateLaneConnectors(self, pIVehicle: Tessng.IVehicle, lInLC: typing.Sequence) -> typing.List: ...**

计算当车辆离开路段时后续可经过的“车道连接”, lInLC是已计算出的当前车道可达的所有“车道连接”, 用户可以从中筛选或重新计算。如果车辆有路径, 则忽略

参数: 

[ in ] pIVehicle 当前车辆

[ in ] lInLC: TESS NG计算出的后续可达“车道连接”列表

返回: 用户确定的后续可达“车道连接”列表

 **def candidateLaneConnector(self, pIVehicle: Tessng.IVehicle, lInLC: typing.Sequence) -> typing.List: ...**

计算车辆后续“车道连接”, 此时车辆正跨出当前路段, 将驶到pCurrLaneConnector。此方法可以改变后续“车道连接”。如果返回的“车道连接”为空, TESSNG会忽略此方法的调用。如果返回的“车道连接”不在原有路径上, 或者此方法设置了新路径且新路径不经过返回的“车道连接”, TESSNG调用此方法后会将路径设为空。

 **void ref_beforeNextPoint(IVehicle pIVehicle, ObjBool ref_keepOn)**

计算车辆移动到下一点前的操作, 用户可以通过此方法让TESSNG放弃对指定车辆到下一点的计算。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ out ] ref_keepOn: 是否继续, 默认为True, 如果keepOn赋值为False, TESSNG放弃移动到下一点的计算, 但不移出路网。

 **ArrayList<Integer> calcLimitedLaneNumber(IVehicle pIVehicle)**

计算限制车道序号: 如管制、危险等, 最右侧编号为0。

参数: 

[ in ] pVehicle: 车辆对象; 

返回: 车道序号集, 保存车辆不可以驰入的车道序号。

 **boolean ref_calcSpeedLimitByLane(ILink pILink, int laneNumber, ObjReal ref_outSpeed)**

由车道确定的限制车速（最高速度, 公里/小时）

参数: 

[ in ] pILink: 路段

[ in ] laneNumber: , laneNumber: 车道序号, 最右侧编号为0

[ in、out ] ref_outSpeed: 限制速度, 公里/小时

返回: False: 忽略, True: 用ref_outSpeed限制指定车道速度

 **boolean ref_calcSpeedLimitByLane_unit(ILink pILink, int laneNumber, ObjReal ref_outSpeed, objUnitOfMeasure ref_unit)**

由车道确定的限制车速（最高速度, 公里/小时）

参数: 

[ in ] pILink: 路段

[ in ] laneNumber: , laneNumber: 车道序号, 最右侧编号为0

[ in、out ] ref_outSpeed: 限制速度, 公里/小时

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: False: 忽略, True: 用ref_outSpeed限制指定车道速度

举例：

```java
public boolean ref_calcSpeedLimitByLane_unit(ILink pILink, int laneNumber, ObjReal ref_outSpeed, objUnitOfMeasure ref_unit) {
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置车道限速为 10 m/s
    ref_outSpeed.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **boolean ref_calcMaxLimitedSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutLimitedSpeed)**

重新计算车辆当前最大限速, 不受道路限速的影响。在没有插件干预的情况下, 车辆速度大于道路限度时按道路最大限速行驶, 在此方法的干预下, 可以提高限速, 让车辆大于道路限速行驶。

TESS NG调用此方法时将当前最高限速赋给inOutLimitedSpeed, 如果需要, 用户可以在方法里重新设置inOutLimitedSpeed值。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] inOutLimitedSpeed: 计算前后的最大限速, 默认单位: 像素/秒。

返回结果: 

如果返回False则忽略, 否则取inOutLimitedSpeed为当前道路最大限速。

 **boolean ref_calcMaxLimitedSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutLimitedSpeed, objUnitOfMeasure ref_unit)**

重新计算车辆当前最大限速, 不受道路限速的影响。在没有插件干预的情况下, 车辆速度大于道路限度时按道路最大限速行驶, 在此方法的干预下, 可以提高限速, 让车辆大于道路限速行驶。

TESS NG调用此方法时将当前最高限速赋给inOutLimitedSpeed, 如果需要, 用户可以在方法里重新设置inOutLimitedSpeed值。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] inOutLimitedSpeed: 计算前后的最大限速, 默认单位: 像素/秒。

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回结果: 

如果返回False则忽略, 否则取inOutLimitedSpeed为当前道路最大限速。

举例：

```java
public boolean ref_calcMaxLimitedSpeed_unit(IVehicle pIVehicle, ObjReal ref_inOutLimitedSpeed, objUnitOfMeasure ref_unit){
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置车辆最大限速为 10 m/s
    ref_inOutLimitedSpeed.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **boolean ref_calcDistToEventObj_unit(IVehicle pIVehicle, ObjReal ref_dist)**

计算到事件对象距离, 如到事故区、施工区的距离

参数: 

[ in ] pIVehicle: 车辆

[ in、out ] ref_dist: 车辆中心点距事件对象距离, 默认单位: 像素

返回: False: 忽略, True: 用ref_dist计算安全变道距离等

 **boolean ref_calcDistToEventObj_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit)**

计算到事件对象距离, 如到事故区、施工区的距离

参数: 

[ in ] pIVehicle: 车辆

[ in、out ] ref_dist: 车辆中心点距事件对象距离, 默认单位: 像素

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: False: 忽略, True: 用ref_dist计算安全变道距离等

举例：

```java
public boolean ref_calcDistToEventObj_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit) {
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置距离为 10 米
    ref_dist.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **boolean ref_calcChangeLaneSafeDist_unit(IVehicle pIVehicle, ObjReal ref_dist)**

计算安全变道距离。

参数: 

[ in ] pIVehicle: 车辆, 计算该车辆安全变道距离。

[ in、out ] ref_dist: 安全变道距离, ref_dist.value保存了TESSNG已算得的安全变道距离, 用户可以在此方法重新计算。

 返回: False 忽略, True TESS NG取ref_dist.value作为安全变道距离

 **boolean ref_calcChangeLaneSafeDist_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit)**

 计算安全变道距离(支持单位参数)。

 参数: 

[ in ] pIVehicle: 车辆, 计算该车辆安全变道距离。

[ in、out ] ref_dist: 安全变道距离, ref_dist.value保存了TESSNG已算得的安全变道距离, 用户可以在此方法重新计算。

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 返回: False 忽略, True TESS NG取ref_dist.value作为安全变道距离

 举例：

```java
public boolean ref_calcChangeLaneSafeDist_unit(IVehicle pIVehicle, ObjReal ref_dist, objUnitOfMeasure ref_unit){
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置变道安全距离为 10 米
    ref_dist.setValue(10);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **void afterStep(IVehicle pIVehicle)**

完成车辆pIVehicle“一个批次计算”后的处理。可以在此获取车辆当前信息, 如当前道路、位置、方向角、速度、期望速度、前后左右车辆等。

参数: 

[ in ] pIVehicle: 车辆对象; 

 **boolean ref_calcAcce(IVehicle pIVehicle, ObjReal acce)**

计算加速度; tessng的车辆按照此加速度进行下一步状态更新

[ in ] pIVehicle: 待计算加速度的车辆

[ out ] acce: 计算结果, 默认单位: 像素/秒^2

返回: False 忽略, True 则TES NG用调用此方法后所得acce.value作为当前车辆的加速度。

 **boolean ref_calcAcce_unit(IVehicle pIVehicle, ObjReal ref_acce, objUnitOfMeasure ref_unit)**

计算加速度; tessng的车辆按照此加速度进行下一步状态更新

[ in ] pIVehicle: 待计算加速度的车辆

[ out ] ref_acce: 计算结果, 默认单位: 像素/秒^2

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回: False 忽略, True 则TES NG用调用此方法后所得ref_acce.value作为当前车辆的加速度。

 举例：

```java
public boolean ref_calcAcce_unit(IVehicle pIVehicle, ObjReal ref_acce, objUnitOfMeasure ref_unit) {
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置车辆加速度为 2.0 m/s²
    ref_acce.setValue(2);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **boolean isCalcVehicleVector3D()**

是否计算车辆3D属性, 如欧拉角等

 **Point3D calcVehicleEuler(IVehicle pIVehicle, boolean bPosiDire)**

计算欧拉角

参数: 

[ in ] pIVehicle: 车辆对象

[ in ] bPosiDire: 车头方向是否正向计算, 如果bPosiDire为False则反向计算

 **boolean ref_reSetAcce(IVehicle pIVehicle, ObjReal ref_inOutAcce)**

重新计算加速度。TESS NG调用此方法时将当前计算所得加速度赋给ref_inOutAcce, 如果需要, 用户可以在此方法中重新计算加速度并赋给ref_inOutAcce.value。

参数: 

[ in ] pIVehicle: 车辆对象

[ in、out ] ref_inOutAcce: 重新计算前及计算后的加速度, 默认单位: 像素/秒^2

返回结果: 

如果返回False则忽略, 如果返回True, 则将ref_inOutAcce作为当前加速度。

举例: 

```java
public boolean ref_reSetAcce(IVehicle vehi, ObjReal inOutAcce) {
    String roadName = vehi.roadName();
    if ("曹安公路".equals(roadName)) {
        if (vehi.currSpeed() > TESSNG.m2p(20 / 3.6)) {
            inOutAcce.setValue(TESSNG.m2p(-5));
            return true;
        }
    }
    return false;
}
```

  **boolean ref_reSetAcce_unit(IVehicle pIVehicle, ObjReal ref_inOutAcce, objUnitOfMeasure ref_unit)**

重新计算加速度。TESS NG调用此方法时将当前计算所得加速度赋给inOutAcce, 如果需要, 用户可以在此方法中重新计算加速度并赋给ref_inOutAcce.value。

参数: 

[ in ] pIVehicle: 车辆对象

[ in、out ] ref_inOutAcce: 重新计算前及计算后的加速度, 默认单位: 像素/秒^2

[ in ] ref_unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

返回结果: 

如果返回False则忽略, 如果返回True, 则将ref_inOutAcce作为当前加速度。

 举例: 

```java
public boolean ref_reSetAcce_unit(IVehicle pIVehicle, ObjReal ref_inOutAcce, objUnitOfMeasure ref_unit) {
    // 设置单位为米制（Metric）
    ref_unit.setValue(UnitOfMeasure.Metric);
    // 设置车辆加速度为 2.0 m/s²
    ref_inOutAcce.setValue(2);
    // 返回 true 表示 TESS NG 应采用修改后的值
    return true;
}
```

 **void afterCalcTracingType(IVehicle pIVehicle)**

计算跟驰类型后处理

参数: 

[ in ] pIVehicle: 车辆

 **boolean travelOnChangingTrace(IVehicle pIVehicle)**

是否在变轨迹上

参数: 

[ in ] pIVehicle: 车辆

 **boolean boundingRect(IVehicle pIVehicle, SWIGTYPE_p_QRectF outRect)**

获取车辆边界矩形; 矩形顶点坐标是以车辆中心点为原点的, 是车辆坐标系下的像素点

参数: 

[ in ] pIVehicle: 车辆对象

[ out ] outRect: 车辆边界矩形

 **boolean shape(IVehicle pIVehicle, SWIGTYPE_p_QPainterPath outShape)**

获取车辆图形路径

参数: 

[ in ] pIVehicle: 车辆对象

[ out ] outShape: 车辆形状路径

 **void rePaintVehicle(IVehicle pIVehicle, SWIGTYPE_p_QPainter painter)**

绘制车辆

参数: 

[ in ] pIVehicle: 车辆对象

[ in ] painter: 笔刷

返回: True, TESSNG不再绘制车辆, False, TESSNG认为用户没有绘制, 继续绘制

 **boolean ref_paintVehicleWithRotation(IVehicle pIVehicle, SWIGTYPE_p_QPainter painter, ObjReal ref_inOutRotation)**

以设定的角度绘制车辆

参数: 

[ in ] pIVehicle, 车辆对象

[ in ] painter: QT的QPainter对象

[ in、out ] ref_inOutRotation: 角度, TESS NG在调用此方法时传入车辆的旋转角, 这个方法内部可以修改这个角度, 改变TESS NG计算结果

返回: 如果True, TESS NG不再绘制, 否则TESS NG按原有规则绘制车辆。

 **boolean paintVehicle(IVehicle pIVehicle, SWIGTYPE_p_QPainter painter)**

绘制车辆

参数: 

[ in ] pIVehicle, 要重绘制的车辆

[ in ] painter, QPainter对象

返回: 如果返回True, TESS NG不再绘制, 否则TESS NG按原有规则绘制车辆。

 **void rePaintVehicle(IVehicle pIVehicle, SWIGTYPE_p_QPainter painter)**

绘制车辆后的再绘制, 客户可在此方法增加绘制内容

参数: 

[ in ] pIVehicle, 要重绘制的车辆

[ in ] painter, QPainter对象

 **boolean ref_reCalcAngle(IVehicle pIVehicle, ObjReal ref_outAngle)**

重新计算角度。TESS NG调用此方法时将当前算得的角度赋给ref_outAngle.value, 如果需要, 用户可在此方法中重新计算车辆角度, 并将算得的角度赋给ref_outAngle.value。

参数: 

[ in ] pIVehicle: 车辆对象; 

[ in、out ] ref_outAngle: 重新计算前后角度, 北向0度顺时针, 一周360度

 **boolean isStopDriving(IVehicle pIVehicle)**

是否停车运行, TESS NG在计算下一点位置后调用, 判断是否要停止车辆pIVehicle的运行。

参数: 

[ in ] pIVehicle: 车辆对象; 

返回结果: 

如果返回True, 则停止该车辆运行, 移出路网。

 **boolean isPassbyEventZone(IVehicle pIVehicle)**

是否正在经过事件区（如: 施工区、限速区等）

参数: 

[ in ] pIVehicle: 车辆对象

 **void beforeStopVehicle(IVehicle pIVehicle)**

车辆停止运行前的处理。

参数: 

[ in ] pIVehicle: 车辆对象; 

 **void afterStopVehicle(IVehicle pIVehicle)**

车辆停止运行后的处理

参数: 

[ in ]pIVehicle: 车辆对象。

 **def recoveredSnapshot(self) -> bool: ...**

用快照恢复仿真场景, 分布式环境可用

 **def vehiRunInfo(self, pIVehicle: Tessng.IVehicle) -> str: ...**

车辆运行信息。在仿真过程中如果某辆车被单选, 按ctrl+i 会弹出被单选车辆运行状态, 文本框中的“其它信息”就是当前方法返回的字符串, 开发者可以借此对实现的业务逻辑进行了解, 用户可以了解仿真过程中具体车辆的一些特殊信息。





<!-- ex_nonav -->


