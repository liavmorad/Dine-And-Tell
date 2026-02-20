package com.example.dine_and_tell

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.dine_and_tell.databinding.FragmentLoginBinding
import com.example.dine_and_tell.viewmodel.UserViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding accessed before onCreateView or after onDestroyView")

    private val userViewModel: UserViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        onSignInResult(result)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Toast.makeText(requireContext(), "Welcome, ${user.username}!", Toast.LENGTH_SHORT).show()
                navigateToExploration()
            }
        }

        binding.signInGoogleButton.setOnClickListener {
            launchSignIn()
        }
    }

    private fun launchSignIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser?.let {
                userViewModel.handleLogin(
                    it.uid,
                    it.displayName ?: "User",
                    it.email ?: "",
                    it.photoUrl?.toString()
                )
            }
        } else {
            if (response == null) {
                Toast.makeText(requireContext(), "Please sign in to continue", Toast.LENGTH_SHORT).show()
            } else {
                val errorMessage = response.error?.message ?: "Sign in failed"
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToExploration() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToExplorationFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
