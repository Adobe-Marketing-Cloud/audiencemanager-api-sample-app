package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.libs.F.Option;
import play.mvc.QueryStringBindable;

@Entity
public class Tag extends Model {

    @Required
    @Id
    public String label;

    @ManyToOne
    public User creator;

    public static Finder<String, Tag> find = new Finder(String.class, Tag.class);

    public static List<Tag> all() {
        return find.all();
    }

    public static void create(Tag task) {
        task.save();
    }

    public static void remove(String id) {
        find.ref(id).delete();
    }

    @Override
    public String toString() {
        return String.format("Tag [label=%s, creator=%s]", label, creator);
    }

}
