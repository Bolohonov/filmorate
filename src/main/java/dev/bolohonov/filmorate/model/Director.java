package dev.bolohonov.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Director {

    private int id;
    @NotBlank
    private String name;

}