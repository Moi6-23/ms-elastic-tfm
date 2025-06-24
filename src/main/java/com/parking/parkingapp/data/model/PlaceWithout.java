package com.parking.parkingapp.data.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document(indexName = "places")
public class PlaceWithout {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String parking;

    @Field(type = FieldType.Keyword)
    private String city;

    @Field(type = FieldType.Nested)
    private Detail detail;
}
