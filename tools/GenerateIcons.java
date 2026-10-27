import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import javax.imageio.ImageIO;

/** Tái tạo các PNG bằng JDK: java tools/GenerateIcons.java. */
public class GenerateIcons {
    public static void main(String[] args) throws Exception {
        Path folder = Path.of("src/resources/icons");
        Files.createDirectories(folder);
        for (String name : new String[]{"app", "login", "logout", "deposit", "withdraw", "transfer", "csv", "month", "refresh"}) {
            int size = name.equals("app") ? 64 : 16;
            BufferedImage large = new BufferedImage(size * 4, size * 4, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = large.createGraphics();
            g.scale(size / 4.0, size / 4.0);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(28, 91, 110));
            g.fill(new RoundRectangle2D.Double(0, 0, 16, 16, 3, 3));
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.35f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (name) {
                case "app":
                    g.draw(new Line2D.Double(3, 6, 8, 3)); g.draw(new Line2D.Double(8, 3, 13, 6));
                    g.drawLine(3, 7, 13, 7); g.drawLine(3, 13, 13, 13);
                    for (int x : new int[]{4, 8, 12}) g.drawLine(x, 8, x, 12);
                    break;
                case "deposit": case "withdraw":
                    g.drawOval(3, 3, 10, 10); g.drawLine(5, 8, 11, 8);
                    if (name.equals("deposit")) g.drawLine(8, 5, 8, 11);
                    break;
                case "transfer":
                    g.drawLine(3, 5, 12, 5); g.drawLine(10, 3, 12, 5); g.drawLine(10, 7, 12, 5);
                    g.drawLine(4, 11, 13, 11); g.drawLine(6, 9, 4, 11); g.drawLine(6, 13, 4, 11);
                    break;
                case "login": case "logout":
                    g.drawRect(3, 3, 5, 10); g.setColor(new Color(28, 91, 110)); g.fillRect(6, 6, 4, 4); g.setColor(Color.WHITE);
                    int end = name.equals("login") ? 7 : 13;
                    g.drawLine(6, 8, 13, 8); g.drawLine(end, 8, end + (end == 7 ? 2 : -2), 6);
                    g.drawLine(end, 8, end + (end == 7 ? 2 : -2), 10);
                    break;
                case "csv":
                    g.drawRect(4, 2, 8, 12); g.drawLine(6, 6, 10, 6); g.drawLine(6, 9, 10, 9); g.drawLine(8, 6, 8, 12);
                    break;
                case "month":
                    g.drawRect(3, 4, 10, 9); g.drawLine(3, 7, 13, 7); g.drawLine(5, 2, 5, 5); g.drawLine(11, 2, 11, 5);
                    g.drawLine(6, 10, 7, 11); g.drawLine(7, 11, 10, 9);
                    break;
                default:
                    g.draw(new Arc2D.Double(3, 3, 10, 10, 30, 285, Arc2D.OPEN));
                    g.drawLine(12, 3, 12, 6); g.drawLine(12, 6, 9, 6);
            }
            g.dispose();
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D down = image.createGraphics();
            down.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            down.drawImage(large, 0, 0, size, size, null); down.dispose();
            ImageIO.write(image, "png", folder.resolve(name + ".png").toFile());
        }
    }
}
