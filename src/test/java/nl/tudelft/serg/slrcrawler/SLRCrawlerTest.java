package nl.tudelft.serg.slrcrawler;

import nl.tudelft.serg.slrcrawler.library.Library;
import nl.tudelft.serg.slrcrawler.library.LibraryCrawler;
import nl.tudelft.serg.slrcrawler.library.LibraryParser;
import nl.tudelft.serg.slrcrawler.output.Outputter;
import nl.tudelft.serg.slrcrawler.storage.HtmlPageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SLRCrawlerTest {

    @Mock HtmlPageStorage storage;

    @Mock Library l1;
    @Mock Library l2;
    @Mock LibraryCrawler c1;
    @Mock LibraryCrawler c2;
    @Mock LibraryParser p1;
    @Mock LibraryParser p2;

    @Mock HtmlPage htmlc11;
    @Mock HtmlPage htmlc12;
    @Mock HtmlPage htmlc21;
    @Mock HtmlPage htmlc22;

    @Mock Outputter out;

    @Mock PaperEntry entry1;
    @Mock PaperEntry entry2;
    @Mock PaperEntry entry3;
    @Mock PaperEntry entry4;
    @Mock PaperEntry entry5;
    @Mock PaperEntry entry6;
    @Mock PaperEntry entry7;
    @Mock PaperEntry entry8;

    SLRCrawler slr;
    String keywords = "a b";
    int pages = 2;

    @BeforeEach
    void setup() {
        this.slr = new SLRCrawler(Arrays.asList(l1, l2), storage, out);
    }

    @Test void
    download_all_pages() throws PageNotFoundException {
        createLibraries();
        bothCrawlersWorkSuccessfully();
        bothParsersWorkSuccessfully();

        slr.collect(keywords, pages);

        Mockito.verify(c1, times(1)).downloadPage(keywords, 0);
        Mockito.verify(c1, times(1)).downloadPage(keywords, 1);
        Mockito.verify(c2, times(1)).downloadPage(keywords, 0);
        Mockito.verify(c2, times(1)).downloadPage(keywords, 1);
    }

    @Test void
    stores_everything() throws PageNotFoundException {
        createLibraries();
        bothCrawlersWorkSuccessfully();
        bothParsersWorkSuccessfully();

        slr.collect(keywords, pages);

        Mockito.verify(storage, times(1)).store(htmlc11);
        Mockito.verify(storage, times(1)).store(htmlc12);
        Mockito.verify(storage, times(1)).store(htmlc21);
        Mockito.verify(storage, times(1)).store(htmlc22);
    }

    @Test void
    outputs_everything() throws PageNotFoundException {
        createLibraries();
        bothCrawlersWorkSuccessfully();
        bothParsersWorkSuccessfully();

        slr.collect(keywords, pages);

        Mockito.verify(out, times(1)).write(entry1);
        Mockito.verify(out, times(1)).write(entry2);
        Mockito.verify(out, times(1)).write(entry3);
        Mockito.verify(out, times(1)).write(entry4);
        Mockito.verify(out, times(1)).write(entry5);
        Mockito.verify(out, times(1)).write(entry6);
        Mockito.verify(out, times(1)).write(entry7);
        Mockito.verify(out, times(1)).write(entry8);
    }

    @Test void
    do_not_stop_due_to_exceptions_in_parsing() throws PageNotFoundException {
        createLibraries();
        bothCrawlersWorkSuccessfully();
        aParserFails();

        slr.collect(keywords, pages);

        Mockito.verify(out, never()).write(entry1);
        Mockito.verify(out, never()).write(entry2);
        Mockito.verify(out, times(1)).write(entry3);
        Mockito.verify(out, times(1)).write(entry4);
        Mockito.verify(out, times(1)).write(entry5);
        Mockito.verify(out, times(1)).write(entry6);
        Mockito.verify(out, times(1)).write(entry7);
        Mockito.verify(out, times(1)).write(entry8);

    }

    @Test void
    do_not_stop_due_to_exceptions_in_crawling() throws PageNotFoundException {
        createLibraries();
        aCrawlerFails();
        parserWorksEvenThoughCrawlerFails();

        slr.collect(keywords, pages);

        Mockito.verify(storage, never()).store(htmlc11);
        Mockito.verify(p1, never()).parse(htmlc11);
    }

    private void parserWorksEvenThoughCrawlerFails() {
        when(p1.parse(htmlc12)).thenReturn(Arrays.asList(entry3, entry4));
        parser2works();
    }

    private void aCrawlerFails() throws PageNotFoundException {
        when(c1.downloadPage(keywords, 0)).thenThrow(new RuntimeException());
        when(c1.downloadPage(keywords, 1)).thenReturn(htmlc12);
        crawler2works();
    }


    private void bothParsersWorkSuccessfully() {
        parser1works();
        parser2works();
    }

    private void aParserFails() {
        when(p1.parse(htmlc11)).thenThrow(new RuntimeException());
        when(p1.parse(htmlc12)).thenReturn(Arrays.asList(entry3, entry4));

        parser2works();
    }


    private void bothCrawlersWorkSuccessfully() throws PageNotFoundException {
        crawler1works();
        crawler2works();
    }

    private void parser2works() {
        // parser 2 return paper entries
        when(p2.parse(htmlc21)).thenReturn(Arrays.asList(entry5, entry6));
        when(p2.parse(htmlc22)).thenReturn(Arrays.asList(entry7, entry8));
    }

    private void parser1works() {
        // parser 1 returns paper entries
        when(p1.parse(htmlc11)).thenReturn(Arrays.asList(entry1, entry2));
        when(p1.parse(htmlc12)).thenReturn(Arrays.asList(entry3, entry4));
    }

    private void crawler2works() throws PageNotFoundException {
        // crawler 2 is called two times and returns two different pages
        when(c2.downloadPage(keywords, 0)).thenReturn(htmlc21);
        when(c2.downloadPage(keywords, 1)).thenReturn(htmlc22);
    }

    private void crawler1works() throws PageNotFoundException {
        // crawler 1 is called two times and returns two different pages
        when(c1.downloadPage(keywords, 0)).thenReturn(htmlc11);
        when(c1.downloadPage(keywords, 1)).thenReturn(htmlc12);
    }

    private void createLibraries() {
        // set the two libraries, their crawlers and parsers
        when(l1.crawler()).thenReturn(c1);
        when(l1.parser()).thenReturn(p1);
        when(l2.crawler()).thenReturn(c2);
        when(l2.parser()).thenReturn(p2);
    }
}