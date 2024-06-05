package com.example.gabinsbar

import PanierManager.monPanier
import SessionManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.gabinsbar.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import java.net.URLEncoder
import android.os.Handler
import android.os.Looper

// Définition de la classe MainActivity
class MainActivity : AppCompatActivity() {

    // Variables nécessaires à la gestion de la barre d'action et de la navigation
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Variable pour gérer le délai entre les pressions du bouton de validation
    private var lastClickTime: Long = 0

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de la vue
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration de la barre d'action
        setSupportActionBar(binding.appBarMain.toolbar)

        // Initialisation du contrôleur de navigation
        navController = findNavController(R.id.nav_host_fragment_content_main)

        // Liaison des vues avec le contrôleur de navigation et la configuration de la barre d'action
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_softs, R.id.nav_sirops, R.id.nav_bieres, R.id.nav_vins, R.id.nav_classiques, R.id.nav_extravagants, R.id.nav_accueil
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Gestion des éléments du menu latéral
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_accueil -> {
                    binding.drawerLayout.closeDrawers()
                    navController.popBackStack(R.id.nav_accueil, false)
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    binding.drawerLayout.closeDrawers()
                }
            }
            true
        }
    }

    // Création du menu d'options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    // Gestion des actions lorsque les éléments du menu d'options sont sélectionnés
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.connexionTopMenu -> {
                navController.navigate(R.id.nav_connexion)
            }
            R.id.deconnexionTopMenu -> {
                if (SessionManager.isLoggedIn.equals(false)) {
                    Toast.makeText(this,"Vous n'étiez pas connecté",Toast.LENGTH_SHORT).show()
                } else {
                    SessionManager.isLoggedIn = false
                    SessionManager.pseudo = ""
                    SessionManager.droits = ""
                    SessionManager.nbrOuvertures = 0
                    Toast.makeText(this,"Vous êtes déconnecté",Toast.LENGTH_SHORT).show()
                }
            }
            R.id.panierTopMenu -> {
                // Gestion du panier
                val currentTime = System.currentTimeMillis()
                MaterialDialog(this).show {
                    title(text = "Panier")
                    icon(R.drawable.cart)
                    listItems(items = monPanier)
                    positiveButton(R.string.validerPanier) { dialog ->
                        if (currentTime - lastClickTime > 3000) {
                            // Vérification de la connexion pour passer la commande
                            if (SessionManager.isLoggedIn && (SessionManager.droits.equals("invite") || SessionManager.droits.equals(
                                    "boss"
                                ))
                            ) {
                                try {
                                    val commande =
                                        monPanier.joinToString(separator = "\n    - ")
                                    val texte =
                                        "${SessionManager.pseudo} aimerait bien:\n    - $commande"
                                    val texteEncode = URLEncoder.encode(texte, "UTF-8")
                                    val url = "https://api.telegram.org/<your API key>/sendMessage?chat_id=<your chat ID>&text=${texteEncode}"

                                    val params = JSONObject()

                                    val request = JsonObjectRequest(
                                        Request.Method.POST, url, params,
                                        { response ->
                                            val jsonResponse = response.toString()
                                        },
                                        { error ->
                                            val errorMessage = error.message
                                        }
                                    )

                                    Volley.newRequestQueue(context).add(request)

                                    val message = "La commande a bien été envoyée"
                                    Toast.makeText(
                                        this@MainActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    monPanier.clear()
                                } catch (e: Exception) {
                                    val message =
                                        "Erreur lors de l'envoi de la commande: " + e.message
                                    Toast.makeText(
                                        this@MainActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Vous devez vous connecter pour passer commande",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Veuillez patienter avant de valider à nouveau", Toast.LENGTH_SHORT).show()
                        }

                    }

                    negativeButton(R.string.viderPanier) { dialog ->
                        monPanier.clear()
                        Toast.makeText(context, "Le panier a été vidé", Toast.LENGTH_SHORT).show()
                    }
                }
                lastClickTime = currentTime
            }

        }
        return super.onOptionsItemSelected(item)
    }

    // Méthode appelée lorsque le bouton de navigation est cliqué
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
