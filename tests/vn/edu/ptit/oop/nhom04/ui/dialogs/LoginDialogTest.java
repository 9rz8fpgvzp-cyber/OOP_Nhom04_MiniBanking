package vn.edu.ptit.oop.nhom04.ui.dialogs;

import java.awt.*;
import java.lang.reflect.*;
import java.time.*;
import javax.swing.*;
import vn.edu.ptit.oop.nhom04.service.PinService;
import vn.edu.ptit.oop.nhom04.ui.dialogs.LoginDialog;

public class LoginDialogTest {
    static int checks;
    static class ClockForTest extends Clock {
        Instant now=Instant.parse("2026-10-27T02:00:00Z");
        public ZoneId getZone(){return ZoneOffset.UTC;}
        public Clock withZone(ZoneId z){return this;}
        public Instant instant(){return now;}
    }
    static class Pins extends PinService {
        int attempts;
        Pins(Clock c){super(c);}
        @Override public synchronized boolean authenticate(String id,char[] pin){attempts++;return super.authenticate(id,pin);}
    }
    static <T>T field(Object target,String name,Class<T> type){try{Field f=target.getClass().getDeclaredField(name);f.setAccessible(true);return type.cast(f.get(target));}catch(Exception e){throw new RuntimeException(e);}}
    static void tick(LoginDialog dialog){try{Method m=LoginDialog.class.getDeclaredMethod("updateLockStatus");m.setAccessible(true);m.invoke(dialog);}catch(Exception e){throw new RuntimeException(e);}}
    static JButton button(Container container,String label){for(Component child:container.getComponents()){if(child instanceof JButton b&&b.getText().equals(label))return b;if(child instanceof Container c){JButton result=button(c,label);if(result!=null)return result;}}return null;}
    static void check(boolean b,String name){checks++;if(!b)throw new AssertionError(name);}
    static void attempt(LoginDialog d,String user,String pin){JTextField uf=field(d,"userField",JTextField.class);if(!uf.getText().equals(user))uf.setText(user);field(d,"pinField",JPasswordField.class).setText(pin);field(d,"loginButton",JButton.class).doClick(0);}
    public static void main(String[] args)throws Exception{
        try{
            SwingUtilities.invokeAndWait(()->{
                ClockForTest c=new ClockForTest();Pins p=new Pins(c);p.registerPin("A","001234".toCharArray());p.registerPin("B","234567".toCharArray());
                LoginDialog d=new LoginDialog(null,p);
                check(!field(d,"loginButton",JButton.class).isEnabled(),"blank user button disabled");
                attempt(d,"A","999999");check(p.getRemainingAttempts("A")==2,"first wrong");
                check(field(d,"messageLabel",JLabel.class).getText().contains("2 lần"),"remaining attempts shown");
                check(field(d,"pinField",JPasswordField.class).getPassword().length==0,"field cleared after failure");
                attempt(d,"A","999999");attempt(d,"A","999999");check(p.isLocked("A"),"third locks");
                check(field(d,"messageLabel",JLabel.class).getText().contains("30 giây"),"countdown shown");
                attempt(d,"A","001234");check(p.getAuthenticatedCustomerId().isEmpty(),"correct PIN cannot bypass lock");
                field(d,"userField",JTextField.class).setText("B");check(field(d,"loginButton",JButton.class).isEnabled(),"switch B allowed");
                check(p.isLocked("A"),"switch preserves A lock");d.dispose();check(!field(d,"countdown",Timer.class).isRunning(),"dispose stops timer");
                LoginDialog cancel=new LoginDialog(null,p);field(cancel,"userField",JTextField.class).setText("A");
                int before=p.attempts;
                SwingUtilities.invokeLater(()->button(cancel.getContentPane(),"Hủy").doClick(0));
                check(!cancel.showDialog(),"cancel returns false");check(p.attempts==before,"cancel does not authenticate");
                check(p.isLocked("A"),"reopening and cancelling preserves lock");check(!field(cancel,"countdown",Timer.class).isRunning(),"cancel stops timer");
                LoginDialog unknown=new LoginDialog(null,p);attempt(unknown,"UNKNOWN","001234");
                check(field(unknown,"messageLabel",JLabel.class).getText().contains("Không tìm thấy khách hàng"),"unknown user message");unknown.dispose();
                LoginDialog expiry=new LoginDialog(null,p);field(expiry,"userField",JTextField.class).setText("A");
                c.now=c.now.plusMillis(29_999);tick(expiry);check(field(expiry,"messageLabel",JLabel.class).getText().contains("1 giây"),"last fraction shows 1 second");
                c.now=c.now.plusMillis(1);tick(expiry);check(field(expiry,"messageLabel",JLabel.class).getText().contains("Đã hết khóa"),"expiry shown");
                check(!p.isAuthenticated("A"),"expiry does not login");expiry.dispose();
                LoginDialog success=new LoginDialog(null,p);
                SwingUtilities.invokeLater(()->attempt(success," A ","001234"));
                check(success.showDialog(),"modal success returns true");
                check(p.getAuthenticatedCustomerId().orElseThrow().equals("A"),"dialog trims input");
                check(field(success,"pinField",JPasswordField.class).getPassword().length==0,"PIN field cleared after success");
                check(!field(success,"countdown",Timer.class).isRunning(),"success stops timer");
                LoginDialog close=new LoginDialog(null,p);before=p.attempts;
                SwingUtilities.invokeLater(()->close.dispatchEvent(new java.awt.event.WindowEvent(close,java.awt.event.WindowEvent.WINDOW_CLOSING)));
                check(!close.showDialog(),"X returns false");check(p.attempts==before,"X does not authenticate");
                check(!field(close,"countdown",Timer.class).isRunning(),"X stops timer");
                System.out.println("LoginDialogTest: PASS ("+checks+" checks)");
            });
        }finally{SwingUtilities.invokeAndWait(()->{for(Window w:Window.getWindows())w.dispose();});}
    }
}
