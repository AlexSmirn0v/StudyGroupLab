package client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("deprecation")
public enum AppLocale {
        RU(new Locale("ru"), mapOf(
                        "tab_table", "Таблица",
                        "tab_graph", "Граф",
                        "search", "Поиск",
                        "add", "Добавить группу",
                        "add_min", "Добавить минимальную группу",
                        "remove_by_id", "Удалить группу",
                        "remove_lower", "Удалить группы меньше ввода",
                        "clear", "Удалить все свои группы",
                        "info", "Вывести информацию",
                        "history", "Вывести историю",
                        "max_by_semester_enum", "Вывести максимальную группу",
                        "execute_script", "Выполнить скрипт",
                        "form_heading", "Новая учебная группа",
                        "form_author", "Автор",
                        "form_cancel", "Отмена",
                        "form_submit", "Добавить",
                        "form_combo_none", "— не указан —",
                        "form_section_params", "Параметры группы",
                        "form_section_name", "Название",
                        "form_section_coords", "Координаты",
                        "form_section_semester", "Номер семестра",
                        "form_section_admin", "Администратор группы",
                        "form_hair_color", "Цвет волос",
                        "form_hint_name", "название группы (строка)",
                        "form_hint_coord_x", "первую координату (x, целое число)",
                        "form_hint_coord_y", "вторую координату (y, целое число, необязательно)",
                        "form_hint_students", "количество студентов (целое число, больше 0, необязательно)",
                        "form_hint_transferred", "количество переведенных студентов (целое число, больше 0)",
                        "form_hint_average", "среднюю оценку (целое число, больше 0)",
                        "form_hint_semester", "номер семестра (необязательно)",
                        "form_hint_admin_name", "имя администратора (строка)",
                        "form_hint_admin_height", "рост администратора (целое число, больше 0)",
                        "form_hint_admin_passport", "номер паспорта администратора (строка)",
                        "form_hint_admin_hair", "цвет волос администратора (необязательно)",
                        "form_coord_x", "Координата x",
                        "form_error_no_author", "Не указан автор группы",
                        "form_error_fill", "Заполните поле: %s",
                        "form_error_admin",
                        "Для администратора укажите имя, рост и номер паспорта (или оставьте все поля пустыми)",
                        "form_error_no_server", "Нет ответа от сервера")),
        NO(new Locale("no"), mapOf(
                        "tab_table", "Tabell",
                        "tab_graph", "Graf",
                        "search", "Søk",
                        "add", "Legg til gruppe",
                        "add_min", "Legg til minimal gruppe",
                        "remove_by_id", "Slett gruppe",
                        "remove_lower", "Slett grupper mindre enn inndata",
                        "clear", "Slett alle egne grupper",
                        "info", "Vis informasjon",
                        "history", "Vis historikk",
                        "max_by_semester_enum", "Vis maksimal gruppe",
                        "execute_script", "Kjør skript",
                        "form_heading", "Ny studiegruppe",
                        "form_author", "Forfatter",
                        "form_cancel", "Avbryt",
                        "form_submit", "Legg til",
                        "form_combo_none", "— ikke angitt —",
                        "form_section_params", "Gruppeparametere",
                        "form_section_name", "Navn",
                        "form_section_coords", "Koordinater",
                        "form_section_semester", "Semesternummer",
                        "form_section_admin", "Gruppeadministrator",
                        "form_hair_color", "Hårfarge",
                        "form_hint_name", "gruppenavn (streng)",
                        "form_hint_coord_x", "første koordinat (x, heltall)",
                        "form_hint_coord_y", "andre koordinat (y, heltall, valgfritt)",
                        "form_hint_students", "antall studenter (heltall, større enn 0, valgfritt)",
                        "form_hint_transferred", "antall overførte studenter (heltall, større enn 0)",
                        "form_hint_average", "gjennomsnittlig karakter (heltall, større enn 0)",
                        "form_hint_semester", "semesternummer (valgfritt)",
                        "form_hint_admin_name", "administrators navn (streng)",
                        "form_hint_admin_height", "administrators høyde (heltall, større enn 0)",
                        "form_hint_admin_passport", "administrators passnummer (streng)",
                        "form_hint_admin_hair", "administrators hårfarge (valgfritt)",
                        "form_coord_x", "Koordinat x",
                        "form_error_no_author", "Ingen forfatter angitt for gruppen",
                        "form_error_fill", "Fyll ut feltet: %s",
                        "form_error_admin",
                        "Oppgi navn, høyde og passnummer for administrator (eller la alle feltene stå tomme)",
                        "form_error_no_server", "Ingen svar fra serveren")),
        DA(new Locale("da"), mapOf(
                        "tab_table", "Tabel",
                        "tab_graph", "Graf",
                        "search", "Søg",
                        "add", "Tilføj gruppe",
                        "add_min", "Tilføj minimal gruppe",
                        "remove_by_id", "Slet gruppe",
                        "remove_lower", "Slet grupper mindre end input",
                        "clear", "Slet alle egne grupper",
                        "info", "Vis information",
                        "history", "Vis historik",
                        "max_by_semester_enum", "Vis største gruppe",
                        "execute_script", "Kør script",
                        "form_heading", "Ny studiegruppe",
                        "form_author", "Forfatter",
                        "form_cancel", "Annuller",
                        "form_submit", "Tilføj",
                        "form_combo_none", "— ikke angivet —",
                        "form_section_params", "Gruppeparametre",
                        "form_section_name", "Navn",
                        "form_section_coords", "Koordinater",
                        "form_section_semester", "Semesternummer",
                        "form_section_admin", "Gruppeadministrator",
                        "form_hair_color", "Hårfarve",
                        "form_hint_name", "gruppenavn (streng)",
                        "form_hint_coord_x", "første koordinat (x, heltal)",
                        "form_hint_coord_y", "anden koordinat (y, heltal, valgfrit)",
                        "form_hint_students", "antal studerende (heltal, større end 0, valgfrit)",
                        "form_hint_transferred", "antal overførte studerende (heltal, større end 0)",
                        "form_hint_average", "gennemsnitlig karakter (heltal, større end 0)",
                        "form_hint_semester", "semesternummer (valgfrit)",
                        "form_hint_admin_name", "administrators navn (streng)",
                        "form_hint_admin_height", "administrators højde (heltal, større end 0)",
                        "form_hint_admin_passport", "administrators pasnummer (streng)",
                        "form_hint_admin_hair", "administrators hårfarve (valgfrit)",
                        "form_coord_x", "Koordinat x",
                        "form_error_no_author", "Ingen forfatter angivet for gruppen",
                        "form_error_fill", "Udfyld feltet: %s",
                        "form_error_admin",
                        "Angiv navn, højde og pasnummer for administrator (eller lad alle felter være tomme)",
                        "form_error_no_server", "Intet svar fra serveren")),
        ES_CR(new Locale("es", "CR"), mapOf(
                        "tab_table", "Tabla",
                        "tab_graph", "Gráfico",
                        "search", "Buscar",
                        "add", "Agregar grupo",
                        "add_min", "Agregar grupo mínimo",
                        "remove_by_id", "Eliminar grupo",
                        "remove_lower", "Eliminar grupos menores a la entrada",
                        "clear", "Eliminar todos mis grupos",
                        "info", "Mostrar información",
                        "history", "Mostrar historial",
                        "max_by_semester_enum", "Mostrar grupo máximo",
                        "execute_script", "Ejecutar script",
                        "form_heading", "Nuevo grupo de estudio",
                        "form_author", "Autor",
                        "form_cancel", "Cancelar",
                        "form_submit", "Agregar",
                        "form_combo_none", "— no indicado —",
                        "form_section_params", "Parámetros del grupo",
                        "form_section_name", "Nombre",
                        "form_section_coords", "Coordenadas",
                        "form_section_semester", "Número de semestre",
                        "form_section_admin", "Administrador del grupo",
                        "form_hair_color", "Color de cabello",
                        "form_hint_name", "nombre del grupo (cadena)",
                        "form_hint_coord_x", "primera coordenada (x, entero)",
                        "form_hint_coord_y", "segunda coordenada (y, entero, opcional)",
                        "form_hint_students", "cantidad de estudiantes (entero, mayor que 0, opcional)",
                        "form_hint_transferred", "cantidad de estudiantes transferidos (entero, mayor que 0)",
                        "form_hint_average", "promedio de calificaciones (entero, mayor que 0)",
                        "form_hint_semester", "número de semestre (opcional)",
                        "form_hint_admin_name", "nombre del administrador (cadena)",
                        "form_hint_admin_height", "altura del administrador (entero, mayor que 0)",
                        "form_hint_admin_passport", "número de pasaporte del administrador (cadena)",
                        "form_hint_admin_hair", "color de cabello del administrador (opcional)",
                        "form_coord_x", "Coordenada x",
                        "form_error_no_author", "No se indicó el autor del grupo",
                        "form_error_fill", "Complete el campo: %s",
                        "form_error_admin",
                        "Indique nombre, altura y pasaporte del administrador (o deje todos los campos vacíos)",
                        "form_error_no_server", "Sin respuesta del servidor"));

        public final Locale locale;
        public final Map<String, String> labels;

        AppLocale(Locale locale, Map<String, String> labels) {
                this.locale = locale;
                this.labels = labels;
        }

        private static Map<String, String> mapOf(String... pairs) {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < pairs.length - 1; i += 2) {
                        map.put(pairs[i], pairs[i + 1]);
                }
                return map;
        }

        public static AppLocale findLocale(Locale locale) {
                for (AppLocale appLocale : values()) {
                        if (appLocale.locale.getLanguage().equals(locale.getLanguage()))
                                return appLocale;
                }
                return RU;
        }
        public static void cycleLocale(Localizable localizable, AppLocale current) {
        AppLocale next = switch (current) {
            case RU -> AppLocale.NO;
            case NO -> AppLocale.DA;
            case DA -> AppLocale.ES_CR;
            case ES_CR -> AppLocale.RU;
        };
        localizable.applyLocale(next);
    }

        public interface Localizable {
                public void applyLocale(AppLocale locale);
        }
}
