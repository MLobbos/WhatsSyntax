package com.syntax_institut.whatssyntax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syntax_institut.whatssyntax.repository.AuthRepository
import com.syntax_institut.whatssyntax.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object OtpSent : AuthUiState()
    object Verified : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var pendingPhone: String = ""

    fun requestOtp(phone: String) {
        if (phone.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter a phone number")
            return
        }
        pendingPhone = phone
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.requestOtp(phone)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.OtpSent
                is AuthResult.Error   -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun verifyOtp(code: String) {
        if (code.length != 6) {
            _uiState.value = AuthUiState.Error("Please enter the 6-digit code")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authRepository.verifyOtp(pendingPhone, code)) {
                is AuthResult.Success -> _uiState.value = AuthUiState.Verified
                is AuthResult.Error   -> _uiState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
