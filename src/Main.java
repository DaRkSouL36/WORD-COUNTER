import javax.swing.*; // IMPORT SWING LIBRARY FOR GUI COMPONENTS
import javax.swing.text.*; // IMPORT FOR TEXT HIGHLIGHTING
import javax.swing.border.EmptyBorder; // IMPORT FOR BORDER STYLES
import javax.swing.undo.UndoManager; // IMPORT FOR UNDO/REDO FUNCTIONALITY
import java.awt.*; // IMPORT FOR AWT (ABSTRACT WINDOW TOOLKIT) COMPONENTS
import java.awt.event.KeyEvent; // IMPORT FOR KEYBOARD SHORTCUT KEYS

public class Main extends JFrame
{
    // =========================================================================================
    // DECLARING GUI COMPONENTS (NOW PUBLIC FOR EXTERNAL CONTROLLER ACCESS)
    // =========================================================================================

    public JTextArea textArea;
    public JLabel charCountLabel, wordCountLabel, sentenceCountLabel, statusBar;
    public JButton clearButton, exitButton, darkModeButton;
    public JPanel buttonPanel, countPanel, bottomPanel;
    public UndoManager undoManager;
    public boolean isDarkMode = false;
    public boolean hasUnsavedChanges = false;
    public String currentFileName = "WORD COUNTER";
    public LineNumberView lineNumbers;
    public Highlighter.HighlightPainter highlightPainter;

    // CONSTRUCTOR TO SET UP THE FRAME AND INITIALIZE COMPONENTS
    public Main()
    {
        super("WORD COUNTER"); // SET FRAME TITLE
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // CLOSE APPLICATION ON WINDOW CLOSE
        setSize(1000, 500); // SET WINDOW SIZE
        setLocationRelativeTo(null); // CENTER THE FRAME ON SCREEN
        setLayout(new BorderLayout()); // SET BORDER LAYOUT
        getContentPane().setBackground(Color.WHITE);
        highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

        // INITIALIZE TEXT AREA
        textArea = new JTextArea();
        textArea.setFont(new Font("Times New Roman", Font.BOLD, 16));
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);

        // INITIALIZE UNDO MANAGER
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);

        // ATTACH THE EXTERNAL DOCUMENT ANALYZER (MODEL)
        textArea.getDocument().addDocumentListener(new DocumentAnalyzer(this));

        // CREATE LINE NUMBER COMPONENT
        lineNumbers = new LineNumberView(textArea);
        lineNumbers.updateLineNumbers();

        // CREATE SCROLLPANE
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ADD CARET LISTENER TO TRACK LINE AND COLUMN POSITION
        textArea.addCaretListener(e -> updateStatusBar());

        // INITIALIZE LABELS
        charCountLabel = new JLabel("CHARACTER COUNT : 0");
        charCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        charCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        charCountLabel.setForeground(Color.DARK_GRAY);

        wordCountLabel = new JLabel("WORD COUNT : 0");
        wordCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        wordCountLabel.setForeground(Color.DARK_GRAY);

        sentenceCountLabel = new JLabel("SENTENCE COUNT : 0");
        sentenceCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sentenceCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        sentenceCountLabel.setForeground(Color.DARK_GRAY);

        statusBar = new JLabel("LINE : 1 | COLUMN : 1");
        statusBar.setFont(new Font("Times New Roman", Font.BOLD, 14));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);

        // =========================================================================================
        // INITIALIZE EXTERNAL CONTROLLERS (MVC ARCHITECTURE)
        // =========================================================================================

        EditorDialogs dialogs = new EditorDialogs(this); // INITIALIZE THE DIALOGS HELPER
        EditorActions actions = new EditorActions(this, dialogs); // INITIALIZE THE ACTIONS CONTROLLER

        // =========================================================================================
        // INITIALIZE BUTTONS USING ACTIONS FROM CONTROLLER
        // =========================================================================================

        clearButton = new JButton(actions.clearAction);
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(Color.BLUE);
        clearButton.setFocusPainted(false);

        exitButton = new JButton(actions.exitAction);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(Color.RED);
        exitButton.setFocusPainted(false);

        darkModeButton = new JButton(actions.darkModeAction);
        darkModeButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        darkModeButton.setForeground(Color.WHITE);
        darkModeButton.setBackground(Color.DARK_GRAY);
        darkModeButton.setFocusPainted(false);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(darkModeButton);

        // =========================================================================================
        // INITIALIZE MENU BAR USING ACTIONS FROM CONTROLLER
        // =========================================================================================

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("FILE");
        fileMenu.add(new JMenuItem(actions.openAction));
        fileMenu.add(new JMenuItem(actions.saveAction));
        fileMenu.add(new JMenuItem(actions.exportPdfAction));
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("EDIT");
        editMenu.add(new JMenuItem(actions.undoAction));
        editMenu.add(new JMenuItem(actions.redoAction));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(actions.findAction));
        editMenu.add(new JMenuItem(actions.findReplaceAction));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem(actions.goToLineAction));
        menuBar.add(editMenu);

        JMenu formatMenu = new JMenu("FORMAT");
        formatMenu.add(new JMenuItem(actions.fontAction));
        formatMenu.add(new JMenuItem(actions.colorAction));
        menuBar.add(formatMenu);

        JMenu viewMenu = new JMenu("VIEW");
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem(actions.wordWrapAction);
        wordWrapItem.setState(false);
        viewMenu.add(wordWrapItem);
        viewMenu.addSeparator();
        viewMenu.add(new JMenuItem(actions.zoomInAction));
        viewMenu.add(new JMenuItem(actions.zoomOutAction));
        menuBar.add(viewMenu);

        JMenu toolsMenu = new JMenu("TOOLS");
        toolsMenu.add(new JMenuItem(actions.textStatsAction));
        menuBar.add(toolsMenu);

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
        // REGISTER GLOBAL KEYBOARD SHORTCUTS
        // =========================================================================================

        JRootPane rootPane = getRootPane();
        rootPane.registerKeyboardAction(actions.clearAction, KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(actions.darkModeAction, KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(actions.exitAction, KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(actions.zoomInAction, KeyStroke.getKeyStroke(KeyEvent.VK_ADD, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), JComponent.WHEN_IN_FOCUSED_WINDOW);
        rootPane.registerKeyboardAction(actions.zoomOutAction, KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setVisible(true);
    }

    // =========================================================================================
    // PUBLIC UI UPDATE METHODS (CALLED BY EXTERNAL CONTROLLERS)
    // =========================================================================================

    public void applyDynamicFont(Font newFont)
    {
        textArea.setFont(newFont);
        lineNumbers.setFont(newFont);
        charCountLabel.setFont(newFont);
        wordCountLabel.setFont(newFont);
        sentenceCountLabel.setFont(newFont);
        statusBar.setFont(newFont);
    }

    public void zoomIn()
    {
        Font currentFont = textArea.getFont();
        int newSize = currentFont.getSize() + 2;
        if(newSize <= 72) applyDynamicFont(new Font(currentFont.getFamily(), currentFont.getStyle(), newSize));
    }

    public void zoomOut()
    {
        Font currentFont = textArea.getFont();
        int newSize = currentFont.getSize() - 2;
        if(newSize >= 8) applyDynamicFont(new Font(currentFont.getFamily(), currentFont.getStyle(), newSize));
    }

    public void toggleDarkMode()
    {
        isDarkMode = !isDarkMode;

        if(isDarkMode)
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
        else
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

    public void updateStatusBar()
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

    public void updateWindowTitle()
    {
        String prefix = hasUnsavedChanges ? "* " : "";
        setTitle(prefix + currentFileName);
    }

    // MAIN METHOD TO RUN THE APPLICATION
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new);
    }
}