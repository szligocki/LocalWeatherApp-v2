package sz.pl.localweatherapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sz.pl.localweatherapp.db.City;
import sz.pl.localweatherapp.dto.city.WeatherMapDto;
import sz.pl.localweatherapp.service.impl.RestServiceImpl;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.city_field) TextView cityField;
    @BindView(R.id.updated_field) TextView updatedField;
    @BindView(R.id.current_temperature_field) TextView currentTemperatureField;
    @BindView(R.id.weather_icon) ImageView weatherIcon;
    @BindView(R.id.description) TextView descriptiopn;
    @BindView(R.id.humidity) TextView humidity;
    @BindView(R.id.pressure) TextView pressure;

    /**
     * Instance of the rest management service
     */
    private RestServiceImpl restService = new RestServiceImpl();

    /**
     * The onCreate method initializes the list
     *
     * @param savedInstanceState {@link Bundle}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        getWeatherInformation();
    }


    /**
     * Method Fills the TextView with data from the server
     */
    private void getWeatherInformation() {
        restService.callForWeather(getIntent().getLongExtra(City.KEY_CITY_ID, 0)).enqueue(new Callback<WeatherMapDto>() {
            @Override
            public void onResponse(@NonNull Call<WeatherMapDto> call, @NonNull Response<WeatherMapDto> response) {
                if (response.isSuccessful()) {
                    WeatherMapDto dto = response.body();
                    try {
                        assert dto != null;
                        cityField.setText(dto.getName());
                        updatedField.setText(DateFormat.getDateTimeInstance().format(new Date((long) (dto.getDt() * 1000))));
                        weatherIcon.setImageResource(dto.getIcon());
                        descriptiopn.setText(dto.getWeather().getDescription());
                        humidity.setText(String.format("%s%%", getString(R.string.humidity) + dto.getMain().getHumidity()));
                        pressure.setText(String.format("%s", getString(R.string.pressure) + dto.getMain().getPressure()));
                        currentTemperatureField.setText(String.format("%s " + getString(R.string.celsius), dto.getMain().getTemp()));

                    } catch (NullPointerException ex) {
                        handleError();
                    }
                } else {
                    onFailure(call, new NullPointerException());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherMapDto> call, @NonNull Throwable t) {
                handleError();
            }
        });
    }

    /**
     * Support for the retrofit error
     */
    private void handleError() {
        Toast.makeText(getApplicationContext(), getString(R.string.get_weather_information_error), Toast.LENGTH_LONG).show();
        finish();
    }
}
