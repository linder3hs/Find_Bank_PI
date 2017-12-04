package com.linder.find_bank.widgetproviders;

/**
 * Created by linderhassinger on 12/3/17.
 */

import android.content.Intent;
import android.widget.RemoteViewsService;


public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
