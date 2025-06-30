package com.parking.parkingapp.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Details {

    @Field(type = FieldType.Integer)
    private Integer availableSpots;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Double)
    private List<Double> position;

    @Field(type = FieldType.Integer)
    private Integer totalFloors;
}
