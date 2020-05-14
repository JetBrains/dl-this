import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiNamedElement

class DownloadThisLink : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = PathSelector.getDestinationDir(currentProject)

        val data = event.getData(CommonDataKeys.NAVIGATABLE)

        if (data is PsiNamedElement && data.isWebReference) {
            val url = data.name ?: return

            DownloadLauncher.runDownloadInBackground(currentProject, url, destinationDir)
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
    }
}