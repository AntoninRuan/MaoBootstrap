package fr.antoninruan.maobootstrap;

import javafx.application.Application;
import javafx.stage.Stage;
import org.update4j.Configuration;

import java.io.InputStreamReader;
import java.net.URL;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new URL("https://antonin-ruan.fr/Mao/config.xml");
        Configuration config = null;
        try (InputStreamReader in = new InputStreamReader(url.openStream())) {
            config = Configuration.read(in);
        }

        new Updater(config, primaryStage);

    }
}
