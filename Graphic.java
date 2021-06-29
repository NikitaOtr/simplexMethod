package example;

import javafx.geometry.HPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Graphic {
    private final Stage window = new Stage();

    private final int countX;
    private final int countRes;
    private final Fraction[] fun;
    private final Fraction[][] con;
    private final int[] horizontal;

    private final Font font = Font.font("Tahoma", FontWeight.NORMAL, 15);

    public Graphic(int countX, int countRes, boolean simple_number, boolean[] start_basis, Fraction[] f, Fraction[][] res) {
        this.countX = countX;
        this.countRes = countRes;
        fun = new Fraction[3];
        con = new Fraction[countRes][3];
        horizontal = new int[countX];

        for (int j = 0; j < countX; j++) {
            horizontal[j] = j + 1;
        }
        boolean[] b = new boolean[countX];
        for (int j = 0; j < countX; j++) {
            b[j] = start_basis[j];
        }
        createHorizontal(b, horizontal);
        executionStartBasis(start_basis, f, res);
        methodG(res);
        F(f, res);
        for (int j = 0; j < (countX - countRes + 1); j++) {
            fun[j] = f[countRes + j];
        }
        for (int i = 0; i < countRes; i++) {
            for (int j = 0; j < (countX - countRes + 1); j++) {
                con[i][j] = res[i][countRes + j];
            }
        }
    }

    public void show() {
        window.setTitle("Графический метод");
        GridPane rood = new GridPane();

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(picture());
        rood.add(scroll, 0, 0);

        rood.add(print(), 2, 0);

        window.setScene(new Scene(rood, 1400, 780));
        window.showAndWait();
    }


    private void executionStartBasis(boolean[] start_basis, Fraction[] f, Fraction[][] res) {
        for (int i = 0; i < countRes; i++) {
            if (start_basis[i]) {
                for (int j = i + 1; j < countX; j++) {
                    if (!start_basis[j]) {
                        Fraction b = f[i];
                        f[i] = f[j];
                        f[j] = b;
                        for (int h = 0; h < countRes; h++) {
                            b = res[h][i];
                            res[h][i] = res[h][j];
                            res[h][j] = b;
                        }
                        start_basis[i] = false;
                        start_basis[j] = true;
                        break;
                    }
                }
            }
        }
    }

    private void createHorizontal(boolean[] start_basis, int[] x) {
        for (int i = 0; i < countRes; i++) {
            if (!start_basis[i]) {
                for (int j = i + 1; j < countX; j++) {
                    if (start_basis[j]) {
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
        f[countX] = f[countX];
    }


    private Group picture() {
        Group group = new Group();
        group.getChildren().addAll(
                new Line(500, 0, 500, 1000),
                new Line(0, 500, 1000, 500)
        );
        return group;
    }

    public GridPane print() {
        GridPane root = new GridPane();
        Text t1 = new Text(" Функция");
        t1.setFont(font);
        root.add(t1, 0, 0);
        GridPane.setHalignment(t1, HPos.CENTER);
        for (int j = 0; j < 2; j++) {
            Text t = new Text(" X" + horizontal[j] + " ");
            t.setFont(font);
            root.add(t, j, 1);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Text t11 = new Text(" B");
        t11.setFont(font);
        root.add(t11, 2, 1);
        GridPane.setHalignment(t11, HPos.CENTER);

        for (int j = 0; j < 2; j++) {
            Text t = new Text(fun[j].toString() + " ");
            t.setFont(font);
            root.add(t, j, 2);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Text t22 = new Text(" = " + fun[2].toString() + " ");
        t22.setFont(font);
        root.add(t22, 2, 2);
        GridPane.setHalignment(t22, HPos.CENTER);

        Text t2 = new Text(" Неравенства");
        t2.setFont(font);
        root.add(t2, 0, 3);
        GridPane.setHalignment(t2, HPos.CENTER);
        for (int j = 0; j < 2; j++) {
            Text t = new Text("X" + horizontal[j] + " ");
            t.setFont(font);
            root.add(t, j, 4);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        Text t12 = new Text("B");
        t12.setFont(font);
        root.add(t12, 2, 4);
        GridPane.setHalignment(t12, HPos.CENTER);
        for (int i = 0; i < countRes; i++) {
            for (int j = 0; j < 2; j++) {
                Text t = new Text(con[i][j].toString() + " ");
                t.setFont(font);
                root.add(t, j, 5 + i);
                GridPane.setHalignment(t, HPos.CENTER);
            }
            Text t = new Text(" <= " + con[i][2].toString() + " ");
            t.setFont(font);
            root.add(t, 2, 5 + i);
            GridPane.setHalignment(t, HPos.CENTER);
        }
        return root;
    }
}