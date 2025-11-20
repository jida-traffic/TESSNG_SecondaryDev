#include "MyPlugin.h"

#include <QDockWidget>
#include <QMessageBox>

#include "MyNet.h"
#include "MySimulator.h"

MyPlugin::MyPlugin()
	:mpMyNet(nullptr), mpMySimulator(nullptr)
{
}

void MyPlugin::init()
{
	mpMyNet = new MyNet();
	mpMySimulator = new MySimulator();
}

void MyPlugin::unload() {
	delete mpMyNet;
	delete mpMySimulator;
}

CustomerGui *MyPlugin::customerGui()
{
	return nullptr;
}

CustomerNet *MyPlugin::customerNet()
{
	return mpMyNet;
}

CustomerSimulator *MyPlugin::customerSimulator()
{
	return mpMySimulator;
}

MyPlugin::~MyPlugin()
{
}