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
 * Indsutry Sizes we're interested in.
 * 
 * @author hsahni
 *
 */
@Entity
public class CompanySize extends Model {

    @Id
    public Long id;

    @Required
    public String label;

    public static Finder<Long, CompanySize> find = new Finder(Long.class,
            CompanySize.class);

    public static List<CompanySize> all() {
        return find.all();
    }

    public static void create(CompanySize task) {
        task.save();
    }

    public static void delete(Long id) {
        find.ref(id).delete();
    }

    public static Map<String, String> options() {
        LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
        for (CompanySize c : CompanySize.find.orderBy("label").findList()) {
            options.put(c.id.toString(), c.label);
        }
        return options;
    }

    @Override
    public String toString() {
        return String.format("CompanySize [id=%s, label=%s]", id, label);
    }

}
