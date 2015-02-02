package com.dvelop.smartnotes.domino.updatesitecreator.dominodirectory;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class AbstractedDominoDirectory {

    private static final String DB_NAMES = "names.nsf";
    private static final String VIEW_SERVERS = "($Servers)";
    private static final int COL_FQHN = 12;

    Session m_session;
    Database m_db;
    Name m_oServer;

    public AbstractedDominoDirectory(Session session, Name m_oServer) {
	m_session = session;

	try {
	    this.m_oServer = m_oServer;

	    if (m_oServer.getCanonical().length() == 0) {
		this.getSessionAddressBook();
	    } else {
		this.getSpecificAddressBook();
	    }

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void getSpecificAddressBook() {
	try {

	    m_db = m_session.getDatabase(m_oServer.getCanonical(), DB_NAMES, false);

	    // fallback
	    if (m_db == null) {
		this.getSessionAddressBook();
	    }

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }

    private void getSessionAddressBook() {
	try {

	    for (Object nab : m_session.getAddressBooks()) {
		Database nabDb;
		if (nab instanceof Database) {
		    nabDb = (Database) nab;

		    if (nabDb.isPublicAddressBook()) {
			if (!nabDb.isDirectoryCatalog()) {
			    m_db = nabDb;
			    m_oServer = m_session.createName(nabDb.getServer());
			    m_db.openWithFailover("", "");
			    break;
			}
		    }
		}
	    }

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);

	}
    }

    public String getServerHostName(Name oServerName) {
	try {

	    View view;
	    ViewEntry entry;

	    if (oServerName == null) {
		return "";
	    }

	    view = m_db.getView(VIEW_SERVERS);
	    if (view != null) {
		entry = view.getEntryByKey(oServerName.getCanonical(), true);
		if (entry != null) {
		    Vector columnValues = entry.getColumnValues();

		    return "" + columnValues.elementAt(COL_FQHN);
		}
	    }

	    return "";

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	    return "";
	}
    }

}
