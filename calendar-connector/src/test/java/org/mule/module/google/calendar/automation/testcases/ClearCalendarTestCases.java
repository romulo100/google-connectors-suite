package org.mule.module.google.calendar.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.google.calendar.model.Calendar;
import org.mule.module.google.calendar.model.Event;
import org.mule.modules.google.api.client.batch.BatchResponse;

public class ClearCalendarTestCases extends GoogleCalendarTestParent {

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		try {
			testObjects = (Map<String, Object>) context.getBean("clearCalendar");
			
			// Insert the calendar
			Calendar calendar = insertCalendar((Calendar) testObjects.get("calendarRef"));
			
			testObjects.put("calendar", calendar);
			testObjects.put("calendarId", calendar.getId());
			
			Event sampleEvent = (Event) testObjects.get("sampleEvent");
			int numEvents = (Integer) testObjects.get("numEvents");
			
			// Instantiate the event objects
			List<Event> events = new ArrayList<Event>();
			for (int i = 0; i < numEvents; i++) {
				events.add(getEvent(sampleEvent.getSummary(), sampleEvent.getStart(), sampleEvent.getEnd()));
			}
			
			// Batch insert the events
			BatchResponse<Event> batchResponse = insertEvents(calendar, events);
			List<Event> successfulEvents = batchResponse.getSuccessful();
			
			testObjects.put("events", successfulEvents);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SmokeTests.class, SanityTests.class})
	@Test
	public void testClearCalendar() {
		try {
			String calendarId = testObjects.get("calendarId").toString();			
			testObjects.put("id", calendarId);
			
			// Clear the calendar
			MessageProcessor flow = lookupFlowConstruct("clear-calendar");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			// Get all events
			flow = lookupFlowConstruct("get-all-events");
			response = flow.process(getTestEvent(testObjects));
			
			// Assert that no events are returned
			List<Event> returnedEvents = (List<Event>) response.getMessage().getPayload();
			assertTrue(returnedEvents.isEmpty());
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			// Delete the calendar
			Calendar calendar = (Calendar) testObjects.get("calendar");
			deleteCalendar(calendar);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
