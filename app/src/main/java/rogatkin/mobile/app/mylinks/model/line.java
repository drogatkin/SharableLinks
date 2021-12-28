package rogatkin.mobile.app.mylinks.model;

import android.webkit.URLUtil;

import java.util.Date;

import rogatkin.mobile.app.mylinks.R;
import rogatkin.mobile.data.pertusin.PresentA;
import rogatkin.mobile.data.pertusin.StoreA;
import rogatkin.mobile.data.pertusin.ValidationHandlerI;
import rogatkin.mobile.data.pertusin.WebA;

import static rogatkin.mobile.data.pertusin.PresentA.FieldType.Hidden;

public class line extends Id {
    //final static String URL_PAT = android.util.Patterns.WEB_URL.pattern();
    @WebA
    @StoreA(index = true)
    @PresentA(required = true, viewFieldId = R.id.ed_linkname, normalize = "t", listViewFieldId = R.id.tv_name)
    public String name;
    @StoreA()
    @WebA("link")
    @PresentA(required = true, viewFieldId = R.id.ed_url, normalize = "t",validator=Validator.class)
    public String url;
    @StoreA(index = true)
    public Date accessed;
    @StoreA()
    @WebA
    @PresentA(viewFieldId = R.id.ed_description)
    public String description;
    @StoreA(index = true)
    @PresentA(presentType = Hidden)
    public long group_id;

    public boolean highlight;

    public line clear() {
        name = "";
        url = "";
        description = "";
        group_id = 0;
        id = 0;
        created_on = modified_on = null;
        return this;
    }

    static public class Validator implements ValidationHandlerI<String> {

        @Override
        public void validate(String s) {
            if (!URLUtil.isValidUrl(s))
                throw new IllegalArgumentException("This is not valid URL");
        }
    }
}
