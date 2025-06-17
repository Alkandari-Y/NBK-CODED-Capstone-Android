package com.coded.capstone.data.Tmp.repository

import android.content.Context
import android.util.Log
import com.coded.capstone.data.Tmp.DateRangeOffer
import com.coded.capstone.data.Tmp.Offer
import com.coded.capstone.data.Tmp.SingleDateOffer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class OfferRepository {
    companion object {
        private const val TAG = "OfferRepository"

        fun loadOffers(context: Context): List<Offer> {
            val singleDateOffers = loadSingleDateOffers(context)
            val dateRangeOffers = loadDateRangeOffers(context)
            val allOffers = singleDateOffers + dateRangeOffers
            Log.d(TAG, "Total offers loaded: ${allOffers.size} (${singleDateOffers.size} single-date, ${dateRangeOffers.size} date-range)")
            return allOffers
        }

        fun getOffersForDate(context: Context, date: Date, selectedCategory: String? = null): List<Offer> {
            val allOffers = loadOffers(context)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            Log.d(TAG, "Filtering offers for date: ${dateFormat.format(date)}")
            
            val validOffers = allOffers.filter { offer ->
                val isValidForDate = when (offer) {
                    is Offer.SingleDate -> offer.isValidForDate(date)
                    is Offer.DateRange -> offer.isValidForDate(date)
                }
                val isValidForCategory = selectedCategory == null || when (offer) {
                    is Offer.SingleDate -> offer.category == selectedCategory
                    is Offer.DateRange -> offer.category == selectedCategory
                }
                isValidForDate && isValidForCategory
            }
            
            Log.d(TAG, "Found ${validOffers.size} valid offers for ${dateFormat.format(date)}")
            return validOffers
        }

        fun getAvailableCategories(offers: List<Offer>): List<String> {
            return offers.mapNotNull { offer ->
                when (offer) {
                    is Offer.SingleDate -> offer.category
                    is Offer.DateRange -> offer.category
                }
            }.distinct().sorted()
        }

        private fun loadSingleDateOffers(context: Context): List<Offer.SingleDate> {
            return try {
                val jsonString = context.assets
                    .open("json/nbk_offers_with_dates.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val type = object : TypeToken<List<SingleDateOffer>>() {}.type
                val offers = Gson().fromJson<List<SingleDateOffer>>(jsonString, type)
                val result = offers.map { Offer.SingleDate(it.name, it.description, it.date, it.category) }
                Log.d(TAG, "Loaded ${result.size} single-date offers")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error loading single-date offers: ${e.message}")
                emptyList()
            }
        }

        private fun loadDateRangeOffers(context: Context): List<Offer.DateRange> {
            return try {
                val jsonString = context.assets
                    .open("json/nbk_offers_with_start_end.json")
                    .bufferedReader()
                    .use { it.readText() }
                
                val type = object : TypeToken<List<DateRangeOffer>>() {}.type
                val offers = Gson().fromJson<List<DateRangeOffer>>(jsonString, type)
                val result = offers.map { Offer.DateRange(it.name, it.description, it.start_date, it.end_date, it.category) }
                Log.d(TAG, "Loaded ${result.size} date-range offers")
                result
            } catch (e: Exception) {
                Log.e(TAG, "Error loading date-range offers: ${e.message}")
                emptyList()
            }
        }
    }
} 