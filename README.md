#PopularMovies
#####PopularMovies is an assignment project of the Android Fast Track program designed by udacity and co-created by Google. This project is currently in Stage - I where I am trying to use a public api offered by [The Movie Database](https://www.themoviedb.org/) website which exposes the metadata about movies and tvshows in JSON format. In this stage, I am trying to parse the JSON data and display the movie related information in a presentable form using Android RecyclerView along with GridView LayoutManager.
#####This project focuses on the following necessary attributes:
- An overflow menu button is included at the top right corner of the AppBar in the main activity window which will let the user to switch between the two main sort orders of the movies i.e. popular movies top rated movies. Upon selecting the appropriate menu item appropriate set of movies will be loaded in the main activity window. By default the sort order will be popular movies i.e. a set of popular movies will be loaded in the Grid when the app is launched first time.

<img src="https://cloud.githubusercontent.com/assets/19269229/22593680/137afd68-ea20-11e6-827f-b6b7dc9cb11d.png" width="220" height="70">

- A check will be performed for the network connection before each potential network operation to prevent the app from crashing when there is disruption in the network connectivity. In case there is a network connectivity disruption, there will be an error message which will advise the user to ensure the network connectivity and tap the Refresh action menu button in the main activity, provided in the AppBar, left hand side of the overflow menu option. A similar error message has also been configured in the detail activity which will alert the user to ensure the network connectivity and relaunch if network is gone.

<img src="https://cloud.githubusercontent.com/assets/19269229/22568737/3d66ed5c-e995-11e6-8c70-1155e28961c3.png" width="220" height="70">
<img src="https://cloud.githubusercontent.com/assets/19269229/22568920/e1df19f4-e995-11e6-9998-227c7460427c.png" width="220" height="70">

- Upon tapping on any of the movie posters in the main activity window an intent will be triggered which will take the user to the detail activity window where we have displayed the movie backdrop image, movie poster, original title, movie rating in 10 point scale, movie release date and plot synopsis.
- In the detail activity page we have also incorporated one up button which loads the main activity, one share button as action menu which lets the user share the web url of the movie using the ShareCompat.IntentBuilder, an overflow menu item to know more about the movie from The Movie Database website using the ActionView intent flag.

<img src="https://cloud.githubusercontent.com/assets/19269229/22569014/4b631a56-e996-11e6-94f4-f89926ed9822.png" width="220" height="70">
<img src="https://cloud.githubusercontent.com/assets/19269229/22569037/63b2da06-e996-11e6-895c-fedd86a6eb2d.png" width="220" height="70">

- Upon pushing the up button in the detail activity in any case will load the main activity with the default setting i.e. ***with the set of popular movies***.
- ConstraintLayout is used to achieve a responsive design. I have faced some issues while working with the ConstraintLayout which I have included in the issues and challenges section.

##### Following are some sample executions:
<img src="https://cloud.githubusercontent.com/assets/19269229/22569216/f5673096-e996-11e6-9817-dfd5e6b0a514.png" width="150" height="250">
<img src="https://cloud.githubusercontent.com/assets/19269229/22569280/41e12076-e997-11e6-8140-b57bba5cbd81.png" width="300" height="150">
<img src="https://cloud.githubusercontent.com/assets/19269229/22569698/d79822da-e998-11e6-929f-b9d01ce4b397.png" width="150" height="250">
<img src="https://cloud.githubusercontent.com/assets/19269229/22570397/6703c6b6-e99b-11e6-8208-7d7cf3836e58.png" width="300" height="150">

##Authentication
#####In order to request for the movies data using the movie database api we need an API Key which is unique to every user account registered with the movie database. The user need to [register](https://www.themoviedb.org/account/signup) with the movie database.
#####Post registration and successful email verification, user will be able to request for a free api key. 
#####User needs to state that the usage will be for educational/non-commercial use. The user will also need to provide some personal information to complete the request. Once the request is submitted, the user should receive the key via email shortly after. While sending the request for the movie data to the appropriate endpoint the api-key must be included as a query parameter to the uri.

##Build-Execution
#####Post obtaining the api-key, following steps should be performed to build and execute the project:
```
- Clone the public repository
	: git clone https://github.com/sarkarchandan/PopularMovies.git
- Import the project in the Android Studio and go to the MovieDataUtils class inside the package com.udacity.project.popularmovies.utilities.
	:package com.udacity.project.popularmovies.utilities.MovieDataUtils class 
- Replace the obtained api-key to static final String field API-KEY marked as this:
/*Your API Key goes here*/
private static final String API_KEY = "###############";
- Build in Android Studio and run the App in the Android Emulator or any android device.
```

##Known Issues- Challenges
#####While using the ConstraintLayout for designing the detail activity page I have faced some issues relating to certain elements in the layout design. I would like to specifically mention two of them here:
- I have designed a separate layout resource file for the toolbar used in the detail activity and used the include tag to incorporate the same in the detail activity layout-
```
<include
   layout="@layout/background_tool_bar"
  	android:id="@+id/toolBar_activity_detail"
   app:layout_constraintLeft_toLeftOf="@+id/imageView_detailActivity_backdrop"
   app:layout_constraintRight_toRightOf="@+id/imageView_detailActivity_backdrop"
   tools:layout_constraintTop_creator="1"
   tools:layout_constraintRight_creator="1"
   tools:layout_constraintLeft_creator="1"
   app:layout_constraintTop_toTopOf="parent" />
```
I have noticed that while altering any other view for this activity layout(activity_detail.xml) from the design tab, for the toolbar ```layout_height``` and ```layout_width``` attributes automatically appear in the activity_detail.xml and are reset to 0dp, making the toolbar disappear. Hence, frequently, when I make some changes in the layout design, before running the app I need to check the toolbar ```include``` tag and correct the unexpected changes.
- DetailActivity design with ConstraintLayout behaves perfectly alright when I execute my app in the Android Emulator running on Nexus 5x API 25 and powered by Android 7.1.1 Nougat, as I have posted the images. But while testing the app in my motorola(G series 2nd generation) device currently running on Marshmallow, for the TextViews with longer texts, some portion of the texts frequently go off the screen. I have not been able to fix this behavior despite many attempts.

I will be waiting for any fix and improvemnt suggestions on the current state of my app.
