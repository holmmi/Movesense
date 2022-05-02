package fi.metropolia.movesense.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import fi.metropolia.movesense.api.UserApi
import fi.metropolia.movesense.model.api.LoginRequest
import fi.metropolia.movesense.model.api.RegisterRequest
import fi.metropolia.movesense.model.api.RegisterResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.await
import retrofit2.awaitResponse
import kotlin.coroutines.EmptyCoroutineContext

class UserRepository(private val context: Context) {
    private val userService = UserApi.service

    val userToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            // No type safety.
            preferences[TOKEN_KEY]
        }

    private val firebaseAuth = Firebase.auth

    private fun saveUserToken(token: String?) {
        token?.let {
            CoroutineScope(EmptyCoroutineContext).launch {
                context.dataStore.edit { preferences ->
                    preferences[TOKEN_KEY] = it
                }
            }
        }
    }

    suspend fun login(loginRequest: LoginRequest) =
        withContext(Dispatchers.IO) {
            try {
                val token = userService.loginUser(loginRequest)?.await()?.token
                token?.let {
                    firebaseAuth.signInWithCustomToken(it)
                        .addOnCompleteListener { signInTask ->
                            if (signInTask.isSuccessful) {
                                signInTask.result.user?.getIdToken(false)
                                    ?.addOnCompleteListener { tokenTask ->
                                        if (tokenTask.isSuccessful) {
                                            saveUserToken(tokenTask.result.token)
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "There was an error logging in a user", exception)
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "There was an error logging in a user", e)
                null
            }
        }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
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

    suspend fun getDetails(token: String) = withContext(Dispatchers.IO) {
        try {
            val authorizationHeader = "Bearer $token"
            userService.getUserDetails(authorizationHeader).await()
        } catch (e: Exception) {
            Log.e(TAG, "getDetails error: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getOrganizations() =
        withContext(Dispatchers.IO) { userService.getOrganizations().await() }

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tokenStore")
        private val TOKEN_KEY = stringPreferencesKey("user_token")
        private val TAG = UserRepository::class.simpleName
    }
}