package com.dvelop.smartnotes.domino.updatesitecreator.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

public class JarWriter {
    /** The standard META-INF/MANIFEST file in jar files */
    private static final String MANIFEST = "META-INF/MANIFEST.MF";
    private static final int BUFFER = 4 * 1024;
    private final JarOutputStream m_jar;
    private final String m_jarFilePath;

    public JarWriter(final String jarFilePath) throws IOException {

	this.m_jarFilePath = new File(jarFilePath).getAbsolutePath();
	this.m_jar = new JarOutputStream(new FileOutputStream(this.m_jarFilePath));
	this.m_jar.setMethod(JarOutputStream.DEFLATED);
	this.m_jar.setLevel(Deflater.BEST_COMPRESSION);
    }

    public JarWriter(final String jarFilePath, final String manifestFilePath) throws IOException {

	// get the manifest

	String path = new File(manifestFilePath).getAbsolutePath();
	FileInputStream fis = new FileInputStream(path);
	Manifest manifest = new Manifest(fis);
	fis.close();

	this.m_jarFilePath = new File(jarFilePath).getAbsolutePath();
	this.m_jar = new JarOutputStream(new FileOutputStream(this.m_jarFilePath), manifest);
	this.m_jar.setMethod(JarOutputStream.DEFLATED);
	this.m_jar.setLevel(Deflater.BEST_COMPRESSION);

    }

    public void addFolder(final String folderPath) throws IOException {

	this.addFolder(folderPath, folderPath);
    }

    public void addFile(final String inputFilePath, String storedFileName) throws IOException {

	// System.out.println("->addFile(" + inputFilePath + ", " +
	// storedFileName + ")");

	// normalize path
	storedFileName = storedFileName.replace('\\', '/');

	if (storedFileName.equalsIgnoreCase(MANIFEST)) {
	    // System.out.println("...ignoring duplicate manifest file " +
	    // storedFileName);
	    return;
	}

	final JarEntry entry = new JarEntry(storedFileName);
	final File file = new File(inputFilePath);
	final FileInputStream in = new FileInputStream(file);
	final byte[] buffer = new byte[BUFFER];

	entry.setTime(file.lastModified());
	entry.setMethod(ZipEntry.DEFLATED);
	this.m_jar.putNextEntry(entry);

	int bytes;
	while ((bytes = in.read(buffer)) > 0) {
	    this.m_jar.write(buffer, 0, bytes);
	}
	in.close();
	this.m_jar.closeEntry();
    }

    private void addFolder(final String baseFolderPath, final String currentFolderPath) throws IOException {

	// System.out.println("->addFolder(" + baseFolderPath + ", " +
	// currentFolderPath + ")");

	final String TOKEN = ":";
	final File baseFolder = new File(baseFolderPath);
	final File currentFolder = new File(currentFolderPath);
	final File[] files = new File(currentFolder.getAbsolutePath()).listFiles();

	for (int i = 0; i < files.length; i++) {

	    final File file = files[i];
	    if ((!file.isDirectory()) && (!file.isHidden())) {

		// exclude our .jar file we're creating from getting included
		// into itself
		if (!file.getAbsolutePath().equalsIgnoreCase(this.m_jarFilePath)) {

		    // normalize paths so they can be used with regular
		    // expressions
		    if (File.separator.equalsIgnoreCase("\\")) {
			// Windows
			final String replAbsolute = file.getAbsolutePath().replaceAll("\\\\", TOKEN);
			final String replBase = baseFolder.getAbsolutePath().replaceAll("\\\\", TOKEN) + TOKEN;
			final String replRelative = replAbsolute.replaceAll(replBase, "");
			final String baseEntry = replRelative.replaceAll(TOKEN, "\\\\");
			this.addFile(file.getAbsolutePath(), baseEntry);
		    } else {
			// non Windows
			final String replAbsolute = file.getAbsolutePath().replaceAll("/", TOKEN);
			final String replBase = baseFolder.getAbsolutePath().replaceAll("/", TOKEN) + TOKEN;
			final String replRelative = replAbsolute.replaceAll(replBase, "");
			final String baseEntry = replRelative.replaceAll(TOKEN, "/");
			this.addFile(file.getAbsolutePath(), baseEntry);
		    }
		    ;
		}
	    } else {
		// System.out.println("...recursing into " +
		// file.getAbsolutePath());
		this.addFolder(baseFolderPath, file.getAbsolutePath());
	    }
	}
    }

    public void nukeFolder(final String folderPath) {

	// System.out.println("->nukeFolder(" + folderPath + ")");

	try {
	    final File folder = new File(folderPath);
	    final File[] files = folder.listFiles();

	    for (int i = 0; i < files.length; i++) {
		if (files[i].isDirectory()) {
		    this.nukeFolder(files[i].getAbsolutePath());
		}
		files[i].delete();
	    }
	    folder.delete();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    public void close() {

	// System.out.println("->close()");

	if (this.m_jar != null) {

	    try {
		this.m_jar.close();
	    } catch (final IOException e) {
		// ignore
	    }
	}
    }
}
