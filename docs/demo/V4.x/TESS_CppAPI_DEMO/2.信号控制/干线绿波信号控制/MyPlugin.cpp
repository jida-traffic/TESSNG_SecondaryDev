#include "MyPlugin.h"
#include "tessinterface.h"
#include "guiinterface.h"
#include "MySimulator.h"
#include "MyNet.h"

MyPlugin::MyPlugin() : mpMyNet(nullptr), mpMySimulator(nullptr){}

MyPlugin::~MyPlugin(){}

void MyPlugin::init()
{
    mpMyNet = new MyNet();
    mpMySimulator = new MySimulator();
    QMainWindow* pMainWindow = gpTessInterface->guiInterface()->mainWindow();
    QObject::connect(mpMySimulator, SIGNAL(forReStartSimu()), pMainWindow, SLOT(doStartSimu()), Qt::QueuedConnection);
    QObject::connect(mpMySimulator, SIGNAL(forStopSimu()), pMainWindow, SLOT(doStopSimu()), Qt::QueuedConnection);
}

void MyPlugin::unload()
{
    delete mpMyNet;
    mpMyNet = nullptr;
    delete mpMySimulator;
    mpMySimulator = nullptr;
}

CustomerGui* MyPlugin::customerGui()
{
    return nullptr;
}

CustomerNet* MyPlugin::customerNet()
{
    return mpMyNet;
}

CustomerSimulator* MyPlugin::customerSimulator()
{
    return mpMySimulator;
}
