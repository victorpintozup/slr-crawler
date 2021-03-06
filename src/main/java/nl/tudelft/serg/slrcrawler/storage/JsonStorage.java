package nl.tudelft.serg.slrcrawler.storage;

import com.google.gson.Gson;
import nl.tudelft.serg.slrcrawler.HtmlPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class JsonStorage implements HtmlPageStorage {

    private static final Logger logger = LogManager.getLogger(JsonStorage.class);

    private final String directory;
    private final FileNamer fileNamer;

    public JsonStorage(FileNamer fileNamer, String directory) {
        this.fileNamer = fileNamer;
        this.directory = directory;
    }

    public JsonStorage(String directory) {
        this(new FileNamer(), directory);
    }

    @Override
    public void store(HtmlPage htmlPage) {
        Gson gson = new Gson();
        String jsonObject = gson.toJson(htmlPage);

        try (PrintWriter pw = new PrintWriter(Paths.get(directory, fileNamer.name(htmlPage, "json")).toFile())) {
            pw.print(jsonObject);
        } catch (FileNotFoundException e) {
            logger.error("Fail when persisting html page in disk", e);
        }
    }

}