package com.chen.globalproxy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chen.globalproxy.databinding.DialogPortEditBinding;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


public class EditPortDialogFragment extends DialogFragment {

    private DialogPortEditBinding binding;

    private static final String TAG = "EditPortDialogFragment";

    EditPortDialogListener listener;


    public interface EditPortDialogListener {
        void editPortDialogPositive(Integer port);
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

        // 获取输入框焦点
        binding.portEditText.setFocusable(true);
        binding.portEditText.setFocusableInTouchMode(true);
        binding.portEditText.requestFocus();

        builder.setView(binding.getRoot());
        builder.setTitle("请输入端口")
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    Log.d(TAG, "positive onClick: " + binding.portEditText.getText());
                    String editTextValue = binding.portEditText.getText().toString();
                    if (editTextValue.equals("") || editTextValue.isEmpty()) {
                        editTextValue = "8888";
                    }
                    listener.editPortDialogPositive(Integer.valueOf(editTextValue));
                    Toast.makeText(getActivity(), "修改端口为：" + editTextValue + "，请重设代理！", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 实现键盘自动弹出
     */
    @Override
    public void onResume() {
        super.onResume();
        (new Handler()).postDelayed(() -> {
            InputMethodManager inManager = (InputMethodManager) binding.portEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }, 200);
    }
}
