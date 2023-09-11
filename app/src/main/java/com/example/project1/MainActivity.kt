
package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
// MainActivity that extends AppCompatActivity (Android class for handling activities)

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
    // Saving instance state (like during screen rotation)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("displayText", displayTextView.text.toString())
        outState.putBoolean("newOperation", newOperation)
        outState.putStringArrayList("calculations", calculations.map { it.toString() } as ArrayList<String>)
    }

    // Creating the activity view

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayTextView = findViewById(R.id.displayTextView)

        // List of number buttons' IDs

        val numberButtons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9
        )
        // Setting click listeners for number buttons

        for (id in numberButtons) {
            val button: Button = findViewById(id)
            button.setOnClickListener {
                if (newOperation) {
                    displayTextView.text = ""
                    newOperation = false
                }
                Log.d("Button Click", "Button ${button.text} clicked")
                onNumberClicked((it as Button).text.toString())
            }
        }

        // Map of operation buttons and their symbols

        val operations = mapOf(
            R.id.buttonPlus to "+",
            R.id.buttonMinus to "-",
            R.id.buttonMultiply to "X",
            R.id.buttonDivide to "/"
        )

        for ((id, operation) in operations) {
            val button: Button = findViewById(id)
            button.setOnClickListener {
                Log.d("Button Click", "Button ${button.text} clicked")
                onOperationClicked(operation) }
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener {
            Log.d("Equal", "Button = Equal")
            calculateResult()
        }
        findViewById<Button>(R.id.buttonC).setOnClickListener {
            Log.d("Reset", "Button = Reset")
            resetCalculator() }
        findViewById<Button>(R.id.buttonDot).setOnClickListener {
            Log.d("Dot", "Button = Dot")
            onDotClicked() }
        findViewById<Button>(R.id.buttonPlusMinus).setOnClickListener {
            Log.d("Sign change", "Button = Sign")
            toggleSign() }
        findViewById<Button>(R.id.buttonPercent).setOnClickListener {
            Log.d("Percentage", "Button = Percentage")
            percentage() }

        findViewById<Button>(R.id.buttonSin)?.setOnClickListener {
            Log.d("Sin", "Button = Sin")
            computeSin() }
        findViewById<Button>(R.id.buttonCos)?.setOnClickListener {
            Log.d("Cos", "Button = Cos")
            computeCos() }
        findViewById<Button>(R.id.buttonTan)?.setOnClickListener {
            Log.d("Tan", "Button = Tan")
            computeTan() }
        findViewById<Button>(R.id.buttonLog10)?.setOnClickListener {
            Log.d("Log10", "Button = Log10")
            computeLog10() }
        findViewById<Button>(R.id.buttonLn)?.setOnClickListener {
            Log.d("Ln", "Button = Ln")
            computeLn() }
        if (savedInstanceState != null) {
            newOperation = savedInstanceState.getBoolean("newOperation")
            displayTextView.text = savedInstanceState.getString("displayText")
            calculations.addAll(savedInstanceState.getStringArrayList("calculations")?.map {
                when {
                    it.isDouble() -> it.toDouble()
                    it.isOperation() -> it
                    else -> null
                }
            }?.filterNotNull() ?: listOf())
        }

    }

    private fun String.isDouble(): Boolean = this.toDoubleOrNull() != null
    private fun String.isOperation(): Boolean = this in listOf("+", "-", "X", "/")
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
    // Helper function to toggle the sign of the displayed number

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
    // Setting listeners for trigonometric and logarithmic functions


    private fun toggleSign() {
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null && !currentValue.isNaN() && !currentValue.isInfinite()) {
            displayTextView.text = (-currentValue).toString()
        } else {
            displayTextView.text = "Error"
        }
    }

    private fun computeCos() {
        val value = displayTextView.text.toString().toDoubleOrNull()
        if (value != null) {
            displayTextView.text = Math.cos(Math.toRadians(value)).toString()
        }
    }
    // Helper functions for trigonometric and logarithmic calculations

    private fun computeSin() {
        val value = displayTextView.text.toString().toDoubleOrNull()
        if (value != null) {
            displayTextView.text = Math.sin(Math.toRadians(value)).toString()
        } else {
            displayTextView.text = "Error"
        }
    }
    private fun computeTan() {
        val value = displayTextView.text.toString().toDoubleOrNull()
        if (value != null && (value % 90).toInt() != 0) { // Avoiding tan(90), tan(270), etc.
            displayTextView.text = Math.tan(Math.toRadians(value)).toString()
        } else {
            displayTextView.text = "Error"
        }
    }

    private fun computeLog10() {
        val value = displayTextView.text.toString().toDoubleOrNull()
        if (value != null && value > 0) {
            displayTextView.text = Math.log10(value).toString()
        } else {
            displayTextView.text = "Error"
        }
    }

    private fun computeLn() {
        val value = displayTextView.text.toString().toDoubleOrNull()
        if (value != null && value > 0) {
            displayTextView.text = Math.log(value).toString()
        } else {
            displayTextView.text = "Error"
        }
    }


    private fun percentage() {
        val currentValue = displayTextView.text.toString().toDoubleOrNull()
        if (currentValue != null && !currentValue.isNaN() && !currentValue.isInfinite()) {
            displayTextView.text = (currentValue / 100).toString()
        } else {
            displayTextView.text = "Error"
        }
    }

}