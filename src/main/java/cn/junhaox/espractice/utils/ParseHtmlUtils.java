package cn.junhaox.espractice.utils;

import cn.junhaox.espractice.entity.Content;
import org.elasticsearch.common.recycler.Recycler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author WJH
 * @Description
 * @date 2020/11/10 15:25
 * @Email ibytecode2020@gmail.com
 */
@Component
public class ParseHtmlUtils {


    public static void main(String[] args) throws Exception {
        new ParseHtmlUtils().parseJDHtml("多线程").forEach(System.out::println);
    }

    public List<Content> parseJDHtml(String keyword) throws Exception {
        String url = "https://search.jd.com/Search?keyword="
                + URLEncoder.encode(keyword, StandardCharsets.UTF_8.name())
                + "&page=" + new Random().nextInt(100);
                ;

        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("J_goodsList");

        Elements elements = element.getElementsByTag("li");

        List<Content> contentList = new ArrayList<>();
        elements.forEach(ele -> {
            String img = ele.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = ele.getElementsByClass("p-price").eq(0).text();
            String title = ele.getElementsByClass("p-name").eq(0).text();
            contentList.add(new Content(new Random().nextInt(1000), title, price, img));
        });

        return contentList;

    }




}
