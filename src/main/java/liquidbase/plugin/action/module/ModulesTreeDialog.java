package liquidbase.plugin.action.module;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;

public class ModulesTreeDialog extends DialogWrapper {
    private Tree tree;
    private DefaultMutableTreeNode rootNode;
    private String selectedApiModPath;
    ArrayList<Module> registeredModules;

    public ModulesTreeDialog(ArrayList<Module> registeredModules) {
        super(true);
        this.registeredModules = registeredModules;
        init();
        setTitle("Registered Modules");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMinimumSize(new Dimension(400, 400));

        rootNode = new DefaultMutableTreeNode("Modules");
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        tree = new Tree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        // Add selection listener
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Object userObject = selectedNode.getUserObject();
                    if (userObject != null) {
                        selectedApiModPath = userObject.toString();
                    }
                }
            }
        });

        populateTree();

        JBScrollPane scrollPane = new JBScrollPane(tree);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void populateTree() {
        // Map to store nodes for easy access
        java.util.Map<Integer, DefaultMutableTreeNode> nodeMap = new java.util.HashMap<>();

        for (Module module : registeredModules) {
            DefaultMutableTreeNode parent;
            Integer nodeId = module.getApimod();
            Integer parentId = module.getApimodParent(); // Cast to Integer to handle null
            String nodeName = module.getApimodPath();

            // If it's a top-level node
            if (parentId == 0) {
                parent = rootNode;
            } else {  // If it's a child node
                parent = nodeMap.get(parentId);
            }

            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeName);
            parent.add(node);
            nodeMap.put(nodeId, node);
        }
    }

    public String getSelectedApiModPath() {
        return selectedApiModPath;
    }
}