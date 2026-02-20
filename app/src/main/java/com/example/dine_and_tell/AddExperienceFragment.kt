package com.example.dine_and_tell

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import com.google.firebase.storage.FirebaseStorage
import androidx.core.content.FileProvider
import java.util.UUID
import android.content.Intent
import android.net.Uri
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.dine_and_tell.databinding.FragmentAddExperienceBinding

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dine_and_tell.model.Experience
import com.example.dine_and_tell.viewmodel.ExperienceViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class AddExperienceFragment : Fragment() {
    private var _binding: FragmentAddExperienceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExperienceViewModel by viewModels {
        val database = com.example.dine_and_tell.database.AppDatabase.getDatabase(requireContext())
        val repository = com.example.dine_and_tell.repository.ExperienceRepository.getInstance(database.experienceDao())
        com.example.dine_and_tell.viewmodel.ExperienceViewModelFactory(repository)
    }
    private val args: AddExperienceFragmentArgs by navArgs()
    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Void?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExperienceBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        AlertDialog.Builder(requireContext())
            .setTitle("Add Photo")
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
        val file = File(cachePath, "experience_${System.currentTimeMillis()}.png")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ActivityResultLauncher for image picking (Gallery)
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri
                    binding.experienceImagePreview.setImageURI(uri)
                    binding.experienceImagePreview.visibility = View.VISIBLE
                    binding.uploadPlaceholder.visibility = View.GONE
                }
            }
        }

        // Initialize ActivityResultLauncher for taking picture (Camera)
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                val uri = getImageUriFromBitmap(bitmap)
                selectedImageUri = uri
                binding.experienceImagePreview.setImageBitmap(bitmap)
                binding.experienceImagePreview.visibility = View.VISIBLE
                binding.uploadPlaceholder.visibility = View.GONE
            }
        }

        binding.imageCard.setOnClickListener {
            showImageSourceDialog()
        }

        val experience = args.experience
        val restaurantName = args.restaurantName ?: experience?.restaurantName ?: "Restaurant"
        binding.restaurantName.text = restaurantName

        binding.deleteButton.paintFlags = binding.deleteButton.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        if (experience != null) {
            binding.title.text = "Edit my experience in"
            binding.reviewEditText.setText(experience.review)
            binding.ratingBar.rating = experience.rating
            binding.saveButton.text = "Save & Update"
            binding.deleteButton.visibility = View.VISIBLE
            
            if (!experience.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(experience.imageUrl).into(binding.experienceImagePreview)
                binding.experienceImagePreview.visibility = View.VISIBLE
                binding.uploadPlaceholder.visibility = View.GONE
            }
        } else {
            binding.title.text = "Tell us about your experience dining in"
            binding.saveButton.text = "Share with the world!"
            binding.deleteButton.visibility = View.GONE
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.deleteButton.setOnClickListener {
            experience?.firestoreId?.let { id ->
                setLoading(true)
                viewModel.deleteExperience(id)
                Toast.makeText(context, "Experience deleted", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.saveButton.setOnClickListener {
            val review = binding.reviewEditText.text.toString()
            val rating = binding.ratingBar.rating
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Toast.makeText(requireContext(), "You must be signed in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (review.isNotEmpty()) {
                setLoading(true)
                
                if (selectedImageUri != null) {
                    val storageRef = FirebaseStorage.getInstance().reference
                    val imagesRef = storageRef.child("experience-images/${UUID.randomUUID()}.jpg")
                    
                    val uploadTask = imagesRef.putFile(selectedImageUri!!)
                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        imagesRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            saveExperienceToFirestore(currentUser, review, rating, downloadUrl)
                        }.addOnFailureListener {
                            setLoading(false)
                            Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        setLoading(false)
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Use existing image URL if editing and no new image selected
                    val imageUrl = experience?.imageUrl
                    saveExperienceToFirestore(currentUser, review, rating, imageUrl)
                }
            } else {
                Toast.makeText(context, "Please write a review", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addExperienceStatus.observe(viewLifecycleOwner) { success ->
            setLoading(false)
            if (success) {
                if (experience == null) {
                    Toast.makeText(context, "Experience saved!", Toast.LENGTH_SHORT).show()
                    binding.reviewEditText.text.clear()
                    binding.ratingBar.rating = 5f
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(context, "Experience updated!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } else {
                Toast.makeText(context, "Failed to save experience", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExperienceToFirestore(currentUser: com.google.firebase.auth.FirebaseUser, review: String, rating: Float, imageUrl: String?) {
        val experience = args.experience
        
        if (experience != null) {
            val updatedExperience = experience.copy(
                review = review,
                rating = rating,
                imageUrl = imageUrl,
                userName = experience.userName.ifEmpty { currentUser.displayName ?: "User" },
                dateOfVisit = System.currentTimeMillis()
            )

            viewModel.updateExperience(updatedExperience)
            Toast.makeText(context, "Experience updated!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        } else {
            val restaurantId = args.restaurantId ?: ""
            val restaurantName = args.restaurantName ?: ""
            val userId = currentUser.uid
            val userName = currentUser.displayName ?: "User"

            val newExperience = Experience(
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                userId = userId,
                userName = userName,
                review = review,
                rating = rating,
                imageUrl = imageUrl,
                dateOfVisit = System.currentTimeMillis()
            )
            viewModel.addExperience(newExperience)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.saveButton.isEnabled = false
            binding.deleteButton.isEnabled = false
            binding.reviewEditText.isEnabled = false
            binding.ratingBar.isEnabled = false
            binding.imageCard.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.saveButton.isEnabled = true
            binding.deleteButton.isEnabled = true
            binding.reviewEditText.isEnabled = true
            binding.ratingBar.isEnabled = true
            binding.imageCard.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
