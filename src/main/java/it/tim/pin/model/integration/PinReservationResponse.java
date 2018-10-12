package it.tim.pin.model.integration;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
public class PinReservationResponse {

	private String esito;
    private String causale;
    private String status;
    private String interactionDate;


}
