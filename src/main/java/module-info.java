module com.neo.twig {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires ini4j;
    requires json.simple;

    opens com.neo.twig to javafx.fxml;

    opens com.neo.twig.audio to ini4j;
    opens com.neo.twig.graphics to ini4j;

    exports com.neo.twig;
    exports com.neo.twig.annotations;
    exports com.neo.twig.input;
    exports com.neo.twig.audio;
    exports com.neo.twig.config;
    exports com.neo.twig.scene;
    exports com.neo.twig.graphics;
    exports com.neo.twig.logger;
    exports com.neo.twig.resources;
    exports com.neo.twig.ui;
}