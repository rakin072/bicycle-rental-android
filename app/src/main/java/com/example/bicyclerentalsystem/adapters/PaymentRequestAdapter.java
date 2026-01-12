package com.example.bicyclerentalsystem.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bicyclerentalsystem.R;
import com.example.bicyclerentalsystem.models.PaymentMessage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class PaymentRequestAdapter extends RecyclerView.Adapter<PaymentRequestAdapter.PaymentRequestViewHolder> {

    private List<PaymentMessage> messages;
    private OnActionListener onActionListener;

    public interface OnActionListener {
        void onApprove(PaymentMessage message);
        void onReject(PaymentMessage message);
    }

    public PaymentRequestAdapter(OnActionListener listener) {
        this.messages = new ArrayList<>();
        this.onActionListener = listener;
    }

    @NonNull
    @Override
    public PaymentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_payment_request, parent, false);
        return new PaymentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRequestViewHolder holder, int position) {
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

    class PaymentRequestViewHolder extends RecyclerView.ViewHolder {

        private TextView senderEmailText;
        private TextView bicycleModelText;
        private Chip messageTypeChip;
        private TextView transactionIdText;
        private TextView amountText;
        private TextView messageText;
        private TextView timestampText;
        private Chip statusChip;
        private LinearLayout actionButtonsLayout;
        private MaterialButton approveButton;
        private MaterialButton rejectButton;

        public PaymentRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            senderEmailText = itemView.findViewById(R.id.senderEmailText);
            bicycleModelText = itemView.findViewById(R.id.bicycleModelText);
            messageTypeChip = itemView.findViewById(R.id.messageTypeChip);
            transactionIdText = itemView.findViewById(R.id.transactionIdText);
            amountText = itemView.findViewById(R.id.amountText);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
            statusChip = itemView.findViewById(R.id.statusChip);
            actionButtonsLayout = itemView.findViewById(R.id.actionButtonsLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);

            approveButton.setOnClickListener(v -> {
                if (onActionListener != null) {
                    onActionListener.onApprove(messages.get(getAdapterPosition()));
                }
            });

            rejectButton.setOnClickListener(v -> {
                if (onActionListener != null) {
                    onActionListener.onReject(messages.get(getAdapterPosition()));
                }
            });
        }

        public void bind(PaymentMessage message) {
            senderEmailText.setText("From: " + message.getSenderEmail());
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
                    actionButtonsLayout.setVisibility(View.VISIBLE);
                    break;
                case "Approved":
                    statusChip.setChipBackgroundColorResource(R.color.status_approved);
                    actionButtonsLayout.setVisibility(View.GONE);
                    break;
                case "Rejected":
                    statusChip.setChipBackgroundColorResource(R.color.status_rejected);
                    actionButtonsLayout.setVisibility(View.GONE);
                    break;
                default:
                    actionButtonsLayout.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
