package com.rayanandisheh.isuperynew.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.constant.ConstantValues;

public class NotesFragment extends Fragment {

    FloatingActionButton switchModeBtn;
    private int currentMode = ConstantValues.DRAWING_NOTE_PAGE;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.NOTES_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.NOTES_PAGE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        setHasOptionsMenu(true);
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionNotes));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        switchModeBtn = view.findViewById(R.id.notes_switch_mode);
        initListeners();
        switchMode();
    }

    private void initListeners() {
        switchModeBtn.setOnClickListener(v -> switchMode());
    }

    private void switchMode() {
        if (currentMode == ConstantValues.DRAWING_NOTE_PAGE){
            currentMode = ConstantValues.TEXT_NOTE_PAGE;

            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.note_frame_layout, new TextNoteFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            switchModeBtn.setImageResource(R.drawable.ic_draw);
        } else {
            currentMode = ConstantValues.DRAWING_NOTE_PAGE;

            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.note_frame_layout, new DrawingNotesFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            switchModeBtn.setImageResource(R.drawable.ic_keyboard);
        }
    }
}