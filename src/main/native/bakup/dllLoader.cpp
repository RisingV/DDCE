#include <windows.h>
#include <iostream.h>
#include <string.h>

//typedef int (*ConnectToServer)(string ipAddr, uint* socketId);
//typedef unsigned int uint
typedef int (__stdcall *ConnectToServer)(std::string, UINT* );

int main(int argc, char *argv[])
{
	UINT sockid = 0;
 	int status = 0;
	HINSTANCE iTesterLibDll = LoadLibrary("F:\\CF-Works\\Dlls\\iTesterLib.dll");
	if ( iTesterLibDll == NULL ) {
    	std::cout << "could not load the dynamic library" << std::endl;
    	return EXIT_FAILURE;
    }
	ConnectToServer cts;
	cts = (ConnectToServer) GetProcAddress(iTesterLibDll, "ConnectToServer");
	if (!cts) {
 		std::cout << "could not locate the function" << std::endl;
    	return EXIT_FAILURE;
	}
 	status = cts("127.0.0.1", &sockid);
	std::cout << "ConnectToServer() returned , status : " << status 
			  << ", sockid : " << sockid
 			  << std::endl;
	return EXIT_SUCCESS;
}
