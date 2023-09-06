package com.example.project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
// MainActivity that extends AppCompatActivity (Android class for handling activities)

class MainActivity : AppCompatActivity() {
    private lateinit var displayTextView: TextView
    private var firstNumber: Double? = null
    private var currentOperation: String? = null
    private var newOperation = false
    // Saving instance state (like during screen rotation)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("firstNumber", firstNumber ?: Double.NaN)
        outState.putString("currentOperation", currentOperation)
        outState.putBoolean("newOperation", newOperation)
        outState.putString("displayText", displayTextView.text.toString())
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
            firstNumber = if (savedInstanceState.containsKey("firstNumber")) {
                val value = savedInstanceState.getDouble("firstNumber")
                if (value.isNaN()) null else value
            } else {
                null
            }
            currentOperation = savedInstanceState.getString("currentOperation")
            newOperation = savedInstanceState.getBoolean("newOperation")
            displayTextView.text = savedInstanceState.getString("displayText")
        }
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
    // Helper function to reset the calculator

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
        firstNumber = null
        currentOperation = null
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
