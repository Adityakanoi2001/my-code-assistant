package com.github.aksh2000.mycodeassistant.control.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

/**
 * @author akshithvarma
 */

class RefactorAction : BaseAction() {

    override fun actionPerformed(event: AnActionEvent) {
        super.actionPerformed(event)
    }

    override fun getActionType(): Action {
        return Action.REFACTOR_CODE

    }

    override fun getReplaceableAction(event: AnActionEvent): (response: String) -> Unit {
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val document = editor.document

        val primaryCaret = editor.caretModel.primaryCaret;
        val start = primaryCaret.selectionStart;
        val end = primaryCaret.selectionEnd

        return { response ->
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(start, end, response)
            }
        }
    }
}
