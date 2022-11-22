package com.example.BudgetTracker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.BudgetTracker.R
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: Transact

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        var label1=findViewById<EditText>(R.id.labelInput)
        var amount1=findViewById<EditText>(R.id.amountInput)
        var description1=findViewById<EditText>(R.id.descriptionInput)
        var updatebtn=findViewById<Button>(R.id.updatebtn)
        var labelLayout= findViewById<TextInputLayout>(R.id.labelLayout)
        var amountLayout= findViewById<TextInputLayout>(R.id.amountLayout)
        var rootview= findViewById<View>(R.id.rootView)

         transaction= intent.getSerializableExtra("transaction") as Transact
        label1.setText(transaction.label)
        amount1.setText(transaction.amount.toString())
        description1.setText(transaction.description)

        rootview.setOnClickListener {
            this.window.decorView.clearFocus()

            val imm= getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        updatebtn.setOnClickListener {

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
                val transact= Transact(transaction.id,label,amount,description)
                update(transact)
            }
        }

        label1.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE
            if (it!!.count()>0){
                labelLayout.error = null
            }
        }

        amount1.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE
            if (it!!.count()>0){
                amountLayout.error = null
            }
        }

        description1.addTextChangedListener {
            updatebtn.visibility= View.VISIBLE

        }


        var close= findViewById<ImageButton>(R.id.close_btn)
        close.setOnClickListener {
            finish()
        }
    }

    private fun update(transact: Transact){
        val  db= Room.databaseBuilder(this,
            AppDatabase::class.java,
            "transactions").build()

        GlobalScope.launch {
            db.transactionDao().update(transact)
            finish()
        }
    }
}
