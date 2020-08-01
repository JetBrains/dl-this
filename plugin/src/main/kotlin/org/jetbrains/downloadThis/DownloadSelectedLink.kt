package org.jetbrains.downloadThis

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.DumbAwareAction

class DownloadSelectedLink : DumbAwareAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val currentProject = event.project ?: return
        val destinationDir = PathSelector.getDestinationDir(currentProject)

        val selectedText = event.getData(CommonDataKeys.CARET)?.selectedText

        if (selectedText != null) {
            DownloadLauncher.runDownloadInBackground(currentProject, selectedText, destinationDir)
        }
    }

    override fun update(event: AnActionEvent) {
        val data = event.getData(CommonDataKeys.CARET)

        event.presentation.isEnabledAndVisible = data?.hasSelection() ?: false
    }
}