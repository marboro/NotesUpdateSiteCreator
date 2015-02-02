package com.dvelop.smartnotes.domino.updatesitecreator.common;

import java.io.File;
import java.util.List;

import lotus.domino.Session;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

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

    private Session session;

    private String gsCaption;

    public Session getSession() {
	return session;
    }

    public void setSession(Session session) {
	this.session = session;
    }

    public String getGsCaption() {
	return gsCaption;
    }

    public void setGsCaption(String gsCaption) {
	this.gsCaption = gsCaption;
    }

    public static String getGsOSPathSep() {
	return File.separator;
    }

    public List<Object> ListToArray(List<Object> hList) {
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

    public static String encodeXML(String sText) {

	// escapes reserved XML entities

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
	    return result;

	} catch (Exception e) {

	    OException.raiseError(e, "", sText);
	}
	return "";
    }

    public static Integer xCint(Object hVariant) {

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

    public static String EncodeURIComponent(String sURIComponent) {

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

    public static String domGetAttribute(Node domNode, String sAttribute) {
	String result = "";
	try {

	    NamedNodeMap domAttribs;
	    int index;
	    int iEntries;

	    domAttribs = domNode.getAttributes();
	    iEntries = domAttribs.getLength();

	    for (index = 0; index < iEntries; index++) {
		if (domAttribs.item(index).getNodeName().toLowerCase().equals(sAttribute.toLowerCase().trim())) {
		    result = domAttribs.item(index).getNodeValue();
		    break;
		}
	    }

	} catch (Exception e) {

	    // Call oException.RaiseError(MODULE_NAME, "", Null)
	}
	return result;
    }

    // Public Function DecodeXML(sXML As String) As String
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
    //
    // }
    // Public Function HasUI As Boolean
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
    // }

    public static String getFormattedText(String sText) {
	// this function returns trimmed text without control chars like CR, LF,
	// TAB, etc.
	String result = "";
	try {
	    sText = sText.replace(Strings.CR, "");
	    sText = sText.replace(Strings.LF, "");
	    sText = sText.replace(Strings.TABCHAR, "");

	    result = sText.trim();

	} catch (Exception e) {
	    // Call oException.RaiseError(MODULE_NAME, "", sText)
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

    // Public Function EncodeURI(sURI As String) As String
    //
    // 'equivalent to Javascript encodeURI()
    //
    // On Error Goto ERROR_HANDLER
    //
    // Dim sTemp As String
    // Dim aEscURIOld(10) As String
    // Dim aEscURINew(10) As String
    // Dim vResult As Variant
    //
    // aEscURIOld(0) = "%3A"
    // aEscURINew(0) = ":"
    //
    // aEscURIOld(1) = "%2F"
    // aEscURINew(1) = "/"
    //
    // aEscURIOld(2) = "%3B"
    // aEscURINew(2) = ";"
    //
    // aEscURIOld(3) = "%3F"
    // aEscURINew(3) = "?"
    //
    // aEscURIOld(4) = "%26"
    // aEscURINew(4) = "&"
    //
    // aEscURIOld(5) = "%3D"
    // aEscURINew(5) = "="
    //
    // aEscURIOld(6) = "%40"
    // aEscURINew(6) = "@"
    //
    // aEscURIOld(7) = "%23"
    // aEscURINew(7) = "#"
    //
    // aEscURIOld(8) = "%2C"
    // aEscURINew(8) = ","
    //
    // aEscURIOld(9) = "%2B"
    // aEscURINew(9) = "+"
    //
    // aEscURIOld(10) = "%24"
    // aEscURINew(10) = "$"
    //
    // sTemp = Replace(sURI, "\", "\\")
    // vResult = Evaluate(|@URLEncode("UTF-8"; "| + sTemp + |")|)
    // vResult = Replace(vResult(0), aEscURIOld, aEscURINew)
    // EncodeURI = vResult
    //
    // Exit Function
    //
    // ERROR_HANDLER:
    //
    // Call oException.RaiseError(MODULE_NAME, "", sURI)
    // Exit Function
    //
    // }

    public static String trimAttributes(String sInput) {

	// 'removes empty attributes
	//
	// '<tag att1="foo" att2="">
	// ' becomes
	// '<tag att1="foo">
	//
	// 'Note: spaces like att=" " or att = "" are NOT handled by this
	// function,
	// 'it is the callers responsibility to pass in a proper string!
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
	} catch (Exception e) {
	    // Call oException.RaiseError(MODULE_NAME, "", Null)
	}
	return result;
    }

    public static long xClng(String hVariant) {

	// ' Convenient wrapper around Clng.
	// ' Clng raises an error if the argument is an empty string etc.
	// ' Instead, xClng() will simply return 0, so that the caller doesn't
	// have
	// ' to do the error handling.

	try {
	    return Long.valueOf(hVariant);
	} catch (NumberFormatException e) {
	    return 0;
	}
    }
    // Public Function xCbin(bValue As Boolean) As String
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
    // }
}
