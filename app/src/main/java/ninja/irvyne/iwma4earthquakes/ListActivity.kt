package ninja.irvyne.iwma4earthquakes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_list.*
import ninja.irvyne.iwma4earthquakes.api.EarthquakeService
import ninja.irvyne.iwma4earthquakes.api.model.EarthquakeData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ListActivity : AppCompatActivity() {

    private lateinit var mService: EarthquakeService
    private lateinit var mExtraMagnitude: String
    private lateinit var mExtraTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        mExtraMagnitude = intent.getStringExtra(EXTRA_MAGNITUDE) ?: "all"
        mExtraTime = intent.getStringExtra(EXTRA_TIME) ?: "day"

        mService = Retrofit.Builder().apply {
            baseUrl("https://earthquake.usgs.gov/")
            addConverterFactory(GsonConverterFactory.create())
            client(
                    OkHttpClient.Builder()
                            .addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.HEADERS
                            })
                            .build()
            )
        }.build().create(EarthquakeService::class.java)

        listRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ListActivity)
        }

        mService.listEarthquakes(magnitude = mExtraMagnitude, time = mExtraTime).enqueue(object : Callback<EarthquakeData> {
            override fun onFailure(call: Call<EarthquakeData>?, t: Throwable?) {
                Log.e(TAG, "response failed!")
                if (t != null) throw t //IDEM: t?.let { throw it }
            }

            override fun onResponse(call: Call<EarthquakeData>?, response: Response<EarthquakeData>?) {
                Log.d(TAG, response?.body().toString())

                listRecyclerView.adapter = EarthquakeAdapter(response?.body()?.features as ArrayList)
            }
        })

    }

    companion object {
        private const val TAG = "ListActivity"
        const val EXTRA_MAGNITUDE = "magnitude"
        const val EXTRA_TIME = "time"
    }
}
