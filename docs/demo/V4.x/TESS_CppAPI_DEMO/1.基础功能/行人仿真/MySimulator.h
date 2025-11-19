#ifndef __MySimulator__
#define __MySimulator__

#include <QObject>

#include "Plugin/customersimulator.h"

class MySimulator : public QObject, public CustomerSimulator
{
    Q_OBJECT

public:
    MySimulator();
    ~MySimulator();
    //一个批次计算后的处理
    void afterOneStep() override;

private:
    long mLastSimuTime{0};
    qreal mOffset{10};
    struct Range { qreal top; qreal left; qreal right; qreal bottom; };
    QHash<long, Range> mCrossConflict;
};

#endif