package com.parking.parkingapp.dto.Parkings;
import com.parking.parkingapp.data.model.PlaceWithout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParkingsWithoutResponse {

    private List<PlaceWithout> parkings;

}
