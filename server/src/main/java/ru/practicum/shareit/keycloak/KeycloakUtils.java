package ru.practicum.shareit.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserKeycloakDto;

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

    public UserKeycloakDto createKeycloakUser(UserDto userDto) {

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(userDto.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(userDto.getName());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(userDto.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        Response response = usersResource.create(kcUser);

        if (response.getStatus() != 201) {
            throw new UserAlreadyExistsException("User already exist!");
        }

        String userID = response.getLocation().getPath();
        userID = userID.substring(userID.lastIndexOf("/") + 1);
        UserRepresentation kcCreatedUser = usersResource.get(userID).toRepresentation();
        UserKeycloakDto userKeycloakDto = new UserKeycloakDto(
                kcCreatedUser.getId(),
                kcCreatedUser.getUsername(),
                kcCreatedUser.getEmail(),
                null
        );

        return userKeycloakDto;
    }

    public void deleteKeycloakUser(String uuid) {
        Response response = usersResource.delete(uuid);
        if (response.getStatus() != 204) {
            throw new UserNotFoundException("User not found!");
        }
    }

    public UserKeycloakDto updateKeycloakUser(UserDto userDto) {
        UserResource userResource = usersResource.get(userDto.getUuid());
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.setUsername(userDto.getName());
        if (userDto.getPassword() != null) {
            CredentialRepresentation credentialRepresentation = createPasswordCredentials(userDto.getPassword());
            userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        }
        userResource.update(userRepresentation);
        UserRepresentation kcUpdatedUser = usersResource.get(userDto.getUuid()).toRepresentation();
        UserKeycloakDto userKeycloakDto = new UserKeycloakDto(
                kcUpdatedUser.getId(),
                kcUpdatedUser.getUsername(),
                kcUpdatedUser.getEmail(),
                null
        );

        return userKeycloakDto;
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
