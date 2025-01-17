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

public class ReverseGeocodingFragment extends Fragment {

    private EditText editTextLatitude, editTextLongitude;
    private TextView textViewAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reverse_geocoding, container, false);

        editTextLatitude = view.findViewById(R.id.editTextLatitude);
        editTextLongitude = view.findViewById(R.id.editTextLongitude);
        Button buttonReverseGeocode = view.findViewById(R.id.buttonReverseGeocode);
        textViewAddress = view.findViewById(R.id.textViewAddress);

        buttonReverseGeocode.setOnClickListener(v -> performReverseGeocoding());

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void performReverseGeocoding() {
        String latitudeStr = editTextLatitude.getText().toString();
        String longitudeStr = editTextLongitude.getText().toString();

        if (!latitudeStr.isEmpty() && !longitudeStr.isEmpty()) {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);

            Geocoder geocoder = new Geocoder(requireActivity());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    textViewAddress.setText("Address: " + addresses.get(0).getAddressLine(0));
                } else {
                    textViewAddress.setText("Address not found");
                }
            } catch (IOException e) {
                textViewAddress.setText("Error: " + e.getMessage());
            }
        } else {
            textViewAddress.setText("Please enter both latitude and longitude");
        }
    }
}
