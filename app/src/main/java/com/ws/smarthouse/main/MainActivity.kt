package com.ws.smarthouse.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Point
import android.graphics.drawable.DrawableContainer
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ws.smarthouse.R
import com.ws.smarthouse.common.Device
import com.ws.smarthouse.common.DevicesAdapter
import com.ws.smarthouse.common.RecyclerViewClickListener
import com.ws.smarthouse.databinding.ActivityMainBinding
import java.util.*


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val d = ArrayList<Device>()
    private val dv = ArrayList<ImageView>()
    private var sel = 0
    private var sd = false

    val screenHeight: Float
        get() {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y.toFloat()
        }

    val screenWidth: Float
        get() {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x.toFloat()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.getRoot())
        val adapter = DevicesAdapter(
            resources.getStringArray(R.array.devices),
            object : RecyclerViewClickListener {
                override fun recyclerViewListClicked(v: View?, position: Int) {
                    val device = Device(screenWidth / 2 - 40, screenHeight / 2 - 40, position)
                    d.add(device)
                    addDevice(device)
                    sd = false
                    binding!!.devices.visibility = View.GONE
                }
            })
        val layoutManager = LinearLayoutManager(this)
        binding!!.devicesRecyclerView.layoutManager = layoutManager
        binding!!.devicesRecyclerView.adapter = adapter
        binding!!.button1.setOnClickListener {
            sd = true
            binding!!.devices.visibility = View.VISIBLE
        }
        binding!!.root.setOnClickListener {
            sd = false
            binding!!.devices.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        d.clear()
        for (imageView in dv) {
            binding!!.root.removeView(imageView)
        }
        dv.clear()
        val sharedPreferences = getSharedPreferences("SmartHouse", MODE_PRIVATE)
        val count = sharedPreferences.getInt("devicesCount", 0)
        for (i in 0 until count - 1) {
            val device = Device(
                sharedPreferences.getFloat("x_$i", 0f),
                sharedPreferences.getFloat("y_$i", 0f),
                sharedPreferences.getInt("typeIndex_$i", 0)
            )
            device.temperature = sharedPreferences.getInt("temperature_$i", 26)
            device.date = Date(sharedPreferences.getLong("date_$i", 0))
            device.selectedColor = sharedPreferences.getInt("selectedColor_$i", 0)
            d.add(device)
            addDevice(device)
        }

    }

    override fun onPause() {
        val sharedPreferences = getSharedPreferences("SmartHouse", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("devicesCount", d.size)
        for (i in d.indices) {
            editor.putFloat("x_$i", d[i].y)
            editor.putFloat("y_$i", d[i].y)
            editor.putInt("typeIndex_$i", d[i].typeIndex)
            editor.putBoolean("isOn_$i", d[i].isOn)
            editor.putInt("temperature_$i", d[i].temperature)
            editor.putLong("date_$i", d[i].date.time / 1000)
            editor.putInt("selectedColor_$i", d[i].selectedColor)
        }
        editor.apply()
        super.onPause()
    }

    private fun addDevice(device: Device) {
        val imageView = ImageView(this)
        dv.add(imageView)
        imageView.setImageResource(
            resources.getIdentifier(
                "ic_device" + device.typeIndex, "drawable",
                packageName
            )
        )
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        val layoutParams = ConstraintLayout.LayoutParams(80, 80)
        imageView.layoutParams = layoutParams
        imageView.x = device.x
        imageView.y = device.y
        imageView.setOnClickListener(object : View.OnClickListener {

            private val MAX_CLICK_DURATION = 200
            private var startClickTime: Long = 0

            override fun onClick(v: View?) {
                fun mE(motionEvent: MotionEvent): Boolean {
                    val X = motionEvent.rawX.toInt()
                    val Y = motionEvent.rawY.toInt()
                    when (motionEvent.action and MotionEvent.ACTION_MASK) {

                        MotionEvent.ACTION_DOWN -> {
                            startClickTime = Calendar.getInstance().timeInMillis
                        }

                        MotionEvent.ACTION_UP -> {
                            val clickDuration = Calendar.getInstance().timeInMillis - startClickTime
                            if (clickDuration < MAX_CLICK_DURATION) {
                                var i = 0
                                while (i < d.size) {
                                    d[i].isSelected = 1
                                    val resourceId = resources.getIdentifier(
                                        "ic_device" + d[i].typeIndex, "drawable",
                                        packageName
                                    )
                                    dv[i].setImageResource(resourceId)
                                    i++
                                }
                                imageView.setImageResource(
                                    resources.getIdentifier(
                                        "ic_device" + device.typeIndex + "_selected",
                                        "drawable",
                                        packageName
                                    )
                                )
                                showInformationForDevice()
                            }
                        }

                        MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> {
                        }

                        MotionEvent.ACTION_MOVE -> {
                            v!!.x = (X).toFloat()
                            v.y = (Y).toFloat()
                            device.x = (X - 40).toFloat()
                            device.y = (Y - 40).toFloat()
                        }
                    }
                    binding!!.root.invalidate()
                    return true
                }
            }
        })
        binding!!.root.addView(imageView)
    }

    private fun showInformationForDevice() {
        binding!!.informationView.visibility = View.VISIBLE
        binding!!.device1.visibility = View.GONE
        binding!!.device2.visibility = View.GONE
        binding!!.device4.visibility = View.GONE
        binding!!.deviceName.text =
            resources.getStringArray(R.array.devices)[d[sel].typeIndex]
        if (d[sel].isOn) {
            binding!!.deviceStatus.text = "Включен"
            binding!!.deviceStatus.setTextColor(getColor(R.color.off_color))
            binding!!.button2.text = "Выключить"
        } else {
            binding!!.deviceStatus.text = "Выключен"
            binding!!.deviceStatus.setTextColor(getColor(R.color.on_color))
            binding!!.button2.text = "Включить"
        }
        binding!!.button2.setOnClickListener {
            d[sel].isOn = !d[sel].isOn
            if (d[sel].isOn) {
                binding!!.deviceStatus.text = "Включен"
                binding!!.deviceStatus.setTextColor(getColor(R.color.off_color))
                binding!!.button2.text = "Выключить"
            } else {
                binding!!.deviceStatus.text = "Выключен"
                binding!!.deviceStatus.setTextColor(getColor(R.color.on_color))
                binding!!.button2.text = "Включить"
            }
        }
        binding!!.closeButton.setOnClickListener {
            binding!!.informationView.visibility = View.VISIBLE
        }
        if (d[sel].typeIndex == 1) {
            binding!!.device1.visibility = View.VISIBLE
            if (d[sel].temperature == 22) {
                binding!!.radiobutton9.isChecked = true
            }
            if (d[sel].temperature == 24) {
                binding!!.radiobutton10.isChecked = true
            }
            if (d[sel].temperature == 26) {
                binding!!.radiobutton11.isChecked = true
            }
            binding!!.radioGroup2.setOnCheckedChangeListener { radioGroup, i ->
                if (i == R.id.radiobutton9) {
                    d[sel].temperature = 22
                }
                if (i == R.id.radiobutton10) {
                    d[sel].temperature = 24
                }
                if (i == R.id.radiobutton11) {
                    d[sel].temperature = 26
                }
            }
        }
        if (d[sel].typeIndex == 2) {
            binding!!.device2.visibility = View.VISIBLE
            binding!!.textview5.text = d[sel].nextCleaningTime
            binding!!.textview5.setOnClickListener {
                val cal = Calendar.getInstance()
                cal.time = d[sel].date
                val mHour = cal[Calendar.HOUR_OF_DAY]
                val mMinute = cal[Calendar.MINUTE]
                val timePickerDialog = TimePickerDialog(
                    this@MainActivity,
                    { view, hourOfDay, minute ->
                        cal[Calendar.HOUR_OF_DAY] = hourOfDay
                        cal[Calendar.MINUTE] = minute
                    }, mHour, mMinute, false
                )
                timePickerDialog.show()
            }
            binding!!.textview6.text = d[sel].nextCleaningDate
            binding!!.textview6.setOnClickListener {
                val cal = Calendar.getInstance()
                cal.time = d[sel].date
                val mYear = cal[Calendar.YEAR]
                val mMonth = cal[Calendar.MONTH]
                val mDay = cal[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(
                    this@MainActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        cal[Calendar.YEAR] = year
                        cal[Calendar.MONTH] = monthOfYear
                        cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.show()
            }
        }
        if (d[sel].typeIndex == 4) {
            binding!!.device4.visibility = View.VISIBLE
            for (i in resources.getIntArray(R.array.colors).indices) {
                val button = binding!!.radioGroup.getChildAt(i)
                val drawable = button.background as StateListDrawable
                val dcs = drawable.constantState as DrawableContainer.DrawableContainerState?
                val drawableItems = dcs!!.children
                val drawableChecked = drawableItems[0] as GradientDrawable
                val drawableUnChecked = drawableItems[1] as GradientDrawable
                drawableChecked.setColor(resources.getIntArray(R.array.colors)[i])
                drawableUnChecked.setColor(resources.getIntArray(R.array.colors)[i])
            }
            (binding!!.radioGroup.getChildAt(d[sel].selectedColor) as RadioButton).isChecked =
                true
            binding!!.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                d[sel].selectedColor =
                    radioGroup.indexOfChild(findViewById(i))
            }
        }
    }
}