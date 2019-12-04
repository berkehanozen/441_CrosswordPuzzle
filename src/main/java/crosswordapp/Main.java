package crosswordapp;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private int gridSize = 5;

    private String[][] grid;

    private Stage primaryStage;
    private final String INSERT_MODE_BLACK = "Insert Black Square";
    private final String INSERT_MODE_LETTER = "Insert/Remove Letters";
    private ComboBox<String> insertMode;
    private BorderPane borderPane;
    private VBox vBox;
    private GridPane gridPane;
    private ColumnConstraints cc;
    private RowConstraints rc;
    private Scene scene;
    private TextField sizeTextField;

    @Override
    public void start(Stage stg) throws Exception{
        primaryStage = stg;

        vBox = new VBox(20);
        vBox.setPadding(new Insets(50,0,0,0));
        vBox.setAlignment(Pos.CENTER);
        borderPane = new BorderPane(null,null,vBox,null,null);
        scene = new Scene(borderPane, 600, 600);
        scene.getStylesheets().add("styles.css");
        cc = new ColumnConstraints(30, 50, Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true );
        rc = new RowConstraints(30, 50, Double.MAX_VALUE, Priority.ALWAYS, VPos.CENTER, true );

        createGrid();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        Button setSize = new Button("Set Size");
        insertMode = new ComboBox<>(FXCollections.observableArrayList(INSERT_MODE_LETTER,INSERT_MODE_BLACK));
        insertMode.setValue(INSERT_MODE_LETTER);
        insertMode.setOnAction(e -> {
            for (Node child : gridPane.getChildren()) {
                Node n = ((StackPane) child).getChildren().get(0);
                Node n_1 = ((StackPane) child).getChildren().get(1);
                if(n instanceof TextField) {
                    ((TextField) n).setDisable(!insertMode.getValue().equals(INSERT_MODE_LETTER) || Color.BLACK.equals(((Rectangle) n_1).getFill()));
                }
                else {
                    ((TextField) n_1).setDisable(!insertMode.getValue().equals(INSERT_MODE_LETTER) || Color.BLACK.equals(((Rectangle) n).getFill()));
                }
            }
        });
        Button finish = new Button("Finish");
        finish.setOnMouseClicked(e -> {     // * -> siyah kare, " " (boşluk) -> boş kutu, <char> -> dolu kutu
            String grid[][] = new String[gridSize][gridSize];
            int i = 0;
            for (Node child : gridPane.getChildren()) {
                Rectangle rect;
                TextField text;
                if(((StackPane) child).getChildren().get(0) instanceof Rectangle) {
                    rect = ((Rectangle) ((StackPane) child).getChildren().get(0));
                    text = ((TextField) ((StackPane) child).getChildren().get(1));
                }else{
                    rect = ((Rectangle) ((StackPane) child).getChildren().get(1));
                    text = ((TextField) ((StackPane) child).getChildren().get(0));
                }
                if(Color.BLACK.equals(rect.getFill())){
                    grid[i/gridSize][i%gridSize] = "*";
                }else {
                    grid[i/gridSize][i%gridSize] = text.getText().length() == 0 ? " " : text.getText();
                }
                i++;
            }
            for(int j = 0; j < grid.length; j++){
                for (int k = 0; k < grid.length; k++) {
                    System.out.print(grid[j][k]+"|");
                }
                System.out.println();
            }
            DynamicSlotTable dst = new DynamicSlotTable();
            dst.setUpTable(grid);
            dst.display();
        });
        sizeTextField = new TextField();
        sizeTextField.setAlignment(Pos.CENTER);
        sizeTextField.setMaxWidth(150);
        sizeTextField.setText(gridSize+"");
        sizeTextField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                sizeTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        }));
        setSize.setOnMouseClicked(e -> {
            int size;
            try{
                size = Integer.parseInt(sizeTextField.getText());
            }catch(NumberFormatException ex){
                return;
            }
            gridSize = size;
            createGrid();
        });
        vBox.getChildren().addAll(sizeTextField,setSize,insertMode,finish);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public void createGrid(){
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        for (int i = 0; i < gridSize; i++) {
            gridPane.getRowConstraints().add(rc);
            gridPane.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {

                TextField text = new TextField();
                text.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
                text.setAlignment(Pos.CENTER);
                text.textProperty().addListener( (observable, oldValue, newValue) -> {
                    if (newValue.length()!= 0) {
                        if (!Character.isLetter(text.getText().charAt(0))) {
                            text.setText("");
                        }else{
                            text.setText(newValue.substring(newValue.length() - 1).toUpperCase());
                        }
                        text.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-display-caret: false;");
                    }else{
                        text.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
                    }
//                    text.fontProperty().bind(gridPane.widthProperty().);
                });
                Rectangle tile = new Rectangle(0, 0);

                tile.setFill(Color.WHITE);
                tile.setStroke(Color.BLACK);
                tile.setOnMouseClicked(e -> {
                    if(insertMode.getValue().equals(INSERT_MODE_BLACK)){
                        if (Color.WHITE.equals(tile.getFill()) && text.getText().length()==0) {
                            tile.setFill(Color.BLACK);
                        }else if(Color.BLACK.equals(tile.getFill())){
                            tile.setFill(Color.WHITE);
                        }
                    }
                });
                StackPane sp = new StackPane(tile, text);
                gridPane.add(sp, j, i);
                tile.widthProperty().bind(gridPane.widthProperty().divide(gridSize));
                tile.heightProperty().bind(gridPane.heightProperty().divide(gridSize));
            }
        }
        borderPane.setCenter(gridPane);
        gridPane.maxWidthProperty().bind(primaryStage.heightProperty().subtract(40));
        gridPane.maxHeightProperty().bind(primaryStage.widthProperty().subtract(vBox.widthProperty()).subtract(40));
        borderPane.setMargin(gridPane, new Insets(20));
    }


    public static void main(String[] args) {
        launch(args);
    }
}