package no.vivende;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

interface IWorkdayCalendar {
    void setHoliday(Calendar date);

    void setRecurringHoliday(Calendar date);

    void setWorkdayStartAndStop(Calendar start, Calendar stop);

    Date getWorkdayIncrement(Date startDate, float incrementInWorkdays);
}

public class WorkdayCalendar implements IWorkdayCalendar {

    private final Set<LocalDate> holidays = new HashSet<>();
    private final Set<MonthDay> recurringHolidays = new HashSet<>();

    @Override
    public void setHoliday(Calendar date) {
        final var localDate = LocalDate.ofInstant(date.toInstant(), date.getTimeZone().toZoneId());
        holidays.add(localDate);
    }

    @Override
    public void setRecurringHoliday(Calendar date) {
        final var localDate = LocalDate.ofInstant(date.toInstant(), date.getTimeZone().toZoneId());
        final var monthDay = MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        recurringHolidays.add(monthDay);
    }

    @Override
    public void setWorkdayStartAndStop(Calendar start, Calendar stop) {

    }

    @Override
    public Date getWorkdayIncrement(Date startDate, float incrementInWorkdays) {
        return null;
    }
}