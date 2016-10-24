package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        mForecastAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList());

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getActivity(), mForecastAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(getActivity(), "teste", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }



    private String getLocationOnMap() {

        final String OWM_CITY = "city";
        final String OWM_COORD = "coord";

        try {
            JSONObject forecastJson = new JSONObject("{\"city\":{\"id\":3448636,\"name\":\"Sao Jose dos Campos\",\"coord\":{\"lon\":-45.88694,\"lat\":-23.17944},\"country\":\"BR\",\"population\":0},\"cod\":\"200\",\"message\":0.0535,\"cnt\":7,\"list\":[{\"dt\":1473948000,\"temp\":{\"day\":16,\"min\":11.64,\"max\":16,\"night\":11.64,\"eve\":16,\"morn\":16},\"pressure\":896.76,\"humidity\":75,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"speed\":1.61,\"deg\":146,\"clouds\":0},{\"dt\":1474034400,\"temp\":{\"day\":24.18,\"min\":10.12,\"max\":24.7,\"night\":11.54,\"eve\":21.08,\"morn\":10.12},\"pressure\":897.65,\"humidity\":49,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"speed\":1.96,\"deg\":135,\"clouds\":32},{\"dt\":1474120800,\"temp\":{\"day\":27.6,\"min\":11.66,\"max\":27.6,\"night\":12.74,\"eve\":20.49,\"morn\":11.66},\"pressure\":897.63,\"humidity\":44,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":3.86,\"deg\":331,\"clouds\":0,\"rain\":1.23},{\"dt\":1474207200,\"temp\":{\"day\":27,\"min\":14.91,\"max\":31.4,\"night\":19.76,\"eve\":31.4,\"morn\":14.91},\"pressure\":929.84,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.73,\"deg\":22,\"clouds\":0,\"rain\":10.01},{\"dt\":1474293600,\"temp\":{\"day\":22.85,\"min\":17.59,\"max\":24.05,\"night\":17.66,\"eve\":24.05,\"morn\":17.59},\"pressure\":928.53,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.17,\"deg\":348,\"clouds\":38,\"rain\":5.9},{\"dt\":1474380000,\"temp\":{\"day\":16.4,\"min\":12.34,\"max\":19.6,\"night\":12.34,\"eve\":19.6,\"morn\":15.66},\"pressure\":931.08,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2,\"deg\":144,\"clouds\":90,\"rain\":1.93},{\"dt\":1474466400,\"temp\":{\"day\":14.96,\"min\":6.52,\"max\":20.07,\"night\":12.41,\"eve\":20.07,\"morn\":6.52},\"pressure\":933.59,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.48,\"deg\":168,\"clouds\":25,\"rain\":0.6}]}");
            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            JSONObject coord = cityJson.getJSONObject(OWM_COORD);

            String longitude = coord.getString("lon");
            String latitude = coord.getString("lat");


            Log.v("MAPS URI:","geo:" + latitude + "," + longitude + "?z=14");
            return "geo:" + latitude + "," + longitude + "?z=11";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "error";

    }

    private void updateWeather() {
        Toast.makeText(getActivity(), "Atualizando", Toast.LENGTH_SHORT).show();
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getActivity(), mForecastAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cityId = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        fetchWeatherTask.execute(cityId);
    }





}