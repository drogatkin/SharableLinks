package rogatkin.mobile.app.mylinks.model;

import java.util.Date;

import rogatkin.mobile.data.pertusin.EndpointA;
import rogatkin.mobile.data.pertusin.StoreA;
import rogatkin.mobile.data.pertusin.WebA;


public class lines {
    @EndpointA( "/sharelinks/webbee/Sync")
    public String endpoint;
    @WebA(value="user-agent", header = true)
    public String user_agent = "mobile:android"; // this will assure your json view
    @WebA(value="Authorization",header=true)
    public String token; // null makes the header not set
    @WebA(header=true, value="If-Modified-Since")
    public Date modifiedSince;
    @StoreA()
    public /*ArrayList<line>*/line[] lines;

    @WebA(response=true)
    public String response;
}
