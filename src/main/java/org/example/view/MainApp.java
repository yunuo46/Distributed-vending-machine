package org.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.Machine;

public class MainApp extends Application {
    private Machine machine;

    @Override
    public void start(Stage primaryStage) {
        // UI 컴포넌트 생성
        Label inputLabel = new Label("Enter some text:");
        TextField inputField = new TextField();
        Button submitButton = new Button("Submit");

        submitButton.setOnAction(e -> {
            String inputText = inputField.getText();
            // 예시
            machine.selectItem(0,1);
        });

        // 레이아웃 설정
        VBox layout = new VBox(10);
        layout.getChildren().addAll(inputLabel, inputField, submitButton);

        // 씬 및 스테이지 설정
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
