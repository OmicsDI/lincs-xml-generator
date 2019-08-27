package uk.ac.ebi.ddi.task.lincsxmlgenerator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.model.Dataset;
import uk.ac.ebi.ddi.task.lincsxmlgenerator.model.DatasetList;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
public class LincsService {

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Returns the Datasets from MtabolomeWorbench
     *
     * @return A list of entries and the facets included
     */
    public List<Dataset> getAllDatasets() throws IOException {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("lincsportal.ccs.miami.edu")
                .path("/dcic/api/fetchdata")
                .queryParam("searchTerm", "*")
                .queryParam("limit", "1000");
        URI uri = builder.build().encode().toUri();
        String res = restTemplate.getForEntity(uri, String.class).getBody();
        if (res != null) {
            DatasetList datasetList = mapper.readValue(res, DatasetList.class);
            return datasetList.getDatasets();
        }
        return Collections.emptyList();
    }

}
