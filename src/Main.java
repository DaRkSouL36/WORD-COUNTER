import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame implements ActionListener 
{
    JTextArea textArea;
    JLabel wordCountLabel, sentenceCountLabel, charCountLabel;
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

        textArea.getDocument().addDocumentListener(new Count());

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wordCountLabel = new JLabel("WORD COUNT : 0");
        wordCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        wordCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        wordCountLabel.setForeground(Color.CYAN);

        sentenceCountLabel = new JLabel("SENTENCE COUNT: 0");
        sentenceCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sentenceCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        sentenceCountLabel.setForeground(Color.MAGENTA);

        charCountLabel = new JLabel("CHARACTER COUNT: 0");
        charCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
        charCountLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        charCountLabel.setForeground(Color.DARK_GRAY);

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

        JPanel countPanel = new JPanel(new GridLayout(3, 1));
        countPanel.add(wordCountLabel);
        countPanel.add(sentenceCountLabel);
        countPanel.add(charCountLabel);

        add(scrollPane, BorderLayout.CENTER);
        add(countPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);
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
                wordCountLabel.setText("WORD COUNT: 0");
                sentenceCountLabel.setText("SENTENCE COUNT: 0");
                charCountLabel.setText("CHARACTER COUNT: 0");
            }

            else
            {
                String[] words = text.split("\\s+");
                int wordCount = words.length;
                int charCount = text.length();
                int sentenceCount = countSentences(text);
                wordCountLabel.setText("WORD COUNT : " + wordCount);
                sentenceCountLabel.setText("SENTENCE COUNT: " + sentenceCount);
                charCountLabel.setText("CHARACTER COUNT: " + charCount);
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
            sentenceCountLabel.setText("SENTENCE COUNT: 0");
            charCountLabel.setText("CHARACTER COUNT: 0");
        }

        else if(e.getSource() == exitButton)
            System.exit(0);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Main::new);
    }
}