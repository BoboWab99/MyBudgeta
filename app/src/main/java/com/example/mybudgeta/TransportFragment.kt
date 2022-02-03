package com.example.mybudgeta

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class TransportFragment : Fragment() {

    /*private lateinit var expenseAdapter: ExpenseAdapter*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expensesRv: RecyclerView = view.findViewById(R.id.rv_transport_expenses)
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        val context = requireContext()

        // load data
        Helper.loadData(expensesRv)

        // fab
        fab.setOnClickListener {
            Helper.showExpenseDialog(context)
        }
    }

}