package com.nyasai.usbdebugswitch

import android.app.KeyguardManager
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.provider.Settings
import android.widget.Toast
import java.lang.Exception

class UsbDebugToggle: TileService() {

    private lateinit var onIcon: Icon
    private lateinit var offIcon: Icon
    private lateinit var unavailableIcon: Icon

    @Override
    override fun onCreate() {
        super.onCreate()
        onIcon = Icon.createWithResource(this, R.drawable.margined_on)
        offIcon = Icon.createWithResource(this, R.drawable.margined_off)
        unavailableIcon = offIcon
    }

    @Override
    override fun onClick() {
        super.onClick()

        val tile = qsTile

        if ((getSystemService(KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked) {
            locked()
        } else {
            try {
                val contentResolver = contentResolver
                val flag = Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED)

                if (flag == 0) {
                    Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, 1)
                    on(tile)
                } else {
                    Settings.Global.putInt(contentResolver, Settings.Global.ADB_ENABLED, 0)
                    off(tile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                unavailable(tile)
            }
        }

        tile.updateTile()
    }

    @Override
    override fun onStartListening() {
        super.onStartListening()

        val tile = qsTile
        try {
            val flag = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED)

            if (flag != 0) {
                on(tile)
            } else {
                off(tile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            unavailable(tile)
        }

        tile.updateTile()
    }

    private fun on(tile: Tile) {
        tile.icon = onIcon
        tile.state = Tile.STATE_ACTIVE
        tile.label = "Debug - ON"
    }

    private fun off(tile: Tile) {
        tile.icon = offIcon
        tile.state = Tile.STATE_INACTIVE
        tile.label = "Debug - OFF"
    }

    private fun locked() {
        Toast.makeText(this, "Unavailable in lock screen", Toast.LENGTH_SHORT).show()
    }

    private fun unavailable(tile: Tile) {
        tile.icon = unavailableIcon
        tile.state = Tile.STATE_UNAVAILABLE
        tile.label = "USB Debug - Unavailable"
    }

}