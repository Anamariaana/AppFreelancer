package com.example.fianca.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private val dao: QuizDao) {
    suspend fun loadQuestionsWithOptions(): List<QuestionWithOptions> = withContext(Dispatchers.IO) {
        val questions = dao.getQuestionsOnce()
        questions.map { q ->
            val options = dao.getOptionsOnce(q.id)
            QuestionWithOptions(id = q.id, text = q.text, options = options)
        }
    }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = dao.countQuestions()
        if (count == 0) {
            val q1 = QuestionEntity(1, "O que sentes quando humilhas alguém?")
            val q2 = QuestionEntity(2, "Como deixar claro que és inteligete sem inferiorizar ninguém?")
            val q3 = QuestionEntity(3, "O que farias se encontrasses uma mulher na tua cama?")
            val q4 = QuestionEntity(4, "Como tratarias uma mulher que está obcecada por ti?")
            val q5 = QuestionEntity(5, "Quais são os princípios que mais importam?")
            dao.insertQuestions(q1, q2, q3, q4, q5)
            dao.insertOptions(
                AnswerOptionEntity(id = 101, questionId = 1, text = "Vontade de torna-los inteligentes", isCorrect = false),
                AnswerOptionEntity(id = 102, questionId = 1, text = "Dopamina", isCorrect = true),
                AnswerOptionEntity(id = 103, questionId = 1, text = "Nada", isCorrect = false),
                AnswerOptionEntity(id = 104, questionId = 1, text = "Nunca parei pra pensar nisso", isCorrect = false),
            )
            dao.insertOptions(
                AnswerOptionEntity(id = 201, questionId = 2, text = "Calando a boca", isCorrect = false),
                AnswerOptionEntity(id = 202, questionId = 2, text = "Falar o que penso em um tom amigavel", isCorrect = true),
                AnswerOptionEntity(id = 203, questionId = 2, text = "Faze-lo entender que está usando mal o cérebro, sem ser arrogante", isCorrect = false),
                AnswerOptionEntity(id = 204, questionId = 2, text = "Fingir que não sei e ficaria quieto", isCorrect = false),
            )
            dao.insertOptions(
                AnswerOptionEntity(id = 301, questionId = 3, text = "Comia ela, ninguém lhe chamou.", isCorrect = false),
                AnswerOptionEntity(id = 302, questionId = 3, text = "Lhe manda sair imediatamente.", isCorrect = false),
                AnswerOptionEntity(id = 303, questionId = 3, text = "Siria do quarto e espersva ela na sala pra conversar.", isCorrect = true),
                AnswerOptionEntity(id = 304, questionId = 3, text = "Acharia ridículo, e riria da cara dela.", isCorrect = false),
            )
            dao.insertOptions(
                AnswerOptionEntity(id = 401, questionId = 4, text = "Ignorava, não tenho paciência para isso", isCorrect = false),
                AnswerOptionEntity(id = 402, questionId = 4, text = "Aproveitava, eu gosto de adrenalina", isCorrect = false),
                AnswerOptionEntity(id = 403, questionId = 4, text = "Conversaria", isCorrect = true),
                AnswerOptionEntity(id = 404, questionId = 4, text = "Fugia, de louco basta eu na minha vida", isCorrect = false),
            )
            dao.insertOptions(
                AnswerOptionEntity(id = 501, questionId = 5, text = "Dinheiro e poder", isCorrect = false),
                AnswerOptionEntity(id = 502, questionId = 5, text = "Irmandade, honestidade e respeito", isCorrect = true),
                AnswerOptionEntity(id = 503, questionId = 5, text = "Religiosos", isCorrect = false),
                AnswerOptionEntity(id = 504, questionId = 5, text = "Ambição", isCorrect = false),
            )
        }
    }
}
