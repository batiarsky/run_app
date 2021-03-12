package com.friends.runchamp.view.start

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.friends.runchamp.R
import com.friends.runchamp.activityService.ActivityService
import com.friends.runchamp.activityService.ActivityServiceImpl
import com.friends.runchamp.activityService.ActivityServiceImpl.Companion.ACTIVITY_DATA
import com.friends.runchamp.entity.RunningData
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.start_screen_layout.*
import org.koin.android.ext.android.inject


class StartFragment : Fragment(), OnMapReadyCallback {

    private var mIsLocationEnabled = false
    private var mIsSatelliteTrue = false

    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 23
        private const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACTIVITY_DATA_ACTION = "ACTIVITY_DATA_ACTION"
    }

    private val mViewModel by inject<StartViewModel>()
    private lateinit var mMap: GoogleMap
    private var mActivityService: ActivityService? = null
    private var mBound = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as ActivityServiceImpl.ActivityServiceBinder
            mActivityService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.start_screen_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(mViewModel)
        getLocationPermission()
        mViewModel.prepareToWork()
        subscribeToLiveData()
        initListeners()

        requireContext().registerReceiver(MyBroadcastReceiver(), IntentFilter(ACTIVITY_DATA_ACTION))
    }

    private fun subscribeToLiveData() {
        mViewModel.mIsActivityStartedLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                start_start_btn.isVisible = false
                onVisibleLockButtons()
                Intent(requireContext(), ActivityServiceImpl::class.java).also { intent ->
                    requireContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
                }
                mActivityService?.getActivityData()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false

        mViewModel.getDeviceLocation()
        mViewModel.setGoogleMap(mMap)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            (childFragmentManager.findFragmentById(R.id.start_map) as SupportMapFragment).getMapAsync(
                this
            )
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty()) {
                    for (i in 0..grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return
                        }
                    }
                    (childFragmentManager.findFragmentById(R.id.start_map) as SupportMapFragment).getMapAsync(
                        this
                    )
                    mIsLocationEnabled = true
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        mViewModel.isActivityTrackingStarted()
    }

    override fun onStop() {
        super.onStop()

        if (mBound) {
            requireActivity().unbindService(mConnection)
            mBound = false
        }
    }

    private fun initListeners() {
        start_start_btn.setOnClickListener {
            start_start_btn.isVisible = false
            onVisibleLockButtons()
            startActivityService()
            mViewModel.onStartClicked()
        }

        start_pause_btn.setOnClickListener {
            onPauseClicked()
            mViewModel.onPauseClicked()
        }

        start_continue_btn.setOnClickListener {
            onContinueClicked()
            mViewModel.onContinueClicked()
        }

        start_stop_btn.setOnClickListener {
            onInvisibleLockButtons()
            mActivityService?.onStopActivityService()
            requireContext().unbindService(mConnection)
            mViewModel.onStopClicked()
        }

        start_lock_btn.setOnClickListener {
            onLockClicked()
        }

        start_unlock_btn.setOnClickListener {
            onUnlockClicked()
        }

        satelliteBtn.setOnClickListener {
            if (!mIsSatelliteTrue) {
                mMap.mapType = MAP_TYPE_SATELLITE
                mIsSatelliteTrue = true
            } else {
                mMap.mapType = MAP_TYPE_NORMAL
                mIsSatelliteTrue = false
            }
        }

        locationBtn.setOnClickListener {
            mViewModel.onShowMyLocationClicked()
        }
    }

    private fun startActivityService() {
        Intent(requireContext(), ActivityServiceImpl::class.java).also { intent ->
            requireContext().startService(intent)
            requireContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun onContinueClicked() {
        onVisibleLockButtons()
        start_continue_btn.isVisible = false
    }

    private fun onVisibleLockButtons() {
        start_pause_btn.isEnabled = false
        start_stop_btn.isEnabled = false
        start_pause_btn.isVisible = true
        start_stop_btn.isVisible = true
        start_unlock_btn.isVisible = true
        start_pause_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
        start_stop_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
    }

    private fun onInvisibleLockButtons() {
        start_pause_btn.isVisible = false
        start_continue_btn.isVisible = false
        start_stop_btn.isVisible = false
        start_lock_btn.isVisible = false
        start_start_btn.isVisible = true
    }

    private fun onPauseClicked() {
        start_pause_btn.isVisible = false
        start_continue_btn.isVisible = true
    }

    private fun onUnlockClicked() {
        start_pause_btn.isEnabled = true
        start_stop_btn.isEnabled = true
        start_continue_btn.isEnabled = true
        start_lock_btn.isVisible = true
        start_unlock_btn.isVisible = false
        start_pause_btn.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        )
        start_stop_btn.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        )
        start_continue_btn.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        )
    }

    private fun onLockClicked() {
        start_pause_btn.isEnabled = false
        start_stop_btn.isEnabled = false
        start_continue_btn.isEnabled = false
        start_unlock_btn.isVisible = true
        start_lock_btn.isVisible = false
        start_stop_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
        start_pause_btn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
        start_continue_btn.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.grey)
        )
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val activityData = intent.getParcelableExtra<RunningData>(ACTIVITY_DATA)
            val distance = activityData?.distance.toString()
            val activityTime = activityData?.activityTime.toString()
            val burnedCalories = activityData?.burnedCalories?.toInt().toString()
            val paceString = activityData?.paceString

            distance_text?.text = getString(R.string.start_fragment_distance_text, distance)
            activityTime_text?.text = getString(R.string.start_fragment_duration_text, activityTime)
            burnedCalories_text?.text =
                getString(R.string.start_fragment_burned_calories_text, burnedCalories)
            pace_text?.text =
                getString(R.string.start_fragment_pace_text, paceString)
        }
    }
}




