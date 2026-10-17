package vn.edu.ptit.oop.nhom04.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import vn.edu.ptit.oop.nhom04.common.BaseWindow;

public class MainWindow extends BaseWindow {
	private static final long serialVersionUID = 1L;
	
    public MainWindow() {
        super("MiniBank — Nhóm 04", 1100, 700);
    }

    @Override
    protected void initUI() {
        JLabel titleLabel = new JLabel("MiniBank — Nhóm 04",
                SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        add(titleLabel, BorderLayout.CENTER);
        setStatus("Sẵn sàng");
        
    }
}
