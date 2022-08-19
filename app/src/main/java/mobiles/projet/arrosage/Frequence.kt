/*
 * Frequence.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */
package mobiles.projet.arrosage
import androidx.room.*

@Entity
data class Frequence(
 @PrimaryKey(autoGenerate = true)
 var IdFrequence : Int=0, // L'identifiant unique de la fréquence
 var IdPlante : Int, // L'identifiant unique de la plante à laquelle cette fréquence est pertinente
 var dateDebut : String, // Nom du mois de début de la fréquence
 var dateFin : String, // Nom du mois de fin de la fréquence
 var nbFois :  String // Entre dateDebut et dateFin : Effectuer l'arrosage tous les nbFois jours
)
