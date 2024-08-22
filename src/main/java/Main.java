import org.eclipse.californium.core.CoapServer;
import processing.core.PApplet;
import controlP5.*;
import org.eclipse.californium.core.config.CoapConfig;
import org.eclipse.californium.elements.config.Configuration.DefinitionsProvider;
import org.eclipse.californium.elements.config.UdpConfig;
import resources.AccelerometerResource;
import resources.GyroscopeResource;
import view.AxisView;

import java.io.File;

public class Main extends PApplet {

    ControlP5 cp5;
    AccelerometerResource accResource;
    GyroscopeResource gyroResource;
    AxisView accView;
    AxisView gyroView;

    boolean receiveData = true;

    private static final File CONFIG_FILE = new File("Californium3.properties");
    private static final int DEFAULT_MAX_RESOURCE_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final int DEFAULT_BLOCK_SIZE = 512;

    static {
        CoapConfig.register();
        UdpConfig.register();
    }

    private static final DefinitionsProvider DEFAULTS = config -> {
        config.set(CoapConfig.MAX_RESOURCE_BODY_SIZE, DEFAULT_MAX_RESOURCE_SIZE);
        config.set(CoapConfig.MAX_MESSAGE_SIZE, DEFAULT_BLOCK_SIZE);
        config.set(CoapConfig.PREFERRED_BLOCK_SIZE, DEFAULT_BLOCK_SIZE);
    };

    public void settings() {
        size(980, 600);
    }

    public void setup() {
        cp5 = new ControlP5(this);
        smooth();
        surface.setTitle("Free Fall");

        cp5.addToggle("receiveData")
                .setPosition(750,10)
                .setSize(50,20)
                .setValue(true)
                .setMode(ControlP5.SWITCH)
                .setCaptionLabel("Receive Data")
                .setColorCaptionLabel(color(40))
        ;

        accResource = new AccelerometerResource("acc");
        accView = new AxisView(cp5, this, 0, "Accelerometer");

        gyroResource = new GyroscopeResource("gyro");
        gyroView = new AxisView(cp5, this, 300, "Gyroscope");

        CoapServer server = new CoapServer();
        server.add(accResource);
        server.add(gyroResource);
        server.start();
    }

    public void draw() {
        background(200, 200, 200);
        if (receiveData) {
            accView.pushData(accResource.getSensorData());
            gyroView.pushData(gyroResource.getSensorData());
        }
    }

    public static void main(String... args){
        PApplet.main(Main.class);
    }
}
