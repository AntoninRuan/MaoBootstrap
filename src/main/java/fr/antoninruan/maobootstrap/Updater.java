package fr.antoninruan.maobootstrap;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.update4j.Archive;
import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.UpdateOptions;
import org.update4j.inject.InjectSource;
import org.update4j.inject.Injectable;
import org.update4j.service.UpdateHandler;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Updater implements UpdateHandler, Injectable {

    @InjectSource
    private Stage primaryStage;
    private Configuration config;

    private Pair<ProgressBar, Label> downloadInfo;

    public Updater(Configuration config, Stage primaryStage) {
        this.config = config;
        this.primaryStage = primaryStage;
        this.initUpdater();
    }

    private void initUpdater() {
        Task<Boolean> checkUpdates = checkUpdates();

        downloadInfo = DialogUtils.loadingInfo(primaryStage);
        downloadInfo.getValue().setText("Recherche de mise à jour");

        checkUpdates.setOnSucceeded(event -> {
            Thread run = new Thread(() -> {
                System.out.println("Launching main app");
               config.launch(this);
            });

            if (checkUpdates.getValue()) {
                Task<Void> doUpdates = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Path zip = Paths.get("temp.zip");
                        if (config.update(UpdateOptions.archive(zip).updateHandler(Updater.this)).getException() == null) {
//                            System.out.println("installé");
                            Archive.read(zip).install();
                            run.start();
                        }
                        return null;
                    }
                };

                run(doUpdates);
            } else {
                downloadInfo.getKey().setProgress(1);
                run.start();
            }


        });

        run(checkUpdates);

    }

    private Task<Boolean> checkUpdates() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return config.requiresUpdate();
            }
        };
    }

    private void run(Runnable runnable) {
//        Thread runner = new Thread(runnable);
////        runner.setDaemon(true);
//        runner.start();
        runnable.run();
    }

    @Override
    public void updateDownloadFileProgress(FileMetadata file, float frac) throws Throwable {
        Platform.runLater(() -> {
            downloadInfo.getValue().setText("Downloading " + file.getPath().getFileName());
        });
    }

    @Override
    public void updateDownloadProgress(float frac) throws Throwable {
        Platform.runLater(() -> {
            downloadInfo.getKey().setProgress(frac);
        });
    }

    @Override
    public void failed(Throwable t) {
        System.out.println(t.getMessage());
    }

    @Override
    public void succeeded() {
        System.out.println("Succès");
    }

    @Override
    public void stop() {
        System.out.println("Arrêt");
    }
}
