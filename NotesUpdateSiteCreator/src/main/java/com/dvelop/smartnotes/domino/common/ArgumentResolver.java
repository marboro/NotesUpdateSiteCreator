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

    private Map<String, String> argsMap = new HashMap<String, String>();
    private Map<String, String> optionMap = new HashMap<String, String>();

    public ArgumentResolver(String[] args) {
	initialize();
	for (int i = 0; i < args.length; i += 2) {
	    if (i + 1 <= args.length && (args[i + 1].startsWith("-") || args[i + 1].startsWith("/"))) {
		argsMap.put(optionMap.get(args[i].substring(1).toUpperCase()), "true");
		i--;
	    } else {
		argsMap.put(optionMap.get(args[i].substring(1).toUpperCase()), args[i + 1]);
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

}