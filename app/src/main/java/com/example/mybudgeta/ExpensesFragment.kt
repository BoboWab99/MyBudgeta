package com.example.mybudgeta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ExpensesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expensesRv: RecyclerView = view.findViewById(R.id.all_expenses_rv)
        val fab: FloatingActionButton = view.findViewById(R.id.all_expenses_fab)
        val context = requireContext()

        // load data
        Helper.loadData(expensesRv)

        // fab
        fab.setOnClickListener {
            Helper.showExpenseDialog(context, showCategorySpinner=true)
        }
    }

}