package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Служебный класс для работы с строками в формате CSV
 */
public final class CSVTools {
    /**
     * Экранирует поле для CSV, добавляя кавычки, если необходимо, и удваивая
     * внутренние кавычки.
     * 
     * @param field     поле для экранирования
     * @param delimiter разделитель полей
     * @return экранированное поле для CSV
     */
    public static String escapeCSVField(String field, String delimiter) {
        if (field == null) {
            return "";
        }
        String escaped = field.replace("\"", "\"\"");
        boolean needQuotes = escaped.contains(delimiter) || escaped.contains("\"") || escaped.contains("\n")
                || escaped.contains("\r");
        return needQuotes ? ("\"" + escaped + "\"") : escaped;
    }

    /**
     * Добавляет экранированное поле в StringBuilder, разделяя его указанным
     * разделителем.
     * 
     * @param sb StringBuilder, в который будет добавлено поле
     * @param field поле для добавления
     * @param delimiter разделитель
     * @return StringBuilder с добавленным полем
     */
    public static StringBuilder addToStringBuilder(StringBuilder sb, String field, String delimiter) {
        sb.append(escapeCSVField(field, delimiter)).append(delimiter);
        return sb;
    }

    /**
     * Парсит строку в формате CSV.
     * 
     * @param line      строка в формате CSV
     * @param delimiter разделитель полей
     * @return массив строк, извлеченных из строки
     */
    public static String[] parseCSVLine(String line, String delimiter) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (!inQuotes && line.startsWith(delimiter, i)) {
                fields.add(cur.toString());
                cur.setLength(0);
                i += delimiter.length() - 1;
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }
}
