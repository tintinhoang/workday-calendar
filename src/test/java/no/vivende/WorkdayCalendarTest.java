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

    @Test
    void getWorkdayIncrement_should_skip_weekend() {
        final var start = toDate("19-05-2023 08:00");
        final var increment = 1f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("22-05-2023 08:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_skip_fixed_holidays() {
        calendar.setHoliday(
                new GregorianCalendar(2023, Calendar.MAY, 10, 0, 0));
        final var start = toDate("09-05-2023 08:00");
        final var increment = 1f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("11-05-2023 08:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_not_skip_fixed_holidays_from_other_years() {
        calendar.setHoliday(
                new GregorianCalendar(2022, Calendar.MAY, 10, 0, 0));
        calendar.setHoliday(
                new GregorianCalendar(2024, Calendar.MAY, 10, 0, 0));
        final var start = toDate("09-05-2023 08:00");
        final var increment = 1f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("10-05-2023 08:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_skip_recurring_holidays_every_year() {
        calendar.setRecurringHoliday(
                new GregorianCalendar(2022, Calendar.MAY, 17, 0, 0));
        final var start = toDate("16-05-2023 08:00");
        final var increment = 1f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("18-05-2023 08:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_support_night_shift() {
        calendar.setWorkdayStartAndStop(
                new GregorianCalendar(2023, Calendar.JANUARY, 1, 22, 0),
                new GregorianCalendar(2023, Calendar.JANUARY, 2, 6, 0));
        final var start = toDate("24-05-2023 22:00");
        final var increment = 5.5f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("01-06-2023 02:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_return_same_workday_when_increment_is_zero_and_start_date_is_valid() {
        final var start = toDate("24-05-2023 08:00");
        final var increment = 0f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("24-05-2023 08:00", f.format(result));
    }

    @Test
    void getWorkdayIncrement_should_return_next_valid_date_and_time_when_increment_is_zero_and_start_date_is_invalid() {
        final var start = toDate("27-05-2023 07:00");
        final var increment = 0f;
        final var result = calendar.getWorkdayIncrement(start, increment);
        assertEquals("29-05-2023 08:00", f.format(result));
    }

    private Date toDate(String dateString) {
        try {
            return f.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}