package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
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
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cityId = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        fetchWeatherTask.execute(cityId);
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        // Retorna a data atual em formato simples
        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        // Retorna MAX e MIN arrendodados e formatados
        private String formatHighLows(double high, double low, String metrics) {
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            if (metrics.equals("celsius")) {
                return roundedHigh + "ºC / " + roundedLow + "ºC";
            } else if (metrics.equals("fahrenheit")) {
                roundedHigh = Math.round(roundedHigh * 1.8 + 32);
                roundedLow = Math.round(roundedLow * 1.8 + 32);
                return roundedHigh + "ºF / " + roundedLow + "ºF";
            }
            return "err-convertion";
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_CITY = "city";
            final String OWM_COORD = "coord";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            dayTime = new Time();

            String[] resultStrs = new String[numDays];


            // Colocar a posição gps da cidade no SharedPreferences

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            JSONObject coord = cityJson.getJSONObject(OWM_COORD);

            String longitude = coord.getString("lon");
            String latitude = coord.getString("lat");

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("LOCATION_GPS", latitude + "," + longitude).apply();

            for (int i = 0; i < weatherArray.length(); i++) {

                String day;
                String description;
                String highAndLow;

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                long dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = getReadableDateString(dateTime);

                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String metrics = prefs.getString(getString(R.string.pref_metrics_key), getString(R.string.pref_metrics_default));
                highAndLow = formatHighLows(high, low, metrics);





                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }

            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            //Se o código da cidade estiver vazio, não há necessidade de verificar nada
            if (params.length == 0)
                return null;

            // Pegar dados da API openweathermap
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Variável que conterá os dados Json
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String key = "fbda110b5af45d9754e36d29d3c2cf63";
            int numDays = 7;

            try {

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "id";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String API_KEY = "appid";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(API_KEY, key).build();

                Log.v(LOG_TAG, "URI: " + builtUri);

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Ler o stream de entrada para a String

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Se o stream estiver vazio,
                    return null;
                }

                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + forecastJsonStr);
                ;


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error ao fechar Stream", e);
                    }
                }
            }

            try {
                //mForecastAdapter.add(getWeatherDataFromJson(forecastJsonStr, numDays));
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }
    }


}