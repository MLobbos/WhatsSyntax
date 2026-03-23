package com.syntax_institut.whatssyntax.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.syntax_institut.whatssyntax.MainActivity
import com.syntax_institut.whatssyntax.databinding.FragmentOtpVerifyBinding
import com.syntax_institut.whatssyntax.viewmodel.AuthUiState
import com.syntax_institut.whatssyntax.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerifyFragment : Fragment() {

    private var _binding: FragmentOtpVerifyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerify.setOnClickListener {
            val code = binding.etOtp.text?.toString()?.trim() ?: ""
            viewModel.verifyOtp(code)
        }

        binding.tvResend.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Loading -> {
                            binding.progress.isVisible = true
                            binding.btnVerify.isEnabled = false
                            binding.tvError.isVisible = false
                        }
                        is AuthUiState.Verified -> {
                            // Navigate to main app, clear back stack
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        is AuthUiState.Error -> {
                            binding.progress.isVisible = false
                            binding.btnVerify.isEnabled = true
                            binding.tvError.text = state.message
                            binding.tvError.isVisible = true
                        }
                        else -> {
                            binding.progress.isVisible = false
                            binding.btnVerify.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
