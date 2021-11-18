package rogatkin.mobile.app.mylinks.model;

import rogatkin.mobile.data.pertusin.EndpointA;
import rogatkin.mobile.data.pertusin.StoreA;
import rogatkin.mobile.data.pertusin.WebA;

public class linnes_back {
   public linnes_back(String host) {
       endpoint = host;
   }
    @EndpointA( "/sharelinks/webbee/Getnew")
    public String endpoint;
    @WebA(value="user-agent", header = true)
    public String user_agent = "mobile:android"; // this will assure your json view
    @StoreA()
    public line[] lines;

    @WebA(response=true)
    public String response;

    public int code;
}
