package br.com.webpublico.negocios.comum.services;

import br.com.webpublico.entidades.BloqueioDesbloqueioUsuario;
import br.com.webpublico.entidades.BloqueioDesbloqueioUsuarioEmail;
import br.com.webpublico.entidades.BloqueioDesbloqueioUsuarioTipoAfastamento;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.BloqueioDesbloqueioUsuarioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.util.EmailService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class ServiceBloqueioDesbloqueioUsuario {

    private static String QUEBRA_LINHA = "<br/>";
    private static final Logger logger = LoggerFactory.getLogger(ServiceBloqueioDesbloqueioUsuario.class.getName());
    private BloqueioDesbloqueioUsuarioFacade bloqueioDesbloqueioUsuarioFacade;
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private ConfiguracaoEmailFacade configuracaoEmailFacade;
    private SistemaFacade sistemaFacade;

    @PostConstruct
    public void init() {
        try {
            bloqueioDesbloqueioUsuarioFacade = (BloqueioDesbloqueioUsuarioFacade) new InitialContext().lookup("java:module/BloqueioDesbloqueioUsuarioFacade");
            usuarioSistemaFacade = (UsuarioSistemaFacade) new InitialContext().lookup("java:module/UsuarioSistemaFacade");
            configuracaoEmailFacade = (ConfiguracaoEmailFacade) new InitialContext().lookup("java:module/ConfiguracaoEmailFacade");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
        } catch (Exception e) {
            logger.error("Erro ao injetar o EJB: " + e.getMessage());
        }
    }

    public void enviarEmail(String mail, String titulo, String conteudo) {
        if (isEmailValido(mail)) {
            EmailService.getInstance().enviarEmail(mail, titulo, conteudo);
        }
    }

    private boolean isEmailValido(String mail) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(mail);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public void processarBloqueios() {
        try {
            List<BloqueioDesbloqueioUsuario> bloqueios = bloqueioDesbloqueioUsuarioFacade.buscarBloqueiosVigentes(new Date());
            for (BloqueioDesbloqueioUsuario bloqueio : bloqueios) {
                Set<UsuarioSistema> usuariosPorAfastamento = new HashSet<>();
                if (bloqueio.getBloquearAfastamento()) {
                    usuariosPorAfastamento.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorAfastamento(new Date(), buscarTiposDeAfastamento(bloqueio), false));
                }
                Set<UsuarioSistema> usuariosPorFerias = new HashSet<>();
                if (bloqueio.getBloquearFerias()) {
                    usuariosPorFerias.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorFerias(new Date(), false));
                }
                Set<UsuarioSistema> usuariosPorLicencaPremio = new HashSet<>();
                if (bloqueio.getBloquearLicensaPremio()) {
                    usuariosPorLicencaPremio.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorTipo(new Date(), TipoPeriodoAquisitivo.LICENCA, false));
                }
                Set<UsuarioSistema> usuariosPorExoneracao = new HashSet<>();
                if (bloqueio.getBloquearExoneracao()) {
                    usuariosPorExoneracao.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorExoneracao(new Date(), false));
                }
                Set<UsuarioSistema> usuariosPorCessaoSemOnus = new HashSet<>();
                if (bloqueio.getBloquearCessaoSemOnus()) {
                    usuariosPorCessaoSemOnus.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorCessaoSemOnus(new Date(), false));
                }
                Set<UsuarioSistema> usuariosPorPerfilEspecialComInatividadeExcedida = new HashSet<>(usuarioSistemaFacade.getUsuarioSistemaService().getUsuariosPorPerfilEspecialComInatividadeExcedida(new Date()));
                enviarEmail(bloqueio, "bloqueados", montarConteudo(usuariosPorAfastamento, usuariosPorFerias, usuariosPorLicencaPremio, usuariosPorExoneracao, usuariosPorCessaoSemOnus, usuariosPorPerfilEspecialComInatividadeExcedida));
            }
        } catch (Exception ex) {
            logger.error("Erro ao Bloquear Usuários: " + ex.getMessage());
        }
    }

    private String montarConteudo(Set<UsuarioSistema> usuariosPorAfastamento, Set<UsuarioSistema> usuariosPorFerias, Set<UsuarioSistema> usuariosPorLicencaPremio, Set<UsuarioSistema> usuariosPorExoneracao, Set<UsuarioSistema> usuariosPorCessaoSemOnus, Set<UsuarioSistema> usuariosPorPerfilEspecialComInatividade) {
        StringBuilder conteudo = new StringBuilder();
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorAfastamento, "<b> por Afastamento: </b> ", true));
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorFerias, "<b> por Férias: </b> ", true));
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorLicencaPremio, "<b> por Licença Prêmio: </b> ", true));
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorExoneracao, "<b> por Exoneração: </b> ", true));
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorCessaoSemOnus, "<b> por Cessão sem Ônus: </b> ", true));
        conteudo.append(montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosPorPerfilEspecialComInatividade, "<b> por Perfil Especial com Inatividade Excedente: </b> ", true));
        return conteudo.toString();
    }

    private String montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(Set<UsuarioSistema> usuarios, String motivo, Boolean bloquear) {
        StringBuilder conteudo = new StringBuilder();
        if (!usuarios.isEmpty()) {
            conteudo.append(motivo).append(QUEBRA_LINHA).append("<ul>");
            for (UsuarioSistema usuario : usuarios) {
                conteudo.append("<li> ").append(usuario.getLogin()).append(QUEBRA_LINHA).append("</li>");
                if (bloquear) {
                    bloquearUsuario(usuario);
                } else {
                    desbloquearUsuario(usuario);
                }
            }
            conteudo.append(QUEBRA_LINHA).append("</ul>");
        }
        return conteudo.toString();
    }

    private void bloquearUsuario(UsuarioSistema usuario) {
        usuario.setExpira(true);
        usuarioSistemaFacade.salvar(usuario);
    }

    private void desbloquearUsuario(UsuarioSistema usuario) {
        usuario.setExpira(false);
        usuarioSistemaFacade.salvar(usuario);
    }

    private void enviarEmail(BloqueioDesbloqueioUsuario bloqueio, String bloqueadoDesbloqueado, String conteudo) {
        if (!conteudo.isEmpty()) {
            for (BloqueioDesbloqueioUsuarioEmail email : bloqueio.getEmails()) {
                enviarEmail(email.getEmail(), "Usuários " + primeiraLetraMaiuscula(bloqueadoDesbloqueado) + " Automaticamente", "<b>Os seguintes usuários foram " + bloqueadoDesbloqueado + " automaticamente</b>" + QUEBRA_LINHA + conteudo);
            }
        }
    }

    private String primeiraLetraMaiuscula(String palavra) {
        return palavra.substring(0, 1).toUpperCase().concat(palavra.substring(1));
    }

    private List<Long> buscarTiposDeAfastamento(BloqueioDesbloqueioUsuario bloqueio) {
        List<Long> tipos = Lists.newArrayList();
        for (BloqueioDesbloqueioUsuarioTipoAfastamento tipoAfastamento : bloqueio.getTiposDeAfastamento()) {
            tipos.add(tipoAfastamento.getTipoAfastamento().getId());
        }
        return tipos;
    }

    public void processarDesbloqueio() {
        try {
            List<BloqueioDesbloqueioUsuario> bloqueios = bloqueioDesbloqueioUsuarioFacade.buscarBloqueiosVigentes(new Date());
            for (BloqueioDesbloqueioUsuario desbloqueio : bloqueios) {
                Set<UsuarioSistema> usuariosBloqueados = new HashSet<>(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosBloqueados());
                if (desbloqueio.getBloquearAfastamento()) {
                    usuariosBloqueados.removeAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorAfastamento(new Date(), buscarTiposDeAfastamento(desbloqueio), true));
                }
                if (desbloqueio.getBloquearFerias()) {
                    usuariosBloqueados.removeAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorFerias(new Date(), true));
                }
                if (desbloqueio.getBloquearLicensaPremio()) {
                    usuariosBloqueados.removeAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorTipo(new Date(), TipoPeriodoAquisitivo.LICENCA, true));
                }
                if (desbloqueio.getBloquearExoneracao()) {
                    usuariosBloqueados.removeAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorExoneracao(new Date(), true));
                }
                if (desbloqueio.getBloquearCessaoSemOnus()) {
                    usuariosBloqueados.addAll(bloqueioDesbloqueioUsuarioFacade.buscarUsuariosParaBloqueioPorCessaoSemOnus(new Date(), true));
                }
                enviarEmail(desbloqueio, "desbloqueados", montarConteudoPorUsuarioAndMotivoEBloqueiaDesbloqueiaUsuario(usuariosBloqueados, "", false));
            }
        } catch (Exception ex) {
            logger.error("Erro ao Desbloquear Usuários: " + ex.getMessage());
        }
    }
}
