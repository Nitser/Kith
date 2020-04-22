package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.accounts.NetworkErrorException
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpThirdBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.entry.CountriesResponse

class SingUpFragmentThird : BaseFragment() {

    private lateinit var presenter: SingUpPresenter
    private lateinit var binding: FragmentSingUpThirdBinding
    private lateinit var user: UserModelView
    private lateinit var editTextBehavior: EditTextBehavior

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpThirdBinding.inflate(layoutInflater)
        initButtons()
        presenter = SingUpPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        user = SingUpFragmentThirdArgs.fromBundle(arguments!!).user
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        return binding.root
    }

    private fun initButtons() {
        binding.next.setOnClickListener(this::onClickNext)
        binding.country.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    binding.region.visibility = View.VISIBLE

                    presenter.getCountries(object : SingUpPresenter.GetCountriesCallback {
                        override fun onSuccess(baseResponse: CountriesResponse) {
                            val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_dropdown_item_1line
                                    , baseResponse.countries.map { it.nameRu })
                            binding.country.setAdapter(adapter)
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, p0.toString())
                } else {
                    binding.region.visibility = View.GONE
                }

            }
        })
        binding.region.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    binding.city.visibility = View.VISIBLE
                } else {
                    binding.city.visibility = View.GONE
                }
            }
        })
    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            parseUser()
//            view.findNavController().navigate(R.id.act, bundle)
        }
    }

    private fun checkImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.phone)
    }

    private fun parseUser(): UserModelView {
        with(user) {
            country = binding.city.text.toString()
            region = binding.region.text.toString()
            city = binding.city.text.toString()
            phone = binding.phone.text.toString()
        }
        return user
    }

    companion object {

        fun newInstance(): SingUpFragmentFirst {
            return SingUpFragmentFirst()
        }
    }
}