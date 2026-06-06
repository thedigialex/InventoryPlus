package com.thedigialex.inventory.ui.budget

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thedigialex.inventory.R
import com.thedigialex.inventory.database.entity.BudgetEntry
import com.thedigialex.inventory.databinding.FragmentBudgetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var adapter: BudgetAdapter

    private val monthYearFmt = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = BudgetAdapter { viewModel.delete(it) }
        binding.rvEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEntries.adapter = adapter

        viewModel.currentMonth.observe(viewLifecycleOwner) { cal ->
            binding.tvMonth.text = monthYearFmt.format(cal.time)
            val now = Calendar.getInstance()
            val isCurrentMonth = cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            binding.fab.visibility = if (isCurrentMonth) View.VISIBLE else View.GONE
        }

        var currentCarryover = 0.0

        fun updateSummary(entries: List<BudgetEntry>?, carryover: Double) {
            val list = entries ?: return
            val (incomeList, expenseList) = list.partition { it.type == "income" }
            val income = incomeList.sumOf { it.amount }
            val expenses = expenseList.sumOf { it.amount }
            binding.tvIncome.text = "Income: +${"%.2f".format(income)}"
            binding.tvExpenses.text = "Expenses: -${"%.2f".format(expenses)}"
            binding.tvBalance.text = "Balance: ${"%.2f".format(carryover + income - expenses)}"
            if (carryover != 0.0) {
                val sign = if (carryover >= 0) "+" else ""
                binding.tvCarryover.text = "Carried Over: $sign${"%.2f".format(carryover)}"
                binding.tvCarryover.visibility = android.view.View.VISIBLE
            } else {
                binding.tvCarryover.visibility = android.view.View.GONE
            }
        }

        viewModel.entries.observe(viewLifecycleOwner) { entries ->
            updateSummary(entries, currentCarryover)
        }

        viewModel.carryoverBalance.observe(viewLifecycleOwner) { carryover ->
            currentCarryover = carryover ?: 0.0
            updateSummary(viewModel.entries.value, currentCarryover)
        }

        viewModel.groupedEntries.observe(viewLifecycleOwner) { adapter.submitList(it) }

        binding.btnPrev.setOnClickListener { viewModel.previousMonth() }
        binding.btnNext.setOnClickListener { viewModel.nextMonth() }
        binding.btnManageCategories.setOnClickListener {
            findNavController().navigate(R.id.action_budget_to_manage_categories)
        }
        binding.fab.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val cats = viewModel.getCategoriesOnce()
                withContext(Dispatchers.Main) { showAddEntryDialog(cats) }
            }
        }
    }

    private fun showAddEntryDialog(allCats: List<com.thedigialex.inventory.database.entity.BudgetCategory>) {
        if (allCats.isEmpty()) {
            Toast.makeText(context, "Add categories first via 'Categories' button", Toast.LENGTH_LONG).show()
            return
        }
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_budget, null)
        val rgType = dialogView.findViewById<RadioGroup>(R.id.rgType)
        val spinnerCat = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerSubCat = dialogView.findViewById<Spinner>(R.id.spinnerSubCategory)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)

        var currentCats = allCats.filter { it.type == "expense" }
        var currentSubCats = listOf<com.thedigialex.inventory.database.entity.BudgetSubCategory>()

        fun refreshCatSpinner() {
            val type = if (rgType.checkedRadioButtonId == R.id.rbIncome) "income" else "expense"
            currentCats = allCats.filter { it.type == type }
            spinnerCat.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner,
                currentCats.map { it.name }).also { it.setDropDownViewResource(R.layout.item_spinner_dropdown) }
        }

        rgType.setOnCheckedChangeListener { _, _ -> refreshCatSpinner() }
        refreshCatSpinner()

        spinnerCat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                if (currentCats.isEmpty()) return
                viewLifecycleOwner.lifecycleScope.launch {
                    currentSubCats = viewModel.getSubCategoriesOnce(currentCats[pos].id)
                    withContext(Dispatchers.Main) {
                        val items = listOf("(none)") + currentSubCats.map { it.name }
                        spinnerSubCat.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, items)
                            .also { it.setDropDownViewResource(R.layout.item_spinner_dropdown) }
                    }
                }
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Budget Entry")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                val type = if (rgType.checkedRadioButtonId == R.id.rbIncome) "income" else "expense"
                val catPos = spinnerCat.selectedItemPosition
                if (currentCats.isEmpty() || catPos < 0) return@setPositiveButton
                val cat = currentCats[catPos]
                val subPos = spinnerSubCat.selectedItemPosition - 1
                val sub = if (subPos >= 0 && currentSubCats.isNotEmpty()) currentSubCats[subPos] else null
                viewModel.insert(BudgetEntry(
                    subCategoryId = sub?.id ?: 0,
                    categoryName = cat.name,
                    subCategoryName = sub?.name ?: "",
                    amount = amount,
                    description = etDescription.text.toString(),
                    date = System.currentTimeMillis(),
                    type = type
                ))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
