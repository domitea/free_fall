package resources;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class SensorResource extends CoapResource {

    public SensorResource(String name) {
        super(name);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        System.out.println(exchange.getRequestText());

        exchange.respond(CoAP.ResponseCode.CREATED);
    }
}
