package krishnaapps.com.pushdatastockbreakout.modules;

public class IntraDay {
    private String mName;
    private String mImageUrl;
    private int mKey;
    private String mDesc;
    private String mDate;

    public IntraDay() {
        //empty constructor needed
    }

    public IntraDay(String name, String imageUrl, int key, String desc, String date) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mKey = key;
        mDesc = desc;
        mDate = date;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        mKey = key;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }
}
