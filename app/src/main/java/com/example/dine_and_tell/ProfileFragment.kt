package com.example.dine_and_tell

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.FileProvider
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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditMode: Boolean = false
    private var selectedImageUri: Uri? = null
    private var currentUser: User? = null

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Void?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.profileImage.setImageURI(uri)
                }
            }
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                val uri = getImageUriFromBitmap(bitmap)
                selectedImageUri = uri
                binding.profileImage.setImageBitmap(bitmap)
            }
        }

        userViewModel.status.observe(viewLifecycleOwner) { success ->
            setLoading(false)
            if (success) {
                isEditMode = false
                toggleEditMode(false)
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
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
            selectedImageUri = null
        }

        binding.saveButton.setOnClickListener {
            saveProfileChanges()
        }

        binding.editProfileImageIcon.setOnClickListener {
            showImageSourceDialog()
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
            setLoading(true)
            
            if (selectedImageUri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val imagesRef = storageRef.child("profile-images/${user.id}.jpg")
                
                val uploadTask = imagesRef.putFile(selectedImageUri!!)
                uploadTask.addOnSuccessListener { taskSnapshot ->
                    imagesRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        val updatedUser = user.copy(username = newUsername, profilePictureUrl = downloadUrl)
                        updateUserAndFinish(updatedUser)
                    }.addOnFailureListener {
                        setLoading(false)
                        Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            } else {
                val updatedUser = user.copy(username = newUsername)
                updateUserAndFinish(updatedUser)
            }
        }
    }

    private fun updateUserAndFinish(updatedUser: User) {
        userViewModel.updateUser(updatedUser)
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.saveButton.isEnabled = false
            binding.cancelButton.isEnabled = false
            binding.profileUsername.isEnabled = false
            binding.editProfileImageIcon.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.saveButton.isEnabled = true
            binding.cancelButton.isEnabled = true
            binding.profileUsername.isEnabled = isEditMode
            binding.editProfileImageIcon.isEnabled = true
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
            binding.profileUsername.setBackgroundResource(android.R.drawable.edit_text)
        } else {
            binding.profileUsername.setBackgroundResource(android.R.color.transparent)
        }

        binding.editProfileButton.visibility = if (inEditMode) View.GONE else View.VISIBLE
        binding.editButtonsLayout.visibility = if (inEditMode) View.VISIBLE else View.GONE
        binding.editProfileImageIcon.visibility = if (inEditMode) View.VISIBLE else View.GONE
        binding.signOutButton.visibility = if (inEditMode) View.GONE else View.VISIBLE
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Change Profile Picture")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePictureLauncher.launch(null)
                    1 -> openImagePicker()
                }
            }
            .show()
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val cachePath = File(requireContext().cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "image_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        return Uri.fromFile(file)
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
