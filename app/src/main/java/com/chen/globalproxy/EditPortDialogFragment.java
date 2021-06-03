package com.chen.globalproxy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chen.globalproxy.databinding.DialogPortEditBinding;

import org.jetbrains.annotations.NotNull;


public class EditPortDialogFragment extends DialogFragment {

    private DialogPortEditBinding binding;

    private static final String TAG = "EditPortDialogFragment";

    EditPortDialogListener listener;


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
        binding = DialogPortEditBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            int port = bundle.getInt("proxy_port");
            binding.portEditText.setText(String.valueOf(port));
        }
        builder.setView(binding.getRoot());
        builder.setTitle("请输入端口")
                .setPositiveButton("确认", (dialog, which) -> {
                    Log.d(TAG, "positive onClick: " + binding.portEditText.getText());
                    String editTextValue = binding.portEditText.getText().toString();
                    if (editTextValue.equals("") || editTextValue.isEmpty()) {
                        editTextValue = "8888";
                    }
                    listener.onDialogPositiveClick(Integer.valueOf(editTextValue));
                    Toast.makeText(getActivity(), "修改端口为：" + editTextValue + "，请重设代理！", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null);
        return builder.create();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
