package com.thedigialex.inventory.ui.budget

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.map
import com.thedigialex.inventory.database.AppDatabase
import com.thedigialex.inventory.database.entity.BudgetCategory
import com.thedigialex.inventory.database.entity.BudgetEntry
import com.thedigialex.inventory.database.entity.BudgetSubCategory
import com.thedigialex.inventory.repository.BudgetRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

sealed class BudgetListItem {
    data class Header(val categoryName: String, val total: Double, val type: String) : BudgetListItem()
    data class Entry(val entry: BudgetEntry) : BudgetListItem()
}

data class CategoryWithTotal(val category: BudgetCategory, val total: Double)

class BudgetViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private val repo = BudgetRepository(db.budgetDao(), db.budgetCategoryDao(), db.budgetSubCategoryDao())

    private val _currentMonth = MutableLiveData(Calendar.getInstance())
    val currentMonth: LiveData<Calendar> = _currentMonth

    val entries: LiveData<List<BudgetEntry>> = _currentMonth.switchMap { cal ->
        repo.getEntriesForMonth(monthStart(cal), monthEnd(cal))
    }

    val groupedEntries: LiveData<List<BudgetListItem>> = entries.map { list ->
        list.groupBy { it.categoryName }
            .flatMap { (name, items) ->
                listOf(BudgetListItem.Header(name, items.sumOf { it.amount }, items.first().type)) +
                        items.map { BudgetListItem.Entry(it) }
            }
    }

    val categories: LiveData<List<BudgetCategory>> = repo.getAllCategories()

    private val _rawCategoryTotals = repo.getAllCategoryTotals()
    private val _sortByTotal = MutableLiveData(false)
    val sortByTotal: LiveData<Boolean> = _sortByTotal

    val categoriesWithTotals: LiveData<List<CategoryWithTotal>> = MediatorLiveData<List<CategoryWithTotal>>().also { med ->
        fun recompute() {
            val cats = categories.value ?: return
            val totalsMap = _rawCategoryTotals.value?.associate { it.categoryId to it.total } ?: emptyMap()
            val byTotal = _sortByTotal.value ?: false
            val list = cats.map { CategoryWithTotal(it, totalsMap[it.id] ?: 0.0) }
            med.value = if (byTotal) {
                list.sortedWith(compareBy<CategoryWithTotal> { if (it.category.type == "income") 0 else 1 }.thenByDescending { it.total })
            } else {
                list.sortedWith(compareBy<CategoryWithTotal> { if (it.category.type == "income") 0 else 1 }.thenBy { it.category.name })
            }
        }
        med.addSource(categories) { recompute() }
        med.addSource(_rawCategoryTotals) { recompute() }
        med.addSource(_sortByTotal) { recompute() }
    }

    fun toggleSort() { _sortByTotal.value = !(_sortByTotal.value ?: false) }

    private val _selectedCategoryId = MutableLiveData<Int>()
    val subCategoriesForSelected: LiveData<List<BudgetSubCategory>> = _selectedCategoryId.switchMap { id ->
        repo.getSubCategoriesFor(id)
    }

    fun selectCategory(categoryId: Int) { _selectedCategoryId.value = categoryId }

    fun getSubCategoriesFor(categoryId: Int) = repo.getSubCategoriesFor(categoryId)

    suspend fun getCategoriesOnce(): List<BudgetCategory> =
        withContext(Dispatchers.IO) { repo.getAllCategoriesOnce() }

    suspend fun getSubCategoriesOnce(categoryId: Int): List<BudgetSubCategory> =
        withContext(Dispatchers.IO) { repo.getSubCategoriesOnce(categoryId) }

    fun previousMonth() {
        val cal = (_currentMonth.value ?: Calendar.getInstance()).clone() as Calendar
        cal.add(Calendar.MONTH, -1)
        _currentMonth.value = cal
    }

    fun nextMonth() {
        val cal = (_currentMonth.value ?: Calendar.getInstance()).clone() as Calendar
        cal.add(Calendar.MONTH, 1)
        _currentMonth.value = cal
    }

    fun insert(entry: BudgetEntry) = viewModelScope.launch { repo.insert(entry) }
    fun delete(entry: BudgetEntry) = viewModelScope.launch { repo.delete(entry) }

    fun insertCategory(c: BudgetCategory) = viewModelScope.launch { repo.insertCategory(c) }
    fun deleteCategory(c: BudgetCategory) = viewModelScope.launch { repo.deleteCategory(c) }
    fun insertSubCategory(s: BudgetSubCategory) = viewModelScope.launch { repo.insertSubCategory(s) }
    fun updateSubCategory(s: BudgetSubCategory) = viewModelScope.launch { repo.updateSubCategory(s) }
    fun deleteSubCategory(s: BudgetSubCategory) = viewModelScope.launch { repo.deleteSubCategory(s) }

    private fun monthStart(cal: Calendar) = (cal.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun monthEnd(cal: Calendar) = (cal.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
