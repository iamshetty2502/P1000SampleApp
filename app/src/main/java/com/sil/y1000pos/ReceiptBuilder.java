package com.sil.y1000pos;



import java.util.ArrayList;
import java.util.List;


public class ReceiptBuilder {
    private final int totalLength;
    public static final int fullLen = 32, halfLen = 16, midLen = 20, tidLen = 12;
    private final StringBuilder sb = new StringBuilder();

    public ReceiptBuilder(int totalLength) {
        this.totalLength = totalLength;
    }

    public final ReceiptBuilder appendCenter(String center) {
        if (center == null)
            center = "";
        if (center.length() > totalLength) {
            List<String> list = splitByMaxLength(totalLength, center);

            for (int i = 0; i < list.size(); i++) {
                appendCenter(list.get(i));
            }
            //list.forEach(s -> appendCenter(s));
        } else {
            int padLen = totalLength - center.length();
            int padLLen = padLen - (padLen + 1) / 2;
            int padRLen = padLen - padLLen;
            sb.append(padLeft(padRight(center, padRLen), padLLen)).append("\n");
        }
        return this;
    }

    public final ReceiptBuilder appendLeft(String left) {
        if (left == null)
            left = "";
        if (left.length() > totalLength) {
            List<String> list = splitByMaxLength(totalLength, left);
            for (int i = 0; i < list.size(); i++) {
                appendLeft(list.get(i));
            }
            //list.forEach(s -> appendLeft(s));
        } else {
            int padLen = totalLength - left.length();
            sb.append(padRight(left, padLen)).append("\n");
        }
        return this;
    }

    public final ReceiptBuilder appendRight(String right) {
        if (right == null)
            right = "";
        if (right.length() > totalLength) {
            List<String> list = splitByMaxLength(totalLength, right);
            for (int i = 0; i < list.size(); i++) {
                appendRight(list.get(i));
            }
            //list.forEach(s -> appendRight(s));
        } else {
            int padLen = totalLength - right.length();
            sb.append(padLeft(right, padLen)).append("\n");
        }
        return this;
    }

    public final ReceiptBuilder appendLeftRight(String left, int leftLen, String right, int rightLen) {
        if (right == null)
            right = "";
        if (left == null)
            left = "";
        if (left.length() <= leftLen && right.length() <= rightLen) {
            int spaceLen = totalLength - (leftLen + rightLen);
            sb.append(padRight(left, leftLen - left.length())).append(padLeft("", spaceLen)).append(padLeft(right, rightLen - right.length())).append("\n");
        } else {
            List<String> lefts = splitByMaxLength(leftLen, left);
            List<String> rights = splitByMaxLength(rightLen, right);
            int listPadLen = Math.abs(lefts.size() - rights.size());
            if (lefts.size() > rights.size()) {
                for (int i = 0; i < listPadLen; i++) {
                    rights.add("");
                }
            } else {
                for (int i = 0; i < listPadLen; i++) {
                    lefts.add("");
                }
            }
            for (int i = 0; i < lefts.size(); i++) {
                appendLeftRight(lefts.get(i), leftLen, rights.get(i), rightLen);
            }
        }
        return this;
    }

    private static final String padRight(String s, int padCount) {
        for (int i = 0; i < padCount; i++) {
            s = s + " ";
        }
        return s;
    }

    private static final String padLeft(String s, int padCount) {
        for (int i = 0; i < padCount; i++) {
            s = " " + s;
        }
        return s;
    }

    public static final List<String> splitByMaxLength(int maxLen, String s) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < s.length(); i = i + maxLen) {
            int endindex = Math.min(i + maxLen, s.length());
            list.add(s.substring(i, endindex));
        }
        return list;
    }

    public final String build() {
        if (sb == null)
            return "";
        else
            return sb.toString();
    }

    public static boolean isNullOrEmpty(String string) {
        if (string == null || string.trim().length() == 0 || string.trim().equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }

    public static String getStr(String s) {
        if (isNullOrEmpty(s)) return "-";
        else return s;
    }

}
