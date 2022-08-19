/*
 * MyDao.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */
package mobiles.projet.arrosage

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insertPlante(vararg plante :Plante): List<Long> // la liste des id des éléments insérés

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    fun insertFrequence(vararg freq : Frequence): List<Long> // la liste des fréquences déja insérées

    /**
     * La méthode loadAll fait une requête SELECT
     * qui retourne un Array<TablePlante> avec tous les plantes.
     */
    @Query("SELECT * FROM Plante") // L'annotation Query contient une requête select et cette requete renvoie tt sur la table plante et renvoie une chaine de tables plantes
    fun loadAll(): Array<Plante> // La fonction qui exécute select retourne un Array d'objets plante

    @Query("SELECT * FROM Plante")
    fun loadAllLive(): LiveData< List<Plante> >

    @Delete
    fun deleteFromPlante(vararg plante : Plante):Int


    @Delete
    fun deleteFrequence(vararg freq: Frequence): Int
    /**
     * La méthode loadFrequencesById(id) retourne un Array<Frequence> avec tous les frequences d'une plante
     */
    @Query("SELECT * FROM Frequence WHERE IdPlante = :idPlante ")
    fun loadFrequencesById(vararg idPlante: Int): Array<Frequence> // Requête paramétrée : dans la requete le nom de paramètre de la fonction précédé par :

    /**
     * La méthode loadPartialName(nom) retourne une liste avec les plantes commençant par nom
     */
    @Query("SELECT * FROM Plante WHERE nomCommun like :nom || '%'") // || représente la concaténation de chaînes, le signe de pourcentage (%) représente zéro, un ou plusieurs caractères
    fun loadPartialName(nom: String): List<Plante> // Requête paramétrée : dans la requete le nom de paramètre de la fonction précédé par :

    @Update
    fun updatePlante(vararg plante :Plante) : Int // La valeur de retour donne le nombre d'items modifiés par update.
}