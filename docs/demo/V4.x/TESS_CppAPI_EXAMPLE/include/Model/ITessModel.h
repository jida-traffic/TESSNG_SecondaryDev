#ifndef __ITessModel__
#define __ITessModel__

#include "tessinterfaces_global.h"

#include <QMetaEnum>

class IModelGui;
class IModelNet;
class IModelSimu;

class TESSINTERFACES_EXPORT ModelTypes : public QObject {
	Q_OBJECT
public:
	enum ModelType
	{
		toolsModel,
		interactionModel,
		tollStation,
		pedestrianModel,
		roadWorkZoneModel,
		emissionsModel,
		junctionModel,
		generalStatisticsModel,
		parkingGarage,
		tess3DModel,
		expandModel
	};
	Q_ENUM(ModelType)

		static QString modelTypeToString(ModelType modelType) {
		QMetaEnum metaEnum = QMetaEnum::fromType<ModelType>();
		return metaEnum.valueToKey(static_cast<int>(modelType));
	}
};

using ModelType = ModelTypes::ModelType;

class TESSINTERFACES_EXPORT ITessModel
{
public:
	ITessModel() {}
	virtual ~ITessModel() {}

	virtual void init(bool isCerted = false);
	virtual ModelType type();
	virtual void setType(ModelType modelType);
	virtual IModelGui* modelGui();
	virtual IModelNet* modelNet();
	virtual IModelSimu* modelSimu();

	//卸载模型时所做工作，可以在此方法从内此移除三个子类的实例
	virtual void unload();

public:
	bool isCerted();

protected:
	bool mbIsCerted = false;

private:
	ModelType mModelType;
};

extern ITessModel TESSINTERFACES_EXPORT* gpPubModel;

#endif