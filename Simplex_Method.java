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


public class Simplex_Method {
    private final Stage window = new Stage();

    private final int countX;
    private final int countRes;
    private final boolean simple_number;
    private final Fraction[][] Table;
    private int[] horizontal;
    private int[] vertical;

    private final int[][] history = new int[100][2];

    private final GridPane pane = new GridPane();
    private int order = 0;
    private int number = -1;
    private final Font font = Font.font("Tahoma", FontWeight.NORMAL, 15);

    public Simplex_Method(int countX, int countRes, boolean simple_number, Fraction[][] Table, int[] horizontal, int[] vertical) {
        this.countX = countX;
        this.countRes = countRes;
        this.simple_number = simple_number;
        this.Table = Table;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public Simplex_Method(int countX, int countRes, boolean simple_number, boolean[] start_basis, Fraction[] f, Fraction[][] res) throws Exception {
        this.countX = countX;
        this.countRes = countRes;
        this.simple_number = simple_number;
        Table = new Fraction[countRes + 1][countX - countRes + 1];
        createNewTable(start_basis, f, res);
    }


    public void showStepByStep() {
        pane.setVgap(5);
        pane.setHgap(5);

        window.setTitle("Сиплекс метод");
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(pane);
        window.setScene(new Scene(scroll, 700, 700));
        printTableStepByStep();
        if (checkFinish()){
            printFinish();
        }
        window.showAndWait();
    }

    public void showAutomatic() {
        pane.setVgap(5);
        pane.setHgap(5);

        window.setTitle("Сиплекс метод");
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(pane);
        window.setScene(new Scene(scroll, 700, 700));

        printTableAutomatic();
        if (checkCorrectness()) {
            while (!checkFinish()) {
                int[] step = TheBest();
                createTable(step[0], step[1]);
                printTableAutomatic();
            }
            printFinish();
        }
        else {
            printIncorrectness();
        }
        window.showAndWait();
    }

    private void showMessage(String message) {
        Alert al = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        al.showAndWait();
    }


    private void createNewTable(boolean[] start_basis, Fraction[] f, Fraction[][] res) throws Exception {
        int[] x = new int[countX];
        for (int j = 0; j < countX; j++) {
            x[j] = j + 1;
        }
        try {
            executionStartBasis(start_basis, f, res, x);
            methodG(res);
            F(f, res);
        } catch (Exception e) {
            throw new Exception("Начальный базис не может быть использован");
        }
        vertical = new int[countRes];
        for (int i = 0; i < countRes; i++) {
            vertical[i] = x[i];
        }
        horizontal = new int[countX - countRes];
        for (int i = 0; i < (countX - countRes); i++) {
            horizontal[i] = x[countRes + i];
        }
        for (int i = 0; i < countRes; i++)
            for (int j = 0; j < (countX - countRes + 1); j++)
                Table[i][j] = res[i][countRes + j];
        for (int j = 0; j < (countX - countRes + 1); j++)
            Table[countRes][j] = f[countRes + j];
    }

    private void methodG(Fraction[][] res) {
        Fraction b;
        Fraction l;
        for (int i = 0; i < countRes; i++) {
            b = res[i][i];
            for (int j = 0; j < countX + 1; j++) {
                res[i][j] = res[i][j].div(b);
            }
            for (int h = (i + 1); h < countRes; h++) {
                l = res[h][i];
                for (int j = 0; j < (countX + 1); j++)
                    res[h][j] = res[h][j].sub(l.mul(res[i][j]));
            }
        }
        for (int i = (countRes - 1); i >= 0; i--) {
            for (int h = (i - 1); h >= 0; h--) {
                l = res[h][i];
                for (int j = 0; j < (countX + 1); j++)
                    res[h][j] = res[h][j].sub(l.mul(res[i][j]));
            }
        }
    }

    private void F(Fraction[] f, Fraction[][] res) {
        Fraction[] f1 = new Fraction[countX + 1];
        System.arraycopy(f, 0, f1, 0, countX + 1);
        for (int i = 0; i < countRes; i++)
            for (int j = countRes; j < countX; j++)
                f[j] = f[j].add(res[i][j].changeSign().mul(f1[i]));
        for (int i = 0; i < countRes; i++)
            f[countX] = f[countX].add(res[i][countX].mul(f1[i]));
        f[countX] = f[countX].changeSign();
    }

    private void executionStartBasis(boolean[] start_basis, Fraction[] f, Fraction[][] res, int[] x) {
        for (int i = 0; i < countRes; i++) {
            if (!start_basis[i]) {
                for (int j = i + 1; j < countX; j++) {
                    if (start_basis[j]) {
                        Fraction b = f[i];
                        f[i] = f[j];
                        f[j] = b;
                        for (int h = 0; h < countRes; h++) {
                            b = res[h][i];
                            res[h][i] = res[h][j];
                            res[h][j] = b;
                        }
                        int a = x[i];
                        x[i] = x[j];
                        x[j] = a;
                        start_basis[i] = true;
                        start_basis[j] = false;
                        break;
                    }
                }
            }
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

    private void printTableAutomatic() {
        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);
        Text t1 = new Text("№" + number);
        t1.setFont(font);
        number++;
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
                if (simple_number) {
                    but[i][j].setText(Table[i][j].toString());
                } else {
                    but[i][j].setText(Double.toString(Table[i][j].getDouble()));
                }
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
        if (best[0] >= 0 && !checkFinish() && checkCorrectness()) {
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
                if (simple_number) {
                    but[i][j].setText(Table[i][j].toString());
                } else {
                    but[i][j].setText(Double.toString(Table[i][j].getDouble()));
                }
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
                        if (checkFinish()) {
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

    private void locButton(Button[][] but, Button button) {
        button.setOnAction((ActionEvent ee) -> {});
        for (int i = 0; i < Table.length - 1; i++) {
            for (int j = 0; j < Table[0].length - 1; j++) {
                but[i][j].setOnAction((ActionEvent ee) -> {});
            }
        }
    }

    private int[] TheBest() {
        double value = -1;
        double min = 0;
        int row = -1;
        int column = -1;
        for (int i = 0; i < countRes; i++) {
            for (int j = 0; j < countX - countRes; j++) {
                if (Table[countRes][j].getDouble() >= 0 || Table[i][j].getDouble() <= 0
                        || Table[i][countX - countRes].getDouble() < 0) {
                    continue;
                }
                if (Table[countRes][j].getDouble() < min) {
                    value = Table[i][countX - countRes].div(Table[i][j]).getDouble();
                    min = Table[countRes][j].getDouble();
                    row = i;
                    column = j;
                    continue;
                }
                if (Table[countRes][j].getDouble() == min
                        && Table[i][countX - countRes].div(Table[i][j]).getDouble() <= value) {
                    value = Table[i][countX - countRes].div(Table[i][j]).getDouble();
                    min = Table[countRes][j].getDouble();
                    row = i;
                    column = j;
                }
            }
        }
        return new int[]{row, column};
    }

    private void TheBestColumn(Button[][] but, int row, int column) {
        if (Table[countRes][column].getDouble() >= 0 || Table[row][column].getDouble() <= 0
                || Table[row][countX - countRes].getDouble() < 0)
            return;
        double b = Table[row][countX - countRes].div(Table[row][column]).getDouble();
        for (int i = 0; i < countRes; i++) {
            if (Table[i][column].getDouble() > 0 && Table[i][countX - countRes].div(Table[i][column]).getDouble() < b)
                return;
        }
        but[row][column].setStyle("-fx-background-color: yellow");
    }


    private boolean checkFinish() {
        return (checkFromAnswer() || checkImpossible()) && checkCorrectness();
    }

    private boolean checkCorrectness() {
        for (int i = 0; i < countRes; i++) {
            if (Table[i][countX - countRes].getDouble() < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean checkImpossible() {
        for (int j = 0; j < countX - countRes; j++)
            if (Table[countRes][j].getDouble() < 0) {
                int count = 0;
                for (int i = 0; i < countRes; i++)
                    if (Table[i][j].getDouble() > 0)
                        count += 1;
                if (count == 0)
                    return true;
            }
        return false;
    }

    public boolean checkFromAnswer() {
        for (int j = 0; j < countX - countRes; j++)
            if (Table[countRes][j].getDouble() < 0)
                return false;
        for (int i = 0; i < countRes; i++)
            if (Table[i][countX - countRes].getDouble() < 0)
                return false;
        return true;
    }

    private void printIncorrectness(){
        Text t = new Text("Данный бизис невозможен");
        t.setFont(font);
        GridPane.setHalignment(t, HPos.CENTER);
        pane.add(t, 0, order);
        order += 2;
    }

    public void printFinish() {
//        Невозможность решения
        if (checkImpossible()) {
            Text t = new Text("F - не ограничена");
            t.setFont(font);
            GridPane.setHalignment(t, HPos.CENTER);
            pane.add(t, 0, order);
            order += 2;
            return;
        }
//        Решение найдено
        if (checkFromAnswer()) {
            StringBuilder s = new StringBuilder(" Ответ: F(x) = " + Table[countRes][countX - countRes].changeSign() + "\n");
            Fraction[] ar = new Fraction[countX + 1];
            for (int i = 0; i < countX + 1; i++)
                ar[i] = new Fraction(0);
            for (int i = 0; i < countRes; i++)
                ar[vertical[i]] = Table[i][countX - countRes];
            for (int i = 1; i < countX + 1; i++)
                s.append(" X").append(i).append(" = ").append(ar[i]).append("\n");
            Text t = new Text(s.toString());
            t.setFont(font);
            GridPane.setHalignment(t, HPos.CENTER);
            pane.add(t, 0, order);
            order += 2;
        }
    }
}
