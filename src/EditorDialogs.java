import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.print.*;
import java.io.File;

// UTILITY CLASS TO HANDLE ALL POP-UP DIALOGS AND HEAVY LOGIC OPERATIONS
public class EditorDialogs
{
    private Main mainApp; // REFERENCE TO THE MAIN APPLICATION WINDOW

    public EditorDialogs(Main mainApp)
    {
        this.mainApp = mainApp;
    }

    // EXPORT TEXT CONTENT AS PDF
    public void exportToPDF()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("document.pdf"));

        int option = fileChooser.showSaveDialog(mainApp);

        if(option == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("EXPORT PDF");

                job.setPrintable((graphics, pageFormat, pageIndex) ->
                {
                    if(pageIndex > 0)
                        return Printable.NO_SUCH_PAGE;

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    mainApp.textArea.printAll(graphics);
                    return Printable.PAGE_EXISTS;
                });

                if(job.printDialog())
                    job.print();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    // SHOW FIND DIALOG FOR WORD SEARCH AND HIGHLIGHT
    public void showFindDialog()
    {
        JTextField findField = new JTextField(15);

        int result = JOptionPane.showConfirmDialog(
                mainApp,
                findField,
                "FIND",
                JOptionPane.OK_CANCEL_OPTION
        );

        if(result == JOptionPane.OK_OPTION)
        {
            String findText = findField.getText();
            highlightText(findText); // HIGHLIGHT ONLY
        }

        mainApp.textArea.requestFocusInWindow();
    }

    // SHOW FIND AND REPLACE DIALOG
    public void showFindAndReplaceDialog()
    {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField findField = new JTextField(10);
        JTextField replaceField = new JTextField(10);

        panel.add(new JLabel("FIND"));
        panel.add(findField);
        panel.add(new JLabel("REPLACE"));
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(mainApp, panel, "FIND & REPLACE", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION)
        {
            String findText = findField.getText();
            String replaceText = replaceField.getText();

            if(findText != null && !findText.isEmpty())
            {
                String text = mainApp.textArea.getText();
                int index = text.lastIndexOf(findText);

                mainApp.textArea.requestFocusInWindow();

                while(index >= 0)
                {
                    mainApp.textArea.replaceRange(replaceText, index, index + findText.length());
                    index = text.lastIndexOf(findText, index - 1);
                }

                highlightText(replaceText);
            }
            else
            {
                highlightText(findText);
                mainApp.textArea.requestFocusInWindow();
            }
        }

        mainApp.textArea.requestFocusInWindow();
    }

    // HIGHLIGHT ALL OCCURRENCES OF SEARCH TEXT
    public void highlightText(String pattern)
    {
        Highlighter highlighter = mainApp.textArea.getHighlighter();
        highlighter.removeAllHighlights();

        if(pattern == null || pattern.isEmpty())
            return;

        String text = mainApp.textArea.getText().toLowerCase();
        pattern = pattern.toLowerCase();

        int index = 0;

        while((index = text.indexOf(pattern, index)) != -1)
        {
            try
            {
                highlighter.addHighlight(index, index + pattern.length(), mainApp.highlightPainter);
                index += pattern.length();
            }
            catch(BadLocationException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    // SHOW FONT SELECTION DIALOG
    public void showFontChooser()
    {
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontComboBox = new JComboBox<>(fontNames);
        JComboBox<Integer> sizeComboBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 36, 40});

        JPanel panel = new JPanel();
        panel.add(new JLabel("FONT : "));
        panel.add(fontComboBox);
        panel.add(new JLabel("SIZE : "));
        panel.add(sizeComboBox);

        int result = JOptionPane.showConfirmDialog(mainApp, panel, "SELECT FONT", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION)
        {
            String selectedFont = (String) fontComboBox.getSelectedItem();
            int selectedSize = (Integer) sizeComboBox.getSelectedItem();
            Font newFont = new Font(selectedFont, Font.PLAIN, selectedSize);

            mainApp.applyDynamicFont(newFont);
        }
    }

    // SHOW COLOR SELECTION DIALOG
    public void showColorChooser()
    {
        Color newColor = JColorChooser.showDialog(mainApp, "SELECT TEXT COLOR", mainApp.textArea.getForeground());

        if(newColor != null)
        {
            mainApp.textArea.setForeground(newColor);
        }
    }

    // SHOW GO TO LINE DIALOG AND JUMP TO SPECIFIED LINE
    public void showGoToLineDialog()
    {
        String input = JOptionPane.showInputDialog(
                mainApp,
                "ENTER LINE NUMBER :",
                "GO TO LINE",
                JOptionPane.QUESTION_MESSAGE
        );

        if(input != null && !input.trim().isEmpty())
        {
            try
            {
                int lineNumber = Integer.parseInt(input.trim());
                int totalLines = mainApp.textArea.getLineCount();

                if(lineNumber < 1 || lineNumber > totalLines)
                {
                    JOptionPane.showMessageDialog(mainApp, "LINE NUMBER OUT OF RANGE (1 - " + totalLines + ").", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int offset = mainApp.textArea.getLineStartOffset(lineNumber - 1);
                mainApp.textArea.setCaretPosition(offset);
                mainApp.textArea.requestFocusInWindow();
            }
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(mainApp, "INVALID INPUT. PLEASE ENTER A VALID NUMBER.", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
            catch(BadLocationException ex)
            {
                ex.printStackTrace();
            }
        }
        else
        {
            mainApp.textArea.requestFocusInWindow();
        }
    }

    // CALCULATE AND DISPLAY DETAILED TEXT STATISTICS
    public void showTextStatistics()
    {
        String text = mainApp.textArea.getText();

        int paragraphs = 0;
        int vowels = 0;
        int consonants = 0;
        String longestWord = "N/A";
        int totalWordLength = 0;
        double averageWordLength = 0.0;

        if(!text.trim().isEmpty())
        {
            paragraphs = text.trim().split("\\n+").length;

            for(char c : text.toLowerCase().toCharArray())
            {
                if(Character.isLetter(c))
                {
                    if("aeiou".indexOf(c) != -1) vowels++;
                    else consonants++;
                }
            }

            String[] words = text.trim().split("\\s+");
            longestWord = "";

            for(String word : words)
            {
                String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "");
                totalWordLength += cleanWord.length();

                if(cleanWord.length() > longestWord.length())
                {
                    longestWord = cleanWord;
                }
            }

            if(words.length > 0)
            {
                averageWordLength = (double) totalWordLength / words.length;
            }

            if(longestWord.isEmpty()) longestWord = "N/A";
        }

        String statsMessage = String.format(
                "TOTAL PARAGRAPHS : %d\n" +
                        "NUMBER OF VOWELS : %d\n" +
                        "NUMBER OF CONSONANTS : %d\n" +
                        "LONGEST WORD : \"%s\"\n" +
                        "AVERAGE WORD LENGTH : %.2f CHARACTERS",
                paragraphs, vowels, consonants, longestWord, averageWordLength
        );

        JOptionPane.showMessageDialog(mainApp, statsMessage, "TEXT STATISTICS", JOptionPane.INFORMATION_MESSAGE);
        mainApp.textArea.requestFocusInWindow();
    }
}