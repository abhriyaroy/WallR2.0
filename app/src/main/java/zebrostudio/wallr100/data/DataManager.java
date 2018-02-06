package zebrostudio.wallr100.data;


import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DataManager {

    private String mCurrentlyInflatedFragmentTag = "";

    public DataManager(){

    }

    public String getCurrentlyInflatedFragemntTag() {
        return mCurrentlyInflatedFragmentTag;
    }

    public void setCurrentlyInflatedFragemntTag(String mCurrentlyInflatedFragemntTag) {
        this.mCurrentlyInflatedFragmentTag = mCurrentlyInflatedFragemntTag;
    }
}
