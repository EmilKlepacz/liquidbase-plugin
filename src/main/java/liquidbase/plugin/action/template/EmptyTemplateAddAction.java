package liquidbase.plugin.action.template;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import liquidbase.plugin.utils.FileUtils;
import liquidbase.plugin.utils.PathUtils;
import liquidbase.plugin.utils.PluginResourcesUtils;
import liquidbase.plugin.utils.XmlModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;

public class EmptyTemplateAddAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
     * declarations. Only needed in {@link EmptyTemplateAddAction} class because a second constructor is overridden.
     */
    public EmptyTemplateAddAction() {
        super();
    }

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    public EmptyTemplateAddAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project currentProject = event.getProject();
        String updatesPath = PathUtils.getPathRelativeToProjectRoot(currentProject, "src/main/database/updates");

        EmptyTemplateAddDialog dialog = new EmptyTemplateAddDialog();
        dialog.show();
        if (dialog.isOK()) {
            String author = dialog.getAuthorTextField().getText();
            String ticketId = dialog.getTicketTextField().getText();

            String newChangelogXMLPath = updatesPath + "/" + ticketId + "/" + ticketId + ".changelog.xml";

            try {
                FileUtils.createFileAndFolder(newChangelogXMLPath);
                PluginResourcesUtils.copyResourceToFile("templates/new_changelog_template.changelog.xml", newChangelogXMLPath);

                FileUtils.replacePlaceholdersInFile(newChangelogXMLPath,
                        new String[]{
                                "$AUTHOR$",
                                "$TICKET_ID$"},
                        new String[]{
                                author,
                                ticketId});

                String devChangelogPath = updatesPath + "/" + "development.changelog.xml";
                String attributeValue = ticketId + "/" + ticketId + ".changelog.xml";
                PsiFile psiFileDevChangelog = FileUtils.createPsiFileFromPath(currentProject, devChangelogPath);
                XmlModifier.addTagInsideRootTag(currentProject, psiFileDevChangelog, "include", "file", attributeValue);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
        }
    }

    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}
