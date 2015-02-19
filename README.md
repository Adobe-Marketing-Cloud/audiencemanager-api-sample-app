# AudienceManager Hands On Lab

A sample blogging application which demonstrates calling Adobe AudienceManager's REST APIs. 

It is built using the [play framework](https://www.playframework.com/) version 2.2 and Java 7. It has no other external dependencies. 

If you have homebrew installed, you can install play by running `brew install play22`. 


## AudienceManager Background
Adobe AudienceManager is a Data Management Platform (DMP). It allows you to combine into audiences information on your visitors from disparate sources such as from your site, your CRM systems, and third party relationships. You are able to make these audiences actionable by sending this information to wherever you chose, whether it's your own systems for site customization or to an ad networks for ad targeting. 


## Getting started
To start the application, in a terminal in the current directory, run `play`, or `play debug` if you wish to connect a debugger. This will start up the play framework's console. From here you can use the commands `clean` and `compile` to build the application. You can also just type `run` to start the application. The files will be compiled the first time you load the application in a browser at http://localhost:9000. The database will also be created the first time you launch the application (you will be asked to to create the database tables by clicking a button on a webpage). To close the application and return to the play console, press `CTRL D`.

### Gotchas
You may see an error `Oops, cannot start the server. @6l8nkm92d: Database 'default' needs evolution!` when trying to run the application. If you encounter this, simply put `applyEvolutions.default=true` in conf/application.conf.

The blogging application is backed by an in-memory database. The database is recreated whenever you enter the play console. 
## Configuring an IDE
An IDE is not necessary as play will automatically build the application. If you want to set up the application for developing in an IDE, type `eclipse` or `idea` in the play console to create the project files for the respective IDEs (eclipse and intellij). 

## Features of Blogging application
The blogging application you will start with supports the following: 
* Registering as user
  * User must pick an industry they belong to and the size of their company
* Viewing blog entries
* Posting blog entries
  * User must be logged in
  * Blogs can be tagged. If the tag does not currently exist, it'll be created.
* Viewing a user to see their profile and all posts they've written
* Filtering blogs by tag.
* Commenting
  * User must be logged in

# Tools
Some useful tools are:
* [curl](http://curl.haxx.se/)
* [Postman](http://www.getpostman.com/)

# Exercises
The exercises will build off each other. If you get stuck on an exercise, you can `git checkout exercise-<exercise-number>` to go on to the next exercise. 

You should refer to the [AudienceMangar REST API Documentation](http://microsite.omniture.com/t2/help/en_US/beta/am/c_rest_api_main.html) to help complete these exercises. If you wish to use Play's webservice classes to make calls to the API, documentation can be found here: https://www.playframework.com/documentation/2.3.x/JavaWS. Note the documentation is for version 2.3 of Play, and we are using version 2.2 (the 2.3 documentation is more thorough).

The initial data for the blog, including user login information into the blog app is at conf/initial-data.yaml

*You will need a AudienceManager client id, client secret for generating OAuth2 tokens. You should have at least one user account (with username and password) in a Partner in AudienceManager.*

## Exercise 1: Get an access token and refresh token
Use curl or Postman to send a request to https://api.demdex.com/oauth/token to get your access token and refresh token. For this exercise, you'll be using the OAuth2 password flow where with a single request you exchange client id, client secret, username, and user's password for tokens.

Here is an example
```
$  curl -v --user summit-api-lab-0-blog:a1ohqrtp9iroq5qlug7b2nbgmgart0mji5hu7oenhk00s2nc8aq -X POST --data 'grant_type=password&username=aam-hands-on-lab-0-user-0&password=cUlqe0!GE' https://api.demdex.com/oauth/token 
* Hostname was NOT found in DNS cache
*   Trying 8.12.226.61...
* Connected to api.demdex.com (8.12.226.61) port 443 (#0)
* TLS 1.2 connection using TLS_RSA_WITH_RC4_128_SHA
* Server certificate: *.demdex.com
* Server certificate: DigiCert SHA2 High Assurance Server CA
* Server certificate: DigiCert High Assurance EV Root CA
* Server auth using Basic with user 'summit-api-lab-0-blog'
> POST /oauth/token HTTP/1.1
> Authorization: Basic c3VtbWl0LWFwaS1sYWItMC1ibG9nOmExb2hxcnRwOWlyb3E1cWx1ZzdiMm5iZ21nYXJ0MG1qaTVodTdvZW5oazAwczJuYzhhcQ==
> User-Agent: curl/7.37.1
> Host: api.demdex.com
> Accept: */*
> Content-Length: 73
> Content-Type: application/x-www-form-urlencoded
> 
* upload completely sent off: 73 out of 73 bytes
< HTTP/1.1 200 OK
< Date: Thu, 12 Feb 2015 22:18:17 GMT
* Server NULL is not blacklisted
< Server: NULL
< Cache-Control: no-store
< Pragma: no-cache
< Content-Type: application/json;charset=UTF-8
< Vary: Accept-Encoding
< X-Cnection: close
< Transfer-Encoding: chunked
< Set-Cookie: BIGipServerdemdex.Production-API.3564=229458756.20480.0000; path=/
< 
* Connection #0 to host api.demdex.com left intact
{"access_token":"0e4a1109-8090-4f0e-991c-cefc5171d131","token_type":"bearer","refresh_token":"94827c6d-0866-41fe-8edb-7463925294ed","expires_in":3599,"scope":"read write"}
```

*Logging in to AudienceManager for OAuth2 tokens has been done for you in the sample blog application. The header displays your audience manager user name if you've successfully logged in. Otherwise, it presents you with a login link.*

## Exercise 2: Setting up a data source and folders for traits. 
Data sources signify where a trait came from. Traits have to be associated to a data source so we'll create one. We'll also need a trait folder to put the trait inside. 

1. Create one data source for "blog"
  1. You should enable unique integration codes for traits and segments. This ensures you can only use an integration code once across traits and once across segments.
2. Create trait folders for:
  1. Blog Post Reader
  2. Blog Post Commenter
  3. Blog Post Tags
  
Try doing this via logging in [AudienceManager UI](https://bank.demdex.com) first, and then also by using Postman or curl. When in the AudienceManager UI, note how the UI is making similar API calls you'll make by opening up Chrome's developer tools or Firebug in Firefox and watching XHR request traffic. 

When using curl or postman, you'll first need to make a request to exchange your client id, client secret, username, and password for an access token. See exercise 1. 

The parent folder for the folders you're creating should be `All Traits` which has a folderId of `0`.

Keep track of the ids of the created resources. You could put them in conf/application.conf. 
 
## Exercise 3: Automatically create traits each time a new blog post is created

Make sure your blog user is linked with AudienceManager (via the link in the navigation header: http://localhost:9000/audienceManagerLogin) so that you can hit the audience manager API. 

1. Create a trait each time a blog post is created to represent a visitor who has read the blog post
  1. The trait rule should be `blogPostId==<blogPostId>`.
  2. Put this trait in the Blog Post Reader folder from Exercise 2.
  3. Set the data source to the data source you created in Exercise 2.
  4. Set the integration code to `post-<blogPostId>`.
  5. Trait type should be `RULE_BASED_TRAIT`.
2. Create a trait to represent a visitor who has posted a comment on that blog post. 
  1. The trait rule should be `comment==1 AND blogPostId==<blogPostId>`. 
  2. Put this in the Blog Post Commenter folder from Exercise 2. 
  3. Set the data source to the data source you created in Exercise 2.
  4. Set the integration code to `commenter-post-<blogPostId>`.
  5. Trait type should be `RULE_BASED_TRAIT`.

*RULE_BASED_TRAITs are traits that are evaluated on calls from pages to AudienceManager's Data Collection Servers*

## Exercise 4: Automatically create a trait each time a new tag is created.
1. Use the trait rule `tag=="<tag>"`. For exercise 3, we didn't need to quote the value becasue the values were numbers. Since here the value is a string, it's quoted. 
2. Put this trait in the Blog Post Tags folder from Exercise 2. 
3. Set the data source to the data source you created in Exercise 2.
4. Set the integration code to `tag-<tag>`. 
5. Trait type should be `RULE_BASED_TRAIT`.

## Exercise 5: Automatically create derived signals
When a blog post is created, we'll create a derived signal. Derived signals take a key, value signal that comes to AudienceManager's *Data Collection Servers* and expands it to represent another key, value. Here we will take the blog post's id (signal: blogPostId=<blogPostId>) and expands it to represent the author (signal: author=<authorId>). 

1. The sourceKey should be `blogPostId`.
2. The sourceValue should be the actual id of the blog post.
3. The destinationKey should be `author`.
4. The destinationValue should be the author's email address.

## Exercise 6: Automatically create and update segments each for industry
Whenever a blog post is created, create a segment for the author's industry and have it contain the trait for the blog post. If the segment already exists for the industry, update the segment to add the trait. 

1. The segment rule should reference the trait id (sid) from the blog post reader trait you created in Exercise 3.1. An example of segment rule would be `<traitSid>T`, where "T" denotes trait.
2. The segment should have an integrationCode of `industry-<industryId>`. 
3. You can access segments (and traits) directly by their integration code. For segments, the resource path is /segments/ic:<integrationCode> instead of /segments/<sid>

*If you wanted to limit the segment to be users who qualified for the segment 2 times in the last 5 days only, the rule would in the following format `frequency([<traitId_1>T OR <traitId_2>T OR <traitId_n>T] <= 5D) >=2`*

### Extra Credit: Use ETags to ensure you're updating the latest version of the segment

## Exercise 7: Map a segment on creation to a destination.
We want to let a company we partner with know whenever a visitor qualifies for an industry segment. We will give them our industry id for the visitor. 

1. Create a destination in UI to receive segment activations. Each time a visitor qualifies for a segment, we'll send the visitor and segment information to this destination.
  1. For Name: ACME Tracking
  2. Platform: All
  3. Type: URL *URL destinations are fired by the user's browser to let the destination company a user has falled into a segment*
  4. Since we'll want to use the industry id, and that is what we use for the integration code, select "Auto-fill destination mapping" and pick "Integration Code value".
  5. Enable serialization
  6. For Base URL: http://acme-tracking.com/?industry=%ALIAS% The mapping, in this case the segment's integration code, will replace the alias. 
  7. For Secure URL: https://acme-tracking.com/?industry=%ALIAS%
  8. Delimiter: ,
  9. Keep track of the destination id for the new destination. 
2. Whenever we create an industry segment (see Exercise 3), map the newly created segment to the destination we created in the first step.

*Segments may be mapped to a destination multiple times. This is useful if the segment can be mapped to multiple ids on the destination.*

## Exercise 8: Display metrics on a blog post readership
When getting a segment or trait, you can have reporting information on membership by including the following query parameter `includeMetrics=true`. Display 30 day unique number when displaying a blog post using the `uniques30Day` property.


# Self guided next steps
These are not necessarily related to AudienceManager's REST APIs, but they are useful for understanding AudienceManager.
## Use Data Integration Library to send visitor activity to AudienceManager
Now that you've created a blog application that automatically creates the necessary resources in AudienceManager, its time to send visitor information for the traits you've created. You can do this by instrumenting blog pages with Data Integration Library. See  Documentation: http://microsite.omniture.com/t2/help/en_US/beta/am/c_dil.html. When a user views a blog post, send DCS calls with signals for the blog id, the tag, the industry, and the company size. Modify /app/views/single_post.scala.html to accomplish this. Also, make a call when a user comments on a blog post. 

## Use Declared ID 
Use declared Id for logged in users when hitting Visitor ID service.

##Join with traits from other sources to create richer segments
Add traits from other sources to create richer segments. 

## Handle access token expiration
You will get the 401 Unauthorizated header if your access token is invalid. You should build a robust application that can handle this gracefully. You can get a new access token if you have a refresh token. 

## Store AudienceManager credentials encrypted
Encrypt access and refresh token information that's stored on your systems to safeguard it.

## Use a dedicated API user for some operations
Some operations on the blog should not require a user have valid audience manager credentials. For instance, displaying unique visitors on a blog post (Exercise 8) is read only and could be done with a dedicated API user that is used when an AudienceManager user isn't available otherwise. 
