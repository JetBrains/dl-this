import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.project.Project

object PathSelector {

    fun getDestinationDir(project: Project): String {
        return project.basePath ?: ProjectUtil.getBaseDir()
    }
}