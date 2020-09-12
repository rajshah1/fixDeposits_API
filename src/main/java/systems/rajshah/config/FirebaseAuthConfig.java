package systems.rajshah.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;


@Configuration
public class FirebaseAuthConfig {
	private static final Logger logger=LoggerFactory.getLogger(FirebaseAuthConfig.class);

	@Value("${google.credentials.location}")
	private String pathToCredentialsFile;
	
	@Bean
	public FirebaseAuth FirebaseAuth() throws FileNotFoundException, IOException {
		logger.info("FirebaseAuth Bean Creation Started :");
		FirebaseApp.getInstance().delete();
		GoogleCredentials googleCred=GoogleCredentials.fromStream(new FileInputStream(pathToCredentialsFile));
		FirebaseOptions options=FirebaseOptions.builder().setCredentials(googleCred).build();
		FirebaseApp fireApp=FirebaseApp.initializeApp(options);
		return FirebaseAuth.getInstance(fireApp);
	}

}
