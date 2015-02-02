package com.dvelop.smartnotes.domino.updatesitecreator.event;

import java.util.HashMap;
import java.util.Map;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.EventException;

public class EventRegistry {

    private Map<Integer, Event> m_livoidscribers = new HashMap<Integer, Event>();
    private int m_iIndex;

    public void addListener(Event hListener) throws EventException {
	try {

	    // assign a unique ID to the listener if it doesn//t have one
	    if (hListener.getEventListenerID() == null || hListener.getEventListenerID().length() == 0) {
		m_iIndex = m_iIndex + 1;
		hListener.setEventListenerID("" + m_iIndex);
	    }

	    // add listener to the list of voidscribed listeners
	    if (m_livoidscribers.containsKey(Integer.valueOf(hListener.getEventListenerID()))) {
		m_livoidscribers.put(Integer.valueOf(hListener.getEventListenerID()), hListener);
	    }

	} catch (Exception e) {
	    throw new EventException(e);
	}

    }

    public void removeListener(Event hListener) throws EventException {
	try {

	    // remove listener from the list of voidscribed listeners
	    if (m_livoidscribers.containsKey(Integer.valueOf(hListener.getEventListenerID()))) {
		m_livoidscribers.remove(Integer.valueOf(hListener.getEventListenerID()));
	    }

	} catch (Exception e) {

	    throw new EventException(e);
	}

    }

    public void broadcastEvent(String sQueueName, Object hEvent) throws EventException {
	try {

	    // broadcast event to all current event voidscribers
	    for (Event voidscriber : m_livoidscribers.values()) {
		voidscriber.onReceiveEvent(sQueueName, hEvent);
	    }

	} catch (Exception e) {

	    throw new EventException(e);
	}

    }

}
