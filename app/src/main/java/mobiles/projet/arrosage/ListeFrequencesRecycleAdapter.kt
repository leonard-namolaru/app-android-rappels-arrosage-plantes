package mobiles.projet.arrosage

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListeFrequencesRecycleAdapter ( val context : Context , val listeDesFrequences: Array<Frequence>, val couleurPaire: Int, val couleurImpaire: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // Parametres du constructeur : la liste de frequences, les couleurs couleurPaire, couleurImpair (en tant que Int)

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    // RecyclerView apelle onCreateViewHolder quand il a besoin d'un nouveau couple (View, Holder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Crer View dun element de la liste a partir de fichier layout xml
        val v = LayoutInflater
            .from(parent.getContext())
            .inflate(R.layout.element_liste_plantes, parent, false)

        val holder: RecyclerView.ViewHolder = VH(v)
        return holder // retourner le ViewHolder
    }

    // La fonction onBindViewHolder est appelee par RecyclerView
    // quand litem a la position "position" devient visible.
    // Le holder a recycler est donne comme le premier parametre.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /* Recuperer la View :
         * holder.itemView cest la View associee a ce holder
         */
        val textViewPourNom: TextView = holder.itemView.findViewById(R.id.nom) as TextView
        val imageViewPourPlante: ImageView = holder.itemView.findViewById(R.id.photoPlante) as ImageView
        imageViewPourPlante.visibility = INVISIBLE;
        textViewPourNom.text = "Chaque " + listeDesFrequences[position].nbFois + " jours entre " + listeDesFrequences[position].dateDebut + " et " + listeDesFrequences[position].dateFin + " "  ;
        if (position % 2 == 0)
            holder.itemView.setBackgroundColor(couleurPaire)
        else
            holder.itemView.setBackgroundColor(couleurImpaire)


    }

    override fun getItemCount(): Int = listeDesFrequences.size
}