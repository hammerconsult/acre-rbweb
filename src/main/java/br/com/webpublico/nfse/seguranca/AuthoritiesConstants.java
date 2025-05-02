package br.com.webpublico.nfse.seguranca;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String CONTRIBUINTE = "ROLE_CONTRIBUINTE";

    public static final String CONTADOR = "ROLE_CONTADOR";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String ROLE_NFS_AVULSA = "ROLE_NFS_AVULSA";
}
