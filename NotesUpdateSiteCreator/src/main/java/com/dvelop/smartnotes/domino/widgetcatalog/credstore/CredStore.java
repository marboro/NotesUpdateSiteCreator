package com.dvelop.smartnotes.domino.widgetcatalog.credstore;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.Session;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class CredStore {

    private final static String LIB_W32 = "nnotes.dll";
    private final static String LIB_MAC = "libnotes.dylib";
    private final static String LIB_LINUX = "libnotes.so";

    private final static String PLATFORM_W32 = "Windows/32";
    private final static String PLATFORM_MAC = "Macintosh";
    private final static String PLATFORM_LINUX = "Linux";
    private final static String PLATFORM_UNIX = "UNIX";

    private final static int CS_FCT_GETDBNAME = Integer.parseInt("&h0001");
    private final static int CS_FCT_ENCRYPT = Integer.parseInt("&h0002");
    private final static int CS_FCT_DECRYPT = Integer.parseInt("&h0003");

    private final static String CREDSTORE_APPLICATION = "SE";
    private final static String CREDSTORE_SERVICE = "OAuth";

    private class SEC_MEMOBJ_DESC {
	long mhObj;
	Byte pObj;
	long dwSize;
	long typ;
    }

    public interface NotesLibrary extends Library {
	NotesLibrary INSTANCE = (NotesLibrary) Native.loadLibrary((Platform.isWindows() ? LIB_W32 : Platform.isMac() ? LIB_MAC : LIB_LINUX), NotesLibrary.class);

	int SECCredStoreRemoteRequest(String pServer, String pAppTag, String pServiceTag, long NoteID, int Fct, String pSecret, long SecretSize, String pEncBulkKeyFieldName,
		String pEncDataFieldName, long Flags, long Reserved, String pReserved, SEC_MEMOBJ_DESC retResponse);
    }

    public void encryptField(Document doc, String fieldValue, String encDataFieldName, Database credStore, Session session) {
	/*
	 * Function EncryptField Description: Call C API
	 * SECCredStoreRemoteRequest to encrypt fieldValue in doc, and then
	 * store the encrypted value in EncDataFieldName filed.
	 */
	try {
	    String pServer;
	    int fieldLength;
	    fieldLength = fieldValue.length();

	    SEC_MEMOBJ_DESC retResponse = this.new SEC_MEMOBJ_DESC();
	    long noteID;
	    String idString;
	    idString = "&H" + doc.getNoteID() + "&";
	    noteID = Long.parseLong(idString);

	    Name canonicalName;

	    canonicalName = session.createName(credStore.getServer());
	    pServer = canonicalName.getCanonical();

	    int errorCode;
	    NotesLibrary notesLibrary = NotesLibrary.INSTANCE;
	    errorCode = notesLibrary.SECCredStoreRemoteRequest(pServer, CREDSTORE_APPLICATION, CREDSTORE_SERVICE, noteID, CS_FCT_ENCRYPT, fieldValue, fieldLength,
		    "$EncryptedBulkKey", encDataFieldName, (long) 0, (long) 0, "0", retResponse);

	    noteID = 0;
	} catch (Exception e) {
	    // 'Print "Error : " & Err & " : " & Error$
	}
    }
}
