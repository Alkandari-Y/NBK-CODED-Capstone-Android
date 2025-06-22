package com.coded.capstone.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.MapAndGeofencing.GeofenceManager
import kotlinx.coroutines.launch

class ServiceFlowViewModel(application: Application) : AndroidViewModel(application) {

    init {
        setupGeofences()
    }

    private fun setupGeofences() {
        viewModelScope.launch {
            try {
                GeofenceManager.startGeofencing(getApplication())
                Log.d("ServiceFlowViewModel", "Successfully started geofencing.")
            } catch (e: Exception) {
                Log.e("ServiceFlowViewModel", "Error starting geofences", e)
            }
        }
    }
} 