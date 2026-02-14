package com.example.dine_and_tell

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dine_and_tell.databinding.FragmentProfileBinding
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.squareup.picasso.Picasso
import android.util.Log

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditMode: Boolean = false
    private var selectedImageUri: Uri? = null

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
            // Revert changes (e.g., reset username text, profile image)
            binding.profileUsername.setText(R.string.profile_default_username) // Or actual stored username
            // Picasso.get().load(initialProfileImageUrl).into(binding.profileImage) // Revert image
            selectedImageUri = null // Clear selected image
        }

        binding.saveButton.setOnClickListener {
            isEditMode = false
            toggleEditMode(false)
            val newUsername = binding.profileUsername.text.toString()
            Log.d("ProfileFragment", "Saved username: $newUsername")
            selectedImageUri?.let { uri ->
                Log.d("ProfileFragment", "Saved image URI: $uri")
            }
            // Here you would typically save to a database or shared preferences
        }

        binding.editProfileImageIcon.setOnClickListener {
            openImagePicker()
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