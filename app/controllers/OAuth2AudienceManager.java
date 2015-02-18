package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.ning.http.client.Realm.AuthScheme;

import models.AudienceManagerAuthentication;
import play.Logger;
import play.Play;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.Response;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class OAuth2AudienceManager extends Controller {
    private static final String AUTHORIZE_TEMPLATE = "%s?client_id=%s&response_type=code&redirect_uri=http://localhost:9000/audienceManagerCode";
    private static final String TOKEN_TEMPLATE = "grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=http://localhost:9000/audienceManagerCode";

    // You must be logged in to the blog before we'll allow you to authenticate
    // to AudienceManager.
    // Otherwise, we won't know who to associate your access and refresh token
    // with.
    @Security.Authenticated(Secured.class)
    public static Result login() {
        // Response type code is used for OAuth2 authorization grant flow
        String authorizeUrl = Play.application().configuration()
                .getString("audienceManager.authorizeUrl");
        String clientId = Play.application().configuration()
                .getString("audienceManager.clientId");
        String clientSecret = Play.application().configuration()
                .getString("audienceManager.clientSecret");

        String completeAuthorizeUrl = String.format(AUTHORIZE_TEMPLATE,
                authorizeUrl, clientId);
        return redirect(completeAuthorizeUrl);
    }

    public static Promise<Result> exchangeCodeForToken(String code) {
        String clientId = Play.application().configuration()
                .getString("audienceManager.clientId");
        String clientSecret = Play.application().configuration()
                .getString("audienceManager.clientSecret");
        String tokenUrl = Play.application().configuration()
                .getString("audienceManager.tokenUrl");

        // Sets the Authorization header to Basic
        // Base64Encoded(clientId:clientSecret) (includes colon)
        Promise<Result> promiseResult = WS
                .url(tokenUrl)
                .setAuth(clientId, clientSecret, AuthScheme.BASIC)
                .setContentType("application/x-www-form-urlencoded")
                .setHeader("Accept", "application/json")
                .post(String.format(TOKEN_TEMPLATE, clientId, clientSecret,
                        code)).map(new Function<Response, Result>() {

                    @Override
                    public Result apply(Response response) throws Throwable {
                        if (response.getStatus() == 200) {
                            AudienceManagerAuthentication aamAuth = new AudienceManagerAuthentication();
                            JsonNode responseJson = response.asJson();
                            aamAuth.accessToken = responseJson.get(
                                    "access_token").asText();
                            aamAuth.refreshToken = responseJson.get(
                                    "refresh_token").asText();
                            aamAuth.email = Application.getLoggedInUser().email;
                            AudienceManagerAuthentication.create(aamAuth);
                            Logger.info(String
                                    .format("Got access token %s and refresh token %s for %s",
                                            aamAuth.accessToken,
                                            aamAuth.refreshToken, aamAuth.email));
                        } else {
                            Logger.error(String.format(
                                    "Eror getting tokens for %s:\n %d\n %s",
                                    Application.getLoggedInUser(),
                                    response.getStatus(), response.getBody()));
                        }
                        return redirect(routes.Application.index());
                    }

                });

        return promiseResult;
    }
}
