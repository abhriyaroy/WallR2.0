package zebrostudio.wallr100;

/**
 * Created by royab on 01-02-2018.
 */

public interface BasePresenter<T> {

    void bindView(T view);

    void unbindView();

}
