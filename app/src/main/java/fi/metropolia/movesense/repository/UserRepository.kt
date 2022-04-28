package fi.metropolia.movesense.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import fi.metropolia.movesense.api.UserApi
import fi.metropolia.movesense.model.api.LoginRequest
import fi.metropolia.movesense.model.api.RegisterRequest
import fi.metropolia.movesense.model.api.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse
import java.lang.Exception

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
            try {
                saveUserToken(
                    userService.loginUser(loginRequest)?.await()?.token
                )
            } catch (e: Exception) {
                Log.e(TAG, "login error: ${e.localizedMessage}")
                null
            }
        }

    suspend fun register(registerRequest: RegisterRequest) =
        withContext(Dispatchers.IO) {
            lateinit var resp: Response<RegisterResponse>
            try {
                resp = userService.registerUser(registerRequest).awaitResponse()
                if (resp.isSuccessful) {
                    RegisterResponse()
                } else {
                    resp.body()
                }
            } catch (e: Exception) {
                Log.e(TAG, "register error: ${e.localizedMessage}")
                null
            }
        }

    suspend fun getDetails() = withContext(Dispatchers.IO) {
        try {
            val token: String? = getUserToken.first()
            userService.userDetails(token).await()
        } catch (e: Exception) {
            Log.e(TAG, "getDetails error: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getOrganizations() =
        withContext(Dispatchers.IO) { userService.getOrganizations().await() }

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenStore")
        val TOKEN_KEY = stringPreferencesKey("user_token")
        val TAG = UserRepository::class.simpleName
    }
}