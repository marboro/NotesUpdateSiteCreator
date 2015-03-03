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
    private final String PARAM_SETTING_FULLNAME = "SFN";
    private final String PARAM_SETTING_DESCRIPTION = "SD";
    private final String PARAM_POLICY_FULLNAME = "PFN";
    private final String PARAM_POLICY_DESCRIPTION = "PD";
    private final String PARAM_POLICY_CATEGORY = "PC";
    private final String PARAM_URL_HTTP = "HTTP";
    private final String PARAM_CREATE_UPDATE_SITE = "CUS";
    private final String PARAM_CREATE_WIDGET_CATALOG = "CWC";
    private final String PARAM_CREATE_DESKTOP_SETTING = "CDS";
    private final String PARAM_CREATE_POLICY = "CP";
    private final String PARAM_CREATE_GROUP = "CG";
    private final String PARAM_GROUP_NAME = "GN";

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
    private final String SETTING_FULLNAME = "settingFullName";
    private final String SETTING_DESCRIPTION = "settingDescription";
    private final String POLICY_FULLNAME = "policyFullName";
    private final String POLICY_DESCRIPTION = "policyDescription";
    private final String POLICY_CATEGORY = "policyCategory";
    private final String URL_HTTP = "urlType";
    private final String CREATE_UPDATE_SITE = "createUpdateSite";
    private final String CREATE_WIDGET_CATALOG = "createWidgetCatalog";
    private final String CREATE_DESKTOP_SETTING = "createDesktopSetting";
    private final String CREATE_POLICY = "createPolicy";
    private final String CREATE_GROUP = "createGroup";
    private final String GROUP_NAME = "groupName";

    private Map<String, String> argsMap = new HashMap<String, String>();
    private Map<String, String> optionMap = new HashMap<String, String>();

    public ArgumentResolver(String[] args) {
	initialize();
	initializeDefaults();
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
	optionMap.put(PARAM_SETTING_FULLNAME, SETTING_FULLNAME);
	optionMap.put(PARAM_SETTING_DESCRIPTION, SETTING_DESCRIPTION);
	optionMap.put(PARAM_POLICY_FULLNAME, POLICY_FULLNAME);
	optionMap.put(PARAM_POLICY_DESCRIPTION, POLICY_DESCRIPTION);
	optionMap.put(PARAM_POLICY_CATEGORY, POLICY_CATEGORY);
	optionMap.put(PARAM_URL_HTTP, URL_HTTP);
	optionMap.put(PARAM_CREATE_UPDATE_SITE, CREATE_UPDATE_SITE);
	optionMap.put(PARAM_CREATE_WIDGET_CATALOG, CREATE_WIDGET_CATALOG);
	optionMap.put(PARAM_CREATE_DESKTOP_SETTING, CREATE_DESKTOP_SETTING);
	optionMap.put(PARAM_CREATE_POLICY, CREATE_POLICY);
	optionMap.put(PARAM_CREATE_GROUP, CREATE_GROUP);
	optionMap.put(PARAM_GROUP_NAME, GROUP_NAME);
    }

    private void initializeDefaults() {
	argsMap.put(optionMap.get(PARAM_SERVER), "currentServer");
	argsMap.put(optionMap.get(PARAM_UPDATESITENSFFILENAME), "dvelop/d3ecm_updatesite.nsf");
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
	argsMap.put(optionMap.get(PARAM_SETTING_FULLNAME), "d.3 smart notes Sidebar Plugin");
	argsMap.put(optionMap.get(PARAM_SETTING_DESCRIPTION), "Sidebar Plugin for IBM Notes to implement functionalities of d.3 smart suite");
	argsMap.put(optionMap.get(PARAM_POLICY_FULLNAME), "d.3 smart notes Sidebar Plugin distribution");
	argsMap.put(optionMap.get(PARAM_POLICY_DESCRIPTION), "This policy is for the distribution of d.3 smart notes Sidebar Plugin");
	argsMap.put(optionMap.get(PARAM_POLICY_CATEGORY), "d.velop d.3ecm");
	argsMap.put(optionMap.get(PARAM_URL_HTTP), "false");
	argsMap.put(optionMap.get(PARAM_CREATE_UPDATE_SITE), "false");
	argsMap.put(optionMap.get(PARAM_CREATE_WIDGET_CATALOG), "false");
	argsMap.put(optionMap.get(PARAM_CREATE_DESKTOP_SETTING), "false");
	argsMap.put(optionMap.get(PARAM_CREATE_POLICY), "false");
	argsMap.put(optionMap.get(PARAM_CREATE_GROUP), "false");
	argsMap.put(optionMap.get(PARAM_GROUP_NAME), "#d3ecm_widgetuser");
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

    public String getSettingFullName() {
	return argsMap.get(SETTING_FULLNAME);
    }

    public String getSettingDescription() {
	return argsMap.get(SETTING_DESCRIPTION);
    }

    public String getPolicyFullName() {
	return argsMap.get(POLICY_FULLNAME);
    }

    public String getPolicyDescription() {
	return argsMap.get(POLICY_DESCRIPTION);
    }

    public String getPolicyCategory() {
	return argsMap.get(POLICY_CATEGORY);
    }

    public boolean isHttpUrl() {
	return Boolean.valueOf(argsMap.get(URL_HTTP));
    }

    public boolean isCreateUpdateSite() {
	return Boolean.valueOf(argsMap.get(CREATE_UPDATE_SITE));
    }

    public boolean isCreateWidgetCatalog() {
	return Boolean.valueOf(argsMap.get(CREATE_WIDGET_CATALOG));
    }

    public boolean isCreateDesktopSetting() {
	return Boolean.valueOf(argsMap.get(CREATE_DESKTOP_SETTING));
    }

    public boolean isCreatePolicy() {
	return Boolean.valueOf(argsMap.get(CREATE_POLICY));
    }

    public boolean isCreateGroup() {
	return Boolean.valueOf(argsMap.get(CREATE_GROUP));
    }

    public String getGroupName() {
	return argsMap.get(GROUP_NAME);
    }
}