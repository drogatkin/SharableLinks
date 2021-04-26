package rogatkin.mobile.app.mylinks.model;

import java.util.Date;

import rogatkin.mobile.app.mylinks.R;
import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;

import static rogatkin.mobile.data.pertusin.PresentA.FieldType.Hidden;

public class line extends Id {
    @StoreA(index=true)
    @PresentA(required=true, viewFieldId= R.id.ed_linkname, listViewFieldId = R.id.tv_name)
    public String name;
    @StoreA()
    @PresentA(required=true, viewFieldId= R.id.ed_url)
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
