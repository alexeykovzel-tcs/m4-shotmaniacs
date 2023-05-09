package com.shotmaniacs.utils;

import com.shotmaniacs.models.event.BookingType;
import com.shotmaniacs.models.event.Event;
import com.shotmaniacs.models.event.EventType;
import com.shotmaniacs.models.user.Client;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Retrieves a list of events from Microsoft Excel, OpenOffice xml, and other XSSF files.
 */
public class EventXSSFConverter {

    /**
     * Retrieves events from the file input stream in the XSSF format. If the file could not be processes,
     * an empty list is returned.
     *
     * @param in stream of the file data
     * @return list of retrieved events
     */
    public List<Event> getByInputStream(InputStream in) {
        try {
            Workbook workbook = new XSSFWorkbook(in);
            return getWorkbookEvents(workbook);
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to process XSSF workbook");
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves events from the 2-nd row of the workbook instance (as the 1-st row is reserved for column titles).
     * The data allocation is the following:
     * <p>
     * { Event Name, Booking Type, Event Type, Location, Start Date, End Date, Duration (h), Notes, Fullname, Email }
     *
     * @param workbook workbook in the table-like format
     * @return the list of retrieved events from the workbook
     */
    public List<Event> getWorkbookEvents(Workbook workbook) {
        List<Event> events = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);

        // Start from the 2-nd row as the 1-st is reserved
        for (int i = 1; true; i++) {
            Row row = sheet.getRow(i);

            // Check if there are no more events in the table
            if ("".equals(row.getCell(0).getStringCellValue())) break;

            // Retrieve data from the first 9 cells
            String eventName = row.getCell(0).getStringCellValue();
            BookingType bookingType = BookingType.valueOfOrNull(row.getCell(1).getStringCellValue());
            EventType eventType = EventType.valueOfOrNull(row.getCell(2).getStringCellValue());
            String location = row.getCell(3).getStringCellValue();
            Date startDate = row.getCell(4).getDateCellValue();
            Date endDate = row.getCell(5).getDateCellValue();
            int duration = (int) row.getCell(6).getNumericCellValue();
            String notes = row.getCell(7).getStringCellValue();
            String fullname = row.getCell(8).getStringCellValue();
            String email = row.getCell(9).getStringCellValue();

            // Allocate the retrieved data to create client and event instances
            Client client = new Client(-1, fullname, null, email, null,null);
            events.add(new Event(-1, eventName, bookingType, eventType, location, startDate,
                    endDate, duration, notes, client));
        }
        return events;
    }
}