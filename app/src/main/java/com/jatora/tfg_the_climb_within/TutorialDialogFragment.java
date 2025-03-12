package com.jatora.tfg_the_climb_within;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class TutorialDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tutorial_dialog, null);

        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Create and set adapter
        String[] titles = {getString(R.string.tutorial_shop_title), getString(R.string.tutorial_yc_title), getString(R.string.tutorial_battle1_title), getString(R.string.tutorial_battle2_title), getString(R.string.tutorial_igs_title), getString(R.string.tutorial_lt_title)};
        int[] images = {R.drawable.tutorial_shop, R.drawable.tutorial_yc, R.drawable.tutorial_battle1, R.drawable.tutorial_battle2, R.drawable.tutorial_igs, R.drawable.tutorial_lt};
        String[] hints = {getString(R.string.tutorial_shop_str),
                getString(R.string.tutorial_yc_str),
                getString(R.string.tutorial_battle1_str),
                getString(R.string.tutorial_battle2_str),
                getString(R.string.tutorial_igs_str),
                getString(R.string.tutorial_lt_str)
        };

        TutorialPagerAdapter adapter = new TutorialPagerAdapter(titles, images, hints);
        viewPager.setAdapter(adapter);

//        // Set RecyclerView LayoutManager manually
//        RecyclerView recyclerView = (RecyclerView) viewPager.getChildAt(0);
//        if (recyclerView != null) {
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//        }

        // Ensure proper layout on first load
        viewPager.post(() -> {
            if (viewPager.getAdapter() != null) {
                viewPager.requestLayout();
                viewPager.setCurrentItem(0, false);
            }
        });

        // Add item decoration to fix spacing
        viewPager.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = 0;
                outRect.right = 0;
            }
        });

        builder.setView(view);
        return builder.create();
    }
}

