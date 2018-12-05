package sample;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import com.sun.javafx.binding.StringFormatter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javax.swing.text.View;
import java.awt.*;
import java.awt.event.InputMethodEvent;
import java.beans.EventHandler;
import java.net.SocketException;
import java.util.Date;
import java.util.List;


public class Controller
{
    @FXML
    Label x;
    @FXML
    Label y;
    @FXML
    Slider sensivitySlider;
    @FXML
    Label sensivityLabel;

    OSCPortIn in;
    Robot r;

    float Xleftover = 0, Yleftover = 0;

    @FXML
    public void initialize() throws SocketException, AWTException
    {
        r = new Robot();
        r.setAutoDelay(5);
        in = new OSCPortIn(12345);
        OSCListener listener = new OSCListener()
        {
            @Override
            public void acceptMessage(Date date, OSCMessage message)
            {
                List<Object> mess = message.getArguments();
                float xp = (float)mess.get(0);
                float yp = (float)mess.get(1);
                float dx = (float)(xp * sensivitySlider.getValue() + Xleftover);
                float dy = (float)(yp * sensivitySlider.getValue() + Yleftover);
                r.mouseMove(MouseInfo.getPointerInfo().getLocation().x - (int)dx, MouseInfo.getPointerInfo().getLocation().y - (int)dy);
                Xleftover = (float)dx - (int)dx;
                Yleftover = (float)dy - (int)dy;

                Platform.runLater(() ->
                {
                    x.setText("x: " + MouseInfo.getPointerInfo().getLocation().x);
                    y.setText("y: " + MouseInfo.getPointerInfo().getLocation().y);

                });
            }
        };
        in.addListener("/mouse/move", listener);
        in.startListening();

        sensivityLabel.setText(String.format("%.2f", sensivitySlider.getValue()));
        sensivitySlider.valueProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                sensivityLabel.setText(String.format("%.2f", newValue.doubleValue()));
            }
        });
    }



}
