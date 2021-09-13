package com.reinkyatto.webjens.arch

import android.content.Context
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

abstract class BaseFragment : Fragment() {

    protected fun showSnackBar(view: View, text: String?) {
        text?.let { Snackbar.make(view, it, Snackbar.LENGTH_LONG).show() }
    }

}