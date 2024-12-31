package Pickling.HTTP

import kotlinx.serialization.Serializable

@Serializable sealed class Serialisation

@Serializable data class Utilisateur(
    val id: Int? = null,
    val userId: Int,
    val title: String,
    val body: String,
) : Serialisation()