# firebase-fdproject


## How to Run this Project :

1 . Run Image : rajshah/fdproject-fireapp:(VERSION_NAME)<br/>


## How to Create a IMAGE Of this Project :

1. Push Changes to Github :<br/>
2. Run Manual Docker Build and Push Workflow In Action (Provide Unique Version Name)<br/>
3. Now , Update the Container to Add Google-Credentails.json><br/>

3.1 Go to the Directory where google-credentails are saved.<br/>
3.2 ls to verify<br/>
3.3 export IMAGE_URL=rajshah1/fdproject-fireapp:v22  (Version Can be anything provided in step 2)<br/>
3.4 docker create --name temp_container $IMAGE_URL   (This Creates a Temp Container)<br/>
3.5 docker cp ./google/google-credentials.json temp_container:/google-credentials.json<br/>
3.6 docker commit temp_container $IMAGE_URL  <br/>
3.7 docker push $IMAGE_URL<br/>

>> This Pushes the image with same tag with google-credentails File<br/>

Run the Image using : docker run -it -p 8080:8080 rajshah1/fdproject-fireapp:v22<br/>


