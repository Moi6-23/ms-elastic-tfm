package com.parking.parkingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Detail {

    @Field(type = FieldType.Integer)
    private Integer plazasLibres;

    @Field(type = FieldType.Text)
    private String decriptionPlace;

    @Field(type = FieldType.Object)
    private Coordenadas coordenadas;

    @Field(type = FieldType.Integer)
    private Integer cantidadPisos;
}
