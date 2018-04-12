package com.citsadigital.relojmultifuncionalcl.fragment


import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.citsadigital.relojmultifuncionalcl.R
import com.citsadigital.relojmultifuncionalcl.util.BluetoothConstants

import com.citsadigital.relojmultifuncionalcl.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_login_dialog.*

class LoginDialogFragment : AppCompatDialogFragment() {
    private var customView: View? = null
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        customView = LayoutInflater.from(activity).inflate(
                R.layout.fragment_login_dialog,
                null
        )

        mainViewModel = ViewModelProviders.of(activity!!)[MainViewModel::class.java]
        mainViewModel.getConnectionState()
                .observe(
                        this,
                        Observer { t ->
                            when (t?.getInt(getString(R.string.key_mainviewmodel_devicestate))) {
                                BluetoothConstants.DeviceState.DISPLAY_CONNECTED -> {
                                    //progressBarLogin.visibility = View.GONE
                                    PreferenceManager
                                            .getDefaultSharedPreferences(context)
                                            .edit()
                                            .putString(
                                                    getString(R.string.pref_key_password),
                                                    passwordText.text.toString()
                                            ).apply()
                                    dismiss()
                                }
                                BluetoothConstants.DeviceState.DISCONNECTED -> dismiss()
                                BluetoothConstants.DeviceState.INVALID_PASSWORD, BluetoothConstants.DeviceState.INVALID_DEVICE -> {
                                    textInputLayoutPassword.error = t.getString(getString(R.string.key_mainviewmodel_message))
                                }
                            }
                        }
                )

        return AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.titledialog_logindialog))
                .setView(customView)
                .setCancelable(false)
                .setOnKeyListener { dialog, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) mainViewModel.closeConnection()
                    false
                }
                .setPositiveButton(getString(R.string.buttontext_messagedialog_accept), null)
                .setNegativeButton(R.string.buttontext_messagedialog_cancel, { dialog, which ->
                    mainViewModel.closeConnection()
                }).create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(passwordText) {
            setText(arguments?.getString(Factory.passwordKey))
            setSelection(passwordText.text.length)
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE).isEnabled =
                            s?.length == resources.getInteger(R.integer.passwordLength)
                    textInputLayoutPassword.error = null
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                        Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })
        }
    }

    override fun onStart() {
        super.onStart()
        val button = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        button.isEnabled = passwordText.text.length ==
                resources.getInteger(R.integer.passwordLength)
        button.setOnClickListener {
            //progressBarLogin.visibility = View.VISIBLE
            mainViewModel.sendPassword(passwordText.text.toString())
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.setCanceledOnTouchOutside(false)
        return customView
    }

    companion object Factory {
        private const val passwordKey = "password_key"

        fun newInstance(password: String): LoginDialogFragment {
            return LoginDialogFragment().apply {
                arguments = Bundle().apply { putString(Factory.passwordKey, password) }
            }
        }
    }


}
