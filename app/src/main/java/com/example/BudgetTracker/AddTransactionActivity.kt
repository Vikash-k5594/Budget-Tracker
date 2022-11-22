package com.example.BudgetTracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        var label1=findViewById<EditText>(R.id.labelInput)
        var amount1=findViewById<EditText>(R.id.amountInput)
        var description1=findViewById<EditText>(R.id.descriptionInput)
        var addtransaction=findViewById<Button>(R.id.addtransaction)
        var labelLayout= findViewById<TextInputLayout>(R.id.labelLayout)
        var amountLayout= findViewById<TextInputLayout>(R.id.amountLayout)


        addtransaction.setOnClickListener {

            val label= label1.text.toString()
            val description=description1.text.toString()
            val amount= amount1.text.toString().toDoubleOrNull()

            if (label.isEmpty()){
                labelLayout.error = "Please enter a valid label"
            }
            else if (amount==null){
                amountLayout.error="Please enter a valid amount"
            }
            else{
                val transact= Transact(0,label,amount,description)
                insert(transact)
            }
        }

        label1.addTextChangedListener {
            if (it!!.count()>0){
                labelLayout.error = null
            }
        }

            amount1.addTextChangedListener {
                if (it!!.count()>0){
                    amountLayout.error = null
                }
            }

        var close= findViewById<ImageButton>(R.id.close_btn)
         close.setOnClickListener {
             finish()
         }
        }

    private fun insert(transact: Transact){
        val  db= Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().insertAll(transact)
            finish()
        }
    }
}
