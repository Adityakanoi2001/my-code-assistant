package com.github.aksh2000.mycodeassistant.views

/**
 * @author akshithvarma
 */


interface PromptFormatter {
    fun getUIPrompt(): String

    fun getRequestPrompt(): String
}

class ActionPromptFormatter(
    private val action: String,
    private val display_action : String,
    private val lang: String,
    private val selectedText: String
) :
    PromptFormatter {
    override fun getUIPrompt(): String {
        return """$display_action:
         <pre><code>$selectedText</pre></code>
        """.trimMargin()
    }

    override fun getRequestPrompt(): String {
        return """$action:
            $selectedText
        """.trimMargin()
    }
}
