package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoSituacaoCalculoISS;
import br.com.webpublico.negocios.CalculaISSFacade;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
@Transactional
public class ServiceIssForaMunicipio {

    private static final Logger logger = LoggerFactory.getLogger(ServiceIssForaMunicipio.class.getName());
    @PersistenceContext
    protected transient EntityManager em;
    @Autowired
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;

    @PostConstruct
    private void init() {
    }

    public void cancelarDebitosVencidos() {
        cancelarDebitosIssForaMunicipioVencidos();
    }

    private void cancelarDebitosIssForaMunicipioVencidos() {
        String sql = "select distinct spvd.* " +
            "   from calculoiss iss " +
            "inner join calculo c on c.id = iss.id " +
            "inner join valordivida vd on vd.calculo_id = iss.id " +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "left join itemdam i on i.parcela_id = pvd.id " +
            "left join dam d on d.id = i.dam_id " +
            "where coalesce(c.geradoportalcontribuinte, 0) = 1 " +
            "  and spvd.situacaoparcela = :situacao_parcela " +
            "  and (d.id is null or (d.tipo = :tipo_dam" +
            "                        and d.situacao = :situacao_dam " +
            "                        and d.vencimento + coalesce((select max(QTDEDIASESTORNO) from CONFIGURACAOISSFORAMUN),0) " +
            "                            < trunc(current_date))) ";
        Query q = em.createNativeQuery(sql, SituacaoParcelaValorDivida.class);
        q.setParameter("situacao_parcela", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("tipo_dam", DAM.Tipo.UNICO.name());
        q.setParameter("situacao_dam", DAM.Situacao.ABERTO.name());
        List<SituacaoParcelaValorDivida> abertos = q.getResultList();
        for (SituacaoParcelaValorDivida aberto : abertos) {
            parcelaValorDividaDAO.inserirSituacaoParcelaValorDivida(aberto.getParcela(), aberto, SituacaoParcela.CANCELAMENTO);
            estornarSituacaoLancamento(aberto.getParcela());
        }
    }

    private void estornarSituacaoLancamento(ParcelaValorDivida parcela) {
        parcelaValorDividaDAO.estornarLancamentoDeIss(parcela);
    }

}
