package fi.metropolia.movesense.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fi.metropolia.movesense.api.UserApi
import fi.metropolia.movesense.model.api.LoginRequest
import fi.metropolia.movesense.model.api.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.await
import retrofit2.awaitResponse

class UserRepository(val context: Context) {
    private val userService = UserApi.service
    val getUserToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[TOKEN_KEY] ?: ""
        }

    suspend fun saveUserToken(token: String?) = token?.let {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun login(loginRequest: LoginRequest) =
        withContext(Dispatchers.IO) {
            saveUserToken(
                userService.loginUser(loginRequest)?.await()?.token
            )
        }

    suspend fun register(registerRequest: RegisterRequest) =
        withContext(Dispatchers.IO) { userService.registerUser(registerRequest).await() }

    suspend fun getOrganizations() = withContext(Dispatchers.IO) { userService.getOrganizations().await() }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenStore")
        val TOKEN_KEY = stringPreferencesKey("user_token")
    }
}