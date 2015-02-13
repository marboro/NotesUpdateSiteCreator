package com.dvelop.smartnotes.domino.updatesite.site.feature.factory;

import java.util.HashMap;
import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.updatesite.common.Constants;
import com.dvelop.smartnotes.domino.updatesite.common.Strings;
import com.dvelop.smartnotes.domino.updatesite.event.EventRegistry;
import com.dvelop.smartnotes.domino.updatesite.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesite.site.Site;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeature;
import com.dvelop.smartnotes.domino.updatesite.site.feature.SiteFeatureContext;

public class SiteFeatureFactory {
    private Site m_vSite;
    private Map<String, SiteFeature> m_liFeatures = new HashMap<String, SiteFeature>();
    private Session session;
    private Database db;
    private EventRegistry eventRegistry;

    public SiteFeatureFactory(Session session, Database db, EventRegistry eventRegistry) {
	this.session = session;
	this.db = db;
	this.eventRegistry = eventRegistry;
    }

    public Map<String, SiteFeature> getFeatures() {
	return m_liFeatures;
    }

    public Site getParent() {
	return m_vSite;
    }

    public void setParent(Site m_vSite) {
	this.m_vSite = m_vSite;
    }

    public void factorNewFeature(SiteFeatureContext oCtx) {
	try {

	    SiteFeature oFeature;

	    // 'factor new feature object
	    oFeature = new SiteFeature(session, db, oCtx, eventRegistry);

	    // 'and add it to the collection, if the feature jar could be found
	    // and processed
	    if (!oFeature.isMissing()) {
		addToCollection(oFeature);
	    }

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void addToCollection(SiteFeature oFeature) {
	try {

	    String sListTag;

	    sListTag = Strings.sprintf2(Constants.FORMAT_ID_VERSION, oFeature.getID(), oFeature.getVersion()).toLowerCase();
	    if (!m_liFeatures.containsKey(sListTag)) {
		m_liFeatures.put(sListTag, oFeature);
	    } else {
		// Call oLog.Debug(sprintf1(MSG_IGNORE_DUPLICATE_FEATURE,
		// sListTag))
	    }

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }
}
