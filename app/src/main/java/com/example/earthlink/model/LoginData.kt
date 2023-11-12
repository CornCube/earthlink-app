package com.example.earthlink.model

data class LoginData(val email: String, val password: String)

data class LoginResponse(val token: String)

data class ValidateResponse(val userID: String)