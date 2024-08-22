package resources;

import models.SensorData;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

public abstract class AbstractResource extends CoapResource {

    SensorData sensorData;
    String name;

    public AbstractResource(String name) {
        super(name);
        this.name = name;
        sensorData = new SensorData(0,0,0);
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        String data = exchange.getRequestText();
        System.out.println( this.name + ": " + exchange.getRequestText());

        if (data.charAt(0) == '[') {
            data = data.replaceAll("[\\[\\]]","");
            String[] values = data.split(",");
            this.sensorData = new SensorData(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
        }

        exchange.respond(CoAP.ResponseCode.CREATED);
    }

    public SensorData getSensorData() {
        return this.sensorData;
    }
}
