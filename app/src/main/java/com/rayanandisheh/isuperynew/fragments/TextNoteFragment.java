package com.rayanandisheh.isuperynew.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.rayanandisheh.isuperynew.R;
import com.rayanandisheh.isuperynew.customs.LinedEditText;
import com.rayanandisheh.isuperynew.utils.Utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TextNoteFragment extends Fragment {

    LinedEditText content;
    ScrollView scrollView;

    @Override
    public void onPause() {
        super.onPause();
        saveNotes();
        Utilities.hideKeyboard(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        content = view.findViewById(R.id.note_editext);
        scrollView = view.findViewById(R.id.text_note_scrollview);
        loadNote();
    }

    private void loadNote() {
        try {
            FileInputStream fin = getContext().openFileInput("text_note");
            InputStreamReader isr = new InputStreamReader(fin, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            int c;
            StringBuilder sb= new StringBuilder();
            while( (c = br.read()) != -1){
                sb.append((char) c);
            }

            br.close();

            content.setText(sb.toString());
            scrollView.fullScroll(View.FOCUS_DOWN);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveNotes() {
        try {
            FileOutputStream fOut = getContext().openFileOutput("text_note", Context.MODE_PRIVATE);
            String str = content.getText().toString();
            fOut.write(str.getBytes());
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}