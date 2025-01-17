package com.example.app.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> userList;
    private final OnItemClickListener itemClickListener;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(User user);
    }

    public UserAdapter(List<User> userList, OnItemClickListener itemClickListener, OnItemDeleteListener deleteListener) {
        this.userList = userList;
        this.itemClickListener = itemClickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        Log.d("UserAdapter", "Binding User: " + user.getName());
        holder.nameTextView.setText(user.getName());
        holder.phoneTextView.setText(user.getPhone());

        // Handle item click
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(user));

        // Handle item delete (swipe-to-delete functionality should trigger this)
        holder.itemView.setOnLongClickListener(v -> {
            deleteListener.onItemDelete(user);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, phoneTextView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
        }
    }
}
