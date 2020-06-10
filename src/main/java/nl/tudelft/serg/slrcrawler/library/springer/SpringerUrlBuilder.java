package nl.tudelft.serg.slrcrawler.library.springer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpringerUrlBuilder {

    public static String buildUrl(SpringerConfig config, String keywords, int zeroBasedPageNumber) {
        String url = String.format("https://link.springer.com/search/page/%d?query=%s",
                zeroBasedPageNumber + 1, /* page starts in one */
                encode(keywords));

        if(!config.getDiscipline().isEmpty())
            url = String.format("%s&facet-discipline=%s", url, encode(config.getDiscipline()));

        if(!config.getSubDiscipline().isEmpty())
            url = String.format("%s&facet-sub-discipline=%s", url, encode(config.getSubDiscipline()));

        if(!config.getContentType().isEmpty())
            url = String.format("%s&facet-content-type=%s", url, encode(config.getContentType()));

        return url;
    }

    private static String encode(String keywords) {
        return URLEncoder.encode(keywords, StandardCharsets.UTF_8);
    }

}
