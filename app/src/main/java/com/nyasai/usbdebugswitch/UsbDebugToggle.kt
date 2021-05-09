package com.nyasai.usbdebugswitch

import android.provider.Settings

class UsbDebugToggle: DevelopModeToggleServiceBase(
        R.drawable.margined_on,
        R.drawable.margined_off,
        Settings.Global.ADB_ENABLED,
        "USB Debug",
        null
)