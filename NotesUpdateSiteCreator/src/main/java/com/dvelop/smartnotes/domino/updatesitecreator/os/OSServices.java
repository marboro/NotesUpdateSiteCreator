package com.dvelop.smartnotes.domino.updatesitecreator.os;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class OSServices {

    public static File createTempDirectory(String prefix) throws IOException {
	final File temp;

	temp = File.createTempFile(prefix, Long.toString(System.nanoTime()));

	if (!(temp.delete())) {
	    throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
	}

	if (!(temp.mkdir())) {
	    throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
	}

	return (temp);
    }

    public static void copyFile(File oldLocation, File newLocation) throws IOException {
	if (oldLocation.exists()) {
	    BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
	    BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
	    try {
		byte[] buff = new byte[8192];
		int numChars;
		while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
		    writer.write(buff, 0, numChars);
		}
	    } catch (IOException ex) {
		throw new IOException("IOException when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
	    } finally {
		try {
		    if (reader != null) {
			writer.close();
			reader.close();
		    }
		} catch (IOException ex) {
		    System.err.println("Error closing files when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
		}
	    }
	} else {
	    throw new IOException("Old location does not exist when transferring " + oldLocation.getPath() + " to " + newLocation.getPath());
	}
    }

    public static void copyFile(String oldLocation, String newLocation) throws IOException {
	copyFile(new File(oldLocation), new File(newLocation));

    }

    public static void fileDelete(File file) {
	if (!file.delete()) {
	    file.deleteOnExit();
	}

    }

    public static void fileDelete(String fileName) {
	fileDelete(new File(fileName));

    }

}
