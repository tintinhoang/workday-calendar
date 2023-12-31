package no.vivende;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;

import static java.time.temporal.ChronoUnit.MINUTES;

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
    public Date getWorkdayIncrement(Date startDate, float increment) {
        final var minutesInADay = 24 * 60;
        final var workDayDurationInMinutes = (MINUTES.between(startTime, stopTime) + minutesInADay) % minutesInADay; // use minutes in a day to support night shift
        final var minutesToAdd = BigDecimal.valueOf(increment)
                .remainder(BigDecimal.ONE)
                .multiply(BigDecimal.valueOf(workDayDurationInMinutes))
                .longValue();
        final var daysToAdd = (long) increment;

        var dateTime = ZonedDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        dateTime = nearestValidWorkDateAndTime(dateTime, increment);
        dateTime = addWorkMinutes(dateTime, minutesToAdd);
        dateTime = addWorkDays(dateTime, daysToAdd);

        return Date.from(dateTime.toInstant());
    }

    private ZonedDateTime nearestValidWorkDateAndTime(ZonedDateTime dateTime, double increment) {
        final var step = increment == 0
                ? 1 
                : (int) Math.signum(increment);

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
        final var daysToAdd = Math.abs(days);

        for (long i = 0; i < daysToAdd; i++) {
            dateTime = nextWorkDate(dateTime, step);
        }

        return dateTime;
    }

    private ZonedDateTime addWorkMinutes(ZonedDateTime dateTime, long minutes) {
        final var step = Long.signum(minutes);
        final var minutesToAdd = Math.abs(minutes);

        for (long i = 0; i < minutesToAdd; i++) {
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
        final var isNightShift = startTime.isAfter(stopTime);
        if (isNightShift) {
            return !time.isBefore(startTime) || !time.isAfter(stopTime);
        }
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
