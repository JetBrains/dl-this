import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiNamedElement

class DownloadThisLink : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = getDestinationDir(currentProject)

        val data = event.getData(CommonDataKeys.NAVIGATABLE)

        if (data is PsiNamedElement && data.isWebReference) {
            val url = data.name ?: return

            DownloadLauncher.runDownloadsInBackground(currentProject, listOf(url), destinationDir)
        }
    }

    override fun update(event: AnActionEvent) {
        val data = event.getData(CommonDataKeys.NAVIGATABLE)

        event.presentation.isEnabledAndVisible = event.project != null &&
                data is PsiNamedElement && data.isWebReference
    }

    companion object {

        private val Navigatable.isWebReference
            get() = (this::class.qualifiedName ?: "").startsWith("com.intellij.openapi.paths.WebReference")

        private fun getDestinationDir(project: Project?): String {
            return ProjectUtil.getBaseDir()  // todo: use project to determine dir
        }
    }
}