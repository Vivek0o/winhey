package com.avfusionapps.winhey.data.models

data class AuthState(
    var isLoggedIn: Boolean = false,
    var userType: UserType = UserType.NONE
)
