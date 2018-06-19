package xyz.tumist.diploma;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyConverter {
    public CurrencyConverter() {
    }

    public String convertCurrency(long amount)
    {
        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        return n.format(amount / 100.0);
    }
    public String convertCurrency(double amount)
    {
        Locale myLocale = new Locale("ru","RU");
        NumberFormat n = NumberFormat.getCurrencyInstance(myLocale);
        return n.format(amount / 100.0);
    }
    public SpannableStringBuilder getConvertedFormattedCurrency(long amount){
        SpannableStringBuilder str = new SpannableStringBuilder(convertCurrency(amount));
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, convertCurrency(amount).length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
    public SpannableStringBuilder getConvertedFormattedCurrency(double amount){
        SpannableStringBuilder str = new SpannableStringBuilder(convertCurrency(amount));
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, convertCurrency(amount).length()-4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }
}
