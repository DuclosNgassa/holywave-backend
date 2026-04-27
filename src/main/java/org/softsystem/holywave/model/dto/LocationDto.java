package org.softsystem.holywave.model.dto;


public record LocationDto(Boolean online, Boolean onsite) {

    public LocationDto() {
        this(false, true);
    }

    public LocationDto {
        if (online == null) {
            online = false;
        }
        if (onsite == null) {
            onsite = true;
        }
    }
}
