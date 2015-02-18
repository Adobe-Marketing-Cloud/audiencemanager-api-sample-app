package models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * Represents an industry we're considering for leads.
 * 
 * @author hsahni
 *
 */
@Entity
public class Industry extends Model {

    @Id
    public Long id;

    @Required
    public String label;

    public static Finder<Long, Industry> find = new Finder(Long.class,
            Industry.class);

    public static List<Industry> all() {
        return find.all();
    }

    public static void create(Industry task) {
        task.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public static Map<String, String> options() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        for (Industry i : Industry.find.orderBy("label").findList()) {
            options.put(i.id.toString(), i.label);
        }
        return options;
    }

    @Override
    public String toString() {
        return String.format("Industry [id=%s, label=%s]", id, label);
    }

}
