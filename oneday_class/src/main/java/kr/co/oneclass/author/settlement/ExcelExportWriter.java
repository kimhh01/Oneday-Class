package kr.co.oneclass.author.settlement;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class ExcelExportWriter {

    private ExcelExportWriter() {
    }

    static void write(OutputStream outputStream, String sheetName, List<String> headers,
            List<List<Object>> rows) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {
            put(zip, "[Content_Types].xml", contentTypes());
            put(zip, "_rels/.rels", rootRelationships());
            put(zip, "xl/workbook.xml", workbook(sheetName));
            put(zip, "xl/_rels/workbook.xml.rels", workbookRelationships());
            put(zip, "xl/styles.xml", styles());
            put(zip, "xl/worksheets/sheet1.xml", worksheet(headers, rows));
        }
    }

    private static void put(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String contentTypes() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                + "</Types>";
    }

    private static String rootRelationships() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String workbook(String sheetName) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"" + xml(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                + "</workbook>";
    }

    private static String workbookRelationships() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
                + "</Relationships>";
    }

    private static String styles() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"맑은 고딕\"/></font>"
                + "<font><b/><sz val=\"11\"/><color rgb=\"FFFFFFFF\"/><name val=\"맑은 고딕\"/></font></fonts>"
                + "<fills count=\"3\"><fill><patternFill patternType=\"none\"/></fill>"
                + "<fill><patternFill patternType=\"gray125\"/></fill>"
                + "<fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF1F6337\"/><bgColor indexed=\"64\"/></patternFill></fill></fills>"
                + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"2\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\"/></cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "</styleSheet>";
    }

    private static String worksheet(List<String> headers, List<List<Object>> rows) {
        int[] widths = widths(headers, rows);
        StringBuilder xml = new StringBuilder(4096);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")
                .append("<sheetViews><sheetView workbookViewId=\"0\"><pane ySplit=\"1\" topLeftCell=\"A2\" activePane=\"bottomLeft\" state=\"frozen\"/></sheetView></sheetViews>")
                .append("<cols>");
        for (int index = 0; index < widths.length; index++) {
            xml.append("<col min=\"").append(index + 1).append("\" max=\"").append(index + 1)
                    .append("\" width=\"").append(widths[index]).append("\" customWidth=\"1\"/>");
        }
        xml.append("</cols><sheetData>");
        appendRow(xml, 1, headers, true);
        for (int index = 0; index < rows.size(); index++) {
            appendRow(xml, index + 2, rows.get(index), false);
        }
        xml.append("</sheetData><autoFilter ref=\"A1:")
                .append(columnName(headers.size())).append(rows.size() + 1).append("\"/>")
                .append("</worksheet>");
        return xml.toString();
    }

    private static void appendRow(StringBuilder xml, int rowNumber, List<?> values, boolean header) {
        xml.append("<row r=\"").append(rowNumber).append("\">");
        for (int index = 0; index < values.size(); index++) {
            Object value = values.get(index);
            String reference = columnName(index + 1) + rowNumber;
            if (value instanceof Number number) {
                xml.append("<c r=\"").append(reference).append("\"")
                        .append(header ? " s=\"1\"" : "")
                        .append("><v>").append(number).append("</v></c>");
            } else {
                xml.append("<c r=\"").append(reference).append("\" t=\"inlineStr\"")
                        .append(header ? " s=\"1\"" : "")
                        .append("><is><t xml:space=\"preserve\">")
                        .append(xml(value == null ? "" : String.valueOf(value)))
                        .append("</t></is></c>");
            }
        }
        xml.append("</row>");
    }

    private static int[] widths(List<String> headers, List<List<Object>> rows) {
        int[] widths = new int[headers.size()];
        for (int index = 0; index < headers.size(); index++) {
            widths[index] = visualLength(headers.get(index));
        }
        for (List<Object> row : rows) {
            for (int index = 0; index < Math.min(row.size(), widths.length); index++) {
                widths[index] = Math.max(widths[index], visualLength(String.valueOf(row.get(index))));
            }
        }
        for (int index = 0; index < widths.length; index++) {
            widths[index] = Math.min(36, Math.max(12, widths[index] + 2));
        }
        return widths;
    }

    private static int visualLength(String value) {
        if (value == null) {
            return 0;
        }
        return value.codePoints().map(codePoint -> codePoint > 127 ? 2 : 1).sum();
    }

    private static String columnName(int number) {
        StringBuilder name = new StringBuilder();
        while (number > 0) {
            number--;
            name.insert(0, (char) ('A' + number % 26));
            number /= 26;
        }
        return name.toString();
    }

    private static String xml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
