/*
 * ListePlantesArroserAujourdhuiRecycleAdapter.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */
package mobiles.projet.arrosage

import android.content.Context
import android.net.Uri
// import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import kotlin.collections.ArrayList

/**
 * @param context
 * @param listeDesPlantes la liste mutable de plantes
 * @param couleurPaire  en tant que Int
 * @param couleurImpair en tant que Int
 */
class ListePlantesArroserAujourdhuiRecycleAdapter ( val context : Context , val listeDesPlantes: MutableList<Plante>, val couleurPaire: Int, val couleurImpaire: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val checked = ArrayList<Plante>()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * La fonction removeChecked() est appelé pour supprimer les items "checked" de l'adapter
     * @param bd Instance vers la base de données
     * @param nomsDesMois une liste de Strings avec la liste des mois
     */
    fun updateChecked(bd: PlantesBD, nomsDesMois : Array<String>) {
        for(i in checked.indices) {
            val prochainArosage : Calendar = Calendar.getInstance()
            prochainArosage.set(Calendar.DAY_OF_YEAR, prochainArosage.get(Calendar.DAY_OF_YEAR) + 1)
            listeDesPlantes[i].dateProchainArosage = prochainArosage

            val t2 = Thread{
                val result : Int = bd.myDao().updatePlante(listeDesPlantes[i])
                // Log.d("result", result.toString())
            }
            t2.start()
            t2.join()
        }
        listeDesPlantes.removeAll(checked)
        checked.clear()
        notifyDataSetChanged()
        arrosagesEffectues(bd, nomsDesMois)
    }

    fun arrosagesEffectues(bd: PlantesBD, nomsDesMois : Array<String>) {
        for(i in listeDesPlantes.indices) {
            var listeDesFrequences : Array<Frequence> = arrayOf() // Le Array de frequences de la plante i
            val t = Thread{ listeDesFrequences = bd.myDao().loadFrequencesById( listeDesPlantes[i].idPlante ) }
            t.start()
            t.join()

            var dateAujourdhui : Calendar = Calendar.getInstance()
            val moisEnCours : Int = dateAujourdhui.get(Calendar.MONTH) + 1
            var frequenceActuelle : Int = 0

            for(j in listeDesFrequences.indices) {
                if ((moisEnCours >= nomsDesMois.indexOf(listeDesFrequences[j].dateDebut)) &&  (moisEnCours <= nomsDesMois.indexOf(listeDesFrequences[j].dateFin))){
                    frequenceActuelle = listeDesFrequences[j].nbFois.toInt()
                    break
                }
            }

            val prochainArosage : Calendar = Calendar.getInstance()
            prochainArosage.set(Calendar.DAY_OF_YEAR, prochainArosage.get(Calendar.DAY_OF_YEAR) + frequenceActuelle)
            listeDesPlantes[i].dateProchainArosage = prochainArosage

            val t2 = Thread{
                val result : Int = bd.myDao().updatePlante(listeDesPlantes[i])
                // Log.d("result", result.toString())
            }
            t2.start()
            t2.join()

            listeDesPlantes.remove(listeDesPlantes[i])
            notifyDataSetChanged()
        }
    }

    // RecyclerView apelle onCreateViewHolder quand il a besoin d'un nouveau couple (View, Holder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //A partir de la liste , on crée les couple view holder
        // Crer View dun element de la liste a partir de fichier layout xml
        val v = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.element_liste_plantes_arroser_aujourdhui, parent, false) as CheckedTextView

        val holder: RecyclerView.ViewHolder = VH(v)
        // Installer le meme listener sur chaqaue View
        v.setOnClickListener( View.OnClickListener { view ->
            val plantePosition: Int = holder.absoluteAdapterPosition // récuperer la position sur la quelle on a cliqué
            val plante: Plante = listeDesPlantes[plantePosition]
            val w = view as CheckedTextView
            w.toggle() // Changer l'état  de la vue à l'inverse de son état actuel
            if (w.isChecked) {
                checked.add(plante)
            } else {
                checked.remove(plante)
            }
        } )

        return holder // retourner le ViewHolder
    }

    // La fonction onBindViewHolder est appelee par RecyclerView
    // quand litem a la position "position" devient visible.
    // Le holder a recycler est donne comme le premier parametre.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /* Recuperer la View :
         * holder.itemView cest la View associee a ce holder
         */

        val checkedTextViewPourNom: CheckedTextView = holder.itemView.findViewById(R.id.nom) as CheckedTextView
        checkedTextViewPourNom.text = listeDesPlantes[position].nomCommun + " "
        if (position % 2 == 0)
            holder.itemView.setBackgroundColor(couleurPaire)
        else
            holder.itemView.setBackgroundColor(couleurImpaire)
    }

    override fun getItemCount(): Int = listeDesPlantes.size
}