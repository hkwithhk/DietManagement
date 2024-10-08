package com.example.dietmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignoutDialogFragment extends DialogFragment {

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("회원 탈퇴")
                .setMessage("정말로 탈퇴하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDeleteRequest(); // 확인 버튼을 눌렀을 때 회원 탈퇴 요청을 보냄
                        Define.ins().userId = "";
                        Define.ins().gender = "";
                        Define.ins().pw = "";
                        Define.ins().height = 0;
                        Define.ins().weight = 0;
                        Define.ins().nickName = "";

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        Log.d("DietManagement", "Stored User Info in Define:");
                        Log.d("DietManagement", "UserID: " + Define.ins().userId);
                        Log.d("DietManagement", "Password: " + Define.ins().pw);
                        Log.d("DietManagement", "Nickname: " + Define.ins().nickName);
                        Log.d("DietManagement", "Height: " + Define.ins().height);
                        Log.d("DietManagement", "Weight: " + Define.ins().weight);
                        Log.d("DietManagement", "Gender: " + Define.ins().gender);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼을 눌렀을 때 아무 작업도 하지 않음
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    // 회원 탈퇴 DELETE 요청을 보내는 메서드
    private void sendDeleteRequest() {
        // Retrofit 클라이언트 생성
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://112.172.248.92:1057/") // 서버의 URL로 변경 필요
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Define.ins().userId를 사용해 UserIdRequest 객체 생성
        UserIdRequest request = new UserIdRequest(Define.ins().userId);

        // DELETE 요청 실행
        apiService.deleteUser(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (getContext() != null) {
                    if (response.isSuccessful()) {
                        // 회원 탈퇴 성공 처리
                        Toast.makeText(getContext(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show();
                        Log.d("SignOut", "회원 탈퇴 성공");
                    } else {
                        // 실패 처리
                        Toast.makeText(getContext(), "회원 탈퇴 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.d("SignOut", "회원 탈퇴 실패: " + response.code());
                    }
                } else {
                    Log.w("SignOut", "Fragment not attached to context during onResponse.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "오류 발생: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SignOut", "오류 발생: " + t.getMessage());
                } else {
                    Log.e("SignOut", "Fragment not attached to context during onFailure.", t);
                }
            }
        });
    }
}
