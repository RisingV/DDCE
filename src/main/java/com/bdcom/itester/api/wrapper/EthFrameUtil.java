package com.bdcom.itester.api.wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. <br/>
 * User: francis    <br/>
 * Date: 13-8-8    <br/>
 * Time: 16:44  <br/>
 */
public abstract class EthFrameUtil {

    //private static final String PROTO_TYPE = "0801";
    private static final String PROTO_TYPE = "0800";

    private static final String PRE_CODE = "55555555555555D5";

    private static final Map<Integer, String> MAC = new HashMap<Integer, String>() {
        {
            put( 0*4 + 0, "1827C2C4215C" );
            put( 0*4 + 1, "1A5C3E15EA61" );
            put( 0*4 + 2, "509D0E21356D" );
            put( 0*4 + 3, "8D98BD0D16DD" );

            put( 1*4 + 0, "A0324CCC2712" );
            put( 1*4 + 1, "D3C335D14803" );
            put( 1*4 + 2, "D6319597C429" );
            put( 1*4 + 3, "E3E73CCCEAC2" );

            put( 2*4 + 0, "4A222E3C5803" );
            put( 2*4 + 1, "83E7BB6409AB" );
            put( 2*4 + 2, "568948964D93" );
            put( 2*4 + 3, "A09E84DBEB06" );

            put( 3*4 + 0, "EE0D71B7BA0F" );
            put( 3*4 + 1, "234205A03BC7" );
            put( 3*4 + 2, "650A06D81B61" );
            put( 3*4 + 3, "BBA36B0D4A85" );
        }
    };

    public static String getMacAddr(int cardId, int portId) {
        return MAC.get( cardId * 4 + portId );
    }

    public static int[] getHeader(int srcCardId, int srcPortId, int dstCardId, int dstPortId) {
        String srcMac = getMacAddr( srcCardId, srcPortId );
        String dstMac = getMacAddr( dstCardId, dstPortId );

        StringBuilder sb = new StringBuilder();
        sb.append( PRE_CODE )
          .append( dstMac )
          .append( srcMac )
          .append( PROTO_TYPE );

        return stringToByteArray( sb.toString() );
    }

    public static String getHeaderRawStr( int srcCardId, int srcPortId, int dstCardId, int dstPortId ) {
        String srcMac = getMacAddr( srcCardId, srcPortId );
        String dstMac = getMacAddr( dstCardId, dstPortId );

        StringBuilder sb = new StringBuilder();
        sb.append( PRE_CODE )
                .append( dstMac )
                .append( srcMac )
                .append( PROTO_TYPE );

        return sb.toString();
    }

    private static int[] stringToByteArray(String s) {
        int[] ia = new int[22];

        int len = s.length();
        int strIndex = 0;
        int bsIndex = 0;
        while( strIndex < len ) {
            ia[bsIndex++] = Integer.parseInt( s.substring(strIndex, strIndex + 2 ) , 16 );
            strIndex += 2;
        }

        return ia;
    }

}
