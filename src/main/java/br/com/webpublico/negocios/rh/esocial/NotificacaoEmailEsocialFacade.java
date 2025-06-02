package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.NotificacaoEmailEsocial;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;
import com.beust.jcommander.internal.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Stateless
public class NotificacaoEmailEsocialFacade extends AbstractFacade<NotificacaoEmailEsocial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;

    public NotificacaoEmailEsocialFacade() {
        super(NotificacaoEmailEsocial.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean hasEmpregadorCadastrado(Long id) {
        String sql = "select * from NotificacaoEmailEsocial where empregador_id = :idEmpregador";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEmpregador", id);
        return !q.getResultList().isEmpty();
    }

    @Override
    public NotificacaoEmailEsocial recuperar(Object id) {
        NotificacaoEmailEsocial notificao = em.find(NotificacaoEmailEsocial.class, id);
        Hibernate.initialize(notificao.getUsuarios());
        return notificao;
    }

    public void enviarEmail(String conteudo, String assunto, String email) {
        EmailService.getInstance().enviarEmail(emailParaEnvio(email), assunto, conteudo);
    }

    public String[] emailParaEnvio(String emails) {
        String[] emailsQuebrados = null;
        if (emails != null && !emails.trim().isEmpty()) {
            emailsQuebrados = emails.split(Pattern.quote(","));
        }
        return emailsQuebrados;
    }

    public Set<EventoFP> buscarEventoFPSemconfiguracaoEmpregador(ConfiguracaoEmpregadorESocial config) {
        if (config.getItemConfiguracaoEmpregadorESocial() == null || config.getItemConfiguracaoEmpregadorESocial().isEmpty()) {
            return null;
        }
        String sql = "select  evento.* " +
            " from folhadepagamento folha " +
            "         inner join fichafinanceirafp ficha on folha.ID = ficha.FOLHADEPAGAMENTO_ID " +
            "         inner join ItemFichaFinanceiraFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +
            "         inner join eventofp evento on item.EVENTOFP_ID = evento.ID " +
            "         inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            " where folha.ano >= :anoInicioObrigatorierdade " +
            "  and  vinculo.unidadeOrganizacional_id in (:unidades) " +
            "  and not exists(select * from EventoFPEmpregador where EVENTOFP_ID = evento.ID and ENTIDADE_ID = :entidade) " +
            " order by to_number(evento.CODIGO)";

        Query q = em.createNativeQuery(sql, EventoFP.class);
        q.setParameter("entidade", config.getEntidade());
        q.setParameter("unidades", montarIdOrgaoEmpregador(config));
        q.setParameter("anoInicioObrigatorierdade", DataUtil.getAno(config.getDataInicioObrigatoriedadeFase3()));
        List list = q.getResultList();
        if (list != null && !list.isEmpty()) {
            return new LinkedHashSet<>(list);
        }
        return null;
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }
}
