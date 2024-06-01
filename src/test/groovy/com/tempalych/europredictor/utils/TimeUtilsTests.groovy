package com.tempalych.europredictor.utils

import spock.lang.Specification

import java.time.LocalDateTime
import java.time.ZoneId

class TimeUtilsTests extends Specification {

    def "Time at #timeZone is #result"() {
        setup:
        def time = LocalDateTime
                .of(2024, 6, 1, 15, 51, 00)
                .atZone(ZoneId.of("UTC")).toLocalDateTime() // 2024-06-01 15:51:00

        expect:
        TimeUtils.getTimeAtTimezone(time, timeZone) == result

        where:
        timeZone           || result
        "America/Chicago"  || LocalDateTime.of(2024, 6, 1, 10, 51, 00)
        "America/St_Johns" || LocalDateTime.of(2024, 6, 1, 13, 21, 00)
        "UTC"              || LocalDateTime.of(2024, 6, 1, 15, 51, 00)
        "Europe/Lisbon"    || LocalDateTime.of(2024, 6, 1, 16, 51, 00)
        "Europe/Podgorica" || LocalDateTime.of(2024, 6, 1, 17, 51, 00)
        "Europe/Moscow"    || LocalDateTime.of(2024, 6, 1, 18, 51, 00)
        "Blah/Blah"        || LocalDateTime.of(2024, 6, 1, 15, 51, 00) // default UTC
        ""                 || LocalDateTime.of(2024, 6, 1, 15, 51, 00) // default UTC
        null               || LocalDateTime.of(2024, 6, 1, 15, 51, 00) // default UTC
    }
}
