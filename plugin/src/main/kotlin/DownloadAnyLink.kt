import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import ui.DownloadAnyLinkDialog

class DownloadAnyLink : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = PathSelector.getDestinationDir(currentProject)

        DownloadAnyLinkDialog(currentProject, destinationDir).show()
    }
}