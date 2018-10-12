package it.tim.pin.model.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by alongo on 30/04/18.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {

    @ApiModelProperty(notes = "Utente utilizzatore per cui prenotare il codice PIN", required = true)
    private String msisdn;
    @ApiModelProperty(notes = "Codice PIN da verificare-pronotare", required = true)
    private String pinCode;
    @ApiModelProperty(notes = "Canale da cui proviene la richiesta", required = true)
    private String subSys;
}
