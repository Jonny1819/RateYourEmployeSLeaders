package com.example.renner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import android.content.SharedPreferences;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class BackupActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        sharedPreferences = getSharedPreferences("LeaderAssessment", MODE_PRIVATE);

        Button saveButton = findViewById(R.id.btn_save_excel);
        Button loadButton = findViewById(R.id.btn_load_excel);
        Button backButton = findViewById(R.id.btn_back);

        saveButton.setOnClickListener(v -> selectFileForSaving());
        loadButton.setOnClickListener(v -> browseForFile());
        backButton.setOnClickListener(v -> finish());
    }

    private void selectFileForSaving() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.putExtra(Intent.EXTRA_TITLE, "leader_backup.xlsx");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        fileSaverLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> fileSaverLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            saveToExcel(uri);
                        }
                    }
                }
            });

    private void saveToExcel(Uri uri) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leader Data");

            Row headerRow = sheet.createRow(0);
            Set<String> employees = sharedPreferences.getStringSet("employees", new HashSet<>());
            Set<String> criteria = sharedPreferences.getStringSet("criteria", new HashSet<>());

            int colIndex = 1;
            for (String criterion : criteria) {
                Cell cell = headerRow.createCell(colIndex++);
                cell.setCellValue(criterion);
            }

            int rowIndex = 1;
            for (String employee : employees) {
                Row row = sheet.createRow(rowIndex++);
                Cell employeeCell = row.createCell(0);
                employeeCell.setCellValue(employee);

                colIndex = 1;
                for (String criterion : criteria) {
                    String key = employee + "_" + criterion;
                    int score = sharedPreferences.getInt(key + "_total", 0);
                    int count = sharedPreferences.getInt(key + "_count", 0);
                    Cell cell = row.createCell(colIndex++);
                    cell.setCellValue(score + "," + count);
                }
            }

            try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                workbook.write(os);
            }

            Toast.makeText(this, getString(R.string.backup_saved_successfully), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_saving_backup), Toast.LENGTH_LONG).show();
        }
    }

    private void loadFromExcel(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);

            Set<String> employees = new HashSet<>();
            Set<String> criteria = new HashSet<>();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Row headerRow = sheet.getRow(0);
            for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                criteria.add(getCellValue(headerRow.getCell(i)));
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String employee = getCellValue(row.getCell(0));
                employees.add(employee);

                for (int j = 1; j < row.getLastCellNum(); j++) {
                    String criterion = getCellValue(headerRow.getCell(j));
                    String value = getCellValue(row.getCell(j));
                    if (value.contains(",")) {
                        String[] parts = value.split(",");
                        int score = Integer.parseInt(parts[0].trim());
                        int count = Integer.parseInt(parts[1].trim());
                        String key = employee + "_" + criterion;
                        editor.putInt(key + "_total", score);
                        editor.putInt(key + "_count", count);
                    } else {
                        // handle improperly formatted cells
                        String key = employee + "_" + criterion;
                        editor.putInt(key + "_total", 0);
                        editor.putInt(key + "_count", 0);
                    }
                }
            }

            editor.putStringSet("employees", employees);
            editor.putStringSet("criteria", criteria);
            editor.apply();

            Toast.makeText(this, getString(R.string.backup_loaded_successfully), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_loading_backup), Toast.LENGTH_LONG).show();
        }
    }

    private void browseForFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            loadFromExcel(uri);
                        }
                    }
                }
            });

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING:
                        return cell.getRichStringCellValue().getString();
                    case NUMERIC:
                        return String.valueOf((int) cell.getNumericCellValue());
                    default:
                        return "";
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
