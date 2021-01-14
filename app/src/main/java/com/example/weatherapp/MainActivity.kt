package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.MainActivity.MyAsyncTask
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Stream
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        tvRes.text=" "
        button.isEnabled = true
    }

    inner class GetResFromApi:AsyncTask<String,String,String>{
        var context:Context?=null
        constructor(context: Context){
            this.context=context
        }
        override fun doInBackground(vararg params: String?): String {
            try{
                val url = URL(params[0])
                val connect = url.openConnection() as HttpsURLConnection
                connect.connectTimeout = 5000

                var dataString = ConvertToString(connect.inputStream)
                publishProgress(dataString)
            }catch (ex:Exception){}
            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {

            var days = arrayListOf<ArrayList<String>>()
            try {
                val json =JSONObject(values[0])
                val consolidated_weather = json.getJSONArray("consolidated_weather")

                for(i in 0..5){
                    var day= arrayListOf<String>()
                    val today= consolidated_weather.getJSONObject(i)
                    day.add(today.getString("applicable_date").toString())
                    day.add(today.getString("weather_state_name").toString())
                    day.add(today.getString("min_temp").toString())
                    day.add(today.getString("max_temp").toString())
                    Log.d("DAY $i","$day")
                    days.add(i,day)
                    Log.d("DAYSSSS $i","$days")
                }
                var intent= Intent(context,Results::class.java)
                intent.putExtra("days",days)
                startActivity(intent)
                //tvRes.text = weather.toString()
            }catch (ex:Exception){}
        }
    }

    inner class MyAsyncTask: AsyncTask<String, String, String> {
        var woeidGbl:String =" "
        var flag:Int= 0
        var context:Context?=null
        constructor(context: Context){
            this.context=context
        }
        override fun doInBackground(vararg params: String?): String {
            try{
                val url =URL(params[0])
                val urlconnect =url.openConnection() as HttpsURLConnection
                urlconnect.connectTimeout = 7000

                var dataString = ConvertToString(urlconnect.inputStream)
                Log.d("DTA","$dataString")
                publishProgress(dataString)

            }catch (ex:Exception){}
            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var json = JSONArray(values[0])
                Log.d("LENGTH","${json}")
                Log.d("LENGTH","${json.length()}")
                if(json.length()==0){
                    tvRes.text="Data not found for ${tvCity.text} please try other city"
                    button.isEnabled = true
                    flag=1
                    tvCity.setText("")
                    MyAsyncTask(context!!).cancel(true)
                }else{
                    var res = json.getJSONObject(0)
                    var woeid = res.getString("woeid")
                    Log.d("RETURNED_ARRAY","$woeid")

                    woeidGbl = woeid.toString()
                }

            }catch (ex:Exception){}

        }

        override fun onPostExecute(result: String?) {
            val url = "https://www.metaweather.com/api/location/$woeidGbl/"
            if(flag!=1){
                GetResFromApi(context!!).execute(url)
            }
        }
    }

    fun ConvertToString(inputStream:InputStream):String{
        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String =""
        try {
            do{
                line=bufferReader.readLine()
                if(line!=null){
                    AllString+=line
                }
            }while(line!=null)
            inputStream.close()
        }catch(ex:Exception){}
        return  AllString
    }

    fun getResult(view: View) {
        var city: String = tvCity.text.toString()
        city = city.replace("\\s+".toRegex(),"")
        val url = "https://www.metaweather.com/api/location/search/?query=$city"
        if(city.length!=0){
            tvRes.text= "Please wait fetching data from server......"
            Log.d("URL","$url")
            MyAsyncTask(this).execute(url)
            button.isEnabled=false
        }else{
            tvRes.text= "Please enter a valid value!!!"
        }

    }


}