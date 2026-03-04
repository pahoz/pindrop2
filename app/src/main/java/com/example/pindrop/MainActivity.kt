package com.example.pindrop

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.annotation.SymbolManager
import org.maplibre.android.plugins.annotation.SymbolOptions
import android.content.Intent
import android.net.Uri

class MainActivity : ComponentActivity() {

    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)

        setContent {
            val context = LocalContext.current
            var currentLat by remember { mutableStateOf<Double?>(null) }
            var currentLon by remember { mutableStateOf<Double?>(null) }
            var pinName by remember { mutableStateOf("") }
            var showNameDialog by remember { mutableStateOf(false) }

            // Paylaş için seçilen pin bilgileri
            var selectedPinLat by remember { mutableStateOf<Double?>(null) }
            var selectedPinLon by remember { mutableStateOf<Double?>(null) }
            var selectedPinName by remember { mutableStateOf("") }
            var showShareDialog by remember { mutableStateOf(false) }

            val mapViewLocal = remember { MapView(context) }
            mapView = mapViewLocal

            mapViewLocal.getMapAsync { map ->
                map.setStyle("https://api.maptiler.com/tiles/satellite-v2/tiles.json?key=Q6me04Vmm87HqXaAcWZ0") { style ->
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(39.0, 35.0))
                        .zoom(6.0)
                        .build()
                }
            }

            // İsim girme dialogu
            if (showNameDialog) {
                AlertDialog(
                    onDismissRequest = { showNameDialog = false },
                    title = { Text("Pin Adı Ver") },
                    text = {
                        TextField(
                            value = pinName,
                            onValueChange = { pinName = it },
                            label = { Text("Pin adı (ör: Ev, İş)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (currentLat != null && currentLon != null && pinName.isNotBlank()) {
                                Toast.makeText(context, "Pin eklendi: $pinName ($currentLat, $currentLon)", Toast.LENGTH_SHORT).show()
                                // Gerçek pin ekleme burada basit Toast ile simüle edildi
                                // İleride Canvas veya başka yöntem ekleriz
                            }
                            pinName = ""
                            showNameDialog = false
                        }) {
                            Text("Ekle")
                        }
                    }
                            }
                            pinName = ""
                            showNameDialog = false
                        }) {
                            Text("Ekle")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNameDialog = false }) {
                            Text("İptal")
                        }
                    }
                )
            }

            // Paylaş dialogu
            if (showShareDialog && selectedPinLat != null && selectedPinLon != null) {
                AlertDialog(
                    onDismissRequest = { showShareDialog = false },
                    title = { Text("Paylaş") },
                    text = { Text("${selectedPinName.ifBlank { "Pin" }} konumunu paylaşmak ister misin?") },
                    confirmButton = {
                        TextButton(onClick = {
                            val locationText = "${selectedPinName.ifBlank { "Konum" }}: https://www.google.com/maps?q=$selectedPinLat,$selectedPinLon"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, locationText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Konumu paylaş"))
                            showShareDialog = false
                        }) {
                            Text("Paylaş")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showShareDialog = false }) {
                            Text("İptal")
                        }
                    }
                )
            }

            // UI
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { mapViewLocal },
                    modifier = Modifier.fillMaxSize()
                )

                Button(
                    onClick = {
                        if (currentLat != null && currentLon != null) {
                            val uri = Uri.parse("google.navigation:q=$currentLat,$currentLon&mode=d")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Google Maps yüklü değil", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Haritadan konum seçin", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Text("Yol Tarifi")
                }
            }
        }
    }

    // Lifecycle
    override fun onStart() { super.onStart(); mapView?.onStart() }
    override fun onResume() { super.onResume(); mapView?.onResume() }
    override fun onPause() { super.onPause(); mapView?.onPause() }
    override fun onStop() { super.onStop(); mapView?.onStop() }
    override fun onLowMemory() { super.onLowMemory(); mapView?.onLowMemory() }
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}