#include "MyNet.h"

#include <QDebug>

#include "tessinterface.h"
#include "netinterface.h"
#include "IRoadWorkZone.h"
#include "IReconstruction.h"
#include "UnitChange.h"

MyNet::MyNet(){
}

//加载完路网后的行为
void MyNet::afterLoadNet() {
    // 创建施工区
    Online::DynaRoadWorkZoneParam param;
    param.name = "施工区";                // 施工区名称
    param.roadId = 1;                      // 施工区所在路段ID
    param.location = m2p(2500);            // 施工区开始位置（像素）
    param.length = m2p(50);                // 施工区长度（像素）
    param.upCautionLength = m2p(70);       // 上游警示区长度（像素）
    param.upTransitionLength = m2p(60);    // 上游过渡区长度（像素）
    param.upBufferLength = m2p(50);        // 上游缓冲区长度（像素）
    param.downTransitionLength = m2p(40);  // 下游过渡区长度（像素）
    param.downTerminationLength = m2p(30); // 下游终止区长度（像素）
    param.mlFromLaneNumber << 3;           // 施工区占用车道序号（从右到左，从0开始）
    param.startTime = 0;                   // 开始时间（秒）
    param.duration = 400;                  // 持续时间（秒）
    param.limitSpeed = 72;                 // 限速（km/h）

    IRoadWorkZone* rwz = gpTessInterface->netInterface()->createRoadWorkZone(param);
    if (rwz) {
        qDebug() << "施工区创建成功";

        const long rwz_id = rwz->id();
        const qreal location_m = p2m(rwz->location());
        const qreal zoneLength_m = p2m(rwz->zoneLength());
        const qreal upCautionLength_m = p2m(rwz->upCautionLength());
        const qreal upTransitionLength_m = p2m(rwz->upTransitionLength());
        const qreal upBufferLength_m = p2m(rwz->upBufferLength());
        const qreal downTransitionLength_m = p2m(rwz->downTransitionLength());
        const qreal downTerminationLength_m = p2m(rwz->downTerminationLength());
        const long duration_s = rwz->duration();
        const qreal limitSpeed_kmh = rwz->limitSpeed() * 3.6; // 转为km/h

        qDebug() << "施工区信息：";
        qDebug() << "\t施工区ID：" << rwz_id;
        qDebug() << "\t施工区位置：" << location_m << "m";
        qDebug() << "\t施工区长度：" << zoneLength_m << "m";
        qDebug() << "\t施工区上游警示区长度：" << upCautionLength_m << "m";
        qDebug() << "\t施工区上游过渡区长度：" << upTransitionLength_m << "m";
        qDebug() << "\t施工区上游缓冲区长度：" << upBufferLength_m << "m";
        qDebug() << "\t施工区下游过渡区长度：" << downTransitionLength_m << "m";
        qDebug() << "\t施工区下游终止区长度：" << downTerminationLength_m << "m";
        qDebug() << "\t施工区持续时间：" << duration_s << "s";
        qDebug() << "\t施工区限速：" << limitSpeed_kmh << "km/h";

        // 创建借道
        Online::DynaReconstructionParam rparam;
        rparam.roadWorkZoneId = rwz_id;      // 施工区ID
        rparam.beBorrowedLinkId = 2;         // 被借道的路段ID
        rparam.borrowedNum = 1;              // 借用的车道数
        rparam.passagewayLength = 80;        // 保通开口长度（米）
        rparam.passagewayLimitedSpeed = 40.0 / 3.6; // 保通开口限速（m/s）

        IReconstruction* reconstruction = gpTessInterface->netInterface()->createReconstruction(rparam);
        if (reconstruction) {
            qDebug() << "借道创建成功";

            const long reconstruction_id = reconstruction->id();
            const int borrowedNum = reconstruction->borrowedNum();
            const qreal passagewayLength_m = p2m(reconstruction->passagewayLength());
            const qreal passagewayLimitedSpeed_kmh = reconstruction->passagewayLimitedSpeed() * 3.6;

            qDebug() << "借道信息：";
            qDebug() << "\t借道ID：" << reconstruction_id;
            qDebug() << "\t借用的车道数：" << borrowedNum;
            qDebug() << "\t保通开口长度：" << passagewayLength_m << "m";
            qDebug() << "\t保通开口限速：" << passagewayLimitedSpeed_kmh << "km/h";
        }
    }
}

MyNet::~MyNet(){
}