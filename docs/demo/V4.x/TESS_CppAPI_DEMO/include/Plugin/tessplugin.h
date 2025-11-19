/********************************************************************************
* 插件最顶层抽象类，用户基于此类开发用户自己插件
*********************************************************************************/

#ifndef TESSPLUGIN_H
#define TESSPLUGIN_H

#include <QObject>
#include <QJsonObject>

class CustomerGui;
class CustomerNet;
class CustomerSimulator;

#include "../tessinterfaces_global.h"
#include "../tessinterface.h"

class TESSINTERFACES_EXPORT TessPlugin
{

public:

	TessPlugin(QString const& name = "defaultPlugin",
		QString const& description = "",
		QString const& category = "",
		QString const& version = "",
		QString const& uuid = "",
		QString const& valicode = "")
		: mName(name)
		, mDescription(description)
		, mCategory(category)
		, mVersion(version)
		, mUuid(uuid)
		, mValiCode(valicode)
	{}

	virtual ~TessPlugin() {};

	QString const & name() const
	{
		return mName;
	}

	QString & name()
	{
		return mName;
	}

	QString const & version() const
	{
		return mVersion;
	}

	QString & version()
	{
		return mVersion;
	}

	QString const & description() const
	{
		return mDescription;
	}

	QString  & description()
	{
		return mDescription;
	}

	QString const & category() const
	{
		return mCategory;
	}

	QString & category()
	{
		return mCategory;
	}

	QString const & uuid() const
	{
		return mUuid;
	}

	QString & uuid()
	{
		return mUuid;
	}

	//是否插件
	static bool isPlugin();
	//设置本动态库是否插件，被集成到其它插件时会调用此方法
	static void setIsPlugin(bool isPlugin);

	//初始化，可在此实现方法里实例化CustomerGui、CustomerNet、CustomerSimulator三个类的子类
	virtual void init();
	//卸载插件时所做工作，可以在此方法从内此移除三个子类的实例
	virtual void unload();
	//对用户或环境等条件进行认证评估的结果，如果返回false，插件不起作用
	virtual bool isChecked();
	//返回CustomerGui实例指针
	virtual CustomerGui *customerGui();
	//返回CustomerNet实例指针
	virtual CustomerNet *customerNet();
	//返回CustomerSimulator实例指针
	virtual CustomerSimulator *customerSimulator();
	//当用户没有权限使用插件时，可在此方法让特殊菜单项或工具按钮无效
	virtual void unEnableActions();
	//当用户有权限使用插件时，可在此方法让特殊菜单项或工具按钮有效
	virtual void enableActions();
	//此方法可在一些特殊开发模式下起作用
	virtual void callMethod(QString methodName, QList<void *> params);
	//身份认证，认证规则由插件方实现，TESSNG调用得到结果
	virtual bool certificate(QString account, int code);
	/*
	身份认证，认证规则由插件方实现，TESSNG调用得到结果
	code:附加码，grade:用户级别，netFilePath:路网路径
	*/
	virtual bool certificate(QString account, int code, int grade, QString netFilePath);
	//项目名称，相同插件在不同环境项目名称可能不一样，有时侯需要用项目名称进行识别，默认是插件名
	virtual QString projName();

	//返回插件全路径名
	QString library();
	//设置插件全路径名，由TESSNG初始化插件时调用
	void setLibrary(QString lib);
	//TESSNG所在文件夹
	QString tessngDir();
	//将TESSNG所在文件夹路径字符串赋给属性mTessngDir
	void setTessngDir(QString dir);
    QJsonObject tessngConfig();
    void setTessngConfig(QJsonObject obj);
	//设置config配置对象属性
	void setConfigProperty(QString key, QJsonValue value);
	//插件所在文件夹
	QString workspace();
	//设置工作所在文件夹
	void setWorkspace(QString workspace);
	//插件文件baseName(不含后缀)
	QString pluginFileBaseName();
	//插件文件名（含扩展名）
	QString pluginFileName();
	//插件文件后缀
	QString pluginFileSuffix();
	//验证码
	QString valiCode();

private:
	//是否插件，默认是，但只是作为动态库集成到另一个插件时HthV2x就不是插件
	static bool mbIsPlugin;
	//插件名称
	QString mName;
	//描述
	QString mDescription;
	//分类
	QString mCategory;
	//版本
	QString mVersion;
	//uuid
	QString mUuid;

	//TESSNG可执行程序相同路径下配置文件config.json内容，插件也可在此文件增加插件所需的配置
	QJsonObject mTessngConfig;
	//TESSNG所在文件夹，插件不在TESSNG文件夹下又需要知道TESSNG文件夹的情况下可读取该属性
	QString mTessngDir;
	//插件全路径名
	QString mLibPath;
	//插件所在文件夹
	QString mWorkSpace;
	//插件文件basename
	QString mPluginFileBaseName;
	//插件文件名
	QString mPluginFileName;
	//插件文件后缀
	QString mSuffix;
	//验证吗
	QString mValiCode;
};

#endif // TESSPLUGIN_H

extern TessPlugin TESSINTERFACES_EXPORT *gpTessPlugin;