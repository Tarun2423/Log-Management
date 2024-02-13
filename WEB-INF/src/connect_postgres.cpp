
// #include <windows.h>
// #include <cstdio>
// #include <ctime>
// #include <libpq-fe.h> 

// void insertEventLog(PGconn *conn, int eventID, int eventType, const char *timeGenerated, int eventCategory) {
//     char query[512];
//     sprintf(query, "INSERT INTO windowlogs (event_id, event_type, time_generated, event_category) VALUES (%d, %d, '%s', %d)", 
//                     eventID, eventType, timeGenerated, eventCategory);
//     PGresult *res = PQexec(conn, query);
//     if (PQresultStatus(res) != PGRES_COMMAND_OK) {
//         printf("Failed to execute SQL query: %s\n", PQerrorMessage(conn));
//     }
//     PQclear(res);
// }

// void readEventLogAndInsert(PGconn *conn) {
//     HANDLE hEventLog = OpenEventLog(NULL, "Security");
//     if (hEventLog == NULL) {
//         printf("Failed to open Event Log\n");
//         return;
//     }

//     DWORD dwReadFlags = EVENTLOG_SEQUENTIAL_READ | EVENTLOG_FORWARDS_READ;
//     DWORD dwRecordOffset = 0;
//     DWORD nNumberOfBytesToRead = 4096; 
//     DWORD pnBytesRead = 0;
//     DWORD pnMinNumberOfBytesNeeded = 0;

//     BYTE* lpBuffer = (BYTE*)malloc(nNumberOfBytesToRead);
   
//     while (ReadEventLog(hEventLog, dwReadFlags, dwRecordOffset, lpBuffer, nNumberOfBytesToRead, &pnBytesRead, &pnMinNumberOfBytesNeeded)) {
//         BYTE* pCurrentPos = lpBuffer;
//         while (pCurrentPos < lpBuffer + pnBytesRead) {
//             EVENTLOGRECORD* pLogRecord = (EVENTLOGRECORD*)pCurrentPos;
//             time_t rawTime = pLogRecord->TimeGenerated;
//             struct tm* timeInfo = localtime(&rawTime);
//             char timeString[32];
//             strftime(timeString, sizeof(timeString), "%Y-%m-%d %H:%M:%S", timeInfo);
            
//             insertEventLog(conn, pLogRecord->EventID, pLogRecord->EventType, timeString, pLogRecord->EventCategory);
            
//             pCurrentPos += pLogRecord->Length;
//         }
//         dwRecordOffset += pnBytesRead;
//     }

//     CloseEventLog(hEventLog);
//     free(lpBuffer);
// }

// int main() {
//     PGconn *conn = PQconnectdb("dbname=Log user=postgres password=root hostaddr=127.0.0.1 port=5432");
//     if (PQstatus(conn) != CONNECTION_OK) {
//         printf("Failed to connect to database: %s\n", PQerrorMessage(conn));
//         PQfinish(conn);
//         return 1;
//     }

//     readEventLogAndInsert(conn);
//     PQfinish(conn);

//     return 0;
// }
#include <windows.h>
#include <cstdio>
#include <ctime>
#include <libpq-fe.h> 

long g_numLogsProcessed = 0;

void insertEventLog(PGconn *conn, int eventID, int eventType, const char *timeGenerated, int eventCategory) {
    char query[512];
    sprintf(query, "INSERT INTO windowlogs (event_id, event_type, time_generated, event_category) VALUES (%d, %d, '%s', %d)", 
                    eventID, eventType, timeGenerated, eventCategory);
    PGresult *res = PQexec(conn, query);
    if (PQresultStatus(res) != PGRES_COMMAND_OK) {
        printf("Failed to execute SQL query: %s\n", PQerrorMessage(conn));
    }
    PQclear(res);
}

void readEventLogAndInsertNewData(PGconn *conn) {
    HANDLE hEventLog = OpenEventLog(NULL, "Security");
    if (hEventLog == NULL) {
        printf("Failed to open Event Log\n");
        return;
    }

    DWORD dwReadFlags = EVENTLOG_SEQUENTIAL_READ | EVENTLOG_FORWARDS_READ;
    DWORD dwRecordOffset = 0;
    DWORD nNumberOfBytesToRead = 4096; 
    DWORD pnBytesRead = 0;
    DWORD pnMinNumberOfBytesNeeded = 0;

    BYTE* lpBuffer = (BYTE*)malloc(nNumberOfBytesToRead);
   
    while (ReadEventLog(hEventLog, dwReadFlags, dwRecordOffset, lpBuffer, nNumberOfBytesToRead, &pnBytesRead, &pnMinNumberOfBytesNeeded)) {
        BYTE* pCurrentPos = lpBuffer;
        while (pCurrentPos < lpBuffer + pnBytesRead) {
            EVENTLOGRECORD* pLogRecord = (EVENTLOGRECORD*)pCurrentPos;
            
            if ((long)pLogRecord->RecordNumber <= g_numLogsProcessed) {
                pCurrentPos += pLogRecord->Length;
                continue;
            }
            
            time_t rawTime = pLogRecord->TimeGenerated;
            struct tm* timeInfo = localtime(&rawTime);
            char timeString[32];
            strftime(timeString, sizeof(timeString), "%Y-%m-%d %H:%M:%S", timeInfo);
            
            insertEventLog(conn, pLogRecord->EventID, pLogRecord->EventType, timeString, pLogRecord->EventCategory);
            
            pCurrentPos += pLogRecord->Length;
            g_numLogsProcessed = pLogRecord->RecordNumber;
        }
        dwRecordOffset += pnBytesRead;
    }

    CloseEventLog(hEventLog);
    free(lpBuffer);
}

void CALLBACK TimerProc(HWND hwnd, UINT uMsg, UINT_PTR idEvent, DWORD dwTime) {
   
    PGconn *conn = PQconnectdb("dbname=Log user=postgres password=root hostaddr=127.0.0.1 port=5432");
    if (PQstatus(conn) != CONNECTION_OK) {
        printf("Failed to connect to database: %s\n", PQerrorMessage(conn));
        PQfinish(conn);
        return;
    }
    readEventLogAndInsertNewData(conn);
    PQfinish(conn);
}

int main() {

    UINT_PTR timerID = SetTimer(NULL, 0, 10000, TimerProc);
    if (!timerID) {
        printf("Failed to set up timer\n");
        return 1;
    }
    MSG msg;
    while (GetMessage(&msg, NULL, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }


    KillTimer(NULL, timerID);

    return 0;
}
