package com.example.socialmap.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.socialmap.*
import com.example.socialmap.Adapter.MapInfoAdapter
import com.example.socialmap.Constants.AppConstants
import com.example.socialmap.Permission.AppPermission
import com.example.socialmap.R
import com.example.socialmap.ViewModels.LocationViewModel
import com.example.socialmap.ViewModels.ProfileViewModel
import com.example.socialmap.activities.MessageActivity
import com.example.socialmap.activities.UserInfoActivity
import com.example.socialmap.databinding.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ui.IconGenerator
import kotlin.math.min


class MapsFragment : Fragment(),OnMapReadyCallback,GoogleMap.OnMarkerClickListener {


    private lateinit var mMap : GoogleMap

    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    private lateinit var profileViewModels: ProfileViewModel
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var mapMessageDialogLayoutBinding: MapMessageDialogLayoutBinding
    private lateinit var mapOyunTeklifiDialogLayoutBinding: MapOyunTeklifiDialogLayoutBinding
    private lateinit var dialogInfowindowBinding: DialogInfoviewBinding
    private lateinit var acilanOyunTeklifiDialogLayoutBinding : AcilanOyunTeklifiDialogLayoutBinding
    private lateinit var dialog:  AlertDialog

    private lateinit var appPermission: AppPermission

    private lateinit var binding: FragmentMapsBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var databaseReferenceLocation: DatabaseReference
    private lateinit var databaseReferenceLocationMessage: DatabaseReference
    private lateinit var databaseReferenceLocationOyunTeklifi: DatabaseReference

    private lateinit var name : String
    private lateinit var messageLocation : String
    private lateinit var titleOyunIsmi : String
    private lateinit var image : String
    private lateinit var status : String
    private var latlngArrayList : ArrayList<LatLng>? = null

    private lateinit var mapArrayList: ArrayList<LocationModel>

    private lateinit var guncelKonum : LatLng

    private val rotateOpen : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_open_animation) }
    private val rotateClose : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.rotate_close_animation) }
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.from_bottom_animation) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(context,R.anim.to_bottom_animation) }

    private var clicked = false

    var markerWidth = 96
    var markerHeight = 96

    //private lateinit var markerName : String
    //private lateinit var markerStatus : String
    //private lateinit var markerImage : String
    //private lateinit var markerUid : String


    private var mapReady = false

    /*
    private var timeLocation = object : CountDownTimer(5000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            marker.remove()
            konumSil()
        }
    }

    private var timeMessage = object : CountDownTimer(15000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            marker.remove()
            konumSil()
        }
    }

     */

    val etkinlik1 = LatLng(41.011752, 28.782395)
    val etkinlik2 = LatLng(41.011654, 28.774788)
    val etkinlik3 = LatLng(41.005003, 28.773638)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_maps,container,false)

        sharedPreferences = requireContext()!!.getSharedPreferences("MY_INFO", Context.MODE_PRIVATE)

        databaseReferenceLocation = FirebaseDatabase.getInstance().getReference("Location")
        databaseReferenceLocationMessage = FirebaseDatabase.getInstance().getReference("LocationMessage")
        databaseReferenceLocationOyunTeklifi = FirebaseDatabase.getInstance().getReference("LocationOyunTeklifi")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapArrayList = arrayListOf<LocationModel>()

        mapFragment?.getMapAsync{
            googleMap -> mMap = googleMap
            onMapReady(googleMap)
        }


        appPermission = AppPermission()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.bottomBarFab.setOnClickListener {
            fabButtonClicked()
        }

/*
        profileViewModels = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity()!!.application).create(
            ProfileViewModel::class.java)


        profileViewModels.getUser().observe(viewLifecycleOwner, Observer {  userModel ->
            binding.userModel = userModel
            name = userModel.name
            status = userModel.status
            image = userModel.image
        })


 */
        image = sharedPreferences.getString("image","")!!
        name = sharedPreferences.getString("name","")!!
        status = sharedPreferences.getString("status","")!!


        latlngArrayList = ArrayList()


    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0

        try {
            var success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.mapstyle)
            )
            if (!success){
                Log.e("TagMaps","Map stili yuklenirken hata olustu")
            }
        }catch (e: Exception){
            Log.e("TagMaps","Duzgun yuklendi")
        }

        databaseReferenceLocation.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(locationSnapShot in snapshot.children){
                        val location = locationSnapShot.getValue(LocationModel::class.java)

                        checkIfFragmentAttached {
                            Glide.with(requireContext())
                                .asBitmap()
                                .load(location!!.imageUrl.toUri().buildUpon().scheme("https").build())
                                .into(object : CustomTarget<Bitmap>(96,96){
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        val mymarker = mMap.addMarker(MarkerOptions().position(LatLng(ParseDouble(location.latitude),ParseDouble(location.longitude)))
                                            .title("")
                                            .snippet(location.uid)
                                            .alpha(1F)
                                            .icon(
                                                getCroppedBitmap(resource)?.let {
                                                    BitmapDescriptorFactory.fromBitmap(
                                                        it
                                                    )
                                                })
                                        )!!

                                        var timeLocation = object : CountDownTimer(15000, 1000) {

                                            override fun onTick(millisUntilFinished: Long) {
                                            }

                                            override fun onFinish() {
                                                mymarker.remove()
                                                konumSil(location.uid)
                                            }
                                        }
                                        timeLocation.start()

                                        mymarker.tag=0


                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        // this is called when imageView is cleared on lifecycle call or for
                                        // some other reason.
                                        // if you are referencing the bitmap somewhere else too other than this imageView
                                        // clear it here as you can no longer have the bitmap
                                    }
                                })
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        databaseReferenceLocationMessage.addValueEventListener(object : ValueEventListener {


            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(locationSnapShot in snapshot.children){
                        val location = locationSnapShot.getValue(LocationMessageModel::class.java)
/*
                        checkIfFragmentAttached {

                            Glide.with(requireContext())
                                .asBitmap()
                                .load(location!!.imageUrl.toUri().buildUpon().scheme("https").build())
                                .into(object : CustomTarget<Bitmap>(96,96){
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        val myMarker = mMap.addMarker(MarkerOptions().position(LatLng(ParseDouble(location.latitude),ParseDouble(location.longitude)))
                                            .title(location.message)
                                            .snippet(location.uid)
                                            .icon(
                                                getCroppedBitmap(resource)?.let {
                                                    BitmapDescriptorFactory.fromBitmap(
                                                        it
                                                    )
                                                })
                                        )!!



                                        var timeLocation = object : CountDownTimer(15000, 1000) {

                                            override fun onTick(millisUntilFinished: Long) {


                                            }

                                            override fun onFinish() {
                                                myMarker.remove()
                                                mesajSil(location.uid)
                                            }
                                        }
                                        timeLocation.start()


                                        myMarker.tag= 0

                                        val tc = IconGenerator(requireContext())
                                        val bmp = tc.makeIcon(myMarker.title) // pass the text you want.


                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                        // this is called when imageView is cleared on lifecycle call or for
                                        // some other reason.
                                        // if you are referencing the bitmap somewhere else too other than this imageView
                                        // clear it here as you can no longer have the bitmap
                                    }
                                })


                        }

 */
                        checkIfFragmentAttached {
                            val tc = IconGenerator(this)
                            tc.setContentPadding(50,20,50,50)
                            //tc.setStyle(IconGenerator.STYLE_BLUE)
                            tc.setBackground(resources.getDrawable(R.drawable.marker_background))
                            tc.setTextAppearance(R.style.iconGenText)


                            val myMarker = mMap.addMarker(MarkerOptions().position(LatLng(ParseDouble(location!!.latitude),ParseDouble(location.longitude)))
                                .title(location.message)
                                .snippet(location.uid)
                                .alpha(1F)
                            )!!

                            val bmp = tc.makeIcon(myMarker.title) // pass the text you want.
                            myMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp))
                            var timeLocation = object : CountDownTimer(15000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {


                                }

                                override fun onFinish() {
                                    myMarker.remove()
                                    mesajSil(location.uid)
                                }
                            }
                            timeLocation.start()

                            myMarker.tag= 0
                        }



                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        databaseReferenceLocationOyunTeklifi.addValueEventListener(object : ValueEventListener {


            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){
                    for(locationSnapShot in snapshot.children){
                        val location = locationSnapShot.getValue(OyunTeklifiModel::class.java)

                        checkIfFragmentAttached {

                            val myMarker = mMap.addMarker(MarkerOptions().position(LatLng(ParseDouble(location!!.latitude),ParseDouble(location.longitude)))
                                .title(location.oyunIsmi)
                                .snippet(location.uid)
                                .alpha(2F)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.oyun_teklifi))
                            )!!

                            var timeLocation = object : CountDownTimer(15000, 1000) {

                                override fun onTick(millisUntilFinished: Long) {


                                }

                                override fun onFinish() {
                                    myMarker.remove()
                                    oyunTeklifiSil(location.uid)
                                }
                            }
                            timeLocation.start()

                            myMarker.tag= 2
                        }
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(konum: Location) {
                // lokasyon,konum degisince yapilacak islemer

                guncelKonum = LatLng(konum.latitude,konum.longitude)


                binding.fabAddLocation.setOnClickListener {

                    konumEkle(guncelKonum)

                    val map = mapOf(
                        "latitude" to konum.latitude.toString(),
                        "longitude" to konum.longitude.toString(),
                        "imageUrl" to image,
                        "uid" to firebaseAuth!!.uid!!.toString(),
                        "message" to "",
                        "name" to name,
                        "status" to status
                    )
                    LocationModel(konum.latitude.toString(),konum.longitude.toString(),image,firebaseAuth!!.uid!!.toString(),"",name,status)
                    databaseReferenceLocation!!.child(firebaseAuth!!.uid!!.toString()).updateChildren(map)
                    //timeLocation.start()
                }



                //konumEkle(guncelKonum)

                binding.fabAddMessage.setOnClickListener{
                    checkIfFragmentAttached{
                        val alertDialog = AlertDialog.Builder(requireContext())
                        mapMessageDialogLayoutBinding = MapMessageDialogLayoutBinding.inflate(layoutInflater)
                        alertDialog.setView(mapMessageDialogLayoutBinding.root)

                        mapMessageDialogLayoutBinding.btnSendMessage.setOnClickListener {
                            val message = mapMessageDialogLayoutBinding.editTextMessage.text.toString()
                            if (message.isNotEmpty()) {

                                messageLocation = message
                                val map = mapOf(
                                    "latitude" to konum.latitude.toString(),
                                    "longitude" to konum.longitude.toString(),
                                    "imageUrl" to image,
                                    "uid" to firebaseAuth!!.uid!!.toString(),
                                    "message" to message,
                                    "name" to name,
                                    "status" to status
                                )
                                LocationMessageModel(konum.latitude.toString(),konum.longitude.toString(),image,firebaseAuth!!.uid!!.toString(),message,name,status)
                                databaseReferenceLocationMessage!!.child(firebaseAuth!!.uid!!.toString()).updateChildren(map)

                                dialog.dismiss()
                            }
                            mesajEkle(guncelKonum,messageLocation)
                            //timeMessage.start()
                        }
                        dialog = alertDialog.create()
                        dialog.show()


                    }


                }

                binding.fabShowLocation.setOnClickListener {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))
                }
                var baslangicKonum = mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                baslangicKonum.apply {
                    baslangicKonum = mMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
                }


                binding.fabOyunTeklifi.setOnClickListener {
                    checkIfFragmentAttached{
                        val alertDialog = AlertDialog.Builder(requireContext())
                        mapOyunTeklifiDialogLayoutBinding = MapOyunTeklifiDialogLayoutBinding.inflate(layoutInflater)
                        alertDialog.setView(mapOyunTeklifiDialogLayoutBinding.root)

                        mapOyunTeklifiDialogLayoutBinding.fabKabul.setOnClickListener {
                            val oyunIsmi = mapOyunTeklifiDialogLayoutBinding.editTextMessage.text.toString()
                            if (oyunIsmi.isNotEmpty()) {

                                titleOyunIsmi = oyunIsmi
                                val map = mapOf(
                                    "latitude" to konum.latitude.toString(),
                                    "longitude" to konum.longitude.toString(),
                                    "uid" to firebaseAuth!!.uid!!.toString(),
                                    "oyunIsmi" to oyunIsmi,
                                    "imageUrl" to image
                                )
                                OyunTeklifiModel(konum.latitude.toString(),konum.longitude.toString(),firebaseAuth!!.uid.toString(),oyunIsmi,image)
                                databaseReferenceLocationOyunTeklifi!!.child(firebaseAuth!!.uid!!.toString()).updateChildren(map)

                                dialog.dismiss()
                            }
                            oyunTeklifiEkle(guncelKonum,oyunIsmi)
                            //timeMessage.start()
                        }
                        dialog = alertDialog.create()
                        dialog.show()

                        mapOyunTeklifiDialogLayoutBinding.fabIptal.setOnClickListener {
                            dialog.dismiss()
                        }

                    }
                }

                /*
                mMap.addMarker(MarkerOptions().position(latlngArrayList!!.get(0)).title("etkinlik 1").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.joystick24)
                ))
                mMap.addMarker(MarkerOptions().position(latlngArrayList!!.get(1)).title("etkinlik 2").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.party36)
                ))
                mMap.addMarker(MarkerOptions().position(latlngArrayList!!.get(2)).title("etkinlik 3").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.joystick48)
                ))


                 */

                checkIfFragmentAttached {
                    mMap.setInfoWindowAdapter(MapInfoAdapter(requireContext()))
                }


                mMap.setOnMarkerClickListener(this@MapsFragment)

                //mMap.uiSettings.isZoomGesturesEnabled = false

            }

            override fun onProviderDisabled(@NonNull provider: String) {

            }

            override fun onProviderEnabled(@NonNull provider: String) {

            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

        }

        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),AppConstants.LOCATION_PERMISSION)
        }else{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
        }

    }

    fun ParseDouble(strNumber: String?): Double {
        return if (strNumber != null && strNumber.length > 0) {
            try {
                return strNumber.toDouble()
            } catch (e: java.lang.Exception) {
                (-1).toDouble() // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else {
            return 0.0
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == AppConstants.LOCATION_PERMISSION){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1f,locationListener)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("LogNotTimber")
    override fun onMarkerClick(mymarker: Marker): Boolean {

        if (mymarker.alpha == 1F){
            val infoWindow = AlertDialog.Builder(requireContext())
            dialogInfowindowBinding = DialogInfoviewBinding.inflate(layoutInflater)
            infoWindow.setView(dialogInfowindowBinding.root)


            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mymarker.snippet.toString())
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)
                        dialogInfowindowBinding.infoName.text = userModel!!.name
                        dialogInfowindowBinding.infoStatus.text = userModel!!.status
                        //Picasso.get().load(userModel.image).into(dialogInfowindowBinding.infoImageView)
                        Glide.with(requireContext()).load(userModel.image).into(dialogInfowindowBinding.infoImageView)

                        dialogInfowindowBinding.infoButton.setOnClickListener {
                            val intent = Intent(it.context, UserInfoActivity::class.java)
                            intent.putExtra("hisId",mymarker.snippet.toString())
                            it.context.startActivity(intent)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            dialog = infoWindow.create()
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.show()
        }

        if(mymarker.alpha == 2F){
            val acilanTeklif = AlertDialog.Builder(requireContext())
            acilanOyunTeklifiDialogLayoutBinding = AcilanOyunTeklifiDialogLayoutBinding.inflate(layoutInflater)
            acilanTeklif.setView(acilanOyunTeklifiDialogLayoutBinding.root)


                FirebaseDatabase.getInstance().getReference("LocationOyunTeklifi").child(mymarker.snippet.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val oyunTeklifiModel = snapshot.getValue(OyunTeklifiModel::class.java)
                                acilanOyunTeklifiDialogLayoutBinding.textAcilanTeklif.text = "${oyunTeklifiModel!!.oyunIsmi} oyun teklifini kabul ediyor musun?"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

            acilanOyunTeklifiDialogLayoutBinding.fabTeklifKabul.setOnClickListener {
                FirebaseDatabase.getInstance().getReference("Users").child(mymarker.snippet.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val userModel = snapshot.getValue(UserModel::class.java)

                                    val intent = Intent(it.context, MessageActivity::class.java)
                                    intent.putExtra("hisId",userModel!!.uid)
                                    intent.putExtra("hisImage",userModel.image)
                                    it.context.startActivity(intent)

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                dialog.dismiss()
            }



            acilanOyunTeklifiDialogLayoutBinding.fabTeklifRet.setOnClickListener {
                dialog.dismiss()
            }


            dialog = acilanTeklif.create()
            dialog.show()
        }

        //mymarker.hideInfoWindow()




        return false
    }


    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    private fun getCircularBitmap(srcBitmap: Bitmap?): Bitmap {
        val squareBitmapWidth = min(srcBitmap!!.width, srcBitmap.height)
        // Initialize a new instance of Bitmap
        // Initialize a new instance of Bitmap
        val dstBitmap = Bitmap.createBitmap(
            squareBitmapWidth,  // Width
            squareBitmapWidth,  // Height
            Bitmap.Config.ARGB_8888 // Config
        )
        val canvas = Canvas(dstBitmap)
        // Initialize a new Paint instance
        // Initialize a new Paint instance
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, squareBitmapWidth, squareBitmapWidth)
        val rectF = RectF(rect)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        // Calculate the left and top of copied bitmap
        // Calculate the left and top of copied bitmap
        val left = ((squareBitmapWidth - srcBitmap.width) / 2).toFloat()
        val top = ((squareBitmapWidth - srcBitmap.height) / 2).toFloat()
        canvas.drawBitmap(srcBitmap, left, top, paint)
        // Free the native object associated with this bitmap.
        // Free the native object associated with this bitmap.
        srcBitmap.recycle()
        // Return the circular bitmap
        // Return the circular bitmap
        return dstBitmap
    }

    private fun konumEkle(guncelKonum : LatLng){
        checkIfFragmentAttached {

            Glide.with(requireContext())
                .asBitmap()
                .load(image.toUri().buildUpon().scheme("https").build())
                .into(object : CustomTarget<Bitmap>(96,96){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val marker = mMap.addMarker(MarkerOptions().position(guncelKonum).title("").icon(
                            getCroppedBitmap(resource)?.let {
                                BitmapDescriptorFactory.fromBitmap(
                                    it
                                )
                            }))!!
                        marker.tag=0
                        marker.remove()

                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })

        }
    }

    private fun mesajEkle(guncelKonum: LatLng,mesaj: String){
        checkIfFragmentAttached {

            Glide.with(requireContext())
                .asBitmap()
                .load(image.toUri().buildUpon().scheme("https").build())
                .into(object : CustomTarget<Bitmap>(96,96){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val marker = mMap.addMarker(MarkerOptions().position(guncelKonum).title(mesaj).icon(
                            getCroppedBitmap(resource)?.let {
                                BitmapDescriptorFactory.fromBitmap(
                                    it
                                )
                            }))!!
                        marker.showInfoWindow()
                        marker.remove()
                        marker.tag=1

                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })


        }
    }


    private fun oyunTeklifiEkle(guncelKonum: LatLng,oyunIsmi: String){
        checkIfFragmentAttached {

            val marker = mMap.addMarker(MarkerOptions()
                .position(guncelKonum)
                .title(oyunIsmi)
            )

            marker!!.remove()
            marker.tag=2





        }
    }

    private fun konumSil(uid:String){

        val locationReference = FirebaseDatabase.getInstance().getReference("Location").child(uid)

        locationReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (locationSnapshot in dataSnapshot.children) {
                    locationSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    private fun mesajSil(uid:String){

        val locationReference = FirebaseDatabase.getInstance().getReference("LocationMessage").child(uid)

        locationReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (locationSnapshot in dataSnapshot.children) {
                    locationSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    private fun oyunTeklifiSil(uid:String){

        val locationReference = FirebaseDatabase.getInstance().getReference("LocationOyunTeklifi").child(uid)

        locationReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (locationSnapshot in dataSnapshot.children) {
                    locationSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }
    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

    private fun fabButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked:Boolean) {

        if (!clicked){
            //binding.fabShowLocation.visibility = View.VISIBLE
            binding.fabAddLocation.visibility = View.VISIBLE
            binding.fabAddMessage.visibility = View.VISIBLE
            binding.fabOyunTeklifi.visibility = View.VISIBLE
        }else{
            //binding.fabShowLocation.visibility = View.INVISIBLE
            binding.fabAddLocation.visibility = View.INVISIBLE
            binding.fabAddMessage.visibility = View.INVISIBLE
            binding.fabOyunTeklifi.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked:Boolean) {

        if (!clicked){
            //binding.fabShowLocation.startAnimation(fromBottom)
            binding.fabAddLocation.startAnimation(fromBottom)
            binding.fabAddMessage.startAnimation(fromBottom)
            binding.fabOyunTeklifi.startAnimation(fromBottom)
            binding.bottomBarFab.startAnimation(rotateOpen)
        }else{
            //binding.fabShowLocation.startAnimation(toBottom)
            binding.fabAddLocation.startAnimation(toBottom)
            binding.fabAddMessage.startAnimation(toBottom)
            binding.fabOyunTeklifi.startAnimation(toBottom)
            binding.bottomBarFab.startAnimation(rotateClose)
        }

    }

}




