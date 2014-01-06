package code.satyagraha.gfm.viewer.bots;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ProjectBot {
    
    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();
    
    private SWTBotTreeItem projectTree;

    public ProjectBot(SWTBotTreeItem projectTree) {
        this.projectTree = projectTree;
    }

    public static ProjectBot createSimpleProject() {
        String projectName = "Project_" + System.currentTimeMillis();
        bot.menu("File").menu("New").menu("Project...").click();
        SWTBotShell shell = bot.shell("New Project");
        shell.activate();
        bot.tree().expandNode("General").select("Project");
        bot.button("Next >").click();
        bot.textWithLabel("Project name:").setText(projectName);
        bot.button("Finish").click();
        SWTUtils.sleep(2000);
        SWTBotTree projectsTree = bot.viewByTitle("Project Explorer").bot().tree();
        SWTBotTreeItem projectTree = projectsTree.getTreeItem(projectName);
        return new ProjectBot(projectTree);
    }

    public class ProjectFileBot {

        private SWTBotTreeItem treeItem;

        public ProjectFileBot(SWTBotTreeItem treeItem) {
            this.treeItem = treeItem;
        }

        public void showInGfmView() {
            treeItem.contextMenu("Show in GFM view").click();
        }
        
        public void generateMarkdownPreview() {
            treeItem.contextMenu("Generate Markdown Preview").click();
        }
        
        public IFile asIFile() {
            Path filePath = new Path(treeItem.getText());
            return ResourcesPlugin.getWorkspace().getRoot().getProject(projectTree.getText()).getFile(filePath);
        }

    }
    
    public ProjectFileBot newFile(String fileName) {
        projectTree.setFocus();
        projectTree.contextMenu("New").menu("File").click();
        SWTBotShell shell = bot.shell("New File");
        shell.activate();
        bot.textWithLabel("File name:").setText(fileName);
        bot.button("Finish").click();
        SWTUtils.sleep(2000);
        SWTBotTreeItem treeItem = projectTree.getNode(fileName);
        return new ProjectFileBot(treeItem);
    }
    
    public void delete() {
        projectTree.contextMenu("Refresh").click();
        SWTUtils.sleep(2000);
        projectTree.contextMenu("Delete").click();
        SWTBotShell shell = bot.shell("Delete Resources");
        shell.activate();
        bot.button("OK").click();
        projectTree = null;
    }
    
}
