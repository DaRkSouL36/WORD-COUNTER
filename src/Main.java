import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame implements ActionListener 
{
    JTextArea textArea;
    JLabel charCountLabel, wordCountLabel, sentenceCountLabel;
    JButton clearButton, exitButton;
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
        clearButton.setBackground(new Color(30, 144, 255));
        clearButton.setFocusPainted(false);

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(this);
        exitButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);

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

        JMenu editMenu = new JMenu("EDIT");
        JMenuItem undoItem = new JMenuItem("UNDO");
        JMenuItem redoItem = new JMenuItem("REDO");
        JMenuItem findReplaceItem = new JMenuItem("FIND & REPLACE");
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(findReplaceItem);
        menuBar.add(editMenu);

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

            if (text.isEmpty())
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

        if(e.getSource() == clearButton)
        {
            textArea.setText("");
            wordCountLabel.setText("WORD COUNT : 0");
            sentenceCountLabel.setText("SENTENCE COUNT : 0");
            charCountLabel.setText("CHARACTER COUNT : 0");
        }

        else if(e.getSource() == exitButton)
            System.exit(0);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new);
    }
}