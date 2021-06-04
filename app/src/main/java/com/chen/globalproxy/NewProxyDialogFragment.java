package com.chen.globalproxy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chen.globalproxy.databinding.NewProxyEditBinding;

import org.jetbrains.annotations.NotNull;

public class NewProxyDialogFragment extends DialogFragment {

    private NewProxyEditBinding binding;

    private NewProxyDialogListener listener;

    public interface NewProxyDialogListener {
        void newProxyDialogPositive(String ipAddress);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        try {
            listener = (NewProxyDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = NewProxyEditBinding.inflate(LayoutInflater.from(getContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 获取输入框焦点
        binding.ipAddressEditText.setFocusable(true);
        binding.ipAddressEditText.setFocusableInTouchMode(true);
        binding.ipAddressEditText.requestFocus();


        builder.setView(binding.getRoot());
        builder.setTitle("新增代理").
                setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ipAddress = binding.ipAddressEditText.getText().toString();
                        if (ipAddress.equals("") || ipAddress.isEmpty()) {
                            Toast.makeText(getActivity(), "代理不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            listener.newProxyDialogPositive(ipAddress);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        (new Handler()).postDelayed(() -> {
            InputMethodManager inManager = (InputMethodManager) binding.ipAddressEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }, 200);
    }
}
