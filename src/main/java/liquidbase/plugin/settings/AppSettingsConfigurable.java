package liquidbase.plugin.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered in an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Cobra Liquidbase Plugin settings";
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        boolean modified =
                !mySettingsComponent.getDefaultAuthor().equals(settings.defaultAuthor) ||
                        !mySettingsComponent.getDefaultTicket().equals(settings.defaultTicket) ||
                        !mySettingsComponent.getSettingsJdbcUrl().equals(settings.JdbcUrl) ||
                        !mySettingsComponent.getDbUser().equals(settings.dbUser) ||
                        !mySettingsComponent.getDbPassword().equals(settings.dbPassword) ||
                        !mySettingsComponent.getSettingsJdbcUrl().equals(settings.ojdbcDriverPath);
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.defaultAuthor = mySettingsComponent.getDefaultAuthor();
        settings.defaultTicket = mySettingsComponent.getDefaultTicket();
        settings.JdbcUrl = mySettingsComponent.getSettingsJdbcUrl();
        settings.dbUser = mySettingsComponent.getDbUser();
        settings.dbPassword = mySettingsComponent.getDbPassword();
        settings.ojdbcDriverPath = mySettingsComponent.getOjdbcDriverPath();
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        mySettingsComponent.setDefaultAuthorText(settings.defaultAuthor);
        mySettingsComponent.setDefaultTicketText(settings.defaultTicket);
        mySettingsComponent.setJdbcUrlText(settings.JdbcUrl);
        mySettingsComponent.setDbUserText(settings.dbUser);
        mySettingsComponent.setDbPasswordText(settings.dbPassword);
        mySettingsComponent.setOjdbcDriverPath(settings.ojdbcDriverPath);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
