package com.example.fianca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fianca.data.AnswerOptionEntity
import com.example.fianca.data.FiancaDatabase
import com.example.fianca.data.QuizRepository
import com.example.fianca.data.QuestionWithOptions
import com.example.fianca.ui.theme.white100
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RomanticQuizTheme {
                QuizApp()
            }
        }
    }
}

@Composable
fun RomanticQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFFFF6B6B),
            secondary = Color(0xFFFFB3B3),
            background = Color(0xFF4A0E0E),
            surface = Color(0xFF6B1515),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

class QuizViewModel(private val repository: QuizRepository) : ViewModel() {
    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: State<Int> = _currentQuestionIndex

    private val _attempts = mutableStateOf(0)
    val attempts: State<Int> = _attempts

    private val _showError = mutableStateOf(false)
    val showError: State<Boolean> = _showError

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage
    private val _questions = mutableStateOf<List<QuestionWithOptions>>(emptyList())
    val questions: State<List<QuestionWithOptions>> = _questions
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            try {
                repository.ensureSeeded()
                _questions.value = repository.loadQuestionsWithOptions()
            } catch (t: Throwable) {
                _questions.value = defaultQuestions()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkAnswer(selectedOptionId: Int, onComplete: () -> Unit, onFail: () -> Unit) {
        val currentQuestion = _questions.value[_currentQuestionIndex.value]
        val selected = currentQuestion.options.firstOrNull { it.id == selectedOptionId }

        if (selected?.isCorrect == true) {
            _attempts.value = 0
            if (_currentQuestionIndex.value < _questions.value.size - 1) {
                _currentQuestionIndex.value++
            } else {
                onComplete()
            }
        } else {
            _attempts.value++
            if (_attempts.value >= 2) {
                _showError.value = true
                _errorMessage.value = "Pra um ser inteligente...enfim!... Vamos recomeçar!"
                _attempts.value = 0
                _currentQuestionIndex.value = 0
                onFail()
            } else {
                _showError.value = true
                _errorMessage.value = "Pensa bem..."
            }
        }
    }

    fun dismissError() {
        _showError.value = false
    }

    fun reset() {
        _currentQuestionIndex.value = 0
        _attempts.value = 0
        _showError.value = false
        _errorMessage.value = ""
    }
}

private fun defaultQuestions(): List<QuestionWithOptions> {
    return listOf(
        QuestionWithOptions(
            id = 1,
            text = "O que eu menos gosto em ti?",
            options = listOf(
                AnswerOptionEntity(id = 101, questionId = 1, text = "Tua timidez", isCorrect = false),
                AnswerOptionEntity(id = 102, questionId = 1, text = "Super ego", isCorrect = true),
                AnswerOptionEntity(id = 103, questionId = 1, text = "Teu silêncio", isCorrect = false),
                AnswerOptionEntity(id = 104, questionId = 1, text = "Tua distração", isCorrect = false),
            )
        ),
        QuestionWithOptions(
            id = 2,
            text = "O que eu mais amo em ti?",
            options = listOf(
                AnswerOptionEntity(id = 201, questionId = 2, text = "Teu sorriso", isCorrect = false),
                AnswerOptionEntity(id = 202, questionId = 2, text = "Teus olhos", isCorrect = true),
                AnswerOptionEntity(id = 203, questionId = 2, text = "Teu abraço", isCorrect = false),
                AnswerOptionEntity(id = 204, questionId = 2, text = "Tua voz", isCorrect = false),
            )
        ),
        QuestionWithOptions(
            id = 3,
            text = "O que farias se encontrasses uma mulher na tua cama?",
            options = listOf(
                AnswerOptionEntity(id = 301, questionId = 3, text = "Ignorava", isCorrect = false),
                AnswerOptionEntity(id = 302, questionId = 3, text = "Chamavas a tua namorada", isCorrect = true),
                AnswerOptionEntity(id = 303, questionId = 3, text = "Ficava confuso", isCorrect = false),
                AnswerOptionEntity(id = 304, questionId = 3, text = "Saia de casa", isCorrect = false),
            )
        ),
        QuestionWithOptions(
            id = 4,
            text = "Como tratarias uma mulher que está obcecada por ti?",
            options = listOf(
                AnswerOptionEntity(id = 401, questionId = 4, text = "Ignorava", isCorrect = false),
                AnswerOptionEntity(id = 402, questionId = 4, text = "Aproveitava", isCorrect = false),
                AnswerOptionEntity(id = 403, questionId = 4, text = "Conversaria", isCorrect = true),
                AnswerOptionEntity(id = 404, questionId = 4, text = "Fugia", isCorrect = false),
            )
        ),
        QuestionWithOptions(
            id = 5,
            text = "Quais são os princípios que mais importam?",
            options = listOf(
                AnswerOptionEntity(id = 501, questionId = 5, text = "Dinheiro e poder", isCorrect = false),
                AnswerOptionEntity(id = 502, questionId = 5, text = "Lealdade, honestidade e respeito", isCorrect = true),
                AnswerOptionEntity(id = 503, questionId = 5, text = "Liberdade total", isCorrect = false),
                AnswerOptionEntity(id = 504, questionId = 5, text = "Sucesso profissional", isCorrect = false),
            )
        ),
    )
}

class QuizViewModelFactory(private val repository: QuizRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun QuizApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val repository = remember(context) {
        val db = FiancaDatabase.getInstance(context)
        QuizRepository(db.quizDao())
    }
    val viewModel: QuizViewModel = viewModel(factory = QuizViewModelFactory(repository))

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                viewModel.reset()
                navController.navigate("quiz")
            })
        }
        composable("quiz") {
            QuizScreen(
                viewModel = viewModel,
                onComplete = { navController.navigate("prize") }
            )
        }
        composable("prize") {
            PrizeScreen(onRestart = {
                viewModel.reset()
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun CrashFallback(error: Throwable) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2D0A0A), Color(0xFF4A0E0E))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6B1515)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Esque isso",
                    color = Color(0xFFFF6B6B),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " Tenta novamente.",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D0A0A),
                        Color(0xFF4A0E0E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6B1515).copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "FIANÇA",
                    fontSize = 48.sp,
                    color=Color(color = 0xFFFFFFFFF),
                            fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Bem-vindo, ser humano \n Vamos lá ver se aprendeste a lição de casa!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB3B3),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "A senha é o adjectivo que mais te chamei durante a nossa conversa",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        showError = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6B6B),
                        unfocusedBorderColor = Color(0xFFFFB3B3),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                if (showError) {
                    Text(
                        text = "Pra alguém que não esquece...!",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        if (password.equals("bonito", ignoreCase = true)) {
                            onLoginSuccess()
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuizScreen(viewModel: QuizViewModel, onComplete: () -> Unit) {
    val currentIndex by viewModel.currentQuestionIndex
    val showError by viewModel.showError
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val questions = viewModel.questions.value
    val currentQuestion: QuestionWithOptions? = questions.getOrNull(currentIndex)

    if (showError) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = {
                Text(
                    text = if (viewModel.attempts.value == 0) " Idiota" else " Tenta de novo",
                    color = Color(0xFFFF6B6B)
                )
            },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("OK", color = Color(0xFFFF6B6B))
                }
            },
            containerColor = Color(0xFF6B1515)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D0A0A),
                        Color(0xFF4A0E0E)
                    )
                )
            )
    ) {
        if (isLoading || currentQuestion == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6B6B))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Seja um cavalheiro...", color = Color(0xFFFFB3B3))
            }
            return@Box
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Questão ${currentIndex + 1} de ${questions.size}",
                    fontSize = 16.sp,
                    color = Color(0xFFFFB3B3),
                    fontWeight = FontWeight.Medium
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF6B1515).copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = currentQuestion.text,
                        modifier = Modifier.padding(24.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(currentQuestion.options) { option: AnswerOptionEntity ->
                Button(
                    onClick = {
                        viewModel.checkAnswer(option.id, onComplete) {}
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B2020)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = option.text,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PrizeScreen(onRestart: () -> Unit) {
    val context = LocalContext.current
    val resId = remember {
        context.resources.getIdentifier("boyfriend_bg", "drawable", context.packageName)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF2D0A0A).copy(alpha = 0.55f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2D0A0A),
                                Color(0xFF4A0E0E),
                                Color(0xFF6B1515)
                            )
                        )
                    )
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF6B1515).copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "🎉",
                    fontSize = 64.sp
                )

                Text(
                    text = "AUAU!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Pelo menos não és Burro",
                    fontSize = 18.sp,
                    color = Color(0xFFFFB3B3),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF6B6B).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = " O Teu Prémio \n\nUma Noite Comigo\nAceitas? Responde por chamada.",
                        modifier = Modifier.padding(32.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Recomeçar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        }
    }
}
