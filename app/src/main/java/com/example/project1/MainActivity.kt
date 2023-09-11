package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

/**
 * MainActivity serves as the primary activity for the calculator application.
 * It provides a user interface to input numbers and arithmetic operations,
 * and displays the result after evaluating the arithmetic expression.
 *
 * The class handles basic arithmetic operations (+, -, *, /) and evaluates
 * expressions based on the standard order of operations (PEMDAS/BODMAS).
 *
 * <p>
 * Usage:
 * <ol>
 *   <li>Input numbers using the numeric buttons.</li>
 *   <li>Select an arithmetic operation (+, -, *, /).</li>
 *   <li>Press "=" to evaluate the expression.</li>
 *   <li>The result is displayed on the screen.</li>
 * </ol>
 * </p>
 *
 * <p>Note: This class uses a basic version of the shunting yard algorithm to evaluate expressions.</p>
 *
 * @author Matt Gacek
 * @version 1.0
 * @since 9/11/2023
 */
class MainActivity : AppCompatActivity() {

    private lateinit var displayTextView: TextView
    private val calculations = mutableListOf<Any>()  // Used to accumulate numbers and operations
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
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null) {
            calculations.add(currentValue)
            calculations.add(operation)
            newOperation = true
        }
    }

    private fun calculateResult() {
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null) {
            calculations.add(currentValue)
        }

        val values = mutableListOf<Double>()
        val ops = mutableListOf<String>()

        for (item in calculations) {
            when (item) {
                is Double -> values.add(item)
                is String -> {
                    while (ops.isNotEmpty() && hasPrecedence(item, ops.last())) {
                        values.add(applyOp(ops.removeAt(ops.size - 1), values.removeAt(values.size - 2), values.removeAt(values.size - 1)))
                    }
                    ops.add(item)
                }
            }
        }

        while (ops.isNotEmpty()) {
            values.add(applyOp(ops.removeAt(ops.size - 1), values.removeAt(values.size - 2), values.removeAt(values.size - 1)))
        }

        displayTextView.text = formatResult(values.firstOrNull())
        calculations.clear()
        newOperation = true
    }

    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if ((op1 == "X" || op1 == "/") && (op2 == "+" || op2 == "-")) {
            return false
        }
        return true
    }

    private fun applyOp(op: String, a: Double, b: Double): Double {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "X" -> a * b
            "/" -> if (b != 0.0) a / b else 0.0  // Here, you can handle divide-by-zero as needed
            else -> 0.0
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