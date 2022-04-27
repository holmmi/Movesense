package fi.metropolia.movesense.view.settings

import android.app.Application
import androidx.lifecycle.*
import fi.metropolia.movesense.model.api.*
import fi.metropolia.movesense.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository: UserRepository = UserRepository(application.applicationContext)

    val userToken: LiveData<String?> = userRepository.getUserToken.asLiveData()

    private var _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse>
        get() = _loginResponse

    private var _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse>
        get() = _registerResponse

    private var _organizationResponse = MutableLiveData<OrganizationResponse>()
    val organizationResponse: LiveData<OrganizationResponse>
        get() = _organizationResponse

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelScope.launch(Dispatchers.IO) {
            _registerResponse.postValue(
                userRepository.register(
                    RegisterRequest(
                        name,
                        username,
                        password,
                        passwordConfirmation,
                        organizationId
                    )
                )
            )
        }
    }

    fun getOrganizations() {
        viewModelScope.launch(Dispatchers.IO) {
            _organizationResponse.postValue(userRepository.getOrganizations())
        }
    }
}