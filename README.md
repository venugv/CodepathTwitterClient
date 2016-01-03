CodepathTwitterClient
=================

Twitter with Fragments

####Total time spent: 240 Hours

####User Stories:
  * [x] User can sign in to Twitter using OAuth login
  * [x] User can view the tweets from their home timeline
    * User should be displayed the username, name, and body for each tweet
    * User should be displayed the relative timestamp for each tweet "8m", "7h"
    * User can view more tweets as they scroll with infinite pagination
  * [x] User can compose a new tweet
    * User can click a “Compose” icon in the Action Bar on the top right
    * User can then enter a new tweet and post this to twitter
    * User is taken back to home timeline with new tweet visible in timeline
  * [x] User can switch between Timeline and Mention views using tabs.
    * User can view their home timeline tweets.
    * User can view the recent mentions of their username.
  * [x] User can navigate to view their own profile
    * User can see picture, tagline, # of followers, # of following, and tweets on their profile.
  * [x] User can click on the profile image in any tweet to see another user's profile.
    * User can see picture, tagline, # of followers, # of following, and tweets of clicked user.
    * Profile view should include that user's timeline
    * Optional: User can view following / followers list through the profile
  * [x] User can infinitely paginate any of these timelines (home, mentions, user) by scrolling to the bottom

The following advanced user stories are completed: 
  * [x] While composing a tweet, user can see a character counter with characters remaining for tweet out of 140
  * [x] Links in tweets are clickable and will launch the web browser (see autolink)
  * [x] User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
  * [x] User can open the twitter app offline and see last loaded tweets
    * Tweets are persisted into sqlite and can be displayed from the local DB
  * [x] User can tap a tweet to display a "detailed" view of that tweet
  * [x] User can select "reply" from detail view to respond to a tweet
  * [x] Improve the user interface and theme the app to feel "twitter branded"
  * [x] User can see embedded image media within the tweet detail view
  * [x] Compose activity is replaced with a modal overlay
  * [x] Replace the existing ActionBar with the newer support App ToolBar instead.
  * [x] Apply the popular Butterknife annotation library to reduce boilerplate.
  * [x] Leverage the popular GSON library to streamline the parsing of JSON data.
  * [x] Robust error handling, check if internet is available, handle error cases, network failures
  * [x] When a network request is sent, user sees an indeterminate progress indicator
  * [x] User can "reply" to any tweet on their home timeline
    * The user that wrote the original tweet is automatically "@" replied in compose
  * [x] User can click on a tweet to be taken to a "detail view" of that tweet
    * Advanced: User can take favorite (and unfavorite) or reweet actions on a tweet
  * [x] Improve the user interface and theme the app to feel "twitter branded"
  * [x] User can search for tweets matching a particular query and see results
  * [x] Apply the popular Butterknife annotation library to reduce view boilerplate.
  * [x] Leverage the popular GSON library to streamline the parsing of JSON data.
  * [ ] User can view their Twitter direct messages (and/or send new ones)


Libraries used: android-async-http-1.4.9, Glide-3.6, Mike Ortiz's TouchView, CardView-v7, jackson-2.0.1 and android-support-appcompat-v7.

