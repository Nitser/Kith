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
import com.project.scratchstudio.kith_andoid.custom_views.CustomAutoCompleteTextView
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpThirdBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse

class SingUpFragmentThird : BaseFragment() {

    private lateinit var presenter: SingUpPresenter
    private lateinit var binding: FragmentSingUpThirdBinding
    private lateinit var user: UserModelView
    private lateinit var editTextBehavior: EditTextBehavior
    private lateinit var countriesAdapter: ArrayAdapter<CustomAutoCompleteTextView.CustomAutoCompleteModel>
    private lateinit var regionsAdapter: ArrayAdapter<CustomAutoCompleteTextView.CustomAutoCompleteModel>
    private lateinit var citiesAdapter: ArrayAdapter<CustomAutoCompleteTextView.CustomAutoCompleteModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpThirdBinding.inflate(layoutInflater)
        initFields()
        presenter = SingUpPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        user = SingUpFragmentThirdArgs.fromBundle(arguments!!).user
        (activity as AppCompatActivity).supportActionBar!!.title = resources.getString(R.string.registration)
        return binding.root
    }

    private fun initFields() {
        binding.next.setOnClickListener(this::onClickNext)
        binding.country.initialize("Страна не найдена", binding.region)
        binding.region.initialize("Регион не найден", binding.city)
        binding.city.initialize("Город не найден", null)

        binding.country.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.country.isValidText(p0.toString())) {
                    presenter.getCountries(object : SingUpPresenter.GetCountriesCallback {
                        override fun onSuccess(baseResponse: CountriesResponse) {

                            if (!(baseResponse.countries.size == 1 && binding.country.getCurrentItem() != null
                                            && baseResponse.countries[0].nameRu == binding.country.getCurrentItem()!!.text)) {
                                binding.country.getItems().clear()
                                binding.country.getItems().addAll(baseResponse.countries.map { CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu) })
                                binding.country.isItemsContainText(binding.country.text.toString())
                                countriesAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, baseResponse.countries.map {
                                    CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                })
                                binding.country.setAdapter(countriesAdapter)
                                binding.country.showDropDown()
                            }
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, p0.toString())
                }
            }
        })

        binding.region.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.region.isValidText(p0.toString())) {
                    presenter.getRegions(object : SingUpPresenter.GetRegionsCallback {
                        override fun onSuccess(response: RegionsResponse) {

                            if (!(response.regions.size == 1 && binding.region.getCurrentItem() != null
                                            && response.regions[0].nameRu == binding.region.getCurrentItem()!!.text)) {
                                binding.region.getItems().clear()
                                binding.region.getItems().addAll(response.regions.map {
                                    CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                })
                                binding.region.isItemsContainText(binding.region.text.toString())
                                regionsAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, response.regions.map {
                                    CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                })
                                binding.region.setAdapter(regionsAdapter)
                                binding.region.showDropDown()
                            }
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, binding.country.getCurrentItem()!!.id, p0.toString())
                }
            }
        })

        binding.city.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.city.isValidText(p0.toString())) {
                    presenter.getCities(object : SingUpPresenter.GetCitiesCallback {
                        override fun onSuccess(response: CitiesResponse) {

                            if (!(response.cities.size == 1 && binding.city.getCurrentItem() != null
                                            && response.cities[0].nameRu == binding.city.getCurrentItem()!!.text)) {
                                binding.city.getItems().clear()
                                binding.city.getItems().addAll(response.cities.map {
                                    CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                })
                                binding.city.isItemsContainText(binding.city.text.toString())
                                citiesAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, response.cities.map {
                                    CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                })
                                binding.city.setAdapter(citiesAdapter)
                                binding.city.showDropDown()
                            }
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, binding.region.getCurrentItem()!!.id, p0.toString())
                }
            }
        })
    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            presenter.checkField(object : SingUpPresenter.GetUserCallback {
                override fun onSuccess(baseResponse: NewBaseResponse) {
                    val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    parseUser()
//            view.findNavController().navigate(R.id.act, bundle)
                }

                override fun onError(networkError: NetworkErrorException) {
                    editTextBehavior.fieldErrorWithText(binding.phone, "Телефон уже занят")
                }
            }, "phone", binding.phone.text.toString())
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