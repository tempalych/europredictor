package com.tempalych.europredictor.utils;

import java.time.*;

public class TimeUtils {
    public static LocalDateTime getTimeAtTimezone(LocalDateTime time, String timeZone) {
        OffsetDateTime timeUtc = time.atOffset(ZoneOffset.UTC);
        ZoneId zone;
        try {
            zone = ZoneId.of(timeZone);
        } catch (Exception ignore) {
            zone = ZoneId.of("UTC");
        }
        var timeZoneOffset = zone.getRules().getOffset(time.toInstant(ZoneOffset.UTC));
        OffsetDateTime offsetTime = timeUtc.withOffsetSameInstant(timeZoneOffset);
        return offsetTime.toLocalDateTime();
    }
}
