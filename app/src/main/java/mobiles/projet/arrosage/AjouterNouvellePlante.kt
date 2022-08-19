/*
 * AjouterNouvellePlante.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */

package mobiles.projet.arrosage

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mobiles.projet.arrosage.databinding.ActivityAjouterNouvellePlanteBinding
import android.view.View
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import java.io.File
import java.util.Calendar

class AjouterNouvellePlante : AppCompatActivity() {
    private lateinit var binding: ActivityAjouterNouvellePlanteBinding
    var localUri : Uri? = null // propriété : Uri de fichier local

    // getContent : une propriété  pour enregistrer une méthode callback appelée au retour de la nouvelle activité.
    var getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Méthode callback qui obtient l’Uri de l’image comme paramètre
        
        if( uri == null ) return@registerForActivityResult // Si uri null rien à faire

        // Si uri de l’image différent de null :
        // Dans le code ci-dessous le nom de ce fichier local est composé de String "plante" auquel on concatène un numéro

        val inputStream = getContentResolver().openInputStream(uri) // inputStream avec l’image de la plante
        val fileNamePrefix = "plante"; // Fabriquer le nom de fichier local pour stocker l’image
        val preferences = getSharedPreferences("numImage", Context.MODE_PRIVATE) // Le numéro de l'image est stocké dans SharedPreferences
        val numImage = preferences.getInt("numImage", 1)
        val fileName = "$fileNamePrefix$numImage"

        // Ouvrir outputStream vers un fichier local
        val file = File(this.filesDir, fileName)
        val outputStream = file.outputStream()

        preferences.edit().putInt("numImage",numImage+1).commit() // Sauvegarder le nouveau compteur d’image
        inputStream?.copyTo(outputStream) // Copier inputStream qui pointe sur l’image de la galerie vers le fichier local
        localUri = file.toUri() // Mémoriser Uri de fichier local dans la propriété localUri

        outputStream.close()
        inputStream?.close()

        binding.lienPic.text = "Image enregistrée"
    }

    fun utiliserImageDeLaGalerieDimage(button: View) {
        getContent.launch("image/*")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjouterNouvellePlanteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.title= "AJouter Plante"

        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)


        val intent = getIntent() //retourne Intent qui a fait démarrer l'activité
        val planteId : Int  = intent.getExtras()?.getInt("id")?:-1
        val nomCommun : String  = intent.getExtras()?.getString( "nomC" )?:""
        val nomLatin : String = intent.getExtras()?.getString( "nomL" )?:""
        val lien : String =  intent.getExtras()?.getString("lien") ?:""

        var freq : MutableList<Frequence> = mutableListOf()
        val nbfreq =  intent.getExtras()?.getInt("nbFreq") ?:-1
        var i:Int =0
        while (i<nbfreq){
            val moisDebut :  String =  intent.getExtras()?.getString("datedeb"+i) ?: ""
            val moisFin :  String =  intent.getExtras()?.getString("datefin"+i) ?: ""
            val nbFois :  String =  intent.getExtras()?.getString("nbfois"+i) ?: ""
            freq.add( Frequence(-1,planteId,moisDebut,moisFin,nbFois))

            i++
        }


        binding.nomCommun.setText(nomCommun)
        binding.nomLatin.setText(nomLatin)

        if(nbfreq > 0){
            binding.freq1.setText(freq[0].nbFois)
        }

        if(nbfreq > 1){
            binding.freq2.setText(freq[1].nbFois)
        }

        if(nbfreq > 2){
            binding.freq3.setText(freq[2].nbFois)
        }

        localUri = Uri.parse(lien)
        if(planteId != -1 && lien != "")
            binding.lienPic.text = "Modifier image"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Cette fonction permet d'ajouter des plantes dans la base de données
    // C'est à dire : on récupère les champs et on les mets dans la bd
    fun valider(button: View){
        val bd = PlantesBD.getDatabase(application).myDao()

        var dateAujourdhui : Calendar = Calendar.getInstance()
        val moisEnCours : Int = dateAujourdhui.get(Calendar.MONTH) + 1
        var frequenceActuelle : Int = 0


        // Spinner 1 : date début et date de fin
        var spinner1 :Spinner = findViewById(R.id.dateDebutFreq1);
        var dateDebutFreq1 : String = spinner1.selectedItem.toString().trim();
        var spinner2 :Spinner = findViewById(R.id.dateFinFreq1);
        var dateFinFreq1 : String = spinner2.selectedItem.toString().trim();

        // Spinner 2 : date début et date de fin
        var spinner3 :Spinner = findViewById(R.id.dateDebutFreq2);
        var dateDebutFreq2 : String = spinner3.selectedItem.toString().trim();
        var spinner4 :Spinner = findViewById(R.id.dateFinFreq2);
        var dateFinFreq2: String = spinner4.selectedItem.toString().trim();

        // Spinner 3 : date début et date de fin
        var spinner5 :Spinner = findViewById(R.id.dateDebutFreq3);
        var dateDebutFreq3: String = spinner5.selectedItem.toString().trim();
        var spinner6 :Spinner = findViewById(R.id.dateFinFreq3);
        var dateFinFreq3 : String = spinner6.selectedItem.toString().trim();

        // Les fréquences des trois spinner
        var freq1: String = binding.freq1.text.toString().trim()
        var freq2: String = binding.freq2.text.toString().trim()
        var freq3 : String = binding.freq3.text.toString().trim()

        // Supprimer les espaces au début et à la fin de chaque valeur String
        var nomCom: String = binding.nomCommun.text.toString().trim()
        var nomLat: String = binding.nomLatin.text.toString().trim()

        // Vérifier que les Strings obtenus ne soient pas vide
        if(nomCom == ""){
            Toast.makeText(this, "Vous devez mentioner un nom commun", Toast.LENGTH_LONG).show()
            return
        }

        var nomC: String?
        var nomL: String?
        var lien : String?

        if(!binding.nomCommun.text.isNullOrBlank())
            nomC= binding.nomCommun.text.toString()
        else
            nomC= null // Si le TextView de nomCommun est vide alors insérer null comme la NomLatin.

        if(!binding.nomLatin.text.isNullOrBlank())
            nomL = binding.nomLatin.text.toString()
        else
            nomL = null // Si le TextView de nomLatin est vide alors insérer null comme NomLatin.

        if(!binding.lienPic.text.isNullOrBlank())
            lien = binding.lienPic.text.toString()
        else
            lien = null // Si le TextView de nomLatin est vide alors insérer null comme NomLatin.

        var insertFrequence1 : Boolean = false
        var insertFrequence2 : Boolean = false
        var insertFrequence3 : Boolean = false

        if(freq1.isNotBlank()) {

            if(!freq1.isDigitsOnly()){ // c'est pas vide et ça contient des caractères
                Toast.makeText(this, "Les frequence doit etre un chiffre uniquement ", Toast.LENGTH_LONG).show()
                return
            }else{  // Ça contient que des digit
                if(freq1.toInt() < 1) {
                    Toast.makeText(this, "Fréquence 1 : La fréquence doit être supérieure à zéro", Toast.LENGTH_LONG).show()
                    return
                }

                if (spinner1.selectedItemPosition >= spinner2.selectedItemPosition) {
                    Toast.makeText(this, "Fréquence 1 : Le mois de fin doit être supérieur au mois de début", Toast.LENGTH_LONG).show()
                    return
                }

                if (moisEnCours >= spinner1.selectedItemPosition && moisEnCours <= spinner2.selectedItemPosition)
                    frequenceActuelle = freq1.toInt()

                insertFrequence1 = true
            } // else
        } // if

        if(freq2.isNotBlank()) {
            if(!freq1.isDigitsOnly()){ // c'est pas vide et ça contient des caractères
                Toast.makeText(this, "Les frequence doit etre un chiffre uniquement ", Toast.LENGTH_LONG).show()
                return
            }else{  // Ça contient que des digit
                if(freq2.toInt() < 1) {
                    Toast.makeText(this, "Fréquence 2 : La fréquence doit être supérieure à zéro", Toast.LENGTH_LONG).show()
                    return
                }

                if ((spinner3.selectedItemPosition < spinner2.selectedItemPosition) || (spinner3.selectedItemPosition >= spinner4.selectedItemPosition)) {
                    Toast.makeText(this, "Fréquence 2 : Le mois de fin doit être supérieur au mois de début et les deux doivent toujours être supérieurs aux mois de fréquence 1", Toast.LENGTH_LONG).show()
                    return
                }

                if ( (frequenceActuelle == 0) && ( (moisEnCours >= spinner3.selectedItemPosition) && (moisEnCours <= spinner4.selectedItemPosition) ) )
                    frequenceActuelle = freq2.toInt()

                insertFrequence2 = true
            } // else
        } // if

        if(freq3.isNotBlank()) {
            if(!freq1.isDigitsOnly()){ // c'est pas vide et ça contient des caractères
                Toast.makeText(this, "Les frequence doit etre un chiffre uniquement ", Toast.LENGTH_LONG).show()
                return
            }else{  // Ça contient que des digit
                if(freq3.toInt() < 1) {
                    Toast.makeText(this, "Fréquence 3 : La fréquence doit être supérieure à zéro", Toast.LENGTH_LONG).show()
                    return
                }

                if ((spinner5.selectedItemPosition < spinner4.selectedItemPosition) || (spinner5.selectedItemPosition >= spinner6.selectedItemPosition)) {
                    Toast.makeText(this, "Fréquence 3 : Le mois de fin doit être supérieur au mois de début et les deux doivent toujours être supérieurs aux mois de fréquence 2", Toast.LENGTH_LONG).show()
                    return
                }

                if ( (frequenceActuelle == 0) && ( (moisEnCours >= spinner5.selectedItemPosition) && (moisEnCours <= spinner6.selectedItemPosition) ) )
                    frequenceActuelle = freq3.toInt()

                insertFrequence3 = true
            } // else
        } // if

        var idPlanteDansBDD : Int = 0

        if (!insertFrequence1 && !insertFrequence2 && !insertFrequence3) {
            Toast.makeText(this, "Veuillez remplir au moins une fréquence", Toast.LENGTH_LONG).show()
            return
        }

        val prochainArosage : Calendar = Calendar.getInstance()
        prochainArosage.set(Calendar.DAY_OF_YEAR, prochainArosage.get(Calendar.DAY_OF_YEAR) + frequenceActuelle)

        // Traitement du cas où la première fréquence se produit dans un mois à venir
        if(frequenceActuelle == 0) {
            if(insertFrequence1) {
                val annee : Int = ( if (moisEnCours < spinner1.selectedItemPosition) dateAujourdhui.get(Calendar.YEAR) else (dateAujourdhui.get(Calendar.YEAR) + 1))
                prochainArosage.set(annee  , spinner1.selectedItemPosition, 1) // set(int year, int month, int date)

            } else if(insertFrequence2) {
                val annee : Int = ( if (moisEnCours < spinner3.selectedItemPosition) dateAujourdhui.get(Calendar.YEAR) else (dateAujourdhui.get(Calendar.YEAR) + 1))
                prochainArosage.set(annee  , spinner3.selectedItemPosition, 1) // set(int year, int month, int date)

            } else if(insertFrequence3) {
                val annee : Int = ( if (moisEnCours < spinner5.selectedItemPosition) dateAujourdhui.get(Calendar.YEAR) else (dateAujourdhui.get(Calendar.YEAR) + 1))
                prochainArosage.set(annee  , spinner5.selectedItemPosition, 1) // set(int year, int month, int date)

            }
        }

        // Insertion
        var t = Thread {
            bd.insertPlante( Plante( nomCommun =nomCom, nomLatin = nomLat /*, lienPhotoDeLaPlante = lien*/, dateProchainArosage = prochainArosage, lienPhotoDeLaPlante = localUri.toString() ))

            val lesPlantes : Array<Plante> = bd.loadAll() // Récupérer toutes les plantes
            idPlanteDansBDD = lesPlantes.last().idPlante;
        }
        t.start()
        t.join();

        if(insertFrequence1) {
            var t1 = Thread {
                bd.insertFrequence( Frequence( IdPlante = idPlanteDansBDD, dateDebut = dateDebutFreq1, dateFin = dateFinFreq1 , nbFois = freq1 ))
            }
            t1.start()
            t1.join();
        }

        if(insertFrequence2) {
            var t2 = Thread {
                bd.insertFrequence( Frequence( IdPlante = idPlanteDansBDD, dateDebut = dateDebutFreq2, dateFin = dateFinFreq2, nbFois = freq2))
            }
            t2.start()
            t2.join();
        }

        if(insertFrequence3) {
            var t3 = Thread {
                bd.insertFrequence( Frequence( IdPlante = idPlanteDansBDD, dateDebut = dateDebutFreq3, dateFin = dateFinFreq3 , nbFois = freq3 ))
            }
            t3.start()
            t3.join();
        }

        // Nettoyer tous les EditText
        binding.nomCommun.text.clear()
        binding.nomLatin.text.clear()
        binding.freq1.text.clear()
        binding.freq2.text.clear()
        binding.freq3.text.clear()
    }

}