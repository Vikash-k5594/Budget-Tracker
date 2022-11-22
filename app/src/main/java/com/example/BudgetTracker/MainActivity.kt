package com.example.BudgetTracker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var deleteTransaction:Transact
    private lateinit var transactions: List<Transact>
    private lateinit var oldTransactions: List<Transact>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var db:AppDatabase


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearlayoutManager = LinearLayoutManager(this)

        db= Room.databaseBuilder(this,
        AppDatabase::class.java,
        "transactions").build()

        recyclerView = findViewById(R.id.recycler)
        recyclerView.adapter = transactionAdapter
        recyclerView.layoutManager = linearlayoutManager

        //swipe to delete
        val itemTouchHelper= object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper= ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

        var addbtn=findViewById<FloatingActionButton>(R.id.addbtn)
        addbtn.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
     }
     private fun fetchAll(){
         GlobalScope.launch {
             transactions= db.transactionDao().getAll()

             runOnUiThread {
                 updateDashboard()
                 transactionAdapter.setData(transactions)
             }
         }
     }

     private fun updateDashboard(){
    val totalAmount= transactions.map { it.amount }.sum()
    val budgetAmount= transactions.filter { it.amount>0 }.map { it.amount }.sum()
    val expenseAmount= totalAmount-budgetAmount
         var balance=findViewById<TextView>(R.id.balance)
         var budget=findViewById<TextView>(R.id.budget)
         var expense=findViewById<TextView>(R.id.expense)

         balance.text="₹ %.2f".format(totalAmount)
         budget.text="₹ %.2f".format(budgetAmount)
         expense.text="₹ %.2f".format(expenseAmount)
     }

    private fun undoDelete() {
        GlobalScope.launch {
            db.transactionDao().insertAll(deleteTransaction)

            transactions= oldTransactions

            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun showSnackBar() {
     val view= findViewById<View>(R.id.coordinator)
        val snackbar= Snackbar.make(view,"Transaction deleted!",Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this,R.color.red))
            .setTextColor(ContextCompat.getColor(this,R.color.white))
            .show()
    }



    private fun deleteTransaction(transact: Transact)
    {
        deleteTransaction = transact
        oldTransactions= transactions

        GlobalScope.launch {
            db.transactionDao().delete(transact)

            transactions=transactions.filter { it.id    != transact.id }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackBar()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        fetchAll()
    }
}
