package no.vivende;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

class WorkdayCalendarTest {

    private final SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private WorkdayCalendar calendar;

    @BeforeEach
    void setUp() {
        calendar = new WorkdayCalendar();
    }

    @Test
    void getWorkdayIncrement_should_work_for_test_cases_specified_in_assignment() {
        calendar.setWorkdayStartAndStop(
                new GregorianCalendar(2023, Calendar.JANUARY, 1, 8, 0),
                new GregorianCalendar(2023, Calendar.JANUARY, 1, 16, 0));
        calendar.setRecurringHoliday(
                new GregorianCalendar(2023, Calendar.MAY, 17, 0, 0));
        calendar.setHoliday(
                new GregorianCalendar(2023, Calendar.MAY, 27, 0, 0));

        assertAll(
                () -> assertEquals(
                        "16-05-2023 12:00",
                        f.format(calendar.getWorkdayIncrement(
                                toDate("24-05-2023 18:05"), -5.5f))
                ),
                () -> assertEquals(
                        "26-07-2023 13:47",
                        f.format(calendar.getWorkdayIncrement(
                                toDate("24-05-2023 19:03"), 44.723656f))
                ),
                () -> assertEquals(
                        "15-05-2023 10:01",
                        f.format(calendar.getWorkdayIncrement(
                                toDate("24-05-2023 18:03"), -6.747021f))
                ),
                () -> assertEquals(
                        "09-06-2023 14:18",
                        f.format(calendar.getWorkdayIncrement(
                                toDate("24-05-2023 08:03"), 12.782709f))
                ),
                () -> assertEquals(
                        "05-06-2023 10:12",
                        f.format(calendar.getWorkdayIncrement(
                                toDate("24-05-2023 07:03"), 8.276628f))
                )
        );
    }

    private Date toDate(String dateString) {
        try {
            return f.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}