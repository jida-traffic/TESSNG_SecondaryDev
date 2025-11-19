#ifndef __tessinterfaces_global__
#define __tessinterfaces_global__

#include <QtCore/qglobal.h>
#include "TessInterfaces_Words.h"

#ifndef BUILD_STATIC
# if defined(TESSINTERFACES_LIB)
#  define TESSINTERFACES_EXPORT Q_DECL_EXPORT
# else
#  define TESSINTERFACES_EXPORT Q_DECL_IMPORT
# endif
#else
# define TESSINTERFACES_EXPORT
#endif

#endif