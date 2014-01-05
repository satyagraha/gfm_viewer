package code.satyagraha.gfm.viewer.bots;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ProjectBot {
    
    private final static SWTWorkbenchBot bot = new SWTWorkbenchBot();

    private String projectName;

    public ProjectBot(String projectName) {
        this.projectName = projectName;
    }

    public static ProjectBot createSimpleProject(String projectName) {
        bot.menu("File").menu("New").menu("Project...").click();
        SWTBotShell shell = bot.shell("New Project");
        shell.activate();
        bot.tree().expandNode("General").select("Project");
        bot.button("Next >").click();
        bot.textWithLabel("Project name:").setText(projectName);
        bot.button("Finish").click();

        return new ProjectBot(projectName);
    }

    public class ProjectFileBot {

        private String fileName;

        public ProjectFileBot(String fileName) {
            this.fileName = fileName;
        }
        
    }
    
    public ProjectFileBot newFile(String fileName) {
        SWTBotTree projectsTree = bot.viewByTitle("Project Explorer").bot().tree();
        SWTBotTreeItem projectTree = projectsTree.getTreeItem(projectName);
        projectTree.setFocus();
        projectTree.contextMenu("New").menu("File").click();
        SWTBotShell shell = bot.shell("New File");
        shell.activate();
        bot.textWithLabel("File name:").setText(fileName);
        bot.button("Finish").click();

        return new ProjectFileBot(fileName);
    }
    
}
