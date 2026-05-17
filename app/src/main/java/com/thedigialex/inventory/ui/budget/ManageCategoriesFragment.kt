package com.thedigialex.inventory.ui.budget

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thedigialex.inventory.R
import com.thedigialex.inventory.database.entity.BudgetCategory
import com.thedigialex.inventory.database.entity.BudgetSubCategory
import com.thedigialex.inventory.databinding.FragmentManageCategoriesBinding

class ManageCategoriesFragment : Fragment() {
    private var _binding: FragmentManageCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var catAdapter: BudgetCategoryAdapter
    private lateinit var subCatAdapter: BudgetSubCategoryAdapter
    private var selectedCategory: BudgetCategory? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        catAdapter = BudgetCategoryAdapter(
            onClick = { cat ->
                selectedCategory = cat
                catAdapter.selectedId = cat.id
                catAdapter.notifyDataSetChanged()
                binding.tvSubCatHeader.text = "${cat.name} — Sub-categories"
                viewModel.getSubCategoriesFor(cat.id).observe(viewLifecycleOwner) {
                    subCatAdapter.submitList(it)
                }
            },
            onDelete = { viewModel.deleteCategory(it) }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = catAdapter

        subCatAdapter = BudgetSubCategoryAdapter { viewModel.deleteSubCategory(it) }
        binding.rvSubCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubCategories.adapter = subCatAdapter

        viewModel.categories.observe(viewLifecycleOwner) { catAdapter.submitList(it) }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAddCategory.setOnClickListener { showAddCategoryDialog() }
        binding.btnAddSubCategory.setOnClickListener {
            val cat = selectedCategory
            if (cat == null) Toast.makeText(context, "Select a category first", Toast.LENGTH_SHORT).show()
            else showAddSubCategoryDialog(cat)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_category, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val rgType = dialogView.findViewById<RadioGroup>(R.id.rgType)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Category")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().ifBlank { return@setPositiveButton }
                val type = if (rgType.checkedRadioButtonId == R.id.rbIncome) "income" else "expense"
                viewModel.insertCategory(BudgetCategory(name = name, type = type))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddSubCategoryDialog(category: BudgetCategory) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_sub_category, null)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etAmount = dialogView.findViewById<EditText>(R.id.etAmount)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New sub-category for \"${category.name}\"")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().ifBlank { return@setPositiveButton }
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.insertSubCategory(BudgetSubCategory(categoryId = category.id, name = name, budgetedAmount = amount))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
