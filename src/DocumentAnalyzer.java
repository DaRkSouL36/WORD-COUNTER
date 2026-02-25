import javax.swing.event.DocumentEvent; // IMPORT FOR DOCUMENT EVENT HANDLING
import javax.swing.event.DocumentListener; // IMPORT FOR DOCUMENT LISTENER INTERFACE
import java.util.Arrays; // IMPORT FOR ARRAYS
import java.util.HashSet; // IMPORT FOR HASHSET
import java.util.Set; // IMPORT FOR SET

// DOCUMENT LISTENER TO TRACK TEXT CHANGES AND UPDATE METRICS
public class DocumentAnalyzer implements DocumentListener
{
    private Main mainApp; // REFERENCE TO THE MAIN APPLICATION WINDOW

    // CONSTRUCTOR THAT ACCEPTS THE MAIN APP AS A PARAMETER
    public DocumentAnalyzer(Main mainApp)
    {
        this.mainApp = mainApp;
    }

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
        String text = mainApp.textArea.getText().trim(); // GET TEXT FROM TEXTAREA IN MAIN

        if(text.isEmpty()) // IF TEXT AREA IS EMPTY
        {
            mainApp.charCountLabel.setText("CHARACTER COUNT : 0");
            mainApp.wordCountLabel.setText("WORD COUNT : 0");
            mainApp.sentenceCountLabel.setText("SENTENCE COUNT : 0");
        }
        else
        {
            String[] words = text.split("\\s+"); // SPLIT TEXT INTO WORDS
            int wordCount = words.length; // COUNT WORDS
            int charCount = text.length(); // COUNT CHARACTERS
            int sentenceCount = countSentences(text); // COUNT SENTENCES

            mainApp.charCountLabel.setText("CHARACTER COUNT : " + charCount);
            mainApp.wordCountLabel.setText("WORD COUNT : " + wordCount);
            mainApp.sentenceCountLabel.setText("SENTENCE COUNT : " + sentenceCount);
        }

        mainApp.lineNumbers.updateLineNumbers();

        // ONLY UPDATE THE TITLE BAR IF THE FLAG ISN'T ALREADY SET TO TRUE
        if(!mainApp.hasUnsavedChanges)
        {
            mainApp.hasUnsavedChanges = true;
            mainApp.updateWindowTitle();
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