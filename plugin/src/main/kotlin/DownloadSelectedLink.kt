import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project

class DownloadSelectedLink : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = getDestinationDir(currentProject)

        val selectedText = event.getData(CommonDataKeys.CARET)?.selectedText

        if (selectedText != null) {
            DownloadLauncher.runDownloadsInBackground(currentProject, listOf(selectedText), destinationDir)
        }
    }

    override fun update(event: AnActionEvent) {
        val data = event.getData(CommonDataKeys.CARET)

        event.presentation.isEnabledAndVisible = data?.hasSelection() ?: false
    }

    companion object {

        private fun getDestinationDir(project: Project?): String {
            return ProjectUtil.getBaseDir()  // todo: use project to determine dir
        }
    }
}