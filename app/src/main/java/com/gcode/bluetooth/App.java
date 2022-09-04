package com.gcode.bluetooth;

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2022/9/4 13:17
// Description: 
// Documentation:

import android.app.Application;

import com.gcode.vasttools.ToolsConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ToolsConfig.init(this);
    }

}
