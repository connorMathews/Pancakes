package me.mattlogan.pancakes;

import android.app.Application;
import android.graphics.Color;
import com.squareup.leakcanary.LeakCanary;
import me.mattlogan.pancakes.view.ColoredViewModel;

public class PancakesApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

        //ColoredViewModel.create(20);
    }
}
