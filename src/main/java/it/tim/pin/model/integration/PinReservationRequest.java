package it.tim.pin.model.integration;

import lombok.Data;

@Data
public class PinReservationRequest {

    private String msisdn;
    private String pinCode;
    private String userType;
    private String subSys;
    private String interactionDate;
	
	
}
