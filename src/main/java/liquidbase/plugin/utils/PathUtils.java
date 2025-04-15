package liquidbase.plugin.utils;

import com.intellij.openapi.project.Project;

public class PathUtils {
    public static String getPathRelativeToProjectRoot(Project project, String relativePath) {
        String projectBasePath = project.getBasePath();
        if (projectBasePath != null) {
            return projectBasePath + "/" + relativePath;
        } else {
            throw new IllegalStateException("Project base path is null.");
        }
    }
}