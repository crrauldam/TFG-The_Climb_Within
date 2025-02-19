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
                return FragmentTowerSelectionItem.newInstance("img/towers/surprise.png", position);
            case 2:
                return FragmentTowerSelectionItem.newInstance("img/towers/disgust.png", position);
            case 3:
                return FragmentTowerSelectionItem.newInstance("img/towers/anger.png", position);
            case 4:
                return FragmentTowerSelectionItem.newInstance("img/towers/sadness.png", position);
            case 5:
                return FragmentTowerSelectionItem.newInstance("img/towers/fear.png", position);
            case 6:
                return FragmentTowerSelectionItem.newInstance("img/towers/calm.png", position);
            default:
                return FragmentTowerSelectionItem.newInstance("img/towers/happiness.png", position);
        }
    }

    @Override
    public int getItemCount() {
        return 7; // total of case statements ( = total of slides)
    }
}
