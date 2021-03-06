package com.emika.app.presentation.viewmodel.calendar;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.emika.app.data.db.callback.calendar.EpicLinksDbCallback;
import com.emika.app.data.db.entity.EpicLinksEntity;
import com.emika.app.data.network.callback.calendar.EpicLinksCallback;
import com.emika.app.data.network.pojo.epiclinks.PayloadEpicLinks;
import com.emika.app.domain.repository.calendar.CalendarRepository;
import com.emika.app.presentation.utils.Converter;

import java.util.List;

public class BottomSheetSelectEpicLinksViewModel extends ViewModel implements EpicLinksDbCallback, EpicLinksCallback {
    private static final String TAG = "BottomSheetSelectEpicLi";
    private MutableLiveData<List<PayloadEpicLinks>> epicLinksMutableLiveData;
    private Converter converter;
    private CalendarRepository repository;
    public BottomSheetSelectEpicLinksViewModel(String token) {
        epicLinksMutableLiveData = new MutableLiveData<>();
        repository = new CalendarRepository(token);
        converter = new Converter();
    }

    public MutableLiveData<List<PayloadEpicLinks>> getEpicLinksMutableLiveData() {
        repository.getDbEpicLinks(this);
        return epicLinksMutableLiveData;
    }

    @Override
    public void onEpicLinksLoaded(List<EpicLinksEntity> epicLinksEntities) {
        if (epicLinksEntities == null)
            repository.downloadEpicLinks(this);
        else
        epicLinksMutableLiveData.postValue(converter.fromEpicLinksEntityToPayloadEpicLinks(epicLinksEntities));
    }

    @Override
    public void onEpicLinksDownloaded(List<PayloadEpicLinks> epicLinks) {
        repository.insertDbEpicLinks(converter.fromPayloadEpicLinksToEpicLinksEntity(epicLinks), this);
    }
}
