// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

package com.fitechlabs.xtier.services.log.formatters;

import com.fitechlabs.xtier.l10n.L10n;
import com.fitechlabs.xtier.services.log.LogFormatter;
import com.fitechlabs.xtier.services.log.LogRecord;
import com.fitechlabs.xtier.services.log.impl.LogUtils;
import com.fitechlabs.xtier.utils.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogCustomFormatter
    implements LogFormatter
{
    private static class LengthFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            String s = obj.toString();
            int i = s.length();
            if(i > len)
                return !truncate ? s : s.substring(0, len);
            if(i < len)
            {
                int j = len - i;
                if(pad == null || pad.length < j)
                {
                    pad = new char[j];
                    Arrays.fill(pad, ' ');
                }
                switch(just)
                {
                case 2: // '\002'
                    return s + new String(pad, 0, j);

                case 1: // '\001'
                    return new String(pad, 0, j) + s;

                case 3: // '\003'
                    return new String(pad, 0, j / 2) + s + new String(pad, 0, j / 2 + j % 2);
                }
                if(!$assertionsDisabled)
                    throw new AssertionError();
                else
                    return null;
            } else
            {
                return s;
            }
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field length formatter [justification=" + just + ", length=" + len + ", truncate=" + truncate + ']';
        }

        static final int RIGTH_JUST = 1;
        static final int LEFT_JUST = 2;
        static final int CENTER_JUST = 3;
        private int just;
        private int len;
        private boolean truncate;
        private char pad[];
        static final boolean $assertionsDisabled; /* synthetic field */


        LengthFieldFormatter(int i, int j, boolean flag)
        {
            pad = null;
            if(!$assertionsDisabled && j <= 0)
                throw new AssertionError();
            if(!$assertionsDisabled && i != 1 && i != 2 && i != 3)
            {
                throw new AssertionError();
            } else
            {
                just = i;
                len = j;
                truncate = flag;
                return;
            }
        }
    }

    private static class CategoryFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            String s = obj.toString();
            int i = -1;
            int j = 0;
            do
            {
                if(j >= catNum)
                    break;
                i = s.indexOf(sep, i + 1);
                if(i < 0)
                    break;
                j++;
            } while(true);
            return i < 0 ? s : s.substring(i + sep.length());
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field category formatter [cat-num=" + catNum + ", separator=" + sep + ']';
        }

        private int catNum;
        private String sep;
        static final boolean $assertionsDisabled; /* synthetic field */


        public CategoryFieldFormatter(int i, String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                catNum = i;
                sep = s;
                return;
            }
        }
    }

    private static class PaddingFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            String s = obj.toString();
            int i = s.length();
            char ac[] = s.toCharArray();
            if(left)
            {
                int j;
                for(j = 0; j < i && ac[j] == ' '; j++);
                insertPads(ac, 0, j);
            } else
            {
                int k;
                for(k = ac.length - 1; k >= 0 && ac[k] == ' '; k--);
                insertPads(ac, k + 1, ac.length - k - 1);
            }
            return new String(ac);
        }

        private void insertPads(char ac[], int i, int j)
        {
            int k = j / pad.length;
            for(int l = 0; l < k; l++)
                System.arraycopy(pad, 0, ac, i + pad.length * l, pad.length);

            System.arraycopy(pad, 0, ac, i + pad.length * k, j % pad.length);
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field padding formatter [left=" + left + ", pad=" + new String(pad) + ']';
        }

        private boolean left;
        private char pad[];
        static final boolean $assertionsDisabled; /* synthetic field */


        public PaddingFieldFormatter(boolean flag, String s)
        {
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                left = flag;
                pad = s.toCharArray();
                return;
            }
        }
    }

    private static class NumberFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            return fmt.format(obj);
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field number formatter: " + fmt;
        }

        private DecimalFormat fmt;
        static final boolean $assertionsDisabled; /* synthetic field */


        public NumberFieldFormatter(String s)
        {
            fmt = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                fmt = new DecimalFormat(s);
                return;
            }
        }
    }

    private static class CaseFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            return !toLower ? obj.toString().toUpperCase() : obj.toString().toLowerCase();
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field case formatter [to-lower=" + toLower + ']';
        }

        private boolean toLower;
        static final boolean $assertionsDisabled; /* synthetic field */


        public CaseFieldFormatter(boolean flag)
        {
            toLower = flag;
        }
    }

    private static class DateFieldFormatter
        implements FieldFormatter
    {

        public String format(Object obj)
        {
            return fmt.format(obj);
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Field date formatter: " + fmt;
        }

        private SimpleDateFormat fmt;
        static final boolean $assertionsDisabled; /* synthetic field */


        public DateFieldFormatter(String s)
        {
            fmt = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                fmt = new SimpleDateFormat(s);
                return;
            }
        }
    }

    static interface FieldFormatter
    {

        public abstract String format(Object obj);
    }

    private static class Field
    {

        void addFormatter(FieldFormatter fieldformatter)
        {
            if(!$assertionsDisabled && fieldformatter == null)
            {
                throw new AssertionError();
            } else
            {
                fmts.add(fieldformatter);
                return;
            }
        }

        char getId()
        {
            return id;
        }

        List getFormatters()
        {
            return fmts;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Custom formatter field [id=" + id + ", formatters=" + Utils.list2Str(fmts) + ']';
        }

        private char id;
        private List fmts;
        static final boolean $assertionsDisabled; /* synthetic field */


        Field(char c)
        {
            fmts = new ArrayList();
            id = c;
        }
    }

    private static class Token
    {

        boolean isField()
        {
            return isField;
        }

        String getText()
        {
            if(!$assertionsDisabled && text == null)
                throw new AssertionError();
            else
                return text;
        }

        Field getField()
        {
            if(!$assertionsDisabled && field == null)
                throw new AssertionError();
            else
                return field;
        }

        public String toString()
        {
            if(!$assertionsDisabled)
                throw new AssertionError();
            else
                return "Custom formatter token [text=" + text + ", field=" + field + ']';
        }

        private boolean isField;
        private String text;
        private Field field;
        static final boolean $assertionsDisabled; /* synthetic field */


        Token(String s)
        {
            isField = false;
            text = null;
            field = null;
            if(!$assertionsDisabled && s == null)
            {
                throw new AssertionError();
            } else
            {
                isField = false;
                text = s;
                return;
            }
        }

        Token(Field field1)
        {
            isField = false;
            text = null;
            field = null;
            if(!$assertionsDisabled && field1 == null)
            {
                throw new AssertionError();
            } else
            {
                isField = true;
                field = field1;
                return;
            }
        }
    }


    private boolean isValidField(char c)
    {
        switch(c)
        {
        case 97: // 'a'
        case 99: // 'c'
        case 100: // 'd'
        case 101: // 'e'
        case 104: // 'h'
        case 105: // 'i'
        case 108: // 'l'
        case 109: // 'm'
        case 110: // 'n'
        case 111: // 'o'
        case 115: // 's'
        case 116: // 't'
        case 117: // 'u'
        case 120: // 'x'
        case 122: // 'z'
            return true;

        case 98: // 'b'
        case 102: // 'f'
        case 103: // 'g'
        case 106: // 'j'
        case 107: // 'k'
        case 112: // 'p'
        case 113: // 'q'
        case 114: // 'r'
        case 118: // 'v'
        case 119: // 'w'
        case 121: // 'y'
        default:
            return false;
        }
    }

    private void parseFieldFormatters(Field field, String s)
    {
        ArrayList arraylist = new ArrayList();
        CharArrayLexReader chararraylexreader = new CharArrayLexReader(s);
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = chararraylexreader.read(); i != 65535; i = chararraylexreader.read())
        {
            if(Character.isWhitespace((char)i))
            {
                if(stringbuffer.length() > 0)
                {
                    arraylist.add(stringbuffer.toString());
                    stringbuffer.setLength(0);
                }
                continue;
            }
            if(i == 34)
            {
                int k = 0;
                for(i = chararraylexreader.read(); i != 65535; i = chararraylexreader.read())
                {
                    if(k == 92)
                    {
                        stringbuffer.append((char)i);
                        k = 0;
                        continue;
                    }
                    if(i == 34)
                        break;
                    if(i != 92)
                        stringbuffer.append((char)i);
                    k = i;
                }

                if(i == 65535)
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR1", s));
            } else
            {
                stringbuffer.append((char)i);
            }
        }

        if(stringbuffer.length() > 0)
            arraylist.add(stringbuffer.toString());
        int j = arraylist.size();
        for(int l = 0; l < j; l++)
        {
            String s1 = (String)arraylist.get(l);
            if(s1.equals("-toupper"))
            {
                field.addFormatter(new CaseFieldFormatter(false));
                continue;
            }
            if(s1.equals("-tolower"))
            {
                field.addFormatter(new CaseFieldFormatter(true));
                continue;
            }
            if(l == j - 1)
                throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR1", s));
            String s2 = (String)arraylist.get(++l);
            if(s1.equals("-num"))
            {
                field.addFormatter(new NumberFieldFormatter(s2));
                continue;
            }
            if(s1.equals("-date"))
            {
                field.addFormatter(new DateFieldFormatter(s2));
                continue;
            }
            if(s1.equals("-cat"))
            {
                String as[] = s2.split(" ");
                if(as.length != 1 && as.length != 2)
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR2", s));
                int i1 = 0;
                try
                {
                    i1 = Integer.parseInt(as[0]);
                }
                catch(NumberFormatException numberformatexception)
                {
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR2", numberformatexception.getMessage()));
                }
                if(i1 <= 0)
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR3", s));
                field.addFormatter(new CategoryFieldFormatter(i1, as.length != 2 ? " " : as[1]));
                continue;
            }
            if(s1.equals("-len"))
            {
                byte byte0 = 2;
                char c = s2.charAt(0);
                if(c == 'c' || c == 'l' || c == 'r')
                {
                    s2 = s2.substring(1);
                    if(c == 'c')
                        byte0 = 3;
                    else
                    if(c == 'r')
                        byte0 = 1;
                }
                c = s2.charAt(s2.length() - 1);
                boolean flag = false;
                if(c == 't')
                {
                    flag = true;
                    s2 = s2.substring(0, s2.length() - 1);
                }
                int j1 = 0;
                try
                {
                    j1 = Integer.parseInt(s2);
                }
                catch(NumberFormatException numberformatexception1)
                {
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR4", numberformatexception1.getMessage()));
                }
                if(j1 <= 0)
                    throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR4", s));
                field.addFormatter(new LengthFieldFormatter(byte0, j1, flag));
                continue;
            }
            if(s1.equals("-lp"))
            {
                field.addFormatter(new PaddingFieldFormatter(true, s2));
                continue;
            }
            if(s1.equals("-rp"))
                field.addFormatter(new PaddingFieldFormatter(false, s2));
            else
                throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR5", s2));
        }

    }

    private List parseTokens(String s)
    {
        ArrayList arraylist = new ArrayList();
        StringBuffer stringbuffer = new StringBuffer();
        CharArrayLexReader chararraylexreader = new CharArrayLexReader(s);
        do
        {
            int i;
            if((i = chararraylexreader.read()) == '\uFFFF')
                break;
            if(i == 37)
            {
                i = chararraylexreader.read();
                if(isValidField((char)i))
                {
                    if(stringbuffer.length() > 0)
                    {
                        arraylist.add(new Token(stringbuffer.toString()));
                        stringbuffer.setLength(0);
                    }
                    Field field = new Field((char)i);
                    i = chararraylexreader.read();
                    if(i == 123)
                    {
                        StringBuffer stringbuffer1;
                        for(stringbuffer1 = new StringBuffer(); (i = chararraylexreader.read()) != '\uFFFF' && (i != 125 || stringbuffer1.charAt(stringbuffer1.length() - 1) == '\\'); stringbuffer1.append((char)i));
                        if(i == 65535)
                            throw new IllegalArgumentException(L10n.format("SRVC.LOG.CSTM.FMT.ERR6", s));
                        parseFieldFormatters(field, stringbuffer1.toString());
                    } else
                    {
                        chararraylexreader.back();
                    }
                    arraylist.add(new Token(field));
                } else
                {
                    stringbuffer.append('%');
                    if(i != 37)
                        stringbuffer.append((char)i);
                }
            } else
            {
                stringbuffer.append((char)i);
            }
        } while(true);
        if(stringbuffer.length() > 0)
            arraylist.add(new Token(stringbuffer.toString()));
        return arraylist;
    }

    public LogCustomFormatter(String s, String s1, String s2)
    {
        toks = null;
        ArgAssert.nullArg(s2, "template");
        header = s;
        footer = s1;
        template = s2;
        toks = parseTokens(s2);
    }

    public String header()
    {
        return header;
    }

    public String footer()
    {
        return footer;
    }

    public String format(LogRecord logrecord)
    {
        if(!$assertionsDisabled && logrecord == null)
            throw new AssertionError();
        StringBuffer stringbuffer = new StringBuffer(logrecord.getMessage().length() * 2);
        int i = toks.size();
        for(int j = 0; j < i; j++)
        {
            Token token = (Token)toks.get(j);
            if(!token.isField())
            {
                stringbuffer.append(token.getText());
                continue;
            }
            Field field = token.getField();
            Object obj = null;
            switch(field.getId())
            {
            case 116: // 't'
                obj = "\t";
                break;

            case 110: // 'n'
                obj = Utils.NEW_LINE;
                break;

            case 115: // 's'
                obj = new Long(logrecord.getSeqNum());
                break;

            case 104: // 'h'
                obj = logrecord.getThreadName();
                break;

            case 100: // 'd'
                obj = logrecord.getTimeStampDate();
                break;

            case 99: // 'c'
                obj = logrecord.getLoggerCategory();
                break;

            case 108: // 'l'
                obj = LogUtils.getLevelStr(logrecord.getLevel());
                break;

            case 101: // 'e'
                obj = new Long(logrecord.getTimeElapsed());
                break;

            case 109: // 'm'
                obj = logrecord.getMessage();
                break;

            case 97: // 'a'
                obj = logrecord.getClassName();
                break;

            case 111: // 'o'
                obj = logrecord.getMethodName();
                break;

            case 105: // 'i'
                obj = logrecord.getFileName();
                break;

            case 117: // 'u'
                obj = new Integer(logrecord.getLineNumber());
                break;

            case 120: // 'x'
                Throwable throwable = logrecord.getThrowable();
                if(throwable != null)
                    obj = throwable.toString();
                break;

            case 122: // 'z'
                Throwable throwable1 = logrecord.getThrowable();
                if(throwable1 != null)
                    obj = Utils.getStackTrace(throwable1);
                break;

            case 98: // 'b'
            case 102: // 'f'
            case 103: // 'g'
            case 106: // 'j'
            case 107: // 'k'
            case 112: // 'p'
            case 113: // 'q'
            case 114: // 'r'
            case 118: // 'v'
            case 119: // 'w'
            case 121: // 'y'
            default:
                if(!$assertionsDisabled)
                    throw new AssertionError("Invalid template: " + template);
                break;
            }
            if(obj == null)
                continue;
            List list = field.getFormatters();
            int k = list.size();
            for(int l = 0; l < k; l++)
                obj = ((FieldFormatter)list.get(l)).format(obj);

            stringbuffer.append(obj.toString());
        }

        return stringbuffer.toString();
    }

    public String toString()
    {
        return L10n.format("SRVC.LOG.CSTM.FMT.TXT1", template);
    }

    static Class _mthclass$(String s)
    {
        try
        {
            return Class.forName(s);
        }
        catch(ClassNotFoundException classnotfoundexception)
        {
            throw (new NoClassDefFoundError()).initCause(classnotfoundexception);
        }
    }

    private String header;
    private String footer;
    private String template;
    private List toks;
    static final boolean $assertionsDisabled; /* synthetic field */

    static
    {
        $assertionsDisabled = !(LogCustomFormatter.class).desiredAssertionStatus();
    }
}
