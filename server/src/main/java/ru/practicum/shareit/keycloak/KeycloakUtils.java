package ru.practicum.shareit.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Slf4j
@Service
public class KeycloakUtils {

    private String serverURL;
    private String realm;
    private String clientID;
    private String clientSecret;

    private static Keycloak keycloak;
    private static RealmResource realmResource;
    private static UsersResource usersResource;

    @Autowired
    public KeycloakUtils(@Value("${keycloak.auth-server-url}") String serverURL,
                         @Value("${keycloak.realm}") String realm,
                         @Value("${keycloak.resource}") String clientID,
                         @Value("${keycloak.credentials.secret}") String clientSecret) {
        this.serverURL = serverURL;
        this.realm = realm;
        this.clientID = clientID;
        this.clientSecret = clientSecret;

        keycloak = KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientID)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        realmResource = keycloak.realm(realm);
        usersResource = realmResource.users();
    }

    public Response createKeycloakUser(UserDto userDto) {

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(userDto.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(userDto.getName());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(userDto.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Response response = usersResource.create(kcUser);
        return response;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
