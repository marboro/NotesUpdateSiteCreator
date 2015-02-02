package com.dvelop.smartnotes.domino.updatesitecreator.event;

import com.dvelop.smartnotes.domino.updatesitecreator.exceptions.EventException;

public class Event {

    private String eventListenerID;
    private EventRegistry eventRegistry;

    public Event(EventRegistry eventRegistry) {
	this.eventRegistry = eventRegistry;
    }

    public void onReceiveEvent(String sQueueName, Object hEvent) {
	// stub to be overridden by implementing class
    }

    public void subscribeEvents() throws EventException {
	try {
	    eventRegistry.addListener(this);
	} catch (EventException e) {
	    throw new EventException(e);
	}
    }

    public void unsubscribeEvents() throws EventException {
	try {
	    eventRegistry.removeListener(this);
	} catch (Exception e) {
	    throw new EventException(e);
	}
    }

    public void raiseEvent(String sQueueName, Object hEvent) throws EventException {
	try {

	    eventRegistry.broadcastEvent(sQueueName, hEvent);

	} catch (Exception e) {

	    throw new EventException(e);
	}

    }

    public String getEventListenerID() {
	return eventListenerID;
    }

    public void setEventListenerID(String m_sEventListenerID) {
	this.eventListenerID = m_sEventListenerID;
    }

}
