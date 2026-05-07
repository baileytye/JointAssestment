package com.joist.assestment.data

data class TextAnalysis(val charCount: Int, val wordCount: Int, val label: String)

interface TextAnalysisRepository {
    fun analyze(text: String): TextAnalysis
}

class TextAnalysisRepositoryImpl : TextAnalysisRepository {
    override fun analyze(text: String): TextAnalysis {
        val wordCount = text.split(" ").filter { it.isNotEmpty() }.size
        val label = when {
            text.length < 5 -> "Too short"
            text.length > 100 -> "Too long"
            else -> "Valid"
        }
        return TextAnalysis(charCount = text.length, wordCount = wordCount, label = label)
    }
}
