package wangdaye.com.geometricweather.db.controllers

fun <E> getNonNullList(list: List<E>?): List<E> = list.orEmpty()
