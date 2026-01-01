package com.example.fianca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val text: String
)

@Entity(tableName = "answer_options")
data class AnswerOptionEntity(
    @PrimaryKey val id: Int,
    val questionId: Int,
    val text: String,
    val isCorrect: Boolean
)

