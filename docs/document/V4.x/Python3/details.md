# 接口详解

## 1. 全局配置参数

### 1.1. main.py文件中config属性说明
在创建TESS NG工厂类实例前创建字典config，config配置了一些重要信息，说明如下：
```python
{
    "__workspace":"xxxxxx", # 当前工作路径
	"__netfilepath":"xxx.tess", # TESSNG启动后加载的路网文件
	"__simuafterload":False, # 是否加载路网后立即开始仿真
	"__timebycpu":False,
	"__custsimubysteps":False # 设置是否启用TESSNG对插件方法调用频次限制
}

```
"workspace"：指定“当前工作路径”，TESS NG会在“当前工作路径”的Cert子文件夹下读取认证文件，在”SimuResult”子文件夹下保存仿真结果，等。

"netfilepath"：指定TESSNG启动后加载的路网文件全路径名；

"simuafterload"：指定TESSNG加载路网文件（指定的路网文件或临时空白路网文件）后是否启动仿真；

"timebycpu"：指定每个仿真周期时间计算依据，是cpu时钟确定的时长（现实时长），还是由仿真精度确定的时长。在线仿真且算力吃紧时可以尝试设置此属性为True；

"custsimubysteps"：设置TESSNG对插件方法调用频次的依据，设为False表示每个计算周期都会调用一次插件实现的方法，即不依据插件端设置的调用频次；设为True时TESSNG依据插件设置的调用频次对插件实现的PyCustomerSimulator方法进行调用。

python二次开发环境下，如果运行车辆不多，可以将"custsimubysteps"设为False。如果运行车辆较多，可以将"__custsimubysteps"设为True，再设定实现的方法调用频次，使对仿真效率的负面影响最小化。

### 1.2. 插件方法调用频次控制

TESSNG调用插件方法的频次是指对插件实现的PyCustomerSimulator接口方法调用频次。

当"__custsimubysteps"设置为True时，**默认调用频次比较低，很多低到毫无意义，只为减少调用次数，不至于影响仿真运行效率**。如果某方法被实现，需要对该方法调用频次进行调整。可参见范例。

假设仿真精度是steps，即每秒计算steps次，各方法默认调用频次如下：

1）、车辆相关方法调用频次

计算下一位置前处理方法beforeNextPoint被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次；

具体车辆一个步长计算完成后的处理方法afterStep被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次；

确定是否停止车辆运行并移出路网方法isStopDriving调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次；

2）、驾驶行为相关方法调用频次

重新设置期望速度方法reCalcdesirSpeed被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次，如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

计算最大限速方法calcMaxLimitedSpeed被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

计算限制车道方法calcLimitedLaneNumber被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

计算车道限速方法calcSpeedLimitByLane被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

计算安全变道方法calcChangeLaneSafeDist被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以左强制变道方法reCalcToLeftLane被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以右强制变道方法reCalcToRightLane被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以左自由变道方法reCalcToLeftFreely被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

重新计算是否可以右自由变道方法reCalcToRightFreely被调用频次：每steps个仿真周期调用一次，即每秒调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

计算跟驰类型后处理方法afterCalcTracingType被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为20个计算周期调用1次或更小。

连接段上汇入到车道前处理方法beforeMergingToLane被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算跟驰状态参数方法reSetFollowingType被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

计算加速度方法calcAcce被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算加速度方法reSetAcce被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

重置车速方法reSetSpeed被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

重新计算角度方法reCalcAngle被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

计算后续道路前处理方法beforeNextRoad被调用频次：每steps * 300个仿真周期调用一次，即5分钟调用一次。如果该方法被实现，建议将该方法调用频次设为1个计算周期调用1次或更大。

### 1.3. 仿真过程中gui界面车辆重绘控制

可以在PyCustomerSimulator的initVehicle(self, pIVehicle:Tessng.IVehicle)方法里通过pIVehicle设置TESSNG对PyCustomerSimulator不同方法调用频次及是否允许插件重绘车辆。

是否允许对车辆重绘方法的调用：默认为False，如果允许，可以传入True，如：pIVehicle.setIsPermitForVehicleDraw(True)。可以通过pIVehicle得到该车辆类型及ID等信息来确定是否允许对该车辆重绘。


## 2. 路网基本元素

### 2.1. IRoadNet

路网基本信息接口，设计此接口的目的是为了TESS NG在导入外源路网时能够保存这些路网的属性，如路网中心点坐标、空间参考等。

接口方法：

**def id(self) -> int: ...**

获取路网ID，即路网编辑弹窗中的编号

 **def netName(self) -> str: ...**

获取路网名称

 **def url(self) -> str: ..**

获取源数据路径，可以是本地文件，可以是网络地址

 **def type(self) -> str: ...**

获取来源分类："TESSNG"表示TESSNG自建；"OpenDrive"表示由OpenDrive数据导入；"GeoJson"表示由geojson数据导入

 **def bkgUrl(self) -> str: ...**

获取背景路径

 **def otherAttrs(self) -> typing.Dict: ...**

获取其它属性数据

 **def explain(self) -> str: ...**

获取路网说明

 **def centerPoint(self) -> PySide2.QtCore.QPointF: ...**

获取路网中心点位置，返回的是仿真像素坐标，用户根据需求通过m2p转成米制单位坐标，并注意y轴的正负号



### 2.2. ISection

路段与连接段的父类接口，方法如下：

 **def gtype(self) -> int: ...**

获取Section类型，GLinkType 或 GConnectorType。在Tessng.pyi / NetItemType类中定义了一批枚举，每一个数值代表路网上一种元素类型。如：GLinkType代表路段、GConnectorType代表连接段。

 **def isLink(self) -> bool: ...**

是否是路段；TESSNG中基础路网由路段Link和连接段connector构成

 **def id(self) -> int: ...**

获取ID：如果当前对象是Link，则id是Link的ID；如果是连接段，则id是连接段ID

 **def sectionId(self) -> int: ...**

获取ID，如果当前Isection对象是Link，则id是Link的ID；
如果是连接段，则id是连接段ID+10000000（TESSNG内部通过加常数的方式来区分路段与连接段）

 **def name(self) -> str: ...**

获取Section名称：路段名或连接段名

 **def setName(self, name:str) -> None: ...**

设置Section名称

 **def v3z(self) -> float: ...**

获取Section高程： 在section路段上的车辆高程关联此属性； 返回的是米制单位，米m

 **def length(self) -> float: ...**

获取Section长度，默认单位：像素

 **def laneObjects(self) -> typing.List: ...**

车道与“车道连接”的父类接口列表

 **def fromSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取上游Section。
如果当前Section是路段且id 为 0则 返回空；
否则返回上游指定ID的连接段；
如果当前Section是连接段且id 为 0 返回上游路段，否则返回空。

举例：

```python
# 根据id获取路段5上游id为2的连接段
sectionLink = tessngIFace().netInterface().findLink(5)
    sectionConnector = sectionLink.fromSection(2)
    if sectionConnector is not None and sectionConnector.gtype() == NetItemType.GConnectorType:
        print("路段5上游id为2的section为：", sectionConnector.id())

```

 

 **def toSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取下游 Section。如果当前section是路段且 id 为 0则 返回空，否则返回下游指定ID的连接段；
如果当前section是连接段且id 为 0 则返回下游路段，否则返回空。

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置路段或连接段其它属性；这些属性可以用户自定义，类型为字典，方便用户做二次开发时扩充属性

 **def castToLink(self) -> Tessng.ILink: ...**

将当前Section转换成其子类ILink，如果当前Section是连接段则返回空

 **def castToConnector(self) -> Tessng.IConnector: ...**

将当前Section转换成其子类转换成IConnector，如果当前Section为路段Link则返回空

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取Section的轮廓， 轮廓由section的一系列顶点组成

 **def workerKey(self) -> int: ...**

Worker节点标识，在分布式环境可用

 **def setWorkerKey(self, key:int) -> None: ...**

设置Worker节点，在分布式环境可用

 **def fromWorkerKey(self) -> int: ...**

上游Worker节点标识，在分布式环境可用

 **def setFromWorkerKey(self, key:int) -> None: ...**

设置上游Worker节点标识，在分布式环境可用

### 2.3 ILaneObject 

车道与车道连接的父类接口，方法如下：

 **def gtype(self) -> int: ...**

类型，GLaneType或GLaneConnectorType

 **def isLane(self) -> bool: ...**

是否车道， 因为也有可能是车道链接

 **def id(self) -> int: ...**

获取ID，如果当前self对象是Lane则id是Lane的ID， 如果是车道连接，则id是“车道连接”ID

 **def length(self) -> float: ...**

获取车道或“车道连接”长度，默认单位：像素

 **def section(self) -> Tessng.ISection: ...**

获取所属的ISection

 **def fromLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据laneObject ID获取其上游的 LaneObject对象。如果当前laneObject对象是车道,则且id 为 0表示未传入laneObject ID信息，则 返回空；否则返回其上游的“车道连接”；
如果当前laneObject对象是连接段且id 为 0，那么 返回其上游车道对象，否则返回空。

 **def toLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取下游 LaneObject。如果当前是车道, id 为 0 返回空，否则返回下游指定ID的“车道连接”；如果当前是连接段，id 为 0 返回下游车道，否则返回空。

 **def centerBreakPoints(self) -> typing.List: ...**

获取laneObject的中心线断点列表；即车道或“车道连接”中心线断点集； 断点都是像素坐标下的点

 **def leftBreakPoints(self) -> typing.List: ...**

获取laneObject的左侧边线断点列表； 即车道或“车道连接”左侧线断点集; 断点都是像素坐标下的点

 **def rightBreakPoints(self) -> typing.List: ...**

获取laneObject的右侧边线断点列表；车道或“车道连接”右侧线断点集; 断点都是像素坐标下的点

 **def centerBreakPoint3Ds(self) -> typing.List: ...**

获取laneObject的右侧边线断点列表；车道或“车道连接”中心线断点(三维)集（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位

 **def leftBreakPoint3Ds(self) -> typing.List: ...**

获取laneObject的左侧边线断点列表；车道或“车道连接”左侧线断点(三维)集；（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位

 **def rightBreakPoint3Ds(self) -> typing.List: ...**

获取laneObject的右侧边线断点列表；车道或“车道连接”右侧线断点(三维)集；（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

通过起终止断点，获取该范围内laneObject的左侧边线断点集；即车道或“车道连接”左侧部分断点(三维)集；入参出参都是像素单位，高程除外为米制单位 

参数：

\[in\] fromPoint：中心线上某一点作为起点

\[in\] toPoint：中心线上某一点作为终点

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

车道或“车道连接”右侧部分断点(三维)集， 同上

参数：

\[in\] fromPoint：中心线上某一点作为起点；QPointF类型，且是像素坐标

\[in\] toPoint：中心线上某一点作为终点；QPointF类型，且是像素坐标

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF) -> float: ...**

中心线上一点到laneObject对象起点的距离； 像素单位

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=...) -> float: ...**

laneObject中心线上一点到起点的距离，像素单位，附加条件是该点所在车道上的分段序号；其中分段是指两个断点之间的部分。

参数：

\[in\] p：当前中心线上点或附近点的坐标; QPointF类型，且是像素坐标

\[in\] segmIndex：参数p点所在车道上的分段序号; 两个断点组成一个分段，分段序号从0开始，沿着道路方向递增

\[in\] bOnCentLine：参数p点是否在中心线上

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int) -> bool: ...**

获取沿着行驶方向，距laneObject起点dist距离处的点和该点所属的分段序号； 如果目标点不在中心线上返回False，否则返回True

参数：

\[in\] dist：中心线起点向下游延伸的距离， 像素单位

[out] outPoint：中心线起点向下游延伸dist距离后所在点， 像素单位

[out] outIndex：中心线起点向下游延伸dist距离后所在分段序号， 像素单位

举例：

```python
# 路段5最左侧车道向前延伸140米后所在点及分段序号
        link = tessngIFace().netInterface().findLink(5)
        laneObjLeft = link.laneObjects()[-1] 
        outPoint = QPointF()
        outIndex = 0
        dist = m2p(140)
        if laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex) is not None:
    print("路段5最左侧车道向前延伸140米后所在点坐标为：({}, {})，分段序号为：{}".format(outPoint.x(), outPoint.y(), outIndex))

```

 

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF) -> bool: ...**

获取距离中心线起点向下游延伸dist处的点, 如果目标点不在中心线上返回False，否则返回True

 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道或“车道连接”其它属性； 字典类型

 **def castToLane(self) -> Tessng.ILane: ...**

将ILaneObject转换为子类ILane，但如果当前ILaneObject是“车道连接”则转化失败，返回空

 **def castToLaneConnector(self) -> Tessng.ILaneConnector: ...**

将ILaneObject转换为ILaneConnector子类，但如果当前ILaneObject是车道则转化失败，返回空

### 2.4. ILink

路段接口，方法如下：

 **def gtype(self) -> int: ...**

类型，返回GLinkType，TESSNG的一个常量， int类型

 **def id(self) -> int: ...**

获取路段ID

 **def length(self) -> float: ...**

获取路段长度，默认单位：像素

 **def width(self) -> float: ...**

获取路段宽度，单位像素

 **def z(self) -> float: ...**

获取路段高程， 单位 米

 **def v3z(self) -> float: ...**

获取路段高程，过载ISection的方法， 等同于上边的z(self)

 **def name(self) -> str: ...**

获取路段名称

 **def setName(self, name:str) -> None: ...**

设置路段名称

 **def linkType(self) -> str: ...**

获取路段类型，出参为字符串枚举：城市主干道、城市次干道、人行道。

 **def setType(self, type:str) -> None: ...**

设置路段类型，路段类型有10种，入参可以为：高速路、城市快速路、匝道、城市主要干道、次要干道、地方街道、非机动车道、人行道、公交专用道、机非共享； 其中的任意一个，其他类型暂不支持

 **def laneCount(self) -> int: ...**

获取车道数

 **def limitSpeed(self) -> float: ...**

获取路段最高限速，单位：千米/小时

 **def setLimitSpeed(self, speed:float) -> None: ...**

设置最高限速， 单位km/h

参数：

\[in\] speed：最高限速，单位：千米/小时

举例：

```python
link = None
# 创建路段省略
if link is not None:
# 设置路段限速30km/h
link.setLimitSpeed(30)

```

 

 **def minSpeed(self) -> float: ...**

获取最低限速，单位：千米/小时； 暂不支持用户设置最低限速的API

 **def lanes(self) -> typing.List: ...**

获取ILink上的车道列表， 列表按照从左到右的顺序排列；列表元素为ILane对象

 **def laneObjects(self) -> typing.List: ...**

获取ILink下所有LaneObject对象，列表类型，LaneObject可以是车道，也可以是“车道连接”的父对象

 **def centerBreakPoints(self) -> typing.List: ...**

获取路段中心线断点集， 像素坐标

 **def leftBreakPoints(self) -> typing.List: ...**

获取路段左侧线断点集， 像素坐标

 **def rightBreakPoints(self) -> typing.List: ...**

获取路段右侧线断点集， 像素坐标

 **def centerBreakPoint3Ds(self) -> typing.List: ...**

获取路段中心线断点(三维)集， 像素坐标，但高程z的单位为米

 **def leftBreakPoint3Ds(self) -> typing.List: ...**

获取路段左侧线断点(三维)集, 像素坐标，但高程z的单位为米

 **def rightBreakPoint3Ds(self) -> typing.List: ...**

获取路段右侧线断点(三维)集， 像素坐标，但高程z的单位为米

 **def fromConnectors(self) -> typing.List: ...**

获取ILink的上游连接段， 其可能有多个，所以返回类型为列表，列表元素为IConnector对象

 **def toConnectors(self) -> typing.List: ...**

获取ILink的上游连接段， 其可能有多个，所以返回类型为列表，列表元素为IConnector对象

 **def fromSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取上游Section。如果当前是路段,且id 为 0则 返回空，否则返回上游指定ID的连接段；

如果当前是连接段且id 为 0 返回上游路段，否则返回空。 

因为连接段的上游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其上游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def toSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取下游Section。如果当前是路段,且id 为 0则 返回空，否则返回下游指定ID的连接段；

如果当前是连接段且id 为 0 返回下游路段，否则返回空。 

因为连接段的下游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其下游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置路段的其它属性， TESSNG仿真过程中仅记录拓展的属性，方便用户拓展，并自定义使用

 **def setLaneTypes(self, lType:typing.Sequence) -> None: ...**

依次为ILink下所有车道设置车道属性（列表顺序为 从左到右的车道顺序），入参为序列类型（列表，元组等），其中元素的类型从这四种常量字符串中获取："机动车道"、"机非共享"、"非机动车道"、"公交专用道"

 **def setLaneOtherAtrrs(self, lAttrs:typing.Sequence) -> None: ...**

依次为ILink下所有车道设置车道其它属性

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF) -> float: ...**

ILink中心线上任意一点到ILink起点的距离， 像素单位

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int) -> bool: ...**

获取ILink中心线起点下游dist距离处的点及其所属分段序号, 如果目标点不在中心线上返回False，否则返回True

参数：

\[in\] dist：中心线起点向下游延伸的距离

[out] outPoint：中心线起点向下游延伸dist距离后所在点

[out] outIndex：中心线起点向下游延伸dist处的点所属分段序号

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF) -> bool: ...**

求ILink中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回False，否则返回True

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取路段的多边型轮廓， 返回值类型QPolygonF， 像素坐标



### 2.5. ILane

车道接口，方法如下：

 **def gtype(self) -> int: ...**

类型，车道类型为GLaneType，其中GLaneType是一种常量，

 **def id(self) -> int: ...**

获取车道ID

 **def link(self) -> Tessng.ILink: ...**

获取车道所属路段，返回路段对象

 **def section(self) -> Tessng.ISection: ...**

获取车道所属Section，返回Section对象，其为ILink的父对象

 **def length(self) -> float: ...**

获取车道长度，默认单位：像素

 **def width(self) -> float: ...**

获取车道宽度，默认单位：像素

 **def number(self) -> int: ...**

获取车道序号，从0开始（自外侧往内侧，即自左向右依次编号）

 **def actionType(self) -> str: ...**

获取车道的行为类型，返回的为行为类型常量字符串，包括："机动车道"、“非机动车道”、“公交专用道”

 **def fromLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取上游LaneObject。如果当前是车道,且id 为 0则 返回空，否则返回上游指定ID的车道连接段；

如果当前是车道连接段且id 为 0 返回上游车道，否则返回空。 

因为车道连接段的上游一定只有一个车道，所以不需要额外指定id； 但如果是车道，则其上游有可能存在多个车道连接段，因此需要传入车道连接段ID来返回指定ID的车道连接器对象

 **def toLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取下游 LaneObject 同fromLaneObject用法

 **def centerBreakPoints(self) -> typing.List: ...**

获取车道中心点断点集，断点坐标用像素表示

 **def leftBreakPoints(self) -> typing.List: ...**

获取车道左侧线断点集,断点坐标用像素表示

 **def rightBreakPoints(self) -> typing.List: ...**

获取车道右侧线断点集,断点坐标用像素表示

 **def centerBreakPoint3Ds(self) -> typing.List: ...**

获取车道中心线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示

 **def leftBreakPoint3Ds(self) -> typing.List: ...**

获取车道左侧线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示

 **def rightBreakPoint3Ds(self) -> typing.List: ...**

获取车道右侧线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

根据指定起终点断点，获取车道左侧部分断点(三维)集，  断点坐标用像素表示，其中高程z用单位米表示

参数：

\[in\] fromPoint：中心线上某一点作为起点， 像素坐标，其中高程z用单位米表示
\[in\] toPoint：中心线上某一点作为终点， 像素坐标，其中高程z用单位米表示

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

根据指定起终点断点，获取车道右侧部分断点(三维)集,  断点坐标用像素表示，其中高程z用单位米表示

参数：

\[in\] fromPoint：中心线上某一点作为起点，像素坐标，其中高程z用单位米表示

\[in\] toPoint：中心线上某一点作为终点，像素坐标，其中高程z用单位米表示

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF) -> float: ...**

获取中心线上一点到起点的距离， 单位像素

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=...) -> float: ...**

根据中心线上任意点所处的车道分段号和该点本身信息，计算该点到车道起点的距离

参数：

\[in\] p：当前中心线上的点坐标，像素单位

\[in\] segmIndex：该点所在车道上的分段序号

\[in\] bOnCentLine：该点是否在中心线上

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int) -> bool: ...**

获取车道中心线起点下游dist距离处的点及其所属分段序号；
如果目标点不在中心线上返回False，否则返回True

参数：

\[in\] dist：中心线起点向前延伸的距离

[out] outPoint：中心线起点向前延伸dist距离后所在点

[out] outIndex：中心线起点向前延伸dist距离后所在分段序号

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF) -> bool: ...**

获取车道中心线起点下游dist距离处的点；
如果目标点不在中心线上返回False，否则返回True

 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道的其它属性，方便用户拓展车道属性； 类型： 字典形式

 **def setLaneType(self, type:str) -> None: ...**

设置车道的类型； 车道类型常量范围："机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

参数：

\[in\] type：车道类型，选下列几种类型其中一种："机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取车道的多边型轮廓顶点, 像素坐标



### 2.6. IConnector

连接段接口，方法如下：

 **def gtype(self) -> int: ...**

类型，连接段类型为GConnectorType，GConnectorType是一种整数型常量。

 **def id(self) -> int: ...**

获取连接段ID； 因为连接段ID和路段ID是相互独立的，所以可能两者的ID之间会有重复

 **def length(self) -> float: ...**

获取连接段长度，像素单位

 **def z(self) -> float: ...**

获取连接段高程，单位：米

 **def v3z(self) -> float: ...**

获取连接段高程，过载自ISection的方法， 单位，米；其效果等同于z()函数

 **def name(self) -> str: ...**

获取连接段名称

 **def setName(self, name:str) -> None: ...**

设置连接段名称

 **def fromLink(self) -> Tessng.ILink: …**

获取当前connector的起始路段， 返回路段对象

 **def toLink(self) -> Tessng.ILink: ...**

获取当前connector的目标路段（出口路段）， 返回路段对象

 **def fromSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取上游Section。如果当前是路段,且id 为 0则 返回空，否则返回上游指定ID的连接段；

如果当前是连接段且id 为 0 返回上游路段，否则返回空。 

因为连接段的上游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其上游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def toSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取下游Section。如果当前是路段,且id 为 0则 返回空，否则返回下游指定ID的连接段；

如果当前是连接段且id 为 0 返回下游路段，否则返回空。 

因为连接段的下游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其下游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def limitSpeed(self) -> float: ...**

获取连接器的最高限速，因为连接器没有最高限速这一属性，因此该函数返回连接器的起始路段最高限速作为连接段的最高限速, 单位 km/h

 **def minSpeed(self) -> float: ...**

获取连接器的最低限速，因为连接器没有最低限速这一属性，因此返回连接器起始路段的最低限速作为连接段的最低限速， 单位 km/h

 **def laneConnectors(self) -> typing.List: ...**

获取连接器下的所有“车道连接”对象， 列表形式，列表元素为ILaneConnector对象

 **def laneObjects(self) -> typing.List: ...**

车道及“车道连接”的接口列表

 **def setLaneConnectorOtherAtrrs(self, lAttrs:typing.Sequence) -> None: ...**

设置包含的“车道连接”其它属性

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置连接段其它属性

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取连接段的多边型轮廓顶点



### 2.7. ILaneConnector

“车道连接”接口，方法如下：

 **def gtype(self) -> int: ...**

类型，GLaneType或GLaneConnectorType，车道连接段为GLaneConnectorType ，这里的返回值只可能是GLaneConnectorType

 **def id(self) -> int: ...**

获取车道连接ID

 **def connector(self) -> Tessng.IConnector: ...**

获取车道连接所属的连接段Connector对象, 返回类型IConnector

 **def section(self) -> Tessng.ISection: ...**

获取车道所属Section, Section为 IConnector的父类

 **def fromLane(self) -> Tessng.ILane: ...**

获取当前车道链接的上游车道对象

 **def toLane(self) -> Tessng.ILane: ...**

获取当前车道链接的下游车道对象

 **def fromLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取上游LaneObject。如果当前是车道,且id 为 0则 返回空，否则返回上游指定ID的车道连接段；

如果当前是车道连接段且id 为 0 返回上游车道，否则返回空。 

因为连接段的上游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其上游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def toLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取下游LaneObject。如果当前是车道,且id 为 0则 返回空，否则返回下游指定ID的车道连接段；

如果当前是车道连接段且id 为 0 返回下游车道，否则返回空。 

因为连接段的下游一定只有一个路段，所以不需要额外指定id； 但如果是路段，则其上游有可能存在多个连接段，因此需要传入连接段ID来返回指定的ID

 **def length(self) -> float: ...**

获取“车道连接”的长度，单位：像素， 是指中心线的长度

 **def centerBreakPoints(self) -> typing.List: ...**

获取“车道连接”的中心线断点集，断点坐标用像素表示

 **def leftBreakPoints(self) -> typing.List: ...**

获取“车道连接”左侧线断点集，断点坐标用像素表示

 **def rightBreakPoints(self) -> typing.List: ...**

获取“车道连接”右侧线断点集，断点坐标用像素表示

 **def centerBreakPoint3Ds(self) -> typing.List: ...**

获取“车道连接”中心线断点(三维)集，断点坐标用像素表示， 高程Z单位米

 **def leftBreakPoint3Ds(self) -> typing.List: ...**

获取“车道连接”左侧线断点(三维)集，断点坐标用像素表示， 高程Z单位米

 **def rightBreakPoint3Ds(self) -> typing.List: ...**

获取“车道连接”右侧线断点(三维)集，断点坐标用像素表示， 高程Z单位米

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

根据指定的起终止点获取“车道连接”左侧部分断点(三维)集，断点坐标用像素表示， 高程Z单位米

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF) -> typing.List: ...**

根据指定的起终止点获取“车道连接”右侧部分断点(三维)集，断点坐标用像素表示， 高程Z单位米

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF) -> float: ...**

计算车道链接中心线上任意点到起点的距离， 单位像素

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=...) -> float: ...**

计算中心线上任意点到起点的距离，附加条件是该点所在车道上的分段序号

参数：

\[in\] p：当前中心线上该点坐标，像素坐标

\[in\] segmIndex：该点所在车道上的分段序号

\[in\] bOnCentLine：是否在中心线上

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int) -> bool: ...**

求中心线起点下游dist距离处的点及分段序号, 如果目标点不在中心线上返回False，否则返回True

参数：

\[in\] dist：中心线起点向前延伸的距离，像素单位

[out] outPoint：中心线起点向前延伸dist距离后所在点， 像素坐标

[out] outIndex：中心线起点向前延伸dist距离后所在分段序号

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF) -> bool: ...**

求中心线起始点下游dist距离处的点, 如果目标点不在中心线上返回False，否则返回True

 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道连接其它属性，方便二次开发过程中使用



### 2.8. IConnectorArea

面域接口，方法如下：

 **def id(self) -> int: ...**

获取面域ID； 面域是指：若干Connector重叠形成的区域

 **def allConnector(self) -> typing.List: ...**

获取当前面域包含的所有连接段， 返回类型列表，元素为IConnector对象

 **def centerPoint(self) -> PySide2.QtCore.QPointF: ...**

获取面域中心点， 像素坐标

 **def workerKey(self) -> int: ...**

获取Worker 标识符，分布式环境可用

 **def setWorkerKey(self, key:int) -> None: ...**

设置 Worker 标识符，分布式环境可用





### 2.9. IDispatchPoint

发车点接口，方法如下：

 **def id(self) -> int: ...**

获取发车点ID

 **def name(self) -> str: ...**

获取发车名称

 **def link(self) -> Tessng.ILink: ...**

获取发车点所在路段

 **def addDispatchInterval(self, vehiCompId:int, interval:int, vehiCount:int) -> int: ...**

为发车点增加发点间隔

参数：

vehicCompId：车型组成ID

interval：时间段，单位：秒

vehiCount：发车数

返回值：

返回发车间隔ID

举例：

```python
# 新建发车点,车型组成ID为动态创建的，600秒发300辆车
if link1:
    dp = netiface.createDispatchPoint(link1)
    if dp:
        dp.addDispatchInterval(vehiCompositionID, 600, 300)

```

 **def setDynaModified(self, bModified:bool) -> None: ...**

设置是否被动态修改，若需要仿真过程中动态修改发车点信息，则需要先将此函数设置为True，否则不能修改发车点信息
说明：发车信息被动态修改后，整个文件不支持保存，以免破坏原有发车设置
已废弃

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取发车点多边型轮廓



------



### 2.10. IDecisionPoint

决策点接口，方法如下：

 **def id(self) -> int: ...**

获取决策点ID

 **def name(self) -> str: ...**

获取决策点名称

 **def link(self) -> Tessng.ILink: ...**

获取决策点所在路段

 **def distance(self) -> float: ...**

获取距路段起点距离，默认单位：像素

 **def routings(self) -> typing.List: ...**

获取决策点控制的所有决策路径， 返回类型列表，元素为IRouting对象

 **def setDynaModified(self, bModified:bool) -> None: ...**

设置是否被动态修改，若需要仿真过程中动态修改决策点信息，则需要先将此函数设置为True，否则不能修改
说明：发车信息被动态修改后，整个文件不能保存，以免破坏原有发车设置
该函数已废弃

参数：

\[in\] bModified：是否被动态修改，True为被动态修改，False为未被动态修改 

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取决策点多边型轮廓

------



### 2.11. IRouting

路径接口，方法如下：

 **def id(self) -> int: ...**

获取路径ID

 **def calcuLength(self) -> float: ...**

计算路径长度, 单位像素

 **def getLinks(self) -> typing.List: ...**

获取当前路径的路段序列， 不包含连接器

 **def deciPointId(self) -> int: ...**

获取当前路径所属的决策点ID

 **def contain(self, pRoad:Tessng.ISection) -> bool: ...**

判定道路是否在当前路径上， 入参需是ISection对象

 **def nextRoad(self, pRoad:Tessng.ISection) -> Tessng.ISection: ...**

根据当前路径，获取所给道路的下一条道路， 返回类型为ISection 即下一条路段可能是Link也可能是Connector

参数：

\[in\] pRoad：路段或连接段



### 2.12. ISignalLamp

信号灯接口，方法如下：

 **def id(self) -> int: ...**

获取信号灯ID

 **def setName(self, name:str) -> None: ...**

设置信号灯名称

 **def setLampColor(self, colorStr:str) -> None: ...**

设置信号灯颜色

参数：

\[in\] colorStr：字符串表达的颜色，有四种可选，支持汉字："红"、"绿"、"黄"、"灰"，也支持字符： "R"、"G"、"Y"、"grey"。

 **def signalPhase(self) -> Tessng.ISignalPhase: ...**

获取当前信号灯所在的相位， 返回类型：ISignalPhase

 **def signalGroup(self) -> Tessng.ISignalGroup: ...**

获取当前信号灯所在的灯组， 这里灯组类似于一个组信号灯控制的路口  ?? ????  改为：获取当前信号灯所在的灯组， 这里灯组类似于一个信号机种的某个信控方案

 **def setDistToStart(self, dist:float) -> None: ...**

设置当前信号灯在车道|车道连接器上的位置； 位置描述为：信号灯距离起点的距离，单位：像素

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取信号灯多边型轮廓



### 2.13. ISignalPhase

信号相位，接口方法：

 **def id(self) -> int: ...**

获取当前相位的相位ID

 **def number(self) -> int: ...**

获取一个灯组中的相位序号，从1开始，对应TESSNG相位列表中的相位编号，表示相位从上到下的序号。？？？
获取一个信号方案中的相位序号，从1开始对应TESSNG相位列表中的相位编号，表示相位从上到下的序号。

 **def phaseName(self) -> str: ...**

获取当前相位的相位名称

 **def signalGroup(self) -> Tessng.ISignalGroup: ...**

获取相位所在信号灯组对象？？？即信控方案

 **def signalLamps(self) -> typing.List: ...**

获取本相位下的信号灯列表

 **def listColor(self) -> typing.List: ...**

获取本相位的相位灯色列表

 **def setColorList(self, lColor:typing.Sequence) -> None: ...**

设置当前相位的信号灯色信息列表

参数：

\[in\] lColor：灯色时长信息，包含信号灯颜色和信号灯色时长

举例：

```python
#以L12路段相位直行信号灯相位为例（ID为7），由红90绿32黄3红25改为红10绿110黄3红28
if method_number == 3:
    signalPhase_L12_7 = netiface.findSignalPhase(7)
    color_list = []  # 按照红灯、绿灯、黄灯、红灯顺序计算
    color_list.append(Online.ColorInterval('红', 10))
    color_list.append(Online.ColorInterval('绿', 110))
    color_list.append(Online.ColorInterval('黄', 3))
    color_list.append(Online.ColorInterval('红', 28))
    signalPhase_L12_7.setColorList(color_list)

```

 **def setNumber(self, number:int) -> None: ...**

设置当前相位的相位序号

 **def setPhaseName(self, name:str) -> None: ...**

设置当前相位名称

### 2.14. ISignalGroup

 **def id(self) -> int: ...**

获取当前信号灯组ID（信控方案）

 **def groupName(self) -> str: ..**

获取当前信号灯组名称（信控方案）

 **def periodTime(self) -> int: ...**

获取当前信号灯组（信控方案）的信号周期：单位秒

 **def fromTime(self) -> int: ...**

获取信号灯组工作的起始时间，自仿真开始计算，单位秒

 **def toTime(self) -> int: ...**

获取信号灯组工作的终止时间，自仿真开始计算，单位秒

 **def phases(self) -> typing.List: ...**

获取当前信号灯组下的相位列表

 **def setName(self, name:str) -> None: ...**

设置信号灯组名称

 **def setPeriodTime(self, period:int) -> None: ...**

设置信号灯组（信控方案）的信号周期时长， 单位秒

参数：

\[in\] period：信号周期，单位：秒

 **def setFromTime(self, time:int) -> None: ...**

设置信号灯组信号方案的工作起始时间，单位：秒

 **def setToTime(self, time:int) -> None: ...**





### 2.15. IBusLine

公交线路接口，接口方法：

 **def id(self) -> int: ...**

获取当前公交线路的ID

 **def name(self) -> str: ...**

获取当前公交线路的名称

 **def length(self) -> float: ...**

获取当前公交线路长度，单位：像素

 **def dispatchFreq(self) -> int: ...**

获取当前公交线路的发车间隔，单位：秒

 **def dispatchStartTime(self) -> int: ...**

获取当前公交线路的发车开始时间，单位：秒

 **def dispatchEndTime(self) -> int: ...**

获取当前公交线路的发车结束时间，单位：秒， 即当前线路的公交调度表的结束时刻

 **def desirSpeed(self) -> float: ...**

获取当前公交线路的期望速度，单位：km/h

 **def passCountAtStartTime(self) -> int: ...**

公交线路中公交车的起始载客人数

 **def links(self) -> typing.List: ...**

获取公交线路经过的路段序列

 **def stations(self) -> typing.List: ...**

获取公交线路上的所有站点

 **def stationLines(self) -> typing.List: ...**

公交站点线路，当前线路相关站点的上下客等参数 ， 所有参数的列表

 **def setName(self, name:str) -> None: ...**

设置当前公交线路的名称

 **def setDispatchFreq(self, freq:int) -> None: ...**

设置当前公交线路的发车间隔，单位：秒

 **def setDispatchStartTime(self, startTime:int) -> None: ...**

设置当前公交线路上的公交首班车辆的开始发车时间

 **def setDispatchEndTime(self, endTime:int) -> None: ...**

设置当前公交线路上的公交末班车的发车时间

 **def setDesirSpeed(self, desirSpeed:float) -> None: ...**

设置当前公交线路的期望速度

举例：

```python
# 创建公交线路
busLine = tessngIFace().netInterface().createBusLine([link10, link11])
if busLine is not None:
    busLine.setDesirSpeed(m2p(60))

```

 **def setPassCountAtStartTime(self, count:int) -> None: ...**

设置当前公交线路的起始载客人数




### 2.16. IBusStation

公交站点接口，接口方法：

 **def id(self) -> int: ...**

获取当前公交站点ID

 **def name(self) -> str: ...**

获取当前公交线路名称

 **def laneNumber(self) -> int: ...**

获取当前公交站点所在车道序号

 **def x(self) -> float: ...**

获取当前公交站点的中心点的位置， X坐标

 **def y(self) -> float: ...**

获取当前公交站点的中心点的位置， Y坐标

 **def length(self) -> float: ...**

获取当前公交站点的长度，单位：像素

 **def stationType(self) -> int: ...**

获取当前公交站点的类型：站点类型 1：路边式、2：港湾式

 **def link(self) -> Tessng.ILink: ...**

获取当前公交站点所在路段

 **def lane(self) -> Tessng.ILane: ...**

获取当前公交站点所在车道

 **def distance(self) -> float: ...**

获取当前公交站点的起始位置距路段起点的距离，默认单位：像素

 **def setName(self, name:str) -> None: ...**

设置站点名称

 **def setDistToStart(self, dist:float) -> None: ...**

设置站点起始点距车道起点距离，默认单位：像素

参数：

\[in\] dist：距车道起点距离，单位：像素

 **def setLength(self, length:float) -> None: ...**

设置当前公交站点的长度，默认单位：像素

 **def setType(self, type:int) -> None: ...**

设置当前公交站点类型

参数：

\[in\] type：站点类型，1 路侧式、2 港湾式

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取 公交站点多边型轮廓的顶点

------



### 2.17. IBusStationLine

公交站点-线路接口，通过此接口可以获取指定线路某站点运行参数，如靠站时间、下客百分比等，还可以设置这些参数。

接口方法：

 **def id(self) -> int: ...**

获取公交“站点-线路”ID

 **def stationId(self) -> int: ...**

获取当前公交站点的ID

 **def lineId(self) -> int: ...**

获取当前公交站台所属的公交线路ID

 **def busParkingTime(self) -> int: ...**

获取当前公交线路下该站台的公交车辆停靠时间(秒)

 **def getOutPercent(self) -> float: ...**

获取当前公交线路下该站台的下客百分比

 **def getOnTimePerPerson(self) -> float: ...**

获取当前公交线路下该站台下的平均每位乘客上车时间，单位：秒

 **def getOutTimePerPerson(self) -> float: ...**

获取当前公交线路下该站台下的平均每位乘客下车时间，单位：秒

 **def setBusParkingTime(self, time:int) -> None: ...**

设置当前公交线路下该站台下的车辆停靠时间(秒)

举例：

```python
lStationLine = busLine.stationLines()
if len(lStationLine)>0:
stationLine = lStationLine[0] 
# 设置车辆停靠时间(秒)
stationLine.setBusParkingTime(20)

```

 **def setGetOutPercent(self, percent:float) -> None: ..**

设置当前公交线路下的该站台的下客百分比

 **def setGetOnTimePerPerson(self, time:float) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客上车时间

 **def setGetOutTimePerPerson(self, time:float) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客下车时间

------





### 2.18. IVehicleDrivInfoCollector

数据采集器接口，方法如下：

 **def id(self) -> int: ...**

获取采集器ID

 **def collName(self) -> str: ...**

获取采集器名称

 **def onLink(self) -> bool: ...**

判断当前数据采集器是否在路段上，返回值为True表示检测器在路段上，返回值False则表示在connector上

 **def link(self) -> Tessng.ILink: ...**

获取采集器所在的路段

 **def connector(self) -> Tessng.IConnector: ...**

获取采集器所在的连接段

 **def lane(self) -> Tessng.ILane: ...**

如果采集器在路段上，则返回ILane对象，否则范围None

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果采集器在连接段上，则返回laneConnector“车道连接”对象，否则返回None

 **def distToStart(self) -> float: ...**

获取采集器距离路段|连接段起点的距离，默认单位：像素

 **def point(self) -> PySide2.QtCore.QPointF: ...**

采集器所在点，像素坐标

 **def fromTime(self) -> int: ...**

获取采集器的工作起始时间，单位：秒

 **def toTime(self) -> int: ...**

获取采集器的工作停止时间，单位：秒

 **def aggregateInterval(self) -> int: ...**

获取数据集计的时间间隔，单位：秒

 **def setName(self, name:str) -> None: ...**

设置采集器名称

 **def setDistToStart(self, dist:float) -> None: ...**

设置采集器距车道起点（或“车道连接”起点）的距离， 单位：像素

参数：

\[in\] dist：采集器距离车道起点（或“车道连接”起点）的距离，默认单位：像素

举例：

```python
collector = tessngIFace().netInterface().createVehiCollectorOnLink(leftLane, dist)
# 将采集器设置到距路段起点400米处
if collector is not None:
    collector.setDistToStart(m2p(400))

```

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间(秒)

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间(秒)

 **def setAggregateInterval(self, interval:int) -> None: ...**

设置集计数据时间间隔(秒)

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取采集器的多边型轮廓顶点

------



### 2.19. IVehicleQueueCounter

排队计数器接口，方法如下：

 **def id(self) -> int: ...**

获取当前排队计数器ID 

 **def counterName(self) -> str: ...**

获取当前排队计数器名称

 **def onLink(self) -> bool: ...**

是否在路段上，如果True则connector()返回None

 **def link(self) -> Tessng.ILink: ...**

获取当前排队计数器所在路段

 **def connector(self) -> Tessng.IConnector: ...**

获取当前计数器所在连接段

 **def lane(self) -> Tessng.ILane: ...**

如果计数器在路段上则lane()返回所在车道，laneConnector()返回None

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果计数器在连接段上则laneConnector返回“车道连接”,lane()返回None

 **def distToStart(self) -> float: ...**

计数器距离起点距离，默认单位：像素

 **def point(self) -> PySide2.QtCore.QPointF: ...**

计数器所在点，像素坐标

举例：

```python
# 在路段9最左侧车道100米处创建排队计数器
counter = tessngIFace().netInterface().createVehiQueueCounterOnLink(leftLane, dist)
if counter is not None:
    print(f"计数器所在点坐标为: ({counter.point().x()}, {counter.point().y()})")

```

 **def fromTime(self) -> int: ...**

获取当前计数器工作起始时间，单位：秒

 **def toTime(self) -> int: ...**

获取当前计数器工作停止时间，单位：秒

 **def aggregateInterval(self) -> int: ...**

计数集计数据时间间隔，单位：秒

 **def setName(self, name:str) -> None: ...**

设置计数器名称

 **def setDistToStart(self, dist:float) -> None: ...**

设置当前计数器距车道起点（或“车道连接”起点）距离

参数：

\[in\] dist：计数器距离车道起点（或“车道连接”起点）的距离，默认单位：像素

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间(秒)

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间(秒)

 **def setAggregateInterval(self, interval:int) -> None: ...**

设置集计数据时间间隔(秒)

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取计数器的多边型轮廓顶点

------



### 2.20. IVehicleTravelDetector

行程时间检测器接口，方法如下：

 **def id(self) -> int: ...**

获取检测器ID

 **def detectorName(self) -> str: ...**

获取检测器名称

 **def isStartDetector(self) -> bool: ...**

是否检测器起始点

 **def isOnLink_startDetector(self) -> bool: ...**

检测器起点是否在路段上，如果否，则起点在连接段上

 **def isOnLink_endDetector(self) -> bool: ...**

检测器终点是否在路段上，如果否，则终点在连接段上

 **def link_startDetector(self) -> Tessng.ILink: ...**

如果检测器起点在路段上则link_startDetector()返回起点所在路段，laneConnector_startDetector()返回None

 **def laneConnector_startDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”,link_startDetector()返回None

 **def link_endDetector(self) -> Tessng.ILink: ...**

如果检测器终点在路段上则link_endDetector()返回终点所在路段，laneConnector_endDetector()返回None

 **def laneConnector_endDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”,link_endDetector()返回None

 **def distance_startDetector(self) -> float: ...**

检测器起点距离所在车道起点或“车道连接”起点距离，默认单位：像素

 **def distance_endDetector(self) -> float: ...**

检测器终点距离所在车道起点或“车道连接”起点距离，默认单位：像素

 **def point_startDetector(self) -> PySide2.QtCore.QPointF: ...**

检测器起点位置

 **def point_endDetector(self) -> PySide2.QtCore.QPointF: ...**

检测器终点位置

 **def fromTime(self) -> int: ...**

检测器工作起始时间，单位：秒

 **def toTime(self) -> int: ...**

检测器工作停止时间，单位：秒

 **def aggregateInterval(self) -> int: ...**

集计数据时间间隔，单位：秒

 **def setName(self, name:str) -> None: ...**

设置检测器名称

 **def setDistance_startDetector(self, dist:float) -> None: ...**

设置检测器起点距车道起点（或“车道连接”起点）距离，默认单位：像素

 **def setDistance_endDetector(self, dist:float) -> None: ...**

设置检测器终点距车道起点（或“车道连接”起点）距离，默认单位：像素

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间，单位：秒

举例：

```python
lVehicleTravelDetector = tessngIFace().netInterface().createVehicleTravelDetector_link2link(link, link, m2p(50),m2p(550))
   if lVehicleTravelDetector is not None:
      for detector in lVehicleTravelDetector:
          detector.setFromTime(10)
          detector.setToTime(60)

```

 

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间，单位：秒

 **def aggregateInterval(self) -> int: ...**

设置集计数据时间间隔，单位：秒

 **def polygon_startDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器起始点多边型轮廓的顶点

 **def polygon_endDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器终止点多边型轮廓的顶点

------



### 2.21. IGuidArrow

导向箭头接口，方法如下：

 **def id(self) -> int: ...**

获取导向箭头ID

 **def lane(self) -> Tessng.ILane: ...**

获取导向箭头所在的车道

 **def length(self) -> float: ...**

获取导向箭头的长度，默认单位：像素

 **def distToTerminal(self) -> float: ...**

获取导向箭头到的终点距离，默认单位：像素

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取导向箭头的多边型轮廓的顶点

------

### 2.22. IAccidentZone

事故区接口，方法如下：

 **def id(self) -> int: ...**

获取事故区ID

 **def name(self) -> str: ...**

获取事故区名称

 **def location(self) -> float: ...**

获取事故区距所在路段起点的距离，默认单位：米

 **def zoneLength(self) -> float: ...**

获取事故区长度，默认单位：米

 **def section(self) -> Tessng.ISection: ...**

获取事故区所在的路段或连接段

 **def roadId(self) -> int: ...**

获取事故区所在路段的ID

 **def roadType(self) -> str: ...**

获取事故区所在的道路类型(路段或连接段)

举例：

```python
# 获取事故区所在的道路类型
zone = tessngIFace().netInterface().createAccidentZone(accidentZone)
print(zone.roadType())

```

 **def laneObjects(self) -> typing.List: ...**

获取事故区占用的车道列表

 **def level(self) -> int: ...**

事故等级，分4级，默认为未定等级(0级)持续时间未定，事故区不会自动移除，一般事故(1级)持续时间10分钟，普通事故(2)级持续时间1小时，重大事故(3级)持续时间3小时

 **def duration(self) -> int: ...**

事故持续时间，单位：秒。如果值为0，事故持续时间由事故等级决定，大于0则由此值决定

 **def needRescue(self) -> int: ...**

是否需要救援，如果为-1，由事故等级决定，重大事故需要求援，如为0不需救援，如果为1需要救援 , 已废弃

 **def waitTimeBeforeRescue(self) -> int: ...**

救援车辆发车时间距事故产生的时间，默认单位：秒，默认60秒

 **def rescueTime(self) -> int: ...**

救援时间，默认60秒，即救援车辆停靠在事故区旁的时间，单位秒

------

### 2.23. IRoadWorkZone

施工区接口，方法如下：

 **def id(self) -> int: ...**

获取当前施工区ID

 **def name(self) -> str: ...**

获取施工区名称

 **def location(self) -> float: ...**

获取施工区距所在路段起点的距离，默认单位：米

 **def zoneLength(self) -> float: ...**

获取施工区长度，默认单位：米

 **def limitSpeed(self) -> float: ...**

施工区限速（最大车速:米/秒）

 **def sectionId(self) -> int: ...**

获取施工区所在路段或连接段的ID

举例：

```python
# 获取路段9上施工区所在路段的ID
zone = tessngIFace().netInterface().createRoadWorkZone(workZone)
print("施工区所在路段或连接段ID为:", zone.sectionId())

```

 **def sectionName(self) -> str: ...**

获取施工区所在路段或连接段的名称

 **def sectionType(self) -> str: ...**

获取施工区所在道路的道路类型，link:路段, connector:连接段

 **def laneObjects(self) -> typing.List: ...**

获取施工区所占的车道列表

 **def duration(self) -> int: ...**

施工持续时间，单位：秒。自仿真过程创建后，持续时间大于此值，则移除


### 2.24. ILimitedZone

限制区接口（借道施工的被借车道，限制对向车辆行走的区域？），方法如下：

 **def id(self) -> int: ...**

获取限制区ID

 **def name(self) -> str: ...**

获取限制区名称

 **def location(self) -> float: ...**

获取距起点距离，单位：米

 **def zoneLength(self) -> float: ...**

获取限制区长度，单位：米

 **def limitSpeed(self) -> float: ...**

获取限制区限速（最大限速），单位：千米/小时

 **def sectionId(self) -> int: ...**

获取限制区所在路段或连接段ID

 **def sectionName(self) -> str: ...**

获取限制区所在路段或连接段的名称

 **def sectionType(self) -> str: ...**

获取限制区所在道路的类型： "link"表示路段，"connector"表示连接段

 **def laneObjects(self) -> typing.List<Tessng.ILaneObject>: ...**

获取限制区所在车道对象列表

 **def duration(self) -> int: ...**

获取限制区的持续时间，单位：秒



### 2.25. IReconstruction

改扩建接口

 **def id(self) -> int: ...**

获取改扩建对象ID

 **def roadWorkZoneId(self) -> int: ...**

获取改扩建对象的起始施工区ID

 **def limitedZoneId(self) -> int: ...**

获取改扩建对象的被借道限速区ID

 **def passagewayLength(self) -> float: ...**

获取改扩建对象的保通长度，单位：米

 **def duration(self) -> int: ...**

获取改扩建的持续时间，单位：秒


 **def borrowedNum(self) -> int: ...**

获取改扩建的借道车道数量

 **def dynaReconstructionParam(self) -> Online.DynaReconstructionParam: ...**

获取改扩建动态参数，返回参数的长度单位为米制

### 2.26. IReduceSpeedArea

限速区接口

 **def id(self) -> int: ...**
获取限速区ID


 **def name(self) -> str: ...**

获取限速区名称

 **def location(self) -> float: ...**

获取距起点距离，单位：米

 **def areaLength(self) -> float: ...**

获取限速区长度，单位：米

 **def sectionId(self) -> int: ...**

获取限制区所在路段或连接段ID

 **def laneNumber(self) -> int: ...**

获取限速区车道序号

 **def tolaneNumber(self) -> int: ...**

获取限速区获取目标车道序号（当限速区设置在连接段时，返回值非空）


 **def polygon(self) -> QPolygonF : ...**

获取限速区获取多边型轮廓


### 2.27. ISignalPlan

信号控制方案接口

 **def id(self) -> int: ...**
获取信控方案ID ？？？


 **def name(self) -> str: ...**

获取信控方案名称（V3版本的信号灯组名称） ？？？


 **def trafficName(self) -> str: ...**

获取信号机名称 ？？？


 **def periodTime(self) -> int: ...**

获取获取信号周期，单位：秒 ？？？

 **def fromTime(self) -> int: ...**

获取什么起始时间，单位：秒 ？？？


 **def toTime(self) -> int: ...**

获取什么结束时间，单位：秒 ？？？

 **def Iphases(self) -> : typing.List<Tessng.ISignalPhase>: ...**

获取相位列表

 **def setName( name: str) -> None: ...**

设置信控方案（V3版本的信号灯组）名称 ？？？

 **def setPeriodTime(period: int) -> None: ...**

设置信控方案（V3版本的信号灯组）的信号周期， 单位：秒 ？？？


 **def setFromTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的起始时间， 单位：秒 ，



 **def setToTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的结束时间， 单位：秒 ，



### 2.28. ITrafficLight ???

信号机接口

 **def id(self) -> int: ...**
获取信控机ID 

 **def name(self) -> str: ...**
获取信控机ID 


 **def setName(name: str) -> None: ...**
设置信控机名称
\[in\] name：信号机名称


 **def IaddPlan(plan: Tessng.ISignalPlan) -> None: ...**
 为信号机添加信控方案
\[in\] plan ：信控方案， 可循环调用设置多时段信控方案

 **def IremovePlan(plan: Tessng.ISignalPlan) -> None: ...**
 移除/删除信号机的信控方案？？？  这个入参不应该是根据信控方案ID或者名称移除吗？ 传入完整的信控方案有点不好吧，不改也行
\[in\] plan ：信控方案， 


 **def Iplans(plan: Tessng.ISignalPlan) -> typing.List<Tessng.ISignalPhase>: ...**
 获取当前信号机中所有的信控方案
\[out\] plan ：信控方案


 **def idValid(self) -> boolen: ...**
检查信号机的有效性， 检查哪些内容 需要补充？？？




### 2.29. ITollLane

收费车道接口

 **def id(self) -> int: ...**
获取收费车道ID 

 **def name(self) -> str: ...**
获取收费车道名称


 **def distance(self) -> float: ...**
获取收费车道起点距当前所在路段起始位置的距离。单位：米 


 **def setName(self,name: str) -> None: ...**
设置收费车道名称
\[in\] name ：信控方案




 **def setWorkTime(self, startTime: int, endTime: int) -> None: ...**
设置收费车道的工作时间，不设置时，默认与仿真时间对应 ？？？
\[in\] startTime 开始时间（秒）
\[in\] endTime 结束时间（秒）




 **def tollPoints(self) -> typing.List<Tessng.ITollPoint>: ...**
获取收费车道所有收费点位
\[out\] 返回所有收费点位


### 2.30. ITollDecisionPoint

收费决策点接口

 **def id(self) -> int: ...**
获取收费决策点ID 

 **def name(self) -> str: ...**
获取收费决策点名称

 **def link(self) -> Tessng.ILink: ...**
获取收费决策点所在路段


 **def distance(self) ->float: ...**
获取收费决策点距离所在路段起点的距离，单位：米

 **def routings(self) ->Type.List<ITollRouting>: ...**
获取收费决策点的所有收费路径

 **def tollDisInfoList(self) ->Type.List<TollDisInfo>: ...**
获取收费决策点收费路径分配信息列表
返回值是 TollDisInfo 不是 ITollDisInfo？？？

 **def polygon(self) -> QPolygonF: ...**
获取收费决策点多边形轮廓， 这个玩意没啥意义，屏蔽掉就好


### 2.31. ITollRouting

收费路径接口

 **def id(self) -> int: ...**
获取收费路径ID 

 **def tollDeciPointId(self) -> int: ...**
获取收费路径所属收费决策点ID

 **def calcuLength(self) -> float: ...**
获取收费决策路径长度，单位：米； 收费路径长度是指：收费决策点到收费车道


 **def contain(self, pRoad: Tessng.ISection) -> boolen: ...**
判断输入的路段是否在当前路径上
\[in\] pRoad ：路段或连接段

 **def nextRoad(self,pRoad: Tessng.ISection) -> Tessng.ISection: ...**
获取输入路段的紧邻下游道路
\[in\] pRoad ：路段或连接段


 **def getLinks(self) -> Type.List<ILink>: ...**
获取当前收费路径的有序路段序列

### 2.32. ITollPoint

收费站停车点接口
 
 **def id(self) -> int: ...**
获取收费站停车点位ID 

 **def distance(self) -> float: ...**
获取收费站停车点距离路段起始位置的距离，单位：米；

 **def tollLaneId(self) -> int: ...**
获取收费站停车点所在的车道ID，注意不是车辆从左到右的序号


 **def setEnabled(self,enabled: bool) -> bool: ...**
设置当前收费站停车点是否启用， 返回是否设置成功的标签
\[in\] enabled ：默认为True表示启用， 若传入False则表明禁用该收费站点



### 2.33. IParkingStall

停车位接口

 **def id(self) -> int: ...**
获取停车位ID 

 **def parkingRegionId(self) -> int: ...**
获取所属停车区域ID



### 2.34. IParkingRegion

停车区域接口

 **def id(self) -> int: ...**
获取停车区域ID 

 **def name(self) -> str: ...**
获取所属停车区域名称


 **def setName(self,name: str) -> None: ...**
设置停车区域名称
\[in\] name ：停车区域名称

 **def parkingStalls(self) -> Type.List<IParkingStall>: ...**
获取所有停车位，返回列表


### 2.35. IParkingDecisionPoint

停车决策点接口

 **def id(self) -> int: ...**
获取停车决策点ID 

 **def name(self) -> str: ...**
获取停车决策点名称

 **def link(self) -> Tessng.ILink: ...**
获取停车决策点所在路段


 **def distance(self) -> float: ...**
获取停车决策点距离所在路段起点的距离，单位：米

 **def routings(self) -> Type.List<Tessng.IParkingRouting>: ...**
获取当前停车决策点对应的所有停车路径


 **def polygon(self) -> : QPolygonF: ...**
获取当前停车决策点多边形轮廓


### 2.36. IParkingRouting

停车决策路径接口

 **def id(self) -> int: ...**
获取停车决策路径ID 

 **def parkingDeciPointId(self) -> str: ...**
获取停车决策路径所属停车决策点的ID

 **def calcuLength(self) -> float: ...**
获取停车决策路径的长度，单位：米


 **def contain(self,pRoad: ISection) -> boolen: ...**
判断输入的道路（ 路段或连接段）是否在当前停车决策路径上
\[in\] pRoad ：道路对象，类型为Tessng.ISection


 **def nextRoad(self,pRoad: ISection) -> Tessng.ISection: ...**
获取输入道路的紧邻下游道路
\[in\] pRoad ：道路对象，类型为Tessng.ISection

 **def getLinks(self) -> Type.List<ILink>: ...**
获取当前停车路径的有序路段序列

### 2.37. IJunction

节点接口

 **def getId(self) -> int: ...**
获取节点ID  ???改成 id 

 **def name(self) -> int: ...**
获取节点名称

 **def setName(strName: str) -> int: ...**
设置节点名称
\[in\] strName ：节点名称


### 2.38. IPedestrian

行人接口

 **def getId(self) -> int: ...**
获取行人ID

 **def getRadius(self) -> float: ...**
获取行人半径大小， 单位：米

 **def getWeight(self) -> float: ...**
获取行人质量， 单位：千克


 **def getColor(self) -> float: ...**
获取行人颜色， 十六进制颜色代码，如"#EE0000"



 **def getPos(self) -> QPointF: ...**
获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米; 

 **def getAngle(self) -> float: ...**
获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度; 

 **def getDirection(self) -> Array: ...**
获取行人当前方向向量，二维向量；


 **def getElevation(self) -> float: ...**
获取行人当前位置的高程，单位：米


 **def getSpeed(self) -> float: ...**
获取行人当前速度，单位：米/秒

 **def getSpeed(self) -> float: ...**
获取行人期望速度，单位：米/秒

 **def getMaxSpeed(self) -> float: ...**
获取行人最大速度限制，单位：米/秒


 **def getAcce(self) -> float: ...**
获取行人当前加速度，单位：米/秒²

 **def getMaxAcce(self) -> float: ...**
获取行人最大加速度限制，单位：米/秒²

 **def getEuler(self) -> Array: ...**
获取行人欧拉角，用于三维的信息展示和计算，单位：度

 **def getWallFDirection(self) -> Array: ...**
获取墙壁方向单位向量


 **def getRegion(self) -> int: ...**
获取行人当前所在面域ID

 **def getPedestrianTypeId(self) -> int: ...**
获取行人类型ID


### 2.39. IPedestrianCrossWalkRegion

人行横道区域接口

 **def getId(self) -> int: ...**
获取行人ID ？？？ 没有吗

 **def getWidth(self) -> float: ...**
获取行人横道宽度，单位：米

 **def setWidth(width:float) -> float: ...**
设置行人横道宽度，单位：米

 **def getSceneLine(width:float) -> QLineF: ...**
获取人行横道起点到终点的线段，场景坐标系下？？？ 描述一下场景坐标系


 **def getAngle(self) -> float: ...**
获取人行横道倾斜角度，单位：度， QT像素坐标系下，X轴正方向为0，逆时针为正？？？

 **def setAngle(angle：float) -> float: ...**
设置人行横道倾斜角度，单位：度， QT像素坐标系下，X轴正方向为0，逆时针为正？？？

 **def getRedLightSpeedFactor(self) -> float: ...**
获取人行横道上红灯清尾速度系数

 **def setRedLightSpeedFactor(factor：float) -> None: ...**
设置人行横道上红灯清尾速度系数
\[in\] factor ：红灯清尾速度系数


 **def getUnitDirectionFromStartToEnd(self) -> Array: ...**
获取人行横道起点到终点的在场景坐标系下的单位方向向量，场景坐标系下？？？ 描述一下场景坐标系

 **def getStartControlPoint(self) -> ArQGraphicsEllipseItem: ...**
获取人行横道起点控制点，场景坐标系下？？？ 描述一下场景坐标系

 **def getEndControlPoint(self) -> ArQGraphicsEllipseItem: ...**
获取人行横道终点控制点，场景坐标系下？？？ 描述一下场景坐标系


 **def getLeftControlPoint(self) -> ArQGraphicsEllipseItem: ...**
获取人行横道左侧控制点，场景坐标系下？？？ 描述一下场景坐标系


 **def getRightControlPoint(self) -> ArQGraphicsEllipseItem: ...**
获取人行横道右侧控制点，场景坐标系下？？？ 描述一下场景坐标系

 **def getPositiveDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**
获取人行横道上管控正向通行的信号灯对象

 **def getNegativeDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**
获取人行横道上管控反向通行的信号灯对象

 **def isPositiveTrafficLightAdded(self) -> boolen: ...**
判断人行横道上是否存在管控正向通行的信号灯


 **def isReverseTrafficLightAdded(self) -> boolen: ...**
判断人行横道上是否存在管控反向通行的信号灯


### 2.40. IPedestrianFanShapeRegion

行人扇形面域接口

 **def getId(self) -> int: ...**
获取行人ID ？？？ 没有吗

 **def getInnerRadius(self) -> float: ...**
获取扇形面域内半径，单位：米

 **def getOuterRadius(width:float) -> float: ...**
获取扇形面域外半径，单位：米

 **def getStartAngle() -> float: ...**
获取扇形面域起始角度，单位：度  QT像素坐标系下，X轴正方向为0，逆时针为正？？？


 **def getSweepAngle() -> float: ...**
获取扇形面域扫过角度，单位：度  QT像素坐标系下，X轴正方向为0，逆时针为正？？？


### 2.41. IPedestrianPath

行人路径接口

 **def getId(self) -> int: ...**
获取行人路径ID 


 **def getPathStartPoint(self) -> Tessng.IPedestrianPathPoint: ...**
获取行人路径起点

 **def getPathEndPoint(self) -> Tessng.IPedestrianPathPoint: ...**
获取行人路径终点


 **def getPathMiddlePoints(self) -> Type.List<Tessng.IPedestrianPathPoint>: ...**
获取行人路径的中间点集合， 有序集合

 **def isLocalPath(self) ->boolen: ...**
判断当前行人路径是否为行人局部路径


### 2.42. IPedestrianPathPoint

行人路径点（起点，终点，途经点）接口

 **def getId(self) -> int: ...**
获取行人路径点ID 

 **def getScenePos(self) -> float: ...**
获取行人路径点场景坐标系下的位置, 场景坐标系下？？？ 描述一下场景坐标系

 **def getRadius(self) -> float: ...**
获取行人路径点的半径,单位：米




### 2.43. IPedestrianRegion

行人区域（面域）接口

 **def getId(self) -> int: ...**
获取行人区域(面域)ID

 **def getName(self) -> str: ...**
获取行人区域(面域)名称


 **def setName(self，name：str) -> None: ...**
设置行人区域(面域)名称


 **def setRegionColor(self，color:QColor) -> None: ...**
设置行人区域(面域)的颜色

 **def getPosition(self) -> QPointF: ...**
获取行人区域(面域)的位置， 这里范围的面域中心点的位置，相对场景坐标系 or QT像素坐标系？？？？


 **def setPosition(self，scenePos: QPoint) ->  None: ...**
设置行人区域(面域)的位置， 这里范围的面域中心点的位置，相对场景坐标系 or QT像素坐标系？？？？

 **def getGType(self) -> int: ...**
获取行人区域(面域)类型， 类型枚举见GType： ？？？？？

 **def getExpectSpeedFactor(self) -> int: ...**
获取行人区域(面域)的期望速度系数


 **def setExpectSpeedFactor(self，factor: float) -> None: ...**
设置行人区域(面域)的期望速度系数


 **def getElevation(self) -> None: ...**
获取面域高程， 单位：米

 **def setElevation(self，elevation: float) -> None: ...**
设置面域高程， 单位：米

 **def getPolygon(self) -> None: ...**
获取面域多边形

 **def getLayerId(self) -> int: ...**
获取面域所在图层ID

 **def setLayerId(self，id:int) -> None: ...**
将面域图层设置为图层id， 如果图层id非法，则不做任何改变

 **def isObstacle(self) -> boolen: ...**
判断行人区域(面域)是否为障碍物
 


 **def setObstacle(self，b: bool) -> None: ...**
设置行人区域(面域)为障碍物，or 为非障碍物
\[in\] b : 若b为True，则将行人区域设置为障碍物，若b为False，则将行人区域设置为非障碍物（行人区域原本为障碍物）


 **def idBoardingArea(self) -> boolen: ...**
判断行人区域(面域)是否为上客区域

 **def setIsBoardingArea(self，b: bool) -> boolen: ...**
设置行人区域(面域)为上客面域，or 为非上客面域
\[in\] b : 若b为True，则将行人区域设置为上客面域，若b为False，则将行人区域设置为非上客面域



 **def idAlightingArea(self) -> boolen: ...**
判断行人区域(面域)是否为下客区域

 **def setIsAlightingArea(self，b: bool) -> boolen: ...**
设置行人区域(面域)为下客面域，or 为非下客面域
\[in\] b : 若b为True，则将行人区域设置为下客面域，若b为False，则将行人区域设置为非下客面域




### 2.44. IPedestrianSideWalkRegion

人行道区域（面域）接口

 **def getWidth(self) -> int: ...**
获取人行道(面域)宽度， 单位：米


 **def setWidth(self，width:float) -> None: ...**
设置人行道(面域)宽度， 单位：米


 **def getVetexs(self) ->  Type.List<QGraphicsEllipseItem>: ...**
获取人行道(面域)顶点，即初始折线顶点


 **def getControl1Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**
获取人行道(面域)贝塞尔曲线控制点P1

 **def getControl2Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**
获取人行道(面域)贝塞尔曲线控制点P2

 **def getCandidateVetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**
获取人行道(面域)候选顶点


 **def removeVetex(self，index: int) ->None: ...**
删除人行道(面域)的第index个顶点： 顺序： 按照人行横道的绘制顺序排列


 **def insertVetex(self，pos: QPointF, index:int) ->None: ...**
在人行道(面域)的第index的位置插入顶点，初始位置为pos： 顺序： 按照人行横道的绘制顺序排列


 
 **def getId(self) -> int: ...**
获取行人区域(面域)ID 没有吗？？？

 **def getName(self) -> str: ...**
获取行人区域(面域)名称，  没有吗？？？





### 2.45. IPedestrianStairRegion

楼梯区域接口


 **def getWidth(self) -> int: ...**
获取楼梯宽度， 单位：米

 **def setWidth(self，width:float) -> None: ...**
设置楼梯(面域)宽度， 单位：米


 **def getStartPoint(self) -> QPointF: ...**
获取楼梯起始点，场景坐标系下


 **def getEndPoint(self) -> QPointF: ...**
获取楼梯终止点，场景坐标系下


 **def getStartConnectionAreaLength(self) -> float: ...**
获取起始衔接区域长度，单位：米


 **def getStartRegionCenterPoint(self) -> QPointF: ...**
获取起始衔接区域中心，场景坐标系下


 **def getEndRegionCenterPoint(self) -> QPointF: ...**
获取终止衔接区域中心，场景坐标系下

 **def getStartSceneRegion(self) -> QPainterPath: ...**
获取起始衔接区域形状，场景坐标系下

 **def getEndSceneRegion(self) -> QPainterPath: ...**
获取终止衔接区域形状，场景坐标系下

 **def getMainQueueRegion(self) -> QPainterPath: ...**
获取楼梯主体形状，场景坐标系下

 **def getFullQueueregion(self) -> QPainterPath: ...**
获取楼梯整体形状，场景坐标系下

 **def getMainQueuePolygon(self) -> QPolygonF : ...**
获取楼梯主体多边形，场景坐标系下

 **def getStairType(self) -> Tessng.StairType: ...**
获取楼梯类型, 类型枚举说明：？？？？


 **def getStartLayerId(self) -> int: ...**
获取楼梯的起始层级

 **def setStartLayerId(id:int) -> None: ...**
设置楼梯的起始层级
 

 **def getEndLayerId(self) -> int: ...**
获取楼梯的终止层级

 **def setEndLayerId(id:int) -> None: ...**
设置楼梯的终止层级


 **def getTransmissionSpeed(self) -> float: ...**
获取楼梯传输速度，单位米/秒， 如果是步行楼梯，则返回值应该是0


 **def setTransmissionSpeed(speed:float) -> None: ...**
设置楼梯传输速度，单位米/秒


 **def getHeadroom(self) -> float: ...**
获取楼梯净高，单位：米


 **def setHeadroom(headroom:float) -> None: ...**
设置楼梯净高，单位：米


 **def getStartControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的起点控制点


 **def getEndControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的终点控制点


 **def getLeftControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的左侧控制点

 **def getRightControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的右侧控制点

 **def getStartConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的起始衔接区域长度控制点

 **def getEndConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**
获取楼梯的终止衔接区域长度控制点



### 2.46. ICrosswalkSignalLamp

人行横道信号灯接口


 **def getICrossWalk(self) -> Tessng.IPedestrianCrossWalkRegion: ...**
获取行人信号灯所属人行横道
没有获取信号灯id，关联信号机或者信控方案的get, set接口吗？？？

创建行人信号灯的方法有吗？？？




------



## 3. 车辆及驾驶行为



### 3.1. IVehicle

车辆接口，用于访问、控制车辆。通过此接口可以读取车辆属性，初始化时设置车辆部分属性，仿真过程读取当前道路情况、车辆前后左右相邻车辆及与它们的距离，可以在车辆未驰出路网时停止车辆运行等。



接口方法：

 **def id(self) -> int: ...**

车辆ID，车辆ID的组成方式为 x * 100000 + y，每个发车点的x值不一样，从1开始递增，y是每个发车点所发车辆序号，从1开始递增。第一个发车点所发车辆ID从100001开始递增，第二个发车点所发车辆ID从200001开始递增。

 **def startLink(self) -> Tessng.ILink: ...**

车辆进入路网时起始路段

 **def startSimuTime(self) -> int: ...**

车辆进入路网时起始时间

 **def roadId(self) -> int: ...**

车辆所在路段link或connector连接段ID

 **def road(self) -> int: ...**

道路，如果在路段上返回ILink, 如果在连接段上返回IConnector

 **def section(self) -> Tessng.ISection: ...**

车辆所在的Section，即路段或连接段

 **def laneObj(self) -> Tessng.ILaneObject: ...**

车辆所在的车道或“车道连接”

 **def segmIndex(self) -> int: ...**

车辆在当前LaneObject上分段序号

 **def roadIsLink(self) -> bool: ...**

车辆所在道路是否路段

 **def roadName(self) -> str: ...**

道路名

 **def initSpeed(self, speed:float=...) -> float: ...**

初始化车速

参数：

\[in\] speed：车速，如果大于0，车辆以指定的速度从发车点出发，单位：像素/秒

返回：初始化车速，单位：像素/秒

 **def initLane(self, laneNumber:int, dist:float=..., speed:float=...) -> None: ...**

初始化车辆, laneNumber:车道序号，从0开始；dist，距起点距离，单位像素；speed：车速，像素/秒

参数：

\[in\] laneNumber：车道序号，从0开始

\[in\] dist：距离路段起点距离，单位：像素

\[in\] speed：起动时的速度，单位：像素/秒

举例：

```python
# 初始化飞机位置
if tmpId == 1:
    IVehicle.setVehiType(12)
    IVehicle.initLane(3, m2p(105), 0)

```

 

 **def initLaneConnector(self, laneNumber:int, toLaneNumber:int, dist:float=..., speed:float=...) -> None: ...**

初始化车辆, laneNumber: “车道连接”起始车道在所在路段的序号，从0开始自右往左；toLaneNumber:“车道连接”目标车道在所在路段的序号，从0开始自右往左， dist，距起点距离，单位像素；speed：车速，像素/秒

参数：

\[in\] laneNumber：车道序号，从0开始自右侧至左侧

\[in\] toLaneNumber：车道序号，从0开始自右侧至左侧

\[in\] dist：距离路段起点距离，单位：像素

\[in\] speed：起动时的速度，单位：像素/秒

 **def setVehiType(self, code:int) -> None: ...**

设置车辆类型，车辆被创建时已确定了类型，通过此方法可以改变车辆类型

参数：

\[in\] code：车辆类型编码

 **def length(self) -> float: ...**

路段或连接段长度，单位：像素

 **def setLength(self, len:float, bRestWidth:bool=...) -> None: ...**

设置车辆长度

参数：

\[in\] len：车辆长度，单位：像素

\[in\] bRestWidth：是否同比例约束宽度，默认为False

 **def laneId(self) -> int: ...**

如果toLaneId() 小于等于0，那么laneId()获取的是当前所在车道ID，如果toLaneId()大于0，则车辆在“车道连接”上，laneId()获取的是上游车道ID

 **def toLaneId(self) -> int: ...**

下游车道ID。如果小于等于0，车辆在路段的车道上，否则车辆在连接段的“车道连接”上

 **def lane(self) -> Tessng.ILane: ...**

获取当前车道，如果车辆在“车道连接”上，获取的是“车道连接”的上游车道

 **def toLane(self) -> Tessng.ILane: ...**

如果车辆在“车道连接”上，返回“车道连接”的下游车道，如果当前不在“车道连接”上，返回对象为空

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

获取当前“车道连接”，如果在车道上，返回空

 **def currBatchNumber(self) -> int: ...**

当前仿真计算批次

 **def roadType(self) -> int: ...**

车辆所在道路类型。包NetItemType中定义了一批常量，每一个数值代表路网上一种元素类型。如：GLinkType代表路段、GConnectorType代表连接段。

 **def limitMaxSpeed(self) -> float: ...**

车辆所在路段或连接段最大限速，兼顾到车辆的期望速度，单位：像素/秒

 **def limitMinSpeed(self) -> float: ...**

车辆所在路段或连接段最小限速，兼顾到车辆的期望速度，单位：像素/秒

 **def vehicleTypeCode(self) -> int: ...**

车辆类型编码。打开TESSNG，通过菜单“车辆”->“车辆类型”打开车辆类型编辑窗体，可以看到不同类型车辆的编码

 **def vehicleTypeName(self) -> str: ...**

获取车辆类型名，如“小客车”

 **def name(self) -> str: ...**

获取车辆名称

 **def vehicleDriving(self) -> Tessng.IVehicleDriving: ...**

获取车辆驾驶行为接口

 **def driving(self) -> None: ...**

驱动车辆。在每个运算周期，每个在运行的车辆被调用一次该方法;
如果用户使用该函数驱动车辆，那后续整个仿真声明周期都需要用户控制该辆车。即TESSNG将此车辆的控制权移交给用户。

 **def pos(self) -> PySide2.QtCore.QPointF: ...**

当前位置，横纵坐标单位：像素

 **def zValue(self) -> float: ...**

当前高程，单位：像素

 **def acce(self) -> float: ...**

当前加速度，单位：像素/秒^2

 **def currSpeed(self) -> float: ...**

当前速度，单位：像素/秒

 **def angle(self) -> float: ...**

当前角度，北向0度顺时针

 **def isStarted(self) -> bool: ...**

是否在运行，如果返回False，表明车辆已驰出路网或尚未上路

 **def vehicleFront(self) -> Tessng.IVehicle: ...**

获取前车， 可能为空

 **def vehicleRear(self) -> Tessng.IVehicle: ...**

后车， 可能为空

 **def vehicleLFront(self) -> Tessng.IVehicle: ...**

左前车， 可能为空

 **def vehicleLRear(self) -> Tessng.IVehicle: ...**

左后车， 可能为空

 **def vehicleRFront(self) -> Tessng.IVehicle: ...**

右前车， 可能为空

 **def vehicleRRear(self) -> Tessng.IVehicle: ...**

右后车， 可能为空

 **def vehiDistFront(self) -> float: ...**

前车间距，单位：像素; 若无前车，则范围固定的常量 ， 单位像素  

 **def vehiSpeedFront(self) -> float: ...**

前车速度，单位：像素/秒  若无前车，则范围固定的常量 单位像素 

 **def vehiHeadwayFront(self) -> float: ...**

距前车时距, 若无前车，则范围固定的常量  单位像素 

 **def vehiDistRear(self) -> float: ...**

后车间距，单位：像素, 若无后车，则范围固定的常量  单位像素 

 **def vehiSpeedRear(self) -> float: ...**

后车速度，单位：像素/秒  若无后车，则范围固定的常量  单位像素  

 **def vehiHeadwaytoRear(self) -> float: ...**

距后车时距，  若无前后车，则范围固定的常量  单位像素  

 **def vehiDistLLaneFront(self) -> float: ...**

相邻左车道前车间距，单位：像素； 若无目标车，则返回固定的常量  单位像素

 **def vehiSpeedLLaneFront(self) -> float: ...**

相邻左车道前车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素

 **def vehiDistLLaneRear(self) -> float: ...**

相邻左车道后车间距，单位：像素;  若无目标车，则返回固定的常量  单位像素

 **def vehiSpeedLLaneRear(self) -> float: ...**

相邻左车道后车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素

 **def vehiDistRLaneFront(self) -> float: ...**

相邻右车道前车间距，单位：像素;  若无目标车，则返回固定的常量  单位像素

 **def vehiSpeedRLaneFront(self) -> float: ...**

相邻右车道前车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素

 **def vehiDistRLaneRear(self) -> float: ...**

相邻右车道后车间距，单位：像素; 若无目标车，则返回固定的常量  单位像素

 **def vehiSpeedRLaneRear(self) -> float: ...**

相邻右车道后车速度，单位：像素/秒；  若无目标车，则返回固定的常量  单位像素

 **def setIsPermitForVehicleDraw(self, bDraw:bool) -> None: ...**

设置是否允许插件绘制车辆

 **def lLaneObjectVertex(self) -> typing.List: ...**

车道或车道连接中心线内点集

 **def routing(self) -> Tessng.IRouting: ...**

获取车辆当前路径； 返回的是当前车辆的全局路径，包括已经行驶过大的路段序列

 **def picture(self) -> PySide2.QtGui.QPicture: ...**

获取车辆图片

 **def boundingPolygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取车辆由方向和长度决定的四个拐角构成的多边型

 **def setTag(self, tag:int) -> None: ...**

设置标签表示的状态

 **def tag(self) -> int: ...**

获取标签表示的状态

 **def setTextTag(self, text:str) -> None: ...**

设置文本信息，用于在运行过程保存临时信息，方便开发

 **def textTag(self) -> str: ...**

文本信息，运行过程临时保存的信息，方便开发

 **def setJsonInfo(self, info:typing.Dict) -> None: ...**

设置json格式数据

 **def jsonInfo(self) -> typing.Dict: ...**

返回json格式数据

 **def jsonProperty(self, propName:str) -> typing.Any: ...**

返回json字段值

 **def setJsonProperty(self, key:str, value:typing.Any) -> None: ...**

设置json数据属性

   **以下方法设置****TESS NG****调用与车辆及驾驶行为相关方法时的调用频次**

 **def setSteps_afterCalcTracingType(self, steps:int) -> None: ...**

设置计算跟驰类型后处理方法afterCalcTracing被调用频次，即steps个计算周期调用1次

 **def setSteps_afterStep(self, steps:int) -> None: ...**

设置车辆一个计算周期后的处理方法afterStep被调用频次，即steps个计算周期调用1次

 **def setSteps_beforeMergingToLane(self, steps:int) -> None: ...**

设置车辆在连接段汇入前处理方法beforeMergingToLane被调用频次，即steps个计算周期调用1次

 **def setSteps_beforeNextRoad(self, steps:int) -> None: ...**

设置计算后续道路前处理方法beforeNextRoad被调用频次，即steps个计算周期调用1次

 **def setSteps_calcAcce(self, steps:int) -> None: ...**

设置计算加速度方法calcAcce被调用频次，即steps个计算周期调用1次

 **def setSteps_calcChangeLaneSafeDist(self, steps:int) -> None: ...**

设置计算安全变道距离方法calcChangeLaneSafeDist被调用频次，即steps个计算周期调用1次

 **def setSteps_calcDistToEventObj(self, steps:int) -> None: ...**

设置计算到事件对象距离方法calcDistToEventObj被调用频次，即steps个计算周期调用1次

 **def setSteps_calcLimitedLaneNumber(self, steps:int) -> None: ...**

设置计算限行车道方法calcLinitedLaneNumber被调用频次，即steps个计算周期调用1次

 **def setSteps_calcMaxLimitedSpeed(self, steps:int) -> None: ...**

设置计算最大限速方法calcMaxLinitedSpeed被调用频次，即steps个计算周期调用1次

 **def setSteps_calcSpeedLimitByLane(self, steps:int) -> None: ...**

设置计算车道限速方法calcSpeedLimitByLane被调用频次，即steps个计算周期调用1次

 **def setSteps_isStopDriving(self, steps:int) -> None: ...**

设置是否停止运行方法isStopDriving被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcAngle(self, steps:int) -> None: ...**

设置重新计算角度方法reCalcAngle被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcToLeftFreely(self, steps:int) -> None: ...**

设置计算左自由变道方法reCalcToLeftFreely被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcToLeftLane(self, steps:int) -> None: ...**

设置计算左强制变道方法reCalcToLeftLane被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcToRightFreely(self, steps:int) -> None: ...**

设置计算右自由变道方法reCalcToRightFreely被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcToRightLane(self, steps:int) -> None: ...**

设置计算右强制变道方法reCalcToRightLane被调用频次，即steps个计算周期调用1次

 **def setSteps_reCalcdesirSpeed(self, steps:int) -> None: ...**

设置重新计算期望速度方法reCalcdesirSpeed被调用频次，即steps个计算周期调用1次

 **def setSteps_reSetAcce(self, steps:int) -> None: ...**

设置重新计算加速度方法reSetAcce被调用频次，即steps个计算周期调用1次

 **def setSteps_reSetFollowingType(self, steps:int) -> None: ...**

设置重新计算跟驰类型方法reSetFollowingType被调用频次，即steps个计算周期调用1次

 **def setSteps_reSetSpeed(self, steps:int) -> None: ...**

设置重新计算车速方法reSetSpeed被调用频次，即steps个计算周期调用1次



------

### 3.2. IVehicleDriving

驾驶行为接口，通过此接口可以控制车辆的左右变道、设置车辆角度，对车辆速度、坐标位置等进行控制，可以在路网中间停止车辆运行，将车辆移出路网，等等。

接口方法：

 **def vehicle(self) -> Tessng.IVehicle: ...**

当前驾驶车辆

 **def getRandomNumber(self) -> int: ...**

获取随机数

 **def nextPoint(self) -> bool: ...**

计算下一点位置，过程包括计算车辆邻车关系、公交车是否进站是否出站、是否变道、加速度、车速、移动距离、跟驰类型、轨迹类型等

 **def zeroSpeedInterval(self) -> int: ...**

当前车速为零持续时间(毫秒)

 **def isHavingDeciPointOnLink(self) -> bool: ...**

当前是否在路段上且有决策点

 **def followingType(self) -> int: ...**

跟驰车辆的类型，即当前车辆前车的类型，分为：0：停车，1: 正常，5：急减速，6：急加速，7：汇入， 8：穿越，9：协作减速，10：协作加速，11：减速待转，12：加速待转

 **def isOnRouting(self) -> bool: ...**

当前是否在路径上

 **def stopVehicle(self) -> None: ...**

停止运行，车辆移出路网

 **def angle(self) -> float: ...**

旋转角，北向0度顺时针

 **def setAngle(self, angle:float) -> None: ...**

设置车辆旋转角； 

参数：

\[in\] angle：旋转角，一周360度

举例：

```python
if  vehi.roadId() == 5:         
    vehi_currentDistToEnd = vehi.vehicleDriving().distToEndpoint(True)       
    if  p2m(vehi_currentDistToEnd) < 50:            
        vehi.vehicleDriving().setAngle(vehi.angle() + 45.0) 
```

 

 **def euler(self, bPositive:bool=...) -> PySide2.QtGui.QVector3D: ...**

返回车辆欧拉角

参数：

\[in\] bPositive：车头方向是否正向计算，如果bPosiDire为False则反向计算

 **def desirSpeed(self) -> float: ...**

当前期望速度，与车辆自身期望速度和道路限速有关，不大于道路限速，单位：像素/秒

 **def getCurrRoad(self) -> Tessng.ISection: ...**

返回当前所在路段或连接段

 **def getNextRoad(self) -> Tessng.ISection: ...**

下一路段或连接段

 **def differToTargetLaneNumber(self) -> int: ...**

与目标车道序号的差值，不等于0表示有强制变道意图，大于0有左变道意图，小于0有右变道意图，绝对值大于0表示需要强制变道次数

 **def toLeftLane(self) -> None: ...**

左变道

 **def toRightLane(self) -> None: ...**

右变道

 **def laneNumber(self) -> int: ...**

当前车道序号，最右侧序号为0

 **def initTrace(self) -> None: ...**

初始化轨迹

 **def setTrace(self, lPoint:typing.Sequence) -> None: ...**

设置轨迹

参数：

\[in\] lPoint：轨迹点坐标集合

 **def calcTraceLength(self) -> None: ...**

计算轨迹长度； 前提是：TESSNG开启车辆轨迹记录|输出 功能

 **def tracingType(self) -> int: ...**

返回轨迹类型，分为：0：跟驰，1：左变道，2：右变道，3：左虚拟変道，4：右虚拟变道，5：左转待转，6：右转待转，7：入湾，8：出湾

 **def setTracingType(self, type:int) -> None: ...**

设置轨迹类型； TESSNG车辆后续运动轨迹按照此轨迹类型的动机产生动作，但因为阈值条件有可能环境不满足，因此动机并不一定能执行

 **def setLaneNumber(self, number:int) -> None: ...**

设置当前车道序号

参数：

\[in\] number：车道序号

 **def currDistance(self) -> float: ...**

当前计算周期移动距离，单位：像素

 **def currDistanceInRoad(self) -> float: ...**

当前路段或连接上已行驶距离，单位：像素

 **def setCurrDistanceInRoad(self, dist:float) -> None: ...**

设置当前路段已行驶距离

参数：

\[in\] dist：距离，单位：像素

 **def setVehiDrivDistance(self, dist:float) -> None: ...**

设置当前已行驶总里程

参数：

\[in\] dist：总里程，单位：像素

 **def getVehiDrivDistance(self) -> float: ...**

已行驶总里程

 **def currDistanceInSegment(self) -> float: ...**

当前分段已行驶距离

 **def setCurrDistanceInSegment(self, dist:float) -> None: ...**

设置当前分段已行驶的距离

 **def setSegmentIndex(self, index:int) -> None: ...**

设置分段序号

 **def setCurrDistanceInTrace(self, dist:float) -> None: ...**

设置曲化轨迹上行驶的距离

 **def setIndexOfSegmInTrace(self, index:int) -> None: ...**

设置曲化轨迹上的分段序号

 **def setChangingTracingType(self, b:bool) -> None: ...**

设置是否改变轨迹，当设为True时会对轨迹初始化，如设轨迹分段序号为0，等

 **def currDistance(self) -> float: ...**

当前时间段移动距离

 **def setRouting(self, pRouting:Tessng.IRouting) -> bool: ...**

设置路径，外界设置的路径不一定有决策点，可能是临时创建的，如果车辆不在此路径上则设置不成功并返回False

\[in\] pRouting：路径

举例：

```python
# 修改路径（L1所有车辆均修改为右转路径）
for vehi in allVehiStarted_lst:
    if vehi.roadId() == 1:
        # 修改车辆路径
        decisionPoints_lst = netiface.decisionPoints()
        decisionPoint_link1 = None
        for decisionPoint in decisionPoints_lst:
            if decisionPoint.link().id() == 1:
                decisionPoint_link1 = decisionPoint
                break
        decisionPoint_link1_routings_lst = []
        if decisionPoint_link1:
            decisionPoint_link1_routings_lst = decisionPoint_link1.routings()
        if len(decisionPoint_link1_routings_lst) > 0:
            if vehi.routing() != decisionPoint_link1_routings_lst[-1]:
                if (vehi.vehicleDriving().setRouting(decisionPoint_link1_routings_lst[-1])):
                    print("{}车辆修改路径成功。".format(vehi.id()))

```

 

 **def setSegmentIndex(self, index:int) -> None: ...**

设置分段序号

\[in\] index：分段序号

 **def currDistanceInSegment(self) -> float: ...**

当前在分段上已行驶距离

 **def setCurrDistanceInSegment(self, dist:float) -> None: ...**

设置在分段上已行驶距离

 **def setX(self, posX:float) -> None: ...**

设置横坐标

参数：

\[in\] posX：横坐标：单位：像素

 **def setY(self, posY:float) -> None: ...**

设置纵坐标

参数：

\[in\] posY：纵坐标：单位：像素

 **def setV3z(self, v3z:float) -> None: ...**

设置高程坐标

参数：

\[in\] v3z：高程坐标：单位：像素

 **def changingTrace(self) -> typing.List: ...**

变轨点集，车辆不在车道中心线或“车道连接”中心线上时的轨迹，如变道过程的轨迹点集

 **def changingTraceLength(self) -> float: ...**

变轨长度

 **def distToStartPoint(self, fromVehiHead:bool=..., bOnCentLine:bool=...) -> float: ...**

在车道或车道连接上到起点距离

参数：

\[in\] fromVehiHead：是否从车头计算，如果为False，从车辆中心点计算，默认值为False

\[in\] bOnCentLine：当前是否在中心线上

 **def distToEndpoint(self, fromVehiHead:bool=...) -> float: ...**

在车道或“车道连接”上车辆到终端距离

参数：

\[in\] fromVehiHead：是否从车头计算，如果为False，从车辆中心点计算，默认值为False

 **def setRouting(self, pRouting:Tessng.IRouting) -> bool: ...**

设置路径，外界设置的路径不一定有决策点，可能是临时创建的，如果车辆不在此路径上则设置不成功并返回False

 **def routing(self) -> Tessng.IRouting: ...**

当前路径

 **def moveToLane(self, pLane:Tessng.ILane, dist:float) -> bool: ...**

将车辆移到另一条车道上； 车辆会瞬间从当前车道移动到目标车道及指定的距离出，后续TESSNG接管车辆继续行驶 

参数：

\[in\] pLane：目标车道

\[in\] dist：到目标车道起点距离，单位：像素

 **def moveToLaneConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float) -> bool: ...**

将车辆移到另一条车道连接上; 车辆会瞬间从当前位置移动到目标车道连接及指定的距离出，后续TESSNG接管车辆继续行驶 

参数：

\[in\] pLaneConnector：目标车道

\[in\] dist：到目标车道起点距离，单位：像素

举例：

```python
# 20秒时，移动飞机
if simuTime == 20 * 1000:
    # 查找飞机和id为1的车道
    plane = tessngIFace().simuInterface().getVehicle(100001)
    lane = tessngIFace().netInterface().findLane(1)
    if plane.vehicleDriving().moveToLane(lane, 400):
        print("移动飞机成功")

```

 

 **def move(self, pILaneObject:Tessng.ILaneObject, dist:float) -> bool: ...**

移动车辆到到另一条车道或“车道连接”； 使用该函数后，车辆脱离TESSNG管控，需要用户维护后期车辆运动

参数：

\[in\] pILaneObject：目标车道或“车道连接”

\[in\] dist：到目标车道起点距离，单位：像素

举例：

```python
# 车辆位置移动（以L5路段上的车辆为例，在3秒时直接将该车移动过路口）
if simuTime == 10 * 1000:
    for index, vehi in enumerate(allVehiStarted_lst):
        print(index,vehi.id())
        if vehi.roadId() == 5:
            next_link = netiface.findLink(9)
            laneObjs_next_link_lst = next_link.laneObjects()
            if (vehi.vehicleDriving().move(laneObjs_next_link_lst[index % len(laneObjs_next_link_lst)],
                                           float(index % 100))):
                print("{}车辆移动成功。".format(vehi.id()))

```

 

 **def changingTrace(self) -> typing.List: ...**

变轨点集，如变道轨迹、公交车进入港湾式站点轨迹。

 **def changingTraceLength(self) -> float: ...**

变轨长度，如变道轨迹长度、公交车进入港湾式站点轨迹长度，单位：像素。

 **def calcTraceLength(self) -> None: ...**

计算变轨长度，如计算变道轨迹长度等。

 **def setTrace(self, lPoint:typing.Sequence) -> None: ...**

设置变轨轨迹； 车辆后续会沿着当前设置的轨迹运动，运动速度默认是当前车辆瞬时速度；该函数一旦使用就需要用户在后续仿真过程中全称控制当前车辆的运动，包括速度，加速度，与其他车辆的交互

 **def setTracingType(self, type:int) -> None: ...**

设置轨迹类型

\[in\] type：轨迹类型 0：跟驰，1：左变道，2：右变道，3：左虚拟変道, 4：右虚拟变道，5：左转待转，6：右转待转, 7：入湾，8：出湾



------



## 4. 自定义窗口组件TessInterface

TessInterface 是TESSN对外暴露的顶级接口，下面有三个子接口：NetInterface、SimuInterface、GuiInterface，分别用于访问或控制路网、仿真过程和用户交互界面。

获取顶层接口的方法是：tessngIFace()。

下面是几个接口方法的说明：

 **def config(self) -> typing.Dict: ...**

获取json对象，其中保存了config.json配置文件中的信息。

每次加载路网时会重新加载配置信息，上次通过setConfigProperty()方法设置的属性会在重新加载路网后丢失。

 **def setConfigProperty(self, key:str, value:typing.Any) -> None: ...**

设置配置属性

 **def releasePlugins(self) -> None: ...**

卸载并释放插件

 **def netInterface(self) -> Tessng.NetInterface: ...**

返回用于访问控制路网的接口NetInterface

 **def simuInterface(self) -> Tessng.SimuInterface: ...**

返回用于控制仿真过程的接口SimuInterface

 **def guiInterface(self) -> Tessng.GuiInterface: ...**

返回用于访问控制用户介面的接口GuiInterface

 **def loadPluginFromMem(self, pPlugin:Tessng.TessPlugin) -> bool: ...**

从内存加载插件，此方法便于用户基于API进行二次开发。

下面对三个子接口进行详解：

### 4.1. NetInterface

NetInterface是TessInterface的子接口，用于访问、控制路网的接口，通过这个接口可以从文件加载路网、创建路段、连接段、发车点等。

下面对NetInterface接口方法作详细解释。

 **def openNetFle(self, filePath:str) -> None: ...**

打开保存在文件中的路网

参数：

\[in\] filePath：路网文件全路径名

举例：

```python
openNetFile("C:/TESSNG/Example/杭州武林门区域路网公交优先方案.tess")
```

 

 **def openNetByNetId(self, netId:int) -> None: ...**

从专业数据库加载路网

 **def saveRoadNet(self) -> None: ...**

保存路网

 **def netFilePath(self) -> str: ...**

获取路网文件全路径名，如果是专业数据保存的路网，返回的是路网ID

 **def roadNet(self) -> Tessng.IRoadNet: ...**

获取路网对象

 **def netAttrs (self) -> Tessng.IRoadNet: ...**

获取路网对象，如果路网是从opendrive导入的，此路网对象可能保存了路网中心点所在的经纬度坐标，以及大地坐标等信息

 **def setNetAttrs(self, name:str, sourceType:str=..., centerPoint:PySide2.QtCore.QPointF=..., backgroundUrl:str=..., otherAttrsJson:typing.Dict=...) -> Tessng.IRoadNet: ...**

设置路网基本信息

**参数：**

\[in\] name:路网名称

\[in\] centerPoint:中心点坐标所在路网，默认为(0,0) ，用户也可以将中心点坐标保存到otherAttrsJson字段里

\[in\] sourceType:数据来源分类，默认为 “TESSNG”，表示路网由TESSNG软件直接创建。取值“OPENDRIVE”，表示路网是经过opendrive路网导入而来

\[in\] backgroundUrl：底图路径

\[in\] otherAttrsJson:保存在json对象中的其它属性，如大地坐标等信息。

 **def graphicsScene(self) -> PySide2.QtWidgets.QGraphicsScene: ...**

获取场景对象

 **def graphicsView(self) -> PySide2.QtWidgets.QGraphicsView: ...**

获取视图对象

 **def sceneScale(self) -> float: ...**

场景中的像素比，单位：米/像素

 **def setSceneSize(self, w:float, h:float) -> None: ...**

设置场景大小，参数w及h分别是场景宽度和高度，单位：米

 **def sceneWidth(self) -> float: ...**

场景宽度，单位：米

 **def sceneHeight(self) -> float: ...**

场景高度，单位：米

 **def backgroundMap(self) -> PySide2.QtCore.QByteArray: ...**

背景图

 **def sections(self) -> typing.List: ...**

所有Section

 **def linkIds(self) -> typing.List: ...**

路段ID集

 **def linkCount(self) -> int: ...**

路段数

 **def links(self) -> typing.List: ...**

路段集

 **def findLink(self, id:int) -> Tessng.ILink: ...**

根据路段ID查找路段

 **def findLane(self, id:int) -> Tessng.ILane: ...**

根据车道ID查找车道

 **def findLaneConnector(self, fromLaneId:int, toLaneId:int) -> Tessng.ILaneConnector: ...**

根据“车道连接”ID查找“车道连接”

 **def connectorIds(self) -> typing.List: ...**

连接段ID集

 **def connectorCount(self) -> int: ...**

连接段数

 **def connectors(self) -> typing.List: ...**

连接段集

 **def findConnector(self, id:int) -> Tessng.IConnector: ...**

根据连接段ID查找连接段

 **def findConnectorArea(self, id:int) -> Tessng.IConnectorArea: ...**

根据面域ID查找面域

 **def findConnectorByLinkIds(self, fromLinkId:int, toLinkId:int) -> Tessng.IConnector: ...**

根据起始路段ID及目标路段ID查找连接段

 **def findLaneConnector(self, fromLaneId:int, toLaneId:int) -> Tessng.ILaneConnector: ...**

根据起始车道ID及目标车道ID查找“车道连接”

 **def guidArrowCount(self) -> int: ...**

导向箭头数

 **def guidArrowIds(self) -> typing.List: ...**

导向箭头ID集

 **def signalLampCount(self) -> int: ...**

信号灯数

 **def signalLampIds(self) -> typing.List: ...**

信号灯ID集

 **def signalLamps(self) -> typing.List: ...**

信号灯集

 **def findSignalLamp(self, id:int) -> Tessng.ISignalLamp: ...**

根据信号灯ID查找信号灯

 **def findSignalPhase(self, id:int) -> Tessng.ISignalPhase: ...**

根据信号相位ID查找信号相位??? 目前的接口有点问题，返回的是none

 **def signalGroupIds(self) -> typing.List: ...**

信号灯组ID集

 **def signalGroups(self) -> typing.List: ...**

信号灯组集

 **def findSignalGroup(self, id:int) -> Tessng.ISignalGroup: ...**

根据信号灯组ID查找信号灯组

 **def dispatchPoints(self) -> typing.List: ...**

发车点集。

 **def findDispatchPoint(self, id:int) -> Tessng.IDispatchPoint: ...**

根据发车点ID查找发车点

参数：

\[in\] id：发车点ID

 **def buslines(self) -> typing.List: ...**

公交线路集

 **def findBusline(self, buslineId:int) -> Tessng.IBusLine: ...**

根据公交线路ID查找公交线路

参数：

\[in\] buslineId：公交线路ID

 **def findBuslineByFirstLinkId(self, linkId:int) -> Tessng.IBusLine: ...**

根据公交线路起始路段ID查找公交线路

参数：

\[in\] linkId：公交线路起始段ID

 **def busStations(self) -> typing.List: ...**

公交站点集

 **def findBusStation(self, stationId:int) -> Tessng.IBusStation: ...**

根据公交站点ID查询公交站点

 **def findBusStationLineByStationId(self, stationId:int) -> typing.List: ...**

根据公交站点ID查询相关BusLineStation

 **def allConnectorArea(self) -> typing.List: ...**

面域集

 **def laneCenterPoints(self, laneId:int) -> typing.List: ...**

**指定车道中心线断点集**

参数：­

\[in\]laneId：指定车道ID

 **def linkCenterPoints(self, linkId:int) -> typing.List: ...**

指定路段中心线断点集

参数：

\[in\]linkId：指定路段ID

 **def judgeLinkToCross(self, linkId:int) -> bool: ...**

判断路段去向是否进入交叉口， 以面域是否存在多连接段以及当前路段与后续路段之间的角度为依据

 **def getIDByItemName(self, name:str) -> int: ...**

根据路网元素名获取自增ID

参数：

\[in\] name：路网元素名。路网元素名的定义在文件plugin/_netitem.h中定义

 **def createLink(self, lCenterPoint:typing.Sequence, laneCount:int, linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterPoint：路段中心线断点集

\[in\] laneCount：车道数

\[in\] linkName：路段名，默认为空，将以路段ID作为路段名

\[in\] bAddToScene：创建后是否放入路网场景，默认为True

举例：

```python
startPoint = QPointF(m2p(-300), 0)
endPoint = QPointF(m2p(300), 0)
lPoint = [startPoint, endPoint]
link1 = netiface.createLink(lPoint, 7, "曹安公路")

```

 

 

返回：路段对象。

 **def createLink3D(self, lCenterV3:typing.Sequence, laneCount:int, linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterV3：路段中心线断点序列，每个断点都是三维空间的点

\[in\] laneCount：车道数

\[in\] linkName：路段名

返回：路段对象。

 **def createLinkWithLaneWidth(self, lCenterPoint:typing.Sequence, lLaneWidth:typing.Sequence, linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterPoint：路段中心线断点序列

\[in\] lLaneWidth：车道宽度列表

\[in\] linkName：路段名

\[in\] bAddToScene：是否加入场景，默认为True

返回：路段对象。

 **def createLink3DWithLaneWidth(self, lCenterV3:typing.Sequence, lLaneWidth:typing.Sequence, linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterV3：路段中心线断点序列，每个断点都是三维空间的点

\[in\] lLaneWidth：车道宽度列表

\[in\] linkName：路段名

\[in\] bAddToScene：是否加入场景，默认为True

返回：路段对象。

 **def createLink3DWithLanePoints(self, lCenterLineV3:typing.Sequence, lanesWithPoints:typing.Sequence, linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterLineV3：路段中心点集(对应TESSNG路段中心点)，每个点都是三维空间的

\[in\] lanesWithPoints：车道数据集合，每个成员是QMap< QString, QList< QVector3D>>类型数据，有三个key，分别是“left”、“center”、“right”、分别表示一条车道左、中、右侧断点序列。

\[in\] linkName：路段名，默认为路段ID

\[in\] bAddToScene：是否加入路网，默认True表示加入

返回：路段对象

 **def createConnector(self, fromLinkId:int, toLinkId:int, lFromLaneNumber:typing.Sequence, lToLaneNumber:typing.Sequence, connName:str=..., bAddToScene:bool=...) -> Tessng.IConnector: ...**

创建连接段

参数：

\[in\] fromLinkId：起始路段ID

\[in\] toLinkId：目标路段ID

\[in\] lFromLaneNumber：连接段起始车道序号集

\[in\] LToLaneNumber：连接段目标车道序号集

\[in\] connName：连接段名，默认为空，以两条路段的ID连接起来作为名称

\[in\] bAddToScene：创建后是否放入路网场景，默认为True

 

 **def createConnector3DWithPoints(self, fromLinkId:int, toLinkId:int, lFromLaneNumber:typing.Sequence, lToLaneNumber:typing.Sequence, laneConnectorWithPoints:typing.Sequence, connName:str=..., bAddToScene:bool=...) -> Tessng.IConnector: ...**

创建连接段，创建连接段后将“车道连接”中自动计算的断点集用参数laneConnectorWithPoints断点替换

参数：

\[in\] fromLinkId：起始路段ID

\[in\] toLinkId：目标路段ID

\[in\] lFromLaneNumber：起始路段参于连接的车道序号

\[in\] lToLaneNumber：目标路段参于连接的车道序号

\[in\] laneConnectorWithPoints：“车道连接”数据列表，成员是QMap< QString, QList< QVector3D>>类型数据，有三种key，分别是“left”、“center”、“right”，表示一条“车道连接”左、中、右侧断点序列

\[in\] connName：连接段名，默认将起始路段ID和目标路段ID用“_”连接表示连接段名，如“100_101”。

\[in\] bAddToScene：是否加入到场景，默认为True

返回：连接段对象

 **def createDispatchPoint(self, pLink:Tessng.ILink, dpName:str=..., bAddToScene:bool=...) -> Tessng.IDispatchPoint: ...**

创建发车点

参数：

\[in\] pLink：路段，在其上创建发车点

\[in\] dpName：发车点名称，默认为空，将以发车点ID作为名称

\[in\] bAddToScene：创建后是否放入路网场景，默认为True

 **def createVehicleComposition(self, name:str, lVehiComp:typing.Sequence) -> int: ...**

创建车型组成，如果车型组成名已存在或相关车型编码不存在或相关车型占比小于0则返回-1，否则新建车型组成，并返回车型组成编码

参数：

\[in\] name：车型组成名

\[in\] lVehiComp：不同车型占比列表

举例：

```python
# 创建车辆组成及指定车辆类型
vehiType_proportion_lst = []
# 车型组成：小客车0.3，大客车0.2，公交车0.1，货车0.4
vehiType_proportion_lst.append(Online.VehiComposition(1, 0.3))
vehiType_proportion_lst.append(Online.VehiComposition(2, 0.2))
vehiType_proportion_lst.append(Online.VehiComposition(3, 0.1))
vehiType_proportion_lst.append(Online.VehiComposition(4, 0.4))
vehiCompositionID = netiface.createVehicleComposition("动态创建车型组成", vehiType_proportion_lst)

```

 

 **def shortestRouting(self, pFromLink:Tessng.ILink, pToLink:Tessng.ILink) -> Tessng.IRouting: ...**

计算最短路径

参数：

\[in\] pFromLink：起始路段

\[in\] pToLink：目标路段

返回：最短路径对象，包含经过的路段对象序列

 **def createRouting(self, lILink:typing.Sequence) -> Tessng.IRouting: ...**

用连续通达的路段序列创建路径

参数：

\[in\] lILink：路段对象序列

返回：路径对象

 **def decisionPoints(self) -> typing.List: ...**

决策点列表

 **def findDecisionPoint(self, id:int) -> Tessng.IDecisionPoint: ...**

根据ID查找决策点

\[in\] id：决策点ID

返回：决策点对象

 **def vehiInfoCollectors(self) -> typing.List: ...**

所有车辆检测器

 **def findVehiInfoCollector(self, id:int) -> Tessng.IVehicleDrivInfoCollector: ...**

根据ID查询车辆检测器

参数：

\[in\] id：车辆检测器ID

返回：车辆检测器对象

 **def vehiQueueCounters(self) -> typing.List: ...**

所有排队计数器

 **def findVehiQueueCounter(self, id:int) -> Tessng.IVehicleQueueCounter: ...**

根据ID查询车辆排队计数器

参数：

\[in\] id：排队计数器ID

返回：排队计数器对象

 **def findVehiQueueCounter(self, id:int) -> Tessng.IVehicleQueueCounter: ...**

所有车辆行程时间检测器，返回列表中的每一个元素是一对行程时间检测器的起始检测器

 **def findVehiTravelDetector(self, id:int) -> Tessng.IVehicleTravelDetector: ...**

根据ID查询车辆行程时间检测器，返回一对行程时间检测器中起始检测器

参数：

\[in\] id：行程时间检测器ID

返回：行程时间检测器对象

 **def findRouting(self, id:int) -> Tessng.IRouting: ...**

根据路径ID查找路径

 **def crossPoints(self, pLaneConnector:Tessng.ILaneConnector) -> typing.List: ...**

当前“车道连接”穿过其它“车道连接”形成的交叉点列表

参数：

\[in\] pLaneConnector：“车道连接”对象

返回：交叉点列表

举例：

```python
# 当前“车道连接”穿过其它“车道连接”形成的交叉点列表
laneConnectors = tessngIFace().netInterface().findConnector(6).laneConnectors()
for laneConnector in laneConnectors:
    crossPoints = tessngIFace().netInterface().crossPoints(laneConnector)
    for crossPoint in crossPoints:
        print("主车道连接，即被交叉的“车道连接”：", crossPoint.mpLaneConnector.id())
        print("交叉点坐标为：(", crossPoint.mCrossPoint.x(), ",", crossPoint.mCrossPoint.y(), ")")

```

 

 **def createEmptyNetFile(self, filePath:str, dbver:int=...) -> bool: ...**

创建空白路网

参数：

\[in\] filePath：空白路网全路径名

\[in\] dbver:：数据库版本

 **def createLink3DWithLanePointsAndAttrs(self, lCenterLineV3:typing.Sequence, lanesWithPoints:typing.Sequence, lLaneType:typing.Sequence, lAttr:typing.Sequence=..., linkName:str=..., bAddToScene:bool=...) -> Tessng.ILink: ...**

创建路段

参数：

\[in\] lCenterLineV3：路段中心点集(对应TESSNG路段中心点)

\[in\] lanesWithPoints：车道点集的集合

\[in\] lLaneType:车道类型集

\[in\] lAttr:车道附加属性集

\[in\] linkName：路段名，默认为路段ID,

\[in\] bAddToScene：是否加入路网，默认True表示加入

返回：路段对象

 **def removeLink(self, pLink:Tessng.ILink) -> None: ...**

移除路段，从场景中移除pLink，但不从文件中删除，保存路网后才会从路网文件中删除

参数：

\[in\] pLink：将要移除的路段

 **def updateLink(self, link:Tessng._Link, lLane:typing.Sequence=..., lPoint:typing.Sequence=...) -> Tessng.ILink: ...**

更新路段，更新后返回路段对象

参数：

\[in\] link：更新的路段数据

\[in\] lLink：更新的车道列表数据

\[in\] lPoint：更新的断点集合

返回：更新后的路段对象

 **def removeConnector(self, pConnector:Tessng.IConnector) -> None: ...**

移除连接段，从场景中移除pLink，但不从文件中删除，保存路网后才会从路网文件中删除

参数：

\[in\] pConnector：连接段对象

 **def updateConnector(self, connector:Tessng._Connector) -> Tessng.IConnector: ...**

更新连接段，更新后返回连接段对象

参数：

\[in\] connector：连接段数据

返回：更新后的连接段对象

 **def removeDispatchPoint(self, pDispPoint:Tessng.IDispatchPoint) -> bool: ...**

移除发车点

参数：

\[in\] pDispPoint：发车点对象

 **def createVehicleType(self, _vt:Tessng._VehicleType) -> bool: ...**

创建车型，如果创建成功，会将新创建的车辆类型存放到全局数据里供使用

参数：

\[in\] vt：车辆类型数据

 **def removeVehicleComposition(self, vehiCompId:int) -> bool: ...**

移除车型组成

参数：

\[in\] vehiCompId：车型组成ID

 **def createDecisionPoint(self, pLink:Tessng.ILink, distance:float, name:str=...) -> Tessng.IDecisionPoint: ...**

创建决策点

参数：

\[in\] pLink：决策点所在的路段

\[in\] distance：决策点距离路段起点的距离，默认单位：像素

\[in\] name：决策点的名称

返回：决策点对象

举例：

```python
# 创建决策点
decisionPoint = netiface.createDecisionPoint(link3, m2p(30))

```

 

 **def createDeciRouting(self, pDeciPoint:Tessng.IDecisionPoint, lILink:typing.Sequence) -> Tessng.IRouting: ...**

创建决策路径

参数：

\[in\] pDeciPoint：决策点

\[in\] lILink：决策路径所包含的路段集合

举例：

```python
# 创建路径(左，直，右)
decisionRouting1 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link6])
decisionRouting2 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link8])
decisionRouting3 = tessngIFace().netInterface().createDeciRouting(decisionPoint, [link3, link10, link7])

```

 

 **def removeDeciRouting(self, pDeciPoint:Tessng.IDecisionPoint, pRouting:Tessng.IRouting) -> bool: ...**

删除决策路径

参数：

\[in\] pDeciPoint：决策点

\[in\] pRouting：将要删除的路径

举例：

```python
# 删除右转路径
if (netiface.removeDeciRouting(decisionPoint, decisionRouting3)):
    print("删除右转路径成功。")

```

 

 **def updateDecipointPoint(self, deciPoint:Tessng._DecisionPoint, lFlowRatio:typing.Sequence=...) -> Tessng.IDecisionPoint: ...**

更新决策点及其各路径不同时间段流量比

参数：

\[in\] deciPoint：决策点数据

\[in\] lFlowRatio：各路径按时间段流量比的数据集合

返回：更新后的决策点

举例：

```python
# 分配左、直、右流量比
flowRatio_left = _RoutingFLowRatio()
flowRatio_left.RoutingFLowRatioID = 1
flowRatio_left.routingID = decisionRouting1.id()
flowRatio_left.startDateTime = 0
flowRatio_left.endDateTime = 999999
flowRatio_left.ratio = 2.0
flowRatio_straight = _RoutingFLowRatio()
flowRatio_straight.RoutingFLowRatioID = 2
flowRatio_straight.routingID = decisionRouting2.id()
flowRatio_straight.startDateTime = 0
flowRatio_straight.endDateTime = 999999
flowRatio_straight.ratio = 3.0
flowRatio_right = _RoutingFLowRatio()
flowRatio_right.RoutingFLowRatioID = 3
flowRatio_right.routingID = decisionRouting3.id()
flowRatio_right.startDateTime = 0
flowRatio_right.endDateTime = 999999
flowRatio_right.ratio = 1.0

# 决策点数据
decisionPointData = _DecisionPoint()
decisionPointData.deciPointID = decisionPoint.id()
decisionPointData.deciPointName = decisionPoint.name()
decisionPointPos = QPointF()
if decisionPoint.link().getPointByDist(decisionPoint.distance(), decisionPointPos):
    decisionPointData.X = decisionPointPos.x()
    decisionPointData.Y = decisionPointPos.y()
    decisionPointData.Z = decisionPoint.link().z()
# 更新决策点及其各路径不同时间段流量比
updated_decision_point = netiface.updateDecipointPoint(
    decisionPointData, [flowRatio_left, flowRatio_straight, flowRatio_right]
)

```

 

 **def createVehiCollectorOnLink(self, pLane:Tessng.ILane, dist:float) -> Tessng.IVehicleDrivInfoCollector: ...**

在路段的车道上创建车辆采集器

参数：

\[in\] pLane：车道对象

\[in\] dist：路车道起点距离，默认单位：像素

返回：车辆采集器

举例：

```python
#在路段9最左侧车道100米处创建车辆采集器
link = tessngIFace().netInterface().findLink(9)
if link is not None:
    leftLane = link.lanes()[-1]
    dist = m2p(100)
    collector = tessngIFace().netInterface().createVehiCollectorOnLink(leftLane, dist)
    # 将采集器设置到距路段起点400米处
    if collector is not None:
        collector.setDistToStart(m2p(400))

```

 

 **def createVehiCollectorOnConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float) -> Tessng.IVehicleDrivInfoCollector: ...**

在连接段的“车道连接”上创建采集器

参数：

\[in\] pLaneConnector：“车道连接”对象

\[in\] dist：距“车道连接”起点距离，单位像素

 **def removeVehiCollector(self, pCollector:Tessng.IVehicleDrivInfoCollector) -> bool: ...**

移除车辆信息采集器

参数：

\[in\] pCollector：车辆信息采集器

 **def createVehiQueueCounterOnLink(self, pLane:Tessng.ILane, dist:float) -> Tessng.IVehicleQueueCounter: ...**

在路段的车道上创建车辆排队计数器

参数：

\[in\] pLane：车道对象

\[in\] dist：默认单位：像素

返回：排队计数器对象

举例：

```python
#在路段9最左侧车道100米处创建排队计数器
link = tessngIFace().netInterface().findLink(9)
if link is not None:
  leftLane = link.lanes()[-1]
  dist = m2p(100)
  counter = tessngIFace().netInterface().createVehiQueueCounterOnLink(leftLane, dist)
  if counter is not None:
     print(f"计数器所在点坐标为: ({counter.point().x()}, {counter.point().y()})")

```

 

 **def createVehiQueueCounterOnConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float) -> Tessng.IVehicleQueueCounter: ...**

在连接段的车道连接上创建车辆排队计数器

参数：

\[in\] pLaneConnector：“车道连接”对象

\[in\] dist：距“车道连接”起点距离，默认单位：像素

返回：排队计数器对象

 **def createVehicleTravelDetector_link2link(self, pStartLink:Tessng.ILink, pEndLink:Tessng.ILink, dist1:float, dist2:float) -> typing.List: ...**

创建行程时间检测器，起点和终点都在路段上

参数：

\[in\] dist1：检测器起点距所在路段起始点距离，默认单位：像素

\[in\] dist2：检测器终点距所在路段起始点距离，默认单位：像素

举例：

```python
#在路段9 50-550米处创建行程检测器
lVehicleTravelDetector = tessngIFace().netInterface().createVehicleTravelDetector_link2link(link, link, m2p(50), m2p(550))
if lVehicleTravelDetector is not None:
for detector in lVehicleTravelDetector:
detector.setFromTime(10)
detector.setToTime(60)

```

 

 **def createVehicleTravelDetector_link2conn(self, pStartLink:Tessng.ILink, pEndLaneConnector:Tessng.ILaneConnector, dist1:float, dist2:float) -> typing.List: ...**

创建行程时间检测器，起点在路段上，终点都在连接段的“车道连接”上

参数：

\[in\] pStartLink：检测器起点所在路段对象

\[in\] pEndLaneConnector：检测器终点所在“车道连接”对象

\[in\] dist1：检测器起点距所在路段起始点距离，默认单位：像素

\[in\] dist2：检测器终点距所在“车道连接”起始点距离，默认单位：像素

返回：行程时间检测器对象

 **def createVehicleTravelDetector_conn2link(self, pStartLaneConnector:Tessng.ILaneConnector, pEndLink:Tessng.ILink, dist1:float, dist2:float) -> typing.List: ...**

创建行程时间检测器，起点在连接段的“车道连接”上，终点在路段上

参数：

\[in\] pStartLaneConnector：检测器起点所在“车道连接”对象

\[in\] pEndLink：检测器终点所在路段对象

\[in\] dist1：检测器起点距所在"车道连接”起始点距离，默认单位：像素

\[in\] dist2：检测器终点距所在路段起始点距离，默认单位：像素

返回：行程时间检测器对象

 **def createVehicleTravelDetector_conn2conn(self, pStartLaneConnector:Tessng.ILaneConnector, pEndLaneConnector:Tessng.ILaneConnector, dist1:float, dist2:float) -> typing.List: ...**

创建行程时间检测器，起点和终点都在连接段的“车道连接”上

参数：

\[in\] pStartLaneConnector：检测器起点所在“车道连接”对象

\[in\] pEndLaneConnector：检测器终点所在“车道连接”对象

\[in\] dist1：检测器起点距所在"车道连接”起始点距离，默认单位：像素

\[in\] dist2：检测器终点距所在“车道连接”起始点距离，默认单位：像素

返回：行程时间检测器对象

 **def createSignalGroup(self, name:str, period:int, fromTime:int, toTime:int) -> Tessng.ISignalGroup: ...**

创建信号灯组（创建信控方案）

参数：

\[in\] name：灯组名称

\[in\] period：周期，默认单位：秒

\[in\] fromTime：起始时间，默认单位：秒

\[in\] toTime:结束时间,默认单位：秒

返回：信号灯组对象

举例：

```python
# 创建信号灯组
signalGroup = netiface.createSignalGroup("信号灯组1", 60, 1, 3600)
```

 

 **def createSignalPhase(self, pGroup:Tessng.ISignalGroup, name:str, lColor:typing.Sequence) -> Tessng.ISignalPhase: ...**

创建相位

参数：

\[in\] pGroup：信号灯组

\[in\] name：相位名称

\[in\] lColor：相位灯色序列，新建相位排在已有相位序列的最后

返回：信号相位对象

举例：

```python
# 创建相位,40秒绿灯，黄灯3秒，全红3秒
red = Online.ColorInterval("G", 40)
green = Online.ColorInterval("Y", 3)
yellow = Online.ColorInterval("R", 3)
signalPhase = netiface.createSignalPhase(signalGroup, "信号灯组1相位1",
                                         [green, yellow, red])

```

 

 **def removeSignalPhase(self, pGroup:Tessng.ISignalGroup, phaseId:int) -> None: ...**

移除已有相位，相位移除后，原相位序列自动重排,

参数：

\[in\] pGroup：信号灯组对象

\[in\] phaseId：将要移除的相位ID

 **def createSignalLamp(self, pPhase:Tessng.ISignalPhase, name:str, laneId:int, toLaneId:int, distance:float) -> Tessng.ISignalLamp: ...**

创建信号灯

参数：

\[in\] pPhase：相位对象

\[in\] name：信号灯名称

\[in\] laneId：信号灯所在车道ID，或所在“车道连接”上游车道ID

\[in\] toLaneId：信号灯所在“车道连接”下游车道ID

\[in\] distance：信号灯距车道或“车道连接”起点距离，默认单位：像素

返回：信号灯对象

举例：

```python
# 创建信号灯
for index, laneObj in enumerate(lLaneObjects):
    signalLamp = netiface.createSignalLamp(signalPhase, "信号灯{}".format(index + 1), laneObj.fromLane().id(),laneObj.toLane().id(), m2p(2.0))

```

 

 **def createBusLine(self, lLink:typing.Sequence) -> Tessng.IBusLine: ...**

创建公交线路，lLink列表中相邻两路段可以是路网上相邻两路段，也可以不相邻，如果不相邻，TESSNG会在它们之间创建一条最短路径。如果lLink列表中相邻路段在路网上不相邻并且二者之间不存在最短路径，则相邻的第二条路段及后续路段无效。

参数：

\[in\] lLink，公交线路经过的路段对象集

返回：公交线路对象

举例：

```python
#创建公交线路
busLine = tessngIFace().netInterface().createBusLine([link10, link11])
if busLine is not None:
   busLine.setDesirSpeed(m2p(60))

```

 

 **def removeBusLine(self, pBusLine:Tessng.IBusLine) -> bool: ...**

移除公交线路

参数：

\[in\] pBusLine：将要移除的公交线路对象

 **def createBusStation(self, pLane:Tessng.ILane, length:float, dist:float, name:str=...) -> Tessng.IBusStation: ...**

创建公交站点

参数：

\[in\] pLane：车道

\[in\] length:站点长度(单位像素)

\[in\] dist:站点起始点距车道起点距离(单位像素)

返回：公交站点对象

举例：

```python
# 创建公交线路
busLine = tessngIFace().netInterface().createBusLine([link10, link11])
if busLine is not None:
   busLine.setDesirSpeed(m2p(60))
   busStation1 = tessngIFace().netInterface().createBusStation(link10.lanes()[0], m2p(30), m2p(50),"公交站1")
   busStation2 = tessngIFace().netInterface().createBusStation(link11.lanes()[0], m2p(15), m2p(50),"公交站2")

```

 

 **def removeBusStation(self, pStation:Tessng.IBusStation) -> bool: ...**

移除公交站点

参数：

\[in\] pStation：公交站点对象

 **def addBusStationToLine(self, pBusLine:Tessng.IBusLine, pStation:Tessng.IBusStation) -> bool: ...**

将公交站点关联到公交线路上

参数：

\[in\] pBusLine：公交线路

\[in\] pStation：公交站点

举例：

```python
# 创建公交线路
if busStation1 and tessngIFace().netInterface().addBusStationToLine(busLine, busStation1):
  	busStation1.setType(2)
    print("公交站1已关联到公交线路")
if busStation2 and tessngIFace().netInterface().addBusStationToLine(busLine, busStation2):
    print("公交站2已关联到公交线路")

```

 

 **def removeBusStationFromLine(self, pBusLine:Tessng.IBusLine, pStation:Tessng.IBusStation) -> bool: ...**

将公交站点与公交线路的关联关系解除

参数：

\[in\] pBusLine：公交线路

\[in\] pStation：公交站点

 **def initSequence(self, schemaName:str=...) -> bool: ...**

初始化数据库序列，对保存路网的专业数据库序列进行初始化，目前支持PostgreSql

参数：

\[in\] schemaName：数据库的schema名称

 **def buildNetGrid(self, width:float=...) -> None: ...**

路网的网格化

参数：

\[in\] width：单元格宽度，默认单位：米

 **def findSectionOn1Cell(self, point:PySide2.QtCore.QPointF) -> typing.List: ...**

根据point查询所在单元格所有经过的ISection

参数：

\[in\] point：路网场景中的点

返回：ISection列表

 **def findSectionOn4Cell(self, point:PySide2.QtCore.QPointF) -> typing.List: ...**

根据point查询最近4个单元格所有经过的ISection

参数：

\[in\] point：路网场景中的一个点

返回：ISection列表

 **def findSectionOn9Cell(self, point:PySide2.QtCore.QPointF) -> typing.List: ...**

根据point查询最近9个单元格所有经过的ISection

参数：

\[in\] point：路网场景中的一个点

返回：ISection列表

 **def locateOnSections(self, point:PySide2.QtCore.QPointF, lSection:typing.Sequence, referDistance:float=...) -> typing.List: ...**

根据point对lSection列表中每一个Section所有LaneObject求最短距离，返回Location列表，列表按最短距离排序，从小到大

参数：

\[in\] point：路网场景中的一个点

\[in\] lSection：section列表

\[in\] referDistance：LaneObject上与point最近的点到LaneObject起点距离，默认单位：像素，是大约数，只为提高计算效率，默认值为0

返回：Online::Location列表

举例：

```python
# 根据point对lSection列表中每一个Section所有LaneObject求最短距离
sections = tessngIFace().netInterface().sections()
# 创建一个点对象
point = QPointF(0, 0)
# 在分段上定位点并获取位置信息
locations = tessngIFace().netInterface().locateOnSections(point, sections)
# 遍历位置信息列表
for location in locations:
    # 输出相关车道或车道连接的ID
    print("相关车道或车道连接为:", location.pLaneObject.id())
    # 输出最近点的坐标
    print("最近点坐标: ({}, {})".format(location.point.x(), location.point.y()))
    # 输出到最近点的最短距离
    print("到最近点的最短距离:", location.leastDist)
    # 输出最近点到起点的里程
    print("最近点到起点的里程:", location.distToStart)
    # 输出最近点所在分段序号
    print("最近点所在分段序号:", location.segmIndex)
    print()  # 换行以分隔不同的位置信息

```

 

 **def locateOnCrid(self, point:PySide2.QtCore.QPointF, cellCount:int=...) -> typing.List: ...**

point周围若干个单元格里查询LaneObject

参数：

\[in\] point：路网场景中的一个点

\[in\] cellCount：单元格数，小于1时默认为1，大于1小于4时默认为4，大于4时默认为9

返回：Online::Location列表

 **def boundingRect(self, pIVehicle:Tessng.IVehicle, outRect:PySide2.QtCore.QRectF) -> bool: ...**

路网外围Rect，用以获取路网边界

 **def createRoadWorkZone(self, param:Tessng.Online.DynaRoadWorkZoneParam) -> Tessng.IRoadWorkZone: ...**

创建施工区

参数：

\[in\] param：动态施工区信息，数据类型在文件 Plugin/_datastruct.h中定义

举例：

```python
# 创建施工区和删除施工区示例,施工区和事故区的删除有两种方式，duration结束后自动删除以及主动删除(removeRoadWorkZone)，此处初始化前者
def createworkZone(self):
    """ 创建施工区
    :param :
    :return:
    """
    # 创建施工区
    workZone = Online.DynaRoadWorkZoneParam()
    # 道路ID
    workZone.roadId = int(5)
    # 施工区名称
    workZone.name = "施工区，限速40,持续20秒"
    # 位置，距离路段或连接段起点距离，单位米
    workZone.location = 50
    # 施工区长度，单位米
    workZone.length = 50
    # 车辆经过施工区的最大车速，单位千米/小时
    workZone.limitSpeed = 40
    # 施工区施工时长，单位秒
    workZone.duration = 20
    # 施工区起始车道
    workZone.mlFromLaneNumber = [0]
    # 创建施工区
    zone = tessngIFace().netInterface().createRoadWorkZone(workZone)

```

 

 **def removeRoadWorkZone(self, pIRoadWorkZone:Tessng.IRoadWorkZone) -> None: ...**

移除施工区

参数：

\[in\] pIRoadWorkZone：将要移除的施工区对象

 **def roadWorkZones(self) -> typing.List: ...**

获取所有施工区

 **def findRoadWorkZone(self, roadWorkZoneId:int) -> Tessng.IRoadWorkZone: ...**

根据ID查询施工区

参数：

\[in\] roadWorkZoneId：施工区ID

返回：施工区对象

 **def createAccidentZone(self, param:Tessng.Online.DynaAccidentZoneParam) -> Tessng.IAccidentZone: ...**

创建事故区

参数：

\[in\] param：动态事故区信息，数据类型在文件 Plugin/_datastruct.h中定义

举例：

```python
# 创建事故区
accidentZone = Online.DynaAccidentZoneParam()
# 道路ID
accidentZone.roadId = 9
# 事故区名称
accidentZone.name = "最左侧车道发生事故"
# 位置，距离路段或连接段起点距离，单位米
accidentZone.location = m2p(200)
# 事故区长度，单位米
accidentZone.length = m2p(50)
# 事故区起始车道序号列表
accidentZone.mlFromLaneNumber=[2]
# 事故等级
accidentZone.level = 1
# 创建事故区
zone = tessngIFace().netInterface().createAccidentZone(accidentZone)

```

 

 **def removeAccidentZone(self, pIAccidentZone:Tessng.IAccidentZone) -> None: ...**

移除事故区

 **def accidentZones(self) -> typing.List: ...**

获取所有事故区

 **def findAccidentZone(self, accidentZoneId:int) -> Tessng.IAccidentZone: ...**

根据ID查询事故区

参数：

\[in\] accidentZoneId：事故区ID




 **def createLimitedZone(param: Online.DynaLimitedZoneParam) -> Tessng.ILimitedZone: ...**

创建限行区

参数：

\[in\] param：动态限行区信息，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数 Online.DynaLimitedZoneParam的案例如下： 改为python的？？？？



```c++

//例：限行区使用，距离、速度等单位为米制而非像素

Online::DynaLimitedZoneParam dynaLimitedZoneParam;

dynaLimitedZoneParam.name = "限行区测试"; //名称

dynaLimitedZoneParam.roadId = 1; //道路ID

dynaLimitedZoneParam.location = 50; // 限行区位置

dynaLimitedZoneParam.length = 100; // 限行区长度

dynaLimitedZoneParam.limitSpeed = 40; // 限行区限速，KM/H

dynaLimitedZoneParam.mlFromLaneNumber << 0 << 1; // 限行车道序号，本例限行右侧两车道

dynaLimitedZoneParam.duration = 3600; // 限行持续时间



gpTessInterface->netInterface()->createLimitedZone(dynaLimitedZoneParam);

```



 **def updateLimitedZone(param: Online.DynaLimitedZoneParam) -> boolen: ...**

更新限行区

参数：

\[in\] param：动态限行区信息，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数 Online.DynaLimitedZoneParam的案例见createLimitedZone



 **def removeLimitedZone(pILimitedZone: Tessng.ILimitedZone) -> boolen: ...**

移除限行区 Tessng.ILimitedZone 还是 Online.ILimitedZone

参数：

\[in\] pILimitedZone：将要移除的限行区对象，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数 Online.DynaLimitedZoneParam的案例见createLimitedZone


 **def limitedZones(self) -> Type.List<ILimitedZone>: ...**

获取所有限行区



 **def findLimitedZone(limitedZoneId:int) -> Tessng.ILimitedZone: ...**

根据ID获取指定的限行区

参数：
\[in\] limitedZoneId：限行区ID



 **def moveLinks(links:Type.List<Tessng.ILink>, offset:QPointF) -> None: ...**

移动路段及相关连接段

参数：
\[in\] lLink：要移动的路段列表
\[in\] offset：移动的偏移量, ？？？？ 这是指移动到指定点吗？？？




 **def createReconstruction(param: Online.DynaReconstructionParam) -> None: ...**

创建改扩建

参数：
\[in\] param：动态改扩建信息，数据类型在文件 Plugin/_datastruct.h中定义, python构造该数据类型的示例代码如下：

```python

//例：改扩建对象初始化案例


```

 **def updateReconStruction(param: Online.DynaReconstructionParam) -> None: ...**

更新改扩建

参数：
\[in\] param：动态改扩建信息，数据类型在文件 Plugin/_datastruct.h中定义, python构造该数据类型的示例代码见createReconstruction

 **def removeReconstruction(pIReconstruction: Online.DynaReconstructionParam*) -> None: ...**

移除改扩建，， python里这个是传引用对象？？ 为啥不加一个根据唯一ID删除的接口

参数：
\[in\] pIReconstruction：将要移除的改扩建对象引用, python构造该数据类型的示例代码见 createReconstruction



 **def reconstructions(self) -> Type.List<IReconstruction>: ...**

获取所有改扩建

 **def findReconstruction(reconstructionId:int) -> Tessng.IReconstruction: ...**

根据ID获取指定的改扩建对象

参数：
\[in\] reconstructionId：改扩建ID


 **def reCalcPassagewayLength(reconstruction:Online::DynaReconstructionParam) -> float: ...**


重新计算保通开口长度，单位：米; 这个改完后如果仿真要生效是不是还得更新改扩建对象（调用updateReconstruction）？？？

参数：
\[in\] reconstruction：改扩建对象，数据类型在文件 Plugin/_datastruct.h中定义，具体案例参见createReconstruction



 **def trafficLightCount(self) -> int: ...**

获取路网中信号机的总数

 **def trafficLightIds(self) -> Tpye.List<int>: ...**

信号机ID集合


 **def trafficLights(self) -> Tpye.List<Tessng.ITrafficLight>: ...**

信号机集合


 **def findTrafficLight(id:int) -> Tessng.ITrafficLight: ...**

根据ID查询信号机
参数：
\[in\] id：信号机ID


 **def findTrafficLightName(name: str) -> Tessng.ITrafficLight: ...**

根据名称查询信号机(如果同名返回第一个)
参数：
\[in\] name：信号机名称


 **def signalPlanCount(self) ->int: ...**
信控方案总数 （路网中所有的信控方案）



 **def signalPlanIds(self) ->int: ...**
信控方案ID集， 没有根据信号机ID查找信控方案的吗？？？？



 **def signalPlans(self) ->Type.List<Tessng.ISignalPlan>: ...**
信控方案ID集， 没有根据信号机ID查找信控方案的吗？？？？

 **def findSignalPlan(id:int) -> Tessng.ISignalPlan: ...**

根据信控方案id，获取信控方案
参数：
\[in\] id：信控方案ID


 **def findSignalPlan(name:str) -> Tessng.ISignalPlan: ...**

根据信控方案名称查找信控方案
参数：
\[in\] name：信控方案名称


 **def creatTrafficLight(name:str) -> Tessng.ITrafficLight: ...**

创建信号机
参数：
\[in\] name：信控机名称


 **def creatSignalPlan(pITrafficLight:Tessng.ITrafficLight, name:str, cycle:int, phasedifference:int, startTime:int, endTime:int) -> Tessng.ITrafficLight: ...**

创建信控方案， phasedifference 改为offset ！！！！ 
参数：
\[in\]  pITrafficLight：信号机对象
\[in\] name：方案名称
\[in\] cycle：周期时长， 单位：秒
\[in\] offset：相位差，单位：秒，指相对仿真起始时间的当前方案首相位的启亮延后时间
\[in\] startTime：起始时间
\[in\] endTime：结束时间

 **def removeSignalPhaseFromLamp(signalPhaseId:int, signalLamp:Tessng.ISignalLamp) ->None: ...**

信号灯移除某个绑定的相位(如果相位列表只存在一个相位则将关联的相位设置为null) 
参数：
\[in\]  SignalPhaseId：信号相位ID
\[in\] signalLamp：信号灯对象


 **def addSignalPhaseToLamp(signalPhaseId:int, signalLamp:Tessng.ISignalLamp) ->None: ...**

信号灯添加绑定的相位; 绑定行人信号灯ICrosswalkSignalLamp也是此函数，ICrosswalkSignalLamp 继承于 signalLamp
参数：
\[in\]  SignalPhaseId：信号相位ID
\[in\] signalLamp：信号灯对象


 **def transferSignalPhase(pFromISignalPhase:Tessng.ISignalPhase, pToISignalPhase:Tessng.ISignalPhase, signalLamp:Tessng.ISignalLamp) ->None: ...**


信号灯更换绑定的相位(不允许跨越信号机)
参数：
\[in\]  pFromISignalPhase: 原相位
\[in\]  pToISignalPhase: 原相位
\[in\]  signalLamp：信号灯对象
 


 **def createReduceSpeedArea(name:str, location:float,areaLength:float,roadId:int, laneNumber:int, toLaneNumber:int, fromTime:int, toTime:int, lSpeedVehiType:Type.List<Online.ReduceSpeedVehicleType>) -> Tessng.IReduceSpeedArea: ...**

创建限速区
参数：
\[in\] name：限速区名称
\[in\] location：距起点距离,单位像素
\[in\] areaLength：限速区长度,单位像素
\[in\] roadId：路段或连接段ID
\[in\] laneNumber：车道序号,从0开始
\[in\] toLaneNumber：目标车道序号,如果大于等于0,roadID是连接段ID,否则是路段ID
\[in\] fromTime：起始时间
\[in\] toTime：结束时间
\[in\] lSpeedVehiType：限速车型列表


 **def removeReduceSpeedArea(pIReduceSpeedArea:Tessng.IReduceSpeedArea) ->None: ...**
移除限速区
参数：
\[in\] pIReduceSpeedArea：限速区对象


 **def reduceSpeedAreas(self) ->Type.List<Tessng.IReduceSpeedArea>: ...**
获取所有限速区

 **def findReduceSpeedArea(id:int) ->Type.List<Tessng.IReduceSpeedArea>: ...**
查询指定ID的限速区
参数：
\[in\] id：限速区ID


 **def tollLanes(self) ->Type.List<Tessng.ITollLane>: ...**
获取所有收费车道列表



 **def tollDecisionPoints(self) ->Type.List<Tessng.ITollDecisionPoint>: ...**
获取所有收费决策点列表


 **def parkingRegions(self) ->Type.List<Tessng.IParkingRegion>: ...**
获取所有停车区列表

 **def parkingDecisionPoints(self) ->Type.List<Tessng.IParkingDecisionPoint>: ...**

获取所有停车决策点列表

 **def findTollLane(self) ->Type.List<Tessng.ITollLane>: ...**
通过id查询收费车道

 **def findTollDecisionPoint(self) ->Type.List<Tessng.ITollDecisionPoint>: ...**
通过id查询收费决策点

 **def findParkingRegion(self) ->Type.List<Tessng.IParkingRegion>: ...**
通过id查询停车区域

 **def findParkingDecisionPoint(self) ->Type.List<Tessng.IParkingDecisionPoint>: ...**
通过id查询停车决策点

 **def removeTollLane(pITollLane:Tessng.ITollLane) ->None: ...**
移除收费车道
新增一个根据ID移除的接口？？？

 **def removeTollDecisionPoint(pITollDecisionPoint:Tessng.ITollDecisionPoint) ->None: ...**
移除收费决策点
新增一个根据ID移除的接口？？？


 **def removeParkingRegion(pIParkingRegion:Tessng.IParkingRegion) ->None: ...**
移除停车区
新增一个根据ID移除的接口？？？

 **def removeParkingDecisionPoint(pIParkingDecisionPoint:Tessng.IParkingDecisionPoint) ->None: ...**
移除收费决策点
新增一个根据ID移除的接口？？？

 **def removeTollRouting(pITollRouting:Tessng.ITollRouting) ->None: ...**
移除收费路径
新增一个根据ID移除的接口？？？

 **def removeParkingRouting(pIParkingRouting:Tessng.IParkingRouting) ->None: ...**
移除停车路径
新增一个根据ID移除的接口？？？


 **def createTollLane(param:Online.TollStation.DynaTollLaneg) ->Tessng.ITollLane: ...**
创建收费车道
\[in\]  param：动态收费车道信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化Online.TollStation.DynaTollLane的示例代码如下：
```python


```



 **def createParkingRegion(param:Online.ParkingLot.DynaParkingRegion) ->Tessng.IParkingRegion: ...**
创建停车区
\[in\]  param：动态停车区信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化Online.ParkingLot.DynaParkingRegion的示例代码如下：
```python


```




 **def updateTollLane(param: Online.TollStation.DynaTollLane) ->Tessng.ITollLane: ...**
更新收费车道
\[in\]  param：：动态收费车道信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化Online.ParkingLot.DynaParkingRegion的示例见createTollLane：




 **def updateParkingRegion(param: Online.ParkingLot.DynaParkingRegion) ->Tessng.IParkingRegion: ...**
更新停车区
\[in\]  param：动态停车区信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化Online.ParkingLot.DynaParkingRegion的示例见createTollLane：





 **def createTollDecisionPoint(pLink:Tessng.ILink, distance:float, name:str(optional)) ->Tessng.ITollDecisionPoint: ...**
创建收费决策点
\[in\]  pLink：收费决策点所在的路段
\[in\]  distance：收费决策点距离路段起点的距离，默认单位：像素？？？ 改为长度
\[in\]  pLink：收费决策点的名称， 可选参数



 **def createTollRouting(pDeciPoint:Tessng.ITollDecisionPoint, pITollLane:Tessng.ITollLane) ->Tessng.ITollRouting: ...**
创建收费路径
\[in\] pDeciPoint：收费决策点
\[in\] pITollLane：收费车道



 **def createParkingDecisionPoint(pLink:Tessng.ILink, distance:float, name:str(optional)) ->Tessng.IParkingDecisionPoint: ...**
创建停车决策点
\[in\] pLink：停车决策点所在的路tollDisInfoList段
\[in\] distance：停车决策点距离路段起点的距离，默认单位：米
\[in\]  pLink：停车决策点的名称， 可选参数




 **def createParkingRouting(pDeciPoint:Tessng.IParkingDecisionPoint, pIParkingRegion:Tessng.IParkingRegion) ->Tessng.IParkingRouting: ...**
创建停车路径
\[in\] pDeciPoint:停车决策点
\[in\]  pIParkingRegion：停车区



 **def tollParkingTimeDis(self) ->Type.List<Online.TollStation.DynaTollParkingTimeDis>: ...**
获取收费站停车时距分布列表


 **def createTollParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**
创建收费站停车时距分布
\[in\]  param：停车时距分布参数


 **def removeTollParkingTimeDis(id:int) ->None: ...**
移除收费站停车时距分布
\[in\]  id ：停车时距分布参数的Id

 **def updateTollParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**
更新收费站停车时距分布
\[in\]  param：停车时距分布参数


 **def parkingTimeDis(self) ->Online.ParkingLot.DynaParkingTimeDis : ...**
获取停车场停车时距分布列表


 **def createParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**
更新收费站停车时距分布
\[in\]  param：停车时距分布参数

 **def removeParkingTimeDis(id:int) ->None: ...**
移除停车场停车时距分布
\[in\]  param：停车时距分布ID

 **def DynaParkingTimeDis (param:Online.ParkingLot.DynaParkingTimeDis) ->Online.ParkingLot.DynaParkingTimeDis: ...**
更新停车场停车时距分布
\[in\]  param：停车时距分布参数


 **def createGJunction (startPoint:QPointF, endPoint:QPointF, name:str) ->Tessng.IJunction: ...**
创建节点
\[in\] startPoint：左上角起始点坐标
\[in\] endPoint：右下角起始点坐标
\[in\] name：节点名字

 **def findGJunction (id:int) ->Tessng.IJunction: ...**
根据路径ID查找节点
\[in\] id：节点ID


 **def getAllGJunction () ->Type.Dict<int,Tessng.IJunction>: ...**
获得所有节点, 返回类型为字典

 **def removeGJunction(id:int) ->None: ...**
删除节点
\[in\] id：节点ID


 **def removeGJunction(id:int) ->None: ...**
删除节点
\[in\] id：节点ID

 **def updateGJunctionName(id:int, name:str) ->None: ...**
更新节点名字
\[in\] id：节点ID
\[in\] name：节点名字


 **def updateGJunctionBuildPathParam(bDeciPointPosFlag:bool, bLaneConnectorFlag:bool，InputLineMinPathNum：long(defulat=3)) ->None: ...**
更新静态路径构建参数
\[in\]  bDeciPointPosFlag：决策点位置是否需要优化
\[in\]  bLaneConnectorFlag：连接段是否需要优化
\[in\]  InputLineMinPathNum：两点间最短路径数(默认为3)


 **def buildPathAndApply(self) ->Type.Tuple(Type.List<Tuple(int,int)>, Type.List<Type.List<Tessng.ILink>>): ...**
计算并应用并返回路网决策路径 ????? 这个函数签名有点奇怪


 **def getJunctionTurnningInfoByID(id:int) ->Type.Dict(int, Type.Dict(int, Online.Junction.FlowTurning)): ...**
获得节点流向信息
参数
\[in\]  id: 节点ID
\[out\]  返回节点流向信息， 外层字典key为，value为  ； 内层字典key为， value为



 **def getJunctionFlowTimeInfo() ->Type.List<Online.Junction.FlowTimeInterval > : ...**
获得节点流向时间段信息（所有节点共用一套流向时间段信息）
参数
\[out\]  节点流向时间信息


 **def addFlowTimeInterval() ->int : ...**
添加流量时间段 

 **def deleteFlowTimeInterval(timeId:int) ->bool : ...**
删除流量时间段
参数
\[in\] timeId：时间段ID
\[out\] 是否删除成功



 **def updateFlowTimeInterval(interval:Online.Junction.FlowTimeInterval) ->bool : ...**
更新流量时间段信息
参数
\[in\]  interval：时间段信息
\[out\] 是否成功


 **def updateJunctionFlowInfo(timeId:long, junctionId:int, turningId:int, inputFlowValue:int) ->bool : ...**
更新节点流向信息
参数
\[in\]  timeId：时间段ID
\[in\]  junctionId：节点ID
\[in\]  turningId：转向ID
\[in\]  inputFlowValue：输入流量值
\[out\] 是否成功



 **def updateJunctionFlowPFEIteraParam(theta:double, bpra:double, bprb:double, maxIterateNum:int, bUseNewPath:bool) ->None : ...**
更新节点流量算法参数
参数
\[in\] theta：参数θ
\[in\] bpra：BPR路阻参数A
\[in\] bprb：BPR路阻参数B
\[in\] maxIterateNum：迭代参数，最大迭代次数
\[out\] bUseNewPath：是否重新构建静态路径


 **def applyJunctionFlowResult() ->Type.Dict(int, Type.List<Online.Junction.FlowTurning>): ...**
计算并应用流量算法结果
参数
\[out\] 节点流向结果映射表


 **def pedestrianTypes() ->Type.List<Tessng.IPedestrianType>: ...**
获取所有行人类型


 **def pedestrianCompositions() ->Type.List<Tessng.IPedestrianComposition >: ...**
获取所有行人组成


 **def layerInfos() ->Type.List<OnLine.Pedestrian.LayerInfo>: ...**
获取所有层级信息


 **def pedestrianRegions() ->Type.List<Tessng.IPedestrianRegion>: ...**
获取所有行人面域

 **def pedestrianRectRegions() ->Type.List<Tessng.IPedestrianRectRegion>: ...**
获取所有矩形面域


 **def pedestrianEllipseRegions() ->Type.List<Tessng.IPedestrianEllipseRegion>: ...**
获取所有椭圆形面域


 **def pedestrianTriangleRegions() ->Type.List<Tessng.IPedestrianTriangleRegion>: ...**
获取所有椭圆形面域


 **def pedestrianFanShapeRegions() ->Type.List<Tessng.IPedestrianFanShapeRegion>: ...**
获取所有扇形面域

 **def pedestrianPolygonRegions() ->Type.List<Tessng.IPedestrianPolygonRegion>: ...**
获取所有多边形面域


 **def pedestrianSideWalkRegions() ->Type.List<Tessng.IPedestrianSideWalkRegion>: ...**
获取所有人行道


 **def pedestrianCrossWalkRegions() ->Type.List<Tessng.IPedestrianCrossWalkRegion>: ...**
获取所有人行横道


 **def pedestrianPathStartPoints() ->Type.List<Tessng.IPedestrianPathPoint>: ...**
获取所有行人发生点


 **def pedestrianPathEndPoints() ->Type.List<Tessng.IPedestrianPathPoint>: ...**
获取所有行人结束点


 **def pedestrianPathDecisionPoints() ->Type.List<Tessng.IPedestrianPathPoint>: ...**
获取所有行人决策点


 **def pedestrianPaths() ->Type.List<Tessng.IPedestrianPath>: ...**
获取所有行人路径，包括局部路径

 **def crosswalkSignalLamps() ->Type.List<Tessng.ICrosswalkSignalLamp>: ...**
获取所有人行横道红绿灯

 **def findPedestrianRegion() ->Tessng.IPedestrianRegion: ...**
根据id获取行人面域

 **def findPedestrianRectRegion() ->Tessng.IPedestrianRectRegion: ...**
根据id获取矩形面域


 **def findPedestrianEllipseRegion() ->Tessng.IPedestrianEllipseRegion: ...**
根据id获取椭圆形面域


 **def findPedestrianTriangleRegion() ->Tessng.IPedestrianTriangleRegion: ...**
根据id获取三角形面域



 **def findPedestrianFanShapeRegion() ->Tessng.IPedestrianFanShapeRegion: ...**
根据id获取扇形面域


 **def findPedestrianPolygonRegion() ->Tessng.IPedestrianPolygonRegion: ...**
根据id获取多边形面域


 **def findPedestrianSideWalkRegion() ->Tessng.IPedestrianSideWalkRegion: ...**
根据id获取人行道


 **def findPedestrianCrossWalkRegion() ->Tessng.IPedestrianCrossWalkRegion: ...**
根据id获取人行横道


 **def findPedestrianPathStartPoint() ->Tessng.IPedestrianPathPoint: ...**
根据id获取行人发生点


 **def findPedestrianPathEndPoint() ->Tessng.IPedestrianPathPoint: ...**
根据id获取行人结束点

 **def findPedestrianDecisionPoint() ->Tessng.IPedestrianPathPoint: ...**
根据id获取行人决策点

 **def findPedestrianPath() ->Tessng.IPedestrianPath: ...**
根据id获取行人路径，包括局部路径


 **def findCrosswalkSignalLamp() ->Tessng.ICrosswalkSignalLamp: ...**
根据id获取人行横道红绿灯


 **def findPedestrianStartPointConfigInfo() ->Tessng.PedestrianPathStartPointConfigInfo : ...**
根据id获取行人发生点配置信息，id为行人发生点ID


 **def findPedestrianDecisionPointConfigInfo() ->Tessng.PedestrianDecisionPointConfigInfo  : ...**
根据id获取行人决策点配置信息，id为行人决策点ID


 **def createPedestrianComposition(name:str, lCompositionDetail:Type.List<Tessng.PedestrianCompositionDetail >) -> int : ...**
创建行人组成
参数
\[in\] name：组成名称
\[in\] lCompositionDetail：组成明细
\[out\] 返回：组成ID，如果创建失败返回-1


 **def updatePedestrianComposition(compositionId:int, lCompositionDetail:Type.List<Tessng.PedestrianCompositionDetail >) -> bool : ...**
创建行人组成
参数
\[in\] compositionId：组成Id
\[in\] lCompositionDetail：组成明细
\[out\] 返回：True表示更新成功，False表示更新失败


 **def removePedestrianComposition(compositionId:int) -> bool : ...**
移除行人组成
参数
\[in\] compositionId：组成Id
\[out\] 返回：True表示成功，False表示失败


 **def addLayerInfo(name:str, height:float, visible:bool,locked:bool) -> Online.Pedestrian.LayerInfo : ...**
新增层级，返回新增的层级信息
参数
\[in\] name：层级名称
\[in\] height：层级高度
\[in\] visible：是否可见
\[in\] locked：是否锁定，锁定后面域不可以修改
\[out\] 返回：图层对象

 **def removeLayerInfo(layerId:int) -> None : ...**
删除某个层级，会删除层级当中的所有元素
参数
\[in\] layerId：层级Id


 **def updateLayerInfo(layerId:int, name:str, height:float, visible:bool,locked:bool) -> bool: ...**
更新层级信息
参数
\[in\] id：层级ID
\[in\] name：层级名称
\[in\] height：层级高度
\[in\] visible：是否可见
\[in\] locked：是否锁定，锁定后面域不可以修改
\[out\] 返回：是否更新成功

 **def updatePedestrianStartPointConfigInfo(info:Online.Pedestrian.PedestrianPathStartPointConfigInfo) -> bool : ...**
更新行人发生点配置信息
参数
\[in\] info：行人发生点配置信息
\[out\] 返回：是否更新成功


 **def createPedestrianRectRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianRectRegion : ...**
创建矩形行人面域
参数
\[in\] startPoint：左上角
\[in\] endPoint：右下角
\[out\] 矩形行人面域对象

 **def removePedestrianRectRegion(pIPedestrianRectRegion:Tessng.IPedestrianRectRegion) -> None : ...**
删除矩形行人面域
参数
\[in\] pIPedestrianRectRegion：矩形行人面域对象



 **def createPedestrianEllipseRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianEllipseRegion : ...**
创建椭圆行人面域
参数
\[in\] startPoint：左上角
\[in\] endPoint：右下角
\[out\] 椭圆行人面域对象
 **def removePedestrianEllipseRegion(pIPedestrianEllipseRegion:Tessng.IPedestrianEllipseRegion) -> None : ...**
删除椭圆行人面域
参数
\[in\] pIPedestrianEllipseRegion：椭圆行人面域对象

 **def createPedestrianTriangleRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianTriangleRegion : ...**
创建三角形行人面域
参数
\[in\] startPoint：左上角
\[in\] endPoint：右下角
\[out\] 三角形行人面域对象
 **def removePedestrianTriangleRegion(pIPedestrianTriangleRegion:Tessng.IPedestrianTriangleRegion) -> None : ...**
删除三角形行人面域
参数
\[in\] pIPedestrianEllipseRegion：三角形行人面域对象


 **def createPedestrianFanShapeRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianFanShapeRegion : ...**
创建扇形行人面域
参数
\[in\] startPoint：左上角
\[in\] endPoint：右下角
\[out\] 扇形行人面域对象
 **def removePedestrianFanShapeRegion(pIPedestrianTriangleRegion:Tessng.IPedestrianTriangleRegion) -> None : ...**
删除扇形行人面域
参数
\[in\] IPedestrianTriangleRegion：扇形行人面域对象


 **def createPedestrianPolygonRegion(polygon:QPolygonF) -> Tessng.IPedestrianPolygonRegion : ...**
创建多边形行人面域
参数
\[in\] polygon：多边形顶点
\[out\]多边形行人面域对象
 **def removePedestrianPolygonRegion(pIPedestrianPolygonRegion:Tessng.IPedestrianPolygonRegion) -> None : ...**
删除多边形行人面域
参数
\[in\] pIPedestrianPolygonRegion：多边形行人面域对象



 **def createPedestrianSideWalkRegion(vertexs:Type.List<QPointF>) -> Tessng.IPedestrianSideWalkRegion : ...**
创建人行道
参数
\[in\] vertexs：顶点列表
\[out\] 人行道对象
 **def removePedestrianSideWalkRegion(pIPedestrianSideWalkRegion:Tessng.IPedestrianSideWalkRegion) -> None : ...**
删除人行道
参数
\[in\]  pIPedestrianSideWalkRegion：人行道对象



 **def createPedestrianCrossWalkRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianCrossWalkRegion: ...**
创建人行横道
参数
\[in\] startPoint：左上角
\[in\] endPoint：右下角
\[out\] 人行横道对象
 **def removePedestrianCrossWalkRegion(pIPedestrianCrossWalkRegion:Tessng.IPedestrianCrossWalkRegion) -> None : ...**
删除人行横道
参数
\[in\] pIPedestrianCrossWalkRegion：人行横道对象


 **def createPedestrianStairRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianStairRegion: ...**
创建人行横道
参数
\[in\] startPoint：起点
\[in\] endPoint：终点
\[out\] 楼梯对象
 **def removePedestrianStairRegion(pIPedestrianStairRegion:Tessng.IPedestrianStairRegion) -> None : ...**
删除楼梯
参数
\[in\] pIPedestrianStairRegion：楼梯对象



 **def createPedestrianPathStartPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**
创建行人发生点
参数
\[in\] scenePos：场景坐标, 场景坐标是啥？ 怎么搞？？？
\[out\] 行人发生点对象
 **def removePedestrianPathStartPoint(pIPedestrianPathStartPoint:Tessng.IPedestrianPathPoint) -> None : ...**
删除行人发生点
参数
\[in\] pIPedestrianPathStartPoint：行人发生点对象


 **def createPedestrianPathEndPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**
创建行人结束点
参数
\[in\] scenePos：场景坐标
\[out\] 行人结束点对象
 **def removePedestrianPathEndPoint(pIPedestrianPathEndPoint:Tessng.IPedestrianPathPoint) -> None : ...**
删除行人结束点
参数
\[in\] pIPedestrianPathStartPoint：删除行人结束点


 **def createPedestrianDecisionPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**
创建行人决策点
参数
\[in\] scenePos：场景坐标
\[out\] 创建行人决策点
 **def removePedestrianDecisionPoint(pIPedestrianDecisionPoint:Tessng.IPedestrianPathPoint) -> None : ...**
删除行人决策点
参数
\[in\] pIPedestrianPathStartPoint：行人决策点对象


 **def createPedestrianPath(pStartPoint:Tessng.IPedestrianPathPoint,pEndPoint:Tessng.IPedestrianPathPoint，middlePoints：Type.List<QPointF>) -> Tessng.IPedestrianPath: ...**
创建行人路径（或行人局部路径）
参数
\[in\] pStartPoint：行人发生点（或行人决策点）
\[in\] pEndPoint：行人结束点
\[in\] middlePoints：一组中间必经点
\[out\] 行人路径对象
 **def removePedestrianPath(pIPedestrianPath:Tessng.IPedestrianPath) -> None : ...**
删除行人路径
参数
\[in\] pIPedestrianPath：行人路径对象



 **def createCrossWalkSignalLamp(pTrafficLight:Tessng.ITrafficLight,name:str，crosswalkid：str, scenePos:QPointF, isPositive:bool) -> Tessng.ICrosswalkSignalLamp: ...**
创建人行横道信号灯
参数
\[in\]  pTrafficLight：信号机
\[in\] name：名称
\[in\] crosswalkId：人行横道ID
\[in\] scenePos：位于人行横道内的场景坐标
\[in\] isPositive：信号灯管控方向是否为正向
\[out\]人行横道信号灯对象
 **def removeCrossWalkSignalLamp(pICrosswalkSignalLamp:Tessng.ICrosswalkSignalLamp) -> None : ...**
删除人行横道信号灯
参数
\[in\]  pICrosswalkSignalLamp：人行横道信号灯对象







------



### 4.2. SimuInterface

SimuInterface是TessInterface的子接口， 通过此接口可以启动、暂停、停止仿真，可以设置仿真精度，获取仿真过程车辆对象、车辆状态（包括位置信息），获取几种检测器检测的样本数据和集计数据，等等。

下面对SimuInterface接口方法作详细解释。

 **def byCpuTime(self) -> bool: ...**

仿真时间是否由现实时间确定。

一个计算周期存在两种时间，一种是现实经历的时间，另一种是由仿真精度决定的仿真时间，如果仿真精度为每秒20次，仿真一次相当于仿真了50毫秒。默认情况下，一个计算周期的仿真时间是由仿真精度决定的。在线仿真时如果算力不够，按仿真精度确定的仿真时间会与现实时间存在时差。

 **def setByCpuTime(self, bByCpuTime:bool) -> bool: ...**

设置是否由现实时间确定仿真时间，如果设为True，每个仿真周期现实经历的时间作为仿真时间，这样仿真时间与现实时间相吻合。

参数：

\[in\] bByCpuTime：是否由现实时间确定仿真时间

 **def startSimu(self) -> bool: ...**

启动仿真

举例：

```python
# TESSNG 顶层接口
iface = tessngIFace()
# TESSNG 仿真子接口
simuiface = iface.simuInterface()
simuiface.startSimu()

```

 **def pauseSimu(self) -> bool: ...**

暂停仿真

举例：

```python
simuiface.pauseSimu()
```

 

 **def stopSimu(self) -> bool: ...**

停止仿真运行 

举例：

```python
 simuiface.stopSimu()
```

 

 **def pauseSimuOrNot(self) -> None: ...**

暂停或恢复仿真。如果当前处于仿真运行状态，此方法暂停仿真，如果当前处于暂停状态，此方法继续仿真

举例：

```python
 simuiface.pauseSimuOrNot()
```

 

 **def isRunning(self) -> bool: ...**

仿真是否在进行

 **def isPausing(self) -> bool: ...**

仿真是否处于暂停状态

 **def isRecordTrace(self) -> bool: ...**

仿真是否记录车辆轨迹

 **def setIsRecordTrace(self, bRecord:bool) -> None: ...**

设置是否记录车辆轨迹

参数：

\[in\] bRecord：是否记录车辆轨迹

 **def simuIntervalScheming(self) -> int: ...**

预期仿真时长，即仿真设置窗口设置的仿真时间

![仿真参数配置](p2.png)

 **def setSimuIntervalScheming(self, interval:int) -> None: ...**

设置预期仿真时长

参数：

\[in\] interval：预期仿真时长，默认单位：秒

 **def simuAccuracy(self) -> int: ...**

获取仿真精度

 **def setSimuAccuracy(self, accuracy:int) -> None: ...**

设置仿真精度，即每秒计算次数

参数：

\[in\] accuracy：每秒计算次数

 **def acceMultiples(self) -> int: ...**

获取加速倍数

 **def setAcceMultiples(self, multiples:int) -> None: ...**

设置加速倍数

参数：

\[in\] multiples 加速倍数

 **def setThreadCount(self, count:int) -> None: ...**

设置工作线程数

 **def batchNumber(self) -> int: ...**

当前仿真批次

 **def batchIntervalReally(self) -> float: ...**

当前批次实际时间

 **def batchNumber(self) -> int: ...**

当前批次

 **def startMSecsSinceEpoch(self) -> int: ...**

获取仿真开始的现实时间

 **def stopMSecsSinceEpoch(self) -> int: ...**

仿真结束的现实时间

 **def simuTimeIntervalWithAcceMutiples(self) -> int: ...**

获取当前已仿真时间

 **def delayTimeOnBatchNumber(self, batchNumber:int) -> int: ...**

仿真到指定批次时总延误，单位：毫秒；

在算力不足的情况下，存在仿真计算每一个周期所需时间大于设置周期时间的情况，造成延误。

参数：

\[in\] batchNumber：仿真批次

返回值：仿真到batchNumber批次时的总延误

 **def vehiCountTotal(self) -> int: ...**

车辆总数，包括已创建尚未进入路网的车辆、正在运行的车辆、已驶出路网的车辆

 **def vehiCountRunning(self) -> int: ...**

正在运行车辆数

 **def getVehicle(self, vehiId:int) -> Tessng.IVehicle: ...**

根据车辆ID获取车辆对象

参数：

\[in\] vehiId：车辆ID

 **def allVehiStarted(self) -> typing.List: ...**

所有正在运行车辆

 **def allVehicle(self) -> typing.List: ...**

所有车辆，包括已创建尚未进入路网的车辆、正在运行的车辆、已驶出路网的车辆

 **def getVehisStatus(self) -> typing.List: ...**

获取所有正在运行的车辆状态，包括轨迹

返回：车辆状态（包括轨迹）Online.VehicleStatus列表

举例：

```python
# TESSNG 顶层接口
iface = tessngIFace()
# TESSNG 仿真子接口
simuiface = iface.simuInterface()
# 当前正在运行车辆列表
vehis = simuiface.allVehiStarted()

```

 

 

 **def getVehiTrace(self, vehiId:int) -> typing.List: ...**

获取指定车辆运行轨迹

参数：

\[in\] vehiId：车辆ID

返回：车辆运行轨迹，即Online.VehiclePosition列表

 **def getSignalPhasesColor(self) -> typing.List: ...**

获取当前所有信号灯组相位颜色？？？？ 闪退

返回:当前相位颜色Online.SignalPhaseColo列表，包括各相位当前颜色设置的时间和已持续时间。 

 **def getVehisInfoCollected(self) -> typing.List: ...**

获取当前完成穿越车辆数据采集器的所有车辆信息

返回：采集的车辆信息列表。数据结构Online::VehiInfoCollected在文件Plugin/_datastruct.h中定义。

举例：

```python
# TESSNG 顶层接口
iface = tessngIFace()
# TESSNG 仿真子接口
simuiface = iface.simuInterface()
# 获取当前仿真时间完成穿越采集器的所有车辆信息
lVehiInfo = simuiface.getVehisInfoCollected()

```

 

 

 **def getVehisInfoAggregated(self) -> typing.List: ...**

获取最近集计时间段内采集器采集的所有车辆集计信息

返回：采集器集计数据Online.VehiInfoAggregated列表

 **def getVehisQueueCounted(self) -> typing.List: ...**

获取当前排队计数器计数的车辆排队信息

返回：车辆排队信息Online.VehiQueueCounted列表

 **def getVehisQueueAggregated(self) -> typing.List: ...**

获取最近集计时间段内排队计数器集计数据

返回：排队计数器集计数据Online.VehiQueueAggregated列表

 **def getVehisTravelDetected(self) -> typing.List: ...**

​    获取当前行程时间检测器完成的行程时间检测信息

返回：行程时间检测器数据Online.VehiTravelDetected列表

 **def getVehisTravelAggregated(self) -> typing.List: ...**

获取最近集计时间段内行程时间检测器集计数据

返回：行程时间集计数据Online.VehiTravelAggregated列表

 **def createGVehicle(self, dynaVehi:Tessng.Online.DynaVehiParam) -> Tessng.IVehicle: ...**

动态创建车辆

参数:

\[in\]: dynaVehi：动态车辆信息

举例：

```python
# 在指定车道和位置动态加载车辆(示例：在0,1,2车道不同位置动态加载车辆)
dvp_lane0 = Online.DynaVehiParam()
dvp_lane1 = Online.DynaVehiParam()
dvp_lane2 = Online.DynaVehiParam()
dvp_lane0.vehiTypeCode = 1
dvp_lane1.vehiTypeCode = 2
dvp_lane2.vehiTypeCode = 3
dvp_lane0.roadId = link3.id()
dvp_lane1.roadId = link3.id()
dvp_lane2.roadId = link4.id()
dvp_lane0.laneNumber = 0
dvp_lane1.laneNumber = 1
dvp_lane2.laneNumber = 2
dvp_lane0.dist = m2p(50)
dvp_lane1.dist = m2p(100)
dvp_lane2.dist = m2p(50)
dvp_lane0.speed = 20
dvp_lane0.speed = 30
dvp_lane0.speed = 40
dvp_lane0.color = "#FF0000"
dvp_lane1.color = "#008000"
dvp_lane2.color = "#0000FF"
vehi_lane0 = simuiface.createGVehicle(dvp_lane0)
vehi_lane1 = simuiface.createGVehicle(dvp_lane1)
vehi_lane2 = simuiface.createGVehicle(dvp_lane2)

```

 

 

 **def createBus(self, pBusLine:Tessng.IBusLine, startSimuDateTime:float) -> Tessng.IVehicle: ...**

动态创建公交车

参数：

\[in\] pBusLine：公交线路

\[in\] startSimuDateTime：发车时间，单位毫秒

举例：

```python
#  动态创建公交车
bus = tessngIFace().simuInterface().createBus(busLine, 10 * 1000)

```

 

 **def catchSnapshotAsString(self) -> str: ...**

创建快照，需要分布式组件支持

 **def loadSnapshotFromString(self, data:str) -> bool: ...**

加载快照，需要分布式组件支持

参数：

\[in\] data：快照数据

 **def stopVehicleDriving(self, pVehicle:Tessng.IVehicle) -> None: ...**

停止指定车辆的仿真运行，车辆被移出路网

参数：

\[in\] pVehicle：车辆对象

举例：

```python
# L5路段车辆行驶至离路段终点50m处被移出路网
if vehi.roadId() == 5:
    vehi_currentDistToEnd = vehi.vehicleDriving().distToEndpoint(True)
    if p2m(vehi_currentDistToEnd) < 50:
        simuiface.stopVehicleDriving(vehi)

```

 

 **def vehisInLink(self, linkId:int) -> typing.List: ...**

指定ID路段上的车辆

参数

\[in\] linkId：路段ID

返回：车辆列表

举例：

```python
class IVehicle():
	#。。。
    def vehisInLink(self, linkid):
    # TESSNG 顶层接口
    iface = tessngIFace()
    # TESSNG 仿真子接口
    simuiface = iface.simuInterface()
    # ID等于1路段上车辆
    vehis = iface.simuInterface().vehisInLink(1)
	return List()

```

 **def vehisInLane(self, laneId:int) -> typing.List: ...**

指定ID车道上的车辆

参数：

\[in\] laneId：车道ID

返回：车辆列表

 **def vehisInConnector(self, connectorId:int) -> typing.List: ...**

指定ID连接段上的车辆

参数：

\[in\] connectorId：连接段ID

返回：车辆列表

 **def vehisInLaneConnector(self, connectorId:int, fromLaneId:int, toLaneId:int) -> typing.List: ...**

指定连接段ID及上游车道ID和下游车道ID相关“车道连接”上的车辆

参数：

\[in\] connectorId: 连接段ID

\[in\] fromLaneId：上游车道ID

\[in\] toLaneId：下游车道ID



 **def queueRecently(queueCounterId:int, ref_queueLength:float, ref_vehiCount:int) -> bool: ...**
获取排队计数器最近一次排队信息, 返回值为是否成功的标签，具体的排队和流量信息见入参ref_queueLength，ref_vehiCount。
该函数的入参为引用，函数直接修改入参数据
参数：
\[in\] queueCounterId：排队计数器ID
\[in & out\] queueLength：排队长度
\[in & out\] vehiCount：排队车辆数
\[out\] 是否获取成功


 **def getPedestriansStatusByRegionId(regionId:int) -> Tpye.List<Online.Pedestrian.PedestrianStatus>: ...**
根据行人面域id获取当前时间面域上所有行人的状态信息
参数：
\[in\]  regionId：面域ID
\[out\] 行人状态信息列表


 **def allPedestrianStarted(self) -> Tpye.List<Online.Pedestrian.IPedestrian>: ...**
获取所有正在运行的行人
参数：
\[out\] 行人对象列表






------

### 4.3.  GuiInterface

GuiInterface是TessInterface的子接口， 通过此接口可以访问控制TESSNG主窗体，在主窗体上创建菜单、自定义窗体等。

 **def mainWindow(self) -> PySide2.QtWidgets.QMainWindow: ...**

获取TESS NG主窗体

------



## 5. 自定义插件TessPlugin

TessPlugin是用户开发的插件顶级接口，下面有三个子接口：PyCustomerNet、PyCustomerSimulator、CustomerGui。TESS NG通过这三个子接口分别在路网、仿真过程、窗体这三个方面与用户插件进行交互。

获取插件顶层接口的方法：tessngPlugin()。

虽然用户可以通过接口TessInterface下的三个子接口访问控制TESS NG的路网、仿真过程及窗体，但用户只能调用TESS NG接口方法，不能深入接口方法内部改变运行逻辑。通过实现接口TessPlugin子接口的方法，用户可以在TESS NG的方法内部施加影响，改变运行逻辑。

TessPlugin下的子接口PyCustomerNet、PyCustomerSimulator可以让用户较多地参于加载路网及仿真过程，改变TESSNG内部运行逻辑。比如，通过实现PyCustomerNet、PyCustomerSimulator接口方法可以让用户加载路网后进行必要的处理，点击仿真按钮后根据需要确定是否继续仿真或者放弃，还可以在仿真过程对部分或全部车辆的速度施加影响，主动干预车辆的自由变道，等等。

插件的三个子接口PyCustomerNet、PyCustomerSimulator、CustomerGui的所有方法都有默认实现，用户可以根据需要实现其中部分方法或全部方法，这些方法都由TESSNG在加载并初始化插件、打开路网前后、仿真前、仿真过程中、仿真结束后进行调用，正是通过TESS NG对这些接口方法的调用达到控制或影响TESS NG运行的目的。

由于插件接口方法调用的场景、目的都不一样，为了尽可尽可能统一对插件接口方法理解，很多方法采用如下结构形式：

```python
def method(self, outParam:type) -> bool
```

TESS NG在调用这些方法时作以下理解：如果返加值为False，视为用户没有反应，忽略。如果返回值为True，表明用户有反应，这时再视参数outParam值进行处理。举范例中的一个例子，曹安路上的车辆排成方正，飞机后的车辆速度重新设置，保持与飞机相同的速度。PyCustomerSimulator的子类MySimulator实现了reSetSpeed方法如下：

```python
def ref_reSetSpeed(self, vehi, ref_inOutSpeed):
    tmpId = vehi.id() % 100000
    roadName = vehi.roadName()
    if roadName == "曹安公路":
        if tmpId == 1:
            self.mrSpeedOfPlane = vehi.currSpeed()
        elif tmpId >= 2 and tmpId <= self.mrSquareVehiCount:
            ref_inOutSpeed.value = self.mrSpeedOfPlane
        return True
    return False

```

TESS NG在计算车辆的速度后会调用插件的reSetSpeed方法，如果该方法返回True，视插件对此方法作出响应，这时再用outSpeed值取代原先计算的车速。

下面对PyCustomerNet、PyCustomerSimulator两个子接口进行说明

### 5.1. PyCustomerNet

PyCustomerNet是TessPlugin子接口，用户实现这个接口，TESSNG在加载路网前后会调用用户实现的接口方法。范例在加载临时路网后创建路段、连接段和发车点。TESSNG在绘制部分路网元素时也会调用PyCustomerNet实现类相关方法。范例通过实现方法labelNameAndFont让部分路段和连接段用路段名（默认为ID）绘制标签。

下面对PyCustomerNet接口方法作详细解释。

 

 **def ref_netFileSuffix(self, ref_suffix:Tessng.objstring) -> bool: ...**

路网文件后缀，由用户通过参数suffix设置

参数：

[out] suffix：路网文件后缀名

 **def customerTableDDL(self) -> typing.Dict: ...**

添加用户设计的表

返回：用户数据库的表定义map，key为表名，value为表的定义

 **def insertCustomerData(self) -> None: ...**

插入用户插件创建的表数据

 **def deleteCustomerData(self) -> None: ...**

删除用户插件创建的表数据

 **def beforeLoadNet(self) -> None: ...**

打开路网前调用，用户可以通过此方法在加载路网前作必要的初始化准备工作

 **def afterLoadNet(self) -> None: ...**

加载路网后调用。

举例：

​    范例加载路网后读路段数，如果路段数为0创建路段、连接段和发车点，创建完成后根据参数'__simuafterload'值决定是否启动仿真：

```python
def afterLoadNet(self):
    # 代表TESS NG的接口
    iface = tessngIFace()
    # 代表TESS NG的路网子接口
    netiface = iface.netInterface()
    # 获取路段数
    count = netiface.linkCount()
    if(count == 0):
        self.createNet()

```

```
 
```

 

 **def linkType(self, lType:typing.Sequence) -> bool: ...**

路段类型

参数：

[out] lType：用户定义的路段类型列表

 **def laneType(self, lType:typing.Sequence) -> bool: ...**

车道类型

参数：

[out] lType：用户定义的车道类型列表

 **def linkBuildGLanes(self, pILink:Tessng.ILink) -> bool: ...**

创建车道

参数：

\[in\] pILink：路段对象

返回：如果返回True，表示用户已创建了车道，TESSNG不再创建

 **def isPermitForCustDraw(self) -> bool: ...**

在绘制路网过程中是否允许调用客户绘制逻辑，默认为False。本方法的目的是在python环境减少不必要的对python代码调用，消除对运行效率的负面影响。可参数范例。

 **def linkBrushColor(self, linkId:int, color:PySide2.QtGui.QColor) -> bool: ...**

路段笔刷颜色

参数：

\[in\] linkId：路段ID

[out] color：笔刷颜色

返回：如果返回False，TESSNG会忽略，否则用color颜色绘制路段 

 **def paint(self, itemType:int, itemId:int, painter:PySide2.QtGui.QPainter) -> bool: ...**

绘制路网元素

参数：

\[in\] itemType：路网元素类型，在包NetItemType中定义

\[in\] itemId：路网元素ID

\[in\] painter：QPainter对象

返回：如果返回True，TESS NG认为插件已绘制，TESS NG不再绘制，否则TESS NG进行绘制。

 **def linkBrushAndPen(self, linkId:int, brush:PySide2.QtGui.QBrush, pen:PySide2.QtGui.QPen) -> bool: ...**

根据指定ID设置绘制路段的笔刷。

参数：

\[in\] linkId：路段ID

[out] brush：绘刷

[out] pen：绘笔

返回：False 忽略，True 用brush及pen参数绘制ID等于linkId的路段。

 **def laneBrushAndPen(self, laneId:int, brush:PySide2.QtGui.QBrush, pen:PySide2.QtGui.QPen) -> bool: ...**

根据指定车道ID设置绘制车道的笔刷。

\[in\] laneId：车道ID

[out] brush：绘刷

[out] pen：绘笔

返回：False 忽略，True 用brush及pen参数绘制ID等于laneId的车道

 **def connectorAreaBrushColor(self, connAreaId:int, color:PySide2.QtGui.QColor) -> bool: ...**

面域笔刷颜色

参数：

\[in\] connAreaId：面域ID

[out] color：笔刷颜色

返回：False：忽略，True：TESSNG用color绘制面域

 **def connectorAreaBrushAndPen(self, connAreaId:int, brush:PySide2.QtGui.QBrush, pen:PySide2.QtGui.QPen) -> bool: ...**

根据指定面域ID设置绘制面域的笔刷。

\[in\] connAreaId：面域ID

[out] brush：绘刷

[out] pen：绘笔

返回：False 忽略，True 用brush及pen参数绘制ID等于connAreaId的面域

 **def ref_labelNameAndFont(self, itemType:int, itemId:int, ref_outPropName:Tessng.objint, ref_outFontSize:Tessng.objreal) -> None: ...**

根据路网元素类型及ID确定用标签用ID或名称作为绘制内容。

参数：

\[in\] itemType：路段元素类型，类型常量在文件Plugin/_netitemtype.h中定义；

\[in\] itemId：路网元素ID；

[out] outPropName：枚举值，在文件Plugin/_netitem.h中定义，如果赋值GraphicsItemPropName::Id，则用ID作为绘制内容，如果赋值GraphicsItemPropName::Name，则用路网元素名作为绘制内容；

 [out] outFontSize：字体大小，单位：米。假设车道宽度是3米，如果赋给outFontSize的值是6，绘出的文字将占用两个车道的宽度。

返回：False 忽略，True 则根据设定的outPropName 值确定用ID或名称绘制标签，并且用指定大小绘制。

举例：

范例中的路段和连接段的标签内容部分是名称，部分是ID。

```python
def ref_labelNameAndFont(self, itemType, itemId, ref_outPropName, ref_outFontSize):
    # 代表TESS NG的接口
    iface = tessngIFace()
    # 代表TESS NG仿真子接口
    simuiface = iface.simuInterface()
    # 如果仿真正在进行，设置ref_outPropName.value等于GraphicsItemPropName.None_，路段和车道都不绘制标签
    if simuiface.isRunning():
        ref_outPropName.value = GraphicsItemPropName.None_
        return
    # 默认绘制ID
    ref_outPropName.value = GraphicsItemPropName.Id
    # 标签大小为6米
    ref_outFontSize.value = 6
    # 如果是连接段一律绘制名称
    if itemType == NetItemType.GConnectorType:
        ref_outPropName.value = GraphicsItemPropName.Name
    elif itemType == NetItemType.GLinkType:
        if itemId == 1 or itemId == 5 or itemId == 6:
            ref_outPropName.value = GraphicsItemPropName.Name

```

 

 

 **def isDrawLinkCenterLine(self, linkId:int) -> bool: ...**

是否绘制路段中心线

参数：

\[in\] linkId：路段ID；

返回值：True绘制，False不绘制。

 **def isDrawLinkCorner(self, linkId:int) -> bool: ...**

是否绘制路段四个拐角的圆形和正方型。

参数：

\[in\] linkId：路段ID；

返回值：True绘制，False不绘制。

 **def isDrawLaneCenterLine(self, laneId:int) -> bool: ...**

是否绘制车道中心线。

参数：

\[in\] laneId：车道ID；

返回值：True绘制，False不绘制。

 **def afterViewKeyReleaseEvent(self, event:PySide2.QtGui.QKeyEvent) -> None: ...**

QGraphicsView的keyReleaseEvent事件后行为，用户可以根据自己的需要接入键盘事件，实现自身业务逻辑。

 **def afterViewMouseDoubleClickEvent(self, event:PySide2.QtGui.QMouseEvent) -> None: ...**

QGraphicsView的mouseDoubleClickEvent事件后的行为，用户可以根据自己的需要编写鼠标双击事件响应代码。

 **def afterViewMouseMoveEvent(self, event:PySide2.QtGui.QMouseEvent) -> None: ...**

QGraphicsView的mouseMoveEvent事件后的行为，用户可以根据自己的需要编写鼠标移动事件响应代码。

 **def afterViewMousePressEvent(self, event:PySide2.QtGui.QMouseEvent) -> None: ...**

QGraphicsView的mousePressEvent事件后的行为，用户可以根据自己的需要编写鼠标点击事件响应代码。

 **def afterViewMouseReleaseEvent(self, event:PySide2.QtGui.QMouseEvent) -> None: ...**

QGraphicsView的mouseReleaseEvent事件后的行为，用户可以根据自己的需要编写鼠标释放事件响应代码。

 **def afterViewResizeEvent(self, event:PySide2.QtGui.QResizeEvent) -> None: ...**

QGraphicsView的resizeEvent事件后的行为，用户可以根据自己的需要编写屏幕缩放事件响应代码。

 **def afterViewWheelEvent(self, event:PySide2.QtGui.QWheelEvent) -> None: ...**

QGraphicsView的鼠标滚动事件后的行为，用户可以根据自己的需要编写鼠标滚动事件后响应代码。

 **def afterViewScrollContentsBy(self, dx:int, dy:int) -> None: ...**

QGraphicsView滚动条移动事件后的行为，用户可以根据自己的需要实现视窗滚动条移动后响应代码。

------

### 5.2. PyCustomerSimulator

PyCustomerSimulator是TessPlugin子接口，用户实现这个接口。TESS NG在仿真前后以及仿真过程中调用这个接口实现的方法，达到与插件交互的目的，用户可以通过这个接口的实现在仿真前后以及仿真运算过程中对TESS NG的仿真进行干预，大到可以控制仿真是否进行，小到干预某一车辆的驾驶行为。

用户对车辆驾驶行为的干预主要通过车速和变道来实现。对车速的干预主要有以下几个方法：

1）重新计算车速；

2）修改路段限速；

3）重新计算加速度；

4）修改跟驰安全距离和安全时距、重新设置前车距

以上几个方法的优先级依次降低。在没有插件干预的情况下，车辆行驶的最高速度受到道路的最高速度限制；在有插件的干预下，如果直接修改了车速，则不受道路最高限速的限制。

下面对PyCustomerSimulator接口方法作详细解释。

 **def ref_beforeStart(self, ref_keepOn:Tessng.objbool) -> None: ...**

仿真前的准备。如果需要，用户可通过设置keepOn为False来放弃仿真。

参数：

[out] ref_keepOn：是否继续，默认为True；

 **def afterStart(self) -> None: ...**

启动仿真后的操作。这个方法的处理时间尽量短，否则影响仿真时长的计算，因为调用这个方法的过程仿真已经计时。仿真前的操作尽可能放到beforeStart方法中处理。

 **def afterStop(self) -> None: ...**

 仿真结束后的操作，如果需要，用户可以在此方法释放资源。**def calcDynaDispatchParameters(self) -> typing.List: ...**

计算动态发车信息，用来修改发车点相关参数，此方法可以用来实现实时动态仿真。

返回：动态发车信息Online.DispatchInterval列表。

举例：

```python
def calcDynaDispatchParameters(self):
        # TESSNG 顶层接口
        iface = tessngIFace()
        currSimuTime = iface.simuInterface().simuTimeIntervalWithAcceMutiples()
        if currSimuTime % (10 * 1000) == 0 and currSimuTime < 60 * 1000:
            # ID等于5路段上车辆
            lVehi = iface.simuInterface().vehisInLink(5)
            if currSimuTime < 1000 * 30 or len(lVehi) > 0:
                return []
            else:
                now = datetime.now()
                # 当前时间秒
                currSecs = now.hour * 3600 + now.minute * 60 + now.second
                # 仿真10秒后且ID等于1的路段上车辆数为0，则为ID等于1的发车点增加发车间隔
                di = Online.DispatchInterval()
                # 动作控制案例-机动车交叉口L5路段发车点ID为11
                di.dispatchId = 11
                di.fromTime = currSecs
                di.toTime = di.fromTime + 300 - 1
                di.vehiCount = 300
                di.mlVehicleConsDetail = [Online.VehiComposition(1, 60), Online.VehiComposition(2, 40)]
                print("流量修改完成，当前时间为{}".format(currSimuTime))
                return [di]
        return []

```

 

 **def calcDynaFlowRatioParameters(self) -> typing.List: ...**

一个或一次数据来源里保存的所有决策点在一个时间间隔的路径流量分配信息，此方法可以用来实现实时动态仿真。

返回：决策点流量分配信息Online.DecipointFlowRatioByInterval列表。

 **def calcDynaSignalContralParameters(self) -> typing.List: ...**

一个或一次数据来源里保存的所有信号灯组的信号控制信息。

返回：信号灯组控制参数Online.SignalContralParam列表。

 **def initVehicle(self, pIVehicle:Tessng.IVehicle) -> None: ...**

初始化车辆，此方法在车辆起动加入路网时被调用，用户可以在这个方法里调用IVehicle的setVehiType方法重新设置类型，调用initLane或initLaneConnector方法对车辆的车道序号、起始位置、车辆大小进行初始化。

参数：

\[in\] pIVehicle：车辆对象

举例：

```python
def initVehicle(self, vehi):
        tmpId = vehi.id() % 100000
        # 车辆所在路段名或连接段名
        roadName = vehi.roadName()
        # 车辆所在路段ID或连接段ID
        roadId = vehi.roadId()
        if roadName == '曹安公路':
            #飞机
            if tmpId == 1:
                vehi.setVehiType(12)
                vehi.initLane(3, m2p(105), 0)
            #工程车
            elif tmpId >=2 and tmpId <=8:
                vehi.setVehiType(8)
                vehi.initLane((tmpId - 2) % 7, m2p(80), 0)
            #消防车
            elif tmpId >=9 and tmpId <=15:
                vehi.setVehiType(9)
                vehi.initLane((tmpId - 2) % 7, m2p(65), 0)
            #消防车
            elif tmpId >=16 and tmpId <=22:
                vehi.setVehiType(10)
                vehi.initLane((tmpId - 2) % 7, m2p(50), 0)
            #最后两队列小车
            elif tmpId == 23:
                vehi.setVehiType(1)
                vehi.initLane(1, m2p(35), 0)
            elif tmpId == 24:
                vehi.setVehiType(1)
                vehi.initLane(5, m2p(35), 0)
            elif tmpId == 25:
                vehi.setVehiType(1)
                vehi.initLane(1, m2p(20), 0)
            elif tmpId == 26:
                vehi.setVehiType(1)
                vehi.initLane(5, m2p(20), 0)
            elif tmpId == 27:
                vehi.setVehiType(1)
                vehi.initLane(1, m2p(5), 0)
            elif tmpId == 28:
                vehi.setVehiType(1)
                vehi.initLane(5, m2p(5), 0)
            # 最后两列小车的长度设为一样长，这个很重要，如果车长不一样长，导致的前车距就不一样，会使它们变道轨迹长度不一样，就会步调不一致。
            if tmpId >= 23 and tmpId <= 28:
                vehi.setLength(m2p(4.5), True)
        # 此处宽度设置为True，表示车身宽度也等比例变化，如果为False，则车身宽度不变
        return True


```

 

  

 **def ref_beforeCreateGVehiclesForBusLine(self, pBusLine:Tessng.IBusLine, ref_keepOn:Tessng.objbool) -> None: ...**

创建公交车辆前的预处理

参数：

\[in\] pBusLine：公交线路

[in、out] keepOn：是否继续执行创建公交车辆，如果KeepOn被赋值为False，TESSNG不再创建公交车辆

 **def shape(self, pIVehicle:Tessng.IVehicle, outShape:PySide2.QtGui.QPainterPath) -> bool: ...**

车辆外型，用户可以用此方法改变车辆外观

参数：

\[in\] pIVehicle：车辆对象

[in、out] outShape：车辆外形

返回：如果返回False，则忽略

 **def ref_beforeCalcLampColor(self, ref_keepOn:Tessng.objbool) -> bool: ...**

计算信号灯色前的预处理。

参数：

[in、out] 是否断续计算

返回：如果返回 True，且keepOn等于False，TESS NG不再计算信号灯色。

 **def calcLampColor(self, pSignalLamp:Tessng.ISignalLamp) -> bool: ...**

计算信号灯的灯色。ISignalLamp有设置信号灯颜色方法。

参数：

\[in\] pSignalLamp：信号灯对象；

返回值：

如果返回True，表明用户已修改了信号灯颜色，TESS NG不再计算灯色。

 **def reCalcToLeftLane(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

计算是否要左强制变道，TESS NG在移动车辆时计算强制左变道的条件，当条件不足时让插件计算，如果返回值为True，强制左变道。

参数：

\[in\] pIVehicle：车辆对象。

返回：False：忽略，True：强制左变道

 **def reCalcToRightLane(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

计算是否要右强制变道，TESS NG在先移动车辆时计算强制右变道的条件，当条件不足时让插件计算，如果返回值为True，强制右变道。 
用户通过此函数设置是车辆是否有强制右边道的动机，但是否变道还要看是否满足变道条件。

参数：

\[in\] pIVehicle：车辆对象

返回：False：忽略，True：强制右变道

 **def ref_beforeToLeftFreely(self, pIVehicle:Tessng.IVehicle, ref_keepOn:Tessng.objbool) -> None: ...**

自由左变道前处理，如果bKeepOn被赋值为False，TESSNG不再计算是否自由左变道
用户通过此函数设置车辆是否在后续的仿真中屏蔽自由左变道的动机生成

参数：

\[in\] pIVehicle：车辆

[in、out] bKeepOn：是否继续，如果设为False，不再计算是否可以左自由变道

 **def ref_beforeToRightFreely(self, pIVehicle:Tessng.IVehicle, ref_keepOn:Tessng.objbool) -> None: ...**

自由右变道前处理，如果bKeepOn被赋值为False，TESSNG不再计算是否自由右变道
用户通过此函数设置车辆是否在后续的仿真中屏蔽自由右变道的动机生成
\[in\] pIVehicle：车辆

[in、out] bKeepOn：是否继续，如果设为False，不再计算是否可以右自由变道

举例：

```python
# 自由左变道前预处理
def ref_beforeToLeftFreely(self, pIVehicle, ref_keepOn):
   if pIVehicle.roadId()==9:
       pIVehicle.setColor("#0000FF")
# 自由右变道前预处理
def ref_beforeToRightFreely(self, pIVehicle, ref_keepOn):
    if pIVehicle.roadId() == 9:
        pIVehicle.setColor("#EE0000")

```

 

 **def reCalcToLeftFreely(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

重新计算是否要自由左变道。TESS NG在移动车辆时计算自由左变道条件，当条件不足时让插件计算，如果返回值为True，自由左变道。
用户可以调用此函数在需要的时候让TESSNG再次计算自由左变道的判断逻辑；但不保证计算结果满足变道条件

\[in\] pIVehicle：车辆

返回：False：忽略，True：左自由変道，但在一些特殊场景也会放弃变道，如危险

 **def reCalcToRightFreely(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

重新计算是否要自由右变道。TESS NG在移动车辆时计算自由右变道条件，当条件不足时让插件计算，如果返回值为True，自由右变道。
用户可以调用此函数在需要的时候让TESSNG再次计算自由右变道的判断逻辑；但不保证计算结果满足变道条件
参数：

\[in\] pIVehicle：车辆对象

返回：False：忽略，True：右自由変道，但在一些特殊场景也会放弃变道，如危险

 **def reCalcDismissChangeLane(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

重新计算是否撤销变道，通过pIVehicle获取到自身条件数据及当前周边环境条件数据，判断是否要撤销正在进行的变道。

参数：

\[in\] pIVehicle：车辆

返回：True 如果当前变道完成度不超过三分之一，则撤销当前变道行为；False 忽略。

 **def ref_reCalcdesirSpeed(self, pIVehicle:Tessng.IVehicle, ref_desirSpeed:Tessng.objreal) -> bool: ...**

重新计算期望速度，TESS NG调用此方法时将车辆当前期望速度赋给inOutDesirSpeed，如果需要，用户可在此方法重新计算期望速度，并赋给inOutDesirSpeed。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutDesirSpeed：重新设置前后的车辆期望速度，单位：像素/秒；

举例：

```python
def ref_reCalcdesirSpeed(self, vehi, ref_desirSpeed):
        # 当前已仿真时间，单位：毫秒
        iface = tessngIFace()
        currSimuTime = iface.simuInterface().simuTimeIntervalWithAcceMutiples()
        if (currSimuTime > 30 * 1000):
            return False
        roadId = vehi.roadId()
        # 以动作控制案例 - 机动车交叉口路网的L5路段为例
        if roadId == 5:
            # L5离路段起点50-150m处为减速区
            distToStart = vehi.vehicleDriving().distToStartPoint()
            if m2p(50) < distToStart < m2p(100) and vehi.lane:
                if vehi.vehicleTypeCode() == 1:
                    ref_desirSpeed.value = m2p(10)
                    print(vehi.id(), "的小客车进入减速区，减速为10，当前速度为", vehi.currSpeed())
                elif vehi.vehicleTypeCode() == 2:
                    print(vehi.id(), "的大客车进入减速区，减速为5，当前速度为", vehi.currSpeed())
                    ref_desirSpeed.value = m2p(5)
                return True
        return False
```

 **def ref_reSetFollowingType(self, pIVehicle:Tessng.IVehicle, ref_outTypeValue:Tessng.objint) -> bool: ...**

重新设置跟驰类型，在计算加速度的过程中被调用

参数：

\[in\] pIVehicle：车辆对象

[out] outTypeValue：跟驰类型，0：停车，1: 正常，5：急减速，6：急加速，7：汇入, 8：穿越，9：协作减速，10：协作加速，11：减速待转，12：加速待转

返回：False：忽略，True：用outTypeValue设置车辆驾驶行为的跟驰类型

 **def ref_reSetFollowingParam(self, pIVehicle:Tessng.IVehicle, ref_inOutSafeInterval:Tessng.objreal, ref_inOutSafeDistance:Tessng.objreal) -> bool: ...**

重新设置跟驰模型的安全间距和安全时距。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutSafeInterval：安全时距，单位：秒；

[in、out] inOutSafeDistance：安全间距：单位：像素；

举例：

```python
范例将第二条连接段上的车辆跟车安全间距设为30米。代码如下：
def ref_reSetFollowingParam(self, vehi, ref_inOutSi, ref_inOutSd):
    roadName = vehi.roadName()
    if roadName == "连接段2":
        ref_inOutSd.value = m2p(30);
        return True
    return False

```

 

 **def reSetFollowingParams(self) -> typing.List: ...**

重新设置跟驰模型参数，影响所有车辆。此方法被TESS NG调用，用返回的跟驰模型取代当前仿真正在采用的跟驰模型。

返回：跟驰参数列表，可对机动车和非机车的跟驰参数重新设置，设置以后会被采用，直到被新的参数所代替。

 **def ref_reSetDistanceFront(self, pIVehicle:Tessng.IVehicle, distance:Tessng.objreal, s0:Tessng.objreal) -> bool: ...**

重新设置前车距及安全跟车距离

参数：

\[in\] pIVehicle：车辆对象

[in、out] distance：当前车辆与前车的距离，默认单位：像素

[in、out] s0：安全跟车距离，默认单位：像素

返回：False：忽略，True：用distance设置前车距，用s0设置安全跟车距离

 **def ref_reSetSpeed(self, pIVehicle:Tessng.IVehicle, ref_inOutSpeed:Tessng.objreal) -> bool: ...**

重新设置车速。TESS NG调用此方法时将当前计算所得车速赋给**ref_inOutSpeed.value**，如果需要，用户可以在此方法重新计算车速并赋给ref_inOutSpeed.value。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutSpeed：重新计算前后的车速，单位：像素/秒。

返回：False：忽略，True：用inOutSpeed设置车辆当前速度

举例：

```python
def ref_reSetSpeed(self, vehi, ref_inOutSpeed):
    # 尝试让L12等待的车辆强制闯红灯
    if vehi.roadId() == 12:
        vehi_currentSpeed = vehi.currSpeed()
        vehi_currentDistToEnd = vehi.vehicleDriving().distToEndpoint(True)
        if m2p(vehi_currentSpeed) < 20 and p2m(vehi_currentDistToEnd) < 3:
            random_number = random.random()
            if random_number < 0.3:
                ref_inOutSpeed.value = m2p(15)
                print(vehi.id(), vehi.currSpeed())
                return True
    # 强制L5路段车辆在距路段终点50m处停车
    vehi_currentDistToEnd = vehi.vehicleDriving().distToEndpoint(True)
    if p2m(vehi_currentDistToEnd) < 50:
        if vehi.roadId() == 5:
            ref_inOutSpeed.value = m2p(0)
            return True

```

 

 **def ref_beforeMergingToLane(self, pIVehicle:Tessng.IVehicle, ref_keepOn:Tessng.objbool) -> None: ...**

在“车道连接”上汇入车道前的计算，可以让TESS NG放弃汇入计算，以便于用户实现自己的汇入逻辑。

参数：

\[in\] pIVehicle：车辆对象；

[out] **ref_keepOn**：是否放弃，默认为True。赋值**ref_keepOn.value**为False，TESSNG则放弃汇入。

 **def afterOneStep(self) -> None: ...**

一个计算批次后的计算，这个时候所有车辆都完成同一个批次的计算。通常在这个方法中获取所有车辆轨迹、检测器数据、进行必要的小计等。在这个方法中进行的计算基本不影响仿真结果的一致性，但效率不高，如果计算量大对仿真效率会有影响。

举例：

```python
范例中在这个方法中获取车辆对象和轨迹等信息。代码如下：
def afterOneStep(self):
    #= == == == == == =以下是获取一些仿真过程数据的方法 == == == == == ==
    # TESSNG 顶层接口
    iface = tessngIFace()
    # TESSNG 仿真子接口
    simuiface = iface.simuInterface()
    # TESSNG 路网子接口
    netiface = iface.netInterface()
    # 当前仿真计算批次
    batchNum = simuiface.batchNumber()
    # 当前已仿真时间，单位：毫秒
    simuTime = simuiface.simuTimeIntervalWithAcceMutiples()
    # 开始仿真的现实时间
    startRealtime = simuiface.startMSecsSinceEpoch()
    # 当前正在运行车辆列表
    vehis = simuiface.allVehiStarted()

```

 

 

 **def duringOneStep(self) -> None: ...**

该方法在各个线程进行同一批次的计算过程中调用，这时存在部分车辆计算完成，部分车辆仍在计算过程中。这个方法中的计算不够安全，但效率较高。

 **def ref_beforeNextRoad(self, pIVehicle:Tessng.IVehicle, pRoad:PySide2.QtWidgets.QGraphicsItem, ref_keepOn:Tessng.objbool) -> None: ...**

计算下一道路前的处理

参数：

\[in\] pIVehicle：车辆

\[in\] pRoad：暂不使用

[in、out] keepOn：是否继续计算，False：TESSNG不再计算后续道路，True：继续计算

 **def candidateLaneConnectors(self, pIVehicle:Tessng.IVehicle, lInLC:typing.Sequence) -> typing.List: ...**

计算当车辆离开路段时后续可经过的“车道连接”, lInLC是已计算出的当前车道可达的所有“车道连接”，用户可以从中筛选或重新计算。如果车辆有路径，则忽略

参数：

\[in\] pIVehicle 当前车辆

\[in\] lInLC：TESS NG计算出的后续可达“车道连接”列表

返回：用户确定的后续可达“车道连接”列表

 **def candidateLaneConnectors(self, pIVehicle:Tessng.IVehicle, lInLC:typing.Sequence) -> typing.List: ...**

计算车辆后续“车道连接”，此时车辆正跨出当前路段，将驶到pCurrLaneConnector。此方法可以改变后续“车道连接”。如果返回的“车道连接”为空，TESSNG会忽略此方法的调用。如果返回的“车道连接”不在原有路径上，或者此方法设置了新路径且新路径不经过返回的“车道连接”，TESSNG调用此方法后会将路径设为空。

 **def ref_beforeNextPoint(self, pIVehicle:Tessng.IVehicle, ref_keepOn:Tessng.objbool) -> None: ...**

计算车辆移动到下一点前的操作，用户可以通过此方法让TESSNG放弃对指定车辆到下一点的计算。

参数：

\[in\] pIVehicle：车辆对象；

[out] keepOn：是否继续, 默认为True，如果keepOn赋值为False，TESSNG放弃移动到下一点的计算，但不移出路网。

 **def calcLimitedLaneNumber(self, pIVehicle:Tessng.IVehicle) -> typing.List: ...**

计算限制车道序号：如管制、危险等，最右侧编号为0。

参数：

\[in\] pVehicle：车辆对象；

返回：车道序号集，保存车辆不可以驰入的车道序号。

 **def ref_calcSpeedLimitByLane(self, pILink:Tessng.ILink, laneNumber:int, ref_outSpeed:Tessng.objreal) -> bool: ...**

由车道确定的限制车速（最高速度, 公里/小时）

参数：

\[in\] pILink：路段

\[in\] laneNumber：,laneNumber:车道序号，最右侧编号为0

[in、out] outSpeed：限制速度，公里/小时

返回：False：忽略，True：用outSpeed限制指定车道速度

 **def ref_calcMaxLimitedSpeed(self, pIVehicle:Tessng.IVehicle, ref_inOutLimitedSpeed:Tessng.objreal) -> bool: ...**

重新计算车辆当前最大限速，不受道路限速的影响。在没有插件干预的情况下，车辆速度大于道路限度时按道路最大限速行驶，在此方法的干预下，可以提高限速，让车辆大于道路限速行驶。

TESS NG调用此方法时将当前最高限速赋给inOutLimitedSpeed，如果需要，用户可以在方法里重新设置inOutLimitedSpeed值。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutLimitedSpeed：计算前后的最大限速，单位：像素/秒。

返回结果：

​    如果返回False则忽略，否则取inOutLimitedSpeed为当前道路最大限速。

 **def ref_calcDistToEventObj(self, pIVehicle:Tessng.IVehicle, ref_dist:Tessng.objreal) -> bool: ...**

计算到事件对象距离，如到事故区、施工区的距离

参数：

\[in\] pIVehicle：车辆

[in、out] dist：车辆中心点距事件对象距离，单位像素

返回：False：忽略，True：用dist计算安全变道距离等

 **def ref_calcChangeLaneSafeDist(self, pIVehicle:Tessng.IVehicle, ref_dist:Tessng.objreal) -> bool: ...**

计算安全变道距离。

参数：

\[in\] pIVehicle：车辆，计算该车辆安全变道距离。

[in、out] dist：安全变道距离，dist.value保存了TESSNG已算得的安全变道距离，用户可以在此方法重新计算。

 返回：False 忽略，True TESS NG取dist.value作为安全变道距离**def afterStep(self, pIVehicle:Tessng.IVehicle) -> None: ...**

完成车辆pIVehicle“一个批次计算”后的处理。可以在此获取车辆当前信息，如当前道路、位置、方向角、速度、期望速度、前后左右车辆等。

参数：

\[in\] pIVehicle：车辆对象；

 **def ref_calcAcce(self, pIVehicle:Tessng.IVehicle, ref_acce:Tessng.objreal) -> bool: ...**

计算加速度； tessng的车辆按照此加速度进行下一步状态更新

\[in\] pIVehicle：待计算加速度的车辆

[out] ref_acce：计算结果，单位：像素/秒^2

返回：False 忽略，True 则TES NG用调用此方法后所得ref_acce.value作为当前车辆的加速度。

 **def isCalcVehicleVector3D(self) -> bool: ...**

是否计算车辆3D属性，如欧拉角等

 **def calcVehicleEuler(self, pIVehicle:Tessng.IVehicle, bPosiDire:bool=...) -> PySide2.QtGui.QVector3D: ...**

计算欧拉角

参数：

\[in\] pIVehicle：车辆对象

\[in\] bPosiDire：车头方向是否正向计算，如果bPosiDire为False则反向计算

 **def ref_reSetAcce(self, pIVehicle:Tessng.IVehicle, ref_inOutAcce:Tessng.objreal) -> bool: ...**

重新计算加速度。TESS NG调用此方法时将当前计算所得加速度赋给inOutAcce，如果需要，用户可以在此方法中重新计算加速度并赋给ref_inOutAcce.value。

参数：

\[in\] pIVehicle：车辆对象

[in、out] ref_inOutAcce：重新计算前及计算后的加速度，单位：像素/秒^2

返回结果：

如果返回False则忽略，如果返回True，则将inOutAcce作为当前加速度。

举例：

```python
def ref_reSetAcce(self, vehi, inOutAcce):
    roadName = vehi.roadName()
    if roadName == "连接段1":
        if vehi.currSpeed() > m2p(20 / 3.6):
            inOutAcce.value = m2p(-5)
            return True
        elif vehi.currSpeed() > m2p(20 / 3.6):
            inOutAcce.value = m2p(-1)
            return True
    return False

```

 

 

 **def afterCalcTracingType(self, pIVehicle:Tessng.IVehicle) -> None: ...**

计算跟驰类型后处理

参数：

\[in\] pIVehicle：车辆

 **def travelOnChangingTrace(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

是否在变轨迹上

参数：

\[in\] pIVehicle：车辆

 **def travelOnChangingTrace(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

获取车辆边界矩形； 矩形顶点坐标是以车辆中心点为原点的，是车辆坐标系下的像素点

参数：

\[in\] pIVehicle：车辆对象

[out] outRect：车辆边界矩形

 **def shape(self, pIVehicle:Tessng.IVehicle, outShape:PySide2.QtGui.QPainterPath) -> bool: ...**

获取车辆图形路径

参数：

\[in\] pIVehicle：车辆对象

[out] outShape：车辆形状路径

 **def rePaintVehicle(self, pIVehicle:Tessng.IVehicle, painter:PySide2.QtGui.QPainter) -> None: ...**

绘制车辆

参数：

\[in\] pIVehicle：车辆对象

\[in\] painter：笔刷

返回：True，TESSNG不再绘制车辆，False，TESSNG认为用户没有绘制，继续绘制

 **def ref_paintVehicleWithRotation(self, pIVehicle:Tessng.IVehicle, painter:PySide2.QtGui.QPainter, ref_inOutRotation:Tessng.objreal) -> bool: ...**

绘制车辆，绘制前将车辆对象旋转指定角度

参数：

\[in\] pIVehicle：车辆对象

\[in\] painter：QPainter指针

\[in\] inOutRotation：旋转的角度

返回：True，TESSNG不再绘制车辆，False，TESSNG认为用户没有绘制，继续绘制

 **def ref_paintVehicleWithRotation(self, pIVehicle:Tessng.IVehicle, painter:PySide2.QtGui.QPainter, ref_inOutRotation:Tessng.objreal) -> bool: ...**

以设定的角度绘制车辆

参数：

\[in\] pIVehicle，车辆对象

\[in\] painter：QT的QPainter对象

[in、out] ref_inOutRotation：角度，TESS NG在调用此方法时传入车辆的旋转角，这个方法内部可以修改这个角度，改变TESS NG计算结果

返回：如果True，TESS NG不再绘制，否则TESS NG按原有规则绘制车辆。

 **def paintVehicle(self, pIVehicle:Tessng.IVehicle, painter:PySide2.QtGui.QPainter) -> bool: ...**

绘制车辆

参数：

\[in\] pIVehicle，要重绘制的车辆

\[in\] painter，QPainter对象

返回：如果返回True，TESS NG不再绘制，否则TESS NG按原有规则绘制车辆。

 **def rePaintVehicle(self, pIVehicle:Tessng.IVehicle, painter:PySide2.QtGui.QPainter) -> None: ...**

绘制车辆后的再绘制，客户可在此方法增加绘制内容

参数：

\[in\] pIVehicle，要重绘制的车辆

\[in\] painter，QPainter对象

 **def ref_reCalcAngle(self, pIVehicle:Tessng.IVehicle, ref_outAngle:Tessng.objreal) -> bool: ...**

重新计算角度。TESS NG调用此方法时将当前算得的角度赋给ref_outAngle.value，如果需要，用户可在此方法中重新计算车辆角度，并将算得的角度赋给ref_outAngle.value。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] ref_outAngle：重新计算前后角度，北向0度顺时针，一周360度

 **def isStopDriving(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

是否停车运行，TESS NG在计算下一点位置后调用，判断是否要停止车辆pIVehicle的运行。

参数：

\[in\] pIVehicle：车辆对象；

返回结果：

如果返回True，则停止该车辆运行，移出路网。

 **def isPassbyEventZone(self, pIVehicle:Tessng.IVehicle) -> bool: ...**

是否正在经过事件区（如：施工区、限速区等）

参数：

\[in\] pIVehicle：车辆对象

 **def beforeStopVehicle(self, pIVehicle:Tessng.IVehicle) -> None: ...**

车辆停止运行前的处理。

参数：

\[in\] pIVehicle：车辆对象；

 **def afterStopVehicle(self, pIVehicle:Tessng.IVehicle) -> None: ...**

车辆停止运行后的处理

参数：

\[in\]pIVehicle：车辆对象。

 **def recoveredSnapshot(self) -> bool: ...**

用快照恢复仿真场景，分布式环境可用

 **def vehiRunInfo(self, pIVehicle:Tessng.IVehicle) -> str: ...**

车辆运行信息。在仿真过程中如果某辆车被单选，按ctrl+i 会弹出被单选车辆运行状态，文本框中的“其它信息”就是当前方法返回的字符串，开发者可以借此对实现的业务逻辑进行了解，用户可以了解仿真过程中具体车辆的一些特殊信息。

![车辆运行过程属性窗口](/img/p22.png)





<!-- ex_nonav -->

