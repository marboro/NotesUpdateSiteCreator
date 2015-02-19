package com.dvelop.smartnotes.domino.resources;

public class Resources {

    // RichTextStyle colors
    public static final int COLOR_BLACK = 0;
    public static final int COLOR_BLUE = 4;
    public static final int COLOR_CYAN = 7;
    public static final int COLOR_DARK_BLUE = 10;
    public static final int COLOR_DARK_CYAN = 13;
    public static final int COLOR_DARK_GREEN = 9;
    public static final int COLOR_DARK_MAGENTA = 11;
    public static final int COLOR_DARK_RED = 8;
    public static final int COLOR_DARK_YELLOW = 12;
    public static final int COLOR_GRAY = 14;
    public static final int COLOR_GREEN = 3;
    public static final int COLOR_LIGHT_GRAY = 15;
    public static final int COLOR_MAGENTA = 5;
    public static final int COLOR_RED = 2;
    public static final int COLOR_WHITE = 1;
    public static final int COLOR_YELLOW = 6;

    public static final String ERR_CAPTION = "Notes error in '%s1':";
    public static final String ERR_FORMAT_LOCATION = "%s1 (#%s2) in:";
    public static final String ERR_FORMAT_MODULE = "%s1: %s2 (line %s3)";
    public static final String ERR_FORMAT_CLASS = "%s1: %s2::%s3 (line %s4)";
    public static final String ERR_ADDITIONAL_INFO = "Additional Info: %s1";

    public static final String ERR_NOTESITEM_REQUIRED = "A NotesItem object is required to instantiate '%s1'";
    public static final String ERR_NOTESRTITEM_REQUIRED = "A NotesRichTextItem object is required to instantiate '%s1'";
    public static final String ERR_NOTESDOC_REQUIRED = "A NotesDocument object is required to instantiate '%s1'";
    public static final String ERR_NOTESUIDOC_REQUIRED = "A NotesUIDocument object is required to instantiate '%s1'";
    public static final String ERR_NOTESVIEW_REQUIRED = "A NotesView object is required to instantiate '%s1'";
    public static final String ERR_NOTESDOC_COLL_REQUIRED = "A NotesDocument or NotesDocumentcollection object is required to instantiate '%s1'";
    public static final String ERR_SITE_REQUIRED = "A cSite object is required to instantiate '%s1'";
    public static final String ERR_FILE_REQUIRED = "A filepath string argument is required to instantiate '%s1'";
    public static final String ERR_FOLDER_REQUIRED = "A folderpath string argument is required to instantiate '%s1'";
    public static final String ERR_INVALID_DESIGNDOCTYPE = "Invalid design doc type '%s1' specified for %s2";

    public static final String ERR_VIEW_NOT_FOUND = "Unable to open view %s1: View not found";
    public static final String ERR_OPEN_FILE = "Unable to open file %s1";
    public static final String ERR_IMPORT_FILE = "Unable to import file %s1: %s2";
    public static final String ERR_UNZIP_FILE = "Unable to unzip file %s1 into folder %s2: %s3";
    public static final String ERR_EMPTY_FILE = "File is empty";
    public static final String ERR_FILE_NOT_FOUND = "File not found";
    public static final String ERR_IMPORT_FEATURE = "Unable to import feature file '%s1': %s2";
    public static final String ERR_IMPORT_PLUGIN = "Unable to import plugin file '%s1': %s2";
    public static final String ERR_IMPORT_DATA = "Unable to import data file '%s1': %s2";

    public static final String ERR_IMPORT_FEATURES_FAILED = "Feature import has failed, the database has been restored to its previous state.\nCheck the activity log for details.";
    public static final String ERR_IMPORT_SITE_FAILED = "Site import has failed, the database has been restored to its previous state.\nCheck the activity log for details.";

    public static final String ERR_SELECT_UPDATE_URL_TYPE = "Please select the type of URLs to be updated.";
    public static final String ERR_SELECT_IMPORT_DB = "Please select an Eclipse Update Site database to import first.";
    public static final String ERR_IMPORT_INVALID_DB = "The selected database is not a valid Eclipse Update Site database.\n%s1";
    public static final String ERR_IMPORT_DB_RETRY = "Please select a different database.";
    public static final String ERR_IMPORT_DB_FAILED = "Import from '%s1' failed, the update site has been restored to its previous state.\nCheck the activity log for details.";
    public static final String ERR_DELETE_PROHIBITED = "You cannot delete individual content from this Update Site.\nTo remove ALL content, select 'Actions, Delete All Content' instead.";
    public static final String ERR_EDIT_PROHIBITED = "You cannot edit this document";

    public static final String ERR_NO_EDITOR_ACCESS = "You need at least EDITOR access to perform this operation";
    public static final String ERR_NO_DEL_DOCS_ACCESS = "You need the ACL option 'Delete documents' to perform this operation";

    public static final String TAG_COMMENT_CPU = "<!-- CPU time used to generate this site: %s1 secs -->";
    public static final String TAG_COMMENT_EFFECTIVE_USER = "<!-- Effective user name is \"%s1\" -->";

    public static final String LBL_LOCAL_SYSTEM = "Local";
    public static final String LBL_BUTTON_CANCEL = "Cancel";
    public static final String LBL_BUTTON_WAIT = "Please Wait";
    public static final String LBL_BUTTON_CANCELLING = "Cancelling...";
    public static final String LBL_GENERATE_SITE_DIGEST = "Generating Site Digest, please wait...";

    public static final String LBL_SIGN_CONTENT = "Signing imported content, please wait...";
    public static final String LBL_SIGN_DOCUMENTS = "Signing %s1 of %s2 documents";

    public static final String LBL_IMPORT_SITE_READ = "Reading update site, please wait...";
    public static final String LBL_IMPORT_SITE_BEGIN = "Importing update site, please wait...";
    public static final String LBL_IMPORT_SITE_END = "Finished importing update site.";

    public static final String LBL_IMPORT_FEATURES_READ = "Reading %s1 selected features, please wait...";
    public static final String LBL_IMPORT_FEATURES_BEGIN = "Importing %s1 selected features, please wait...";
    public static final String LBL_IMPORT_FEATURES_END = "Finished importing %s1 features";
    public static final String LBL_IMPORT_COMPLETED = "Import successfully completed!";

    public static final String LBL_IMPORT_DB_READ = "Reading Update Site database, please wait...";
    public static final String LBL_IMPORT_DB_BEGIN = "Importing Update Site database, please wait...";
    public static final String LBL_IMPORT_DB_END = "Finished importing Update Site database";
    public static final String LBL_IMPORT_DB_UPTODATE = "Database was already uptodate, no import was required";
    public static final String LBL_IMPORT_DB_READ_ENTRY = "Reading %s1";
    public static final String LBL_IMPORT_DB_IMPORT_NOTE = "Importing %s1";

    public static final String LBL_UPDATE_URLS_BEGIN = "Updating URL references, please wait...";
    public static final String LBL_UPDATE_URLS_END = "Finished updating URL references";
    public static final String LBL_UPDATE_URLS_COMPLETED = "Update successfully completed!";

    public static final String LBL_TABLE_PLUGIN_ID = "Plugin ID";
    public static final String LBL_TABLE_FRAGMENT_ID = "Fragment ID";

    public static final String MSG_NO_PASTE = "You cannot paste documents into this database.";
    public static final String MSG_LOADING_PLEASE_WAIT = "Loading, please wait...";
    public static final String MSG_SELECT_SITEXML_FILE = "Please select a site.xml file to import first.";
    public static final String MSG_SELECT_FEATURES = "Please select one or more features to import first.";

    public static final String MSG_NO_PLUGINS_OR_FRAGMENTS = "This feature contains no plugins or fragments.";
    public static final String MSG_BUNDLED_PLUGINS = "Plugins bundled by this feature:";
    public static final String MSG_BUNDLED_FRAGMENTS = "Fragments bundled by this feature:";

    public static final String MSG_SELECT_IMPORT_DB = "Select Eclipse Update Site Database To Import:";
    public static final String MSG_SELECT_SITEMAP_FILE = "Select Eclipse Update Site File To Import:";
    public static final String MSG_SELECT_FEATURE_FOLDER = "Select Features To Import:";
    public static final String MSG_IMPORT_CANCELLED = "Import cancelled.";
    public static final String MSG_UPDATE_CANCELLED = "Update cancelled.";

    public static final String MSG_IMPORT_SITE_LOCAL = "Import Local Update Site";
    public static final String MSG_IMPORT_FEATURES = "Import Features";
    public static final String MSG_IMPORT_DB = "Import Update Site Database";
    public static final String MSG_UPDATE_URL_REFERENCES = "Update URL References";
    public static final String MSG_SHOW_URLS = "Show URLs";
    public static final String MSG_SELECT_URL = "Select URL";
    public static final String MSG_GENERATE_SITE_DIGEST = "Generate Site Digest";

    public static final String MSG_URL_COPIED_TO_CLIPBOARD = "The URL has been copied to the clipboard";

    public static final String MSG_IGNORE_MISSING_PLUGIN = "Ignoring missing plugin '%s1'";
    public static final String MSG_IGNORE_MISSING_FEATURE = "Ignoring missing feature '%s1'";
    public static final String MSG_IGNORE_DUPLICATE_FEATURE = "Ignoring duplicate feature '%s1'";
    public static final String MSG_IGNORE_DUPLICATE_PLUGIN = "Ignoring duplicate plugin '%s1'";

    public static final String MSG_SIGN_DOCS_CONFIRM = "This will sign ALL content in this update site with your current ID '%s1'. Proceed?";
    public static final String MSG_SIGN_DOCS_CANCELLED = "Signing cancelled";
    public static final String MSG_SIGN_NO_DOCS = "No content found to sign, update site is empty";
    public static final String MSG_SIGN_DOCS_BEGIN = "Signing %s1 documents, please wait...";
    public static final String MSG_SIGN_DOCS_END = "Finished signing %s1 documents";
    public static final String MSG_SIGN_LOOP = "Signing %s1 of %s2 documents, please wait...";

    public static final String MSG_DEL_DOCS_CONFIRM = "This will delete ALL content from this update site. Proceed?";
    public static final String MSG_DEL_DOCS_CANCELLED = "Deletion cancelled";
    public static final String MSG_DEL_NO_DOCS = "No content found to delete, update site is empty";
    public static final String MSG_DEL_DOCS_BEGIN = "Deleting %s1 documents, please wait...";
    public static final String MSG_DEL_DOCS_END = "Finished deleting %s1 documents";

    public static final String MSG_IMPORTING_CATEGORY = "Importing category %s1";
    public static final String MSG_UPDATING_CATEGORY = "Updating category %s1";

    public static final String MSG_IMPORTING_ARCHIVE = "Importing archive %s1";
    public static final String MSG_UPDATING_ARCHIVE = "Updating archive %s1";

    public static final String MSG_READING_FEATURE = "Reading feature %s1";
    public static final String MSG_IMPORTING_FEATURE = "Importing feature %s1";
    public static final String MSG_UPDATING_FEATURE = "Updating feature %s1";
    public static final String MSG_ATTACHING_DATA = "Attaching feature data %s1";

    public static final String MSG_READING_PLUGIN = "Reading plugin %s1";
    public static final String MSG_IMPORTING_PLUGIN = "Importing plugin %s1";
    public static final String MSG_UPDATING_PLUGIN = "Updating plugin %s1";

    public static final String MSG_FILTER_ALL = "All";
    public static final String MSG_FILTER_SITEMAP = "Site Map File";
    public static final String MSG_FILTER_FEATURE = "Feature JAR Files";

    public static final String LOG_INITIATED_BY = "Initiated by %s1 (using %s2)";
    public static final String LOG_ACTIVITY_COMPLETED = "Activity completed";
    public static final String LOG_IMPORTING_SITE = "Importing site %s1";

    public static final String LOG_REMOTE_ARCHIVE = "Found remote archive for '%s1' pointing to %s2";
    public static final String LOG_LOCAL_ARCHIVE = "Found local archive for '%s1' pointing to %s2";
    public static final String LOG_IGNORE_LOCAL_ARCHIVE = "Ignoring local archive '%s1' for serialization";
    public static final String LOG_CREATE_ARCHIVE_DOC = "Creating new archive doc for '%s1'";
    public static final String LOG_UPDATE_ARCHIVE_DOC = "Updating existing archive doc for '%s1'";
    public static final String LOG_UPTODATE_ARCHIVE_DOC = "The existing archive doc for '%s1' is already uptodate";

    public static final String LOG_CATEGORY = "Found category '%s1' %s2";
    public static final String LOG_CREATE_CATEGORY_DOC = "Creating new category doc for '%s1'";
    public static final String LOG_UPDATE_CATEGORY_DOC = "Updating existing category doc for '%s1'";
    public static final String LOG_UPTODATE_CATEGORY_DOC = "The existing category doc for '%s1' is already uptodate";

    public static final String LOG_CREATE_FEATURE_DOC = "Creating new feature doc for '%s1'";
    public static final String LOG_UPDATE_FEATURE_DOC = "Updating existing feature doc for '%s1'";
    public static final String LOG_UPTODATE_FEATURE_DOC = "The existing feature doc for '%s1' is already uptodate";

    public static final String LOG_CREATE_PLUGIN_DOC = "Creating new plugin doc for '%s1'";
    public static final String LOG_UPDATE_PLUGIN_DOC = "Updating existing plugin doc for '%s1'";
    public static final String LOG_UPTODATE_PLUGIN_DOC = "The existing plugin doc for '%s1' is already uptodate";

    public static final String LOG_REGENERATE_SITE_DIGEST = "Regenerating site digest '%s1'";

    public static final String LOG_IMPORT_DB_LOCAL = "Importing from local update site database '%s1'";
    public static final String LOG_IMPORT_DB_SERVER = "Importing from update site database '%s1' on '%s2'";
    public static final String LOG_IMPORT_DB_FEATURES = "Found %s1 new feature documents to import";
    public static final String LOG_IMPORT_DB_PLUGINS = "Found %s1 new plugin documents to import";
    public static final String LOG_IMPORT_DB_ARCHIVES = "Found %s1 new archive documents to import";
    public static final String LOG_IMPORT_DB_CATEGORIES = "Found %s1 new category documents to import";

    public static final String LOG_IMPORT_NEW_DOC = "The document for '%s1' will be imported into this database";
    public static final String LOG_IMPORT_EXISTING_DOC = "A document for '%s1' already exists in this database";
    public static final String LOG_IMPORT_BASE_FOLDER = "Base folderpath is '%s1'";
    public static final String LOG_IMPORT_SET_FEATURE_URL = "Set feature URL to '%s1'";

    public static final String LOG_URL_NEW_LABEL = "New Label: %s1";
    public static final String LOG_URL_NEW_URL = "New URL: %s1";
    public static final String LOG_URL_CHANGE_UPDATESITES = "Change Update Sites: %s1";
    public static final String LOG_URL_CHANGE_DISCOVERY = "Change Discovery Sites: %s1";
    public static final String LOG_URL_CHANGE_ALLFEATURES = "Change all features: %s1";
    public static final String LOG_URL_UPDATED_FEATURE = "Updated feature '%s1' (%s2 %s3)";
    public static final String LOG_SEPARATOR_START = "--------------------START----------------------";
    public static final String LOG_SEPARATOR_END = "---------------------END-----------------------";
}
