package it.tim.pin.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import it.tim.pin.integration.client.BMVClient;
import it.tim.pin.model.exception.GenericException;
import it.tim.pin.model.integration.PinReservationRequest;
import it.tim.pin.model.integration.PinReservationResponse;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class PinReservationService {

    private static final DateTimeFormatter AUTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String USER_TYPE = "prepagato";
    private static final String BMV_OK_CODE ="BMV_VPC000";
    private BMVClient bmvClient;
    
    @Autowired
    public PinReservationService( BMVClient bmvClient) {
        this.bmvClient = bmvClient;
    }

    public PinReservationResponse pinCodeReservation(String toMsisdn, String pinCode, String subSys, HttpHeaders headers){

        // Chiamata al servizio callPagamS2S per il blocco plafond

    	LocalDateTime interactionDate = LocalDateTime.now();
        PinReservationRequest pinReservReq = new PinReservationRequest();
        pinReservReq.setMsisdn(toMsisdn);
        pinReservReq.setPinCode(pinCode);
        pinReservReq.setSubSys(subSys);
        pinReservReq.setUserType(USER_TYPE);
        
        pinReservReq.setInteractionDate(interactionDate.format(AUTH_DATE_FORMATTER));

        PinReservationResponse pinResp = initiativeCodeMgt(pinReservReq,headers);

        return pinResp;
    }


    
	private PinReservationResponse initiativeCodeMgt(PinReservationRequest pinRequest, HttpHeaders headers){
		
		PinReservationResponse resp = new PinReservationResponse();
		resp.setStatus("KO");

		try {
			
			String bmvReq = getOBJRequest(pinRequest, headers);
			log.info("------------------------------ BMV REQ: " + bmvReq);
			
			String bmvResponse = bmvClient.callOBJ(bmvReq);

			log.info("------------------------------ BMV REPONSE: " + bmvResponse);
			
			String esito = getTagValue(bmvResponse, "ns:returnCode" , "-1");
			log.info("esito from BMV = " + esito);
			
			String causale = getTagValue(bmvResponse, "ns:returnDescription" , "-1");
			log.info("causale from BMV = " + causale);
			
			if (esito!=null && esito.equals(BMV_OK_CODE)) resp.setStatus("OK");

			resp.setCausale(causale);
			resp.setEsito(esito);
			
			
	        StringBuilder interactionDate = new StringBuilder(pinRequest.getInteractionDate().substring(8, 10)).append("-")
	        										.append(pinRequest.getInteractionDate().substring(5, 7)).append("-")
	        										.append(pinRequest.getInteractionDate().substring(0, 4)).append(" ")
													.append((pinRequest.getInteractionDate().substring(11))).append(".000");
			
	        
	        resp.setInteractionDate(interactionDate.toString());
			return resp;
		}
		catch(Exception ex) {
			log.error("BMV EXC " + ex);
			throw new GenericException("Incomplete response reveived by service 'prepagatoMobile/offerte'");
		}

	}


	private String getOBJRequest(PinReservationRequest pinRequest,HttpHeaders headers ) {
		
 
        
		StringBuilder buff = new StringBuilder();

		buff.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		buff.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://telecomitalia.it/SOA/SOAP/SOAPHeader\" xmlns:ns=\"http://telecomitalia.it/SOA/InitiativeCodeMgmt/2015-05-11\" xmlns:ns1=\"http://telecomitalia.it/SOA/InitiativeCodeMgmtCustomTypes/2015-05-11\"> ");
		buff.append("<soapenv:Header> ");
		buff.append("<soap:Header> ");
		buff.append("<soap:sourceSystem>").append(headers.getFirst("sourceSystem")).append("</soap:sourceSystem> ");
		buff.append("<soap:interactionDate> ");
		buff.append("<soap:Date>").append(pinRequest.getInteractionDate().substring(0, 10)).append("</soap:Date> ");
		buff.append("<soap:Time>").append(pinRequest.getInteractionDate().substring(11)).append("</soap:Time> ");
		buff.append("</soap:interactionDate> ");
		buff.append("<soap:businessID>").append(headers.getFirst("businessID")).append("</soap:businessID> ");
		buff.append("<soap:messageID>").append(headers.getFirst("messageID")).append("</soap:messageID> ");
		buff.append("<soap:transactionID>").append(headers.getFirst("transactionID")).append("</soap:transactionID> ");
		buff.append("</soap:Header> ");
		buff.append("</soapenv:Header> ");
		buff.append("<soapenv:Body> ");
		buff.append("<ns:checkPinCodeReservationRequest> ");
		buff.append("<ns:CustomerOrder> ");
		buff.append("<ns1:CustomerOrderItem> ");
		buff.append("<ns1:ProductBundle> ");
		buff.append("<ns1:ProductCharacteristicValue> ");
		buff.append("<ns1:value>").append(pinRequest.getMsisdn()).append("</ns1:value> ");
		buff.append("<ns1:ProductSpecCharacteristic> ");
		buff.append("<ns1:name>ServiceNumber</ns1:name> ");
		buff.append("</ns1:ProductSpecCharacteristic> "); 
		buff.append("</ns1:ProductCharacteristicValue> ");
		buff.append("<ns1:ProductCharacteristicValue> ");
	    buff.append("<ns1:value>").append(pinRequest.getPinCode()).append("</ns1:value> ");
	    buff.append("<ns1:ProductSpecCharacteristic> ");
	    buff.append("<ns1:name>InitiativePinCode</ns1:name> ");
	    buff.append("</ns1:ProductSpecCharacteristic> ");
	    buff.append("</ns1:ProductCharacteristicValue> ");
	    buff.append("<ns1:ProductCharacteristicValue> ");
	    buff.append("<ns1:value>prepagato</ns1:value> ");
	    buff.append("<ns1:ProductSpecCharacteristic> ");
	    buff.append("<ns1:name>BillingProfileType</ns1:name> ");
	    buff.append("</ns1:ProductSpecCharacteristic> ");
	    buff.append("</ns1:ProductCharacteristicValue> ");
	    buff.append("</ns1:ProductBundle> ");
	    buff.append("<ns1:CharacteristicValue> ");
	    buff.append("<ns1:value>").append(pinRequest.getSubSys()).append("</ns1:value> ");
	    buff.append("<ns1:CharacteristicSpecification> ");
	    buff.append("<ns1:name>Subsys</ns1:name> ");
	    buff.append("</ns1:CharacteristicSpecification> ");
	    buff.append("</ns1:CharacteristicValue> ");
	    buff.append("</ns1:CustomerOrderItem> ");
	    buff.append("</ns:CustomerOrder> ");
	    buff.append("</ns:checkPinCodeReservationRequest> ");
	    buff.append("</soapenv:Body> ");
	    buff.append("</soapenv:Envelope> ");

		return buff.toString();
	}

	
	
	
	private static String getTagValue(String resp, String tag, String defaultVal ) {
		String tagValue = defaultVal;
		
		String tag1 = "<"+tag+">";
		String tag2 = "</"+tag+">";
		
		int idx1 = resp.indexOf(tag1);
		int idx2 = resp.indexOf(tag2);
		
		if(idx1>0 && idx2>0) {
			tagValue = resp.substring(idx1 + tag1.length(),idx2).trim();
		}
		
		return tagValue;
	}
	
	
	
	public static void main(String[] args) {
		String anno = "2021";
		String year = anno.substring(2);
		System.out.println("year = " + year);
		
		String objResp = "<TransactionType>PAGAM</TransactionType><TransactionResult>KO</TransactionResult><ShopTransactionID>1234</ShopTransactionID><BankTransactionID>5678900000</BankTransactionID><AuthorizationCode>"
				+ "</AuthorizationCode><Currency></Currency><Amount></Amount><Country></Country><Buyer><BuyerName></BuyerName><BuyerEmail></BuyerEmail></Buyer><CustomInfo></CustomInfo><ErrorCode>1125</ErrorCode><ErrorDescription>Anno di scadenza non valido</ErrorDescription><AlertCode></AlertCode><AlertDescription></AlertDescription><TransactionKey>196704321</TransactionKey><VbV><VbVFlag></VbVFlag><VbVBuyer>KO</VbVBuyer><VbVRisp></VbVRisp></VbV><TOKEN></TOKEN><TokenExpiryMonth></TokenExpiryMonth><TokenExpiryYear></TokenExpiryYear></GestPayS2S></callPagamS2SResult></callPagamS2SResponse></S:Body></S:Envelope>";

		
		String erroCode = getTagValue(objResp, "ErrorCode" , "-1");
		System.out.println("erroCode = " + erroCode);
		
		String shopTransactionID = getTagValue(objResp, "ShopTransactionID" , "");
		System.out.println("shopTransactionID = " + shopTransactionID);
		
		String bankTransactionID = getTagValue(objResp, "BankTransactionID" , "");
		System.out.println("bankTransactionID = " + bankTransactionID);
		
		LocalDateTime bankAuthDate = LocalDateTime.now();
		String authDate = bankAuthDate.format(AUTH_DATE_FORMATTER);
		
		System.out.println("authDate = " + authDate);
		
		

		String debit = "25.00000000000";
		int idx = debit.indexOf(".");
		String db1 = debit.substring(0,idx+3);
		System.out.println("db1= " + db1);
		
		
		UUID transaction = UUID.randomUUID();
    	String tid = transaction.toString();
    	System.out.println("tid="+tid);
		
    	SecureRandom random = new SecureRandom();
    	byte bytes[] = new byte[16];
    	random.nextBytes(bytes);
    	Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    	String token = encoder.encodeToString(bytes);
    	System.out.println(token);
		
	}
    
}
