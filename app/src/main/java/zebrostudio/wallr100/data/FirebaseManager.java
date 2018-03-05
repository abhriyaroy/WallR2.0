package zebrostudio.wallr100.data;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Responsible for handling all firebase related events. Is called upon from the DataManager
 * ({@link DataManager})
 */
@Singleton
public class FirebaseManager {

    @Inject
    FirebaseManager(){

    }

    public Observable<Void> completableFirebasePersistence(final Application application) {
        return Observable.create(new ObservableOnSubscribe<Void>() {
            @Override
            public void subscribe(ObservableEmitter<Void> observableEmitter) throws Exception {
                if (!FirebaseApp.getApps(application).isEmpty()) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.setPersistenceEnabled(true);
                    observableEmitter.onComplete();
                }
            }
        });
    }
}
