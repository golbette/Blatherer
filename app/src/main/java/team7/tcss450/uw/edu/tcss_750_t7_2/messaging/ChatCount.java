package team7.tcss450.uw.edu.tcss_750_t7_2.messaging;

public class ChatCount {
    private int chatid;
    private int count;

    public ChatCount(int chatid, int count) {
        this.chatid = chatid;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getChatid() {
        return chatid;
    }
}
