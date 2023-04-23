package com.github.aksh2000.mycodeassistant.control.actions

import com.intellij.openapi.actionSystem.AnActionEvent

/**
 * @author akshithvarma
 */
class UnitTestCaseAction : BaseAction() {
    override fun actionPerformed(event: AnActionEvent) {
        super.actionPerformed(event)
    }

    override fun getActionType(): Action {
        return Action.GENERATE_UNIT_TEST_CASES
    }
}
