package com.example.templepocforground.models

data class User(val userId: String, val username: String, val password: String)

val users = listOf(
    User("userid1", "John", "test@123"),
    User("userid2", "Sridhar", "test@123"),
    User("userid3", "Vishnu", "test@123"),
    User("userid4", "Saneesh", "test@123"),
    User("userid5", "karthik", "test@123"),
    User("userid6", "Thomas", "test@123"),
    User("userid7", "Amber", "test@123"),
    User("userid8", "Arama", "test@123"),
    User("userid9", "Nidheesh", "test@123"),
    User("userid10", "Test", "test@123")
)

fun authenticate(username: String, password: String): String? {
    val user = users.find { it.username.lowercase().equals(username.lowercase(), ignoreCase = true) && it.password.lowercase() == password.lowercase() }
    return user?.userId
}

