package com.example.ladm_proyecto_cd_artes_v3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ladm_proyecto_cd_artes_v3.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    val posicion = ArrayList<Data>()
    val lugares = ArrayList<String>()
    private var listaLugares= ArrayList<String>()
    private lateinit var locacion:LocationManager
    private lateinit var autoCompletar : AutoCompleteTextView

    //BottomSheetLayout
    lateinit var tvNombre:TextView
    lateinit var tvDes:TextView
    lateinit var tvHor:TextView
    lateinit var tvTel:TextView
    lateinit var rbCal:RatingBar
    lateinit var ivImagen:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //PERMISOS
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        //BUSCADOR DE LUGARES
        autoCompletar = findViewById(R.id.acBusqueda)
        listaLugares = ArrayList()

        //RECUPERACION DE COORDENAS FIREBASE ------------------------------------------
        FirebaseFirestore.getInstance()
            .collection("cdArtes")
            .addSnapshotListener { query, error ->
                var res=""
                lugares.clear() // si no se pone te estara duplicando datos
                posicion.clear()
                listaLugares.clear()

                if(error!=null){
                    //si hubo error
                    AlertDialog.Builder(this)
                        .setMessage(error.message)
                        .show()
                    return@addSnapshotListener //pasa salirme
                }

                for(documento in query!!){//ciclo que recoje los datos de la colleccion
                    var cadena = "Nombre: ${documento.getString("nombre")}\n" +
                            " punto 1: ${documento.getGeoPoint("punto1")!! }\n"+
                            " punto 2: ${documento.getGeoPoint("punto2")!! }\n\n"
                    lugares.add(cadena)
                    listaLugares!!.add(documento.getString("nombre")!!)

                    var data=Data(this)
                    data.nombre = documento.getString("nombre").toString()
                    data.posicion1=documento.getGeoPoint("punto1")!!
                    data.posicion2=documento.getGeoPoint("punto2")!!
                    data.descripcion=documento.getString("descripcion").toString()
                    data.horario=documento.getString("horario").toString()
                    data.telefono=documento.getString("telefono").toString()
                    data.imagen=documento.getString("imagen").toString()
                    posicion.add(data)
                    res+=data.toString()+"\n\n"

                }
                autoCompletar.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, listaLugares!!))


                /*
                //saber los datos que esta recuperando
                Toast.makeText(this,"DATOS-GEOPOINTS \n\n"+res, Toast.LENGTH_LONG).show()
                AlertDialog.Builder(this)
                    .setMessage("       - INFO DE FIREBASE - \n\n"+lugares)
                    .setPositiveButton("OK"){p,q-> }
                    .show()*/
            } //fin evento snapshoot



        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val oyente = Oyente(this)

        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,01f,oyente)

        //BUSQUEDA (AUTOCOMPLETAR)
        autoCompletar.setOnClickListener {
            autoCompletar.showDropDown()
        }

        autoCompletar.setOnItemClickListener { _, _, _, _ ->
            var nomLugarClick = autoCompletar.text.toString()

            if(nomLugarClick=="Universidad de M??sica")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.511986208992873, -104.90210851297405),19f),3000,null )
            if(nomLugarClick=="Tacos AFTER")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.51165095509239, -104.90403546698616),19f),3000,null )
            if(nomLugarClick=="Parque de la Dignidad")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.512381454724817, -104.90404305341924),19f),3000,null )
            if(nomLugarClick=="Zona de Teatro")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.51108085342471, -104.90307804391163),19f),3000,null )
            if(nomLugarClick=="Zona de Comida")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.51091873667444, -104.9037786567254),19f),3000,null )
            if(nomLugarClick=="Skate Park")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.51197562204376, -104.9027154276223),19f),3000,null )

        }

        binding.btnBorrar.setOnClickListener {
            binding.acBusqueda.setText("")
        }
        
    }
    


    override fun onMapReady(googleMap: GoogleMap) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        mMap = googleMap
        crearMarcadores()

        //ANIMACION DE CAMARA
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(21.51108496011311, -104.90307329600743),17f),3000,null )//Cuanto zoom y cuando durar??)

        //CLIC EN MAPA
        mMap.setOnMapClickListener {
            //miUbicacion()
           // abrirBottomSheet()
        }

        //Ajustes de botones graficos de google
        mMap.uiSettings.isZoomControlsEnabled =  true
        mMap.isMyLocationEnabled = true

    }

   // fun abrirBottomSheet(nom : String,desc :String, cal:String){
   fun abrirBottomSheet(nom : String,desc :String,tel :String,hor :String, cal : Float,ima:String){
        //----- BOTTOM SHEET DIALOG --------
        val dialog = BottomSheetDialog(this)
        val vista = layoutInflater.inflate(R.layout.bottom_sheet_dialog,null)
        tvNombre = vista.findViewById(R.id.tvNombre)
        tvDes = vista.findViewById(R.id.tvDes)
        rbCal = vista.findViewById(R.id.rbCal)
        tvTel = vista.findViewById(R.id.tvTel)
        tvHor = vista.findViewById(R.id.tvHorario)
        ivImagen = vista.findViewById(R.id.ivFoto)


        var ubicacion = object {
            var nombre = nom
            var descripcion = desc
            var telefono = tel
            var horario = hor
            var calificacion = cal

        }
        tvNombre.text=ubicacion.nombre
        tvDes.text=ubicacion.descripcion
        tvTel.text=ubicacion.telefono
        tvHor.text=ubicacion.horario
        rbCal.rating = ubicacion.calificacion

       if(ima=="casaemmanuel")
        ivImagen.setImageResource(R.drawable.casa)
       if(ima=="escuela")
           ivImagen.setImageResource(R.drawable.escuela)
       if(ima=="after")
           ivImagen.setImageResource(R.drawable.after)
       if(ima=="comida")
           ivImagen.setImageResource(R.drawable.comida)
       if(ima=="dignidad")
           ivImagen.setImageResource(R.drawable.dignidad)
       if(ima=="skate")
           ivImagen.setImageResource(R.drawable.skate)
       if(ima=="teatro")
           ivImagen.setImageResource(R.drawable.teatro)

        dialog.setCancelable(true)
        dialog.setContentView(vista)
        dialog.show()
    }


/*
    private  fun miUbicacion(){
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation.addOnSuccessListener {
                var geoPos = GeoPoint(it.latitude,it.longitude)
                //  Toast.makeText(this,""+it.latitude+""+""+it.longitude,Toast.LENGTH_SHORT).show()
                var bandera=false
                for(item in posicion){
                    AlertDialog.Builder(this)
                        .setMessage("(M-01) Sus Coordenadas Actuales: "+it.latitude+","+it.longitude+"\n\n")
                        .setPositiveButton("OK"){p,q-> }
                        .show()

                    //SI Compara
                    if(item.estoyEn(geoPos)){
                        AlertDialog.Builder(this)
                            .setMessage("Usted esta en "+item.nombre)
                            .setPositiveButton("OK"){p,q-> }
                            .show()
                        bandera=true
                        //aqui es cuando le da clic al mapa
                    }
                }

                if(bandera==false){
                    AlertDialog.Builder(this)
                        .setMessage("No se encontro ninguna Ubicacion Cercana")
                        .setPositiveButton("OK"){p,q-> }
                        .show()
                }
            }.addOnFailureListener {
                AlertDialog.Builder(this)
                    .setMessage("ERROR DE UBICACION")
                    .setPositiveButton("OK"){p,q-> }
                    .show()
            }
    }*/

    //CREAR LOS MARCADORES
    private fun crearMarcadores() {
         val jardinDignidad=MarkerOptions().position(LatLng(21.512381454724817, -104.90404305341924)).title("Jard??n de la Dignidad")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .snippet("Parque")
            .flat(true).rotation(0f)


        val cdArtes=MarkerOptions().position(LatLng(21.51103975981268, -104.90306478583207)).title("Ciudad de las Artes")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .snippet("Parque")

        val afterTacos=MarkerOptions().position(LatLng(21.51165095509239, -104.90403546698616)).title("Tacos El After")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .snippet("Alimentos")

        val skatePark=MarkerOptions().position(LatLng(21.51197562204376, -104.9027154276223)).title("Skate Park")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .snippet("Zona Recreativa")


        val escuelaMusica=MarkerOptions().position(LatLng(21.511986208992873, -104.90210851297405)).title("Escuela de Musica")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .snippet("Istituci??n")

        val zonaTacos =MarkerOptions().position(LatLng(    21.51091873667444, -104.9037786567254)).title("Zona de Tacos")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .snippet("Alimentos")

        val zonaTeatro =MarkerOptions().position(LatLng(    21.51108085342471, -104.90307804391163)).title("Zona de Tacos")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            .snippet("Zona Recreativa")


        mMap.setOnMapClickListener {
            Toast.makeText(this,"Clic en el Mapa", Toast.LENGTH_SHORT).show()
        }

        mMap.addMarker(cdArtes)
        mMap.addMarker(jardinDignidad)
        mMap.addMarker(afterTacos)
        mMap.addMarker(skatePark)
        mMap.addMarker(escuelaMusica)
        mMap.addMarker(zonaTeatro)
        mMap.addMarker(zonaTacos)



    }
}


//EVENTO CADA VES QUE ME MUEVO
class Oyente(puntero:MapsActivity) : LocationListener {
    private val p = puntero
    override fun onLocationChanged(location: Location) {

        var geoPosicionGPS = GeoPoint(location.latitude,location.longitude)
        Toast.makeText(p,"Te estas moviendo...", Toast.LENGTH_SHORT).show()

        for(item in p.posicion ){
            if(item.estoyEn(geoPosicionGPS)){
               /* AlertDialog.Builder(p)
                    .setMessage("Usted se encuentra en ${item.nombre}  (onLocationChanged)")
                    .setPositiveButton("OK"){p,q-> }
                    .show()*/

                //Abrir la ventanita de informacion
                p.abrirBottomSheet(item.nombre,item.descripcion,item.telefono,item.horario,item.calificacion,item.imagen)
               // p.abrirBottomSheet(item.nombre,item.descripcion,item.calificacion)

                /*    //Abrir la otra ventana Main Activity
                AlertDialog.Builder(p)
                    .setMessage("Usted se encuentra en ${item.nombre}. ??Desea conocer m??s acerca de este lugar?")
                    .setPositiveButton("OK"){r, q->
                        val intent = Intent(p, MainActivity ::class.java)
                        intent.putExtra("idNombre",item.nombre)
                        p.startActivity(intent)
                    }
                    .setNegativeButton("NO"){d,i->

                    }
                    .show()  */
            }
        }
    }
}

