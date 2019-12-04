package crosswordapp;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox vBox = new VBox(20);
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        ColumnConstraints cc = new ColumnConstraints(50, 50, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true );
        RowConstraints rc = new RowConstraints(50, 50, Double.MAX_VALUE, Priority.ALWAYS, VPos.CENTER, true );


        int blocksize = 10;
        for (int i = 0; i < blocksize; i++) {
            gridPane.getRowConstraints().add(rc);
            gridPane.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < blocksize; i++) {
            for (int j = 0; j < blocksize; j++) {

                Rectangle tile = new Rectangle(50, 50);
                tile.autosize();
                tile.setFill(Color.WHITE);
                tile.setStroke(Color.BLACK);


                TextField text = new TextField();
                text.setStyle("-fx-background-color: transparent; -fx-text-fill: black; ");
                text.setFont(Font.font(30));
                text.textProperty().addListener( (observable, oldValue, newValue) -> {
                    if (newValue.length()!= 0)
                        text.setText(newValue.substring(0,1).toUpperCase());
                });
                primaryStage.heightProperty().addListener( l -> {
                    text.setFont(Font.font(primaryStage.getHeight()/25));
                });
                StackPane sp = new StackPane(tile, text);
                gridPane.add(sp, j, i);
                tile.widthProperty().bind(gridPane.widthProperty().divide(blocksize));
                tile.heightProperty().bind(gridPane.heightProperty().divide(blocksize));
            }
        }
        BorderPane borderPane = new BorderPane(gridPane,null,vBox,null,null);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(borderPane, 600, 600));
        Button setSize = new Button("Set Size");
        Button insertBlackGrid = new Button("Insert Black Grid");
        Button insertLetter = new Button("Insert Letter");
        Button finish = new Button("Finish");
        vBox.setAlignment(Pos.CENTER);
        finish.setOnMouseClicked(e -> {
            System.out.println("finished");
        });
        borderPane.setMargin(gridPane, new Insets(20));
        vBox.getChildren().addAll(setSize,insertBlackGrid,insertLetter,finish);
        gridPane.maxWidthProperty().bind(primaryStage.heightProperty().subtract(40));
        gridPane.maxHeightProperty().bind(primaryStage.widthProperty().subtract(vBox.widthProperty()).subtract(40));
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}