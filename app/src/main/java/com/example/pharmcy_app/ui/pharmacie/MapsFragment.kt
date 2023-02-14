package com.example.pharmcy_app.ui.pharmacie

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.pharmcy_app.R
import com.example.pharmcy_app.databinding.FragmentMapsBinding
import com.example.pharmcy_app.databinding.FragmentPharmacieDetailsBinding
import com.example.pharmcy_app.receiclerView.DataPharmacie
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class MapsFragment : Fragment() , OnMapReadyCallback{
    lateinit var mContext: Context
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private var listMarkers: ArrayList<Marker> = ArrayList<Marker>()
    private var pharmacieMame: String = ""
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        var item: MenuItem = menu.getItem(0)
        item.setVisible(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var pharmacieSerialisable: DataPharmacie = DataPharmacie()
        val gson = Gson()

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabBtnCheck.setOnClickListener {
            var bundle = Bundle()
            bundle.putDouble("latitude", latitude)
            bundle.putDouble("longitude", longitude)
            val output = gson.toJson(pharmacieSerialisable)
            bundle.putSerializable("data",output)
            view?.findNavController()?.navigate(R.id.nav_pharmacie_form, bundle)
        }
        if(arguments != null){
            latitude = arguments?.getDouble("latitude") as Double
            longitude = arguments?.getDouble("longitude") as Double
            pharmacieMame = arguments?.getString("pharmacieName") as String
            Log.i("positughvjkh", latitude.toString())

            if(arguments?.getString("data") != null){
                pharmacieSerialisable = gson.fromJson(arguments?.getString("data"), DataPharmacie::class.java)
            }
        }
        return root
//        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = this.childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

    }

    override fun onStart() {
        super.onStart()

        setupPermission()

        if (map != null) {
            map!!.clear()
            updateLocationUI()
            getDeviceLocation()
        }

    }

    override fun onMapReady(map: GoogleMap) {
        Log.i("taggg83", map.toString())
        this.map = map
        updateLocationUI()
        getDeviceLocation()

    }

    private var locationPermissionGranted = false

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val mLocationRequest: LocationRequest = LocationRequest.create()
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                val mLocationCallback: LocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        if (locationResult == null) {
                            return
                        }

                        for (location in locationResult.locations) {

                            if (location != null) {
                                updateLocationUI()
                                if(latitude != 0.0)
                                    addMarker(latitude, longitude, pharmacieMame)
                                else
                                addMarker(location.latitude, location.longitude)
                            }
                        }
                    }
                }
                LocationServices.getFusedLocationProviderClient(mContext)
                    .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

   private fun canGetLocation(): Boolean {
        var gpsEnabled = false
        var networkEnabled = false
        val mgr = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            gpsEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }
        try {
            networkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }
        return gpsEnabled && networkEnabled
    }


    private fun setupPermission() {
        val res = canGetLocation()
        if (res) {
            if (ContextCompat.checkSelfPermission(
                    this.mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {

                locationPermissionGranted = true
            } else {
                ActivityCompat.requestPermissions(
                    mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    122
                )
                updateLocationUI()

            }
        } else {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(mContext)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            )
            { dialog, id ->
                dialog.cancel()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun addMarker(latitude: Double, longitude: Double, title:String="") {

        map?.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title(title))
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 17.0f
            )
        )
    }
    var map: GoogleMap? = null

    private fun updateLocationUI() {
        if (map == null) {
            return
        }

        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isZoomControlsEnabled = true
                map?.uiSettings?.isZoomGesturesEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true

                map?.setOnMapClickListener {
                    val m = map?.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title(pharmacieMame))
                    for (elm: Marker in listMarkers)
                        elm.remove()
                    listMarkers.clear()
                    listMarkers.add(m as Marker)
                    latitude = it.latitude
                    longitude = it.longitude

                    binding.fabBtnCheck.visibility = View.VISIBLE
                }
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
//                setupPermission()
            }
        } catch (e: SecurityException) {
            Log.i("Line231", e.toString())
        }
    }




}