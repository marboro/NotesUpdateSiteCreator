package com.dvelop.smartnotes.domino.updatesitecreator.bundle;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesitecreator.jar.JarLoader;

public class BundleReader {
    private ResourceBundle _bundle = null;

    public BundleReader(final String jarFilePath, final String propFileName) throws Exception {

	try {
	    File file = new File(jarFilePath);
	    URL url = file.toURI().toURL();
	    URL urls[] = {};
	    JarLoader loader = new JarLoader(urls);
	    loader.addURL(url);

	    try {
		_bundle = ResourceBundle.getBundle(propFileName, java.util.Locale.getDefault(), loader);
	    } catch (Exception e) {
		System.out.println(e.getMessage() + " in " + jarFilePath);
	    }

	    if (_bundle == null) {
		try {
		    System.out.println("Trying " + java.util.Locale.ENGLISH);
		    _bundle = ResourceBundle.getBundle(propFileName, java.util.Locale.ENGLISH, loader);
		} catch (Exception e) {
		    System.out.println(e.getMessage() + " in " + jarFilePath);
		}
	    }

	} catch (Exception e) {
	    System.out.println("ERROR:" + e);
	    throw e;
	}
    }

    private String getBundleProperty(final String name) {

	String value = name;

	try {
	    if (_bundle != null) {
		value = _bundle.getString(name);
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	}
	return value;
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
		sProperty = getBundleProperty(sValue.substring(0));
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
