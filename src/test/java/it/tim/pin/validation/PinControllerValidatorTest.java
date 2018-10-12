package it.tim.pin.validation;

import org.junit.Test;

import it.tim.pin.model.configuration.Constants;
import it.tim.pin.model.exception.BadRequestException;
import it.tim.pin.model.web.ReservationRequest;

/**
 * Created by alongo on 30/04/18.
 */
public class PinControllerValidatorTest {

    @Test
    public void validatePrivateConstructor() throws Exception {
        new PinControllerValidator();
    }

    @Test
    public void validateReservationRequestOk() throws Exception {
    	ReservationRequest request = new ReservationRequest(
    			"3400000001",
                "1111111111111111",
                Constants.Subsystems.MYTIMAPP.name());
    	
        PinControllerValidator.validateReservationRequest(request);
    }

    @Test(expected = BadRequestException.class)
    public void validateReservationRequestNoMSISDN() throws Exception {
    	
    	ReservationRequest request = new ReservationRequest(
    			null,
                "1111111111111111",
                Constants.Subsystems.MYTIMAPP.name());
    	
    	PinControllerValidator.validateReservationRequest(request);
    }


    @Test(expected = BadRequestException.class)
    public void validateReservationRequestNoSubSys() throws Exception {
    	ReservationRequest request = new ReservationRequest(
    			"3400000001",
                "1111111111111111",
                null);
    	
        PinControllerValidator.validateReservationRequest(request);
    }
    
    @Test(expected = BadRequestException.class)
    public void validateReservationRequestNoPinCode() throws Exception {
    	ReservationRequest request = new ReservationRequest(
    			"3400000001",
                null,
                Constants.Subsystems.MYTIMAPP.name());
    	
        PinControllerValidator.validateReservationRequest(request);
    }
}