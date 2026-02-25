import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.*;

// CONTROLLER CLASS TO HANDLE ALL USER ACTIONS AND KEYBOARD SHORTCUTS
public class EditorActions
{
    private Main mainApp; // REFERENCE TO MAIN APP FOR UI MANIPULATION
    private EditorDialogs dialogs; // REFERENCE TO DIALOG HELPER

    // PUBLIC ACTION FIELDS TO BE ACCESSED BY THE MAIN UI
    public Action openAction, saveAction, exportPdfAction, undoAction, redoAction;
    public Action findAction, findReplaceAction, goToLineAction, fontAction, colorAction;
    public Action zoomInAction, zoomOutAction, clearAction, exitAction, darkModeAction;
    public Action textStatsAction, wordWrapAction;

    // CONSTRUCTOR
    public EditorActions(Main mainApp, EditorDialogs dialogs)
    {
        this.mainApp = mainApp;
        this.dialogs = dialogs;
        initializeActions();
    }

    // INITIALIZE ALL ACTIONS
    private void initializeActions()
    {
        openAction = new AbstractAction("OPEN")
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showOpenDialog(mainApp);
                if(option == JFileChooser.APPROVE_OPTION)
                {
                    try(BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile())))
                    {
                        mainApp.textArea.read(reader, null);
                        mainApp.lineNumbers.updateLineNumbers();
                        mainApp.undoManager.discardAllEdits();

                        mainApp.currentFileName = fileChooser.getSelectedFile().getName();
                        mainApp.hasUnsavedChanges = false;
                        mainApp.updateWindowTitle();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        openAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        saveAction = new AbstractAction("SAVE")
        {
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILE", "txt"));
                int option = fileChooser.showSaveDialog(mainApp);
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
                        mainApp.textArea.write(writer);
                        writer.close();

                        mainApp.currentFileName = file.getName();
                        mainApp.hasUnsavedChanges = false;
                        mainApp.updateWindowTitle();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        saveAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        exportPdfAction = new AbstractAction("EXPORT AS PDF")
        {
            public void actionPerformed(ActionEvent e) { dialogs.exportToPDF(); }
        };
        exportPdfAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        undoAction = new AbstractAction("UNDO")
        {
            public void actionPerformed(ActionEvent e)
            {
                try { if(mainApp.undoManager.canUndo()) mainApp.undoManager.undo(); }
                catch(Exception ex) { ex.printStackTrace(); }
            }
        };
        undoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        redoAction = new AbstractAction("REDO")
        {
            public void actionPerformed(ActionEvent e)
            {
                try { if(mainApp.undoManager.canRedo()) mainApp.undoManager.redo(); }
                catch(Exception ex) { ex.printStackTrace(); }
            }
        };
        redoAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        findAction = new AbstractAction("FIND")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showFindDialog(); }
        };
        findAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        findReplaceAction = new AbstractAction("FIND & REPLACE")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showFindAndReplaceDialog(); }
        };
        findReplaceAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        goToLineAction = new AbstractAction("GO TO LINE")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showGoToLineDialog(); }
        };
        goToLineAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        fontAction = new AbstractAction("FONT...")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showFontChooser(); }
        };

        colorAction = new AbstractAction("COLOR...")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showColorChooser(); }
        };

        zoomInAction = new AbstractAction("ZOOM IN")
        {
            public void actionPerformed(ActionEvent e) { mainApp.zoomIn(); }
        };
        zoomInAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        zoomOutAction = new AbstractAction("ZOOM OUT")
        {
            public void actionPerformed(ActionEvent e) { mainApp.zoomOut(); }
        };
        zoomOutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        clearAction = new AbstractAction("CLEAR TEXT")
        {
            public void actionPerformed(ActionEvent e) { mainApp.textArea.setText(""); }
        };
        clearAction.putValue(Action.SHORT_DESCRIPTION, "CLEAR ALL TEXT IN THE TEXT AREA");

        exitAction = new AbstractAction("EXIT")
        {
            public void actionPerformed(ActionEvent e) { System.exit(0); }
        };
        exitAction.putValue(Action.SHORT_DESCRIPTION, "EXIT THE APPLICATION");

        darkModeAction = new AbstractAction("DARK MODE")
        {
            public void actionPerformed(ActionEvent e) { mainApp.toggleDarkMode(); }
        };
        darkModeAction.putValue(Action.SHORT_DESCRIPTION, "TOGGLE DARK MODE");

        textStatsAction = new AbstractAction("TEXT STATISTICS")
        {
            public void actionPerformed(ActionEvent e) { dialogs.showTextStatistics(); }
        };
        textStatsAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        wordWrapAction = new AbstractAction("WORD WRAP")
        {
            public void actionPerformed(ActionEvent e)
            {
                boolean isWrapped = mainApp.textArea.getLineWrap();
                mainApp.textArea.setLineWrap(!isWrapped);
                mainApp.textArea.setWrapStyleWord(!isWrapped);
            }
        };
        wordWrapAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_DOWN_MASK));
    }
}