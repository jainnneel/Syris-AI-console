package ai.syris.app;

import javafx.scene.input.ClipboardContent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

public class CopyAndPaste {
    public static void main(String[] args) throws AWTException {
        String textToCopy = "Hello, this is a copied text!";

        // Copy text to clipboard
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(textToCopy);
        clipboard.setContents(selection, null);

        System.out.println("Text copied to clipboard!");

        // Simulate Ctrl + V to paste the copied text
        Robot robot = new Robot();
        robot.delay(500);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        System.out.println("Text pasted!");
    }
}
