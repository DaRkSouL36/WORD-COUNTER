import javax.swing.*; // IMPORT FOR JTEXTAREA
import java.awt.*;    // IMPORT FOR COLOR AND INSETS

// COMPONENT TO DISPLAY LINE NUMBERS
public class LineNumberView extends JTextArea
{
    JTextArea textArea;

    public LineNumberView(JTextArea textArea)
    {
        this.textArea = textArea;
        setBackground(Color.LIGHT_GRAY);
        setEditable(false);
        setFont(textArea.getFont());

        setMargin(new Insets(0, 2, 0, 2));
        setFocusable(false);
    }

    public void updateLineNumbers()
    {
        int lines = textArea.getLineCount();
        StringBuilder builder = new StringBuilder();

        for(int i = 1; i <= lines; i++)
        {
            builder.append(i).append(System.lineSeparator());
        }

        setText(builder.toString());
    }
}