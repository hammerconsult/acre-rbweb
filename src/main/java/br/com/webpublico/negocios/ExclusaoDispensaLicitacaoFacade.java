package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.DispensaLicitacaoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelacionamentoLicitacao;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ExclusaoDispensaLicitacaoFacade extends AbstractFacade<ExclusaoDispensaLicitacao> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExclusaoDispensaLicitacaoFacade() {
        super(ExclusaoDispensaLicitacao.class);
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;

    public ExclusaoDispensaLicitacao salvarRetornando(ExclusaoDispensaLicitacao entity, List<RelacionamentoLicitacao> relacionamentos) {
        DispensaDeLicitacao dispensaDeLicitacao = em.find(DispensaDeLicitacao.class, entity.getIdDispensa());
        updateItemProcessoCompra(dispensaDeLicitacao);
        excluirDispensaDeLicitacao(relacionamentos, dispensaDeLicitacao);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExclusaoDispensaLicitacao.class, "numero"));
        }
        return em.merge(entity);
    }

    private void updateItemProcessoCompra(DispensaDeLicitacao dispensaDeLicitacao) {
        List<ItemProcessoDeCompra> itens = processoDeCompraFacade.buscarItensProcessoCompra(dispensaDeLicitacao.getProcessoDeCompra());
        for (ItemProcessoDeCompra item : itens) {
            if (!SituacaoItemProcessoDeCompra.AGUARDANDO.equals(item.getSituacaoItemProcessoDeCompra())) {
                item.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.AGUARDANDO);
                em.merge(item);
            }
        }
    }

    public void excluirDispensaDeLicitacao(List<RelacionamentoLicitacao> relacionamentos, DispensaDeLicitacao dispensaDeLicitacao) {
        for (RelacionamentoLicitacao rel : relacionamentos) {
            if (rel.isPortal()) {
                DispensaLicitacaoPortal dispensaLicitacaoPortal = em.find(DispensaLicitacaoPortal.class, rel.getId());
                em.remove(dispensaLicitacaoPortal);
            }
        }

        em.remove(dispensaDeLicitacao);
    }

    public List<RelacionamentoLicitacao> buscarRelacionamentosDispensaLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {

        String sql = "select id, " +
            "       tipo_movimento, " +
            "       data_movimento, " +
            "       movimento, " +
            "       cor " +
            "from (select dp.id                             as id, " +
            "             'PORTAL'                          as tipo_movimento, " +
            "             dl.datadadispensa                 as data_movimento, " +
            "             'Id da entidade portal ' || dp.id as movimento, " +
            "             'green'                           as cor " +
            "      from dispensalicitacaoportal dp " +
            "               inner join dispensadelicitacao dl on dl.id = dp.dispensadelicitacao_id " +
            "      where dl.id = :dispensaDeLicitacao " +
            "      union all " +
            "      select " +
            "          fdl.id                              as id, " +
            "          'PROPOSTA_FORNECEDOR'                        as tipo_movimento, " +
            "          dl.datadadispensa                  as data_movimento, " +
            "          formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || '-' || " +
            "          coalesce(pf.nome, pj.razaosocial) as movimento, " +
            "          'green' " +
            "      from fornecedordisplic fdl " +
            "               inner join dispensadelicitacao dl on fdl.dispensadelicitacao_id = dl.id " +
            "               inner join pessoa p on fdl.pessoa_id = p.id " +
            "               left join pessoafisica pf on p.id = pf.id " +
            "               left join pessoajuridica pj on p.id = pj.id " +
            "               inner join exercicio e on dl.exerciciodadispensa_id = e.id " +
            "      where dl.id = " +
            "            :dispensaDeLicitacao " +
            "      union all " +
            "      select c.id                              as id, " +
            "             'CONTRATO'                        as tipo_movimento, " +
            "             c.datalancamento                  as data_movimento, " +
            "             c.numerocontrato || ' - ' || c.numerotermo || '/' || e.ano || ' - ' || " +
            "             formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || '-' || " +
            "             coalesce(pf.nome, pj.razaosocial) as movimento, " +
            "             'red'                           as cor " +
            "      from condispensalicitacao cdl " +
            "               inner join contrato c on cdl.contrato_id = c.id " +
            "               inner join pessoa p on c.contratado_id = p.id " +
            "               left join pessoafisica pf on p.id = pf.id " +
            "               left join pessoajuridica pj on p.id = pj.id " +
            "               inner join exercicio e on c.exerciciocontrato_id = e.id " +
            "      where cdl.dispensadelicitacao_id = :dispensaDeLicitacao " +
            "      )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dispensaDeLicitacao", dispensaDeLicitacao.getId());
        List<RelacionamentoLicitacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RelacionamentoLicitacao rel = new RelacionamentoLicitacao();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setTipo(RelacionamentoLicitacao.TipoRelacionamento.valueOf((String) obj[1]));
            rel.setData((Date) obj[2]);
            rel.setMovimento((String) obj[3]);
            rel.setCor((String) obj[4]);
            toReturn.add(rel);
        }
        return toReturn;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

}
