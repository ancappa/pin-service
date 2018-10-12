package it.tim.pin.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import it.tim.pin.integration.client.BMVClient;
import it.tim.pin.model.configuration.Constants;
import it.tim.pin.model.exception.GenericException;
import it.tim.pin.model.integration.PinReservationResponse;

@RunWith(MockitoJUnitRunner.class)
public class PinReservationServiceTest {


    @Mock
    private BMVClient bmvClient;


    private PinReservationService service;

    @Before
    public void init(){
        service = new PinReservationService(bmvClient);
    }

    @After
    public void cleanup(){
        Mockito.reset(bmvClient);
    }

    @Test(expected = GenericException.class)
    public void pinCodeReservationIncompleteResponse() {

    	PinReservationResponse reserve = service.pinCodeReservation(
                "3400000001",
                "10000000000",
                Constants.Subsystems.MYTIMAPP.toString(),
                new HttpHeaders()
        );

        Assert.assertNotNull(reserve);

    }
}