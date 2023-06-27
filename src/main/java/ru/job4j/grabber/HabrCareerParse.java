package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class HabrCareerParse implements Parse{

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-description__text");
        StringBuilder sb = new StringBuilder();
        for (Element row : rows) {
            for (Element el : row.children()) {
                sb.append(el.text()).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        return null;
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 5; i++) {
            String page = String.format("%s?page=%d", PAGE_LINK, i);
            System.out.println("Page " + i);
            Connection connection = Jsoup.connect(page);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateLinkElement = dateElement.child(0);
                String vacancyDate = dateLinkElement.attr("datetime");
                LocalDateTime dateTime = new HabrCareerDateTimeParser().parse(vacancyDate);
                System.out.printf("%s %s %s%n", vacancyName, link, dateTime);
                try {
                    System.out.println(HabrCareerParse.retrieveDescription(link));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}