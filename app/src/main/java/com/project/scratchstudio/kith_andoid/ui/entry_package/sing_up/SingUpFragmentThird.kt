package com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up

import android.accounts.NetworkErrorException
import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.findNavController
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.app.BaseFragment
import com.project.scratchstudio.kith_andoid.app.Const
import com.project.scratchstudio.kith_andoid.custom_views.CustomAutoCompleteTextView
import com.project.scratchstudio.kith_andoid.custom_views.EditTextBehavior
import com.project.scratchstudio.kith_andoid.databinding.FragmentSingUpThirdBinding
import com.project.scratchstudio.kith_andoid.model.UserModelView
import com.project.scratchstudio.kith_andoid.network.model.NewBaseResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse
import java.util.Locale

class SingUpFragmentThird : BaseFragment() {

    private lateinit var presenter: SingUpPresenter
    private lateinit var binding: FragmentSingUpThirdBinding
    private lateinit var user: UserModelView
    private lateinit var editTextBehavior: EditTextBehavior
    private lateinit var countriesAdapter: ArrayAdapter<String>
    private lateinit var regionsAdapter: ArrayAdapter<String>
    private lateinit var citiesAdapter: ArrayAdapter<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSingUpThirdBinding.inflate(layoutInflater)
        initFields()
        presenter = SingUpPresenter(context!!)
        editTextBehavior = EditTextBehavior(context!!)
        user = SingUpFragmentThirdArgs.fromBundle(arguments!!).user
        return binding.root
    }

    private fun initFields() {
        val text = "Я соглашаюсь с пользовательским соглашением и политикой конфиденциальности"
        val ss = SpannableString(text)
        val clickableTermsOfService = object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.findNavController()
                        .navigate(SingUpFragmentThirdDirections.actionSingUpFragmentThirdToSingUpShowPermissionFragment(Const.TERMS_URL))
            }
        }
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.findNavController()
                        .navigate(SingUpFragmentThirdDirections.actionSingUpFragmentThirdToSingUpShowPermissionFragment(Const.POLICY_URL))
            }
        }
        ss.setSpan(clickableTermsOfService, 15, 43, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickablePrivacyPolicy, 44, 74, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.permissions.text = ss
        binding.permissions.movementMethod = LinkMovementMethod.getInstance()

        binding.next.setOnClickListener(this::onClickNext)
        binding.country.initialize("Страна не найдена", binding.region)
        binding.region.initialize("Регион не найден", binding.city)
        binding.city.initialize("Город не найден", null)

        binding.country.addTextChangedListener(object : TextWatcher {
            val view = binding.country
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    presenter.getCountries(object : SingUpPresenter.GetCountriesCallback {
                        override fun onSuccess(baseResponse: CountriesResponse) {
                            val result = baseResponse.countries.map {
                                it.nameRu.toLowerCase(Locale.ROOT) to CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                            }
                            val resultForAdapter = baseResponse.countries.map { it.nameRu }

                            if (!(baseResponse.countries.size == 1 && view.isViewSelected()
                                            && view.isItemsContainText(view.text.toString()))) {
                                view.initItems(HashMap(result.toMap()))
                                view.setSelectedIfItemsContainText(view.text.toString())
                                countriesAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, resultForAdapter)
                                view.setAdapter(countriesAdapter)
                                view.showDropDown()
                            } else if (baseResponse.countries.size == 0) {
                                view.getItems().clear()
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
            val view = binding.region
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    if (binding.country.getItems()[binding.country.text.toString().toLowerCase(Locale.ROOT)] != null)
                        presenter.getRegions(object : SingUpPresenter.GetRegionsCallback {
                            override fun onSuccess(response: RegionsResponse) {
                                val result = response.regions.map {
                                    it.nameRu.toLowerCase(Locale.ROOT) to CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                }
                                val resultForAdapter = response.regions.map { it.nameRu }

                                if (!(response.regions.size == 1 && view.isViewSelected()
                                                && view.isItemsContainText(view.text.toString()))) {
                                    view.initItems(HashMap(result.toMap()))
                                    view.setSelectedIfItemsContainText(view.text.toString())
                                    regionsAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, resultForAdapter)
                                    view.setAdapter(regionsAdapter)
                                    view.showDropDown()
                                } else if (response.regions.size == 0) {
                                    view.getItems().clear()
                                }
                            }

                            override fun onError(networkError: NetworkErrorException) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        }, binding.country.getItems()[binding.country.text.toString().toLowerCase(Locale.ROOT)]!!.id, p0.toString())
                }
            }
        })
        binding.city.addTextChangedListener(object : TextWatcher {
            val view = binding.city
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    if (binding.region.getItems()[binding.region.text.toString().toLowerCase(Locale.ROOT)] != null)
                        presenter.getCities(object : SingUpPresenter.GetCitiesCallback {
                            override fun onSuccess(response: CitiesResponse) {
                                val result = response.cities.map {
                                    it.nameRu.toLowerCase(Locale.ROOT) to CustomAutoCompleteTextView.CustomAutoCompleteModel(it.id, it.nameRu)
                                }
                                val resultForAdapter = response.cities.map { it.nameRu }

                                if (!(response.cities.size == 1 && view.isViewSelected()
                                                && view.isItemsContainText(view.text.toString()))) {
                                    view.initItems(HashMap(result.toMap()))
                                    view.setSelectedIfItemsContainText(view.text.toString())
                                    citiesAdapter = ArrayAdapter(context!!, R.layout.drop_list_item, resultForAdapter)
                                    view.setAdapter(citiesAdapter)
                                    view.showDropDown()
                                } else if (response.cities.size == 0) {
                                    view.getItems().clear()
                                }
                            }

                            override fun onError(networkError: NetworkErrorException) {
                                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                            }
                        }, binding.region.getItems()[binding.region.text.toString().toLowerCase(Locale.ROOT)]!!.id, p0.toString())
                }
            }
        })

        binding.phone.addTextChangedListener(PhoneNumberFormattingTextWatcher())

    }

    private fun onClickNext(view: View) {
        if (checkImportantFields()) {
            presenter.checkField(object : SingUpPresenter.GetUserCallback {
                override fun onSuccess(baseResponse: NewBaseResponse) {
                    editTextBehavior.fieldClear(binding.phone)
                    val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    parseUser()

                    presenter.singUp(object : SingUpPresenter.GetUserCallback {
                        override fun onSuccess(baseResponse: NewBaseResponse) {
                            view.findNavController().navigate(SingUpFragmentThirdDirections.actionSingUpFragmentThirdToCheckSmsFragment(user))
                        }

                        override fun onError(networkError: NetworkErrorException) {
                            Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
                        }
                    }, user)
                }

                override fun onError(networkError: NetworkErrorException) {
                    editTextBehavior.fieldErrorWithText(binding.phone, "Телефон уже занят")
                }
            }, "phone", PhoneNumberUtils.normalizeNumber(binding.phone.text.toString()))
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.country.isViewSelected()) {
            binding.region.visibility = View.VISIBLE
        }
        if (binding.region.isViewSelected()) {
            binding.city.visibility = View.VISIBLE
        }
        if (binding.region.getItems()[binding.region.text.toString().toLowerCase(Locale.ROOT)] != null) {
            binding.city.visibility = View.VISIBLE
        }
    }

    private fun checkImportantFields(): Boolean {
        return editTextBehavior.notEmptyField(binding.phone)
    }

    private fun parseUser(): UserModelView {
        with(user) {
            countryId = if (binding.country.getSelected())
                binding.country.getItems()[binding.country.text.toString().toLowerCase(Locale.ROOT)]!!.id
            else
                -1
            regionId = if (binding.region.getSelected())
                binding.region.getItems()[binding.region.text.toString().toLowerCase(Locale.ROOT)]!!.id
            else
                -1
            cityId = if (binding.city.getSelected())
                binding.city.getItems()[binding.city.text.toString().toLowerCase(Locale.ROOT)]!!.id
            else
                -1
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