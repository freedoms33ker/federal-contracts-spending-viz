package com.fedspending.contracts.model.dto;

import java.util.List;

public record PagedResponse<T>(List<T> results, int page, int totalPages, boolean hasNext) {
}
