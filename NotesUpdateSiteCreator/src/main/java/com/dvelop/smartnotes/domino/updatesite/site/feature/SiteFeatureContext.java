package com.dvelop.smartnotes.domino.updatesite.site.feature;

import lotus.domino.View;

import com.dvelop.smartnotes.domino.updatesite.site.feature.factory.SiteFeatureFactory;

public class SiteFeatureContext {
    public View viewFeatures;
    public View viewPlugins;
    public SiteFeatureFactory vParentFactory;
    public String sBaseFolderPath;
    public String sJarFilePath;
    public String sCategory;
    public String sPatch;
    public String sURL;
    public boolean bIsIncluded;
}
