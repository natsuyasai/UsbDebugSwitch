package com.nyasai.usbdebugswitch

import android.app.KeyguardManager
import android.graphics.drawable.Icon
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

/**
 * 開発者機能のトグルスイッチ切り替え用サービスのベースクラス
 * @param onIconResourceId ON時のアイコンリソースID
 * @param offIconResourceId OFF時のアイコンリソースID
 * @param settingFunction 対象の機能
 * @param functionText 対象機能名
 * @param unavailableIconResourceId 実行不可時のアイコンリソースID(未指定時はOFFと同じ)
 */
abstract class DevelopModeToggleServiceBase(
        onIconResourceId: Int,
        offIconResourceId: Int,
        settingFunction: String,
        functionText: String,
        unavailableIconResourceId: Int?
) : TileService() {

    // ON時のアイコンリソースID
    private var _onIconResourceId: Int = onIconResourceId
    // OFF時のアイコンリソースID
    private var _offIconResourceId: Int = offIconResourceId
    // 実行不可時のアイコンリソースID(未指定時はOFFと同じ)
    private var _unavailableIconResourceId: Int? = unavailableIconResourceId
    // 対象の機能
    private var _settingFunction: String = settingFunction
    // 対象機能名
    private var _functionText: String = functionText

    // ON時のアイコン
    private lateinit var _onIcon: Icon
    // OFF時のアイコン
    private lateinit var _offIcon: Icon
    // 実行不可時のアイコン
    private lateinit var _unavailableIcon: Icon

    /**
     * onCreate
     */
    override fun onCreate() {
        _onIcon = Icon.createWithResource(this, _onIconResourceId)
        _offIcon = Icon.createWithResource(this, _offIconResourceId)
        _unavailableIcon = if(_unavailableIconResourceId != null) {
            Icon.createWithResource(this, _unavailableIconResourceId!!)
        }
        else {
            _offIcon
        }
    }

    /**
     * onClick()
     */
    @Override
    override fun onClick() {
        super.onClick()

        val tile = qsTile

        if ((getSystemService(KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked) {
            // ロック画面のため操作禁止
            locked()
        } else {
            try {
                val flag = Settings.Global.getInt(contentResolver, _settingFunction)

                if (flag == 0) {
                    Settings.Global.putInt(contentResolver, _settingFunction, 1)
                    on(tile)
                } else {
                    Settings.Global.putInt(contentResolver, _settingFunction, 0)
                    off(tile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                unavailable(tile)
            }
        }

        tile.updateTile()
    }

    /**
     * onStartListening
     */
    @Override
    override fun onStartListening() {
        super.onStartListening()

        val tile = qsTile
        try {
            val flag = Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED)

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

    /**
     * 機能ON
     */
    private fun on(tile: Tile) {
        tile.icon = _onIcon
        tile.state = Tile.STATE_ACTIVE
        tile.label = "$_functionText - ON"
    }

    /**
     * 機能OFF
     */
    private fun off(tile: Tile) {
        tile.icon = _offIcon
        tile.state = Tile.STATE_INACTIVE
        tile.label = "$_functionText - OFF"
    }

    /**
     * ロック画面での操作を受けた
     */
    private fun locked() {
        Toast.makeText(this, "Unavailable in lock screen", Toast.LENGTH_SHORT).show()
    }

    /**
     * 実行不可時
     */
    private fun unavailable(tile: Tile) {
        tile.icon = _unavailableIcon
        tile.state = Tile.STATE_UNAVAILABLE
        tile.label = "$_functionText - Unavailable"
    }
}