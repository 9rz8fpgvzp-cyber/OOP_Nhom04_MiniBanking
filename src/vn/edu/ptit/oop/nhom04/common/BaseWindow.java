package vn.edu.ptit.oop.nhom04.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public abstract class BaseWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
    protected JMenuBar menuBar;
    protected JMenu fileMenu, viewMenu, helpMenu;
    protected JToolBar toolBar;
    protected JPanel statusBar;
    protected JLabel statusLabel;

    public BaseWindow(String title, int width, int height) {
        super(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        loadIcon();
        initMenuBar();
        initToolBar();
        initStatusBar();
        initUI();
    }

    protected abstract void initUI();

    protected void showAbout() {
        JOptionPane.showMessageDialog(this, getTitle(), "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    protected void setStatus(String msg) {
        statusLabel.setText(" " + msg);
    }

    protected static ImageIcon loadImageIcon(String path) {
        URL url = BaseWindow.class.getResource(path);
        return url != null ? new ImageIcon(url) : null;
    }

    protected JButton addToolButton(String iconPath, String tooltip,
            ActionListener action) {
        ImageIcon icon = loadImageIcon(iconPath);
        JButton btn = icon != null ? new JButton(icon) : new JButton(tooltip);
        btn.setToolTipText(tooltip);
        btn.setFocusable(false);
        btn.addActionListener(action);
        toolBar.add(btn);
        return btn;
    }

    private void loadIcon() {
        ImageIcon icon = loadImageIcon("/resources/icons/app.png");
        if (icon != null) {
            setIconImage(icon.getImage());
        }
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        exitItem.addActionListener(e -> dispatchEvent(
                new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        fileMenu.add(exitItem);

        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);
    }

    private void initStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel(" Sẵn sàng");
        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }
}
