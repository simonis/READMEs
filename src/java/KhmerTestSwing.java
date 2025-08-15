import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.File;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import java.awt.Font;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class KhmerTestSwing {

  static class DrawTextLayout extends JPanel {
    private TextLayout textLayout;
    private Dimension preferredSize;
    private int height;
    public DrawTextLayout(TextLayout textLayout) {
      this.textLayout = textLayout;
      height = (int)(textLayout.getAscent()) + 5;
      preferredSize = new Dimension((int)textLayout.getBounds().getWidth(),
                                    (int)(height + textLayout.getLeading() + textLayout.getDescent()) + 10);
    }
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D)g;
      textLayout.draw(g2d, 0f, (float)height);
    }
    public Dimension getPreferredSize() {
      return preferredSize;
    }
  }

  public static void main(String[] args) throws Exception {
    String khmer_short = "បានស្នើសុំនៅតែត្រូវបានបដិសេធ";
    Font font;
    if (args.length > 0) {
      font = Font.createFont(Font.TRUETYPE_FONT, new File(args[0])).deriveFont(60f);
    } else {
      font = new Font(Font.DIALOG, Font.PLAIN, 60);
    }

    boolean swingUI = !Boolean.getBoolean("noSwingUI");
    JFrame frame = null;
    if (swingUI) {
      frame = new JFrame();
      frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JLabel label = new JLabel(khmer_short);
      label.setBorder(new EmptyBorder(10, 0, 20, 0));
      label.setFont(font);
      frame.add(label, "Center");
      frame.pack();
      frame.setVisible(true);
    }
    AttributedString attrStr = new AttributedString(khmer_short);
    attrStr.addAttribute(TextAttribute.FONT, font);
    AttributedCharacterIterator it = attrStr.getIterator();
    int width = 300; // Maximum width for the text
    Locale locale = new Locale.Builder().setLanguage("km").setRegion("KH").build();
    BreakIterator breakIterator = BreakIterator.getLineInstance(locale);
    FontRenderContext frc = new FontRenderContext(null, true, true);
    LineBreakMeasurer measurer = new LineBreakMeasurer(it, breakIterator, frc);
    int currentOffset = 0, nextOffset = 0;
    while (measurer.getPosition() < it.getEndIndex()) {
      if (swingUI) {
        TextLayout layout = measurer.nextLayout(width);
        int ccount = layout.getCharacterCount();
        int start = measurer.getPosition() - ccount;
        System.out.println("Segment: " +
                           khmer_short.substring(start, start + ccount) + " " +
                           start + " " + ccount);
        frame.add(new DrawTextLayout(layout));
        frame.pack();
      } else {
        currentOffset = nextOffset;
        nextOffset = measurer.nextOffset(width);
        measurer.setPosition(nextOffset);
        System.out.println("Segment: " +
                           khmer_short.substring(currentOffset, nextOffset) + " " +
                           currentOffset + " " + (nextOffset - currentOffset));
      }
    }
  }
}
