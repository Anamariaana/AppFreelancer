package com.example.fianca.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class QuestionWithOptions(
    val id: Int,
    val text: String,
    val options: List<AnswerOptionEntity>
)

@Dao
interface QuizDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM answer_options WHERE questionId = :questionId ORDER BY id ASC")
    fun getOptionsForQuestion(questionId: Int): Flow<List<AnswerOptionEntity>>

    @Query("SELECT * FROM questions ORDER BY id ASC")
    suspend fun getQuestionsOnce(): List<QuestionEntity>

    @Query("SELECT * FROM answer_options WHERE questionId = :questionId ORDER BY id ASC")
    suspend fun getOptionsOnce(questionId: Int): List<AnswerOptionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun countQuestions(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(vararg questions: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(vararg options: AnswerOptionEntity)

    @Query("DELETE FROM questions")
    suspend fun clearQuestions()

    @Query("DELETE FROM answer_options")
    suspend fun clearOptions()
}
