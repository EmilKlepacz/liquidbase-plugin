package liquidbase.plugin.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public abstract class AttachToChangelogAction extends AnAction {
    @Override
    public void update(AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            VirtualFile file = (VirtualFile) event.getDataContext().getData("virtualFile");
            if (file != null) {
                // Get the file name
                String fileName = file.getName();
                event.getPresentation().setEnabledAndVisible(fileName.endsWith(".changelog.xml"));
            }
        }
    }

    @Override
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // or ActionUpdateThread.EDT if it requires the main thread
    }
}
