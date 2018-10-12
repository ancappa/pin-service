package it.tim.pin.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import it.tim.pin.integration.client.BMVClient;
import it.tim.pin.service.PinReservationService;
import it.tim.pin.common.headers.TimHeaders;
import it.tim.pin.common.headers.TimSession;
import it.tim.pin.model.configuration.BuiltInConfiguration;
import it.tim.pin.model.configuration.Constants;
import it.tim.pin.model.exception.BadRequestException;
import it.tim.pin.model.exception.SubsystemException;
import it.tim.pin.model.web.ReservationRequest;
import it.tim.pin.web.PinController;

/**
 * Created by alongo on 30/04/18.
 */
@RunWith(MockitoJUnitRunner.class)
//Tested as in-service integration test
public class PinControllerTest {

    @Mock
    TimHeaders timHeaders;

    @Mock
    TimSession timHeadersSession;

    @Mock
    BuiltInConfiguration configuration;

    @Mock
    BMVClient bmvClient;

    PinReservationService pinService;
    PinController controller;

    @Before
    public void init(){
    
        
        pinService = new PinReservationService(bmvClient);
        controller = new PinController(pinService);
    }

    @After
    public void cleanup(){
        Mockito.reset(timHeaders, configuration);
        Mockito.reset(bmvClient);

    }



    @Test(expected = BadRequestException.class)
    public void pinCodeReservationKoOnInvalidRequest() throws Exception {
    	
        controller.pinreservation(new ReservationRequest(), null, null, null, null, null, null);
    }

  
}