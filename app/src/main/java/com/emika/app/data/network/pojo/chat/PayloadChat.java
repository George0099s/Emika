package com.emika.app.data.network.pojo.chat;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayloadChat {

    @SerializedName("messages")
    @Expose
    private List<Message> messages = null;
    @SerializedName("suggestions")
    @Expose
    private List<Object> suggestions = null;
    @SerializedName("placeholder")
    @Expose
    private Placeholder placeholder;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Object> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<Object> suggestions) {
        this.suggestions = suggestions;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Placeholder placeholder) {
        this.placeholder = placeholder;
    }
}
