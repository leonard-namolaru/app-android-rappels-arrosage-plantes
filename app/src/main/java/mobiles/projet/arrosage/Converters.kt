/*
 * Converters.kt
 * Projet : Application rappels d'arrosage pour plantes - UE Programmation des Composants Mobiles 2021 - 2022
 * M1 : Master Informatique fondamentale et appliquée - Université de Paris.
 * @author AIT BENALI faycal, NAMOLARU leonard
 */
package mobiles.projet.arrosage

import  androidx.room.TypeConverter
import java.util.Calendar

/**
 * Étant donné que la base de données room ne peut pas stocker de données de type Calendar,
 * par exemple : la date du dernier arrosage de l'entité Plante,
 * nous convertissons Calendar en Long (et dans le sens opposé).
 */
class Converters {

    @TypeConverter
    fun TimestampToCalendar(timeStamp: Long?): Calendar? {
        var calendar : Calendar = Calendar.getInstance();
        if(timeStamp != null)
            calendar.setTimeInMillis(timeStamp)
        return calendar
    }

    @TypeConverter
    fun CalendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.getTimeInMillis()
    }

}