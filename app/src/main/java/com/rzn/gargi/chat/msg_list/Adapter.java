package com.rzn.gargi.chat.msg_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.rzn.gargi.R;
import com.rzn.gargi.chat.OneToOneChat;
import com.rzn.gargi.helper.MessegesModel;
import com.rzn.gargi.helper.MsgListModel;

import java.util.Calendar;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECIEVED = 2;
    private static final int VIEW_TYPE_MESSAGE_ADMIN = 3;
    private List<MessegesModel> mMessageList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    public Adapter(List<MessegesModel> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i==VIEW_TYPE_MESSAGE_SENT){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_msg, parent, false);

            return new Adapter.ViewHolder(itemView);

        }
        else if (i==VIEW_TYPE_MESSAGE_RECIEVED){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_msg, parent, false);

            return new Adapter.ViewHolder(itemView);

        }


        return null;
    }

    @Override
    public int getItemViewType(int position) {
        MessegesModel messages = (MessegesModel) mMessageList.get(position);
        String id = messages.getGetter();
        String userid = messages.getSender();
        if (id.equals(auth.getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else return VIEW_TYPE_MESSAGE_RECIEVED;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MessegesModel messages = mMessageList.get(position);
            switch (holder.getItemViewType()){
                case VIEW_TYPE_MESSAGE_RECIEVED:
                    holder._msg.setText(messages.getMsg());
                    holder.convertime(messages.getTime());
                break;
                case VIEW_TYPE_MESSAGE_SENT:
                    holder._msg.setText(messages.getMsg());
                    holder.convertime(messages.getTime());

                    break;
                    default:
                    return;
            }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        TextView _msg=(TextView)itemView.findViewById(R.id.msg);
        TextView timeAgo = (TextView)itemView.findViewById(R.id.time);

        public void getMsg(String msg){

            TextView _msg=(TextView)itemView.findViewById(R.id.msg);
            _msg.setText(msg);
        }
        public void convertime(Long milliseconds)
        {
            String dakika;
            int seconds = (int) ((milliseconds+10800000) / 1000) % 60 ;
            int minutes = (int) (((milliseconds+10800000)  / (1000*60)) % 60);
            int hours   = (int) (((milliseconds+10800000)  / (1000*60*60)) % 24);
            if (minutes<=9)
            {
                dakika = "0"+String.valueOf(minutes);
            }
            else
            {
                dakika = String.valueOf(minutes);
            }

            String saat = String.valueOf(hours);
            String saniye = String.valueOf(seconds);
            String time= saat+":"+dakika;
            timeAgo.setText(time);
        }
    }
}
