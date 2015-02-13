package com.dvelop.smartnotes.domino.updatesitecreator.dominodirectory;

import java.util.Vector;
import java.util.logging.Logger;

import lotus.domino.Database;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.ViewEntry;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class AbstractedDominoDirectory {

    private Logger logger = Logger.getLogger(AbstractedDominoDirectory.class.getName());

    private static final String DB_NAMES = "names.nsf";
    private static final String VIEW_SERVERS = "($Servers)";
    private static final int COL_FQHN = 12;

    Session m_session;
    Database m_db;
    Name m_oServer;

    public AbstractedDominoDirectory(Session session, String m_oServer) throws NotesException {
	this(session, session.createName(m_oServer));
    }

    public AbstractedDominoDirectory(Session session, Name m_oServer) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create Abstracted Domino Directory");
	m_session = session;

	try {
	    this.m_oServer = m_oServer;

	    if (m_oServer.getCanonical().length() == 0) {
		getSessionAddressBook();
	    } else {
		getSpecificAddressBook();
	    }

	} catch (NotesException e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    private void getSpecificAddressBook() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get specific Addressbook");
	try {
	    m_db = m_session.getDatabase(m_oServer.getCanonical(), DB_NAMES, false);

	    // fallback
	    if (m_db == null) {
		logger.fine("fallback");
		getSessionAddressBook();
	    }

	} catch (NotesException e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private void getSessionAddressBook() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get session Addressbook");
	try {
	    logger.fine("find public addressbook");
	    for (Object nab : m_session.getAddressBooks()) {
		Database nabDb;
		if (nab instanceof Database) {
		    nabDb = (Database) nab;

		    if (nabDb.isPublicAddressBook()) {
			if (!nabDb.isDirectoryCatalog()) {
			    m_db = nabDb;
			    m_oServer = m_session.createName(nabDb.getServer());
			    m_db.openWithFailover("", "");
			    logger.fine("found public addressbook");
			    break;
			}
		    }
		}
	    }

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);

	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    public String getServerHostName(String sServerName) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get Server Hostname string");
	Name oServerName = null;
	try {
	    oServerName = m_session.createName(sServerName);
	    return getServerHostName(oServerName);
	} catch (NotesException e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";
    }

    public String getServerHostName(Name oServerName) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get Server Hostname");
	try {

	    View view;
	    ViewEntry entry;

	    if (oServerName == null) {
		return "";
	    }

	    logger.fine("get view " + VIEW_SERVERS);
	    view = m_db.getView(VIEW_SERVERS);
	    if (view != null) {
		logger.fine("get entry by key " + oServerName.getCanonical());
		entry = view.getEntryByKey(oServerName.getCanonical(), true);
		if (entry != null) {
		    Vector columnValues = entry.getColumnValues();
		    String result = "" + columnValues.elementAt(COL_FQHN);
		    logger.fine("result: " + result);
		    return result;
		}
	    }

	    return "";

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);

	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";
    }

}
