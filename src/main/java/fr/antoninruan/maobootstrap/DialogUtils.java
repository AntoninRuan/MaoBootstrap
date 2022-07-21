package fr.antoninruan.maobootstrap;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

public class DialogUtils {

    public static Pair<ProgressBar, Label> loadingInfo(Stage primaryStage) {

        Stage stage = new Stage();
        stage.setTitle("Recherche de mise à jour");
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
//        stage.getIcons().add(MainApp.ICON);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setResizable(false);
        stage.setOnCloseRequest(Event::consume);

        VBox vBox = new VBox();
        vBox.setSpacing(5d);
        vBox.setPrefSize(375, 50);
        vBox.setPadding(new Insets(5));
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);

        vBox.getStyleClass().add("vbox");
        vBox.getStylesheets().add(DialogUtils.class.getClassLoader().getResource("style/download-progress.css").toString());

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(Double.MAX_VALUE);

        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1) {
                try {
//                    Thread.sleep(100);
                    Platform.runLater(stage::hide);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Label label = new Label("Début du chargement des emotes");
        vBox.getChildren().addAll(progressBar, label);

        // Set the max status
        int maxStatus = 12;
        // Create the Property that holds the current status count
        IntegerProperty statusCountProperty = new SimpleIntegerProperty(1);
        // Create the timeline that loops the statusCount till the maxStatus
        Timeline timelineBar = new Timeline(
                new KeyFrame(
                        // Set this value for the speed of the animation
                        Duration.millis(1000),
                        new KeyValue(statusCountProperty, maxStatus)
                )
        );
        // The animation should be infinite
        timelineBar.setCycleCount(Timeline.INDEFINITE);
        timelineBar.play();
        // Add a listener to the statusproperty
        statusCountProperty.addListener((ov, statusOld, statusNewNumber) -> {
            int statusNew = statusNewNumber.intValue();
            // Remove old status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusOld.intValue()), false);
            // Add current status pseudo from progress-bar
            progressBar.pseudoClassStateChanged(PseudoClass.getPseudoClass("status" + statusNew), true);
        });

        Scene scene = new Scene(vBox);
        stage.setScene(scene);

        stage.show();

        return new Pair<>(progressBar, label);
    }

}
