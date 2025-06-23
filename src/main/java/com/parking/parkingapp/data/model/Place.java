package com.parking.parkingapp.data.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.util.List;

@Document(indexName = "places", createIndex = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Place {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String parking;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Nested)
    private Detail detail;

    @Field(type = FieldType.Nested)
    private List<Plaza> plazas;
}
