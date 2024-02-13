#include <windows.h>
#include <stdio.h>
#include <time.h>
#include <jni.h>

long g_numLogsProcessed = 0;

const char* getEventCategory(int eventCategory) {
    switch(eventCategory) {
        case 13824:
            return "User Account Management";
        case 12544:
            return "Logon";
        case 12548:
            return "Special Logon";
        case 13826:
            return "Security Group Management";
        case 12545:
            return "Logoff";
        case 12292:
            return "Other System Events";
        case 12290:
            return "System Integrity";
        case 12288:
            return "Security State Change";
        case 13568:
            return "Audit Policy Change";
        case 103:
            return "Service Shutdown";
        case 13312:
            return "Process creation";
        case 13573:
            return "Other Policy Change Events";
        default:
            return "Other Events";
    }
}

void processLogRecord(JNIEnv *env, jobject obj, EVENTLOGRECORD* pLogRecord) {
    int eventID = pLogRecord->EventID;
    int eventCategory = pLogRecord->EventCategory;

    const char* eventCategoryStr = getEventCategory(eventCategory);

    time_t rawtime = pLogRecord->TimeGenerated;
    struct tm *timeinfo;
    char buffer[80];
    
    timeinfo = localtime(&rawtime);
    if (timeinfo == NULL) {
        printf("Failed to convert time to local time\n");
        return;
    }
    
    strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeinfo);
    jstring timeGenerated = (*env)->NewStringUTF(env, buffer);
    if (timeGenerated == NULL) {
        printf("Failed to create Java string for TimeGenerated\n");
        return;
    }

    jclass cls = (*env)->GetObjectClass(env, obj);
    jmethodID mid = (*env)->GetMethodID(env, cls, "processLogMessage", "(ILjava/lang/String;Ljava/lang/String;)V");
    if (mid == NULL) {
        printf("Failed to find Java method\n");
        return;
    }

    (*env)->CallVoidMethod(env, obj, mid, eventID, (*env)->NewStringUTF(env, eventCategoryStr), timeGenerated);

    (*env)->DeleteLocalRef(env, timeGenerated);
    
    g_numLogsProcessed++;
}

void readSecurityEventLog(JNIEnv *env, jobject obj) {
    HANDLE hEventLog = OpenEventLog(NULL, "Security");
    if (hEventLog == NULL) {
        printf("Failed to open Security Event Log: %lu\n", GetLastError());
        return;
    }

    DWORD dwReadFlags = EVENTLOG_SEQUENTIAL_READ | EVENTLOG_FORWARDS_READ;
    DWORD dwRecordOffset = g_numLogsProcessed;
    DWORD nNumberOfBytesToRead = 4096;
    DWORD pnBytesRead = 0;
    DWORD pnMinNumberOfBytesNeeded = 0;

    BYTE* lpBuffer = (BYTE*)malloc(nNumberOfBytesToRead);

    if (lpBuffer == NULL) {
        printf("Failed to allocate memory for reading event log\n");
        return;
    }

    while (ReadEventLog(hEventLog, dwReadFlags, dwRecordOffset, lpBuffer, nNumberOfBytesToRead, &pnBytesRead, &pnMinNumberOfBytesNeeded)) {
        BYTE* pCurrentPos = lpBuffer;
        while (pCurrentPos < lpBuffer + pnBytesRead) {
            EVENTLOGRECORD* pLogRecord = (EVENTLOGRECORD*)pCurrentPos;
            processLogRecord(env, obj, pLogRecord);
            pCurrentPos += pLogRecord->Length;
        }
        dwRecordOffset += pnBytesRead;
    }

    CloseEventLog(hEventLog);
    free(lpBuffer);
}

JNIEXPORT void JNICALL Java_EventLogReader_retrieveSecurityLogs(JNIEnv *env, jobject obj) {
    readSecurityEventLog(env, obj);
}

JNIEXPORT jlong JNICALL Java_EventLogReader_getNumLogsProcessed(JNIEnv *env, jobject obj) {
    long numLogsProcessed = g_numLogsProcessed;
    g_numLogsProcessed = 0; 
    return numLogsProcessed;
}
