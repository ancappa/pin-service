package it.tim.pin.validation;

import java.util.function.Predicate;

import it.tim.pin.model.exception.BadRequestException;
import it.tim.pin.model.web.ReservationRequest;

/**
 * Created by alongo on 30/04/18.
 */
public class PinControllerValidator {

    PinControllerValidator() {}

    public static void validateReservationRequest(ReservationRequest request) {

        boolean valid = validateStrings(CommonValidators.validPhoneNumber, request.getMsisdn())
                && request.getPinCode()!=null
                && request.getSubSys()!=null;
        
        if(!valid)
            throw new BadRequestException("Missing/Wrong parameters in ReservationRequest");

    }

    //UTIL

    private static boolean validateStrings(Predicate<String> predicate, String... strings){
        for(String s : strings){
            if(!predicate.test(s))
                return false;
        }
        return true;
    }

}
