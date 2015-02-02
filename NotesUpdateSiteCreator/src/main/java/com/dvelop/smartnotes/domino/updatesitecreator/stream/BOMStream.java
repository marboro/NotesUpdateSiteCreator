package com.dvelop.smartnotes.domino.updatesitecreator.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.Stream;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.OException;

public class BOMStream {

    private Session m_session;

    private final String DEFAULT_CHARSET = "UTF-8";

    public BOMStream(Session session) {
	try {
	    m_session = session;
	} catch (Exception e) {
	    OException.raiseError(e, this.getClass().getName(), session.toString());
	}

    }

    public Stream getStream(String sFilePath) {

	try {
	    Stream stream;

	    stream = m_session.createStream();
	    stream.open(sFilePath, this.getEncodingCharset(sFilePath));
	    return stream;

	} catch (NotesException e) {

	    OException.raiseError(e, this.getClass().getName(), sFilePath);
	    return null;
	}

    }

    public String getEncodingCharset(String sFilePath) {
	String defaultEncoding = "UTF-8";
	InputStream inputStream = null;
	try {
	    inputStream = new FileInputStream(new File(sFilePath));

	    BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
	    ByteOrderMark bom = bOMInputStream.getBOM();
	    return bom == null ? defaultEncoding : bom.getCharsetName();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (inputStream != null) {
		try {
		    inputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return "";
    }
}
