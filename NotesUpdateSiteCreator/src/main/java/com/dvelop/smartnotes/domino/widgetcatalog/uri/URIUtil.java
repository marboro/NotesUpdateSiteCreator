package com.dvelop.smartnotes.domino.widgetcatalog.uri;

import java.net.URI;

public class URIUtil {
    public String normalizeGadgetURL(String url)
    /*
     * THIS METHOD IS DUPLICATED IN Notes SOURCES (CapabilitiesRegistry.java)
     * AND THE iNotes (f_toolbox.h). ANY CHANGES NEED TO BE DUPLICATED IN THOSE
     * AREAS OF CODE.
     */
    {
	try {
	    // create the URI with a decoded url
	    String encodedurl = url.replaceAll(" ", "%20");
	    URI tempUri = URI.create(encodedurl);
	    tempUri = tempUri.normalize();
	    int port = tempUri.getPort();
	    String scheme = tempUri.getScheme();
	    // build a default port if appropriate
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
	    tempUri = new URI(schemeLC, tempUri.getUserInfo(), hostLC, port, tempUri.getPath(), tempUri.getQuery(), tempUri.getFragment());
	    return tempUri.toString();
	} catch (Throwable e) {
	}
	return url;
    }
}
