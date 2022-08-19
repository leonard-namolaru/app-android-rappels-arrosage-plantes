package mobiles.projet.arrosage

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mobiles.projet.arrosage.databinding.ActivityAffichePlanteBinding
import java.io.File
import java.util.*
import androidx.activity.*
import kotlin.concurrent.thread

class AffichePlante : AppCompatActivity() {
    private lateinit var binding: ActivityAffichePlanteBinding
    val model by lazy { ViewModelProvider(this).get(PlanteViewModel::class.java) }
    lateinit var plante :Plante
    var listeDesFrequences : Array<Frequence> = arrayOf() // Le Array de frequences à afficher dans RecyclerView

    fun TimestampToCalendar(timeStamp: Long?): Calendar? {
        var calendar : Calendar = Calendar.getInstance();
        if(timeStamp != null)
            calendar.setTimeInMillis(timeStamp)
        return calendar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affiche_plante)
        val actionBar = supportActionBar
        actionBar!!.title= "Affichage Plante"

        val bd = PlantesBD.getDatabase(application)

        //val localUri = Uri.parse( s )
        //imageView.setImageUri( localUri )

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)

        binding = ActivityAffichePlanteBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = getIntent() //retourne Intent qui a fait démarrer l'activité

        val s=intent.getExtras()?.getString("photoPlante")

        val localUri = Uri.parse(s)
        binding.photoPlante.setImageURI(localUri)


        val nomCommun : String?  = intent.getExtras()?.getString( "nomCommun" )
        val nomLatin : String?  = intent.getExtras()?.getString( "nomLatin" )
        val dateProchainArosage :Long ?= intent.getExtras()?.getLong("ProchainArosage")
        val lien : String?=  intent.getExtras()?.getString("photoPlante")
        val planteId : Int?  = intent.getExtras()?.getInt("id")
        if (nomCommun != null) binding.leNomCommun.text = nomCommun  // afichage
        if (nomLatin != null) binding.leNomLatin.text = nomLatin
        if(planteId!=null && nomCommun!=null && nomLatin!=null && lien!=null ){

            val id : Int = planteId!!
            val nomC : String = nomCommun!!
            val nomL: String = nomLatin!!
            val dateP : Calendar =TimestampToCalendar(dateProchainArosage)!!
            val lienP :String = lien
            plante = Plante(idPlante = id,nomCommun=nomC,nomLatin=nomL, dateProchainArosage = dateP, lienPhotoDeLaPlante = lienP)

        }

        if (dateProchainArosage!= null) {
            var prochainArosage : Calendar = Calendar.getInstance()
            prochainArosage.setTimeInMillis(dateProchainArosage)
            val jour : String = prochainArosage.get(Calendar.DAY_OF_MONTH).toString()
            var moins : String = (prochainArosage.get(Calendar.MONTH).toInt() + 1).toString()
            val annee : String = prochainArosage.get(Calendar.YEAR).toString()
            binding.dateArosage.text = jour + " " + moins + " " + annee
        }

        // récupérer la référence vers RecyclerView
        val recyclerView = binding.recycler as RecyclerView
        recyclerView.hasFixedSize() /* pour améliorer les pérformances*/
        recyclerView.layoutManager = LinearLayoutManager(this)

        if(planteId != null) {
            // BDD
            val t = Thread{
                listeDesFrequences = bd.myDao().loadFrequencesById(planteId)

            }
            t.start()
            t.join()

            val couleurPaire: Int = ResourcesCompat.getColor(resources, R.color.couleurPaire, null)
            val couleurImpaire: Int = ResourcesCompat.getColor(resources, R.color.couleurImpaire, null)

            var adapter: ListeFrequencesRecycleAdapter = ListeFrequencesRecycleAdapter( this , listeDesFrequences, couleurPaire, couleurImpaire)
            recyclerView.adapter = adapter

        } else {
            Toast.makeText(this, "Les fréquences pour cette plante n'ont pas pu être trouvées dans la base de données. \n", Toast.LENGTH_LONG).show()
        }

    }


    fun CalendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.getTimeInMillis()
    }

    fun getListePlantes(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    fun supprimer(view: View){
        model.removePlante(plante)
        model.removePlanteFrequences(listeDesFrequences)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
    fun modifier(view:View){
        supprimer(view)
        val intent = Intent(this, AjouterNouvellePlante::class.java)
        val dateP:Long ?=  CalendarToTimestamp(plante.dateProchainArosage)
        intent.putExtra("id", plante.idPlante)
        intent.putExtra("nomC", plante.nomCommun)
        intent.putExtra("nomL", plante.nomLatin)
        intent.putExtra("dateP", dateP)
        intent.putExtra("lien", plante.lienPhotoDeLaPlante)
        intent.putExtra("nbFreq", listeDesFrequences.size)
        for(i in listeDesFrequences.indices){
            var datedeb : String = listeDesFrequences[i].dateDebut
            var datefin : String  = listeDesFrequences[i].dateFin
            var nb : String =  listeDesFrequences[i].nbFois
            intent.putExtra("datedeb"+i, datedeb)
            intent.putExtra("datefin"+i, datefin)
            intent.putExtra("nbfois"+i, nb)

        }


        startActivity(intent)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}