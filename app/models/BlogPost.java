package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * A blog post is written by an author, has a title, and content. Blog posts can
 * have tags associated with them to help better organize them.
 * 
 * @author hsahni
 *
 */
@Entity
public class BlogPost extends Model {

    @Id
    public Long id;

    public Date published;

    @Required
    public String title;

    @MaxLength(2048)
    @Column(length = 2048)
    @Required
    public String content;

    @ManyToMany
    public List<Tag> tags;

    @ManyToOne
    public User author;

    public static Finder<Long, BlogPost> find = new Finder(Long.class,
            BlogPost.class);

    public static List<BlogPost> all() {
        return find.all();
    }

    public static List<BlogPost> byDate() {
        return find.order().desc("published").findList();
    }

    public static List<BlogPost> byDateForTag(Tag tag) {
        return find.where().eq("tags", tag).order().desc("published")
                .findList();
    }

    public static List<BlogPost> byDateForAuthor(User author) {
        return find.where().eq("author", author).order().desc("published")
                .findList();
    }

    public static void create(BlogPost blogPost) {
        blogPost.save();
    }

    public static List<User> authors() {
        Set<User> authors = new HashSet<User>();
        for (BlogPost post : find.all()) {
            authors.add(post.author);
        }
        return new ArrayList<User>(authors);
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    @Override
    public String toString() {
        return String
                .format("BlogPost [id=%s, published=%s, title=%s, content=%s, tags=%s, author=%s]",
                        id, published, title, content, tags, author);
    }

}
