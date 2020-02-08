package com.github.rxyor.plugin.pom.assistant.common.jsoup.parse;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.idea.maven.model.MavenId;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2020/2/9 周日 00:24:00
 * @since 1.0.0
 */
public class ListVersionHelper {


    private final static String SEARCH_URL = "https://mvnrepository.com/artifact/%s/%s";

    private ListVersionHelper() {
    }


    public static List<MavenId> list(String groupId, String artifactId) {
        if (StringUtils.isBlank(groupId) || StringUtils.isBlank(artifactId)) {
            return new ArrayList<>(0);
        }

        Document document = connect(String.format(SEARCH_URL, groupId.trim(), artifactId.trim()));
        Elements elements = document.getElementsByClass("vbtn release");
        String text = Jsoup.parse(elements.text()).text();
        List<String> versions = Splitter.on(" ").trimResults().omitEmptyStrings().splitToList(text);
        return versions.stream().map((Function<String, MavenId>) v -> new MavenId(groupId, artifactId, v))
            .collect(Collectors.toList());
    }

    private static Document connect(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            throw new RuntimeException("request url:[" + url + "] fail, error:" + e.getMessage(), e);
        }
    }

}
