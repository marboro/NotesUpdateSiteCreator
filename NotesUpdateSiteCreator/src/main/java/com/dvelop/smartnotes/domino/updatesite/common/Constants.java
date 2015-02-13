package com.dvelop.smartnotes.domino.updatesite.common;

public class Constants {

    public static final int COUNT_CATEGORIES = 1;
    public static final int COUNT_ARCHIVES = 2;
    public static final int COUNT_FEATURES = 3;
    public static final int COUNT_PLUGINS = 4;

    public static final int EOL_CRLF = 0; // appends a carriage return and line
					  // feed (ASCII 10 + 13).
    public static final int EOL_LF = 1; // appends a line feed (ASCII 10).
    public static final int EOL_CR = 2; // appends a carriage return (ASCII 13).
    public static final int EOL_PLATFORM = 3; // follows the conventions of the
					      // current platform.
    public static final int EOL_NONE = 5; // appends nothing. Default.

    // database picker context
    public static final int DBCTX_SERVER = 0;
    public static final int DBCTX_FILE = 1;
    public static final int DBCTX_TITLE = 2;

    public static final String PREFIX_HTTP = "http://";
    public static final String PREFIX_HTTPS = "https://";
    public static final String XML_COMMENT = "<!-- %s1 -->";
    public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>";
    public static final String XML_CONTENT_TYPE_HEADER = "Content-type: application/xml; charset=utf-8";

    public static final String NRPC_SITE_URL = "nrpc://%s1/__%s2/site.xml";
    public static final String HTTP_SITE_URL = "http://%s1/__%s2.nsf/site.xml";

    public static final String HTTP_URL = "HTTPURL";
    public static final String NRPC_URL = "NRPCURL";

    public static final String TYPENAME_STRING = "STRING";
    public static final String TYPENAME_STRING_LIST = "STRING LIST";

    public static final String QUEUE_PROGRESS_HEADER = "$header";
    public static final String QUEUE_PROGRESS_LABEL = "$label";
    public static final String QUEUE_PROGRESS_BAR = "$bar";
    public static final String QUEUE_PROGRESS_RESET = "$reset";
    public static final String QUEUE_PROGRESS_SET_MIN = "$min";
    public static final String QUEUE_PROGRESS_SET_MAX = "$max";
    public static final String QUEUE_CANCEL_UI = "$cancel";

    public static final String FORM_FEATURE = "fmFeature";

    public static final String ITEM_HTTP_HOST = "HTTP_HOST";
    public static final String ITEM_XML_OUTPUT = "output.xml";
    public static final String ITEM_COMMITTED = "committed";
    public static final String ITEM_TEMP_UNDO = "temp.undo";

    public static final String FILE_FEATURE_XML = "feature.xml";
    public static final String FILE_FEATURE_PROPERTIES = "feature.properties";
    public static final String FILE_FEATURE_BUNDLE = "feature";
    public static final String FILE_PLUGIN_XML = "plugin.xml";
    public static final String FILE_PLUGIN_PROPERTIES = "plugin.properties";
    public static final String FILE_FRAGMENT_XML = "fragment.xml";
    public static final String FILE_FRAGMENT_PROPERTIES = "fragment.properties";
    public static final String FILE_DIGEST_ZIP = "digest.zip";
    public static final String FILE_MANIFEST_MF = "MANIFEST.MF";

    public static final String FOLDER_META_INF = "META-INF";
    public static final String FOLDER_FEATURES = "features";
    public static final String FOLDER_UNIQUE_TEMP = "~updatesite_";

    public static final String INI_PICKER_URL_DEFAULT = "UpdateSiteURLDefault";
    public static final String INI_DEBUG_UPDATESITE = "debug_updatesite";
    public static final String INI_DIRECTORY = "directory";

    public static final String ITEM_FORM = "Form";
    public static final String JAR_EXTENSION = ".jar";
    public static final String PROPERTIES_EXTENSION = ".properties";
    public static final String PLUGIN_URL = "plugins/%s1_%s2.jar";
    public static final String FEATURE_URL = "features/%s1_%s2.jar";
    public static final String FORMAT_ID_VERSION = "%s1_%s2";
    public static final String JAR_ATTACHMENT = "%s1_%s2.jar";

    // view names
    public static final String VIEW_CATEGORIES = "$vwCategories";
    public static final String VIEW_FEATURES = "$vwFeatures";
    public static final String VIEW_PLUGINS = "$vwPlugins";
    public static final String VIEW_FRAGMENTS = "$vwFragments";
    public static final String VIEW_ARCHIVES = "$vwArchives";
    public static final String VIEW_UNDO = "$vwUndo";
    public static final String VIEW_RESOURCES = "$vwResources";

    // XML tags
    public static final String TAG_SITE = "site";
    public static final String TAG_FEATURE = "feature";
    public static final String TAG_DESCRIPTION = "description";
    public static final String TAG_COPYRIGHT = "copyright";
    public static final String TAG_LICENSE = "license";
    public static final String TAG_UPDATE = "update";
    public static final String TAG_URL = "url";
    public static final String TAG_PLUGIN = "plugin";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_CATEGORYDEF = "category-def";
    public static final String TAG_ARCHIVE = "archive";
    public static final String TAG_INSTALL_HANDLER = "install-handler";
    public static final String TAG_DISCOVERY = "discovery";
    public static final String TAG_INCLUDES = "includes";
    public static final String TAG_REQUIRES = "requires";
    public static final String TAG_IMPORT = "import";
    public static final String TAG_DATA = "data";

    // 'XML attributes
    public static final String ATT_TYPE = "type";
    public static final String ATT_URL = "url";
    public static final String ATT_MIRRORSURL = "mirrorsURL";
    public static final String ATT_ID = "id";
    public static final String ATT_VERSION = "version";
    public static final String ATT_NAME = "name";
    public static final String ATT_LABEL = "label";
    public static final String ATT_PATH = "path";
    public static final String ATT_PATCH = "patch";
    public static final String ATT_PROVIDER_NAME = "provider-name";
    public static final String ATT_IMAGE = "image";
    public static final String ATT_DOWNLOAD_SIZE = "download-size";
    public static final String ATT_INSTALL_SIZE = "install-size";
    public static final String ATT_UNPACK = "unpack";
    public static final String ATT_OS = "os";
    public static final String ATT_NL = "nl";
    public static final String ATT_ARCH = "arch";
    public static final String ATT_WS = "ws";
    public static final String ATT_COLO_AFFINITY = "colocation-affinity";
    public static final String ATT_PRIMARY = "primary";
    public static final String ATT_APPLICATION = "application";
    public static final String ATT_PLUGIN = "plugin";
    public static final String ATT_EXCLUSIVE = "exclusive";
    public static final String ATT_LIBRARY = "library";
    public static final String ATT_HANDLER = "handler";
    public static final String ATT_OPTIONAL = "optional";
    public static final String ATT_SEARCH_LOCATION = "search-location";
    public static final String ATT_FEATURE = "feature";
    public static final String ATT_MATCH = "match";
    public static final String ATT_FRAGMENT = "fragment";
    public static final String ATT_CLASS = "class";
    public static final String ATT_PLUGIN_ID = "plugin-id";
    public static final String ATT_PLUGIN_VERSION = "plugin-version";

    // OSGi manifest
    public static final String MF_BUNDLE_NAME = "Bundle-Name";
    public static final String MF_BUNDLE_VENDOR = "Bundle-Vendor";
    public static final String MF_BUNDLE_ACTIVATOR = "Bundle-Activator";
    public static final String MF_BUNDLE_LOCALIZATION = "Bundle-Localization";
    public static final String MF_FRAGMENT_HOST = "Fragment-Host";

    // Feature
    public static final String FORM_PLUGIN_TABLE = "fmPluginTable";

    public static final int TABLE_MODE_PLUGINS = 1;
    public static final int TABLE_MODE_FRAGMENTS = 2;

    public static final String ITEM_BODY_TEMP = "Body.Temp";
    public static final String ITEM_BODY_PLUGINS = "Body.Plugins";
    public static final String ITEM_FEATURE_VIEW_XML_FEATURE = "feature.view.xml.feature";
    public static final String ITEM_FEATURE_VIEW_XML_ARCHIVE = "feature.view.xml.archive";

    // public because they are also used in cSiteDigest
    public static final String TEM_FEATURE_TYPE = "feature.type";
    public static final String ITEM_FEATURE_ID = "feature.id";
    public static final String ITEM_FEATURE_LABEL = "feature.label";
    public static final String ITEM_FEATURE_PROVIDERNAME = "feature.providername";
    public static final String ITEM_FEATURE_DESCRIPTION = "feature.description";
    public static final String ITEM_FEATURE_DESCRIPTION_URL = "feature.description.url";
    public static final String ITEM_FEATURE_COPYRIGHT = "feature.copyright";
    public static final String ITEM_FEATURE_COPYRIGHT_URL = "feature.copyright.url";
    public static final String ITEM_FEATURE_LICENSE = "feature.license";
    public static final String ITEM_FEATURE_LICENSE_URL = "feature.license.url";
    public static final String ITEM_FEATURE_VERSION = "feature.version";
    public static final String ITEM_FEATURE_URL = "feature.url";
    public static final String ITEM_FEATURE_PATCH = "feature.patch";
    public static final String ITEM_FEATURE_OS = "feature.os";
    public static final String ITEM_FEATURE_NL = "feature.nl";
    public static final String ITEM_FEATURE_ARCH = "feature.arch";
    public static final String ITEM_FEATURE_WS = "feature.ws";
    public static final String ITEM_FEATURE_CATEGORY = "feature.category";
    public static final String ITEM_FEATURE_FILE = "feature.file";
    public static final String ITEM_FEATURE_FILE_LASTMODIFIED = "feature.file.lastModified";
    public static final String ITEM_FEATURE_MANIFEST_XML = "feature.manifest.xml";
    public static final String ITEM_FEATURE_IMAGE = "feature.image";
    public static final String ITEM_FEATURE_COLOCATIONAFFINITY = "feature.colocationAffinity";
    public static final String ITEM_FEATURE_PRIMARY = "feature.primary";
    public static final String ITEM_FEATURE_EXCLUSIVE = "feature.exclusive";
    public static final String ITEM_FEATURE_PLUGIN = "feature.plugin";
    public static final String ITEM_FEATURE_APPLICATION = "feature.application";
    public static final String ITEM_FEATURE_INSTALL_HANDLER = "feature.install.handler";
    public static final String ITEM_FEATURE_INSTALL_LIBRARY = "feature.install.library";
    public static final String ITEM_FEATURE_UPDATE_LABEL = "feature.update.label";
    public static final String ITEM_FEATURE_UPDATE_URL = "feature.update.url";
    public static final String ITEM_FEATURE_DISCOVERY_LABEL = "feature.discovery.label";
    public static final String ITEM_FEATURE_DISCOVERY_URL = "feature.discovery.url";
    public static final String ITEM_FEATURE_DISCOVERY_TYPE = "feature.discovery.type";

    public static final String ITEM_FEATURE_INCLUDED = "feature.included";
    public static final String ITEM_FEATURE_INCLUDES_ID = "feature.includes.id";
    public static final String ITEM_FEATURE_INCLUDES_VERSION = "feature.includes.version";
    public static final String ITEM_FEATURE_INCLUDES_NAME = "feature.includes.name";
    public static final String ITEM_FEATURE_INCLUDES_OPTIONAL = "feature.includes.optional";
    public static final String ITEM_FEATURE_INCLUDES_OS = "feature.includes.os";
    public static final String ITEM_FEATURE_INCLUDES_WS = "feature.includes.ws";
    public static final String ITEM_FEATURE_INCLUDES_ARCH = "feature.includes.arch";
    public static final String ITEM_FEATURE_INCLUDES_NL = "feature.includes.nl";
    public static final String ITEM_FEATURE_INCLUDES_SEARCHLOC = "feature.includes.searchlocation";

    public static final String ITEM_FEATURE_IMPORT_PLUGIN = "feature.import.plugin";
    public static final String ITEM_FEATURE_IMPORT_FEATURE = "feature.import.feature";
    public static final String ITEM_FEATURE_IMPORT_VERSION = "feature.import.version";
    public static final String ITEM_FEATURE_IMPORT_MATCH = "feature.import.match";
    public static final String ITEM_FEATURE_IMPORT_PATCH = "feature.import.patch";

    public static final String ITEM_FEATURE_DATA_ID = "feature.data.id";
    public static final String ITEM_FEATURE_DATA_OS = "feature.data.os";
    public static final String ITEM_FEATURE_DATA_ARCH = "feature.data.arch";
    public static final String ITEM_FEATURE_DATA_WS = "feature.data.ws";
    public static final String ITEM_FEATURE_DATA_NL = "feature.data.nl";
    public static final String ITEM_FEATURE_DATA_DOWNLOAD_SIZE = "feature.data.downloadsize";
    public static final String ITEM_FEATURE_DATA_INSTALL_SIZE = "feature.data.installsize";
    public static final String ITEM_FEATURE_DATA_FILENAME = "feature.data.filename";

    public static final String ITEM_FEATURE_TYPE = "feature.type";
    public static final String ITEM_FEATURE_PLUGIN_ID = "feature.plugin.id";
    public static final String ITEM_FEATURE_PLUGIN_VERSION = "feature.plugin.version";
    public static final String ITEM_FEATURE_PLUGIN_FRAGMENT = "feature.plugin.fragment";
    public static final String ITEM_FEATURE_PLUGIN_OS = "feature.plugin.os";
    public static final String ITEM_FEATURE_PLUGIN_ARCH = "feature.plugin.arch";
    public static final String ITEM_FEATURE_PLUGIN_WS = "feature.plugin.ws";
    public static final String ITEM_FEATURE_PLUGIN_NL = "feature.plugin.nl";
    public static final String ITEM_FEATURE_PLUGIN_DOWNLOADSIZE = "feature.plugin.downloadsize";
    public static final String ITEM_FEATURE_PLUGIN_INSTALLSIZE = "feature.plugin.installsize";
    public static final String ITEM_FEATURE_PLUGIN_UNPACK = "feature.plugin.unpack";
    public static final String ITEM_FEATURE_PLUGIN_UNID = "feature.plugin.UNID";

    // FeaturePlugin
    public static final String FORM_PLUGIN = "fmPlugin";

    public static final String ITEM_PLUGIN_ID = "plugin.id";
    public static final String ITEM_PLUGIN_NAME = "plugin.name";
    public static final String ITEM_PLUGIN_VERSION = "plugin.version";
    public static final String ITEM_PLUGIN_PROVIDERNAME = "plugin.providername";
    public static final String ITEM_PLUGIN_CLASS = "plugin.class";
    public static final String ITEM_PLUGIN_FILE = "plugin.file";
    public static final String ITEM_PLUGIN_FILE_LASTMODIFIED = "plugin.file.lastModified";
    public static final String ITEM_PLUGIN_FRAGMENT = "plugin.fragment";
    public static final String ITEM_PLUGIN_MANIFEST_MF = "plugin.manifest.mf";
    public static final String ITEM_PLUGIN_MANIFEST_XML = "plugin.manifest.xml";
    public static final String ITEM_PLUGIN_MANIFEST_XML_AVAIL = "plugin.manifest.xml.avail";
    public static final String ITEM_PLUGIN_VIEW_XML = "plugin.view.xml";

    // PropertiesReader
    public static final String BUNDLE_DEFAULT = "META-INF/bundle.properties";
}
