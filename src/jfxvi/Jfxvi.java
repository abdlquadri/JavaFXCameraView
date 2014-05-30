/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfxvi;

import com.sleepingdumpling.jvideoinput.VideoInputDemo;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.SwingUtilities;

/**
 *
 * @author abdlquadri
 */
public class Jfxvi extends Application {

    private Thread cameraRetrieverThread;

    @Override
    public void start(Stage primaryStage) {
        final SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);

        VBox root = new VBox();
        HBox controls = new HBox();
        controls.setSpacing(30);
        controls.setPadding(new Insets(5));
        Button btnCapture = new Button("Capture");
        Button btnSave = new Button("Save");
        Button btnRedo = new Button("Redo");
        controls.getChildren().addAll(btnCapture, btnRedo, btnSave);
        btnCapture.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                controls.setVisible(false);
                cameraRetrieverThread.suspend();
                capture(primaryStage);
                controls.setVisible(true);
            }
        });
        btnRedo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                cameraRetrieverThread.resume();
            }
        });
        btnSave.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                cameraRetrieverThread.stop();
                primaryStage.close();
            }
        });
        root.getChildren().add(swingNode);
        root.getChildren().add(controls);
//        root.getChildren().add(btnSave);
//        root.getChildren().add(btnRedo);

        Scene scene = new Scene(root, 250, 300);
        primaryStage.setTitle("Mugshot");
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent t) {
                cameraRetrieverThread.stop();
            }
        });
        primaryStage.show();
    }

    private void capture(Stage primaryStage) {
        WritableImage snapshot = primaryStage.getScene().snapshot(null);
//        primaryStage.getScene()
//                snapshot.getPixelWriter().
        BufferedImage fromFXImage = SwingFXUtils.fromFXImage(snapshot, null);
        try {
            File passportFile = new File(UUID.randomUUID() + ".png");
            ImageIO.write(fromFXImage, "png", passportFile);
        } catch (IOException ex) {
            Logger.getLogger(Jfxvi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                VideoInputDemo videoInputDemo = new VideoInputDemo(350, 250, 30);
                swingNode.setContent(videoInputDemo);
                cameraRetrieverThread = videoInputDemo.startRetrieverThread(350, 250, 30);
            }

        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
