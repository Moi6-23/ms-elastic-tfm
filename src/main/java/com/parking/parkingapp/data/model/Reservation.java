package com.parking.parkingapp.data.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "reservations", createIndex = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String parkingId;

    @Field(type = FieldType.Keyword)
    private String plazaId;

    @Field(type = FieldType.Integer)
    private int pisoId;

    @Field(type = FieldType.Keyword)
    private String placa;

    @Field(type = FieldType.Keyword)
    private String userEmail;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
