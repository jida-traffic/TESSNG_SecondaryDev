# 软件安装

## 下载

win10系统: [TESSNG V3.1 二次开发包](https://www.jidatraffic.com/#/simulation)

Linux系统： [TESSNG V3.1 二次开发包](https://www.jidatraffic.com/#/simulation)

## 安装与激活

第一步：下载TESSNG Python二次开发包 [TESSNG V3.1 二次开发包](https://www.jidatraffic.com/#/simulation)并解压

![开发包内文件列表](/img/p27.png)

TESS NG python 接口开发包有两个关键文件（Windows环境）：Tessng.pyd，以及附加动态库shiboken2.abi3.dll。另外Tessng.pyi是文本形式的描述文件，在开发时提供方便，但不是必须的。

开发包主要文件截图如下：

![开发包主要文件截图](/img/p8.png)

第二步：安装python3.6环境和IDE（建议使用pycharm）
step1: 安装python3.6.6, 下载网址为：https://www.python.org/ftp/python/3.6.6/python-3.6.6-amd64.exe
![python安装界面](/img/python.png)

step2: 安装pycharm, 下载网址为：https://www.jetbrains.com/pycharm/download/download-thanks.html?platform=windows&code=PCC

![python安装界面](/img/pycharm1.png)
![python安装界面](/img/pycharm2.png)
![python安装界面](/img/pycharm3.png)
![python安装界面](/img/pycharm4.png)
![python安装界面](/img/pycharm5.png)

第三步：pycharm打开下载的二次开发包中的范例工程TESS_PythonAPI_EXAMPLE

![pycharm打开范例文件](/img/p1.png)
* 双击main.py， 使用pycharm打开，选择在项目中打开当前文件；
![python安装界面](/img/pycharm6.png)
* 选择右下角的<无解释器>选择添加新的解释器；
![python安装界面](/img/pycharm7.png)

* 选择Virtualenv 环境-新建，基础解释器选择刚安装的Python3.6.6目录下的python.exe， 然后点击确定；
![python安装界面](/img/pycharm8.png)
* 点击左下角的终端，出现(venv)表示环境配置成功，如果没有出现就把终端关掉重新打开；
![python安装界面](/img/pycharm9.png)
* 下载第三方库Pyside2,在终端位置输入以下命令：
pip install PySide2 -i https://pypi.tuna.tsinghua.edu.cn/simple/
出现“Successfully installed PySide2-5.15.2.1 shiboken2-5.15.2.1”表示安装成功；
![python安装界面](/img/pycharm10.png)
![python安装界面](/img/pycharm11.png)
* 在界面任意位置右击，选择运行main, 出现激活界面表示代码启动成功
![python安装界面](/img/pycharm12.png)
![python安装界面](/img/pycharm13.png)

第四步：激活TESSNG软件license

用户在首次使用二次开发包时需要激活软件；
激活方式：双击运行main.py函数，弹出激活弹窗，选择激活license文件。具体激活界面如下：

![Python开发环境激活界面](/img/p10.jpg)

试用用户与首次激活软件流程相同，采用安装包的Cert文件夹下的JidaTraffic_key激活即可。

​    软件的试用期为30天，以前激活过软件的客户激活V3.0.1版本时重新延长30天试用期（识别激活电脑的物理地址）。试用期结束后将无法调用接口的二次开发功能。

​    商业版用户使用不受限制。

第五步： 激活后关闭弹窗，重新运行main.py即可运行范例

![范例运行界面](/img/p31.png)







<!-- ex_nonav -->

