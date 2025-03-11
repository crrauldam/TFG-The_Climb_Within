package com.jatora.tfg_the_climb_within;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SelectionAdapter extends FragmentStateAdapter {
    public SelectionAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return FragmentTowerSelectionItem.newInstance("img/towers/surprise.jpg", position);
            case 2:
                return FragmentTowerSelectionItem.newInstance("img/towers/disgust.jpg", position);
            case 3:
                return FragmentTowerSelectionItem.newInstance("img/towers/anger.jpg", position);
            case 4:
                return FragmentTowerSelectionItem.newInstance("img/towers/sadness.jpg", position);
            case 5:
                return FragmentTowerSelectionItem.newInstance("img/towers/fear.jpg", position);
            case 6:
                return FragmentTowerSelectionItem.newInstance("img/towers/calm.jpg", position);
            default:
                return FragmentTowerSelectionItem.newInstance("img/towers/happiness.jpg", position);
        }
    }

    @Override
    public int getItemCount() {
        return 7; // total of case statements ( = total of slides)
    }
}
