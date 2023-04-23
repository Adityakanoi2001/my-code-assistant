package com.github.aksh2000.mycodeassistant.control

import com.github.aksh2000.mycodeassistant.control.actions.Action
import com.github.aksh2000.mycodeassistant.control.actions.ChatBotActionService
import com.github.aksh2000.mycodeassistant.views.ContentPanelComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


/**
 * @author akshithvarma
 */

class ChatBotToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val chatBotActionService = ChatBotActionService(Action.EXPLAIN_CODE)
        val contentPanel = ContentPanelComponent(chatBotActionService)
        val createContent = contentFactory?.createContent(contentPanel, "Assistant", false)
        toolWindow.contentManager.addContent(createContent!!)
    }
}
