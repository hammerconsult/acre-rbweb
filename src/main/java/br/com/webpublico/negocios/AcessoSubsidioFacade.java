/*
 * Codigo gerado automaticamente em Thu Oct 06 11:04:36 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFG;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFGFacade;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class AcessoSubsidioFacade extends AbstractFacade<AcessoSubsidio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ParametroControleCargoFGFacade parametroControleCargoFGFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EnquadramentoCCFacade enquadramentoCCFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;

    public AcessoSubsidioFacade() {
        super(AcessoSubsidio.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FuncaoGratificadaFacade getFuncaoGratificadaFacade() {
        return funcaoGratificadaFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    @Override
    public AcessoSubsidio recuperar(Object id) {

        System.out.println("id >> " + id);
        AcessoSubsidio cc = em.find(AcessoSubsidio.class, id);
        cc.getEnquadramentoCCs().size();
        if (cc.getBaseFP() != null) {
            Hibernate.initialize(cc.getBaseFP().getItensBasesFPs());
        }
        return cc;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void validarValoresEntidade(VinculoFP vinculo) {
        ValidacaoException ve = new ValidacaoException();
        String codigoHO = contratoFPFacade.recuperarCodigoHierarquiaVinculoFP(vinculo);
        ParametroControleCargoFG parametro = parametroControleCargoFGFacade.recuperarPorEntidadeAndVigencia(codigoHO, sistemaFacade.getDataOperacao());
        if (parametro != null && parametro.getValorCargoComissao() != null) {
            BigDecimal valorTotalEntidade = parametroControleCargoFGFacade.recuperarRemuneracaoAcessoCargoComissaoPorEntidade(codigoHO, sistemaFacade.getDataOperacao());
            if (valorTotalEntidade != null && valorTotalEntidade.compareTo(parametro.getValorCargoComissao()) >= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade Máxima em Reais para os servidores para a Entidade <b>" + vinculo.getContratoFP().getUnidadeOrganizacional().getEntidade() + "</b> " +
                    "é de <b>R$" + Util.formataNumero(parametro.getValorCargoComissao()) + "</b>, no momento possuindo <b>R$" + Util.formataNumero(valorTotalEntidade) + "</b>.");
            }
        }
        ve.lancarException();
    }

    public void salvarAcessoSubsidio(AcessoSubsidio acessoSubsidio, ProvimentoFP provimentoFP) {
        if (provimentoFP.getId() == null) {
            getEntityManager().persist(provimentoFP);
        } else {
            getEntityManager().merge(provimentoFP);
        }

        acessoSubsidio.setProvimentoFP(provimentoFP);

        if (acessoSubsidio.getId() == null) {
            getEntityManager().persist(acessoSubsidio);
        } else {
            getEntityManager().merge(acessoSubsidio);
        }
    }

    public List<CargoConfianca> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select cargo from CargoConfianca cargo "
            + " inner join cargo.contratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join cargo.opcaoSalarialCC opcao "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(opcao.codigo) like :filtro) "
            + " or (lower(opcao.descricao) like :filtro)"
            + " or (to_char(cargo.inicioVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(cargo.finalVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(cargo.dataNomeacao,'dd/MM/yyyy') like :filtro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<AcessoSubsidio> buscarAcessoSubsidioPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select cargo from AcessoSubsidio cargo "
            + " inner join cargo.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= cargo.inicioVigencia "
            + " and :dataVigencia <= coalesce(cargo.finalVigencia,:dataVigencia)  ");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();

    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EnquadramentoCCFacade getEnquadramentoCCFacade() {
        return enquadramentoCCFacade;
    }

    public void salvarRetornoProvimentoAcessoSubsidio(AcessoSubsidio acessoSubsidio) {
        acessoSubsidio.setProvimentoRetorno(em.merge(getProvimentoFPParaRetornoCargoCarreira(acessoSubsidio)));
        em.merge(acessoSubsidio);
    }

    public ProvimentoFP getProvimentoFPParaRetornoCargoCarreira(AcessoSubsidio acessoSubsidio) {
        ProvimentoFP provimentoRetornoCargoCarreira = new ProvimentoFP();
        if (acessoSubsidio.temProvimentoRetorno()) {
            provimentoRetornoCargoCarreira = acessoSubsidio.getProvimentoRetorno();
        }
        provimentoRetornoCargoCarreira.setAtoLegal(acessoSubsidio.getAtoLegal());
        provimentoRetornoCargoCarreira.setVinculoFP(acessoSubsidio.getContratoFP());
        provimentoRetornoCargoCarreira.setDataProvimento(acessoSubsidio.getFinalVigencia());
        provimentoRetornoCargoCarreira.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(acessoSubsidio.getContratoFP()));
        provimentoRetornoCargoCarreira.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.RETORNO_CARGO_CARREIRA));

        return provimentoRetornoCargoCarreira;
    }

    public List<AcessoSubsidio> buscarFuncaoGratificadaPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select cargo from AcessoSubsidio cargo "
            + " inner join cargo.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= cargo.inicioVigencia "
            + " and :dataVigencia <= coalesce(cargo.finalVigencia,:dataVigencia)  ");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();

    }

    public List<AcessoSubsidio> completaAcessoSubsidio(String s) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select distinct ");
        hql.append(" cargo.id  ");
        hql.append(" from CONTRATOFP contrato inner join VINCULOFP vinculo on vinculo.ID = contrato.ID inner join");
        hql.append(" MATRICULAFP matricula on matricula.ID = vinculo.MATRICULAFP_ID");
        hql.append(" inner join ACESSOSubsidio cargo on cargo.contratofp_id = contrato.id");
        hql.append(" inner join PESSOAFISICA pf on pf.ID = matricula.PESSOA_ID WHERE");
        hql.append(" ((lower(pf.NOME) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.MATRICULA) like :filtro))  ");
        hql.append(" and contrato.id not in (select c.contratofp_id from AcessoSubsidio c where c.provimentoRetorno_id is not null) ");
        hql.append(" and cargo.inicioVigencia <= :hoje ");
        hql.append(" and coalesce(cargo.finalVigencia,:hoje) >= :hoje ");
        hql.append(" and rownum <= 10 ");

        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("hoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(9);
        List<BigDecimal> ids = q.getResultList();
        List<AcessoSubsidio> acessos = new ArrayList<>();
        for (BigDecimal id : ids) {
            acessos.add(em.find(AcessoSubsidio.class, id.longValue()));
        }
        return acessos;
    }

    public List<ContratoFP> recuperaContratoVigentePorAcessoSubsidio(String s) {
        String hql = "select distinct contrato from AcessoSubsidio obj "
            + " join obj.contratoFP contrato "
            + " join contrato.matriculaFP matricula "
            + " join matricula.pessoa pes "
            + " left join pes.documentosPessoais docs "
            + " where ((lower(pes.nome) like :filtro) OR (lower(pes.cpf) like :filtro) OR (lower(matricula.matricula) like :filtro) or (docs.numero like :filtro)) "
            + " and :dataVigencia between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataVigencia) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }
}
