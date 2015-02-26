package com.dvelop.smartnotes.domino.widgetcatalog.uri;

import java.net.URI;
import java.util.logging.Logger;

import com.dvelop.smartnotes.domino.resources.Resources;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;

public class URIUtil {
    private Logger logger = Logger.getLogger(URIUtil.class.getName());

    public String normalizeGadgetURL(String url) {
	/*
	 * THIS METHOD IS DUPLICATED IN Notes SOURCES (CapabilitiesRegistry.java)
	 * AND THE iNotes (f_toolbox.h). ANY CHANGES NEED TO BE DUPLICATED IN THOSE
	 * AREAS OF CODE.
	 */
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("normalize gadget uri");
	try {
	    // create the URI with a decoded url
	    logger.fine("create the URI with a decoded url");
	    String encodedurl = url.replaceAll(" ", "%20");
	    URI tempUri = URI.create(encodedurl);
	    tempUri = tempUri.normalize();
	    int port = tempUri.getPort();
	    String scheme = tempUri.getScheme();
	    // build a default port if appropriate
	    logger.fine("build a default port if appropriate");
	    if (port == -1) {
		if (scheme != null) {
		    if (scheme.equalsIgnoreCase("http")) {
			port = 80;
		    } else if (scheme.equalsIgnoreCase("https")) {
			port = 443;
		    }
		}
	    }
	    String schemeLC = scheme;
	    if (schemeLC != null)
		schemeLC = schemeLC.toLowerCase();
	    String hostLC = tempUri.getHost();
	    if (hostLC != null)
		hostLC = hostLC.toLowerCase();
	    // rebuild the URI with the modification
	    logger.fine("rebuild the URI with the modification");
	    tempUri = new URI(schemeLC, tempUri.getUserInfo(), hostLC, port, tempUri.getPath(), tempUri.getQuery(), tempUri.getFragment());
	    return tempUri.toString();
	} catch (Throwable e) {
	    OException.raiseError((Exception) e, URIUtil.class.getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return url;
    }
}
