package com.example.the_chance_calculator_task

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.the_chance_calculator_task.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var isArabic: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.equation.movementMethod = ScrollingMovementMethod.getInstance()

        addCallBacks()

    }

    private fun addCallBacks() {
        binding.clearButton.setOnClickListener {
            binding.equation.text = ""
            binding.history.text = ""
        }

        binding.backspaceButton.setOnClickListener {
            val length = binding.equation.length()
            if (length > 0) {
                binding.equation.text = binding.equation.text.substring(0, length - 1)
            }
        }

        binding.dotButton.setOnClickListener {
            if (binding.equation.text.isNotEmpty() && binding.equation.text.last() == '.') return@setOnClickListener
            else binding.equation.append(".")
        }

        binding.toggleSign.setOnClickListener {
            if (binding.equation.text.isNotEmpty())
                binding.equation.text = toggleSign(binding.equation.text.toString())
        }

        binding.equalButton.setOnClickListener {
            val result = calculateResult(binding.equation.text.toString().replace("x", "*"))
            if (result == INVALID_OPERATION) {
                Toast.makeText(this, INVALID_OPERATION, Toast.LENGTH_LONG).show()
            } else if (result == INVALID_EXPRESSION) {
                Toast.makeText(
                    this,
                    INVALID_EXPRESSION,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.history.text = binding.equation.text
                binding.equation.text = result
            }
        }
    }


    fun onClickNumber(view: View) {
        val currentNumber = (view as AppCompatButton).text.toString()
        binding.equation.append(currentNumber)
    }

    fun onClickOperation(view: View) {
        val operation = (view as AppCompatButton).text.toString()
        binding.equation.append(operation)
    }

    private fun toggleSign(expression: String): String {

        val regex = Regex("([+\\-*/])?\\d+(\\.\\d+)?$")
        val match = regex.find(expression) ?: return expression

        val matchedText = match.value
        val startIndex = match.range.first

        return if (startIndex > 0 && expression[startIndex - 1] == '-') {
            expression.removeRange(startIndex - 1..match.range.last)
                .plus(matchedText)
        } else {
            expression.removeRange(match.range)
                .plus("-$matchedText")
        }
    }

    private fun calculateResult(expression: String): String {
        return try {
            val normalizedExpression = convertArabicToEnglish(expression)
            val result = ExpressionBuilder(normalizedExpression).build().evaluate().toString()
            if (isArabic) convertEnglishToArabic(result)
            else result
        } catch (e: ArithmeticException) {
            INVALID_OPERATION
        } catch (e: Exception) {
            INVALID_EXPRESSION
        }
    }

    private fun convertArabicToEnglish(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishDigits = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        var result = input
        arabicDigits.forEachIndexed { index, c ->
            result = result.replace(c, englishDigits[index])
        }
        isArabic = result != input
        return result
    }

    private fun convertEnglishToArabic(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        val englishDigits = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

        var result = input
        englishDigits.forEachIndexed { index, c ->
            result = result.replace(c, arabicDigits[index])
        }
        return result
    }

    companion object {
        const val INVALID_EXPRESSION = "عملية غير صحيحه تاكد من الادخال ثم عاود المحاولة مرة اخرى"
        const val INVALID_OPERATION = "لا يمكنك القسمه على صفر :) "
    }
}