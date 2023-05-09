package com.shotmaniacs;

import com.shotmaniacs.utils.EventXSSFConverter;
import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventXSSFConverterTest {

    private final String filePath = "src/test/resources/Test_Events.xlsx";

    private EventXSSFConverter fileConverter;

    @BeforeEach
    public void setUp() {
        fileConverter = new EventXSSFConverter();
    }

    @Test
    public void readXLSXFile() throws FileNotFoundException {
        fileConverter.getByInputStream(new FileInputStream(filePath));
    }

    @Test
    public void textEventCount() throws FileNotFoundException {
        List<Event> events = fileConverter.getByInputStream(new FileInputStream(filePath));
        assertEquals(8, events.size());
    }

    @Test
    public void verifyFirstEventData() throws FileNotFoundException, ParseException {
        List<Event> events = fileConverter.getByInputStream(new FileInputStream(filePath));
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Event event = events.get(0);
        assertEquals("Some name 1", event.getName());
        assertEquals("Some location 1", event.getLocation());
        assertEquals(dateFormat.parse("01-01-2022 17:00"), event.getStartDate());
        assertEquals(dateFormat.parse("02-01-2022 19:00"), event.getEndDate());
        assertEquals(BookingType.PHOTOGRAPHY, event.getBookingType());
        assertEquals(EventType.CLUB, event.getEventType());
        assertEquals(2, event.getDuration());
        assertEquals("", event.getNotes());
    }

    @Test
    public void verifyFirstEventClient() throws FileNotFoundException {
        List<Event> events = fileConverter.getByInputStream(new FileInputStream(filePath));
        Client client = events.get(0).getClient();
        assertEquals("Test Client", client.getFullname());
        assertEquals("client@gmail.com", client.getEmail());
    }
}