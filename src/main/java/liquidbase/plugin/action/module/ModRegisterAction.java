package liquidbase.plugin.action.module;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import liquidbase.plugin.action.AttachToChangelogAction;
import liquidbase.plugin.settings.AppSettingsState;
import liquidbase.plugin.utils.*;

import java.io.IOException;

public class ModRegisterAction extends AttachToChangelogAction {

    AppSettingsState settings = AppSettingsState.getInstance();

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        if (project != null) {
            VirtualFile virtualFile = (VirtualFile) event.getDataContext().getData("virtualFile");
            PsiManager psiManager = PsiManager.getInstance(project);
            PsiFile psiFile = psiManager.findFile(virtualFile);

            ModRegisterDialog dialog = new ModRegisterDialog();
            dialog.show();
            if (dialog.isOK()) {

                OracleConnector.getApiModuleRegisterSQL(dialog.getSelectedModule())
                        .thenAccept(moduleRegisterSQL -> {
                            String changelogPath = PathUtils.getPathRelativeToProjectRoot(event.getProject(), "src/main/database/updates/" + psiFile.getName().split("\\.")[0]);
                            String apiModId = String.valueOf((moduleRegisterSQL.getApiMod()));
                            String newChangelogXMLPath = changelogPath + "/" + "apimod_" + apiModId + ".changelog.xml";
                            String author = settings.defaultAuthor;
                            String ticketId = psiFile.getName().split("\\.")[0] + "-" + "apimod_" + apiModId;
                            String apimodRegisterSQL = moduleRegisterSQL.getRegisterSQL();
                            try {
                                FileUtils.createFileAndFolder(newChangelogXMLPath);
                                PluginResourcesUtils.copyResourceToFile("templates/apimod.changelog.xml", newChangelogXMLPath);
                                FileUtils.replacePlaceholdersInFile(newChangelogXMLPath,
                                        new String[]{
                                                "$AUTHOR$",
                                                "$TICKET_ID$",
                                                "$APIMOD_REGISTER_SQL$",
                                                "$APIMOD_ID$"
                                        },
                                        new String[]{
                                                author,
                                                ticketId,
                                                apimodRegisterSQL,
                                                apiModId});
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            //modify selected changelog
                            String tagName = "include";
                            String attributeName = "file";

                            String attributeVal = psiFile.getName().split("\\.")[0] + "/" + "apimod_" + apiModId + ".changelog.xml";
                            XmlModifier.addTagBetweenTags(project, psiFile, "changeSet", tagName, attributeName, attributeVal);

                            ApplicationManager.getApplication().invokeAndWait(() -> {
                                VirtualFileManager.getInstance().syncRefresh();
                            });

                            // Use WriteCommandAction to reformat the files
                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                VirtualFile newVirtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + newChangelogXMLPath);
                                if (newVirtualFile != null) {
                                    PsiFile newPsiFile = PsiManager.getInstance(project).findFile(newVirtualFile);
                                    if (newPsiFile != null) {
                                        CodeStyleManager.getInstance(project).reformat(newPsiFile);
                                    }
                                }
                                // Format the modified file
                                CodeStyleManager.getInstance(project).reformat(psiFile);
                            });

                        });

            }
        }
    }
}
