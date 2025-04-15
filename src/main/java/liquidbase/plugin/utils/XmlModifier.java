package liquidbase.plugin.utils;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.*;

public class XmlModifier {
    public static void addTagInsideRootTag(Project project,
                                           PsiFile psiFile,
                                           String tagName,
                                           String attributeName,
                                           String attributeValue) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Find the root XML tag
            XmlTag rootTag = ((XmlFile) psiFile).getRootTag();

            if (rootTag != null) {
                XmlTag includeTag = rootTag.createChildTag(tagName, "", null, false);
                includeTag.setAttribute(attributeName, attributeValue);

                // Add the include tag as the last child of the root tag
                rootTag.addSubTag(includeTag, false);
            }
        });
    }

    public static void addTagBetweenTags(Project project,
                                         PsiFile psiFile,
                                         String betweenTagName,
                                         String tagName,
                                         String attributeName,
                                         String attributeValue) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
                    XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
                    if (rootTag != null) {
                        XmlTag[] changeSets = rootTag.findSubTags(betweenTagName);


                        boolean foundFirstChangeSetTag = false;
                        // Iterate over changeSet tags and find the position to insert the new tag
                        for (XmlTag changeSet : changeSets) {

                            if (!foundFirstChangeSetTag) {
                                foundFirstChangeSetTag = true;
                            } else {
                                // Create a new tag
                                XmlTag newXmlTag = XmlElementFactory.getInstance(psiFile.getProject()).createTagFromText("<" + tagName + " " + attributeName + "=\"" + attributeValue+ "\"/>");

                                // Insert the new tag before the last changeSet
                                changeSet.getParent().addBefore(newXmlTag, changeSet);
                                return; // Exit after adding the new tag
                            }

                        }
                    }

                }
        );
    }
}