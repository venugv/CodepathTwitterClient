GoogleImageSearch
=================

An image searching application powered by the Google Image Search API

####Total time spent: 40 Hours

####User Stories:
  * [x] User can enter a search query that will display a grid of image results from the Google Image API.
  * [x] User can click on "settings" which allows selection of advanced search options to filter results.
  * [x] User can configure advanced search filters such as:
    * Size (small, medium, large, extra-large)
    * Color filter (black, blue, brown, gray, green, etc...)
    * Type (faces, photo, clip art, line art)
    * Site (espn.com)
  * [x] Subsequent searches will have any filters applied to the search results
  * [x] User can tap on any image in results to see the image full-screen
  * [x] User can scroll down “infinitely” to continue loading more image results
  * [x] Robust error handling, check if internet is available, handle error cases, network failures
  * [x] Use the ActionBar SearchView or custom layout as the query box instead of an EditText
  * [x] User can share an image to their friends or email it to themselves
  * [x] Replace Filter Settings Activity with a lightweight modal overlay
  * [x] Use RecyclerView instead of ListView
  * [x] Use the StaggeredGridView to display improve the grid of image results (Used StaggeredGridLayout)
  * [x] User can zoom or pan images displayed in full-screen detail view
  * [x] Use custom layout themes and colors to polish the app
  * [x] Use activity and fragment transitions in the app
  * [x] Used the SnackBar to display internet down message.  This can be hooked up to the wifi settings activity if required.
  * [x] Used CardView for presenting the images better.
  
####Points to Note:
  * [x] Google’s image search API has been deprecated and hence this app can’t be used to perform more than 100 searches
  * [x] Need to rewrite this app to use the Flickr search API for a more robust experience

Libraries used: android-async-http-1.4.9, Glide-3.6, Mike Ortiz's TouchView, CardView-v7, jackson-2.0.1 and android-support-appcompat-v7.

####Demo:
![Video Walkthrough](demo.gif)
