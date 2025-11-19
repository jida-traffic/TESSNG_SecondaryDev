#include <QtWidgets/QApplication>
#include <QTextCodec>
#include <QFileInfo>
#include <QDir>
#include <QLibrary>
#include "MyPlugin.h"
#include "tessinterface.h"

int main(int argc, char *argv[])
{
    char *pAppFilePath = argv[0];
    QTextCodec* pLocalCode = QTextCodec::codecForLocale();
    QTextCodec* pUtf8 = QTextCodec::codecForName("UTF-8");
    QString tmpString = pLocalCode->toUnicode(pAppFilePath);
    QString appFilePath = pUtf8->fromUnicode(tmpString);
    appFilePath = QString(pAppFilePath);
    appFilePath.replace('\\', '/');
    QFileInfo appFileInfo = QFileInfo(appFilePath);
    QDir appDir = appFileInfo.dir();
    QString pluginDir = appDir.path() + "/plugins";
    QCoreApplication::addLibraryPath(pluginDir);

    QFont font = QFont();
    font.setFixedPitch(true);
    font.setPixelSize(13);
    QGuiApplication::setFont(font);

    QApplication a(argc, argv);

    bool result = false;
    QMainWindow *pWindow = tessng();
    if (pWindow)
    {
        MyPlugin *p = new MyPlugin();
        p->init();
        gpTessInterface->loadPluginFromMem(p);
        pWindow->showMaximized();
        result = a.exec();
        pWindow->deleteLater();
    }
    return result;
}