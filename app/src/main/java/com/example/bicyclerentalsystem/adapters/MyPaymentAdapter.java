package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.PaymentMessage;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class MyPaymentAdapter extends RecyclerView.Adapter<MyPaymentAdapter.MyPaymentViewHolder> {

    private List<PaymentMessage> messages;

    public MyPaymentAdapter() {
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_my_payment, parent, false);
        return new MyPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPaymentViewHolder holder, int position) {
        PaymentMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateList(List<PaymentMessage> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    class MyPaymentViewHolder extends RecyclerView.ViewHolder {

        private TextView receiverEmailText;
        private TextView bicycleModelText;
        private Chip messageTypeChip;
        private TextView transactionIdText;
        private TextView amountText;
        private TextView messageText;
        private TextView timestampText;
        private Chip statusChip;
        private TextView rejectionReasonText;

        public MyPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverEmailText = itemView.findViewById(R.id.receiverEmailText);
            bicycleModelText = itemView.findViewById(R.id.bicycleModelText);
            messageTypeChip = itemView.findViewById(R.id.messageTypeChip);
            transactionIdText = itemView.findViewById(R.id.transactionIdText);
            amountText = itemView.findViewById(R.id.amountText);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
            statusChip = itemView.findViewById(R.id.statusChip);
            rejectionReasonText = itemView.findViewById(R.id.rejectionReasonText);
        }

        public void bind(PaymentMessage message) {
            receiverEmailText.setText("To: " + message.getReceiverEmail());
            bicycleModelText.setText("Bicycle: " + (message.getBicycleModel() != null ? message.getBicycleModel() : "N/A"));
            transactionIdText.setText("Transaction ID: " + message.getTransactionId());
            amountText.setText(String.format("Amount: %.2f Taka", message.getAmount()));
            messageText.setText(message.getMessage());
            timestampText.setText(message.getTimeAgo());

            // Set message type chip
            if (message.isPaymentType()) {
                messageTypeChip.setText("Payment");
                messageTypeChip.setChipBackgroundColorResource(R.color.payment_type);
            } else {
                messageTypeChip.setText("Overdue Payment");
                messageTypeChip.setChipBackgroundColorResource(R.color.overdue_type);
            }

            // Set status chip
            statusChip.setText(message.getStatus());
            switch (message.getStatus()) {
                case "Pending":
                    statusChip.setChipBackgroundColorResource(R.color.status_pending);
                    break;
                case "Approved":
                    statusChip.setChipBackgroundColorResource(R.color.status_approved);
                    break;
                case "Rejected":
                    statusChip.setChipBackgroundColorResource(R.color.status_rejected);
                    break;
            }

            // Show rejection reason if rejected
            if ("Rejected".equals(message.getStatus()) && message.getRejectionReason() != null && !message.getRejectionReason().isEmpty()) {
                rejectionReasonText.setVisibility(View.VISIBLE);
                rejectionReasonText.setText("Rejection Reason: " + message.getRejectionReason());
            } else {
                rejectionReasonText.setVisibility(View.GONE);
            }
        }
    }
}
