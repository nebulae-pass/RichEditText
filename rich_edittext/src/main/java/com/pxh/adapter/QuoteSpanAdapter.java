package com.pxh.adapter;

import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

import com.pxh.RichEditText;
import com.pxh.richedittext.TextSpanStatus;
import com.pxh.richedittext.TextSpans;
import com.pxh.span.HolderSpan;
import com.pxh.span.RichQuoteSpan;

/**
 * Created by pxh on 2016/9/30.
 * QuoteSpan在当前行没有文字时也能够显示，主要用于充填能被QuoteSpan影响却不显示的空行
 * 无字时插入，换行时插入。一旦当前行有新的输入，则去掉占位符，当前行文字全部被取消，则插入占位符
 * 连续两次回车取消QuoteSpan影响
 */
public class QuoteSpanAdapter extends ParagraphAdapter {
//    RichQuoteSpan quoteSpan;
//
//    boolean flag = true;
//    boolean debug = false;
//
//    public QuoteSpanAdapter(RichEditText editor)
//    {
//        super(editor);
//    }
//
//    @Override
//    public void enableSpan(boolean isEnable, TextSpanStatus state, int code)
//    {
//        int start = editor.getSelectionStart();
//        int end = editor.getSelectionEnd();
//        if (end < start) {
//            start = start ^ end;
//            end = start ^ end;
//            start = start ^ end;
//        }
//        if (start < end) {
////            setSelectionTextQuote(isValid, start, end);
//        } else {
//            if (isEnable) {
//                int quoteStart = getParagraphStart(start);
//                int quoteEnd = getParagraphEnd(start);
//                //if there is just a single line,insert a replacement span
//                if (quoteStart == start &&
//                        (getEditableText().length() == quoteStart ||
//                                getEditableText().charAt(quoteStart) == '\n')) {
//                    insertHolderSpan(start);
//                } else {
//                    //else set whole paragraph by quote span
//                    setSpan(getDrawSpan(), quoteStart, quoteEnd);
//                    quoteSpan = null;
//                }
//            } else {
//                if (!removeHolderSpan(start)) {
//                    Object richQuoteSpan = getAssignSpan(RichQuoteSpan.class, start, start);
//                    getEditableText().removeSpan(richQuoteSpan);
//                }
//            }
//        }
//        state.enableQuote(isEnable);
//    }
//
//    protected void insertHolderSpan(int start)
//    {
//        getEditableText().insert(start, "|");
//        getEditableText().setSpan(new HolderSpan(getDrawSpan()), start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//    }
//
//    protected boolean removeHolderSpan(int start)
//    {
//        if (isHasHolderSpan(start)) {
//            Object replacementSpan = getAssignSpan(HolderSpan.class, start, start);
//            getEditableText().removeSpan(replacementSpan);
//            getEditableText().delete(start - 1, start);
//            return true;
//        }
//        return false;
//    }
//
//    protected LeadingMarginSpan getDrawSpan()
//    {
//        if (quoteSpan == null) {
//            quoteSpan = new RichQuoteSpan();
//        }
//        return quoteSpan;
//    }
//
//    @Override
//    public boolean changeStatusBySelection(int start, int end)
//    {
//        if (!flag) return false;
//        QuoteSpan quoteSpan = getAssignSpan(RichQuoteSpan.class, start - 1, start);
//        return quoteSpan != null && isRangeInSpan(quoteSpan, start, end) || isHasHolderSpan(start);
//    }
//
//    private boolean isHasHolderSpan(int start)
//    {
//        HolderSpan[] holderSpans = getAssignSpans(HolderSpan.class, start - 1, start);
//        for (HolderSpan span : holderSpans) {
//            if (span.getInnerSpan() instanceof RichQuoteSpan) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void changeSpanByTextChanged(int start,int lengthBefore, int lengthAfter)
//    {
//        //when the span ahead of input is HolderSpan, remove it, then decrease start
//        if (removeHolderSpan(start)) {
//            start--;
//        }
//        setTextSpanByTextChanged(RichQuoteSpan.class, start, lengthAfter);
//        //when input last character is CRLF, insert a HolderSpan
//        if (getEditableText().charAt(start + lengthAfter - 1) == '\n') {
//            insertHolderSpan(start);
//        }
//    }

    private boolean addReplacement = false;
    private LeadingMarginSpan leadingMarginSpan;

    public QuoteSpanAdapter(RichEditText editor) {
        super(editor);
    }

    @Override
    public void enableSpan(boolean isEnable, TextSpanStatus state, int code) {
        int start = editor.getSelectionStart();
        int end = editor.getSelectionEnd();
        if (end < start) {
            start = start ^ end;
            end = start ^ end;
            start = start ^ end;
        }
        state.enableQuote(isEnable);
        if (start < end) {
        } else {
            if (isEnable) {
                int quoteStart = getParagraphStart(start);
                int quoteEnd = getParagraphEnd(start);
                //if there is just a single line,insert a replacement span
                if (quoteStart == start &&
                        (getEditableText().length() == quoteStart ||
                                getEditableText().charAt(quoteStart) == '\n')) {
                    if (leadingMarginSpan == null) {
                        leadingMarginSpan = new RichQuoteSpan();
                    }
                    addReplacement = true;
                    insertReplacement(quoteStart);
                    addReplacement = false;
//                    editor.setEnableStatusChangeBySelection(false);
//                    editor.setSelection(start);
//                    editor.setEnableStatusChangeBySelection(true);
                } else {
                    //else set whole paragraph by quote span
                    setSelectionTextSpan(true, new RichQuoteSpan(), quoteStart, quoteEnd);
                }
            } else {
                RichQuoteSpan span = getAssignSpan(RichQuoteSpan.class, start, end);
                getEditableText().removeSpan(span);
                //todo:estimate
                getEditableText().delete(start, start + 1);
            }
        }
    }

    @Override
    protected void setSelectionText(boolean isEnable, int start, int end) {

    }

    @Override
    public boolean changeStatusBySelectionChanged(int start, int end) {
        HolderSpan[] holderSpans = getAssignSpans(HolderSpan.class, start - 1, start);
        for (HolderSpan span : holderSpans) {
            if (span.getInnerSpan() instanceof RichQuoteSpan) {
                return true;
            }
        }
        RichQuoteSpan[] quoteSpans = getEditableText().getSpans(start - 1, start, RichQuoteSpan.class);
        if (quoteSpans.length != 0 && isRangeInSpan(quoteSpans[0], start, end)) {
            return true;
        } else {
            return isInHolderMode(start);
        }
    }

    private boolean isInHolderMode(int start) {
        if (start >= getEditableText().length()) {
            return false;
        }
        if (getEditableText().charAt(start) != '\n') {
            return false;
        }
        RichQuoteSpan[] quoteSpans = getEditableText().getSpans(start, start + 1, RichQuoteSpan.class);
        if (quoteSpans.length > 0) {
            int len = getEditableText().getSpanEnd(quoteSpans[0]) - getEditableText().getSpanStart(quoteSpans[0]);
            if (len == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeSpanByTextChanged(int start, int lengthBefore, int lengthAfter) {
        if (addReplacement) {//add replacement will not change span
            return;
        }
        if (start + lengthAfter >= 1
                && getEditableText().charAt(start + lengthAfter - 1) == '\n'
                && editor.getSelectionStart() == getEditableText().length()
                && !addReplacement) {
            editor.setEnableStatusChangeBySelection(false);
            getEditableText().append("\n");
            editor.setSelection(start + lengthAfter);
            editor.setEnableStatusChangeBySelection(true);
            lengthAfter++;
        }
        setTextSpanByTextChanged(RichQuoteSpan.class, start, lengthAfter);
    }

    @Override
    public int getSpanStatusCode() {
        return TextSpans.Quote;
    }

    protected void insertReplacement(int start) {
        HolderSpan holderSpan = new HolderSpan(leadingMarginSpan);
        getEditableText().insert(start, "|");
        getEditableText().setSpan(holderSpan, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    protected void removeReplacement(int start) {
//        getEditableText().getSpans()
    }
}