package systems.rajshah.firebase;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Service
public class FirebaseService {

	@PostConstruct
	public void initialize() {
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.getApplicationDefault())
					.setDatabaseUrl("https://infy-rest-data.firebaseio.com/")
					.build();
					FirebaseApp.initializeApp(options);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
