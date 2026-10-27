package vn.edu.ptit.oop.nhom04.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.lang.instrument.Instrumentation;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** Chỉ dùng bởi test: đóng bản JAR thử sau khi xác nhận màn hình khởi động. */
public final class JarLaunchSmoke {
    public static void premain(String argument, Instrumentation instrumentation) {
        long deadline = System.nanoTime() + 10_000_000_000L;
        SwingUtilities.invokeLater(() -> {
            Timer observer = new Timer(50, null);
            observer.addActionListener(event -> {
                try {
                    Window main = null;
                    boolean login = false;
                    for (Window window : Window.getWindows()) {
                        if (window.isShowing() && window.getClass().getSimpleName().equals("MainWindow")) { main = window; }
                        if (window.isShowing() && window.getClass().getSimpleName().equals("LoginDialog")) { login = true; }
                    }
                    if (main != null && login) {
                        JList<?> list = accountList(main);
                        if (list == null || list.getModel().getSize() != 0 || ((Frame) main).getIconImages().isEmpty()) {
                            throw new AssertionError("JAR thiếu icon hoặc lộ STK trước đăng nhập");
                        }
                        System.out.println("PackagedLaunch: PASS (JAR, icon, login, no accounts before login)");
                        observer.stop();
                        for (Window window : Window.getWindows()) { window.dispose(); }
                        System.exit(0);
                    }
                    if (System.nanoTime() > deadline) { throw new AssertionError("JAR chưa mở màn hình đăng nhập"); }
                } catch (Throwable failure) {
                    failure.printStackTrace();
                    System.exit(1);
                }
            });
            observer.start();
        });
    }

    private static JList<?> accountList(Container parent) {
        for (Component child : parent.getComponents()) {
            if (child instanceof JList<?> list && "accountList".equals(list.getName())) { return list; }
            if (child instanceof Container nested) {
                JList<?> found = accountList(nested);
                if (found != null) { return found; }
            }
        }
        return null;
    }
}
