package com.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuantityInputDTO {

    @Valid
    @NotNull(message = "thisQuantityDTO must not be null")
    private QuantityDTO thisQuantityDTO;

    @Valid
    @NotNull(message = "thatQuantityDTO must not be null")
    private QuantityDTO thatQuantityDTO;
}