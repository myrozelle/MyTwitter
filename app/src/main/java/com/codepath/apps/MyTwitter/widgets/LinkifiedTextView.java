package com.codepath.apps.MyTwitter.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrozelle on 5/8/2015.
 */
// source: http://www.michaelevans.org/blog/2013/03/29/clickable-links-in-android-listviews/
// additional source:
//  http://stackoverflow.com/questions/17928599/how-to-identify-hashtag-and-http-link-from-a-string-in-android
//  http://www.javacodegeeks.com/2012/09/android-custom-hyperlinked-textview.html
//  https://zunostudios.com/blog/development/176-how-to-implment-hashtags-and-callouts-in-android
public class LinkifiedTextView extends TextView {
    private static final String InstagramBlue = "#517fa4";

    public LinkifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //set text color blue for username, hashtags and callouts (fake link, for now)
    public void setStyledText(String userName, String caption) {
        SpannableString styledText;
        if (!userName.isEmpty()) {
            String text = userName + "  " + caption;
            styledText = new SpannableString(text);
            styledText.setSpan(new ForegroundColorSpan(Color.parseColor(InstagramBlue)), 0, userName.length(), 0);
            styledText.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), 0);
        } else {
            styledText = new SpannableString(caption);
        }

        Matcher hashtagMatcher = Pattern.compile("#\\w+").matcher(styledText);
        while (hashtagMatcher.find()) {
            styledText.setSpan(new ForegroundColorSpan(Color.parseColor(InstagramBlue)), hashtagMatcher.start(), hashtagMatcher.end(), 0);
        }
        Matcher calloutMatcher = Pattern.compile("@\\w+").matcher(styledText); //Pattern.compile("@([A-Za-z0-9_-]+)").matcher(styledText);
        while (calloutMatcher.find()) {
            styledText.setSpan(new ForegroundColorSpan(Color.parseColor(InstagramBlue)), calloutMatcher.start(), calloutMatcher.end(), 0);
        }

        setText(styledText, BufferType.SPANNABLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView widget = (TextView) this;
        Object text = widget.getText();
        if (text instanceof Spanned) {
            Spannable buffer = (Spannable) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                }
            }

        }

        return false;
    }
}
