//package chatapplication;

import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;

    private int type = 0;
    private String message;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.

    public ChatMessage(int type ,String message)
    {
        this.type = type;
        this.message = message;
    }

    public int getType()
    {
        if(message.equalsIgnoreCase("/logout"))
        {
            type = 1;
        }
        return this.type;
    }

    public String getMessage()
    {
        return this.message;
    }


}
