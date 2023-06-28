package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

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
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String page = String.format("%s?page=%d", link, i);
            System.out.println("Parsing page " + i);
            Connection connection = Jsoup.connect(page);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateLinkElement = dateElement.child(0);
                String vacancyDate = dateLinkElement.attr("datetime");
                LocalDateTime dateTime = dateTimeParser.parse(vacancyDate);
                String vacancyDescription = HabrCareerParse.retrieveDescription(vacancyLink);
                posts.add(new Post(vacancyName, vacancyLink, vacancyDescription, dateTime));
            }
        }
        System.out.println("Parsing finished" + System.lineSeparator());
        return posts;
    }

    public static void main(String[] args) throws IOException {
        DateTimeParser dateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse parser = new HabrCareerParse(dateTimeParser);
        String pageLink = String.format("%s/vacancies/java_developer", SOURCE_LINK);
        List<Post> posts = parser.list(pageLink);
        for (Post post : posts) {
            System.out.println(post.getDescription());
        }
    }
}