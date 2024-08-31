package com.neo.twig;

import com.neo.twig.audio.AudioService;
import com.neo.twig.config.ConfigManager;
import com.neo.twig.graphics.GraphicsService;
import com.neo.twig.input.InputService;
import com.neo.twig.resources.ResourceService;
import com.neo.twig.scene.SceneService;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

public final class Engine {
    private static final CountDownLatch latch = new CountDownLatch(1);
    static boolean isEditor;
    static boolean shouldQuit;
    private static boolean initialised;
    private static long startDelta;
    private static EngineConfig config;
    private static AudioService audioService;
    private static GraphicsService graphicsService;
    //private static PhysicsService g_PhysicsService;
    private static TimeService fixedTimeService;
    private static TimeService realTimeService;
    private static TimeService gameTimeService;
    private static SceneService sceneService;
    private static InputService inputService;
    private static ResourceService resourceService;

    @SuppressWarnings("unused")
    public static boolean init(EngineConfig config) {
        if (Arrays.stream(config.args()).toList().contains("editor")) {
            isEditor = true;
        }

        Engine.config = config;
        Engine.config.createEngineConfigs();
        ConfigManager.setCurrentUserIdentifier(System.getProperty("user.name"));

        ConfigManager.loadConfig(Engine.config.appConfig());
        ConfigManager.loadConfig(Engine.config.graphicsConfig());
        ConfigManager.loadConfig(Engine.config.audioConfig());

        realTimeService = new TimeService();
        gameTimeService = new TimeService();

        sceneService = new SceneService();

        audioService = AudioService.createService(config.audioConfig().subsystem);
        audioService.setAudioBusRoot(config.audioConfig().mixerTree);

        graphicsService = new GraphicsService(config.graphicsConfig());

        inputService = new InputService();

        resourceService = new ResourceService();

        //g_PhysicsService = new PhysicsService();

        startDelta = System.currentTimeMillis();

        initialised = true;

        return true;
    }

    @SuppressWarnings("unused")
    public static void start() {
        Platform.startup(() -> {
            Stage stage = new Stage();
            Scene gameScene = generateGameScene();
            stage.setScene(gameScene);
            Canvas canvas = (Canvas) gameScene.getRoot().getChildrenUnmodifiable().getFirst();
            graphicsService.setGraphicsContext(canvas.getGraphicsContext2D());

            stage.setTitle(String.format("%s %s - Twig", config.appConfig().name, config.appConfig().version));
            if (config.appConfig().icon != null) {
                stage.getIcons().add(config.appConfig().icon);
            }

            switch (config.graphicsConfig().mode) {
                case Maximised -> stage.setMaximized(true);
                case Fullscreen -> stage.setFullScreen(true);
            }

            getSceneService().setStage(stage);
            try {
                getSceneService().setScene(config.appConfig().initialScene.getPath().toFile().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            stage.setResizable(config.graphicsConfig().allowWindowResizing);
            stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> {
                quit();
            });
            stage.addEventHandler(KeyEvent.KEY_PRESSED, inputService.getKeyEventHandler());
            stage.addEventHandler(KeyEvent.KEY_RELEASED, inputService.getKeyEventHandler());
            stage.addEventHandler(MouseEvent.MOUSE_PRESSED, inputService.getMouseEventHandler());
            stage.addEventHandler(MouseEvent.MOUSE_RELEASED, inputService.getMouseEventHandler());
            stage.show();

            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException ignored) {

        }

        while (!shouldQuit) {
            runAndWait(Engine::runFrame);
        }

        shutdown();
    }

    public static void runAndWait(Runnable runnable) {
        try {
            if (Platform.isFxApplicationThread()) {
                runnable.run();
            } else {
                FutureTask<Object> futureTask = new FutureTask<>(runnable,
                        null);
                Platform.runLater(futureTask);
                futureTask.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runFrame() {
        long highResTime = System.currentTimeMillis();

        float delta = (float) (highResTime - startDelta);

        realTimeService.updateDelta(delta);
        gameTimeService.updateDelta(delta);

        //g_PhysicsService.update(g_RealTimeService.getDeltaTime());

        sceneService.update(gameTimeService.getDeltaTime());

        inputService.update(realTimeService.getDeltaTime());

        graphicsService.update(gameTimeService.getDeltaTime());

        startDelta = highResTime;
    }

    @SuppressWarnings("unused")
    public static void quit() {
        runAndWait(() -> {
            shouldQuit = true;
        });
    }

    @SuppressWarnings("unused")
    public static boolean getShouldQuit() {
        return shouldQuit;
    }

    //TODO: Fix alerts not showing and blocking the application
    private static void quit(FatalError error) {
        Alert alert = new Alert(Alert.AlertType.ERROR, getFatalErrorMessage(error), ButtonType.OK);

        latch.countDown();
        getSceneService().getStage().show();

        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> quit());
    }

    private static void shutdown() {
        sceneService.destroy();
        audioService.releaseAllPlayers();
        Platform.exit();
    }

    @SuppressWarnings("unused")
    public static EngineConfig getConfig() {
        return config;
    }

    @SuppressWarnings("unused")
    public static AudioService getAudioService() {
        assert initialised;

        return audioService;
    }

    @SuppressWarnings("unused")
    public static GraphicsService getGraphicsService() {
        assert initialised;

        return graphicsService;
    }

    @SuppressWarnings("unused")
    public static TimeService getTimeService() {
        assert initialised;

        return gameTimeService;
    }

    /*public static PhysicsService getPhysicsService() {
        assert m_Initialised;

        return g_PhysicsService;
    }*/

    @SuppressWarnings("unused")
    public static SceneService getSceneService() {
        assert initialised;

        return sceneService;
    }

    @SuppressWarnings("unused")
    public static InputService getInputService() {
        assert initialised;

        return inputService;
    }

    @SuppressWarnings("unused")
    public static ResourceService getResourceService() {
        assert initialised;

        return resourceService;
    }

    private static Scene generateGameScene() {
        StackPane pane = new StackPane();
        pane.setId("root");
        pane.setPrefSize(config.graphicsConfig().width, config.graphicsConfig().height);

        Canvas drawable = new Canvas(config.graphicsConfig().width, config.graphicsConfig().height);
        pane.getChildren().add(drawable);

        return new Scene(pane);
    }

    @SuppressWarnings("unused")
    public static boolean isEditor() {
        return isEditor;
    }

    private static String getFatalErrorMessage(FatalError error) {
        return switch (error) {
            case SCENE_LOAD_FAILURE -> "";
            default -> "An unknown error has occurred. The engine must shutdown.";
        };
    }
}
