package nl.tudelft.serg.slrcrawler.library.scholar;

import nl.tudelft.serg.slrcrawler.HtmlPage;
import nl.tudelft.serg.slrcrawler.PaperEntry;
import nl.tudelft.serg.slrcrawler.FileReader;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleScholarParserTest {

    @Test void
    parse_google_page() {

        HtmlPage htmlPage = htmlFrom("scholar-2020-jun-7.html");

        List<PaperEntry> entries = new GoogleScholarParser().parse(htmlPage);

        PaperEntry entry1 = new PaperEntry("Software systems as cities: A controlled experiment", "https://dl.acm.org/doi/abs/10.1145/1985793.1985868","R Wettel", 2011, 228);
        PaperEntry entry2 = new PaperEntry("A controlled experiment quantitatively comparing software development approaches", "https://ieeexplore.ieee.org/abstract/document/1702844/","VR Basili", 1981, 105);
        PaperEntry entry3 = new PaperEntry("Evaluating advantages of test driven development: a controlled experiment with professionals", "https://dl.acm.org/doi/abs/10.1145/1159733.1159788","G Canfora", 2006, 105);
        PaperEntry entry10 = new PaperEntry("Assessing the changeability of two object-oriented design alternatives--A controlled experiment", "https://link.springer.com/article/10.1023/A:1011439416657","E Arisholm", 2001, 56);

        assertThat(entries)
                .hasSize(10)
                .contains(entry1, entry2, entry3, entry10);
    }

    private HtmlPage htmlFrom(String fileName) {
        String html = readResource(fileName);
        return new HtmlPage("scholar", 1, "https://any-url.com", html);
    }

    private String readResource(String fileName) {
        try {
            URL url = getClass().getClassLoader().getResource(".");
            String filePath = url.getPath();

            return FileReader.readFile(Paths.get(filePath), fileName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
