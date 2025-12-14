package com.example.dokusochka

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var backButton: ImageView
    private lateinit var exportClientsCheckbox: CheckBox
    private lateinit var exportOrdersCheckbox: CheckBox
    private lateinit var exportMenuCheckbox: CheckBox
    private lateinit var exportStatisticsCheckbox: CheckBox
    private lateinit var exportButton: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var resultLayout: LinearLayout
    private lateinit var resultText: TextView
    private lateinit var shareButton: LinearLayout  // Изменено с Button на LinearLayout
    private lateinit var openButton: LinearLayout   // Изменено с Button на LinearLayout

    private var exportedFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        databaseHelper = DatabaseHelper(this)
        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        exportClientsCheckbox = findViewById(R.id.exportClientsCheckbox)
        exportOrdersCheckbox = findViewById(R.id.exportOrdersCheckbox)
        exportMenuCheckbox = findViewById(R.id.exportMenuCheckbox)
        exportStatisticsCheckbox = findViewById(R.id.exportStatisticsCheckbox)
        exportButton = findViewById(R.id.exportButton)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        resultLayout = findViewById(R.id.resultLayout)
        resultText = findViewById(R.id.resultText)
        shareButton = findViewById(R.id.shareButton)  // Теперь LinearLayout
        openButton = findViewById(R.id.openButton)    // Теперь LinearLayout

        // Скрываем результат и прогресс по умолчанию
        resultLayout.visibility = android.view.View.GONE
        progressBar.visibility = android.view.View.GONE
        progressText.visibility = android.view.View.GONE
    }

    private fun setupClickListeners() {
        // Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        // Кнопка экспорта
        exportButton.setOnClickListener {
            if (!isAnyCheckboxSelected()) {
                Toast.makeText(this, "Выберите данные для экспорта", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startExport()
        }

        // Кнопка поделиться
        shareButton.setOnClickListener {
            if (exportedFiles.isNotEmpty()) {
                shareFiles(exportedFiles)
            }
        }

        // Кнопка открыть
        openButton.setOnClickListener {
            if (exportedFiles.isNotEmpty()) {
                openFileInExplorer(exportedFiles.first())
            }
        }
    }

    private fun isAnyCheckboxSelected(): Boolean {
        return exportClientsCheckbox.isChecked ||
                exportOrdersCheckbox.isChecked ||
                exportMenuCheckbox.isChecked ||
                exportStatisticsCheckbox.isChecked
    }

    private fun startExport() {
        // Сбрасываем предыдущие результаты
        exportedFiles.clear()
        resultLayout.visibility = android.view.View.GONE

        // Показываем прогресс
        progressBar.visibility = android.view.View.VISIBLE
        progressText.visibility = android.view.View.VISIBLE
        progressBar.progress = 0

        // Запускаем экспорт в фоновом потоке
        Thread {
            val totalSteps = getSelectedCount()
            var currentStep = 0

            runOnUiThread {
                progressText.text = "Начинаем экспорт..."
            }

            // Экспорт клиентов
            if (exportClientsCheckbox.isChecked) {
                runOnUiThread {
                    progressText.text = "Экспортируем клиентов..."
                }
                try {
                    val file = databaseHelper.exportClientsToCsv()
                    exportedFiles.add(file)
                    currentStep++
                    runOnUiThread {
                        progressBar.progress = (currentStep * 100 / totalSteps)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Экспорт заказов
            if (exportOrdersCheckbox.isChecked) {
                runOnUiThread {
                    progressText.text = "Экспортируем заказы..."
                }
                try {
                    val file = databaseHelper.exportOrdersToCsv()
                    exportedFiles.add(file)
                    currentStep++
                    runOnUiThread {
                        progressBar.progress = (currentStep * 100 / totalSteps)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Экспорт меню
            if (exportMenuCheckbox.isChecked) {
                runOnUiThread {
                    progressText.text = "Экспортируем меню..."
                }
                try {
                    val file = databaseHelper.exportMenuToCsv()
                    exportedFiles.add(file)
                    currentStep++
                    runOnUiThread {
                        progressBar.progress = (currentStep * 100 / totalSteps)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Экспорт статистики
            if (exportStatisticsCheckbox.isChecked) {
                runOnUiThread {
                    progressText.text = "Экспортируем статистику..."
                }
                try {
                    val file = databaseHelper.exportStatisticsToCsv()
                    exportedFiles.add(file)
                    currentStep++
                    runOnUiThread {
                        progressBar.progress = (currentStep * 100 / totalSteps)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Завершение
            runOnUiThread {
                progressBar.visibility = android.view.View.GONE
                progressText.visibility = android.view.View.GONE

                if (exportedFiles.isNotEmpty()) {
                    showResult(exportedFiles)
                } else {
                    Toast.makeText(this, "Ошибка при экспорте", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun getSelectedCount(): Int {
        var count = 0
        if (exportClientsCheckbox.isChecked) count++
        if (exportOrdersCheckbox.isChecked) count++
        if (exportMenuCheckbox.isChecked) count++
        if (exportStatisticsCheckbox.isChecked) count++
        return count
    }

    private fun showResult(files: List<File>) {
        resultLayout.visibility = android.view.View.VISIBLE

        val totalSize = files.sumOf { it.length() } / 1024 // в КБ
        val fileNames = files.joinToString("\n") { "• ${it.name}" }

        resultText.text = "✅ Экспорт завершен!\n\n" +
                "Экспортировано файлов: ${files.size}\n" +
                "Общий размер: $totalSize КБ\n\n" +
                "Созданные файлы:\n$fileNames\n\n" +
                "Файлы сохранены в:\n${files.first().parent}"
    }

    private fun shareFiles(files: List<File>) {
        if (files.isEmpty()) return

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/csv"
            putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                ArrayList(files.map {
                    FileProvider.getUriForFile(
                        this@ExportActivity,
                        "${packageName}.fileprovider",
                        it
                    )
                })
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(intent, "Поделиться файлами"))
    }

    private fun openFileInExplorer(file: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                FileProvider.getUriForFile(
                    this@ExportActivity,
                    "${packageName}.fileprovider",
                    file
                ),
                "text/csv"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Установите приложение для просмотра CSV", Toast.LENGTH_SHORT).show()
        }
    }
}