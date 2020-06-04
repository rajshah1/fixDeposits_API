package systems.rajshah.firebase;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

@Service
public class FirebaseInitialize {

	@PostConstruct
	public void initialize() {
		try {
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.getApplicationDefault())
					.setDatabaseUrl("https://infy-rest-data.firebaseio.com/")
					.build();
			if(FirebaseApp.getApps().isEmpty()) {
			FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
		    FirebaseAuth.getInstance(firebaseApp);
			}
					
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
