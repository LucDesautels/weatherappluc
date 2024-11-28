import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var cityTextView: TextView
    private lateinit var tempTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var weatherService: WeatherService
    private val apiKey = "2129e41d236f3c2ba2d13dda07966072"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        citySpinner = findViewById(R.id.citySpinner)
        cityTextView = findViewById(R.id.cityTextView)
        tempTextView = findViewById(R.id.tempTextView)
        humidityTextView = findViewById(R.id.humidityTextView)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherService = retrofit.create(WeatherService::class.java)

        val cities = arrayOf("Toronto", "New York", "London", "Paris", "Tokyo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedCity = parent.getItemAtPosition(position) as String
                fetchWeatherData(selectedCity)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun fetchWeatherData(cityName: String) {
        weatherService.getCurrentWeather(cityName, apiKey, "metric").enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val weatherResponse = response.body()!!
                    cityTextView.text = "City: ${weatherResponse.name}"
                    tempTextView.text = "Temperature: ${weatherResponse.main.temp}°C"
                    humidityTextView.text = "Humidity: ${weatherResponse.main.humidity}%"
                } else {
                    cityTextView.text = "City: "
                    tempTextView.text = "Temperature: "
                    humidityTextView.text = "Humidity: "
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                cityTextView.text = "City: "
                tempTextView.text = "Temperature: "
                humidityTextView.text = "Humidity: "
            }
        })
    }
}
