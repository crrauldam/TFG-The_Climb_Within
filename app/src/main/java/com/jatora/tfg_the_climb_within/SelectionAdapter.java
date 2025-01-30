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
                return FragmentTowerSelectionItem.newInstance("img/towers/surprise.png", true);
            case 2:
                return FragmentTowerSelectionItem.newInstance("img/towers/disgust.png", true);
            case 3:
                return FragmentTowerSelectionItem.newInstance("img/towers/anger.png", true);
            case 4:
                return FragmentTowerSelectionItem.newInstance("img/towers/sadness.png", true);
            case 5:
                return FragmentTowerSelectionItem.newInstance("img/towers/fear.png", true);
            case 6:
                return FragmentTowerSelectionItem.newInstance("img/towers/calm.png", true);
            default:
                return FragmentTowerSelectionItem.newInstance("img/towers/happiness.png", true);
        }
    }

    @Override
    public int getItemCount() {
        return 7; // total of case statements ( = total of slides)
    }
}
