#include "MyNet.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "ilink.h"
#include "IAccidentZone.h"
#include "UnitChange.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    Q_UNUSED(this);
}

MyNet::~MyNet(){
}