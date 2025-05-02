package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.DeprecicaoNotificacao;
import br.com.webpublico.enums.SituacaoReducaoValorBem;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class NotificacaoDepreciacaoService {

    @PersistenceContext
    protected transient EntityManager em;

    @Transactional(timeout = 10000, propagation = Propagation.REQUIRES_NEW)
    public void notificar() {
        List<DeprecicaoNotificacao> depreciacoes = buscarDepreciacoes();
        if (depreciacoes.isEmpty()) {
            return;
        }
        for (DeprecicaoNotificacao depreciacao : depreciacoes) {
            Notificacao not = new Notificacao();
            not.setGravidade(Notificacao.Gravidade.INFORMACAO);
            not.setLink("/depreciacao-movel/ver/" + depreciacao.getId() + "/");
            not.setDescricao("A depreciação nº" + depreciacao.getNumero() + " realizada em " + DataUtil.getDataFormatada(depreciacao.getDataLacamento())
                + " encontra-se em elaboração, faz necessário concluí-la dentro do mês para compor o fechamento mensal contábil.");
            not.setTitulo("Depreciação em Elaboração");
            not.setUnidadeOrganizacional(depreciacao.getUnidadeOrganizacional());
            not.setUsuarioSistema(depreciacao.getUsuarioSistema());
            not.setTipoNotificacao(TipoNotificacao.AVISO_DEPRECIACAO_EM_ELABORACAO);
            NotificacaoService.getService().notificar(not);
        }
    }

    private List<DeprecicaoNotificacao> buscarDepreciacoes() {
        String sql = " " +
            " select " +
            "   dep.id as id_depreciacao, " +
            "   dep.codigo as numero, " +
            "   dep.datalancamento as data_lancamento, " +
            "   dep.unidadeorcamentaria_id as id_unidade," +
            "   dep.usuariosistema_id as id_usuario " +
            " from lotereducaovalorbem dep " +
            "   where dep.tiporeducao = :tipoReducao " +
            "   and dep.situacao = :situacao " +
            "   and trunc(dep.data) <= to_date(:dataOperacao, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
        q.setParameter("tipoReducao", TipoReducaoValorBem.DEPRECIACAO.name());
        q.setParameter("situacao", SituacaoReducaoValorBem.EM_ELABORACAO.name());
        List<DeprecicaoNotificacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            DeprecicaoNotificacao dep = new DeprecicaoNotificacao();
            dep.setId(((BigDecimal) obj[0]).longValue());
            dep.setNumero(((BigDecimal) obj[1]).longValue());
            dep.setDataLacamento((Date) obj[2]);
            UnidadeOrganizacional unidadeOrganizacional = em.find(UnidadeOrganizacional.class, ((BigDecimal) obj[3]).longValue());
            dep.setUnidadeOrganizacional(unidadeOrganizacional);
            UsuarioSistema usuarioSistema = em.find(UsuarioSistema.class, ((BigDecimal) obj[4]).longValue());
            dep.setUsuarioSistema(usuarioSistema);
            toReturn.add(dep);
        }
        return toReturn;
    }
}
