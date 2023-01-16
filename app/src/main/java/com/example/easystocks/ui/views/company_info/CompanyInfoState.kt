package com.example.easystocks.ui.views.company_info

import com.example.easystocks.domain.model.CompanyInfo
import com.example.easystocks.domain.model.IntraDayInfo

data class CompanyInfoState (
    val stockInfos: List<IntraDayInfo> = emptyList(),
    val company: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)