package com.example.trackerminapi24

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackerminapi24.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

/**
 * The MapsActivity.kt file instantiates the SupportMapFragment in the onCreate() method
 * and uses the class's getMapAsync() to automatically initialize the maps system and the view.
 * The activity that contains the SupportMapFragment must implement the OnMapReadyCallback interface
 * and that interface's onMapReady() method. The onMapReady() method is called when the map is loaded.
 **/
/**
 * Manipulates the map once available.
 * This callback is triggered when the map is ready to be used.
 * This is where we can add markers or lines, add listeners or move the camera.
 * In this case, we just add a marker near Sydney, Australia.
 * If Google Play services is not installed on the device, the user will be prompted to
 * install it inside the SupportMapFragment. This method will only be triggered once the
 * user has installed Google Play services and returned to the app.
 */
class MapsFragment : Fragment(), OnMapReadyCallback {
    private val TAG = MapsFragment::class.java.simpleName
    private val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmentMapsBinding
    private val REQUEST_FOREGROUND_LOCATION_PERMISSION = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-34.0, 151.0)
        val myHome = LatLng(29.975507526586643, 31.40644697381402)

        map.apply {
            addMarker(MarkerOptions().position(myHome).title("Marker at 30 Marrket"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(myHome, 16f))
        }

        setMapStyle(map)
        setMapLongClick(map)
        setPoiClick(map)
        checkPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_menu, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Change the map type based on the user's selection.
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    // when you click on the marked location, show these details.
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
        }
    }
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(MarkerOptions().position(poi.latLng).title(poi.name).rotation(90f))
            poiMarker?.showInfoWindow()
        }
    }
    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined in a raw resource file.
            val success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_1))

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkPermission() { //foregroundLocationPermissionApproved
        if(foregroundLocationPermissionApproved())
            map.isMyLocationEnabled = true      //show the user location's icon.
        else
            requestForegroundLocationPermission()
    }

    private fun foregroundLocationPermissionApproved(): Boolean {
        return(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                && PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun requestForegroundLocationPermission() {
        if(foregroundLocationPermissionApproved())
            return

        val permissionsArray = PERMISSIONS
        val resultCode = REQUEST_FOREGROUND_LOCATION_PERMISSION
        requestPermissions(permissionsArray, resultCode)
    }

    // This function is being called after each fragment dialog of a permission finished.
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_FOREGROUND_LOCATION_PERMISSION)
            checkPermission()
    }

}