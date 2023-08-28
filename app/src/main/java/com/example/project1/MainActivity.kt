package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var displayTextView: TextView
    private var firstNumber: Double? = null
    private var currentOperation: String? = null
    private var newOperation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayTextView = findViewById(R.id.displayTextView)

        val numberButtons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )

        for (id in numberButtons) {
            val button: Button = findViewById(id)
            button.setOnClickListener {
                if (newOperation) {
                    displayTextView.text = ""
                    newOperation = false
                }
                onNumberClicked((it as Button).text.toString())
            }
        }

        val operations = mapOf(
            R.id.buttonPlus to "+",
            R.id.buttonMinus to "-",
            R.id.buttonMultiply to "X",
            R.id.buttonDivide to "/"
        )

        for ((id, operation) in operations) {
            val button: Button = findViewById(id)
            button.setOnClickListener { onOperationClicked(operation) }
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.buttonC).setOnClickListener { resetCalculator() }
        findViewById<Button>(R.id.buttonDot).setOnClickListener { onDotClicked() }
        findViewById<Button>(R.id.buttonPlusMinus).setOnClickListener { toggleSign() }
        findViewById<Button>(R.id.buttonPercent).setOnClickListener { percentage() }
    }

    private fun onNumberClicked(number: String) {
        if (displayTextView.text == "0" || newOperation) {
            displayTextView.text = number
            newOperation = false
        } else {
            displayTextView.text = "${displayTextView.text}$number"
        }
    }

    private fun onDotClicked() {
        if (!displayTextView.text.contains(".")) {
            displayTextView.text = "${displayTextView.text}."
        }
    }

    private fun onOperationClicked(operation: String) {
        firstNumber = displayTextView.text.toString().toDoubleOrNull()
        currentOperation = operation
        newOperation = true
    }

    private fun calculateResult() {
        val secondNumber = displayTextView.text.toString().toDoubleOrNull()
        if (firstNumber != null && currentOperation != null && secondNumber != null) {
            val result = when (currentOperation) {
                "+" -> firstNumber!! + secondNumber
                "-" -> firstNumber!! - secondNumber
                "X" -> firstNumber!! * secondNumber
                "/" -> if (secondNumber != 0.0) firstNumber!! / secondNumber else null
                else -> null
            }

            displayTextView.text = formatResult(result)
            firstNumber = result
            currentOperation = null
            newOperation = true
        }
    }

    private fun formatResult(result: Double?): String {
        if (result == null) return "Error"

        return if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
    }

    private fun resetCalculator() {
        firstNumber = null
        currentOperation = null
        displayTextView.text = "0"
    }

    private fun toggleSign() {
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null) {
            displayTextView.text = (-currentValue).toString()
        }
    }

    private fun percentage() {
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null) {
            displayTextView.text = (currentValue / 100).toString()
        }
    }
}
