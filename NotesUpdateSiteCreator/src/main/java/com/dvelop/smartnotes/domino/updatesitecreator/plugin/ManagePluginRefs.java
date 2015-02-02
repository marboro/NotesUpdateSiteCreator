package com.dvelop.smartnotes.domino.updatesitecreator.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.RichTextNavigator;
import lotus.domino.RichTextRange;
import lotus.domino.RichTextStyle;
import lotus.domino.RichTextTable;
import lotus.domino.Session;
import lotus.domino.View;

import com.dvelop.smartnotes.domino.updatesitecreator.common.Common;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Constants;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Resources;
import com.dvelop.smartnotes.domino.updatesitecreator.common.Strings;
import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.SiteFeature;
import com.dvelop.smartnotes.domino.updatesitecreator.site.feature.plugin.SiteFeaturePlugin;

public class ManagePluginRefs {
    private Session m_session;
    private Database m_db;
    private View m_view;
    private Document m_doc;
    private RichTextItem m_rtItem;
    private SiteFeature m_oFeature;
    private List<PluginContext> m_liCtx = new ArrayList<PluginContext>();
    private int m_lPluginCount;
    private int m_lFragmentCount;

    public ManagePluginRefs(Session session, Database db, Document doc, SiteFeature oFeature, View view) {
	try {
	    m_session = session;
	    m_db = db;
	    m_doc = doc;
	    m_oFeature = oFeature;
	    m_view = view;

	    // delete existing item
	    if (m_doc.hasItem(Constants.ITEM_BODY_PLUGINS)) {
		m_doc.removeItem(Constants.ITEM_BODY_PLUGINS);
	    }

	    // create main richtext item
	    m_rtItem = m_doc.createRichTextItem(Constants.ITEM_BODY_PLUGINS);

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    public void fixup() {
	try {
	    normalizeContext();
	    renderTable(Constants.TABLE_MODE_PLUGINS);
	    renderTable(Constants.TABLE_MODE_FRAGMENTS);
	    storePluginUNIDs();

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void normalizeContext() {
	try {

	    Document doc;
	    PluginContext oPluginCtx;
	    List<SiteFeaturePlugin> liPlugins = new ArrayList<SiteFeaturePlugin>();
	    List<String> vPluginID = new ArrayList<String>();
	    List<String> vPluginVersion = new ArrayList<String>();
	    List<String> vPluginFragment = new ArrayList<String>();
	    String sKey;
	    int lIndex;

	    if (m_oFeature != null) {

		// called from cSiteFeature::Serialize() WITH an oFeature object

		liPlugins.addAll(m_oFeature.getPluginList());
		for (SiteFeaturePlugin plugin : liPlugins) {
		    oPluginCtx = new PluginContext();
		    oPluginCtx.document = plugin.getDocument();
		    oPluginCtx.id = plugin.getID();
		    oPluginCtx.version = plugin.getVersion();
		    oPluginCtx.isFragment = plugin.isFragment();

		    m_liCtx.add(oPluginCtx);

		    if (!plugin.isFragment()) {
			m_lPluginCount = m_lPluginCount + 1;
		    } else {
			m_lFragmentCount = m_lFragmentCount + 1;
		    }

		}

	    } else {

		// called from cImportDatabase::Serialize() WITHOUT an oFeature
		// object

		vPluginID = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_ID);
		vPluginVersion = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_VERSION);
		vPluginFragment = m_doc.getItemValue(Constants.ITEM_FEATURE_PLUGIN_FRAGMENT);

		// refresh the plugins view, otherwise newly serialized plugins
		// won't be found!
		m_view.refresh();

		for (lIndex = 0; lIndex < vPluginID.size(); lIndex++) {

		    sKey = Strings.sprintf2(Constants.FORMAT_ID_VERSION, vPluginID.get(lIndex), vPluginVersion.get(lIndex));
		    doc = m_view.getDocumentByKey(sKey, true);

		    if (doc != null) {

			oPluginCtx = new PluginContext();
			oPluginCtx.document = doc;
			oPluginCtx.id = vPluginID.get(lIndex);
			oPluginCtx.version = vPluginVersion.get(lIndex);
			oPluginCtx.isFragment = Common.xCstr2Bool(vPluginFragment.get(lIndex));

			m_liCtx.add(oPluginCtx);

			if (!oPluginCtx.isFragment) {
			    m_lPluginCount = m_lPluginCount + 1;
			} else {
			    m_lFragmentCount = m_lFragmentCount + 1;
			}

		    }

		}
	    }

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void renderTable(int iMode) {
	try {

	    Document docTable;
	    RichTextItem rtTemp;
	    RichTextStyle rtStyle;
	    RichTextTable rtTable;
	    RichTextRange rtRange;
	    RichTextNavigator rtNav;
	    List<String> liRowItem = new ArrayList<String>();
	    int iRowNumber = 0;

	    final int TABLE_COLUMNS = 11;
	    final String TABLE_TYPE = "%type%";

	    // set initial label style
	    rtStyle = m_session.createRichTextStyle();
	    rtStyle.setFontSize(9);
	    rtStyle.setColor(Resources.COLOR_GRAY);
	    m_rtItem.appendStyle(rtStyle);

	    // 'don't render an empty table if there are no plugins or fragments
	    // for this feature

	    switch (iMode) {

	    case Constants.TABLE_MODE_PLUGINS:
		if ((m_lPluginCount + m_lFragmentCount) == 0) {
		    m_rtItem.appendText(Resources.MSG_NO_PLUGINS_OR_FRAGMENTS);
		    return;
		}

		if (m_lPluginCount == 0) {
		    return;
		} else {
		    m_rtItem.appendText(Resources.MSG_BUNDLED_PLUGINS);
		    m_rtItem.addNewLine(1, false);
		}
		break;

	    case Constants.TABLE_MODE_FRAGMENTS:
		if (m_lFragmentCount == 0) {
		    return;
		} else {
		    m_rtItem.appendText(Resources.MSG_BUNDLED_FRAGMENTS);
		    m_rtItem.addNewLine(1, false);
		}
		break;

	    default:
		break;
	    }

	    // 'list of item value arrays to be rendered as table rows
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_ID));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_VERSION));

	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_DOWNLOADSIZE));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_INSTALLSIZE));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_UNPACK));

	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_OS));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_ARCH));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_WS));
	    liRowItem.add(m_doc.getItemValueString(Constants.ITEM_FEATURE_PLUGIN_NL));

	    // 'create a temporary doc based on the table form
	    docTable = m_db.createDocument();
	    docTable.replaceItemValue(Constants.ITEM_FORM, Constants.FORM_PLUGIN_TABLE);

	    // 'create a temporary rich text item and render the table doc into
	    // it
	    rtTemp = m_doc.createRichTextItem(Constants.ITEM_BODY_TEMP);
	    docTable.renderToRTItem(rtTemp);

	    // 'set proper table header label
	    rtRange = rtTemp.createRange();
	    switch (iMode) {
	    case Constants.TABLE_MODE_PLUGINS:
		rtRange.findandReplace(TABLE_TYPE, Resources.LBL_TABLE_PLUGIN_ID);
		break;
	    case Constants.TABLE_MODE_FRAGMENTS:
		rtRange.findandReplace(TABLE_TYPE, Resources.LBL_TABLE_FRAGMENT_ID);
		break;
	    }

	    // 'get the table
	    rtNav = rtTemp.createNavigator();
	    rtNav.findFirstElement(RichTextItem.RTELEM_TYPE_TABLE);
	    rtTable = (RichTextTable) rtNav.getElement();

	    // 'add required # of rows to the table (there is already 1 empty
	    // row, so -1)
	    switch (iMode) {
	    case Constants.TABLE_MODE_PLUGINS:
		rtTable.addRow(m_lPluginCount - 1);
		break;
	    case Constants.TABLE_MODE_FRAGMENTS:
		rtTable.addRow(m_lFragmentCount - 1);
		break;
	    }

	    // 'position into first empty table cell
	    rtNav.findNextElement(RichTextItem.RTELEM_TYPE_TABLECELL, TABLE_COLUMNS);

	    // 'set style
	    rtStyle.setFontSize(8);
	    rtTemp.beginInsert(rtNav);
	    rtTemp.appendStyle(rtStyle);
	    rtTemp.endInsert();
	    for (PluginContext ctx : m_liCtx) {

		// 'render EITHER plugins OR fragments, depending on calling
		// mode
		switch (iMode) {
		case Constants.TABLE_MODE_PLUGINS:
		    if (ctx.isFragment) {
			continue;
		    }
		    break;
		case Constants.TABLE_MODE_FRAGMENTS:
		    if (!ctx.isFragment) {
			continue;
		    }
		    break;
		}

		// 'number row
		iRowNumber = iRowNumber + 1;
		rtNav.findNextElement(RichTextItem.RTELEM_TYPE_TABLECELL);
		rtStyle.setColor(RichTextStyle.COLOR_GRAY);
		rtTemp.beginInsert(rtNav);
		rtTemp.appendStyle(rtStyle);
		rtTemp.appendText(String.valueOf(iRowNumber));
		rtStyle.setColor(RichTextStyle.COLOR_BLACK);
		rtTemp.appendStyle(rtStyle);
		rtTemp.endInsert();

		// 'doclink row
		rtNav.findNextElement(RichTextItem.RTELEM_TYPE_TABLECELL);
		rtTemp.beginInsert(rtNav);
		rtTemp.appendDocLink(ctx.document, ctx.id);
		rtTemp.endInsert();

		// 'all other rows
		for (String theItem : liRowItem) {
		    rtNav.findNextElement(RichTextItem.RTELEM_TYPE_TABLECELL);
		    rtTemp.beginInsert(rtNav);
		    rtTemp.appendText(theItem);
		    rtTemp.endInsert();
		}
	    }

	    rtTemp.compact();
	    m_rtItem.appendRTItem(rtTemp);
	    m_rtItem.addNewLine(1, false);
	    m_rtItem.compact();
	    rtTemp.remove();

	} catch (Exception e) {

	    OException.raiseError(e, this.getClass().getName(), null);
	}

    }

    private void storePluginUNIDs() {

	try {
	    String sUNIDS = "";
	    final String DELIMITER = "|";

	    for (PluginContext ctx : m_liCtx) {
		sUNIDS = sUNIDS + ctx.document.getUniversalID() + DELIMITER;
	    }

	    // 'store all plugin UNIDs in a multi-value list item
	    Vector<String> vector = new Vector<String>();
	    for (String unid : sUNIDS.split("\\" + DELIMITER)) {
		if (unid != null && !unid.equals("")) {
		    vector.add(unid);
		}
	    }

	    m_doc.replaceItemValue(Constants.ITEM_FEATURE_PLUGIN_UNID, vector);

	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), null);
	}
    }
}
