package view;

import controlP5.Chart;
import controlP5.ControlP5;
import controlP5.Textlabel;
import models.SensorData;
import processing.core.PApplet;
import processing.core.PFont;

public class AxisView {
    ControlP5 instanceP5;
    PApplet applet;
    String name;

    Chart xChart;
    Chart yChart;
    Chart zChart;

    Textlabel label;

    public AxisView(ControlP5 instanceP5, PApplet applet, int y_offset, String name) {
        this.instanceP5 = instanceP5;
        this.applet = applet;
        this.name = name;

        label = instanceP5.addTextlabel(name)
                .setPosition(20, 20 + y_offset)
                .setSize(200, 20)
                .setText(name)
                .setColor(applet.color(40))
        ;

        xChart = instanceP5.addChart("X " + name + " axis")
                .setPosition(20, 50 + y_offset)
                .setSize(300, 150)
                .setRange(-20, 20)
                .setView(Chart.LINE)
                .setStrokeWeight(3)
                .setColorCaptionLabel(applet.color(40))
        ;

        yChart = instanceP5.addChart("Y " + name + " axis")
                .setPosition(340, 50 + y_offset)
                .setSize(300, 150)
                .setRange(-20, 20)
                .setView(Chart.LINE)
                .setStrokeWeight(2)
                .setColorCaptionLabel(applet.color(40))
        ;

        zChart = instanceP5.addChart("Z " + name + " axis")
                .setPosition(660, 50 + y_offset)
                .setSize(300, 150)
                .setRange(-20, 20)
                .setView(Chart.LINE)
                .setStrokeWeight(2)
                .setColorCaptionLabel(applet.color(40))
        ;

        xChart.addDataSet("x_data_" + name);
        xChart.setData("x_data_" + name, new float[100]);
        xChart.getCaptionLabel().setSize(13);

        yChart.addDataSet("y_data_" + name);
        yChart.setData("y_data_" + name, new float[100]);
        yChart.getCaptionLabel().setSize(13);

        zChart.addDataSet("z_data_" + name);
        zChart.setData("z_data_" + name, new float[100]);
        zChart.getCaptionLabel().setSize(13);

        label.getValueLabel().setSize(20);
    }

    public void pushData(SensorData data) {
        xChart.push("x_data_" + name, data.getX());
        yChart.push("y_data_" + name, data.getY());
        zChart.push("z_data_" + name, data.getZ());
    }
}
