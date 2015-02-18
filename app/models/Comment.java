package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Comment extends Model {

    @Id
    public Long id;

    public Date published;

    @Required
    public String title;

    @Required
    public String content;

    @ManyToOne
    public BlogPost blogPost;

    @ManyToOne
    public User author;

    public static Finder<Long, Comment> find = new Finder(Long.class,
            Comment.class);

    public static List<Comment> all() {
        return find.all();
    }

    public static List<Comment> byDate() {
        return find.order().desc("published").findList();
    }

    public static void create(Comment comment) {
        comment.save();

    }

    @Override
    public String toString() {
        return String
                .format("Comment [id=%s, published=%s, title=%s, content=%s, blogPost=%s, author=%s]",
                        id, published, title, content, blogPost, author);
    }

    // public static void delete(Long id) {
    // find.ref(id).delete();
    // }

}
