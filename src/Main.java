import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
public class Main extends JFrame implements ActionListener 
{
    JTextArea textArea;
    JLabel charCountLabel, wordCountLabel, sentenceCountLabel;
    JButton clearButton, exitButton, darkModeButton;
    UndoManager undoManager;
    boolean isDarkMode = false;
    public Main()
    {
        super("WORD COUNTER");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        textArea = new JTextArea();
        textArea.setFont(new Font("Times New Roman", Font.BOLD, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(undoManager);
        textArea.getDocument().addDocumentListener(new Count());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        clearButton = new JButton("CLEAR TEXT");
        clearButton.addActionListener(this);
        clearButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBackground(Color.BLUE);
        clearButton.setFocusPainted(false);
        clearButton.setToolTipText("CLEAR ALL TEXT IN THE TEXT AREA");

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(Color.RED);
        exitButton.setFocusPainted(false);
        exitButton.setToolTipText("EXIT THE APPLICATION");

        darkModeButton = new JButton("DARK MODE");
        darkModeButton.addActionListener(this);
        darkModeButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        darkModeButton.setForeground(Color.WHITE);
        darkModeButton.setBackground(Color.DARK_GRAY);
        darkModeButton.setFocusPainted(false);
        darkModeButton.setToolTipText("TOGGLE DARK MODE");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(darkModeButton);

        JPanel countPanel = new JPanel(new BorderLayout());
        countPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        countPanel.add(charCountLabel, BorderLayout.WEST);
        countPanel.add(wordCountLabel, BorderLayout.CENTER);
        countPanel.add(sentenceCountLabel, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(countPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("FILE");
        JMenuItem openItem = new JMenuItem("OPEN");
        JMenuItem saveItem = new JMenuItem("SAVE");
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        openItem.addActionListener(this);
        saveItem.addActionListener(this);

        JMenu editMenu = new JMenu("EDIT");
        JMenuItem undoItem = new JMenuItem("UNDO");
        JMenuItem redoItem = new JMenuItem("REDO");
        JMenuItem findReplaceItem = new JMenuItem("FIND & REPLACE");
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(findReplaceItem);
        menuBar.add(editMenu);
        undoItem.addActionListener(this);
        redoItem.addActionListener(this);
        findReplaceItem.addActionListener(this);

        setJMenuBar(menuBar);
    }

    private class Count implements DocumentListener
    {
        public void insertUpdate(DocumentEvent e)
        {
            updateCount();
        }
        public void removeUpdate(DocumentEvent e)
        {
            updateCount();
        }
        public void changedUpdate(DocumentEvent e)
        {
            updateCount();
        }

        private void updateCount()
        {
            String text = textArea.getText().trim();

            if(text.isEmpty())
            {
                charCountLabel.setText("CHARACTER COUNT : 0");
                wordCountLabel.setText("WORD COUNT : 0");
                sentenceCountLabel.setText("SENTENCE COUNT : 0");
            }

            else
            {
                String[] words = text.split("\\s+");
                int wordCount = words.length;
                int charCount = text.length();
                int sentenceCount = countSentences(text);
                charCountLabel.setText("CHARACTER COUNT : " + charCount);
                wordCountLabel.setText("WORD COUNT : " + wordCount);
                sentenceCountLabel.setText("SENTENCE COUNT : " + sentenceCount);
            }
        }

        private int countSentences(String text)
        {
            if(text.isEmpty())
                return 0;

            String[] sentences = text.split("[.!?](?=\\s)");
            int sentenceCount = 0;

            for(String element : sentences)
            {
                if(!element.trim().isEmpty())
                    sentenceCount++;
            }

            return sentenceCount;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        switch(command)
        {
            case "OPEN" ->
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showOpenDialog(this);
                if(option == JFileChooser.APPROVE_OPTION)
                {
                    try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile())))
                    {
                        textArea.read(reader, null);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            case "SAVE" ->
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showSaveDialog(this);
                if(option == JFileChooser.APPROVE_OPTION)
                {
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile())))
                    {
                        textArea.write(writer);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            case "UNDO" ->
            {
                if(undoManager.canUndo())
                    undoManager.undo();
            }
            case "REDO" ->
            {
                if(undoManager.canRedo())
                    undoManager.redo();
            }
            case "FIND & REPLACE" -> showFindAndReplaceDialog();
            case "CLEAR TEXT" -> textArea.setText("");
            case "EXIT" -> System.exit(0);
        }
    }

    private void showFindAndReplaceDialog()
    {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField findField = new JTextField(10);
        JTextField replaceField = new JTextField(10);

        panel.add(new JLabel("FIND"));
        panel.add(findField);
        panel.add(new JLabel("REPLACE"));
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "FIND & REPLACE", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION)
        {
            String findText = findField.getText();
            String replaceText = replaceField.getText();
            textArea.setText(textArea.getText().replace(findText, replaceText));
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new);
    }
}