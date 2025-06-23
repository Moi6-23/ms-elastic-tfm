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
public class Plaza {

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Integer)
    private Integer numeroPLaza;

    @Field(type = FieldType.Boolean)
    private Boolean ocupado;

    @Field(type = FieldType.Keyword)
    private String pisoId;

    @Field(type = FieldType.Integer)
    private Integer piso;
}