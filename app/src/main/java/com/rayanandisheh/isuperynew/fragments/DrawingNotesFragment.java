package com.rayanandisheh.isuperynew.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.divyanshu.draw.widget.DrawView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.rayanandisheh.isuperynew.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawingNotesFragment extends Fragment {

    DrawView drawView;
    FloatingActionButton undoBtn, redoBtn, eraseBtn;

    @Override
    public void onPause() {
        super.onPause();
        saveBitmap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_drawing_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawView = view.findViewById(R.id.drawing_note_drawing_pad);
        undoBtn = view.findViewById(R.id.drawing_notes_undo_fab);
        redoBtn = view.findViewById(R.id.drawing_notes_redo_fab);
        eraseBtn = view.findViewById(R.id.drawing_notes_erase_fab);
        loadBitmap();
        initListeners();
    }

    private void initListeners() {
        undoBtn.setOnClickListener(v -> drawView.undo());
        redoBtn.setOnClickListener(v -> drawView.redo());
        eraseBtn.setOnClickListener(v -> {
            drawView.clearCanvas();
            drawView.setBackground(null);
        });
    }

    private void loadBitmap() {
        try {
            FileInputStream fin = getContext().openFileInput("drawing_note");
            int c;
            StringBuilder sb= new StringBuilder();
            while( (c = fin.read()) != -1){
                sb.append((char) c);
            }

            fin.close();
            drawView.setBackground(new BitmapDrawable(getResources(), base64ToBitmap(sb.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap() {
        try {
            FileOutputStream fOut = getContext().openFileOutput("drawing_note", Context.MODE_PRIVATE);
            fOut.write(bitmapToBase64(drawView.getBitmap()).getBytes());
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


}