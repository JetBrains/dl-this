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
package org.jetbrains.downloadThis.ui

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.SpellCheckingEditorCustomizationProvider
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.ui.*
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel

class LinksEditor(project: Project) : JPanel(BorderLayout()), Disposable, LafManagerListener {

    val editorField: EditorTextField

    override fun lookAndFeelChanged(source: LafManager) {
        val editor = editorField.editor
        if (editor is EditorEx) {
            COLOR_SCHEME_FOR_CURRENT_UI_THEME_CUSTOMIZATION.customize(editor)
        }
    }

    override fun dispose() {}

    companion object {

        private val COLOR_SCHEME_FOR_CURRENT_UI_THEME_CUSTOMIZATION = EditorCustomization { editor: EditorEx ->
            editor.setBackgroundColor(null)  // to use background from set color scheme
            editor.colorsScheme = colorScheme
        }

        private val colorScheme: EditorColorsScheme
            get() {
                val isLaFDark = ColorUtil.isDark(UIUtil.getPanelBackground())
                val isEditorDark = EditorColorsManager.getInstance().isDarkEditor

                return when (isLaFDark == isEditorDark) {
                    true -> EditorColorsManager.getInstance().globalScheme
                    false -> EditorColorsManager.getInstance().schemeForCurrentUITheme
                }
            }


        @OptIn(ExperimentalStdlibApi::class)
        private fun createLinksEditor(project: Project): EditorTextField {
            val features = buildSet<EditorCustomization> {
                add(SoftWrapsEditorCustomization.ENABLED)
                add(AdditionalPageAtBottomEditorCustomization.DISABLED)
                add(COLOR_SCHEME_FOR_CURRENT_UI_THEME_CUSTOMIZATION)
                SpellCheckingEditorCustomizationProvider.getInstance().disabledCustomization?.let { add(it) }
            }

            return EditorTextFieldProvider.getInstance().getEditorField(FileTypes.PLAIN_TEXT.language, project, features).apply {
                setFontInheritedFromLAF(false)  // Global editor color scheme is set by EditorTextField logic. We also need to use font from it and not from the current LaF.
            }
        }
    }

    init {
        editorField = createLinksEditor(project)
        add(editorField, BorderLayout.CENTER)
        border = BorderFactory.createEmptyBorder()
        project.messageBus.connect(this).subscribe(LafManagerListener.TOPIC, this)
    }
}
