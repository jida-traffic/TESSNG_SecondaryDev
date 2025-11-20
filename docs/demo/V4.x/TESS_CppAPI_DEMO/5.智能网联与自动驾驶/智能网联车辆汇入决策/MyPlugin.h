#ifndef __MyPlugin__
#define __MyPlugin__

#include "Plugin/tessplugin.h"

class MyNet;
class MySimulator;

class MyPlugin : public TessPlugin
{
public:
	MyPlugin();
	~MyPlugin();

	void init() override;
	void unload() override;
	CustomerGui *customerGui() override;
	CustomerNet *customerNet() override;
	CustomerSimulator *customerSimulator() override;
private:
	MyNet *mpMyNet;
	MySimulator *mpMySimulator;
};

#endif