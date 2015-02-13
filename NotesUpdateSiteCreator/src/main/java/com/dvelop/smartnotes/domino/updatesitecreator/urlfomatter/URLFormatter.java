package com.dvelop.smartnotes.domino.updatesitecreator.urlfomatter;

import java.util.logging.Logger;

import lotus.domino.Database;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Common;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class URLFormatter {
    private Logger logger = Logger.getLogger(URLFormatter.class.getName());

    private final String LOCALHOST = "localhost";

    private Database m_db;
    private String m_sHostName;
    private String m_sReplicaID;

    public URLFormatter(String sHostName, Database db) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create URL Fomatter");
	try {
	    m_db = db;

	    m_sHostName = sHostName.trim();
	    m_sReplicaID = m_db.getReplicaID().toLowerCase();

	} catch (Exception e) {

	    OException.raiseError(e, URLFormatter.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}

    }

    public String getNRPCURL() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get NRPCURL");

	try {

	    return Strings.sprintf2(Constants.NRPC_SITE_URL, m_sHostName, m_sReplicaID);

	} catch (Exception e) {

	    OException.raiseError(e, URLFormatter.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";
    }

    public String getHTTPURL() {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get HTTPURL");
	try {

	    String sURL;

	    if (m_sHostName.length() == 0) {
		sURL = Strings.sprintf2(Constants.HTTP_SITE_URL, LOCALHOST, m_sReplicaID);
	    } else {
		sURL = Strings.sprintf2(Constants.HTTP_SITE_URL, m_sHostName, m_sReplicaID);
	    }

	    return Common.encodeURI(sURL);

	} catch (Exception e) {

	    OException.raiseError(e, URLFormatter.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";

    }
}
