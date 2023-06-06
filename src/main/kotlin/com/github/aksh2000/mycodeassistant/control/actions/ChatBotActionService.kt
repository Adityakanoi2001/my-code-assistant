package com.github.aksh2000.mycodeassistant.control.actions

import ChatBot
import ChatCompletionRequest
import ChatGptHttp
import com.github.aksh2000.mycodeassistant.control.model.ContentPanelComponentModel
import com.github.aksh2000.mycodeassistant.settings.AppSettingsState
import com.github.aksh2000.mycodeassistant.views.ContentPanelComponent
import com.github.aksh2000.mycodeassistant.views.ContentPanelComponent.Companion.contentPanelComponentModel
import com.github.aksh2000.mycodeassistant.views.PromptFormatter
import com.intellij.openapi.application.ApplicationManager

/**
 * @author akshithvarma
 */
class ChatBotActionService(private var actionType: Action) {
    val action = actionType.prompt;

    fun setActionType(actionType: Action) {
        this.actionType = actionType
    }

    fun getLabel(): String {
        val capitalizedAction = action.capitalize()
        return "$capitalizedAction Code"
    }


    private fun getCodeSection(content: String): String {
        val pattern = "```(.+?)```".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = pattern.find(content)

        if (match != null) return match.groupValues[1].trim()
        return ""
    }


    private fun makeChatBotRequest(prompt: String,contentPanelComponentModel: ContentPanelComponentModel): String {
        val apiKey = AppSettingsState.instance.apiKey
        val model = AppSettingsState.instance.model.ifEmpty { "gpt-3.5-turbo" }

        if (apiKey.isEmpty()) {
            return "Please add an API Key in the ChatBot Settings Using Steps :- \n  IntelliJ Idea > Preferences > Tools > Code Assistant : Config > API Key"
        }

        try {
            val chatbot = ChatBot(ChatGptHttp(apiKey))
            val system = "Be as helpful as possible and concise with your response"
            val request = ChatCompletionRequest(model, system)
            request.addMessage(prompt)
            val generateResponse = chatbot.generateResponse(request)
            contentPanelComponentModel.previousResponse = generateResponse.choices[0].message.content
            return generateResponse.choices[0].message.content
        } catch (e: Exception) {
            return "Error while fetching the response: ${e.message}";
        }
    }

    fun handlePromptAndResponse(
        ui: ContentPanelComponent,
        prompt: PromptFormatter,
        replaceSelectedText: ((response: String) -> Unit)? = null
    ) {
        ui.add(prompt.getUIPrompt(), true)
        ui.add("Loading...")

        ApplicationManager.getApplication().executeOnPooledThread {
            val response = this.makeChatBotRequest(prompt.getRequestPrompt(),contentPanelComponentModel)
            ApplicationManager.getApplication().invokeLater {
                when {
                    actionType === Action.REFACTOR_CODE || actionType === Action.IMPROVISE  -> ui.updateReplaceableContent(
                        response
                    ) {
                        replaceSelectedText?.invoke(getCodeSection(response))
                    }
                    else -> ui.updateMessage(response)
                }
            }
        }
    }
}

enum class Action(val prompt: String) {
    REFACTOR_CODE("Refactor the java code to improve its structure and maintainability & Optimize where ever required, Include Java Doc in the updated code if applicable"),
    EXPLAIN_CODE("Give a brief description on the below method, Explain it to me as if I have never interacted with this code base"),
    GENERATE_UNIT_TEST_CASES("Generate JUnit Test cases (as Code) for the below code from Spring boot framework. Consider edge cases such as null inputs or empty collections, as well as boundary cases such as the upper and lower limits of any input parameters. Additionally, consider testing any dependencies that the class relies on, and whether the class properly handles any exceptions that may be thrown during its execution. Finally, consider testing the class\\'s interaction with any external resources such as databases, web services, or message queues. Ignore verifying log statements."),
    GENERATE_JAVA_DOC("Generate JavaDoc for below method. Don't include code, Just return Java Doc comment in response"),
    EDGE_CASE_ANALYSIS("List all possible edge cases which could break the method with line of code which has potential error, error level, probability of occurence, updated code where the scenario is fixed as a list"),
    IMPROVISE("Generate the Junit Test cases (as Code) for below code from Spring boot framework.Use the last test case generated improvise the previous test case with more assertions and more coverage over the code also consider if and else statements (if any) into consideration and generate test cases accordingly")
}
