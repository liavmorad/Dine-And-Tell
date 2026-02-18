package com.example.dine_and_tell

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dine_and_tell.databinding.FragmentProfileBinding
import com.example.dine_and_tell.model.User
import com.example.dine_and_tell.viewmodel.UserViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditMode: Boolean = false
    private var selectedImageUri: Uri? = null
    private var currentUser: User? = null

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize with default view mode
        toggleEditMode(false)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user
                updateUI(user)
            } else {
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                if (firebaseUser != null) {
                    userViewModel.fetchUser(firebaseUser.uid)
                }
            }
        }

        // Initialize ActivityResultLauncher for image picking
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.profileImage.setImageURI(uri)
                }
            }
        }

        binding.editProfileButton.setOnClickListener {
            isEditMode = true
            toggleEditMode(true)
        }

        binding.cancelButton.setOnClickListener {
            isEditMode = false
            toggleEditMode(false)
            currentUser?.let { user ->
                updateUI(user)
            }
            selectedImageUri = null // Clear selected image
        }

        binding.saveButton.setOnClickListener {
            saveProfileChanges()
        }

        binding.editProfileImageIcon.setOnClickListener {
            openImagePicker()
        }

        binding.signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun updateUI(user: User) {
        binding.profileUsername.setText(user.username)
        if (!user.profilePictureUrl.isNullOrEmpty()) {
            Picasso.get().load(user.profilePictureUrl).placeholder(R.drawable.person).into(binding.profileImage)
        } else {
            binding.profileImage.setImageResource(R.drawable.person)
        }
    }

    private fun saveProfileChanges() {
        val newUsername = binding.profileUsername.text.toString()
        
        if (newUsername.isBlank()) {
            Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.let { user ->
            // TODO: Handle image upload to Firebase Storage if selectedImageUri is not null

            val updatedUser = user.copy(username = newUsername)
            userViewModel.updateUser(updatedUser)

            isEditMode = false
            toggleEditMode(false)
            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        userViewModel.clear()
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                findNavController().navigate(R.id.action_global_loginFragment)
            }
    }

    private fun toggleEditMode(inEditMode: Boolean) {
        binding.profileUsername.isEnabled = inEditMode

        if (inEditMode) {
            // Restore default EditText background when enabled
            binding.profileUsername.setBackgroundResource(android.R.drawable.edit_text)
        } else {
            // Remove background (underline) when disabled
            binding.profileUsername.setBackgroundResource(android.R.color.transparent)
        }

        binding.editProfileButton.visibility = if (inEditMode) View.GONE else View.VISIBLE
        binding.editButtonsLayout.visibility = if (inEditMode) View.VISIBLE else View.GONE
        binding.editProfileImageIcon.visibility = if (inEditMode) View.VISIBLE else View.GONE
        binding.signOutButton.visibility = if (inEditMode) View.GONE else View.VISIBLE
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
