package me.hoot215.simplechat.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
  {
    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss");
    
    public static String stripColour (String string)
      {
        char[] chars = string.toCharArray();
        char[] ret = new char[chars.length];
        for (int i = 0; i < chars.length; i++)
          {
            if (chars[i] == '§')
              {
                i++;
                continue;
              }
            ret[i] = chars[i];
          }
        return new String(ret).replace("\u0000", "");
      }
    
    @Override
    public String format (LogRecord record)
      {
        StringBuilder sb = new StringBuilder();
        sb.append(dateFormat.format(Long.valueOf(record.getMillis())))
            .append(' ').append(this.formatMessage(record))
            .append(LINE_SEPARATOR);
        if (record.getThrown() != null)
          {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.toString());
          }
        return LogFormatter.stripColour(sb.toString());
      }
  }
