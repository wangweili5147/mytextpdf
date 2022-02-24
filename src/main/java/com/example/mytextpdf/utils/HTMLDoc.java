package com.example.mytextpdf.utils;

import org.json.simple.JSONObject;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 输出 HTML 文档
 */
public class HTMLDoc extends TextDoc
{
	static public final int TYPE_INPUT = 1;
	static public final int TYPE_COMBO = 2;

	private boolean is_open = false;
	private JSONObject json_object;
	private List<String> css_paths;
	private List<String> js_paths;
	private String declare = null;
	private String extra = null;
	private int type = TYPE_INPUT;

	private String html_open = ""
			+ "<!DOCTYPE html>\n"
			+ "<html>\n"
			+ "  <head>\n"
			+ "    <title>__TITLE__</title>\n"
			+ "    <meta name=\"author\" content=\"Lucky Byte, Inc.\"/>\n"
			+ "    <meta name=\"generator\" content=\"TextPDF\" />\n"
			+ "    <meta name=\"description\" content=\"TextPDF HTML Editor\" />\n"
			+ "    <meta name=\"keywords\" content=\"TextPDF,PDF,Template\" />\n"
			+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=__ENCODING__\">\n"
			+ "    __CSS_URL__\n"
			+ "    __JS_URL__\n"
			+ "  </head>\n"
			+ "  <body>\n";

	private String html_close = "  </body>\n</html>\n";


	public HTMLDoc(OutputStream out_stream) {
		super(out_stream);
	}

	public void setJSONObject(JSONObject json_object) {
		this.json_object = json_object;
	}

	public void setLinkPaths(List<String> css_paths, List<String> js_paths) {
		this.css_paths = css_paths;
		this.js_paths = js_paths;
	}

	public void setDeclare(String declare) {
		this.declare = declare;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setType(int type) {
		this.type = type;
	}

	private boolean writeStream(String string) {
		try {
			out_stream.write(string.getBytes(encoding));
			return true;
		} catch (UnsupportedEncodingException e) {
			System.err.println("Unsupported encoding.");
			return false;
		} catch (IOException e) {
			System.err.println("Write to html stream failed.");
			return false;
		}
	}

	private void substituteDeclare() {
		if (declare != null) {
			html_open = html_open.replace("<!DOCTYPE html>", declare);
		}
	}

	private void substituteTitle() {
		if (json_object != null) {
			if (json_object.containsKey("title")) {
				Object value = json_object.get("title");
				if (value instanceof String) {
					html_open = html_open.replace("__TITLE__",
							Util.escapeHTMLString((String) value));
				}
			}
		}
		html_open = html_open.replace("__TITLE__", "");
	}

	private void substituteCSSLinks() {
		if (css_paths != null) {
			StringBuilder builder = new StringBuilder();
			for (String path : css_paths) {
				builder.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
				builder.append(Util.escapeHTMLString(path));
				builder.append("\"/>\n");
			}
			html_open = html_open.replace("__CSS_URL__",
					builder.toString().trim());
		} else {
			html_open = html_open.replace("__CSS_URL__", "");
		}
	}

	private void substituteJSLinks() {
		if (js_paths != null) {
			StringBuilder builder = new StringBuilder();
			for (String path : js_paths) {
				builder.append("    <script src=\"");
				builder.append(Util.escapeHTMLString(path));
				builder.append("\"></script>\n");
			}
			html_open = html_open.replace("__JS_URL__",
					builder.toString().trim());
		} else {
			html_open = html_open.replace("__JS_URL__", "");
		}
	}

	@Override
	public boolean open() {
		if (out_stream == null)
			return false;

		substituteDeclare();
		substituteTitle();
		html_open = html_open.replace("__ENCODING__", encoding);
		substituteCSSLinks();
		substituteJSLinks();

		is_open = true;
		return writeStream(html_open);
	}

	@Override
	public void close() {
		if (is_open && out_stream != null) {
			if (extra != null) {
				writeStream(extra);
			}
			writeStream(html_close);
		}
	}

	@Override
	public boolean isOpen() {
		return is_open;
	}

	private String[][] block_labels = {
			{ "title",   "h1" },
			{ "chapter", "h2" },
			{ "section", "h3" },
			{ "para",    "p" },
	};

	private String getHtmlLabel(String block_name) {
		for (int i = 0; i < block_labels.length; i++) {
			if (block_name.equalsIgnoreCase(block_labels[i][0])) {
				return block_labels[i][1];
			}
		}
		return null;
	}

	private Map<String, String> getHtmlAttrs(TextChunk chunk,
			boolean block_element) {
		Map<String, String> chunk_attrs = chunk.getAttrs();
		Map<String, String> html_attrs = new HashMap<String, String>();
		StringBuilder style_string = new StringBuilder();

		for (String key : chunk_attrs.keySet()) {
			if (key.equalsIgnoreCase("font-style")) {
				String[] styles = chunk_attrs.get(key).split(",");
				for (int i = 0; i < styles.length; i++) {
					String style_name = styles[i].trim();
					if (style_name.equalsIgnoreCase("bold")) {
						style_string.append("font-weight: bold; ");
					} else if (style_name.equalsIgnoreCase("italic")) {
						style_string.append("font-style: italic; ");
					} else if (style_name.equalsIgnoreCase("underline")) {
						style_string.append("font-decoration: underline; ");
					}
				}
			} else if (block_element && key.equalsIgnoreCase("indent")) {
				style_string.append("text-indent: " + chunk_attrs.get(key) + "px; ");
			} else if (block_element && key.equalsIgnoreCase("align")) {
				style_string.append("text-align: " + chunk_attrs.get(key) + "; ");
			}
		}
		if (style_string.toString().length() > 0) {
			html_attrs.put("style", style_string.toString());
		}
		return html_attrs;
	}

	private String htmlCharEscape(String contents) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < contents.length(); i++) {
			char ch = contents.charAt(i);
			if (ch == '\n') {
				builder.append("<br/>");
			} else {
				String escape = Util.escapeHTMLChars(ch);
				if (escape != null) {
					builder.append(escape);
				} else {
					builder.append(ch);
				}
			}
		}
		return builder.toString();
	}

	private void writeValue(TextChunk chunk) {
		Map<String, String> attrs = chunk.getAttrs();
		String id = attrs.get("id");
		String minlen = attrs.get("minlen");

		switch (type) {
		case TYPE_INPUT:
			writeStream("<input type=\"text\"");
			if (id != null && id.length() > 0) {
				writeStream(" id=\"" + id + "\" name=\"" + id + "\"");
			}
			if (minlen != null && minlen.length() > 0) {
				writeStream(" size=\"" + minlen + "\"");
			}
			writeStream(" />");
			break;
		case TYPE_COMBO:
			writeStream("<input type=\"text\"");
			if (minlen != null && minlen.length() > 0) {
				writeStream(" size=\"" + minlen + "\"");
			}
			writeStream(" readonly=\"readonly\"");
			writeStream(" />");

			writeStream("<select");
			if (id != null && id.length() > 0) {
				writeStream(" id=\"" + id + "\" name=\"" + id + "\"");
			}
			writeStream(">\n");
			writeStream("<option value =\"1\">必输</option>\n");
			writeStream("<option value =\"0\">可选</option>\n");
			writeStream("</select>\n");
			break;
		}
	}

	@Override
	public void writeBlock(String block_name, List<TextChunk> chunk_list)
			throws IOException {
		if (out_stream == null || chunk_list.size() == 0)
			return;

		String label = getHtmlLabel(block_name);
		if (label == null) {
			System.err.println("unable map block name '"
					+ block_name + "'to html label.");
			return;
		}

		for (int i = 0; i < chunk_list.size(); i++) {
			TextChunk chunk = chunk_list.get(i);
			if (chunk.isValue()) {
				writeValue(chunk);
				continue;
			}
			if (i == 0) {
				writeStream("    <" + label +
						" class=\"" + block_name + "\"");
			} else {
				writeStream("<span");
			}
			Map<String, String> html_attrs = getHtmlAttrs(chunk, i == 0);
			for (String key : html_attrs.keySet()) {
				writeStream(" " + key + "=\"" + html_attrs.get(key) + "\"");
			}
			writeStream(">");
			writeStream(htmlCharEscape(chunk.getContents()));
			if (i > 0) {
				writeStream("</span>");
			}
		}
		writeStream("</" + label + ">\n");
	}

	@Override
	public void newPage() {
		writeStream("    <hr/>\n");
	}

	@Override
	public void addHRule(Attributes attrs) {
		writeStream("    <hr/>\n");
	}

	@Override
	public void addImage(Attributes attrs) {
		String value = attrs.getValue("src");
		if (value == null) {
			System.err.println("img missing src attribute.");
			return;
		}
		writeStream("<img src=\"" + Util.escapeHTMLString(value) + "\"/>");
	}

	@Override
	public void writeTable(TextTable table) throws IOException {
		if (!isOpen() || table == null) {
			return;
		}
		Map<String, String> attrs = table.getAttrs();
		int[] columns = null;

		String value = attrs.get("columns");
		if (value != null) {
			try {
				String[] array = value.split(",");
				columns = new int[array.length];
				int total = 0;
				for (int i = 0; i < array.length; i++) {
					columns[i] = Integer.parseInt(array[i]);
					total += columns[i];
				}
				for (int i = 0; i < columns.length; i++) {
					columns[i] = columns[i] * 100 / total;
				}
			} catch (Exception ex) {
				System.err.println("column must has a integer value");
			}
		}
		if (columns == null) {
			return;
		}

		writeStream("    <table border=\"2\" width=\"100%\">\n");
		for (int i = 0; i < table.getCells().size(); i++) {
			int colno = i % columns.length;
			if (colno == 0) {
				if (i > 0) {
					writeStream("      </tr>\n");
				}
				writeStream("      <tr>\n");
			}
			if (columns[colno] > 0) {
				TextChunk text_chunk = table.getCells().get(i);
				writeStream("        <td width=\"" + columns[colno] + "%\">" +
						text_chunk.getContents() + "</td>\n");
			}
		}
		writeStream("      </tr>\n");
		writeStream("    </table>\n");
	}

}
