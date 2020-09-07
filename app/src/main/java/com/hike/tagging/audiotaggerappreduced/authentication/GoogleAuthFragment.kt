package com.hike.tagging.audiotaggerappreduced.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hike.tagging.audiotaggerappreduced.R
import com.hike.tagging.audiotaggerappreduced.data.AuthTokenResponse
import com.hike.tagging.audiotaggerappreduced.data.QueryReponse
import com.hike.tagging.audiotaggerappreduced.data.User
import com.hike.tagging.audiotaggerappreduced.retrofit.RetrofitUtils
import com.hike.tagging.audiotaggerappreduced.utils.AuthenticationUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleAuthFragment : Fragment(), View.OnClickListener {

    lateinit var signin: SignInButton
    var mGoogleSignInClient: GoogleSignInClient? = null

    val RC_SIGN_IN = 0
    val clientId = "200366557867-5vpiu36hln5l0quqqhvu83mi3q6ldgs2.apps.googleusercontent.com"

    lateinit var navController: NavController

    companion object {
        private val TAG = "GoogleAuthFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        signin = view.findViewById(R.id.sign_in_button)
        signin.setOnClickListener(this)

        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build()
        mGoogleSignInClient = context?.let { GoogleSignIn.getClient(it, gso)}
        val account = GoogleSignIn.getLastSignedInAccount(context)
        updateUI(account)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sign_in_button -> {
                signIn()
            }
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w(GoogleAuthFragment.TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun getClientToken() {
        val taggerRESTAPIService = RetrofitUtils.getTaggerRestApiClient()
        val call = taggerRESTAPIService.getClientToken(AuthenticationUtils.getAuthToken())
        call.enqueue(object : Callback<AuthTokenResponse> {
            override fun onFailure(call: Call<AuthTokenResponse>, t: Throwable) {
                //Toast.makeText(activity?.applicationContext, "Failure in get client code call", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AuthTokenResponse>, response: Response<AuthTokenResponse>) {
                //Toast.makeText(activity?.applicationContext, "Success in get client code call", Toast.LENGTH_SHORT).show()
                AuthenticationUtils.clientToken = response.body()?.clientToken
                getUserDetails()
            }

        })
    }

    private fun getUserDetails() {
        val taggerRESTAPIService = RetrofitUtils.getTaggerRestApiClient()
        val call = taggerRESTAPIService.getUserDetails(AuthenticationUtils.clientToken!!)
        call.enqueue(object: Callback<QueryReponse> {
            override fun onFailure(call: Call<QueryReponse>, t: Throwable) {
                Toast.makeText(activity?.applicationContext, "Get User Details failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<QueryReponse>, response: Response<QueryReponse>) {
                if(response.body()?.isDetailsFilled == true) {
                    initUserDetails()
                    navController.navigate(R.id.action_googleAuthFragment_to_recorderFragment)
                }
            }

        })
    }

    private fun initUserDetails() {
        User.setUser(AuthenticationUtils.getEmail(), "", "", "")
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if(account == null) {
            Toast.makeText(activity?.applicationContext, "Sign in failed", Toast.LENGTH_SHORT).show()
        } else {
            AuthenticationUtils.googleAccount = account
            getClientToken()
        }
    }
}