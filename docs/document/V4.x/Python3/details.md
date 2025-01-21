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

举例：

```python
# 获取路网ID
iface = tessngIFace()
# TESSNG路网子接口
netiface = iface.netInterface()
netAttrs = netiface.netAttrs()
print(f"路网ID={netAttrs.id()}")
```

 **def netName(self) -> str: ...**

获取路网名称

举例：

```python
# 获取路网名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"路网名称={netAttrs.netName()}")
```

 **def url(self) -> str: ..**

获取源数据路径，可以是本地文件，可以是网络地址

举例：

```python
# 获取源数据路径
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"源数据路径={netAttrs.url()}")
```

 **def type(self) -> str: ...**

获取来源分类："TESSNG"表示TESSNG自建；"OpenDrive"表示由OpenDrive数据导入；"GeoJson"表示由geojson数据导入

举例：

```python
# 获取来源分类
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"来源分类={netAttrs.type()}")
```

 **def bkgUrl(self) -> str: ...**

获取背景路径

举例：

```python
# 获取背景路径
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"背景路径={netAttrs.bkgUrl()}")
```

 **def otherAttrs(self) -> typing.Dict: ...**

获取其它属性数据, json 数据

举例：

```python
# 获取其它属性数据
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"其它属性数据={netAttrs.otherAttrs()}")
```

 **def explain(self) -> str: ...**

获取路网说明

举例：

```python
# 获取路网说明
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"路网说明={netAttrs.explain()}")
```

 **def centerPoint(self，unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

获取路网中心点位置，默认单位：像素，可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）
参数：
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取路网中心点位置
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网的基本属性
netAttrs = netiface.netAttrs()
print(f"路网中心点位置={netAttrs.centerPoint()}")
print(f"路网中心点位置(米制)={netAttrs.centerPoint(UnitOfMeasure.Metric)}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showRoadNetAttr(netiface)

def showRoadNetAttr(netiface):
    netpath = netiface.netFilePath()
    print(f"===保存路网，文件路径={netpath}")
    roadNet = netiface.roadNet()
    print(f"路网属性netAttrs={netiface.netAttrs()}")
    print(f"===获取当前路网基本信息:  id={roadNet.id()}, netName={roadNet.netName()},url={roadNet.url()},"
          f"路网来源type={roadNet.type()}, 背景图片路径={roadNet.bkgUrl()},其他属性={roadNet.otherAttrs()},"
          f"路网说明={roadNet.explain()}, 路网中心点位置(像素)={roadNet.centerPoint()}，路网中心点位置(米制)={roadNet.centerPoint(UnitOfMeasure.Metric)} ")

    print("===获取场景信息")
    graphicScene = netiface.graphicsScene()
    graphicsView = netiface.graphicsView()
    sceneScale = netiface.sceneScale()
    print(f"像素比={sceneScale}，场景宽度={netiface.sceneWidth()}，"
          # f"高度={netiface.sceneHeight()}, "
          f"背景图={netiface.backgroundMap()},")
```






### 2.2. ISection

路段与连接段的父类接口，方法如下：

 **def gtype(self) -> int: ...**

获取Section类型，GLinkType 或 GConnectorType。在Tessng.pyi / NetItemType类中定义了一批枚举，每一个数值代表路网上一种元素类型。如：GLinkType代表路段、GConnectorType代表连接段。

举例：

```python
# 获取Section类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的类型是{section.gtype()}")
```

 **def isLink(self) -> bool: ...**

是否是路段；TESSNG中基础路网由路段Link和连接段connector构成

举例：

```python
# 获取Section类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    if section.isLink():
        print(f"id为{section.id()}的Section是路段")
    else:
        print(f"id为{section.id()}的Section是连接段")
```

 **def id(self) -> int: ...**

获取ID：如果当前对象是Link，则id是Link的ID；如果是连接段，则id是连接段ID

举例：

```python
# 获取Section的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"Section的ID是{section.id()}")
```

 **def sectionId(self) -> int: ...**

获取ID，如果当前Isection对象是Link，则id是Link的ID；
如果是连接段，则id是连接段ID+10000000（TESSNG内部通过加常数的方式来区分路段与连接段）

举例：

```python
# 获取Section的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的sectionId是{section.sectionId()}")
```

 **def name(self) -> str: ...**

获取Section名称：路段名或连接段名

举例：

```python
# 获取Section名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的名称是{section.name()}")
```

 **def setName(self, name:str) -> None: ...**

设置Section名称

举例：

```python
# 设置Section名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    section.setName(section.name() + str(section.id()))
    print(f"id为{section.id()}的Section的名称是{section.name()}")
```

 **def v3z(self，unit:Tess.UnitOfMeasure) -> float: ...**
获取Section高程，默认单位：像素，可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）
参数：
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取Section高程
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的高程是{section.v3z()}")
    print(f"id为{section.id()}的Section的高程(米制)是{section.v3z(UnitOfMeasure.Metric)}")
```

 **def length(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取Section长度，默认单位：像素，可通过unit参数设置单位
参数：
\[in\]  unit：单位参数，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取Section长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的长度是{section.length()}")
    print(f"id为{section.id()}的Section的长度(米制)是{section.length(UnitOfMeasure.Metric)}")
```

 **def laneObjects(self) -> typing.List: ...**

车道与“车道连接”的父类接口列表

举例：

```python
# 获取Section的车道与“车道连接”的父类接口列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    laneObjects = section.laneObjects()
    for laneObject in laneObjects:
        print(f"id为{section.id()}的Section包含id为{laneObject.id()}的laneObject")
```

 **def fromSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取上游Section。
如果当前Section是路段且id 为 0则 返回空；
否则返回上游指定ID的连接段；
如果当前Section是连接段且id 为 0 返回上游路段，否则返回空。

举例：

```python
# 获取Section的上游Section
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    if section.gtype() == NetItemType.GConnectorType:
        print(f"id为{section.id()}的Section的上游Section是{section.fromSection(0)}")
```

 **def toSection(self, id:int=...) -> Tessng.ISection: ...**

根据ID获取下游 Section。如果当前section是路段且 id 为 0则 返回空，否则返回下游指定ID的连接段；
如果当前section是连接段且id 为 0 则返回下游路段，否则返回空。

举例：

```python
# 获取Section的下游Section
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    if section.gtype() == NetItemType.GConnectorType:
        print(f"id为{section.id()}的Section的下游Section是{section.toSection(0)}")
```

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置路段或连接段其它属性；这些属性可以用户自定义，类型为字典，方便用户做二次开发时扩充属性

举例：

```python
# 设置Section的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    section.setOtherAttr({'newAttr': 'add a new attr'})
```

 **def castToLink(self) -> Tessng.ILink: ...**

将当前Section转换成其子类ILink，如果当前Section是连接段则返回空

举例：

```python
# 将当前Section转换成其子类ILink
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section转换成ILink后是{section.castToLink()}")
```

 **def castToConnector(self) -> Tessng.IConnector: ...**

将当前Section转换成其子类转换成IConnector，如果当前Section为路段Link则返回空

举例：

```python
# 将当前Section转换成其子类转换成IConnector
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section转换成IConnector后是{section.castToConnector()}")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取Section的轮廓， 轮廓由section的一系列顶点组成

举例：

```python
# 获取Section的轮廓
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
sections = netiface.sections()
for section in sections:
    print(f"id为{section.id()}的Section的轮廓是{section.polygon()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showSectionAttr(netiface)

def showSectionAttr(netiface):
    print(
        f"===场景中的section个数（路段与连接段的父类对象）={len(netiface.sections())}，第一个section的属性={netiface.sections()[0].id()}")
    section = netiface.sections()[0]
    for s in netiface.sections():
        if s.id()==135 and s.isLink():
            print(f"link={s.id()}, v3z={s.v3z()},m2p_v3z={m2p(s.v3z())},p2m_v3z={p2m(s.v3z())}v3z_m={s.v3z(UnitOfMeasure.Metric)}")
            print(
                f"link={s.id()}, v3z={s.v3z()},m2p_v3z={m2p(s.v3z())},p2m_v3z={p2m(s.v3z())}v3z_m={s.v3z(UnitOfMeasure.Metric)}")

            print(f"该section的属性：id(linkid or connectorid)={section.id()}, 类型gtype={section.gtype()}, "
                  f"是否为link={section.isLink()}, sectionId={section.sectionId()}, name={section.name()}, 设置新的name={section.setName(section.name() + str(1))},"
                  f"v3z(像素制)={section.v3z()}，v3z(米制)={section.v3z(UnitOfMeasure.Metric)}，长度length（像素制）={section.length()}， 米制={section.length(UnitOfMeasure.Metric)}"
                  f"section下包含的laneObject（lane和lanconnector的父类）={section.laneObjects()}, fromSection={section.fromSection()}, toSection={section.toSection()},"
                  f"设置自定义属性setOtherAttr={section.setOtherAttr({'newAttr': 'add a new attr'})}, 将section强转为子类link={section.castToLink()},"
                  f"将section强转为子类Iconnector={section.castToLink()}, 获取section外轮廓={section.polygon()}")
    print()
    print(f"该section的属性：id(linkid or connectorid)={section.id()}, 类型gtype={section.gtype()}, "
          f"是否为link={section.isLink()}, sectionId={section.sectionId()}, name={section.name()}, 设置新的name={section.setName(section.name() + str(1))},"
          f"v3z(像素制)={section.v3z()}，v3z(米制)={section.v3z(UnitOfMeasure.Metric)}，长度length（像素制）={section.length()}， 米制={section.length(UnitOfMeasure.Metric)}"
          f"section下包含的laneObject（lane和lanconnector的父类）={section.laneObjects()}, fromSection={section.fromSection()}, toSection={section.toSection()},"
          f"设置自定义属性setOtherAttr={section.setOtherAttr({'newAttr': 'add a new attr'})}, 将section强转为子类link={section.castToLink()},"
          f"将section强转为子类Iconnector={section.castToLink()}, 获取section外轮廓={section.polygon()}")
```







### 2.3 ILaneObject 

车道与车道连接的父类接口，方法如下：

 **def gtype(self) -> int: ...**

类型，GLaneType或GLaneConnectorType

举例：

```python
# 获取ILaneObject的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有Section
lSections = netiface.sections()
for section in lSections:
    # 获取路网中的所有ILaneObject
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        print(f"id为{laneObject.id()}的ILaneObject的类型是{laneObject.gtype()}")
```

 **def isLane(self) -> bool: ...**

是否车道， 因为也有可能是车道连接

举例：

```python
# 判断ILaneObject是否是车道
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        print(f"id为{laneObject.id()}的ILaneObject是否是车道：{laneObject.isLane()}")
```

 **def id(self) -> int: ...**

获取ID，如果当前self对象是Lane则id是Lane的ID， 如果是车道连接，则id是“车道连接”ID

举例：

```python
# 获取ILaneObject的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        print(f"ILaneObject的ID是{laneObject.id()}")
```

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取车道或“车道连接”长度，默认单位：像素;可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        print(f"id为{laneObject.id()}的ILaneObject的长度是{laneObject.length()}")
        print(f"id为{laneObject.id()}的ILaneObject的长度(米制单位)是{laneObject.length(UnitOfMeasure.Metric)}")
```

 **def section(self) -> Tessng.ISection: ...**

获取所属的ISection

举例：

```python
# 获取ILaneObject所属的ISection
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        print(f"id为{laneObject.id()}的ILaneObject所属的ISection是{laneObject.section()}")
```

 **def fromLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据laneObject ID获取其上游的 LaneObject对象。如果当前laneObject对象是车道,则且id 为 0表示未传入laneObject ID信息，则 返回空；否则返回其上游的“车道连接”；  
如果当前laneObject对象是车道连接且id 为 0，那么 返回其上游车道对象，否则返回空。

举例：

```python
# 获取ILaneObject的上游LaneObject
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        if laneObject.gtype() == NetItemType.GLaneConnectorType:
        print(f"id为{laneObject.id()}的ILaneObject的上游LaneObject是{laneObject.fromLaneObject(0)}")
```

 **def toLaneObject(self, id:int=...) -> Tessng.ILaneObject: ...**

根据ID获取下游 LaneObject。如果当前是车道, id 为 0 返回空，否则返回下游指定ID的“车道连接”；如果当前是连接段，id 为 0 返回下游车道，否则返回空。

举例：

```python
# 获取ILaneObject的下游LaneObject
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        if laneObject.gtype() == NetItemType.GLaneConnectorType:
            print(f"id为{laneObject.id()}的ILaneObject的下游LaneObject是{laneObject.toLaneObject(0)}")
```

 **def centerBreakPoints(self,unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取laneObject的中心线断点列表；即车道或“车道连接”中心线断点集； 断点都是像素坐标下的点  
参数：
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的中心线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lCenterBreakPoints = laneObject.centerBreakPoints()
        for centerBreakPoint in lCenterBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的中心线断点列表是{centerBreakPoint}")
        lCenterBreakPoints = laneObject.centerBreakPoints(UnitOfMeasure.Metric)
        for centerBreakPoint in lCenterBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的中心线断点列表(米制)是{centerBreakPoint}")
```

 **def leftBreakPoints(self,unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取laneObject的左侧边线断点列表； 即车道或“车道连接”左侧线断点集; 断点都是像素坐标下的点  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的左侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lLeftBreakPoints = laneObject.leftBreakPoints()
        for leftBreakPoint in lLeftBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表是{leftBreakPoint}")
        lLeftBreakPoints = laneObject.leftBreakPoints(UnitOfMeasure.Metric)
        for leftBreakPoint in lLeftBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表(米制)是{leftBreakPoint}")
```

 **def rightBreakPoints(self,unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取laneObject的右侧边线断点列表；车道或“车道连接”右侧线断点集; 断点都是像素坐标下的点  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的右侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lRightBreakPoints = laneObject.rightBreakPoints()
        for rightBreakPoint in lRightBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表是{rightBreakPoint}")
        lRightBreakPoints = laneObject.rightBreakPoints(UnitOfMeasure.Metric)
        for rightBreakPoint in lRightBreakPoints:
            print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表(米制)是{rightBreakPoint}")
```

 **def centerBreakPoint3Ds(self,unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取laneObject的右侧边线断点列表；车道或“车道连接”中心线断点(三维)集（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的中心线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lCenterBreakPoint3Ds = laneObject.centerBreakPoint3Ds()
        for centerBreakPoint3D in lCenterBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的中心线断点列表(三维)是{centerBreakPoint3D}")
        lCenterBreakPoint3Ds = laneObject.centerBreakPoint3Ds(UnitOfMeasure.Metric)
        for centerBreakPoint3D in lCenterBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的中心线断点列表(三维，米制)是{centerBreakPoint3D}")
```

 **def leftBreakPoint3Ds(self,unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取laneObject的左侧边线断点列表；车道或“车道连接”左侧线断点(三维)集；（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的左侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lLeftBreakPoint3Ds = laneObject.leftBreakPoint3Ds()
        for leftBreakPoint3D in lLeftBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表(三维)是{leftBreakPoint3D}")
        lLeftBreakPoint3Ds = laneObject.leftBreakPoint3Ds(UnitOfMeasure.Metric)
        for leftBreakPoint3D in lLeftBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表(三维，米制)是{leftBreakPoint3D}")
```

 **def rightBreakPoint3Ds(self,unit:Tess.UnitOfMeasure) -> typing.List: ... **  

获取laneObject的右侧边线断点列表；车道或“车道连接”右侧线断点(三维)集；（包含高程v3z属性的点）除高程是米制单位，x/y均为像素坐标，像素单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的右侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lRightBreakPoint3Ds = laneObject.rightBreakPoint3Ds()
        for rightBreakPoint3D in lRightBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表(三维)是{rightBreakPoint3D}")
        lRightBreakPoint3Ds = laneObject.rightBreakPoint3Ds(UnitOfMeasure.Metric)
        for rightBreakPoint3D in lRightBreakPoint3Ds:
            print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表(三维，米制)是{rightBreakPoint3D}")
```

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF,unit:Tess.UnitOfMeasure) -> typing.List: ...**

通过起终止断点，获取该范围内laneObject的左侧边线断点集；即车道或“车道连接”左侧部分断点(三维)集；入参出参都是像素单位，高程除外为米制单位   
参数：  
\[in\] fromPoint：中心线上某一点作为起点  
\[in\] toPoint：中心线上某一点作为终点  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位    

举例：

```python
# 获取ILaneObject的左侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lLeftBreakPoints = laneObject.leftBreakPoints()
        if len(lLeftBreakPoints) > 2:
            lLeftBreak3Ds = laneObject.leftBreak3DsPartly(lLeftBreakPoints[0],lLeftBreakPoints[2])
            for leftBreak3D in lLeftBreak3Ds:
                print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表(三维)是{leftBreak3D}")
            lLeftBreak3Ds = laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[0],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[2],UnitOfMeasure.Metric)
            for leftBreak3D in lLeftBreak3Ds:
                print(f"id为{laneObject.id()}的ILaneObject的左侧边线断点列表(三维，米制)是{leftBreak3D}")
```

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF,unit:Tess.UnitOfMeasure) -> typing.List: ...**

车道或“车道连接”右侧部分断点(三维)集， 同上  
参数：  
\[in\] fromPoint：中心线上某一点作为起点；QPointF类型，且是像素坐标  
\[in\] toPoint：中心线上某一点作为终点；QPointF类型，且是像素坐标  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

举例：

```python
# 获取ILaneObject的右侧边线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lRightBreakPoints = laneObject.rightBreakPoints()
        if len(lRightBreakPoints) > 2:
            lRightBreak3Ds = laneObject.rightBreak3DsPartly(lRightBreakPoints[0],lRightBreakPoints[2])
            for rightBreak3D in lRightBreak3Ds:
                print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表(三维)是{rightBreak3D}")
            lRightBreak3Ds = laneObject.rightBreak3DsPartly(laneObject.rightBreakPoints(UnitOfMeasure.Metric)[0],laneObject.rightBreakPoints(UnitOfMeasure.Metric)[2],UnitOfMeasure.Metric)
            for rightBreak3D in lRightBreak3Ds:
                print(f"id为{laneObject.id()}的ILaneObject的右侧边线断点列表(三维，米制)是{rightBreak3D}")
```

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF,unit:Tess.UnitOfMeasure) -> float: ...**

中心线上一点到laneObject对象起点的距离； 默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的中心线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        dist = laneObject.distToStartPoint(laneObject.centerBreakPoints()[1])
        print(f"id为{laneObject.id()}的ILaneObject的距中心线起点距离为{dist}")
        dist = laneObject.distToStartPoint(laneObject.centerBreakPoints(UnitOfMeasure.Metric)[1],UnitOfMeasure.Metric)
        print(f"id为{laneObject.id()}的ILaneObject的距中心线起点距离(米制)为{dist}")
```

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=...,unit:Tess.UnitOfMeasure) -> float: ...**

laneObject中心线上一点到起点的距离，像素单位，附加条件是该点所在车道上的分段序号；其中分段是指两个断点之间的部分。往往可以根据当前车辆所在的segmIndex信息，调用该函数，这样比distToStartPoint函数效率要高一些  
参数：  
\[in\] p：当前中心线上点或附近点的坐标; QPointF类型，且是像素坐标  
\[in\] segmIndex：参数p点所在车道上的分段序号; 两个断点组成一个分段，分段序号从0开始，沿着道路方向递增  
\[in\] bOnCentLine：参数p点是否在中心线上  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的中心线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        dist = laneObject.distToStartPointWithSegmIndex(laneObject.centerBreakPoints()[1],1)
        print(f"id为{laneObject.id()}的ILaneObject的距中心线起点距离为{dist}")
        dist = laneObject.distToStartPointWithSegmIndex(laneObject.centerBreakPoints(UnitOfMeasure.Metric)[1],1,True,UnitOfMeasure.Metric)
        print(f"id为{laneObject.id()}的ILaneObject的距中心线起点距离(米制)为{dist}")
```

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int,unit:Tess.UnitOfMeasure) -> bool: ...**

获取沿着行驶方向，距laneObject起点dist距离处的点和该点所属的分段序号； 如果目标点不在中心线上返回False，否则返回True  
参数：  
\[in\] dist：中心线起点向下游延伸的距离， 像素单位  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
\[out\] outPoint：中心线起点向下游延伸dist距离后所在点， 默认单位：像素单位，具体返回单位受unit参数控制  
\[out\] outIndex：中心线起点向下游延伸dist距离后所在分段序号，默认单位：像素单位，具体返回单位受unit参数控制  

举例：

```python
# 路段5最左侧车道向前延伸140米后所在点及分段序号， 返回像素
        link = tessngIFace().netInterface().findLink(5)
        laneObjLeft = link.laneObjects()[-1] 
        outPoint = QPointF()
        outIndex = 0
        dist = m2p(140)
        if laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex) is not None:
    print("路段5最左侧车道向前延伸140米后所在点坐标为：({}, {})，分段序号为：{}".format(outPoint.x(), outPoint.y(), outIndex))

# 路段5最左侧车道向前延伸140米后所在点及分段序号， 返回米制
        link = tessngIFace().netInterface().findLink(5)
        laneObjLeft = link.laneObjects()[-1] 
        outPoint = QPointF()
        outIndex = 0
        dist = 140
        if laneObjLeft.getPointAndIndexByDist(dist, outPoint, outIndex,unit=UnitOfMeasure.Metric()) is not None:
    print("路段5最左侧车道向前延伸140米后所在点坐标为：({}, {})，分段序号为：{}".format(outPoint.x(), outPoint.y(), outIndex))


```

 

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF,unit:Tess.UnitOfMeasure) -> bool: ...**

获取距离中心线起点向下游延伸dist处的点, 如果目标点不在中心线上返回False，否则返回True;默认单位：像素，可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILaneObject的中心线断点列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        outPoint = QPointF()
        dist = laneObject.getPointByDist(50, outPoint)
        print(f"id为{laneObject.id()}的ILaneObject的距离中心线起点向下游延伸的点为{outPoint}")
        dist = laneObject.getPointByDist(50, outPoint,UnitOfMeasure.Metric)
        print(f"id为{laneObject.id()}的ILaneObject的距离中心线起点向下游延伸的点(米制)为{outPoint}")
```

 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道或“车道连接”其它属性； 字典类型

举例：

```python
# 设置ILaneObject的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        laneObject.setOtherAttr({'newAttr':'add a new attr'})
```

 **def castToLane(self) -> Tessng.ILane: ...**

将ILaneObject转换为子类ILane，但如果当前ILaneObject是“车道连接”则转化失败，返回空

举例：

```python
# 将ILaneObject转换为子类ILane
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        lane = laneObject.castToLane()
        print(f"id为{laneObject.id()}的ILaneObject转换为子类ILane为{lane}")
```

 **def castToLaneConnector(self) -> Tessng.ILaneConnector: ...**

将ILaneObject转换为ILaneConnector子类，但如果当前ILaneObject是车道则转化失败，返回空

举例：

```python
# 将ILaneObject转换为ILaneConnector子类
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILaneObject
lSections = netiface.sections()
for section in lSections:
    lLaneObjects = section.laneObjects()
    for laneObject in lLaneObjects:
        laneConnector = laneObject.castToLaneConnector()
        print(f"id为{laneObject.id()}的ILaneObject转换为子类ILaneConnector为{laneConnector}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showLaneObjectAttr(netiface)

def showLaneObjectAttr(self, netiface):
    section = netiface.findLink(netiface.sections()[0].id())
    laneObject = section.laneObjects()[0]
    for i in section.laneObjects():
        if i.number() == 0:
            laneObject = i
            break
    print(laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric))
    print(f"===section中的第一个laneObject(最右侧) id(linkid or connectorid)={laneObject.id()}, 类型gtype={laneObject.gtype()}, "
          f"是否为link={laneObject.isLane()}, 所属section={laneObject.section()},长度length（像素制）={laneObject.length()}， 米制={laneObject.length(UnitOfMeasure.Metric)}"
          f"fromLaneObject={laneObject.fromLaneObject()}, toLaneObject={laneObject.toLaneObject()},"
          f"centerBreakPoints(像素制)={laneObject.centerBreakPoints()},centerBreakPoints(米制)={laneObject.centerBreakPoints(UnitOfMeasure.Metric)},"
          f"leftBreakPoints(像素制)={laneObject.leftBreakPoints()},leftBreakPoints(米制)={laneObject.leftBreakPoints(UnitOfMeasure.Metric)},"
          f"rightBreakPoints(像素制)={laneObject.rightBreakPoints()},rightBreakPoints(米制)={laneObject.rightBreakPoints(UnitOfMeasure.Metric)},"
          f"centerBreakPoint3Ds(像素制)={laneObject.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={laneObject.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreakPoint3Ds(像素制)={laneObject.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={laneObject.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"rightBreakPoint3Ds(像素制)={laneObject.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={laneObject.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreak3DsPartly(像素制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints()[1],laneObject.leftBreakPoints()[-1])},"
          f"leftBreak3DsPartly(米制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"rightBreak3DsPartly(像素制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints()[1],laneObject.leftBreakPoints()[-1])},"
          f"rightBreak3DsPartly(米制)={laneObject.leftBreak3DsPartly(laneObject.leftBreakPoints(UnitOfMeasure.Metric)[1],laneObject.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"distToStartPoint(像素制)={laneObject.distToStartPoint(laneObject.centerBreakPoints()[0])}，distToStartPoint(米制)={laneObject.distToStartPoint(laneObject.centerBreakPoints(UnitOfMeasure.Metric)[0],UnitOfMeasure.Metric)}，"
          f"设置自定义属性setOtherAttr={laneObject.setOtherAttr({'newAttr':'add a new attr'})}, 将section强转为子类link={laneObject.castToLane()},"
          f"将section强转为子类Iconnector={laneObject.castToLaneConnector()}")
    outPoint = QPointF()
    outIndex = 0
    outPoint1 = QPointF()
    outIndex1 = 0
    laneObject.getPointAndIndexByDist(2.0, outPoint, outIndex)
    laneObject.getPointAndIndexByDist(2.0, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"getPointAndIndexByDist(像素制)={outPoint, outIndex}, getPointAndIndexByDist(米制)={outPoint1, outIndex1}")

    outPoint2 = QPointF()
    outPoint3 = QPointF()
    laneObject.getPointByDist(2.0, outPoint2)
    laneObject.getPointByDist(2.0, outPoint3, UnitOfMeasure.Metric)
    print(f"getPointByDist(像素制)={outPoint2}, getPointByDist(米制)={outPoint3}")

```





### 2.4. ILink

路段接口，方法如下：

 **def gtype(self) -> int: ...**

类型，返回GLinkType，TESSNG的一个常量， int类型

举例：

```python
# 获取ILink的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的类型为{link.gtype()}")
```

 **def id(self) -> int: ...**

获取路段ID

举例：

```python
# 获取ILink的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"ILink的ID为{link.id()}")
```

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取路段长度，默认单位：像素，可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILink的长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的长度为{link.length()}")
    print(f"id为{link.id()}的ILink的长度(米制)为{link.length(UnitOfMeasure.Metric)}")
```

 **def width(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取路段宽度，单位像素;可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILink的宽度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的宽度为{link.width()}")
    print(f"id为{link.id()}的ILink的宽度(米制)为{link.width(UnitOfMeasure.Metric)}")
```

 **def z(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取路段高程， 单位 像素，可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号）  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

举例：

```python
# 获取ILink的高程
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的高程为{link.z()}")
    print(f"id为{link.id()}的ILink的高程(米制)为{link.z(UnitOfMeasure.Metric)}")
```

 **def v3z(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取路段高程，过载ISection的方法， 等同于上边的z(self)  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取ILink的高程
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的高程为{link.v3z()}")
    print(f"id为{link.id()}的ILink的高程(米制)为{link.v3z(UnitOfMeasure.Metric)}")
```

 **def name(self) -> str: ...** 

获取路段名称

举例：

```python
# 获取ILink的名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的名称{link.name()}")
```

 **def setName(self, name:str) -> None: ...**

设置路段名称

举例：

```python
# 设置ILink的名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    link.setName('test_name')
    print(f"id为{link.id()}的ILink的名称{link.name()}")
```

 **def linkType(self) -> str: ...**

获取路段类型，出参为字符串枚举：城市主干道、城市次干道、人行道。

举例：

```python
# 获取ILink的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的类型为{link.linkType()}")
```

 **def setType(self, type:str) -> None: ...**

设置路段类型，路段类型有10种，入参可以为：高速路、城市快速路、匝道、城市主要干道、次要干道、地方街道、非机动车道、人行道、公交专用道、机非共享； 其中的任意一个，其他类型暂不支持

举例：

```python
# 设置ILink的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    link.setType('机非共享')
    print(f"id为{link.id()}的ILink的类型为{link.linkType()}")
```

 **def laneCount(self) -> int: ...**

获取车道数

举例：

```python
# 获取ILink的车道数
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的车道数为{link.laneCount()}")
```

 **def limitSpeed(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取路段最高限速，默认单位：千米/小时  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的最高限速
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的最高限速为{link.limitSpeed()}")
    print(f"id为{link.id()}的ILink的最高限速(米制)为{link.limitSpeed(UnitOfMeasure.Metric)}")
```

 **def setLimitSpeed(self, speed:float, unit:Tess.UnitOfMeasure) -> None: ...**

设置最高限速， 单位km/h  
参数：  
\[in\] speed：最高限速，单位：千米/小时  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 设置ILink的最高限速
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    link.setLimitSpeed(link.limitSpeed()*1.2)
    print(f"id为{link.id()}的ILink的最高限速为{link.limitSpeed()}")
    print(f"id为{link.id()}的ILink的最高限速(米制)为{link.limitSpeed(UnitOfMeasure.Metric)}")
```

 **def minSpeed(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取最低限速，单位：千米/小时； 暂不支持用户设置最低限速的API  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的最低限速
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的ILink的最低限速为{link.minSpeed()}")
    print(f"id为{link.id()}的ILink的最低限速(米制)为{link.minSpeed(UnitOfMeasure.Metric)}")
```

 **def lanes(self) -> typing.List: ...**

获取ILink上的车道列表， 列表按照从左到右的顺序排列；列表元素为ILane对象

举例：

```python
# 获取ILink上的车道列表
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    lanes = link.lanes()
    for lane in lanes:
        print(f"id为{lane.id()}的车道对象为{lane}")
```

 **def laneObjects(self) -> typing.List: ...**

获取ILink下所有LaneObject对象，列表类型，LaneObject可以是车道，也可以是“车道连接”的父对象

举例：

```python
# 获取ILink下的所有LaneObject对象
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    laneObjects = link.laneObjects()
    for laneObject in laneObjects:
        print(f"id为{laneObject.id()}的LaneObject对象为{laneObject}")
```

 **def centerBreakPoints(self，unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段中心线断点集， 像素坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的中心线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    centerBreakPoints = link.centerBreakPoints()
    for centerBreakPoint in centerBreakPoints:
        print(f"路段id为{link.id()}的中心线断点为{centerBreakPoint}")
    centerBreakPoints = link.centerBreakPoints(UnitOfMeasure.Metric)
    for centerBreakPoint in centerBreakPoints:
        print(f"路段id为{link.id()}的中心线断点(米制)为{centerBreakPoint}")
```

 **def leftBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段左侧线断点集， 像素坐标   
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的左侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    leftBreakPoints = link.leftBreakPoints()
    for leftBreakPoint in leftBreakPoints:
        print(f"路段id为{link.id()}的左侧线断点为{leftBreakPoint}")
    leftBreakPoints = link.leftBreakPoints(UnitOfMeasure.Metric)
    for leftBreakPoint in leftBreakPoints:
        print(f"路段id为{link.id()}的左侧线断点(米制)为{leftBreakPoint}")
```

 **def rightBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段右侧线断点集， 像素坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的右侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    rightBreakPoints = link.rightBreakPoints()
    for rightBreakPoint in rightBreakPoints:
        print(f"路段id为{link.id()}的右侧线断点为{rightBreakPoint}")
    rightBreakPoints = link.rightBreakPoints(UnitOfMeasure.Metric)
    for rightBreakPoint in rightBreakPoints:
        print(f"路段id为{link.id()}的右侧线断点(米制)为{rightBreakPoint}")
```

 **def centerBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段中心线断点(三维)集， 像素坐标，但高程z的单位为米  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的中心线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    centerBreakPoint3Ds = link.centerBreakPoint3Ds()
    for centerBreakPoint3D in centerBreakPoint3Ds:
        print(f"路段id为{link.id()}的中心线断点(三维)为{centerBreakPoint3D}")
    centerBreakPoint3Ds = link.centerBreakPoint3Ds(UnitOfMeasure.Metric)
    for centerBreakPoint3D in centerBreakPoint3Ds:
        print(f"路段id为{link.id()}的中心线断点(三维)(米制)为{centerBreakPoint3D}")
```

 **def leftBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段左侧线断点(三维)集, 像素坐标，但高程z的单位为米  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的左侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    leftBreakPoint3Ds = link.leftBreakPoint3Ds()
    for leftBreakPoint3D in leftBreakPoint3Ds:
        print(f"路段id为{link.id()}的左侧线断点(三维)为{leftBreakPoint3D}")
    leftBreakPoint3Ds = link.leftBreakPoint3Ds(UnitOfMeasure.Metric)
    for leftBreakPoint3D in leftBreakPoint3Ds:
        print(f"路段id为{link.id()}的左侧线断点(三维)(米制)为{leftBreakPoint3D}")
```

 **def rightBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取路段右侧线断点(三维)集， 像素坐标，但高程z的单位为米  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink的右侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    rightBreakPoint3Ds = link.rightBreakPoint3Ds()
    for rightBreakPoint3D in rightBreakPoint3Ds:
        print(f"路段id为{link.id()}的右侧线断点(三维)为{rightBreakPoint3D}")
    rightBreakPoint3Ds = link.rightBreakPoint3Ds(UnitOfMeasure.Metric)
    for rightBreakPoint3D in rightBreakPoint3Ds:
        print(f"路段id为{link.id()}的右侧线断点(三维)(米制)为{rightBreakPoint3D}")
```

 **def fromConnectors(self) -> typing.List: ...**

获取ILink的上游连接段， 其可能有多个，所以返回类型为列表，列表元素为IConnector对象

举例：

```python
# 获取ILink的上游连接段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    fromConnectors = link.fromConnectors()
    for fromConnector in fromConnectors:
        print(f"路段id为{link.id()}的上游连接段之一为{fromConnector.id()}")
```

 **def toConnectors(self) -> typing.List: ...**

获取ILink的下游连接段， 其可能有多个，所以返回类型为列表，列表元素为IConnector对象

举例：

```python
# 获取ILink的下游连接段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    toConnectors = link.toConnectors()
    for toConnector in toConnectors:
        print(f"路段id为{link.id()}的下游连接段之一为{toConnector.id()}")
```

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置路段的其它属性， TESSNG仿真过程中仅记录拓展的属性，方便用户拓展，并自定义使用

举例：

```python
# 设置ILink的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    link.setOtherAttr({'new_msg':'test'})
    print(f"路段id为{link.id()}的其它属性为{link.otherAttr()}")
```

 **def otherAttr(self) -> typing.Dict: ...**

获取路段的其它属性， TESSNG仿真过程中仅记录拓展的属性，方便用户拓展，并自定义使用

举例：

```python
# 获取ILink的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"路段id为{link.id()}的其它属性为{link.otherAttr()}")
```

 **def setLaneTypes(self, lType:typing.Sequence) -> None: ...**

依次为ILink下所有车道设置车道属性（列表顺序为 从左到右的车道顺序），入参为序列类型（列表，元组等），其中元素的类型从这四种常量字符串中获取："机动车道"、"机非共享"、"非机动车道"、"公交专用道"

举例：

```python
# 依次为ILink下所有车道设置车道属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    if link.laneCount() == 3:
        link.setLaneTypes(['机动车道','非机动车道','公交专用道'])
```

 **def setLaneOtherAtrrs(self, lAttrs:typing.Sequence) -> None: ...**

依次为ILink下所有车道设置车道其它属性

举例：

```python
# 依次为ILink下所有车道设置车道其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    if link.laneCount() == 3:
        link.setLaneOtherAtrrs([{'new_name':'自定义机动车道'},{'new_name':'自定义非机动车道'},{'new_name':'自定义公交专用道'}])
```

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> float: ...**

ILink中心线上任意一点到ILink起点的距离， 像素单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILink中心线上任意一点到ILink起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的路段的中心线到起点的距离为{link.distToStartPoint(link.centerBreakPoints()[1])}")
    print(f"id为{link.id()}的路段的中心线到起点的距离(米制单位){link.distToStartPoint(link.centerBreakPoints(UnitOfMeasure.Metric)[1],UnitOfMeasure.Metric)}")
```

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int, unit:Tess.UnitOfMeasure) -> bool: ...**

获取ILink中心线起点下游dist距离处的点及其所属分段序号, 如果目标点不在中心线上返回False，否则返回True  
参数：  
\[in\] dist：中心线起点向下游延伸的距离  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  
[out] outPoint：中心线起点向下游延伸dist距离后所在点  
[out] outIndex：中心线起点向下游延伸dist处的点所属分段序号

举例：

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
    print(f"id为{link.id()}的路段的中心线起点向下游延伸dist米处的点为{outPoint}，所属分段序号为{outIndex}")
    outPoint1 = QPointF()
    outIndex1 = 0
    link.getPointAndIndexByDist(50, outPoint1, outIndex1, UnitOfMeasure.Metric)
    print(f"id为{link.id()}的路段的中心线起点向下游延伸dist米处的点(米制单位)为{outPoint1}，所属分段序号为{outIndex1}")
```

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> bool: ...**

求ILink中心线起点向前延伸dist距离后所在点, 如果目标点不在中心线上返回False，否则返回True  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

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

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取路段的多边型轮廓， 返回值类型QPolygonF， 像素坐标

举例：

```python
# 获取ILink的多边型轮廓
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILink
lLinks = netiface.links()
for link in lLinks:
    print(f"id为{link.id()}的路段的多边型轮廓为{link.polygon()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showLinkAttr(netiface)

def showLinkAttr(netiface):
    print(f"===场景中的link总数={netiface.linkCount()}，第一个link的id={netiface.linkIds()[0]}")
    link = netiface.findLink(netiface.linkIds()[0])
    link1 = netiface.links()[0]
    print(link1)
    print(f"该link的属性：id={link.id()}, ")
    print(f"link.fromConnectors()={link.fromConnectors()}")
    print(f"该link的最右侧车道为：id={link.id()}, 其属性为：路段类型={link.gtype()}, 路段长度（像素制）={link.length()}，米制={link.length(UnitOfMeasure.Metric)},"
          f"宽度（像素制）={link.width()}，米制={link.width(UnitOfMeasure.Metric)}， 高程（像素制）={link.z()}， 米制={link.z(UnitOfMeasure.Metric)},"
          f"高程v3z(像素制)={link.v3z()},米制={link.v3z(UnitOfMeasure.Metric)}, 设置新名字={link.setName('test_name')}name={link.name()},linkType={link.linkType()},"
          f"设置路段类型为城市次干道={link.setType('次要干道')}，再次获取城市类型={link.linkType()}, 车道数={link.laneCount()},"
          f"路段最高限速(像素制)={link.limitSpeed()}， 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)},路段最低限速(像素制)={link.minSpeed()}， 米制（km/h）={link.minSpeed(UnitOfMeasure.Metric)},"
          f"将路段最高限速提高百分之20={link.setLimitSpeed(link.limitSpeed()*1.2)} or {link.setLimitSpeed(link.limitSpeed(UnitOfMeasure.Metric)*1.2, UnitOfMeasure.Metric)},"
          f"路段最高限速(像素制)={link.limitSpeed()}， 米制（km/h）={link.limitSpeed(UnitOfMeasure.Metric)}, 路段包含的车道对象={link.lanes()},"
          f"路段包含的laneObject对象={link.laneObjects()},"
          f"路段中心线（像素制）={link.centerBreakPoints()},米制={link.centerBreakPoints(UnitOfMeasure.Metric)},"
          f"路段左侧线（像素制）={link.leftBreakPoints()},米制={link.leftBreakPoints(UnitOfMeasure.Metric)},"
          f"路段右侧线（像素制）={link.rightBreakPoints()},米制={link.rightBreakPoints(UnitOfMeasure.Metric)},"
          f"路段中心线3D（像素制）={link.centerBreakPoint3Ds()},米制={link.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"路段左侧线3D（像素制）={link.leftBreakPoint3Ds()},米制={link.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"路段右侧线3D（像素制）={link.rightBreakPoint3Ds()},米制={link.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"fromConnector={link.fromConnectors()}, toConnectors={link.toConnectors()},"
          f"fromSection={link.fromConnectors()[0].id() if link.fromConnectors() is not None and len(link.fromConnectors()) > 0 else 0},"
          f"toSection={link.toSection(link.toConnectors()[0].id() if link.toConnectors() is not None and len(link.toConnectors() )> 0 else 0)}, "
          f"自定义其他属性： setOtherAttr={link.setOtherAttr({'new_msg':'this is a av car'})},"
          f"从右到左依次为车道设置类别={link.setLaneTypes(['公交专用道','机动车道','机动车道'])}，为车道设置其他属性={link.setLaneOtherAtrrs([{'new_name':'自定义公交专用车道'},{'new_name':'自定义机动车道'},{'new_name':'自定义机动车道'}])}，"
          f"distToStartPoint距离起点长度（像素制）={link.distToStartPoint(link.centerBreakPoints()[-1])}, 米制={link.distToStartPoint(link.centerBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
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

车道接口，方法如下：

 **def gtype(self) -> int: ...**

类型，车道类型为GLaneType，其中GLaneType是一种常量

举例：

```python
# 获取ILane的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILane
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的类型为{lane.gtype()}")
```

 **def id(self) -> int: ...**

获取车道ID

举例：

```python
# 获取ILane的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILane
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取车道所属路段，返回路段对象

举例：

```python
# 获取ILane所属路段
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道所属路段为{lane.link()}")
```

 **def section(self) -> Tessng.ISection: ...**

获取车道所属Section，返回Section对象，其为ILink的父对象

举例：

```python
# 获取ILane所属Section
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道所属Section为{lane.section()}")
```

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取车道长度，默认单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILane
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道长度为{lane.length()}")
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道长度(米制单位)为{lane.length(UnitOfMeasure.Metric)}")
```

 **def width(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取车道宽度，默认单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的宽度
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道宽度为{lane.width()}")
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道宽度(米制单位)为{lane.width(UnitOfMeasure.Metric)}")
```

 **def number(self) -> int: ...**

获取车道序号，从0开始（自外侧往内侧，即自左向右依次编号）

举例：

```python
# 获取ILane的序号
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道序号为{lane.number()}")
```

 **def actionType(self) -> str: ...**

获取车道的行为类型，返回的为行为类型常量字符串，包括："机动车道"、“非机动车道”、“公交专用道”

举例：

```python
# 获取ILane的行为类型
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道行为类型为{lane.actionType()}")
```

**def  fromLaneConnectors() ->typing.List: ...**

获取上游车道连接列表

举例：

```python
# 获取ILane的上游车道连接列表
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lLaneConnectors = lane.fromLaneConnectors()
        for laneConnector in lLaneConnectors:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道上车道连接列表为{laneConnector.id()}")
```

**def toLaneConnectors()->typing.List: ...**

获取下游车道连接列表

举例：

```python
# 获取ILane的下游车道连接列表
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lLaneConnectors = lane.toLaneConnectors()
        for laneConnector in lLaneConnectors:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道下游车道连接列表为{laneConnector.id()}")
```

 **def centerBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道中心点断点集，断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的中心点断点集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lCenterBreakPoints = lane.centerBreakPoints()
        for centerBreakPoint in lCenterBreakPoints:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心点断点集为{centerBreakPoint}")
        lCenterBreakPointsMeter = lane.centerBreakPoints(UnitOfMeasure.Metric)
        for centerBreakPointMeter in lCenterBreakPointsMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心点断点集(米制单位)为{centerBreakPointMeter}")
```

 **def leftBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道左侧线断点集,断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的左侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lLeftBreakPoints = lane.leftBreakPoints()
        for leftBreakPoint in lLeftBreakPoints:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧线断点集为{leftBreakPoint}")
        lLeftBreakPointsMeter = lane.leftBreakPoints(UnitOfMeasure.Metric)
        for leftBreakPointMeter in lLeftBreakPointsMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧线断点集(米制单位)为{leftBreakPointMeter}")
```

 **def rightBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道右侧线断点集,断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的右侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lRightBreakPoints = lane.rightBreakPoints()
    for rightBreakPoint in lRightBreakPoints:
        print(f"id为{lane.id()}的车道的右侧线断点集为{rightBreakPoint}")
    lRightBreakPointsMeter = lane.rightBreakPoints(UnitOfMeasure.Metric)
    for rightBreakPointMeter in lRightBreakPointsMeter:
        print(f"id为{lane.id()}的车道的右侧线断点集(米制单位)为{rightBreakPointMeter}")
```

 **def centerBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道中心线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的中心线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lCenterBreakPoint3Ds = lane.centerBreakPoint3Ds()
        for centerBreakPoint3D in lCenterBreakPoint3Ds:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线断点(三维)集为{centerBreakPoint3D}")
        lCenterBreakPoint3DsMeter = lane.centerBreakPoint3Ds(UnitOfMeasure.Metric)
        for centerBreakPoint3DMeter in lCenterBreakPoint3DsMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线断点(三维)集(米制单位)为{centerBreakPoint3DMeter}")
```

 **def leftBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道左侧线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的左侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lLeftBreakPoint3Ds = lane.leftBreakPoint3Ds()
        for leftBreakPoint3D in lLeftBreakPoint3Ds:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧线断点(三维)集为{leftBreakPoint3D}")
        lLeftBreakPoint3DsMeter = lane.leftBreakPoint3Ds(UnitOfMeasure.Metric)
        for leftBreakPoint3DMeter in lLeftBreakPoint3DsMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧线断点(三维)集(米制单位)为{leftBreakPoint3DMeter}")
```

 **def rightBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取车道右侧线断点(三维)集， 断点坐标用像素表示，其中高程z用单位米表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的右侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
    lRightBreakPoint3Ds = lane.rightBreakPoint3Ds()
        for rightBreakPoint3D in lRightBreakPoint3Ds:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的右侧线断点(三维)集为{rightBreakPoint3D}")
        lRightBreakPoint3DsMeter = lane.rightBreakPoint3Ds(UnitOfMeasure.Metric)
        for rightBreakPoint3DMeter in lRightBreakPoint3DsMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的右侧线断点(三维)集(米制单位)为{rightBreakPoint3DMeter}")
```

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据指定起终点断点，获取车道左侧部分断点(三维)集，  断点坐标用像素表示，其中高程z用单位米表示  

参数：  
\[in\] fromPoint：中心线上某一点作为起点， 像素坐标，其中高程z用单位米表示   
\[in\] toPoint：中心线上某一点作为终点， 像素坐标，其中高程z用单位米表示  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的左侧部分断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lLeftBreakPoints = lane.leftBreakPoints()
    if len(lLeftBreakPoints) > 2:
        lLeftBreak3DsPartly = lane.leftBreak3DsPartly(lane.leftBreakPoints()[0], lane.leftBreakPoints()[2])
        for leftBreak3DPartly in lLeftBreak3DsPartly:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧部分断点(三维)集为{leftBreak3DPartly}")
        lLeftBreak3DsPartlyMeter = lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[0], lane.leftBreakPoints(UnitOfMeasure.Metric)[2], UnitOfMeasure.Metric)
        for leftBreak3DPartlyMeter in lLeftBreak3DsPartlyMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的左侧部分断点(三维)集(米制单位)为{leftBreak3DPartlyMeter}")
```

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据指定起终点断点，获取车道右侧部分断点(三维)集,  断点坐标用像素表示，其中高程z用单位米表示

参数：  
\[in\] fromPoint：中心线上某一点作为起点，像素坐标，其中高程z用单位米表示  
\[in\] toPoint：中心线上某一点作为终点，像素坐标，其中高程z用单位米表示  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  

举例：

```python
# 获取ILane的右侧部分断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lRightBreakPoints = lane.rightBreakPoints()
    if len(lRightBreakPoints) > 2:
        lRightBreak3DsPartly = lane.rightBreak3DsPartly(lane.rightBreakPoints()[0], lane.rightBreakPoints()[2])
        for rightBreak3DPartly in lRightBreak3DsPartly:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的右侧部分断点(三维)集为{rightBreak3DPartly}")
        lRightBreak3DsPartlyMeter = lane.rightBreak3DsPartly(lane.rightBreakPoints(UnitOfMeasure.Metric)[0], lane.rightBreakPoints(UnitOfMeasure.Metric)[2], UnitOfMeasure.Metric)
        for rightBreak3DPartlyMeter in lRightBreak3DsPartlyMeter:
            print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的右侧部分断点(三维)集(米制单位)为{rightBreak3DPartlyMeter}")
```

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> float: ...**

获取中心线上一点到起点的距离， 单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的中心线上一点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lDistToStartPoint = lane.distToStartPoint(lane.centerBreakPoints()[1])
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线上一点到起点的距离为{lDistToStartPoint}")
        lDistToStartPointMeter = lane.distToStartPoint(lane.centerBreakPoints(UnitOfMeasure.Metric)[1], UnitOfMeasure.Metric)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线上一点到起点的距离(米制单位)为{lDistToStartPointMeter}")
```

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=..., unit:Tess.UnitOfMeasure) -> float: ...**

根据中心线上任意点所处的车道分段号和该点本身信息，计算该点到车道起点的距离

参数：  
\[in\] p：当前中心线上的点坐标，像素单位  
\[in\] segmIndex：该点所在车道上的分段序号  
\[in\] bOnCentLine：该点是否在中心线上  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取ILane的中心线上一点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lDistToStartPoint = lane.distToStartPointWithSegmIndex(lane.centerBreakPoints()[1], 1)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线上一点到起点的距离为{lDistToStartPoint}")
        lDistToStartPointMeter = lane.distToStartPointWithSegmIndex(lane.centerBreakPoints(UnitOfMeasure.Metric)[1], 1, True, UnitOfMeasure.Metric)
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的中心线上一点到起点的距离(米制单位)为{lDistToStartPointMeter}")
```

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int, unit:Tess.UnitOfMeasure) -> bool: ...**

获取车道中心线起点下游dist距离处的点及其所属分段序号；  
如果目标点不在中心线上返回False，否则返回True  

参数：  
\[in\] dist：中心线起点向前延伸的距离  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  
\[out\] outPoint：中心线起点向前延伸dist距离后所在点  
\[out\] outIndex：中心线起点向前延伸dist距离后所在分段序号 

举例：

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

 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> bool: ...**

获取车道中心线起点下游dist距离处的点；  
如果目标点不在中心线上返回False，否则返回True  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

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

 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道的其它属性，方便用户拓展车道属性； 类型： 字典形式

举例：

```python
# 设置ILane的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lane.setOtherAttr({'newAttr':'add a new attr'})
```

 **def setLaneType(self, type:str) -> None: ...**

设置车道的类型； 车道类型常量范围："机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

参数：

\[in\] type：车道类型，选下列几种类型其中一种："机动车道"、"机非共享"、"非机动车道"、 "公交专用道"

举例：

```python
# 设置ILane的类型
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        lane.setLaneType('机动车道')
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取车道的多边型轮廓顶点, 像素坐标

举例：

```python
# 获取ILane的多边型轮廓顶点
iface = tessngIFace()
netiface = iface.netInterface()
lLinks = netiface.links()
for link in lLinks:
    lLanes = link.lanes()
    for lane in lLanes:
        print(f"路段id为{link.id()}的车道id为{lane.id()}的车道的多边型轮廓顶点为{lane.polygon()}")
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
          f"是否为lane={lane.isLane()}, 所属link={lane.link()}, 所属section={lane.section()},长度length（像素制）={lane.length()}， 米制={lane.length(UnitOfMeasure.Metric)},"
          f"宽度width（像素制）={lane.width()}， 米制={lane.width(UnitOfMeasure.Metric)}, 车道序号number={lane.number()},行为类型={lane.actionType()}"
          f"fromLaneObject={lane.fromLaneObject()}, toLaneObject={lane.toLaneObject()},"
          f"centerBreakPoints(像素制)={lane.centerBreakPoints()},centerBreakPoints(米制)={lane.centerBreakPoints(UnitOfMeasure.Metric)},"
          f"leftBreakPoints(像素制)={lane.leftBreakPoints()},leftBreakPoints(米制)={lane.leftBreakPoints(UnitOfMeasure.Metric)},"
          f"rightBreakPoints(像素制)={lane.rightBreakPoints()},rightBreakPoints(米制)={lane.rightBreakPoints(UnitOfMeasure.Metric)},"
          f"centerBreakPoint3Ds(像素制)={lane.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={lane.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreakPoint3Ds(像素制)={lane.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={lane.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"rightBreakPoint3Ds(像素制)={lane.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={lane.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1],lane.leftBreakPoints()[-1])},"
          f"leftBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1],lane.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"rightBreak3DsPartly(像素制)={lane.leftBreak3DsPartly(lane.leftBreakPoints()[1],lane.leftBreakPoints()[-1])},"
          f"rightBreak3DsPartly(米制)={lane.leftBreak3DsPartly(lane.leftBreakPoints(UnitOfMeasure.Metric)[1],lane.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"distToStartPoint(像素制)={lane.distToStartPoint(lane.centerBreakPoints()[0])}，distToStartPoint(米制)={lane.distToStartPoint(lane.centerBreakPoints(UnitOfMeasure.Metric)[0],UnitOfMeasure.Metric)}，"
          f"设置自定义属性setOtherAttr={lane.setOtherAttr({'newAttr':'add a new attr'})}, setLaneType={lane.setLaneType('机动车道')},action Type={lane.actionType()}"
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

连接段接口，方法如下：

 **def gtype(self) -> int: ...**

类型，连接段类型为GConnectorType，GConnectorType是一种整数型常量。

举例：

```python
# 获取IConnector的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的类型为{connector.gtype()}")
```

 **def id(self) -> int: ...**

获取连接段ID； 因为连接段ID和路段ID是相互独立的，所以可能两者的ID之间会有重复

举例：

```python
# 获取IConnector的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"连接段的ID为{connector.id()}")
```

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取连接段长度，像素单位；可通过可选参数：unit设置单位，（或者用户也可以根据需求通过m2p转成米制单位坐标，并注意y轴的正负号  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取IConnector的长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的长度为{connector.length()}")
    print(f"id为{connector.id()}的连接段的长度(米制单位)为{connector.length(UnitOfMeasure.Metric)}")
```

 **def z(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取连接段高程，单位：米； ，默认单位：像素，可通过unit参数设置单位

举例：

```python
# 获取IConnector的高程
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的高程为{connector.z()}")
    print(f"id为{connector.id()}的连接段的高程(米制单位)为{connector.z(UnitOfMeasure.Metric)}")
```

 **def v3z(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取连接段高程，过载自ISection的方法， 单位，像素；其效果等同于z()函数 ，默认单位：像素，可通过unit参数设置单位

举例：

```python
# 获取IConnector的高程
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的高程为{connector.v3z()}")
    print(f"id为{connector.id()}的连接段的高程(米制单位)为{connector.v3z(UnitOfMeasure.Metric)}")
```

 **def name(self) -> str: ...**

获取连接段名称

举例：

```python
# 获取IConnector的名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的名称{connector.name()}")
```

 **def setName(self, name:str) -> None: ...**

设置连接段名称

举例：

```python
# 设置IConnector的名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    connector.setName('new connector')
```

 **def fromLink(self) -> Tessng.ILink: ...**

获取当前connector的起始路段， 返回路段对象

举例：

```python
# 获取IConnector的起始路段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的起始路段为{connector.fromLink()}")
```

 **def toLink(self) -> Tessng.ILink: ...**

获取当前connector的目标路段（出口路段）， 返回路段对象

举例：

```python
# 获取IConnector的目标路段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的目标路段为{connector.toLink()}")
```

 **def limitSpeed(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取连接器的最高限速，因为连接器没有最高限速这一属性，因此该函数返回连接器的起始路段最高限速作为连接段的最高限速, 单位 km/h
参数：
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  这个本身返回的就是km/h把，

举例：

```python
# 获取IConnector的最高限速
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的最高限速为{connector.limitSpeed()}")
    print(f"id为{connector.id()}的连接段的最高限速(米制单位)为{connector.limitSpeed(UnitOfMeasure.Metric)}")
```

 **def minSpeed(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取连接器的最低限速，因为连接器没有最低限速这一属性，因此返回连接器起始路段的最低限速作为连接段的最低限速， 单位 km/h
参数：
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制 ？？？ 这个本身返回的就是km/h把，

举例：

```python
# 获取IConnector的最低限速
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的最低限速为{connector.minSpeed()}")
    print(f"id为{connector.id()}的连接段的最低限速(米制单位)为{connector.minSpeed(UnitOfMeasure.Metric)}")
```

 **def laneConnectors(self) -> typing.List: ...**

获取连接器下的所有“车道连接”对象， 列表形式，列表元素为ILaneConnector对象

举例：

```python
# 获取IConnector的车道连接
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的车道连接为{connector.laneConnectors()}")
```

 **def laneObjects(self) -> typing.List: ...**

车道及“车道连接”的接口列表

举例：

```python
# 获取IConnector的车道连接
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的车道连接为{connector.laneObjects()}")
```

 **def setLaneConnectorOtherAtrrs(self, lAttrs:typing.Sequence) -> None: ...**

设置包含的“车道连接”其它属性

举例：

```python
# 设置IConnector的车道连接其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    connector.setLaneConnectorOtherAtrrs([{'newAttr':i} for i in range(len(connector.laneConnectors()))])
```

 **def setOtherAttr(self, otherAttr:typing.Dict) -> None: ...**

设置连接段其它属性

举例：

```python
# 设置IConnector的其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    connector.setOtherAttr({'newAttr':'add a new attr'})
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取连接段的多边型轮廓顶点

举例：

```python
# 获取IConnector的多边型轮廓顶点
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    print(f"id为{connector.id()}的连接段的多边型轮廓顶点为{connector.polygon()}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showConnectorAttr(netiface)
def showConnectorAttr(netiface):
    print(
        f"===场景中的connector个数（连接段对象）={len(netiface.connectors())},{netiface.connectorCount()}, {len(netiface.connectorIds())}，"
        f"第一个connector的属性={netiface.connectors()[0].id()}")
    connector = netiface.connectors()[0]
    connector1 = netiface.findConnector(netiface.connectorIds()[0])
    print(type(connector), dir(connector))
    print(f"该connectors的属性：id(连接段和路段ID是独立的，因此两者ID可能会重复)={connector.id()}, 类型gtype={connector.gtype()},"
          f"name={connector.name()}, setName={connector.setName('new connector')} "
          f"长度length（像素制）={connector.length()}， 米制={connector.length(UnitOfMeasure.Metric)},"
          f"高程={connector.z()}, toLaneObject={connector.v3z()},"
          f"fromLink={connector.fromLink()}, toLink={connector.toLink()},fromSection={connector.fromSection(id=0)}, toSection={connector.toSection(id=0)},"
          f"最高限速(像素制)={connector.limitSpeed()},最高限速(米制)={connector.limitSpeed(UnitOfMeasure.Metric)},"
          f"最低限速(像素制)={connector.minSpeed()},最低限速(米制)={connector.minSpeed(UnitOfMeasure.Metric)},"
          f"laneConnectors={connector.laneConnectors()},laneObjects={connector.laneObjects()},"
          f"设置自定义属性setLaneConnectorOtherAtrrs={connector.setLaneConnectorOtherAtrrs([{'newAttr':i} for i in range(len(connector.laneConnectors()))])},"
          f"设置自定义属性setOtherAttr={connector.setOtherAttr({'newAttr':'add a new attr'})},"
          f"polygon={connector.polygon()}")
```







### 2.7. ILaneConnector

“车道连接”接口，方法如下：

 **def gtype(self) -> int: ...**

类型，GLaneType或GLaneConnectorType，车道连接段为GLaneConnectorType ，这里的返回值只可能是GLaneConnectorType

举例：

```python
# 获取IConnector的类型
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的类型为{laneConnector.gtype()}")
```

 **def id(self) -> int: ...**

获取车道连接ID

举例：

```python
# 获取车道连接ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的ID为{laneConnector.id()}")
```

 **def connector(self) -> Tessng.IConnector: ...**

获取车道连接所属的连接段Connector对象, 返回类型IConnector

举例：

```python
# 获取车道连接所属的连接段Connector对象
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}所属的连接段Connector对象为{laneConnector.connector()}")
```

 **def section(self) -> Tessng.ISection: ...**

获取车道所属Section, Section为 IConnector的父类

举例：

```python
# 获取车道连接所属的Section
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}所属的Section为{laneConnector.section()}")
```

 **def fromLane(self) -> Tessng.ILane: ...**

获取当前车道链接的上游车道对象

举例：

```python
# 获取车道连接的上游车道对象
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的上游车道对象为{laneConnector.fromLane()}")
```

 **def toLane(self) -> Tessng.ILane: ...**

获取当前车道链接的下游车道对象

举例：

```python
# 获取车道连接的下游车道对象
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的下游车道对象为{laneConnector.toLane()}")
```

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取“车道连接”的长度，单位：像素， 是指中心线的长度  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的长度
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的长度为{laneConnector.length()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的长度(米制单位)为{laneConnector.length(UnitOfMeasure.Metric)}")
```

 **def centerBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”的中心线断点集，断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的中心线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线断点集为{laneConnector.centerBreakPoints()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线断点集(米制单位)为{laneConnector.centerBreakPoints(UnitOfMeasure.Metric)}")
```

 **def leftBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”左侧线断点集，断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的左侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的左侧线断点集为{laneConnector.leftBreakPoints()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的左侧线断点集(米制单位)为{laneConnector.leftBreakPoints(UnitOfMeasure.Metric)}")
```

 **def rightBreakPoints(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”右侧线断点集，断点坐标用像素表示  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的右侧线断点集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的右侧线断点集为{laneConnector.rightBreakPoints()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的右侧线断点集(米制单位)为{laneConnector.rightBreakPoints(UnitOfMeasure.Metric)}")
```

 **def centerBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”中心线断点(三维)集，断点坐标用像素表示， 高程Z单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的中心线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线断点(三维)集为{laneConnector.centerBreakPoint3Ds()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线断点(三维)集(米制单位)为{laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric)}")
```

 **def leftBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”左侧线断点(三维)集，断点坐标用像素表示， 高程Z单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的左侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的左侧线断点(三维)集为{laneConnector.leftBreakPoint3Ds()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的左侧线断点(三维)集(米制单位)为{laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric)}")
```

 **def rightBreakPoint3Ds(self, unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取“车道连接”右侧线断点(三维)集，断点坐标用像素表示， 高程Z单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的右侧线断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的右侧线断点(三维)集为{laneConnector.rightBreakPoint3Ds()}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的右侧线断点(三维)集(米制单位)为{laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric)}")
```

 **def leftBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据指定的起终止点获取“车道连接”左侧部分断点(三维)集，断点坐标用像素表示， 高程Z单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 获取车道连接的左侧部分断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接的左侧部分断点(三维)集为{laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[0], laneConnector.leftBreakPoints()[-1])}")
        print(f"id为{connector.id()}的连接段的车道连接的左侧部分断点(三维)集(米制单位)为{laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[0], laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}")
```

 **def rightBreak3DsPartly(self, fromPoint:PySide2.QtCore.QPointF, toPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据指定的起终止点获取“车道连接”右侧部分断点(三维)集，断点坐标用像素表示， 高程Z单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制 

举例：

```python
# 获取车道连接的右侧部分断点(三维)集
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接的右侧部分断点(三维)集为{laneConnector.rightBreak3DsPartly(laneConnector.rightBreakPoints()[0], laneConnector.rightBreakPoints()[-1])}")
        print(f"id为{connector.id()}的连接段的车道连接的右侧部分断点(三维)集(米制单位)为{laneConnector.rightBreak3DsPartly(laneConnector.rightBreakPoints(UnitOfMeasure.Metric)[0], laneConnector.rightBreakPoints(UnitOfMeasure.Metric)[-1], UnitOfMeasure.Metric)}")
```

 **def distToStartPoint(self, p:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> float: ...**

计算车道链接中心线上任意点到起点的距离， 单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
# 计算车道连接中心线上任意点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线上任意点到起点的距离为{laneConnector.distToStartPoint(laneConnector.centerBreakPoints()[1])}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线上任意点到起点的距离(米制单位)为{laneConnector.distToStartPoint(laneConnector.centerBreakPoints(UnitOfMeasure.Metric)[1], UnitOfMeasure.Metric)}")
```

 **def distToStartPointWithSegmIndex(self, p:PySide2.QtCore.QPointF, segmIndex:int=..., bOnCentLine:bool=..., unit:Tess.UnitOfMeasure) -> float: ...**

计算中心线上任意点到起点的距离，附加条件是该点所在车道上的分段序号，默认单位为像素;可通过unit参数设置单位  
参数：  
\[in\] p：当前中心线上该点坐标，像素坐标   
\[in\] segmIndex：该点所在车道上的分段序号  
\[in\] bOnCentLine：是否在中心线上  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  
注：如传入米制参数，请勿遗忘传入segmIndex与bOnCentLine参数。

举例：

```python
# 计算中心线上任意点到起点的距离
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线上任意点到起点的距离为{laneConnector.distToStartPointWithSegmIndex(laneConnector.centerBreakPoints()[1], 1)}")
        print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线上任意点到起点的距离(米制单位)为{laneConnector.distToStartPointWithSegmIndex(laneConnector.centerBreakPoints(UnitOfMeasure.Metric)[1], 1, True, UnitOfMeasure.Metric)}")
```

 **def getPointAndIndexByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, outIndex:int, unit:Tess.UnitOfMeasure) -> bool: ...**

求中心线起点下游dist距离处的点及分段序号, 如果目标点不在中心线上返回False，否则返回True  

参数：  
\[in\] dist：中心线起点向前延伸的距离，像素单位  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  
\[out\] outPoint：中心线起点向前延伸dist距离后所在点， 像素坐标  
\[out\] outIndex：中心线起点向前延伸dist距离后所在分段序号

举例：

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
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点为{outPoint},分段序号为{outIndex}")
        if laneConnector.getPointAndIndexByDist(50, outPoint, outIndex, UnitOfMeasure.Metric):
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点(米制单位)为{outPoint},分段序号为{outIndex}")
```



 **def getPointByDist(self, dist:float, outPoint:PySide2.QtCore.QPointF, unit:Tess.UnitOfMeasure) -> bool: ...**

求中心线起始点下游dist距离处的点, 如果目标点不在中心线上返回False，否则返回True  
参数：  
\[in\] dist：中心线起点向前延伸的距离，像素单位  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制  
\[out\] outPoint：中心线起点向前延伸dist距离后所在点， 像素坐标  

举例：

```python
# 求中心线起始点下游dist距离处的点
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        outPoint = QPointF()
        if laneConnector.getPointByDist(50, outPoint):
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点为{outPoint}")
        if laneConnector.getPointByDist(50, outPoint, UnitOfMeasure.Metric):
            print(f"id为{connector.id()}的连接段的车道连接{laneConnector.id()}的中心线起点向前延伸dist距离后所在点(米制单位)为{outPoint}")
```



 **def setOtherAttr(self, attr:typing.Dict) -> None: ...**

设置车道连接其它属性，方便二次开发过程中使用

举例：

```python
# 设置车道连接其它属性
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnector
lConnectors = netiface.connectors()
for connector in lConnectors:
    laneConnectors = connector.laneConnectors()
    for laneConnector in laneConnectors:
        laneConnector.setOtherAttr({'newAttr':'add a new attr'})
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
          f"其所属的连接段={laneConnector.connector()}, 所属section={laneConnector.section()},"
          f"fromLane={laneConnector.fromLane()}, toLane={laneConnector.toLane()},"
          f"fromLaneObject={laneConnector.fromLaneObject()}, toLaneObject={laneConnector.toLaneObject()},"
          f"长度length（像素制）={laneConnector.length()}， 米制={laneConnector.length(UnitOfMeasure.Metric)},"
          f"centerBreakPoints(像素制)={laneConnector.centerBreakPoints()},centerBreakPoints(米制)={laneConnector.centerBreakPoints(UnitOfMeasure.Metric)},"
          f"leftBreakPoints(像素制)={laneConnector.leftBreakPoints()},leftBreakPoints(米制)={laneConnector.leftBreakPoints(UnitOfMeasure.Metric)},"
          f"rightBreakPoints(像素制)={laneConnector.rightBreakPoints()},rightBreakPoints(米制)={laneConnector.rightBreakPoints(UnitOfMeasure.Metric)},"
          f"centerBreakPoint3Ds(像素制)={laneConnector.centerBreakPoint3Ds()},centerBreakPoint3Ds(米制)={laneConnector.centerBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreakPoint3Ds(像素制)={laneConnector.leftBreakPoint3Ds()},leftBreakPoint3Ds(米制)={laneConnector.leftBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"rightBreakPoint3Ds(像素制)={laneConnector.rightBreakPoint3Ds()},rightBreakPoint3Ds(米制)={laneConnector.rightBreakPoint3Ds(UnitOfMeasure.Metric)},"
          f"leftBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1],laneConnector.leftBreakPoints()[-1])},"
          f"leftBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1],laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"rightBreak3DsPartly(像素制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints()[1],laneConnector.leftBreakPoints()[-1])},"
          f"rightBreak3DsPartly(米制)={laneConnector.leftBreak3DsPartly(laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[1],laneConnector.leftBreakPoints(UnitOfMeasure.Metric)[-1],UnitOfMeasure.Metric)},"
          f"distToStartPoint(像素制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints()[0])}，distToStartPoint(米制)={laneConnector.distToStartPoint(laneConnector.centerBreakPoints(UnitOfMeasure.Metric)[0],UnitOfMeasure.Metric)}，"
          f"设置自定义属性setOtherAttr={laneConnector.setOtherAttr({'newAttr':'add a new attr'})}")
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

面域接口，方法如下：

 **def id(self) -> int: ...**

获取面域ID； 面域是指：若干Connector重叠形成的区域;

举例：

```python
# 获取面域ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnectorArea
lConnectorAreas = netiface.allConnectorArea()
for connectorArea in lConnectorAreas:
    print(f"面域ID为{connectorArea.id()}")
```

 **def allConnector(self) -> typing.List: ...**

获取当前面域包含的所有连接段， 返回类型列表，元素为IConnector对象

举例：

```python
# 获取当前面域包含的所有连接段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnectorArea
lConnectorAreas = netiface.allConnectorArea()
for connectorArea in lConnectorAreas:
    lConnectors = connectorArea.allConnector()
    for connector in lConnectors:
        print(f"id为{connector.id()}的连接段的面域ID为{connectorArea.id()}")
```

 **def centerPoint(self, unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

获取面域中心点， 像素坐标; 可通过unit参数设置单位  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

 举例：

```python
# 获取面域中心点
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IConnectorArea
lConnectorAreas = netiface.allConnectorArea()
for connectorArea in lConnectorAreas:
    print(f"id为{connectorArea.id()}的面域中心点为{connectorArea.centerPoint()}")
    print(f"id为{connectorArea.id()}的面域中心点(米制单位)为{connectorArea.centerPoint(UnitOfMeasure.Metric)}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showConnectorAreaAttr(netiface)
def showConnectorAreaAttr(self, netiface):
    allConnectorArea = netiface.allConnectorArea()
    connectorArea = allConnectorArea[0]
    connectorArea1 = netiface.findConnectorArea(connectorArea.id())
    print(
        f"===场景中的connectorArea个数={len(allConnectorArea)}，第一个connectorArea的属性={connectorArea.id()}")
    print(f"该connectorArea的属性：id={connectorArea.id()}, 包含的所有connector={connectorArea.allConnector()}, "
          f"面域中心点(像素制)={connectorArea.centerPoint()}，(米制)={connectorArea.centerPoint(UnitOfMeasure.Metric)}")
```





### 2.9. IDispatchPoint

发车点接口，方法如下：

 **def id(self) -> int: ...**

获取发车点ID

举例：

```python
# 获取发车点ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDispatchPoint
lDispatchPoints = netiface.dispatchPoints()
for dispatchPoint in lDispatchPoints:
    print(f"发车点ID为{dispatchPoint.id()}")
```

 **def name(self) -> str: ...**

获取发车名称

举例：

```python
# 获取发车名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDispatchPoint
lDispatchPoints = netiface.dispatchPoints()
for dispatchPoint in lDispatchPoints:
    print(f"发车点名称={dispatchPoint.name()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取发车点所在路段

举例：

```python
# 获取发车点所在路段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDispatchPoint
lDispatchPoints = netiface.dispatchPoints()
for dispatchPoint in lDispatchPoints:
    print(f"发车点所在路段ID={dispatchPoint.link().id()}")
```

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

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取发车点多边型轮廓

举例：

```python
# 获取发车点多边型轮廓
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDispatchPoint
lDispatchPoints = netiface.dispatchPoints()
for dispatchPoint in lDispatchPoints:
    print(f"发车点多边型轮廓={dispatchPoint.polygon()}")
```


**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showDispatchPointAttr(netiface)
def showDispatchPointAttr(self, netiface):
    dispatchPoints = netiface.dispatchPoints()
    dispatchPoint = dispatchPoints[0]
    connectorArea1 = netiface.findDispatchPoint(dispatchPoint.id())
    print(
        f"===场景中的dispatchPoint个数={len(dispatchPoints)}，第一个dispatchPoint的属性={dispatchPoint.id()}")
    print(f"该dispatchPoint的属性：id={dispatchPoint.id()}, dispatchPoint name ={dispatchPoint.name()}, "
          f"所在路段名称={dispatchPoint.link().name()}，polygon={dispatchPoint.polygon()})
```



------



### 2.10. IDecisionPoint

决策点接口，方法如下：

 **def id(self) -> int: ...**

获取决策点ID

举例：

```python
# 获取决策点ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    print(f"决策点ID为{decisionPoint.id()}")
```

 **def name(self) -> str: ...**

获取决策点名称

举例：

```python
# 获取决策点名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    print(f"决策点{decisionPoint.id()}的名称={decisionPoint.name()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取决策点所在路段

举例：

```python
# 获取决策点所在路段
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    print(f"决策点{decisionPoint.id()}所在路段ID={decisionPoint.link().id()}")
```

 **def distance(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取距路段起点距离，默认单位：像素，可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 获取距路段起点距离
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    print(f"决策点{decisionPoint.id()}距路段起点距离={decisionPoint.distance()}")
    print(f"决策点{decisionPoint.id()}距路段起点距离(米制单位)={decisionPoint.distance(UnitOfMeasure.Metric)}")
```

 **def routings(self) -> typing.List: ...**

获取决策点控制的所有决策路径， 返回类型列表，元素为IRouting对象

举例：

```python
# 获取决策点控制的所有决策路径
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取决策点多边型轮廓

举例：

```python
# 获取决策点多边型轮廓
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IDecisionPoint
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    print(f"决策点{decisionPoint.id()}的多边型轮廓={decisionPoint.polygon()}")
```


**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showDecisionPointAttr(netiface)
def showDecisionPointAttr(self, netiface):
    decisionPoints = netiface.decisionPoints()
    decisionPoint = decisionPoints[0]
    decisionPoint1 = netiface.findDispatchPoint(decisionPoint.id())
    print(
        f"===场景中的decisionPoint个数={len(decisionPoints)}，第一个decisionPoint的属性={decisionPoint.id()}")
    print(f"该decisionPoint的属性：id={decisionPoint.id()}, dispatchPoint name ={decisionPoint.name()}, "
          f"所在路段名称={decisionPoint.link().name()}，距离路段起点距离distance(像素制)={decisionPoint.distance()}, 米制={decisionPoint.distance(UnitOfMeasure.Metric)},"
          f"控制的决策路径id={[(route.id(), [link.name() for link in route.getLinks()]) for route in decisionPoint.routings()]}"
          f"polygon={decisionPoint.polygon()},设置为仿真过程中可被动态修改={decisionPoint.setDynaModified(True)}")
```





------



### 2.11. IRouting

路径接口，方法如下：

 **def id(self) -> int: ...**

获取路径ID

举例：

```python
# 获取路径ID
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
# 获取决策点控制的所有决策路径
    lRoutings = decisionPoint.routings()
        for routing in lRoutings:
            print(f"决策点{decisionPoint.id()}的决策路径ID为{routing.id()}")
```

 **def calcuLength(self) -> float: ...**

计算路径长度，默认单位：像素，可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
# 计算路径长度
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}长度={routing.calcuLength()}")
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}长度(米制单位)={routing.calcuLength(UnitOfMeasure.Metric)}")
```

 **def getLinks(self) -> typing.List: ...**

获取当前路径的路段序列， 不包含连接器

举例：

```python
# 获取当前路径的路段序列
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        links = routing.getLinks()
        for link in links:
            print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}的路段序列={link.name()}")
```

 **def deciPointId(self) -> int: ...**

获取当前路径所属的决策点ID

举例：

```python
# 获取当前路径所属的决策点ID
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}所属的决策点ID={routing.deciPointId()}")
```

 **def contain(self, pRoad:Tessng.ISection) -> bool: ...**

判定道路是否在当前路径上， 入参需是ISection对象

举例：

```python
# 判定道路是否在当前路径上
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        links = routing.getLinks()
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}判断道路是否在当前路径上={routing.contain(links[0])}")
```

 **def nextRoad(self, pRoad:Tessng.ISection) -> Tessng.ISection: ...**

根据当前路径，获取所给道路的下一条道路， 返回类型为ISection 即下一条路段可能是Link也可能是Connector

参数：

\[in\] pRoad：路段或连接段

举例：

```python
# 根据当前路径，获取所给道路的下一条道路
iface = tessngIFace()
netiface = iface.netInterface()
lDecisionPoints = netiface.decisionPoints()
for decisionPoint in lDecisionPoints:
    lRoutings = decisionPoint.routings()
    for routing in lRoutings:
        links = routing.getLinks()
        print(f"决策点{decisionPoint.id()}的决策路径{routing.id()}根据当前路径，获取所给道路的下一条道路={routing.nextRoad(links[0])}")
```

**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
showRoutingAttr(netiface)
def showRoutingAttr(self, netiface):
    decisionPoints = netiface.decisionPoints()
    decisionPoint = decisionPoints[0]
    routes = decisionPoint.routings()
    route =routes[0]
    route1 = netiface.findRouting(route.id())
    print(f"该route的属性：id={route.id()},"
          f"route 长度 calcuLength (像素制)={route.calcuLength()}, 米制={route.calcuLength(UnitOfMeasure.Metric)},"
          f"所属决策点id={ route.deciPointId()}, 路径上的links={[link.name() for link in route.getLinks()]}")
    links = route.getLinks()
    print(f"判断link是否在路径上={route.contain(links[0])}")
    print(f"获取路径上，指定link的下游道路，返回值可能是link也可能是connector={route.nextRoad(links[0])}")
    links1 = netiface.links()
    print(f"判断link是否在路径上={route.contain(links1[0])}")
    if route.contain(links1[0]):
        print(f"获取路径上，指定link的下游道路，返回值可能是link也可能是connector={route.nextRoad(links1[0])}")
```



### 2.31. ITrafficController

信号机接口

 **def id(self) -> int: ...**

获取信控机ID 

举例：

```python
# 获取信控机ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    print(f"信控机ID为{trafficController.id()}")
```

 **def name(self) -> str: ...**

获取信控机名称

举例：

```python
# 获取信控机名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    print(f"信控机{trafficController.id()}名称={trafficController.name()}")
```

 **def setName(name: str) -> None: ...**

设置信控机名称  
\[in\] name：信号机名称

举例：

```python
# 设置信控机名称
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    print(f"设置信控机{trafficController.id()}名称,返回值={trafficController.setName('new_'+trafficController.name())}")
    print(f"获取信控机{trafficController.id()}名称={trafficController.name()}")
```

 **def addPlan(plan: Tessng.ISignalPlan) -> None: ...**

 为信号机添加信控方案  
\[in\] plan ：信控方案， 可循环调用设置多时段信控方案

举例：

```python
# 为信号机添加信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    print(f"移除/删除信号机{trafficController.id()}的信控方案")
    trafficController.removePlan(plans[0])
    print(f"为信号机{trafficController.id()}添加信控方案")
    trafficController.addPlan(plans[0])
```

 **def removePlan(plan: Tessng.ISignalPlan) -> None: ...**

 移除/删除信号机的信控方案  
\[in\] plan ：信控方案， 

举例：

```python
# 移除/删除信号机的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    print(f"移除/删除信号机{trafficController.id()}的信控方案")
    trafficController.removePlan(plans[0])
```

 **def IPlans(self) -> typing.List<Tessng.ISignalPlan>: ...**

 获取当前信号机中所有的信控方案  
\[out\] plan ：信控方案

举例：

```python
# 获取当前信号机中所有的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    print(f"获取信号机{trafficController.id()}中所有的信控方案={trafficController.IPlans()}")
```


**案例代码**

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
# 创建信控方案
signalPlan = netiface.createSignalPlan(trafficController, "早高峰", 150, 50,0,1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿",50)
yellow = Online.ColorInterval("黄",3)
red = Online.ColorInterval("红",97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行",w_e_straight_phasecolor)
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

trafficController = netiface.createTrafficController("交叉口1")
showTrafficControllerAttr(netiface)
def showTrafficControllerAttr(netiface):
    controllers = netiface.trafficControllers()
    controllerCount = netiface.trafficControllerCount()
    trafficControllerIds = netiface.trafficControllerIds()
    controller = netiface.findTrafficControllerById(trafficControllerIds[0])
    controller = netiface.findTrafficControllerByName(controllers[0].name())
    print(f"路网中的信号机总数={controllerCount},所有的信号机id列表={trafficControllerIds},信号机编号={trafficControllerIds[0]}的具体信息："
          f"编号={controller.id()},名称={controller.name()}, 设置新名字={controller.setName('new_'+controller.name())},"
          f"获取信号机的信控方案={controller.IPlans()}")
    IPlans = controller.IPlans()
    print(f"移除/删除信号机的信控方案={controller.removePlan(IPlans[0])}")
    print(f"为信号机添加信控方案,添加回原有信控方案={controller.addPlan(IPlans[0])}")
    print(f"信号机当前信控方案={controller.IPlans()}")
```



### 2.30. ISignalPlan

信号控制方案接口

 **def id(self) -> int: ...**

获取信控方案ID 

举例：

```python
# 获取当前信号机中所有的信控方案
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案ID={signalPlan.id()}")
```

 **def name(self) -> str: ...**

获取信控方案名称（V3版本的信号灯组名称） 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案名称={signalPlan.name()}")
```

 **def trafficName(self) -> str: ...**

获取信号机名称(在增加一个获取信号机ID的属性？因为名称不唯一)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信号机名称={signalPlan.trafficName()}")
```

 **def cycleTime(self) -> int: ...**

获取获取信号周期，单位：秒 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案周期={signalPlan.cycleTime()}")
```

 **def fromTime(self) -> int: ...**

获取信控方案起始时间，单位：秒 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案起始时间={signalPlan.fromTime()}")
```

 **def toTime(self) -> int: ...**

获取信控方案结束时间，单位：秒 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案结束时间={signalPlan.toTime()}")
```

 **def phases(self) -> typing.List<Tessng.ISignalPhase>: ...**

获取信控方案中的相位列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"信控方案中的相位列表={signalPlan.phases()}")
```

 **def setName( name: str) -> None: ...**

设置信控方案（V3版本的信号灯组）名称 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"设置信控方案名称={signalPlan.setName('new_'+signalPlan.name())}")
        print(f"获取信控方案名称={signalPlan.name()}")
```

 **def setCycleTime(period: int) -> None: ...**

设置信控方案（V3版本的信号灯组）的信号周期， 单位：秒 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"设置信控方案周期={signalPlan.setCycleTime(100)}")
        print(f"获取信控方案周期={signalPlan.cycleTime()}")
```

 **def setFromTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的起始时间， 单位：秒 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        print(f"设置信控方案起始时间={signalPlan.setFromTime(100)}")
        print(f"获取信控方案起始时间={signalPlan.fromTime()}")
```

 **def setToTime(time: int) -> None: ...**

设置信控方案（V3版本的信号灯组）起作用时段的结束时间， 单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
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
signalPlan = netiface.createSignalPlan(trafficController, "早高峰", 150, 50,0,1800) # createSignalPlan
# 创建方向详情--相位
green = Online.ColorInterval("绿",50)
yellow = Online.ColorInterval("黄",3)
red = Online.ColorInterval("红",97)
w_e_straight_phasecolor = [green, yellow, red]
w_e_straight_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行",w_e_straight_phasecolor)
we_ped_phase = netiface.createSignalPlanSignalPhase(signalPlan, "东西直行行人", w_e_straight_phasecolor)
showSignalPlanAttr(netiface)
def showSignalPlanAttr(netiface):
    signalPlans = netiface.signalPlans()
    signalPlanCount = netiface.signalPlanCount()
    signalPlanIds = netiface.signalPlanIds()
    signalPlan = netiface.findSignalPlanById(signalPlanIds[0])
    signalPlan = netiface.findSignalPlanByName(signalPlans[0].name())
    print(
        f"路网中的信控方案总数={signalPlanCount},所有信控方案列表={signalPlanIds},信控方案编号={signalPlanIds[0]}的具体信息："
        f"编号={signalPlan.id()},名称={signalPlan.name()}, 所属信号机名称={signalPlan.trafficName()},设置新名字={signalPlan.setName('new_' + signalPlan.name())},"
        f"获取信控方案信控周期={signalPlan.cycleTime()},开始时间-结束时间={signalPlan.fromTime()}-{signalPlan.toTime()},"
        f"所有相位信息={signalPlan.phases()}")
```



### 2.13. ISignalPhase

信号相位，接口方法：

 **def id(self) -> int: ...**

获取当前相位的相位ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"相位ID={signalPhase.id()}")
```

 **def phaseName(self) -> str: ...**

获取当前相位的相位名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"相位名称={signalPhase.phaseName()}")
```

 **def signalLamps(self) -> typing.List: ...**

获取本相位下的信号灯列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"本相位下的信号灯列表={signalPhase.signalLamps()}")
```

 **def listColor(self) -> typing.List: ...**

获取本相位的相位灯色列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"本相位的相位灯色列表={signalPhase.listColor()}")
```

 **def setColorList(self, lColor:typing.List<Online.ColorInterval>) -> None: ...**

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

 **def setPhaseName(self, name:str) -> None: ...**

设置当前相位名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"设置当前相位名称={signalPhase.setPhaseName('new_'+signalPhase.phaseName())}")
            print(f"获取当前相位名称={signalPhase.phaseName()}")
```

 **def cycleTime(self) -> int: ...**

相位周期，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"相位周期={signalPhase.cycleTime()}")
```

 **def phaseColor(self) -> Online.SignalPhaseColor: ...**

获取当前相位灯色，Online.SignalPhaseColor

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"当前相位灯色={signalPhase.phaseColor()}")
```

 **def signalPlan(self) -> Tess.ISignalPlan: ...**

获取相位所在信控方案

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITrafficController
lTrafficControllers = netiface.trafficControllers()
for trafficController in lTrafficControllers:
    plans = trafficController.IPlans()
    for signalPlan in plans:
        phases = signalPlan.phases()
        for signalPhase in phases:
            print(f"相位所在信控方案={signalPhase.signalPlan()}")
```

**案例代码**

```python

```





### 2.12. ISignalLamp

信号灯接口，方法如下：

 **def id(self) -> int: ...**

获取信号灯ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"信号灯ID={signalLamp.id()}")
```

 **def color(self) -> str: ...**

获取信号灯当前信号灯色，"R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"信号灯当前信号灯色={signalLamp.color()}")
```

 **def name(self) -> str: ...**

获取信号灯名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"信号灯名称={signalLamp.name()}")
```

 **def setName(self, name:str) -> None: ...**

设置信号灯名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"设置信号灯名称={signalLamp.setName('new_'+signalLamp.name())}")
    print(f"获取信号灯名称={signalLamp.name()}")
```

 **def setLampColor(self, colorStr:str) -> None: ...**

设置信号灯颜色

参数：

\[in\] colorStr：字符串表达的颜色，有四种可选，支持汉字："红"、"绿"、"黄"、"灰"，也支持字符： "R"、"G"、"Y"、"grey"。

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"设置信号灯颜色={signalLamp.setLampColor('R')}")
    print(f"获取信号灯颜色={signalLamp.color()}")
```

 **def signalPhase(self) -> Tessng.ISignalPhase: ...**

获取当前信号灯所在的相位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"当前信号灯所在的相位={signalLamp.signalPhase()}")
```

 **def setSignalPhase(self, signalPhase: Tessng.ISignalPhase) -> None: ...**

为信号灯设置相位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
signalPhase = netiface.findSignalPhase(2)
for signalLamp in lSignalLamps:
    if signalPhase:
        print(f"为信号灯设置相位={signalLamp.setSignalPhase(signalPhase)}")
```

 **def signalPlan(self) -> Tessng.ISignalPlan: ...**

获取当前信号灯所在的灯组， 这里灯组类似于一个信号机种的某个信控方案

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"当前信号灯所在的灯组={signalLamp.signalPlan()}")
```

 **def setDistToStart(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置信号灯距路段起点距离，默认单位：像素，可通过unit参数设置单位  
参数：  
\[in\] dist：距离值  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"设置信号灯距路段起点距离={signalLamp.setDistToStart(100)}")
    print(f"获取信号灯距路段起点距离={signalLamp.distToStart()}")
    print(f"设置信号灯距路段起点距离(米制)={signalLamp.setDistToStart(100, UnitOfMeasure.Metric)}")
    print(f"获取信号灯距路段起点距离(米制)={signalLamp.distToStart(UnitOfMeasure.Metric)}")
```

 **def laneObject(self) -> Tess.ILaneObject: ...**

获取所在车道或车道连接

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"所在车道或车道连接={signalLamp.laneObject()}")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取信号灯多边型轮廓

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"信号灯多边型轮廓={signalLamp.polygon()}")
```

**def angle()->float:**

获取信号灯角度，正北为0顺时针方向

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ISignalLamp
lSignalLamps = netiface.signalLamps()
for signalLamp in lSignalLamps:
    print(f"信号灯角度={signalLamp.angle()}")
```

**案例代码**

```python

```



### 2.14. IBusLine

公交线路接口，接口方法：

 **def id(self) -> int: ...**

获取当前公交线路的ID

举例：

```python

# 获取当前公交线路的ID
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路ID={busLine.id()}")

```

 **def name(self) -> str: ...**

获取当前公交线路的名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路名称={busLine.name()}")
```

 **def length(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交线路长度，单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路长度={busLine.length()}")
    print(f"公交线路长度={busLine.length(UnitOfMeasure.Metric)}")
```

 **def dispatchFreq(self) -> int: ...**

获取当前公交线路的发车间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路发车间隔={busLine.dispatchFreq()}")
```

 **def dispatchStartTime(self) -> int: ...**

获取当前公交线路的发车开始时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路发车开始时间={busLine.dispatchStartTime()}")
```

 **def dispatchEndTime(self) -> int: ...**

获取当前公交线路的发车结束时间，单位：秒， 即当前线路的公交调度表的结束时刻

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路发车结束时间={busLine.dispatchEndTime()}")
```

 **def desirSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交线路的期望速度，单位：像素/秒 （km/h）的像素制（经过了p2m的比例尺转化）   
参数：
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路期望速度={busLine.desirSpeed()}")
    print(f"公交线路期望速度={busLine.desirSpeed(UnitOfMeasure.Metric)}")
```

 **def passCountAtStartTime(self) -> int: ...**

公交线路中公交车的起始载客人数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路中公交车的起始载客人数={busLine.passCountAtStartTime()}")
```

 **def links(self) -> typing.List: ...**

获取公交线路经过的路段序列

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路经过的路段序列={busLine.links()}")
```

 **def stations(self) -> typing.List: ...**

获取公交线路上的所有站点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交线路上的所有站点={busLine.stations()}")
```

 **def stationLines(self) -> typing.List: ...**

公交站点线路，当前线路相关站点的上下客等参数 ， 所有参数的列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"公交站点线路，当前线路相关站点的上下客等参数 ， 所有参数的列表={busLine.stationLines()}")
```

 **def setName(self, name:str) -> None: ...**

设置当前公交线路的名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路的名称={busLine.setName('new name')}")
    print(f"获取当前公交线路的名称={busLine.name()}")
```

 **def setDispatchFreq(self, freq:int) -> None: ...**

设置当前公交线路的发车间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路的发车间隔，单位：秒={busLine.setDispatchFreq(20)}")
    print(f"获取当前公交线路的发车间隔，单位：秒={busLine.dispatchFreq()}")
```

 **def setDispatchStartTime(self, startTime:int) -> None: ...**

设置当前公交线路上的公交首班车辆的开始发车时间

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路上的公交首班车辆的开始发车时间={busLine.setDispatchStartTime(0)}")
    print(f"获取当前公交线路上的公交首班车辆的开始发车时间={busLine.dispatchStartTime()}")
```

 **def setDispatchEndTime(self, endTime:int) -> None: ...**

设置当前公交线路上的公交末班车的发车时间

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路上的公交末班车的发车时间={busLine.setDispatchEndTime(300)}")
    print(f"获取当前公交线路上的公交末班车的发车时间={busLine.dispatchEndTime()}")
```

 **def setDesirSpeed(self, desirSpeed:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位  
参数： 
\[in\]  desirSpeed：期望速度，单位：像素/秒 （km/h）的像素制（经过了p2m的比例尺转化）
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位={busLine.setDesirSpeed(40, UnitOfMeasure.Metric)}")
    print(f"获取当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位={busLine.desirSpeed()}")
    print(f"获取当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位={busLine.desirSpeed(UnitOfMeasure.Metric)}")
```

 **def setPassCountAtStartTime(self, count:int) -> None: ...**

设置当前公交线路的起始载客人数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
lBusLines = netiface.buslines()
for busLine in lBusLines:
    print(f"设置当前公交线路的起始载客人数={busLine.setPassCountAtStartTime(60)}")
    print(f"获取当前公交线路的起始载客人数={busLine.passCountAtStartTime()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showBusLineAttr(netiface)
def showBusLineAttr(netiface):
    busLines = netiface.buslines()
    if len(busLines) > 0:
        path = busLines[0]
        path2 = netiface.findBusline(path.id())
        path3 = netiface.findBuslineByFirstLinkId(path.links()[0].id())
        print(f"获取当前公交线路的ID={path.id()},获取当前公交线路的名称={path.name()},"
              f"获取当前公交线路长度，单位：像素={path.length()},单位：米={path.length(UnitOfMeasure.Metric)},"
              f"获取当前公交线路的发车间隔，单位：秒={path.dispatchFreq()},获取当前公交线路的发车开始时间，单位：秒={path.dispatchStartTime()},"
              f"获取当前公交线路的发车结束时间，单位：秒， 即当前线路的公交调度表的结束时刻={path.dispatchEndTime()},"
              f"获取当前公交线路的期望速度，像素制：={path.desirSpeed()}, 米制km/h={path.desirSpeed(UnitOfMeasure.Metric)},"
              f"公交线路中公交车的起始载客人数={path.passCountAtStartTime()},获取公交线路经过的路段序列={path.links()},"
              f"获取公交线路上的所有站点={path.stations()}, 公交站点线路，当前线路相关站点的上下客等参数 ， 所有参数的列表={path.stationLines()},"
              f"设置当前公交线路的名称={path.setName('new name')},设置当前公交线路的发车间隔，单位：秒={path.setDispatchFreq(20)},"
              f"设置当前公交线路上的公交首班车辆的开始发车时间={path.setDispatchStartTime(0)},设置当前公交线路上的公交末班车的发车时间={path.setDispatchEndTime(300)},"
              f"设置当前公交线路的期望速度,默认输入是像素，可通过unit参数设置单位={path.setDesirSpeed(40, UnitOfMeasure.Metric)},"
              f"设置当前公交线路的起始载客人数={path.setPassCountAtStartTime(60)}")
```








### 2.15. IBusStation

公交站点接口，接口方法：

 **def id(self) -> int: ...**

获取当前公交站点ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点ID={busStation.id()}")
```

 **def name(self) -> str: ...**

获取当前公交线路名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点名称={busStation.name()}")
```

 **def laneNumber(self) -> int: ...**

获取当前公交站点所在车道序号

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点所在车道序号={busStation.laneNumber()}")
```

 **def x(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交站点的中心点的位置， X坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点的中心点的位置， X坐标={busStation.x()}")
    print(f"获取当前公交站点的中心点的位置， X坐标={busStation.x(UnitOfMeasure.Metric)}")
```

 **def y(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交站点的中心点的位置， Y坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点的中心点的位置， Y坐标={busStation.y()}")
    print(f"获取当前公交站点的中心点的位置， Y坐标={busStation.y(UnitOfMeasure.Metric)}")
```

 **def length(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交站点的长度，单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点的长度，单位：像素={busStation.length()}")
    print(f"获取当前公交站点的长度，单位：米={busStation.length(UnitOfMeasure.Metric)}")
```

 **def stationType(self) -> int: ...**

获取当前公交站点的类型：站点类型 1：路边式、2：港湾式

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点的类型：站点类型 1：路边式、2：港湾式={busStation.stationType()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取当前公交站点所在路段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点所在路段={busStation.link()}")
```

 **def lane(self) -> Tessng.ILane: ...**

获取当前公交站点所在车道

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点所在车道={busStation.lane()}")
```

 **def distance(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取当前公交站点的起始位置距路段起点的距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取当前公交站点的起始位置距路段起点的距离，默认单位：像素={busStation.distance()}")
    print(f"获取当前公交站点的起始位置距路段起点的距离，单位：米={busStation.distance(UnitOfMeasure.Metric)}")
```

 **def setName(self, name:str) -> None: ...**

设置站点名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"设置站点名称={busStation.setName('new name')}")
    print(f"获取站点名称={busStation.name()}")
```

 **def setDistToStart(self, dist:float) -> None: ...**

设置站点起始点距车道起点距离，默认单位：像素  
参数：  
\[in\] dist：距车道起点距离，单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"设置站点起始点距车道起点距离，默认单位：像素={busStation.setDistToStart(100)}")
    print(f"获取当前公交站点的起始位置距路段起点的距离，默认单位：像素={busStation.distance()}")
    print(f"获取当前公交站点的起始位置距路段起点的距离，单位：米={busStation.distance(UnitOfMeasure.Metric)}")
```

 **def setLength(self, length:float) -> None: ...**

设置当前公交站点的长度，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"设置当前公交站点的长度，默认单位：像素={busStation.setLength(100)}")
    print(f"获取当前公交站点的长度，单位：像素={busStation.length()}")
    print(f"获取当前公交站点的长度，单位：米={busStation.length(UnitOfMeasure.Metric)}")
```

 **def setType(self, type:int) -> None: ...**

设置当前公交站点类型

参数：

\[in\] type：站点类型，1 路侧式、2 港湾式

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"设置当前公交站点类型={busStation.setType(2)}")
    print(f"获取当前公交站点类型={busStation.stationType()}")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取 公交站点多边型轮廓的顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusStation
lBusStations = netiface.busStations()
for busStation in lBusStations:
    print(f"获取 公交站点多边型轮廓的顶点={busStation.polygon()}")
```



**案例代码**

```python
netiface = tessngIFace().netInterface()
showBusStationAttr(netiface)
def showBusStationAttr(self, netiface):
    busStations = netiface.busStations()
    if len(busStations) > 0:
        busStation = busStations[0]
        busStation1 = netiface.findBusStation(busStation.id())
        print(f"获取当前公交站点ID={busStation.id()},获取当前公交线路名称={busStation.name()},"
              f"获取当前公交站点所在车道序号={busStation.laneNumber()},"
              f"获取当前公交站点的中心点的位置， X坐标 像素制={busStation.x()},米制={busStation.x()},"
              f"获取当前公交站点的中心点的位置， y坐标 像素制={busStation.y()},米制={busStation.y()},"
              f"获取当前公交站点的长度，，像素制：={busStation.length()}, 米制km/h={busStation.length(UnitOfMeasure.Metric)},"
              f"获取当前公交站点所在路段={busStation.link()},"
              f"获取当前公交站点所在车道={busStation.lane()}, 设置站点名称={busStation.setName('new trans station')},"
              f"获取当前公交站点的起始位置距路段起点的距离，像素制：={busStation.distance()}, 米制km/h={busStation.distance(UnitOfMeasure.Metric)},"
              f"设置站点起始点距车道起点距离，像素制：={busStation.setDistToStart(busStation.distance()+1)}, 米制km/h={busStation.setDistToStart(busStation.distance(UnitOfMeasure.Metric)+1,UnitOfMeasure.Metric)},"
              f"设置站点长度，像素制：={busStation.setLength(busStation.length()+1)}, 米制km/h={busStation.setLength(busStation.length(UnitOfMeasure.Metric)+1,UnitOfMeasure.Metric)},"
              f"设置当前公交站点类型={busStation.setType(2)}, {busStation.setType(1)},获取 公交站点多边型轮廓的顶点={busStation.polygon()} "
              )

```







------



### 2.16. IBusStationLine

公交站点-线路接口，通过此接口可以获取指定线路某站点运行参数，如靠站时间、下客百分比等，还可以设置这些参数。

接口方法：

 **def id(self) -> int: ...**

获取公交“站点-线路”ID

举例：

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

举例：

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

举例：

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

举例：

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

 **def getOutPercent(self) -> float: ...**

获取当前公交线路下该站台的下客百分比

举例：

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

 **def getOnTimePerPerson(self) -> float: ...**

获取当前公交线路下该站台下的平均每位乘客上车时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines:
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines:
        print(f"获取当前公交线路下该站台下的平均每位乘客上车时间，单位：秒={busStationLine.getOnTimePerPerson()}")
```

 **def getOutTimePerPerson(self) -> float: ...**

获取当前公交线路下该站台下的平均每位乘客下车时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IBusLine
buslines = netiface.buslines()
for busLine in buslines:
    busStationLines = busLine.stationLines()
    for busStationLine in busStationLines:
        print(f"获取当前公交线路下该站台下的平均每位乘客下车时间，单位：秒={busStationLine.getOutTimePerPerson()}")
```

 **def setBusParkingTime(self, time:int) -> None: ...**

设置当前公交线路下该站台下的车辆停靠时间(秒)

举例：

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

 **def setGetOutPercent(self, percent:float) -> None: ..**

设置当前公交线路下的该站台的下客百分比

举例：

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

 **def setGetOnTimePerPerson(self, time:float) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客上车时间

举例：

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

 **def setGetOutTimePerPerson(self, time:float) -> None: ...**

设置当前公交线路下的该站台的平均每位乘客下车时间

举例：

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
            print(f"获取公交“站点-线路”ID={busStationLine.id()},获取当前公交站点的ID={busStationLine.stationId()},"
                  f"获取当前公交站台所属的公交线路ID={busStationLine.lineId()},"
                  f"获取当前公交线路下该站台的公交车辆停靠时间(秒)={busStationLine.busParkingTime()},"
                  f"获取当前公交线路下该站台的下客百分比={busStationLine.getOutPercent()},"
                  f"获取当前公交线路下该站台下的平均每位乘客上车时间，单位：秒={busStationLine.getOnTimePerPerson()},"
                  f"获取当前公交线路下该站台下的平均每位乘客下车时间，单位：秒={busStationLine.getOutTimePerPerson()},"
                  f"设置当前公交线路下该站台下的车辆停靠时间(秒)={busStationLine.setBusParkingTime(20)}, "
                  f"设置当前公交线路下的该站台的下客百分比={busStationLine.setGetOutPercent(0.60)},"
                  f"设置当前公交线路下的该站台的平均每位乘客上车时间={busStationLine.setGetOnTimePerPerson(2.0)},"
                  f"设置当前公交线路下的该站台的平均每位乘客下车时间：={busStationLine.setGetOutTimePerPerson(1.0)}"
                  )
```



------





### 2.18. IVehicleDrivInfoCollector

数据采集器接口，方法如下：

 **def id(self) -> int: ...**

获取采集器ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器ID：{vehicleDrivInfoCollector.id()}")
```

 **def collName(self) -> str: ...**

获取采集器名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}名称：{vehicleDrivInfoCollector.collName()}")
```

 **def onLink(self) -> bool: ...**

判断当前数据采集器是否在路段上，返回值为True表示检测器在路段上，返回值False则表示在connector上

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}是否在路段上：{vehicleDrivInfoCollector.onLink()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取采集器所在的路段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的路段：{vehicleDrivInfoCollector.link()}")
```

 **def connector(self) -> Tessng.IConnector: ...**

获取采集器所在的连接段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的连接段：{vehicleDrivInfoCollector.connector()}")
```

 **def lane(self) -> Tessng.ILane: ...**

如果采集器在路段上，则返回ILane对象，否则范围None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的车道：{vehicleDrivInfoCollector.lane()}")
```

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果采集器在连接段上，则返回laneConnector“车道连接”对象，否则返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}所在的车道连接：{vehicleDrivInfoCollector.laneConnector()}")
```

 **def distToStart(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取采集器距离路段|连接段起点的距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}距离路段|连接段起点的距离为{vehicleDrivInfoCollector.distToStart()}")
    print(f"采集器{vehicleDrivInfoCollector.id()}距离路段|连接段起点的距离（米制）为{vehicleDrivInfoCollector.distToStart(UnitOfMeasure.Metric)}")
```

 **def point(self, unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

采集器所在点，像素坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}所在点，坐标为{vehicleDrivInfoCollector.point()}")
    print(f"采集器{vehicleDrivInfoCollector.id()}所在点，米制坐标为{vehicleDrivInfoCollector.point(UnitOfMeasure.Metric)}")
```

 **def fromTime(self) -> int: ...**

获取采集器的工作起始时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作起始时间，为{vehicleDrivInfoCollector.fromTime()}秒")
```

 **def toTime(self) -> int: ...**

获取采集器的工作停止时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作停止时间，为{vehicleDrivInfoCollector.toTime()}秒")
```

 **def aggregateInterval(self) -> int: ...**

获取数据集计的时间间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    print(f"采集器{vehicleDrivInfoCollector.id()}数据集计的时间间隔，为{vehicleDrivInfoCollector.aggregateInterval()}秒")
```

 **def setName(self, name:str) -> None: ...**

设置采集器名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    vehicleDrivInfoCollector.setName("采集器名称")
    print(f"采集器{vehicleDrivInfoCollector.id()}的名称，为{vehicleDrivInfoCollector.collName()}")
```

 **def setDistToStart(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置采集器距车道起点（或“车道连接”起点）的距离， 单位：像素 

参数：  
\[in\] dist：采集器距离车道起点（或“车道连接”起点）的距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    vehicleDrivInfoCollector.setDistToStart(400)
    print(f"采集器{vehicleDrivInfoCollector.id()}距车道起点（或“车道连接”起点）的距离，为{vehicleDrivInfoCollector.distToStart()}像素")
    vehicleDrivInfoCollector.setDistToStart(400, UnitOfMeasure.Metric)
    print(f"采集器{vehicleDrivInfoCollector.id()}距车道起点（或“车道连接”起点）的距离（米制），为{vehicleDrivInfoCollector.distToStart(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    vehicleDrivInfoCollector.setFromTime(10)
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作起始时间，为{vehicleDrivInfoCollector.fromTime()}秒")
```

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    vehicleDrivInfoCollector.setToTime(60)
    print(f"采集器{vehicleDrivInfoCollector.id()}的工作停止时间，为{vehicleDrivInfoCollector.toTime()}秒")
```

 **def setAggregateInterval(self, interval:int) -> None: ...**

设置集计数据时间间隔(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleDrivInfoCollector
lVehicleDrivInfoCollectors = netiface.vehiInfoCollectors()
for vehicleDrivInfoCollector in lVehicleDrivInfoCollectors:
    vehicleDrivInfoCollector.setAggregateInterval(10)
    print(f"采集器{vehicleDrivInfoCollector.id()}数据集计的时间间隔，为{vehicleDrivInfoCollector.aggregateInterval()}秒")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取采集器的多边型轮廓顶点

举例：

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

```



------



### 2.19. IVehicleQueueCounter

排队计数器接口，方法如下：

 **def id(self) -> int: ...**

获取当前排队计数器ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}的名称为{vehicleQueueCounter.counterName()}")
```

 **def onLink(self) -> bool: ...**

是否在路段上，如果True则connector()返回None

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}所在连接段为{vehicleQueueCounter.connector()}")
```

 **def lane(self) -> Tessng.ILane: ...**

如果计数器在路段上则lane()返回所在车道，laneConnector()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}所在车道为{vehicleQueueCounter.lane()}")
```

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

如果计数器在连接段上则laneConnector返回“车道连接”,lane()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}所在车道连接为{vehicleQueueCounter.laneConnector()}")
```

 **def distToStart(self，unit:Tess.UnitOfMeasure) -> float: ...**

计数器距离起点距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离为{vehicleQueueCounter.distToStart()}")
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离（米制）为{vehicleQueueCounter.distToStart(UnitOfMeasure.Metric)}")
```

 **def point(self，unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

计数器所在点，像素坐标  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
举例：

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

获取当前计数器工作起始时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}的工作起始时间，为{vehicleQueueCounter.fromTime()}秒")
```

 **def toTime(self) -> int: ...**

获取当前计数器工作停止时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}的工作停止时间，为{vehicleQueueCounter.toTime()}秒")
```

 **def aggregateInterval(self) -> int: ...**

计数集计数据时间间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    print(f"排队计数器{vehicleQueueCounter.id()}数据集计的时间间隔，为{vehicleQueueCounter.aggregateInterval()}秒")
```

 **def setName(self, name:str) -> None: ...**

设置计数器名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    vehicleQueueCounter.setName("计数器名称")
    print(f"排队计数器{vehicleQueueCounter.id()}的名称，为{vehicleQueueCounter.counterName()}")
```

 **def setDistToStart(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置当前计数器距车道起点（或“车道连接”起点）距离

参数：  
\[in\] dist：计数器距离车道起点（或“车道连接”起点）的距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    vehicleQueueCounter.setDistToStart(100)
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离，为{vehicleQueueCounter.distToStart()}")
    vehicleQueueCounter.setDistToStart(100, UnitOfMeasure.Metric)
    print(f"排队计数器{vehicleQueueCounter.id()}距离起点距离（米制），为{vehicleQueueCounter.distToStart(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    vehicleQueueCounter.setFromTime(10)
    print(f"排队计数器{vehicleQueueCounter.id()}的工作起始时间，为{vehicleQueueCounter.fromTime()}秒")
```

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    vehicleQueueCounter.setToTime(60)
    print(f"排队计数器{vehicleQueueCounter.id()}的工作结束时间，为{vehicleQueueCounter.toTime()}秒")
```

 **def setAggregateInterval(self, interval:int) -> None: ...**

设置集计数据时间间隔(秒)

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleQueueCounter
lVehicleQueueCounters = netiface.vehiQueueCounters()
for vehicleQueueCounter in lVehicleQueueCounters:
    vehicleQueueCounter.setAggregateInterval(10)
    print(f"排队计数器{vehicleQueueCounter.id()}数据集计的时间间隔，为{vehicleQueueCounter.aggregateInterval()}秒")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取计数器的多边型轮廓顶点

举例：

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

```





------



### 2.20. IVehicleTravelDetector

行程时间检测器接口，方法如下：

 **def id(self) -> int: ...**

获取检测器ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的名称，为{vehicleTravelDetector.detectorName()}")
```

 **def isStartDetector(self) -> bool: ...**

是否检测器起始点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}是否为起始点，为{vehicleTravelDetector.isStartDetector()}")
```

 **def isOnLink_startDetector(self) -> bool: ...**

检测器起点是否在路段上，如果否，则起点在连接段上

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}是否在路段上，为{vehicleTravelDetector.isOnLink_startDetector()}")
```

 **def isOnLink_endDetector(self) -> bool: ...**

检测器终点是否在路段上，如果否，则终点在连接段上

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}是否在路段上，为{vehicleTravelDetector.isOnLink_endDetector()}")
```

 **def link_startDetector(self) -> Tessng.ILink: ...**

如果检测器起点在路段上则link_startDetector()返回起点所在路段，laneConnector_startDetector()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}所在路段，为{vehicleTravelDetector.link_startDetector()}")
```

 **def laneConnector_startDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器起点在连接段上则laneConnector_startDetector()返回起点“车道连接”,link_startDetector()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器起点{vehicleTravelDetector.id()}所在连接段，为{vehicleTravelDetector.laneConnector_startDetector()}")
```

 **def link_endDetector(self) -> Tessng.ILink: ...**

如果检测器终点在路段上则link_endDetector()返回终点所在路段，laneConnector_endDetector()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}所在路段，为{vehicleTravelDetector.link_endDetector()}")
```

 **def laneConnector_endDetector(self) -> Tessng.ILaneConnector: ...**

如果检测器终点在连接段上则laneConnector_endDetector()返回终点“车道连接”,link_endDetector()返回None

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器终点{vehicleTravelDetector.id()}所在连接段，为{vehicleTravelDetector.laneConnector_endDetector()}")
```

 **def distance_startDetector(self，unit:Tess.UnitOfMeasure) -> float: ...**

检测器起点距离所在车道起点或“车道连接”起点距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离，为{vehicleTravelDetector.distance_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离（米制），为{vehicleTravelDetector.distance_startDetector(UnitOfMeasure.Metric)}米")
```

 **def distance_endDetector(self，unit:Tess.UnitOfMeasure) -> float: ...**

检测器终点距离所在车道起点或“车道连接”起点距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离，为{vehicleTravelDetector.distance_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离（米制），为{vehicleTravelDetector.distance_endDetector(UnitOfMeasure.Metric)}米")
```

 **def point_startDetector(self，unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

检测器起点位置,默认单位：像素，可通过可选参数：unit设置单位，  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点位置，为{vehicleTravelDetector.point_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点位置（米制），为{vehicleTravelDetector.point_startDetector(UnitOfMeasure.Metric)}米")
```

 **def point_endDetector(self，unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

检测器终点位置,默认单位：像素，可通过可选参数：unit设置单位，  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点位置，为{vehicleTravelDetector.point_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点位置（米制），为{vehicleTravelDetector.point_endDetector(UnitOfMeasure.Metric)}米")
```

 **def fromTime(self) -> int: ...**

检测器工作起始时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作起始时间，为{vehicleTravelDetector.fromTime()}")
```

 **def toTime(self) -> int: ...**

检测器工作停止时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作停止时间，为{vehicleTravelDetector.toTime()}")
```

 **def aggregateInterval(self) -> int: ...**

集计数据时间间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的集计数据时间间隔，为{vehicleTravelDetector.aggregateInterval()}")
```

 **def setName(self, name:str) -> None: ...**

设置检测器名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setName("检测器名称")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的名称，为{vehicleTravelDetector.detectorName()}")
```

 **def setDistance_startDetector(self, dist:float) -> None: ...**

设置检测器起点距车道起点（或“车道连接”起点）距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setDistance_startDetector(100)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离，为{vehicleTravelDetector.distance_startDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点距离所在车道起点或“车道连接”起点距离（米制），为{vehicleTravelDetector.distance_startDetector(UnitOfMeasure.Metric)}米")
```

 **def setDistance_endDetector(self, dist:float) -> None: ...**

设置检测器终点距车道起点（或“车道连接”起点）距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setDistance_endDetector(100)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离，为{vehicleTravelDetector.distance_endDetector()}")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点距离所在车道起点或“车道连接”起点距离（米制），为{vehicleTravelDetector.distance_endDetector(UnitOfMeasure.Metric)}米")
```

 **def setFromTime(self, time:int) -> None: ...**

设置工作起始时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setFromTime(10)
    vehicleTravelDetector.setToTime(60)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作起始时间，为{vehicleTravelDetector.fromTime()}秒")
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作结束时间，为{vehicleTravelDetector.toTime()}秒")
```

 

 **def setToTime(self, time:int) -> None: ...**

设置工作结束时间，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setToTime(60)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的工作结束时间，为{vehicleTravelDetector.toTime()}")
```

 **def setAggregateInterval(self) -> int: ...**

设置集计数据时间间隔，单位：秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    vehicleTravelDetector.setAggregateInterval(10)
    print(f"行程时间检测器{vehicleTravelDetector.id()}的集计数据时间间隔，为{vehicleTravelDetector.aggregateInterval()}")
```

 **def polygon_startDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器起始点多边型轮廓的顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的起点多边型轮廓的顶点，为{vehicleTravelDetector.polygon_startDetector()}")
```

 **def polygon_endDetector(self) -> PySide2.QtGui.QPolygonF: ...**

获取行程时间检测器终止点多边型轮廓的顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lVehicleTravelDetectors = netiface.vehiTravelDetectors()
for vehicleTravelDetector in lVehicleTravelDetectors:
    print(f"行程时间检测器{vehicleTravelDetector.id()}的终点多边型轮廓的顶点，为{vehicleTravelDetector.polygon_endDetector()}")
```

**案例代码**

```python

```



------



### 2.21. IGuidArrow

导向箭头接口，方法如下：

 **def id(self) -> int: ...**

获取导向箭头ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows:
    print(f"导向箭头{guidArrow.id()}所在的车道为{guidArrow.lane()}")
```

 **def length(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取导向箭头的长度，默认单位：像素，可通过可选参数：unit设置单位，  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows:
    print(f"导向箭头{guidArrow.id()}的长度，为{guidArrow.length()}")
    print(f"导向箭头{guidArrow.id()}的长度（米制），为{guidArrow.length(UnitOfMeasure.Metric)}米")
```

 **def distToTerminal(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取导向箭头到的终点距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows:
    print(f"导向箭头{guidArrow.id()}的终点距离，为{guidArrow.distToTerminal()}")
    print(f"导向箭头{guidArrow.id()}的终点距离（米制），为{guidArrow.distToTerminal(UnitOfMeasure.Metric)}米")
```

 **def polygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取导向箭头的多边型轮廓的顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IVehicleTravelDetector
lGuidArrows = netiface.guidArrows()
for guidArrow in lGuidArrows:
    print(f"导向箭头{guidArrow.id()}的多边型轮廓的顶点，为{guidArrow.polygon()}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showGuidArrowAttr(netiface)
def showGuidArrowAttr(netiface):
    guidArrow = netiface.findGuidArrow(netiface.guidArrows()[0].id())
    print(f"导向箭头数={netiface.guidArrowCount()},导向箭头集={netiface.guidArrows()},")
    print(f"导向箭头ID={guidArrow.id()},"
          f"获取导向箭头所在的车道={guidArrow.lane()},"
          f"获取导向箭头长度，像素制={guidArrow.length()}， 米制={guidArrow.length(UnitOfMeasure.Metric)},"
          f"获取导向箭头到终点距离，像素制， 米制={guidArrow.distToTerminal()}, 米制={guidArrow.distToTerminal(UnitOfMeasure.Metric)},"
          f"获取导向箭头的类型，导向箭头的类型分为：直行、左转、右转、直行或左转、直行或右转、"
          f"直行左转或右转、左转或右转、掉头、直行或掉头和左转或掉头={guidArrow.arrowType()},"
          f"获取导向箭头的多边型轮廓的顶点={guidArrow.polygon()}")
```







------

### 2.22. IAccidentZone

事故区接口，方法如下：

 **def id(self) -> int: ...**

获取事故区ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区的名称为{accidentZone.name()}")
```

 **def location(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区距所在路段起点的距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}距所在路段起点的距离，为{accidentZone.location()}")
    print(f"事故区{accidentZone.id()}距所在路段起点的距离（米制），为{accidentZone.location(UnitOfMeasure.Metric)}米")
```

 **def zoneLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区长度，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}的长度，为{accidentZone.zoneLength()}")
    print(f"事故区{accidentZone.id()}的长度（米制），为{accidentZone.zoneLength(UnitOfMeasure.Metric)}米")
```

 **def limitedSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区当前时段限速，默认单位：像素(km/h)，可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位(km/h)，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}的限速，为{accidentZone.limitedSpeed()}")
    print(f"事故区{accidentZone.id()}的限速（米制），为{accidentZone.limitedSpeed(UnitOfMeasure.Metric)}km/h")
```

 **def section(self) -> Tessng.ISection: ...**

获取事故区所在的路段或连接段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}所在的路段或连接段，为{accidentZone.section()}")
```

 **def roadId(self) -> int: ...**

获取事故区所在路段的ID

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}当前时段占用的车道列表为{accidentZone.laneObjects()}")
```

 **def controlLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区当前时段控制距离（车辆距离事故区起点该距离内，强制变道），默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}当前时段控制距离，为{accidentZone.controlLength()}")
    print(f"事故区{accidentZone.id()}当前时段控制距离（米制），为{accidentZone.controlLength(UnitOfMeasure.Metric)}米")
```

 **def addAccidentZoneInterval(self，param:Tess.Online.DynaAccidentZoneIntervalParam) -> Tess.IAccidentZoneInterval: ...**

添加事故时段, 
参数：  
\[in\]   param：事故时段参数,入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例：

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

 **def removeAccidentZoneInterval(self，accidentZoneIntervalId:int) -> None: ...**

移除事故时段 
参数：  
\[in\] accidentZoneIntervalId：事故时段ID

举例：

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

 **def updateAccidentZoneInterval(self，param:Tess.Online.DynaAccidentZoneIntervalParam) -> bool: ...**

更新事故时段
参数：  
\[in\] param：事故时段参数,入参数据结构见pyi文件的 Online.DynaAccidentZoneIntervalParam类

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    print(f"事故区{accidentZone.id()}的事故时段，为{accidentZone.accidentZoneIntervals()}")
```

 **def findAccidentZoneIntervalById(self，accidentZoneIntervalId:int) -> Tess.IAccidentZoneInterval: ...**

根据ID查询事故时段
参数：  
\[in\] accidentZoneIntervalId：事故时段ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    accidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in accidentZoneIntervals:
        print(f"事故区{accidentZone.id()}的事故时段，为{accidentZone.findAccidentZoneIntervalById(accidentZoneInterval.intervalId())}")
```

**def findAccidentZoneIntervalByStartTime(self, startTime:int) -> Tess.IAccidentZoneInterval: ...**

根据开始时间查询事故时段
参数：  
\[in\] startTime：事故时段开始时间

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IAccidentZone
lAccidentZones = netiface.accidentZones()
for accidentZone in lAccidentZones:
    accidentZoneIntervals = accidentZone.accidentZoneIntervals()
    for accidentZoneInterval in accidentZoneIntervals:
        print(f"事故区{accidentZone.id()}的事故时段，为{accidentZone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime())}")
```

**案例代码**

```python
netiface = tessngIFace().netInterface()
showAccidentZoneAttr(netiface)

def showAccidentZoneAttr(self, netiface):
    acczones = netiface.accidentZones()
    acczone = netiface.findAccidentZone(acczones[0].id())
    print(f"获取事故区ID={acczone.id()},获取事故区名称={acczone.name()},"
          f"获取事故区当前时段距所在路段起点的距离,像素制={acczone.location()},米制={acczone.location(UnitOfMeasure.Metric)},"
          f"获取事故区当前时段长度,像素制={acczone.zoneLength()},米制={acczone.zoneLength(UnitOfMeasure.Metric)},"
          f"获取事故区当前时段限速,像素制 km/h={acczone.limitedSpeed()},米制={acczone.limitedSpeed(UnitOfMeasure.Metric)},"
          f"获取事故区所在的路段或连接段={acczone.section()},获取事故区所在路段的ID={acczone.roadId()},"
          f"获取事故区所在的道路类型(路段或连接段)={acczone.roadType()}，"
          f"获取事故区当前时段占用的车道列表={acczone.laneObjects()}, "
          f"获取事故区当前时段控制距离（车辆距离事故区起点该距离内，强制变道, 像素制={acczone.controlLength()}, 米制={acczone.controlLength(UnitOfMeasure.Metric)},"
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
    print(f"获取所有事故时段={acczone.accidentZoneIntervals()},"
          # f"根据ID查询事故时段={acczone.findAccidentZoneIntervalById(accidentZoneInterval.intervalId())},"
          f"根据开始时间查询事故时段={acczone.findAccidentZoneIntervalByStartTime(accidentZoneInterval.startTime())}")

```



### 2.23. IAccidentZoneInterval

事故时段接口，方法如下：

 **def intervalId(self) -> int: ...**

获取事故时段ID

举例：

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

举例：

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

举例：

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

举例：

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

 **def length(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区在该时段的长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

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

 **def location(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区在该时段的距起点距离，默认单位：像素，可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

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

 **def limitedSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区在该时段的限速，默认单位：像素(km/h)，可通过unit参数设置单位

参数：   
[ in ] unit：单位参数，默认为Default，Metric表示米制单位(km/h)，Default表示无单位限制

举例：

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

 **def controlLength(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取事故区在该时段的控制距离（车辆距离事故区起点该距离内，强制变道），默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

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

举例：

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
        # f"获取事故时段ID={interval.intervalId()},"
          f"获取所属事故区ID={interval.accidentZoneId()},"
          f"获取事故时段开始时间={interval.startTime()},获取事故时段结束时间={interval.endTime()},"
          f"获取事故区在该时段的长度,像素制={interval.location()},米制={interval.location(UnitOfMeasure.Metric)},"
          f"获取事故区在该时段的限速，像素制 km/h={interval.limitedSpeed()},米制={interval.limitedSpeed(UnitOfMeasure.Metric)},"
          f"获取事故区在该时段的控制距离（车辆距离事故区起点该距离内，强制变道），像素制 km/h={interval.controlLength()},"
          f"米制={interval.controlLength(UnitOfMeasure.Metric)},"
          f"获取事故区在该时段的占用车道序号={interval.laneNumbers()}")
```



------

### 2.24. IRoadWorkZone

施工区接口，方法如下：

 **def id(self) -> int: ...**

获取当前施工区ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区名称={roadWorkZone.name()}")
```

 **def location(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取施工区距所在路段起点的距离，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区距所在路段起点的距离={roadWorkZone.location()}")
    print(f"施工区距所在路段起点的距离={roadWorkZone.location(UnitOfMeasure.Metric)}")
```

 **def zoneLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取施工区长度，默认单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区长度={roadWorkZone.zoneLength()}")
    print(f"施工区长度={roadWorkZone.zoneLength(UnitOfMeasure.Metric)}")
```

 **def limitSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

施工区限速（最大车速:像素/秒（km/h））  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区所在路段或连接段名称={roadWorkZone.sectionName()}")
```

 **def sectionType(self) -> str: ...**

获取施工区所在道路的道路类型，link:路段, connector:连接段

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区所占的车道ID列表={roadWorkZone.laneObjectIds()}")
```

 **def upCautionLength(self, unit:UnitOfMeasure) ->float: ...**

获取施工区上游警示区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区上游警示区长度={roadWorkZone.upCautionLength()}")
    print(f"施工区上游警示区长度={roadWorkZone.upCautionLength(UnitOfMeasure.Metric)}")
```

 **def upTransitionLength(self, unit:UnitOfMeasure) ->float: ...**

获取施工区上游过渡区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区上游过渡区长度={roadWorkZone.upTransitionLength()}")
    print(f"施工区上游过渡区长度={roadWorkZone.upTransitionLength(UnitOfMeasure.Metric)}")
```

 **def upBufferLength(self, unit:UnitOfMeasure) ->float: ...**

获取施工区上游缓冲区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区上游缓冲区长度={roadWorkZone.upBufferLength()}")
    print(f"施工区上游缓冲区长度={roadWorkZone.upBufferLength(UnitOfMeasure.Metric)}")
```

 **def downTransitionLength(self, unit:UnitOfMeasure) ->float: ...**

获取施工区下游过渡区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lRoadWorkZones = netiface.roadWorkZones()
for roadWorkZone in lRoadWorkZones:
    print(f"施工区下游过渡区长度={roadWorkZone.downTransitionLength()}")
    print(f"施工区下游过渡区长度={roadWorkZone.downTransitionLength(UnitOfMeasure.Metric)}")
```

 **def downTerminationLength(self, unit:UnitOfMeasure) ->float: ...**

获取施工区下游终止区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

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

施工持续时间，单位：秒。自仿真过程创建后，持续时间大于此值，则移除

举例：

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

举例：

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

    print(f"roadWorkZones={roadworkzone.id()},获取施工区名称={roadworkzone.name()}"
          f",获取施工区距所在路段起点的距离,像素制={roadworkzone.location()}, 米制={roadworkzone.location(UnitOfMeasure.Metric)},"
          f"获取施工区长度,像素制={roadworkzone.zoneLength()},米制={roadworkzone.zoneLength(UnitOfMeasure.Metric)},"
          f"获取施工区限速，像素制={roadworkzone.limitSpeed()}, 米制={roadworkzone.limitSpeed(UnitOfMeasure.Metric)},"
          f"获取施工区所在路段或连接段的ID={roadworkzone.sectionId()},"
          f"获取施工区所在路段或连接段的名称={roadworkzone.sectionName()},"
          f"获取施工区所在道路的道路类型，link:路段, connector:连接段={roadworkzone.sectionType()},"
          f"获取施工区所占的车道列表={roadworkzone.laneObjects()},"
          f"获取施工区所占的车道ID列表={roadworkzone.laneObjectIds()},"
          f"获取施工区上游警示区长度,像素制={roadworkzone.upCautionLength()}， 米制={roadworkzone.upCautionLength(UnitOfMeasure.Metric)},"
          f"获取施工区上游过渡区长度,像素制={roadworkzone.upTransitionLength()}， 米制={roadworkzone.upTransitionLength(UnitOfMeasure.Metric)},"
          f"获取施工区上游缓冲区长度,像素制={roadworkzone.upBufferLength()},米制={roadworkzone.upBufferLength(UnitOfMeasure.Metric)},"
          f"获取施工区下游过渡区长度，,像素制={roadworkzone.downTransitionLength()},米制={roadworkzone.downTransitionLength(UnitOfMeasure.Metric)},"
          f"施工持续时间，单位：秒。自仿真过程创建后，持续时间大于此值，则移除={roadworkzone.duration()},"
          f"获取施工区是否被借道={roadworkzone.isBorrowed()}")



```








### 2.25. ILimitedZone

限行区接口（借道施工的被借车道，限制对向车辆行走的区域），方法如下：

 **def id(self) -> int: ...**

获取限行区ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones:
    print(f"限行区名称={limitedZone.name()}")
```

 **def location(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取距起点距离，单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones:
    print(f"限行区距起点距离={limitedZone.location()}")
    print(f"限行区距起点距离={limitedZone.location(UnitOfMeasure.Metric)}")
```

 **def zoneLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取限行区长度，单位：像素  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones:
    print(f"限行区长度={limitedZone.zoneLength()}")
    print(f"限行区长度={limitedZone.zoneLength(UnitOfMeasure.Metric)}")
```

 **def limitSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取限行区限速（最大限速），单位：像素（千米/小时）可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones:
    print(f"限行区所在路段或连接段名称={limitedZone.sectionName()}")
```

 **def sectionType(self) -> str: ...**

获取限行区所在道路的类型： "link"表示路段，"connector"表示连接段

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ILimitedZone
lLimitedZones = netiface.limitedZones()
for limitedZone in lLimitedZones:
    print(f"限行区所在车道对象列表={limitedZone.laneObjects()}")
```

 **def duration(self) -> int: ...**

获取限行区的持续时间，单位：秒

举例：

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
    print(f"获取限行区ID={limitedZone.id()},获取限行区名称={limitedZone.name()}"
          f",获取限行区距所在路段起点的距离,像素制={limitedZone.location()}, 米制={limitedZone.location(UnitOfMeasure.Metric)},"
          f"获取限行区长度,像素制={limitedZone.zoneLength()},米制={limitedZone.zoneLength(UnitOfMeasure.Metric)},"
          f"获取限行区限速，像素制={limitedZone.limitSpeed()}, 米制={limitedZone.limitSpeed(UnitOfMeasure.Metric)},"
          f"获取路段或连接段ID={limitedZone.sectionId()},"
          f"获取Section名称={limitedZone.sectionName()},"
          f"获取道路类型，link表示路段，connector表示连接段={limitedZone.sectionType()},"
          f"获取相关车道对象列表={limitedZone.laneObjects()},"
          f"获取限行持续时间，单位：秒。自仿真过程创建后，持续时间大于此值则删除={limitedZone.duration()},")
```







### 2.26. IReconstruction

改扩建接口， 此接口最好是在构造路网的最后调用，避免后续其他接口调用原因导致创建施工区的路段线性被更改

 **def id(self) -> int: ...**

获取改扩建对象ID

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions:
    print(f"改扩建对象的被借道限行区ID={reconstruction.limitedZoneId()}")
```

 **def passagewayLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取改扩建对象的保通长度，单位：像素，可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

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

获取改扩建的持续时间，单位：秒

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IRoadWorkZone
lReconstructions = netiface.reconstructions()
for reconstruction in lReconstructions:
    print(f"改扩建的借道车道数量={reconstruction.borrowedNum()}")
```

 **def passagewayLimitedSpeed(self, unit:Tess.UnitOfMeasure) -> float: ...**

获取保通开口限速，默认单位：像素/秒（km/h），可通过unit参数设置单位  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

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

举例：

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

        print(f"获取改扩建ID={reconstruction.id()},获取改扩建起始施工区ID={reconstruction.roadWorkZoneId()}"
              f",获取被借道限行区ID={reconstruction.limitedZoneId()},"
              f"获取保通长度,像素制={reconstruction.passagewayLength()},米制={reconstruction.passagewayLength(UnitOfMeasure.Metric)},"
              f"获取保通开口限速，像素制={reconstruction.passagewayLimitedSpeed()}, 米制={reconstruction.passagewayLimitedSpeed(UnitOfMeasure.Metric)},"
              f"获取改扩建持续时间={reconstruction.duration()},"
              f"获取借道数量={reconstruction.borrowedNum()},"
              f"获取改扩建动态参数，返回参数为米制={reconstruction.dynaReconstructionParam()},")
```



### 2.27. IReduceSpeedArea

限速区接口

 **def id(self) -> int: ...**

获取限速区ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"限速区名称={reduceSpeedArea.name()}")
```

 **def location(self, unit: UnitOfMeasure) -> float: ...**

获取距起点距离，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"限速区距起点距离={reduceSpeedArea.location()}")
    print(f"限速区距起点距离={reduceSpeedArea.location(UnitOfMeasure.Metric)}")
```

 **def areaLength(self, unit: UnitOfMeasure) -> float: ...**

获取限速区长度，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，Metric表示米制单位，Default表示无单位限制

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"限速区车道序号={reduceSpeedArea.laneNumber()}")
```

 **def toLaneNumber(self) -> int: ...**

获取限速区获取目标车道序号（当限速区设置在连接段时，返回值非空）

举例：

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

参数：  
[ in ]  param：限速时段参数，入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例：

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

 **def removeReduceSpeedInterval(self, id:int) -> None: ...**

移除限速时段

参数：  
[ in ] id：限速时段ID

举例：

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

参数：  
[ in ]  param：限速时段参数，入参数据结构见pyi文件的 Online.DynaReduceSpeedIntervalParam类 

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"获取限速时段列表={reduceSpeedArea.reduceSpeedIntervals()}")
```

 **def findReduceSpeedIntervalById(self, id:int) -> Tess.IReduceSpeedInterval: ...**

根据ID获取限速时段

参数：  
[ in ] id：限速时段ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"根据ID获取限速时段={reduceSpeedArea.findReduceSpeedIntervalById(reduceSpeedArea.reduceSpeedIntervals()[0].id())}")
```

 **def findReduceSpeedIntervalByStartTime(self, startTime:int) -> Tess.IReduceSpeedInterval: ...**

根据起始时间获取限速时段

参数：  
[ in ] startTime：起始时间


举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有IReduceSpeedArea
lReduceSpeedAreas = netiface.reduceSpeedAreas()
for reduceSpeedArea in lReduceSpeedAreas:
    print(f"获取限速区获取多边型轮廓={reduceSpeedArea.polygon()}")
```

### 2.28. IReduceSpeedInterval

限速时段接口

 **def id(self) -> int: ...**

获取限速时段ID

举例：

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

举例：

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

举例：

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

举例：

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

 **def addReduceSpeedVehiType(self,param:Online.DynaReduceSpeedVehiTypeParam) -> Tess.IReduceSpeedVehiType: ...**

添加限速车型

参数：  
[ in ] param：限速车型参数，数据结构见Online.DynaReduceSpeedVehiTypeParam

举例：

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

 **def removeReduceSpeedVehiType(self,id:int) -> bool: ...**

移除限速车型

参数：  
[ in ]  id：限速车型ID

举例：

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

 **def updateReduceSpeedVehiType(self,param:Online.DynaReduceSpeedVehiTypeParam) -> Tess.IReduceSpeedVehiType: ...**

更新限速车型

参数：  
[ in ] param：限速车型参数，数据结构见Online.DynaReduceSpeedVehiTypeParam

举例：

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

举例：

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

 **def findReduceSpeedVehiTypeById(self,id:int) -> Tess.IReduceSpeedVehiType: ...**

根据车型代码获取限速车型

参数：  
[ in ] id：限速车型ID

举例：

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

        print(f"获取限速区ID={reduceSpeedArea.id()},获取限速区名称={reduceSpeedArea.name()}"
              f",获取距起点距离，={reduceSpeedArea.location()},米制={reduceSpeedArea.location(UnitOfMeasure.Metric)}"
              f"获取限速区长度，像素制={reduceSpeedArea.areaLength()},米制={reduceSpeedArea.areaLength(UnitOfMeasure.Metric)},"
              f"获取路段或连接段ID={reduceSpeedArea.sectionId()},"
              f"获取车道序号={reduceSpeedArea.laneNumber()},"
              f"获取目标车道序号={reduceSpeedArea.toLaneNumber()},"
              f"获取限速时段列表={reduceSpeedArea.reduceSpeedIntervals()},"
              f"获取多边型轮廓={reduceSpeedArea.polygon()}")
        print("添加限速时段时段")
        print(f"获取所有限速时段={reduceSpeedArea.reduceSpeedIntervals()},"
              f"根据ID获取限速时段 ={reduceSpeedArea.findReduceSpeedIntervalById(reduceSpeedArea.reduceSpeedIntervals()[0].id())},"
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



### 2.29. IReduceSpeedVehiType

限速车型接口

 **def id(self) -> int: ...**

获取限速车型ID

举例：

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

举例：

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

举例：

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

举例：

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

 **def averageSpeed(self,unit:UnitOfMeasure) -> float: ...**

获取平均车速，默认单位：像素/秒(m/s)，可通过unit参数设置单位  
参数：  
[ in ] unit：单位参数，Metric表示米制单位，Default表示无单位限制

举例：

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
            print(f"获取平均车速，单位：米/秒={reduceSpeedVehiType.averageSpeed(UnitOfMeasure.Metric)}")
```

 **def speedStandardDeviation(self,unit:UnitOfMeasure) -> float: ...**

获取车速标准差，默认单位：像素/秒，可通过unit参数设置单位   
参数：  
[ in ] unit：单位参数，Metric表示米制单位，Default表示无单位限制

举例：

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
            print(f"获取车速标准差，单位：米/秒={reduceSpeedVehiType.speedStandardDeviation(UnitOfMeasure.Metric)}")
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
    print(f"获取限速时段ID={interval.id()},获取所属限速区ID={interval.reduceSpeedAreaId()},"
          f"获取开始时间={interval.intervalStartTime()},获取结束时间={interval.intervalEndTime()},")

    reduceSpeedVehiTypes = interval.reduceSpeedVehiTypes()
    reduceSpeedVehiType = interval.findReduceSpeedVehiTypeById(reduceSpeedVehiTypes[0].id())

    reduceSpeedVehiType1 = interval.findReduceSpeedVehiTypeByCode(reduceSpeedVehiTypes[0].vehiTypeCode())
```






### 2.32. ITollLane

收费车道接口

 **def id(self) -> int: ...**

获取收费车道ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    print(f"获取收费车道名称={tollLane.name()}")
```

 **def distance(self) -> float: ...**

获取收费车道起点距当前所在路段起始位置的距离。单位：米 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    print(f"获取收费车道起点距当前所在路段起始位置的距离={tollLane.distance()}")
```

 **def setName(self,name: str) -> None: ...**

设置收费车道名称  
参数：  
[ in ] name ：收费车道名称

举例：

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

设置收费车道的工作时间，不设置时，默认与仿真时间对应   
参数：  
[ in ] startTime 开始时间（秒）  
[ in ] endTime 结束时间（秒）

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    print(f"设置收费车道的工作时间={tollLane.setWorkTime(0, 3000)}")
```

 **def dynaTollLane(self) -> typing.List<Online.TollStation.DynaTollLane>: ... **

获取动态收费车道信息, 具体数据结构见Online.TollStation.DynaTollLane  
\[out\] 返回动态收费车道信息

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollLane
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    print(f"获取动态收费车道信息={tollLane.dynaTollLane()}")
```

 **def tollPoints(self) -> typing.List<Tessng.ITollPoint>: ... **

获取收费车道所有收费点位  
\[out\] 返回所有收费点位

举例：

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
    print(f"获取收费车道ID={r.id()},获取收费车道名称={r.name()}"
          f",获取距路段起始位置，单位：米={r.distance()},"
          f"设置收费车道名称={r.setName('test lane toll')},"
          f"设置工作时间，工作时间与仿真时间对应={r.setWorkTime(0, 3000)},"
          f"获取动态收费车道信息={r.dynaTollLane()},"
          f"获取收费车道所有收费点={r.tollPoints()},")
```








### 2.33. ITollDecisionPoint

收费决策点接口

 **def id(self) -> int: ...**

获取收费决策点ID 

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints:
    print(f"获取收费决策点所在路段={tollDecisionPoint.link()}")
```

 **def distance(self) ->float: ...**

获取收费决策点距离所在路段起点的距离，默认单位为米

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints:
    print(f"获取收费决策点的所有收费路径={tollDecisionPoint.routings()}")
```

 **def tollDisInfoList(self) ->Type.List<Online.TollStation.TollDisInfo>: ...**

获取收费决策点收费路径分配信息列表  
返回值是 TollDisInfo ; 数据结构见Online.TollStation.TollDisInfo

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
# 获取路网中的所有ITollDecisionPoint
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints:
    print(f"获取收费决策点收费路径分配信息列表={tollDecisionPoint.tollDisInfoList()}")
```

 **def updateTollDisInfoList(self,tollDisInfoList: Type.List<Online.TollStation.DynaTollDisInfo>) ->None: ...**

更新收费分配信息列表, 先创建决策点，再更新决策点的车道分配信息  
参数：  
[ in ] Online::TollStation::DynaTollDisInfo：收费分配信息列表 数据结构见Online.TollStation.DynaTollDisInfo

举例：

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

举例：

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

    print(f"获取收费决策点ID={r.id()},获取收费决策点名称={r.name()},获取收费决策点所在路段={r.link()},"
          f",获取距路段起始位置，单位：米={r.distance()},"
          f"获取相关收费路径={r.routings()},"
          f"获取收费分配信息列表={r.tollDisInfoList()},"
          f"更新收费分配信息列表={r.updateTollDisInfoList(r.tollDisInfoList())}," 
          f"获取收费决策点多边型轮廓={r.polygon()},")
```




### 2.34. ITollRouting

收费路径接口

 **def id(self) -> int: ...** 

获取收费路径ID 

举例：

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

举例：

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

举例：

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

 **def calcuLength(self) -> float: ...**

获取收费决策路径长度，单位：米； 收费路径长度是指：收费决策点到收费车道

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollDecisionPoints = netiface.tollDecisionPoints()
for tollDecisionPoint in tollDecisionPoints:
    # 获取路网中的所有ITollRouting
    tollRoutings = tollDecisionPoint.routings()
    for tollRouting in tollRoutings:
    print(f"获取收费决策路径长度，单位：米={tollRouting.calcuLength()}")
```

 **def contain(self, pRoad: Tessng.ISection) -> boolen: ...**

判断输入的路段是否在当前路径上  
\[in\] pRoad ：路段或连接段

举例：

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

 **def nextRoad(self,pRoad: Tessng.ISection) -> Tessng.ISection: ...**

获取输入路段的紧邻下游道路  
\[in\] pRoad ：路段或连接段

举例：

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

举例：

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
    print(f"获取路径ID={r.id()},获取所属收费决策点ID={r.tollDeciPointId()},"
          f",计算路径长度，单位：米={r.calcuLength()},"
          f"根据所给道路判断是否在当前路径上={r.contain(tollDecisionPoint.link())},"
          f"根据所给道路求下一条道路={r.nextRoad(tollDecisionPoint.link())},"
          f"获取路段序列={r.getLinks()},")
```



### 2.35. ITollPoint

收费站停车点接口

 **def id(self) -> int: ...**

获取收费站停车点位ID 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints:
    print(f"获取收费站停车点位ID={tollPoint.id()}")
```

 **def distance(self) -> float: ...**

获取收费站停车点距离路段起始位置的距离，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints:
    print(f"获取收费站停车点距离路段起始位置的距离，单位：米={tollPoint.distance()}")
```

 **def tollLaneId(self) -> int: ...**

获取收费站停车点所在的车道ID，注意不是车辆从左到右的序号

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints:
    print(f"获取是否启用的状态={tollPoint.isEnabled()}")
```

 **def setEnabled(self,enabled: bool) -> bool: ...**

设置当前收费站停车点是否启用， 返回是否设置成功的标签  
\[in\] enabled ：默认为True表示启用， 若传入False则表明禁用该收费站点

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints:
    print(f"获取收费类型={tollPoint.tollType()}")
```

 **def setTollType(self,tollType: int) -> bool: ...**

设置收费类型  
\[in\] tollType：收费类型, 数据结构 见OnLine.TollType

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
tollLanes = netiface.tollLanes()
for tollLane in tollLanes:
    tollPoints = tollLane.tollPoints()
    for tollPoint in tollPoints:
    print(f"获取停车时间分布ID={tollPoint.timeDisId()}")
```

 **def setTimeDisId(self,timeDisId: int) -> bool: ...**

设置停车时间分布ID 
\[in\] tollType： timeDisId：时间分布ID

举例：

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

    print(f"获取收费点ID={r.id()},获取所属收费车道ID={r.tollLaneId()}"
          f",获取距路段起始位置，单位：米={r.distance()},"
          f"获取是否启用={r.isEnabled()},"
          f"设置启用状态={r.setEnabled(True)},"
          f"获取收费类型={r.tollType()},设置收费类型={r.setTollType(r.tollType())},"
          f"获取停车时间分布ID={r.timeDisId()},设置停车时间分布ID={r.setTimeDisId(r.timeDisId())}")
```



### 2.36. IParkingStall

停车位接口

 **def id(self) -> int: ...**

获取停车位ID 

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls:
        print(f"获取所属停车区域={parkingStall.parkingRegion()}")
```

 **def distance(self) -> float: ...**

获取距路段起始位置，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls:
        print(f"获取距路段起始位置，单位：米={parkingStall.distance()}")
```

 **def stallType(self) -> int: ...**

获取车位类型，与车辆类型编码一致

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    parkingStalls = parkingRegion.parkingStalls()
    for parkingStall in parkingStalls:
        print(f"获取车位类型，与车辆类型编码一致={parkingStall.stallType()}")
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
        print(f"获取停车位ID={r.id()},获取所属停车区域ID={r.parkingRegionId()},"
              f"获取距路段起始位置，单位：米={r.distance()},"
              f"获取车位类型，与车辆类型编码一致={r.stallType()},")
```



### 2.37. IParkingRegion

停车区域接口

 **def id(self) -> int: ...**

获取停车区域ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    print(f"获取停车区域ID={parkingRegion.id()}")
```

 **def name(self) -> str: ...**

获取所属停车区域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    print(f"获取停车区域名称={parkingRegion.name()}")
```

 **def setName(self,name: str) -> None: ...**

设置停车区域名称  
\[in\] name ：停车区域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    print(f"设置停车区域名称={parkingRegion.setName('test parking name')}")
    print(f"获取停车区域名称={parkingRegion.name()}")
```

 **def parkingStalls(self) -> Type.List<Tess.IParkingStall>: ...**

获取所有停车位，返回列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingRegions = netiface.parkingRegions()
for parkingRegion in parkingRegions:
    print(f"获取所有停车位，返回列表={parkingRegion.parkingStalls()}")
```

 **def dynaParkingRegion(self) -> Online.ParkingLot.DynaParkingRegion : ...**

获取动态停车区域信息

举例：

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
    print(f"获取停车区域ID={r.id()},获取停车区域名称={r.name()}"
          f",获取所有停车位={r.parkingStalls()},"
          f"设置停车区域名称={r.setName('test parking name')},"
          f"获取动态停车区域信息={r.dynaParkingRegion()},")
```




### 2.38. IParkingDecisionPoint

停车决策点接口

 **def id(self) -> int: ...**

获取停车决策点ID 

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取停车决策点ID={parkingDecisionPoint.id()}")
```

 **def name(self) -> str: ...**

获取停车决策点名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取停车决策点名称={parkingDecisionPoint.name()}")
```

 **def link(self) -> Tessng.ILink: ...**

获取停车决策点所在路段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取停车决策点所在路段={parkingDecisionPoint.link()}")
```

 **def distance(self) -> float: ...**

获取停车决策点距离所在路段起点的距离，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取停车决策点距离所在路段起点的距离，单位：米={parkingDecisionPoint.distance()}")
```

 **def routings(self) -> Type.List<Tessng.IParkingRouting>: ...**

获取当前停车决策点对应的所有停车路径

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取当前停车决策点对应的所有停车路径={parkingDecisionPoint.routings()}")
```

**def updateParkDisInfo(self, tollDisInfoList:Type.List<Online.ParkingLot.DynaParkDisInfo>)->bool**

更新停车分配信息，先构建停车决策点，再通过此更新方法补充完善停车分配信息

参数：  
[ in ] tollDisInfoList：停车分配信息列表, 见pyi文件的Online.ParkingLot.DynaParkDisInfo

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"更新停车分配信息={parkingDecisionPoint.updateParkDisInfo(parkingDecisionPoint.parkDisInfoList())}")
```

**def parkDisInfoList()->Type.List<Online.ParkingLot.DynaParkDisInfo >**

获取停车分配信息列表

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    print(f"获取停车分配信息列表={parkingDecisionPoint.parkDisInfoList()}")
```

 **def polygon(self) -> QPolygonF: ...**

获取当前停车决策点多边形轮廓

举例：

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
    print(f"获取停车决策点ID={r.id()},获取停车决策点名称={r.name()}"
          f",获取停车决策点所在路段={r.link()},"
          f"获取停车决策点距路段起点距离，单位：米={r.distance()},获取停车分配信息列表={r.parkDisInfoList()},"
          f"获取相关停车路径={r.routings()},更新停车分配信息={r.updateParkDisInfo(r.parkDisInfoList())},"
          f"获取停车决策点多边型轮廓={r.polygon()}")
```




### 2.39. IParkingRouting

停车决策路径接口

 **def id(self) -> int: ...**

获取停车决策路径ID 

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings():
        print(f"获取路径到达的停车区域id={parkingRouting.parkingRegionId()}")
```

 **def calcuLength(self) -> float: ...**

获取停车决策路径的长度，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings():
        print(f"获取停车决策路径的长度，单位：米={parkingRouting.calcuLength()}")
```

 **def contain(self,pRoad: Tessng.ISection) -> boolen: ...**

判断输入的道路（ 路段或连接段）是否在当前停车决策路径上  
\[in\] pRoad ：道路对象，类型为Tessng.ISection

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
parkingDecisionPoints = netiface.parkingDecisionPoints()
for parkingDecisionPoint in parkingDecisionPoints:
    parkingDecisionPoint.routings()
    for parkingRouting in parkingDecisionPoint.routings():
        print(f"判断输入的道路（ 路段或连接段）是否在当前停车决策路径上={parkingRouting.contain(parkingDecisionPoint.link())}")
```

 **def nextRoad(self,pRoad: Tessng.ISection) -> Tessng.ISection: ...**

获取输入道路的紧邻下游道路  
\[in\] pRoad ：道路对象，类型为Tessng.ISection

举例：

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

举例：

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
    print(f"获取路径ID={r.id()},获取所属决策点ID={r.parkingDeciPointId()}"
          f",计算路径长度={r.calcuLength()},"
          f"根据所给道路判断是否在当前路径上={r.contain(parkingDecisionPoint.link())},"
          f"根据所给道路求下一条道路={r.nextRoad(parkingDecisionPoint.link())},"
          f"获取路段序列={r.getLinks()}")
```



### 2.40. IJunction

节点接口

 **def getId(self) -> int: ...**

获取节点ID   

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes:
    print(f"获取节点ID={node.getId()}")
```

 **def name(self) -> int: ...**

获取节点名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes:
    print(f"获取节点名称={node.name()}")
```

 **def setName(strName: str) -> int: ...**

设置节点名称  
\[in\] strName ：节点名称

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes:
    print(f"获取节点内的路段={node.getJunctionLinks()}")
```

 **def getJunctionConnectors(self) -> Tess.Connector: ...**

获取节点内的连接段

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes:
    print(f"获取节点内的连接段={node.getJunctionConnectors()}")
```

 **def getAllTurnningInfo(self) ->Type.List<Online.Junction.TurnningBaseInfo>: ...**

获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
nodes = netiface.getAllJunctions()
for node in nodes:
    print(f"获取节点内的流向信息={node.getAllTurnningInfo()}")
```

 **def getTurnningInfo(self) ->Online.Junction.TurnningBaseInfo: ...**

根据转向编号获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件

举例：

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
    node = netiface.findTrafficControllerByName(node.name())
    print(
        f"路网中的节点总数={len(nodes)},节点编号={nodes[0].id()}的具体信息："
        f"获取节点ID={node.getId()},名称={node.name()}, 设置新名字={node.setName('new_' + node.name())},"
        f"获取节点内的路段={node.getJunctionLinks()},"
        f"获取节点内的连接段={node.getJunctionConnectors()},"
        f"获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getAllTurnningInfo()},"
        f"根据转向编号获取节点内的流向信息， Online.Junction.TurnningBaseInfo 数据结构见 pyi文件={node.getTurnningInfo()}")


```






### 2.41. IPedestrian

行人接口

 **def getId(self) -> int: ...**

获取行人ID

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人ID={ped.getId()}")
```

 **def getRadius(self) -> float: ...**

获取行人半径大小， 单位：米

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人半径大小， 单位：米={ped.getRadius()}")
```

 **def getWeight(self) -> float: ...**

获取行人质量， 单位：千克

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人质量， 单位：千克={ped.getWeight()}")
```

 **def getColor(self) -> float: ...**

获取行人颜色， 十六进制颜色代码，如"#EE0000"

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人颜色={ped.getColor()}")
```

 **def getPos(self) -> QPointF: ...**

获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米; 

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米={ped.getPos()}")
```

 **def getAngle(self) -> float: ...**

获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度; 

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度={ped.getAngle()}")
```

 **def getDirection(self) -> Array: ...**

获取行人当前方向向量，二维向量；

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前方向向量，二维向量={ped.getDirection()}")
```

 **def getElevation(self) -> float: ...**

获取行人当前位置的高程，单位：米

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前位置的高程，单位：米={ped.getElevation()}")
```

 **def getSpeed(self) -> float: ...**

获取行人当前速度，单位：米/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前速度，单位：米/秒={ped.getSpeed()}")
```

 **def getDesiredSpeed(self) -> float: ...**

获取行人期望速度，单位：米/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人期望速度，单位：米/秒={ped.getDesiredSpeed()}")
```

 **def getMaxSpeed(self) -> float: ...**

获取行人最大速度限制，单位：米/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人最大速度限制，单位：米/秒={ped.getMaxSpeed()}")
```

 **def getAcce(self) -> float: ...**

获取行人当前加速度，单位：米/秒²

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前加速度，单位：米/秒²={ped.getAcce()}")
```

 **def getMaxAcce(self) -> float: ...**

获取行人最大加速度限制，单位：米/秒²

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人最大加速度限制，单位：米/秒²={ped.getMaxAcce()}")
```

 **def getEuler(self) -> Type.List: ...**

获取行人欧拉角，单位：度

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人欧拉角，用于三维的信息展示和计算，单位：度={ped.getEuler()}")
```

 **def getSpeedEuler(self) -> Type.List: ...**

获取行人速度欧拉角，单位：度

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人速度欧拉角，用于三维的信息展示和计算，单位：度={ped.getSpeedEuler()}")
```

 **def getWallFDirection(self) ->Type.List: ...**

获取墙壁方向单位向量

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取墙壁方向单位向量={ped.getWallFDirection()}")
```

 **def getRegion(self) -> Tess.IPedestrianRegion: ...**

获取行人当前所在面域

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人当前所在面域={ped.getRegion()}")
```

 **def getPedestrianTypeId(self) -> int: ...**

获取行人类型ID

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"获取行人类型ID={ped.getPedestrianTypeId()}")
```

 **def stop(self) -> None: ...**

停止仿真，会在下一个仿真批次移除当前行人，释放资源

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allPedestrian = simuiface.allPedestrianStarted()
for ped in allPedestrian:
    print(f"停止仿真，会在下一个仿真批次移除当前行人，释放资源={ped.stop()}")
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
            f"获取行人ID={ped.getId()},"
            f"获取行人半径大小， 单位：米={ped.getRadius()},"
            f"获取行人质量， 单位：千克={ped.getWeight()},"
            f"获取行人颜色， 十六进制颜色代码，如#EE0000={ped.getColor()},"
            f"设置面域颜色={ped.setRegionColor(QColor('red'))},"
            f"获取行人当前位置（瞬时位置），像素坐标系下的坐标点，单位：米; ={ped.getPos()},"
            f"获取行人当前角度，QT像素坐标系下，X轴正方向为0，逆时针为正，单位：度; ={ped.getAngle()},"
            f"获取行人当前方向向量，二维向量；={ped.getDirection()},"
            f"获取行人当前位置的高程，单位：米={ped.getElevation()},"
            f"获取行人当前速度，单位：米/秒={ped.getSpeed()},"
            f"获取行人期望速度，单位：米/秒={ped.getDesiredSpeed()},"
            f"获取行人最大速度限制，单位：米/秒={ped.getMaxSpeed()},"
            f"获取行人当前加速度，单位：米/秒²={ped.getAcce()},"
            f"获取行人最大加速度限制，单位：米/秒²={ped.getMaxAcce()},"
            f"获取行人欧拉角，用于三维的信息展示和计算，单位：度={ped.getEuler()},"
            f"获取行人速度欧拉角，用于三维的信息展示和计算，单位：度={ped.getSpeedEuler()},"
            f"获取墙壁方向单位向量={ped.getWallFDirection()},"
            f"获取行人当前所在面域={ped.getRegion()},"
            f"获取行人类型ID={ped.getPedestrianTypeId()},"
            f"停止当前行人仿真运动，会在下一个仿真批次移除当前行人，释放资源={ped.stop()}")

```


### 2.43. IPedestrianPathRegionBase

行人可通行路径面域基类接口，用例见下文子类

 **def getId(self) -> int: ...**

获取面域id

 **def getName(self) ->str: ...**

获取面域名称

 **def setName(self, name) ->None: ...**

设置面域名称

[in] name： 面域名称

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

 **def setRegionColor(self, color:QColor) ->None: ...**

获取面域颜色，

[in] color： 面域颜色

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类


### 2.42. IObstacleRegion

障碍物面域基类接口，用例见下文子类

 **def isObstacleRegion(self) -> bool: ...**

获取面域是否为障碍物

 **def setObstacleRegion(self，b:bool) ->None: ...**

设置面域是否为障碍物

[in] b： True表示设置为障碍物，False表示设置为非障碍物


### 2.44. IPassengerRegion

乘客面域基类接口，用例见下文子类

 **def isBoardingArea(self) -> bool: ...**

获取面域是否为上客区域

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

 **def isAlightingArea(self) -> bool: ...**

获取面域是否为下客区域

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 



### 2.53. IPedestrianRegion 

行人区域（面域）接口，用例见下文子类

 **def getId(self) -> int: ...**

获取行人区域(面域)ID

 **def getName(self) -> str: ...**

获取行人区域(面域)名称

 **def setName(self，name：str) -> None: ...**

设置行人区域(面域)名称

 **def setRegionColor(self，color:QColor) -> None: ...**

设置行人区域(面域)的颜色

 **def getPosition(self,unit:UnitOfMeasure) -> QPointF: ...**

获取面域位置，默认单位：像素，可通过unit参数设置单位， 这里范围的面域中心点的位置， QT像素坐标系  

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

 **def setPosition(self，scenePos: QPoint,unit:UnitOfMeasure) ->  None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数： 

[ in ] scenePos：场景坐标系下的位置   
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

 **def getGType(self) -> int: ...**

获取行人区域(面域)类型， 面域类型见pyi文件NetItemType类

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



### 2.54. IPedestrianSideWalkRegion

人行道区域（面域）接口

**def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域ID={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

[in] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('new_' + region.getName())}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置,米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置面域位置，像素制={region.setPosition(region.getPosition())},"
          f"设置面域位置，米制={region.setPosition(region.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation： 高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId： 图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置面域所在图层，如果图层ID非法，则不做任何改变={region.setLayerId(region.getLayerId())}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def getWidth(self) -> int: ...**

获取人行道(面域)宽度， 单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取人行道(面域)宽度， 单位：米={region.getWidth()}")
```

 **def setWidth(self，width:float) -> None: ...**

设置人行道(面域)宽度， 单位：米

参数：
[ in ] width： 宽度

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"设置人行道(面域)宽度， 单位：米={region.setWidth(region.getWidth()+0.1)}")
    print(f"获取人行道(面域)宽度， 单位：米={region.getWidth()}")
```

 **def getVetexs(self) ->  Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)顶点，即初始折线顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取人行道(面域)顶点，即初始折线顶点={region.getVetexs()}")
```

 **def getControl1Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)贝塞尔曲线控制点P1

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取人行道(面域)贝塞尔曲线控制点P1={region.getControl1Vetexs()}")
```

 **def getControl2Vetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)贝塞尔曲线控制点P2

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取人行道(面域)贝塞尔曲线控制点P2={region.getControl2Vetexs()}")
```

 **def getCandidateVetexs(self) -> Type.List<QGraphicsEllipseItem>: ...**

获取人行道(面域)候选顶点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"获取人行道(面域)候选顶点={region.getCandidateVetexs()}")
```

 **def removeVetex(self，index: int) ->None: ...**

删除人行道(面域)的第index个顶点： 顺序： 按照人行横道的绘制顺序排列

参数：
[ in ] index： 顶点索引

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"删除人行道(面域)的第index个顶点={region.removeVetex(1)}")
```

 **def insertVetex(self，pos: QPointF, index:int) ->None: ...**

在人行道(面域)的第index的位置插入顶点，初始位置为pos： 顺序： 按照人行横道的绘制顺序排列

参数：
[ in ] pos： 顶点位置
[ in ] index： 顶点索引

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianSideWalkRegions()
for region in allRegion:
    print(f"在第index个位置插入顶点，初始位置为pos={region.insertVetex(QPointF(region.getCandidateVetexs()[0].pos().x()+0.1, region.getCandidateVetexs()[0].pos().y()+0.1), 0)}")
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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")
        print(f"仿真路网中人行道区域总数={len(regions)},"
              f"获取人行道宽度={r.getWidth()},设置人行道宽度={r.setWidth(r.getWidth()+0.5)},"
              f"获取人行道顶点，即初始折线顶点={r.getVetexs()},获取人行道贝塞尔曲线控制点P1={r.getControl1Vetexs()},"
              f"获取人行道贝塞尔曲线控制点P2={r.getControl2Vetexs()}, 获取候选顶点={r.getCandidateVetexs()}")
        print(f"在第index个位置插入顶点，初始位置为pos={r.insertVetex(QPointF(r.getCandidateVetexs()[0].pos().x()+0.1, r.getCandidateVetexs()[0].pos().y()+0.1), 0)},"
              f"删除第index个顶点={r.removeVetex(1)}")
        #
        print(f"在第index个位置插入顶点，初始位置为pos={r.insertVetex(QPointF(100,100), 0)},"
              f"删除第index个顶点={r.removeVetex(1)}")

```






### 2.45. IPedestrianCrossWalkRegion

人行横道区域接口

**def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置面域所在图层={region.setLayerId(1)}")
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def getWidth(self) -> float: ...**

获取人行横道宽度，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道宽度，单位：米={region.getWidth()}")
```

 **def setWidth(self, width:float) -> float: ...**

设置行人横道宽度，单位：米

参数：
[ in ] width：宽度

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置行人横道宽度，单位：米={region.setWidth(1.5)}")
    print(f"获取行人横道宽度，单位：米={region.getWidth()}")
```

 **def getSceneLine(self, unit:UnitOfMeasure) -> QLineF: ...**  

获取人行横道起点到终点的线段，QT场景坐标系，场景坐标系下，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道起点到终点的线段，场景坐标系，默认单位：像素={region.getSceneLine()}")
    print(f"获取人行横道起点到终点的线段，场景坐标系，米制={region.getSceneLine(UnitOfMeasure.Metric)}")
```

 **def getAngle(self) -> float: ...**  

获取人行横道倾斜角度，单位：度， QT像素坐标系下，X轴正方向为0，逆时针为正

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道倾斜角度，单位：度={region.getAngle()}")
```

 **def setAngle(self, angle:float) -> None: ...**

设置人行横道倾斜角度，单位：度， QT像素坐标系下，X轴正方向为0，逆时针为正

参数：
[ in ] angle：角度

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置人行横道倾斜角度，单位：度={region.setAngle(5)}")
    print(f"获取人行横道倾斜角度，单位：度={region.getAngle()}")
```

 **def getRedLightSpeedFactor(self) -> float: ...**

获取人行横道上红灯清尾速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道上红灯清尾速度系数={region.getRedLightSpeedFactor()}")
```

 **def setRedLightSpeedFactor(self, factor:float) -> None: ...**

设置人行横道上红灯清尾速度系数  

参数：
[ in ] factor ：红灯清尾速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"设置人行横道上红灯清尾速度系数={region.setRedLightSpeedFactor(1.5)}")
    print(f"获取人行横道上红灯清尾速度系数={region.getRedLightSpeedFactor()}")
```

 **def getUnitDirectionFromStartToEnd(self) -> Type.List: ...**

获取人行横道起点到终点的在场景坐标系下的单位方向向量，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道起点到终点的在场景坐标系下的单位方向向量={region.getUnitDirectionFromStartToEnd()}")
```

 **def getLocalUnitDirectionFromStartToEnd(self) -> Type.List: ...**

获取人行横道本身坐标系下从起点到终点的单位方向

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道本身坐标系下从起点到终点的单位方向={region.getLocalUnitDirectionFromStartToEnd()}")
```

 **def getStartControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道起点控制点，场景坐标系

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道起点控制点，场景坐标系={region.getStartControlPoint()}")
```

 **def getEndControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道终点控制点，场景坐标系

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道终点控制点，场景坐标系下={region.getEndControlPoint()}")
```

 **def getLeftControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道左侧控制点，场景坐标系

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道左侧控制点，场景坐标系={region.getLeftControlPoint()}")
```

 **def getRightControlPoint(self) -> QGraphicsEllipseItem: ...**

获取人行横道右侧控制点，场景坐标系

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道右侧控制点，场景坐标系={region.getRightControlPoint()}")
```

 **def getPositiveDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**

获取人行横道上管控正向通行的信号灯对象

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道上管控正向通行的信号灯对象={region.getPositiveDirectionSignalLamp()}")
```

 **def getNegativeDirectionSignalLamp(self) -> Tessng.ICrosswalkSignalLamp: ...**

获取人行横道上管控反向通行的信号灯对象

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"获取人行横道上管控反向通行的信号灯对象={region.getNegativeDirectionSignalLamp()}")
```

 **def isPositiveTrafficLightAdded(self) -> boolen: ...**

判断人行横道上是否存在管控正向通行的信号灯

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianCrossWalkRegions()
for region in allRegion:
    print(f"判断人行横道上是否存在管控正向通行的信号灯={region.isPositiveTrafficLightAdded()}")
```

 **def isReverseTrafficLightAdded(self) -> boolen: ...**

判断人行横道上是否存在管控反向通行的信号灯

举例：

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
              f"获取面域ID={crossWalkRegion.getId()},"
              f"获取面域名称={crossWalkRegion.getName()},"
              f"设置面域名称={crossWalkRegion.setName('test_area')}," 
              f"获取面域颜色={crossWalkRegion.getRegionColor()},"
              f"设置面域颜色={crossWalkRegion.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={crossWalkRegion.getPosition()},"
              f"获取面域位置,米制={crossWalkRegion.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={crossWalkRegion.setPosition(crossWalkRegion.getPosition())},"
              f"设置面域位置，米制={crossWalkRegion.setPosition(crossWalkRegion.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={crossWalkRegion.getGType()},"
              f"获取期望速度系数={crossWalkRegion.getExpectSpeedFactor()},"
              f"设置期望速度系数={crossWalkRegion.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={crossWalkRegion.getElevation() },"
              f"设置面域高程={crossWalkRegion.setElevation(0.1)},"
              f"获取面域多边形={crossWalkRegion.getPolygon()}," 
              f"获取面域所在图层ID={crossWalkRegion.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={crossWalkRegion.setLayerId(crossWalkRegion.getLayerId())}")
        print(f"仿真路网中人行横道区域总数={len(allCrossWalkRegion)},"
              f"获取人行横道宽度，单位：米={crossWalkRegion.getWidth()},设置人行横道宽度，单位：米={crossWalkRegion.setWidth(crossWalkRegion.getWidth()+0.1)},"
              f"获取人行横道起点到终点的线段，场景坐标系下={crossWalkRegion.getSceneLine()},获取人行横道倾斜角度={crossWalkRegion.getAngle()},"
              f"设置人行横道倾斜角度={crossWalkRegion.setAngle(5)}, 获取红灯清尾速度系数={crossWalkRegion.getRedLightSpeedFactor()},"
              f"设置红灯清尾速度系数={crossWalkRegion.setRedLightSpeedFactor(1.5)},"
              f"获取场景坐标系下从起点到终点的单位方向向量={crossWalkRegion.getUnitDirectionFromStartToEnd()},"
              f"获取人行横道本身坐标系下从起点到终点的单位方向={crossWalkRegion.getLocalUnitDirectionFromStartToEnd()},"
              f"获取起点控制点={crossWalkRegion.getStartControlPoint()},"
              f"获取终点控制点={crossWalkRegion.getEndControlPoint()},"
              f"获取左侧控制点={crossWalkRegion.getLeftControlPoint()},"
              f"获取右侧控制点={crossWalkRegion.getRightControlPoint()},"
              f"判断是否添加了管控正向通行的信号灯={crossWalkRegion.isPositiveTrafficLightAdded()},"
              f"判断是否添加了管控反向通行的信号灯={crossWalkRegion.isReverseTrafficLightAdded()},"
              f"获取管控正向通行的信号灯={crossWalkRegion.getPositiveDirectionSignalLamp()},"
              f"获取管控反向通行的信号灯={crossWalkRegion.getNegativeDirectionSignalLamp()},")
```

### 2.46. IPedestrianEllipseRegion

行人椭圆面域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self,b:bool) -> None: ...**

设置面域是否为障碍物

参数：
[ in ] b：是否为障碍物

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

参数：
[ in ] b：是否为上客区域

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianEllipseRegions()
for region in allRegion:
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 

参数：
[ in ] b：是否为下客区域

举例：

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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
              f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
              f"获取面域是否为下客区域={r.isAlightingArea()}" 
              f"仿真路网中pedestrianEllipseRegions总数={len(areas)}")
```






### 2.47. IPedestrianFanShapeRegion

行人扇形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self,b:bool) -> None: ...**

设置面域是否为障碍物

参数：
[ in ] b：是否为障碍物

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

参数：
[ in ] b：是否为上客区域

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 

参数：
[ in ] b：是否为下客区域

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"设置面域是否为下客区域={region.setIsAlightingArea(True)}")
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
``` 

**def getInnerRadius(self) -> float: ...**

获取扇形面域内半径，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取扇形面域内半径={region.getInnerRadius()}")
```

 **def getOuterRadius(self) -> float: ...**

获取扇形面域外半径，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取扇形面域外半径={region.getOuterRadius()}")
```

 **def getStartAngle(self) -> float: ...**

获取扇形面域起始角度，单位：度  QT像素坐标系下，X轴正方向为0，逆时针为正

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianFanShapeRegions()
for region in allRegion:
    print(f"获取扇形面域起始角度={region.getStartAngle()}")
```

 **def getSweepAngle(self) -> float: ...**

获取扇形面域扫过角度，单位：度  QT像素坐标系下，X轴正方向为0，逆时针为正

举例：

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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")
```

### 2.48.IPedestrianPolygonRegion

行人多边形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self,b:bool) -> None: ...**

设置面域是否为障碍物

参数：
[ in ] b：是否为障碍物

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

参数：
[ in ] b：是否为上客区域

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianPolygonRegions()
for region in allRegion:
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 

参数：
[ in ] b：是否为下客区域

举例：

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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")
```



### 2.49. IPedestrianRectRegion

行人矩形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self,b:bool) -> None: ...**

设置面域是否为障碍物

参数：
[ in ] b：是否为障碍物

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

参数：
[ in ] b：是否为上客区域

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianRectRegions()
for region in allRegion:
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 

参数：
[ in ] b：是否为下客区域

举例：

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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())}")
```

### 2.50. IPedestrianTriangleRegion

行人三角形面域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")
```

 **def getExpectSpeedFactor(self) -> float: ...**

获取期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def setExpectSpeedFactor(self，val:float) -> None: ...**

设置期望速度系数

参数：
[ in ] val：期望速度系数

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"设置期望速度系数={region.setExpectSpeedFactor(1.5)}")
    print(f"获取期望速度系数={region.getExpectSpeedFactor()}")
```

 **def getElevation(self) -> float: ...**

获取面域高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域高程={region.getElevation()}")
```

 **def setElevation(self，elevation:float) -> None: ...**

设置面域高程

参数：
[ in ] elevation：高程

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"设置面域高程={region.setElevation(0.1)}")
    print(f"获取面域高程={region.getElevation()}")
```

 **def getPolygon(self) -> float: ...**

获取面域多边形

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域多边形={region.getPolygon()}")
```

 **def getLayerId(self) -> float: ...**

获取面域所在图层ID

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域所在图层ID={region.getLayerId()}")
```

 **def setLayerId(self，elevation:float) -> None: ...**

设置面域所在图层，如果图层ID非法，则不做任何改变

参数：
[ in ] layerId：图层ID

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域是否为障碍物={region.isObstacle()}")
```

 **def setObstacle(self,b:bool) -> None: ...**

设置面域是否为障碍物

参数：
[ in ] b：是否为障碍物

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域是否为上客区域={region.isBoardingArea()}")
```

 **def setIsBoardingArea(self, b:bool) -> None: ...**

设置面域是否为上客区域

参数：
[ in ] b：是否为上客区域

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianTriangleRegions()
for region in allRegion:
    print(f"获取面域是否为下客区域={region.isAlightingArea()}")
```

 **def setIsAlightingArea(self, b:bool) -> None: ...**

设置面域是否为下客区域 

参数：
[ in ] b：是否为下客区域

举例：

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
              f"获取面域ID={r.getId()},"
              f"获取面域名称={r.getName()},"
              f"设置面域名称={r.setName('test_area')}," 
              f"获取面域颜色={r.getRegionColor()},"
              f"设置面域颜色={r.setRegionColor(QColor('red'))},"
              f"获取面域位置，默认单位：像素={r.getPosition()},"
              f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
              f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
              f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
              f"获取面域类型={r.getGType()},"
              f"获取期望速度系数={r.getExpectSpeedFactor()},"
              f"设置期望速度系数={r.setExpectSpeedFactor(1.5)},"
              f"获取面域高程={r. getElevation() },"
              f"设置面域高程={r. setElevation(0.1)},"
              f"获取面域多边形={r.getPolygon()}," 
              f"获取面域所在图层ID={r.getLayerId() },设置面域所在图层，如果图层ID非法，则不做任何改变={r.setLayerId(r.getLayerId())},"
              f"获取面域是否为障碍物={r.isObstacle()},获取面域是否为上客区域={r.isBoardingArea()},"
              f"获取面域是否为下客区域={r.isAlightingArea()}" 
              f"仿真路网中pedestrianTriangleRegions总数={len(areas)}")

```





### 2.55. IPedestrianStairRegion

楼梯区域接口

 **def getId(self) -> int: ...**

获取面域id

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取面域id={region.getId()}")
```

 **def getName(self) ->str: ...**

获取面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取面域名称={region.getName()}")
```

 **def setName(self, name) ->None: ...**

设置面域名称

参数：
[ in ] name： 面域名称

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置面域名称={region.setName('test_area')}")
    print(f"获取面域名称={region.getName()}")
```

 **def getRegionColor(self) ->QColor: ...**

获取面域颜色，返回pyside2的QColor类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def setRegionColor(self, color:QColor) ->None: ...**

设置面域颜色

参数：
[ in ] color： 面域颜色

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置面域颜色={region.setRegionColor(QColor('red'))}")
    print(f"获取面域颜色={region.getRegionColor()}")
```

 **def getPosition(self,unit:UnitOfMeasure) ->QPointF: ...**

获取面域位置，默认单位：像素，可通过unit设置单位

参数：
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def setPosition(self, scenePos:QPointF, unit:UnitOfMeasure) ->None: ...**

设置面域位置，默认单位：像素，可通过unit参数设置单位

参数：
[ in ] scenePos：场景坐标系下的位置
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    region.setPosition(QPointF(100,100))
    print(f"获取面域位置，默认单位：像素={region.getPosition()}")
    region.setPosition(QPointF(100,100), UnitOfMeasure.Metric)
    print(f"获取面域位置，米制={region.getPosition(UnitOfMeasure.Metric)}")
```

 **def getGType(self) ->int: ...**

获取面域类型，面域类型见pyi文件NetItemType类

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取面域类型={region.getGType()}")

 **def getWidth(self) -> int: ...**

获取楼梯宽度， 单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯宽度，单位：米={region.getWidth()}")
```

 **def setWidth(self，width:float) -> None: ...**

设置楼梯(面域)宽度， 单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置楼梯宽度，单位：米={region.setWidth(region.getWidth()+0.2)}")
    print(f"获取楼梯宽度，单位：米={region.getWidth()}")
```

 **def getStartPoint(self) -> QPointF: ...**

获取楼梯起始点，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯起始点，场景坐标系下={region.getStartPoint()}")
```

 **def getEndPoint(self) -> QPointF: ...**

获取楼梯终止点，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯终止点，场景坐标系下={region.getEndPoint()}")
```

 **def getStartConnectionAreaLength(self) -> float: ...**

获取起始衔接区域长度，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取起始衔接区域长度，单位：米={region.getStartConnectionAreaLength()}")
```

 **def getEndConnectionAreaLength(self) -> float: ...**

获取终止衔接区域长度，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取终止衔接区域长度，单位：米={region.getEndConnectionAreaLength()}")
```

 **def getStartRegionCenterPoint(self) -> QPointF: ...**

获取起始衔接区域中心，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取起始衔接区域中心，场景坐标系下={region.getStartRegionCenterPoint()}")
```

 **def getEndRegionCenterPoint(self) -> QPointF: ...**

获取终止衔接区域中心，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取终止衔接区域中心，场景坐标系下={region.getEndRegionCenterPoint()}")
```

 **def getStartSceneRegion(self) -> QPainterPath: ...**

获取起始衔接区域形状，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取起始衔接区域形状，场景坐标系下={region.getStartSceneRegion()}")
```

 **def getEndSceneRegion(self) -> QPainterPath: ...**

获取终止衔接区域形状，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取终止衔接区域形状，场景坐标系下={region.getEndSceneRegion()}")
```

 **def getMainQueueRegion(self) -> QPainterPath: ...**

获取楼梯主体形状，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯主体形状，场景坐标系下={region.getMainQueueRegion()}")
```

 **def getFullQueueregion(self) -> QPainterPath: ...**

获取楼梯整体形状，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯整体形状，场景坐标系下={region.getFullQueueregion()}")
```

 **def getMainQueuePolygon(self) -> QPolygonF : ...**

获取楼梯主体多边形，场景坐标系下

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯主体多边形，场景坐标系下={region.getMainQueuePolygon()}")
```

 **def getStairType(self) -> Tessng.StairType: ...**

获取楼梯类型, 类型枚举说明， 参见pyi的 StariType类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯类型={region.getStairType()}")
```

 **def setStairType(self，type:StairType) -> None: ...**

设置楼梯类型, 类型枚举说明， 参见pyi的 StariType类型

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置楼梯类型={region.setStairType(region.getStairType())}")
```

 **def getStartLayerId(self) -> int: ...**

获取楼梯的起始层级

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的起始层级={region.getStartLayerId()}")
```

 **def setStartLayerId(self,id:int) -> None: ...**

设置楼梯的起始层级

举例：

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

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的终止层级={region.getEndLayerId()}")
```

 **def setEndLayerId(self,id:int) -> None: ...**

设置楼梯的终止层级

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置楼梯的终止层级={region.setEndLayerId(region.getEndLayerId())}")
    print(f"获取楼梯的终止层级={region.getEndLayerId()}")
```

 **def getTransmissionSpeed(self) -> float: ...**

获取楼梯传输速度，单位米/秒， 如果是步行楼梯，则返回值应该是0

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯传输速度，单位米/秒={region.getTransmissionSpeed()}")
```

 **def setTransmissionSpeed(self,speed:float) -> None: ...**

设置楼梯传输速度，单位米/秒

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置楼梯传输速度，单位米/秒={region.setTransmissionSpeed(region.getTransmissionSpeed())}")
    print(f"获取楼梯传输速度，单位米/秒={region.getTransmissionSpeed()}")
```

 **def getHeadroom(self) -> float: ...**

获取楼梯净高，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯净高，单位：米={region.getHeadroom()}")
```

 **def setHeadroom(self,headroom:float) -> None: ...**

设置楼梯净高，单位：米

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"设置楼梯净高，单位：米={region.setHeadroom(region.getHeadroom() + 0.2)}")
    print(f"获取楼梯净高，单位：米={region.getHeadroom()}")
```

 **def getStartControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的起点控制点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的起点控制点={region.getStartControlPoint()}")
```

 **def getEndControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的终点控制点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的终点控制点={region.getEndControlPoint()}")
```

 **def getLeftControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的左侧控制点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的左侧控制点={region.getLeftControlPoint()}")
```

 **def getRightControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的右侧控制点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的右侧控制点={region.getRightControlPoint()}")
```

 **def getStartConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的起始衔接区域长度控制点

举例：

```python
iface = tessngIFace()
netiface = iface.netInterface()
allRegion = netiface.pedestrianStairRegions()
for region in allRegion:
    print(f"获取楼梯的起始衔接区域长度控制点={region.getStartConnectionAreaControlPoint()}")
```

 **def getEndConnectionAreaControlPoint(self) -> QGraphicsEllipseItem: ...**

获取楼梯的终止衔接区域长度控制点

举例：

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
            f"获取面域ID={r.getId()},"
            f"获取面域名称={r.getName()},"
            f"设置面域名称={r.setName('test_area')},"
            f"获取面域颜色={r.getRegionColor()},"
            f"设置面域颜色={r.setRegionColor(QColor('red'))},"
            f"获取面域位置，默认单位：像素={r.getPosition()},"
            f"获取面域位置,米制={r.getPosition(UnitOfMeasure.Metric)},"
            f"设置面域位置，像素制={r.setPosition(r.getPosition())},"
            f"设置面域位置，米制={r.setPosition(r.getPosition(UnitOfMeasure.Metric), UnitOfMeasure.Metric)},"
            f"获取面域类型={r.getGType()},")

        print(f"仿真路网中楼梯区域总数={len(stairRegions)},"
              f"获取楼梯宽度，单位：米={r.getWidth()},设置楼梯宽度，单位：米度={r.setWidth(r.getWidth()+0.2)},"
              f"获取起始点，场景坐标系下={r.getStartPoint()},获取终止点，场景坐标系下={r.getEndPoint()},"
              f"获取起始衔接区域长度，单位：米={r.getStartConnectionAreaLength()}, 获取终止衔接区域长度，单位：米={r.getEndConnectionAreaLength()},"
              f"获取起始衔接区域中心，场景坐标系下={r.getStartRegionCenterPoint()},获取终止衔接区域中心，场景坐标系下={r.getEndRegionCenterPoint()},"
              f"获取起始衔接区域形状，场景坐标系下={r.getStartSceneRegion()},获取终止衔接区域形状，场景坐标系下={r.getEndSceneRegion()},"
              f"获取楼梯主体形状，场景坐标系下={r.getMainQueueRegion()}, 获取楼梯整体形状，场景坐标系下={r.getFullQueueregion()},"
              f"获取楼梯主体多边形，场景坐标系下={r.getMainQueuePolygon()} "
              f"获取楼梯类型={r.getStairType()},设置楼梯类型={r.setStairType(r.getStairType())},"
              f"获取起始层级={r.getStartLayerId()},设置起始层级={r.setStartLayerId(r.getStartLayerId())},"
              f"获取终止层级={r.getEndLayerId()}, 设置终止层级={r.setEndLayerId(r.getEndLayerId())},获取传送速度，单位：米/秒={r.getTransmissionSpeed()},"
              f"设置传送速度，单位：米/秒={r.setTransmissionSpeed(r.getTransmissionSpeed())},"
              f"获取楼梯净高={r.getHeadroom()},设置楼梯净高={r.setHeadroom(r.getHeadroom())},获取起点控制点={r.getStartControlPoint()},"
              f"获取终点控制点={r.getEndControlPoint()}, 获取左侧控制点={r.getLeftControlPoint()},获取右侧控制点={r.getRightControlPoint()},"
              f"获取起始衔接区域长度控制点={r.getStartConnectionAreaControlPoint() },获取终止衔接区域长度控制点={r.getEndConnectionAreaControlPoint()}")


```



### 2.56. ICrosswalkSignalLamp

人行横道信号灯接口

 **def id(self) ->int: ...**

获取行人信号灯ID

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion:
    print(f"获取行人信号灯ID={region.id()}")
```

 **def setSignalPhase(self, phase:Tess.ISignalPhase) ->None: ...**

设置相位，所设相位可以是其它信号灯组的相位

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion:
    print(f"设置相位，所设相位可以是其它信号灯组的相位={region.setSignalPhase(region.signalPhase())}")
```

 **def setLampColor(self, colorStr:str) ->None: ...**

设置信号灯颜色    

参数：

colorStr：字符串表达的颜色，有四种可选，分别是"红"、"绿"、"黄"、"灰"，，或者是"R"、"G"、"Y"、"gray"。

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion:
    print(f"设置信号灯颜色={region.setLampColor('gray')}")
```

 **def color(self) -> str: ...**

获取信号灯色，"R"、“G”、“Y”、“gray”分别表示"红"、"绿"、"黄"、"灰"

举例：

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

举例：

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

举例：

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

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion:
    print(f"获取信号灯多边型轮廓的顶点={region.polygon()}")
```

 **def angle(self) -> float: ...**

获取信号灯角度, 正北为0，顺时针

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.crosswalkSignalLamps()
for region in allRegion:
    print(f"获取信号灯角度, 正北为0，顺时针={region.angle()}")
```

 **def getICrossWalk(self) -> Tessng.IPedestrianCrossWalkRegion: ...**

获取行人信号灯所属人行横道

举例：

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
        f"行人信号灯列表={crosswalkSignalLamps},行人信号灯{crosswalkSignalLamp.id()}的具体信息："
        f"编号={crosswalkSignalLamp.id()},获取信号灯当前信号灯色={crosswalkSignalLamp.color()}, 名称={crosswalkSignalLamp.name()},"
        f"设置信号灯名称={crosswalkSignalLamp.setName('new_' + crosswalkSignalLamp.name())},"
        f"获取当前信号灯所在的相位={crosswalkSignalLamp.signalPhase()},获取当前信号灯所在的灯组={crosswalkSignalLamp.signalPlan()},"
        f"获取所在车道或车道连接={crosswalkSignalLamp.getICrossWalk()}，获取信号灯多边型轮廓={crosswalkSignalLamp.polygon()}, "
        f"获取信号灯角度，正北为0顺时针方向={crosswalkSignalLamp.angle()}")

```

 


### 2.51 IPedestrianPath

行人路径接口

 **def getId(self) -> int: ...**

获取行人路径ID 

举例：

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

举例：

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

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion:
    print(f"获取行人路径终点={region.getPathEndPoint()}")
```

 **def getPathMiddlePoints(self) -> Type.List<Tessng.IPedestrianPathPoint>: ...**

获取行人路径的中间点集合， 有序集合

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPaths()
for region in allRegion:
    print(f"获取行人路径的中间点集合， 有序集合={region.getPathMiddlePoints()}")
```

 **def isLocalPath(self) ->boolen: ...**

判断当前行人路径是否为行人局部路径

举例：

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
        print(f"仿真路网中行人路径总数={len(paths)},"
              f"获取行人路径起始点={path.getPathStartPoint()},获取行人路径终点={path.getPathEndPoint()},"
              f"获取行人路径中间点={path.getPathMiddlePoints()},判断是否是局部路径={path.isLocalPath()},")
```




### 2.52. IPedestrianPathPoint

行人路径点（起点，终点，途经点）接口

 **def getId(self) -> int: ...**

获取行人路径点ID 

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion:
    print(f"获取行人路径点ID={region.getId()}")
```

 **def getScenePos(self, unit:UnitOfMeasure) -> float: ...**

获取行人路径点场景坐标系下的位置，默认单位：像素，可通过unit参数设置单位

参数：  
[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion:
    print(f"获取行人路径点场景坐标系下的位置,单位：像素={region.getScenePos()}")
    print(f"获取行人路径点场景坐标系下的位置,单位：米={region.getScenePos(UnitOfMeasure.Metric)}")
```

 **def getRadius(self) -> float: ...**

获取行人路径点的半径,单位：米

举例：

```python
iface = tessngIFace()
# 代表TESS NG的路网子接口
netiface = iface.netInterface()
allRegion = netiface.pedestrianPathDecisionPoints()
for region in allRegion:
    print(f"获取行人路径点的半径,单位：米={region.getRadius()}")
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
            print(f"获取行人路径点ID={sp.getId()},获取行人路径点场景坐标系下的位置,单位：像素={sp.getScenePos()},"
                  f"获取行人路径点场景坐标系下的位置,单位：像素={sp.getScenePos(UnitOfMeasure.Metric)},获取行人路径点的半径，单位：米={sp.getRadius()},")

```






------



## 3. 车辆及驾驶行为



### 3.1. IVehicle

车辆接口，用于访问、控制车辆。通过此接口可以读取车辆属性，初始化时设置车辆部分属性，仿真过程读取当前道路情况、车辆前后左右相邻车辆及与它们的距离，可以在车辆未驰出路网时停止车辆运行等。



接口方法：

 **def id(self) -> int: ...**

车辆ID，车辆ID的组成方式为 x * 100000 + y，每个发车点的x值不一样，从1开始递增，y是每个发车点所发车辆序号，从1开始递增。第一个发车点所发车辆ID从100001开始递增，第二个发车点所发车辆ID从200001开始递增。

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆ID为{vehi.id()}")
```

 **def startLink(self) -> Tessng.ILink: ...**

车辆进入路网时起始路段

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆进入路网时起始路段={vehi.startLink()}")
```

 **def startSimuTime(self) -> int: ...**

车辆进入路网时起始时间

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆进入路网时起始时间={vehi.startSimuTime()}")
```

 **def roadId(self) -> int: ...**

车辆所在路段link或connector连接段ID

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在路段link或connector连接段ID={vehi.roadId()}")
```

 **def road(self) -> int: ...**

道路，如果在路段上返回ILink, 如果在连接段上返回IConnector

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在道路={vehi.road()}")
```

 **def section(self) -> Tessng.ISection: ...**

车辆所在的Section，即路段或连接段

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在的Section，即路段或连接段={vehi.section()}")
```

 **def laneObj(self) -> Tessng.ILaneObject: ...**

车辆所在的车道或“车道连接”

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在的车道或“车道连接”={vehi.laneObj()}")
```

 **def segmIndex(self) -> int: ...**

车辆在当前LaneObject上分段序号

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆在当前LaneObject上分段序号={vehi.segmIndex()}")
```

 **def roadIsLink(self) -> bool: ...**

车辆所在道路是否路段

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在道路是否路段={vehi.roadIsLink()}")
```

 **def roadName(self) -> str: ...**

道路名

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"道路名={vehi.roadName()}")
```

 **def initSpeed(self, speed:float=...，unit:Tess.UnitOfMeasure) -> float: ...**

初始化车速

参数：  
\[in\] speed：车速，如果大于0，车辆以指定的速度从发车点出发，单位：像素/秒  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：初始化车速，单位：像素/秒

举例：

```python
def initVehicle(self, vehi):
    vehi.initSpeed(5, UnitOfMeasure.Metric)
```

 **def initLane(self, laneNumber:int, dist:float=..., speed:float=...，unit:Tess.UnitOfMeasure) -> None: ...**

初始化车辆, laneNumber:车道序号，从0开始；dist，距起点距离，单位像素；speed：车速，像素/秒 初始化车速，可通过unit参数设置单位

参数：  
\[in\] laneNumber：车道序号，从0开始  
\[in\] dist：距离路段起点距离，单位：像素  
\[in\] speed：起动时的速度，单位：像素/秒  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

举例：

```python
def initVehicle(self, vehi):
    if vehi.id() == 100001:
        vehi.initLane(0, 5, 0, UnitOfMeasure.Metric)
```

 **def initLaneConnector(self, laneNumber:int, toLaneNumber:int, dist:float=..., speed:float=...，unit:Tess.UnitOfMeasure) -> None: ...**

初始化车辆, laneNumber: “车道连接”起始车道在所在路段的序号，从0开始自右往左；toLaneNumber:“车道连接”目标车道在所在路段的序号，从0开始自右往左， dist，距起点距离，单位像素；speed：车速，像素/秒  
默认单位：像素，可通过unit参数设置单位  
参数：

\[in\] laneNumber：车道序号，从0开始自右侧至左侧  
\[in\] toLaneNumber：车道序号，从0开始自右侧至左侧  
\[in\] dist：距离路段起点距离，单位：：像素或米(取决于unit参数)  
\[in\] speed：起动时的速度，单位：像素/秒或米/秒(取决于unit参数)  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

举例：
```python
def initVehicle(self, vehi):
    if vehi.id() == 100001:
        vehi.initLaneConnector(0, 0, 5, 0, UnitOfMeasure.Metric)
```

 **def setVehiType(self, code:int) -> None: ...**

设置车辆类型，车辆被创建时已确定了类型，通过此方法可以改变车辆类型

参数：

\[in\] code：车辆类型编码

举例：

```python
def initVehicle(self, vehi):
    if vehi.id() == 100001:
        vehi.setVehiType(12)
```

 **def setColor(self, color:str) -> None: ...**

设置车辆颜色  

参数：  
\[in\] color：颜色RGB，如："#EE0000"

举例：

```python
def initVehicle(self, vehi):
    if vehi.roadId() == 2:
        vehi.setColor("#EE0000")
```


 **def length(self，unit:Tess.UnitOfMeasure) -> float: ...**

获取车辆长度，默认单位：像素，可通过unit参数设置单位

参数：  

\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：车辆长度，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆长度={vehi.length()}")
    print(f"车辆长度，单位：米={vehi.length(UnitOfMeasure.Metric)}")
```

 **def setLength(self, len:float, bRestWidth:bool=...，unit:Tess.UnitOfMeasure) -> None: ...**

设置车辆长度

参数：  
\[in\] len：车辆长度，单位：像素  
\[in\] bRestWidth：是否同比例约束宽度，默认为False  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

举例：

```python
def initVehicle(self, vehi):
    if vehi.id() == 100001:
        vehi.setLength(10, False, UnitOfMeasure.Metric)
```

 **def laneId(self) -> int: ...**

如果toLaneId() 小于等于0，那么laneId()获取的是当前所在车道ID，如果toLaneId()大于0，则车辆在“车道连接”上，laneId()获取的是上游车道ID

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在车道ID={vehi.laneId()}")
```

 **def toLaneId(self) -> int: ...**

下游车道ID。如果小于等于0，车辆在路段的车道上，否则车辆在连接段的“车道连接”上

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆下游车道ID={vehi.toLaneId()}")
```

 **def lane(self) -> Tessng.ILane: ...**

获取当前车道，如果车辆在“车道连接”上，获取的是“车道连接”的上游车道

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前车道={vehi.lane()}")
```

 **def toLane(self) -> Tessng.ILane: ...**

如果车辆在“车道连接”上，返回“车道连接”的下游车道，如果当前不在“车道连接”上，返回对象为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆下游车道={vehi.toLane()}")
```

 **def laneConnector(self) -> Tessng.ILaneConnector: ...**

获取当前“车道连接”，如果在车道上，返回空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前车道连接={vehi.laneConnector()}")
```

 **def currBatchNumber(self) -> int: ...**

当前仿真计算批次

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前仿真计算批次={vehi.currBatchNumber()}")
```

 **def roadType(self) -> int: ...**

车辆所在道路类型。包NetItemType中定义了一批常量，每一个数值代表路网上一种元素类型。如：GLinkType代表路段、GConnectorType代表连接段。

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在道路类型={vehi.roadType()}")
```

 **def limitMaxSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

车辆所在路段或连接段最大限速，兼顾到车辆的期望速度，单位：像素/秒  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：最大限速，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在路段或连接段最大限速={vehi.limitMaxSpeed()}")
    print(f"车辆所在路段或连接段最大限速，单位：米/秒={vehi.limitMaxSpeed(UnitOfMeasure.Metric)}")
```

 **def limitMinSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

车辆所在路段或连接段最小限速，兼顾到车辆的期望速度，单位：像素/秒  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：最小限速，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆所在路段或连接段最小限速={vehi.limitMinSpeed()}")
    print(f"车辆所在路段或连接段最小限速，单位：米/秒={vehi.limitMinSpeed(UnitOfMeasure.Metric)}")
```

 **def vehicleTypeCode(self) -> int: ...**

车辆类型编码。打开TESSNG，通过菜单“车辆”->“车辆类型”打开车辆类型编辑窗体，可以看到不同类型车辆的编码

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆类型编码={vehi.vehicleTypeCode()}")
```

 **def vehicleTypeName(self) -> str: ...**

获取车辆类型名，如“小客车”

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆类型名={vehi.vehicleTypeName()}")
```

 **def name(self) -> str: ...**

获取车辆名称

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆名称={vehi.name()}")
```

 **def vehicleDriving(self) -> Tessng.IVehicleDriving: ...**

获取车辆驾驶行为接口

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆驾驶行为接口={vehi.vehicleDriving()}")
```

 **def driving(self) -> None: ...**

驱动车辆。在每个运算周期，每个在运行的车辆被调用一次该方法;  
如果用户使用该函数驱动车辆，那后续整个仿真生命周期都需要用户控制该辆车。即TESSNG将此车辆的控制权移交给用户。

 **def pos(self，unit:Tess.UnitOfMeasure) -> PySide2.QtCore.QPointF: ...**

当前位置，横纵坐标单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：当前位置，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前位置={vehi.pos()}")
    print(f"车辆当前位置，单位：米制={vehi.pos(UnitOfMeasure.Metric)}")
```

 **def zValue(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前高程，单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：当前高程，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前高程={vehi.zValue()}")
    print(f"车辆当前高程，单位：米制={vehi.zValue(UnitOfMeasure.Metric)}")
```

 **def acce(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前加速度，单位：像素/秒^2  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：当前加速度，单位：像素/秒^2

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前加速度={vehi.acce()}")
    print(f"车辆当前加速度，单位：米制={vehi.acce(UnitOfMeasure.Metric)}")
```

 **def currSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前速度，单位：像素/秒  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：当前速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前速度={vehi.currSpeed()}")
    print(f"车辆当前速度，单位：米制={vehi.currSpeed(UnitOfMeasure.Metric)}")
```

 **def angle(self) -> float: ...**

当前角度，北向0度顺时针  

返回：当前角度，单位：度

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前角度={vehi.angle()}")
```

 **def isStarted(self) -> bool: ...**

是否在运行，如果返回False，表明车辆已驰出路网或尚未上路

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆是否在运行={vehi.isStarted()}")
```

 **def vehicleFront(self) -> Tessng.IVehicle: ...**

获取前车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"前车={vehi.vehicleFront()}")
```

 **def vehicleRear(self) -> Tessng.IVehicle: ...**

后车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"后车={vehi.vehicleRear()}")
```

 **def vehicleLFront(self) -> Tessng.IVehicle: ...**

左前车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"左前车={vehi.vehicleLFront()}")
```

 **def vehicleLRear(self) -> Tessng.IVehicle: ...**

左后车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"左后车={vehi.vehicleLRear()}")
```

 **def vehicleRFront(self) -> Tessng.IVehicle: ...**

右前车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"右前车={vehi.vehicleRFront()}")
```

 **def vehicleRRear(self) -> Tessng.IVehicle: ...**

右后车， 可能为空

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"右后车={vehi.vehicleRRear()}")
```

 **def vehiDistFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

前车间距，单位：像素; 若无前车，则范围固定的常量 ， 单位像素  

参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：前车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"前车间距={vehi.vehiDistFront()}")
    print(f"前车间距，单位：米制={vehi.vehiDistFront(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

前车速度，单位：像素/秒  若无前车，则范围固定的常量 单位像素 

参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：前车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"前车速度={vehi.vehiSpeedFront()}")
    print(f"前车速度，单位：米/秒={vehi.vehiSpeedFront(UnitOfMeasure.Metric)}")
```

 **def vehiHeadwayFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

距前车时距, 若无前车，则范围固定的常量  单位像素   
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：距前车时距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"距前车时距={vehi.vehiHeadwayFront()}")
    print(f"距前车时距，单位：米制={vehi.vehiHeadwayFront(UnitOfMeasure.Metric)}")
```

 **def vehiDistRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

后车间距，单位：像素, 若无后车，则范围固定的常量  单位像素 

参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：后车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"后车间距={vehi.vehiDistRear()}")
    print(f"后车间距，单位：米制={vehi.vehiDistRear(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

后车速度，单位：像素/秒  若无后车，则范围固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：后车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"后车速度={vehi.vehiSpeedRear()}")
    print(f"后车速度，单位：米制={vehi.vehiSpeedRear(UnitOfMeasure.Metric)}")
```

 **def vehiHeadwaytoRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

距后车时距，单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：距后车时距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"距后车时距={vehi.vehiHeadwaytoRear()}")
    print(f"距后车时距，单位：米制={vehi.vehiHeadwaytoRear(UnitOfMeasure.Metric)}")
```

 **def vehiDistLLaneFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻左车道前车间距，单位：像素； 若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻左车道前车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻左车道前车间距={vehi.vehiDistLLaneFront()}")
    print(f"相邻左车道前车间距，单位：米制={vehi.vehiDistLLaneFront(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedLLaneFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻左车道前车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻左车道前车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻左车道前车速度={vehi.vehiSpeedLLaneFront()}")
    print(f"相邻左车道前车速度，单位：米制={vehi.vehiSpeedLLaneFront(UnitOfMeasure.Metric)}")
```

 **def vehiDistLLaneRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻左车道后车间距，单位：像素;  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻左车道后车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻左车道后车间距={vehi.vehiDistLLaneRear()}")
    print(f"相邻左车道后车间距，单位：米制={vehi.vehiDistLLaneRear(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedLLaneRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻左车道后车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻左车道后车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻左车道后车速度={vehi.vehiSpeedLLaneRear()}")
    print(f"相邻左车道后车速度，单位：米制={vehi.vehiSpeedLLaneRear(UnitOfMeasure.Metric)}")
```

 **def vehiDistRLaneFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻右车道前车间距，单位：像素;  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻右车道前车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻右车道前车间距={vehi.vehiDistRLaneFront()}")
    print(f"相邻右车道前车间距，单位：米制={vehi.vehiDistRLaneFront(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedRLaneFront(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻右车道前车速度，单位：像素/秒;  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻右车道前车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻右车道前车速度={vehi.vehiSpeedRLaneFront()}")
    print(f"相邻右车道前车速度，单位：米制={vehi.vehiSpeedRLaneFront(UnitOfMeasure.Metric)}")
```

 **def vehiDistRLaneRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻右车道后车间距，单位：像素; 若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻右车道后车间距，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻右车道后车间距={vehi.vehiDistRLaneRear()}")
    print(f"相邻右车道后车间距，单位：米制={vehi.vehiDistRLaneRear(UnitOfMeasure.Metric)}")
```

 **def vehiSpeedRLaneRear(self，unit:Tess.UnitOfMeasure) -> float: ...**

相邻右车道后车速度，单位：像素/秒；  若无目标车，则返回固定的常量  单位像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：相邻右车道后车速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"相邻右车道后车速度={vehi.vehiSpeedRLaneRear()}")
    print(f"相邻右车道后车速度，单位：米制={vehi.vehiSpeedRLaneRear(UnitOfMeasure.Metric)}")
```

 **def setIsPermitForVehicleDraw(self, bDraw:bool) -> None: ...**

设置是否允许插件绘制车辆

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.setIsPermitForVehicleDraw(True)
```

 **def lLaneObjectVertex(self，unit:Tess.UnitOfMeasure) -> typing.List: ...**

车道或车道连接中心线内点集  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：车道或车道连接中心线内点集

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车道或车道连接中心线内点集={vehi.lLaneObjectVertex()}")
    print(f"车道或车道连接中心线内点集，单位：米制={vehi.lLaneObjectVertex(UnitOfMeasure.Metric)}")
```

 **def routing(self) -> Tessng.IRouting: ...**

获取车辆当前路径； 返回的是当前车辆的全局路径，包括已经行驶过大的路段序列

返回：车辆当前路径

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆当前路径={vehi.routing()}")
```

 **def picture(self) -> PySide2.QtGui.QPicture: ...**

获取车辆图片

返回：车辆图片

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆图片={vehi.picture()}")
```

 **def boundingPolygon(self) -> PySide2.QtGui.QPolygonF: ...**

获取车辆由方向和长度决定的四个拐角构成的多边型

返回：车辆由方向和长度决定的四个拐角构成的多边型

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆由方向和长度决定的四个拐角构成的多边型={vehi.boundingPolygon()}")
```

 **def setTag(self, tag:int) -> None: ...**

设置标签表示的状态

参数：  
\[in\] tag：标签表示的状态

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.setTag(1)
```

 **def tag(self) -> int: ...**

获取标签表示的状态

返回：标签表示的状态

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"标签表示的状态={vehi.tag()}")
```

 **def setTextTag(self, text:str) -> None: ...**

设置文本信息，用于在运行过程保存临时信息，方便开发

参数：  
\[in\] text：文本信息

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.setTextTag("test")
```

 **def textTag(self) -> str: ...**

文本信息，运行过程临时保存的信息，方便开发

返回：文本信息

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"文本信息={vehi.textTag()}")
```

 **def setJsonInfo(self, info:typing.Dict) -> None: ...**

设置json格式数据

参数：  
\[in\] info：json格式数据

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.setJsonInfo({"test": "test"})
```

 **def jsonInfo(self) -> typing.Dict: ...**

返回json格式数据

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"json格式数据={vehi.jsonInfo()}")
```

 **def jsonProperty(self, propName:str) -> typing.Any: ...**

返回json字段值

参数：  
\[in\] propName：json字段名

返回：json字段值

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"json字段值={vehi.jsonProperty('test')}")
```

 **def setJsonProperty(self, key:str, value:typing.Any) -> None: ...**

设置json数据属性

参数：  
\[in\] key：json字段名  
\[in\] value：json字段值

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.setJsonProperty("test", "test")
```

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

返回：当前驾驶车辆对象

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前驾驶车辆={vehi.vehicleDriving().vehicle()}")
```

 **def getRandomNumber(self) -> int: ...**

获取车辆被赋予的随机数

返回：车辆被赋予的随机数

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"随机数={vehi.vehicleDriving().getRandomNumber()}")
```

 **def nextPoint(self) -> bool: ...**

计算下一点位置，过程包括计算车辆邻车关系、公交车是否进站是否出站、是否变道、加速度、车速、移动距离、跟驰类型、轨迹类型等

返回：计算下一点位置成功与否

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"计算下一点位置={vehi.vehicleDriving().nextPoint()}")
```

 **def zeroSpeedInterval(self) -> int: ...**

当前车速为零持续时间(毫秒)

返回：当前车速为零持续时间(毫秒)

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前车速为零持续时间(毫秒)={vehi.vehicleDriving().zeroSpeedInterval()}")
```

 **def isHavingDeciPointOnLink(self) -> bool: ...**

当前是否在路段上且有决策点

返回：当前是否在路段上且有决策点

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前是否在路段上且有决策点={vehi.vehicleDriving().isHavingDeciPointOnLink()}")
```

 **def followingType(self) -> int: ...**

车辆的跟驰类型，分为：0：停车，1: 正常，5：急减速，6：急加速，7：汇入， 8：穿越，9：协作减速，10：协作加速，11：减速待转，12：加速待转

返回：车辆的跟驰类型

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆的跟驰类型={vehi.vehicleDriving().followingType()}")
```

 **def isOnRouting(self) -> bool: ...**

当前是否在路径上

返回：当前是否在路径上

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前是否在路径上={vehi.vehicleDriving().isOnRouting()}")
```

 **def stopVehicle(self) -> None: ...**

停止运行，车辆移出路网

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
simuTime = simuiface.simuTimeIntervalWithAcceMutiples()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    if simuTime  == 20 * 1000:
        vehi.vehicleDriving().stopVehicle()
```

 **def angle(self) -> float: ...**

旋转角，北向0度顺时针

返回：旋转角，北向0度顺时针

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"旋转角={vehi.vehicleDriving().angle()}")
```

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

返回：车辆欧拉角

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"车辆欧拉角={vehi.vehicleDriving().euler()}")
```

 **def desirSpeed(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前期望速度，与车辆自身期望速度和道路限速有关，不大于道路限速，单位：像素/秒  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前期望速度，单位：像素/秒

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前期望速度={vehi.vehicleDriving().desirSpeed()}")
    print(f"当前期望速度单位={vehi.vehicleDriving().desirSpeed(UnitOfMeasure.Metric)}")
```

 **def getCurrRoad(self) -> Tessng.ISection: ...**

返回当前所在路段或连接段

返回：当前所在路段或连接段

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前所在路段或连接段={vehi.vehicleDriving().getCurrRoad()}")
```

 **def getNextRoad(self) -> Tessng.ISection: ...**

下一路段或连接段

返回：下一路段或连接段

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"下一路段或连接段={vehi.vehicleDriving().getNextRoad()}")
```

 **def differToTargetLaneNumber(self) -> int: ...**

与目标车道序号的差值，不等于0表示有强制变道意图，大于0有左变道意图，小于0有右变道意图，绝对值大于0表示需要强制变道次数

返回：与目标车道序号的差值

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"与目标车道序号的差值={vehi.vehicleDriving().differToTargetLaneNumber()}")
```

 **def toLeftLane(self) -> None: ...**

左变道

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.vehicleDriving().toLeftLane()
```

 **def toRightLane(self) -> None: ...**

右变道

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    vehi.vehicleDriving().toRightLane()
```

 **def laneNumber(self) -> int: ...**

当前车道序号，最右侧序号为0

返回：当前车道序号，最右侧序号为0

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前车道序号={vehi.vehicleDriving().laneNumber()}")
```

 **def initTrace(self) -> None: ...**

初始化轨迹

 **def setTrace(self, lPoint:typing.Sequence，unit:Tess.UnitOfMeasure) -> None: ...**

设置轨迹

参数：  
\[in\] lPoint：轨迹点坐标集合  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def calcTraceLength(self，unit:Tess.UnitOfMeasure) -> None: ...**

计算轨迹长度； 前提是：TESSNG开启车辆轨迹记录|输出 功能  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def tracingType(self) -> int: ...**

返回轨迹类型，分为：0：跟驰，1：左变道，2：右变道，3：左虚拟変道，4：右虚拟变道，5：左转待转，6：右转待转，7：入湾，8：出湾

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"轨迹类型={vehi.vehicleDriving().tracingType()}")
```

 **def setTracingType(self, type:int) -> None: ...**

设置轨迹类型； TESSNG车辆后续运动轨迹按照此轨迹类型的动机产生动作，但因为阈值条件有可能环境不满足，因此动机并不一定能执行

参数：

\[in\] type：轨迹类型

 **def setLaneNumber(self, number:int) -> None: ...**

设置当前车道序号

参数：

\[in\] number：车道序号

 **def currDistance(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前计算周期移动距离，单位：像素  
参数： 
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前计算周期移动距离，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前计算周期移动距离={vehi.vehicleDriving().currDistance()}")
    print(f"当前计算周期移动距离，米制={vehi.vehicleDriving().currDistance(UnitOfMeasure.Metric)}")
```

 **def currDistanceInRoad(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前路段或连接上已行驶距离，单位：像素  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前路段或连接上已行驶距离，单位：像素

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前路段或连接上已行驶距离={vehi.vehicleDriving().currDistanceInRoad()}")
    print(f"当前路段或连接上已行驶距离，米制={vehi.vehicleDriving().currDistanceInRoad(UnitOfMeasure.Metric)}")
```

 **def setCurrDistanceInRoad(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置当前路段已行驶距离

参数：

\[in\] dist：距离，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def setVehiDrivDistance(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置当前已行驶总里程

参数：

\[in\] dist：总里程，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def getVehiDrivDistance(self，unit:Tess.UnitOfMeasure) -> float: ...**

已行驶总里程  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：已行驶总里程

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"已行驶总里程={vehi.vehicleDriving().getVehiDrivDistance()}")
    print(f"已行驶总里程，米制={vehi.vehicleDriving().getVehiDrivDistance(UnitOfMeasure.Metric)}")
```

 **def currDistanceInSegment(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前分段已行驶距离  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前分段已行驶距离

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前分段已行驶距离={vehi.vehicleDriving().currDistanceInSegment()}")
    print(f"当前分段已行驶距离，米制={vehi.vehicleDriving().currDistanceInSegment(UnitOfMeasure.Metric)}")
```

 **def setCurrDistanceInSegment(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置当前分段已行驶的距离  
参数：  
\[in\] dist：距离，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def setSegmentIndex(self, index:int) -> None: ...**

设置分段序号

参数：

\[in\] index：分段序号

 **def setCurrDistanceInTrace(self, dist:float)，unit:Tess.UnitOfMeasure -> None: ...**

设置曲化轨迹上行驶的距离  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def setIndexOfSegmInTrace(self, index:int) -> None: ...**

设置曲化轨迹上的分段序号

 **def setChangingTracingType(self, b:bool) -> None: ...**

设置是否改变轨迹，当设为True时会对轨迹初始化，如设轨迹分段序号为0，等

 **def currDistance(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前时间段移动距离  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前时间段移动距离

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前时间段移动距离={vehi.vehicleDriving().currDistance()}")
    print(f"当前时间段移动距离，米制={vehi.vehicleDriving().currDistance(UnitOfMeasure.Metric)}")
```

 **def setRouting(self, pRouting:Tessng.IRouting) -> bool: ...**

设置路径，外界设置的路径不一定有决策点，可能是临时创建的，如果车辆不在此路径上则设置不成功并返回False

参数：

\[in\] pRouting：路径

返回：是否设置成功

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

 **def currDistanceInSegment(self，unit:Tess.UnitOfMeasure) -> float: ...**

当前在分段上已行驶距离  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：当前在分段上已行驶距离

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前在分段上已行驶距离={vehi.vehicleDriving().currDistanceInSegment()}")
    print(f"当前在分段上已行驶距离，米制={vehi.vehicleDriving().currDistanceInSegment(UnitOfMeasure.Metric)}")
```

 **def setCurrDistanceInSegment(self, dist:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置在分段上已行驶距离

 **def setX(self, posX:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置横坐标

参数：

\[in\] posX：横坐标：单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

 **def setY(self, posY:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置纵坐标

参数：

\[in\] posY：纵坐标：单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def setV3z(self, v3z:float，unit:Tess.UnitOfMeasure) -> None: ...**

设置高程坐标

参数：

\[in\] v3z：高程坐标：单位：像素

 **def changingTrace(self) -> typing.List: ...**

变轨点集，车辆不在车道中心线或“车道连接”中心线上时的轨迹，如变道过程的轨迹点集  
参数：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：变轨点集

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"变轨点集={vehi.vehicleDriving().changingTrace()}")
```

 **def changingTraceLength(self，unit:Tess.UnitOfMeasure) -> float: ...**

变轨长度

参数：

\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：变轨长度

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"变轨长度={vehi.vehicleDriving().changingTraceLength()}")
    print(f"变轨长度，米制={vehi.vehicleDriving().changingTraceLength(UnitOfMeasure.Metric)}")
```

 **def distToStartPoint(self, fromVehiHead:bool=..., bOnCentLine:bool=..., unit:Tess.UnitOfMeasure) -> float: ...**

在车道或车道连接上到起点距离

参数：

\[in\] fromVehiHead：是否从车头计算，如果为False，从车辆中心点计算，默认值为False  
\[in\] bOnCentLine：当前是否在中心线上  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

返回：在车道或车道连接上到起点距离

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"在车道或车道连接上到起点距离={vehi.vehicleDriving().distToStartPoint()}")
    print(f"在车道或车道连接上到起点距离，米制={vehi.vehicleDriving().distToStartPoint(False, True, UnitOfMeasure.Metric)}")
```

 **def distToEndpoint(self, fromVehiHead:bool=..., bOnCentLine:bool=..., unit:Tess.UnitOfMeasure) -> float: ...**

在车道或“车道连接”上车辆到终点距离

参数：  
\[in\] fromVehiHead：是否从车头计算，如果为False，从车辆中心点计算，默认值为False  
\[in\] bOnCentLine：当前是否在中心线上  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

返回：在车道或“车道连接”上车辆到终点距离

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"在车道或“车道连接”上车辆到终点距离={vehi.vehicleDriving().distToEndpoint()}")
    print(f"在车道或“车道连接”上车辆到终点距离，米制={vehi.vehicleDriving().distToEndpoint(False, True, UnitOfMeasure.Metric)}")
```

 **def setRouting(self, pRouting:Tessng.IRouting) -> bool: ...**

设置路径，外界设置的路径不一定有决策点，可能是临时创建的，如果车辆不在此路径上则设置不成功并返回False

 **def routing(self) -> Tessng.IRouting: ...**

当前路径

举例：

```python
iface = tessngIFace()
simuiface = iface.simuInterface()
allVehicleStarted = simuiface.allVehiStarted()
for vehi in allVehicleStarted:
    print(f"当前路径={vehi.vehicleDriving().routing()}")
```

 **def moveToLane(self, pLane:Tessng.ILane, dist:float，unit:Tess.UnitOfMeasure) -> bool: ...**

将车辆移到另一条车道上； 车辆会瞬间从当前车道移动到目标车道及指定的距离出，后续TESSNG接管车辆继续行驶   

参数：  
\[in\] pLane：目标车道  
\[in\] dist：到目标车道起点距离，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def moveToLaneConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float，unit:Tess.UnitOfMeasure) -> bool: ...**

将车辆移到另一条车道连接上; 车辆会瞬间从当前位置移动到目标车道连接及指定的距离出，后续TESSNG接管车辆继续行驶 

参数：  
\[in\] pLaneConnector：目标车道  
\[in\] dist：到目标车道起点距离，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

 

 **def move(self, pILaneObject:Tessng.ILaneObject, dist:float，unit:Tess.UnitOfMeasure) -> bool: ...**

移动车辆到到另一条车道或“车道连接”； 使用该函数后，车辆脱离TESSNG管控，需要用户维护后期车辆运动

参数：

\[in\] pILaneObject：目标车道或“车道连接”  
\[in\] dist：到目标车道起点距离，单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

保存路网,打开另存为窗口，但无法覆盖已保存的路网文件。

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


 **def sceneWidth(self，unit:Tess.UnitOfMeasure) -> float: ...**

场景宽度，单位：米  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def sceneHeight(self，unit:Tess.UnitOfMeasure) -> float: ...**

场景高度，单位：米  
参数：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

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

 **def connectorIds(self) -> typing.List: ...**

连接段ID集

 **def connectorCount(self) -> int: ...**

连接段数

 **def connectors(self) -> typing.List: ...**

连接段集


 **def allConnectorArea(self) -> typing.List: ...**

面域集

 **def signalLampCount(self) -> int: ...**

信号灯数

 **def signalLampIds(self) -> typing.List: ...**

信号灯ID集

 **def signalLamps(self) -> typing.List: ...**

信号灯集

 **def guidArrowCount(self) -> int: ...**

导向箭头数

 **def guidArrowIds(self) -> typing.List: ...**

导向箭头ID集

 **def dispatchPoints(self) -> typing.List: ...**

发车点集。

 **def buslines(self) -> typing.List: ...**

公交线路集

**def busStations(self) -> typing.List: ...**

公交站点集

 **def decisionPoints(self) -> typing.List: ...**

决策点列表

 **def vehiInfoCollectors(self) -> typing.List: ...**

所有车辆检测器

 **def vehiQueueCounters(self) -> typing.List: ...**

所有排队计数器

 **def vehiTravelDetectors(self, id:int) -> Tessng.IVehicleTravelDetector**: ...**

所有车辆行程时间检测器，返回列表中的每一个元素是一对行程时间检测器的起始检测器

 **def crossPoints(self, pLaneConnector:Tessng.ILaneConnector) -> typing.List: ...**

当前“车道连接”穿过其它“车道连接”形成的交叉点列表；

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

  **def trafficControllerCount(self) -> int: ...**

获取路网中信号机总数数

 **def trafficControllerIds(self) -> Type.List<int>: ...**

获取信号机编号列表

 **def trafficControllers(self) -> Type.List<Tess.ITrafficController>: ...**

获取信号机对象列表

 **def signalPlanCount(self) -> int: ...**

获取路网中信控方案总数

 **def signalPlanIds(self) -> Type.List<int>: ...**

信控方案ID集合

 **def signalPlans(self) -> Type.List<Tess.ISignalPlan>: ...**

获取信控方案对象列表

  **def signalPhases(self) -> Type.List<Tess.ISignalPhase>: ...**

获取所有信控方案的相位信息

 **def roadWorkZones(self) -> typing.List: ...**

获取所有施工区

 **def accidentZones(self) -> typing.List: ...**

获取所有事故区

 **def findAccidentZone(self, accidentZoneId:int) -> Tessng.IAccidentZone: ...**

根据ID查询事故区

参数：

\[in\] accidentZoneId：事故区ID


 **def limitedZones(self) -> Type.List<ILimitedZone>: ...**

获取所有限行区

 **def reconstructions(self) -> Type.List<Tess.IReconstruction>: ...**

获取所有改扩建

 **def reduceSpeedAreas(self) ->Type.List<Tessng.IReduceSpeedArea>: ...**

获取所有限速区 


 **def tollLanes(self) ->Type.List<Tessng.ITollLane>: ...**

获取所有收费车道列表

 **def tollDecisionPoints(self) ->Type.List<Tessng.ITollDecisionPoint>: ...**

获取所有收费决策点列表

 **def parkingRegions(self) ->Type.List<Tessng.IParkingRegion>: ...**

获取所有停车区列表

 **def parkingDecisionPoints(self) ->Type.List<Tessng.IParkingDecisionPoint>: ...**

获取所有停车决策点列表

 **def parkingTimeDis(self) ->Online.ParkingLot.DynaParkingTimeDis : ...**

获取停车场停车时距分布列表

 **def tollParkingTimeDis(self) ->Type.List<Online.TollStation.DynaTollParkingTimeDis>: ...**

获取收费站停车时距分布列表

 **def getAllJunctions () ->Type.Dict<int,Tessng.IJunction>: ...**

获得所有节点, 返回类型为字典

 **def getFlowTimeIntervals(self) ->Type.List<Tess.Online.Junction.FlowTimeInterval>: ...**

获取所有时间段

 **def addFlowTimeInterval(self) ->Online.Junction.FlowTimeInterval: ...**

添加时间段，返回新时间段ID，失败返回-1


 **def getJunctionFlows(self, junctionId:int) ->Type.Dict(int, Type.Dict(int, Tess.Online.Junction.FlowTurning)): ...**

获取节点流向信息  
\[in\]junctionId：节点ID

 **def buildAndApplyPaths(self) ->Type.Dict(Type.Tuple(int,int),Type.List<Type.List<Tess.ILink>>): ...**

构建并应用路径，返回路径结果映射:< 起始路段ID,终点路段ID > - > 可行路径列表

 **def calculateFlows(self) ->Type.Dict(int,Type.List<Tess.Online.Junction.FlowTurning>): ...**

计算并应用流量结果，返回时间段ID到流量计算结果的映射

 **def pedestrianTypes() ->Type.List<Tessng.IPedestrianType>: ...**

获取所有行人类型

 **def pedestrianCompositions() ->Type.List<Online.Pedestrian.PedestrianComposition >: ...**

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

获取所有三角形面域

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







 **def findLink(self, id:int) -> Tessng.ILink: ...**

根据路段ID查找路段

 **def findLane(self, id:int) -> Tessng.ILane: ...**

根据车道ID查找车道

 **def findLaneConnector(self, connectorId:int) -> Tessng.ILaneConnector: ...**

根据“车道连接”ID查找“车道连接”

 **def findLaneConnector(self, fromLaneId:int, toLaneId:int) -> Tessng.ILaneConnector: ...**

根据起始车道ID及目标车道ID查找“车道连接”


 **def findConnector(self, id:int) -> Tessng.IConnector: ...**

根据连接段ID查找连接段

 **def findConnectorArea(self, id:int) -> Tessng.IConnectorArea: ...**

根据面域ID查找面域

 **def findConnectorByLinkIds(self, fromLinkId:int, toLinkId:int) -> Tessng.IConnector: ...**

根据起始路段ID及目标路段ID查找连接段

 **def findSignalLamp(self, id:int) -> Tessng.ISignalLamp: ...**

根据信号灯ID查找信号灯

 **def findSignalPhase(self, id:int) -> Tessng.ISignalPhase: ...**

根据信号相位ID查找信号相位, 目前的接口有点问题，返回的是none

 **def findDispatchPoint(self, id:int) -> Tessng.IDispatchPoint: ...**

根据发车点ID查找发车点

参数：

\[in\] id：发车点ID

 **def findBusline(self, buslineId:int) -> Tessng.IBusLine: ...**

根据公交线路ID查找公交线路

参数：

\[in\] buslineId：公交线路ID

 **def findBuslineByFirstLinkId(self, linkId:int) -> Tessng.IBusLine: ...**

根据公交线路起始路段ID查找公交线路

参数：

\[in\] linkId：公交线路起始段ID

 **def findBusStation(self, stationId:int) -> Tessng.IBusStation: ...**

根据公交站点ID查询公交站点

 **def findBusStationLineByStationId(self, stationId:int) -> typing.List: ...**

根据公交站点ID查询相关BusLineStation

 **def findDecisionPoint(self, id:int) -> Tessng.IDecisionPoint: ...**

根据ID查找决策点

\[in\] id：决策点ID

返回：决策点对象

 **def findVehiInfoCollector(self, id:int) -> Tessng.IVehicleDrivInfoCollector: ...**

根据ID查询车辆检测器

参数：

\[in\] id：车辆检测器ID

返回：车辆检测器对象

 **def findVehiQueueCounter(self, id:int) -> Tessng.IVehicleQueueCounter: ...**

根据ID查询车辆排队计数器

参数：

\[in\] id：排队计数器ID

返回：排队计数器对象

 **def findRouting(self, id:int) -> Tessng.IRouting: ...**

根据路径ID查找路径

 **def findVehiTravelDetector(self, id:int) -> Tessng.IVehicleTravelDetector: ...**

根据ID查询车辆行程时间检测器，返回一对行程时间检测器中起始检测器

参数：

\[in\] id：行程时间检测器ID

返回：行程时间检测器对象

 **def findTrafficControllerById(self, id:long) -> Tess.ITrafficController: ...**

获取指定id的信号机对象

 **def findTrafficControllerByName(self, name:str) -> Tess.ITrafficController: ...**

根据名称查询信号机(如果同名返回第一个)

 **def findSignalPlanById(self, id:long) -> Tess.ISignalPlan: ...**

获取指定id的信号机对象

 **def findSignalPlanByName(self, name:str) -> Tess.ISignalPlan: ...**

根据名称查询信号机(如果同名返回第一个)

 **def findRoadWorkZone(self, roadWorkZoneId:int) -> Tessng.Online.IRoadWorkZone: ...**

根据ID查询施工区

参数：

\[in\] roadWorkZoneId：施工区ID

返回：施工区对象

 **def findLimitedZone(limitedZoneId:int) -> Tessng.ILimitedZone: ...**

根据ID获取指定的限行区

参数：
\[in\] limitedZoneId：限行区ID

 **def findReconstruction(reconstructionId:int) -> Tessng.IReconstruction: ...**

根据ID获取指定的改扩建对象

参数：  
\[in\] reconstructionId：改扩建ID

 **def findReduceSpeedArea(id:int) ->Type.List<Tessng.IReduceSpeedArea>: ...**

查询指定ID的限速区  
参数：  
\[in\] id：限速区ID

 **def findTollLane(self) ->Type.List<Tessng.ITollLane>: ...**

通过id查询收费车道

 **def findTollDecisionPoint(self) ->Type.List<Tessng.ITollDecisionPoint>: ...**

通过id查询收费决策点

 **def findParkingRegion(self) ->Type.List<Tessng.IParkingRegion>: ...**

通过id查询停车区域

 **def findParkingDecisionPoint(self) ->Type.List<Tessng.IParkingDecisionPoint>: ...**

通过id查询停车决策点

 **def findJunction (id:int) ->Tessng.IJunction: ...**

根据路径ID查找节点  
\[in\] id：节点ID

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

 **def findCrosswalkSignalLamp(id:int) ->Tessng.ICrosswalkSignalLamp: ...**

根据id获取人行横道红绿灯

 **def findPedestrianStartPointConfigInfo() ->Tessng.PedestrianPathStartPointConfigInfo : ...**

根据id获取行人发生点配置信息，id为行人发生点ID

 **def findPedestrianDecisionPointConfigInfo() ->Tessng.PedestrianDecisionPointConfigInfo  : ...**

根据id获取行人决策点配置信息，id为行人决策点ID







 **def linkCenterPoints(self, linkId:int，unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取指定路段的中心线断点集

参数：  
\[in\]linkId：指定路段ID  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def laneCenterPoints(self, laneId:int，unit:Tess.UnitOfMeasure) -> typing.List: ...**

**获取指定车道的中心线断点集**

参数：­  
\[in\] laneId：指定车道ID  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def judgeLinkToCross(self, linkId:int) -> bool: ...**

判断路段去向是否进入交叉口， 以面域是否存在多连接段以及当前路段与后续路段之间的角度为依据； 和节点Ijunction没啥关系



 **def createLink(self, lCenterPoint:typing.Sequence, laneCount:int, linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建路段

参数：  
\[in\] lCenterPoint：路段中心线断点集  
\[in\] laneCount：车道数  
\[in\] linkName：路段名，默认为空，将以路段ID作为路段名  
\[in\] bAddToScene：创建后是否放入路网场景，默认为True  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
注：如传入米制参数，请勿遗忘传入linkName与bAddToScene参数。  

举例：

```python
startPoint = QPointF(m2p(-300), 0)
endPoint = QPointF(m2p(300), 0)
lPoint = [startPoint, endPoint]
link1 = netiface.createLink(lPoint, 7, "曹安公路")

```

返回：路段对象。

 **def createLink3D(self, lCenterV3:typing.Sequence, laneCount:int, linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建路段,默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] lCenterV3：路段中心线断点序列，每个断点都是三维空间的点  
\[in\] laneCount：车道数  
\[in\] linkName：路段名  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：路段对象。  
注：如传入米制参数，请勿遗忘传入linkName与bAddToScene参数。

 **def createLinkWithLaneWidth(self, lCenterPoint:typing.Sequence, lLaneWidth:typing.Sequence, linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建路段,默认单位：像素，可通过unit参数设置单位

参数：

\[in\] lCenterPoint：路段中心线断点序列  
\[in\] lLaneWidth：车道宽度列表  
\[in\] linkName：路段名  
\[in\] bAddToScene：是否加入场景，默认为True  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：路段对象。  
注：如传入米制参数，请勿遗忘传入linkName与bAddToScene参数。

 **def createLink3DWithLaneWidth(self, lCenterV3:typing.Sequence, lLaneWidth:typing.Sequence, linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建指定车道宽度的3D路段，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] lCenterV3：路段中心线断点序列，每个断点都是三维空间的点  
\[in\] lLaneWidth：车道宽度列表  
\[in\] linkName：路段名  
\[in\] bAddToScene：是否加入场景，默认为True  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：路段对象。  
注：如传入米制参数，请勿遗忘传入linkName与bAddToScene参数。  

 **def createLink3DWithLanePoints(self, lCenterLineV3:typing.Sequence, lanesWithPoints:typing.Sequence, linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建指定车道断点的3D路段，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] lCenterLineV3：路段中心点集(对应TESSNG路段中心点)，每个点都是三维空间的  
\[in\] lanesWithPoints：车道数据集合，每个成员是QMap< QString, QList< QVector3D>>类型数据，有三个key，分别是“left”、“center”、“right”、分别表示一条车道左、中、右侧断点序列。  
\[in\] linkName：路段名，默认为路段ID  
\[in\] bAddToScene：是否加入路网，默认True表示加入  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：路段对象  
注：如传入米制参数，请勿遗忘传入linkName与bAddToScene参数。

 **def createConnector(self, fromLinkId:int, toLinkId:int, lFromLaneNumber:typing.Sequence, lToLaneNumber:typing.Sequence, connName:str=..., bAddToScene:bool=...) -> Tessng.IConnector: ...**

创建连接段

参数：

\[in\] fromLinkId：起始路段ID  
\[in\] toLinkId：目标路段ID  
\[in\] lFromLaneNumber：连接段起始车道序号集  
\[in\] LToLaneNumber：连接段目标车道序号集   
\[in\] connName：连接段名，默认为空，以两条路段的ID连接起来作为名称   
\[in\] bAddToScene：创建后是否放入路网场景，默认为True  

 

 **def createConnector3DWithPoints(self, fromLinkId:int, toLinkId:int, lFromLaneNumber:typing.Sequence, lToLaneNumber:typing.Sequence, laneConnectorWithPoints:typing.Sequence, connName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.IConnector: ...**

创建连接段，创建连接段后将“车道连接”中自动计算的断点集用参数laneConnectorWithPoints断点替换;  即创建指定断点的3D连接段，默认单位：像素，可通过unit参数设置单位  

参数：  
\[in\] fromLinkId：起始路段ID  
\[in\] toLinkId：目标路段ID  
\[in\] lFromLaneNumber：起始路段参于连接的车道序号  
\[in\] lToLaneNumber：目标路段参于连接的车道序号  
\[in\] laneConnectorWithPoints：“车道连接”数据列表，成员是QMap< QString, QList< QVector3D>>类型数据，有三种key，分别是“left”、“center”、“right”，表示一条“车道连接”左、中、右侧断点序列   
\[in\] connName：连接段名，默认将起始路段ID和目标路段ID用“_”连接表示连接段名，如“100_101”。  
\[in\] bAddToScene：是否加入到场景，默认为True  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位
返回：连接段对象  
注：如传入米制参数，请勿遗忘传入connName与bAddToScene参数。  

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

 **def createLink3DWithLanePointsAndAttrs(self, lCenterLineV3:typing.Sequence, lanesWithPoints:typing.Sequence, lLaneType:typing.Sequence, lAttr:typing.Sequence=..., linkName:str=..., bAddToScene:bool=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

创建路段

参数：  
\[in\] lCenterLineV3：路段中心点集(对应TESSNG路段中心点)  
\[in\] lanesWithPoints：车道点集的集合  
\[in\] lLaneType:车道类型集  
\[in\] lAttr:车道附加属性集  
\[in\] linkName：路段名，默认为路段ID,  
\[in\] bAddToScene：是否加入路网，默认True表示加入  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：路段对象  
注：如传入米制参数，请勿遗忘传入connName与bAddToScene参数。


 **def createGuidArrow(self, ref_pLane:Tessng.ILane, length:float, distToTerminal:float, arrowType:Online.GuideArrowType，unit:Tess.UnitOfMeasure) -> :Online.GuideArrowType: ...**

创建导向箭头，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] pLane：车道  
\[in\] length：长度，默认单位像素，可通过制定unit更改为米制  
\[in\] distToTerminal：到车道终点距离，默认单位像素，可通过制定unit更改为米制  
\[in\] arrowType：箭头类型，Online.GuideArrowType 枚举值  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：创建车道箭头  

```python
//在路段4的最右侧车道上添加直行或右转箭头,导向箭头距离路段起终点不能小于9米
    ILink* pLink = gpTessInterface->netInterface()->findLink(4);
    if (pLink) {
        ILane* pRightLane = pLink->lanes().front();
        qreal length = m2p(4.0);
        qreal distToTerminal = m2p(50);
        Online::GuideArrowType arrowType = Online::GuideArrowType::StraightRight;
        if (pRightLane) {
IGuidArrow* pGuideArrow = gpTessInterface->netInterface()->createGuidArrow(pRightLane, length, distToTerminal, arrowType);
            qDebug() << "创建箭头成功，箭头所在车道为：" << pGuideArrow->lane()->id() << endl;
        }
}

```

**def createVehicleType(self, _vt:Tessng._VehicleType) -> bool: ...**

创建车型，如果创建成功，会将新创建的车辆类型存放到全局数据里供使用

参数：

\[in\] vt：车辆类型数据

**def createDecisionPoint(self, pLink:Tessng.ILink, distance:float, name:str=...，unit:Tess.UnitOfMeasure) -> Tessng.IDecisionPoint: ...**

创建决策点，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] pLink：决策点所在的路段  
\[in\] distance：决策点距离路段起点的距离，默认单位：像素  
\[in\] name：决策点的名称  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：决策点对象  
注：如传入米制参数，请勿遗忘传入name参数。  
举例：  

```python
# 创建决策点
decisionPoint = netiface.createDecisionPoint(link3, 30，Tess.UnitOfMeasure.Metric)

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

 

 **def createVehiCollectorOnLink(self, pLane:Tessng.ILane, dist:float，unit:Tess.UnitOfMeasure) -> Tessng.IVehicleDrivInfoCollector: ...**

在路段的车道上创建车辆采集器，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] pLane：车道对象  
\[in\] dist：路车道起点距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

 

 **def createVehiCollectorOnConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float，unit:Tess.UnitOfMeasure) -> Tessng.IVehicleDrivInfoCollector: ...**

在连接段的“车道连接”上创建采集器

参数：  
\[in\] pLaneConnector：“车道连接”对象  
\[in\] dist：距“车道连接”起点距离，单位像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  



 **def createVehiQueueCounterOnLink(self, pLane:Tessng.ILane, dist:float，unit:Tess.UnitOfMeasure) -> Tessng.IVehicleQueueCounter: ...**

在路段的车道上创建车辆排队计数器

参数：  
\[in\] pLane：车道对象  
\[in\] dist：默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位 
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

 

 **def createVehiQueueCounterOnConnector(self, pLaneConnector:Tessng.ILaneConnector, dist:float，unit:Tess.UnitOfMeasure) -> Tessng.IVehicleQueueCounter: ...**

在连接段的车道连接上创建车辆排队计数器，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] pLaneConnector：“车道连接”对象  
\[in\] dist：距“车道连接”起点距离，默认单位：像素  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：排队计数器对象  

 **def createVehicleTravelDetector_link2link(self, pStartLink:Tessng.ILink, pEndLink:Tessng.ILink, dist1:float, dist2:float，unit:Tess.UnitOfMeasure) -> typing.List: ...**

创建行程时间检测器，起点和终点都在路段上；默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] dist1：检测器起点距所在路段起始点距离，默认单位：像素  
\[in\] dist2：检测器终点距所在路段起始点距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
举例：  

```python
#在路段9 50-550米处创建行程检测器
lVehicleTravelDetector = tessngIFace().netInterface().createVehicleTravelDetector_link2link(link, link, m2p(50), m2p(550))
if lVehicleTravelDetector is not None:
for detector in lVehicleTravelDetector:
detector.setFromTime(10)
detector.setToTime(60)

```

 

 **def createVehicleTravelDetector_link2conn(self, pStartLink:Tessng.ILink, pEndLaneConnector:Tessng.ILaneConnector, dist1:float, dist2:float，unit:Tess.UnitOfMeasure) -> typing.List: ...**

创建路段到连接段的行程时间检测器，起点在路段上，终点都在连接段的“车道连接”上； 默认单位：像素，可通过unit参数设置单位  

参数：

\[in\] pStartLink：检测器起点所在路段对象  
\[in\] pEndLaneConnector：检测器终点所在“车道连接”对象  
\[in\] dist1：检测器起点距所在路段起始点距离，默认单位：像素  
\[in\] dist2：检测器终点距所在“车道连接”起始点距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：行程时间检测器对象  

 **def createVehicleTravelDetector_conn2link(self, pStartLaneConnector:Tessng.ILaneConnector, pEndLink:Tessng.ILink, dist1:float, dist2:float，unit:Tess.UnitOfMeasure) -> typing.List: ...**

创建连接段到路段的行程时间检测器，起点在连接段的“车道连接”上，终点在路段上

参数：  
\[in\] pStartLaneConnector：检测器起点所在“车道连接”对象  
\[in\] pEndLink：检测器终点所在路段对象  
\[in\] dist1：检测器起点距所在"车道连接”起始点距离，默认单位：像素  
\[in\] dist2：检测器终点距所在路段起始点距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：行程时间检测器对象   

 **def createVehicleTravelDetector_conn2conn(self, pStartLaneConnector:Tessng.ILaneConnector, pEndLaneConnector:Tessng.ILaneConnector, dist1:float, dist2:float，unit:Tess.UnitOfMeasure) -> typing.List: ...**

创建连接段到连接段的行程时间检测器，，起点和终点都在连接段的“车道连接”上；默认单位：像素，可通过unit参数设置单位

参数：

\[in\] pStartLaneConnector：检测器起点所在“车道连接”对象  
\[in\] pEndLaneConnector：检测器终点所在“车道连接”对象  
\[in\] dist1：检测器起点距所在"车道连接”起始点距离，默认单位：像素  
\[in\] dist2：检测器终点距所在“车道连接”起始点距离，默认单位：像素  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：行程时间检测器对象  



 **def createSignalPlanSignalPhase(self, pSignalPlan:Tessng.ISignalPlan, name:str, lColor:typing.List<Online.ColorInterval>) -> Tessng.ISignalPhase: ...**

创建相位，参数 signa1plan:信控方案， name:相位名称，1Co1or:相位灯色序列，新建相位排在已有相位序列的最后

参数：

\[in\] pSignalPlan：信号方案  
\[in\] name：相位名称  
\[in\] lColor：相位灯色序列，新建相位排在已有相位序列的最后  

返回：信号相位对象

举例：

```python

```



 **def createSignalLamp(self, pPhase:Tessng.ISignalPhase, name:str, laneId:int, toLaneId:int, distance:float) -> Tessng.ISignalLamp: ...**

创建信号灯

参数：

\[in\] pPhase：相位对象  
\[in\] name：信号灯名称  
\[in\] laneId：信号灯所在车道ID，或所在“车道连接”上游车道ID  
\[in\] toLaneId：信号灯所在“车道连接”下游车道ID, 如果放到连接器上需要该参数，如果在非连接器上，此值赋值为-1  
\[in\] distance：信号灯距车道或“车道连接”起点距离，默认单位：像素  

返回：信号灯对象

举例：

```python
# 创建信号灯
for index, laneObj in enumerate(lLaneObjects):
    signalLamp = netiface.createSignalLamp(signalPhase, "信号灯{}".format(index + 1), laneObj.fromLane().id(),laneObj.toLane().id(), m2p(2.0))

```

 

 **def createTrafficSignalLamp(self, pTrafficLight:ITrafficController, name:str, laneId:int, toLaneId:int, distance:float) -> Tessng.ISignalLamp: ...**

创建信号灯

参数：

\[in\] ITrafficController：信号控制机

\[in\] pPhase：相位对象  
\[in\] name：信号灯名称  
\[in\] laneId：信号灯所在车道ID，或所在“车道连接”上游车道ID  
\[in\] toLaneId：信号灯所在“车道连接”下游车道ID, 如果放到连接器上需要该参数，如果在非连接器上，此值赋值为-1  
\[in\] distance：信号灯距车道或“车道连接”起点距离，默认单位：像素  

返回：信号灯对象

举例：

```python
# 创建信号灯
for index, laneObj in enumerate(lLaneObjects):
    signalLamp = netiface.createTrafficSignalLamp(trafficController,signalPhase, "信号灯{}".format(index + 1), laneObj.fromLane().id(),laneObj.toLane().id(), m2p(2.0))

```

 

 **def createTrafficController(self,name:str) -> Tessng.ITrafficContoller: ...**

创建信号机

参数： 
\[in\] name：信号灯名称  

 **def createSignalPlan(self,trafficController:ITrafficController, name:str, cycleTime:int, offset:int, startTime:int, endTime:int) -> Tessng.ISignalPlan: ...**

创建信控方案

参数： 
\[in\] trafficController：信号控制器

\[in\] name：信控方案名称

\[in\] cycleTime：信控 方案周期，秒

\[in\] offset：信控方案相位差  

\[in\] startTime：信控方案开始时间，秒  

\[in\] endTime：信控方案结束时间，秒    



 **def addSignalPhaseToLamp(self,signalPhaseId:int, signalLamp:Tess.ISignalLamp) -> None: ...**

将信号灯与相位绑定， 不允许跨越信号机绑定

参数： 
\[in\] signalPhaseId：相位id

\[in\] signalLamp：信号灯对象

 **def addCrossWalkSignalPhaseToLamp(self,signalPhaseId:int, signalLamp:Tess.ISignalLamp) -> None: ...**

将人行横道与相位绑定， 不允许跨越信号机绑定

参数： 
\[in\] signalPhaseId：相位id

\[in\] signalLamp：信号灯对象

 **def transferSignalPhase(self,pFromISignalPhase:Tess.ISignalPhase, pToISignalPhase:Tess.ISignalPhase, signalLamp:Tess.ISignalLamp) -> None: ...**

更换信号灯绑定的信控相位，不允许跨越信号机

参数： 
\[in\] pFromISignalPhase：原相位

\[in\] pToISignalPhase：新相位

\[in\] signalLamp：信号灯对象

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

  **def createBusStation(self, pLane:Tessng.ILane, length:float, dist:float, name:str=...，unit:Tess.UnitOfMeasure) -> Tessng.IBusStation: ...**

创建公交站点，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] pLane：车道  
\[in\] length:站点长度(单位像素)  
\[in\] dist:站点起始点距车道起点距离(单位像素)  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

  **def createRoadWorkZone(self, param:Tessng.Online.DynaRoadWorkZoneParam，unit:Tess.UnitOfMeasure) -> Tessng.Online.IRoadWorkZone: ...**

创建施工区，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] param：动态施工区信息，数据类型在文件 Plugin/_datastruct.h中定义  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

  **def createAccidentZone(self, param:Tessng.Online.DynaAccidentZoneParam) -> Tessng.IAccidentZone: ...**

创建事故区

参数：

\[in\] param：动态事故区信息，数据类型在文件pyi的Online.DynaAccidentZoneParam中定义

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
# 创建事故区
zone = tessngIFace().netInterface().createAccidentZone(accidentZone)

```

 **def createLimitedZone(param: Online.DynaLimitedZoneParam，unit:Tess.UnitOfMeasure) -> Tessng.ILimitedZone: ...**

创建限行区，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] param：动态限行区信息，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数      Online.DynaLimitedZoneParam的案例如下：  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位


```python
//例：限行区使用，距离、速度等单位为米制而非像素

dynaLimitedZoneParam = Online.DynaLimitedZoneParam();

dynaLimitedZoneParam.name = "限行区测试"; //名称

dynaLimitedZoneParam.roadId = 1; //道路ID

dynaLimitedZoneParam.location = 50; // 限行区位置

dynaLimitedZoneParam.length = 100; // 限行区长度

dynaLimitedZoneParam.limitSpeed = 40; // 限行区限速，KM/H

dynaLimitedZoneParam.mlFromLaneNumber=[0]; // 限行车道序号，本例限行右侧两车道

dynaLimitedZoneParam.duration = 3600; // 限行持续时间

gpTessInterface.netInterface().createLimitedZone(dynaLimitedZoneParam);

```

 **def createReconstruction(param: Online.DynaReconstructionParam，unit:Tess.UnitOfMeasure) -> None: ...**

创建改扩建，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] param：动态改扩建信息，数据类型在文件 Plugin/_datastruct.h中定义, python构造该数据类型的示例代码如下：  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

```python
//例：改扩建对象初始化案例


```

 **def reCalcPassagewayLength(reconstruction:Online::DynaReconstructionParam ，unit:Tess.UnitOfMeasure) -> float: ...**

重新计算保通开口长度，默认单位：像素，可通过unit参数设置单位; 这个改完后如果仿真要生效是不是还得更新改扩建对象（调用updateReconstruction）

参数：  
\[in\] reconstruction：改扩建对象，数据类型在文件pyi 中定义:Online.DynaReconstructionParam ，具体参见createReconstruction  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位

 **def createReduceSpeedArea(self, param:Online.DynaReduceSpeedAreaParam) -> Tessng.IReduceSpeedArea: ...**

创建限速区  
参数：

[in\] param：限速区参数，数据类型在文件 pyi的Online.DynaReduceSpeedAreaParam定义，其属性有：  

name：限速区名称
location：距起点距离,单位像素
areaLength：限速区长度,单位像素
roadId：路段或连接段ID
laneNumber：车道序号,从0开始
toLaneNumber：目标车道序号,如果大于等于0,roadID是连接段ID,否则是路段ID
fromTime：起始时间
toTime：结束时间
lSpeedVehiType：限速车型列表

 **def createTollLane(param:Online.TollStation.DynaTollLaneg) ->Tessng.ITollLane: ...**

创建收费车道  
\[in\]  param：动态收费车道信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化  Online.TollStation.DynaTollLane的示例代码如下：

```python

```

 **def createParkingRegion(param:Online.ParkingLot.DynaParkingRegion) ->Tessng.IParkingRegion: ...**

创建停车区

\[in\]  param：动态停车区信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化

Online.ParkingLot.DynaParkingRegion的示例代码如下：

```python

```


 **def createTollDecisionPoint(pLink:Tessng.ILink, distance:float, name:str(optional)) ->Tessng.ITollDecisionPoint: ...**

创建收费决策点  
\[in\]  pLink：收费决策点所在的路段  
\[in\]  distance：收费决策点距离路段起点的距离，默认单位：像素  
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



 **def createTollParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**

创建收费站停车时距分布  
\[in\]  param：停车时距分布参数

 **def createParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**

更新收费站停车时距分布  
\[in\]  param：停车时距分布参数


 **def createJunction (startPoint:QPointF, endPoint:QPointF, name:str) ->Tessng.IJunction: ...**

创建节点  
\[in\] startPoint：左上角起始点坐标  
\[in\] endPoint：右下角起始点坐标  
\[in\] name：节点名字  

 **def createPedestrianComposition(name:str,mpCompositionRatio:Type.Dict<int, float>) -> int : ...**

创建行人组成  
参数  
\[in\] name：组成名称  
\[in\] mpCompositionRatio：组成明细,key为行人类型编码，value为行人类型占比 ,
\[out\] 返回：组成ID，如果创建失败返回-1


 **def addLayerInfo(name:str, height:float, visible:bool,locked:bool) -> Online.Pedestrian.LayerInfo : ...**

新增层级，返回新增的层级信息  
参数  
\[in\] name：层级名称  
\[in\] height：层级高度  
\[in\] visible：是否可见  
\[in\] locked：是否锁定，锁定后面域不可以修改  
\[out\] 返回：图层对象  




 **def createPedestrianRectRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianRectRegion : ...**

创建矩形行人面域  
参数  
\[in\] startPoint：左上角  
\[in\] endPoint：右下角  
\[out\] 矩形行人面域对象  



 **def createPedestrianEllipseRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianEllipseRegion : ...** 

创建椭圆行人面域  
参数  
\[in\] startPoint：左上角  
\[in\] endPoint：右下角  
\[out\] 椭圆行人面域对象

 **def createPedestrianTriangleRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianTriangleRegion : ...**

创建三角形行人面域  
参数  
\[in\] startPoint：左上角  
\[in\] endPoint：右下角  
\[out\] 三角形行人面域对象  


 **def createPedestrianFanShapeRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianFanShapeRegion : ...**

创建扇形行人面域  
参数  
\[in\] startPoint：左上角  
\[in\] endPoint：右下角  
\[out\] 扇形行人面域对象  


 **def createPedestrianPolygonRegion(polygon:QPolygonF) -> Tessng.IPedestrianPolygonRegion : ...**

创建多边形行人面域  
参数  
\[in\] polygon：多边形顶点  
\[out\]多边形行人面域对象


 **def createPedestrianSideWalkRegion(vertexs:Type.List<QPointF>) -> Tessng.IPedestrianSideWalkRegion : ...**

创建人行道  
参数  
\[in\] vertexs：顶点列表  
\[out\] 人行道对象


 **def createPedestrianCrossWalkRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianCrossWalkRegion: ...**

创建人行横道  
参数  
\[in\] startPoint：左上角  
\[in\] endPoint：右下角  
\[out\] 人行横道对象


 **def createPedestrianStairRegion(startPoint:QPointF, endPoint:QPointF) -> Tessng.IPedestrianStairRegion: ...**

创建人行横道  
参数  
\[in\] startPoint：起点  
\[in\] endPoint：终点  
\[out\] 楼梯对象


 **def createPedestrianPathStartPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**

创建行人发生点  
参数   
\[in\] scenePos：场景坐标,  
\[out\] 行人发生点对象


 **def createPedestrianPathEndPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**

创建行人结束点  
参数  
\[in\] scenePos：场景坐标  
\[out\] 行人结束点对象


 **def createPedestrianDecisionPoint(scenePos:QPointF) -> Tessng.IPedestrianPathPoint: ...**

创建行人决策点  
参数  
\[in\] scenePos：场景坐标  
\[out\] 创建行人决策点  


 **def createPedestrianPath(pStartPoint:Tessng.IPedestrianPathPoint,pEndPoint:Tessng.IPedestrianPathPoint，middlePoints：Type.List<QPointF>) -> Tessng.IPedestrianPath: ...**

创建行人路径（或行人局部路径）  
参数  
\[in\] pStartPoint：行人发生点（或行人决策点）  
\[in\] pEndPoint：行人结束点  
\[in\] middlePoints：一组中间必经点  
\[out\] 行人路径对象


 **def createCrossWalkSignalLamp(pTrafficController:Tessng.ITrafficController,name:str，crosswalkid：str, scenePos:QPointF, isPositive:bool) -> Tessng.ICrosswalkSignalLamp: ...**

创建人行横道信号灯  
参数  
\[in\]  pTrafficController：信号机  
\[in\] name：名称  
\[in\] crosswalkId：人行横道ID  
\[in\] scenePos：位于人行横道内的场景坐标  
\[in\] isPositive：信号灯管控方向是否为正向  
\[out\]人行横道信号灯对象  



 **def updateLink(self, link:Tessng._Link, lLane:typing.Sequence=..., lPoint:typing.Sequence=...，unit:Tess.UnitOfMeasure) -> Tessng.ILink: ...**

更新路段，更新后返回路段对象  
参数：  
\[in\] link：更新的路段数据  
\[in\] lLink：更新的车道列表数据  
\[in\] lPoint：更新的断点集合  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：更新后的路段对象  
注：如传入米制参数，请勿遗忘传入lLane与lPoint参数。  

 **def updateConnector(self, connector:Tessng._Connector) -> Tessng.IConnector: ...**

更新连接段，更新后返回连接段对象

参数：

\[in\] connector：连接段数据

返回：更新后的连接段对象

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

  **def updateRoadWorkZone(self, pIRoadWorkZone:Tessng.Online.IRoadWorkZone，unit:Tess.UnitOfMeasure) -> None: ...**

更新施工区，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] pIRoadWorkZone：将要移除的施工区对象

 **def updateLimitedZone(param: Online.DynaLimitedZoneParam) -> boolen: ...**

更新限行区

参数：

\[in\] param：动态限行区信息，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数   Online.DynaLimitedZoneParam的案例见createLimitedZone

 **def updateReconStruction(param: Online.DynaReconstructionParam，unit:Tess.UnitOfMeasure) -> None: ...**

更新改扩建

参数：
\[in\] param：动态改扩建信息，数据类型在文件 Plugin/_datastruct.h中定义, python构造该数据类型的示例代码见createReconstruction  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  

 **def removeReconstruction(ref_pIReconstruction: Online.DynaReconstructionParam) -> None: ...**

移除改扩建
参数：  
\[in\] pIReconstruction：将要移除的改扩建对象引用, python构造该数据类型的示例代码见 createReconstruction

 **def updateReduceSpeedArea(self, param:Online.DynaReduceSpeedAreaParam) -> bool: ...**

更新限速区  
参数：

[in\] param：限速区参数，数据类型在文件 pyi的Online.DynaReduceSpeedAreaParam定义，

 **def updateTollLane(param: Online.TollStation.DynaTollLane) ->Tessng.ITollLane: ...**

更新收费车道  

\[in\]  param：：动态收费车道信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化Online.ParkingLot.DynaParkingRegion的示例见createTollLane：

 **def updateParkingRegion(param: Online.ParkingLot.DynaParkingRegion) ->Tessng.IParkingRegion: ...**

更新停车区  
\[in\]  param：动态停车区信息，数据类型在文件 Plugin/_datastruct.h中定义, python初始化  Online.ParkingLot.DynaParkingRegion的示例见createTollLane：

 **def updateTollParkingTimeDis(param:Online.TollStation.DynaTollParkingTimeDis) ->Online.TollStation.DynaTollParkingTimeDis: ...**

更新收费站停车时距分布  
\[in\]  param：停车时距分布参数

 **def updateParkingTimeDis (param:Online.ParkingLot.DynaParkingTimeDis) ->Online.ParkingLot.DynaParkingTimeDis: ...**

更新停车场停车时距分布  
\[in\]  param：停车时距分布参数

 **def updateJunctionName(id:int, name:str) ->None: ...**

更新节点名字  
\[in\] id：节点ID  
\[in\] name：节点名字

 **def updateFlowTimeInterval(timeId:int，startTime:int, endTime:int) ->Online.Junction.FlowTimeInterval: ...**

更新时间段(节点的流量时间段)  
\[in\]timeId：时间段ID  
\[in\]startTime：开始时间(秒)  
\[in\]endTime：结束时间(秒)

 **def updateFlow(self, timeId:int， junctionId:int, turningId:int, inputFlowValue:int) ->bool: ...**

更新节点流向流量  
\[in\]timeId：时间段ID  
\[in\]junctionId：节点ID  
\[in\]turningId：转向ID  
\[in\]inputFlowValue：输入流量值（辆/小时）

 **def updateFlowAlgorithmParams(self, theta:float， bpra:float, bprb:float, maxIterateNum:int，useNewPath:bool) ->bool: ...**

更新流量算法参数  
\[in\]theta：参数θ(0.01-1)  
\[in\]BPR路阻参数A(0.05-0.5)   
\[in\]bprb：BPR路阻参数B(1-10)  
\[in\]maxIterateNum：最大迭代次数(1-5000)  
\[in\]useNewPath：是否重新构建静态路径

 **def updatePathBuildParams(self,bDeciPointPosFlag:bool, bLaneConnectorFlag:bool，InputLineMinPathNum：long(defulat=3)) ->None: ...**

更新静态路径构建参数  
\[in\]  bDeciPointPosFlag：是否考虑决策点位置  
\[in\]  bLaneConnectorFlag：是否考虑车道连接  
\[in\]  InputLineMinPathNum：最小路径数量(默认3)  

 **def updatePedestrianComposition(compositionId:int, mpCompositionRatio:Type.Dict<int, float>) -> bool : ...**

创建行人组成  
参数  
\[in\] compositionId：组成Id  
\[in\] mpCompositionRatio：组成明细,key为行人类型编码，value为行人类型占比 ,
\[out\] 返回：True表示更新成功，False表示更新失败

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

 **def updatePedestrianDecisionPointConfigInfo(info:Online.Pedestrian.PedestrianDecisionPointConfigInfo ) -> bool : ...**

更新行人决策点配置信息  
参数  
\[in\] info：行人决策点配置信息  
\[out\] 返回：是否更新成功  



 **def removeLink(self, pLink:Tessng.ILink) -> None: ...**

移除路段，从场景中移除pLink，但不从文件中删除，保存路网后才会从路网文件中删除  
参数：  
\[in\] pLink：将要移除的路段

 **def removeConnector(self, pConnector:Tessng.IConnector) -> None: ...**

移除连接段，从场景中移除pLink，但不从文件中删除，保存路网后才会从路网文件中删除

参数：

\[in\] pConnector：连接段对象

 **def removeGuidArrow(self, pArrow:Online.GuideArrowType) -> None: ...**

移除导向箭头

参数：  
\[in\] pArrow：导向箭头对象


 **def removeDispatchPoint(self, pDispPoint:Tessng.IDispatchPoint) -> bool: ...**

移除发车点

参数：

\[in\] pDispPoint：发车点对象

 **def removeVehicleComposition(self, vehiCompId:int) -> bool: ...**

移除车型组成

参数：

\[in\] vehiCompId：车型组成ID

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

 **def removeVehiCollector(self, pCollector:Tessng.IVehicleDrivInfoCollector) -> bool: ...**

移除车辆信息采集器

参数：

\[in\] pCollector：车辆信息采集器

 **def removeSignalPhase(self, pPlan:Tessng.ISignalPlan, phaseId:int) -> None: ...**

移除已有相位，相位移除后，原相位序列自动重排,

参数：

\[in\] pPlan：信控方案

\[in\] phaseId：将要移除的相位ID

 **def removeSignalPhaseFromLamp(self, signalPhaseId:int, signalLamp:Tess.ISignalLamp) -> None: ...**

为信号灯移除指定的（已绑定的）相位(如果相位列表只存在一个相位则将关联的相位设置为nu11)

 **def removeBusLine(self, pBusLine:Tessng.IBusLine) -> bool: ...**

移除公交线路

参数：

\[in\] pBusLine：将要移除的公交线路对象

 **def removeBusStation(self, pStation:Tessng.IBusStation) -> bool: ...**

移除公交站点

参数：

\[in\] pStation：公交站点对象

 **def removeBusStationFromLine(self, pBusLine:Tessng.IBusLine, pStation:Tessng.IBusStation) -> bool: ...**

将公交站点与公交线路的关联关系解除

参数：

\[in\] pBusLine：公交线路

\[in\] pStation：公交站点

 **def removeAccidentZone(self, pIAccidentZone:Tessng.IAccidentZone) -> None: ...**

移除事故区

 **def removeRoadWorkZone(self, pIRoadWorkZone:Tessng.Online.IRoadWorkZone) -> None: ...**

移除施工区

参数：

\[in\] pIRoadWorkZone：将要移除的施工区对象

 **def removeLimitedZone(pILimitedZone: Tessng.ILimitedZone) -> boolen: ...**

移除限行区 Tessng.ILimitedZone 还是 Online.ILimitedZone

参数：

\[in\] pILimitedZone：将要移除的限行区对象，数据类型在文件 Plugin/_datastruct.h中定义, python 构造限行区参数 Online.DynaLimitedZoneParam的案例见createLimitedZone

 **def removeReduceSpeedArea(pIReduceSpeedArea:Tessng.IReduceSpeedArea) ->None: ...**

移除限速区
参数：  
\[in\] pIReduceSpeedArea：限速区对象

 **def removeTollLane(pITollLane:Tessng.ITollLane) ->None: ...**

移除收费车道


 **def removeTollDecisionPoint(pITollDecisionPoint:Tessng.ITollDecisionPoint) ->None: ...**

移除收费决策点


 **def removeParkingRegion(pIParkingRegion:Tessng.IParkingRegion) ->None: ...**

移除停车区


 **def removeParkingDecisionPoint(pIParkingDecisionPoint:Tessng.IParkingDecisionPoint) ->None: ...**

移除收费决策点


 **def removeTollRouting(pITollRouting:Tessng.ITollRouting) ->None: ...**

移除收费路径


 **def removeParkingRouting(pIParkingRouting:Tessng.IParkingRouting) ->None: ...**

移除停车路径

 **def removeTollLaneById(id:int) ->None: ...**

通过ID移除收费车道

 **def removeTollDecisionPointById(id:int) ->None: ...**

通过ID移除收费决策点

 **def removeParkingRegionById(id:int) ->None: ...**

通过ID移除停车区

 **def removeParkingDecisionPointById(id:int) ->None: ...**

通过ID移除停车决策点

 **def removeTollRoutingById(id:int) ->None: ...**

通过ID移除收费路径

 **def removeParkingRoutingById(id:int) ->None: ...**

通过ID移除停车路径

 **def removeTollParkingTimeDis(id:int) ->None: ...**

移除收费站停车时距分布  
\[in\]  id ：停车时距分布参数的Id

 **def removeParkingTimeDis(id:int) ->None: ...**

移除停车场停车时距分布  
\[in\]  param：停车时距分布ID

 **def removeJunction(id:int) ->None: ...**

删除节点  
\[in\] id：节点ID

 **def deleteFlowTimeInterval(timeId:int) ->bool: ...**

删除时间段(节点的流量时间段)  
\[in\]timeId：时间段ID

 **def removePedestrianComposition(compositionId:int) -> bool : ...**

移除行人组成  
参数  
\[in\] compositionId：组成Id  
\[out\] 返回：True表示成功，False表示失败

 **def removeLayerInfo(layerId:int) -> None : ...**

删除某个层级，会删除层级当中的所有元素  
参数  
\[in\] layerId：层级Id

 **def removePedestrianEllipseRegion(pIPedestrianEllipseRegion:Tessng.IPedestrianEllipseRegion) -> None : ...**

删除椭圆行人面域  
参数  
\[in\] pIPedestrianEllipseRegion：椭圆行人面域对象

 **def removePedestrianRectRegion(pIPedestrianRectRegion:Tessng.IPedestrianRectRegion) -> None : ...** 

删除矩形行人面域  
参数  
\[in\] pIPedestrianRectRegion：矩形行人面域对象

 **def removePedestrianTriangleRegion(pIPedestrianTriangleRegion:Tessng.IPedestrianTriangleRegion) -> None : ...**

删除三角形行人面域  
参数  
\[in\] pIPedestrianEllipseRegion：三角形行人面域对象  

 **def removePedestrianFanShapeRegion(pIPedestrianTriangleRegion:Tessng.IPedestrianTriangleRegion) -> None : ...**  

删除扇形行人面域  
参数  
\[in\] IPedestrianTriangleRegion：扇形行人面域对象

 **def removePedestrianPolygonRegion(pIPedestrianPolygonRegion:Tessng.IPedestrianPolygonRegion) -> None : ...**

删除多边形行人面域  
参数  
\[in\] pIPedestrianPolygonRegion：多边形行人面域对象

 **def removePedestrianSideWalkRegion(pIPedestrianSideWalkRegion:Tessng.IPedestrianSideWalkRegion) -> None : ...**

删除人行道  
参数  
\[in\]  pIPedestrianSideWalkRegion：人行道对象

 **def removePedestrianStairRegion(pIPedestrianStairRegion:Tessng.IPedestrianStairRegion) -> None : ...**

删除楼梯  
参数   
\[in\] pIPedestrianStairRegion：楼梯对象  

 **def removePedestrianPathStartPoint(pIPedestrianPathStartPoint:Tessng.IPedestrianPathPoint) -> None : ...**

删除行人发生点  
参数  
\[in\] pIPedestrianPathStartPoint：行人发生点对象

 **def removePedestrianPathEndPoint(pIPedestrianPathEndPoint:Tessng.IPedestrianPathPoint) -> None : ...**

删除行人结束点  
参数  
\[in\] pIPedestrianPathStartPoint：删除行人结束点

 **def removePedestrianDecisionPoint(pIPedestrianDecisionPoint:Tessng.IPedestrianPathPoint) -> None : ...**

删除行人决策点  
参数  
\[in\] pIPedestrianPathStartPoint：行人决策点对象 

 **def removePedestrianPath(pIPedestrianPath:Tessng.IPedestrianPath) -> None : ...**

删除行人路径  
参数  
\[in\] pIPedestrianPath：行人路径对象

 **def removeCrossWalkSignalLamp(pICrosswalkSignalLamp:Tessng.ICrosswalkSignalLamp) -> None : ...**

删除人行横道信号灯  
参数  
\[in\]  pICrosswalkSignalLamp：人行横道信号灯对象

 **def removePedestrianCrossWalkRegion(pIPedestrianCrossWalkRegion:Tessng.IPedestrianCrossWalkRegion) -> None : ...**

删除人行横道  
参数  
\[in\] pIPedestrianCrossWalkRegion：人行横道对象





 **def createEmptyNetFile(self, filePath:str, dbver:int=...) -> bool: ...**

创建空白路网

参数：

\[in\] filePath：空白路网全路径名

\[in\] dbver:：数据库版本

 **def initSequence(self, schemaName:str=...) -> bool: ...**

初始化数据库序列，对保存路网的专业数据库序列进行初始化，目前支持PostgreSql

参数：

\[in\] schemaName：数据库的schema名称

 **def buildNetGrid(self, width:float=...，unit:Tess.UnitOfMeasure) -> None: ...**

路网网格化，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] width：单元格宽度，默认单位：米  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  


 **def findSectionOn1Cell(self, point:PySide2.QtCore.QPointF，unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据point查询所在单元格所有Section，默认单位：像素，可通过unit参数设置单位

参数：
\[in\] point：路网场景中的点  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：ISection列表   

 **def findSectionOn4Cell(self, point:PySide2.QtCore.QPointF，unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据point查询最近4个单元格所有经过的ISection，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] point：路网场景中的一个点  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：ISection列表

 **def findSectionOn9Cell(self, point:PySide2.QtCore.QPointF，unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据point查询最近9个单元格所有经过的ISection，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] point：路网场景中的一个点  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：ISection列表  

 **def locateOnSections(self, point:PySide2.QtCore.QPointF, lSection:typing.Sequence, referDistance:float=...，unit:Tess.UnitOfMeasure) -> typing.List: ...**

根据point对lSection列表中每一个Section所有LaneObject求最短距离，返回Location列表，列表按最短距离排序，从小到大，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] point：路网场景中的一个点  
\[in\] lSection：section列表  
\[in\] referDistance：LaneObject上与point最近的点到LaneObject起点距离，默认单位：像素，是大约数，只为提高计算效率，默认值为0  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

 

 **def locateOnCrid(self, point:PySide2.QtCore.QPointF, cellCount:int=...，unit:Tess.UnitOfMeasure) -> typing.List: ...**

point周围若干个单元格里查询LaneObject，默认单位：像素，可通过unit参数设置单位

参数：  
\[in\] point：路网场景中的一个点  
\[in\] cellCount：单元格数，小于1时默认为1，大于1小于4时默认为4，大于4时默认为9  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：Online::Location列表

 **def boundingRect(self, pIVehicle:Tessng.IVehicle, outRect:PySide2.QtCore.QRectF) -> bool: ...**

路网外围Rect，用以获取路网边界

 **def getIDByItemName(self, name:str) -> int: ...**

根据路网元素名获取自增ID

参数：

\[in\] name：路网元素名。路网元素名的定义在文件plugin/_netitem.h中定义

**def moveLinks(links:Type.List<Tessng.ILink>, offset:QPointF，unit:Tess.UnitOfMeasure) -> None: ...**

移动路段及相关连接段，默认单位：像素，可通过unit参数设置单位

参数：
\[in\] lLink：要移动的路段列表  
\[in\] offset：移动的偏移量, 指移动到指定点吗？？？  
\[in\] unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位







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

 **def getVehisStatus(self, batchNumber:int，unit:Tess.UnitOfMeasure) -> typing.List<Tess.Online.VehicleStatus>: ...**

获取所有正在运行的车辆状态，包括轨迹  
参数：  
\[in\] batchNumber: 批次号  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：车辆状态（包括轨迹）Online.VehicleStatus列表  
注：如使用米制单位，请勿遗忘传入unit参数  
举例：

```python
# TESSNG 顶层接口
iface = tessngIFace()
# TESSNG 仿真子接口
simuiface = iface.simuInterface()
# 当前正在运行车辆列表
vehis = simuiface.allVehiStarted()

```

 

 

 **def getVehiTrace(self, vehiId:int，unit:Tess.UnitOfMeasure) -> typing.List: ...**

获取指定车辆运行轨迹，默认单位：像素，可通过unit参数设置单位

参数：

\[in\] vehiId：车辆ID  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
返回：车辆运行轨迹，即Online.VehiclePosition列表  

 **def getSignalPhasesColor(self) -> typing.List<Online.SignalPhaseColor>: ...**

获取当前所有信号灯组相位颜色

返回:当前相位颜色Online.SignalPhaseColo列表，包括各相位当前颜色设置的时间和已持续时间。 

 **def getVehisInfoCollected(self) -> typing.List< Online.VehiInfoCollected>: ...**

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

 

 

 **def getVehisInfoAggregated(self) -> typing.List<Online.VehiInfoAggregated >: ...**

获取最近集计时间段内采集器采集的所有车辆集计信息

返回：采集器集计数据Online.VehiInfoAggregated列表

 **def getVehisQueueCounted(self) -> typing.List<Online.VehiQueueCounted>: ...**

获取当前排队计数器计数的车辆排队信息

返回：车辆排队信息Online.VehiQueueCounted列表

 **def getVehisQueueAggregated(self) -> typing.List<Online.VehiQueueAggregated>: ...**

获取最近集计时间段内排队计数器集计数据

返回：排队计数器集计数据Online.VehiQueueAggregated列表

 **def getVehisTravelDetected(self) -> typing.List<Online.VehiTravelDetected>: ...**

​    获取当前行程时间检测器完成的行程时间检测信息

返回：行程时间检测器数据Online.VehiTravelDetected列表

 **def getVehisTravelAggregated(self) -> typing.List<Online.VehiTraveAggregated>: ...**

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

 

 **def vehisInLink(self, linkId:int) -> typing.List<Tess.IVehicle>: ...**

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

 **def vehisInLane(self, laneId:int) -> typing.List<Tess.IVehicle>: ...**

指定ID车道上的车辆

参数：

\[in\] laneId：车道ID

返回：车辆列表

 **def vehisInConnector(self, connectorId:int) -> typing.List<Tess.IVehicle>: ...**

指定ID连接段上的车辆

参数：

\[in\] connectorId：连接段ID

返回：车辆列表

 **def vehisInLaneConnector(self, connectorId:int, fromLaneId:int, toLaneId:int) -> typing.List<Tess.IVehicle>: ...**

指定连接段ID及上游车道ID和下游车道ID相关“车道连接”上的车辆

参数：

\[in\] connectorId: 连接段ID

\[in\] fromLaneId：上游车道ID

\[in\] toLaneId：下游车道ID



 **def queueRecently(queueCounterId:int, ref_queueLength:float, ref_vehiCount:int，unit:Tess.UnitOfMeasure) -> bool: ...**

获取排队计数器最近一次排队信息, 返回值为是否成功的标签，具体的排队和流量信息见入参ref_queueLength，ref_vehiCount。  
该函数的入参为引用，函数直接修改入参数据  
参数：  
\[in\] queueCounterId：排队计数器ID  
\[in & out\] queueLength：排队长度，默认单位：像素，可通过unit参数设置单位  
\[in & out\] vehiCount：排队车辆数，默认单位：像素，可通过unit参数设置单位  
\[in\]  unit：单位参数，默认为Default，Metric表示米制单位，Default表示不指定单位返回接口默认的单位  
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

  **def ref_reCalcdesirSpeed_unit(self, pIVehicle:Tessng.IVehicle, ref_desirSpeed:Tessng.objreal, unit:UnitOfMeasure) -> bool: ...**

重新计算期望速度，TESS NG调用此方法时将车辆当前期望速度赋给inOutDesirSpeed，如果需要，用户可在此方法重新计算期望速度，并赋给inOutDesirSpeed。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutDesirSpeed：重新设置前后的车辆期望速度，默认单位：像素/秒；

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制



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

 

**def ref_reSetFollowingParam_unit(self, pIVehicle:Tessng.IVehicle, ref_inOutSafeInterval:Tessng.objreal, ref_inOutSafeDistance:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

重新设置跟驰模型的安全间距和安全时距（支持单位参数)。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutSafeInterval：安全时距，单位：秒；

[in、out] inOutSafeDistance：安全间距：单位：像素；

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

返回：false：忽略，true：用inOutSafeInterval设置安全时距，用inOutSafeDistance设置安全间距



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

 **def ref_reSetDistanceFront_unit(self, pIVehicle:Tessng.IVehicle, distance:Tessng.objreal, s0:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

重新设置前车距及安全跟车距离

参数：

\[in\] pIVehicle：车辆对象

[in、out] distance：当前车辆与前车的距离，默认单位：像素

[in、out] s0：安全跟车距离，默认单位：像素

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

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

 

 **def ref_reSetSpeed_unit(self, pIVehicle:Tessng.IVehicle, ref_inOutSpeed:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

重新设置车速。TESS NG调用此方法时将当前计算所得车速赋给**ref_inOutSpeed.value**，如果需要，用户可以在此方法重新计算车速并赋给ref_inOutSpeed.value。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutSpeed：重新计算前后的车速，单位：像素/秒。

返回：False：忽略，True：用inOutSpeed设置车辆当前速度

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制



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

 **def candidateLaneConnector(self, pIVehicle:Tessng.IVehicle, lInLC:typing.Sequence) -> typing.List: ...**

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



 **def ref_calcSpeedLimitByLane_unit(self, pILink:Tessng.ILink, laneNumber:int, ref_outSpeed:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

由车道确定的限制车速（最高速度, 公里/小时）

参数：

\[in\] pILink：路段

\[in\] laneNumber：,laneNumber:车道序号，最右侧编号为0

[in、out] outSpeed：限制速度，公里/小时

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

返回：False：忽略，True：用outSpeed限制指定车道速度



 **def ref_calcMaxLimitedSpeed(self, pIVehicle:Tessng.IVehicle, ref_inOutLimitedSpeed:Tessng.objreal) -> bool: ...**

重新计算车辆当前最大限速，不受道路限速的影响。在没有插件干预的情况下，车辆速度大于道路限度时按道路最大限速行驶，在此方法的干预下，可以提高限速，让车辆大于道路限速行驶。

TESS NG调用此方法时将当前最高限速赋给inOutLimitedSpeed，如果需要，用户可以在方法里重新设置inOutLimitedSpeed值。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutLimitedSpeed：计算前后的最大限速，单位：像素/秒。

返回结果：

​    如果返回False则忽略，否则取inOutLimitedSpeed为当前道路最大限速。





 **def ref_calcMaxLimitedSpeed_unit(self, pIVehicle:Tessng.IVehicle, ref_inOutLimitedSpeed:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

重新计算车辆当前最大限速，不受道路限速的影响。在没有插件干预的情况下，车辆速度大于道路限度时按道路最大限速行驶，在此方法的干预下，可以提高限速，让车辆大于道路限速行驶。

TESS NG调用此方法时将当前最高限速赋给inOutLimitedSpeed，如果需要，用户可以在方法里重新设置inOutLimitedSpeed值。

参数：

\[in\] pIVehicle：车辆对象；

[in、out] inOutLimitedSpeed：计算前后的最大限速，单位：像素/秒。

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

返回结果：

​    如果返回False则忽略，否则取inOutLimitedSpeed为当前道路最大限速。

 **def ref_calcDistToEventObj(self, pIVehicle:Tessng.IVehicle, ref_dist:Tessng.objreal) -> bool: ...**

计算到事件对象距离，如到事故区、施工区的距离

参数：

\[in\] pIVehicle：车辆

[in、out] dist：车辆中心点距事件对象距离，单位像素

返回：False：忽略，True：用dist计算安全变道距离等



 **def ref_calcDistToEventObj_unit(self, pIVehicle:Tessng.IVehicle, ref_dist:Tessng.objreal，unit:UnitOfMeasure) -> bool: ...**

计算到事件对象距离，如到事故区、施工区的距离

参数：

\[in\] pIVehicle：车辆

[in、out] dist：车辆中心点距事件对象距离，单位像素

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

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

 **def ref_calcAcce_unit(self, pIVehicle:Tessng.IVehicle, ref_acce:Tessng.objreal， unit:UnitOfMeasure) -> bool: ...**

计算加速度； tessng的车辆按照此加速度进行下一步状态更新

\[in\] pIVehicle：待计算加速度的车辆

[out] ref_acce：计算结果，单位：像素/秒^2

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

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

  **def ref_reSetAcce_unit(self, pIVehicle:Tessng.IVehicle, ref_inOutAcce:Tessng.objreal, unit:UnitOfMeasure) -> bool: ...**

重新计算加速度。TESS NG调用此方法时将当前计算所得加速度赋给inOutAcce，如果需要，用户可以在此方法中重新计算加速度并赋给ref_inOutAcce.value。

参数：

\[in\] pIVehicle：车辆对象

[in、out] ref_inOutAcce：重新计算前及计算后的加速度，单位：像素/秒^2

[ in ] unit：单位参数，默认为Default，Metric表示米制单位，Default表示无单位限制

返回结果：

如果返回False则忽略，如果返回True，则将inOutAcce作为当前加速度。

 

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

