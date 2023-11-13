package no.vivende;

import java.time.*;
import java.util.*;

interface IWorkdayCalendar {
    void setHoliday(Calendar date);

    void setRecurringHoliday(Calendar date);

    void setWorkdayStartAndStop(Calendar start, Calendar stop);

    Date getWorkdayIncrement(Date startDate, float incrementInWorkdays);
}

public class WorkdayCalendar implements IWorkdayCalendar {

    private LocalTime startTime = LocalTime.of(8, 0);
    private LocalTime stopTime = LocalTime.of(16, 0);
    private final Set<LocalDate> holidays = new HashSet<>();
    private final Set<MonthDay> recurringHolidays = new HashSet<>();
    private final Set<DayOfWeek> weekendDaysOfWeek = new HashSet<>(Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

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
        startTime = LocalTime.ofInstant(start.toInstant(), start.getTimeZone().toZoneId());
        stopTime = LocalTime.ofInstant(stop.toInstant(), stop.getTimeZone().toZoneId());
    }

    @Override
    public Date getWorkdayIncrement(Date startDate, float incrementInWorkdays) {
        return null;
    }

    private ZonedDateTime nextWorkDate(ZonedDateTime dateTime, int step) {
        do {
            dateTime = dateTime.plusDays(step);
        } while (!isWorkDate(dateTime.toLocalDate()));

        return dateTime;
    }

    private boolean isWorkDate(LocalDate date) {
        return !isWeekend(date) && !isHoliday(date) && !isRecurringHoliday(date);
    }

    private boolean isWeekend(LocalDate date) {
        return weekendDaysOfWeek.contains(date.getDayOfWeek());
    }

    private boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
    }

    private boolean isRecurringHoliday(LocalDate date) {
        final var monthDay = MonthDay.from(date);
        return recurringHolidays.contains(monthDay);
    }
}
