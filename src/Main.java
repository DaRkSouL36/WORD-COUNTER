import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame implements ActionListener
{
    JTextArea textArea;
    JLabel countLabel;
    JButton countButton;
    public Main()
    {
        super("WORD COUNTER");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        textArea = new JTextArea();
        textArea.setFont(new Font("Times New Roman", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        countLabel = new JLabel("WORD COUNT : 0");
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        countLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        countLabel.setForeground(Color.BLUE);

        countButton = new JButton("COUNT WORDS");
        countButton.addActionListener(this);
        countButton.setFont(new Font("Times New Roman", Font.BOLD, 14));
        countButton.setForeground(Color.WHITE);
        countButton.setBackground(new Color(30, 144, 255));
        countButton.setFocusPainted(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(countButton);

        add(scrollPane, BorderLayout.CENTER);
        add(countLabel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == countButton) {
            String text = textArea.getText().trim();
            if (text.isEmpty())
                countLabel.setText("NO WORDS ENTERED!");

            else
            {
                String[] words = text.split("\\s+");
                int wordCount = words.length;
                countLabel.setText("WORD COUNT : " + wordCount);
            }
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new Main();
            }
        });
    }
}