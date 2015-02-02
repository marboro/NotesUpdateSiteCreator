package com.dvelop.smartnotes.domino.updatesitecreator.common;

public class Strings {

    /*
     * #####################################################
     * 
     * contains some basic string handling code (only).
     * 
     * written by: Thomas Gumz
     * 
     * #####################################################
     */

    public static final String sprintfSEP = "|";

    public static final String CR = "\r";
    public static final String LF = "\n";
    public static final String CRLF = CR + LF;
    public static final String TABCHAR = "\t";

    public static String sprintf1(String sFormat, String sData1) {

	// 1 argument wrapper for sprintf
	return sprintf(sFormat, sData1);

    }

    public static String sprintf2(String sFormat, String sData1, String sData2) {

	// 2 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2);

    }

    public static String sprintf3(String sFormat, String sData1, String sData2, String sData3) {

	// 3 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3);

    }

    public static String sprintf4(String sFormat, String sData1, String sData2, String sData3, String sData4) {

	// 4 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4);

    }

    public static String sprintf5(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5) {

	// 5 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5);

    }

    public static String sprintf6(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5, String sData6) {

	// 6 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5 + sprintfSEP + sData6);

    }

    public static String sprintf10(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5, String sData6, String sData7, String sData8,
	    String sData9, String sData10) {

	// 10 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5 + sprintfSEP + sData6 + sprintfSEP + sData7
		+ sprintfSEP + sData8 + sprintfSEP + sData9 + sprintfSEP + sData10);

    }

    public static String sprintf7(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5, String sData6, String sData7) {

	// 7 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5 + sprintfSEP + sData6 + sprintfSEP + sData7);

    }

    private static String sprintf(String sFormat, String sData) {

	// C-style sprintf() function for string formatting. Currently only %s
	// is implemented

	// hsFormat: the input string, like "In %s2 we trust, but lock your %s1"
	// hsData: the keywords separated by //|//, like "Car|God"
	String result = "";
	try {
	    int count = -1;
	    String[] vData = null;
	    String[] vDataTemp = null;
	    if (sData.contains(sprintfSEP)) {
		vDataTemp = sData.split("\\" + sprintfSEP);
		char[] chars = sData.toCharArray();
		for (char c : chars) {
		    if (sprintfSEP.equals(String.valueOf(c))) {
			count++;
		    }
		}
		vData = new String[count + 2];
		int i;
		for (i = 0; i < vData.length; i++) {
		    vData[i] = "";
		}
		for (i = 0; i < vDataTemp.length; i++) {
		    vData[i] = vDataTemp[i];
		}
	    } else {
		vData = new String[1];
		vData[0] = sData;
	    }

	    result = sFormat.replace(LF, CRLF);

	    for (int iCounter = 0; iCounter < vData.length; iCounter++) {
		result = result.replace("\"%s" + (iCounter + 1) + "\"", "\"" + vData[iCounter] + "\"");
	    }

	} catch (Exception e) {
	    // can//t use the oException object, circular reference
	    // Msgbox "ERROR " & Error$ & " in line " & Erl & " in sprintf(" &
	    // sFormat & ", " & sData & ")"
	}
	return result;

    }

    public static String sprintf8(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5, String sData6, String sData7, String sData8) {

	// 8 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5 + sprintfSEP + sData6 + sprintfSEP + sData7
		+ sprintfSEP + sData8);

    }

    public static String sprintf9(String sFormat, String sData1, String sData2, String sData3, String sData4, String sData5, String sData6, String sData7, String sData8,
	    String sData9) {

	// 9 argument wrapper for sprintf
	return sprintf(sFormat, sData1 + sprintfSEP + sData2 + sprintfSEP + sData3 + sprintfSEP + sData4 + sprintfSEP + sData5 + sprintfSEP + sData6 + sprintfSEP + sData7
		+ sprintfSEP + sData8 + sprintfSEP + sData9);

    }
}
