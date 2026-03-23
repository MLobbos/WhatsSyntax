package com.syntax_institut.whatssyntax

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.syntax_institut.whatssyntax.data.TokenDataStore
import com.syntax_institut.whatssyntax.databinding.ActivityMainBinding
import com.syntax_institut.whatssyntax.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val isLoggedIn = tokenDataStore.isLoggedIn.first()
            if (!isLoggedIn) {
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                finish()
                return@launch
            }
        }

        val navHost =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        binding.bottomNavigationBar.setupWithNavController(navHost.navController)
    }
}
