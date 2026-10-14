package vn.edu.ptit.oop.nhom04;

import javax.swing.SwingUtilities;
import vn.edu.ptit.oop.nhom04.ui.MainWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
