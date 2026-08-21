package com.gyaanguru.quiz.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.gyaanguru.quiz.R;
import com.gyaanguru.quiz.data.QuestionBank;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    public interface OnCategoryClick { void onClick(String category); }

    private final List<String> categories;
    private final OnCategoryClick listener;

    private final String[] EMOJIS = {"📚","🌍","🔬","⚖️","💰","🏏","📰","💡","💻","🏆","🎨","🌿"};
    private final int[] COLORS = {
        0xFFE53935, 0xFF1E88E5, 0xFF43A047, 0xFF8E24AA,
        0xFFFF6F00, 0xFF00897B, 0xFFD81B60, 0xFF3949AB,
        0xFF0097A7, 0xFFF57F17, 0xFF6A1B9A, 0xFF2E7D32
    };

    public CategoryAdapter(List<String> categories, OnCategoryClick listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        String cat = categories.get(pos);
        int count = QuestionBank.countByCategory(cat);
        int color = COLORS[pos % COLORS.length];
        String emoji = EMOJIS[pos % EMOJIS.length];

        h.tvEmoji.setText(emoji);
        h.tvName.setText(cat);
        h.tvCount.setText(count + " प्रश्न");
        h.card.setCardBackgroundColor(Color.WHITE);
        h.tvEmoji.setBackgroundColor(color & 0x33FFFFFF | (color & 0xFF000000));

        int alpha = 0x22;
        int bgColor = (alpha << 24) | (color & 0x00FFFFFF);
        h.tvEmoji.setBackgroundColor(bgColor);

        h.card.setOnClickListener(v -> listener.onClick(cat));
    }

    @Override public int getItemCount() { return categories.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvEmoji, tvName, tvCount;
        VH(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            tvEmoji = v.findViewById(R.id.tv_emoji);
            tvName = v.findViewById(R.id.tv_name);
            tvCount = v.findViewById(R.id.tv_count);
        }
    }
}
