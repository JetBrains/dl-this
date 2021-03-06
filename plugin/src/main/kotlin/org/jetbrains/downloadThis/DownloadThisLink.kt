/*
 * MIT License
 *
 * Copyright (c) 2020-2020 JetBrains s.r.o.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jetbrains.downloadThis

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