package rogatkin.mobile.app.mylinks.model;

import java.util.Date;

import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;

import static rogatkin.mobile.data.pertusin.PresentA.FieldType.Hidden;

public class Id {
    @StoreA(auto=1,key=true, storeName="_id")
    @PresentA(required = true,  presentType= Hidden)
    public long id;

    @StoreA()
    @PresentA(presentType= Hidden)
    public long global_id;

    @StoreA()
    public Date created_on;

    @StoreA(index=true)
    public Date modified_on;

    @StoreA(index=true)
    public Date synchronized_on;
}

