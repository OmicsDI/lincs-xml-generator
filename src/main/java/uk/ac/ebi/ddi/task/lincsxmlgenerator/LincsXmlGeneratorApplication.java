package uk.ac.ebi.ddi.task.lincsxmlgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.ddi.api.readers.utils.Constants;
import uk.ac.ebi.ddi.api.readers.utils.Transformers;
import uk.ac.ebi.ddi.ddifileservice.services.IFileSystem;
import uk.ac.ebi.ddi.ddifileservice.type.ConvertibleOutputStream;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.configuration.LincsTaskProperties;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.model.Dataset;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.model.DatasetList;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.services.LincsService;
import uk.ac.ebi.ddi.xml.validator.parser.marshaller.OmicsDataMarshaller;
import uk.ac.ebi.ddi.xml.validator.parser.model.Database;
import uk.ac.ebi.ddi.xml.validator.parser.model.Entry;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class LincsXmlGeneratorApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LincsXmlGeneratorApplication.class);

    @Autowired
    private IFileSystem fileSystem;

    @Autowired
    private LincsService lincsService;

    @Autowired
    private LincsTaskProperties taskProperties;

	public static void main(String[] args) {
		SpringApplication.run(LincsXmlGeneratorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
        fileSystem.cleanDirectory(taskProperties.getOutputDir());
        AtomicInteger fileCount = new AtomicInteger(0);
        List<Entry> entries = new ArrayList<>();
        List<Dataset> datasets = lincsService.getAllDatasets();
        for (Dataset dataset : datasets) {
            try {
                Entry entry = Transformers.transformAPIDatasetToEntry(dataset);
                entries.add(entry);
                if (entries.size() % taskProperties.getEntriesPerFile() == 0) {
                    writeDatasetsToFile(entries, datasets.size(), fileCount.getAndIncrement());
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred when processing dataset {}, ", dataset.getIdentifier(), e);
            }
        }
        writeDatasetsToFile(entries, datasets.size(), fileCount.getAndIncrement());
	}

    private void writeDatasetsToFile(List<Entry> entries, int total, int fileCount) throws IOException {
        if (entries.size() < 1) {
            return;
        }

        String releaseDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        ConvertibleOutputStream outputStream = new ConvertibleOutputStream();
        try (Writer w = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            OmicsDataMarshaller mm = new OmicsDataMarshaller();

            Database database = new Database();
            database.setDescription(Constants.LINCS_DESCRIPTION);
            database.setName(Constants.LINCS);
            database.setRelease(releaseDate);
            database.setEntries(entries);
            database.setEntryCount(total);
            mm.marshall(database, w);
        }

        String filePath = taskProperties.getOutputDir() + "/" + taskProperties.getPrefix() + fileCount + ".xml";
        LOGGER.info("Attempting to write data to {}", filePath);
        fileSystem.saveFile(outputStream, filePath);
        entries.clear();
    }
}
