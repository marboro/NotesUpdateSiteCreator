package com.dvelop.smartnotes.domino.updatesitecreator.bundle;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesitecreator.jar.JarLoader;

public class BundleReader {
    private Logger logger = Logger.getLogger(BundleReader.class.getName());
    private ResourceBundle _bundle = null;

    public BundleReader(final String jarFilePath, final String propFileName) throws Exception {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("create bundle reader");
	try {
	    File file = new File(jarFilePath);
	    URL url = file.toURI().toURL();
	    URL urls[] = {};

	    JarLoader loader = new JarLoader(urls);
	    loader.addURL(url);

	    try {
		_bundle = ResourceBundle.getBundle(propFileName, java.util.Locale.getDefault(), loader);
	    } catch (Exception e) {
		logger.fine(e.getMessage() + " in " + jarFilePath);
		// OException.raiseError(e, BundleReader.class.getName(),
		// jarFilePath + " : " + propFileName);
	    }

	    if (_bundle == null) {
		try {
		    logger.fine("Trying " + java.util.Locale.ENGLISH);
		    _bundle = ResourceBundle.getBundle(propFileName, java.util.Locale.ENGLISH, loader);
		} catch (Exception e) {
		    logger.fine(e.getMessage() + " in " + jarFilePath);
		    // OException.raiseError(e, BundleReader.class.getName(),
		    // jarFilePath + " : " + propFileName);
		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, BundleReader.class.getName(), jarFilePath + " : " + propFileName);
	    throw e;
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
    }

    private String getBundleProperty(final String name) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get bundle property");
	String value = name;

	try {
	    if (_bundle != null) {
		value = _bundle.getString(name);
		logger.fine("value: " + value);
	    }
	} catch (Exception e) {
	    System.out.println(e.getMessage());
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return value;
    }

    public List<String> getProperties(List<String> hVariant) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get properties");
	try {
	    List<String> result = new ArrayList<String>();
	    for (String entry : hVariant) {
		result.add(getProperty(entry));
	    }
	    logger.fine("result: " + result);
	    return result;

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return hVariant;
    }

    public String getProperties(String hVariant) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get properties");
	try {

	    return getProperty(hVariant);

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return hVariant;
    }

    public String getProperty(String vValue) {
	logger.fine(Resources.LOG_SEPARATOR_START);
	logger.fine("get property");
	try {

	    String sValue;
	    String sProperty;

	    sValue = vValue;

	    // 'check if the property is a %placeholder: if not, return the
	    // original value
	    logger.fine("check if the property is a %placeholder: if not, return the original value");
	    if (sValue.startsWith("%")) {
		logger.fine("return: " + vValue);
		return vValue;
	    } else {
		sProperty = getBundleProperty(sValue.substring(0));
		if (sProperty.length() == 0) {
		    logger.fine("return: " + vValue);
		    return vValue;
		} else {
		    logger.fine("return: " + sProperty);
		    return sProperty;
		}
	    }

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), vValue);
	} finally {
	    logger.fine(Resources.LOG_SEPARATOR_END);
	}
	return "";

    }
}
