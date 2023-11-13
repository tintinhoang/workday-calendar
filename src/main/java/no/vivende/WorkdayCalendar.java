package no.vivende;

import java.util.Calendar;
import java.util.Date;

interface IWorkdayCalendar {
    void setHoliday(Calendar date);

    void setRecurringHoliday(Calendar date);

    void setWorkdayStartAndStop(Calendar start, Calendar stop);

    Date getWorkdayIncrement(Date startDate, float incrementInWorkdays);
}

public class WorkdayCalendar implements IWorkdayCalendar {
    @Override
    public void setHoliday(Calendar date) {

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
