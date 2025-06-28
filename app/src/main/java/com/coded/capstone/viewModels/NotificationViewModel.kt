package com.coded.capstone.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coded.capstone.data.responses.notification.NotificationResponseDto
import com.coded.capstone.navigation.NavRoutes
import com.coded.capstone.providers.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NotificationUiState {
    data object Loading : NotificationUiState()
    data class Success(val notifications: List<NotificationResponseDto>) : NotificationUiState()
    data class Error(val message: String) : NotificationUiState()
}

class NotificationViewModel(
    private val context: Context
) : ViewModel() {

    private val _notificationsUiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val notificationsUiState: StateFlow<NotificationUiState> = _notificationsUiState

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val notificationService = RetrofitInstance.getNotificationServiceProvide(context)

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                _notificationsUiState.value = NotificationUiState.Loading

                val response = notificationService.getAllNotifications()

                if (response.isSuccessful) {
                    val notifications = response.body() ?: emptyList()
                    _notificationsUiState.value = NotificationUiState.Success(notifications)
                    _unreadCount.value = notifications.count { !it.delivered }
                } else {
                    _notificationsUiState.value = NotificationUiState.Error("Failed to fetch notifications")
                }

            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error: ${e.message}")
                _notificationsUiState.value = NotificationUiState.Error("Network error: ${e.message}")
            }
        }
    }

    fun markAsRead(notificationId: Long) {
        val currentState = _notificationsUiState.value
        if (currentState is NotificationUiState.Success) {
            val updated = currentState.notifications.map {
                if (it.id == notificationId) it.copy(delivered = true) else it
            }
            _notificationsUiState.value = NotificationUiState.Success(updated)
            _unreadCount.value = updated.count { !it.delivered }
        }
    }

    fun getRoute(notification: NotificationResponseDto): String {
        return if (notification.recommendationId != null) {
            "${NavRoutes.NAV_ROUTE_RECOMMENDATIONS}?fromNotification=true"
        } else {
            NavRoutes.promotionDetailsRoute(notification.promotionId ?: 1L)
        }
    }
}