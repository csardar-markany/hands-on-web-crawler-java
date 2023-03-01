package com.crsardar.java.webcroler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Main {

    private static final int MAX_DEPTH = 2;

    private HashSet<String> urlLinks;
    private List<List<String>> articles;

    public Main() {
        urlLinks = new HashSet<>();
        articles = new ArrayList<>();
    }

    public void getPageLinks(String URL, int depth) {

        if (urlLinks.size() != 50 && !urlLinks.contains(URL) && (depth < MAX_DEPTH)
//                && (URL.startsWith("http://www.javatpoint.com") || URL.startsWith("https://www.javatpoint.com"))
        ) {

            System.out.println(">> Depth: " + depth + " [" + URL + "]");

            try {
                urlLinks.add(URL);
                Document document = Jsoup.connect(URL).get();
                Elements availableLinksOnPage = document.select("a[href]");
                depth++;

                for (Element element : availableLinksOnPage) {
//                    if (element.attr("abs:href").startsWith("http://www.javatpoint.com")
//                            || element.attr("abs:href").startsWith("https://www.javatpoint.com")) {
                    getPageLinks(element.attr("abs:href"), depth);
//                    }
                }
            } catch (IOException e) {
                System.err.println("Error for '" + URL + "': " + e.getMessage());
            }
        }
    }

    public void getArticles() {
        Iterator<String> i = urlLinks.iterator();
        while (i.hasNext()) {
            Document document;
            try {
                final String url = i.next();
                document = Jsoup.connect(url).get();
                Elements elements = document.select("a[href]");
                for (Element element : elements) {
                    System.out.println(element.text());
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(element.text());
                    temp.add(element.attr("abs:href"));
                    articles.add(temp);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void writeToFile(String fName) {

        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(fName);
            for (int i = 0; i < articles.size(); i++) {
                try {
                    String article = "- Title: " + articles.get(i).get(0) + " (link: " + articles.get(i).get(1) + ")\n";

                    System.out.println(article);
                    fileWriter.write(article);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        Main obj = new Main();

        obj.getPageLinks("http://www.javatpoint.com", 0);

        obj.getArticles();
        obj.writeToFile("web-crawler-example.txt");
    }
}
