package com.dvelop.smartnotes.domino.updatesitecreator.jar;

import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader extends URLClassLoader {

    public JarLoader(URL[] urls) {
	super(urls);
    }

    public void addURL(URL url) {
	super.addURL(url);
    }
}
