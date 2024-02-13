#include <jni.h>

#ifndef _Included_EventLogReader
#define _Included_EventLogReader
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_EventLogReader_startEventLogReader(JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
