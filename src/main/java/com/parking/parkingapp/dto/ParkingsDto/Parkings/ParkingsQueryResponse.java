package com.parking.parkingapp.dto.ParkingsDto.Parkings;
import com.parking.parkingapp.data.model.Places;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParkingsQueryResponse {

    private List<Places> parkings;

}
