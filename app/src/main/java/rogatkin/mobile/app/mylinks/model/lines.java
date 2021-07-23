package rogatkin.mobile.app.mylinks.model;

import rogatkin.mobile.app.mylinks.MainActivity;
import rogatkin.mobile.data.pertusin.EndpointA;
import rogatkin.mobile.data.pertusin.StoreA;

@EndpointA(MainActivity.server_url_base + "webbee/Linesupdate")
public class lines {
    @StoreA()
    public line[] lines;

}
