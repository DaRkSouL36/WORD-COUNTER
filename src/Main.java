import java.util.*; // IMPORT FOR COLLECTIONS USED IN SENTENCE COUNTING
import javax.swing.*; // IMPORT SWING LIBRARY FOR GUI COMPONENTS
import javax.swing.border.EmptyBorder; // IMPORT FOR BORDER STYLES
import javax.swing.event.DocumentEvent; // IMPORT FOR DOCUMENT EVENT HANDLING
import javax.swing.event.DocumentListener; // IMPORT FOR DOCUMENT LISTENER INTERFACE
import javax.swing.filechooser.FileNameExtensionFilter; // IMPORT FOR FILE FILTERING IN FILE CHOOSER
import javax.swing.undo.UndoManager; // IMPORT FOR UNDO/REDO FUNCTIONALITY
import java.awt.*; // IMPORT FOR AWT (ABSTRACT WINDOW TOOLKIT) COMPONENTS
import java.awt.event.ActionEvent; // IMPORT FOR ACTION EVENT HANDLING
import java.awt.event.ActionListener; // IMPORT FOR ACTION LISTENER INTERFACE
import java.io.*; // IMPORT FOR FILE HANDLING
import java.awt.event.KeyEvent; // IMPORT FOR KEYBOARD SHORTCUT KEYS

public class Main extends JFrame implements ActionListener 
{
    // DECLARING GUI COMPONENTS (TEXTAREA, LABELS, BUTTONS, PANELS, ETC.)
    JTextArea textArea; // TEXTAREA FOR ENTERING AND DISPLAYING TEXT
    JLabel charCountLabel, wordCountLabel, sentenceCountLabel, statusBar; // LABELS TO DISPLAY CHARACTER, WORD, AND SENTENCE COUNTS; CURRENT LINE AND COLUMN POSITION
    JButton clearButton, exitButton, darkModeButton; // BUTTONS FOR CLEARING TEXT, EXITING, AND TOGGLING DARK MODE
    JPanel buttonPanel, countPanel, bottomPanel; // PANELS FOR BUTTONS, COUNTS, AND BOTTOM SECTION
    UndoManager undoManager; // UNDO MANAGER TO HANDLE UNDO/REDO ACTIONS
    boolean isDarkMode = false; // FLAG TO TOGGLE DARK MODE

    // CONSTRUCTOR TO SET UP THE FRAME AND INITIALIZE COMPONENTS
    public Main()
    {
        super("WORD COUNTER"); // SET FRAME TITLE
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // CLOSE APPLICATION ON WINDOW CLOSE
        setSize(1000, 500); // SET WINDOW SIZE (WIDTH: 1000px, HEIGHT: 500px)
        setLocationRelativeTo(null); // CENTER THE FRAME ON SCREEN
        setLayout(new BorderLayout()); // SET THE FRAME'S LAYOUT TO BORDER LAYOUT
        getContentPane().setBackground(Color.WHITE); // SET BACKGROUND COLOR OF THE FRAME TO WHITE

        // INITIALIZE TEXT AREA
        textArea = new JTextArea();
        textArea.setFont(new Font("Times New Roman", Font.BOLD, 16)); // SET FONT TO TIMES NEW ROMAN, BOLD, SIZE 16
        textArea.setLineWrap(true); // ENABLE LINE WRAPPING IN THE TEXT AREA
        textArea.setWrapStyleWord(true); // ENABLE WORD-WRAPPING IN THE TEXT AREA

        // INITIALIZE UNDO MANAGER TO HANDLE UNDO AND REDO ACTIONS
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager); // ATTACH UNDO MANAGER TO DOCUMENT CHANGES
        textArea.getDocument().addDocumentListener(new Count()); // ADD DOCUMENT LISTENER TO UPDATE COUNTS

        // CREATE A SCROLLPANE TO HOLD THE TEXT AREA (SCROLLING TEXT)
        JScrollPane scrollPane = new JScrollPane(textArea);
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

        // INITIALIZE BUTTONS FOR CLEARING TEXT, EXITING, AND TOGGLING DARK MODE
        clearButton = new JButton("CLEAR TEXT");
        clearButton.addActionListener(this); // ADD ACTION LISTENER FOR CLEAR BUTTON
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 14)); // SET FONT STYLE AND SIZE
        clearButton.setForeground(Color.WHITE); // SET BUTTON TEXT COLOR
        clearButton.setBackground(Color.BLUE); // SET BUTTON BACKGROUND COLOR
        clearButton.setFocusPainted(false); // REMOVE FOCUS PAINT
        clearButton.setToolTipText("CLEAR ALL TEXT IN THE TEXT AREA"); // SET TOOLTIP TEXT

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this); // ADD ACTION LISTENER FOR EXIT BUTTON
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 14)); // SET FONT STYLE AND SIZE
        exitButton.setForeground(Color.WHITE); // SET BUTTON TEXT COLOR
        exitButton.setBackground(Color.RED); // SET BUTTON BACKGROUND COLOR
        exitButton.setFocusPainted(false); // REMOVE FOCUS PAINT
        exitButton.setToolTipText("EXIT THE APPLICATION"); // SET TOOLTIP TEXT

        darkModeButton = new JButton("DARK MODE");
        darkModeButton.addActionListener(this); // ADD ACTION LISTENER FOR DARK MODE BUTTON
        darkModeButton.setFont(new Font("Times New Roman", Font.BOLD, 14)); // SET FONT STYLE AND SIZE
        darkModeButton.setForeground(Color.WHITE); // SET BUTTON TEXT COLOR
        darkModeButton.setBackground(Color.DARK_GRAY); // SET BUTTON BACKGROUND COLOR
        darkModeButton.setFocusPainted(false); // REMOVE FOCUS PAINT
        darkModeButton.setToolTipText("TOGGLE DARK MODE"); // SET TOOLTIP TEXT

        // CREATE BUTTON PANEL AND ADD BUTTONS
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // CENTER BUTTONS IN PANEL
        buttonPanel.setBackground(Color.WHITE); // SET BUTTON PANEL BACKGROUND TO WHITE
        buttonPanel.add(clearButton); // ADD CLEAR BUTTON
        buttonPanel.add(exitButton); // ADD EXIT BUTTON
        buttonPanel.add(darkModeButton); // ADD DARK MODE BUTTON

        // CREATE PANEL TO DISPLAY COUNTS AND ADD LABELS
        countPanel = new JPanel(new BorderLayout());
        countPanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // ADD PADDING TO PANEL
        countPanel.add(charCountLabel, BorderLayout.WEST); // ADD CHARACTER COUNT LABEL
        countPanel.add(wordCountLabel, BorderLayout.CENTER); // ADD WORD COUNT LABEL
        countPanel.add(sentenceCountLabel, BorderLayout.EAST); // ADD SENTENCE COUNT LABEL

        // INITIALIZE STATUS BAR TO DISPLAY CARET POSITION
        statusBar = new JLabel("LINE : 1 | COLUMN : 1");
        statusBar.setFont(new Font("Times New Roman", Font.BOLD, 14));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);

        // CREATE BOTTOM PANEL TO HOLD COUNT PANEL AND STATUS BAR
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(countPanel, BorderLayout.NORTH);
        bottomPanel.add(statusBar, BorderLayout.SOUTH);

        // ADD COMPONENTS TO MAIN FRAME
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        // CREATE MENU BAR
        JMenuBar menuBar = new JMenuBar();

        // FILE MENU WITH OPEN AND SAVE OPTIONS
        JMenu fileMenu = new JMenu("FILE");
        JMenuItem openItem = new JMenuItem("OPEN");
        JMenuItem saveItem = new JMenuItem("SAVE");

        // ADD KEYBOARD SHORTCUTS FOR FILE MENU
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        fileMenu.add(openItem); // ADD OPEN ITEM TO FILE MENU
        fileMenu.add(saveItem); // ADD SAVE ITEM TO FILE MENU
        menuBar.add(fileMenu); // ADD FILE MENU TO MENU BAR
        openItem.addActionListener(this); // ADD ACTION LISTENER FOR OPEN
        saveItem.addActionListener(this); // ADD ACTION LISTENER FOR SAVE

        // EDIT MENU WITH UNDO, REDO, AND FIND & REPLACE OPTIONS
        JMenu editMenu = new JMenu("EDIT");
        JMenuItem undoItem = new JMenuItem("UNDO");
        JMenuItem redoItem = new JMenuItem("REDO");
        JMenuItem findReplaceItem = new JMenuItem("FIND & REPLACE");

        // ADD KEYBOARD SHORTCUTS FOR EDIT MENU
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        findReplaceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        editMenu.add(undoItem); // ADD UNDO ITEM TO EDIT MENU
        editMenu.add(redoItem); // ADD REDO ITEM TO EDIT MENU
        editMenu.add(findReplaceItem); // ADD FIND & REPLACE ITEM TO EDIT MENU
        menuBar.add(editMenu); // ADD EDIT MENU TO MENU BAR
        undoItem.addActionListener(this); // ADD ACTION LISTENER FOR UNDO
        redoItem.addActionListener(this); // ADD ACTION LISTENER FOR REDO
        findReplaceItem.addActionListener(this); // ADD ACTION LISTENER FOR FIND & REPLACE

        // FORMAT MENU FOR FONT AND COLOR CUSTOMIZATION
        JMenu formatMenu = new JMenu("FORMAT");
        JMenuItem fontItem = new JMenuItem("FONT...");
        JMenuItem colorItem = new JMenuItem("COLOR...");

        formatMenu.add(fontItem);
        formatMenu.add(colorItem);
        menuBar.add(formatMenu);

        fontItem.addActionListener(this);   // ADD ACTION LISTENER FOR FONT
        colorItem.addActionListener(this);  // ADD ACTION LISTENER FOR COLOR

        // ADD KEYBOARD SHORTCUTS FOR BUTTON ACTIONS
        JRootPane rootPane = getRootPane();

        // CLEAR TEXT : CTRL + L
        rootPane.registerKeyboardAction(
                e -> textArea.setText(""),
                KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // DARK MODE : CTRL + D
        rootPane.registerKeyboardAction(
                e -> toggleDarkMode(),
                KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // EXIT : CTRL + Q
        rootPane.registerKeyboardAction(
                e -> System.exit(0),
                KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // SET MENU BAR
        setJMenuBar(menuBar);

        setVisible(true); // MAKE FRAME VISIBLE
    }

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

    // ACTION HANDLER FOR BUTTON AND MENU ACTIONS
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand(); // GET ACTION COMMAND (BUTTON/MENU ITEM)

        switch(command)
        {
            case "OPEN" -> // OPEN FILE
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt")); // FILTER TEXT FILES
                int option = fileChooser.showOpenDialog(this); // OPEN FILE DIALOG
                if(option == JFileChooser.APPROVE_OPTION) // IF FILE SELECTED
                {
                    try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile())))
                    {
                        textArea.read(reader, null); // READ FILE CONTENT INTO TEXTAREA
                    }
                    catch(IOException ex) // HANDLE EXCEPTION
                    {
                        ex.printStackTrace();
                    }
                }
            }
            case "SAVE" -> // SAVE FILE
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt")); // FILTER TEXT FILES

                int option = fileChooser.showSaveDialog(this); // SHOW SAVE DIALOG

                if(option == JFileChooser.APPROVE_OPTION) // IF FILE SELECTED
                {
                    try
                    {
                        File file = fileChooser.getSelectedFile(); // GET SELECTED FILE

                        // IF USER DID NOT TYPE ".txt", ADD IT AUTOMATICALLY
                        if(!file.getName().toLowerCase().endsWith(".txt"))
                        {
                            file = new File(file.getAbsolutePath() + ".txt");
                        }

                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        textArea.write(writer); // WRITE CONTENT TO FILE
                        writer.close(); // CLOSE WRITER
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            case "UNDO" -> // UNDO LAST ACTION
            {
                try
                {
                    if(undoManager.canUndo())
                        undoManager.undo();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            case "REDO" -> // REDO LAST ACTION
            {
                try
                {
                    if(undoManager.canRedo())
                        undoManager.redo();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            case "FONT..." -> showFontChooser(); // OPEN FONT SELECTION DIALOG
            case "COLOR..." -> showColorChooser(); // OPEN COLOR SELECTION DIALOG
            case "FIND & REPLACE" -> showFindAndReplaceDialog(); // SHOW FIND & REPLACE DIALOG
            case "CLEAR TEXT" -> textArea.setText(""); // CLEAR TEXTAREA CONTENT
            case "EXIT" -> System.exit(0); // EXIT THE APPLICATION
            case "DARK MODE" -> toggleDarkMode(); // TOGGLE DARK MODE
        }
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
            String findText = findField.getText(); // GET TEXT TO FIND
            String replaceText = replaceField.getText(); // GET TEXT TO REPLACE

            if(!findText.isEmpty())
            {
                textArea.setText(textArea.getText().replace(findText, replaceText)); // REPLACE TEXT IN TEXTAREA
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
            textArea.setFont(newFont);
            charCountLabel.setFont(newFont);
            wordCountLabel.setFont(newFont);
            sentenceCountLabel.setFont(newFont);
            statusBar.setFont(newFont); // ALSO UPDATE STATUS BAR
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

    // MAIN METHOD TO RUN THE APPLICATION
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new); // RUN THE APPLICATION ON EVENT DISPATCH THREAD
    }
}
