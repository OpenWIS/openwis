package org.openwis.metadataportal.kernel.search.index.solr;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static final Pattern ISO_LOCAL_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern ISO_INSTANT = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z");
    private static final Pattern ISO_INSTANT_CORRUPTED = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");

    public static LocalDateTime parseDate(String dateString) throws ParseException{
        Matcher m = ISO_INSTANT_CORRUPTED.matcher(dateString);
        if (m.matches()) {
            dateString = String.format("%sZ", dateString);
        }

        DateTimeFormatter isoFormatter = DateUtil.getDateFormatter(dateString);
        if (isoFormatter == null) {
            throw new ParseException(String.format("Wrong date formate: %s", dateString), 0);
        }
        if (isoFormatter == DateTimeFormatter.ISO_LOCAL_DATE) {
            LocalDate dt = LocalDate.parse(dateString, isoFormatter);
            return LocalDateTime.of(dt, LocalTime.of(0,0,0));
        }
        Instant dateInstant = Instant.from(isoFormatter.parse(dateString));
        return LocalDateTime.ofInstant(dateInstant, ZoneId.of(ZoneOffset.UTC.getId()));

   }

   public static String format(LocalDateTime date) {
        return ZonedDateTime.of(date, ZoneId.of(ZoneOffset.UTC.getId())).format(DateTimeFormatter.ISO_INSTANT);
   }

    public static Date convertToDate(LocalDateTime dateToConvert) {
        return Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public static LocalDateTime convertToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static DateTimeFormatter getDateFormatter(String dateString) {
        Map<DateTimeFormatter, Pattern> patterns = new HashMap<>();

        patterns.put(DateTimeFormatter.ISO_INSTANT, DateUtil.ISO_INSTANT);
        patterns.put(DateTimeFormatter.ISO_LOCAL_DATE, DateUtil.ISO_LOCAL_DATE_PATTERN);

        for(Map.Entry<DateTimeFormatter, Pattern> entry: patterns.entrySet()) {
            Matcher m = entry.getValue().matcher(dateString);
                 if (m.matches()) {
                    return entry.getKey();
                }
            }
        return null;
    }
}
