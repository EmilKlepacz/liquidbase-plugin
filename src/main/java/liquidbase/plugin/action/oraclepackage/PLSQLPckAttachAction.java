package liquidbase.plugin.action.oraclepackage;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import liquidbase.plugin.action.AttachToChangelogAction;
import liquidbase.plugin.settings.AppSettingsState;
import liquidbase.plugin.utils.FileUtils;
import liquidbase.plugin.utils.PathUtils;
import liquidbase.plugin.utils.PluginResourcesUtils;
import liquidbase.plugin.utils.XmlModifier;

import java.io.IOException;

public class PLSQLPckAttachAction extends AttachToChangelogAction {
    private final AppSettingsState settings = AppSettingsState.getInstance();

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        String packagesFolderPath = PathUtils.getPathRelativeToProjectRoot(project, "src/main/database/plsql");

        if (project != null) {
            VirtualFile virtualFile = (VirtualFile) event.getDataContext().getData("virtualFile");
            PsiManager psiManager = PsiManager.getInstance(project);
            PsiFile psiFile = psiManager.findFile(virtualFile);

            PLSQLPckAttachDialog dialog = new PLSQLPckAttachDialog(packagesFolderPath);
            dialog.show();
            if (dialog.isOK()) {
                String changelogPath = PathUtils.getPathRelativeToProjectRoot(event.getProject(), "src/main/database/updates/" + psiFile.getName().split("\\.")[0]);
                String oraclePLSQLPackageName = dialog.getTextFieldWithBrowseButton().getText();
                String oraclePLSQLPackageNameNoExt = oraclePLSQLPackageName.split("\\.")[0].toUpperCase();
                String oraclePLSQLPackagePrefix = oraclePLSQLPackageName.substring(0, 3).toLowerCase();
                String newChangelogXMLPath = changelogPath + "/" + oraclePLSQLPackageName.split("\\.")[0] + ".changelog.xml";
                String author = settings.defaultAuthor;
                String ticketId = psiFile.getName().split("\\.")[0] + "-" + oraclePLSQLPackageName.split("\\.")[0];
                try {
                    FileUtils.createFileAndFolder(newChangelogXMLPath);
                    PluginResourcesUtils.copyResourceToFile("templates/plsqlpackage.changelog.xml", newChangelogXMLPath);

                    FileUtils.replacePlaceholdersInFile(newChangelogXMLPath,
                            new String[]{
                                    "$AUTHOR$",
                                    "$TICKET_ID$",
                                    "$PACKAGE_NAME$",
                                    "$PACKAGE_NAME_NO_EXT$",
                                    "$PACKAGE_PREFIX$"},
                            new String[]{
                                    author,
                                    ticketId,
                                    oraclePLSQLPackageName,
                                    oraclePLSQLPackageNameNoExt,
                                    oraclePLSQLPackagePrefix});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                //modify selected changelog
                String tagName = "include";
                String attributeName = "file";

                String attributeVal = psiFile.getName().split("\\.")[0] + "/" + oraclePLSQLPackageName.split("\\.")[0] + ".changelog.xml";
                XmlModifier.addTagBetweenTags(project, psiFile, "changeSet", tagName, attributeName, attributeVal);

                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
            }
        }
    }
}