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

    private ZonedDateTime nearestValidWorkDateAndTime(ZonedDateTime dateTime, double increment) {
        final var step = (int) Math.signum(increment);

        if (!isWorkTime(dateTime.toLocalTime())) {
            dateTime = nextWorkMinute(dateTime, step);
        }

        if (!isWorkDate(dateTime.toLocalDate())) {
            dateTime = nextWorkDate(dateTime, step);
        }

        return dateTime;
    }

    private ZonedDateTime addWorkDays(ZonedDateTime dateTime, long days) {
        final var step = Long.signum(days);
        var remainingDays = Math.abs(days);

        for (long i = 0; i < remainingDays; i++) {
            dateTime = nextWorkDate(dateTime, step);
        }

        return dateTime;
    }

    private ZonedDateTime addWorkMinutes(ZonedDateTime dateTime, long minutes) {
        final var step = Long.signum(minutes);
        var remainingMinutes = Math.abs(minutes);

        for (long i = 0; i < remainingMinutes; i++) {
            dateTime = nextWorkMinute(dateTime, step);
        }

        return dateTime;
    }

    private ZonedDateTime nextWorkDate(ZonedDateTime dateTime, int step) {
        do {
            dateTime = dateTime.plusDays(step);
        } while (!isWorkDate(dateTime.toLocalDate()));

        return dateTime;
    }

    private ZonedDateTime nextWorkMinute(ZonedDateTime dateTime, int step) {
        do {
            dateTime = dateTime.plusMinutes(step);
        } while (!isWorkTime(dateTime.toLocalTime()));

        return dateTime;
    }

    private boolean isWorkDate(LocalDate date) {
        return !isWeekend(date) && !isHoliday(date) && !isRecurringHoliday(date);
    }

    private boolean isWorkTime(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(stopTime);
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
