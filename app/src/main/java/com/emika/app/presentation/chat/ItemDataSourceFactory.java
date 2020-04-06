package com.emika.app.presentation.chat;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.emika.app.data.network.pojo.chat.Message;

public class ItemDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDataSource<Integer, Message>> itemLiveDataSource = new MutableLiveData<>();
    private String token;
    public ItemDataSourceFactory(String token){
         this.token = token;
    }
    @Override
    public DataSource create() {
        ItemDataSource itemDataSource = new ItemDataSource(token);
        itemLiveDataSource.postValue(itemDataSource);
        return itemDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, Message>> getItemLiveDataSource() {
        return itemLiveDataSource;
    }
}
