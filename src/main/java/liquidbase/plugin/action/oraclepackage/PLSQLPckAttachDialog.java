package liquidbase.plugin.action.oraclepackage;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import liquidbase.plugin.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PLSQLPckAttachDialog extends DialogWrapper {
    TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
    private final String packagesFolderPath;

    protected PLSQLPckAttachDialog(String packagesFolderPath) {
        super(true);
        this.packagesFolderPath = packagesFolderPath;
        init();
        String title = "Attach PL/SQL package to changeSet.";
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setMinimumSize(new Dimension(400, 30));

        // Refresh and find the virtual file for the initial directory
        VirtualFile initialDir = LocalFileSystem.getInstance().refreshAndFindFileByPath(packagesFolderPath);

        // Add labels and file selector
        String oraclePLSQLPackageNameLabel = "package:";
        panel.add(new JLabel(oraclePLSQLPackageNameLabel));

        // Create the FileChooserDescriptor with the specified extensions and initial directory
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        descriptor.setTitle("Select Folder");
        if (initialDir != null) {
            descriptor.setRoots(initialDir); // Set initial directory if it exists
        }
        descriptor.setShowFileSystemRoots(true);
        descriptor.setForcedToUseIdeaFileChooser(true); // Force IntelliJ's file chooser

        String[] extensions = {"pck"};
        // Add the file filter for specific extensions
        descriptor.withFileFilter(file -> {
            String extension = FileUtils.getExtension(file);
            return extension != null && Arrays.asList(extensions).contains(extension.toLowerCase()) && !file.isDirectory();
        });

        textFieldWithBrowseButton.setPreferredSize(new Dimension(150, 24));
        textFieldWithBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor) {
            @Override
            protected void onFileChosen(@NotNull VirtualFile chosenFile) {
                textFieldWithBrowseButton.setText(chosenFile.getName());
            }
        });

        panel.add(textFieldWithBrowseButton);

        return panel;
    }

    public TextFieldWithBrowseButton getTextFieldWithBrowseButton() {
        return textFieldWithBrowseButton;
    }
}

