package com.example.sharedpreference3

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.sharedpreference3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val PREFS_NAME = "my_prefs"
    private val KEY_NAME = "key_name"
    private val KEY_AGE = "key_age"
    private val KEY_NOTIFY = "key_notify"
    private val KEY_DARK = "key_dark"

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedDark = prefs.getBoolean(KEY_DARK, false)
        AppCompatDelegate.setDefaultNightMode(
            if (savedDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()

        binding.darkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit { putBoolean(KEY_DARK, isChecked) }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.firstET.text.toString().trim()
            val ageText = binding.secondET.text.toString().trim()
            val notify = binding.notifySwitch.isChecked
            val dark = binding.darkMode.isChecked

            if (name.isEmpty() || ageText.isEmpty()) {
                Toast.makeText(this, "Please enter name and age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = try {
                ageText.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            prefs.edit {
                putString(KEY_NAME, name)
                putInt(KEY_AGE, age)
                putBoolean(KEY_NOTIFY, notify)
                putBoolean(KEY_DARK, dark)
            }

            Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show()
            updateSavedDataText(name, age, notify)
        }

        binding.clearBtn.setOnClickListener {
            prefs.edit { clear() }
            binding.firstET.setText("")
            binding.secondET.setText("")
            binding.notifySwitch.isChecked = false
            binding.darkMode.isChecked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            updateSavedDataText(null, null, false)
            Toast.makeText(this, "Preferences cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedName = prefs.getString(KEY_NAME, null)
        val savedAge = prefs.getInt(KEY_AGE, 0)
        val savedNotify = prefs.getBoolean(KEY_NOTIFY, false)
        val savedDark = prefs.getBoolean(KEY_DARK, false)

        binding.firstET.setText(savedName ?: "")
        binding.secondET.setText(if (savedAge == 0) "" else savedAge.toString())
        binding.notifySwitch.isChecked = savedNotify
        binding.darkMode.isChecked = savedDark

        updateSavedDataText(savedName, if (savedAge == 0) null else savedAge, savedNotify)
    }

    private fun updateSavedDataText(name: String?, age: Int?, notify: Boolean) {
        binding.output.text = if (name == null || age == null) {
            "Saved: (none)"
        } else {
            "Saved: Name = $name, Age = $age, Notification = ${if (notify) "On" else "Off"}"
        }
    }
}
