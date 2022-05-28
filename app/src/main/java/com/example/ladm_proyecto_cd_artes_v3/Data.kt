package com.example.ladm_proyecto_cd_artes_v3

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

    class Data (activity: MapsActivity){
       // class Data (){
        var act = activity
        var nombre: String = ""
        var posicion1: GeoPoint = GeoPoint(0.0,0.0)
        var posicion2: GeoPoint = GeoPoint(0.0,0.0)
        var descripcion: String = "Aqui va la Descripcion... n_n"

        override fun toString(): String {
            return nombre+"\n"+posicion1.latitude+","+posicion1.longitude+"\n"+
                    posicion2.latitude+","+posicion2.longitude
        }

        fun estoyEn(posicionActual:GeoPoint): Boolean{
         /*   AlertDialog.Builder(act)
                .setMessage("estoyEn(1er IF ${nombre}) \n\n     posicionActual.latitude  / posicion1.latitude &&\n" + "posicionActual.latitude /  posicion2.latitude"
                   +" \n\n(Var)     ${posicionActual.latitude} < ${posicion1.latitude} && \n" + "${posicionActual.latitude} > ${posicion2.latitude}"
                )
                .setPositiveButton("OK"){p,q-> }
                .show()*/

            if(posicionActual.latitude < posicion1.latitude &&
                posicionActual.latitude > posicion2.latitude){

                /* AlertDialog.Builder(act)
                    .setMessage("\n\n estoyEn(2do IF ${nombre}) \n\n    invertir(posicionActual.longitude) / invertir(posicion1.longitude) &&  invertir(posicionActual.longitude) / invertir(posicion2.longitude"
                            +" \n\n(Var)    ${invertir(posicionActual.longitude)} < ${invertir(posicion1.longitude)} && ${invertir(posicionActual.longitude)} > ${invertir(posicion2.longitude)}")
                    .setPositiveButton("OK"){p,q-> }
                    .show()*/

                if(invertir(posicionActual.longitude) < invertir(posicion1.longitude) &&
                    invertir(posicionActual.longitude) > invertir(posicion2.longitude)){
                    return true
                }
            }
            return false
        }

        private fun invertir(valor:Double):Double{
            return (valor*-1)
        }
    }


