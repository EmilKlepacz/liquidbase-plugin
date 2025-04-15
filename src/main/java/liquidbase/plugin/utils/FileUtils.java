package liquidbase.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.io.*;

public class FileUtils {

    public static void createFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (file.createNewFile()) {
            System.out.println("File created successfully.");
        } else {
            System.out.println("File already exists.");
        }
    }

    public static void createFileAndFolder(String filePath) throws IOException {
        File file = new File(filePath);

        // Get parent directory
        File parentDir = file.getParentFile();

        // Create parent directory if it doesn't exist
        if (!parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Parent directory created successfully.");
            } else {
                throw new IOException("Failed to create parent directory.");
            }
        }

        // Create file if it doesn't exist
        if (file.createNewFile()) {
            System.out.println("File created successfully.");
        } else {
            System.out.println("File already exists.");
        }
    }

    public static void replacePlaceholdersInFile(String filePath, String[] placeholders, String[] finalValues) throws IOException {
        if (placeholders.length != finalValues.length) {
            throw new IllegalArgumentException();
        }

        File file = new File(filePath);

        // Read the content of the file
        StringBuilder templateContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line).append(System.lineSeparator());
            }
        }

        String finalContent = templateContent.toString();
        for (int i = 0; i < placeholders.length; i++) {
            finalContent = finalContent.toString().replace(placeholders[i], finalValues[i]);
        }

        // Write the modified content back to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(finalContent);
        }
    }

    public static PsiFile createPsiFileFromPath(Project project, String filePath) {
        PsiManager psiManager = PsiManager.getInstance(project);
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        // Get the VirtualFile corresponding to the file path
        com.intellij.openapi.vfs.VirtualFile virtualFile = localFileSystem.findFileByPath(filePath);
        if (virtualFile != null) {
            // Get or create the PsiFile instance for the VirtualFile
            return psiManager.findFile(virtualFile);
        }

        return null; // File not found
    }

    public static String getExtension(VirtualFile file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? null : fileName.substring(dotIndex + 1);
    }
}