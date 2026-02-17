package com.example.dine_and_tell.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dine_and_tell.model.User
import com.example.dine_and_tell.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val repository = UserRepository.getInstance()

    val currentUser: LiveData<User?> = repository.currentUser

    private val _status = MutableLiveData<Boolean>()
    val status: LiveData<Boolean> = _status

    fun fetchUser(userId: String) {
        viewModelScope.launch {
            repository.fetchUser(userId)
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.saveUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun handleLogin(uid: String, name: String, email: String, photoUrl: String?) {
        viewModelScope.launch {
            val existingUser = repository.fetchUser(uid)

            if (existingUser == null) {
                val newUser = User(
                    id = uid,
                    username = name,
                    email = email,
                    profilePictureUrl = photoUrl
                )

                repository.saveUser(newUser)
            }
        }
    }

    fun clear() {
        repository.clear()
    }
}
