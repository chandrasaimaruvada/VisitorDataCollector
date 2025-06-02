package com.app.visitorform.controller;

import com.app.visitorform.service.GoogleSheetService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private GoogleSheetService sheetService;

    @GetMapping("/admin/login")
    public String loginPage() {
        return "login"; // matches .loginPage("/admin/login") in SecurityConfig
    }

    @PostMapping("/login")
    public String loginError() {
        // This can be empty - Spring Security will handle actual auth
        return "redirect:/login?error";
    }

//    @GetMapping("/admin/dashboard")
//    public String showDashboard(Model model) {
//        model.addAttribute("entries", sheetService.getAllData());
//        return "admin-dashboard";
//    }

    @GetMapping("/admin/dashboard")
    public String showDashboard(
            @RequestParam(value = "search", required = false) String search,
            Model model) throws IOException {

        List<List<Object>> sheetData = sheetService.getAllData();

        if (sheetData == null || sheetData.size() < 2) {
            model.addAttribute("headers", List.of());
            model.addAttribute("rows", List.of());
            model.addAttribute("search", search);
            model.addAttribute("message", "No data available.");
            return "admin-dashboard";
        }

        List<Object> headers = sheetData.get(0);
        List<List<Object>> filteredRows = sheetData.stream()
                .skip(1)
                .filter(row -> search == null || search.isBlank() || row.toString().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("headers", headers);
        model.addAttribute("rows", filteredRows);
        model.addAttribute("search", search);

        if (filteredRows.isEmpty()) {
            model.addAttribute("message", "No matching data found.");
        }

        return "admin-dashboard";
    }



    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {}
        }

        throw new DateTimeParseException("Unrecognized date format", dateStr, 0);
    }

    @GetMapping("/admin/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam(value = "search", required = false) String search) throws IOException {
        List<List<Object>> sheetData = sheetService.getAllData();

        if (sheetData == null || sheetData.size() < 2) {
            // Return empty file or 204 No Content
            return ResponseEntity.noContent().build();
        }

        List<Object> headers = sheetData.get(0);

        List<List<Object>> filteredData = sheetData.stream()
                .skip(1)
                .filter(row -> {
                    boolean searchMatch = search == null || search.isBlank() || row.toString().toLowerCase().contains(search.toLowerCase());
                    return searchMatch;
                })
                .collect(Collectors.toList());

        // Create Excel workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Visitor Submissions");

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i).toString());
        }

        // Fill data rows
        int rowIdx = 1;
        for (List<Object> rowData : filteredData) {
            Row row = sheet.createRow(rowIdx++);
            for (int i = 0; i < rowData.size(); i++) {
                Cell cell = row.createCell(i);
                Object val = rowData.get(i);
                if (val != null) {
                    cell.setCellValue(val.toString());
                }
            }
        }

        // Autosize columns
        for (int i = 0; i < headers.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();

        byte[] excelBytes = bos.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=visitor_submissions.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }





}
