/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class ParametrosTransitoTransporteFacade extends AbstractFacade<ParametrosTransitoTransporte> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private IndiceEconomicoFacade indiceFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametrosTransitoTransporteFacade() {
        super(ParametrosTransitoTransporte.class);
    }

    @Override
    public ParametrosTransitoTransporte recuperar(Object id) {
        ParametrosTransitoTransporte param = em.find(ParametrosTransitoTransporte.class, id);
        Hibernate.initialize(param.getTaxasTransito());
        Hibernate.initialize(param.getVencimentos());
        Hibernate.initialize(param.getParametrosTermos());
        Hibernate.initialize(param.getParametrosValoresTransferencias());
        Collections.sort(param.getParametrosValoresTransferencias());
        return param;
    }

    public ParametrosTransitoRBTransFacade getParametrosTransitoRBTransFacade() {
        return parametrosTransitoRBTransFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public TipoAutonomoFacade getTipoAutonomoFacade() {
        return tipoAutonomoFacade;
    }

    public IndiceEconomicoFacade getIndiceFacade() {
        return indiceFacade;
    }

    public void salvar(Map<TipoPermissaoRBTrans, ParametrosTransitoTransporte> mapa) {
        for (Map.Entry<TipoPermissaoRBTrans, ParametrosTransitoTransporte> param : mapa.entrySet()) {
            ParametrosTransitoTransporte parametrosTransitoTransporte = param.getValue();
            ParametrosTransitoTransporte novoParametro = copiarDadosDoParametroTransitoTranporteParaNovo(parametrosTransitoTransporte);

            if (parametrosTransitoTransporte.getId() != null) {
                parametrosTransitoTransporte = recuperar(parametrosTransitoTransporte.getId());
                parametrosTransitoTransporte.setFimVigencia(new Date());
                em.merge(parametrosTransitoTransporte);
            }
            em.persist(novoParametro);
        }
    }

    private ParametrosTransitoTransporte copiarDadosDoParametroTransitoTranporteParaNovo(ParametrosTransitoTransporte parametros) {
        ParametrosTransitoTransporte novo = new ParametrosTransitoTransporte();
        novo.setTipoPermissao(parametros.getTipoPermissao());
        novo.setTipo(parametros.getTipo());
        novo.setAlteradoPor(sistemaFacade.getUsuarioCorrente());
        novo.setInicioVigencia(new Date());
        novo.setFimVigencia(null);
        novo.setLimiteIdade(parametros.getLimiteIdade());
        novo.setLimitePermissoes(parametros.getLimitePermissoes());
        novo.setNaturezaJuridica(parametros.getNaturezaJuridica());
        novo.setTipoAutonomo(parametros.getTipoAutonomo());
        novo.setGerarOutorga(parametros.getGerarOutorga());
        novo.setHoraAntecedenciaViagem(parametros.getHoraAntecedenciaViagem());

        for (TaxaTransito taxa : parametros.getTaxasTransito()) {
            TaxaTransito novaTaxa = new TaxaTransito();
            novaTaxa.setDivida(taxa.getDivida());
            novaTaxa.setParametrosTransitoConfiguracao(novo);
            novaTaxa.setTipoCalculoRBTRans(taxa.getTipoCalculoRBTRans());
            novaTaxa.setTributo(taxa.getTributo());
            novaTaxa.setValor(taxa.getValor());
            novo.getTaxasTransito().add(novaTaxa);
        }

        for (ParametrosTransitoTermos termo : parametros.getParametrosTermos()) {
            ParametrosTransitoTermos novoTermo = new ParametrosTransitoTermos();
            novoTermo.setTipoTermoRBTrans(termo.getTipoTermoRBTrans());
            novoTermo.setParametrosTransitoConfiguracao(novo);
            novoTermo.setTipoDoctoOficial(termo.getTipoDoctoOficial());

            novo.getParametrosTermos().add(novoTermo);
        }

        for (ParametrosValoresTransferencia transferencia : parametros.getParametrosValoresTransferencias()) {
            ParametrosValoresTransferencia novaTransferencia = new ParametrosValoresTransferencia();
            novaTransferencia.setParametrosTransitoConfiguracao(novo);
            novaTransferencia.setQuantidade(transferencia.getQuantidade());
            novaTransferencia.setDivida(transferencia.getDivida());
            novaTransferencia.setTributo(transferencia.getTributo());
            novaTransferencia.setValor(transferencia.getValor());

            novo.getParametrosValoresTransferencias().add(novaTransferencia);
        }

        for (DigitoVencimento obj : parametros.getVencimentos()) {
            DigitoVencimento novoVencimento = new DigitoVencimento();

            novoVencimento.setDigito(obj.getDigito());
            novoVencimento.setTipoDigitoVencimento(obj.getTipoDigitoVencimento());
            novoVencimento.setDia(obj.getDia());
            novoVencimento.setMes(obj.getMes());
            novoVencimento.setParametro(novo);

            novo.getVencimentos().add(novoVencimento);
        }
        return novo;
    }

    public ParametrosTransitoTransporte recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans tipo) {
        Query q = em.createNativeQuery("select p.id from paramtransitotransporte p " +
                " inner join ParametrosTransitoRBTrans ptrbtrans on p.id = ptrbtrans.id " +
                " where p.tipoPermissao = :tipo and ptrbtrans.fimVigencia is null")
            .setParameter("tipo", tipo.name());
        List<BigDecimal> result = q.getResultList();
        if (!result.isEmpty()) return recuperar(result.get(0).longValue());
        return null;
    }

    public List<TaxaTransito> recuperarTaxaTransitoPeloTipo(ParametrosTransitoTransporte param, TipoCalculoRBTRans... tipoCalculo) {
        Query q = em.createQuery("from TaxaTransito t where t.parametrosTransitoConfiguracao = :param and t.tipoCalculoRBTRans in (:tipoCalculo)")
            .setParameter("param", param).setParameter("tipoCalculo", Lists.newArrayList(tipoCalculo));
        return q.getResultList();
    }

    public List<Divida> listaTodasDividasInformadasNosParametros() {
        String hql = "select distinct divida from TaxaTransito";
        List<Divida> retorno = (List<Divida>) em.createQuery(hql).getResultList();
        hql = "select distinct divida from ParametrosValoresTransferencia";
        retorno.addAll((List<Divida>) em.createQuery(hql).getResultList());
        return retorno;
    }

    public int maximoTransferenciasMotoTaxi() {
        String hql = "select max(trans.quantidade) from ParametrosValoresTransferencia trans " +
            " join trans.parametrosTransitoConfiguracao param " +
            " where param.tipoPermissao = :tipo and param.fimVigencia is null ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", TipoPermissaoRBTrans.MOTO_TAXI);
        if (q.getResultList().isEmpty()) return 0;
        return (int) q.getSingleResult();
    }

    public List<TaxaTransito> recuperarTaxaTransitoTransferenciaMotoTaxi(ParametrosTransitoTransporte param, int quantidade) {
        List<TaxaTransito> taxaTransitos = Lists.newArrayList();
        Query q = em.createQuery("from ParametrosValoresTransferencia p where p.parametrosTransitoConfiguracao = :param and p.quantidade = :qtde")
            .setParameter("param", param).setParameter("qtde", quantidade);
        for (ParametrosValoresTransferencia p : (List<ParametrosValoresTransferencia>) q.getResultList()) {
            TaxaTransito tx = new TaxaTransito();
            tx.setDivida(p.getDivida());
            tx.setTributo(p.getTributo());
            tx.setValor(p.getValor());
            tx.setParametrosTransitoConfiguracao(p.getParametrosTransitoConfiguracao());
            tx.setTipoCalculoRBTRans(TipoCalculoRBTRans.TRANSFERENCIA_PERMISSAO);
            taxaTransitos.add(tx);
        }
        return taxaTransitos;
    }
}
