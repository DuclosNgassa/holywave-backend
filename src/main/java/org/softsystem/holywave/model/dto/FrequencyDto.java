package org.softsystem.holywave.model.dto;


public record FrequencyDto(Boolean daily, Boolean weekly, Boolean monthly, Boolean yearly) {

    public FrequencyDto() {
        this(false, false, false, false);
    }

    public FrequencyDto {
        if (daily == null) {
            daily = false;
        }
        if (weekly == null) {
            weekly = false;
        }
        if (monthly == null) {
            monthly = false;
        }
        if (yearly == null) {
            yearly = false;
        }
    }
}
