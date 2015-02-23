package com.dvelop.smartnotes.domino.common;

import java.util.HashMap;
import java.util.Map;

public class ArgumentResolver {

    private final String PARAM_SERVER = "S";
    private final String PARAM_UPDATESITENSFFILENAME = "USNFN";
    private final String PARAM_UPDATESITENSFTITLE = "USNT";
    private final String PARAM_UPDATESITETEMPLATEFILENAME = "USTFN";
    private final String PARAM_UPDATESITEPATH = "USP";
    private final String PARAM_WIDGETCATALOGNSFFILENAME = "WCNFN";
    private final String PARAM_WIDGETCATALOGNSFTITLE = "WCNT";
    private final String PARAM_WIDGETCATALOGTEMPLATEFILENAME = "WCTFN";
    private final String PARAM_EXTENSIONXMLPATH = "EXP";
    private final String PARAM_UPDATE = "U";
    private final String PARAM_WIDGETCATALOGCATEGORY = "WCC";
    private final String PARAM_WIDGETCATALOGTYPE = "WCT";
    private final String PARAM_LOGGING = "L";
    private final String PARAM_FILE_LOGGING = "FL";
    private final String PARAM_LOG_FILE_PATH = "LFP";
    private final String PARAM_CONSOLE_LOGGING = "CL";

    private final String SERVER = "server";
    private final String UPDATESITENSFFILENAME = "updateSiteNsfFileName";
    private final String UPDATESITENSFTITLE = "updateSiteNsfTitle";
    private final String UPDATESITETEMPLATEFILENAME = "updateSiteTemplateFileName";
    private final String UPDATESITEPATH = "updateSitePath";
    private final String WIDGETCATALOGNSFFILENAME = "widgetCatalogNsfFileName";
    private final String WIDGETCATALOGNSFTITLE = "widgetCatalogNsfTitle";
    private final String WIDGETCATALOGTEMPLATEFILENAME = "widgetCatalogTemplateFileName";
    private final String EXTENSIONXMLPATH = "extensionXMLPath";
    private final String UPDATE = "update";
    private final String WIDGETCATALOGCATEGORY = "widgetCatalogCategory";
    private final String WIDGETCATALOGTYPE = "widgetCatalogType";
    private final String LOGGING = "logging";
    private final String FILE_LOGGING = "fileLogging";
    private final String CONSOLE_LOGGING = "consoleLogging";
    private final String LOG_FILE_PATH = "logFilePath";

    private Map<String, String> argsMap = new HashMap<String, String>();
    private Map<String, String> optionMap = new HashMap<String, String>();

    public ArgumentResolver(String[] args) {
	initialize();
	for (int i = 0; i < args.length; i += 2) {
	    if ((i + 1 <= args.length - 1)) {
		if (args[i + 1].startsWith("-") || args[i + 1].startsWith("/")) {
		    argsMap.put(optionMap.get(args[i].substring(1).toUpperCase()), "true");
		    i--;
		} else {
		    argsMap.put(optionMap.get(args[i].substring(1).toUpperCase()), args[i + 1]);
		}
	    } else {
		argsMap.put(optionMap.get(args[i].substring(1).toUpperCase()), "true");
	    }
	}

    }

    private void initialize() {
	optionMap.put(PARAM_SERVER, SERVER);
	optionMap.put(PARAM_UPDATESITENSFFILENAME, UPDATESITENSFFILENAME);
	optionMap.put(PARAM_UPDATESITENSFTITLE, UPDATESITENSFTITLE);
	optionMap.put(PARAM_UPDATESITETEMPLATEFILENAME, UPDATESITETEMPLATEFILENAME);
	optionMap.put(PARAM_UPDATESITEPATH, UPDATESITEPATH);
	optionMap.put(PARAM_WIDGETCATALOGNSFFILENAME, WIDGETCATALOGNSFFILENAME);
	optionMap.put(PARAM_WIDGETCATALOGNSFTITLE, WIDGETCATALOGNSFTITLE);
	optionMap.put(PARAM_WIDGETCATALOGTEMPLATEFILENAME, WIDGETCATALOGTEMPLATEFILENAME);
	optionMap.put(PARAM_EXTENSIONXMLPATH, EXTENSIONXMLPATH);
	optionMap.put(PARAM_UPDATE, UPDATE);
	optionMap.put(PARAM_WIDGETCATALOGCATEGORY, WIDGETCATALOGCATEGORY);
	optionMap.put(PARAM_WIDGETCATALOGTYPE, WIDGETCATALOGTYPE);
	optionMap.put(PARAM_LOGGING, LOGGING);
	optionMap.put(PARAM_FILE_LOGGING, FILE_LOGGING);
	optionMap.put(PARAM_CONSOLE_LOGGING, CONSOLE_LOGGING);
	optionMap.put(PARAM_LOG_FILE_PATH, LOG_FILE_PATH);
	argsMap.put(optionMap.get(PARAM_SERVER), "currentServer");
	argsMap.put(optionMap.get(PARAM_UPDATESITENSFFILENAME), "snus.nsf");
	argsMap.put(optionMap.get(PARAM_UPDATESITENSFTITLE), "d.3 smart notes Update Site");
	argsMap.put(optionMap.get(PARAM_UPDATESITETEMPLATEFILENAME), "updatesite.ntf");
	argsMap.put(optionMap.get(PARAM_WIDGETCATALOGNSFFILENAME), "snwidget.nsf");
	argsMap.put(optionMap.get(PARAM_WIDGETCATALOGNSFTITLE), "d.3 smart notes Widget Catalog");
	argsMap.put(optionMap.get(PARAM_WIDGETCATALOGTEMPLATEFILENAME), "toolbox.ntf");
	argsMap.put(optionMap.get(PARAM_UPDATE), "false");
	argsMap.put(optionMap.get(PARAM_WIDGETCATALOGCATEGORY), "d.3ecm");
	argsMap.put(optionMap.get(PARAM_WIDGETCATALOGTYPE), "T");
	argsMap.put(optionMap.get(PARAM_LOGGING), "false");
	argsMap.put(optionMap.get(PARAM_FILE_LOGGING), "false");
	argsMap.put(optionMap.get(PARAM_CONSOLE_LOGGING), "false");
	argsMap.put(optionMap.get(PARAM_LOG_FILE_PATH), "c:\\temp\\UpdateSiteCreator.log");
    }

    public String getServer() {
	return argsMap.get(SERVER);
    }

    public String getUpdateSiteNsfFileName() {
	return argsMap.get(UPDATESITENSFFILENAME);
    }

    public String getUpdateSiteNsfTitle() {
	return argsMap.get(UPDATESITENSFTITLE);
    }

    public String getUpdateSiteTemplateFileName() {
	return argsMap.get(UPDATESITETEMPLATEFILENAME);
    }

    public String getUpdateSitePath() {
	return argsMap.get(UPDATESITEPATH);
    }

    public String getWidgetCatalogNsfFileName() {
	return argsMap.get(WIDGETCATALOGNSFFILENAME);
    }

    public String getWidgetCatalogNsfTitle() {
	return argsMap.get(WIDGETCATALOGNSFTITLE);
    }

    public String getWidgetCatalogTemplateFileName() {
	return argsMap.get(WIDGETCATALOGTEMPLATEFILENAME);
    }

    public String getExtensionXMLPath() {
	return argsMap.get(EXTENSIONXMLPATH);
    }

    public boolean isUpdate() {
	return Boolean.valueOf(argsMap.get(UPDATE));
    }

    public String getWidgetCategory() {
	return argsMap.get(WIDGETCATALOGCATEGORY);
    }

    public String getWidgetType() {
	return argsMap.get(WIDGETCATALOGTYPE);
    }

    public boolean isLogging() {
	return Boolean.valueOf(argsMap.get(LOGGING));
    }

    public boolean isFileLogging() {
	return Boolean.valueOf(argsMap.get(FILE_LOGGING));
    }

    public boolean isConsoleLogging() {
	return Boolean.valueOf(argsMap.get(CONSOLE_LOGGING));
    }

    public String getLogFilePath() {
	return argsMap.get(LOG_FILE_PATH);
    }
}