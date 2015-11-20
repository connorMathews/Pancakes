package me.mattlogan.pancakes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import me.mattlogan.pancakes.view.ColoredViewModel
import me.mattlogan.pancakes.view.RedView

class MainActivity : AppCompatActivity(), PancakesActivity {

    private lateinit var pancakes: Pancakes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pancakes = Pancakes(findViewById(R.id.container) as ViewGroup) {
            Toast.makeText(applicationContext, "Finish stack!", Toast.LENGTH_LONG)
        }

        if (savedInstanceState != null) {
            pancakes.onLoad(savedInstanceState)
        } else {
            pancakes.push(RedView.RedViewSlice(
                    ColoredViewModel.builder().build()))
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        pancakes.onSave(outState)
        super.onSaveInstanceState(outState)
        Log.d("testing", "MainActivity onSaveInstanceState bundle:" + outState)
    }

    override fun onBackPressed() {
        pancakes.pop()
    }

    override fun pancakes(): Pancakes {
        return pancakes
    }
}
