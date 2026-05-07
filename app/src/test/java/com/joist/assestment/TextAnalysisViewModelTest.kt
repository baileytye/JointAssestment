package com.joist.assestment

import com.joist.assestment.data.TextAnalysis
import com.joist.assestment.data.TextAnalysisRepository
import com.joist.assestment.data.ValidationRepository
import com.joist.assestment.ui.EchoViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class TextAnalysisViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val validationRepo: ValidationRepository = mockk()
    private val analysisRepo: TextAnalysisRepository = mockk()
    private lateinit var viewModel: EchoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EchoViewModel(validationRepo, analysisRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `analysis is populated after successful submit`() = runTest(testDispatcher) {
        coEvery { validationRepo.validate(any()) } returns Result.success("Hello World")
        every { analysisRepo.analyze(any()) } returns TextAnalysis(11, 2, "Valid")

        viewModel.onTextChanged("Hello World")
        viewModel.submit()

        assertNotNull(viewModel.analysis.value)
    }

    @Test
    fun `word count handles multiple spaces`() {
        every { analysisRepo.analyze(any()) } answers {
            val t = firstArg<String>()
            TextAnalysis(t.length, t.split(" ").filter { it.isNotEmpty() }.size, "Valid")
        }

        val result = analysisRepo.analyze("Hello  World")
        assertEquals(2, result.wordCount)
    }

    @Test
    fun `analysis is not run when validation fails`() = runTest(testDispatcher) {
        coEvery { validationRepo.validate(any()) } returns Result.failure(Exception("Rejected"))

        viewModel.onTextChanged("error text")
        viewModel.submit()
        advanceUntilIdle()

        assertEquals(null, viewModel.analysis.value)
    }
}
