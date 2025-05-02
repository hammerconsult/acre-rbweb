package br.com.webpublico.seguranca.firebase;

import org.joda.time.DateTime;

public class TokenFirebase {
    private String kind;
    private String localId;
    private String email;
    private String displayName;
    private String idToken;
    private Boolean registered;
    private String refreshToken;
    private String expiresIn;
    private DateTime created = DateTime.now();

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public Boolean isExpired() {
        return created.plusSeconds(Integer.parseInt(getExpiresIn())).isBefore(DateTime.now());
    }

    @Override
    public String toString() {
        return "TokenFirebase{" +
            "kind='" + kind + '\'' +
            ", localId='" + localId + '\'' +
            ", email='" + email + '\'' +
            ", displayName='" + displayName + '\'' +
            ", idToken='" + idToken + '\'' +
            ", registered=" + registered +
            ", refreshToken='" + refreshToken + '\'' +
            ", expiresIn='" + expiresIn + '\'' +
            ", created=" + created +
            '}';
    }
}
