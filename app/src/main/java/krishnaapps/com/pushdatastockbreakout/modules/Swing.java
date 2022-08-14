package krishnaapps.com.pushdatastockbreakout.modules;

public class Swing {
    private String swingName;
    private String swingImageUrl;
    private int swingKey;
    private String swingDesc;
    private String swingDate;

    public Swing() {
        //empty constructor needed
    }

    public Swing(String name, String imageUrl, int key, String desc, String date) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        swingName = name;
        swingImageUrl = imageUrl;
        swingKey = key;
        swingDesc = desc;
        swingDate = date;
    }

    public String getSwingName() {
        return swingName;
    }

    public void setSwingName(String name) {
        swingName = name;
    }

    public String getSwingImageUrl() {
        return swingImageUrl;
    }

    public void setSwingImageUrl(String imageUrl) {
        swingImageUrl = imageUrl;
    }

    public int getSwingKey() {
        return swingKey;
    }

    public void setSwingKey(int key) {
        swingKey = key;
    }

    public String getSwingDate() {
        return swingDate;
    }

    public void setSwingDate(String mDate) {
        this.swingDate = mDate;
    }

    public String getSwingDesc() {
        return swingDesc;
    }

    public void setSwingDesc(String mDesc) {
        this.swingDesc = mDesc;
    }
}

