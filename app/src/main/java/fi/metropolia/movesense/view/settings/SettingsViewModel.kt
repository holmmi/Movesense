package fi.metropolia.movesense.view.settings

import android.app.Application
import androidx.lifecycle.*
import fi.metropolia.movesense.model.api.*
import fi.metropolia.movesense.repository.UserRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application.applicationContext)

    val userToken = userRepository.userToken.asLiveData()

    private var _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse>
        get() = _loginResponse

    private var _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse>
        get() = _registerResponse

    private var _organizationResponse = MutableLiveData<List<OrganizationResponse>>()
    val organizationResponse: LiveData<List<OrganizationResponse>>
        get() = _organizationResponse

    private var _detailsResponse = MutableLiveData<DetailResponse?>()
    val detailsResponse: LiveData<DetailResponse?>
        get() = _detailsResponse

    fun login(username: String, password: String) {
        viewModelScope.launch {
            userRepository.login(
                LoginRequest(
                    username,
                    password
                )
            )
        }
    }

    fun register(
        name: String,
        username: String,
        password: String,
        passwordConfirmation: String,
        organizationId: Int
    ) {
        viewModelScope.launch {
            _registerResponse.value =
                userRepository.register(
                    RegisterRequest(
                        name,
                        username,
                        password,
                        passwordConfirmation,
                        organizationId
                    )
                )
        }
    }

    fun getUserDetails(token: String) {
        if (token.isNotEmpty()) {
            viewModelScope.launch {
                _detailsResponse.value = userRepository.getDetails(token)
            }
        }
    }

    fun getOrganizations() {
        viewModelScope.launch {
            _organizationResponse.value = userRepository.getOrganizations()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}