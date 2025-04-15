package liquidbase.plugin.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import liquidbase.plugin.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel settingsMainPanel;
    private final JBTextField settingsDefaultAuthor = new JBTextField();
    private final JBTextField settingsDefaultTicket = new JBTextField();
    private final JBTextField settingsJdbcUrl = new JBTextField();
    private final JBTextField settingsDbUser = new JBTextField();
    private final JPasswordField settingsDbPassword = new JPasswordField();
    private final TextFieldWithBrowseButton settingsOjdbcDriverPath = new TextFieldWithBrowseButton();

    public AppSettingsComponent() {
        // Create the FileChooserDescriptor with the specified extensions and initial directory
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        descriptor.setTitle("Select ojdbc.jar");
        descriptor.setShowFileSystemRoots(true);

        String[] extensions = {"jar"};
        // Add the file filter for specific extensions
        descriptor.withFileFilter(file -> {
            String extension = FileUtils.getExtension(file);
            return extension != null && Arrays.asList(extensions).contains(extension.toLowerCase()) && !file.isDirectory();
        });

        settingsOjdbcDriverPath.setPreferredSize(new Dimension(150, 24));
        settingsOjdbcDriverPath.addBrowseFolderListener(new TextBrowseFolderListener(descriptor) {
            @Override
            protected void onFileChosen(@org.jetbrains.annotations.NotNull VirtualFile chosenFile) {
                settingsOjdbcDriverPath.setText(chosenFile.getPath());
            }
        });

        settingsMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Default author name: "), settingsDefaultAuthor, 1, false)
                .addLabeledComponent(new JBLabel("Default ticket name: "), settingsDefaultTicket, 1, false)
                .addLabeledComponent(new JBLabel("JDBC URL: "), settingsJdbcUrl, 1, false)
                .addLabeledComponent(new JBLabel("Db User: "), settingsDbUser, 1, false)
                .addLabeledComponent(new JBLabel("Password: "), settingsDbPassword, 1, false)
                .addLabeledComponent(new JBLabel("ojdbc.jar: "), settingsOjdbcDriverPath, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return settingsMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return settingsDefaultAuthor;
    }

    @NotNull
    public String getDefaultAuthor() {
        return settingsDefaultAuthor.getText();
    }

    @NotNull
    public String getDefaultTicket() {
        return settingsDefaultTicket.getText();
    }

    public String getSettingsJdbcUrl() {
        return settingsJdbcUrl.getText();
    }

    public String getDbUser() {
        return settingsDbUser.getText();
    }

    public String getDbPassword() {
        return new String(settingsDbPassword.getPassword());
    }

    public String getOjdbcDriverPath() {
        return settingsOjdbcDriverPath.getText();
    }

    public void setDefaultAuthorText(@NotNull String newText) {
        settingsDefaultAuthor.setText(newText);
    }

    public void setDefaultTicketText(@NotNull String newText) {
        settingsDefaultTicket.setText(newText);
    }

    public void setJdbcUrlText(@NotNull String newText) {
        settingsJdbcUrl.setText(newText);
    }

    public void setDbUserText(@NotNull String newText) {
        settingsDbUser.setText(newText);
    }

    public void setDbPasswordText(@NotNull String newText) {
        settingsDbPassword.setText(newText);
    }

    public void setOjdbcDriverPath(@NotNull String newText) {
        settingsOjdbcDriverPath.setText(newText);
    }
}
