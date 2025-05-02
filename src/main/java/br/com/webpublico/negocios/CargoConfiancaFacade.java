/*
 * Codigo gerado automaticamente em Thu Oct 06 11:04:36 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFG;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFGFacade;
import br.com.webpublico.negocios.rh.pccr.CategoriaPCCRFacade;
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
public class CargoConfiancaFacade extends AbstractFacade<CargoConfianca> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private EnquadramentoCCFacade enquadramentoCCFacade;
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
    private CategoriaPCCRFacade categoriaPCCRFacade;

    public CargoConfiancaFacade() {
        super(CargoConfianca.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoCCFacade getEnquadramentoCCFacade() {
        return enquadramentoCCFacade;
    }

    public FuncaoGratificadaFacade getFuncaoGratificadaFacade() {
        return funcaoGratificadaFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    @Override
    public CargoConfianca recuperar(Object id) {
        CargoConfianca cc = em.find(CargoConfianca.class, id);
        cc.getEnquadramentoCCs().size();
        if (cc.getBaseFP() != null) {
            Hibernate.initialize(cc.getBaseFP().getItensBasesFPs());
        }
        return cc;
    }

    public void validarValoresEntidade(CargoConfianca cargoConfianca) {
        ValidacaoException ve = new ValidacaoException();
        String codigoHO = contratoFPFacade.recuperarCodigoHierarquiaVinculoFP(cargoConfianca.getContratoFP());
        ParametroControleCargoFG parametro = parametroControleCargoFGFacade.recuperarPorEntidadeAndVigencia(codigoHO, sistemaFacade.getDataOperacao());
        if (parametro != null && parametro.getValorCargoComissao() != null) {
            BigDecimal valorTotalEntidade = parametroControleCargoFGFacade.recuperarRemuneracaoAcessoCargoComissaoPorEntidade(codigoHO, sistemaFacade.getDataOperacao());
            if (valorTotalEntidade != null && valorTotalEntidade.compareTo(parametro.getValorCargoComissao()) >= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade Máxima em Reais para os servidores para a Entidade <b>" + cargoConfianca.getContratoFP().getUnidadeOrganizacional().getEntidade() + "</b> " +
                    "é de <b>R$" + Util.formataNumero(parametro.getValorCargoComissao()) + "</b>, no momento possuindo <b>R$" + Util.formataNumero(valorTotalEntidade) + "</b>.");
            }
        }
        ve.lancarException();
    }

    public List<CargoConfianca> buscarVinculofpPorServidorAndPeriodo(ContratoFP contratofp, Date inicioVigencia, Date finalVigencia) {
        String sql = "select cc.* " +
            "  from cargoconfianca cc " +
            " where  (cc.contratofp_id = :contratofp) " +
            "   and (:inicioVigencia between cc.iniciovigencia and cc.finalvigencia) ";
        if (finalVigencia != null) {
            sql += "   or (:finalVigencia between cc.finalvigencia and cc.iniciovigencia) ";
        }
        Query q = em.createNativeQuery(sql, CargoConfianca.class);
        q.setParameter("contratofp", contratofp);
        q.setParameter("inicioVigencia", inicioVigencia);
        if (finalVigencia != null) {
            q.setParameter("finalVigencia", finalVigencia);
        }
        return q.getResultList();
    }

    public void salvarCargoConfianca(CargoConfianca cargoConfianca, ProvimentoFP provimentoFP) {
        if (provimentoFP.getId() == null) {
            getEntityManager().persist(provimentoFP);
        } else {
            getEntityManager().merge(provimentoFP);
        }

        cargoConfianca.setProvimentoFP(provimentoFP);

        if (cargoConfianca.getId() == null) {
            getEntityManager().persist(cargoConfianca);
        } else {
            getEntityManager().merge(cargoConfianca);
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

    public List<CargoConfianca> listaFiltrandoXRetorno(String s, int inicio, int max) {
        String hql = " select cargo from CargoConfianca cargo "
            + " inner join cargo.contratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join cargo.opcaoSalarialCC opcao "
            + " where cargo.provimentoRetorno is not null and  ((lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(opcao.codigo) like :filtro) "
            + " or (lower(opcao.descricao) like :filtro)"
            + " or (to_char(cargo.inicioVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(cargo.finalVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(cargo.dataNomeacao,'dd/MM/yyyy') like :filtro)) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<CargoConfianca> listaCargosRetornados() {
        Query q = em.createQuery("from CargoConfianca cargo where cargo.provimentoRetorno is not null");
        return q.getResultList();
    }

    public List<CargoConfianca> completaCargoConfianca(String s) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select distinct ");
        hql.append(" cargo.id  ");
        hql.append(" from CONTRATOFP contrato inner join VINCULOFP vinculo on vinculo.ID = contrato.ID inner join");
        hql.append(" MATRICULAFP matricula on matricula.ID = vinculo.MATRICULAFP_ID");
        hql.append(" inner join CargoConfianca cargo on cargo.contratofp_id = contrato.id");
        hql.append(" inner join PESSOAFISICA pf on pf.ID = matricula.PESSOA_ID WHERE");
        hql.append(" ((lower(pf.NOME) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.MATRICULA) like :filtro))  ");
        hql.append(" and contrato.id not in (select c.contratofp_id from CargoConfianca c where c.provimentoRetorno_id is not null) ");
        hql.append(" and cargo.inicioVigencia <= :hoje ");
        hql.append(" and coalesce(cargo.finalVigencia,:hoje) >= :hoje ");
        hql.append(" and rownum <= 10 ");

        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("hoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(9);
        List<BigDecimal> ids = q.getResultList();
        List<CargoConfianca> cargosConfianca = new ArrayList<>();
        for (BigDecimal id : ids) {
            cargosConfianca.add(em.find(CargoConfianca.class, id.longValue()));
        }
        return cargosConfianca;
    }

    public List<CargoConfianca> buscarFuncaoGratificadaPorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select cargo from CargoConfianca cargo "
            + " inner join cargo.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= cargo.inicioVigencia "
            + " and :dataVigencia <= coalesce(cargo.finalVigencia,:dataVigencia)  ");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();

    }

    public List<CargoConfianca> recuperaCargoConfiancaContratoSemVigencia(ContratoFP contratoFP) {
        Query q = em.createQuery(" select cargo from CargoConfianca cargo "
            + " inner join cargo.contratoFP contrato "
            + " where contrato = :parametroContrato ");
        q.setParameter("parametroContrato", contratoFP);
        return q.getResultList();
    }

    public void salvarRetornoProvimentoCargoConfianca(CargoConfianca cargoConfianca) {
        cargoConfianca.setProvimentoRetorno(em.merge(getProvimentoFPParaRetornoCargoCarreira(cargoConfianca)));
        em.merge(cargoConfianca);
    }

    public ProvimentoFP getProvimentoFPParaRetornoCargoCarreira(CargoConfianca cargoConfianca) {
        ProvimentoFP provimentoRetornoCargoCarreira = new ProvimentoFP();
        if (cargoConfianca.temProvimentoRetorno()) {
            provimentoRetornoCargoCarreira = cargoConfianca.getProvimentoRetorno();
        }
        provimentoRetornoCargoCarreira.setAtoLegal(cargoConfianca.getAtoLegal());
        provimentoRetornoCargoCarreira.setVinculoFP(cargoConfianca.getContratoFP());
        provimentoRetornoCargoCarreira.setDataProvimento(cargoConfianca.getFinalVigencia());
        provimentoRetornoCargoCarreira.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(cargoConfianca.getContratoFP()));
        provimentoRetornoCargoCarreira.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.RETORNO_CARGO_CARREIRA));

        return provimentoRetornoCargoCarreira;
    }

    public boolean excluir(CargoConfianca cc) {
        try {
            CargoConfianca c = em.find(CargoConfianca.class, cc.getId());
            ProvimentoFP p = em.find(ProvimentoFP.class, c.getProvimentoRetorno().getId());
            c.setAtoLegal(null);
            c.setAtoLegalExoneracao(null);
            c.setFinalVigencia(null);
            c.setProvimentoRetorno(null);
            em.merge(c);
            em.remove(p);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CargoConfianca> recuperaCargoConfiacaProvimento(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("  from CargoConfianca cargoConfianca");
        hql.append(" where cargoConfianca.contratoFP = :contrato");
        hql.append("   and :dataProvimento >= cargoConfianca.inicioVigencia");
        hql.append(" order by cargoConfianca.inicioVigencia");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<CargoConfianca> listaCC = q.getResultList();

        for (CargoConfianca cc : listaCC) {
            cc.getEnquadramentoCCs().size();

            if (dataProvimento.getTime() < cc.getInicioVigencia().getTime()) {
                continue;
            }

            for (EnquadramentoCC ecc : cc.getEnquadramentoCCs()) {
                EnquadramentoPCS enquadramentoPCS = enquadramentoPCSFacade.recuperaUltimoValor(ecc.getCategoriaPCS(), ecc.getProgressaoPCS(), ecc.getInicioVigencia(), ecc.getInicioVigencia());
                if (enquadramentoPCS != null) {
                    ecc.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
                }
            }
        }
        return listaCC;
    }

    public CargoConfianca recuperaCargoConfiancaVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from CargoConfianca cargoConfianca ");
        hql.append(" where cargoConfianca.contratoFP = :contrato ");
        hql.append(" and :dataProvimento between cargoConfianca.inicioVigencia and coalesce(cargoConfianca.finalVigencia, :dataProvimento) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<CargoConfianca> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new CargoConfianca();
        }
        return lista.get(0);
    }

    public boolean existeCargoConfiancaVigente(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(
            "select case when count(cargoConfianca) > 0 then true else false end " +
                "from CargoConfianca cargoConfianca " +
                "where cargoConfianca.contratoFP = :contrato " +
                "and :dataProvimento between cargoConfianca.inicioVigencia and coalesce(cargoConfianca.finalVigencia, :dataProvimento)"
        );
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);
        return (boolean) q.getSingleResult();
    }

    public CargoConfianca recuperaCargoConfiancaVigente(ContratoFP contratoFP) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from CargoConfianca cargoConfianca ");
        hql.append(" where cargoConfianca.contratoFP = :contrato ");
        hql.append(" and :dataAtual between cargoConfianca.inicioVigencia and coalesce(cargoConfianca.finalVigencia, :dataAtual) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataAtual", Util.getDataHoraMinutoSegundoZerado(new Date()));

        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        return (CargoConfianca) resultList.get(0);
    }

    public EnquadramentoCC recuperaEnquadramentoCCVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("select cc from CargoConfianca cargoConfianca inner join cargoConfianca.enquadramentoCCs cc ");
        hql.append(" where cargoConfianca.contratoFP = :contrato ");
        hql.append(" and :dataProvimento >= cargoConfianca.inicioVigencia ");
        hql.append(" and :dataProvimento <= coalesce(cargoConfianca.finalVigencia, :dataProvimento) ");

        hql.append(" and :dataProvimento >= cc.inicioVigencia ");
        hql.append(" and :dataProvimento <= coalesce(cc.finalVigencia, :dataProvimento) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<EnquadramentoCC> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public Integer buscarQuantidadeDeServidoresNomeadosCC(Date inicio) {
        Integer total = 0;
        String hql = "select count(acesso) from CargoConfianca acesso where to_date(to_char(acesso.dataRegistro,'dd/MM/yyyy'),'dd/MM/yyyy') = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public Integer buscarQuantidadeDeServidoresRetornoAcessoCC(Date inicio) {
        Integer total = 0;
        String hql = "select count(distinct r.id) from CargoConfianca_aud r inner join revisaoAuditoria rev on rev.id= r.rev where r.revtype = 1 and r.finalVigencia is not null and to_date(to_char(rev.datahora,'dd/MM/yyyy'),'dd/MM/yyyy') =  :data ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        BigDecimal bg = (BigDecimal) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
