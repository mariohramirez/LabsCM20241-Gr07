package com.example.jetsnack.network

import com.example.jetsnack.model.Snack
import retrofit2.http.GET

//Se encarga de hacer las solicitudes HTTP
interface SnacksAPIService {

    //Se envia al endpoint snacks
    @GET("snacks")
    suspend fun listSnacks(): List<Snack>
}