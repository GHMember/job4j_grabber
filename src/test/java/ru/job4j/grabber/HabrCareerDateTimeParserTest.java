package ru.job4j.grabber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HabrCareerDateTimeParserTest {

    @Test
    void ParseTest() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String date = "2023-06-22T14:27:15+03:00";
        String expected = "2023-06-22T14:27:15";
        assertEquals(parser.parse(date).toString(), expected);
    }
}