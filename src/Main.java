import java.util.*; // IMPORT FOR COLLECTIONS USED IN SENTENCE COUNTING
import javax.swing.*; // IMPORT SWING LIBRARY FOR GUI COMPONENTS
import javax.swing.text.*; // IMPORT FOR TEXT HIGHLIGHTING
import javax.swing.border.EmptyBorder; // IMPORT FOR BORDER STYLES
import javax.swing.event.DocumentEvent; // IMPORT FOR DOCUMENT EVENT HANDLING
import javax.swing.event.DocumentListener; // IMPORT FOR DOCUMENT LISTENER INTERFACE
import javax.swing.filechooser.FileNameExtensionFilter; // IMPORT FOR FILE FILTERING IN FILE CHOOSER
import javax.swing.undo.UndoManager; // IMPORT FOR UNDO/REDO FUNCTIONALITY
import java.awt.*; // IMPORT FOR AWT (ABSTRACT WINDOW TOOLKIT) COMPONENTS
import java.awt.print.*;   // IMPORT FOR PRINTING
import java.awt.event.ActionEvent; // IMPORT FOR ACTION EVENT HANDLING
import java.io.*; // IMPORT FOR FILE HANDLING
import java.awt.event.KeyEvent; // IMPORT FOR KEYBOARD SHORTCUT KEYS

public class Main extends JFrame
{
    // DECLARING GUI COMPONENTS (TEXTAREA, LABELS, BUTTONS, PANELS, ETC.)
    JTextArea textArea; // TEXTAREA FOR ENTERING AND DISPLAYING TEXT
    JLabel charCountLabel, wordCountLabel, sentenceCountLabel, statusBar; // LABELS TO DISPLAY CHARACTER, WORD, AND SENTENCE COUNTS; CURRENT LINE AND COLUMN POSITION
    JButton clearButton, exitButton, darkModeButton; // BUTTONS FOR CLEARING TEXT, EXITING, AND TOGGLING DARK MODE
    JPanel buttonPanel, countPanel, bottomPanel; // PANELS FOR BUTTONS, COUNTS, AND BOTTOM SECTION
    UndoManager undoManager; // UNDO MANAGER TO HANDLE UNDO/REDO ACTIONS
    boolean isDarkMode = false; // FLAG TO TOGGLE DARK MODE
    LineNumberView lineNumbers; // TO COUNT LINE NUMBERS
    Highlighter.HighlightPainter highlightPainter; // HIGHLIGHT PAINTER FOR SEARCH

    // CONSTRUCTOR TO SET UP THE FRAME AND INITIALIZE COMPONENTS
    public Main()
    {
        super("WORD COUNTER"); // SET FRAME TITLE
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // CLOSE APPLICATION ON WINDOW CLOSE
        setSize(1000, 500); // SET WINDOW SIZE (WIDTH: 1000px, HEIGHT: 500px)
        setLocationRelativeTo(null); // CENTER THE FRAME ON SCREEN
        setLayout(new BorderLayout()); // SET THE FRAME'S LAYOUT TO BORDER LAYOUT
        getContentPane().setBackground(Color.WHITE); // SET BACKGROUND COLOR OF THE FRAME TO WHITE
        highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW); // HIGHLIGHT COLOR

        // INITIALIZE TEXT AREA
        textArea = new JTextArea();
        textArea.setFont(new Font("Times New Roman", Font.BOLD, 16)); // SET FONT TO TIMES NEW ROMAN, BOLD, SIZE 16
        textArea.setLineWrap(false); // DISABLE LINE WRAPPING IN THE TEXT AREA
        textArea.setWrapStyleWord(true); // ENABLE WORD-WRAPPING IN THE TEXT AREA

        // INITIALIZE UNDO MANAGER TO HANDLE UNDO AND REDO ACTIONS
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager); // ATTACH UNDO MANAGER TO DOCUMENT CHANGES
        textArea.getDocument().addDocumentListener(new Count()); // ADD DOCUMENT LISTENER TO UPDATE COUNTS

        // CREATE LINE NUMBER COMPONENT FIRST
        lineNumbers = new LineNumberView(textArea);
        lineNumbers.updateLineNumbers();

        // CREATE A SCROLLPANE TO HOLD THE TEXT AREA (SCROLLING TEXT)
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // ADD PADDING TO SCROLLPANE BORDER

        // ADD CARET LISTENER TO TRACK LINE AND COLUMN POSITION
        textArea.addCaretListener(e -> updateStatusBar());

        // INITIALIZE LABELS FOR CHARACTER COUNT, WORD COUNT, AND SENTENCE COUNT
        charCountLabel = new JLabel("CHARACTER COUNT : 0");
        charCountLabel.setHorizontalAlignment(SwingConstants.CENTER); // CENTER ALIGN TEXT
        charCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18)); // SET FONT SIZE AND STYLE
        charCountLabel.setForeground(Color.DARK_GRAY); // SET TEXT COLOR TO DARK GRAY

        wordCountLabel = new JLabel("WORD COUNT : 0");
        wordCountLabel.setHorizontalAlignment(SwingConstants.CENTER); // CENTER ALIGN TEXT
        wordCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18)); // SET FONT SIZE AND STYLE
        wordCountLabel.setForeground(Color.DARK_GRAY); // SET TEXT COLOR TO DARK GRAY

        sentenceCountLabel = new JLabel("SENTENCE COUNT : 0");
        sentenceCountLabel.setHorizontalAlignment(SwingConstants.CENTER); // CENTER ALIGN TEXT
        sentenceCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18)); // SET FONT SIZE AND STYLE
        sentenceCountLabel.setForeground(Color.DARK_GRAY); // SET TEXT COLOR TO DARK GRAY

        // INITIALIZE STATUS BAR TO DISPLAY CARET POSITION
        statusBar = new JLabel("LINE : 1 | COLUMN : 1");
        statusBar.setFont(new Font("Times New Roman", Font.BOLD, 14));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);

        // =========================================================================================
        // DEFINE ALL ABSTRACT ACTIONS
        // =========================================================================================

        Action openAction = new AbstractAction("OPEN")
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showOpenDialog(Main.this);
                if(option == JFileChooser.APPROVE_OPTION)
                {
                    try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile())))
                    {
                        textArea.read(reader, null);
                        lineNumbers.updateLineNumbers();
                        undoManager.discardAllEdits();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        openAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action saveAction = new AbstractAction("SAVE")
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showSaveDialog(Main.this);
                if(option == JFileChooser.APPROVE_OPTION)
                {
                    try
                    {
                        File file = fileChooser.getSelectedFile();
                        if(!file.getName().toLowerCase().endsWith(".txt"))
                        {
                            file = new File(file.getAbsolutePath() + ".txt");
                        }
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        textArea.write(writer);
                        writer.close();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        saveAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action exportPdfAction = new AbstractAction("EXPORT AS PDF")
        {
            public void actionPerformed(ActionEvent e) { exportToPDF(); }
        };
        exportPdfAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action undoAction = new AbstractAction("UNDO")
        {
            public void actionPerformed(ActionEvent e)
            {
                try { if(undoManager.canUndo()) undoManager.undo(); }
                catch(Exception ex) { ex.printStackTrace(); }
            }
        };
        undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action redoAction = new AbstractAction("REDO")
        {
            public void actionPerformed(ActionEvent e)
            {
                try { if(undoManager.canRedo()) undoManager.redo(); }
                catch(Exception ex) { ex.printStackTrace(); }
            }
        };
        redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action findAction = new AbstractAction("FIND")
        {
            public void actionPerformed(ActionEvent e) { showFindDialog(); }
        };
        findAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action findReplaceAction = new AbstractAction("FIND & REPLACE")
        {
            public void actionPerformed(ActionEvent e) { showFindAndReplaceDialog(); }
        };
        findReplaceAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action goToLineAction = new AbstractAction("GO TO LINE")
        {
            public void actionPerformed(ActionEvent e) { showGoToLineDialog(); }
        };
        goToLineAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action fontAction = new AbstractAction("FONT...")
        {
            public void actionPerformed(ActionEvent e) { showFontChooser(); }
        };

        Action colorAction = new AbstractAction("COLOR...")
        {
            public void actionPerformed(ActionEvent e) { showColorChooser(); }
        };

        Action zoomInAction = new AbstractAction("ZOOM IN")
        {
            public void actionPerformed(ActionEvent e) { zoomIn(); }
        };
        zoomInAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action zoomOutAction = new AbstractAction("ZOOM OUT")
        {
            public void actionPerformed(ActionEvent e) { zoomOut(); }
        };
        zoomOutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action clearAction = new AbstractAction("CLEAR TEXT")
        {
            public void actionPerformed(ActionEvent e) { textArea.setText(""); }
        };
        clearAction.putValue(Action.SHORT_DESCRIPTION, "CLEAR ALL TEXT IN THE TEXT AREA");

        Action exitAction = new AbstractAction("EXIT")
        {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        };
        exitAction.putValue(Action.SHORT_DESCRIPTION, "EXIT THE APPLICATION");

        Action darkModeAction = new AbstractAction("DARK MODE")
        {
            public void actionPerformed(ActionEvent e) { toggleDarkMode(); }
        };
        darkModeAction.putValue(Action.SHORT_DESCRIPTION, "TOGGLE DARK MODE");

        Action textStatsAction = new AbstractAction("TEXT STATISTICS")
        {
            public void actionPerformed(ActionEvent e) { showTextStatistics(); }
        };
        // ASSIGN CTRL + I (FOR INFORMATION/INSPECTION) AS SHORTCUT
        textStatsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        // =========================================================================================
        // INITIALIZE BUTTONS USING ACTIONS
        // =========================================================================================

        clearButton = new JButton(clearAction);
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(Color.BLUE);
        clearButton.setFocusPainted(false);

        exitButton = new JButton(exitAction);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(Color.RED);
        exitButton.setFocusPainted(false);

        darkModeButton = new JButton(darkModeAction);
        darkModeButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        darkModeButton.setForeground(Color.WHITE);
        darkModeButton.setBackground(Color.DARK_GRAY);
        darkModeButton.setFocusPainted(false);

        // CREATE BUTTON PANEL AND ADD BUTTONS
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(darkModeButton);

        // =========================================================================================
        // INITIALIZE MENU BAR USING ACTIONS
        // =========================================================================================

        JMenuBar menuBar = new JMenuBar();

        // FILE MENU
        JMenu fileMenu = new JMenu("FILE");
        fileMenu.add(new JMenuItem(openAction));
        fileMenu.add(new JMenuItem(saveAction));
        fileMenu.add(new JMenuItem(exportPdfAction));
        menuBar.add(fileMenu);

        // EDIT MENU
        JMenu editMenu = new JMenu("EDIT");
        editMenu.add(new JMenuItem(undoAction));
        editMenu.add(new JMenuItem(redoAction));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(findAction));
        editMenu.add(new JMenuItem(findReplaceAction));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(goToLineAction));
        menuBar.add(editMenu);

        // FORMAT MENU
        JMenu formatMenu = new JMenu("FORMAT");
        formatMenu.add(new JMenuItem(fontAction));
        formatMenu.add(new JMenuItem(colorAction));
        menuBar.add(formatMenu);

        // VIEW MENU
        JMenu viewMenu = new JMenu("VIEW");
        viewMenu.add(new JMenuItem(zoomInAction));
        viewMenu.add(new JMenuItem(zoomOutAction));
        menuBar.add(viewMenu);

        // TOOLS MENU (NEW MENU)
        JMenu toolsMenu = new JMenu("TOOLS");
        toolsMenu.add(new JMenuItem(textStatsAction));
        menuBar.add(toolsMenu);

        // SET MENU BAR
        setJMenuBar(menuBar);

        // =========================================================================================
        // ASSEMBLE PANELS AND FRAME COMPONENTS
        // =========================================================================================

        countPanel = new JPanel(new BorderLayout());
        countPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        countPanel.add(charCountLabel, BorderLayout.WEST);
        countPanel.add(wordCountLabel, BorderLayout.CENTER);
        countPanel.add(sentenceCountLabel, BorderLayout.EAST);

        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(countPanel, BorderLayout.NORTH);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        // =========================================================================================
        // REGISTER GLOBAL KEYBOARD SHORTCUTS FOR BUTTONS AND SECONDARY KEYS
        // =========================================================================================

        JRootPane rootPane = getRootPane();

        // CLEAR TEXT : CTRL + L
        rootPane.registerKeyboardAction(
                clearAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // DARK MODE : CTRL + D
        rootPane.registerKeyboardAction(
                darkModeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // EXIT : CTRL + Q
        rootPane.registerKeyboardAction(
                exitAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // ZOOM IN (NUMPAD ADD SUPPORT)
        rootPane.registerKeyboardAction(
                zoomInAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ADD, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // ZOOM OUT (NUMPAD SUBTRACT SUPPORT)
        rootPane.registerKeyboardAction(
                zoomOutAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setVisible(true); // MAKE FRAME VISIBLE
    }

    // =========================================================================================
    // UTILITY CLASSES AND METHODS
    // =========================================================================================

    // DOCUMENT LISTENER TO TRACK TEXT CHANGES
    private class Count implements DocumentListener
    {
        // ACTION WHEN TEXT IS INSERTED
        public void insertUpdate(DocumentEvent e)
        {
            updateCount(); // UPDATE COUNTS ON INSERT
        }

        // ACTION WHEN TEXT IS REMOVED
        public void removeUpdate(DocumentEvent e)
        {
            updateCount(); // UPDATE COUNTS ON REMOVE
        }

        // ACTION WHEN DOCUMENT IS CHANGED
        public void changedUpdate(DocumentEvent e)
        {
            updateCount(); // UPDATE COUNTS ON CHANGE
        }

        // UPDATE COUNTS FOR CHARACTERS, WORDS, AND SENTENCES
        private void updateCount()
        {
            String text = textArea.getText().trim(); // GET TEXT FROM TEXTAREA

            if(text.isEmpty()) // IF TEXT AREA IS EMPTY
            {
                charCountLabel.setText("CHARACTER COUNT : 0"); // SET CHAR COUNT TO 0
                wordCountLabel.setText("WORD COUNT : 0"); // SET WORD COUNT TO 0
                sentenceCountLabel.setText("SENTENCE COUNT : 0"); // SET SENTENCE COUNT TO 0
            }
            else
            {
                String[] words = text.split("\\s+"); // SPLIT TEXT INTO WORDS
                int wordCount = words.length; // COUNT WORDS
                int charCount = text.length(); // COUNT CHARACTERS
                int sentenceCount = countSentences(text); // COUNT SENTENCES
                charCountLabel.setText("CHARACTER COUNT : " + charCount); // UPDATE CHAR COUNT LABEL
                wordCountLabel.setText("WORD COUNT : " + wordCount); // UPDATE WORD COUNT LABEL
                sentenceCountLabel.setText("SENTENCE COUNT : " + sentenceCount); // UPDATE SENTENCE COUNT LABEL
            }

            lineNumbers.updateLineNumbers();
        }

        // COUNT SENTENCES WITH MULTIPLE PUNCTUATION AND ABBREVIATION HANDLING
        private int countSentences(String text)
        {
            if(text.isEmpty()) // IF TEXT IS EMPTY
                return 0;

            // LIST OF COMMON ABBREVIATIONS
            String[] abbreviations = {"Mr.", "Mrs.", "Ms.", "Dr.", "Prof.", "Sr.", "Jr."};
            Set<String> abbreviationSet = new HashSet<>(Arrays.asList(abbreviations));

            int sentenceCount = 0;

            // REGEX TO MATCH SENTENCE-LIKE STRUCTURES
            java.util.regex.Pattern pattern =
                    java.util.regex.Pattern.compile("[^.!?]+[.!?]+");

            java.util.regex.Matcher matcher = pattern.matcher(text);

            while(matcher.find())
            {
                String sentence = matcher.group().trim();

                boolean isAbbreviation = false;

                // CHECK IF MATCH IS JUST AN ABBREVIATION
                for(String abbr : abbreviationSet)
                {
                    if(sentence.equals(abbr))
                    {
                        isAbbreviation = true;
                        break;
                    }
                }

                // COUNT ONLY IF NOT PURE ABBREVIATION
                if(!isAbbreviation)
                {
                    sentenceCount++;
                }
            }

            // IF TEXT EXISTS BUT NO PUNCTUATION FOUND, COUNT AS ONE SENTENCE
            if(sentenceCount == 0 && !text.trim().isEmpty())
            {
                sentenceCount = 1;
            }

            return sentenceCount;
        }
    }

    // EXPORT TEXT CONTENT AS PDF
    private void exportToPDF()
    {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("document.pdf"));

        int option = fileChooser.showSaveDialog(this);

        if(option == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File file = fileChooser.getSelectedFile();

                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("EXPORT PDF");

                job.setPrintable((graphics, pageFormat, pageIndex) ->
                {
                    if(pageIndex > 0)
                        return Printable.NO_SUCH_PAGE;

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    textArea.printAll(graphics);
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
    private void showFindDialog()
    {
        JTextField findField = new JTextField(15);

        int result = JOptionPane.showConfirmDialog(
                this,
                findField,
                "FIND",
                JOptionPane.OK_CANCEL_OPTION
        );

        if(result == JOptionPane.OK_OPTION)
        {
            String findText = findField.getText();
            highlightText(findText); // HIGHLIGHT ONLY
        }

        // RETURN FOCUS TO EDITOR BEFORE CLOSING
        textArea.requestFocusInWindow();
    }

    // SHOW FIND AND REPLACE DIALOG
    private void showFindAndReplaceDialog()
    {
        JPanel panel = new JPanel(new GridLayout(2, 2)); // CREATE PANEL WITH GRID LAYOUT
        JTextField findField = new JTextField(10); // TEXT FIELD FOR FIND TEXT
        JTextField replaceField = new JTextField(10); // TEXT FIELD FOR REPLACE TEXT

        panel.add(new JLabel("FIND")); // ADD LABEL FOR FIND
        panel.add(findField); // ADD FIND TEXT FIELD
        panel.add(new JLabel("REPLACE")); // ADD LABEL FOR REPLACE
        panel.add(replaceField); // ADD REPLACE TEXT FIELD

        int result = JOptionPane.showConfirmDialog(this, panel, "FIND & REPLACE", JOptionPane.OK_CANCEL_OPTION); // SHOW DIALOG
        if(result == JOptionPane.OK_OPTION) // IF OK PRESSED
        {
            String findText = findField.getText();
            String replaceText = replaceField.getText();

            // REPLACE IF PROVIDED (NON-DESTRUCTIVE METHOD)
            if(findText != null && !findText.isEmpty())
            {
                String text = textArea.getText();
                int index = text.lastIndexOf(findText); // START FROM THE END TO PREVENT INDEX SHIFTING

                textArea.requestFocusInWindow(); // SET FOCUS BEFORE REPLACING

                // LOOP BACKWARDS THROUGH THE TEXT
                while(index >= 0)
                {
                    // REPLACE ONLY THE TARGET RANGE
                    textArea.replaceRange(replaceText, index, index + findText.length());

                    // FIND THE PREVIOUS OCCURRENCE
                    index = text.lastIndexOf(findText, index - 1);
                }

                // HIGHLIGHT THE NEW REPLACED TEXT (OPTIONAL BUT HELPFUL)
                highlightText(replaceText);
            }
            else
            {
                // IF ONLY FINDING TEXT, JUST HIGHLIGHT IT
                highlightText(findText);
                textArea.requestFocusInWindow();
            }
        }

        // RETURN FOCUS TO EDITOR BEFORE CLOSING
        textArea.requestFocusInWindow();
    }

    // HIGHLIGHT ALL OCCURRENCES OF SEARCH TEXT
    private void highlightText(String pattern)
    {
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();

        if(pattern == null || pattern.isEmpty())
            return;

        String text = textArea.getText().toLowerCase();
        pattern = pattern.toLowerCase();

        int index = 0;

        while((index = text.indexOf(pattern, index)) != -1)
        {
            try
            {
                highlighter.addHighlight(index, index + pattern.length(), highlightPainter);
                index += pattern.length();
            }
            catch(BadLocationException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    // SHOW FONT SELECTION DIALOG
    private void showFontChooser()
    {
        // GET AVAILABLE SYSTEM FONTS
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        // CREATE FONT AND SIZE SELECTION COMPONENTS
        JComboBox<String> fontComboBox = new JComboBox<>(fontNames);
        JComboBox<Integer> sizeComboBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 36, 40});

        // CREATE PANEL FOR DIALOG
        JPanel panel = new JPanel();
        panel.add(new JLabel("FONT : "));
        panel.add(fontComboBox);
        panel.add(new JLabel("SIZE : "));
        panel.add(sizeComboBox);

        // SHOW DIALOG
        int result = JOptionPane.showConfirmDialog(this, panel, "SELECT FONT", JOptionPane.OK_CANCEL_OPTION);

        if(result == JOptionPane.OK_OPTION)
        {
            String selectedFont = (String) fontComboBox.getSelectedItem();
            int selectedSize = (Integer) sizeComboBox.getSelectedItem();

            Font newFont = new Font(selectedFont, Font.PLAIN, selectedSize);

            // APPLY FONT TO TEXT AREA AND COUNT LABELS
            applyDynamicFont(newFont);
        }
    }

    // SHOW COLOR SELECTION DIALOG
    private void showColorChooser()
    {
        // OPEN COLOR CHOOSER WITH CURRENT TEXT COLOR AS DEFAULT
        Color newColor = JColorChooser.showDialog(this, "SELECT TEXT COLOR", textArea.getForeground());

        // APPLY SELECTED COLOR IF USER DID NOT CANCEL
        if(newColor != null)
        {
            textArea.setForeground(newColor);
        }
    }

    // INCREASE FONT SIZE BY 2 POINTS (ZOOM IN)
    private void zoomIn()
    {
        Font currentFont = textArea.getFont();
        int newSize = currentFont.getSize() + 2;

        if(newSize <= 72) // MAXIMUM FONT SIZE LIMIT TO PREVENT RENDERING ISSUES
        {
            applyDynamicFont(new Font(currentFont.getFamily(), currentFont.getStyle(), newSize));
        }
    }

    // DECREASE FONT SIZE BY 2 POINTS (ZOOM OUT)
    private void zoomOut()
    {
        Font currentFont = textArea.getFont();
        int newSize = currentFont.getSize() - 2;

        if(newSize >= 8) // MINIMUM FONT SIZE LIMIT TO KEEP TEXT READABLE
        {
            applyDynamicFont(new Font(currentFont.getFamily(), currentFont.getStyle(), newSize));
        }
    }

    // APPLY NEW FONT SIZE TO ALL RELEVANT UI COMPONENTS
    private void applyDynamicFont(Font newFont)
    {
        textArea.setFont(newFont);
        lineNumbers.setFont(newFont);
        charCountLabel.setFont(newFont);
        wordCountLabel.setFont(newFont);
        sentenceCountLabel.setFont(newFont);
        statusBar.setFont(newFont);
    }

    // SHOW GO TO LINE DIALOG AND JUMP TO SPECIFIED LINE
    private void showGoToLineDialog()
    {
        // PROMPT USER FOR LINE NUMBER
        String input = JOptionPane.showInputDialog(
                this,
                "ENTER LINE NUMBER :",
                "GO TO LINE",
                JOptionPane.QUESTION_MESSAGE
        );

        // CHECK IF USER CLICKED CANCEL OR ENTERED EMPTY STRING
        if(input != null && !input.trim().isEmpty())
        {
            try
            {
                int lineNumber = Integer.parseInt(input.trim()); // PARSE INPUT TO INTEGER
                int totalLines = textArea.getLineCount(); // GET TOTAL LINES IN DOCUMENT

                // VALIDATE IF LINE NUMBER IS WITHIN BOUNDS
                if(lineNumber < 1 || lineNumber > totalLines)
                {
                    JOptionPane.showMessageDialog(
                            this,
                            "LINE NUMBER OUT OF RANGE (1 - " + totalLines + ").",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // GET THE STARTING OFFSET OF THE TARGET LINE (0-INDEXED FOR API, 1-INDEXED FOR USER)
                int offset = textArea.getLineStartOffset(lineNumber - 1);

                // MOVE CARET TO THE CALCULATED OFFSET
                textArea.setCaretPosition(offset);
                textArea.requestFocusInWindow(); // RETURN FOCUS TO THE TEXT EDITOR
            }
            catch(NumberFormatException ex) // HANDLE NON-NUMERIC INPUT
            {
                JOptionPane.showMessageDialog(
                        this,
                        "INVALID INPUT. PLEASE ENTER A VALID NUMBER.",
                        "ERROR",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            catch(BadLocationException ex) // HANDLE TEXT AREA BOUNDARY ERRORS
            {
                ex.printStackTrace();
            }
        }
        else
        {
            // IF DIALOG IS CANCELLED, JUST RETURN FOCUS TO EDITOR
            textArea.requestFocusInWindow();
        }
    }

    // TOGGLE BETWEEN DARK AND LIGHT MODE
    private void toggleDarkMode()
    {
        isDarkMode = !isDarkMode; // TOGGLE DARK MODE FLAG

        if(isDarkMode) // IF DARK MODE ENABLED
        {
            textArea.setBackground(Color.BLACK);
            textArea.setForeground(Color.WHITE);

            charCountLabel.setForeground(Color.WHITE);
            wordCountLabel.setForeground(Color.WHITE);
            sentenceCountLabel.setForeground(Color.WHITE);
            statusBar.setForeground(Color.WHITE);

            bottomPanel.setBackground(Color.DARK_GRAY);
            countPanel.setBackground(Color.DARK_GRAY);
            buttonPanel.setBackground(Color.DARK_GRAY);

            getContentPane().setBackground(Color.DARK_GRAY);
            lineNumbers.setBackground(Color.DARK_GRAY);
            lineNumbers.setForeground(Color.WHITE);
        }
        else // IF LIGHT MODE ENABLED
        {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);

            charCountLabel.setForeground(Color.DARK_GRAY);
            wordCountLabel.setForeground(Color.DARK_GRAY);
            sentenceCountLabel.setForeground(Color.DARK_GRAY);
            statusBar.setForeground(Color.BLACK);

            bottomPanel.setBackground(Color.WHITE);
            countPanel.setBackground(Color.WHITE);
            buttonPanel.setBackground(Color.WHITE);

            getContentPane().setBackground(Color.WHITE);
            lineNumbers.setBackground(Color.LIGHT_GRAY);
            lineNumbers.setForeground(Color.BLACK);
        }
    }

    // UPDATE STATUS BAR WITH CURRENT LINE AND COLUMN NUMBER
    private void updateStatusBar()
    {
        int caretPosition = textArea.getCaretPosition();
        int lineNumber = 0;
        int columnNumber = 0;

        try
        {
            lineNumber = textArea.getLineOfOffset(caretPosition);
            columnNumber = caretPosition - textArea.getLineStartOffset(lineNumber);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        statusBar.setText("LINE : " + (lineNumber + 1) + " | COLUMN : " + (columnNumber + 1));
    }

    // CALCULATE AND DISPLAY DETAILED TEXT STATISTICS
    private void showTextStatistics()
    {
        String text = textArea.getText(); // GET CURRENT TEXT

        // INITIALIZE COUNTERS
        int paragraphs = 0;
        int vowels = 0;
        int consonants = 0;
        String longestWord = "N/A";
        int totalWordLength = 0;
        double averageWordLength = 0.0;

        if(!text.trim().isEmpty()) // ONLY CALCULATE IF TEXT IS NOT EMPTY
        {
            // COUNT PARAGRAPHS (SPLIT BY ONE OR MORE NEWLINES)
            paragraphs = text.trim().split("\\n+").length;

            // COUNT VOWELS AND CONSONANTS
            for(char c : text.toLowerCase().toCharArray())
            {
                if(Character.isLetter(c)) // ONLY CHECK ALPHABETIC CHARACTERS
                {
                    if("aeiou".indexOf(c) != -1)
                        vowels++;
                    else
                        consonants++;
                }
            }

            // FIND LONGEST WORD AND AVERAGE LENGTH
            String[] words = text.trim().split("\\s+");
            longestWord = ""; // RESET FOR ACTUAL CALCULATION

            for(String word : words)
            {
                // REMOVE PUNCTUATION FROM WORD FOR ACCURATE LENGTH CALCULATION
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

            if(longestWord.isEmpty())
            {
                longestWord = "N/A";
            }
        }

        // FORMAT THE OUTPUT MESSAGE FOR THE DIALOG
        String statsMessage = String.format(
                "TOTAL PARAGRAPHS : %d\n" +
                        "NUMBER OF VOWELS : %d\n" +
                        "NUMBER OF CONSONANTS : %d\n" +
                        "LONGEST WORD : \"%s\"\n" +
                        "AVERAGE WORD LENGTH : %.2f CHARACTERS",
                paragraphs, vowels, consonants, longestWord, averageWordLength
        );

        // SHOW THE DIALOG BOX
        JOptionPane.showMessageDialog(
                this,
                statsMessage,
                "TEXT STATISTICS",
                JOptionPane.INFORMATION_MESSAGE
        );

        textArea.requestFocusInWindow(); // RETURN FOCUS TO EDITOR
    }

    // MAIN METHOD TO RUN THE APPLICATION
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new); // RUN THE APPLICATION ON EVENT DISPATCH THREAD
    }
}

// COMPONENT TO DISPLAY LINE NUMBERS
class LineNumberView extends JTextArea
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