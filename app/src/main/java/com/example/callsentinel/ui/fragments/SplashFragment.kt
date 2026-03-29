package com.example.callsentinel.ui.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.callsentinel.R
import com.example.callsentinel.databinding.FragmentSplashBinding
import com.example.callsentinel.ui.viewmodels.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimations()
        observeNavigation()
    }

    private fun startAnimations() {
        // Logo animation
        val logoAnimator = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 0f, 1f)
        logoAnimator.duration = 800
        logoAnimator.start()

        // Subtitle animation with delay
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(400)
            .start()

        // Progress indicator animation
        val progressAnimator = ValueAnimator.ofFloat(0f, 1f)
        progressAnimator.duration = 2000
        progressAnimator.addUpdateListener { animation ->
            binding.progressIndicator.progress = (animation.animatedValue as Float * 100).toInt()
        }
        progressAnimator.start()

        // Pulse animation for the shield icon
        val scaleX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 1f, 1.1f, 1f)
        scaleX.duration = 1000
        scaleY.duration = 1000
        scaleX.repeatCount = ObjectAnimator.INFINITE
        scaleY.repeatCount = ObjectAnimator.INFINITE
        scaleX.start()
        scaleY.start()
    }

    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    when (event) {
                        is SplashViewModel.NavigationEvent.NavigateToHome -> {
                            delay(500) // Small delay for smooth transition
                            findNavController().navigate(R.id.action_splash_to_home)
                        }
                        is SplashViewModel.NavigationEvent.NavigateToOnboarding -> {
                            delay(500)
                            findNavController().navigate(R.id.action_splash_to_onboarding)
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