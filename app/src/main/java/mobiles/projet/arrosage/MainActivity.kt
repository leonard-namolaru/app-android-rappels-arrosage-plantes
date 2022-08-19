/*
 * MainActivity.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */

package mobiles.projet.arrosage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mobiles.projet.arrosage.databinding.ActivityMainBinding
import java.util.Calendar
import kotlin.system.exitProcess

/**
 * L'activité principale de l'application. C'est la première activité qui est affichée à l'utilisateur
 * lorsqu'il se connecte à l'application et elle affiche la liste des plantes.
 * L'utilisateur peut ajouter de nouvelles plantes à la base de données et dans ce cas,
 * la liste sera mise à jour automatiquement (LiveData)
 */
class MainActivity : AppCompatActivity() {
    // ActivityMainBinding : la classe générée par le view binding qui contient toutes les références vers tous les views de la layout
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ListePlantesRecycleAdapter
    val model by lazy { ViewModelProvider(this).get(PlanteViewModel::class.java) }


    // L’activité démarre par l’appel à la fonction onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On utilise la classe ActivityMainBinding pour créer
        // un objet binding de cette classe à l'aide de la méthode inflate().
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // binding.root est la référence vers le layout à la racine.

        binding.recherche.setVisibility(View.GONE)

        // récupérer la référence vers le RecyclerView qui se trouve dans activity_main.xml
        val recyclerView = binding.recycler
        recyclerView.hasFixedSize() // Pour améliorer les pérformances
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Les plantes de la liste seront affichées une fois sous un fond vert foncé (couleurPaire)
        // et une fois sous un fond vert clair (couleurImpaire)
        val couleurPaire: Int = ResourcesCompat.getColor(resources, R.color.couleurPaire, null)
        val couleurImpaire: Int = ResourcesCompat.getColor(resources, R.color.couleurImpaire, null)

        // Construire adapter
         adapter = ListePlantesRecycleAdapter( this@MainActivity, couleurPaire, couleurImpaire)
        recyclerView.adapter = adapter // Associer adapter à RecyclerView


        /* **** AFFICHAGE DE LA LISTE DES PLANTES **** */

        val bd = PlantesBD.getDatabase(application) // Récupéerer l'instance vers la base de données
        var listeDesPlantes : LiveData<List<Plante>> = bd.myDao().loadAllLive()  // La liste des plantes à afficher dans RecyclerView

        /* Quand listeDesPlantes change : mettre à jour la liste de plantes dans adapter */
        listeDesPlantes.observe(this) { listePlantes: List<Plante> -> adapter.setPlantes(listePlantes) }


        /* **** UNE ALARME QUI SE DÉCLENCHE PÉRIODIQUEMENT CHAQUE 24H **** */

        // AlarmManager un service d'Android qui gère les alarmes.
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager // Récupérer la référence vers AlarmManager

        // "Lancer l'alarme" --> AlarmManager lance un Intent

        // intent à mettre dans pendingIntent. AlarmManager va ensuite l'envoyer
        val intent = Intent(this, MyService::class.java)
        // création de pendingIntent avec le Intent dedans
        val pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        // L'alarme se déclenchera tous les jours à 7h30 AM
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.AM_PM, Calendar.AM)
            set(Calendar.MINUTE, 30)
        }

        // Programmer une alarme répétitive
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    fun ajouterPlante(view: View) {
        val intent = Intent(this, AjouterNouvellePlante::class.java)
        startActivity(intent)
    }

    fun plantesArroserAujourdhui(item: MenuItem) {
        val intent = Intent(this, PlantesArroserAujourdhui::class.java)
        startActivity(intent)
    }

    fun exitApp(item: MenuItem) {
        exitProcess(0)
    }

    fun rechercheDePlantes(item: MenuItem) {

        if(binding.recherche.visibility == View.GONE) {
            binding.recherche.setVisibility(View.VISIBLE)
            binding.titre.setVisibility(View.GONE)

            Toast.makeText(this, "Astuce : Appuyez à nouveau sur le bouton de recherche en bas de l'écran pour masquer à nouveau la barre de recherche", Toast.LENGTH_LONG).show()

            binding.recherche.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    model.partialNomPays(s.toString())
                    model.plantes.observe(this@MainActivity){ adapter.setPlantes(it) }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

        } else {
            binding.recherche.setVisibility(View.GONE)
            binding.titre.setVisibility(View.VISIBLE)
        }

    }

}