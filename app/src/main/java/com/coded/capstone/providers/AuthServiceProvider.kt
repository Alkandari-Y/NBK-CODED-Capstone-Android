package com.coded.capstone.providers

import com.coded.capstone.data.requests.authentication.LoginRequest
import com.coded.capstone.data.requests.authentication.RefreshRequest
import com.coded.capstone.data.requests.authentication.RegisterCreateRequest
import com.coded.capstone.data.responses.authentication.JwtResponse
import com.coded.capstone.data.responses.authentication.UserInfoDto
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthServiceProvider {
    // Auth Controller

    // ROLE -> UNAUTHORIZED
    @POST("/api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterCreateRequest
    ): Response<JwtResponse>

    // ROLE -> UNAUTHORIZED
    @POST("/api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<JwtResponse>


    @POST("/api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body refresh: RefreshRequest
    ): Response<JwtResponse>


    // User Controller
    // NOTE Not all controller mappings included

    // ROLE -> ROLE_ADMIN
    @GET("/api/v1/users/details/{userId}")
    suspend fun getUserInfo(
        @Path("userId") userId: Long
    ): Response<UserInfoDto>

}