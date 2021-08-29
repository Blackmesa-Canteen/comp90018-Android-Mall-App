package com.sj.emoji;

import android.content.Context;
import android.text.Spannable;

/**
 * Created by sj on 16/3/22.
 * https://github.com/w446108264/AndroidEmoji
 */
public interface EmojiDisplayListener {

	void onEmojiDisplay(Context context, Spannable spannable, String emojiHex, int fontSize, int start, int end);
}
