package com.example.mybudgeta

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ExpenseDetailsActivity : AppCompatActivity() {
    private val db = Helper.db
    private val auth = Helper.auth
    private lateinit var currExp: Expense
    private lateinit var dbExpenses: DatabaseReference

    private lateinit var expTitleTv: TextView
    private lateinit var expCostTv: TextView
    private lateinit var expCategoryTv: TextView
    private lateinit var expDateTv: TextView
    private lateinit var expDeleteBtn: Button
    private lateinit var expUpdateBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_details)

        expTitleTv = findViewById(R.id.exp_details_tv_title)
        expCostTv = findViewById(R.id.exp_details_tv_cost)
        expCategoryTv = findViewById(R.id.exp_details_tv_category)
        expDateTv = findViewById(R.id.exp_details_tv_date)
        expDeleteBtn = findViewById(R.id.exp_details_btn_delete)
        expUpdateBtn = findViewById(R.id.exp_details_btn_edit)

        val expenseId = intent.getStringExtra("expenseId")
        dbExpenses = db.child(Helper.DB_REF_EXPENSES).child(auth.currentUser!!.uid)

        // load data
        loadExpData(expenseId)

        // update
        expUpdateBtn.setOnClickListener {
            Helper.showExpenseDialog(this, currExp, true, expenseId!!)
        }

        // delete
        expDeleteBtn.setOnClickListener {
            dbExpenses.child(expenseId!!).removeValue()
            Helper.showToast(this, "Expense deleted!")
            this.finish()
        }

        // back
        findViewById<ImageView>(R.id.app_back_icon).setOnClickListener {
            this.finish()
        }
    }

    private fun loadExpData(expenseId: String?) {
        val query: Query = dbExpenses.child(expenseId!!)
        val context: Context = this

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists())
                    return

                val title = snapshot.child(Helper.EXPENSE_ATTR_TITLE).value.toString()
                val cost = snapshot.child(Helper.EXPENSE_ATTR_COST).value.toString().toDouble()
                val category = snapshot.child(Helper.EXPENSE_ATTR_CATEGORY).value.toString()
                val date = snapshot.child(Helper.EXPENSE_ATTR_DATE).value.toString()
                val uid = snapshot.child(Helper.EXPENSE_ATTR_UID).value.toString()

                currExp = Expense(title=title, cost=cost, category=category, date=date, uid=uid)

                expTitleTv.text = currExp.title
                expCostTv.text = currExp.cost.toString()
                expCategoryTv.text = currExp.category
                expDateTv.text = currExp.date
            }

            override fun onCancelled(error: DatabaseError) {
                Helper.showToast(context, "read expId=$expenseId:onCancelled")
            }
        })
    }

}