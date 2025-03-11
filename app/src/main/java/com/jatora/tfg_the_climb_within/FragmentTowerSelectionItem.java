package com.jatora.tfg_the_climb_within;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentTowerSelectionItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentTowerSelectionItem extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "tower";
    private static final String ARG_PARAM2 = "id";
//    private static final String ARG_PARAM3 = "unlocked_towers";

    String img;
    int id;
    int[] unlocked_towers = new int[1];

    public FragmentTowerSelectionItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param img Parameter 1.
     * @return A new instance of fragment FragmentTowerSelectionItem.
     */
    public static FragmentTowerSelectionItem newInstance(String img, int id) {
        FragmentTowerSelectionItem fragment = new FragmentTowerSelectionItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, img);
        args.putInt(ARG_PARAM2, id);
//        args.putIntArray(ARG_PARAM2, unlocked_towers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            img = getArguments().getString(ARG_PARAM1);
            id = getArguments().getInt(ARG_PARAM2);
//            unlocked_towers = getArguments().getIntArray(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tower_selection_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String TAG = "FragmentTowerSelectionItem-onViewCreated";

        ImageView towerImg = view.findViewById(R.id.towerImg);
        View isLocked = view.findViewById(R.id.isLocked);
        ImageView lockImg = view.findViewById(R.id.lockImg);

        unlocked_towers = PlayerManager.getInstance(requireContext()).getUnlocked_towers();
        boolean unlocked = false;

        for (int tower : unlocked_towers) {
            if (tower == id) {
                unlocked = true;
                Log.d(TAG, "tower unlocked: "+tower);
                break;
            }
        }

        // set tower img
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(requireActivity().getAssets().open(this.img));
//            Bitmap bitmap = BitmapFactory.decodeStream(requireActivity().getAssets().open("img/towers/calm.png"));
            Log.d(TAG, "bitmap state: "+bitmap);
            Log.d(TAG, "image container state: "+towerImg);
            towerImg.setImageBitmap(bitmap);

            if (!unlocked) {
                isLocked.setVisibility(View.VISIBLE);
                lockImg.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while getting bitmap image from assets: " + e);
        }
    }
}