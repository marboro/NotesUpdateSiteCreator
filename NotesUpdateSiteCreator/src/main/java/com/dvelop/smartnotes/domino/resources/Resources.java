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

    // WidgetCatalog
    public static final String WIDGET_CREATED_TEXT = "'VAR_WIDGETNAME' was successfully imported. You should open this widget and configure it. VAR_NEWLINE VAR_NEWLINE Source: VAR_WIDGETXMLPATH";
    public static final String FAIL_TO_IMPORT_WIDGET_TEXT = "Widget import failed.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following file:VAR_NEWLINE VAR_WIDGETXMLPATH";
    public static final String FAIL_TO_IMPORT_TEXT = "'VAR_WIDGETNAME' was imported,with errors.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following file:VAR_NEWLINE VAR_PROXYXMLPATH VAR_OAUTHXMLPATH";
    public static final String FAIL_TO_IMPORT_PROXY_OAUTH_TEXT = "'VAR_WIDGETNAME' was imported,with errors.VAR_NEWLINE VAR_NEWLINE To correct the errors, verify and correct the source code in the following files:VAR_NEWLINE VAR_PROXYXMLPATH VAR_NEWLINE VAR_OAUTHXMLPATH";
    public static final String FAIL_TO_OPEN_OAUTH_DB = "'VAR_WIDGETNAME' was imported,with OAuth error: Not able to open Credential Store database.VAR_NEWLINE VAR_NEWLINE To correct the error, click Configure Credential Store button on the Configuration view to reset the values and { re-import the widget.";
    public static final String FAIL_TO_FIND_OAUTH_CONFIG = "'VAR_WIDGETNAME' was imported,with OAuth error: Not able to find an available OAuth configuration in the file.VAR_NEWLINE VAR_NEWLINE To correct the error, verify and correct the source code in the following file:VAR_NEWLINE VAR_OAUTHXMLPATH";
    public static final String FAIL_TO_FIND_OAUTH_FILE = "'VAR_WIDGETNAME' was imported, with OAuth error: Not able to find an available OAuth file in VAR_OAUTHXMLPATH.VAR_NEWLINE VAR_NEWLINE To correct the error, provide an available OAuth file in VAR_OAUTHXMLPATH and re-import the widget.";
    public static final String ANONYMOUS_CONTEXT_PATH = "/anonymous";

    // REM "Begin Translatable Text”
    public static final String PROXY_DIALOG_TITLE = "Configure Proxy";
    public static final String ANONYMOUS_DIALOG_TITLE = "Configure Anonymous Proxy";

    public static final String ERROR_DLG_TITLE = "Error";
    public static final String VALIDATION_ERROR = "The fields marked with * are required.";

    public static final String MODIFY_SETTING_TITLE = "Modify";
    public static final String MODIFY_SETTING_TEXT = "Do you want to replace an existing setting?";

    public static final String COPY_POLICY_TXT = "You must first select a policy to edit.";

    public static final String REMOVE_DLG_TITLE = "Nothing selected";
    public static final String REMOVE_SETTING_TITLE = "Remove";
    public static final String REMOVE_SETTING_TXT = "Do you want to remove the selected setting?";
    public static final String NO_SELECTION_ERROR = "You must first select a policy to remove.";

    public static final String REMOVE_ALL_DLG_TITLE = "Policy List Empty";
    public static final String RM_ALL_TITLE = "Remove All";
    public static final String RM_ALL_TEXT = "You will remove all settings. Do you want to continue?";
    public static final String NO_SETTING_TEXT = "There are no policies in the list to remove.";
    public static final String EMPTY_VALUE_ERROR = "Policy List must be filled in.";

    public static final String SPECIAL_CHARS_ERROR = "Can't input Eual or Semicolon in all text fields.";

    public static final String INVALID_FIELD_TITLE = "Invalid Field";
    public static final String POLICY_URL_VALIDATE_ERR = "The URL field is not valid. The value must either be a valid URL, or if it contains a wildcard character (*), it can be only in the last component of the URL.";
    public static final String HEADERS_VALIDATE_ERR = "The Headers field is not valid. Header names may contain ASCII characters except for ()<>@,;:\\/[]?={} or double quotation marks, spaces or tabs. * may be used as a wildcard character.";
    public static final String MIME_TYPES_VALIDATE_ERR = "The MIME Types field is not valid. MIME types are specified in the form token/token.  Tokens contain ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, spaces Or tabs.";
    public static final String COOKIES_VALIDATE_ERR = "The Cookies field is not valid. Cookie names contain ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, spaces or tabs.";
    public static final String FIELD_NAME_VALIDATE_ERR = "The metadata Name field is not valid. The Name field contains ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, tabs.";
    public static final String FIELD_VALUE_VALIDATE_ERR = "The metadata Value field is not valid. The Value field contains ASCII characters except for ()<>@,;:\\/[]?={} Or double quotation marks, tabs.";
    public static final String NO_MAPPED_FIELD_VALUE_ERR = "The Value field does not contain a value appropriate for the parameter specified in the Name field.";
    public static final String ALLOW_LIST_VALIDATE_ERR = "The Allow list is not valid.  Valid contents include a fully qualified domain name (no wildcards), an IP-address with subnet mask specified as address/mask, where each component is a valid IP address, or an IP-address with a * for specific components. * may not be used by itself.";
    public static final String DENY_LIST_VALIDATE_ERR = "The Deny list is not valid.  Valid contents include a fully qualified domain name (no wildcards), an IP-address with subnet mask specified as address/mask, where each component is a valid IP address, or an IP-address with a * for specific components.  * may not be used by itself.";

    public static final String OAUTH_DIALOG_TITLE = "Configure OAuth Consumer Information";
    public static final String PROXY_WILD_URL_WARNING_TEXT = "Setting the URL of the content proxy to * will allow ALL traffic through the proxy and will affect ALL OpenSocial Widgets. A more restrictive rule is recommended. Are you sure you want to continue?";
    public static final String PROXY_WILD_URL_WARNING_TITLE = "Warning";

    // 'Strings for C4 Integration
    public static final String WIDGET_IMPORT_ERR_TXT = "Widget Import Error";
    public static final String PROXY_IMPORT_ERR_TXT = "Proxy Import Error";
    public static final String OAUTH_IMPORT_ERR_TXT = "OAuth Import Error";

    public static final String INFO_TXT = "New Notes Widget";
    public static final String ATTACH_FILE_TITLE = "Attach File";
    public static final String NO_WIDGET_CREATED_ERROR = "Fail to create a new widget.";

    public static final String OVERRIDE_DLG_TITLE = "Replace Existing Documents?";
    public static final String OVERRIDE_OAUTH_TEXT = "You will import OAuth information for this gadget. If there are existing OAuth documents for this gadget, do you want to replace the existing documents with these new files? ";
    public static final String DUPLICATE_PROXY_ERROR = "Error: There are duplicate proxy rules in the VAR_PROXYXMLNAME file.";
    public static final String INVALID_PROXY_PROPERTY_ERROR = "Error: There are invalid values for proxy url, action, header, mime-type, or cookie in the VAR_PROXYXMLNAME file.";
    public static final String INVALID_ALLOW_DENYLIST_ERROR = "Error: There are invalid values for allow list or deny list in the VAR_PROXYXMLNAME file.";
    // "End Translatable Text”
}
