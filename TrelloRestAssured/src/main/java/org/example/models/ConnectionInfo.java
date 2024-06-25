package org.example.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionInfo {
    @JsonProperty
    List<String> idBoards;

    public List<String> getIdBoards() {
        return idBoards;
    }

    public void setIdBoard(List<String> idBoard) {
        this.idBoards = idBoard;
    }
}
