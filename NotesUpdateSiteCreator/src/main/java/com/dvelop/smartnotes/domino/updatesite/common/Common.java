package com.dvelop.smartnotes.domino.updatesite.common;

import java.io.File;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;

public class Common {
    /*
     * ######################################################
     * 
     * contains common code used throughout the application
     * 
     * written by: Thomas Gumz
     * 
     * ######################################################
     */
    private static Logger logger = Logger.getLogger(Common.class.getName());

    // private Session session;
    //
    // private String gsCaption;
    //
    // public Session getSession() {
    // return session;
    // }
    //
    // public void setSession(Session session) {
    // this.session = session;
    // }
    //
    // public String getGsCaption() {
    // return gsCaption;
    // }
    //
    // public void setGsCaption(String gsCaption) {
    // this.gsCaption = gsCaption;
    // }

    public static String getGsOSPathSep() {
	return File.separator;
    }

    public static String encodeXML(String sText) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("encode xml");

	// escapes reserved XML entities
	logger.fine("escapes reserved XML entities");
	try {

	    String[] aOld = new String[5];
	    String[] aNew = new String[5];

	    aOld[0] = "&";
	    aNew[0] = "&amp;";

	    aOld[1] = "<";
	    aNew[1] = "&lt;";

	    aOld[2] = ">";
	    aNew[2] = "&gt;";

	    aOld[3] = "\"";
	    aNew[3] = "&quot;";

	    aOld[4] = "'";
	    aNew[4] = "&apos;";

	    String result = sText;

	    for (int i = 0; i < aOld.length; i++) {
		result = result.replace(aOld[i], aNew[i]);
	    }
	    logger.fine("result: " + result);
	    return result;

	} catch (Exception e) {

	    OException.raiseError(e, Common.class.getName(), sText);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";
    }

    public static String domGetAttribute(Node domNode, String sAttribute) {
	logger.fine(Resources.LOG_SEPARATOR_END);
	logger.fine("dom get attribute");
	String result = "";
	try {

	    NamedNodeMap domAttribs;
	    int index;
	    int iEntries;

	    domAttribs = domNode.getAttributes();
	    iEntries = domAttribs.getLength();

	    for (index = 0; index < iEntries; index++) {
		logger.fine("Node Name: " + domAttribs.item(index).getNodeName());
		if (domAttribs.item(index).getNodeName().toLowerCase().equals(sAttribute.toLowerCase().trim())) {
		    result = domAttribs.item(index).getNodeValue();
		    logger.fine("Result: " + result);
		    break;
		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, Common.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return result;
    }

    public static String getFormattedText(String sText) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get formatted text");
	// this function returns trimmed text without control chars like CR, LF,
	// TAB, etc.
	String result = "";
	try {
	    sText = sText.replace(Strings.CR, "");
	    sText = sText.replace(Strings.LF, "");
	    sText = sText.replace(Strings.TABCHAR, "");

	    result = sText.trim();
	    logger.fine("Result: " + result);

	} catch (Exception e) {
	    OException.raiseError(e, Common.class.getName(), sText);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return result;

    }

    public static boolean xCstr2Bool(String sArg) {
	// Convenient wrapper around Cbool for string arguments.
	// Cbool raises an error if the argument is an empty string etc.
	// Instead, xCstr2Bool() will simply return FALSE, so that the
	// caller doesn't have to do the error handling.
	return Boolean.parseBoolean(sArg);

    }

    public static String trimAttributes(String sInput) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("trim attributes");
	/*
	 * 'removes empty attributes
	 * 
	 * '<tag att1="foo" att2=""> ' becomes '<tag att1="foo">
	 * 
	 * 'Note: spaces like att=" " or att = "" are NOT handled by this
	 * function, 'it is the callers responsibility to pass in a proper
	 * string!
	 */
	String result = "";
	try {

	    String[] vSplit;
	    String sAttrib;

	    final String TOKEN = "=\"\"";

	    result = sInput;

	    vSplit = result.split(TOKEN);
	    if (vSplit.length > 0) {
		for (String chunk : vSplit) {
		    sAttrib = " " + chunk.substring(chunk.lastIndexOf(" ")) + TOKEN;
		    result = result.replace(sAttrib, "");
		}
	    }
	    logger.fine("Result: " + result);
	} catch (Exception e) {
	    OException.raiseError(e, Common.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return result;
    }

    public static long xClng(String hVariant) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("xClng");

	// ' Convenient wrapper around Clng.
	// ' Clng raises an error if the argument is an empty string etc.
	// ' Instead, xClng() will simply return 0, so that the caller doesn't
	// have
	// ' to do the error handling.

	try {
	    return Long.valueOf(hVariant);
	} catch (NumberFormatException e) {
	    return 0;
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    public static String xCbin(boolean bValue) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("xCbin");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);

	//
	// 'converts a boolean to a binary string "0" (false) or "1" (true)
	//
	// On Error Goto RETURN_ZERO
	//
	// If bValue = True Then
	// xCbin = "1"
	// Else
	// xCbin = "0"
	// End If
	//
	// Exit Function
	//
	// RETURN_ZERO:
	//
	// xCbin = "0"
	// Exit Function
	//
	return null;
    }

    public static List<Object> listToArray(List<Object> hList) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("list to array");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);
	return null;
	//
	// On Error Goto ERROR_HANDLER
	//
	// Dim array() As Variant
	// Dim lIndex As Long
	// Dim bIsValid As Boolean
	//
	// Forall entry In hList
	//
	// Redim Preserve array(lIndex)
	// array(lIndex) = entry
	// lIndex = lIndex + 1
	// bIsValid = True
	//
	// End Forall
	//
	// If bIsValid = True Then
	// ListToArray = array
	// Else
	// ListToArray = Null
	// End If
	//
	// Exit Function
	//
	// ERROR_HANDLER:
	//
	// Call oException.RaiseError(MODULE_NAME, "", Null)
	// Exit Function
	//
    }

    public static Integer xCint(Object hVariant) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("xCint");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);

	// ' Convenient wrapper around Cint.
	// ' Cint raises an error if the argument is an empty string etc.
	// ' Instead, xCint() will simply return 0, so that the caller doesn't
	// have
	// ' to do the error handling.
	//
	//
	// xCint = Cint(Val(Cstr(hVariant)))
	//
	// xCint = 0
	return 0;

    }

    public static String encodeURIComponent(String sURIComponent) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("encode URI component");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);

	// equivalent to Javascript encodeURIComponent()

	// On Error Goto ERROR_HANDLER
	// On Error 4044 Goto EVAL_ALTERNATE

	// Dim sTemp As String
	// Dim vResult As Variant
	//
	// sTemp = Replace(sURIComponent, "\", "\\")
	// vResult = Evaluate(|@URLEncode({UTF-8}; {| + sTemp + |})|)
	// EncodeURIComponent = Cstr(vResult(0))
	//
	// EVAL_ALTERNATE:
	//
	// // fallback in case the { } chars conflict inside the sURIComponent
	// string
	// vResult = Evaluate(|@URLEncode("UTF-8"; "| + sTemp + |")|)
	// Resume Next

	// Call oException.RaiseError(MODULE_NAME, "", sURIComponent)

	return "";
    }

    public static String decodeXML(String sXML) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("decode XML");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);
	//
	// 'un-escapes reserved XML entities
	//
	// On Error Goto ERROR_HANDLER
	//
	// Dim aOld(4) As String
	// Dim aNew(4) As String
	//
	// aOld(0) = "&amp;"
	// aNew(0) = "&"
	//
	// aOld(1) = "&lt;"
	// aNew(1) = "<"
	//
	// aOld(2) = "&gt;"
	// aNew(2) = ">"
	//
	// aOld(3) = |&quot;|
	// aNew(3) = |"|
	//
	// aOld(4) = |&apos;|
	// aNew(4) = |'|
	//
	// DecodeXML = sXML
	// DecodeXML = Replace(DecodeXML, aOld, aNew)
	//
	// Exit Function
	//
	// ERROR_HANDLER:
	//
	// Call oException.RaiseError(MODULE_NAME, "", sXML)
	// Exit Function
	return null;
    }

    public static boolean hasUI() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("has UI");
	logger.fine("not implemented");
	logger.fine(Resources.LOG_SEPARATOR_END);
	//
	// On Error Goto ERROR_HANDLER
	//
	// Dim oUIWorkspace As New NotesUIWorkspace
	//
	// HasUI = True
	//
	// Exit Function
	//
	// ERROR_HANDLER:
	//
	// Err = 0
	// Exit Function
	//
	return false;
    }

    public static String encodeURI(String sURI) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("encode URI");
	// equivalent to Javascript encodeURI()
	try {
	    logger.fine("URI: " + sURI);
	    String sTemp;
	    String[] aEscURIOld = new String[11];
	    String[] aEscURINew = new String[11];
	    String sResult;

	    aEscURIOld[0] = "%3A";
	    aEscURINew[0] = ":";

	    aEscURIOld[1] = "%2F";
	    aEscURINew[1] = "/";

	    aEscURIOld[2] = "%3B";
	    aEscURINew[2] = ";";

	    aEscURIOld[3] = "%3F";
	    aEscURINew[3] = "?";

	    aEscURIOld[4] = "%26";
	    aEscURINew[4] = "&";

	    aEscURIOld[5] = "%3D";
	    aEscURINew[5] = "=";

	    aEscURIOld[6] = "%40";
	    aEscURINew[6] = "@";

	    aEscURIOld[7] = "%23";
	    aEscURINew[7] = "#";

	    aEscURIOld[8] = "%2C";
	    aEscURINew[8] = ",";

	    aEscURIOld[9] = "%2B";
	    aEscURINew[9] = "+";

	    aEscURIOld[10] = "%24";
	    aEscURINew[10] = "$";

	    sTemp = sURI.replaceAll("\\\\", "\\\\\\\\");
	    sResult = URLEncoder.encode(sTemp, "UTF-8");
	    for (int i = 0; i < aEscURINew.length; i++) {
		sResult = sResult.replaceAll(aEscURIOld[i], aEscURINew[i]);
	    }
	    logger.fine("result: " + sResult);
	    return sResult;

	} catch (Exception e) {
	    OException.raiseError(e, Common.class.getName(), sURI);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";
    }
}
