import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import ui.DownloadAnyLinkDialog

class DownloadAnyLink : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = getDestinationDir(currentProject)

        DownloadAnyLinkDialog(currentProject, destinationDir).show()
    }

    companion object {

        private fun getDestinationDir(project: Project?): String {
            return ProjectUtil.getBaseDir()  // todo: use project to determine dir
        }
    }
}