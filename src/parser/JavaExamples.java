package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

// https://github.com/mfornos/clojure-soup
// https://jsoup.org/cookbook/extracting-data/selector-syntax
/*
  (def jobs-link
  "https://www.job.kg/resume/search?&category%5B%5D=4&category%5B%5D=749&category%5B%5D=750&category%5B%5D=751&age%5Bmin%5D=18&age%5Bmax%5D=30&gender%5B%5D=2")

  (let [content (.. Jsoup
  (connect jobs-link)
  get
  (select "a[href]")
  #_body
  #_(getElementsByClass "vrl-contacts"
  #_"pad-"
  #_"vrlo-base-alternative"
  #_"cabinet-content"))
  contact (first content)]
  contact
  #_(.. contact (getElementsByClass "vrl-contacts"))
  #_(soup/text content))
 */
public class JavaExamples {

    private static final String LINK
            = "https://www.job.kg/resume/search?&category%5B%5D=4&category%5B%5D=749&category%5B%5D=750&category%5B%5D=751&age%5Bmin%5D=18&age%5Bmax%5D=30&gender%5B%5D=2";

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();

            f.getContentPane().setBackground(new Color(0, 0, 255));
            f.getContentPane();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(new Dimension(100, 100));
            f.setVisible(true);
        });

        Robot robot = new Robot();
        robot.keyPress(1);
        robot.delay(100);
    }
}
