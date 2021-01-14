package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_results.*
import kotlinx.android.synthetic.main.ticket.*
import kotlinx.android.synthetic.main.ticket.view.*

class Results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        var adapter:InfoAdapter?=null
        adapter=InfoAdapter(this)
        ListView.adapter = adapter
    }

    inner class InfoAdapter: BaseAdapter {
        var context: Context?=null
        var days= intent.getSerializableExtra("days") as ArrayList<ArrayList<String>>
        var wtype= mapOf(
                "Snow" to 's',
                "Sleet" to "sl",
                "Hail" to "h",
                "Thunderstorm" to "t",
                "Heavy Rain" to "hr",
                "Light Rain" to "lr",
                "Showers" to "s",
                "Heavy Cloud" to "hc",
                "Light Cloud" to "lc",
                "Clear" to "c"
        )

        constructor(context: Context){
            this.context=context
        }

        override fun getCount(): Int {
            return 6
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            Log.d("DAYS","$days")
            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var myView= inflater.inflate(R.layout.ticket, null)
            var bundle:Bundle? = intent.extras
            var day:ArrayList<String>?=null
            day = days!![position]

            val tvDate: View by lazy {findViewById(R.id.tvDate)}
            Log.d("Day","${day[0]}")
            Picasso
                .get()
                .load("https://www.metaweather.com/static/img/weather/png/${wtype[day[1].toString()]}.png")
                .resize(150,150)
                .into(myView.iv)
            myView.tvDate.text = "Date : ${day[0]}"
            myView.tvWeather.text = "Weather : ${day[1]}"
            myView.tvMinTemp.setText(" Min Temperature : ${day[2].take(5)}")
            myView.tvMaxTemp.text= "Max temperature : ${day[3].take(5)}"

            Log.d("DAY", "$day")
            return myView
        }

    }
}
