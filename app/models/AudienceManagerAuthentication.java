package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class AudienceManagerAuthentication extends Model {

    @Id
    public String email;

    public String accessToken;

    public String refreshToken;

    public static Finder<String, AudienceManagerAuthentication> find = new Finder(
            String.class, AudienceManagerAuthentication.class);

    public static AudienceManagerAuthentication forUser(User user) {
        return find.byId(user.email);
    }

    public static void create(AudienceManagerAuthentication authentication) {
        AudienceManagerAuthentication existing = find
                .byId(authentication.email);
        if (existing != null) {
            existing.delete();
        }
        authentication.save();
    }

    public static void delete(User user) {
        find.ref(user.email).delete();
    }

    @Override
    public String toString() {
        return String
                .format("AudienceManagerAuthentication [user=%s, accessToken=%s, refreshToken=%s]",
                        email, accessToken, refreshToken);
    }

}
