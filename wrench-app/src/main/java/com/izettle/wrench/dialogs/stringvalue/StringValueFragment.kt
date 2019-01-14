package com.izettle.wrench.dialogs.stringvalue

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.izettle.wrench.R
import com.izettle.wrench.databinding.FragmentStringValueBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class StringValueFragment : DialogFragment() {

    private lateinit var binding: FragmentStringValueBinding
    private val viewModel: FragmentStringValueViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        assert(arguments != null)

        binding = FragmentStringValueBinding.inflate(LayoutInflater.from(context))

        val args = StringValueFragmentArgs.fromBundle(arguments!!)

        viewModel.init(args.configurationId, args.scopeId)

        viewModel.configuration.observe(this, Observer { wrenchConfiguration ->
            if (wrenchConfiguration != null) {
                requireDialog().setTitle(wrenchConfiguration.key)
            }
        })

        viewModel.selectedConfigurationValueLiveData.observe(this, Observer { wrenchConfigurationValue ->
            viewModel.selectedConfigurationValue = wrenchConfigurationValue
            if (wrenchConfigurationValue != null) {
                binding.value.setText(wrenchConfigurationValue.value)
            }
        })

        binding.value.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateConfigurationValue(binding.value.text!!.toString())
                dismiss()
            }
            false
        }

        return AlertDialog.Builder(requireActivity())
                .setTitle(".")
                .setView(binding.root)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.updateConfigurationValue(binding.value.text!!.toString())
                    dismiss()
                }
                .setNegativeButton(R.string.revert) { _, _ ->
                    if (viewModel.selectedConfigurationValue != null) {
                        viewModel.deleteConfigurationValue()
                    }
                    dismiss()
                }
                .create()
    }

    companion object {

        fun newInstance(args: StringValueFragmentArgs): StringValueFragment {
            val fragment = StringValueFragment()
            fragment.arguments = args.toBundle()
            return fragment
        }
    }
}
