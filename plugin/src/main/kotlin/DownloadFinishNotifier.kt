import com.intellij.notification.Notification
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object DownloadFinishNotifier {

    fun notify(project: Project, title: String, content: String): Notification {
        val notification: Notification = NOTIFICATION_GROUP.createNotification(title, content, NotificationType.INFORMATION)
        notification.notify(project)
        return notification
    }

    private val NOTIFICATION_GROUP = NotificationGroup("Download This", NotificationDisplayType.BALLOON, true)
}
