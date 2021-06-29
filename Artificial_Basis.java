package example;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Artificial_Basis {

    private final int countX;
    private final int countRes;
    private final boolean simple_number;
    private final Fraction[] f;
    private final Fraction[][] res;
    private final Fraction[][] Table;
    private final int[] horizontal;
    private final int[] vertical;

    private final int[][] history = new int[100][2];

    private final GridPane pane = new GridPane();
    private int order = 0;
    private int number = -1;
    private final Font font = Font.font("Tahoma", FontWeight.NORMAL, 15);

    public Artificial_Basis(int countX, int countRes, boolean simple_number, Fraction[] f, Fraction[][] res) {
        this.countX = countX;
        this.countRes = countRes;
        this.simple_number = simple_number;
        this.f = f;
        this.res = res;
        Table = new Fraction[countRes + 1][countX + 1];
        createNewTable();

        vertical = new int[countRes];
        for (int i = 0; i < countRes; i++)
            vertical[i] = (countX + i + 1);

        horizontal = new int[countX];
        for (int i = 0; i < (countX); i++)
            horizontal[i] = (i + 1);
    }

    public void showStepByStep() {
        pane.setVgap(5);
        pane.setHgap(5);

        Stage window = new Stage();
        window.setTitle("Метод искусственного базиса");
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(pane);
        window.setScene(new Scene(scroll, 700, 700));

        printTableStepByStep();
        window.showAndWait();
    }

    public void showAutomatic(){
        pane.setVgap(5);
        pane.setHgap(5);

        Stage window = new Stage();
        window.setTitle("Метод искусственного базиса");
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(pane);
        window.setScene(new Scene(scroll, 700, 700));

        printTableAutomatic();
        while (!checkFinish()){
            int[] step = TheBest();
            createTable(step[0], step[1]);
            printTableAutomatic();
        }
        printFinish();

        window.showAndWait();
    }

    private void showMessage(String message) {
        Alert al = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        al.showAndWait();
    }


    private void createNewTable() {
        for (int i = 0; i < countRes; i++)
            for (int j = 0; j < (countX + 1); j++)
                Table[i][j] = res[i][j];

        for (int i = 0; i < countRes; i++)
            if (Table[i][countX].getDouble() < 0)
                for (int j = 0; j < (countX + 1); j++) {
                    Table[i][j] = Table[i][j].changeSign();
                }

        for (int j = 0; j < (countX + 1); j++) {
            Table[countRes][j] = new Fraction(0);
            for (int i = 0; i < countRes; i++)
                Table[countRes][j] = Table[countRes][j].add(Table[i][j]);
            Table[countRes][j] = Table[countRes][j].changeSign();
        }
    }


    private void createTable(int row, int column) {
        int f = horizontal[column];
        horizontal[column] = vertical[row];
        vertical[row] = f;

        Fraction bas = new Fraction(1).div(Table[row][column]);
        Fraction[] basLine = new Fraction[Table[0].length];
        for (int j = 0; j < basLine.length; j++)
            basLine[j] = Table[row][j].mul(bas);
        basLine[column] = bas;

        for (int i = 0; i < Table.length; i++) {
            Fraction b = Table[i][column];
            for (int j = 0; j < Table[0].length; j++)
                Table[i][j] = Table[i][j].sub(basLine[j].mul(b));
            Table[i][column] = b;
        }

        for (int i = 0; i < Table.length; i++)
            Table[i][column] = Table[i][column].mul(bas).changeSign();
        System.arraycopy(basLine, 0, Table[row], 0, Table[0].length);
    }

    private void printTableStepByStep() {
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        number++;
        Text t1 = new Text("№" + number);
        t1.setFont(font);
        root.add(t1, 1, 0);
        GridPane.setHalignment(t1, HPos.CENTER);
        for (int j = 0; j < horizontal.length; j++) {
            Text t = new Text("X" + horizontal[j]);
            t.setFont(font);
            root.add(t, 2 + j, 0);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Button[][] but = new Button[Table.length - 1][Table[0].length - 1];
        Button button = new Button("Назад");
        for (int i = 0; i < Table.length - 1; i++) {
            Text t = new Text("X" + vertical[i]);
            t.setFont(font);
            root.add(t, 1, 1 + i);
            GridPane.setHalignment(t, HPos.CENTER);
            for (int j = 0; j < Table[0].length - 1; j++) {
                but[i][j] = new Button();
                if (simple_number)
                    but[i][j].setText(Table[i][j].toString());
                else
                    but[i][j].setText(Double.toString(Table[i][j].getDouble()));
                but[i][j].setFont(font);
                TheBestColumn(but, i, j);
                int finalI = i;
                int finalJ = j;
                but[i][j].setOnAction((ActionEvent e) -> {
                    if (Table[finalI][finalJ].getDouble() == 0) {
                        showMessage("Деление на ноль !!!");
                    } else {
                        but[finalI][finalJ].setStyle("-fx-background-color: brown");
                        locButton(but, button);
                        history[number][0] = finalI;
                        history[number][1] = finalJ;
                        createTable(finalI, finalJ);
                        printTableStepByStep();
                        if (checkFinish()){
                            printFinish();
                        }
                    }
                });
                root.add(but[i][j], 2 + j, 1 + i);
                GridPane.setHalignment(but[i][j], HPos.CENTER);
            }
            Text t11 = new Text(Table[i][Table[0].length - 1].toString());
            t11.setFont(font);
            root.add(t11, 2 + (Table[0].length - 1), 1 + i);
            GridPane.setHalignment(t11, HPos.CENTER);
        }
        int[] best = TheBest();
        if (best[0] >= 0) {
            but[best[0]][best[1]].setStyle("-fx-background-color: green");
        }
        for (int j = 0; j < Table[0].length; j++) {
            Text t = new Text(Table[Table.length - 1][j].toString());
            t.setFont(font);
            root.add(t, 2 + j, 1 + Table.length - 1);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        for (int j = 0; j < Table[0].length + 2; j++) {
            Text t = new Text("  ");
            t.setFont(font);
            root.add(t, j, 1 + Table.length);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        pane.add(root, 0, order);
        button.setFont(font);
        button.setOnAction((ActionEvent e) -> {
            if (number <= 0){
                showMessage("Таблица не доступна");
            }
            else {
                number--;
                createTable(history[number][0], history[number][1]);
                number--;
                locButton(but, button);
                printTableStepByStep();
                if (checkFinish()) {
                    printFinish();
                }
            }
        });
        pane.add(button, 2, order);
        order += 2;
    }

    private void printTableAutomatic() {
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        Text t1 = new Text("№" + number);
        number++;
        t1.setFont(font);
        root.add(t1, 1, 0);
        GridPane.setHalignment(t1, HPos.CENTER);
        for (int j = 0; j < horizontal.length; j++) {
            Text t = new Text("X" + horizontal[j]);
            t.setFont(font);
            root.add(t, 2 + j, 0);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Button[][] but = new Button[Table.length - 1][Table[0].length - 1];
        for (int i = 0; i < Table.length - 1; i++) {
            Text t = new Text("X" + vertical[i]);
            t.setFont(font);
            root.add(t, 1, 1 + i);
            GridPane.setHalignment(t, HPos.CENTER);
            for (int j = 0; j < Table[0].length - 1; j++) {
                but[i][j] = new Button();
                if (simple_number)
                    but[i][j].setText(Table[i][j].toString());
                else
                    but[i][j].setText(Double.toString(Table[i][j].getDouble()));
                but[i][j].setFont(font);
                root.add(but[i][j], 2 + j, 1 + i);
                GridPane.setHalignment(but[i][j], HPos.CENTER);
            }
            Text t11 = new Text(Table[i][Table[0].length - 1].toString());
            t11.setFont(font);
            root.add(t11, 2 + (Table[0].length - 1), 1 + i);
            GridPane.setHalignment(t11, HPos.CENTER);
        }
        int[] best = TheBest();
        if (best[0] >= 0 && !checkFinish()) {
            but[best[0]][best[1]].setStyle("-fx-background-color: brown");
        }
        for (int j = 0; j < Table[0].length; j++) {
            Text t = new Text(Table[Table.length - 1][j].toString());
            t.setFont(font);
            root.add(t, 2 + j, 1 + Table.length - 1);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        for (int j = 0; j < Table[0].length + 2; j++) {
            Text t = new Text("  ");
            t.setFont(font);
            root.add(t, j, 1 + Table.length);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        pane.add(root, 0, order);
        order += 2;
    }

    private void locButton(Button[][] but, Button button) {
        button.setOnAction((ActionEvent ee) -> {});
        for (int i = 0; i < Table.length - 1; i++) {
            for (int j = 0; j < Table[0].length - 1; j++) {
                but[i][j].setOnAction((ActionEvent ee) -> {});
            }
        }
    }

    private int[] TheBest(){
        double value = -1;
        double min = 0;
        int row = -1;
        int column = -1;
        for (int i = 0 ; i < countRes; i++){
            for (int j = 0; j < countX; j++){
                if (horizontal[j] > countX || Table[countRes][j].getDouble() >= 0
                        || Table[i][j].getDouble() <= 0){
                    continue;
                }
                if (Table[countRes][j].getDouble() < min){
                    value = Table[i][countX].div(Table[i][j]).getDouble();
                    min = Table[countRes][j].getDouble();
                    row = i;
                    column = j;
                    continue;
                }
                if (Table[countRes][j].getDouble() == min
                        && Table[i][countX].div(Table[i][j]).getDouble() < value){
                    value = Table[i][countX].div(Table[i][j]).getDouble();
                    min = Table[countRes][j].getDouble();
                    row = i;
                    column = j;
                }
            }
        }
        return new int[]{row, column};
//        if (row >= 0) {
//            but[row][column].setStyle("-fx-background-color: green");
    }

    private void TheBestColumn(Button[][] but,int row, int column) {
        if (horizontal[column] > countX || vertical[row] <= countX) {
            return;
        }
        if (Table[countRes][column].getDouble() >= 0 || Table[row][column].getDouble() <= 0) {
            return;
        }
        double b = Table[row][countX].div(Table[row][column]).getDouble();
        for (int i = 0; i < countRes; i++) {
            if (Table[i][column].getDouble() > 0 && Table[i][countX].div(Table[i][column]).getDouble() < b) {
                return;
            }
        }
        but[row][column].setStyle("-fx-background-color: yellow");
    }


    private boolean checkFinish(){
        return checkFromAnswer() || checkImpossible();
    }

    private boolean checkFromAnswer(){
        for (int j : vertical){
            if(j > countX){
                return false;
            }
        }
        return true;
    }

    private boolean checkImpossible(){
        for (int j = 0; j < countX; j++) {
            if (Table[countRes][j].getDouble() < 0) {
                return false;
            }
        }
        return Table[countRes][countX].getDouble() != 0;
    }

    private void printFinish(){
        if (checkImpossible()){
            Text t = new Text("Услоивия задачи противоречивы");
            t.setFont(font);
            GridPane.setHalignment(t, HPos.CENTER);
            pane.add(t, 0, order);
            order += 2;
            pane.add(new Text("           "), 0, order);
            order += 2;
            return;
        }
        if(checkFromAnswer()){
            Button buttonS = new Button("Симплекс метод пошагово");
            buttonS.setFont(font);
            buttonS.setOnAction(event -> simplex_method("S"));
            GridPane.setHalignment(buttonS, HPos.CENTER);
            pane.add(buttonS, 0, order);
            order += 2;

            Button buttonA = new Button("Симплекс метод автоматически");
            buttonA.setFont(font);
            buttonA.setOnAction(event -> simplex_method("A"));
            GridPane.setHalignment(buttonA, HPos.CENTER);
            pane.add(buttonA, 0, order);
            order += 2;

            pane.add(new Text("           "), 0, order);
            order += 2;
        }

    }


    private void simplex_method(String tip) {
        int[] newVertical = new int[countRes];
        System.arraycopy(vertical, 0, newVertical, 0, countRes);
        int[] newHorizontal = new int[countX - countRes];
        Fraction[][] newTable = new Fraction[countRes + 1][countX - countRes + 1];
        int h = 0;
        for (int j = 0; j < (countX); j++) {
            if (horizontal[j] <= countX) {
                newHorizontal[h] = horizontal[j];
                for (int i = 0; i < countRes; i++)
                    newTable[i][h] = Table[i][j];
                h += 1;
            }
        }
        for (int i = 0; i < countRes; i++)
            newTable[i][countX - countRes] = Table[i][countX];
        F(newTable, newHorizontal);
        Simplex_Method window = new Simplex_Method(countX, countRes, simple_number, newTable, newHorizontal, newVertical);
        if (tip.equals("S")) {
            window.showStepByStep();
        }
        else {
            window.showAutomatic();
        }
    }

    private void F(Fraction[][] NewTable, int[] Newhorizontal) {
        for (int j = 0; j < countX - countRes; j++)
            NewTable[countRes][j] = f[Newhorizontal[j] - 1];
        NewTable[countRes][countX - countRes] = f[countX];

        for (int j = 0; j < (countX - countRes); j++) {
            for (int i = 0; i < countRes; i++)
                NewTable[countRes][j] = NewTable[countRes][j].add(f[vertical[i] - 1].mul(NewTable[i][j].changeSign()));
        }
        for (int i = 0; i < countRes; i++)
            NewTable[countRes][countX - countRes] = NewTable[countRes][countX - countRes].add(f[vertical[i] - 1].mul(
                    NewTable[i][countX - countRes]));
        NewTable[countRes][countX - countRes] = NewTable[countRes][countX - countRes].changeSign();
    }
}
