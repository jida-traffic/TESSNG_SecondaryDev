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

```python
# 获取路网ID
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网ID=" + netAttrs.id());
```

 ** String netName();**

获取路网名称

举例: 

```python
// 获取路网名称
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网名称=" + netAttrs.netName());
```

 ** String url();**

获取源数据路径, 可以是本地文件, 可以是网络地址

举例: 

```python
// 获取源数据路径
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("源数据路径=" + netAttrs.url());
```

 ** String type();**

获取来源分类: "TESSNG"表示TESSNG自建; "OpenDrive"表示由OpenDrive数据导入; "GeoJson"表示由geojson数据导入

举例: 

```python
// 获取来源分类
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("来源分类=" + netAttrs.type());
```

 ** String bkgUrl();**

获取背景路径

举例: 

```python
// 获取背景路径
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("背景路径=" + netAttrs.bkgUrl());
```

 **Map<String, Object> otherAttrs();**

获取其它属性数据, json 数据

举例: 

```python
// 获取其它属性数据
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("其它属性数据=" + netAttrs.otherAttrs());
```

 **String explain();**

获取路网说明

举例: 

```python
// 获取路网说明
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网说明=" + netAttrs.explain());
```

 **PySide2.QtCore.QPointF centerPoint(Tess.UnitOfMeasure unit);**

获取路网中心点位置, 默认单位: 像素, 可通过可选参数: unit设置单位, （用户也可以根据需求通过m2p转成米制单位坐标, 并注意y轴的正负号）

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位, 返回接口默认的单位

注: 米制与像素制仅在比例尺(像素比)不为1时生效。

举例: 

```python
// 获取路网中心点位置
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
IRoadNet netAttrs = netiface.netAttrs();
System.out.println("路网中心点位置=" + netAttrs.centerPoint());
System.out.println("路网中心点位置(米制)=" + netAttrs.centerPoint());
```

**案例代码**

```python
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
            + "路网说明=" + roadNet.explain() + ", 路网中心点位置(像素)=" + roadNet.centerPoint() + ", 路网中心点位置(米制)=" + roadNet.centerPoint() + " ");

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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

 **double v3z();**

获取Section高程, 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取Section高程
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的高程是" + section.v3z());
    System.out.println("id为" + section.id() + "的Section的高程(米制)是" + section.v3z());
}
```

 **double length();**

获取Section长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取Section长度
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有Section
List<ISection> sections = netiface.sections();
for (ISection section : sections) {
    System.out.println("id为" + section.id() + "的Section的长度是" + section.length());
    System.out.println("id为" + section.id() + "的Section的长度(米制)是" + section.length());
}
```

 **List<ILaneObject> laneObjects();**

车道与“车道连接”的父类接口列表

举例: 

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

 **double length(Tess.UnitOfMeasure unit);**

获取车道或“车道连接”长度, 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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

```python
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

```python
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

```python
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

 **java.util.List<?> centerBreakPoints();**

获取laneObject的中心线断点列表, 即车道或“车道连接”中心线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取ILaneObject的中心线断点列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        List<?> lCenterBreakPoints = laneObject.centerBreakPoints();
        for (Object centerBreakPoint : lCenterBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表是" + centerBreakPoint);
        }
        
    }
}
```

 **ArrayList<Point>  leftBreakPoints(Tess.UnitOfMeasure unit);**

获取laneObject的左侧边线断点列表; 即车道或“车道连接”左侧线断点集; 断点均为像素坐标下的点 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取ILaneObject的左侧边线断点列表
TessInterface iface = TESSNG.tessngIFace();
NetInterface netiface = iface.netInterface();
// 获取路网中的所有ILaneObject
List< ISection> lSections = netiface.sections();
for (ISection section : lSections) {
    List< ILaneObject> lLaneObjects = section.laneObjects();
    for (ILaneObject laneObject : lLaneObjects) {
        List<?> lLeftBreakPoints = laneObject.leftBreakPoints();
        for (Object leftBreakPoint : lLeftBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表是" + leftBreakPoint);
        }
        
    }
}
```

 **ArrayList<Point> rightBreakPoints(Tess.UnitOfMeasure unit);**

获取laneObject的右侧边线断点列表; 车道或“车道连接”右侧线断点集; 断点均为像素坐标下的点 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
        lRightBreakPoints = laneObject.rightBreakPoints(SWIGTYPE_UnitOfMeasure.swigToEnum(1));
        for (Object rightBreakPoint : lRightBreakPoints) {
            System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(米制)是" + rightBreakPoint);
        }
    }
}
```

 **ArrayList<Point3D> centerBreakPoint3Ds(Tess.UnitOfMeasure unit);**

获取laneObject的右侧边线断点列表; 车道或“车道连接”中心线断点(三维)集（包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
                List<?> lCenterBreakPoint3Ds = laneObject.centerBreakPoint3Ds();
                for (Object centerBreakPoint3D : lCenterBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的中心线断点列表(三维)是" + centerBreakPoint3D);
                }
                
            }
        }
    }
}
```

 **ArrayList<Point3D> leftBreakPoint3Ds(SWIGTYPE_UnitOfMeasure unit);**

获取laneObject的左侧边线断点列表; 车道或“车道连接”左侧线断点(三维)集; （包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
                List<?> lLeftBreakPoint3Ds = laneObject.leftBreakPoint3Ds();
                for (Object leftBreakPoint3D : lLeftBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的左侧边线断点列表(三维)是" + leftBreakPoint3D);
                }
                
            }
        }
    }
}
```

 **ArrayList<Point3D> rightBreakPoint3Ds(SWIGTYPE_UnitOfMeasure unit); **  

获取laneObject的右侧边线断点列表; 车道或“车道连接”右侧线断点(三维)集; （包含高程v3z属性的点）除高程是米制单位, x/y均为像素坐标, 像素单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
                lRightBreakPoint3Ds = laneObject.rightBreakPoint3Ds(SWIGTYPE_UnitOfMeasure.swigToEnum(1));
                for (Object rightBreakPoint3D : lRightBreakPoint3Ds) {
                    System.out.println("id为" + laneObject.id() + "的ILaneObject的右侧边线断点列表(三维, 米制)是" + rightBreakPoint3D);
                }
            }
        }
    }
}
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint);**

通过起终止断点, 获取该范围内laneObject的左侧边线断点集; 即车道或“车道连接”左侧部分断点(三维)集; 入参出参均为像素单位 

参数: 
[ in ] fromPoint: 中心线上某一点作为起点  
[ in ] toPoint: 中心线上某一点作为终点  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位    

举例: 

```python
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
                            laneObject.leftBreakPoints(SWIGTYPE_UnitOfMeasure.swigToEnum(1)).get(0),
                            laneObject.leftBreakPoints(SWIGTYPE_UnitOfMeasure.swigToEnum(1)).get(2),
                            SWIGTYPE_UnitOfMeasure.swigToEnum(1)
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

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint, SWIGTYPE_UnitOfMeasure unit);**

通过起终止断点, 获取该范围内laneObject的右侧边线断点集; 即车道或“车道连接”右侧部分断点(三维)集; 入参出参均为像素单位  

参数: 
[ in ] fromPoint: 中心线上某一点作为起点; QPointF类型, 且是像素坐标  
[ in ] toPoint: 中心线上某一点作为终点; QPointF类型, 且是像素坐标  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```python
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
                            laneObject.rightBreakPoints(SWIGTYPE_UnitOfMeasure.swigToEnum(1)).get(0),
                            laneObject.rightBreakPoints(SWIGTYPE_UnitOfMeasure.swigToEnum(1)).get(2),
                            SWIGTYPE_UnitOfMeasure.swigToEnum(1)
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

 **double distToStartPoint(Point p);**

中心线上一点到laneObject对象起点的距离; 默认单位: 像素  

参数: 
[ in ] p: 当前点坐标  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
                
            }
        }
    }
}
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex) ;**

laneObject中心线上一点到起点的距离, 默认单位: 像素, 附加条件是该点所在车道上的分段序号; 其中分段是指两个断点之间的部分。往往可以根据当前车辆所在的segmIndex信息, 调用该函数, 这样比distToStartPoint函数效率要高一些 

参数: 
[ in ] p: 当前中心线上点或附近点的坐标; QPointF类型, 且是像素坐标  
[ in ] segmIndex: 参数p点所在车道上的分段序号; 两个断点组成一个分段, 分段序号从0开始, 沿着道路方向递增  
[ in ] bOnCentLine: 参数p点是否在中心线上  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

注: 如传入米制参数, 请勿遗忘传入segmIndex与bOnCentLine参数。

举例: 

```python
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

```python
# 路段5最左侧车道向前延伸140米后所在点及分段序号, 返回像素
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
                QPointF outPoint = new QPointF();
                int outIndex = 0;
                double dist = m2p(140); // 假设m2p方法已实现
                if (laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex) != null) {
                    System.out.println("路段5最左侧车道向前延伸140米后所在点坐标为: (" + outPoint.x() + ", " + outPoint.y() + "), 分段序号为: " + outIndex);
                }
            }
        }
# 路段5最左侧车道向前延伸140米后所在点及分段序号, 返回米制
        link = netiface.findLink(5);
        if (link != null) {
            List<ILaneObject> laneObjects = link.laneObjects();
            if (!laneObjects.isEmpty()) {
                ILaneObject laneObjLeft = laneObjects.get(laneObjects.size() - 1); // 最左侧车道
                QPointF outPoint = new QPointF();
                int outIndex = 0;
                double dist = 140;
                if (laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex, UnitOfMeasure.Metric) != null) {
                    System.out.println("路段5最左侧车道向前延伸140米后所在点坐标为: (" + outPoint.x() + ", " + outPoint.y() + "), 分段序号为: " + outIndex);
                }
            }
        }

```

 

 **boolean getPointByDist(double dist, Point outPoint, SWIGTYPE_UnitOfMeasure unit);**

获取距离中心线起点向下游延伸dist处的点, 如果目标点不在中心线上返回False, 否则返回True; 默认单位: 像素, 可通过可选参数: unit设置单位, 

参数: 
[ in ] dist: 中心线起点向下游延伸的距离, 默认单位: 像素  
[ out ] outPoint: 中心线起点向下游延伸dist距离后所在点, 默认单位: 像素单位, 具体返回单位受unit参数控制 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```python
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

                        QPointF metricOutPoint = new QPointF();
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

```python
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

```python
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

```python
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

```python
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

```python
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

```python
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

 **double length(SWIGTYPE_UnitOfMeasure unit);**

获取路段长度, 默认单位: 像素, 可通过可选参数unit设置单位 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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

 **double width(SWIGTYPE_UnitOfMeasure unit);**

获取路段宽度, 默认单位: 像素; 可通过可选参数unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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

 **double z(SWIGTYPE_UnitOfMeasure unit)**

获取路段高程, 默认单位: 像素, 可通过可选参数unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```python
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

 **double v3z(SWIGTYPE_UnitOfMeasure unit) **

获取路段高程, 过载ISection的方法  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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

 **String name()** 

获取路段名称

举例: 

```python
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

 **void setName(String name)**

设置路段名称

举例: 

```python
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

 **String linkType()**

获取路段类型, 出参为字符串枚举: 城市主干道、城市次干道、人行道。

举例: 

```python
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

 **void setType(String type)**

设置路段类型, 路段类型有10种, 入参可以为: 高速路、城市快速路、匝道、城市主要干道、次要干道、地方街道、非机动车道、人行道、公交专用道、机非共享; 其中的任意一个, 其他类型暂不支持

举例: 

```python
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

 **int laneCount()**

获取车道数

举例: 

```python
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

 **double limitSpeed(SWIGTYPE_UnitOfMeasure unit)**

获取路段最高限速, 默认单位: 千米/小时  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

 **void setLimitSpeed(double speed);**

设置最高限速, 默认单位: 千米/小时 

参数: 
[ in ] speed: 最高限速, 单位: 千米/小时  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
            System.out.println("id为" + link.id() + "的ILink的最高限速(米制)为" + link.limitSpeed(UnitOfMeasure.Metric));
        }
    }
}
```

 **double minSpeed(SWIGTYPE_UnitOfMeasure unit)**

获取最低限速, 单位: 千米/小时 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

 **ArrayList<ILane> lanes()**

获取ILink上的车道列表, 列表按照从右到左的顺序排列; 列表元素为ILane对象

举例: 

```python
# 获取ILink上的车道列表
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

 **ArrayList<ILaneObject> laneObjects()**

获取ILink下所有LaneObject对象, 列表类型, LaneObject可以是车道, 也可以是“车道连接”的父对象

举例: 

```python
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

 **ArrayList<Point> centerBreakPoints(SWIGTYPE_UnitOfMeasure unit)**

获取路段中心线断点集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
# 获取ILink的中心线断点集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> centerBreakPoints = link.centerBreakPoints();
            for (Object centerBreakPoint : centerBreakPoints) {
                System.out.println("路段id为" + link.id() + "的中心线断点为" + centerBreakPoint);
            }
            
        }
    }
}
```

 **ArrayList<Point> leftBreakPoints(SWIGTYPE_UnitOfMeasure unit)**

获取路段左侧线断点集, 默认单位: 像素   

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
# 获取ILink的左侧线断点集
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ILink
        List<ILink> lLinks = netiface.links();
        for (ILink link : lLinks) {
            List<?> leftBreakPoints = link.leftBreakPoints();
            for (Object leftBreakPoint : leftBreakPoints) {
                System.out.println("路段id为" + link.id() + "的左侧线断点为" + leftBreakPoint);
            }
            
        }
    }
}
```

 **ArrayList<Point> rightBreakPoints(SWIGTYPE_UnitOfMeasure unit)**

获取路段右侧线断点集, 默认单位: 像素  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
# 获取ILink的右侧线断点集
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
            
        }
    }
}
```

 **ArrayList<Point3D> centerBreakPoint3Ds(SWIGTYPE_UnitOfMeasure unit)**

获取路段中心线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
            
        }
    }
}
```

 **ArrayList<Point3D> leftBreakPoint3Ds(SWIGTYPE_UnitOfMeasure unit)**

获取路段左侧线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
            
        }
    }
}
```

 **ArrayList<Point3D> rightBreakPoint3Ds()**

获取路段右侧线断点(三维)集, 默认单位: 像素

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
            
        }
    }
}
```

 **ArrayList<IConnector> fromConnectors()**

获取ILink的上游连接段, 其可能有多个, 返回类型为列表, 列表元素为IConnector对象

举例: 

```python
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

 **ArrayList<IConnector> toConnectors()**

获取ILink的下游连接段, 其可能有多个, 返回类型为列表, 列表元素为IConnector对象

举例: 

```python
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

 **void setOtherAttr(JsonObject otherAttr)**

设置路段的其它属性, TESSNG仿真过程中仅记录拓展的属性, 方便用户拓展, 并自定义使用

举例: 

```python
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

 **JsonObject otherAttr()**

获取路段的其它属性, TESSNG仿真过程中仅记录拓展的属性, 方便用户拓展, 并自定义使用

举例: 

```python
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

 **void setLaneTypes(ArrayList<String> lType)**

依次为ILink下所有车道设置车道属性（列表顺序为从右到左的车道顺序）, 入参为序列类型（列表, 元组等）, 其中元素的类型从这四种常量字符串中获取: "机动车道"、"机非共享"、"非机动车道"、"公交专用道"

举例: 

```python
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

 **void setLaneOtherAtrrs(SWIGTYPE_p_QListT_QJsonObject_t lAttrs)**

依次为ILink下所有车道设置车道其它属性

举例: 

```python
# 依次为ILink下所有车道设置车道其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks: 
    if link.laneCount() == 3: 
        link.setLaneOtherAtrrs([{'new_name': '自定义机动车道'}, {'new_name': '自定义非机动车道'}, {'new_name': '自定义公交专用道'}])

```

 **double distToStartPoint(Point p)**

ILink中心线上任意一点到ILink起点的距离, 默认单位: 像素  

参数: 
[ in ] p: 当前点坐标
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                }
            }
        }
```

 **def getPointAndIndexByDist(self, dist: double, outPoint: PySide2.QtCore.QPointF, outIndex: int, unit: Tess.UnitOfMeasure) -> bool: ...**

获取ILink中心线起点下游dist距离处的点及其所属分段序号, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向下游延伸的距离  
[ out ] outPoint: 中心线起点向下游延伸dist距离后所在点  
[ out ] outIndex: 中心线起点向下游延伸dist处的点所属分段序号
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
# 获取ILink中心线起点下游dist距离处的点及其所属分段序号
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks: 
    outPoint = QPointF()
    outIndex = 0
    link.getPointAndIndexByDist(50, outPoint, outIndex)
    print(f"id为{link.id()}的路段的中心线起点向下游延伸dist米处的点为{outPoint}, 所属分段序号为{outIndex}")
    outPoint1 = QPointF()
    outIndex1 = 0
    link.getPointAndIndexByDist(50, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"id为{link.id()}的路段的中心线起点向下游延伸dist米处的点(米制单位)为{outPoint1}, 所属分段序号为{outIndex1}")
```

 **def getPointByDist(self, dist: double, outPoint: PySide2.QtCore.QPointF, unit: Tess.UnitOfMeasure) -> bool: ...**

求ILink中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
# 获取ILink中心线起点向前延伸dist距离后所在点
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks: 
    outPoint = QPointF()
    link.getPointByDist(50, outPoint)
    print(f"id为{link.id()}的路段的中心线起点向前延伸dist米处的点(像素制)为{outPoint}")
    outPoint1 = QPointF()
    link.getPointByDist(50, outPoint1, UnitOfMeasure.Metric)
    print(f"id为{link.id()}的路段的中心线起点向前延伸dist米处的点(米制单位)为{outPoint1}")
```

 **Vector<Point> polygon()**

获取路段的多边型轮廓, 返回值类型QPolygonF, 默认单位: 像素

举例: 

```python
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

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showLinkAttr(netiface)

def showLinkAttr(netiface): 
    print(f"===场景中的link总数={netiface.linkCount()}, 第一个link的id={netiface.linkIds()[0]}")
    link = netiface.findLink(netiface.linkIds()[0])
    link1 = netiface.links()[0]
    print(link1)
    print(f"该link的属性: id={link.id()}, ")
    print(f"link.fromConnectors()={link.fromConnectors()}")
    print(f"该link: id={link.id()}, 其属性为: 路段类型={link.gtype()}, 路段长度（像素制）={link.length()}, 米制={link.length(UnitOfMeasure.Metric)}, "
          f"宽度（像素制）={link.width()}, 米制={link.width(UnitOfMeasure.Metric)}, 高程（像素制）={link.z()}, 米制={link.z(UnitOfMeasure.Metric)}, "
          f"高程v3z(像素制)={link.v3z()}, 米制={link.v3z(UnitOfMeasure.Metric)}, 设置新名字={link.setName('test_name')}name={link.name()}, linkType={link.linkType()}, "
          f"设置路段类型为城市次干道={link.setType('次要干道')}, 再次获取城市类型={link.linkType()}, 车道数={link.laneCount()}, "
          f"路段最高限速(像素制)={link.limitSpeed()}, 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)}, 路段最低限速(像素制)={link.minSpeed()}, 米制（km/h）={link.minSpeed(UnitOfMeasure.Metric)}, "
          f"将路段最高限速提高百分之20={link.setLimitSpeed(link.limitSpeed()*1.2)} or {link.setLimitSpeed(link.limitSpeed(UnitOfMeasure.Metric)*1.2, UnitOfMeasure.Metric)}, "
          f"路段最高限速(像素制)={link.limitSpeed()}, 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)}, 路段包含的车道对象={link.lanes()}, "
          f"路段包含的laneObject对象={link.laneObjects()}, "
          f"路段中心线（像素制）={link.centerBreakPoints()}, 米制={link.centerBreakPoints(UnitOfMeasure.Metric)}, "
          f"路段左侧线（像素制）={link.leftBreakPoints()}, 米制={link.leftBreakPoints(UnitOfMeasure.Metric)}, "
          f"路段右侧线（像素制）={link.rightBreakPoints()}, 米制={link.rightBreakPoints(UnitOfMeasure.Metric)}, "
          f"路段中心线3D（像素制）={link.centerBreakPoint3Ds()}, 米制={link.centerBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"路段左侧线3D（像素制）={link.leftBreakPoint3Ds()}, 米制={link.leftBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"路段右侧线3D（像素制）={link.rightBreakPoint3Ds()}, 米制={link.rightBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"fromConnector={link.fromConnectors()}, toConnectors={link.toConnectors()}, "
          f"fromSection={link.fromConnectors()[0].id() if link.fromConnectors() is not None and len(link.fromConnectors()) > 0 else 0}, "
          f"toSection={link.toSection(link.toConnectors()[0].id() if link.toConnectors() is not None and len(link.toConnectors() )> 0 else 0)}, "
          f"自定义其他属性: setOtherAttr={link.setOtherAttr({'new_msg': 'this is a av car'})}, "
          f"从右到左依次为车道设置类别={link.setLaneTypes(['公交专用道', '机动车道', '机动车道'])}, 为车道设置其他属性={link.setLaneOtherAtrrs([{'new_name': '自定义公交专用车道'}, {'new_name': '自定义机动车道'}, {'new_name': '自定义机动车道'}])}, "
          f"distToStartPoint距离起点长度（像素制）={link.distToStartPoint(link.centerBreakPoints()[-1])}, 米制={link.distToStartPoint(link.centerBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}, "
          f"polygon={link.polygon()}"
          )
    outPoint = QPointF()
    outIndex = 0
    outPoint1 = QPointF()
    outIndex1 = 0
    link.getPointAndIndexByDist(2.0, outPoint, outIndex)
    link.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

    outPoint2 = QPointF()
    outPoint3 = QPointF()
    link.getPointByDist(2.0, outPoint2)
    link.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
    print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")
```






### 2.5. ILane

车道接口, 方法如下: 

 **int gtype()**

类型, 车道类型为GLaneType

举例: 

```python
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

 **int id()**

获取车道ID

举例: 

```python
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

 **ILink link()**

获取车道所属路段, 返回路段对象

举例: 

```python
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

 **ISection section()**

获取车道所属Section, 返回Section对象, 其为ILink的父对象

举例: 

```python
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

 **double length(SWIGTYPE_UnitOfMeasure unit)**

获取车道长度, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                        System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道长度(米制单位)为" + lane.length(SWIGTYPE_UnitOfMeasure.swigToEnum(0)));
                    }
                }
            }
        }
```

 **double width(SWIGTYPE_UnitOfMeasure unit)**

获取车道宽度, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
                System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道宽度(米制单位)为" + lane.width(UnitOfMeasure.Metric));
            }
        }
    }
}
```

 **int number()**

获取车道序号, 从0开始（自外侧往内侧, 即自右向左依次编号）

举例: 

```python
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

 **String actionType()**

获取车道的行为类型, 返回的为行为类型常量字符串, 包括: "机动车道"、“非机动车道”、“公交专用道”

举例: 

```python
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

**ArrayList<ILaneConnector> fromLaneConnectors()**

获取上游车道连接列表

举例: 

```python
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

**ArrayList<ILaneConnector> toLaneConnectors()**

获取下游车道连接列表

举例: 

```python
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

 **ArrayList<Point> centerBreakPoints()**

获取车道中心点断点集, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取ILane的中心点断点集
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<ILink> lLinks = netiface.links();
                for (ILink link : lLinks) {
                    List<ILane> lLanes = link.lanes();
                    for (ILane lane : lLanes) {
                        List<Point> lCenterBreakPoints = lane.centerBreakPoints();
                        for (Object centerBreakPoint : lCenterBreakPoints) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心点断点集为" + "(" +  centerBreakPoint.getX() + "," +centerBreakPoint.getY() +")" );
                        }
                        List<Point> lCenterBreakPointsMeter = lane.centerBreakPoints(UnitOfMeasure.Metric);
                        for (Point centerBreakPointMeter : lCenterBreakPointsMeter) {
                            System.out.println("路段id为" + link.id() + "的车道id为" + lane.id() + "的车道的中心点断点集(米制单位)为" + "(" +  centerBreakPointMeter.getX() + "," +centerBreakPointMeter.getY() +")" );
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point> leftBreakPoints()**

获取车道左侧线断点集, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
            }
        }
    }
}
```

 **ArrayList<Point> rightBreakPoints()**

获取车道右侧线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
                    }
                }
            }
        }
```

 **ArrayList<Point3D> centerBreakPoint3Ds()**

获取车道中心线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreakPoint3Ds()**

获取车道左侧线断点(三维)集, 默认单位: 像素,  可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreakPoint3Ds()**

获取车道右侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint)**

根据指定起终点断点, 获取车道左侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点, 默认单位: 像素,    
[ in ] toPoint: 中心线上某一点作为终点, 默认单位: 像素,   
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                        }
                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint)**

根据指定起终点断点, 获取车道右侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点, 像素坐标,   
[ in ] toPoint: 中心线上某一点作为终点, 像素坐标,   
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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
                        }
                    }
                }
            }
        }
```

 **double distToStartPoint(Point p)**

获取中心线上一点到起点的距离, 默认单位: 像素 , 可通过unit参数设置单位

参数: 
[ in ] p: 当前点坐标
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                        }


                    }
                }
            }
        }
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex)**

根据中心线上任意点所处的车道分段号和该点本身信息, 计算该点到车道起点的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] p: 当前中心线上的点坐标, 像素单位  
[ in ] segmIndex: 该点所在车道上的分段序号  
[ in ] bOnCentLine: 该点是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                        }

                    }
                }
            }
        }
```

 **def getPointAndIndexByDist(self, dist: double, outPoint: PySide2.QtCore.QPointF, outIndex: int, unit: Tess.UnitOfMeasure) -> bool: ...**

获取车道中心线起点下游dist距离处的点及其所属分段序号; 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点  
[ out ] outIndex: 中心线起点向前延伸dist距离后所在分段序号  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  


举例: 

```python
# 获取ILane的中心线上一点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks: 
    lLanes = link.lanes()
    for lane in lLanes: 
        outPoint = QPointF()
        outIndex = 0
        lDistToStartPoint = lane.getPointAndIndexByDist(50, outPoint, outIndex)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道中心线起点下游dist距离处的点为{outPoint}, 分段序号为{outIndex}")
        lDistToStartPointMeter = lane.getPointAndIndexByDist(50, outPoint, outIndex, UnitOfMeasure.Metric)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道中心线起点下游dist距离处的点为{outPoint}, 分段序号为{outIndex}")
```

 **def getPointByDist(self, dist: double, outPoint: PySide2.QtCore.QPointF, unit: Tess.UnitOfMeasure) -> bool: ...**

获取车道中心线起点下游dist距离处的点; 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向前延伸的距离
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
# 获取ILane的中心线上一点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks: 
    lLanes = link.lanes()
    for lane in lLanes: 
        outPoint = QPointF()
        lDistToStartPoint = lane.getPointByDist(50, outPoint)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道中心线起点下游dist距离处的点为{outPoint}")
        lDistToStartPointMeter = lane.getPointByDist(50, outPoint, UnitOfMeasure.Metric)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道中心线起点下游dist距离处的点为{outPoint}")
```

 **void setOtherAttr(JsonObject attr)**

设置车道的其它属性, 方便用户拓展车道属性; 类型: 字典形式

举例: 

```python
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

 **void setLaneType(String type)**

设置车道的类型; 车道类型常量范围: "机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

参数: 

[ in ] type: 车道类型, 选下列几种类型其中一种: "机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

举例: 

```python
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

 **Vector<Point> polygon()**

获取车道的多边型轮廓顶点, 默认单位: 像素

举例: 

```python
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

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showLaneAttr(netiface)
def showLaneAttr(netiface): 
    link = netiface.links()[0]
    lane = link.lanes()[0]
    for i in link.lanes(): 
        if i.number() == 0: 
            lane = i
            break
    print(f"===link中的第一个lane(最右侧) id={lane.id()}, 类型gtype={lane.gtype()}, "
          f"是否为lane={lane.isLane()}, 所属link={lane.link()}, 所属section={lane.section()}, 长度length（像素制）={lane.length()}, 米制={lane.length(UnitOfMeasure.Metric)}, "
          f"宽度width（像素制）={lane.width()}, 米制={lane.width(UnitOfMeasure.Metric)}, 车道序号number={lane.number()}, 行为类型={lane.actionType()}"
          f"fromLaneObject={lane.fromLaneObject()}, toLaneObject={lane.toLaneObject()}, "
          f"centerBreakPoints(像素制)={lane.centerBreakPoints()}, centerBreakPoints(米制)={lane.centerBreakPoints(UnitOfMeasure.Metric)}, "
          f"leftBreakPoints(像素制)={lane.leftBreakPoints()}, leftBreakPoints(米制)={lane.leftBreakPoints(UnitOfMeasure.Metric)}, "
          f"rightBreakPoints(像素制)={lane.rightBreakPoints()}, rightBreakPoints(米制)={lane.rightBreakPoints(UnitOfMeasure.Metric)}, "
          f"centerBreakPoint3Ds(像素制)={lane.centerBreakPoint3Ds()}, centerBreakPoint3Ds(米制)={lane.centerBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"leftBreakPoint3Ds(像素制)={lane.leftBreakPoint3Ds()}, leftBreakPoint3Ds(米制)={lane.leftBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"rightBreakPoint3Ds(像素制)={lane.rightBreakPoint3Ds()}, rightBreakPoint3Ds(米制)={lane.rightBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"leftBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1], lane.leftBreakPoints()[-1])}, "
          f"leftBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1], lane.leftBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}, "
          f"rightBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1], lane.leftBreakPoints()[-1])}, "
          f"rightBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1], lane.leftBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}, "
          f"distToStartPoint(像素制)={lane.distToStartPoint(lane.centerBreakPoints()[0])}, distToStartPoint(米制)={lane.distToStartPoint(lane.centerBreakPoints(UnitOfMeasure.Metric)[0], UnitOfMeasure.Metric)}, "
          f"设置自定义属性setOtherAttr={lane.setOtherAttr({'newAttr': 'add a new attr'})}, setLaneType={lane.setLaneType('机动车道')}, action Type={lane.actionType()}"
          f"polygon={lane.polygon()}")
    outPoint = QPointF()
    outIndex = 0
    outPoint1 = QPointF()
    outIndex1 = 0
    lane.getPointAndIndexByDist(2.0, outPoint, outIndex)
    lane.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

    outPoint2 = QPointF()
    outPoint3 = QPointF()
    lane.getPointByDist(2.0, outPoint2)
    lane.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
    print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")

```





### 2.6. IConnector

连接段接口, 方法如下: 

 **int gtype()**

类型, 连接段类型为GConnectorType, GConnectorType是一种整数型常量。

举例: 

```python
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

 **int id()**

获取连接段ID; 因为连接段ID和路段ID是相互独立的, 所以可能两者的ID之间会有重复

举例: 

```python
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

 **double length()**

获取连接段长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取IConnector的长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的长度为" + connector.length());
        }
    }
}
```

 **double z()**

获取连接段高程, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取IConnector的高程
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的高程为" + connector.z());
        }
    }
}
```

 **double v3z()**

获取连接段高程, 过载自ISection的方法, 与z()方法作用相同, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取IConnector的高程
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnector
                List<IConnector> lConnectors = netiface.connectors();
                for (IConnector connector : lConnectors) {
                    System.out.println("id为" + connector.id() + "的连接段的高程为" + connector.v3z());
                }
            }
        }
```

 **String name()**

获取连接段名称

举例: 

```python
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

 **void setName(String name)**

设置连接段名称

举例: 

```python
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

 **ILink fromLink()**

获取当前connector的起始路段, 返回路段对象

举例: 

```python
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

 **ILink toLink()**

获取当前connector的目标路段（出口路段）, 返回路段对象

举例: 

```python
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

 **double limitSpeed()**

获取连接器的最高限速, 因为连接器没有最高限速这一属性, 因此该函数返回连接器的起始路段最高限速作为连接段的最高限速, 默认单位: 千米/小时, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取IConnector的最高限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的最高限速为" + connector.limitSpeed());
        }
    }
}
```

 **double minSpeed()**

获取连接器的最低限速, 因为连接器没有最低限速这一属性, 因此返回连接器起始路段的最低限速作为连接段的最低限速, 默认单位: 千米/小时, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 获取IConnector的最低限速
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IConnector
        List<IConnector> lConnectors = netiface.connectors();
        for (IConnector connector : lConnectors) {
            System.out.println("id为" + connector.id() + "的连接段的最低限速为" + connector.minSpeed());
        }
    }
}
```

 **ArrayList<ILaneConnector> laneConnectors()**

获取连接器下的所有“车道连接”对象, 列表形式, 列表元素为ILaneConnector对象

举例: 

```python
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

 **ArrayList<ILaneObject> laneObjects()**

车道及“车道连接”的接口列表

举例: 

```python
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

 **def setLaneConnectorOtherAtrrs(self, lAttrs: typing.Sequence) -> None: ...**

设置包含的“车道连接”其它属性

举例: 

```python
// 设置IConnector的其它属性
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

 **def setOtherAttr(self, otherAttr: typing.Dict) -> None: ...**

设置连接段其它属性

举例: 

```python
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

 **Vector<Point> polygon()**

获取连接段的多边型轮廓顶点

举例: 

```python
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

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showConnectorAttr(netiface)
def showConnectorAttr(netiface): 
    print(
        f"===场景中的connector个数（连接段对象）={len(netiface.connectors())}, {netiface.connectorCount()}, {len(netiface.connectorIds())}, "
        f"第一个connector的属性={netiface.connectors()[0].id()}")
    connector = netiface.connectors()[0]
    connector1 = netiface.findConnector(netiface.connectorIds()[0])
    print(type(connector), dir(connector))
    print(f"该connectors的属性: id(连接段和路段ID是独立的, 因此两者ID可能会重复)={connector.id()}, 类型gtype={connector.gtype()}, "
          f"name={connector.name()}, setName={connector.setName('new connector')} "
          f"长度length（像素制）={connector.length()}, 米制={connector.length(UnitOfMeasure.Metric)}, "
          f"高程={connector.z()}, toLaneObject={connector.v3z()}, "
          f"fromLink={connector.fromLink()}, toLink={connector.toLink()}, fromSection={connector.fromSection(id=0)}, toSection={connector.toSection(id=0)}, "
          f"最高限速(像素制)={connector.limitSpeed()}, 最高限速(米制)={connector.limitSpeed(UnitOfMeasure.Metric)}, "
          f"最低限速(像素制)={connector.minSpeed()}, 最低限速(米制)={connector.minSpeed(UnitOfMeasure.Metric)}, "
          f"laneConnectors={connector.laneConnectors()}, laneObjects={connector.laneObjects()}, "
          f"设置自定义属性setLaneConnectorOtherAtrrs={connector.setLaneConnectorOtherAtrrs([{'newAttr': i} for i in range(len(connector.laneConnectors()))])}, "
          f"设置自定义属性setOtherAttr={connector.setOtherAttr({'newAttr': 'add a new attr'})}, "
          f"polygon={connector.polygon()}")
```





### 2.7. ILaneConnector

“车道连接”接口, 方法如下: 

 **int gtype()**

类型, GLaneType或GLaneConnectorType, 车道连接段为GLaneConnectorType , 这里的返回值只可能是GLaneConnectorType

举例: 

```python
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

 **int id()**

获取车道连接ID

举例: 

```python
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

 **IConnector connector()**

获取车道连接所属的连接段Connector对象, 返回类型IConnector

举例: 

```python
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

 **ISection section()**

获取车道所属Section, Section为 IConnector的父类

举例: 

```python
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

 **ILane fromLane()**

获取当前车道链接的上游车道对象

举例: 

```python
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

 **ILane toLane()**

获取当前车道链接的下游车道对象

举例: 

```python
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

 **double length()**

获取“车道连接”的长度, 是指中心线的长度, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                    }
                }
            }
        }
```

 **ArrayList<Point> centerBreakPoints()**

获取“车道连接”的中心线断点集, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point> leftBreakPoints()**

获取“车道连接”左侧线断点集, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point> rightBreakPoints()**

获取“车道连接”右侧线断点集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point3D> centerBreakPoint3Ds()**

获取“车道连接”中心线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreakPoint3Ds()**

获取“车道连接”左侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreakPoint3Ds()**

获取“车道连接”右侧线断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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

                    }
                }
            }
        }
```

 **ArrayList<Point3D> leftBreak3DsPartly(Point fromPoint, Point toPoint)**

根据指定的起终止点获取“车道连接”左侧部分断点(三维)集, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] fromPoint: 中心线上某一点作为起点
[ in ] toPoint: 中心线上某一点作为终点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                        }

                    }
                }
            }
        }
```

 **ArrayList<Point3D> rightBreak3DsPartly(Point fromPoint, Point toPoint)**

根据指定的起终止点获取“车道连接”右侧部分断点(三维)集, 默认单位: 像素, 高程Z默认单位: 像素

参数: 
[ in ] fromPoint: 中心线上某一点作为起点
[ in ] toPoint: 中心线上某一点作为终点
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制 

举例: 

```python
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
                }

            }
        }
    }
}
```

 **double distToStartPoint(Point p)**

计算车道链接中心线上任意点到起点的距离, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
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
                }

            }
        }
    }
}
```

 **double distToStartPointWithSegmIndex(Point p, int segmIndex)**

计算中心线上任意点到起点的距离, 附加条件是该点所在车道上的分段序号, 默认单位为像素; 可通过unit参数设置单位  

参数: 
[ in ] p: 当前中心线上该点坐标, 像素坐标   
[ in ] segmIndex: 该点所在车道上的分段序号  
[ in ] bOnCentLine: 是否在中心线上  
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  
注: 如传入米制参数, 请勿遗忘传入segmIndex与bOnCentLine参数。

举例: 

```python
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
                        }

                    }
                }
            }
        }
```

 **def getPointAndIndexByDist(self, dist: double, outPoint: PySide2.QtCore.QPointF, outIndex: int, unit: Tess.UnitOfMeasure) -> bool: ...**

求中心线起点下游dist距离处的点及分段序号, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 中心线起点向前延伸的距离, 像素单位  
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点, 默认单位: 像素  
[ out ] outIndex: 中心线起点向前延伸dist距离后所在分段序号
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  


举例: 

```python
# 求中心线起点下游dist距离处的点及分段序号
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors: 
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors: 
        outPoint = QPointF()
        outIndex = 0
        if laneConnector.getPointAndIndexByDist(50, outPoint, outIndex): 
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点为{outPoint}, 分段序号为{outIndex}")
        if laneConnector.getPointAndIndexByDist(50, outPoint, outIndex, UnitOfMeasure.Metric): 
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点(米制单位)为{outPoint}, 分段序号为{outIndex}")
```



 **boolean getPointByDist(double dist, Point outPoint)**

求中心线起始点下游dist距离处的点, 如果目标点不在中心线上返回False, 否则返回True, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ] dist: 中心线起点向前延伸的距离, 像素单位  
[ out ] outPoint: 中心线起点向前延伸dist距离后所在点, 默认单位: 像素 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制  

举例: 

```python
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

                    }
                }
            }
        }
```



 **void setOtherAttr(JsonObject attr)**

设置车道连接其它属性, 方便二次开发过程中使用

举例: 

```python
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

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showLaneConnectorAttr(netiface)
def showLaneConnectorAttr(self, netiface): 
    connector = netiface.findConnector(netiface.connectorIds()[0])
    laneConnector = connector.laneConnectors()[0]
    print(f"===laneConnector id={laneConnector.id()}, 类型gtype={laneConnector.gtype()}, "
          f"其所属的连接段={laneConnector.connector()}, 所属section={laneConnector.section()}, "
          f"fromLane={laneConnector.fromLane()}, toLane={laneConnector.toLane()}, "
          f"fromLaneObject={laneConnector.fromLaneObject()}, toLaneObject={laneConnector.toLaneObject()}, "
          f"长度length（像素制）={laneConnector.length()}, 米制={laneConnector.length(UnitOfMeasure.Metric)}, "
          f"centerBreakPoints(像素制)={laneConnector.centerBreakPoints()}, centerBreakPoints(米制)={laneConnector.centerBreakPoints(UnitOfMeasure.Metric)}, "
          f"leftBreakPoints(像素制)={laneConnector.leftBreakPoints()}, leftBreakPoints(米制)={laneConnector.leftBreakPoints(UnitOfMeasure.Metric)}, "
          f"rightBreakPoints(像素制)={laneConnector.rightBreakPoints()}, rightBreakPoints(米制)={laneConnector.rightBreakPoints(UnitOfMeasure.Metric)}, "
          f"centerBreakPoint3Ds(像素制)={laneConnector.centerBreakPoint3Ds()}, centerBreakPoint3Ds(米制)={laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"leftBreakPoint3Ds(像素制)={laneConnector.leftBreakPoint3Ds()}, leftBreakPoint3Ds(米制)={laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"rightBreakPoint3Ds(像素制)={laneConnector.rightBreakPoint3Ds()}, rightBreakPoint3Ds(米制)={laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric)}, "
          f"leftBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1], laneConnector.leftBreakPoints()[-1])}, "
          f"leftBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1], laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}, "
          f"rightBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1], laneConnector.leftBreakPoints()[-1])}, "
          f"rightBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1], laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}, "
          f"distToStartPoint(像素制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints()[0])}, distToStartPoint(米制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints(UnitOfMeasure.Metric)[0], UnitOfMeasure.Metric)}, "
          f"设置自定义属性setOtherAttr={laneConnector.setOtherAttr({'newAttr': 'add a new attr'})}")
    outPoint = QPointF()
    outIndex = 0
    outPoint1 = QPointF()
    outIndex1 = 0
    laneConnector.getPointAndIndexByDist(2.0, outPoint, outIndex)
    laneConnector.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

    outPoint2 = QPointF()
    outPoint3 = QPointF()
    laneConnector.getPointByDist(2.0, outPoint2)
    laneConnector.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
    print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")


    dist = laneConnector.distToStartPointWithSegmIndex(outPoint, outIndex)
    dist1 = laneConnector.distToStartPointWithSegmIndex(outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"distToStartPointWithSegmIndex(像素制)={dist}, distToStartPointWithSegmIndex(米制)={dist1}")

```





### 2.8. IConnectorArea

面域接口, 方法如下: 

 **int id()**

获取面域ID, 面域是指若干Connector重叠形成的区域

举例: 

```python
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

 **ArrayList<IConnector> allConnector()**

获取当前面域包含的所有连接段, 返回类型列表, 元素为IConnector对象

举例: 

```python
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

 **Point centerPoint()**

获取面域中心点, 默认单位: 像素; 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

 举例: 

```python
// 获取面域中心点
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IConnectorArea
                List<IConnectorArea> lConnectorAreas = netiface.allConnectorArea();
                for (IConnectorArea connectorArea : lConnectorAreas) {
                    System.out.println("id为" + connectorArea.id() + "的面域中心点为" + connectorArea.centerPoint());
                }
            }
        }
```

**案例代码**

```python
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

 **int id()**

获取发车点ID

举例: 

```python
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

 **String name()**

获取发车名称

举例: 

```python
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

 **ILink link()**

获取发车点所在路段

举例: 

```python
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

 **int addDispatchInterval(int vehiCompId, int interval, int vehiCount)**

为发车点增加发点间隔

参数: 
[ in ] vehiCompId: 车型组成ID
[ in ] interval: 时间段, 单位: 秒
[ in ] vehiCount: 发车数

返回值: 

返回发车间隔ID

举例: 

```python
// 新建发车点, 车型组成ID为动态创建的, 600秒发300辆车

    if (link != null) {
        IDispatchPoint dp = netiface.createDispatchPoint(link );
        if (dp != null) {
            // vehiCompositionID为动态创建的车型组成ID
            dp.addDispatchInterval(vehiCompositionID, 600, 300);
        }
    }

```

 **Vector<Point> polygon()**

获取发车点多边型轮廓

举例: 

```python
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

```python
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

 **int id()**

获取决策点ID

举例: 

```python
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

 **String name( )**

获取决策点名称

举例: 

```python
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

 **ILink link()**

获取决策点所在路段

举例: 

```python
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

 **double distance()**

获取距路段起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取距路段起点距离
        TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                // 获取路网中的所有IDecisionPoint
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    System.out.println("决策点" + decisionPoint.id() + "距路段起点距离=" + decisionPoint.distance());
                }
            }
        }
```

 **ArrayList<IRouting> routings()**

获取决策点控制的所有决策路径, 返回类型列表, 元素为IRouting对象

举例: 

```python
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

 **Vector<Point> polygon()**

获取决策点多边型轮廓

举例: 

```python
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

```python
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

 **int id()**

获取路径ID

举例: 

```python
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

 **double calcuLength()**

计算路径长度, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
TessInterface iface = TESSNG.tessngIFace();
        if (iface != null) {
            NetInterface netiface = iface.netInterface();
            if (netiface != null) {
                List<IDecisionPoint> lDecisionPoints = netiface.decisionPoints();
                for (IDecisionPoint decisionPoint : lDecisionPoints) {
                    List<IRouting> lRoutings = decisionPoint.routings();
                    for (IRouting routing : lRoutings) {
                        System.out.println("决策点" + decisionPoint.id() + "的决策路径" + routing.id() + "长度=" + routing.calcuLength());
                    }
                }
            }
        }
```

 **ArrayList<ILink> getLinks()**

获取当前路径的路段序列, 不包含连接段

举例: 

```python
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

 **int deciPointId()**

获取当前路径所属的决策点ID

举例: 

```python
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

 **boolean contain(ISection pRoad)**

判定道路是否在当前路径上, 入参需是ISection对象

参数：
[ in ] pRoad: 路段或连接段

举例: 

```python
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

 **ISection nextRoad(ISection pRoad)**

根据当前路径, 获取所给道路的下一条道路, 返回类型为ISection, 即下一条路段可能是Link也可能是Connector

参数: 

[ in ] pRoad: 路段或连接段

举例: 

```python
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

```python
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

 **def id(self) -> int: ...**

获取信控机ID 

举例: 

```python
# 获取信控机ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    print(f"信控机ID为{signalController.id()}")
```

 **def name(self) -> str: ...**

获取信控机名称

举例: 

```python
# 获取信控机名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers:
    print(f"信控机{signalController.id()}名称={signalController.name()}")
```

 **def setName(name: str) -> None: ...**

设置信控机名称  

参数: 
[ in ] name: 信号机名称

举例: 

```python
# 设置信控机名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    print(f"设置信控机{signalController.id()}名称, 返回值={signalController.setName('new_'+signalController.name())}")
    print(f"获取信控机{signalController.id()}名称={signalController.name()}")
```

 **def addPlan(plan: Tessng.ISignalPlan) -> None: ...**

 为信号机添加信控方案  

参数: 
[ in ] plan : 信控方案, 可循环调用设置多时段信控方案

举例: 

```python
# 为信号机添加信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    print(f"移除/删除信号机{signalController.id()}的信控方案")
    signalController.removePlan(plans[0])
    print(f"为信号机{signalController.id()}添加信控方案")
    signalController.addPlan(plans[0])
```

 **def removePlan(plan: Tessng.ISignalPlan) -> None: ...**

 移除/删除信号机的信控方案  

参数: 
[ in ] plan : 信控方案, 

举例: 

```python
# 移除/删除信号机的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    print(f"移除/删除信号机{signalController.id()}的信控方案")
    signalController.removePlan(plans[0])
```

 **def IPlans(self) -> typing.List<Tessng.ISignalPlan>: ...**

 获取当前信号机中所有的信控方案  

举例: 

```python
# 获取当前信号机中所有的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    print(f"获取信号机{signalController.id()}中所有的信控方案={signalController.IPlans()}")
```


**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
# 创建信控方案
signalPlan = netiface.createSignalPlan(signalController, "早高峰", 150, 50, 0, 1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿", 50)
yellow = Online.ColorInterval("黄", 3)
red = Online.ColorInterval("红", 97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor)
we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)
w_e_straight_lamps = []
for lane in w_approach.lanes(): 
    if lane.number()< w_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
        w_e_straight_lamps.append(signalLamp)
for lane in e_approach.lanes(): 
    if lane.number()< e_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
        w_e_straight_lamps.append(signalLamp)

signalController = netiface.createSignalController("交叉口1")
showSignalControllerAttr(netiface)
def showSignalControllerAttr(netiface): 
    controllers = netiface.signalControllers()
    controllerCount = netiface.signalControllerCount()
    signalControllerIds = netiface.signalControllerIds()
    controller = netiface.findSignalControllerById(signalControllerIds[0])
    controller = netiface.findSignalControllerByName(controllers[0].name())
    print(f"路网中的信号机总数={controllerCount}, 所有的信号机id列表={signalControllerIds}, 信号机编号={signalControllerIds[0]}的具体信息: "
          f"编号={controller.id()}, 名称={controller.name()}, 设置新名字={controller.setName('new_'+controller.name())}, "
          f"获取信号机的信控方案={controller.IPlans()}")
    IPlans = controller.IPlans()
    print(f"移除/删除信号机的信控方案={controller.removePlan(IPlans[0])}")
    print(f"为信号机添加信控方案, 添加回原有信控方案={controller.addPlan(IPlans[0])}")
    print(f"信号机当前信控方案={controller.IPlans()}")
```



### 2.13. ISignalPlan

信号控制方案接口

 **def id(self) -> int: ...**

获取信控方案ID 

举例: 

```python
# 获取当前信号机中所有的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案ID={signalPlan.id()}")
```

 **def name(self) -> str: ...**

获取信控方案名称（V3版本的信号灯组名称） 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案名称={signalPlan.name()}")
```

 **def trafficName(self) -> str: ...**

获取信号机名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信号机名称={signalPlan.trafficName()}")
```

 **def cycleTime(self) -> int: ...**

获取获取信号周期, 单位: 秒 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案周期={signalPlan.cycleTime()}")
```

 **def fromTime(self) -> int: ...**

获取信控方案起始时间, 单位: 秒 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案起始时间={signalPlan.fromTime()}")
```

 **def toTime(self) -> int: ...**

获取信控方案结束时间, 单位: 秒 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案结束时间={signalPlan.toTime()}")
```

 **def phases(self) -> typing.List<Tessng.ISignalPhase>: ...**

获取信控方案中的相位列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"信控方案中的相位列表={signalPlan.phases()}")
```

 **def setName( name: str) -> None: ...**

设置信控方案（V3版本的信号灯组）名称 

参数: 

[ in ] name: 信控方案名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"设置信控方案名称={signalPlan.setName('new_'+signalPlan.name())}")
        print(f"获取信控方案名称={signalPlan.name()}")
```

 **def setCycleTime(period: int) -> None: ...**

设置信控方案（V3版本的信号灯组）的信号周期, 单位: 秒 

参数: 

[ in ] period: 信号周期, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"设置信控方案周期={signalPlan.setCycleTime(100)}")
        print(f"获取信控方案周期={signalPlan.cycleTime()}")
```

 **def setFromTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的起始时间, 单位: 秒 

参数: 

[ in ] time: 起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"设置信控方案起始时间={signalPlan.setFromTime(100)}")
        print(f"获取信控方案起始时间={signalPlan.fromTime()}")
```

 **def setToTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的结束时间, 单位: 秒

参数: 

[ in ] time: 结束时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        print(f"设置信控方案结束时间={signalPlan.setToTime(100)}")
        print(f"获取信控方案结束时间={signalPlan.toTime()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
# 创建信控方案
signalPlan = netiface.createSignalPlan(signalController, "早高峰", 150, 50, 0, 1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿", 50)
yellow = Online.ColorInterval("黄", 3)
red = Online.ColorInterval("红", 97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor)
we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)
showSignalPlanAttr(netiface)
def showSignalPlanAttr(netiface): 
    signalPlans = netiface.signalPlans()
    signalPlanCount = netiface.signalPlanCount()
    signalPlanIds = netiface.signalPlanIds()
    signalPlan = netiface.findSignalPlanById(signalPlanIds[0])
    signalPlan = netiface.findSignalPlanByName(signalPlans[0].name())
    print(
        f"路网中的信控方案总数={signalPlanCount}, 所有信控方案列表={signalPlanIds}, 信控方案编号={signalPlanIds[0]}的具体信息: "
        f"编号={signalPlan.id()}, 名称={signalPlan.name()}, 所属信号机名称={signalPlan.trafficName()}, 设置新名字={signalPlan.setName('new_' + signalPlan.name())}, "
        f"获取信控方案信控周期={signalPlan.cycleTime()}, 开始时间-结束时间={signalPlan.fromTime()}-{signalPlan.toTime()}, "
        f"所有相位信息={signalPlan.phases()}")
```



### 2.14. ISignalPhase

信号灯相位, 接口方法: 

 **def id(self) -> int: ...**

获取当前相位的相位ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"相位ID={signalPhase.id()}")
```

 **def phaseName(self) -> str: ...**

获取当前相位的相位名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"相位名称={signalPhase.phaseName()}")
```

 **def signalLamps(self) -> typing.List: ...**

获取本相位下的信号灯列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"本相位下的信号灯列表={signalPhase.signalLamps()}")
```

 **def listColor(self) -> typing.List: ...**

获取本相位的相位灯色列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"本相位的相位灯色列表={signalPhase.listColor()}")
```

 **def setColorList(self, lColor: typing.List<Online.ColorInterval>) -> None: ...**

设置当前相位的信号灯色信息列表

参数: 

[ in ] lColor: 灯色时长信息, 包含信号灯颜色和信号灯色时长
举例: 

```python
#以L12路段相位直行信号灯相位为例（ID为7）, 由红90绿32黄3红25改为红10绿110黄3红28
if method_number == 3: 
    signalPhase_L12_7 = netiface.findSignalPhase(7)
    color_list = []  # 按照红灯、绿灯、黄灯、红灯顺序计算
    color_list.append(Online.ColorInterval('红', 10))
    color_list.append(Online.ColorInterval('绿', 110))
    color_list.append(Online.ColorInterval('黄', 3))
    color_list.append(Online.ColorInterval('红', 28))
    signalPhase_L12_7.setColorList(color_list)

```

 **def setPhaseName(self, name: str) -> None: ...**

设置当前相位名称

参数: 

[ in ] name: 相位名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"设置当前相位名称={signalPhase.setPhaseName('new_'+signalPhase.phaseName())}")
            print(f"获取当前相位名称={signalPhase.phaseName()}")
```

 **def cycleTime(self) -> int: ...**

获取相位周期, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"相位周期={signalPhase.cycleTime()}")
```

 **def phaseColor(self) -> Online.SignalPhaseColor: ...**

获取当前相位灯色, Online.SignalPhaseColor

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"当前相位灯色={signalPhase.phaseColor()}")
```

 **def signalPlan(self) -> Tess.ISignalPlan: ...**

获取相位所在信控方案

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalController
lSignalControllers = netiface.signalControllers()
for signalController in lSignalControllers: 
    plans = signalController.IPlans()
    for signalPlan in plans: 
        phases = signalPlan.phases()
        for signalPhase in phases: 
            print(f"相位所在信控方案={signalPhase.signalPlan()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
# 创建信控方案
signalPlan = netiface.creatSignalPlan(signalController, "早高峰", 150, 50, 0, 1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿", 50)
yellow = Online.ColorInterval("黄", 3)
red = Online.ColorInterval("红", 97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor)
we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)
showSignalPlanAttr(netiface)
def showSignalPlanAttr(netiface): 
    signalPlans = netiface.signalPlans()
    signalPlanCount = netiface.signalPlanCount()
    signalPlanIds = netiface.signalPlanIds()
    signalPlan = netiface.findSignalPlanById(signalPlanIds[0])
    signalPlan = netiface.findSignalPlanByName(signalPlans[0].name())
    print(
        f"路网中的信控方案总数={signalPlanCount}, 所有信控方案列表={signalPlanIds}, 信控方案编号={signalPlanIds[0]}的具体信息: "
        f"编号={signalPlan.id()}, 名称={signalPlan.name()}, 所属信号机名称={signalPlan.trafficName()}, 设置新名字={signalPlan.setName('new_' + signalPlan.name())}, "
        f"获取信控方案信控周期={signalPlan.cycleTime()}, 开始时间-结束时间={signalPlan.fromTime()}-{signalPlan.toTime()}, "
        f"所有相位信息={signalPlan.phases()}")
showSignalPhaseAttr(netiface)

def showSignalPhaseAttr(netiface): 
    signalPlans = netiface.signalPlans()
    signalPlan = netiface.findSignalPlanById(signalPlans[0].name())
    signalPhases = signalPlan.phases()
    signalPhase = signalPhases[0]
    print(
        f"信控方案={signalPlans[0].name()}, 的所有相位列表={signalPhases}, 第一相位={signalPhase.id()}的具体信息: "
        f"编号={signalPhase.id()}, 名称={signalPhase.phaseName()}, 获取本相位下的信号灯列表={signalPhase.signalLamps()}, "
        f"获取本相位的相位灯色列表={signalPhase.listColor()}, "
        f"相位周期, 单位: 秒={signalPhase.cycleTime()}, 当前相位灯色, Online.SignalPhaseColor={signalPhase.phaseColor()}, "
        f"所在信控方案={signalPhase.signalPlan()}")
```





### 2.15. ISignalLamp

信号灯接口, 方法如下: 

 **int id()**

获取信号灯ID

举例: 

```python
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

 **String color()**

获取信号灯当前信号灯色, "R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例: 

```python
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

 **String name()**

获取信号灯名称

举例: 

```python
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

 **void setName(String name)**

设置信号灯名称

举例: 

```python
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

 **void setLampColor(String colorStr)**

设置信号灯颜色

参数: 

[ in ] colorStr: 字符串表达的颜色, 有四种可选, 支持汉字: "红"、"绿"、"黄"、"灰", 也支持字符: "R"、"G"、"Y"、"gray"。

举例: 

```python
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

 **ISignalPhase signalPhase()**

获取当前信号灯所在的相位

举例: 

```python
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

 **void setSignalPhase(ISignalPhase pPhase)**

设置信号灯相位

参数: 

[ in ] signalPhase: 信号灯相位

举例: 

```python
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

 **ISignalPlan signalPlan()**

获取当前信号灯所在的灯组, 这里灯组类似于一个信号机种的某个信控方案

举例: 

```python
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

 **void setDistToStart(double dist)**

设置信号灯距路段起点距离, 默认单位: 像素, 可通过unit参数设置单位  

参数: 
[ in ] dist: 距离值  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
// 设置并获取信号灯距路段起点距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有ISignalLamp
        List<ISignalLamp> lSignalLamps = netiface.signalLamps();
        for (ISignalLamp signalLamp : lSignalLamps) {
            // 处理void方法：先执行设置操作
            signalLamp.setDistToStart(100);
            System.out.println("设置信号灯距路段起点距离=执行成功");
            System.out.println("获取信号灯距路段起点距离=" + signalLamp);
        }
    }
}
```

 **ILaneObject laneObject()**

获取所在车道或车道连接

举例: 

```python
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

 **Vector<Point> polygon()**

获取信号灯多边型轮廓

举例: 

```python
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

**double angle()**

获取信号灯角度, 正北为0顺时针方向

举例: 

```python
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

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
# 创建信控方案
signalPlan = netiface.creatSignalPlan(signalController, "早高峰", 150, 50, 0, 1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿", 50)
yellow = Online.ColorInterval("黄", 3)
red = Online.ColorInterval("红", 97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行", w_e_straight_phasecolor)
we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)

red = Online.ColorInterval("红", 53)
green = Online.ColorInterval("绿", 30)
yellow = Online.ColorInterval("黄", 3)
red1 = Online.ColorInterval("红", 64)
w_e_left_phasecolor = [red, green, yellow, red1]
w_e_left_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西左转", w_e_left_phasecolor)


red = Online.ColorInterval("红", 86)
green = Online.ColorInterval("绿", 30)
yellow = Online.ColorInterval("黄", 3)
red1 = Online.ColorInterval("红", 31)
s_n_straight_phasecolor = [red, green, yellow, red1]
s_n_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行", s_n_straight_phasecolor)
ns_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北直行行人", s_n_straight_phasecolor)

red = Online.ColorInterval("红", 119)
green = Online.ColorInterval("绿", 29)
yellow = Online.ColorInterval("黄", 3)
s_n_left_phasecolor = [red, green, yellow]
s_n_left_phase = netiface.createSignalPlanSignalPhase(signalPlan, "南北左转", s_n_left_phasecolor)

# 创建机动车信号灯
w_e_straight_lamps = []
for lane in w_approach.lanes(): 
    if lane.number()< w_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
        w_e_straight_lamps.append(signalLamp)
for lane in e_approach.lanes(): 
    if lane.number()< e_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(w_e_straight_phase, "东西直行信号灯", lane.id(), -1, lane.length()-0.5)
        w_e_straight_lamps.append(signalLamp)


w_e_left_lamps = []
for lane in w_approach.lanes(): 
    if lane.number()== w_approach.laneCount()-1: 
        signalLamp = netiface.createSignalLamp(w_e_left_phase, "东西左转信号灯", lane.id(), -1, 
                                               lane.length() - 0.5)
        w_e_left_lamps.append(signalLamp)
for lane in e_approach.lanes(): 
    if lane.number()== e_approach.laneCount()-1: 
        signalLamp = netiface.createSignalLamp(w_e_left_phase, "东西左转信号灯", lane.id(), -1, 
                                               lane.length() - 0.5)
        w_e_left_lamps.append(signalLamp)

n_s_straight_lamps = []
for lane in n_approach.lanes(): 
    if lane.number()< n_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(s_n_straight_phase, "南北直行信号灯", lane.id(), -1, 
                                               lane.length() - 0.5)
        n_s_straight_lamps.append(signalLamp)
for lane in s_approach.lanes(): 
    if lane.number()< s_approach.laneCount()-1 and lane.number()>0: 
        signalLamp = netiface.createSignalLamp(s_n_straight_phase, "南北直行信号灯", lane.id(), -1, 
                                               lane.length() - 0.5)
        n_s_straight_lamps.append(signalLamp)

n_s_left_lamps = []
for lane in n_approach.lanes(): 
    if lane.number()== n_approach.laneCount()-1: 
        signalLamp = netiface.createSignalLamp(s_n_left_phase, "南北左转信号灯", lane.id(), -1, 
                                               lane.length() - 0.5)
        n_s_left_lamps.append(signalLamp)
for lane in s_approach.lanes(): 
        if lane.number()== s_approach.laneCount()-1: 
            signalLamp = netiface.createSignalLamp(s_n_left_phase, "南北左转信号灯", lane.id(), -1, 
                                                   lane.length() - 0.5)
            n_s_left_lamps.append(signalLamp)


# 创建行人信号灯, 并行人信号灯关联相位
signalLamp1_positive = netiface.createCrossWalkSignalLamp(signalController, "南斑马线信号灯", s_crosswalk.getId() , QPointF(13, 22), True)
signalLamp1_negetive = netiface.createCrossWalkSignalLamp(signalController, "南斑马线信号灯", s_crosswalk.getId() , QPointF(-13, 22), False)
signalLamp1_positive.setSignalPhase(we_ped_phase)
signalLamp1_negetive.setSignalPhase(we_ped_phase)
# netiface.addSignalPhaseToLamp(we_ped_phase.id(), signalLamp1_positive)
# netiface.addSignalPhaseToLamp(we_ped_phase.id(), signalLamp1_negetive)

signalLamp2_positive = netiface.createCrossWalkSignalLamp(signalController, "北斑马线信号灯", n_crosswalk.getId() , QPointF(13, -22), True)
signalLamp2_negetive = netiface.createCrossWalkSignalLamp(signalController, "北斑马线信号灯", n_crosswalk.getId(), QPointF(-13, -22), False)
signalLamp2_positive.setSignalPhase(we_ped_phase)
signalLamp2_negetive.setSignalPhase(we_ped_phase)
# netiface.addSignalPhaseToLamp(we_ped_phase.id(), signalLamp2_positive)
# netiface.addSignalPhaseToLamp(we_ped_phase.id(), signalLamp2_negetive)

signalLamp3_positive = netiface.createCrossWalkSignalLamp(signalController, "东斑马线信号灯", e_crosswalk.getId() , QPointF(22, -13), True)
signalLamp3_negetive = netiface.createCrossWalkSignalLamp(signalController, "东斑马线信号灯", e_crosswalk.getId() , QPointF(22, 13), False)
signalLamp3_positive.setSignalPhase(ns_ped_phase)
signalLamp3_negetive.setSignalPhase(ns_ped_phase)
# netiface.addSignalPhaseToLamp(ns_ped_phase.id(), signalLamp3_positive)
# netiface.addSignalPhaseToLamp(ns_ped_phase.id(), signalLamp3_negetive)

signalLamp4_positive = netiface.createCrossWalkSignalLamp(signalController, "西斑马线信号灯", w_crosswalk.getId() , QPointF(-22, -13), True)
signalLamp4_negetive = netiface.createCrossWalkSignalLamp(signalController, "西斑马线信号灯", w_crosswalk.getId() , QPointF(-22, 13), False)
signalLamp4_positive.setSignalPhase(ns_ped_phase)
signalLamp4_negetive.setSignalPhase(ns_ped_phase)
# netiface.addSignalPhaseToLamp(ns_ped_phase.id(), signalLamp4_positive)
# netiface.addSignalPhaseToLamp(ns_ped_phase.id(), signalLamp4_negetive)
        
showSignalLampAttr(netiface)
    
def showSignalLampAttr(netiface): 
    signalLampCount = netiface.signalLampCount()
    signalLampIds = netiface.signalLampIds()
    signalLamps = netiface.signalLamps()
    signalLamp = netiface.findSignalLamp(signalLampIds[0])

    print(
        f"机动车信号灯总数={signalLampCount}, 编号列表={signalLampIds}, {signalLamp.id()}的具体信息: "
        f"编号={signalLamp.id()}, 获取信号灯当前信号灯色={signalLamp.color()}, 名称={signalLamp.name()}, "
        f"设置信号灯名称={signalLamp.setName('new_' + signalLamp.name())}, "
        f"获取当前信号灯所在的相位={signalLamp.signalPhase()}, 获取当前信号灯所在的灯组={signalLamp.signalPlan()}, "
        f"获取所在车道或车道连接={signalLamp.laneObject()}, 获取信号灯多边型轮廓={signalLamp.polygon()}, "
        f"获取信号灯角度, 正北为0顺时针方向={signalLamp.angle()}")
```



### 2.16. IBusLine

公交线路接口, 接口方法: 

 **int id()**

获取当前公交线路的ID

举例: 

```python
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

 **String name()**

获取当前公交线路的名称

举例: 

```python
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

 **double length()**

获取当前公交线路长度, 默认单位: 像素, 可通过unit参数设置单位 

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取公交线路长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路长度=" + busLine.length());
        }
    }
}
```

 **int dispatchFreq()**

获取当前公交线路的发车间隔, 单位: 秒

举例: 

```python
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

 **int dispatchStartTime()**

获取当前公交线路的发车开始时间, 单位: 秒

举例: 

```python
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

 **int dispatchEndTime()**

获取当前公交线路的发车结束时间, 单位: 秒, 即当前线路的公交调度表的结束时刻

举例: 

```python
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

 **double desirSpeed()**

获取当前公交线路的期望速度, 默认单位: 像素/秒, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取公交线路期望速度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("公交线路期望速度=" + busLine.desirSpeed());
        }
    }
}
```

 **int passCountAtStartTime()**

公交线路中公交车的起始载客人数

举例: 

```python
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

 **ArrayList<ILink> links()**

获取公交线路经过的路段序列

举例: 

```python
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

 **ArrayList<IBusStation> stations()**

获取公交线路上的所有站点 

举例: 

```python
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

 **SWIGTYPE_p_QListT_IBusStationLine_p_t stationLines()**

公交站点线路, 当前线路相关站点的上下客等参数, 所有参数的列表

举例: 

```python
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

 **void setName(String name)**

设置当前公交线路的名称

举例: 

```python
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

 **void setDispatchFreq(int freq)**

设置当前公交线路的发车间隔, 单位: 秒

参数: 
[ in ] freq: 发车间隔, 单位: 秒

举例: 

```python
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

 **void setDispatchStartTime(int startTime)**

设置当前公交线路上的公交首班车辆的开始发车时间

参数: 
[ in ] startTime: 开始发车时间, 单位: 秒

举例: 

```python
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

 **void setDispatchEndTime(int endTime)**

设置当前公交线路上的公交末班车的发车时间

参数: 
[ in ] endTime: 结束发车时间, 单位: 秒

举例: 

```python
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

 **void setDesirSpeed(double desirSpeed)**

设置当前公交线路的期望速度, 默认单位：像素, 可通过unit参数设置单位

参数: 
[ in ]  desirSpeed: 期望速度, 默认单位: 像素/秒 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  

举例: 

```python
// 设置并获取公交线路期望速度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusLine
        List<IBusLine> lBusLines = netiface.buslines();
        for (IBusLine busLine : lBusLines) {
            System.out.println("设置当前公交线路期望速度(米制)=" + "40");
            busLine.setDesirSpeed(40);
            System.out.println("获取当前公交线路期望速度=" + busLine.desirSpeed());
            System.out.println("获取当前公交线路期望速度(米制)=" + busLine.desirSpeed());
        }
    }
}
```

 **void setPassCountAtStartTime(int count)**

设置当前公交线路的起始载客人数

举例: 

```python
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

```python
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

 **int id()**

获取当前公交站点ID

举例: 

```python
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

 **String name()**

获取当前公交站点名称

举例: 

```python
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

 **int laneNumber()**

获取当前公交站点所在车道序号

举例: 

```python
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

 **double x()**

获取当前公交站点的中心点的位置, X坐标, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取当前公交站点的中心点X坐标
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的中心点的位置, X坐标=" + busStation.x());
        }
    }
}
```

 **double y()**

获取当前公交站点的中心点的位置, Y坐标, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取当前公交站点的中心点X坐标
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的中心点的位置, Y坐标=" + busStation.y());
        }
    }
}
```

 **double length()**

获取当前公交站点的长度, 默认单位: 像素, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取当前公交站点的长度
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的长度(像素)=" + busStation.length());
        }
    }
}
```

 **int stationType()**

获取当前公交站点的类型: 站点类型 1: 路边式、2: 港湾式

举例: 

```python
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

 **ILink link()**

获取当前公交站点所在路段

举例: 

```python
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

 **ILane lane()**

获取当前公交站点所在车道

举例: 

```python
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

 **double distance()**

获取当前公交站点的起始位置距路段起点的距离, 默认单位: 像素, 可通过unit参数设置单位 
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
// 获取当前公交站点的起始位置距路段起点的距离
TessInterface iface = TESSNG.tessngIFace();
if (iface != null) {
    NetInterface netiface = iface.netInterface();
    if (netiface != null) {
        // 获取路网中的所有IBusStation
        List<IBusStation> lBusStations = netiface.busStations();
        for (IBusStation busStation : lBusStations) {
            System.out.println("获取当前公交站点的起始位置距路段起点的距离(像素)=" + busStation.distance());
        }
    }
}
```

 **void setName(String name)**

设置当前公交站点名称

参数: 
[ in ] name: 新名称

举例: 

```python
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

 **void setDistToStart(double dist)**

设置站点起始点距车道起点距离, 默认单位: 像素, 可通过unit参数设置单位 
参数: 
[ in ] dist: 距车道起点距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例: 

```python
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
        }
    }
}
```

 **void setLength(double length)**

设置当前公交站点的长度, 默认单位: 像素, 可通过unit参数设置单位  
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
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
                }
            }
        }
```

 **void setType(int type)**

设置当前公交站点类型

参数: 

[ in ] type: 站点类型, 1 路侧式、2 港湾式

举例: 

```python
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

 **Vector<Point> polygon()**

获取 公交站点多边型轮廓的顶点

举例: 

```python
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

```python
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

 **def id(self) -> int: ...**

获取公交“站点-线路”ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取公交“站点-线路”ID={busStationLine.id()}")
```

 **def stationId(self) -> int: ...**

获取当前公交站点的ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交站点的ID={busStationLine.stationId()}")
```

 **def lineId(self) -> int: ...**

获取当前公交站台所属的公交线路ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交站台所属的公交线路ID={busStationLine.lineId()}")
```

 **def busParkingTime(self) -> int: ...**

获取当前公交线路下该站台的公交车辆停靠时间(秒)

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交线路下该站台的公交车辆停靠时间(秒)={busStationLine.busParkingTime()}")
```

 **def getOutPercent(self) -> double: ...**

获取当前公交线路下该站台的下客百分比

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交线路下该站台的下客百分比={busStationLine.getOutPercent()}")
```

 **def getOnTimePerPerson(self) -> double: ...**

获取当前公交线路下该站台下的平均每位乘客上车时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交线路下该站台下的平均每位乘客上车时间, 单位: 秒={busStationLine.getOnTimePerPerson()}")
```

 **def getOutTimePerPerson(self) -> double: ...**

获取当前公交线路下该站台下的平均每位乘客下车时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"获取当前公交线路下该站台下的平均每位乘客下车时间, 单位: 秒={busStationLine.getOutTimePerPerson()}")
```

 **def setBusParkingTime(self, time: int) -> None: ...**

设置当前公交线路下该站台下的车辆停靠时间(秒)

参数: 
[ in ] time: 车辆停靠时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"设置当前公交线路下的该站台下的车辆停靠时间(秒)={busStationLine.setBusParkingTime(20)}")
        print(f"获取当前公交线路下的该站台下的车辆停靠时间(秒)={busStationLine.busParkingTime()}")
```

 **def setGetOutPercent(self, percent: double) -> None: ..**

设置当前公交线路下的该站台的下客百分比

参数: 
[ in ] percent: 下客百分比

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"设置当前公交线路下的该站台的下客百分比={busStationLine.setGetOutPercent(20)}")
        print(f"获取当前公交线路下的该站台的下客百分比={busStationLine.getOutPercent()}")
```

 **def setGetOnTimePerPerson(self, time: double) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客上车时间

参数: 
[ in ] time: 平均每位乘客上车时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"设置当前公交线路下的该站台的平均每位乘客上车时间={busStationLine.setGetOnTimePerPerson(5.0)}")
        print(f"获取当前公交线路下的该站台的平均每位乘客上车时间={busStationLine.getOnTimePerPerson()}")
```

 **def setGetOutTimePerPerson(self, time: double) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客下车时间

参数: 
[ in ] time: 平均每位乘客下车时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines: 
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines: 
        print(f"设置当前公交线路下的该站台的平均每位乘客下车时间={busStationLine.setGetOutTimePerPerson(1.0)}")
        print(f"获取当前公交线路下的该站台的平均每位乘客下车时间={busStationLine.getOutTimePerPerson()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showBusStationLineAttr(netiface)

def showBusStationLineAttr(netiface): 
    busStations = netiface.busStations()
    if len(busStations) > 0: 
        busStation = busStations[0]
        busStationLines = netiface.findBusStationLineByStationId(busStation.id())
        if len(busStationLines)>0: 
            busStationLine = busStationLines[0]
            print(f"获取公交“站点-线路”ID={busStationLine.id()}, 获取当前公交站点的ID={busStationLine.stationId()}, "
                  f"获取当前公交站台所属的公交线路ID={busStationLine.lineId()}, "
                  f"获取当前公交线路下该站台的公交车辆停靠时间(秒)={busStationLine.busParkingTime()}, "
                  f"获取当前公交线路下该站台的下客百分比={busStationLine.getOutPercent()}, "
                  f"获取当前公交线路下该站台下的平均每位乘客上车时间, 单位: 秒={busStationLine.getOnTimePerPerson()}, "
                  f"获取当前公交线路下该站台下的平均每位乘客下车时间, 单位: 秒={busStationLine.getOutTimePerPerson()}, "
                  f"设置当前公交线路下该站台下的车辆停靠时间(秒)={busStationLine.setBusParkingTime(20)}, "
                  f"设置当前公交线路下的该站台的下客百分比={busStationLine.setGetOutPercent(0.60)}, "
                  f"设置当前公交线路下的该站台的平均每位乘客上车时间={busStationLine.setGetOnTimePerPerson(2.0)}, "
                  f"设置当前公交线路下的该站台的平均每位乘客下车时间: ={busStationLine.setGetOutTimePerPerson(1.0)}"
                  )
```





### 2.19. IVehicleDrivInfoCollector

数据采集器接口, 方法如下: 

 **def id(self) -> int: ...**

获取采集器ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器ID: {vehicleDrivInfoCollector.id()}")
```

 **def collName(self) -> str: ...**

获取采集器名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}名称: {vehicleDrivInfoCollector.collName()}")
```

 **def onLink(self) -> bool: ...**

判断当前数据采集器是否在路段上, 返回值为True表示检测器在路段上, 返回值False则表示在connector上

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}是否在路段上: {vehicleDrivInfoCollector.onLink()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取采集器所在的路段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的路段: {vehicleDrivInfoCollector.link()}")
```

 **def connector(self) -> Tessng.IConnector: ...**

获取采集器所在的连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的连接段: {vehicleDrivInfoCollector.connector()}")
```

 **def lane(self) -> Tessng.ILane: ...**

如果采集器在路段上, 则返回ILane对象, 否则范围None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的车道: {vehicleDrivInfoCollector.lane()}")
```

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果采集器在连接段上, 则返回laneConnector“车道连接”对象, 否则返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的车道连接: {vehicleDrivInfoCollector.laneConnector()}")
```

 **def distToStart(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取采集器距离路段或连接段起点的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}距离路段|连接段起点的距离为{vehicleDrivInfoCollector.distToStart()}")
    print(f"采集器{vehicleDrivInfoCollector.id()}距离路段|连接段起点的距离（米制）为{vehicleDrivInfoCollector.distToStart(UnitOfMeasure.Metric)}")
```

 **def point(self, unit: Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

获取采集器所在点, 像素坐标, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}所在点, 坐标为{vehicleDrivInfoCollector.point()}")
    print(f"采集器{vehicleDrivInfoCollector.id()}所在点, 米制坐标为{vehicleDrivInfoCollector.point(UnitOfMeasure.Metric)}")
```

 **def fromTime(self) -> int: ...**

获取采集器的工作起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作起始时间, 为{vehicleDrivInfoCollector.fromTime()}秒")
```

 **def toTime(self) -> int: ...**

获取采集器的工作停止时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作停止时间, 为{vehicleDrivInfoCollector.toTime()}秒")
```

 **def aggregateInterval(self) -> int: ...**

获取数据集计的时间间隔, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}数据集计的时间间隔, 为{vehicleDrivInfoCollector.aggregateInterval()}秒")
```

 **def setName(self, name: str) -> None: ...**

设置采集器名称

参数: 
[ in ] name: 新名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    vehicleDrivInfoCollector.setName("采集器名称")
    print(f"采集器{vehicleDrivInfoCollector.id()}的名称, 为{vehicleDrivInfoCollector.collName()}")
```

 **def setDistToStart(self, dist: double, unit: Tess.UnitOfMeasure) -> None: ...**

设置采集器距车道起点（或“车道连接”起点）的距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 采集器距离车道起点（或“车道连接”起点）的距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    vehicleDrivInfoCollector.setDistToStart(400)
    print(f"采集器{vehicleDrivInfoCollector.id()}距车道起点（或“车道连接”起点）的距离, 为{vehicleDrivInfoCollector.distToStart()}像素")
    vehicleDrivInfoCollector.setDistToStart(400, UnitOfMeasure.Metric)
    print(f"采集器{vehicleDrivInfoCollector.id()}距车道起点（或“车道连接”起点）的距离（米制）, 为{vehicleDrivInfoCollector.distToStart(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time: int) -> None: ...**

设置工作起始时间(秒)

参数: 
[ in ] time: 工作起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    vehicleDrivInfoCollector.setFromTime(10)
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作起始时间, 为{vehicleDrivInfoCollector.fromTime()}秒")
```

 **def setToTime(self, time: int) -> None: ...**

设置工作结束时间(秒)

参数: 
[ in ] time: 工作结束时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    vehicleDrivInfoCollector.setToTime(60)
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作停止时间, 为{vehicleDrivInfoCollector.toTime()}秒")
```

 **def setAggregateInterval(self, interval: int) -> None: ...**

设置集计数据时间间隔(秒)

参数: 
[ in ] interval: 集计数据时间间隔, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    vehicleDrivInfoCollector.setAggregateInterval(10)
    print(f"采集器{vehicleDrivInfoCollector.id()}数据集计的时间间隔, 为{vehicleDrivInfoCollector.aggregateInterval()}秒")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取采集器的多边型轮廓顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors: 
    print(f"采集器{vehicleDrivInfoCollector.id()}的多边型轮廓顶点为{vehicleDrivInfoCollector.polygon()}")
```



**案例代码**

```python
netiface = tessngIFace().netInterface()
showBusStationLineAttr(netiface)
showVehicleDrivInfoCounter(netiface)
def showVehicleDrivInfoCounter(netiface): 
    collectors = netiface.vehiInfoCollectors()
    if len(collectors)>0: 
        collector = netiface.findVehiInfoCollector(collectors[0].id())

        print(
            f"获取采集器ID={collector.id()}, 获取采集器名称={collector.collName()}, 判断当前数据采集器是否在路段上, 返回值为True表示检测器在路段上, 返回值False则表示在connector上={controller.onLink()}"
            f"获取采集器所在的路段={collector.link()}, 获取采集器所在的连接段={collector.connector()}, "
            f"如果采集器在路段上, 则返回ILane对象, 否则范围None={collector.lane()}, 如果采集器在连接段上, 则返回laneConnector“车道连接”对象, 否则返回None={collector.laneConnector()}"
            f" 获取采集器的工作起始时间, 工作停止时间={collector.fromTime()}-{collector.toTime()}, 采集器所在点, 像素坐标={collector.point()}"
            f"获取采集器距离路段|连接段起点的距离={collector.distToStart()}")
```





### 2.20. IVehicleQueueCounter

排队计数器接口, 方法如下: 

 **def id(self) -> int: ...**

获取当前排队计数器ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}的ID为{vehicleQueueCounter.id()}")
```

 **def counterName(self) -> str: ...**

获取当前排队计数器名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}的名称为{vehicleQueueCounter.counterName()}")
```

 **def onLink(self) -> bool: ...**

是否在路段上, 如果True则connector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}是否在路段上为{vehicleQueueCounter.onLink()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取当前排队计数器所在路段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}所在路段为{vehicleQueueCounter.link()}")
```

 **def connector(self) -> Tessng.IConnector: ...**

获取当前计数器所在连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}所在连接段为{vehicleQueueCounter.connector()}")
```

 **def lane(self) -> Tessng.ILane: ...**

如果计数器在路段上则lane()返回所在车道, laneConnector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}所在车道为{vehicleQueueCounter.lane()}")
```

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果计数器在连接段上则laneConnector返回“车道连接”, lane()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}所在车道连接为{vehicleQueueCounter.laneConnector()}")
```

 **def distToStart(self, unit: Tess.UnitOfMeasure) -> double: ...**

计数器距离起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离为{vehicleQueueCounter.distToStart()}")
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离（米制）为{vehicleQueueCounter.distToStart(UnitOfMeasure.Metric)}")
```

 **def point(self, unit: Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

计数器所在点, 像素坐标, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位  
举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}所在点坐标为{vehicleQueueCounter.point()}")
    print(f"排队计数器{vehicleQueueCounter.id()}所在点坐标（米制）为{vehicleQueueCounter.point(UnitOfMeasure.Metric)}")
```

 **def fromTime(self) -> int: ...**

获取当前计数器工作起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}的工作起始时间, 为{vehicleQueueCounter.fromTime()}秒")
```

 **def toTime(self) -> int: ...**

获取当前计数器工作停止时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}的工作停止时间, 为{vehicleQueueCounter.toTime()}秒")
```

 **def aggregateInterval(self) -> int: ...**

计数集计数据时间间隔, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}数据集计的时间间隔, 为{vehicleQueueCounter.aggregateInterval()}秒")
```

 **def setName(self, name: str) -> None: ...**

设置计数器名称

参数: 
[ in ] name: 计数器名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    vehicleQueueCounter.setName("计数器名称")
    print(f"排队计数器{vehicleQueueCounter.id()}的名称, 为{vehicleQueueCounter.counterName()}")
```

 **def setDistToStart(self, dist: double, unit: Tess.UnitOfMeasure) -> None: ...**

设置当前计数器距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] dist: 计数器距离车道起点（或“车道连接”起点）的距离, 默认单位: 像素  
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    vehicleQueueCounter.setDistToStart(100)
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离, 为{vehicleQueueCounter.distToStart()}")
    vehicleQueueCounter.setDistToStart(100, UnitOfMeasure.Metric)
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离（米制）, 为{vehicleQueueCounter.distToStart(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time: int) -> None: ...**

设置工作起始时间(秒)

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    vehicleQueueCounter.setFromTime(10)
    print(f"排队计数器{vehicleQueueCounter.id()}的工作起始时间, 为{vehicleQueueCounter.fromTime()}秒")
```

 **def setToTime(self, time: int) -> None: ...**

设置工作结束时间(秒)

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    vehicleQueueCounter.setToTime(60)
    print(f"排队计数器{vehicleQueueCounter.id()}的工作结束时间, 为{vehicleQueueCounter.toTime()}秒")
```

 **def setAggregateInterval(self, interval: int) -> None: ...**

设置集计数据时间间隔(秒)

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    vehicleQueueCounter.setAggregateInterval(10)
    print(f"排队计数器{vehicleQueueCounter.id()}数据集计的时间间隔, 为{vehicleQueueCounter.aggregateInterval()}秒")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取计数器的多边型轮廓顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters: 
    print(f"排队计数器{vehicleQueueCounter.id()}的多边型轮廓顶点为{vehicleQueueCounter.polygon()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showVehicleQueueCounter(netiface)

def showVehicleQueueCounter(netiface): 
    collectors = netiface.vehiQueueCounters()
    if len(collectors)>0: 
        collector = netiface.findVehiQueueCounter(collectors[0].id())

        print(
            f"获取当前排队计数器ID={collector.id()}, 获取当前排队计数器名称={collector.counterName()}, "
            f"判断当前数据采集器是否在路段上, 返回值为True表示检测器在路段上, 返回值False则表示在connector上={collector.onLink()}, "
            f"获取当前排队计数器所在路段={collector.link()}, 获取当前排队计数器所在连接段={collector.connector()}, "
            f"如果计数器在路段上则lane()返回所在车道, laneConnector()返回None={collector.lane()}, "
            f"如果计数器在连接段上则laneConnector返回“车道连接”, lane()返回None={collector.laneConnector()}"
            f" 获取当前计数器工作起始时间, 工作停止时间={collector.fromTime()}-{collector.toTime()}, "
            f"计数器所在点, 像素坐标={collector.point()}, 计数集计数据时间间隔={collector.aggregateInterval()}"
            f"计数器距离起点距离, 默认单位: 像素 ={collector.distToStart()}")
```




### 2.21. IVehicleTravelDetector

行程时间检测器接口, 方法如下: 

 **def id(self) -> int: ...**

获取检测器ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的ID为{vehicleTravelDetector.id()}")
```

 **def detectorName(self) -> str: ...**

获取检测器名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的名称, 为{vehicleTravelDetector.detectorName()}")
```

 **def isStartDetector(self) -> bool: ...**

是否检测器起始点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}是否为起始点, 为{vehicleTravelDetector.isStartDetector()}")
```

 **def isOnLink_startDetector(self) -> bool: ...**

检测器起点是否在路段上, 如果否, 则起点在连接段上

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}是否在路段上, 为{vehicleTravelDetector.isOnLink_startDetector()}")
```

 **def isOnLink_endDetector(self) -> bool: ...**

检测器终点是否在路段上, 如果否, 则终点在连接段上

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}是否在路段上, 为{vehicleTravelDetector.isOnLink_endDetector()}")
```

 **def link_startDetector(self) -> Tessng.ILink: ...**

如果检测器起点在路段上则link_startDetector()返回起点所在路段, laneConnector_startDetector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}所在路段, 为{vehicleTravelDetector.link_startDetector()}")
```

 **def laneConnector_startDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”, link_startDetector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}所在连接段, 为{vehicleTravelDetector.laneConnector_startDetector()}")
```

 **def link_endDetector(self) -> Tessng.ILink: ...**

如果检测器终点在路段上则link_endDetector()返回终点所在路段, laneConnector_endDetector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}所在路段, 为{vehicleTravelDetector.link_endDetector()}")
```

 **def laneConnector_endDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”, link_endDetector()返回None

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}所在连接段, 为{vehicleTravelDetector.laneConnector_endDetector()}")
```

 **def distance_startDetector(self, unit: Tess.UnitOfMeasure) -> double: ...**

检测器起点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离, 为{vehicleTravelDetector.distance_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离（米制）, 为{vehicleTravelDetector.distance_startDetector(UnitOfMeasure.Metric)}米")
```

 **def distance_endDetector(self, unit: Tess.UnitOfMeasure) -> double: ...**

检测器终点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离, 为{vehicleTravelDetector.distance_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离（米制）, 为{vehicleTravelDetector.distance_endDetector(UnitOfMeasure.Metric)}米")
```

 **def point_startDetector(self, unit: Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

检测器起点位置, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点位置, 为{vehicleTravelDetector.point_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点位置（米制）, 为{vehicleTravelDetector.point_startDetector(UnitOfMeasure.Metric)}米")
```

 **def point_endDetector(self, unit: Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

检测器终点位置, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点位置, 为{vehicleTravelDetector.point_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点位置（米制）, 为{vehicleTravelDetector.point_endDetector(UnitOfMeasure.Metric)}米")
```

 **def fromTime(self) -> int: ...**

获取检测器工作起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作起始时间, 为{vehicleTravelDetector.fromTime()}")
```

 **def toTime(self) -> int: ...**

检测器工作停止时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作停止时间, 为{vehicleTravelDetector.toTime()}")
```

 **def aggregateInterval(self) -> int: ...**

集计数据时间间隔, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的集计数据时间间隔, 为{vehicleTravelDetector.aggregateInterval()}")
```

 **def setName(self, name: str) -> None: ...**

设置检测器名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setName("检测器名称")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的名称, 为{vehicleTravelDetector.detectorName()}")
```

 **def setDistance_startDetector(self, dist: double) -> None: ...**

设置检测器起点距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setDistance_startDetector(100)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离, 为{vehicleTravelDetector.distance_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离（米制）, 为{vehicleTravelDetector.distance_startDetector(UnitOfMeasure.Metric)}米")
```

 **def setDistance_endDetector(self, dist: double) -> None: ...**

设置检测器终点距车道起点（或“车道连接”起点）距离, 默认单位: 像素, 可通过unit参数设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setDistance_endDetector(100)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离, 为{vehicleTravelDetector.distance_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离（米制）, 为{vehicleTravelDetector.distance_endDetector(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time: int) -> None: ...**

设置检测器工作起始时间, 单位: 秒

参数: 
[ in ] time: 工作起始时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setFromTime(10)
    vehicleTravelDetector.setToTime(60)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作起始时间, 为{vehicleTravelDetector.fromTime()}秒")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作结束时间, 为{vehicleTravelDetector.toTime()}秒")
```

 

 **def setToTime(self, time: int) -> None: ...**

设置检测器工作结束时间, 单位: 秒

参数: 
[ in ] time: 工作结束时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setToTime(60)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作结束时间, 为{vehicleTravelDetector.toTime()}")
```

 **def setAggregateInterval(self) -> int: ...**

设置检测器集计数据时间间隔, 单位: 秒

参数: 
[ in ] interval: 集计数据时间间隔, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    vehicleTravelDetector.setAggregateInterval(10)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的集计数据时间间隔, 为{vehicleTravelDetector.aggregateInterval()}")
```

 **def polygon_startDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器起始点多边型轮廓的顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点多边型轮廓的顶点, 为{vehicleTravelDetector.polygon_startDetector()}")
```

 **def polygon_endDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器终止点多边型轮廓的顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors: 
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点多边型轮廓的顶点, 为{vehicleTravelDetector.polygon_endDetector()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showVehicleTravelDetector(netiface)

def showVehicleTravelDetector(netiface): 
    collectors = netiface.vehiTravelDetectors()
    if len(collectors) > 0: 
        collector = netiface.findVehiTravelDetector(collectors[0].id())

        print(
            f"获取检测器ID={collector.id()}, 获取检测器名称={collector.detectorName()}, 是否检测器起始点={collector.isStartDetector()}, "
            f"判断当前数据采集器是否在路段上, 返回值为True表示检测器在路段上, 返回值False则表示在connector上={collector.onLink()}, "
            f"检测器起点是否在路段上, 如果否, 则起点在连接段上={collector.isOnLink_startDetector()}, "
            f"检测器终点是否在路段上, 如果否, 则终点在连接段上={collector.isOnLink_endDetector()}, "
            f"如果检测器起点在路段上则link_startDetector()返回起点所在路段, laneConnector_startDetector()返回None={collector.link_startDetector()}, "
            f"如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”, link_startDetector()返回None={collector.laneConnector_startDetector()}"
            f"如果检测器终点在路段上则link_endDetector()返回终点所在路段, laneConnector_endDetector()返回None={collector.link_endDetector()}, "
            f"如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”, link_endDetector()返回None={collector.laneConnector_endDetector()}, "
            f"检测器起点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素={collector.distance_startDetector()}, "
            f"检测器终点距离所在车道起点或“车道连接”起点距离, 默认单位: 像素={collector.distance_endDetector()}, "
            f"检测器起点位置, 默认单位: 像素, 可通过可选参数: unit设置单位, ={collector.point_startDetector()}, "
            f"检测器终点位置, 默认单位: 像素, 可通过可选参数: unit设置单位, ={collector.point_endDetector()}, "
            f"检测器工作起始时间, 单位: 秒={collector.fromTime()}, 检测器工作停止时间, 单位: 秒={collector.toTime()}, "
            f"集计数据时间间隔, 单位: 秒={collector.aggregateInterval()}, "
            f"获取行程时间检测器起始点多边型轮廓的顶点={collector.polygon_startDetector()}, "
            f"获取行程时间检测器终止点多边型轮廓的顶点={collector.polygon_endDetector()}")
```






### 2.22. IGuidArrow

导向箭头接口, 方法如下: 

 **def id(self) -> int: ...**

获取导向箭头ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头的ID为{guidArrow.id()}")
```

 **def lane(self) -> Tessng.ILane: ...**

获取导向箭头所在的车道

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头{guidArrow.id()}所在的车道为{guidArrow.lane()}")
```

 **def length(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取导向箭头的长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头{guidArrow.id()}的长度, 为{guidArrow.length()}")
    print(f"导向箭头{guidArrow.id()}的长度（米制）, 为{guidArrow.length(UnitOfMeasure.Metric)}米")
```

 **def distToTerminal(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取导向箭头到的终点距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头{guidArrow.id()}的终点距离, 为{guidArrow.distToTerminal()}")
    print(f"导向箭头{guidArrow.id()}的终点距离（米制）, 为{guidArrow.distToTerminal(UnitOfMeasure.Metric)}米")
```
 **def arrowType(self) -> Tess.Online.GuideArrowType: ...**

获取导向箭头的类型, 导向箭头的类型分为: 直行、左转、右转、直行或左转、直行或右转、直行左转或右转、左转或右转、掉头、直行或掉头和左转或掉头

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头{guidArrow.id()}的类型, 为{guidArrow.arrowType()}")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取导向箭头的多边型轮廓的顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows: 
    print(f"导向箭头{guidArrow.id()}的多边型轮廓的顶点, 为{guidArrow.polygon()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showGuidArrowAttr(netiface)
def showGuidArrowAttr(netiface): 
    guidArrow = netiface.findGuidArrow(netiface.guidArrows()[0].id())
    print(f"导向箭头数={netiface.guidArrowCount()}, 导向箭头集={netiface.guidArrows()}, ")
    print(f"导向箭头ID={guidArrow.id()}, "
          f"获取导向箭头所在的车道={guidArrow.lane()}, "
          f"获取导向箭头长度, 像素制={guidArrow.length()}, 米制={guidArrow.length(UnitOfMeasure.Metric)}, "
          f"获取导向箭头到终点距离, 像素制, 米制={guidArrow.distToTerminal()}, 米制={guidArrow.distToTerminal(UnitOfMeasure.Metric)}, "
          f"获取导向箭头的类型, 导向箭头的类型分为: 直行、左转、右转、直行或左转、直行或右转、"
          f"直行左转或右转、左转或右转、掉头、直行或掉头和左转或掉头={guidArrow.arrowType()}, "
          f"获取导向箭头的多边型轮廓的顶点={guidArrow.polygon()}")
```





### 2.23. IAccidentZone

事故区接口, 方法如下: 

 **def id(self) -> int: ...**

获取事故区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区的ID为{accidentZone.id()}")
```

 **def name(self) -> str: ...**

获取事故区名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区的名称为{accidentZone.name()}")
```

 **def location(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区距所在路段起点的距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}距所在路段起点的距离, 为{accidentZone.location()}")
    print(f"事故区{accidentZone.id()}距所在路段起点的距离（米制）, 为{accidentZone.location(UnitOfMeasure.Metric)}米")
```

 **def zoneLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区长度, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}的长度, 为{accidentZone.zoneLength()}")
    print(f"事故区{accidentZone.id()}的长度（米制）, 为{accidentZone.zoneLength(UnitOfMeasure.Metric)}米")
```

 **def limitedSpeed(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区当前时段限速, 默认单位: 像素(km/h), 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位(km/h), Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}的限速, 为{accidentZone.limitedSpeed()}")
    print(f"事故区{accidentZone.id()}的限速（米制）, 为{accidentZone.limitedSpeed(UnitOfMeasure.Metric)}km/h")
```

 **def section(self) -> Tessng.ISection: ...**

获取事故区所在的路段或连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}所在的路段或连接段, 为{accidentZone.section()}")
```

 **def roadId(self) -> int: ...**

获取事故区所在路段的ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}所在路段的ID为{accidentZone.roadId()}")
```

 **def roadType(self) -> str: ...**

获取事故区所在的道路类型(路段或连接段)

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}所在的道路类型为{accidentZone.roadType()}")
```

 **def laneObjects(self) -> typing.List< Tess.ILaneObjects >: ...**

获取事故区当前时段占用的车道列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}当前时段占用的车道列表为{accidentZone.laneObjects()}")
```

 **def controlLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区当前时段控制距离（车辆距离事故区起点该距离内, 强制变道）, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}当前时段控制距离, 为{accidentZone.controlLength()}")
    print(f"事故区{accidentZone.id()}当前时段控制距离（米制）, 为{accidentZone.controlLength(UnitOfMeasure.Metric)}米")
```

 **def addAccidentZoneInterval(self, param: Tess.Online.DynaAccidentZoneIntervalParam) -> Tess.IAccidentZoneInterval: ...**

添加事故时段

参数: 
[ in ]   param: 事故时段参数, 入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    accidentZoneIntervals=accidentZone.accidentZoneIntervals()
    param = accidentZoneIntervals[-1]
    accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
    accidentZoneIntervalParam.accidentZoneId = param.accidentZoneId()
    accidentZoneIntervalParam.startTime = param.endTime()
    accidentZoneIntervalParam.endTime = param.endTime() + 300
    accidentZoneIntervalParam.length = param.length()
    accidentZoneIntervalParam.location = param.location()
    accidentZoneIntervalParam.limitedSpeed = param.limitedSpeed()
    accidentZoneIntervalParam.controlLength = param.controlLength()
    laneNumbers =  [lane.number() for lane in netiface.findLink(accidentZone.roadId()).lanes()]
    accidentZoneIntervalParam.mlFromLaneNumber = list(set(laneNumbers) - set( param.laneNumbers()))

    print(f"添加前事故时段列表{accidentZone.accidentZoneIntervals()}")
    accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)
    print(f"添加后事故时段列表{accidentZone.accidentZoneIntervals()}")
```

 **def removeAccidentZoneInterval(self, accidentZoneIntervalId: int) -> None: ...**

移除事故时段 

参数: 
[ in ] accidentZoneIntervalId: 事故时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    accidentZoneIntervals=accidentZone.accidentZoneIntervals()
    param = accidentZoneIntervals[-1]
    accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
    accidentZoneIntervalParam.accidentZoneId = param.accidentZoneId()
    accidentZoneIntervalParam.startTime =param.endTime()
    accidentZoneIntervalParam.endTime = param.endTime()+300
    accidentZoneIntervalParam.length = param.length()
    accidentZoneIntervalParam.location = param.location()
    accidentZoneIntervalParam.limitedSpeed = param.limitedSpeed()
    accidentZoneIntervalParam.controlLength = param.controlLength()
    laneNumbers =  [lane.number() for lane in netiface.findLink(accidentZone.roadId()).lanes()]
    accidentZoneIntervalParam.mlFromLaneNumber = list(set(laneNumbers) - set( param.laneNumbers()))

    accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)

    accidentZoneIntervalParam1 = accidentZoneIntervalParam
    accidentZoneIntervalParam1.startTime =accidentZoneIntervalParam1.endTime
    accidentZoneIntervalParam1.endTime = accidentZoneIntervalParam1.endTime+300
    accidentZoneInterval1 = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)

    # 移除刚添加的事故时段accidentZoneInterval1
    print(f"移除前事故时段列表{accidentZone.accidentZoneIntervals()}")
    accidentZone.removeAccidentZoneInterval(accidentZoneInterval1.intervalId())
    print(f"移除后事故时段列表{accidentZone.accidentZoneIntervals()}")
```

 **def updateAccidentZoneInterval(self, param: Tess.Online.DynaAccidentZoneIntervalParam) -> bool: ...**

更新事故时段

参数: 
[ in ] param: 事故时段参数, 入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    accidentZoneIntervals = accidentZone.accidentZoneIntervals()
    param = accidentZoneIntervals[-1]
    accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
    accidentZoneIntervalParam.accidentZoneId = param.accidentZoneId()
    accidentZoneIntervalParam.startTime = param.endTime()
    accidentZoneIntervalParam.endTime = param.endTime() + 300
    accidentZoneIntervalParam.length = param.length()
    accidentZoneIntervalParam.location = param.location()
    accidentZoneIntervalParam.limitedSpeed = param.limitedSpeed()
    accidentZoneIntervalParam.controlLength = param.controlLength()
    laneNumbers =  [lane.number() for lane in netiface.findLink(accidentZone.roadId()).lanes()]
    accidentZoneIntervalParam.mlFromLaneNumber = list(set(laneNumbers) - set( param.laneNumbers()))

    accidentZoneInterval = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)

    accidentZoneIntervalParam1 = accidentZoneIntervalParam
    accidentZoneIntervalParam1.startTime =accidentZoneIntervalParam1.endTime
    accidentZoneIntervalParam1.endTime = accidentZoneIntervalParam1.endTime+300
    accidentZoneInterval1 = accidentZone.addAccidentZoneInterval(accidentZoneIntervalParam)

    accidentZoneIntervalParam.controlLength = param.controlLength() + 10
    accidentZone.updateAccidentZoneInterval(accidentZoneIntervalParam)
```

 **def accidentZoneIntervals(self) -> typing.List<Tess.IAccidentZoneInterval>: ...**

获取所有事故时段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    print(f"事故区{accidentZone.id()}的事故时段, 为{accidentZone.accidentZoneIntervals()}")
```

 **def findAccidentZoneIntervalById(self, accidentZoneIntervalId: int) -> Tess.IAccidentZoneInterval: ...**

根据ID查询事故时段

参数: 
[ in ] accidentZoneIntervalId: 事故时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    accidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in accidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段, 为{accidentZone.findAccidentZoneIntervalById(accidentZoneInterval.intervalId())}")
```

**def findAccidentZoneIntervalByStartTime(self, startTime: int) -> Tess.IAccidentZoneInterval: ...**

根据开始时间查询事故时段

参数: 
[ in ] startTime: 事故时段开始时间

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    accidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in accidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段, 为{accidentZone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime())}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showAccidentZoneAttr(netiface)

def showAccidentZoneAttr(self, netiface): 
    acczones = netiface.accidentZones()
    acczone = netiface.findAccidentZone(acczones[0].id())
    print(f"获取事故区ID={acczone.id()}, 获取事故区名称={acczone.name()}, "
          f"获取事故区当前时段距所在路段起点的距离, 像素制={acczone.location()}, 米制={acczone.location(UnitOfMeasure.Metric)}, "
          f"获取事故区当前时段长度, 像素制={acczone.zoneLength()}, 米制={acczone.zoneLength(UnitOfMeasure.Metric)}, "
          f"获取事故区当前时段限速, 像素制 km/h={acczone.limitedSpeed()}, 米制={acczone.limitedSpeed(UnitOfMeasure.Metric)}, "
          f"获取事故区所在的路段或连接段={acczone.section()}, 获取事故区所在路段的ID={acczone.roadId()}, "
          f"获取事故区所在的道路类型(路段或连接段)={acczone.roadType()}, "
          f"获取事故区当前时段占用的车道列表={acczone.laneObjects()}, "
          f"获取事故区当前时段控制距离（车辆距离事故区起点该距离内, 强制变道, 像素制={acczone.controlLength()}, 米制={acczone.controlLength(UnitOfMeasure.Metric)}, "
          )
    print("添加事故时段")

    accidentZoneIntervals=acczone.accidentZoneIntervals()
    param = accidentZoneIntervals[-1]
    accidentZoneIntervalParam = Online.DynaAccidentZoneIntervalParam()
    accidentZoneIntervalParam.accidentZoneId = param.accidentZoneId()
    accidentZoneIntervalParam.startTime =param.endTime()
    accidentZoneIntervalParam.endTime = param.endTime()+300
    accidentZoneIntervalParam.length = param.length()
    accidentZoneIntervalParam.location = param.location()
    accidentZoneIntervalParam.limitedSpeed = param.limitedSpeed()
    accidentZoneIntervalParam.controlLength = param.controlLength()
    laneNumbers =  [lane.number() for lane in netiface.findLink(acczone.roadId()).lanes()]
    accidentZoneIntervalParam.mlFromLaneNumber = list(set(laneNumbers) - set( param.laneNumbers()))

    accidentZoneInterval = acczone.addAccidentZoneInterval(accidentZoneIntervalParam)

    accidentZoneIntervalParam1 = accidentZoneIntervalParam
    accidentZoneIntervalParam1.startTime =accidentZoneIntervalParam1.endTime
    accidentZoneIntervalParam1.endTime = accidentZoneIntervalParam1.endTime+300
    accidentZoneInterval1 = acczone.addAccidentZoneInterval(accidentZoneIntervalParam)

    # acczone.removeAccidentZoneInterval(accidentZoneInterval1.intervalId())
    accidentZoneIntervalParam.controlLength = param.controlLength() + 10
    acczone.updateAccidentZoneInterval(accidentZoneIntervalParam)
    print(f"获取所有事故时段={acczone.accidentZoneIntervals()}, "
          # f"根据ID查询事故时段={acczone.findAccidentZoneIntervalById(accidentZoneInterval.intervalId())}, "
          f"根据开始时间查询事故时段={acczone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime())}")

```



### 2.24. IAccidentZoneInterval

事故时段接口, 方法如下: 

 **def intervalId(self) -> int: ...**

获取事故时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段ID为{accidentZoneInterval.intervalId()}")
```

 **def accidentZoneId(self) -> int: ...**

获取所属事故区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段所属事故区ID为{accidentZoneInterval.accidentZoneId()}")
```

 **def startTime(self) -> int: ...**

获取事故区开始时间

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段开始时间为{accidentZoneInterval.startTime()}")
```

 **def endTime(self) -> int: ...**

获取事故区结束时间

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段结束时间为{accidentZoneInterval.endTime()}")
```

 **def length(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区在该时段的长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段长度为{accidentZoneInterval.length()}")
        print(f"事故区{accidentZone.id()}的事故时段长度为{accidentZoneInterval.length(UnitOfMeasure.Metric)}")
```

 **def location(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区在该时段的距起点距离, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段距起点距离为{accidentZoneInterval.location()}")
        print(f"事故区{accidentZone.id()}的事故时段距起点距离为{accidentZoneInterval.location(UnitOfMeasure.Metric)}")
```

 **def limitedSpeed(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区在该时段的限速, 默认单位: 像素(km/h), 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位(km/h), Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段限速为{accidentZoneInterval.limitedSpeed()}")
        print(f"事故区{accidentZone.id()}的事故时段限速为{accidentZoneInterval.limitedSpeed(UnitOfMeasure.Metric)}")
```

 **def controlLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取事故区在该时段的控制距离（车辆距离事故区起点该距离内, 强制变道）, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段控制距离为{accidentZoneInterval.controlLength()}")
        print(f"事故区{accidentZone.id()}的事故时段控制距离为{accidentZoneInterval.controlLength(UnitOfMeasure.Metric)}")
```

 **def laneNumbers(self) -> int: ...**

获取事故区在该时段的占用车道序号

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones: 
    lAccidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in lAccidentZoneIntervals: 
        print(f"事故区{accidentZone.id()}的事故时段占用车道序号为{accidentZoneInterval.laneNumbers()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
acczones = netiface.accidentZones()
acczone = netiface.findAccidentZone(acczones[0].id())
_showAccidentZoneIntervalAttr(netiface)
def _showAccidentZoneIntervalAttr(acczone): 
    interval = acczone.accidentZoneIntervals()[0]
    print(
        # f"获取事故时段ID={interval.intervalId()}, "
          f"获取所属事故区ID={interval.accidentZoneId()}, "
          f"获取事故时段开始时间={interval.startTime()}, 获取事故时段结束时间={interval.endTime()}, "
          f"获取事故区在该时段的长度, 像素制={interval.location()}, 米制={interval.location(UnitOfMeasure.Metric)}, "
          f"获取事故区在该时段的限速, 像素制 km/h={interval.limitedSpeed()}, 米制={interval.limitedSpeed(UnitOfMeasure.Metric)}, "
          f"获取事故区在该时段的控制距离（车辆距离事故区起点该距离内, 强制变道）, 像素制 km/h={interval.controlLength()}, "
          f"米制={interval.controlLength(UnitOfMeasure.Metric)}, "
          f"获取事故区在该时段的占用车道序号={interval.laneNumbers()}")
```





### 2.25. IRoadWorkZone

施工区接口, 方法如下: 

 **def id(self) -> int: ...**

获取当前施工区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区ID为{roadWorkZone.id()}")
```

 **def name(self) -> str: ...**

获取施工区名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区名称={roadWorkZone.name()}")
```

 **def location(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取施工区距所在路段起点的距离, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区距所在路段起点的距离={roadWorkZone.location()}")
    print(f"施工区距所在路段起点的距离={roadWorkZone.location(UnitOfMeasure.Metric)}")
```

 **def zoneLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取施工区长度, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区长度={roadWorkZone.zoneLength()}")
    print(f"施工区长度={roadWorkZone.zoneLength(UnitOfMeasure.Metric)}")
```

 **def limitSpeed(self, unit: Tess.UnitOfMeasure) -> double: ...**

施工区限速 , 默认单位: 像素（km/h）, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区限速={roadWorkZone.limitSpeed()}")
    print(f"施工区限速={roadWorkZone.limitSpeed(UnitOfMeasure.Metric)}")
```

 **def sectionId(self) -> int: ...**

获取施工区所在路段或连接段的ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区所在路段或连接段ID为{roadWorkZone.sectionId()}")
```

 **def sectionName(self) -> str: ...**

获取施工区所在路段或连接段的名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区所在路段或连接段名称={roadWorkZone.sectionName()}")
```

 **def sectionType(self) -> str: ...**

获取施工区所在道路的道路类型, link: 路段, connector: 连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区所在道路的道路类型={roadWorkZone.sectionType()}")
```

 **def laneObjects(self) -> typing.List<Tess.LaneObject>: ...**

获取施工区所占的车道列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区所占的车道列表={roadWorkZone.laneObjects()}")
```

 **def laneObjectIds(self) -> typing.List<int>: ...**

获取施工区所占的车道ID列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区所占的车道ID列表={roadWorkZone.laneObjectIds()}")
```

 **def upCautionLength(self, unit: UnitOfMeasure) ->double: ...**

获取施工区上游警示区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区上游警示区长度={roadWorkZone.upCautionLength()}")
    print(f"施工区上游警示区长度={roadWorkZone.upCautionLength(UnitOfMeasure.Metric)}")
```

 **def upTransitionLength(self, unit: UnitOfMeasure) ->double: ...**

获取施工区上游过渡区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区上游过渡区长度={roadWorkZone.upTransitionLength()}")
    print(f"施工区上游过渡区长度={roadWorkZone.upTransitionLength(UnitOfMeasure.Metric)}")
```

 **def upBufferLength(self, unit: UnitOfMeasure) ->double: ...**

获取施工区上游缓冲区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区上游缓冲区长度={roadWorkZone.upBufferLength()}")
    print(f"施工区上游缓冲区长度={roadWorkZone.upBufferLength(UnitOfMeasure.Metric)}")
```

 **def downTransitionLength(self, unit: UnitOfMeasure) ->double: ...**

获取施工区下游过渡区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区下游过渡区长度={roadWorkZone.downTransitionLength()}")
    print(f"施工区下游过渡区长度={roadWorkZone.downTransitionLength(UnitOfMeasure.Metric)}")
```

 **def downTerminationLength(self, unit: UnitOfMeasure) ->double: ...**

获取施工区下游终止区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区下游终止区长度={roadWorkZone.downTerminationLength()}")
    print(f"施工区下游终止区长度={roadWorkZone.downTerminationLength(UnitOfMeasure.Metric)}")
```

 **def duration(self) -> int: ...**

施工持续时间, 单位: 秒。自仿真过程创建后, 持续时间大于此值, 则移除

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工持续时间={roadWorkZone.duration()}")
```

 **def isBorrowed(self) -> bool: ...**

获取施工区是否被借道

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones: 
    print(f"施工区是否被借道={roadWorkZone.isBorrowed()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showRoadWorkZoneAttr(netiface)
def showRoadWorkZoneAttr(self, netiface): 
    roadworkzones = netiface.roadWorkZones()
    roadworkzone = roadworkzones[0]

    print(f"roadWorkZones={roadworkzone.id()}, 获取施工区名称={roadworkzone.name()}"
          f", 获取施工区距所在路段起点的距离, 像素制={roadworkzone.location()}, 米制={roadworkzone.location(UnitOfMeasure.Metric)}, "
          f"获取施工区长度, 像素制={roadworkzone.zoneLength()}, 米制={roadworkzone.zoneLength(UnitOfMeasure.Metric)}, "
          f"获取施工区限速, 像素制={roadworkzone.limitSpeed()}, 米制={roadworkzone.limitSpeed(UnitOfMeasure.Metric)}, "
          f"获取施工区所在路段或连接段的ID={roadworkzone.sectionId()}, "
          f"获取施工区所在路段或连接段的名称={roadworkzone.sectionName()}, "
          f"获取施工区所在道路的道路类型, link: 路段, connector: 连接段={roadworkzone.sectionType()}, "
          f"获取施工区所占的车道列表={roadworkzone.laneObjects()}, "
          f"获取施工区所占的车道ID列表={roadworkzone.laneObjectIds()}, "
          f"获取施工区上游警示区长度, 像素制={roadworkzone.upCautionLength()}, 米制={roadworkzone.upCautionLength(UnitOfMeasure.Metric)}, "
          f"获取施工区上游过渡区长度, 像素制={roadworkzone.upTransitionLength()}, 米制={roadworkzone.upTransitionLength(UnitOfMeasure.Metric)}, "
          f"获取施工区上游缓冲区长度, 像素制={roadworkzone.upBufferLength()}, 米制={roadworkzone.upBufferLength(UnitOfMeasure.Metric)}, "
          f"获取施工区下游过渡区长度, , 像素制={roadworkzone.downTransitionLength()}, 米制={roadworkzone.downTransitionLength(UnitOfMeasure.Metric)}, "
          f"施工持续时间, 单位: 秒。自仿真过程创建后, 持续时间大于此值, 则移除={roadworkzone.duration()}, "
          f"获取施工区是否被借道={roadworkzone.isBorrowed()}")
```





### 2.26. ILimitedZone

限行区接口（借道施工的被借车道, 限制对向车辆行走的区域）, 方法如下: 

 **def id(self) -> int: ...**

获取限行区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区ID={limitedZone.id()}")
```

 **def name(self) -> str: ...**

获取限行区名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区名称={limitedZone.name()}")
```

 **def location(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取距起点距离, 默认单位: 像素, 可通过可选参数: unit设置单位
参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区距起点距离={limitedZone.location()}")
    print(f"限行区距起点距离={limitedZone.location(UnitOfMeasure.Metric)}")
```

 **def zoneLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取限行区长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区长度={limitedZone.zoneLength()}")
    print(f"限行区长度={limitedZone.zoneLength(UnitOfMeasure.Metric)}")
```

 **def limitSpeed(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取限行区限速, 默认单位: 像素(km/h), 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区限速={limitedZone.limitSpeed()}")
    print(f"限行区限速={limitedZone.limitSpeed(UnitOfMeasure.Metric)}")
```

 **def sectionId(self) -> int: ...**

获取限行区所在路段或连接段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区所在路段或连接段ID={limitedZone.sectionId()}")
```

 **def sectionName(self) -> str: ...**

获取限行区所在路段或连接段的名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区所在路段或连接段名称={limitedZone.sectionName()}")
```

 **def sectionType(self) -> str: ...**

获取限行区所在道路的类型: "link"表示路段, "connector"表示连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区所在道路的类型={limitedZone.sectionType()}")
```

 **def laneObjects(self) -> typing.List< Tessng.ILaneObject >: ...**

获取限行区所在车道对象列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区所在车道对象列表={limitedZone.laneObjects()}")
```

 **def duration(self) -> int: ...**

获取限行区的持续时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones: 
    print(f"限行区持续时间={limitedZone.duration()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showLimitZoneAttr(netiface)

def showLimitZoneAttr(self, netiface): 
    limitedZones = netiface.limitedZones()
    limitedZone = limitedZones[0]
    limitedZone1 = netiface.findLimitedZone(limitedZone.id())
    print(type(limitedZone), dir(limitedZone))
    print(f"获取限行区ID={limitedZone.id()}, 获取限行区名称={limitedZone.name()}"
          f", 获取限行区距所在路段起点的距离, 像素制={limitedZone.location()}, 米制={limitedZone.location(UnitOfMeasure.Metric)}, "
          f"获取限行区长度, 像素制={limitedZone.zoneLength()}, 米制={limitedZone.zoneLength(UnitOfMeasure.Metric)}, "
          f"获取限行区限速, 像素制={limitedZone.limitSpeed()}, 米制={limitedZone.limitSpeed(UnitOfMeasure.Metric)}, "
          f"获取路段或连接段ID={limitedZone.sectionId()}, "
          f"获取Section名称={limitedZone.sectionName()}, "
          f"获取道路类型, link表示路段, connector表示连接段={limitedZone.sectionType()}, "
          f"获取相关车道对象列表={limitedZone.laneObjects()}, "
          f"获取限行持续时间, 单位: 秒。自仿真过程创建后, 持续时间大于此值则删除={limitedZone.duration()}, ")
```







### 2.27. IReconstruction

改扩建接口, 此接口最好是在构造路网的最后调用, 避免后续其他接口调用原因导致创建施工区的路段线性被更改

 **def id(self) -> int: ...**

获取改扩建对象ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建对象ID={reconstruction.id()}")
```

 **def roadWorkZoneId(self) -> int: ...**

获取改扩建对象的起始施工区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建对象的起始施工区ID={reconstruction.roadWorkZoneId()}")
```

 **def limitedZoneId(self) -> int: ...**

获取改扩建对象的被借道限行区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建对象的被借道限行区ID={reconstruction.limitedZoneId()}")
```

 **def passagewayLength(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取改扩建对象的保通长度, 默认单位: 像素, 可通过可选参数: unit设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建对象的保通长度={reconstruction.passagewayLength()}")
    print(f"改扩建对象的保通长度={reconstruction.passagewayLength(UnitOfMeasure.Metric)}")
```

 **def duration(self) -> int: ...**

获取改扩建的持续时间, 单位: 秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建的持续时间={reconstruction.duration()}")
```

 **def borrowedNum(self) -> int: ...**

获取改扩建的借道车道数量

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"改扩建的借道车道数量={reconstruction.borrowedNum()}")
```

 **def passagewayLimitedSpeed(self, unit: Tess.UnitOfMeasure) -> double: ...**

获取保通开口限速, 默认单位: 像素（km/h）, 可通过unit参数设置单位

参数: 
[ in ]  unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示不指定单位返回接口默认的单位

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"保通开口限速={reconstruction.passagewayLimitedSpeed()}")
    print(f"保通开口限速={reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric)}")
```

 **def dynaReconstructionParam(self) -> Online.DynaReconstructionParam: ...**

获取改扩建动态参数; 入参数据结构见pyi文件的 Online.DynaReconstructionParam类 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions: 
    print(f"获取改扩建动态参数={reconstruction.dynaReconstructionParam()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showReconstructionAttr(netiface)
def showReconstructionAttr(self, netiface): 
    reconstructions = netiface.reconstructions()
    if len(reconstructions) > 0: 
        reconstruction = reconstructions[0]
        reconstruction1 = netiface.findReconstruction(reconstruction.id())

        print(f"获取改扩建ID={reconstruction.id()}, 获取改扩建起始施工区ID={reconstruction.roadWorkZoneId()}"
              f", 获取被借道限行区ID={reconstruction.limitedZoneId()}, "
              f"获取保通长度, 像素制={reconstruction.passagewayLength()}, 米制={reconstruction.passagewayLength(UnitOfMeasure.Metric)}, "
              f"获取保通开口限速, 像素制={reconstruction.passagewayLimitedSpeed()}, 米制={reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric)}, "
              f"获取改扩建持续时间={reconstruction.duration()}, "
              f"获取借道数量={reconstruction.borrowedNum()}, "
              f"获取改扩建动态参数, 返回参数为米制={reconstruction.dynaReconstructionParam()}, ")
```



### 2.28. IReduceSpeedArea

限速区接口

 **def id(self) -> int: ...**

获取限速区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区ID={reduceSpeedArea.id()}")
```

 **def name(self) -> str: ...**

获取限速区名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区名称={reduceSpeedArea.name()}")
```

 **def location(self, unit: UnitOfMeasure) -> double: ...**

获取距起点距离, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区距起点距离={reduceSpeedArea.location()}")
    print(f"限速区距起点距离={reduceSpeedArea.location(UnitOfMeasure.Metric)}")
```

 **def areaLength(self, unit: UnitOfMeasure) -> double: ...**

获取限速区长度, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区长度={reduceSpeedArea.areaLength()}")
    print(f"限速区长度={reduceSpeedArea.areaLength(UnitOfMeasure.Metric)}")
```

 **def sectionId(self) -> int: ...**

获取限速区所在路段或连接段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区所在路段或连接段ID={reduceSpeedArea.sectionId()}")
```

 **def laneNumber(self) -> int: ...**

获取限速区车道序号

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区车道序号={reduceSpeedArea.laneNumber()}")
```

 **def toLaneNumber(self) -> int: ...**

获取限速区获取目标车道序号（当限速区设置在连接段时, 返回值非空）

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"限速区获取目标车道序号={reduceSpeedArea.toLaneNumber()}")
```

 **def addReduceSpeedInterval(self, param: Online.DynaReduceSpeedIntervalParam) -> Tess.IReduceSpeedInterval: ...**

添加限速时段

参数: 
[ in ]  param: 限速时段参数, 入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    param = Online.DynaReduceSpeedIntervalParam()
    param.startTime = 3601 # 需要注意新增的时段要和已有时段不冲突
    param.endTime = 7200
    type1 = Online.DynaReduceSpeedVehiTypeParam()
    type1.vehicleTypeCode = 2
    type1.avgSpeed = 10
    type1.speedSD = 5
    param.mlReduceSpeedVehicleTypeParam = [type1]
    print(f"添加前限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
    interval = reduceSpeedArea.addReduceSpeedInterval(param)
    print(f"添加后限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
```

 **def removeReduceSpeedInterval(self, id: int) -> None: ...**

移除限速时段

参数: 
[ in ] id: 限速时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    param = Online.DynaReduceSpeedIntervalParam()
    param.startTime = 3601 # 需要注意新增的时段要和已有时段不冲突
    param.endTime = 7200
    type1 = Online.DynaReduceSpeedVehiTypeParam()
    type1.vehicleTypeCode = 2
    type1.avgSpeed = 10
    type1.speedSD = 5
    param.mlReduceSpeedVehicleTypeParam = [type1]
    interval = reduceSpeedArea.addReduceSpeedInterval(param)
    print(f"移除前限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
    reduceSpeedArea.removeReduceSpeedInterval(interval.id())
    print(f"移除后限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
```

 **def updateReduceSpeedInterval(self, param: Online.DynaReduceSpeedIntervalParam) -> bool: ...**

更新限速时段

参数: 
[ in ]  param: 限速时段参数, 入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    param = Online.DynaReduceSpeedIntervalParam()
    param.startTime = 3601  # 需要注意新增的时段要和已有时段不冲突
    param.endTime = 7200
    type1 = Online.DynaReduceSpeedVehiTypeParam()
    type1.vehicleTypeCode = 2
    type1.avgSpeed = 10
    type1.speedSD = 5
    param.mlReduceSpeedVehicleTypeParam = [type1]
    interval = reduceSpeedArea.addReduceSpeedInterval(param)
    reduceSpeedArea.removeReduceSpeedInterval(interval.id())
    interval1 = reduceSpeedArea.addReduceSpeedInterval(param)
    print(f" reduceSpeedArea.addReduceSpeedInterval(param) 添加成功={interval1}")
    param.id = interval1.id()
    param.reduceSpeedAreaId = reduceSpeedArea.id()
    param.startTime = 7200
    param.endTime = 10000
    flag = reduceSpeedArea.updateReduceSpeedInterval(param)
    print(f" reduceSpeedArea.updateReduceSpeedInterval(param) 更新成功={flag}")
```

 **def reduceSpeedIntervals(self) -> Type.List< Tess.IReduceSpeedInterval >: ...**

获取限速时段列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"获取限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
```

 **def findReduceSpeedIntervalById(self, id: int) -> Tess.IReduceSpeedInterval: ...**

根据ID获取限速时段

参数: 
[ in ] id: 限速时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"根据ID获取限速时段={reduceSpeedArea.findReduceSpeedIntervalById(reduceSpeedArea.reduceSpeedIntervals()[0].id())}")
```

 **def findReduceSpeedIntervalByStartTime(self, startTime: int) -> Tess.IReduceSpeedInterval: ...**

根据起始时间获取限速时段

参数: 
[ in ] startTime: 起始时间


举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"根据起始时间获取限速时段={reduceSpeedArea.findReduceSpeedIntervalByStartTime(reduceSpeedArea.reduceSpeedIntervals()[0].intervalStartTime())}")
```

 **def polygon(self) -> QPolygonF : ...**

获取限速区获取多边型轮廓

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    print(f"获取限速区获取多边型轮廓={reduceSpeedArea.polygon()}")
```

### 2.29. IReduceSpeedInterval

限速时段接口

 **def id(self) -> int: ...**

获取限速时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        print(f"获取限速时段ID={reduceSpeedInterval.id()}")
```

 **def reduceSpeedAreaId(self) -> int: ...**

获取所属限速区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        print(f"获取所属限速区ID={reduceSpeedInterval.reduceSpeedAreaId()}")
```

 **def intervalStartTime(self) -> int: ...**

获取开始时间

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        print(f"获取开始时间={reduceSpeedInterval.intervalStartTime()}")
```

 **def intervalEndTime(self) -> int: ...**

获取结束时间

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        print(f"获取结束时间={reduceSpeedInterval.intervalEndTime()}")
```

 **def addReduceSpeedVehiType(self, param: Online.DynaReduceSpeedVehiTypeParam) -> Tess.IReduceSpeedVehiType: ...**

添加限速车型

参数: 
[ in ] param: 限速车型参数, 数据结构见Online.DynaReduceSpeedVehiTypeParam

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        param = Online.DynaReduceSpeedVehiTypeParam()
        param.vehicleTypeCode = 13
        param.avgSpeed = 10
        param.speedSD = 5
        interval = reduceSpeedInterval.addReduceSpeedVehiType(param)
        print(f"添加限速车型成功={interval}")
```

 **def removeReduceSpeedVehiType(self, id: int) -> bool: ...**

移除限速车型

参数: 
[ in ]  id: 限速车型ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"移除限速车型{reduceSpeedVehiType.id()}")
            reduceSpeedInterval.removeReduceSpeedVehiType(reduceSpeedVehiType.id())
```

 **def updateReduceSpeedVehiType(self, param: Online.DynaReduceSpeedVehiTypeParam) -> Tess.IReduceSpeedVehiType: ...**

更新限速车型

参数: 
[ in ] param: 限速车型参数, 数据结构见Online.DynaReduceSpeedVehiTypeParam

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        vehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        vehiType = vehiTypes[-1]

        param = Online.DynaReduceSpeedVehiTypeParam()
        param.vehicleTypeCode = vehiType.vehiTypeCode()
        param.avgSpeed = vehiType.averageSpeed() + 10
        param.speedSD = vehiType.speedStandardDeviation() + 5
        param.reduceSpeedAreaId = reduceSpeedArea.id()
        param.reduceSpeedIntervalId = reduceSpeedInterval.id()
        param.id = vehiType.id()
        b = reduceSpeedInterval.updateReduceSpeedVehiType(param)
        print(f"更新限速车型成功={b}")
```

 **def reduceSpeedVehiTypes(self) -> Type.List< Tess.IReduceSpeedVehiType >: ...**

获取本时段限速车型及限速参数列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        print(f"获取本时段限速车型及限速参数列表={reduceSpeedInterval.reduceSpeedVehiTypes()}")
```

 **def findReduceSpeedVehiTypeById(self, id: int) -> Tess.IReduceSpeedVehiType: ...**

根据车型代码获取限速车型

参数: 
[ in ] id: 限速车型ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"根据ID获取限速车型={reduceSpeedInterval.findReduceSpeedVehiTypeById(reduceSpeedVehiType.id())}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showLimitZoneAttr(netiface)
def showReduceSpeedAreaAttr(netiface): 
    reduceSpeedAreas = netiface.reduceSpeedAreas()
    if len(reduceSpeedAreas) > 0: 
        reduceSpeedArea = reduceSpeedAreas[0]
        reduceSpeedArea1 = netiface.findReduceSpeedArea(reduceSpeedAreas[0].id())

        print(f"获取限速区ID={reduceSpeedArea.id()}, 获取限速区名称={reduceSpeedArea.name()}"
              f", 获取距起点距离, ={reduceSpeedArea.location()}, 米制={reduceSpeedArea.location(UnitOfMeasure.Metric)}"
              f"获取限速区长度, 像素制={reduceSpeedArea.areaLength()}, 米制={reduceSpeedArea.areaLength(UnitOfMeasure.Metric)}, "
              f"获取路段或连接段ID={reduceSpeedArea.sectionId()}, "
              f"获取车道序号={reduceSpeedArea.laneNumber()}, "
              f"获取目标车道序号={reduceSpeedArea.toLaneNumber()}, "
              f"获取限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}, "
              f"获取多边型轮廓={reduceSpeedArea.polygon()}")
        print("添加限速时段时段")
        print(f"获取所有限速时段={reduceSpeedArea.reduceSpeedIntervals()}, "
              f"根据ID获取限速时段 ={reduceSpeedArea.findReduceSpeedIntervalById(reduceSpeedArea.reduceSpeedIntervals()[0].id())}, "
              f"根据开始时间查询事故时段={reduceSpeedArea.findReduceSpeedIntervalByStartTime(reduceSpeedArea.reduceSpeedIntervals()[0].intervalStartTime())}")

    param = Online.DynaReduceSpeedIntervalParam()
    param.startTime = 100 # 需要注意新增的时段要和已有时段不冲突
    param.endTime = 500
    type1 = Online.DynaReduceSpeedVehiTypeParam()
    type1.vehicleTypeCode = 2
    type1.avgSpeed = 10
    type1.speedSD = 5
    param.mlReduceSpeedVehicleTypeParam = [type1, ]
    interval = reduceSpeedArea.addReduceSpeedInterval(param)
    reduceSpeedArea.removeReduceSpeedInterval(interval.id())
    interval1 = reduceSpeedArea.addReduceSpeedInterval(param)
    print(f" reduceSpeedArea.addReduceSpeedInterval(param) 添加成功={interval1}")
    flag = reduceSpeedArea.updateReduceSpeedInterval(param)
    print(f" reduceSpeedArea.updateReduceSpeedInterval(param) 更新成功={flag}")
```





### 2.30. IReduceSpeedVehiType

限速车型接口

 **def id(self) -> int: ...**

获取限速车型ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取限速车型ID={reduceSpeedVehiType.id()}")
```

 **def intervalId(self) -> int: ...**

获取所属限速时段ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取所属限速时段ID={reduceSpeedVehiType.intervalId()}")
```

 **def reduceSpeedAreaId(self) -> int: ...**

获取所属限速区ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取所属限速区ID={reduceSpeedVehiType.reduceSpeedAreaId()}")
```

 **def vehiTypeCode(self) -> int: ...**

获取车型编码

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取车型编码={reduceSpeedVehiType.vehiTypeCode()}")
```

 **def averageSpeed(self, unit: UnitOfMeasure) -> double: ...**

获取平均车速, 默认单位: 像素/秒, 可通过unit参数设置单位 

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取平均车速={reduceSpeedVehiType.averageSpeed()}")
            print(f"获取平均车速, 单位: 米/秒={reduceSpeedVehiType.averageSpeed(UnitOfMeasure.Metric)}")
```

 **def speedStandardDeviation(self, unit: UnitOfMeasure) -> double: ...**

获取车速标准差, 默认单位: 像素/秒, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas: 
    lReduceSpeedIntervals = reduceSpeedArea.reduceSpeedIntervals()
    for reduceSpeedInterval in lReduceSpeedIntervals: 
        lReduceSpeedVehiTypes = reduceSpeedInterval.reduceSpeedVehiTypes()
        for reduceSpeedVehiType in lReduceSpeedVehiTypes: 
            print(f"获取车速标准差={reduceSpeedVehiType.speedStandardDeviation()}")
            print(f"获取车速标准差, 单位: 米/秒={reduceSpeedVehiType.speedStandardDeviation(UnitOfMeasure.Metric)}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
_showReduceSpeedAreaIntervalAttr(netiface)
reduceSpeedAreas = netiface.reduceSpeedAreas()
if len(reduceSpeedAreas) > 0: 
    reduceSpeedArea = reduceSpeedAreas[0]
    _showReduceSpeedAreaIntervalAttr(reduceSpeedArea)
    
def _showReduceSpeedAreaIntervalAttr(reduceSpeedArea): 
    interval = reduceSpeedArea.reduceSpeedIntervals()[0]
    print(f"获取限速时段ID={interval.id()}, 获取所属限速区ID={interval.reduceSpeedAreaId()}, "
          f"获取开始时间={interval.intervalStartTime()}, 获取结束时间={interval.intervalEndTime()}, ")

    reduceSpeedVehiTypes = interval.reduceSpeedVehiTypes()
    reduceSpeedVehiType = interval.findReduceSpeedVehiTypeById(reduceSpeedVehiTypes[0].id())

    reduceSpeedVehiType1 = interval.findReduceSpeedVehiTypeByCode(reduceSpeedVehiTypes[0].vehiTypeCode())
```





### 2.31. ITollLane

收费车道接口

 **def id(self) -> int: ...**

获取收费车道ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"获取收费车道ID={tollLane.id()}")
```

 **def name(self) -> str: ...**

获取收费车道名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"获取收费车道名称={tollLane.name()}")
```

 **def distance(self) -> double: ...**

获取收费车道起点距当前所在路段起始位置的距离。单位: 米 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"获取收费车道起点距当前所在路段起始位置的距离={tollLane.distance()}")
```

 **def setName(self, name: str) -> None: ...**

设置收费车道名称 

参数: 
[ in ] name : 收费车道名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"设置收费车道名称={tollLane.setName('test lane toll')}")
    print(f"获取收费车道名称={tollLane.name()}")
```

 **def setWorkTime(self, startTime: int, endTime: int) -> None: ...**

设置收费车道的工作时间, 不设置时, 默认与仿真时间对应 

参数: 
[ in ] startTime 开始时间（秒）  
[ in ] endTime 结束时间（秒）

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"设置收费车道的工作时间={tollLane.setWorkTime(0, 3000)}")
```

 **def dynaTollLane(self) -> typing.List<Online.TollStation.DynaTollLane>: ...**

获取动态收费车道信息, 具体数据结构见Online.TollStation.DynaTollLane  

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"获取动态收费车道信息={tollLane.dynaTollLane()}")
```

 **def tollPoints(self) -> typing.List<Tessng.ITollPoint>: ...**

获取收费车道所有收费点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    print(f"获取收费车道所有收费点={tollLane.tollPoints()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showTollLaneAttr(netiface)
def showTollLaneAttr(netiface): 
    rs = netiface.tollLanes()
    r = rs[0]
    r1 = netiface.findTollLane(rs[0].id())
    print(f"获取收费车道ID={r.id()}, 获取收费车道名称={r.name()}"
          f", 获取距路段起始位置, 单位: 米={r.distance()}, "
          f"设置收费车道名称={r.setName('test lane toll')}, "
          f"设置工作时间, 工作时间与仿真时间对应={r.setWorkTime(0, 3000)}, "
          f"获取动态收费车道信息={r.dynaTollLane()}, "
          f"获取收费车道所有收费点={r.tollPoints()}, ")
```






### 2.32. ITollDecisionPoint

收费决策点接口

 **def id(self) -> int: ...**

获取收费决策点ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点ID={tollDecisionPoint.id()}")
```

 **def name(self) -> str: ...**

获取收费决策点名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点名称={tollDecisionPoint.name()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取收费决策点所在路段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点所在路段={tollDecisionPoint.link()}")
```

 **def distance(self) ->double: ...**

获取收费决策点距离所在路段起点的距离, 默认单位为米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点距离所在路段起点的距离={tollDecisionPoint.distance()}")
```

 **def routings(self) ->Type.List<Tess.ITollRouting>: ...**

获取收费决策点的所有收费路径

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点的所有收费路径={tollDecisionPoint.routings()}")
```

 **def tollDisInfoList(self) ->Type.List<Online.TollStation.TollDisInfo>: ...**

获取收费决策点收费路径分配信息列表, 返回值为 TollDisInfo; 数据结构见Online.TollStation.TollDisInfo

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点收费路径分配信息列表={tollDecisionPoint.tollDisInfoList()}")
```

 **def updateTollDisInfoList(self, tollDisInfoList: Type.List<Online.TollStation.DynaTollDisInfo>) ->None: ...**

更新收费分配信息列表, 先创建决策点, 再更新决策点的车道分配信息 

参数: 
[ in ] Online.TollStation.DynaTollDisInfo: 收费分配信息列表 数据结构见Online.TollStation.DynaTollDisInfo

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"更新收费分配信息列表={tollDecisionPoint.updateTollDisInfoList(tollDecisionPoint.tollDisInfoList())}")
```

 **def polygon(self) -> QPolygonF: ...**

获取收费决策点多边形轮廓

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    print(f"获取收费决策点多边形轮廓={tollDecisionPoint.polygon()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showTollDecisionPointAttr(netiface)
def showTollDecisionPointAttr(netiface): 
    rs = netiface.tollDecisionPoints()
    r = rs[0]
    r1 = netiface.findDecisionPoint(rs[0].id())

    print(f"获取收费决策点ID={r.id()}, 获取收费决策点名称={r.name()}, 获取收费决策点所在路段={r.link()}, "
          f", 获取距路段起始位置, 单位: 米={r.distance()}, "
          f"获取相关收费路径={r.routings()}, "
          f"获取收费分配信息列表={r.tollDisInfoList()}, "
          f"更新收费分配信息列表={r.updateTollDisInfoList(r.tollDisInfoList())}, " 
          f"获取收费决策点多边型轮廓={r.polygon()}, ")
```





### 2.33. ITollRouting

收费路径接口

 **def id(self) -> int: ...** 

获取收费路径ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取收费路径ID={tollRouting.id()}")
```

 **def tollDeciPointId(self) -> int: ...**

获取收费路径所属收费决策点ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取收费路径所属收费决策点ID={tollRouting.tollDeciPointId()}")
```

 **def tollLaneId(self) -> int: ...**

获取路径到达的收费区域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取路径到达的收费区域id={tollRouting.tollLaneId()}")
```

 **def calcuLength(self) -> double: ...**

获取收费决策路径长度, 单位: 米; 收费路径长度是指: 收费决策点到收费车道

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取收费决策路径长度, 单位: 米={tollRouting.calcuLength()}")
```

 **def contain(self, pRoad: Tessng.ISection) -> boolen: ...**

判断输入的路段是否在当前路径上 

参数: 
[ in ] pRoad : 路段或连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"判断输入的路段是否在当前路径上={tollRouting.contain(tollDecisionPoint.link())}")
```

 **def nextRoad(self, pRoad: Tessng.ISection) -> Tessng.ISection: ...**

获取输入路段的紧邻下游道路 

参数: 
[ in ] pRoad : 路段或连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取输入路段的紧邻下游道路={tollRouting.nextRoad(tollDecisionPoint.link())}")
```

 **def getLinks(self) -> Type.List<Tess.ILink>: ...**

获取当前收费路径的有序路段序列

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints: 
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings: 
    print(f"获取当前收费路径的有序路段序列={tollRouting.getLinks()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showTollRoutingAttr(netiface)
def showTollRoutingAttr(netiface): 
    tollDecisionPoints = netiface.tollDecisionPoints()
    tollDecisionPoint = tollDecisionPoints[0]
    routes = tollDecisionPoint.routings()
    r = routes[0]
    print(f"获取路径ID={r.id()}, 获取所属收费决策点ID={r.tollDeciPointId()}, "
          f", 计算路径长度, 单位: 米={r.calcuLength()}, "
          f"根据所给道路判断是否在当前路径上={r.contain(tollDecisionPoint.link())}, "
          f"根据所给道路求下一条道路={r.nextRoad(tollDecisionPoint.link())}, "
          f"获取路段序列={r.getLinks()}, ")
```





### 2.34. ITollPoint

收费站停车点接口

 **def id(self) -> int: ...**

获取收费站停车点位ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取收费站停车点位ID={tollPoint.id()}")
```

 **def distance(self) -> double: ...**

获取收费站停车点距离路段起始位置的距离, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取收费站停车点距离路段起始位置的距离, 单位: 米={tollPoint.distance()}")
```

 **def tollLaneId(self) -> int: ...**

获取收费站停车点所在的车道ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取收费站停车点所在的车道ID={tollPoint.tollLaneId()}")
```

 **def tollLane(self) -> TypeList<Tess.ITollLane>: ...**

获取所属收费车道

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取所属收费车道={tollPoint.tollLane()}")
```

 **def isEnabled() -> bool: ...**

获取是否启用的状态

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取是否启用的状态={tollPoint.isEnabled()}")
```

 **def setEnabled(self, enabled: bool) -> bool: ...**

设置当前收费站停车点是否启用, 返回是否设置成功的标签 

参数: 
[ in ] enabled : 默认为True表示启用, 若传入False则表明禁用该收费站点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"设置当前收费站停车点是否启用={tollPoint.setEnabled(False)}")
    print(f"获取当前收费站停车点是否启用={tollPoint.isEnabled()}")
```

 **def tollType(self) -> int: ...**

获取收费类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取收费类型={tollPoint.tollType()}")
```

 **def setTollType(self, tollType: int) -> bool: ...**

设置收费类型 

参数: 
[ in ] tollType: 收费类型, 数据结构 见OnLine.TollType

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"设置收费类型={tollPoint.setTollType(tollPoint.tollType())}")
    print(f"获取收费类型={tollPoint.tollType()}")
```

 **def timeDisId(self) -> int: ...**

获取停车时间分布ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"获取停车时间分布ID={tollPoint.timeDisId()}")
```

 **def setTimeDisId(self, timeDisId: int) -> bool: ...**

设置停车时间分布ID 

参数: 
[ in ] timeDisId: 时间分布ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes: 
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints: 
    print(f"设置停车时间分布ID={tollPoint.setTimeDisId(tollPoint.timeDisId())}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showTollPointAttr(netiface)
def showTollPointAttr(netiface): 
    tollLanes = netiface.tollLanes()
    tollLane = tollLanes[0]
    rs= tollLane.tollPoints()
    r = rs[0]

    print(f"获取收费点ID={r.id()}, 获取所属收费车道ID={r.tollLaneId()}"
          f", 获取距路段起始位置, 单位: 米={r.distance()}, "
          f"获取是否启用={r.isEnabled()}, "
          f"设置启用状态={r.setEnabled(True)}, "
          f"获取收费类型={r.tollType()}, 设置收费类型={r.setTollType(r.tollType())}, "
          f"获取停车时间分布ID={r.timeDisId()}, 设置停车时间分布ID={r.setTimeDisId(r.timeDisId())}")
```



### 2.35. IParkingStall

停车位接口

 **def id(self) -> int: ...**

获取停车位ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls: 
        print(f"获取停车位ID={parkingStall.id()}")
```

 **def parkingRegionId(self) -> int: ...**

获取所属停车区域ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls: 
        print(f"获取所属停车区域ID={parkingStall.parkingRegionId()}")
```

 **def parkingRegion(self) -> Tess.IParkingRegion: ...**

获取所属停车区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls: 
        print(f"获取所属停车区域={parkingStall.parkingRegion()}")
```

 **def distance(self) -> double: ...**

获取距路段起始位置, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls: 
        print(f"获取距路段起始位置, 单位: 米={parkingStall.distance()}")
```

 **def stallType(self) -> int: ...**

获取车位类型, 与车辆类型编码一致

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls: 
        print(f"获取车位类型, 与车辆类型编码一致={parkingStall.stallType()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showParkingStallAttr(netiface)
def showParkingStallAttr(netiface): 
    parkingRegions = netiface.parkingRegions()
    parkingRegion = parkingRegions[0]
    rs = parkingRegion.parkingStalls()
    if len(rs) > 0: 
        r = rs[0]
        print(f"获取停车位ID={r.id()}, 获取所属停车区域ID={r.parkingRegionId()}, "
              f"获取距路段起始位置, 单位: 米={r.distance()}, "
              f"获取车位类型, 与车辆类型编码一致={r.stallType()}, ")
```




### 2.36. IParkingRegion

停车区域接口

 **def id(self) -> int: ...**

获取停车区域ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    print(f"获取停车区域ID={parkingRegion.id()}")
```

 **def name(self) -> str: ...**

获取所属停车区域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    print(f"获取停车区域名称={parkingRegion.name()}")
```

 **def setName(self, name: str) -> None: ...**

设置停车区域名称 

参数: 
[ in ] name : 停车区域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    print(f"设置停车区域名称={parkingRegion.setName('test parking name')}")
    print(f"获取停车区域名称={parkingRegion.name()}")
```

 **def parkingStalls(self) -> Type.List<Tess.IParkingStall>: ...**

获取所有停车位, 返回列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    print(f"获取所有停车位, 返回列表={parkingRegion.parkingStalls()}")
```

 **def dynaParkingRegion(self) -> Online.ParkingLot.DynaParkingRegion : ...**

获取动态停车区域信息

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions: 
    print(f"获取动态停车区域信息={parkingRegion.dynaParkingRegion()}")
```

 数据结构见pyi文件的Online.ParkingLot.DynaParkingRegion 

**案例代码**

```python
netiface = tessngIFace().netInterface()
showParkingRegionAttr(netiface)
def showParkingRegionAttr(netiface): 
    parkingRegions = netiface.parkingRegions()
    parkingRegion = parkingRegions[0]
    r1 = netiface.findParkingRegion(parkingRegion.id())
    r = parkingRegion
    print(f"获取停车区域ID={r.id()}, 获取停车区域名称={r.name()}"
          f", 获取所有停车位={r.parkingStalls()}, "
          f"设置停车区域名称={r.setName('test parking name')}, "
          f"获取动态停车区域信息={r.dynaParkingRegion()}, ")
```




### 2.37. IParkingDecisionPoint

停车决策点接口

 **def id(self) -> int: ...**

获取停车决策点ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取停车决策点ID={parkingDecisionPoint.id()}")
```

 **def name(self) -> str: ...**

获取停车决策点名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取停车决策点名称={parkingDecisionPoint.name()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取停车决策点所在路段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取停车决策点所在路段={parkingDecisionPoint.link()}")
```

 **def distance(self) -> double: ...**

获取停车决策点距离所在路段起点的距离, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取停车决策点距离所在路段起点的距离, 单位: 米={parkingDecisionPoint.distance()}")
```

 **def routings(self) -> Type.List<Tessng.IParkingRouting>: ...**

获取当前停车决策点对应的所有停车路径

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取当前停车决策点对应的所有停车路径={parkingDecisionPoint.routings()}")
```

**def updateParkDisInfo(self, tollDisInfoList: Type.List<Online.ParkingLot.DynaParkDisInfo>)->bool**

更新停车分配信息, 先构建停车决策点, 再通过此更新方法补充完善停车分配信息

参数: 
[ in ] tollDisInfoList: 停车分配信息列表, 见pyi文件的Online.ParkingLot.DynaParkDisInfo

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"更新停车分配信息={parkingDecisionPoint.updateParkDisInfo(parkingDecisionPoint.parkDisInfoList())}")
```

**def parkDisInfoList()->Type.List<Online.ParkingLot.DynaParkDisInfo >**

获取停车分配信息列表

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取停车分配信息列表={parkingDecisionPoint.parkDisInfoList()}")
```

 **def polygon(self) -> QPolygonF: ...**

获取当前停车决策点多边形轮廓

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    print(f"获取当前停车决策点多边形轮廓={parkingDecisionPoint.polygon()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showParkingDecisionPointAttr(netiface)
def showParkingDecisionPointAttr(netiface): 
    parkingDecisionPoints = netiface.parkingDecisionPoints()
    parkingDecisionPoint = parkingDecisionPoints[0]
    r1 = netiface.findParkingDecisionPoint(parkingDecisionPoint.id())
    r = parkingDecisionPoint
    print(f"获取停车决策点ID={r.id()}, 获取停车决策点名称={r.name()}"
          f", 获取停车决策点所在路段={r.link()}, "
          f"获取停车决策点距路段起点距离, 单位: 米={r.distance()}, 获取停车分配信息列表={r.parkDisInfoList()}, "
          f"获取相关停车路径={r.routings()}, 更新停车分配信息={r.updateParkDisInfo(r.parkDisInfoList())}, "
          f"获取停车决策点多边型轮廓={r.polygon()}")
```




### 2.38. IParkingRouting

停车决策路径接口

 **def id(self) -> int: ...**

获取停车决策路径ID 

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取停车决策路径ID={parkingRouting.id()}")
```

 **def parkingDeciPointId(self) -> int: ...**

获取停车决策路径所属停车决策点的ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取停车决策路径所属停车决策点的ID={parkingRouting.parkingDeciPointId()}")
```

 **def parkingRegionId(self) -> int: ...**

获取路径到达的停车区域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取路径到达的停车区域id={parkingRouting.parkingRegionId()}")
```

 **def calcuLength(self) -> double: ...**

获取停车决策路径的长度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取停车决策路径的长度, 单位: 米={parkingRouting.calcuLength()}")
```

 **def contain(self, pRoad: Tessng.ISection) -> boolen: ...**

判断输入的道路（ 路段或连接段）是否在当前停车决策路径上 

参数: 
[ in ] pRoad : 道路对象, 类型为Tessng.ISection

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"判断输入的道路（ 路段或连接段）是否在当前停车决策路径上={parkingRouting.contain(parkingDecisionPoint.link())}")
```

 **def nextRoad(self, pRoad: Tessng.ISection) -> Tessng.ISection: ...**

获取输入道路的紧邻下游道路 

参数: 
[ in ] pRoad : 道路对象, 类型为Tessng.ISection

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取输入道路的紧邻下游道路={parkingRouting.nextRoad(parkingDecisionPoint.link())}")
```

 **def getLinks(self) -> Type.List<Tessng.ILink>: ...**

获取当前停车路径的有序路段序列

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints: 
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings(): 
        print(f"获取当前停车路径的有序路段序列={parkingRouting.getLinks()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showParkingRoutingAttr(netiface)
def showParkingRoutingAttr(netiface): 
    parkingDecisionPoints = netiface.parkingDecisionPoints()
    parkingDecisionPoint = parkingDecisionPoints[0]
    r1 = parkingDecisionPoint.routings()
    r = r1[0]
    print(f"获取路径ID={r.id()}, 获取所属决策点ID={r.parkingDeciPointId()}"
          f", 计算路径长度={r.calcuLength()}, "
          f"根据所给道路判断是否在当前路径上={r.contain(parkingDecisionPoint.link())}, "
          f"根据所给道路求下一条道路={r.nextRoad(parkingDecisionPoint.link())}, "
          f"获取路段序列={r.getLinks()}")
```



### 2.39. IJunction

节点接口

 **def getId(self) -> int: ...**

获取节点ID   

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"获取节点ID={node.getId()}")
```

 **def name(self) -> int: ...**

获取节点名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"获取节点名称={node.name()}")
```

 **def setName(strName: str) -> int: ...**

设置节点名称  
[ in ] strName : 节点名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"设置节点名称={node.setName('new_' + node.name())}")
    print(f"获取节点名称={node.name()}")
```

 **def getJunctionLinks(self) -> Tess.ILink: ...**

获取节点内的路段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"获取节点内的路段={node.getJunctionLinks()}")
```

 **def getJunctionConnectors(self) -> Type.List<Tess.Connector>: ...**

获取节点内的连接段

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"获取节点内的连接段={node.getJunctionConnectors()}")
```

 **def getAllTurnningInfo(self) ->Type.List<Online.Junction.TurnningBaseInfo>: ...**

获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"获取节点内的流向信息={node.getAllTurnningInfo()}")
```

 **def getTurnningInfo(self, turningId) ->Online.Junction.TurnningBaseInfo: ...**

参数: 
[ in ] turningId: 转向编号

根据转向编号获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes: 
    print(f"根据转向编号获取节点内的流向信息={node.getTurnningInfo()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showJunctionAttr(netiface)
def showJunctionAttr(netiface): 
    nodes = netiface.getAllJunctions()
    node = netiface.findJunction(nodes[0].id())
    node = netiface.findSignalControllerByName(node.name())
    print(
        f"路网中的节点总数={len(nodes)}, 节点编号={nodes[0].id()}的具体信息: "
        f"获取节点ID={node.getId()}, 名称={node.name()}, 设置新名字={node.setName('new_' + node.name())}, "
        f"获取节点内的路段={node.getJunctionLinks()}, "
        f"获取节点内的连接段={node.getJunctionConnectors()}, "
        f"获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getAllTurnningInfo()}, "
        f"根据转向编号获取节点内的流向信息, Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getTurnningInfo()}")
```






### 2.40. IPedestrian

行人接口

 **def getId(self) -> int: ...**

获取行人ID

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人ID={ped.getId()}")
```

 **def getRadius(self) -> double: ...**

获取行人半径大小, 单位: 米

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人半径大小, 单位: 米={ped.getRadius()}")
```

 **def getWeight(self) -> double: ...**

获取行人质量, 单位: 千克

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人质量, 单位: 千克={ped.getWeight()}")
```

 **def getColor(self) -> double: ...**

获取行人颜色, 十六进制颜色代码, 如"#EE0000"

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人颜色={ped.getColor()}")
```

 **def getPos(self) -> QPointF: ...**

获取行人当前位置（瞬时位置）, 像素坐标系下的坐标点, 单位: 米

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前位置（瞬时位置）, 像素坐标系下的坐标点, 单位: 米={ped.getPos()}")
```

 **def getAngle(self) -> double: ...**

获取行人当前角度, QT像素坐标系下, X轴正方向为0, 逆时针为正, 单位: 度 

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前角度, QT像素坐标系下, X轴正方向为0, 逆时针为正, 单位: 度={ped.getAngle()}")
```

 **def getDirection(self) -> Array: ...**

获取行人当前方向向量, 二维向量

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前方向向量, 二维向量={ped.getDirection()}")
```

 **def getElevation(self) -> double: ...**

获取行人当前位置的高程, 单位: 米

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前位置的高程, 单位: 米={ped.getElevation()}")
```

 **def getSpeed(self) -> double: ...**

获取行人当前速度, 单位: 米/秒

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前速度, 单位: 米/秒={ped.getSpeed()}")
```

 **def getDesiredSpeed(self) -> double: ...**

获取行人期望速度, 单位: 米/秒

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人期望速度, 单位: 米/秒={ped.getDesiredSpeed()}")
```

 **def getMaxSpeed(self) -> double: ...**

获取行人最大速度限制, 单位: 米/秒

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人最大速度限制, 单位: 米/秒={ped.getMaxSpeed()}")
```

 **def getAcce(self) -> double: ...**

获取行人当前加速度, 单位: 米/秒²

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前加速度, 单位: 米/秒²={ped.getAcce()}")
```

 **def getMaxAcce(self) -> double: ...**

获取行人最大加速度限制, 单位: 米/秒²

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人最大加速度限制, 单位: 米/秒²={ped.getMaxAcce()}")
```

 **def getEuler(self) -> Type.List: ...**

获取行人欧拉角, 单位: 度

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人欧拉角, 用于三维的信息展示和计算, 单位: 度={ped.getEuler()}")
```

 **def getSpeedEuler(self) -> Type.List: ...**

获取行人速度欧拉角, 单位: 度

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人速度欧拉角, 用于三维的信息展示和计算, 单位: 度={ped.getSpeedEuler()}")
```

 **def getWallFDirection(self) ->Type.List: ...**

获取墙壁方向单位向量

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取墙壁方向单位向量={ped.getWallFDirection()}")
```

 **def getRegion(self) -> Tess.IPedestrianRegion: ...**

获取行人当前所在面域

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人当前所在面域={ped.getRegion()}")
```

 **def getPedestrianTypeId(self) -> int: ...**

获取行人类型ID

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"获取行人类型ID={ped.getPedestrianTypeId()}")
```

 **def stop(self) -> None: ...**

停止仿真, 会在下一个仿真批次移除当前行人, 释放资源

举例: 

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian: 
    print(f"停止仿真, 会在下一个仿真批次移除当前行人, 释放资源={ped.stop()}")
```

**案例代码**

```python
netiface = tessngIFace().simuInterface()
showJunctionAttr(simuiface)
def showJunctionAttr(simuiface): 
    allPedestrian = simuiface.allPedestrianStarted()
    if len(allPedestrian) > 0: 
        ped = allPedestrian[0]
        print(
            f"获取行人ID={ped.getId()}, "
            f"获取行人半径大小, 单位: 米={ped.getRadius()}, "
            f"获取行人质量, 单位: 千克={ped.getWeight()}, "
            f"获取行人颜色, 十六进制颜色代码, 如#EE0000={ped.getColor()}, "
            f"设置面域颜色={ped.setRegionColor(QColor('red'))}, "
            f"获取行人当前位置（瞬时位置）, 像素坐标系下的坐标点, 单位: 米; ={ped.getPos()}, "
            f"获取行人当前角度, QT像素坐标系下, X轴正方向为0, 逆时针为正, 单位: 度; ={ped.getAngle()}, "
            f"获取行人当前方向向量, 二维向量; ={ped.getDirection()}, "
            f"获取行人当前位置的高程, 单位: 米={ped.getElevation()}, "
            f"获取行人当前速度, 单位: 米/秒={ped.getSpeed()}, "
            f"获取行人期望速度, 单位: 米/秒={ped.getDesiredSpeed()}, "
            f"获取行人最大速度限制, 单位: 米/秒={ped.getMaxSpeed()}, "
            f"获取行人当前加速度, 单位: 米/秒²={ped.getAcce()}, "
            f"获取行人最大加速度限制, 单位: 米/秒²={ped.getMaxAcce()}, "
            f"获取行人欧拉角, 用于三维的信息展示和计算, 单位: 度={ped.getEuler()}, "
            f"获取行人速度欧拉角, 用于三维的信息展示和计算, 单位: 度={ped.getSpeedEuler()}, "
            f"获取墙壁方向单位向量={ped.getWallFDirection()}, "
            f"获取行人当前所在面域={ped.getRegion()}, "
            f"获取行人类型ID={ped.getPedestrianTypeId()}, "
            f"停止当前行人仿真运动, 会在下一个仿真批次移除当前行人, 释放资源={ped.stop()}")

```




### 2.41. IPedestrianPathRegionBase

行人可通行路径面域基类接口, 用例见下文子类

 **int getId()**

获取面域id

 **String getName()**

获取面域名称

 **void setName(String name);**

设置面域名称

[ in ] name: 面域名称

 **def setRegionColor(QColor color);**

获取面域颜色, 返回pyside2的QColor类型

 **def setRegionColor(QColor color);**

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

**def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域ID={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('new_' + region.getName())}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置面域位置, 像素制={region.setPosition(region.getPosition())}, "
          f"设置面域位置, 米制={region.setPosition(region.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置面域所在图层, 如果图层ID非法, 则不做任何改变={region.setLayerId(region.getLayerId())}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def getWidth(self) -> int: ...**

获取人行道(面域)宽度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取人行道(面域)宽度, 单位: 米={region.getWidth()}")
```

 **def setWidth(self, width: double) -> None: ...**

设置人行道(面域)宽度, 单位: 米

参数: 
[ in ] width: 宽度

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"设置人行道(面域)宽度, 单位: 米={region.setWidth(region.getWidth()+0.1)}")
    print(f"获取人行道(面域)宽度, 单位: 米={region.getWidth()}")
```

 **def getVetexs(self) ->  Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)顶点, 即初始折线顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取人行道(面域)顶点, 即初始折线顶点={region.getVetexs()}")
```

 **def getControl1Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)贝塞尔曲线控制点P1

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取人行道(面域)贝塞尔曲线控制点P1={region.getControl1Vetexs()}")
```

 **def getControl2Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)贝塞尔曲线控制点P2

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取人行道(面域)贝塞尔曲线控制点P2={region.getControl2Vetexs()}")
```

 **def getCandidateVetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)候选顶点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"获取人行道(面域)候选顶点={region.getCandidateVetexs()}")
```

 **def removeVetex(self, index: int) ->None: ...**

删除人行道(面域)的第index个顶点: 顺序: 按照人行横道的绘制顺序排列

参数: 
[ in ] index: 顶点索引

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"删除人行道(面域)的第index个顶点={region.removeVetex(1)}")
```

 **def insertVetex(self, pos: QPointF, index: int) ->None: ...**

在人行道(面域)的第index的位置插入顶点, 初始位置为pos: 顺序: 按照人行横道的绘制顺序排列

参数: 
[ in ] pos: 顶点位置
[ in ] index: 顶点索引

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion: 
    print(f"在第index个位置插入顶点, 初始位置为pos={region.insertVetex(QPointF(region.getCandidateVetexs()[0].pos().x()+0.1, region.getCandidateVetexs()[0].pos().y()+0.1), 0)}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
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
        print(f"在第index个位置插入顶点, 初始位置为pos={r.insertVetex(QPointF(r.getCandidateVetexs()[0].pos().x()+0.1, r.getCandidateVetexs()[0].pos().y()+0.1), 0)}, "
              f"删除第index个顶点={r.removeVetex(1)}")
        #
        print(f"在第index个位置插入顶点, 初始位置为pos={r.insertVetex(QPointF(100, 100), 0)}, "
              f"删除第index个顶点={r.removeVetex(1)}")

```






### 2.46. IPedestrianCrossWalkRegion

人行横道区域接口

**def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def getWidth(self) -> double: ...**

获取人行横道宽度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道宽度, 单位: 米={region.getWidth()}")
```

 **def setWidth(self, width: double) -> double: ...**

设置行人横道宽度, 单位: 米

参数: 
[ in ] width: 宽度

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置行人横道宽度, 单位: 米={region.setWidth(1.5)}")
    print(f"获取行人横道宽度, 单位: 米={region.getWidth()}")
```

 **def getSceneLine(self, unit: UnitOfMeasure) -> QLineF: ...**  

获取人行横道起点到终点的线段, QT场景坐标系, 场景坐标系下, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道起点到终点的线段, 场景坐标系, 默认单位: 像素={region.getSceneLine()}")
    print(f"获取人行横道起点到终点的线段, 场景坐标系, 米制={region.getSceneLine(UnitOfMeasure.Metric)}")
```

 **def getAngle(self) -> double: ...**  

获取人行横道倾斜角度, 单位: 度, QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道倾斜角度, 单位: 度={region.getAngle()}")
```

 **def setAngle(self, angle: double) -> None: ...**

设置人行横道倾斜角度, 单位: 度, QT像素坐标系下, X轴正方向为0, 逆时针为正

参数: 
[ in ] angle: 角度

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置人行横道倾斜角度, 单位: 度={region.setAngle(5)}")
    print(f"获取人行横道倾斜角度, 单位: 度={region.getAngle()}")
```

 **def getRedLightSpeedFactor(self) -> double: ...**

获取人行横道上红灯清尾速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道上红灯清尾速度系数={region.getRedLightSpeedFactor()}")
```

 **def setRedLightSpeedFactor(self, factor: double) -> None: ...**

设置人行横道上红灯清尾速度系数  

参数: 
[ in ] factor : 红灯清尾速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"设置人行横道上红灯清尾速度系数={region.setRedLightSpeedFactor(1.5)}")
    print(f"获取人行横道上红灯清尾速度系数={region.getRedLightSpeedFactor()}")
```

 **def getUnitDirectionFromStartToEnd(self) -> Type.List: ...**

获取人行横道起点到终点的在场景坐标系下的单位方向向量, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道起点到终点的在场景坐标系下的单位方向向量={region.getUnitDirectionFromStartToEnd()}")
```

 **def getLocalUnitDirectionFromStartToEnd(self) -> Type.List: ...**

获取人行横道本身坐标系下从起点到终点的单位方向

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道本身坐标系下从起点到终点的单位方向={region.getLocalUnitDirectionFromStartToEnd()}")
```

 **def getStartControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道起点控制点, 场景坐标系

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道起点控制点, 场景坐标系={region.getStartControlPoint()}")
```

 **def getEndControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道终点控制点, 场景坐标系

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道终点控制点, 场景坐标系下={region.getEndControlPoint()}")
```

 **def getLeftControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道左侧控制点, 场景坐标系

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道左侧控制点, 场景坐标系={region.getLeftControlPoint()}")
```

 **def getRightControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道右侧控制点, 场景坐标系

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道右侧控制点, 场景坐标系={region.getRightControlPoint()}")
```

 **def getPositiveDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**

获取人行横道上管控正向通行的信号灯对象

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道上管控正向通行的信号灯对象={region.getPositiveDirectionSignalLamp()}")
```

 **def getNegativeDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**

获取人行横道上管控反向通行的信号灯对象

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"获取人行横道上管控反向通行的信号灯对象={region.getNegativeDirectionSignalLamp()}")
```

 **def isPositiveTrafficLightAdded(self) -> boolen: ...**

判断人行横道上是否存在管控正向通行的信号灯

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"判断人行横道上是否存在管控正向通行的信号灯={region.isPositiveTrafficLightAdded()}")
```

 **def isReverseTrafficLightAdded(self) -> boolen: ...**

判断人行横道上是否存在管控反向通行的信号灯

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion: 
    print(f"判断人行横道上是否存在管控反向通行的信号灯={region.isReverseTrafficLightAdded()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianCrossWalkRegionAttr(netiface)
def showPedestrianCrossWalkRegionAttr(netiface): 
    allCrossWalkRegion = netiface.pedestrianCrossWalkRegions()
    if len(allCrossWalkRegion) > 0: 
        crossWalkRegion = allCrossWalkRegion[0]
        print(
              f"获取面域ID={crossWalkRegion.getId()}, "
              f"获取面域名称={crossWalkRegion.getName()}, "
              f"设置面域名称={crossWalkRegion.setName('test_area')}, " 
              f"获取面域颜色={crossWalkRegion.getRegionColor()}, "
              f"设置面域颜色={crossWalkRegion.setRegionColor(QColor('red'))}, "
              f"获取面域位置, 默认单位: 像素={crossWalkRegion.getPosition()}, "
              f"获取面域位置, 米制={crossWalkRegion.getPosition(UnitOfMeasure.Metric)}, "
              f"设置面域位置, 像素制={crossWalkRegion.setPosition(crossWalkRegion.getPosition())}, "
              f"设置面域位置, 米制={crossWalkRegion.setPosition(crossWalkRegion.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)}, "
              f"获取面域类型={crossWalkRegion.getGType()}, "
              f"获取期望速度系数={crossWalkRegion.getExpectSpeedFactor()}, "
              f"设置期望速度系数={crossWalkRegion.setExpectSpeedFactor(1.5)}, "
              f"获取面域高程={crossWalkRegion.getElevation() }, "
              f"设置面域高程={crossWalkRegion.setElevation(0.1)}, "
              f"获取面域多边形={crossWalkRegion.getPolygon()}, " 
              f"获取面域所在图层ID={crossWalkRegion.getLayerId() }, 设置面域所在图层, 如果图层ID非法, 则不做任何改变={crossWalkRegion.setLayerId(crossWalkRegion.getLayerId())}")
        print(f"仿真路网中人行横道区域总数={len(allCrossWalkRegion)}, "
              f"获取人行横道宽度, 单位: 米={crossWalkRegion.getWidth()}, 设置人行横道宽度, 单位: 米={crossWalkRegion.setWidth(crossWalkRegion.getWidth()+0.1)}, "
              f"获取人行横道起点到终点的线段, 场景坐标系下={crossWalkRegion.getSceneLine()}, 获取人行横道倾斜角度={crossWalkRegion.getAngle()}, "
              f"设置人行横道倾斜角度={crossWalkRegion.setAngle(5)}, 获取红灯清尾速度系数={crossWalkRegion.getRedLightSpeedFactor()}, "
              f"设置红灯清尾速度系数={crossWalkRegion.setRedLightSpeedFactor(1.5)}, "
              f"获取场景坐标系下从起点到终点的单位方向向量={crossWalkRegion.getUnitDirectionFromStartToEnd()}, "
              f"获取人行横道本身坐标系下从起点到终点的单位方向={crossWalkRegion.getLocalUnitDirectionFromStartToEnd()}, "
              f"获取起点控制点={crossWalkRegion.getStartControlPoint()}, "
              f"获取终点控制点={crossWalkRegion.getEndControlPoint()}, "
              f"获取左侧控制点={crossWalkRegion.getLeftControlPoint()}, "
              f"获取右侧控制点={crossWalkRegion.getRightControlPoint()}, "
              f"判断是否添加了管控正向通行的信号灯={crossWalkRegion.isPositiveTrafficLightAdded()}, "
              f"判断是否添加了管控反向通行的信号灯={crossWalkRegion.isReverseTrafficLightAdded()}, "
              f"获取管控正向通行的信号灯={crossWalkRegion.getPositiveDirectionSignalLamp()}, "
              f"获取管控反向通行的信号灯={crossWalkRegion.getNegativeDirectionSignalLamp()}, ")
```




### 2.47. IPedestrianEllipseRegion

行人椭圆面域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def isObstacle(self) -> bool: ...**

获取面域是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self, b: bool) -> None: ...**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域是否为障碍物={region.setObstacle(True)}")
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

**def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b: bool) -> None: ...**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域是否为上客区域={region.setIsBoardingArea(True)}")
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b: bool) -> None: ...**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion: 
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianEllipseRegionAttr(netiface)
def showPedestrianEllipseRegionAttr(netiface): 
    areas = netiface.pedestrianEllipseRegions()
    if len(areas) > 0: 
        r = areas[0]
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
              f"获取面域所在图层ID={r.getLayerId() }, 设置面域所在图层, 如果图层ID非法, 则不做任何改变={r.setLayerId(r.getLayerId())}, "
              f"获取面域是否为障碍物={r.isObstacle()}, 获取面域是否为上客区域={r.isBoardingArea()}, "
              f"获取面域是否为下客区域={r.isAlightingArea()}" 
              f"仿真路网中pedestrianEllipseRegions总数={len(areas)}")
```






### 2.48. IPedestrianFanShapeRegion

行人扇形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def isObstacle(self) -> bool: ...**

获取面域是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self, b: bool) -> None: ...**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域是否为障碍物={region.setObstacle(True)}")
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

**def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b: bool) -> None: ...**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域是否为上客区域={region.setIsBoardingArea(True)}")
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b: bool) -> None: ...**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

**def getInnerRadius(self) -> double: ...**

获取扇形面域内半径, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取扇形面域内半径={region.getInnerRadius()}")
```

 **def getOuterRadius(self) -> double: ...**

获取扇形面域外半径, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取扇形面域外半径={region.getOuterRadius()}")
```

 **def getStartAngle(self) -> double: ...**

获取扇形面域起始角度, 单位: 度  QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取扇形面域起始角度={region.getStartAngle()}")
```

 **def getSweepAngle(self) -> double: ...**

获取扇形面域扫过角度, 单位: 度  QT像素坐标系下, X轴正方向为0, 逆时针为正

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion: 
    print(f"获取扇形面域扫过角度={region.getSweepAngle()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianFanShapRegionAttr(netiface)
def showPedestrianFanShapRegionAttr(netiface): 
    areas = netiface.pedestrianFanShapeRegions()
    if len(areas) > 0: 
        r = areas[0]
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
```




### 2.49.IPedestrianPolygonRegion

行人多边形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def isObstacle(self) -> bool: ...**

获取面域是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self, b: bool) -> None: ...**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域是否为障碍物={region.setObstacle(True)}")
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

**def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b: bool) -> None: ...**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域是否为上客区域={region.setIsBoardingArea(True)}")
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b: bool) -> None: ...**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion: 
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianPolygonRegionAttr(netiface)
def showPedestrianPolygonRegionAttr(netiface): 
    areas = netiface.pedestrianPolygonRegions()
    if len(areas) > 0: 
        r = areas[0]
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
```





### 2.50. IPedestrianRectRegion

行人矩形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def isObstacle(self) -> bool: ...**

获取面域是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self, b: bool) -> None: ...**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域是否为障碍物={region.setObstacle(True)}")
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

**def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b: bool) -> None: ...**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域是否为上客区域={region.setIsBoardingArea(True)}")
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b: bool) -> None: ...**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion: 
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianPolygonRegionAttr(netiface)
def showPedestrianRectRegionAttr(netiface): 
    areas = netiface.pedestrianRectRegions()
    if len(areas) > 0: 
        r = areas[0]
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
```

### 2.51. IPedestrianTriangleRegion

行人三角形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> double: ...**

获取期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self, val: double) -> None: ...**

设置期望速度系数

参数: 
[ in ] val: 期望速度系数

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> double: ...**

获取面域高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self, elevation: double) -> None: ...**

设置面域高程

参数: 
[ in ] elevation: 高程

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> double: ...**

获取面域多边形

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> double: ...**

获取面域所在图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self, elevation: double) -> None: ...**

设置面域所在图层, 如果图层ID非法, 则不做任何改变

参数: 
[ in ] layerId: 图层ID

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def isObstacle(self) -> bool: ...**

获取面域是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self, b: bool) -> None: ...**

设置面域是否为障碍物

参数: 
[ in ] b: 是否为障碍物

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域是否为障碍物={region.setObstacle(True)}")
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

**def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b: bool) -> None: ...**

设置面域是否为上客区域

参数: 
[ in ] b: 是否为上客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域是否为上客区域={region.setIsBoardingArea(True)}")
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b: bool) -> None: ...**

设置面域是否为下客区域 

参数: 
[ in ] b: 是否为下客区域

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion: 
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showPedestrianTriangleRegionAttr(netiface)
def showPedestrianTriangleRegionAttr(netiface): 
    areas = netiface.pedestrianTriangleRegions()
    if len(areas) > 0: 
        r = areas[0]
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
              f"获取面域所在图层ID={r.getLayerId() }, 设置面域所在图层, 如果图层ID非法, 则不做任何改变={r.setLayerId(r.getLayerId())}, "
              f"获取面域是否为障碍物={r.isObstacle()}, 获取面域是否为上客区域={r.isBoardingArea()}, "
              f"获取面域是否为下客区域={r.isAlightingArea()}" 
              f"仿真路网中pedestrianTriangleRegions总数={len(areas)}")

```





### 2.52. IPedestrianStairRegion

楼梯区域接口

 **def getId(self) -> int: ...**

获取面域id

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数: 
[ in ] name: 面域名称

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色, 返回pyside2的QColor类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color: QColor) ->None: ...**

设置面域颜色

参数: 
[ in ] color: 面域颜色

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self, unit: UnitOfMeasure) ->QPointF: ...**

获取面域位置, 默认单位: 像素, 可通过unit设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos: QPointF, unit: UnitOfMeasure) ->None: ...**

设置面域位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] scenePos: 场景坐标系下的位置
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    region.setPosition(QPointF(100, 100))
    print(f"获取面域位置, 默认单位: 像素={region.getPosition()}")
    region.setPosition(QPointF(100, 100), UnitOfMeasure.Metric)
    print(f"获取面域位置, 米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型, 面域类型见pyi文件NetItemType类

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取面域类型={region.getGType()}")

 **def getWidth(self) -> int: ...**

获取楼梯宽度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯宽度, 单位: 米={region.getWidth()}")
```

 **def setWidth(self, width: double) -> None: ...**

设置楼梯(面域)宽度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯宽度, 单位: 米={region.setWidth(region.getWidth()+0.2)}")
    print(f"获取楼梯宽度, 单位: 米={region.getWidth()}")
```

 **def getStartPoint(self) -> QPointF: ...**

获取楼梯起始点, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯起始点, 场景坐标系下={region.getStartPoint()}")
```

 **def getEndPoint(self) -> QPointF: ...**

获取楼梯终止点, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯终止点, 场景坐标系下={region.getEndPoint()}")
```

 **def getStartConnectionAreaLength(self) -> double: ...**

获取起始衔接区域长度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取起始衔接区域长度, 单位: 米={region.getStartConnectionAreaLength()}")
```

 **def getEndConnectionAreaLength(self) -> double: ...**

获取终止衔接区域长度, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取终止衔接区域长度, 单位: 米={region.getEndConnectionAreaLength()}")
```

 **def getStartRegionCenterPoint(self) -> QPointF: ...**

获取起始衔接区域中心, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取起始衔接区域中心, 场景坐标系下={region.getStartRegionCenterPoint()}")
```

 **def getEndRegionCenterPoint(self) -> QPointF: ...**

获取终止衔接区域中心, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取终止衔接区域中心, 场景坐标系下={region.getEndRegionCenterPoint()}")
```

 **def getStartSceneRegion(self) -> QPainterPath: ...**

获取起始衔接区域形状, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取起始衔接区域形状, 场景坐标系下={region.getStartSceneRegion()}")
```

 **def getEndSceneRegion(self) -> QPainterPath: ...**

获取终止衔接区域形状, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取终止衔接区域形状, 场景坐标系下={region.getEndSceneRegion()}")
```

 **def getMainQueueRegion(self) -> QPainterPath: ...**

获取楼梯主体形状, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯主体形状, 场景坐标系下={region.getMainQueueRegion()}")
```

 **def getFullQueueregion(self) -> QPainterPath: ...**

获取楼梯整体形状, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯整体形状, 场景坐标系下={region.getFullQueueregion()}")
```

 **def getMainQueuePolygon(self) -> QPolygonF : ...**

获取楼梯主体多边形, 场景坐标系下

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯主体多边形, 场景坐标系下={region.getMainQueuePolygon()}")
```

 **def getStairType(self) -> Tessng.StairType: ...**

获取楼梯类型, 类型枚举说明, 参见pyi的 StariType类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯类型={region.getStairType()}")
```

 **def setStairType(self, type: StairType) -> None: ...**

设置楼梯类型, 类型枚举说明, 参见pyi的 StariType类型

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯类型={region.setStairType(region.getStairType())}")
```

 **def getStartLayerId(self) -> int: ...**

获取楼梯的起始层级

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的起始层级={region.getStartLayerId()}")
```

 **def setStartLayerId(self, id: int) -> None: ...**

设置楼梯的起始层级

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯的起始层级={region.setStartLayerId(region.getStartLayerId())}")
    print(f"获取楼梯的起始层级={region.getStartLayerId()}")
```

 **def getEndLayerId(self) -> int: ...**

获取楼梯的终止层级

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的终止层级={region.getEndLayerId()}")
```

 **def setEndLayerId(self, id: int) -> None: ...**

设置楼梯的终止层级

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯的终止层级={region.setEndLayerId(region.getEndLayerId())}")
    print(f"获取楼梯的终止层级={region.getEndLayerId()}")
```

 **def getTransmissionSpeed(self) -> double: ...**

获取楼梯传输速度, 单位米/秒, 如果是步行楼梯, 则返回值应该是0

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯传输速度, 单位米/秒={region.getTransmissionSpeed()}")
```

 **def setTransmissionSpeed(self, speed: double) -> None: ...**

设置楼梯传输速度, 单位米/秒

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯传输速度, 单位米/秒={region.setTransmissionSpeed(region.getTransmissionSpeed())}")
    print(f"获取楼梯传输速度, 单位米/秒={region.getTransmissionSpeed()}")
```

 **def getHeadroom(self) -> double: ...**

获取楼梯净高, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯净高, 单位: 米={region.getHeadroom()}")
```

 **def setHeadroom(self, headroom: double) -> None: ...**

设置楼梯净高, 单位: 米

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"设置楼梯净高, 单位: 米={region.setHeadroom(region.getHeadroom() + 0.2)}")
    print(f"获取楼梯净高, 单位: 米={region.getHeadroom()}")
```

 **def getStartControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的起点控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的起点控制点={region.getStartControlPoint()}")
```

 **def getEndControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的终点控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的终点控制点={region.getEndControlPoint()}")
```

 **def getLeftControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的左侧控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的左侧控制点={region.getLeftControlPoint()}")
```

 **def getRightControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的右侧控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的右侧控制点={region.getRightControlPoint()}")
```

 **def getStartConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的起始衔接区域长度控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的起始衔接区域长度控制点={region.getStartConnectionAreaControlPoint()}")
```

 **def getEndConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的终止衔接区域长度控制点

举例: 

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion: 
    print(f"获取楼梯的终止衔接区域长度控制点={region.getEndConnectionAreaControlPoint()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showPedestrianStairRegionAttr(netiface)
def showPedestrianStairRegionAttr(netiface): 
    stairRegions = netiface.pedestrianStairRegions()
    if len(stairRegions) > 0: 
        r = stairRegions[0]
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
            f"获取面域类型={r.getGType()}, ")

        print(f"仿真路网中楼梯区域总数={len(stairRegions)}, "
              f"获取楼梯宽度, 单位: 米={r.getWidth()}, 设置楼梯宽度, 单位: 米度={r.setWidth(r.getWidth()+0.2)}, "
              f"获取起始点, 场景坐标系下={r.getStartPoint()}, 获取终止点, 场景坐标系下={r.getEndPoint()}, "
              f"获取起始衔接区域长度, 单位: 米={r.getStartConnectionAreaLength()}, 获取终止衔接区域长度, 单位: 米={r.getEndConnectionAreaLength()}, "
              f"获取起始衔接区域中心, 场景坐标系下={r.getStartRegionCenterPoint()}, 获取终止衔接区域中心, 场景坐标系下={r.getEndRegionCenterPoint()}, "
              f"获取起始衔接区域形状, 场景坐标系下={r.getStartSceneRegion()}, 获取终止衔接区域形状, 场景坐标系下={r.getEndSceneRegion()}, "
              f"获取楼梯主体形状, 场景坐标系下={r.getMainQueueRegion()}, 获取楼梯整体形状, 场景坐标系下={r.getFullQueueregion()}, "
              f"获取楼梯主体多边形, 场景坐标系下={r.getMainQueuePolygon()} "
              f"获取楼梯类型={r.getStairType()}, 设置楼梯类型={r.setStairType(r.getStairType())}, "
              f"获取起始层级={r.getStartLayerId()}, 设置起始层级={r.setStartLayerId(r.getStartLayerId())}, "
              f"获取终止层级={r.getEndLayerId()}, 设置终止层级={r.setEndLayerId(r.getEndLayerId())}, 获取传送速度, 单位: 米/秒={r.getTransmissionSpeed()}, "
              f"设置传送速度, 单位: 米/秒={r.setTransmissionSpeed(r.getTransmissionSpeed())}, "
              f"获取楼梯净高={r.getHeadroom()}, 设置楼梯净高={r.setHeadroom(r.getHeadroom())}, 获取起点控制点={r.getStartControlPoint()}, "
              f"获取终点控制点={r.getEndControlPoint()}, 获取左侧控制点={r.getLeftControlPoint()}, 获取右侧控制点={r.getRightControlPoint()}, "
              f"获取起始衔接区域长度控制点={r.getStartConnectionAreaControlPoint() }, 获取终止衔接区域长度控制点={r.getEndConnectionAreaControlPoint()}")


```





### 2.53. ICrosswalkSignalLamp

人行横道信号灯接口

 **def id(self) ->int: ...**

获取行人信号灯ID

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取行人信号灯ID={region.id()}")
```

 **def setSignalPhase(self, phase: Tess.ISignalPhase) ->None: ...**

设置相位, 所设相位可以是其它信号灯组的相位

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"设置相位, 所设相位可以是其它信号灯组的相位={region.setSignalPhase(region.signalPhase())}")
```

 **def setLampColor(self, colorStr: str) ->None: ...**

设置信号灯颜色    

参数: 

colorStr: 字符串表达的颜色, 有四种可选, 分别是"红"、"绿"、"黄"、"灰", , 或者是"R"、"G"、"Y"、"gray"。

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"设置信号灯颜色={region.setLampColor('gray')}")
```

 **def color(self) -> str: ...**

获取信号灯色, "R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取信号灯色={region.color()}")
```

**def name(self) -> str: ...**

获取信号灯名称

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取信号灯名称={region.name()}")
```

**def setName(self, name) -> None: ...**

设置信号灯名称

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"设置信号灯名称={region.setName('new_' + region.name())}")
    print(f"获取信号灯名称={region.name()}")
```

 **def signalPlan(self) -> Tessng.ISignalPlan: ...**

获取信控方案

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取信控方案={region.signalPlan()}")
```

 **def signalPhase(self) -> Tessng.ISignalPhase: ...**

获取相位

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取相位={region.signalPhase()}")
```

 **def polygon(self) -> QPolygonF: ...**

获取信号灯多边型轮廓的顶点

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取信号灯多边型轮廓的顶点={region.polygon()}")
```

 **def angle(self) -> double: ...**

获取信号灯角度, 正北为0, 顺时针

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取信号灯角度, 正北为0, 顺时针={region.angle()}")
```

 **def getICrossWalk(self) -> Tessng.IPedestrianCrossWalkRegion: ...**

获取行人信号灯所属人行横道

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion: 
    print(f"获取行人信号灯所属人行横道={region.getICrossWalk()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showCrossWalkSignalLampAttr(netiface)

def showCrossWalkSignalLampAttr(netiface): 
    crosswalkSignalLamps = netiface.crosswalkSignalLamps()
    crosswalkSignalLamp = netiface.findCrosswalkSignalLamp(crosswalkSignalLamps[0].id())

    print(
        f"行人信号灯列表={crosswalkSignalLamps}, 行人信号灯{crosswalkSignalLamp.id()}的具体信息: "
        f"编号={crosswalkSignalLamp.id()}, 获取信号灯当前信号灯色={crosswalkSignalLamp.color()}, 名称={crosswalkSignalLamp.name()}, "
        f"设置信号灯名称={crosswalkSignalLamp.setName('new_' + crosswalkSignalLamp.name())}, "
        f"获取当前信号灯所在的相位={crosswalkSignalLamp.signalPhase()}, 获取当前信号灯所在的灯组={crosswalkSignalLamp.signalPlan()}, "
        f"获取所在车道或车道连接={crosswalkSignalLamp.getICrossWalk()}, 获取信号灯多边型轮廓={crosswalkSignalLamp.polygon()}, "
        f"获取信号灯角度, 正北为0顺时针方向={crosswalkSignalLamp.angle()}")

```

 


### 2.54 IPedestrianPath

行人路径接口

 **def getId(self) -> int: ...**

获取行人路径ID 

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion: 
    print(f"获取行人路径ID={region.getId()}")
```

 **def getPathStartPoint(self) -> Tessng.IPedestrianPathPoint: ...**

获取行人路径起点

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion: 
    print(f"获取行人路径起点={region.getPathStartPoint()}")
```

 **def getPathEndPoint(self) -> Tessng.IPedestrianPathPoint: ...**

获取行人路径终点

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion: 
    print(f"获取行人路径终点={region.getPathEndPoint()}")
```

 **def getPathMiddlePoints(self) -> Type.List<Tessng.IPedestrianPathPoint>: ...**

获取行人路径的中间点集合, 有序集合

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion: 
    print(f"获取行人路径的中间点集合, 有序集合={region.getPathMiddlePoints()}")
```

 **def isLocalPath(self) ->boolen: ...**

判断当前行人路径是否为行人局部路径

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion: 
    print(f"判断当前行人路径是否为行人局部路径={region.isLocalPath()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showPedestrianPathAttr(netiface)
def showPedestrianPathAttr(netiface): 
    paths = netiface.pedestrianPaths()
    if len(paths) > 0: 
        path = paths[0]
        print(f"仿真路网中行人路径总数={len(paths)}, "
              f"获取行人路径起始点={path.getPathStartPoint()}, 获取行人路径终点={path.getPathEndPoint()}, "
              f"获取行人路径中间点={path.getPathMiddlePoints()}, 判断是否是局部路径={path.isLocalPath()}, ")
```




### 2.55. IPedestrianPathPoint

行人路径点（起点, 终点, 途经点）接口

 **def getId(self) -> int: ...**

获取行人路径点ID 

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion: 
    print(f"获取行人路径点ID={region.getId()}")
```

 **def getScenePos(self, unit: UnitOfMeasure) -> double: ...**

获取行人路径点场景坐标系下的位置, 默认单位: 像素, 可通过unit参数设置单位

参数: 
[ in ] unit: 单位参数, 默认为Default, Metric表示米制单位, Default表示无单位限制

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion: 
    print(f"获取行人路径点场景坐标系下的位置, 默认单位: 像素={region.getScenePos()}")
    print(f"获取行人路径点场景坐标系下的位置, 单位: 米={region.getScenePos(UnitOfMeasure.Metric)}")
```

 **def getRadius(self) -> double: ...**

获取行人路径点的半径, 单位: 米

举例: 

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion: 
    print(f"获取行人路径点的半径, 单位: 米={region.getRadius()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showPedestrianPathPointAttr(netiface)
def showPedestrianPathPointAttr(netiface): 
    paths = netiface.pedestrianPaths()
    if len(paths) > 0: 
        path = paths[0]
        sp = path.getPathStartPoint()
        if sp is not None: 
            print(f"获取行人路径点ID={sp.getId()}, 获取行人路径点场景坐标系下的位置, 默认单位: 像素={sp.getScenePos()}, "
                  f"获取行人路径点场景坐标系下的位置, 默认单位: 像素={sp.getScenePos(UnitOfMeasure.Metric)}, 获取行人路径点的半径, 单位: 米={sp.getRadius()}, ")

```

]()