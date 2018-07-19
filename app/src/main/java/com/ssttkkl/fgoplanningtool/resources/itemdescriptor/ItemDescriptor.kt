package com.ssttkkl.fgoplanningtool.resources.itemdescriptor

import android.preference.PreferenceManager
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.PreferenceKeys
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import java.io.File
import java.io.Serializable
import java.util.*

data class ItemDescriptor(val codename: String,
                          val cnName: String,
                          val jpName: String,
                          val enName: String,
                          val type: ItemType,
                          val wikiLinks: Map<String, String>) : Serializable {
    val imgFile
        get() = File(ResourcesProvider.instance.itemImgDir, "$codename.jpg")

    val localizedName
        get() = when (PreferenceManager.getDefaultSharedPreferences(MyApp.context).getString(PreferenceKeys.KEY_NAME_LANGUAGE, "zh")) {
            "ja" -> jpName
            "en" -> enName
            else -> cnName
        }
}