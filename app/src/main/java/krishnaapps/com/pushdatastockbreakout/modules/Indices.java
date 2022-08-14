package krishnaapps.com.pushdatastockbreakout.modules;

public class Indices {

    private String indicesName;
    private String indicesImageUrl;
    private int indicesKey;
    private String indicesDesc;
    private String indicesDate;

    public Indices() {
        //empty constructor needed
    }

    public Indices(String name, String imageUrl, int key, String desc, String date) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        indicesName = name;
        indicesImageUrl = imageUrl;
        indicesKey = key;
        indicesDesc = desc;
        indicesDate = date;
    }

    public String getIndicesName() {
        return indicesName;
    }

    public void setIndicesName(String name) {
        indicesName = name;
    }

    public String getIndicesImageUrl() {
        return indicesImageUrl;
    }

    public void setIndicesImageUrl(String imageUrl) {
        indicesImageUrl = imageUrl;
    }

    public int getIndicesKey() {
        return indicesKey;
    }

    public void setIndicesKey(int key) {
        indicesKey = key;
    }

    public String getIndicesDate() {
        return indicesDate;
    }

    public void setIndicesDate(String mDate) {
        this.indicesDate = mDate;
    }

    public String getIndicesDesc() {
        return indicesDesc;
    }

    public void setIndicesDesc(String mDesc) {
        this.indicesDesc = mDesc;
    }

}
