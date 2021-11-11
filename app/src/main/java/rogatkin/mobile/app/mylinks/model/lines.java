package rogatkin.mobile.app.mylinks.model;

import rogatkin.mobile.data.pertusin.EndpointA;
import rogatkin.mobile.data.pertusin.StoreA;
import rogatkin.mobile.data.pertusin.WebA;


public class lines {
    @EndpointA( "webbee/Sync")
    public String endpoint;
    @StoreA()
    public /*ArrayList<line>*/line[] lines;

    @WebA(response=true)
    public String response;
}
