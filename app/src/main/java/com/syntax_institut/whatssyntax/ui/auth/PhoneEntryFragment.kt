package com.syntax_institut.whatssyntax.ui.auth

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
import androidx.navigation.fragment.findNavController
import com.syntax_institut.whatssyntax.R
import com.syntax_institut.whatssyntax.databinding.FragmentPhoneEntryBinding
import com.syntax_institut.whatssyntax.viewmodel.AuthUiState
import com.syntax_institut.whatssyntax.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PhoneEntryFragment : Fragment() {

    private var _binding: FragmentPhoneEntryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val phone = binding.etPhone.text?.toString()?.trim() ?: ""
            viewModel.requestOtp(phone)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is AuthUiState.Loading -> {
                            binding.progress.isVisible = true
                            binding.btnNext.isEnabled = false
                            binding.tvError.isVisible = false
                        }
                        is AuthUiState.OtpSent -> {
                            binding.progress.isVisible = false
                            binding.btnNext.isEnabled = true
                            findNavController().navigate(R.id.action_phoneEntry_to_otpVerify)
                        }
                        is AuthUiState.Error -> {
                            binding.progress.isVisible = false
                            binding.btnNext.isEnabled = true
                            binding.tvError.text = state.message
                            binding.tvError.isVisible = true
                        }
                        else -> {
                            binding.progress.isVisible = false
                            binding.btnNext.isEnabled = true
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
