package com.dvelop.smartnotes.domino.updatesitecreator.properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class PropertiesReader {
    Properties m_properties = new Properties();

    public void loadString(final String propFileString) throws IOException {

	this.m_properties.load(new ByteArrayInputStream(propFileString.getBytes("8859_1")));
    }

    private String getPropertyFromProperties(final String propName) {

	final String prop = this.m_properties.getProperty(propName);
	return (prop == null) ? "" : prop;
    }

    public List<String> getProperties(List<String> hVariant) {
	try {
	    List<String> result = new ArrayList<String>();
	    for (String entry : hVariant) {
		result.add(getProperty(entry));
	    }

	    return result;

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}
	return hVariant;
    }

    public String getProperties(String hVariant) {

	try {

	    return getProperty(hVariant);

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}
	return hVariant;
    }

    public String getProperty(String vValue) {

	try {

	    String sValue;
	    String sProperty;

	    sValue = vValue;

	    // 'check if the property is a %placeholder: if not, return the
	    // original value
	    if (sValue.startsWith("%")) {
		return vValue;
	    } else {
		sProperty = getPropertyFromProperties(sValue.substring(1));
		if (sProperty.length() == 0) {
		    return vValue;
		} else {
		    return sProperty;
		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), vValue);

	}
	return "";

    }
}
