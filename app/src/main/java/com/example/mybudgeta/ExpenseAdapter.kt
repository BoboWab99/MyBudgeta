package com.example.mybudgeta

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.expense.view.*


class ExpenseAdapter(
    private val expensesList: MutableList<Expense>
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {
    // holds an expense instance components
    class ExpenseViewHolder(expenseView: View) : RecyclerView.ViewHolder(expenseView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val expenseView = LayoutInflater.from(parent.context)
            .inflate(R.layout.expense, parent, false)
        return ExpenseViewHolder(expenseView)
    }

    fun toggleSelectedExpenseBg(itemView: View) {
        // add/remove highlight to/from (un)selected expense
        /*itemView.setBackgroundColor(R.color.black_lighter_10)*/
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currExpense = expensesList[position]
        val keys = Helper.keys
        val currKey = keys[position]

        holder.itemView.apply {
            tv_expense_desc.text = currExpense.title
            tv_expense_cost.text = currExpense.cost.toString()
            tv_expense_category.text = currExpense.category
            tv_expense_date.text = currExpense.date

            btn_expense_details.setOnClickListener {
                viewDetails(context, currKey)
            }
            expense_details_layout.setOnClickListener {
                viewDetails(context, currKey)
            }
            /*cb_expense_paid.setOnCheckedChangeListener{_, isChecked ->
                toggleSelectedExpenseBg(holder.itemView)
            }*/
        }
        /*holder.itemView.tv_expense_desc.text = currExpense.title
        holder.itemView.tv_expense_cost.text = currExpense.cost.toString()*/
    }

    override fun getItemCount(): Int {
        return expensesList.size
    }

    private fun viewDetails(context: Context, expenseId: String) {
        val intent = Intent(context, ExpenseDetailsActivity::class.java)
        intent.putExtra("expenseId", expenseId)
        context.startActivity(intent)
    }

}