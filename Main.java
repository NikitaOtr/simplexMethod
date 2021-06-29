package example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class Main extends Application {

    int countX = 3;
    int countRes = 2;
    boolean simple_number = true;
    TextField[] f_field;
    TextField[][] res_field;
    TextField[] start_basis_field;
    Fraction[] f;
    Fraction[][] res;
    boolean[] start_basis;

    private final GridPane pane = new GridPane();
    private final Font font = Font.font("Tahoma", FontWeight.NORMAL, 15);

    public static void main(String[] arg) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(createFileMenu(), createReference());

        primaryStage.setTitle("Симплекс Метод");
        pane.setHgap(20);
        Left_Scene();
        Right_Scene();

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(pane);

        Scene scene = new Scene(root, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private Menu createReference() {
        Menu menuReference = new Menu("Справка");
        MenuItem about = new MenuItem("О программе");
        about.setOnAction((ActionEvent t) ->{
            Stage window = new Stage();
            window.setTitle("О программе");
            Text text = new Text(" Симплекс-метод — алгоритм решения оптимизационной \n" +
                    " задачи линейного программирования путём перебора \n" +
                    " вершин выпуклого многогранника в многомерном \n" +
                    " пространстве. Сущность метода: построение базисных \n" +
                    " решений, на которых монотонно убывает линейный \n" +
                    " функционал, до ситуации, когда выполняются \n" +
                    " необходимые условия локальной оптимальности.");
            text.setFont(font);
            GridPane  p = new GridPane();
            p.add(text, 0,0);
            window.setScene(new Scene(p, 410, 300));
            window.showAndWait();

        });
        menuReference.getItems().add(about);
        return  menuReference;
    }

    private Menu createFileMenu() {
        Menu menuFile = new Menu("Файл");

        CheckMenuItem check = new CheckMenuItem("Обыкновенные дроби");
        check.setSelected(true);
        check.setOnAction((ActionEvent e) -> simple_number = check.isSelected());

        MenuItem save = new MenuItem("Сохранить");
        save.setOnAction((ActionEvent e) -> FileSave());

        MenuItem open = new MenuItem("Открыть");
        open.setOnAction((ActionEvent e) -> FileOpen());

        MenuItem exit = new MenuItem("Выход");
        exit.setOnAction((ActionEvent e) -> Platform.exit());

        menuFile.getItems().addAll(check, save, open, exit);
        return menuFile;
    }

    private void FileSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохрание файла");
        File file = fileChooser.showSaveDialog(null);
        if (file == null)
            return;
        saveDataToFile(file);
    }

    private void saveDataToFile(File dataFile) {
        try {
            FileWriter out = new FileWriter(dataFile);
            out.write(countX + " " + countRes);
            out.write("\n");
            for (int j = 0; j < countX + 1; j++) {
                out.write(f_field[j].getText() + " ");
            }
            out.write("\n");
            for (int i = 0; i < countRes; i++){
                for (int j = 0; j < countX + 1; j++) {
                    out.write(res_field[i][j].getText() + " ");
                }
                out.write("\n");
            }
            for (int j = 0; j < countX; j++){
                out.write(start_basis_field[j].getText() + " ");
            }
            out.close();
        } catch (IOException e){
            showMessage("Файл сохранить не удалось!!!");
        }
    }

    private void FileOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Data File");
        File file = fileChooser.showOpenDialog(null);
        if (file == null)
            return;
        readDataFromFile(file);
    }

    private void readDataFromFile(File dataFile) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(dataFile));
            String[] data;
            try {
                data = in.readLine().strip().split(" +");
                if (data.length != 2){
                    throw new Exception();
                }
                countX = Integer.parseInt(data[0]);
                countRes = Integer.parseInt(data[1]);
                Left_Scene();
                Right_Scene();

                data = in.readLine().strip().split(" +");
                if (data.length != (countX + 1)){
                    throw new Exception();
                }
                for (int j = 0; j < countX + 1; j++)
                    f_field[j].setText(data[j]);

                for (int i = 0; i < countRes; i++){
                    data = in.readLine().strip().split(" +");
                    for (int j = 0; j < countX + 1; j++ ) {
                        res_field[i][j].setText(data[j]);
                    }
                }
                data = in.readLine().strip().split(" +");
                for (int j = 0; j < countX; j++ ) {
                    start_basis_field[j].setText(data[j]);
                }
                in.close();
            } catch (Exception e){
                showMessage("Данные в файле не корректные!!!");
                in.close();
            }
        } catch (IOException e){
            showMessage("Файл не удалось отрыть для чтения!!!");
        }
    }


    private void showMessage(String message){
        Alert al = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        al.showAndWait();
    }


    private void collectionData() throws Exception {
        try {
            f = new Fraction[countX + 1];
            res = new Fraction[countRes][countX + 1];
            for (int i = 0; i < (countX + 1); i++){
                if (f_field[i].getText().contains("\\")){
                    String[] s = f_field[i].getText().split("\\\\");
                    f[i] = new Fraction(s[0], s[1]);
                }
                else {
                    f[i] = new Fraction(f_field[i].getText());
                }
            }
            for (int i = 0; i < countRes; i++)
                for (int j = 0; j < (countX + 1); j++)
                    if (res_field[i][j].getText().contains("\\")){
                        String[] s = res_field[i][j].getText().split("\\\\");
                        res[i][j] = new Fraction(s[0], s[1]);
                    }
                    else {
                        res[i][j] = new Fraction(res_field[i][j].getText());
                    }
        }
        catch(Exception e) {
            throw new Exception("Данные введены не корректно");
        }
    }

    private void preparationStartBasis() throws Exception {
        start_basis = new boolean[countX];
        int count = 0;
        try {
            for (int j = 0; j < (countX); j++){
                start_basis[j] = (0.0 != Double.parseDouble(start_basis_field[j].getText()));
                if (start_basis[j]){
                    count += 1;
                }
            }
        }
        catch(Exception e) {
            throw new Exception("Данный введены не корректно");
        }
        if (count != countRes)
            throw new Exception("Количество базисных переменных не соответствует \nколичестку ограничений");
    }

    private void preparationStartBasis1() throws Exception {
        start_basis = new boolean[countX];
        int count = 0;
        try {
            for (int j = 0; j < (countX); j++){
                start_basis[j] = (0.0 != Double.parseDouble(start_basis_field[j].getText()));
                if (start_basis[j]){
                    count += 1;
                }
            }
        }
        catch(Exception e) {
            throw new Exception("Данный введены не корректно");
        }
        if (count != 2)
            throw new Exception("Количество базисных переменных не соответствует \nколичестку ограничений");
    }


    private void Left_Scene() {
        GridPane root = new GridPane();
        root.setVgap(10);
        root.setHgap(10);

        Text text1 = new Text("Колическов переменных");
        text1.setFont(font);
        root.add(text1, 0, 1);

        Text text2 = new Text("Количество ограничений");
        text2.setFont(font);
        root.add(text2, 1, 1);

        TextField f_CountX = new TextField(Integer.toString(countX));
        f_CountX.setStyle("-fx-font-size: 15px");
        f_CountX.setPrefWidth(100);
        GridPane.setHalignment(f_CountX, HPos.CENTER);
        root.add(f_CountX, 0, 2);

        TextField f_CountRes = new TextField(Integer.toString(countRes));
        f_CountRes.setStyle("-fx-font-size: 15px");
        f_CountRes.setPrefWidth(100);
        GridPane.setHalignment(f_CountRes, HPos.CENTER);
        root.add(f_CountRes, 1, 2);

        Button btnApply = new Button("Применить");
        btnApply.setMinSize(200, 50);
        btnApply.setFont(font);
        btnApply.setAlignment(Pos.CENTER);
        btnApply.setOnAction((ActionEvent e) -> {
            try {
                countX = Integer.parseInt(f_CountX.getText());
                countRes = Integer.parseInt(f_CountRes.getText());
            }
            catch(Exception ex){
                showMessage("Данные введены не корректно");
                return;
            }
            if (countX <= 0 || countRes <= 0){
                showMessage("Количества должны быть положительными");
                return;
            }
            if (countX <= countRes) {
                showMessage("Количество переменных должно быть больше \nколичесва ограничений!!!");
                return;
            }
                Right_Scene();
        });
        root.add(btnApply, 0, 3, 2, 1);
        GridPane.setHalignment(btnApply, HPos.CENTER);

        Button btnSMA = new Button("Сиплекс метод автоматически");
        btnSMA.setMinSize(300, 50);
        btnSMA.setFont(font);
        btnSMA.setAlignment(Pos.CENTER);
        btnSMA.setOnAction((ActionEvent e) -> {
            try{
                collectionData();
                preparationStartBasis();
                Simplex_Method window = new Simplex_Method(countX, countRes, simple_number, start_basis, f, res);
                window.showAutomatic();
            }catch(Exception ex){
                showMessage(ex.getMessage());
        }
        });
        root.add(btnSMA, 0, 4, 2, 1);
        GridPane.setHalignment(btnSMA, HPos.CENTER);

        Button btnSMR = new Button("Сиплекс метод пошагово");
        btnSMR.setMinSize(300, 50);
        btnSMR.setFont(font);
        btnSMR.setAlignment(Pos.CENTER);
        btnSMR.setOnAction((ActionEvent e) -> {
            try{
                collectionData();
                preparationStartBasis();
                Simplex_Method window = new Simplex_Method(countX, countRes, simple_number, start_basis, f, res);
                window.showStepByStep();
            }catch(Exception ex){
                showMessage(ex.getMessage());
            }
        });
        root.add(btnSMR, 0, 5, 2, 1);
        GridPane.setHalignment(btnSMR, HPos.CENTER);

        Button btnIBA = new Button("Метод искусственного базиса автоматически");
        btnIBA.setMinSize(300, 50);
        btnIBA.setFont(font);
        btnIBA.setAlignment(Pos.CENTER);
        btnIBA.setOnAction((ActionEvent e) -> {
            try{
                collectionData();
                Artificial_Basis window = new Artificial_Basis(countX, countRes, simple_number, f, res);
                window.showAutomatic();
            }catch(Exception ex){
                showMessage(ex.getMessage());
            }
        });
        root.add(btnIBA, 0, 6, 2, 1);
        GridPane.setHalignment(btnIBA, HPos.CENTER);

        Button btnIBR = new Button("Метод искусственного базиса пошагово");
        btnIBR.setMinSize(300, 50);
        btnIBR.setFont(font);
        btnIBR.setAlignment(Pos.CENTER);
        btnIBR.setOnAction((ActionEvent e) -> {
            try{
                collectionData();
                Artificial_Basis window = new Artificial_Basis(countX, countRes, simple_number, f, res);
                window.showStepByStep();
            }catch(Exception ex){
                showMessage(ex.getMessage());
            }
        });
        root.add(btnIBR, 0, 7, 2, 1);
        GridPane.setHalignment(btnIBR, HPos.CENTER);

        Button btnGR = new Button("Графический метод");
        btnGR.setMinSize(300, 50);
        btnGR.setFont(font);
        btnGR.setAlignment(Pos.CENTER);
        btnGR.setOnAction((ActionEvent e) -> {
            try {
                collectionData();
                preparationStartBasis1();
                Graphic window = new Graphic(countX, countRes, simple_number, start_basis, f, res);
                window.show();
            }
            catch(Exception ex) {
                showMessage(ex.getMessage());
            }
        });
        root.add(btnGR, 0, 8, 2, 1);
        GridPane.setHalignment(btnGR, HPos.CENTER);

        pane.add(root, 1, 1);
    }

    private void Right_Scene() {
        f_field = new TextField[countX + 1];
        res_field = new TextField[countRes][countX + 1];
        start_basis_field = new TextField[countX];

        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        for (int i = 0; i < countX; i++) {
            Text t = new Text("X" + (i + 1));
            t.setFont(font);
            root.add(t, i + 1, 0);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Text text = new Text("B");
        text.setFont(font);
        root.add(text, countX + 1, 0);
        GridPane.setHalignment(text, HPos.CENTER);

        Text text1 = new Text("F(x)");
        text1.setFont(font);
        root.add(text1, 0, 1);
        GridPane.setHalignment(text1, HPos.CENTER);

        // + 1 из за свободных членов (B)
        for (int j = 0; j < countX + 1; j++) {
            f_field[j] = new TextField();
            f_field[j].setStyle("-fx-font-size: 15px");
            f_field[j].setPrefWidth(50);
            root.add(f_field[j], j + 1, 1);
        }
        for (int i = 0; i < countRes; i++) {
            Text t = new Text("f" + (i + 1));
            t.setFont(font);
            root.add(t, 0, 3 + i);
            GridPane.setHalignment(t, HPos.CENTER);
            // + 1 из за свободных членов (B)
            for (int j = 0; j < countX + 1; j++) {
                res_field[i][j] = new TextField();
                res_field[i][j].setStyle("-fx-font-size: 15px");
                res_field[i][j].setPrefWidth(50);
                root.add(res_field[i][j], (j + 1), 3 + i);
            }
        }
        Text t = new Text("НБ");
        t.setFont(font);
        root.add(t, 0, countRes + 4);
        GridPane.setHalignment(t, HPos.CENTER);
        for (int j = 0; j < countX; j++) {
            start_basis_field[j] = new TextField();
            start_basis_field[j].setStyle("-fx-font-size: 15px");
            start_basis_field[j].setPrefWidth(50);
            root.add(start_basis_field[j], j + 1, countRes + 4);
        }
        for (int j = 0; j < countRes; j++) {
            start_basis_field[j].setText("1");
        }
        for (int j = countRes; j < countX; j++) {
            start_basis_field[j].setText("0");
        }
            ScrollPane scroll = new ScrollPane();
        scroll.setContent(root);
        pane.add(scroll, 2, 1);
    }
}
