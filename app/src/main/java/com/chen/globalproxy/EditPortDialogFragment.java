package com.chen.globalproxy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;


public class EditPortDialogFragment extends DialogFragment {

    private static final String TAG = "EditPortDialogFragment";

    EditPortDialogListener listener;

    private EditText portEditText;

    public interface EditPortDialogListener {
        void onDialogPositiveClick(Integer port);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (EditPortDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_edit_port, null);
        portEditText = view.findViewById(R.id.port_number);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int port = bundle.getInt("proxy_port");
            portEditText.setText(String.valueOf(port));
        }
        builder.setView(view);
        builder.setTitle("请输入端口")
                .setPositiveButton("确认", (dialog, which) -> {
                    Log.d(TAG, "positive onClick: " + portEditText.getText());
                    listener.onDialogPositiveClick(Integer.valueOf(portEditText.getText().toString()));
                })
                .setNegativeButton("取消", null);
        return builder.create();
    }
}
