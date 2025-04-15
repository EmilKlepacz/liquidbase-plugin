package liquidbase.plugin.action.function;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import liquidbase.plugin.action.module.ModulesTreeDialog;
import liquidbase.plugin.settings.AppSettingsState;
import liquidbase.plugin.utils.OracleConnector;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.util.Optional;

public class FunRegisterDialog extends DialogWrapper {
    AppSettingsState settings = AppSettingsState.getInstance();

    private ComboBox<String> securityLevelComboBox;
    private ComboBox<String> implementationComboBox;
    private ComboBox<String> outputTypeComboBox;

    private JTextField moduleTextField;
    private final JButton moduleSelectButton = new JButton("Select module");

    private JTextField nameTextField;
    private JTextField shortDescTextField;

    private ModulesTreeDialog modulesTreeDialog;

    protected FunRegisterDialog() {
        super(true);

        try {
            OracleConnector.loadOJDBCDriver();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        init();
        String title = "Attach apifun# registration to changeSet.";
        setTitle(title);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.setMinimumSize(new Dimension(400, 30));

        // Add labels and file selector
        String authorLabel = "author:";
        panel.add(new JLabel(authorLabel));
        JTextField authorTextField = new JTextField();
        authorTextField.setText(settings.defaultAuthor);
        panel.add(authorTextField);

        panel.add(moduleSelectButton);

        moduleTextField = new JTextField();
        moduleTextField.setEditable(false);
        panel.add(moduleTextField);

        moduleSelectButton.addActionListener(e -> OracleConnector.getModules().thenAccept(modules -> {
            // Process the retrieved modules here
            // This code will be executed once the modules are fetched and the connection is closed

            //invokeLater() - used for thread safe
            SwingUtilities.invokeLater(() -> {
                modulesTreeDialog = new ModulesTreeDialog(modules);
                modulesTreeDialog.show();

                if (modulesTreeDialog.isOK()) {
                    String selectedApiModPath = modulesTreeDialog.getSelectedApiModPath();
                    moduleTextField.setText(selectedApiModPath);
                }
            });

        }));

        String nameLabel = "Name:";
        panel.add(new JLabel(nameLabel));
        nameTextField = new JTextField();
        panel.add(nameTextField);

        String shortDescLabel = "Short Desc:";
        panel.add(new JLabel(shortDescLabel));
        shortDescTextField = new JTextField();
        panel.add(shortDescTextField);

        String securityLevelLabel = "Security Level:";
        panel.add(new JLabel(securityLevelLabel));
        String[] securityLevelOptions = {"PUBLIC", "AUTHENTICATION", "AUTHORIZATION", "INTERNAL"}; // Example items
        securityLevelComboBox = new ComboBox<>(securityLevelOptions);
        securityLevelComboBox.setSelectedItem("AUTHENTICATION");
        panel.add(securityLevelComboBox);

        String implementationLabel = "Implementation:";
        panel.add(new JLabel(implementationLabel));
        String[] implementationOptions = {"STANDARD", "INTERNAL", "DOCUMENT"};
        implementationComboBox = new ComboBox<>(implementationOptions);
        implementationComboBox.setSelectedItem("STANDARD");
        panel.add(implementationComboBox);

        String outputTypeLabel = "Output Type:";
        panel.add(new JLabel(outputTypeLabel));
        String[] outputTypeOptions = {"VOID", "SCALAR", "SET"};
        outputTypeComboBox = new ComboBox<>(outputTypeOptions);
        panel.add(outputTypeComboBox);

        return panel;
    }

    public String getName() {
        return nameTextField.getText();
    }

    public String getShortDesc() {
        return shortDescTextField.getText();
    }

    public int getSecurityLevel() {
        return Optional.ofNullable(securityLevelComboBox.getSelectedItem())
                .map(Object::toString)
                .map(selected -> {
                    switch (selected) {
                        case "INTERNAL": return 1;
                        case "AUTHORIZATION": return 2;
                        case "AUTHENTICATION": return 3;
                        case "PUBLIC": return 4;
                        default: return -1;
                    }
                })
                .orElse(-1);
    }

    public int getImplementation() {
        return Optional.ofNullable(implementationComboBox.getSelectedItem())
                .map(Object::toString)
                .map(selected -> {
                    switch (selected) {
                        case "STANDARD":
                            return 1;
                        case "INTERNAL":
                            return 2;
                        case "DOCUMENT":
                            return 3;
                        default:
                            return -1;
                    }
                })
                .orElse(-1);
    }

    public int getOutputType() {
        return Optional.ofNullable(outputTypeComboBox.getSelectedItem())
                .map(Object::toString)
                .map(selected -> {
                    switch (selected) {
                        case "VOID":
                            return 1;
                        case "SCALAR":
                            return 2;
                        case "SET":
                            return 3;
                        default:
                            return -1;
                    }
                })
                .orElse(-1);
    }

    public String getApiModPath() {
        return moduleTextField.getText();
    }


}
