package com.example.mybudgeta

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import android.widget.ArrayAdapter

import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*


class Helper {
    companion object {
        // *** VARIABLES & CONSTANTS

        val db: DatabaseReference = FirebaseDatabase.getInstance().reference
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val keys = arrayListOf<String>()
        lateinit var adapter: ExpenseAdapter

        const val DB_REF_USERS = "users"
        const val DB_REF_EXPENSES = "expenses"

        const val FRAGMENT_LABEL_LUNCH = "Lunch"
        const val FRAGMENT_LABEL_TRANSPORT = "Transport"
        const val FRAGMENT_LABEL_SNACKS = "Snacks"
        const val FRAGMENT_LABEL_OTHER = "Other"

        const val USER_ATTR_USERNAME = "username"

        const val EXPENSE_ATTR_TITLE = "title"
        const val EXPENSE_ATTR_COST = "cost"
        const val EXPENSE_ATTR_CATEGORY = "category"
        const val EXPENSE_ATTR_DATE = "date"
        const val EXPENSE_ATTR_UID = "uid"

        val SPINNER_CHOICES = mutableListOf(
            FRAGMENT_LABEL_LUNCH,
            FRAGMENT_LABEL_TRANSPORT,
            FRAGMENT_LABEL_SNACKS,
            FRAGMENT_LABEL_OTHER
        )

        // *** FUNCTIONS ***

        fun showExpenseDialog(
            context: Context,
            expense: Expense? = null,
            showCategorySpinner: Boolean = false,
            postKey: String? = null
        ) {
            // *** Make layout ***
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(40, 20, 40, 10)

            val expTitleTf = EditText(context)
            expTitleTf.hint = "Expense"
            expTitleTf.inputType = InputType.TYPE_CLASS_TEXT

            val expCostTf = EditText(context)
            expCostTf.hint = "Cost"
            expCostTf.inputType = InputType.TYPE_CLASS_NUMBER

            layout.addView(expTitleTf)
            addSpace(context, layout)
            layout.addView(expCostTf)

            var expCategorySpinner: Spinner? = null

            if(showCategorySpinner) {
                expCategorySpinner = Spinner(context)

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                expCategorySpinner.layoutParams = layoutParams

                val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    SPINNER_CHOICES
                )
                expCategorySpinner.adapter = spinnerAdapter

                addSpace(context, layout)
                layout.addView(expCategorySpinner)
            }
            if(expense != null) {
                expTitleTf.setText(expense.title)
                expCostTf.setText(expense.cost.toString())
                if(showCategorySpinner)
                    expCategorySpinner!!.setSelection(SPINNER_CHOICES.indexOf(expense.category))
                expTitleTf.requestFocus()
            }
            // *** End layout ***

            val dialogTitle = if(postKey != null) "Update expense" else "Add expense"

            val dialog: AlertDialog = AlertDialog.Builder(context)
                .setView(layout)
                .setTitle(dialogTitle)
                .setCancelable(false)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()

            // override click listener handlers for dialog buttons
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                dialog.dismiss()
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val closeDialog: Boolean
                val expTitle = trimText(expTitleTf)
                val expCost = trimText(expCostTf)
                val expCategory = if(showCategorySpinner)
                    expCategorySpinner?.selectedItem.toString() else getCategory()

                if(expTitle.isEmpty() || expCost.isEmpty() || expCategory.isEmpty()) {
                    showToast(context, "Not added! Fill all fields")
                    return@setOnClickListener
                } else
                    closeDialog = true

                val currUser = auth.currentUser
                val uid = currUser!!.uid
                val currDate  = currDateString()
                val exp = Expense(expTitle, expCost.toDouble(), expCategory, uid, currDate)

                if(postKey != null)
                    updateData(context, exp, postKey)
                else
                    saveData(context, exp)

                if(closeDialog)
                    dialog.dismiss()
            }
        }

        fun loadData(rv: RecyclerView) {
            val dbExpenses = db.child(DB_REF_EXPENSES).child(auth.currentUser!!.uid)
            val currFragment = getCurrFragment()
            val query: Query

            if(currFragment is LunchFragment)
                query = dbExpenses.orderByChild(EXPENSE_ATTR_CATEGORY).equalTo(FRAGMENT_LABEL_LUNCH)
            else if(currFragment is TransportFragment)
                query = dbExpenses.orderByChild(EXPENSE_ATTR_CATEGORY).equalTo(FRAGMENT_LABEL_TRANSPORT)
            else if(currFragment is SnacksFragment)
                query = dbExpenses.orderByChild(EXPENSE_ATTR_CATEGORY).equalTo(FRAGMENT_LABEL_SNACKS)
            else if(currFragment is OtherFragment)
                query = dbExpenses.orderByChild(EXPENSE_ATTR_CATEGORY).equalTo(FRAGMENT_LABEL_OTHER)
            else
                query = dbExpenses

            query.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) {
                        showToast(rv.context, "No data found!")
                        return
                    }
                    val expenseList = arrayListOf<Expense>()
                    keys.clear()

                    for(data in snapshot.children) {
                        keys.add(data.key!!)
                        val title = data.child(EXPENSE_ATTR_TITLE).value.toString()
                        val cost = data.child(EXPENSE_ATTR_COST).value.toString().toDouble()
                        val category = data.child(EXPENSE_ATTR_CATEGORY).value.toString()
                        val date = data.child(EXPENSE_ATTR_DATE).value.toString()
                        val uid = data.child(EXPENSE_ATTR_UID).value.toString()

                        val exp = Expense(title=title, cost=cost, category=category, date=date, uid=uid)
                        expenseList.add(exp)
                    }

                    adapter = ExpenseAdapter(expenseList)
                    rv.adapter = adapter
                    rv.layoutManager = LinearLayoutManager(rv.context)
                    rv.setHasFixedSize(true)
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast(rv.context, "loadData:onCancelled -> Database error!")
                }
            })
        }

        private fun saveData(context: Context, expense: Expense) {
            val dbExpenses = db.child(DB_REF_EXPENSES).child(auth.currentUser!!.uid)
            dbExpenses.push().setValue(expense).addOnSuccessListener {
                showToast(context, "Successfully added!")
            }.addOnFailureListener {
                showToast(context, "Failed to add! An error occurred!")
            }
        }

        private fun updateData(context: Context, expense: Expense, postKey: String) {
            val dbExpenses = db.child(DB_REF_EXPENSES).child(auth.currentUser!!.uid)
            dbExpenses.child(postKey).setValue(expense).addOnSuccessListener {
                showToast(context, "Successfully updated!")
            }.addOnFailureListener {
                showToast(context, "Failed to update! An error occurred!")
            }
        }

        private fun getCategory(): String {
            val currFragment = getCurrFragment()
            val category: String

            if(currFragment is LunchFragment)
                category = FRAGMENT_LABEL_LUNCH
            else if(currFragment is TransportFragment)
                category = FRAGMENT_LABEL_TRANSPORT
            else if(currFragment is SnacksFragment)
                category = FRAGMENT_LABEL_SNACKS
            else if(currFragment is OtherFragment)
                category = FRAGMENT_LABEL_OTHER
            else
                category = ""
            return category
        }

        private fun getCurrFragment() : Fragment? {
            val navHostFragment: Fragment? = MainActivity.getNavHostFragment()
            return navHostFragment?.childFragmentManager?.fragments?.get(0)
        }

        fun showToast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun currDateString() : String {
            val calendar = Calendar.getInstance()
            val currDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return currDate.format(calendar.time)
        }

        fun addSpace(context: Context, layout: ViewGroup) {
            val space = Space(context)
            space.minimumHeight = 20
            layout.addView(space)
        }

        fun trimText(inputField: TextView): String {
            return inputField.text.toString().trim()
        }

    }

}