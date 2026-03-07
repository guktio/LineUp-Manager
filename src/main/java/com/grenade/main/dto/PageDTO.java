package com.grenade.main.dto;

import java.util.List;

public record PageDTO<D>(List<D> items, int page, int totalPages) {
    
}
