package com.example.sisko.runforhelp;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by sisko on 15-11-7.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private Context mContext;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
    private LocationRequest locationRequest;
    private Location stopPosition, startPosition, currentLocation;

    private Button mStart;
    private Button mStop;
    private TextView mRunnedMeters;

    double longitude = 0;
    double latitude = 0;
    double x,y;
    float currentDistance = 0;

    boolean trackChanges = false;


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // inflate and return the layout
        View v = inflater.inflate(R.layout.map_fragment, container,
                false);

        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        // Need to get the map to display at once
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mGoogleMap = mMapView.getMap();
        mGoogleMap.setMyLocationEnabled(true);

        //LocationRequest
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(16);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

        //Initialize map elements
        mStart = (Button) v.findViewById(R.id.start_button);
        mStop = (Button) v.findViewById(R.id.stop_button);
        mRunnedMeters = (TextView) v.findViewById(R.id.runned_meters);

        mStart.setOnClickListener(this);
        mStop.setOnClickListener(this);

        return v;
    }



    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public MapFragment() {
    }

    public static final String ARG_SECTION_NUMBER = "section_number";

    public static MapFragment newInstance(int sectionNumber) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onLocationChanged(Location location) {

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        if(trackChanges = true && currentLocation != null){
            currentDistance += location.distanceTo(currentLocation);
        }
        currentLocation = location;

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    @Override
    public void onConnected(Bundle bundle) {

        fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.start_button:

                trackChanges = true;
                currentDistance = 0;
                mRunnedMeters.setText(String.format("%.1f m", currentDistance));

                //Get start position
                startPosition = currentLocation;

                //Create marker
                MarkerOptions markerStart = new MarkerOptions().position(
                        new LatLng(latitude, longitude));

                //Changing marker icon
                markerStart.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                //Adding marker
                mGoogleMap.addMarker(markerStart);
                CameraPosition cameraPositionStart = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(15).build();
                mGoogleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPositionStart));

                //Swap Buttons
                mStart.setVisibility(v.GONE);
                mStop.setVisibility(v.VISIBLE);




            break;
            case R.id.stop_button:

                trackChanges = false;

                //Get stop position
                stopPosition = currentLocation;

                // create marker
                MarkerOptions markerStop = new MarkerOptions().position(
                        new LatLng(latitude, longitude));

                // Changing marker icon
                markerStop.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                // adding marker
                mGoogleMap.addMarker(markerStop);
                CameraPosition cameraPositionStop = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(15).build();
                mGoogleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPositionStop));

                //Swap Buttons
                mStop.setVisibility(v.GONE);
                mStart.setVisibility(v.VISIBLE);

                //Calculate meters
                mRunnedMeters.setText(String.format("%.1f m", currentDistance));

            break;

        }
    }
}
