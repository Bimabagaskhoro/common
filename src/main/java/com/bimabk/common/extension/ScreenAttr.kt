package com.bimabk.common.extension

import androidx.compose.ui.graphics.Color

object ScreenAttr {
    data class ActionMenu(
        val icon: Int,
        val nameIcon: String,
        val showBadge: Boolean,
        val onClickActionMenu: ((String) -> Unit)? = null
    )

    data class ScreenArgs(
        val actionMenus: List<ActionMenu> = emptyList(),
        val screenName: String? = null,
        val statusBarColor: Color? = null,
        val navigationBarColor: Color? = null,
        val topBarColor: Color? = null,
        val colorBackIcon: Color? = null,
        val onClickBack: (() -> Unit)? = null
    )
}