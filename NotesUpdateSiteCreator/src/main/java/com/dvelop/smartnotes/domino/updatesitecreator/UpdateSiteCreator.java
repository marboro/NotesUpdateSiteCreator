package com.dvelop.smartnotes.domino.updatesitecreator;

import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.NotesThread;
import lotus.domino.Session;

import com.dvelop.smartnotes.domino.updatesitecreator.builder.UpdateSiteBuilder;

public class UpdateSiteCreator {

    public static void main(String[] args) {
	try {
	    NotesThread.sinitThread();
	    Session session = null;
	    try {

		session = NotesFactory.createSessionWithFullAccess();
		UpdateSiteBuilder updateSiteBuilder = new UpdateSiteBuilder(session);
		updateSiteBuilder.setServer(args[0]);
		updateSiteBuilder.setUpdateSiteNsfFileName(args[1]);
		updateSiteBuilder.setUpdateSiteNsfTitle(args[2]);
		String ustfn = ("".equals(args[3])) ? "updatesite.ntf" : args[3];
		updateSiteBuilder.setUpdateSiteTemplateFileName(ustfn);
		updateSiteBuilder.setUpdateSitePath(args[4]);
		updateSiteBuilder.buildUpdateSite();
	    } catch (Exception e) {
		e.printStackTrace();
	    } finally {
		if (session != null) {
		    try {
			session.recycle();
		    } catch (NotesException e) {

			e.printStackTrace();
		    }
		}
		NotesThread.stermThread();
		System.out.println("done");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
