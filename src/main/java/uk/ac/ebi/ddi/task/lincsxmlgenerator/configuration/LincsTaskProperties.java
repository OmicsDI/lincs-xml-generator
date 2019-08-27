package uk.ac.ebi.ddi.task.lincsxmlgenerator.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("lincs")
public class LincsTaskProperties {

    private int entriesPerFile = 10;

    private String prefix = "Lincs-";

    private String outputDir;

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public int getEntriesPerFile() {
        return entriesPerFile;
    }

    public void setEntriesPerFile(int entriesPerFile) {
        this.entriesPerFile = entriesPerFile;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
