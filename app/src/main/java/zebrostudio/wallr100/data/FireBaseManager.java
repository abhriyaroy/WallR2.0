package zebrostudio.wallr100.data;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

@Singleton
public class FireBaseManager {

    @Inject
    FireBaseManager(){

    }

    public Single configureFirebasePersistence(final Application application) {
        Single singleFirebaseInitializer = Single.create(new SingleOnSubscribe() {
            @Override
            public void subscribe(SingleEmitter singleEmitter) throws Exception {
                if (!FirebaseApp.getApps(application).isEmpty()) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.setPersistenceEnabled(true);
                }
            }
        });
        return singleFirebaseInitializer;
    }
}
