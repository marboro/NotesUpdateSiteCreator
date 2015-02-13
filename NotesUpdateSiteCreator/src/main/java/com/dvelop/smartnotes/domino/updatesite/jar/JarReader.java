package com.dvelop.smartnotes.domino.updatesite.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

public class JarReader {
    private static final int BUFFER = 4 * 1028;

    private final JarFile _jarFile;
    private Manifest _manifest;
    private Attributes _attributes;

    public JarReader(final String jarFilePath) throws IOException {

	this._jarFile = new JarFile(jarFilePath, false); // do NOT verify signed
							 // JARs
	this._manifest = this._jarFile.getManifest();

	if (this._manifest != null) {
	    this._attributes = this._manifest.getMainAttributes();
	}
    }

    public String getLastModified() {

	final File file = new File(this._jarFile.getName());
	return new Long(file.lastModified()).toString();
    }

    public boolean hasManifest() throws IOException {

	return (this._manifest == null) ? false : true;
    }

    public String getManifestValue(final String key) throws IOException {

	if (this.hasManifest()) {
	    final String value = this._attributes.getValue(key);
	    return (value == null) ? "" : value;
	} else {
	    return "";
	}
    }

    public String getManifestAsText() throws IOException {

	if (this.hasManifest()) {
	    final ByteArrayOutputStream os = new ByteArrayOutputStream();
	    this._manifest.write(os);
	    return os.toString();
	} else {
	    return "";
	}
    }

    public boolean hasFile(final String filePath) {

	final ZipEntry entry = this._jarFile.getEntry(filePath);
	return (entry == null) ? false : true;
    }

    public void saveAllFiles(final String targetFolderPath) throws IOException {

	final Enumeration jarEntries = this._jarFile.entries();

	while (jarEntries.hasMoreElements()) {
	    final JarEntry entry = (JarEntry) jarEntries.nextElement();
	    if (entry.isDirectory()) {
		final File folder = new File(targetFolderPath, entry.getName());
		folder.mkdir();
		folder.setLastModified(entry.getTime());

	    } else {
		this.saveFile(entry.getName(), targetFolderPath);
	    }
	}
    }

    public void saveFile(final String filePath, final String targetFolderPath) throws IOException {

	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	BufferedInputStream bis = null;
	ZipEntry entry = null;
	File file = null;

	try {
	    file = new File(targetFolderPath, filePath);

	    file.getParentFile().mkdirs();
	    entry = this._jarFile.getEntry(filePath);
	    bis = new BufferedInputStream(this._jarFile.getInputStream(entry));
	    fos = new FileOutputStream(file);
	    bos = new BufferedOutputStream(fos, BUFFER);

	    int bytesRead;
	    final byte data[] = new byte[BUFFER];
	    while ((bytesRead = bis.read(data, 0, BUFFER)) != -1) {
		bos.write(data, 0, bytesRead);
	    }
	    bos.flush();
	} finally {
	    if (bos != null) {
		bos.close();
	    }
	    if (bis != null) {
		bis.close();
	    }
	    if (fos != null) {
		fos.close();
	    }
	}
	file.setLastModified(entry.getTime());
    }

    public String getFileXMLEncoding(final String filePath) throws IOException {

	final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding=\"(.[^\"]*)\".*\\?>");

	String encoding = "";
	InputStream is = null;
	BufferedReader reader = null;

	final ZipEntry entry = this._jarFile.getEntry(filePath);
	if (entry == null) {
	    return "";
	}

	try {
	    is = this._jarFile.getInputStream(entry);
	    reader = new BufferedReader(new InputStreamReader(is));
	    Matcher m = ENCODING_PATTERN.matcher(reader.readLine());
	    encoding = (m.find()) ? m.group(1).toUpperCase() : "";
	} finally {
	    if (reader != null) {
		reader.close();
	    }
	    if (is != null) {
		is.close();
	    }
	}

	return encoding;
    }

    public InputStream getFileAsInputStream(final String filePath, final String charSet) throws IOException {
	final ZipEntry entry = this._jarFile.getEntry(filePath);
	if (entry == null) {
	    return null;
	}

	return this._jarFile.getInputStream(entry);

    }

    public String getFileAsText(final String filePath, final String charSet) throws IOException {

	InputStream is = null;
	StringBuffer sbuffer = null;
	BufferedReader reader = null;

	final ZipEntry entry = this._jarFile.getEntry(filePath);
	if (entry == null) {
	    return "";
	}

	try {
	    is = this._jarFile.getInputStream(entry);

	    if (charSet.trim().length() == 0) {
		reader = new BufferedReader(new InputStreamReader(is));
	    } else {
		reader = new BufferedReader(new InputStreamReader(is, charSet));
	    }

	    String line;
	    sbuffer = new StringBuffer();
	    while ((line = reader.readLine()) != null) {
		sbuffer.append(line + "\n");
	    }
	} finally {
	    if (reader != null) {
		reader.close();
	    }
	    if (is != null) {
		is.close();
	    }
	}

	return sbuffer.toString();

    }

    public void close() {

	if (this._jarFile != null) {

	    try {
		this._jarFile.close();
	    } catch (final IOException e) {
		// ignore
	    }
	}
    }
}
