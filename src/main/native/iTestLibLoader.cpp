#include <windows.h>
#include <time.h>
#include <map>
#include <string>
#include <fstream>
#include <sstream>
#include <jni.h>
#include <win32/jni_md.h>
#define _EXPORTDLL
#include "iTestLibLoader.h"
using namespace std;

//#define _DEBUG

enum ETH_PHY_SPEED { SP_10M, SP_100M, SP_1000M, SP_1G };

typedef int (__stdcall *ConnectToServer)( char*, UINT* );
typedef int (__stdcall *DisconnectToServer)( UINT );
typedef int (__stdcall *GetChassisInfo)( UINT, int*, int*, char** );
typedef int (__stdcall *GetCardInfo)( UINT, int, int*, int*, char** );
typedef int (__stdcall *GetEthernetPhysical)( UINT, int, int, int*, int*, enum ETH_PHY_SPEED*, int*, BOOL* );
typedef int (__stdcall *ClearStatReliably)( UINT, int, int );
typedef int (__stdcall *SetHeader)( UINT, int, int, int, int, byte[] );
typedef int (__stdcall *SetPayload)( UINT, int, int, int, byte[], int );
typedef int (__stdcall *SetDelayCount)( UINT, int, int, UINT );
typedef int (__stdcall *SetTxMode)( UINT, int, int, int, UINT );
typedef int (__stdcall *StartPort)( UINT, int, int );
typedef int (__stdcall *StopPort)( UINT, int, int );
typedef int (__stdcall *GetPortAllStats)( UINT, int, int, int, UINT[] );
typedef int (__stdcall *GetLinkStatus)( UINT, int, int, int* );
typedef int (__stdcall *GetWorkInfo)( UINT, int, int, int* );
typedef int (__stdcall *SetUsedState)( UINT, int, int, int );
typedef int (__stdcall *GetUsedState)( UINT, int, int, int* );
typedef int (__stdcall *SetStreamId)( UINT, int, int, int, int );
typedef int (__stdcall *SetEthernetPhysicalForATT)( UINT, int, int, int, int, int, int );
typedef int (__stdcall *SetFramLengthChange)( UINT, int, int, int );
typedef int (__stdcall *LoadFpga)( UINT, int, int );
typedef int (__stdcall *ResetFpga)( UINT, int );
typedef int (__stdcall *GetStreamSendInfo)( UINT, int, int, int, UINT* );
typedef int (__stdcall *GetStreamRecInfo)( UINT, int, int, int, UINT* );
typedef int (__stdcall *StartCapture)( UINT, int, int );
typedef int (__stdcall *StopCapture)( UINT, int, int, std::string* );
typedef int (__stdcall *SetStreamLength)( UINT, int, int, int, int );

HINSTANCE iTesterLibDll;
ConnectToServer ConnectToServerFunc;
DisconnectToServer DisconnectToServerFunc;
GetChassisInfo GetChassisInfoFunc;
GetCardInfo GetCardInfoFunc;
GetEthernetPhysical GetEthernetPhysicalFunc;
ClearStatReliably ClearStatReliablyFunc;
SetHeader SetHeaderFunc;
SetPayload SetPayloadFunc;
SetDelayCount SetDelayCountFunc;
SetTxMode SetTxModeFunc;
StartPort StartPortFunc;
StopPort StopPortFunc;
GetPortAllStats GetPortAllStatsFunc;
GetLinkStatus GetLinkStatusFunc;
GetWorkInfo GetWorkInfoFunc;
SetUsedState SetUsedStateFunc;
GetUsedState GetUsedStateFunc;
SetStreamId SetStreamIdFunc;
SetEthernetPhysicalForATT SetEthernetPhysicalForATTFunc;
SetFramLengthChange SetFramLengthChangeFunc;
LoadFpga LoadFpgaFunc;
ResetFpga ResetFpgaFunc;
GetStreamSendInfo GetStreamSendInfoFunc;
GetStreamRecInfo GetStreamRecInfoFunc;
StartCapture StartCaptureFunc;
StopCapture StopCaptureFunc;
SetStreamLength SetStreamLengthFunc;

static std::map<const char*, jclass> clz_map;
static std::map<const char*, jmethodID> mid_map;

#ifdef _DEBUG
static char* nowtime;
char* NowTime() {
	char *pszCurrTime = (char*)malloc(sizeof(char)*20);
	memset(pszCurrTime, 0, sizeof(char)*20);

	time_t now;
	time(&now);
	strftime(pszCurrTime, 20, "%Y.%m.%d.%H.%M.%S", localtime(&now));
	
	return pszCurrTime;
}
void Print2File(const char *msg) {
	if ( NULL == nowtime ) {
		nowtime = NowTime();
	}
	
	std::string fileNameStr;
	fileNameStr += "jni_debug.";
	fileNameStr += (std::string) nowtime;
	fileNameStr += (std::string) ".txt";

	std::ofstream outfile( fileNameStr.c_str(), ios::app );
	//outfile.write( msg, strlen(msg) );
	//outfile.write( "\n", 1);
	outfile << msg << std::endl;
	outfile.close();
}
#endif

jclass FindClass(JNIEnv* env, const char *clzName) {
	jclass clz;
	if ( !env ) {
		return NULL;
	}
	clz = clz_map[ clzName ];
	if ( NULL == clz ) {
		clz = env->FindClass( clzName );
		clz = (jclass) env->NewGlobalRef( clz );
		clz_map[ clzName ] = clz;
	} 

	return clz;
}

jmethodID FindMethodID(JNIEnv* env, jclass clz, const char *methodKey, 
		const char *methodName, const char *methodSig) {
	jmethodID mid;
	if ( !env ) {
		return NULL;
	}
	mid = mid_map[ methodKey ];
	if ( NULL != mid ) {
		return mid;
	}
	mid = env->GetMethodID( clz, methodName, methodSig );
	mid_map[ methodKey ] = mid;
	
	return mid;			
}

std::string jstring2str(JNIEnv* env, jstring jstr) {     
    char* rtn = NULL;     
    jclass clsstring = env->FindClass("java/lang/String");     
    jstring strencode = env->NewStringUTF("GB2312");     
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");     
    jbyteArray barr = (jbyteArray)env->CallObjectMethod(jstr, mid, strencode);     
    jsize alen =  env->GetArrayLength(barr);     
    jbyte* ba = env->GetByteArrayElements(barr,JNI_FALSE);     
    if( alen > 0 ) {     
        rtn = (char*)malloc(alen+1);           
        memcpy(rtn,ba,alen);     
        rtn[alen]=0;     
    }     
    env->ReleaseByteArrayElements(barr,ba,0);     
    std::string stemp(rtn);  
    free(rtn);  
    return stemp;     
}     

jstring str2jstring(JNIEnv* env, std::string str)  
{  
	const char* pat = str.c_str();
    jclass strClass = env->FindClass("Ljava/lang/String;");  
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");  
    jbyteArray bytes = env->NewByteArray(strlen(pat));   
    env->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);  
    jstring encoding = env->NewStringUTF("GB2312");  
    return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);  
} 

JNIEXPORT void JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_loadITesterDllLib
  (JNIEnv* env, jclass loaderClz, jstring path) {
  	if ( NULL == iTesterLibDll) {
  		std::string dllpath_str = jstring2str(env, path);
  		const CHAR* dllpath = dllpath_str.c_str();
		//iTesterLibDll = LoadLibrary("F:\\CF-Works\\Dlls\\iTesterLib.dll"); 
		iTesterLibDll = LoadLibrary( dllpath );
		
		#ifdef _DEBUG
		if ( NULL != iTesterLibDll ) {
			Print2File("DLL loading success!");
		} else {
			Print2File("DLL loading fail!");
		}
		#endif
  	}
  	if ( !ConnectToServerFunc ) {
	  	ConnectToServerFunc = (ConnectToServer) GetProcAddress(iTesterLibDll, "ConnectToServer");
  	}
  	if ( !DisconnectToServerFunc ) {
	  	DisconnectToServerFunc = (DisconnectToServer) GetProcAddress(iTesterLibDll, "DisconnectToServer");
	  }
  	if ( !GetChassisInfoFunc ) {
	  	GetChassisInfoFunc = (GetChassisInfo) GetProcAddress(iTesterLibDll, "GetChassisInfo");
  	}
  	if ( !GetCardInfoFunc ) {
		GetCardInfoFunc = (GetCardInfo) GetProcAddress(iTesterLibDll, "GetCardInfo");  	
	}
	if ( !GetEthernetPhysicalFunc ) {
		GetEthernetPhysicalFunc = (GetEthernetPhysical) GetProcAddress(iTesterLibDll, "GetEthernetPhysical");
	}
	if ( !ClearStatReliablyFunc ) {
		ClearStatReliablyFunc = (ClearStatReliably) GetProcAddress(iTesterLibDll, "ClearStatReliably");
	}
	if ( !SetHeaderFunc ) {
		SetHeaderFunc = (SetHeader) GetProcAddress(iTesterLibDll, "SetHeader");
	}
	if ( !SetPayloadFunc ) {
		SetPayloadFunc = (SetPayload) GetProcAddress(iTesterLibDll, "SetPayload");
	}
	if ( !SetDelayCountFunc ) {
		SetDelayCountFunc = (SetDelayCount) GetProcAddress(iTesterLibDll, "SetDelayCount");
	}
	if ( !SetTxModeFunc ) {
		SetTxModeFunc = (SetTxMode) GetProcAddress(iTesterLibDll, "SetTxMode");
	}
	if ( !StartPortFunc ) {
		StartPortFunc = (StartPort) GetProcAddress(iTesterLibDll, "StartPort");
	}
	if ( !StopPortFunc ) {
		StopPortFunc = (StopPort) GetProcAddress(iTesterLibDll, "StopPort");
	}
	if ( !GetPortAllStatsFunc ) {
		GetPortAllStatsFunc = (GetPortAllStats) GetProcAddress(iTesterLibDll, "GetPortAllStats");
	}
	if ( !GetLinkStatusFunc ) {
		GetLinkStatusFunc = (GetLinkStatus) GetProcAddress(iTesterLibDll, "GetLinkStatus");
	}
	if ( !GetWorkInfoFunc ) {
		GetWorkInfoFunc = (GetWorkInfo) GetProcAddress(iTesterLibDll, "GetWorkInfo");
	}
	if ( !SetUsedStateFunc ) {
		SetUsedStateFunc = (SetUsedState) GetProcAddress(iTesterLibDll, "SetUsedState");
	}
	if ( !GetUsedStateFunc ) {
		GetUsedStateFunc = (GetUsedState) GetProcAddress(iTesterLibDll, "GetUsedState");
	}
	if ( !SetStreamIdFunc ) {
		SetStreamIdFunc = (SetStreamId) GetProcAddress(iTesterLibDll, "SetStreamId");
	}
	if ( !SetEthernetPhysicalForATTFunc ) {
		SetEthernetPhysicalForATTFunc = (SetEthernetPhysicalForATT) GetProcAddress(iTesterLibDll, "SetEthernetPhysicalForATT");
	}
	if ( !SetFramLengthChangeFunc ) {
		SetFramLengthChangeFunc = (SetFramLengthChange) GetProcAddress(iTesterLibDll, "SetFramLengthChange");
	}
	if ( !LoadFpgaFunc ) {
		LoadFpgaFunc = (LoadFpga) GetProcAddress(iTesterLibDll, "LoadFpga");
	}
	if ( !ResetFpgaFunc ) {
		ResetFpgaFunc = (ResetFpga) GetProcAddress(iTesterLibDll, "ResetFpga");
	}
	if ( !GetStreamSendInfoFunc ) {
		GetStreamSendInfoFunc = (GetStreamSendInfo) GetProcAddress(iTesterLibDll, "GetStreamSendInfo");
	}
	if ( !GetStreamRecInfoFunc ) {
		GetStreamRecInfoFunc = (GetStreamRecInfo) GetProcAddress(iTesterLibDll, "GetStreamRecInfo");
	}
	if ( !StartCaptureFunc ) {
		StartCaptureFunc = (StartCapture) GetProcAddress(iTesterLibDll, "StartCapture");
	}
	if ( !StopCaptureFunc ) {
		StopCaptureFunc = (StopCapture) GetProcAddress(iTesterLibDll, "StopCapture");
	}
	if ( !SetStreamLengthFunc ) {
		SetStreamLengthFunc = (SetStreamLength) GetProcAddress(iTesterLibDll, "SetStreamLength");
	}
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_connectToServer
  (JNIEnv* env, jobject loader, jstring ipAddr) {
  	jclass commuStatusClz = FindClass( env, "com/bdcom/itester/lib/CommuStatus" );
  	jobject commuStatusObj = env->AllocObject( commuStatusClz );
  	jmethodID setConnected = FindMethodID( env, commuStatusClz, "commuStatus_setConnected", 
													"setConnected", "(Z)V");
  	jmethodID setSocketId = FindMethodID( env, commuStatusClz, "commuStatus_setSocketId",
	  												"setSocketId", "(I)V");
  	
  	UINT socketId = 0;
  	int status = 1;
  	
  	jint jsocketId = (jint) 0;
  	jboolean connected = (jboolean) 0;
  	std::string ip = jstring2str(env, ipAddr);
  	
  	if ( NULL !=  ConnectToServerFunc ) {
  		char *ip_ptr = new char[ip.length() + 1];
		strcpy(ip_ptr, ip.c_str());
	  	status = ConnectToServerFunc(ip_ptr , &socketId);
	  	delete ip_ptr;
	  	jsocketId = (jint) socketId;
  		#ifdef _DEBUG
  		{
		  stringstream ss;
  		  ss << "ConnectToServerFunc called:"
  		     << "ip: "
  		     << ip
  		     << " socketId: "
  		     << socketId
  		     << " status: "
  		     <<  status;
	      std::string s = ss.str();
		  Print2File( s.c_str() );	
	    }
   		#endif
  	} 
  	#ifdef _DEBUG
  	else {
		Print2File("ConnectToServerFunc is NULL");  	
  	}
  	if ( NULL == setConnected ) {
		Print2File("jMethodID setConnected is NULL");  	
  	}
  	#endif
  	
  	//connected = (jboolean) status;
 	if ( status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
  	
  	env->CallVoidMethod( commuStatusObj, setConnected, connected );
  	env->CallVoidMethod( commuStatusObj, setSocketId, jsocketId );
  	
  	return commuStatusObj;
 }
 
 JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_disconnectToServer
  (JNIEnv * env, jobject loader, jint socketId ) {
  	int status = 1;
  	if ( socketId >=0 && NULL != DisconnectToServerFunc ) {
		status = DisconnectToServerFunc( (UINT) socketId ); 
		#ifdef _DEBUG
  		{
		  stringstream ss;
  		  ss << "DisconnectToServerFunc called:"
  		     << " socketId: "
  		     << socketId
  		     << " status: "
  		     <<  status;
	      std::string s = ss.str();
		  Print2File( s.c_str() );	
	    }
   		#endif
    }
    #ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("DisconnectToServerFunc: socketId < 0");
		}
		if ( NULL == DisconnectToServerFunc ) {
			Print2File("DisconnectToServerFunc is NULL");  	
		}		
  	}
  	#endif
    return (jint) status;
  }

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getChassisInfo
  (JNIEnv * env, jobject loader, jint socketId ) {
  	jclass chassisInfoClz = FindClass( env, "com/bdcom/itester/lib/ChassisInfo" ); 
	jobject chassisInfoObj = env->AllocObject( chassisInfoClz );
	
	jmethodID setChassisType = FindMethodID( env, chassisInfoClz, "chassis_setChassisType",
												"setChassisType", "(I)V");
	jmethodID setCardNum = FindMethodID( env, chassisInfoClz, "chassis_setCardNum",
 												"setCardNum", "(I)V");
	jmethodID setDescription = FindMethodID( env, chassisInfoClz, "chassis_setDescription",
												"setDescription", "(Ljava/lang/String;)V");
	jmethodID setConnected = FindMethodID( env, chassisInfoClz, "chassis_setConnected",
												"setConnected", "(Z)V");

	int chassisType  = 0;
	int cardNum = 0;
	//std::string description;
	char* description;
	int status = 1;
	jboolean connected = (jboolean) 0;
	jstring jdescription;

	if ( socketId >= 0 && NULL != GetChassisInfoFunc ) {
		status = GetChassisInfoFunc( (UINT) socketId, &chassisType, &cardNum, &description ); 
		jdescription = str2jstring( env, (std::string)description );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("GetChassisInfoFunc: socketId < 0");
		}
		if ( NULL == GetChassisInfoFunc ) {
			Print2File("GetChassisInfoFunc is NULL");  	
		}		
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( chassisInfoObj, setChassisType, (jint) chassisType  );
  	env->CallVoidMethod( chassisInfoObj, setCardNum, (jint) cardNum );
  	env->CallVoidMethod( chassisInfoObj, setDescription, jdescription );
  	env->CallVoidMethod( chassisInfoObj, setConnected, connected );

	return chassisInfoObj;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getCardInfo
  (JNIEnv* env, jobject loader, jint socketId, jint cardId) {
  	jclass CardInfoClz = FindClass(env, "com/bdcom/itester/lib/CardInfo");
  	jobject CardInfoObj = env->AllocObject( CardInfoClz );
  	
  	jmethodID setCardId = FindMethodID( env, CardInfoClz, "cardInfo_setCardId",
		 										"setCardId", "(I)V");
	jmethodID setCardType = FindMethodID( env, CardInfoClz, "cardInfo_setCardType",
												"setCardType", "(I)V");
	jmethodID setPortNumber = FindMethodID( env, CardInfoClz, "cardInfo_setPortNumber",
												"setPortNumber", "(I)V");
  	jmethodID setDescription = FindMethodID( env, CardInfoClz, "cardInfo_setDescription",
	  											"setDescription", "(Ljava/lang/String;)V");
  	jmethodID setConnected = FindMethodID( env, CardInfoClz, "cardInfo_setConnected",
		 										"setConnected", "(Z)V");
  		
  	int cardType = 0;
  	int portNum = 0;
  	//std::string description;
  	char* description;
  	int status = 1;
	jboolean connected = (jboolean) 0;
	jstring jdescription;
  	if ( socketId >=0 && NULL != GetCardInfoFunc ) {
  		status = GetCardInfoFunc( (UINT) socketId, (int) cardId, &cardType, &portNum, &description ); 
  		jdescription = str2jstring( env, (std::string)description );
		#ifdef _DEBUG
  		{
		  stringstream ss;
  		  ss << "GetCardInfoFunc called:"
  		     << " socketId: "
  		     << socketId
  		     << " status: "
  		     <<  status;
	      std::string s = ss.str();
		  Print2File( s.c_str() );	
	    }
   		#endif
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("GetCardInfoFunc: socketId < 0");
		}
		if ( NULL == GetCardInfoFunc ) {
			Print2File("GetCardInfoFunc is NULL");  
		}			
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( CardInfoObj, setCardId, cardId );
	env->CallVoidMethod( CardInfoObj, setCardType, (jint) cardType );
	env->CallVoidMethod( CardInfoObj, setPortNumber, (jint) portNum );
	env->CallVoidMethod( CardInfoObj, setDescription, jdescription );
	env->CallVoidMethod( CardInfoObj, setConnected, connected );
  	
  	return CardInfoObj;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getEthernetPhysical
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
	jclass EthPhyProperClz = FindClass(env, "com/bdcom/itester/lib/EthPhyProper");	
  	jobject EthPhyProperObj = env->AllocObject( EthPhyProperClz );
  	
	jmethodID setLinked = FindMethodID( env, EthPhyProperClz, "epp_setLinked", 
												"setLinked", "(Z)V" );
	jmethodID setNego = FindMethodID( env, EthPhyProperClz, "epp_setNego",
												"setNego", "(I)V" );											
  	jmethodID setSpeed = FindMethodID( env, EthPhyProperClz, "epp_setSpeed", 
												"setSpeed", "(I)V");
	jmethodID setFullDuplex = FindMethodID( env, EthPhyProperClz, "epp_setFullDuplex", 
												"setFullDuplex", "(I)V");
  	jmethodID setLoopback = FindMethodID( env, EthPhyProperClz, "epp_setLoopback", 
	  											"setLoopback", "(I)V");
	jmethodID setConnected = FindMethodID( env, EthPhyProperClz, "epp_setConnected",
												"setConnected", "(Z)V");
  	
  	int link = 0;
  	int nego = 0;
  	ETH_PHY_SPEED speed;
  	int duplex = 0;
  	int status = 1;
  	BOOL loopback;
	jboolean connected = (jboolean) 0;
  	if ( socketId >= 0 && NULL != GetEthernetPhysicalFunc ) {
		status = GetEthernetPhysicalFunc( (UINT) socketId, (int) cardId, 
							(int) portId, &link, &nego, &speed, &duplex, &loopback);  	
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "GetEthernetPhysicalFunc: socketId < 0" );
		}
		if ( NULL == GetEthernetPhysicalFunc) {
			Print2File("GetEthernetPhysicalFunc is NULL");  
		}			
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
  	
  	env->CallVoidMethod( EthPhyProperObj, setLinked, (jboolean) link );
  	env->CallVoidMethod( EthPhyProperObj, setNego, (jint) nego );
  	env->CallVoidMethod( EthPhyProperObj, setSpeed, (jint) speed );
  	env->CallVoidMethod( EthPhyProperObj, setFullDuplex, (jint) duplex );
  	env->CallVoidMethod( EthPhyProperObj, setLoopback, (jint) loopback );
  	env->CallVoidMethod( EthPhyProperObj, setConnected, connected );
  	
  	return EthPhyProperObj;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_clearStatReliably
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
  	int status = 1;
  	if ( socketId >= 0 && NULL != ClearStatReliablyFunc ) {
		status = ClearStatReliablyFunc( (UINT) socketId, (int) cardId, (int) portId );  	
  	}
  	#ifdef _DEBUG
  	else {
  		if ( socketId < 0 ) {
		  	Print2File( "ClearStatReliablyFunc: socketId < 0 " );
	  	}
	  	if ( NULL == ClearStatReliablyFunc ) {
	  		Print2File("ClearStatReliablyFunc is NULL");  
	  	}			
  	}
  	#endif
  	
  	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setHeader
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, 
  				jint validStreamCount, jint length, jbyteArray StrHead) {
	int status = 1;	
	jbyte* bytes = env->GetByteArrayElements( StrHead, 0 );
	BYTE* bytearr = (BYTE*) bytes;
 	if ( socketId >= 0 && NULL != SetHeaderFunc ) {
		status = SetHeaderFunc( (UINT) socketId, (int) cardId, (int) portId, 
					(int) validStreamCount, (int) length, bytearr); 	
 	}
 	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetHeaderFunc: socketId < 0 " );
		}
		if ( NULL == SetHeaderFunc ) {
			Print2File("SetHeaderFunc is NULL");  
		}			
  	}
  	#endif
 	
 	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setPayload
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId,
  			 jint length, jbyteArray data, jint type ) {
 	int status = 1;
 	jbyte* bytes = env->GetByteArrayElements( data, 0 );
 	BYTE* bytearr = (BYTE*) bytes;
 	if ( socketId >= 0 && NULL != SetPayloadFunc ) {
		status = SetPayloadFunc( (UINT) socketId, (int) cardId, (int) portId, 
					(int) length, bytearr, (int) type ); 	
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetPayloadFunc: socketId < 0" );	
		}
		if ( NULL == SetPayloadFunc ) {
			Print2File("SetPayloadFunc is NULL");  	
		}		
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setDelayCount
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint delayCount) {
  	int status = 1;
  	if ( socketId >= 0 && NULL != SetDelayCountFunc ) {
		status = SetDelayCountFunc( (UINT) socketId, (int) cardId, (int) portId, (UINT) delayCount );  	
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetDelayCountFunc: socketId < 0" );	
		}
		if ( NULL == SetPayloadFunc ) {
			Print2File("SetDelayCountFunc is NULL");  	
		}		
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setTxMode
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint mode, jint burstNum) {
  int status = 1;
  if ( socketId >= 0 && NULL != SetTxModeFunc ) {
  	status = SetTxModeFunc( (UINT) socketId, (int) cardId, (int) portId, (int) mode, (UINT) burstNum );
  }
  #ifdef _DEBUG
  else {
  	if ( socketId < 0 ) {
	  Print2File( "SetTxModeFunc: socketId < 0" );	
  	}
  	if ( NULL == SetTxModeFunc ) {
	  Print2File("SetTxModeFunc is NULL");  	
	}
  }
  #endif
  
  return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_startPort
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
	int status = 1;
	if ( socketId >= 0 && NULL != StartPortFunc ) {
		status = StartPortFunc( (UINT) socketId, (int) cardId, (int) portId );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "StartPortFunc: socketId < 0" );
		}
		if ( NULL == StartPortFunc ) {
			Print2File("StartPortFunc is NULL");  
		}			
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_stopPort
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
  	int status = 1;
  	if ( socketId >= 0 && NULL != StopPortFunc ) {
		status = StopPortFunc( (UINT) socketId, (int) cardId, (int) portId );	  	
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "StopPortFunc: socketId < 0" );
		}
		if ( NULL == StopPortFunc ) {
			Print2File("StopPortFunc is NULL");  
		}			
  	}
  	#endif	
	
	return (jint) status;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getPortAllStats
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint length ) {
  	jclass PortStatsClz = FindClass( env, "com/bdcom/itester/lib/PortStats" );
  	jobject PortStatsObj = env->AllocObject( PortStatsClz );
  	
  	jmethodID setStats = FindMethodID( env, PortStatsClz, "portStats_setStats", 
	  									"setStats", "([I)V" );
  	jmethodID setConnected = FindMethodID( env, PortStatsClz, "portStats_setConnected",
	  									"setConnected", "(Z)V" );
	int status = 1; 
	UINT stats[length];
	jintArray jstats = env->NewIntArray( (jsize) length );
	jint *elems = env->GetIntArrayElements( jstats, NULL );
	jboolean connected = (jboolean) 0;
  	if ( socketId >= 0 && NULL != GetPortAllStatsFunc ) {
	  	status = GetPortAllStatsFunc( (UINT) socketId, (int) cardId,
		  			 (int) portId, (int) length, stats );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("GetPortAllStatsFunc: socketId < 0");
		}
		if ( NULL == GetPortAllStatsFunc ) {
			Print2File("GetPortAllStatsFunc is NULL");  
		}			
  	}
  	#endif
  	
  	if ( !status ) {
 		for ( int i = 0; i < (int) length; i++ ) {
			elems[i] = (jint) stats[i];  	
 		}
 		connected = JNI_TRUE; 	
 	} else {
		connected = JNI_FALSE;
	}
  
  	env->CallVoidMethod( PortStatsObj, setStats, jstats );
  	env->CallVoidMethod( PortStatsObj, setConnected, connected );
  	
  	return PortStatsObj;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getLinkStatus
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
	jclass linkStatusClz = FindClass( env, "com/bdcom/itester/lib/LinkStatus" );
	jobject linkStatusObj = env->AllocObject( linkStatusClz );
	
	jmethodID setLinked = FindMethodID( env, linkStatusClz, "linkStatus_setLinked",
									 	"setLinked", "(Z)V");
	jmethodID setConnected = FindMethodID( env, linkStatusClz, "linkStatus_setConnected",
										"setConnected", "(Z)V");
										
	int status = 1;
	int linkup = 0;
	jboolean connected = (jboolean) 0;
	if ( socketId >= 0 && NULL != GetLinkStatusFunc ) {
		status = GetLinkStatusFunc( (UINT) socketId, (int) cardId, (int) portId, &linkup);
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0) {
			Print2File( "GetLinkStatusFunc: socketId < 0" );
		}
		if ( NULL == GetLinkStatusFunc ) {
			Print2File("GetLinkStatusFunc is NULL");  
		}			
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( linkStatusObj, setLinked, (jboolean) linkup );
	env->CallVoidMethod( linkStatusObj, setConnected, connected );
	
	return linkStatusObj;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getWorkInfo
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
  	jclass workInfoClz = FindClass( env, "com/bdcom/itester/lib/WorkInfo" );
  	jobject workInfoObj = env->AllocObject( workInfoClz );
  	
  	jmethodID setWorkNow = FindMethodID( env, workInfoClz, "workInfo_setWorkNow", 
	  									"setWorkNow", "(Z)V");
	jmethodID setConnected = FindMethodID( env, workInfoClz, "workInfo_setConnected",
										"setConnected", "(Z)V");
										
	int status = 1;
	int workNow = 0;
	jboolean connected = (jboolean) 0;
	if ( socketId >= 0 && NULL != GetWorkInfoFunc ) {
		status = GetWorkInfoFunc( (UINT) socketId, (int) cardId, (int) portId, &workNow );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "GetWorkInfoFunc: socketId < 0");
		}
		if ( NULL == GetWorkInfoFunc ) {
			Print2File("GetWorkInfoFunc is NULL");  
		}		
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( workInfoObj, setWorkNow, (jboolean) workNow );
	env->CallVoidMethod( workInfoObj, setConnected, connected );
	
	return workInfoObj;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setUsedState
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint iUsedState) {
  	int status = 1;
  	
  	if ( socketId >= 0 && NULL != SetUsedStateFunc ) {
		status = SetUsedStateFunc( (UINT) socketId, (int) cardId, (int) portId, (int) iUsedState ); 	
  	}
  	#ifdef _DEBUG
  	else {
  		if ( socketId < 0 ) {
		  	Print2File( "SetUsedStateFunc: socketId < 0" );
	  	}
	  	if ( NULL == SetUsedStateFunc ) {
	  		Print2File("SetUsedStateFunc is NULL"); 
	  	}	 	
  	}
  	#endif
  	
  	return (jint) status;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getUsedState
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId ) {
  	jclass usedStateClz = FindClass( env, "com/bdcom/itester/lib/UsedState" );
  	jobject usedStateObj = env->AllocObject( usedStateClz );
  	
  	jmethodID setUsed = FindMethodID( env, usedStateClz, "usedState_setUsed", 
	  											"setUsed", "(Z)V");
	jmethodID setConnected = FindMethodID( env, usedStateClz, "usedState_setConnected",
												"setConnected", "(Z)V");
	int status = 1;
	int used = 0;
	jboolean connected = ( jboolean ) 0;
	if ( socketId >= 0 && NULL != GetUsedStateFunc ) {
		status = GetUsedStateFunc( (UINT) socketId, (int) cardId, (int) portId, &used );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "GetUsedStateFunc: socketId < 0" );
		}
		if ( NULL == GetUsedStateFunc ) {
			Print2File("GetUsedStateFunc is NULL");  
		}			
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( usedStateObj, setUsed, (jboolean) used );
	env->CallVoidMethod( usedStateObj, setConnected, connected );
	
	return usedStateObj;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setStreamId
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, 
				jint portId, jint iStartId, jint iIdNum ) {
	int status = 1;	
	if ( socketId >= 0 && NULL != SetStreamIdFunc ) {
		status = SetStreamIdFunc( (UINT) socketId, (int) cardId, (int) portId, 
			(int) iStartId, (int) iIdNum );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetStreamIdFunc: socketId < 0" );
		}
		if ( NULL == SetStreamIdFunc ) {
			Print2File("SetStreamIdFunc is NULL");
		}		  	
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setEthernetPhysicalForATT
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, 
  				jint nego, jint speed, jint fullDuplex, jint loopback) {
	int status = 1;
	if ( socketId >= 0 && NULL != SetEthernetPhysicalForATTFunc ) {
		status = SetEthernetPhysicalForATTFunc( (UINT) socketId, (int) cardId, 
			(int) portId, (int) nego, (int) speed, (int) fullDuplex, (int) loopback );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("SetEthernetPhysicalForATTFunc: socketId < 0");
		}
		if ( NULL == SetEthernetPhysicalForATTFunc ) {
			Print2File("SetEthernetPhysicalForATTFunc is NULL"); 
		}		 	
  	}
  	#endif
	
	return (jint) status;				  	
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setFramLengthChange
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint isChange ) {
	int status = 1;
	if ( socketId >= 0 && NULL != SetFramLengthChangeFunc ) {
		status = SetFramLengthChangeFunc( (UINT) socketId, (int) cardId, (int) portId,
			(int) isChange );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetFramLengthChangeFunc: socketId < 0" );
		}
		if ( NULL == SetFramLengthChangeFunc ) {
			Print2File("SetFramLengthChangeFunc is NULL");  
		}			
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_loadFPGA
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint speed ) {
	int status = 1;
	if ( socketId >= 0 && NULL != LoadFpgaFunc ) {
		status = LoadFpgaFunc( (UINT) socketId, (int) cardId, (int) speed );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "LoadFpgaFunc: socketId < 0" );
		}
		if ( NULL == LoadFpgaFunc ) {
			Print2File("LoadFpgaFunc is NULL");  
		}		
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_resetFPGA
  (JNIEnv* env, jobject loader, jint socketId, jint cardId) {
  	int status = 1;
  	if ( socketId >= 0 && NULL != ResetFpgaFunc ) {
		status = ResetFpgaFunc( (UINT) socketId, (int) cardId );  	
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "ResetFpgaFunc: socketId < 0" );
		}
		if ( NULL == ResetFpgaFunc ) {
			Print2File( "ResetFpgaFunc is NULL" );  	
		}		
  	}
  	#endif
	
	return (jint) status;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getStreamSendInfo
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint streamId) {
	jclass streamInfoClz = FindClass( env, "com/bdcom/itester/lib/StreamInfo" );
	jobject streamInfoObj = env->AllocObject( streamInfoClz );
	
	jmethodID setPacketCount = FindMethodID( env, streamInfoClz, "streamInfo_setPacketCount",
												"setPacketCount", "(J)V");
	jmethodID setConnected = FindMethodID(env, streamInfoClz, "streamInfo_setConnected", 
												"setConnected", "(Z)V");
	
	int status = 1;
	UINT packetCount = 0;
	jboolean connected = ( jboolean ) 0;
	if ( socketId >= 0 && NULL != GetStreamSendInfoFunc ) {
		status = GetStreamSendInfoFunc( (UINT) socketId, (int) cardId, 
						(int) portId, (int) streamId, &packetCount );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "GetStreamSendInfoFunc: socketId < 0" );
		}
		if ( NULL == GetStreamSendInfoFunc ) {
			Print2File("GetStreamSendInfoFunc is NULL");  		
		}
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( streamInfoObj, setPacketCount, (jlong) packetCount );
	env->CallVoidMethod( streamInfoObj, setConnected, connected );
	
	return streamInfoObj;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_getStreamRecInfo
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, jint streamId) {	
	jclass streamInfoClz = FindClass( env, "com/bdcom/itester/lib/StreamInfo" );
	jobject streamInfoObj = env->AllocObject( streamInfoClz );
	
	jmethodID setPacketCount = FindMethodID( env, streamInfoClz, "streamInfo_setPacketCount",
												"setPacketCount", "(J)V");
	jmethodID setConnected = FindMethodID(env, streamInfoClz, "streamInfo_setConnected", 
												"setConnected", "(Z)V");
	
	int status = 1;
	UINT packetCount = 0;
	jboolean connected = ( jboolean ) 0;
	if ( socketId >= 0 && NULL != GetStreamRecInfoFunc ) {
		status = GetStreamRecInfoFunc( (UINT) socketId, (int) cardId, 
						(int) portId, (int) streamId, &packetCount );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "GetStreamRecInfoFunc: socketId < 0" );
		}
		if ( NULL == GetStreamRecInfoFunc ) {
			Print2File("GetStreamRecInfoFunc is NULL");  	
		}			
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( streamInfoObj, setPacketCount, (jlong) packetCount );
	env->CallVoidMethod( streamInfoObj, setConnected, connected );
	
	return streamInfoObj;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_startCapture
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId) {
	int status = 1;
	if ( socketId >= 0 && NULL != StartCaptureFunc ) {
		status = StartCaptureFunc( (UINT) socketId, (int) cardId, (int) portId ); 
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File("StartCaptureFunc: socketId < 0");
		}
		if ( NULL == StartCaptureFunc ) {
			Print2File("StartCaptureFunc is NULL");  	
		}		
  	}
  	#endif
	
	return status;
}

JNIEXPORT jobject JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_stopCapture
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId ) {
	jclass captureResultClz = FindClass( env, "com/bdcom/itester/lib/CaptureResult" );
	jobject captureResultObj = env->AllocObject( captureResultClz );
	
	jmethodID setFrames = FindMethodID( env, captureResultClz, "cpr_setFrames",
									"setFrames", "(Ljava/lang/String;)V");
	jmethodID setConnected = FindMethodID(env, captureResultClz, "streamInfo_setConnected", 
									"setConnected", "(Z)V"); 
	
	int status = 1;
	std::string frames;
	jstring jframes;
	jboolean connected = ( jboolean ) 0; 
	if ( socketId >= 0 && NULL != StopCaptureFunc ) {
		status = StopCaptureFunc( (UINT) socketId, (int) cardId, (int) portId, &frames );
		jframes = str2jstring( env, frames );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "StopCaptureFunc: socketId < 0" );
		}
		if ( NULL == StopCaptureFunc ) {
			Print2File("StopCaptureFunc is NULL");
		}		  	
  	}
  	#endif
	if ( !status ) {
		connected = JNI_TRUE; 	
	} else {
		connected = JNI_FALSE;
	}
	
	env->CallVoidMethod( captureResultObj, setFrames, jframes );
	env->CallVoidMethod( captureResultObj, setConnected, connected );
	
	return captureResultObj;
}

JNIEXPORT jint JNICALL Java_com_bdcom_itester_lib_ITesterLibLoader_setStreamLength
  (JNIEnv* env, jobject loader, jint socketId, jint cardId, jint portId, 
  			jint streamId, jint length ) {
	int status = 1;
	if ( socketId >= 0 && NULL != SetStreamLengthFunc ) {
		status = SetStreamLengthFunc( (UINT) socketId, (int) cardId, (int) portId, 
			(int) streamId, (int) length );
	}
	#ifdef _DEBUG
	else {
		if ( socketId < 0 ) {
			Print2File( "SetStreamLengthFunc: socketId < 0" );
		}
		if ( NULL == SetStreamLengthFunc ) {
			Print2File("SetStreamLengthFunc is NULL");
		}		  	
  	}
  	#endif
	
	return status;
}

/*
BOOL APIENTRY DllMain (HINSTANCE hInst,     // Library instance handle. 
                       DWORD reason,       	//Reason this function is being called
                       LPVOID reserved)		// Not used    
{
	switch (reason)
	{
		case DLL_PROCESS_ATTACH:
		break;

		case DLL_PROCESS_DETACH:
		break;

		case DLL_THREAD_ATTACH:
		break;

		case DLL_THREAD_DETACH:
		break;
	}

	// Returns TRUE on success, FALSE on failure 
	return TRUE;
}

void CallFromDll(char* str)
{
	MessageBox(NULL,str,"iTestLibLoader",MB_OK);
}
*/
