package com.dvelop.smartnotes.domino.widgetcatalog.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class ProxyUtil {
    private static final String IPAddrException = "The specified IP-Address is not valid!";
    private static final String subnetMaskException = "The specified subnet-mask is not valid!";
    private InetAddress ipAddress;
    private InetAddress subnetMask;

    private final String HEADER_DEFAULTS = "[default]";

    // Global Meta-Data
    private final String SOCKET_TIMEOUT = "socket-timeout";
    private final String CONNECTION_TIMEOUT = "connection-timeout";
    private final String RETRIES = "retries";
    private final String MAX_CONNECTIONS_PER_HOST = "max-connections-per-host";
    private final String MAX_TOTAL_CONNECTIONS = "max-total-connections";
    private final String UNSIGNED_SSL_CERTIFICATE_SUPPORT = "unsigned_ssl_certificate_support";
    private final String FORWARD_HTTP_ERRORS = "forward-http-errors";
    private final String PASSTHRU_HOST = "passthru_host";
    private final String PASSTHRU_PORT = "passthru_port";
    private final String XHR_AUTHENTICATION_SUPPORT = "xhr-authentication-support";

    // Regular expressions for header, mime type, cookie, ip address, meta data
    // item fields.
    /*
     * Header field: token = 1*<any CHAR except CTLs or separators> separators =
     * "(" | ")" | "<" | ">" | "@" | "," | ";" | ":" | "\" | <"> | "/" | "[" |
     * "]" | "?" | "=" | "{" | "}" | SP | HT CHAR = <any US-ASCII character
     * (octets 0 - 127)> CTL = <any US-ASCII control character (octets 0 - 31)
     * and DEL(127)> SP = <US-ASCII SP, space (32)> HT = <US-ASCII HT,
     * horizontal-tab(9)>
     */
    private final String headerRegex = "[\\p{ASCII}&&[^\\x00-\\x20\\x7F\\x22\\x28-\\x29\\x2c\\x2f\\x3a-\\x40\\x5b-\\x5d\\x7b\\x7d\\s]]+";

    /*
     * MimeType field: token := 1*<any (US-ASCII) CHAR except SPACE, CTLs, or
     * tspecials> tspecials := "(" / ")" / "<" / ">" / "@" / "," / ";" / ":" /
     * "\" / <"> "/" / "[" / "]" / "?" / "="
     */
    private final String mimeTypeRegex = "[\\p{ASCII}&&[^\\x00-\\x20\\x7F\\x22\\x28-\\x29\\x2c\\x3a-\\x40\\x5b-\\x5d\\s]]+";

    // Cookie field: The regular expression of cookies is similar with header's.
    // The only difference is that cookie field can't allow a wildcard(*)
    private final String cookieRegex = "[\\p{ASCII}&&[^\\x00-\\x20\\x7F\\x22\\x28-\\x2A\\x2c\\x2f\\x3a-\\x40\\x5b-\\x5d\\x7b\\x7d\\s]]+";

    // Meta Data Item field: The regular expression of cookies is similar with
    // header's. The only difference is that meta data item field can allow a
    // space
    private final String metaDataItemRegex = "[\\p{ASCII}&&[^\\x00-\\x1F\\x7F\\x22\\x28-\\x29\\x2c\\x2f\\x3a-\\x40\\x5b-\\x5d\\x7b\\x7d\\s]]+";

    private final String ipRegex = "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    private boolean isEmpty(String value) {
	return (value == null || value.trim().length() == 0) ? true : false;
    }

    private boolean validate(String fieldValues, String regex, boolean isHeader) {
	String[] values;
	if (isEmpty(fieldValues))
	    return true;
	if (fieldValues.contains(",")) {
	    values = fieldValues.split(",");
	} else {
	    values = new String[1];
	    values[0] = fieldValues;
	}

	if (values.length == 0)
	    return false;

	for (int i = 0; i < values.length; i++) {
	    String s = values[i].trim();
	    if (isHeader && s.equals(HEADER_DEFAULTS)) {
		continue;
	    }
	    if (!s.matches(regex))
		return false;
	}
	return true;
    }

    public String encodeURL(String url) {
	try {
	    return URLEncoder.encode(url, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    // e.printStackTrace();
	}
	return url;
    }

    public String decodeURL(String encodedURL) {
	try {
	    return URLDecoder.decode(encodedURL, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    // e.printStackTrace();
	}
	return encodedURL;
    }

    public String[] splitByEqual(String value) {
	String[] subStrings = new String[2];
	int index = value.lastIndexOf("=");
	int length = value.length();
	if (index != -1) {
	    subStrings[0] = value.substring(0, index);
	    subStrings[1] = value.substring(index + 1, length - 1);
	}
	return subStrings;
    }

    public boolean isValidURI(String fieldValue) {
	boolean isValid = false;
	int starIndex = fieldValue.indexOf("*");
	// If the string is not a *, then only the last path
	// element can be a *
	if (starIndex == fieldValue.length() - 1)
	    return true;
	if (starIndex != -1 && starIndex != fieldValue.length() - 1)
	    return false;
	// Query parameters should not be included
	if (fieldValue.indexOf("?") != -1)
	    return false;
	// encode space with code points(Hex)
	fieldValue = fieldValue.replaceAll(" ", "%20");
	try {
	    // use the URI's constructor to validate whether a string is a valid
	    // uri
	    URI uri = new URI(fieldValue);
	    if (uri.isAbsolute()) {
		try {
		    // use the URL's constructor to validate the protocols and
		    URL url = new URL(fieldValue);
		    int port = url.getPort();
		    if (port != -1 && (port < 0 || port > 65535))
			return false;
		} catch (MalformedURLException e) {
		    return false;
		}
	    }
	    return true;
	} catch (URISyntaxException e2) {
	    return false;
	}
    }

    // Returns the content of policy uri as a US-ASCII string.
    public String uriASCIIString(String fieldValue) {
	try {
	    URI value = new URI(fieldValue);
	    return value.toASCIIString();
	} catch (URISyntaxException e) {
	    return fieldValue;
	}
    }

    public boolean isValidActions(String fieldValues) {
	String[] values;
	if (isEmpty(fieldValues))
	    return false;
	int lastCommaIndex = fieldValues.trim().lastIndexOf(",");
	// If the last char is comma, like 'POST , GET, ', return false
	if (lastCommaIndex == fieldValues.trim().length() - 1)
	    return false;
	if (fieldValues.contains(",")) {
	    values = fieldValues.trim().split(",");
	} else {
	    values = new String[1];
	    values[0] = fieldValues;
	}
	if (values.length == 0)
	    return false;
	for (int i = 0; i < values.length; i++) {
	    if (!(values[i].trim().equalsIgnoreCase("GET") || values[i].trim().equalsIgnoreCase("POST") || values[i].trim().equalsIgnoreCase("PUT")
		    || values[i].trim().equalsIgnoreCase("HEAD") || values[i].trim().equalsIgnoreCase("DELETE"))) {
		return false;
	    }
	}
	return true;
    }

    public boolean isValidHeaders(String fieldValues) {
	return validate(fieldValues, headerRegex, true);

    }

    // A wildcard (*) is not allowed -- cookie matching is by exact name
    public boolean isValidCookies(String fieldValues) {
	return validate(fieldValues, cookieRegex, false);
    }

    // token/token, token/*, token* is acceptable
    public boolean isValidMimeTypes(String fieldValues) {
	String[] values;
	if (isEmpty(fieldValues))
	    return true;
	if (fieldValues.contains(",")) {
	    values = fieldValues.split(",");
	} else {
	    values = new String[1];
	    values[0] = fieldValues;
	}

	if (values.length == 0)
	    return false;
	for (int i = 0; i < values.length; i++) {
	    int starIndex = values[i].indexOf("*");
	    int slashIndex = values[i].indexOf("/");
	    int length = values[i].length();
	    if (starIndex == -1 && (slashIndex == -1) || (slashIndex == length - 1))
		return false;
	    if (!values[i].trim().matches(mimeTypeRegex))
		return false;
	}
	return true;
    }

    public boolean isValidIPFilter(String fieldValues) {
	if (isEmpty(fieldValues))
	    return true;
	String values[];
	if (fieldValues.contains(",")) {
	    values = fieldValues.split(",");
	} else {
	    values = new String[1];
	    values[0] = fieldValues;
	}
	if (values.length == 0)
	    return false;
	for (int i = 0; i < values.length; i++) {
	    try {
		parseRule(values[i].trim());
	    } catch (IOException e) {
		// e.printStackTrace();
		return false;
	    }
	}
	return true;
    }

    private void parseRule(String s) throws IOException {
	if (s.indexOf("/") > -1)
	    parseSubnetRule(s);
	else if (s.indexOf("*") > -1) {
	    parseStarRule(s);
	} else {
	    ipAddress = InetAddress.getByName(s);
	    subnetMask = InetAddress.getByAddress(new byte[] { -1, -1, -1, -1 });
	}
    }

    private void parseSubnetRule(String s) throws IOException {
	String as[] = s.split("\\/");
	if (as.length != 2)
	    throw new IOException(IPAddrException);
	String s1 = as[0].trim();
	String s2 = as[1].trim();
	Pattern ipPattern = Pattern.compile(ipRegex);

	if (!ipPattern.matcher(s1).matches())
	    throw new IOException(IPAddrException);
	ipAddress = InetAddress.getByName(s1);
	if (s2.length() > 2) {
	    if (!ipPattern.matcher(s2).matches())
		throw new IOException(subnetMaskException);
	    subnetMask = InetAddress.getByName(s2);
	} else {
	    try {
		byte byte0 = Byte.parseByte(s2);
		if (byte0 < 0 || byte0 > 32)
		    throw new IOException(subnetMaskException);
		byte abyte0[] = new byte[4];
		int i;
		for (i = 0; i < (byte) (byte0 / 8); i++)
		    abyte0[i] = -1;

		for (int j = 1; j <= byte0 % 8; j++)
		    abyte0[i] += (byte) (int) Math.pow(2D, 8 - j);

		subnetMask = InetAddress.getByAddress(abyte0);
	    } catch (NumberFormatException _ex) {
		throw new IOException(subnetMaskException);
	    }
	}
    }

    private void parseStarRule(String s) throws IOException {
	String as[] = s.split("\\.");
	if (as.length != 4)
	    throw new IOException(IPAddrException);
	byte abyte0[] = new byte[4];
	byte abyte1[] = new byte[4];
	boolean flag = false;
	try {
	    for (int j = 0; j < 4; j++)
		if (as[j].equals("*")) {
		    abyte0[j] = 0;
		    abyte1[j] = 0;
		} else {
		    int i = Integer.parseInt(as[j]);
		    if (i < 0 || i > 255)
			throw new IOException(IPAddrException);
		    abyte0[j] = (byte) i;
		    abyte1[j] = -1;
		}

	} catch (NumberFormatException _ex) {
	    throw new IOException(IPAddrException);
	}
	ipAddress = InetAddress.getByAddress(abyte0);
	subnetMask = InetAddress.getByAddress(abyte1);
    }

    public boolean isValidMetaDataField(String fieldName) {
	if (isEmpty(fieldName))
	    return true;
	return fieldName.trim().matches(metaDataItemRegex);
    }

    public boolean isvalidMetaDataValue(String fieldName, String fieldValues) {
	try {
	    fieldName = fieldName.trim();
	    fieldValues = fieldValues.trim();
	    if (isEmpty(fieldName) || isEmpty(fieldValues))
		return true;

	    if (fieldName.equalsIgnoreCase(SOCKET_TIMEOUT) || fieldName.equalsIgnoreCase(CONNECTION_TIMEOUT)) {
		Integer value = new Integer(fieldValues);
		return (value.intValue() == -1 || value.intValue() > 0) ? true : false;
	    }

	    if (fieldName.equalsIgnoreCase(RETRIES) || fieldName.equalsIgnoreCase(MAX_CONNECTIONS_PER_HOST) || fieldName.equalsIgnoreCase(MAX_TOTAL_CONNECTIONS)) {
		Integer value = new Integer(fieldValues);
		return (value.intValue() > 0) ? true : false;
	    }

	    if (fieldName.equalsIgnoreCase(UNSIGNED_SSL_CERTIFICATE_SUPPORT) || fieldName.equalsIgnoreCase(FORWARD_HTTP_ERRORS)
		    || fieldName.equalsIgnoreCase(XHR_AUTHENTICATION_SUPPORT)) {
		return (fieldValues.equalsIgnoreCase("true") || fieldValues.equalsIgnoreCase("false")) ? true : false;
	    }

	    if (fieldName.equalsIgnoreCase(PASSTHRU_HOST)) {
		return isValidIPFilter(fieldValues);
	    }

	    if (fieldName.equalsIgnoreCase(PASSTHRU_PORT)) {
		Integer value = new Integer(fieldValues);
		return (value.intValue() > 0 && value.intValue() < 65536) ? true : false;
	    }
	    return true;
	} catch (Exception ex) {
	    return false;
	}
    }
}
