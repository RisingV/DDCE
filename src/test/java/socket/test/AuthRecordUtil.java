package socket.test;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-22 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class AuthRecordUtil implements CommuniConstants {
	
	public static void main(String[] args) {
		String xx = "username" + SEPARATOR + "password" + SESSION_SPT + "sessionid";
		System.out.println("rawString : " + xx);
		System.out.println("getUserName : " + getUserName(xx) );
		System.out.println("getPassword : " + getPassword(xx) );
		System.out.println("getSessionId : " + getSessionId(xx) );
	}
	
	public static boolean isVaild(String rawString) {
		int l1 = rawString.length();
		int l2 = SEPARATOR.length();
		int l3 = SESSION_SPT.length();
		int i = rawString.indexOf(SEPARATOR);
		int j = rawString.indexOf(SESSION_SPT);
		
		if ( 0 < i && i < (l1-l2-l3-2) &&
			 0 < j && j < (l1-l3-1)
				) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getUserName(String rawString) {
		String usrPwd = null;
		if ( !isBlank( rawString ) ) {
			usrPwd = removeSessionId(rawString);
		}
		if ( !isBlank( usrPwd ) ) {
			int si = usrPwd.indexOf(SEPARATOR);
			return usrPwd.substring(si + SEPARATOR.length());
		} else {
			return null;
		}
	}
	
	public static String getPassword(String rawString) {
		String usrPwd = null;
		if ( !isBlank( rawString ) ) {
			usrPwd = removeSessionId(rawString);
		}
		if ( !isBlank( usrPwd ) ) {
			int si = usrPwd.indexOf(SEPARATOR);
			return usrPwd.substring(0, si);
		} else {
			return null;
		}
	}
	
	public static String getSessionId(String rawString) {
		String usrPwd = null;
		if ( !isBlank( rawString ) ) {
			usrPwd = removeSessionId(rawString);
		}
		if ( !isBlank( usrPwd ) ) {
			return rawString.substring(usrPwd.length() + 
					SESSION_SPT.length(), rawString.length());
		} else {
			return null;
		}
	}
	
	public static String removeSessionId(String rawString) {
		int sptIndex = -1;
		if ( null != rawString ) {
			sptIndex = rawString.indexOf(SESSION_SPT);
		}
		if ( sptIndex >= 0 ) {
			return rawString.substring(0, sptIndex);
		} else {
			return rawString;
		}
	}
	
	private static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
}