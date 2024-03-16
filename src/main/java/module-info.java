module com.innov8.robusdrawer {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires java.logging;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;
    requires batik.util;
    requires batik.parser;
    requires batik.anim;
    requires com.fazecast.jSerialComm;

    opens com.innov8.robusdrawer to javafx.fxml;
    exports com.innov8.robusdrawer;
    exports com.innov8.robusdrawer.ressource;
    opens com.innov8.robusdrawer.ressource to javafx.fxml;

    opens com.innov8.robusdrawer.maze to javafx.fxml;
    exports com.innov8.robusdrawer.maze;
    exports com.innov8.robusdrawer.draw;
}