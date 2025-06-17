package com.coded.capstone.data.Tmp

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

sealed class Offer {
    data class SingleDate(
        val name: String,
        val description: String,
        val date: String,
        val category: String
    ) : Offer() {
        fun isValidForDate(selectedDate: Date): Boolean {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val offerDate = dateFormat.parse(date)
                val result = offerDate?.let { !selectedDate.after(it) } ?: false
                Log.d("Offer", "SingleDate offer '$name' (date: $date) validation for ${dateFormat.format(selectedDate)}: $result")
                result
            } catch (e: Exception) {
                Log.e("Offer", "Error validating SingleDate offer '$name': ${e.message}")
                false
            }
        }
    }

    data class DateRange(
        val name: String,
        val description: String,
        val startDate: String,
        val endDate: String,
        val category: String
    ) : Offer() {
        fun isValidForDate(selectedDate: Date): Boolean {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val start = dateFormat.parse(startDate)
                val end = dateFormat.parse(endDate)
                
                if (start == null || end == null) {
                    Log.e("Offer", "DateRange offer '$name' has invalid dates: start=$startDate, end=$endDate")
                    return false
                }
                
                val result = !selectedDate.before(start) && !selectedDate.after(end)
                Log.d("Offer", "DateRange offer '$name' (${startDate} to ${endDate}) validation for ${dateFormat.format(selectedDate)}: $result")
                result
            } catch (e: Exception) {
                Log.e("Offer", "Error validating DateRange offer '$name': ${e.message}")
                false
            }
        }
    }
} 