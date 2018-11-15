package com.ssttkkl.fgoplanningtool.ui.preferences

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.webkit.MimeTypeMap
import com.ssttkkl.fgoplanningtool.MyApp
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.ui.preferences.updaterespack.UpdateResPackDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.*

class ResPackGroupPresenter(val view: PreferencesFragment) {
    init {
        val curVersion = view.findPreference(KEY_CUR_VERSION)
        curVersion.summary = when {
            ResourcesProvider.instance.isAbsent -> view.getString(R.string.summary_curResPackVersion_absent_pref)
            ResourcesProvider.instance.isNotTargeted -> {
                if (ResourcesProvider.instance.resPackInfo.targetVersion < ResourcesProvider.TARGET_VERSION)
                    view.getString(R.string.summary_curResPackVersion_lowTargetVersion_pref,
                            ResourcesProvider.instance.resPackInfo.content,
                            ResourcesProvider.instance.resPackInfo.releaseDate)
                else
                    view.getString(R.string.summary_curResPackVersion_highTargetVersion_pref,
                            ResourcesProvider.instance.resPackInfo.content,
                            ResourcesProvider.instance.resPackInfo.releaseDate)
            }
            ResourcesProvider.instance.isBroken -> view.getString(R.string.summary_curResPackVersion_broken_pref,
                    ResourcesProvider.instance.resPackInfo.content,
                    ResourcesProvider.instance.resPackInfo.releaseDate)
            else -> view.getString(R.string.summary_curResPackVersion_pref,
                    ResourcesProvider.instance.resPackInfo.content,
                    ResourcesProvider.instance.resPackInfo.releaseDate)
        }

        view.findPreference(KEY_AUTO_UPDATE).setOnPreferenceClickListener {
            val tag = UpdateResPackDialogFragment::class.qualifiedName
            val fm = (view.activity as AppCompatActivity).supportFragmentManager
            if (fm.findFragmentByTag(tag) == null)
                UpdateResPackDialogFragment().show(fm, tag)
            true
        }

        view.findPreference(KEY_MANUALLY_UPDATE).setOnPreferenceClickListener {
            gotoOpenZipUi(REQUEST_CODE_UPDATE_RES)
            true
        }
    }

    private fun gotoOpenZipUi(requestCode: Int) {
        view.startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip")
            addCategory(Intent.CATEGORY_OPENABLE)
        }, requestCode)
    }

    fun onActivityResultOK(requestCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_UPDATE_RES) {
            GlobalScope.launch(Dispatchers.IO) {
                val input = view.activity.contentResolver.openInputStream(data!!.data)
                val tempFile = File(MyApp.context.cacheDir, "${UUID.randomUUID()}.zip").apply { deleteOnExit() }
                tempFile.createNewFile()
                tempFile.outputStream().use { output ->
                    IOUtils.copy(input, output)
                }
                input.close()

                GlobalScope.launch(Main) {
                    val tag = UpdateResPackDialogFragment::class.qualifiedName
                    val fm = (view.activity as AppCompatActivity).supportFragmentManager
                    if (fm.findFragmentByTag(tag) == null)
                        UpdateResPackDialogFragment.newInstanceForManuallyUpdate(tempFile)
                                .show(fm, tag)
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_UPDATE_RES = 1
        private const val KEY_CUR_VERSION = "curResPackVersion"
        private const val KEY_AUTO_UPDATE = "autoUpdateRes"
        private const val KEY_MANUALLY_UPDATE = "manuallyUpdateRes"
    }
}