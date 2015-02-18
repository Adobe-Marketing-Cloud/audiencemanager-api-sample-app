package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class User extends Model {

    @Required
    public String name;

    @Id
    @Required
    public String email;

    @ManyToOne
    @Required
    public Industry industry;

    @ManyToOne
    @Required
    public CompanySize companySize;

    @Required
    public String password;

    public static Finder<String, User> find = new Finder(String.class,
            User.class);

    public static User authenticate(String email, String password) {
        // Password should be salted if this was an actual application!
        return find.where().eq("email", email).eq("password", password)
                .findUnique();
    }

    public static List<User> all() {
        return find.all();
    }

    public static void create(User user) {
        user.save();
    }

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "User [name=%s, email=%s, industry=%s, companySize=%s]", name,
                email, industry, companySize);
    }

}
