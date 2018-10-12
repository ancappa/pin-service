package it.tim.pin.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.tim.pin.aspects.Loggable;
import it.tim.pin.model.integration.PinReservationResponse;
import it.tim.pin.model.web.ReservationRequest;
import it.tim.pin.model.web.ReservationResponse;
import it.tim.pin.service.PinReservationService;
import it.tim.pin.validation.PinControllerValidator;

/**
 * Created by alongo on 13/04/18.
 */
@RestController
@RequestMapping("/api")
@Api("Controller exposing pin operations")
public class PinController {

    private PinReservationService pinService;

    private static final DateTimeFormatter AUTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    @Autowired
    public PinController(PinReservationService pinService) {
        this.pinService = pinService;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/pinreservation" , produces = "application/json")
    @ApiOperation(value = "Refill operation with scratch card", response = ReservationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Refill success"),
            @ApiResponse(code = 400, message = "Missing or wrong mandatory parameters"),
            @ApiResponse(code = 404, message = "Wrong card number or card not found"),
            @ApiResponse(code = 401, message = "Not authorized due to max attempts reached"),
            @ApiResponse(code = 500, message = "Generic error"),
    })
    @Loggable
    public ReservationResponse pinreservation( @RequestBody ReservationRequest request, 
    		                                @RequestHeader HttpHeaders headers,
    		                                @RequestHeader(value = "businessID", required = false) String xBusinessId,    		
											@RequestHeader(value = "messageID", required = false) String xMessageID,    		
											@RequestHeader(value = "transactionID", required = false) String xTransactionID,    		
											@RequestHeader(value = "channel", required = false) String xChannel,    		
											@RequestHeader(value = "sourceSystem", required = false) String xSourceSystem   		
								    	  )
    {
    	
        PinControllerValidator.validateReservationRequest(request);
        
        PinReservationResponse pinResp = pinService.pinCodeReservation(request.getMsisdn(),request.getPinCode(),request.getSubSys(),headers);
        
        return new ReservationResponse(pinResp.getStatus(), pinResp.getEsito(), pinResp.getCausale(),pinResp.getInteractionDate());
        

    }

    public String composeHeaderDateTime(){
    	
        LocalDateTime bankAuthDate = LocalDateTime.now();
        String authDate = bankAuthDate.format(AUTH_DATE_FORMATTER);
        return authDate;
    }
}
