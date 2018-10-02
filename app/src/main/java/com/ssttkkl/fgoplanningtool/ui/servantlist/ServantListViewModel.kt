package com.ssttkkl.fgoplanningtool.ui.servantlist

import android.arch.lifecycle.ViewModel
import com.ssttkkl.fgoplanningtool.resources.ResourcesProvider
import com.ssttkkl.fgoplanningtool.resources.servant.ServantClass
import com.ssttkkl.fgoplanningtool.resources.servant.WayToGet

class ServantListViewModel : ViewModel() {
    val data = ServantListLiveData(ResourcesProvider.instance.servants.values)

    var viewType: ViewType = ViewType.List

    // search
    var searchText: CharSequence = ""
        set(value) {
            field = value
            data.nameFilter.clear()
            value.split(' ').filter { it.isNotEmpty() }.forEach {
                data.nameFilter.add(it)
            }
            data.notifyFiltersChanged()
        }

    // order and orderby
    var orderSelectedPosition: Int = 0
        set(value) {
            field = value
            data.order = Order.values()[value]
            data.notifyFiltersChanged()
        }

    var orderBySelectedPosition: Int = 0
        set(value) {
            field = value
            data.orderBy = OrderBy.values()[value]
            data.notifyFiltersChanged()
        }

    // star
    val starSelectedPosition: Set<Int> = HashSet()

    fun setStarPositionSelected(pos: Int) {
        (starSelectedPosition as MutableSet<Int>).add(pos)
        data.starFilter.add(listOf(1, 2, 3, 4, 5)[pos])
        data.notifyFiltersChanged()
    }

    fun setStarPositionUnselected(pos: Int) {
        (starSelectedPosition as MutableSet<Int>).remove(pos)
        data.starFilter.remove(listOf(1, 2, 3, 4, 5)[pos])
        data.notifyFiltersChanged()
    }

    // class
    val classSelectedPosition: Set<Int> = HashSet()

    fun setClassPositionSelected(pos: Int) {
        (classSelectedPosition as MutableSet<Int>).add(pos)
        data.classFilter.add(ServantClass.values()[pos])
        data.notifyFiltersChanged()
    }

    fun setClassPositionUnselected(pos: Int) {
        (classSelectedPosition as MutableSet<Int>).remove(pos)
        data.classFilter.remove(ServantClass.values()[pos])
        data.notifyFiltersChanged()
    }

    // wayToGet
    val wayToGetSelectedPosition: Set<Int> = HashSet()

    fun setWayToGetPositionSelected(pos: Int) {
        (wayToGetSelectedPosition as MutableSet<Int>).add(pos)
        data.wayToGetFilter.add(WayToGet.values()[pos])
        data.notifyFiltersChanged()
    }

    fun setWayToGetPositionUnselected(pos: Int) {
        (wayToGetSelectedPosition as MutableSet<Int>).remove(pos)
        data.wayToGetFilter.remove(WayToGet.values()[pos])
        data.notifyFiltersChanged()
    }

    // items
    val items: Set<String> = HashSet()

    var itemFilterModeSpinnerPosition = 0
        set(value) {
            field = value
            data.itemFilterMode = ItemFilterMode.values()[value]
            data.notifyFiltersChanged()
        }

    fun addItem(codename: String) {
        (items as MutableSet<String>).add(codename)
        data.itemFilter.add(codename)
        data.notifyFiltersChanged()
    }

    fun removeItem(codename: String) {
        (items as MutableSet<String>).remove(codename)
        data.itemFilter.remove(codename)
        data.notifyFiltersChanged()
    }
}