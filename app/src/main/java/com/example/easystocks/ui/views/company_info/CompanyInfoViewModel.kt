package com.example.easystocks.ui.views.company_info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easystocks.domain.repository.StockRepository
import com.example.easystocks.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: StockRepository
): ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)
            val companyInfoResult = async { repository.getCompanyInfo(symbol) }
            val intraDayInfoResult = async { repository.getIntraDayInfo(symbol) }

            // TODO: If graphic info fails, but company doesn't, we want to still show the company info.
            when(val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(company = result.data, isLoading = false, error = null)
                }

                is Resource.Error -> {
                    state = state.copy(company = null, isLoading = false, error = result.message)
                }
                else -> Unit
            }

            when(val result = intraDayInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(stockInfos = result.data ?: emptyList(), isLoading = false, error = null)
                }

                is Resource.Error -> {
                    state = state.copy(company = null, isLoading = false, error = result.message)
                }
                else -> Unit
            }
        }
    }
}