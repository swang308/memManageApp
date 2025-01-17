package com.example.app;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class ForwardGeocodingFragment extends Fragment {

    private EditText editTextStreet, editTextCity, editTextCountry;
    private TextView textViewCoordinates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forward_geocoding, container, false);

        editTextStreet = view.findViewById(R.id.editTextStreet);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextCountry = view.findViewById(R.id.editTextCountry);
        Button buttonGeocode = view.findViewById(R.id.buttonGeocode);
        textViewCoordinates = view.findViewById(R.id.textViewCoordinates);

        buttonGeocode.setOnClickListener(v -> performGeocoding());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void performGeocoding() {
        String address = editTextStreet.getText().toString() + ", " +
                editTextCity.getText().toString() + ", " +
                editTextCountry.getText().toString();

        Geocoder geocoder = new Geocoder(requireActivity());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                textViewCoordinates.setText("Coordinates: " + location.getLatitude() + ", " + location.getLongitude());
            } else {
                textViewCoordinates.setText("Address not found");
            }
        } catch (IOException e) {
            textViewCoordinates.setText("Error: " + e.getMessage());
        }
    }
}
