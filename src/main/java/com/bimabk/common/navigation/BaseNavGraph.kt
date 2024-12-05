package com.bimabk.common.navigation

import androidx.navigation.NavGraphBuilder

open class BaseNavGraph {
    open fun buildGraph(
        navigator: BaseNavigator,
        navGraphBuilder: NavGraphBuilder
    ) = Unit
}
