package rogatkin.mobile.app.mylinks.model;

import rogatkin.mobile.data.pertusin.StoreA;

@StoreA(storeName = "rogatkin.mobile.app.mylinks_preferences")
public class setting {

    @StoreA(storeName = "sync")
    public boolean sync_enabled;
    @StoreA(storeName = "mode")
    public String sync_mode;
    @StoreA(storeName = "host")
    public String server_name;
    @StoreA(storeName = "token")
    public String token;
    @StoreA
    public boolean divider;
}
