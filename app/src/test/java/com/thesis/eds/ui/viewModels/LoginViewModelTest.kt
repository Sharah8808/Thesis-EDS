package com.thesis.eds.ui.viewModels

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    private val auth: FirebaseAuth = mock(FirebaseAuth::class.java)
    private val authResult: AuthResult = mock(AuthResult::class.java)
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = LoginViewModel(auth)
    }

    @Test
    fun testLoginSuccess() {
        val email = "lala@gmail.com"
        val password = "loli80"
        val mockTask: Task<AuthResult> = mock(Task::class.java) as Task<AuthResult>
        `when`(auth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(true)

        val loginResult = viewModel.login(email, password)

        assertEquals(true, loginResult)
    }

    @Test
    fun testLoginFailure() {
        val email = "lala@gmail.com"
        val password = "loli80"
        val mockTask: Task<AuthResult> = mock(Task::class.java) as Task<AuthResult>
        `when`(auth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.isSuccessful).thenReturn(false)

        val loginResult = viewModel.login(email, password)

        assertEquals(false, loginResult)
    }
}




