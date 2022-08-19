/*
 * PlantesArroserAujourdhui.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */
package mobiles.projet.arrosage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mobiles.projet.arrosage.databinding.ActivityPlantesArroserAujourdhuiBinding
import java.util.Calendar

class PlantesArroserAujourdhui : AppCompatActivity() {
    // ActivityPlantesArroserAujourdhuiBinding : la classe générée par le view binding qui contient toutes les références vers tous les views de la layout
    private lateinit var binding: ActivityPlantesArroserAujourdhuiBinding
    lateinit var adapter: ListePlantesArroserAujourdhuiRecycleAdapter

    // L’activité démarre par l’appel à la fonction onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar = supportActionBar
        actionBar!!.title= "A Arroser "

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        // On utilise la classe ActivityPlantesArroserAujourdhuiBinding pour créer
        // un objet binding de cette classe à l'aide de la méthode inflate().
        binding = ActivityPlantesArroserAujourdhuiBinding.inflate(layoutInflater)
        setContentView(binding.root) // binding.root est la référence vers le layout à la racine.

        // récupérer la référence vers le RecyclerView qui se trouve dans le fichier layout
        val recyclerView = binding.recycler
        recyclerView.hasFixedSize() // Pour améliorer les pérformances
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Récupéerer l'instance vers la base de données
        val bd = PlantesBD.getDatabase(application)

        var listeDesPlantes : Array<Plante> = arrayOf() // Le Array avec tous les plantes de la BDD
        val t= Thread{ listeDesPlantes = bd.myDao().loadAll() }
        t.start()  // loadall  fait : "SELECT * FROM TablePlante" et renvoie un Array<TablePlante>
        t.join()

        /* Nous avons récupéré dans la base de données la liste complète des plantes
         * et maintenant nous voulons afficher uniquement les plantes qui doivent être arrosées aujourd'hui
         */

        var listePourAffichage : MutableList<Plante> = mutableListOf()
        for(plante in listeDesPlantes) { // Nous parcourons la liste complète des plantes
            val ajd : Calendar = Calendar.getInstance() // Obtenir la date d'aujourd'hui
            val jour = ajd.get(Calendar.DAY_OF_MONTH) == plante.dateProchainArosage.get(Calendar.DAY_OF_MONTH)
            val mois = ajd.get(Calendar.MONTH) == plante.dateProchainArosage.get(Calendar.MONTH)
            val annee = ajd.get(Calendar.YEAR) == plante.dateProchainArosage.get(Calendar.YEAR)

            // Si le numéro du jour dans le mois, le mois et l'année de la prochaine date d'arrosage de la plante correspond à la date d'aujourd'hui
            if (jour && mois && annee)
                listePourAffichage.add(plante)
        }

        // Les plantes de la liste seront affichées une fois sous un fond vert foncé (couleurPaire)
        // et une fois sous un fond vert clair (couleurImpaire)
        val couleurPaire: Int = ResourcesCompat.getColor(resources, R.color.couleurPaire, null)
        val couleurImpaire: Int = ResourcesCompat.getColor(resources, R.color.couleurImpaire, null)

        // Construire adapter
        adapter = ListePlantesArroserAujourdhuiRecycleAdapter( this , listePourAffichage, couleurPaire, couleurImpaire)
        recyclerView.adapter = adapter // Associer adapter à RecyclerView

    }

    fun reportArrosagesPourDemain(button: View) {
        // Récupéerer l'instance vers la base de données
        val bd = PlantesBD.getDatabase(application)

        // créer une liste de Strings à partir de fichier ressources (resources : une propriété d'Activity)
        val nomsDesMois: Array<String> =  resources.getStringArray(R.array.mois)

        // Appeler la fonction de Adapter qui update tous les items dans l'état "checked"
        adapter.updateChecked(bd, nomsDesMois)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun arrosagesEffectues(button: View) {
        // Récupéerer l'instance vers la base de données
        val bd = PlantesBD.getDatabase(application)

        // créer une liste de Strings à partir de fichier ressources (resources : une propriété d'Activity)
        val nomsDesMois: Array<String> =  resources.getStringArray(R.array.mois)

        adapter.arrosagesEffectues(bd, nomsDesMois)
    }

    // Activité en arrière plan, invisible
    override fun onStop() {
        super.onStop()

        // Récupéerer l'instance vers la base de données
        val bd = PlantesBD.getDatabase(application)

        // créer une liste de Strings à partir de fichier ressources (resources : une propriété d'Activity)
        val nomsDesMois: Array<String> =  resources.getStringArray(R.array.mois)

        adapter.arrosagesEffectues(bd, nomsDesMois)
    }

}