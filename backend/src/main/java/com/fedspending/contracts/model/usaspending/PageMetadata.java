package com.fedspending.contracts.model.usaspending;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PageMetadata(int page, boolean hasNext) {
}
