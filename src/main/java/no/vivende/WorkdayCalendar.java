package no.vivende;

import java.time.LocalDate;
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

    @Override
    public void setHoliday(Calendar date) {
        final var localDate = LocalDate.ofInstant(date.toInstant(), date.getTimeZone().toZoneId());
        holidays.add(localDate);
    }

    @Override
    public void setRecurringHoliday(Calendar date) {

    }

    @Override
    public void setWorkdayStartAndStop(Calendar start, Calendar stop) {

    }

    @Override
    public Date getWorkdayIncrement(Date startDate, float incrementInWorkdays) {
        return null;
    }
}
