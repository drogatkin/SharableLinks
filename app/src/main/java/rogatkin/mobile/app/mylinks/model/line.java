package rogatkin.mobile.app.mylinks.model;

import java.util.Date;

import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;

import static rogatkin.mobile.data.pertusin.PresentA.FieldType.Hidden;

public class line {
    @StoreA()
    public String name;
    @StoreA()
    @PresentA()
    public String url;
    @StoreA()
    public Date accessed;
    @StoreA()
    @PresentA()
    public String description;
    @StoreA()
    @PresentA(presentType = Hidden)
    public long group_id;
}
