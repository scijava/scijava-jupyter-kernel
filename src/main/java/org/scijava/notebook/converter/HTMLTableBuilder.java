
package org.scijava.notebook.converter;

public class HTMLTableBuilder {

    /** Returns the style tag used to style HTML tables */
    public static String getTableStyle(final boolean displayRowLabel) {
        String style = "<style>" +
            "table.converted {color: #333; font-family: Helvetica, Arial, sans-serif; border-collapse: collapse; border-spacing: 0;}" +
            "table.converted td, table.converted th {border: 1px solid #C9C7C7;}" +
            "table.converted th, table.converted td.rowLabel {background: #626262; color: #FFFFFF; font-weight: bold; text-align: left;}" +
            "table.converted td {text-align: left;}" +
            "table.converted tr:nth-child(even) {background: #F3F3F3;}" +
            "table.converted tr:nth-child(odd) {background: #FFFFFF;}" +
            "table.converted tbody tr:hover {background: #BDF4B5;}";

        if (!displayRowLabel) style +=
            "table.converted td.rowLabel, table.converted th.rowLabel {display: none;}";
        style += "</style>";

        return style;
    }

    public static String startTable() {
        return "<table class =\"converted\"><thead><tr>";
    }

    public static String appendRowLabelHeading() {
        return "<th class=\"rowLabel\">&nbsp;</th>";
    }

    public static String appendHeadings(final String data,
        final boolean end)
    {
        final String html = "<th>" + data + "</th>";
        if (end) return html + "</tr></thead><tbody>";
        return html;
    }

    public static String appendData(final String data,
        final boolean start, final boolean end)
    {
        String html = "";
        if (start) html += "<tr>";
        html += "<td>" + data + "</td>";
        if (end) html += "</tr>";
        return html;
    }

    public static String appendRowLabelData(final String data) {
        String html = "<tr><td class =\"rowLabel\">";

        if (data == null) html += "&nbsp;";
        else html += data;

        html += "</td>";
        return html;
    }

    public static String endTable() {
        return "</tbody></table>";
    }
}
