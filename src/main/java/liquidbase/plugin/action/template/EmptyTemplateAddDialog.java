package liquidbase.plugin.action.template;


import com.intellij.openapi.ui.DialogWrapper;
import liquidbase.plugin.settings.AppSettingsState;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class EmptyTemplateAddDialog extends DialogWrapper {
    AppSettingsState settings = AppSettingsState.getInstance();

    private JTextField authorTextField;
    private JTextField ticketTextField;

    protected EmptyTemplateAddDialog() {
        super(true);
        init();
        String title = "Create changelog.";
        setTitle(title);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setMinimumSize(new Dimension(400, 30));

        // Add labels and text fields
        String authorLabel = "author:";
        panel.add(new JLabel(authorLabel));
        authorTextField = new JTextField();
        authorTextField.setText(settings.defaultAuthor);
        panel.add(authorTextField);

        String ticketLabel = "ticket:";
        panel.add(new JLabel(ticketLabel));
        ticketTextField = new JTextField();
        ticketTextField.setText(settings.defaultTicket);
        panel.add(ticketTextField);

        return panel;
    }

    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    public JTextField getTicketTextField() {
        return ticketTextField;
    }
}

