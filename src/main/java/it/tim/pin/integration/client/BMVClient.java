package it.tim.pin.integration.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import it.tim.pin.integration.FeignConfiguration;

@FeignClient(
        name="bmvPin",
        url = "${integration.soap.bmvbasepath}"
        , configuration = FeignConfiguration.class
)
public interface BMVClient {
    @PostMapping(value = "/${integration.soap.bmvvalue}",  produces = "application/xml", consumes = "application/xml")
    String callOBJ(@RequestBody String request );
}
