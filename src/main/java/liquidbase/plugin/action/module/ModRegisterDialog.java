package liquidbase.plugin.action.module;

import com.intellij.openapi.ui.DialogWrapper;
import liquidbase.plugin.settings.AppSettingsState;
import liquidbase.plugin.utils.OracleConnector;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.util.Optional;

public class ModRegisterDialog extends DialogWrapper {
    AppSettingsState settings = AppSettingsState.getInstance();

    private JComboBox<String> unregisteredModulesCb;

    protected ModRegisterDialog() {
        super(true);

        try {
            OracleConnector.loadOJDBCDriver();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        init();
        String title = "Attach apimod# registration to changeSet.";
        setTitle(title);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        unregisteredModulesCb = new JComboBox<>();
        unregisteredModulesCb.setEnabled(false); // Disable until data is loaded

        OracleConnector.getUnregisteredModules().thenAccept(unregisteredModules -> {
            // Populate combo box with module names
            for (String unregisteredModule : unregisteredModules) {
                unregisteredModulesCb.addItem(unregisteredModule);
            }
            // Enable combo box after data is loaded
            unregisteredModulesCb.setEnabled(true);
        });

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setMinimumSize(new Dimension(400, 30));

        // Add labels and file selector
        String authorLabel = "author:";
        panel.add(new JLabel(authorLabel));
        JTextField authorTextField = new JTextField();
        authorTextField.setText(settings.defaultAuthor);
        panel.add(authorTextField);

        // Create the panel and add the combo box
        String selectModuleLabel = "Select Module:";
        panel.add(new JLabel(selectModuleLabel));
        panel.add(unregisteredModulesCb);
        panel.setMinimumSize(new Dimension(400, 30));

        return panel;
    }

    public String getSelectedModule() {
        return Optional.ofNullable(unregisteredModulesCb.getSelectedItem())
                .map(Object::toString)
                .orElse("");
    }
}
