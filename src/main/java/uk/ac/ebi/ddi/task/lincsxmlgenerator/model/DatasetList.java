package uk.ac.ebi.ddi.task.lincsxmlgenerator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * ==Overview==
 * <p>
 * This class
 * <p>
 * Created by ypriverol (ypriverol@gmail.com) on 25/01/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetList {

    @JsonProperty("results")
    private
    Results results;

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public int getTotal() {
        if (results != null && results.getDatasets() != null) {
            return results.getTotal();
        }
        return -1;
    }

    public List<Dataset> getDatasets() {
        if (results != null && results.getDatasets() != null) {
            return Arrays.asList(results.getDatasets());
        }
        return new ArrayList<>();
    }
}
