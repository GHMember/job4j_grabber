package ru.job4j.grabber;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        return LocalDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(parse));
    }
}