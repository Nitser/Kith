package com.project.scratchstudio.kith_andoid.ui.home_package.board_list.bottom_sheet

import android.accounts.NetworkErrorException
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.scratchstudio.kith_andoid.BoardPresenter
import com.project.scratchstudio.kith_andoid.R
import com.project.scratchstudio.kith_andoid.custom_views.CustomAutoCompleteTextView
import com.project.scratchstudio.kith_andoid.databinding.FragmentBottomSheetBinding
import com.project.scratchstudio.kith_andoid.model.SearchOptions
import com.project.scratchstudio.kith_andoid.network.model.category.Category
import com.project.scratchstudio.kith_andoid.network.model.category.CategoryResponse
import com.project.scratchstudio.kith_andoid.network.model.city.CitiesResponse
import com.project.scratchstudio.kith_andoid.network.model.country.CountriesResponse
import com.project.scratchstudio.kith_andoid.network.model.region.RegionsResponse
import com.project.scratchstudio.kith_andoid.ui.entry_package.sing_up.SingUpPresenter
import com.project.scratchstudio.kith_andoid.view_model.SearchViewModel
import java.util.Locale

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var boardPresenter: BoardPresenter
    private lateinit var singUpPresenter: SingUpPresenter
    private val searchOptions: SearchViewModel by activityViewModels()

    private lateinit var adapter: ArrayAdapter<CharSequence>
    private var categoriesList = ArrayList<Category>()

    private lateinit var countriesAdapter: ArrayAdapter<String>
    private lateinit var regionsAdapter: ArrayAdapter<String>
    private lateinit var citiesAdapter: ArrayAdapter<String>

    private var categoryPosition: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        boardPresenter = BoardPresenter(requireContext())
        singUpPresenter = SingUpPresenter(requireContext())
        binding.bottomSheetButtonDone.setOnClickListener { saveSearchOptions() }
        initCategory()
        initAddress()
    }

    private fun saveSearchOptions() {
        val newSearchOptions = SearchOptions()
        newSearchOptions.categoryId = categoryPosition
        newSearchOptions.countryId = if (binding.bottomSheetCountry.getSelected())
            binding.bottomSheetCountry.getItems()[binding.bottomSheetCountry.text.toString().toLowerCase(Locale.ROOT)]!!.id
        else
            -1
        newSearchOptions.regionId = if (binding.bottomSheetRegion.getSelected())
            binding.bottomSheetRegion.getItems()[binding.bottomSheetRegion.text.toString().toLowerCase(Locale.ROOT)]!!.id
        else
            -1
        newSearchOptions.cityId = if (binding.bottomSheetCity.getSelected())
            binding.bottomSheetCity.getItems()[binding.bottomSheetCity.text.toString().toLowerCase(Locale.ROOT)]!!.id
        else
            -1
        searchOptions.setSearchOptions(newSearchOptions)
        this.dismiss()
    }

    private fun initCategory() {
        adapter = ArrayAdapter(requireContext()
                , R.layout.drop_list_item, categoriesList.map { it.nameRu })
        adapter.add("Категория")
        binding.bottomSheetCategory.adapter = adapter
        binding.bottomSheetCategory.setSelection(0, true)
        binding.bottomSheetCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.bottomSheetCategory.setSelection(position)
                categoryPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        boardPresenter!!.getCategories(object : BoardPresenter.CategoryCallback {
            override fun onSuccess(categoriesResponse: CategoryResponse) {
                categoriesList = categoriesResponse.categories
                adapter.addAll(categoriesList.map { it.nameRu })
                adapter.notifyDataSetChanged()
            }

            override fun onError(networkError: NetworkErrorException) {
                Toast.makeText(context, "Ошибка отправки запроса", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initAddress() {
        binding.bottomSheetCountry.initialize("Страна не найдена", binding.bottomSheetRegion)
        binding.bottomSheetRegion.initialize("Регион не найден", binding.bottomSheetCity)
        binding.bottomSheetCity.initialize("Город не найден", null)

        binding.bottomSheetCountry.addTextChangedListener(object : TextWatcher {
            val view = binding.bottomSheetCountry
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    singUpPresenter.getCountries(object : SingUpPresenter.GetCountriesCallback {
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
        binding.bottomSheetRegion.addTextChangedListener(object : TextWatcher {
            val view = binding.bottomSheetRegion
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    if (binding.bottomSheetCountry.getItems()[binding.bottomSheetCountry.text.toString().toLowerCase(Locale.ROOT)] != null)
                        singUpPresenter.getRegions(object : SingUpPresenter.GetRegionsCallback {
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
                        }, binding.bottomSheetCountry.getItems()[binding.bottomSheetCountry.text.toString().toLowerCase(Locale.ROOT)]!!.id, p0.toString())
                }
            }
        })
        binding.bottomSheetCity.addTextChangedListener(object : TextWatcher {
            val view = binding.bottomSheetCity
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.isValidText(p0.toString())) {
                    if (binding.bottomSheetRegion.getItems()[binding.bottomSheetRegion.text.toString().toLowerCase(Locale.ROOT)] != null)
                        singUpPresenter.getCities(object : SingUpPresenter.GetCitiesCallback {
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
                        }, binding.bottomSheetRegion.getItems()[binding.bottomSheetRegion.text.toString().toLowerCase(Locale.ROOT)]!!.id, p0.toString())
                }
            }
        })

    }

}
