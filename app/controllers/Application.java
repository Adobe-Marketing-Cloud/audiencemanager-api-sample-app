package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.AudienceManagerAuthentication;
import models.BlogPost;
import models.Comment;
import models.Tag;
import models.User;
import play.Logger;
import play.Play;
import play.api.templates.Html;
import play.data.Form;
import play.libs.F.Callback;
import play.libs.F.Option;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.WS;
import play.libs.WS.Response;
import play.libs.WS.WSRequestHolder;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.login;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Blogging platform for demonstrating Adobe AudienceManager APIs.
 * 
 * @author hsahni
 */
public class Application extends Controller {

    /** Login information holder */
    public static class Login {
        public String email;
        public String password;

        public String validate() {
            if (User.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
    }

    /** Holds tag information on blog creation in comma separated format */
    public static class TagContainer {
        public String commaSeparatedTags;
    }

    /** Form for commenting on a blog post. */
    static Form<Comment> commentForm = Form.form(Comment.class);

    /** Form for creating new blog posts. */
    static Form<BlogPost> postForm = Form.form(BlogPost.class);

    /** Form for tag information when creating new blog post. */
    static Form<TagContainer> tagContainerForm = Form.form(TagContainer.class);

    /** Form for user registration. */
    static Form<User> userForm = Form.form(User.class);

    /** Presents the user with a login page. */
    public static Result login() {
        return ok(login.render(Form.form(Login.class)));
    }

    /** Logs the user out. */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(routes.Application.login());
    }

    /** Logs user into application if correct credentials presented. */
    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("email", loginForm.get().email);
            return redirect(routes.Application.index());
        }
    }

    /**
     * Returns the logged in user, or null.
     * 
     * @return the user.
     */
    public static User getLoggedInUser() {
        if (session().get("email") == null) {
            return null;
        }
        return User.find.byId(session().get("email"));
    }

    /** Presents the user with a registration page. */
    public static Result register() {
        return ok(views.html.register.render(userForm));
    }

    /** Registers a user. */
    public static Result createUser() {
        Form<User> filledForm = userForm.bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(views.html.register.render(userForm));
        } else {
            User toCreate = filledForm.get();
            User.create(toCreate);
            return redirect(routes.Application.login());
        }
    }

    /** Presents the user with a page to author a new blog post. */
    @Security.Authenticated(Secured.class)
    public static Result authorPost() {
        return ok(views.html.create_post.render(postForm, tagContainerForm));
    }

    /** Presents a single blog post. */
    public static Result getPost(Long id) {
        BlogPost post = BlogPost.find.byId(id);
        if (post == null) {
            return notFound();
        }

        return ok(views.html.single_post.render(BlogPost.find.byId(id),
                Comment.all(), commentForm, getLoggedInUser()));
    }

    /**
     * Renders the navigation header.
     * 
     * @return HTML for the header.
     */
    public static Html header() {
        return views.html.nav.render(getLoggedInUser(), Tag.all(),
                BlogPost.authors(), getAudienceManagerUser());
    }

    /**
     * Gets the audience manager username using the access token for the blog
     * user. Returns null if could not get user.
     * 
     * @return The username in audiencemanager, or null.
     */
    private static String getAudienceManagerUser() {
        String audienceManagerUser = null;
        // This call will always succeed if you have valid audience manager
        // credentials.
        // It's a good way to check that everything is working.
        Response selfResponse = audienceManagerWS("users/self").get().get();
        if (selfResponse.getStatus() == OK) {
            audienceManagerUser = selfResponse.asJson().get("username")
                    .asText();
        }
        return audienceManagerUser;
    }

    /** Displays the main page of the blog. */
    public static Result index() {
        Option<String> noTag = Option.None();
        return redirect(routes.Application.posts(noTag));
    }

    /** Creates a new blog post. */
    @Security.Authenticated(Secured.class)
    public static Result newPost() {
        Form<BlogPost> filledForm = postForm.bindFromRequest();
        Form<TagContainer> filledTagForm = tagContainerForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.create_post.render(filledForm,
                    filledTagForm));
        } else {
            List<Tag> tags = getTags(filledTagForm.get().commaSeparatedTags);
            BlogPost blogPost = filledForm.get();
            blogPost.tags = tags;
            blogPost.published = new Date();
            blogPost.author = getLoggedInUser();
            BlogPost.create(blogPost);

            return redirect(routes.Application.getPost(blogPost.id));
        }
    }

    /** Creates a new comment on the blog post. */
    @Security.Authenticated(Secured.class)
    public static Result newComment(Long id) {
        Form<Comment> filledForm = commentForm.bindFromRequest();
        BlogPost post = BlogPost.find.byId(id);

        if (filledForm.hasErrors()) {
            return badRequest(views.html.single_post.render(
                    BlogPost.find.byId(id), Comment.all(), filledForm,
                    getLoggedInUser()));
        } else {
            Comment toCreate = filledForm.get();
            toCreate.blogPost = post;
            toCreate.published = new Date();
            toCreate.author = getLoggedInUser();
            Comment.create(toCreate);
            return redirect(routes.Application.getPost(id));
        }
    }

    /**
     * Gets tags from comma separated string. Creates tag as needed.
     * 
     * @param commaSeparatedTags
     * @return
     */
    private static List<Tag> getTags(String commaSeparatedTags) {
        List<Tag> tags = new ArrayList<Tag>();
        String[] tagStrings = commaSeparatedTags.split("\\s*,\\s*");
        for (final String tagString : tagStrings) {
            Tag tag = Tag.find.byId(tagString);
            if (tag == null) {
                // Tag doesn't exist, create
                tag = new Tag();
                tag.creator = getLoggedInUser();
                tag.label = tagString;
                Tag.create(tag);
            }
            tags.add(tag);
        }
        return tags;
    }

    /** Presents the blog posts in the system. */
    public static Result posts(Option<String> tag) {
        List<BlogPost> blogPosts = tag.isEmpty() ? BlogPost.byDate() : BlogPost
                .byDateForTag(Tag.find.byId(tag.get()));
        return ok(views.html.index.render(blogPosts));
    }

    /** Presents the user page for the user */
    public static Result user(String userId) {
        User user = User.find.byId(userId);
        return ok(views.html.user.render(user, BlogPost.byDateForAuthor(user)));
    }

    /**
     * Sets standard headers for accessing AudienceManager APIs for
     * authentication and content type negotiation.
     * 
     * @param resourcePath
     *            the resource path to be accessed. This will be combined with
     *            the
     * @return
     */
    public static WSRequestHolder audienceManagerWS(String resourcePath) {
        String url = Play.application().configuration()
                .getString("audienceManager.apiBase")
                + resourcePath;
        WSRequestHolder audienceManagerWSRH = WS.url(url)
                .setContentType("application/json")
                .setHeader("Accept", "application/json");
        // Set authorization header to user's access token if it exists.
        if (getLoggedInUser() != null
                && AudienceManagerAuthentication.forUser(getLoggedInUser()) != null) {
            audienceManagerWSRH = audienceManagerWSRH.setHeader(
                    "Authorization",
                    "Bearer "
                            + AudienceManagerAuthentication
                                    .forUser(getLoggedInUser()).accessToken);
        }

        return audienceManagerWSRH;
    }
}
