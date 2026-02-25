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
    boolean hasUnsavedChanges = false; // FLAG TO TRACK IF DOCUMENT HAS BEEN MODIFIED
    String currentFileName = "WORD COUNTER"; // TRACKS THE CURRENT FILE NAME (DEFAULTS TO APP NAME)
    LineNumberView lineNumbers; // TO COUNT LINE NUMBERS
    Highlighter.HighlightPainter highlightPainter; // HIGHLIGHT PAINTER FOR SEARCH
    EditorDialogs dialogs = new EditorDialogs(this); // INITIALIZE THE DIALOGS HELPER

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
        textArea.getDocument().addDocumentListener(new DocumentAnalyzer(this)); // ADD DOCUMENT LISTENER TO UPDATE COUNTS

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
                        textArea.read(reader, null); // THIS TRIGGERS THE DOCUMENT LISTENER
                        lineNumbers.updateLineNumbers();
                        undoManager.discardAllEdits();

                        // --- LOGIC FOR UNSAVED CHANGES INDICATOR ---
                        currentFileName = fileChooser.getSelectedFile().getName(); // GET FILE NAME
                        hasUnsavedChanges = false; // RESET FLAG (MUST BE AFTER textArea.read!)
                        updateWindowTitle(); // UPDATE TITLE BAR
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };

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

                        // --- LOGIC FOR UNSAVED CHANGES INDICATOR ---
                        currentFileName = file.getName(); // UPDATE TO SAVED FILE NAME
                        hasUnsavedChanges = false; // RESET FLAG
                        updateWindowTitle(); // UPDATE TITLE BAR
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };

        Action exportPdfAction = new AbstractAction("EXPORT AS PDF")
        {
            public void actionPerformed(ActionEvent e) { dialogs.exportToPDF(); }
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
            public void actionPerformed(ActionEvent e) { dialogs.showFindDialog(); }
        };
        findAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action findReplaceAction = new AbstractAction("FIND & REPLACE")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showFindAndReplaceDialog(); }
        };
        findReplaceAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action goToLineAction = new AbstractAction("GO TO LINE")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showGoToLineDialog(); }
        };
        goToLineAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action fontAction = new AbstractAction("FONT...")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showFontChooser(); }
        };

        Action colorAction = new AbstractAction("COLOR...")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showColorChooser(); }
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
            public void actionPerformed(ActionEvent e) { dialogs.showTextStatistics(); }
        };
        // ASSIGN CTRL + I (FOR INFORMATION/INSPECTION) AS SHORTCUT
        textStatsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        Action wordWrapAction = new AbstractAction("WORD WRAP")
        {
            public void actionPerformed(ActionEvent e)
            {
                // GET CURRENT WRAP STATE AND TOGGLE IT
                boolean isWrapped = textArea.getLineWrap();
                textArea.setLineWrap(!isWrapped);
                textArea.setWrapStyleWord(!isWrapped); // KEEP WORDS INTACT WHEN WRAPPING
            }
        };
        // ASSIGN ALT + W AS A QUICK SHORTCUT FOR WORD WRAP
        wordWrapAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_DOWN_MASK));

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

        // CREATE CHECKBOX MENU ITEM FOR WORD WRAP AND SET DEFAULT STATE TO FALSE
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem(wordWrapAction);
        wordWrapItem.setState(false); // MATCHES THE INITIAL textArea.setLineWrap(false)

        viewMenu.add(wordWrapItem);
        viewMenu.addSeparator(); // ADD VISUAL SEPARATOR BETWEEN WRAP AND ZOOM
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
    // UTILITY METHODS
    // =========================================================================================

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
    public void applyDynamicFont(Font newFont)
    {
        textArea.setFont(newFont);
        lineNumbers.setFont(newFont);
        charCountLabel.setFont(newFont);
        wordCountLabel.setFont(newFont);
        sentenceCountLabel.setFont(newFont);
        statusBar.setFont(newFont);
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

    // DYNAMICALLY UPDATE THE WINDOW TITLE BASED ON SAVE STATE AND FILE NAME
    public void updateWindowTitle()
    {
        // IF THERE ARE UNSAVED CHANGES, PREPEND AN ASTERISK
        String prefix = hasUnsavedChanges ? "* " : "";
        setTitle(prefix + currentFileName);
    }

    // MAIN METHOD TO RUN THE APPLICATION
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new); // RUN THE APPLICATION ON EVENT DISPATCH THREAD
    }
}