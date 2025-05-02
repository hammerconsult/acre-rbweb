package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Seguranca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class UsuarioSistemaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            UsuarioSistema usuarioSistema = findUsuarioSistema(authentication);
            if (usuarioSistema.isAutenticacaoCorreta(Seguranca.md5(authentication.getCredentials().toString()))
                && !usuarioSistema.getExpira()) {

                if (usuarioSistema.getUsuarioUnidadeOrganizacional() != null) {
                    if (!usuarioSistema.getUsuarioUnidadeOrganizacional().isEmpty()) {
                        usuarioSistema.getUsuarioUnidadeOrganizacional().size();
                    }
                }
                return new UsernamePasswordAuthenticationToken(usuarioSistema, null, usuarioSistema.getAuthorities());
            } else {
                throw new BadCredentialsException("Autenticação incorreta.");
            }
        }
        return null;
    }

    private UsuarioSistema findUsuarioSistema(Authentication authentication) {
        singletonRecursosSistema.limparUsuario(authentication.getName());
        UsuarioSistema usuarioSistema = singletonRecursosSistema.getUsuarioPorCpf(authentication.getName());
        if (usuarioSistema == null) {
            throw new UsernameNotFoundException(String.format("Usuário não encontrado: '%s'",
                    authentication.getName()));
        }
        return usuarioSistema;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
