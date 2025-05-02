/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * @Author - Felipe Marinzeck
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.entidadesauxiliares.rh.ParametrosRelatorioConferenciaSefip;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Stateless
public class SefipFacade extends AbstractFacade<Sefip> {

    public static final String VALOR_DESCONTADO_DO_SEGURADO = "VALOR_DESCONTADO_DO_SEGURADO";
    private static final Logger logger = LoggerFactory.getLogger(SefipFacade.class);
    private static final String DESCRICAO_MODULO_EXPORTACAO = "PREVIDÊNCIA";
    private static final String CHAVE_SALARIO_FAMILIA = "#SALARIOFAMILIA"; // OBRIGATORIAMENTE 15 POSIÇÕES
    private static final String CHAVE_SALARIO_MATERNIDADE = "#SALARIOMATERNI"; // OBRIGATORIAMENTE 15 POSIÇÕES
    private List<ParametrosRelatorioConferenciaSefip> relatorioConferenciaSefip;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Sefip selecionado;
    private DependenciasDirf dependenciasDirf;
    private OcorrenciaSEFIP ocorrenciaSefipMultiplosVinculos;

    @EJB
    private RecolhimentoSEFIPFacade recolhimentoSEFIPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private DeclaracaoPrestacaoContasFacade declaracaoPrestacaoContasFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SefipAcompanhamentoFacade sefipAcompanhamentoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;


    public SefipFacade() {
        super(Sefip.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ParametrosRelatorioConferenciaSefip> getRelatorioConferenciaSefip() {
        return relatorioConferenciaSefip;
    }

    @Override
    public Sefip recuperar(Object id) {
        Sefip entidade = super.recuperar(id);
        try {
            entidade.getSefipFolhasDePagamento().size();
        } catch (NullPointerException npe) {
        }
        return entidade;
    }

    public boolean doisOuMaisContratosAtivosNaPrefeitura(ContratoFP cfp) {
        String sql = "  SELECT count(distinct v.id) FROM fichafinanceirafp ff                       " +
            " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id " +
            " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id         " +
            " INNER JOIN matriculafp                        m on m.id = v.matriculafp_id        " +
            " INNER JOIN pessoafisica                      pf on pf.id = m.pessoa_id            " +
            "      WHERE pf.id     = :pessoa_id                                                 " +
            "        AND fp.mes = :mes" +
            "        AND fp.ano = :ano";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pessoa_id", cfp.getMatriculaFP().getPessoa().getId());
        q.setParameter("mes", selecionado.getMes() > 12 ? selecionado.getMes() - 2 : selecionado.getMes() - 1);
        q.setParameter("ano", selecionado.getAno());
        return Integer.parseInt(q.getSingleResult().toString()) >= 2;
    }

    public BigDecimal getTodosValoresDaUnidadePorEvento(Sefip s, HierarquiaOrganizacional ho, SefipNomeReduzido sefipNomeReduzido) {
        String sql = "select coalesce(sum(valor), 0) from (select distinct iff.id, iff.valor as valor from itemfichafinanceirafp iff"
            + "                             inner join fichafinanceirafp          ff on iff.fichafinanceirafp_id = ff.id"
            + "                             inner join folhadepagamento           fp on fp.id                    = ff.folhadepagamento_id"
            + "                             inner join contratofp                cfp on cfp.id = ff.vinculofp_id"
            + "                             inner join vinculofp                   v on v.id = cfp.id"
            + "                             inner join modalidadecontratofp       mc on mc.id = cfp.modalidadecontratofp_id"
            + "                             inner join lotacaofuncional           lf on lf.vinculofp_id          = v.id"
            + "                             inner join sefipfolhadepagamento     sfp on sfp.folhadepagamento_id  = fp.id"
            + "                             inner join VwHierarquiaAdministrativa vw on vw.subordinada_id        = lf.unidadeorganizacional_id"
            + "                             inner join itemgrupoexportacao       ige on ige.eventofp_id = iff.eventofp_id"
            + "                             inner join grupoexportacao            ge on ige.grupoexportacao_id = ge.id"
            + "                             inner join moduloexportacao           me on ge.moduloexportacao_id = me.id"
            + "                                  where sfp.sefip_id = :sefip_id"
            + "                                    and (mc.codigo = :contrato_temporario or mc.codigo = :contrato_cargo_comissao)"
            + "                                    and vw.codigo like :unidade "
            + "                                    and me.descricao = :descricaoModulo"
            + "                                    and ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido"
            + "                                    and (:inicio between trunc(lf.inicioVigencia) and coalesce(trunc(lf.finalVigencia), :fim)"
            + "                                         or :fim between trunc(lf.inicioVigencia) and coalesce(trunc(lf.finalVigencia), :fim)"
            + "                                         or (trunc(lf.inicioVigencia) between :inicio and :fim and trunc(lf.finalVigencia) between :inicio and :fim)))";

        Query q = em.createNativeQuery(sql);

        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", sefipNomeReduzido.name());
        q.setParameter("inicio", DataUtil.getPrimeiroDiaAno(selecionado.getAno()));
        q.setParameter("fim", s.getUltimoDiaDoMes());
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("sefip_id", s);
        q.setParameter("contrato_temporario", Long.parseLong("4"));
        q.setParameter("contrato_cargo_comissao", Long.parseLong("2"));

        return (BigDecimal) q.getSingleResult();
    }


    public BigDecimal getTodosValoresDaUnidadePorEventoAndContrato(Sefip s, VinculoFP v, HierarquiaOrganizacional ho, SefipNomeReduzido sefipNomeReduzido) {
        String sql = "select coalesce(sum(valor), 0) from (select distinct iff.id, iff.valor as valor from itemfichafinanceirafp iff"
            + "                             inner join fichafinanceirafp          ff on iff.fichafinanceirafp_id = ff.id"
            + "                             inner join folhadepagamento           fp on fp.id                    = ff.folhadepagamento_id"
            + "                             inner join contratofp                cfp on cfp.id = ff.vinculofp_id"
            + "                             inner join vinculofp                   v on v.id = cfp.id"
            + "                             inner join modalidadecontratofp       mc on mc.id = cfp.modalidadecontratofp_id"
            + "                             inner join lotacaofuncional           lf on lf.vinculofp_id          = v.id"
            + "                             inner join sefipfolhadepagamento     sfp on sfp.folhadepagamento_id  = fp.id"
            + "                             inner join VwHierarquiaAdministrativa vw on vw.subordinada_id        = lf.unidadeorganizacional_id"
            + "                             inner join itemgrupoexportacao       ige on ige.eventofp_id = iff.eventofp_id"
            + "                             inner join grupoexportacao            ge on ige.grupoexportacao_id = ge.id"
            + "                             inner join moduloexportacao           me on ge.moduloexportacao_id = me.id"
            + "                                  where sfp.sefip_id = :sefip_id"
            + "                                   and v.id = :id "
            + "                                    and (mc.codigo = :contrato_temporario or mc.codigo = :contrato_cargo_comissao)"
            + "                                    and vw.codigo like :unidade "
            + "                                    and me.descricao = :descricaoModulo"
            + "                                    and ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido"
            + "                                    and (:inicio between trunc(lf.inicioVigencia) and coalesce(trunc(lf.finalVigencia), :fim) "
            + "                                         or :fim between trunc(lf.inicioVigencia) and coalesce(trunc(lf.finalVigencia), :fim) "
            + "                                         or (trunc(lf.inicioVigencia) between :inicio and :fim and trunc(lf.finalVigencia) between :inicio and :fim)))";

        Query q = em.createNativeQuery(sql);

        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("nomeReduzido", sefipNomeReduzido.name());
        q.setParameter("inicio", DataUtil.getPrimeiroDiaAno(s.getAno()));
        q.setParameter("fim", s.getUltimoDiaDoMes());
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("sefip_id", s);
        q.setParameter("id", v.getId());
        q.setParameter("contrato_temporario", Long.parseLong("4"));
        q.setParameter("contrato_cargo_comissao", Long.parseLong("2"));

        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getBaseDoEvento(Sefip s, ContratoFP c, String... eventos) {
        String sql = "  SELECT coalesce(sum(iff.valorbasedecalculo),0)   FROM itemfichafinanceirafp iff                               "
            + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
            + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
            + " INNER JOIN sefipfolhadepagamento            sfp ON sfp.folhadepagamento_id = fp.id                           "
            + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
            + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                                  "
            + "      WHERE v.id          = :contratofp_id                                                                    "
            + "        AND v.numero      = :numero_contrato                                                                  "
            + "        AND sfp.sefip_id  = :sefip_id                                                                         "
            + "        AND efp.codigo    in :codigos_eventos";

        Query q = em.createNativeQuery(sql);

        q.setParameter("contratofp_id", c.getId());
        q.setParameter("numero_contrato", c.getNumero());
        q.setParameter("sefip_id", s.getId());
        q.setParameter("codigos_eventos", Arrays.asList(eventos));

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDosEventos(Sefip s, ContratoFP c, SefipNomeReduzido nomeReduzido) {
        String sql = "  SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                                 "
            + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
            + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
            + " INNER JOIN sefipfolhadepagamento            sfp ON sfp.folhadepagamento_id = fp.id                           "
            + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
            + "      WHERE v.id          = :contratofp_id                                                                    "
            + "        AND v.numero      = :numero_contrato                                                                  "
            + "        AND sfp.sefip_id  = :sefip_id                                                                         "
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                                        "
            + "                                   AND ige.operacaoFormula = :adicao                                          "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido))                        "
            + " -                                                                                                            "
            + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                                         "
            + " INNER JOIN fichafinanceirafp         ff ON ff.id = iff.fichafinanceirafp_id                                  "
            + " INNER JOIN folhadepagamento          fp ON fp.id = ff.folhadepagamento_id                                    "
            + " INNER JOIN sefipfolhadepagamento    sfp ON sfp.folhadepagamento_id = fp.id                                   "
            + " INNER JOIN vinculofp                  v ON v.id = ff.vinculofp_id                                            "
            + "      WHERE v.id           = :contratofp_id                                                                   "
            + "        AND v.numero      = :numero_contrato                                                                  "
            + "        AND sfp.sefip_id  = :sefip_id                                                                         "
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                                        "
            + "                                   AND ige.operacaoFormula = :subtracao                                       "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido)) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contratofp_id", c.getId());
        q.setParameter("numero_contrato", c.getNumero());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido.name());
        q.setParameter("sefip_id", s.getId());
        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getSomaDosEventosReferencia(Sefip s, ContratoFP c, SefipNomeReduzido nomeReduzido) {
        String sql = " SELECT (SELECT coalesce(sum(iff.valorreferencia),0)                                                   "
            + " FROM itemfichafinanceirafp iff                                                                               "
            + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
            + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
            + " INNER JOIN sefipfolhadepagamento            sfp ON sfp.folhadepagamento_id = fp.id                           "
            + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
            + "      WHERE v.id          = :contratofp_id                                                                    "
            + "        AND v.numero      = :numero_contrato                                                                  "
            + "        AND sfp.sefip_id  = :sefip_id                                                                         "
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                                        "
            + "                                   AND ige.operacaoFormula = :adicao                                          "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido))                        "
            + " -                                                                                                            "
            + "    (SELECT coalesce(sum(iff.valorreferencia),0)                                                              "
            + " FROM itemfichafinanceirafp iff                                                                               "
            + " INNER JOIN fichafinanceirafp         ff ON ff.id = iff.fichafinanceirafp_id                                  "
            + " INNER JOIN folhadepagamento          fp ON fp.id = ff.folhadepagamento_id                                    "
            + " INNER JOIN sefipfolhadepagamento    sfp ON sfp.folhadepagamento_id = fp.id                                   "
            + " INNER JOIN vinculofp                  v ON v.id = ff.vinculofp_id                                            "
            + "      WHERE v.id           = :contratofp_id                                                                   "
            + "        AND v.numero      = :numero_contrato                                                                  "
            + "        AND sfp.sefip_id  = :sefip_id                                                                         "
            + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                                        "
            + "                                   AND ige.operacaoFormula = :subtracao                                       "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido))                        "
            + " AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("contratofp_id", c.getId());
        q.setParameter("numero_contrato", c.getNumero());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido.name());
        q.setParameter("sefip_id", s.getId());

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public List<ContratoFP> getContratosComInss(Sefip s, HierarquiaOrganizacional hierarquia) {
        String hql = "  select c from ContratoFP c, FichaFinanceiraFP ff"
            + " inner join c.recursosDoVinculoFP rv"
            + " inner join rv.recursoFP rfp"
            + " inner join ff.folhaDePagamento fp"
            + " inner join fp.sefipsFolhaDePagamento sfp"
            + " inner join ff.itemFichaFinanceiraFP iffs"
            + " inner join iffs.eventoFP ev"
            + " where (:inicio between rfp.inicioVigencia and coalesce(rfp.finalVigencia, coalesce(rfp.finalVigencia, :fim))"
            + " or :fim between rfp.inicioVigencia and coalesce(rfp.finalVigencia, coalesce(rfp.finalVigencia, :fim))"
            + " or (rfp.inicioVigencia between :inicio and :fim and rfp.finalVigencia between :inicio and :fim))"

            + " and (:inicio between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + "  or :fim between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + " or (rv.inicioVigencia between :inicio and :fim and rv.finalVigencia between :inicio and :fim))"
            + "        and (ev in( select ige.eventoFP from ItemGrupoExportacao ige "
            + "           inner join ige.grupoExportacao grupoExportacao "
            + "           where grupoExportacao.nomeReduzido = :nomeReduzido or grupoExportacao.nomeReduzido = :nomeReduzido2))"
            + "        and rfp.codigoOrgao = :unidade"
            + "        and ff.vinculoFP = c"
            + "        and sfp.sefip = :sefip ";

        Query q = em.createQuery(hql);
        q.setParameter("nomeReduzido", VALOR_DESCONTADO_DO_SEGURADO);
        q.setParameter("nomeReduzido2", SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO13.name());
        q.setParameter("inicio", s.getPrimeiroDiaDoMes());
        q.setParameter("fim", s.getUltimoDiaDoMes());
        q.setParameter("sefip", s);
        q.setParameter("unidade", Integer.parseInt(hierarquia.getCodigoDo2NivelDeHierarquia()));
        return q.getResultList();
    }

    public List<ContratoFP> getContratosSemInss(Sefip s, HierarquiaOrganizacional hierarquia) {
        String hql = "  select c from ContratoFP c, FichaFinanceiraFP ff"
            + " inner join c.recursosDoVinculoFP rv"
            + " inner join rv.recursoFP rfp"
            + " inner join c.previdenciaVinculoFPs prev"
            + " inner join ff.folhaDePagamento fp"
            + " inner join fp.sefipsFolhaDePagamento sfp"
            + " inner join ff.itemFichaFinanceiraFP iffs"
            + " inner join iffs.eventoFP ev"
            + " where (:inicio between rfp.inicioVigencia and coalesce(rfp.finalVigencia, coalesce(rfp.finalVigencia, :fim))"
            + " or :fim between rfp.inicioVigencia and coalesce(rfp.finalVigencia, coalesce(rfp.finalVigencia, :fim))"
            + " or (rfp.inicioVigencia between :inicio and :fim and rfp.finalVigencia between :inicio and :fim))"
            + "   and (:inicio between prev.inicioVigencia and coalesce(prev.finalVigencia, coalesce(prev.finalVigencia, :fim))"
            + " or :fim between prev.inicioVigencia and coalesce(prev.finalVigencia, coalesce(prev.finalVigencia, :fim))"
            + " or (prev.inicioVigencia between :inicio and :fim and prev.finalVigencia between :inicio and :fim))"
            + "        and prev.tipoPrevidenciaFP.codigo = :cod_isento"
            + "        and ev.codigo != :codigo_ev_inss"
            + "        and ev.codigo != :codigo_ev_rpps"
            + "        and rfp.codigoOrgao = :unidade"
            + "        and c.categoriaSEFIP.codigo = :codigo_categoria"
            + "        and ff.vinculoFP = c"
            + "        and sfp.sefip = :sefip ";

        Query q = em.createQuery(hql);
        q.setParameter("codigo_ev_inss", "900");
        q.setParameter("codigo_ev_rpps", "898");
        q.setParameter("inicio", s.getPrimeiroDiaSeisMesesAntes());
        q.setParameter("fim", s.getUltimoDiaDoMes());
        q.setParameter("sefip", s);
        q.setParameter("unidade", Integer.parseInt(hierarquia.getCodigoDo2NivelDeHierarquia().substring(0, 2)));
        q.setParameter("cod_isento", Long.parseLong("5"));
        q.setParameter("codigo_categoria", Long.parseLong("12"));
        List<ContratoFP> contratos = q.getResultList();

        if (contratos != null) {
            for (ContratoFP c : contratos) {
                c.setOcorrenciaSEFIP(getOcorrenciaSefipDoTipoMultiplosVinculos());
            }
        }
        return contratos;
    }

    public OcorrenciaSEFIP getOcorrenciaSefipDoTipoMultiplosVinculos() {
        if (this.ocorrenciaSefipMultiplosVinculos != null) {
            return this.ocorrenciaSefipMultiplosVinculos;
        }
        String hql = "select o from OcorrenciaSEFIP o where o.codigo = :codigo";
        Query q = em.createQuery(hql, OcorrenciaSEFIP.class);
        q.setMaxResults(1);
        q.setParameter("codigo", Long.parseLong("5"));
        try {
            this.ocorrenciaSefipMultiplosVinculos = (OcorrenciaSEFIP) q.getSingleResult();
            return ocorrenciaSefipMultiplosVinculos;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public CategoriaSEFIP getCategoriaSefipNoMesDoSefip(ContratoFP c) {
        String hql = "select cvc.vinculoDeContratoFP.categoriaSEFIP from ContratoVinculoDeContrato cvc" +
            "  where cvc.contratoFP = :contrato" +
            "  and cvc.inicioVigencia <= :primeiroDiaMes" +
            "  order by cvc.inicioVigencia desc";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", c);
        q.setParameter("primeiroDiaMes", selecionado.getPrimeiroDiaDoMes());
        q.setMaxResults(1);
        try {
            return (CategoriaSEFIP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //tipo 00
    public String headerArquivo() throws Exception {
        String logradouro = "";
        String bairro = "";
        String cep = "";
        String cidade = "";
        String uf = "";
        String telefone = "";
        String dataRecolhimentoFGTSFormatada = "";
        String dataRecolhimentoPrevidenciaFormatada = "";

        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");

        if (selecionado.getDataRecolhimentoFGTS() != null) {
            dataRecolhimentoFGTSFormatada = sf.format(selecionado.getDataRecolhimentoFGTS());
        }
        if (selecionado.getDataRecolhimentoPrevidencia() != null) {
            dataRecolhimentoPrevidenciaFormatada = sf.format(selecionado.getDataRecolhimentoPrevidencia());
        }

        String indiceAtraso = "";
        if (selecionado.getIndiceAtrasoPrevidencia() != null) {
            indiceAtraso = selecionado.getIndiceAtrasoPrevidencia() + "";
            indiceAtraso = StringUtil.removeCaracteresEspeciaisSemEspaco(indiceAtraso);
        }
//        indiceAtraso = ""; // De acordo com layout

        UnidadeGestora ug = unidadeGestoraFacade.getUnidadeGestoraDo(CategoriaDeclaracaoPrestacaoContas.SEFIP);
        selecionado.setResponsavel((PessoaFisica) pessoaFacade.recuperarPF(selecionado.getResponsavel().getId()));
        PessoaJuridica pessoaJuridica = (PessoaJuridica) pessoaFacade.recuperar(ug.getPessoaJuridica().getId());

        try {
            EnderecoCorreio e = pessoaJuridica.getEnderecoPrincipal();
            logradouro = e.getLogradouro().trim() + " " + e.getNumero().trim();
            bairro = e.getBairro();
            cep = e.getCep();
            cidade = e.getLocalidade();
            uf = e.getUf();
        } catch (NullPointerException e) {
            throw new Exception("<font style='color : red'>O <B>LOGRADOURO</B> OU O <B>NÚMERO</B> DO ENDEREÇO DA ENTIDADE NÃO FORAM INFORMADOS CORRETAMENTE. VERIFIQUE AS INFORMAÇÕES NO CADASTRO DE PESSOA JURÍDICA (" + pessoaJuridica + ").</font>");
        }

        for (Telefone t : pessoaJuridica.getTelefones()) {
            telefone = t.getTelefone();
            break;
        }

        String tipoInscricao = "";
        String inscricao = "";

        if (pessoaJuridica.getTipoInscricao() != null) {
            tipoInscricao = pessoaJuridica.getTipoInscricao().getCodigo();
            if (pessoaJuridica.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
                inscricao = pessoaJuridica.getCnpj();
            } else {
                inscricao = pessoaJuridica.getCei();
            }
        }

        SefipRegistroTipo00 sefipRegistroTipo00 = new SefipRegistroTipo00(
            " ",
            "1",
            tipoInscricao.trim(),
            inscricao.trim().replace(".", "").replace("-", "").replace("/", ""),
            pessoaJuridica.getNome(),
            selecionado.getResponsavel().getNome(),
            logradouro,
            bairro,
            cep,
            cidade,
            uf,
            telefone,
            selecionado.getResponsavel().getEmail(),
            (selecionado.getAno().toString() + selecionado.getMesCompetencia()),
            selecionado.getRecolhimentoSEFIP().getCodigo().toString(),
            selecionado.getSefipFGTS().getCodigo(),
            selecionado.getSefipModalidadeArquivo().getCodigo(),
            dataRecolhimentoFGTSFormatada,
            selecionado.getSefipPrevidenciaSocial().getCodigo(),
            dataRecolhimentoPrevidenciaFormatada,
            indiceAtraso,
            tipoInscricao.trim(),
            inscricao.replace(".", "").replace("-", "").replace("/", "").trim(),
            " ");

        return sefipRegistroTipo00.getRegistroTipo00();
    }

    public String registroTipo10() {
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(selecionado.getEntidade().getPessoaJuridica().getId());
        EnderecoCorreio enderecoCorreio = pj.getEnderecoPrincipal();
        //se não possuir nenhum endereço marcado como principal pega o primeiro endereco;
        if (enderecoCorreio == null) {
            for (EnderecoCorreio e : pj.getEnderecos()) {
                enderecoCorreio = e;
                break;
            }
        }

        Telefone telefone = null;
        for (Telefone t : pj.getTelefones()) {
            if (t.getPrincipal()) {
                telefone = t;
                break;
            }
        }
        //se não possuir nenhum telefone marcado como principal pega o primeiro telefone;
        if (telefone == null) {
            for (Telefone t : pj.getTelefones()) {
                telefone = t;
                break;
            }
        }

        String inscricaoDaEmpresa = "";

        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }

        String codigoCentralizacao = "0";
        if (Arrays.asList(130l, 135l, 150l, 155l, 211l, 317l, 337l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            codigoCentralizacao = "0";
        }

        String codigoPagamentoGPS = selecionado.getEntidade().getPagamentoDaGPS().getCodigo().toString();
        if (Arrays.asList(115l, 150l, 211l, 650l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            if (selecionado.getEntidade().getCodigoFpas().compareTo(868) == 0) {
                if (!(codigoPagamentoGPS.equals("1600") && codigoPagamentoGPS.equals("1651"))) {
                    codigoPagamentoGPS = " ";
                }
            }
        }

        String percentualIsencaoFilantropia = "";
        if (selecionado.getEntidade().getCodigoFpas().compareTo(639) == 0) {
            percentualIsencaoFilantropia = "";
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setDecimalSeparatorAlwaysShown(false);

        BigDecimal valorSalarioFamilia = new BigDecimal(BigInteger.ZERO);
        String salarioFamilia = getValorFormatadoParaArquivo(valorSalarioFamilia);
        if (selecionado.getMes() != 13) {
            if (!Arrays.asList(145, 307, 327, 345, 640, 650, 660, 868).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
                salarioFamilia = CHAVE_SALARIO_FAMILIA;
            }
        }

        BigDecimal valorSalarioMaternidade = new BigDecimal(BigInteger.ZERO);
        String salarioMaternidade = getValorFormatadoParaArquivo(valorSalarioMaternidade);
        if (selecionado.getMes() != 13) {
            if (!Arrays.asList(130, 135, 145, 211, 307, 317, 327, 337, 345, 640, 650, 660, 868).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
                salarioMaternidade = CHAVE_SALARIO_MATERNIDADE;
            }
        }

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String numeroTelefone = "";

        if (telefone != null) {
            numeroTelefone = telefone.getTelefone();
        }

        String logradouro = "";
        String bairro = "";
        String cep = "";
        String localidade = "";
        String uf = "";

        if (enderecoCorreio != null) {
            if (enderecoCorreio.getLogradouro() != null) {
                logradouro += enderecoCorreio.getLogradouro().trim();
            }

            if (enderecoCorreio.getNumero() != null) {
                logradouro += " " + enderecoCorreio.getNumero().trim();
            }

            if (enderecoCorreio.getBairro() != null) {
                bairro += enderecoCorreio.getBairro();
            }

            if (enderecoCorreio.getCep() != null) {
                cep += enderecoCorreio.getCep();
            }

            if (enderecoCorreio.getLocalidade() != null) {
                localidade += enderecoCorreio.getLocalidade();
            }

            if (enderecoCorreio.getUf() != null) {
                uf += enderecoCorreio.getUf();
            }
        }

        String codigoCnae = "";

        if (selecionado.getEntidade().getCnae() != null) {
            codigoCnae = selecionado.getEntidade().getCnae().getCodigoCnae();
        }

        String aliquotaRat = "";

        if (selecionado.getEntidade().getAliquotaRAT() != null) {
            aliquotaRat = getValorFormatadoParaArquivo(selecionado.getEntidade().getAliquotaRAT());
        }

        String codigoSimples = "";
        if (selecionado.getEntidade().getSimples() != null) {

            if (selecionado.getRecolhimentoSEFIP().getCodigo().compareTo(640l) != 0) {
                codigoSimples = selecionado.getEntidade().getSimples().getCodigo();
                if ((Arrays.asList("3", "4", "5", "6").contains(codigoSimples)) && (selecionado.getAno().intValue() >= 2007) && (selecionado.getMes() != 01)) {
                    codigoSimples = "";
                }
            }
        }

        String codigoFpas = "";
        if (selecionado.getEntidade().getCodigoFpas() != null) {
            codigoFpas = selecionado.getEntidade().getCodigoFpas().toString();
        }

        String codigoOutrasEntidades = "";
//        if (selecionado.getEntidade().getCodigoOutrasEntidades() != null) {
        if (!Arrays.asList(145l, 307l, 317l, 327l, 337l, 345l, 640l, 660l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {

            if (codigoFpas.equals("582") || codigoFpas.equals("868")) {
                codigoOutrasEntidades = "0000";
            } else {
//                    codigoOutrasEntidades = selecionado.getEntidade().getCodigoOutrasEntidades().toString();
                codigoOutrasEntidades = "0079";
            }
        }
//        }

        String ordenacao = "";
        String razao = pj.getRazaoSocial().replace("-", "");
        razao = StringUtil.removeEspacos(razao);

        SefipRegistroTipo10 sefipRegistroTipo10 = new SefipRegistroTipo10(ordenacao,
            tipoInscricaoPJ,
            inscricaoDaEmpresa,
            "0",
            razao,
            logradouro,
            bairro,
            cep,
            localidade,
            uf,
            numeroTelefone,
            "N",
            codigoCnae,
            "N",
            aliquotaRat,
            codigoCentralizacao,
            codigoSimples,
            codigoFpas,
            codigoOutrasEntidades,
            codigoPagamentoGPS,
            " ",
            salarioFamilia,
            salarioMaternidade,
            "0",
            "0",
            "0",
            " ",
            " ",
            " ",
            "0",
            " ");

        return sefipRegistroTipo10.getRegistroTipo10();
    }

    private List<HierarquiaOrganizacional> buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado() {
        return entidadeFacade.buscarHierarquiasDaEntidade(selecionado.getEntidade(), CategoriaDeclaracaoPrestacaoContas.SEFIP, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
    }

    public String registroTipo12() {
        Entidade entidade = selecionado.getEntidade();
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(entidade.getPessoaJuridica().getId());

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String inscricaoDaEmpresa = "";

        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setDecimalSeparatorAlwaysShown(false);

        String compensacao = "0";

        if (selecionado.getValorCompensacao() != null) {
            compensacao = df.format(selecionado.getValorCompensacao());
        }

        String inicioCompensacao = "";

        if (selecionado.getAnoInicioCompensacao() != null && selecionado.getMesInicioCompensacao() != null) {
            inicioCompensacao = selecionado.getAnoInicioCompensacao() + MesCalendarioPagamento.getMesToInt(selecionado.getMesInicioCompensacao()).getNumeroMesString();
        }

        String finalCompensacao = "";

        if (selecionado.getAnoFimCompensacao() != null && selecionado.getMesFimCompensacao() != null) {
            finalCompensacao = selecionado.getAnoFimCompensacao() + MesCalendarioPagamento.getMesToInt(selecionado.getMesFimCompensacao()).getNumeroMesString();
        }

        String ordenacao = "";

        SefipRegistroTipo12 sefipRegistroTipo12 = new SefipRegistroTipo12(ordenacao,
            tipoInscricaoPJ,
            inscricaoDaEmpresa,
            "0",
            "0",
            "0",
            "",
            "0",
            "0",
            "",
            "",
            "",
            "",
            "",
            compensacao,
            inicioCompensacao,
            finalCompensacao,
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            null,
            "0",
            "");


        return sefipRegistroTipo12.getRegistroTipo12();
    }

    public String verificaNull(String parte) {
        if (parte != null) {
            return parte;
        }
        return "";
    }

    public String registroTipo14() {
        Entidade entidade = selecionado.getEntidade();
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(entidade.getPessoaJuridica().getId());

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String inscricaoDaEmpresa = "";

        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }


        String registrosTipo14 = "";
        List<String> listaRegistroTipo14 = new ArrayList<>();

        HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(selecionado.getDataOperacao(), selecionado.getEntidade().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);

        for (ContratoFP contrato : contratoFPFacade.recuperaServidoresVigentesNaCompetenciaMatriculadosNaEntidade(ho,
            selecionado.getMes(),
            selecionado.getAno())) {

            PessoaFisica pf = contrato.getMatriculaFP().getPessoa();
            pf = (PessoaFisica) pessoaFacade.recuperarPF(pf.getId());
            CarteiraTrabalho carteiraTrabalho = null;

            CategoriaSEFIP categoriaDaGeracaoSefip = getCategoriaSefipNoMesDoSefip(contrato);
            contrato.setCategoriaSEFIP(categoriaDaGeracaoSefip != null ? categoriaDaGeracaoSefip : contrato.getCategoriaSEFIP());

            //Categorias Permitidas - 01,02,03,04,05,06 e 07
            if (!Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
                continue;
            }

            String numeroCTPS = "";
            String serieCTPS = "";

            for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
                if (doc instanceof CarteiraTrabalho) {
                    CarteiraTrabalho c = (CarteiraTrabalho) doc;

                    if (carteiraTrabalho == null) {
                        carteiraTrabalho = c;
                    } else {
                        if (c.getDataEmissao() != null) {
                            Calendar calendarMesAno = Calendar.getInstance();
                            calendarMesAno.setTime(c.getDataEmissao());

                            if ((c.getDataEmissao().compareTo(carteiraTrabalho.getDataEmissao()) > 0)
                                && ((calendarMesAno.get(Calendar.MONTH) <= selecionado.getMes())
                                && ((calendarMesAno.get(Calendar.YEAR) <= selecionado.getAno().intValue())))) {
                                carteiraTrabalho = c;
                            }
                        }
                    }
                }
            }

            String pisPasepCI = "";

            if (carteiraTrabalho != null) {
                pisPasepCI = carteiraTrabalho.getPisPasep();
            }

            SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");

            String dataAdmissao = "";
            String categoria = "";

            if (contrato.getCategoriaSEFIP() != null) {
                if (Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
                    categoria = "0" + contrato.getCategoriaSEFIP().getCodigo().toString();
                }
                if (Arrays.asList(1l, 3l, 4l, 5l, 6l, 7l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
                    if (contrato.getDataAdmissao() != null) {
                        dataAdmissao = sf.format(contrato.getDataAdmissao());
                    }
                }
            }

            if (carteiraTrabalho != null) {
                if (Arrays.asList("01", "03", "04", "06", "07").contains(categoria)) {
                    if (carteiraTrabalho.getNumero() != null) {
                        numeroCTPS = carteiraTrabalho.getNumero().trim();

                        if (!numeroCTPS.equals("")) {
                            numeroCTPS = StringUtil.cortaOuCompletaEsquerda(numeroCTPS, 7, "0");
                        }
                    }


                    if (carteiraTrabalho.getSerie() != null) {
                        serieCTPS = carteiraTrabalho.getSerie().trim();
                        if (!serieCTPS.equals("")) {
                            serieCTPS = StringUtil.cortaOuCompletaEsquerda(serieCTPS, 5, "0");
                        }
                    }
                }
            }

            String logradouro = "";
            String bairro = "";
            String cep = "";
            String cidade = "";
            String uf = "";
            EnderecoCorreio endereco = null;

            endereco = pf.getEnderecoPrincipal();

//            if (pf.getEnderecos() != null) {
//                for (EnderecoCorreio e : pf.getEnderecos()) {
//                    if (e.getPrincipal()) {
//                        endereco = e;
//                        break;
//                    }
//                }

            if (endereco == null) {
                if (pf.getEnderecos() != null) {
                    for (EnderecoCorreio e : pf.getEnderecos()) {
                        endereco = e;
                        break;
                    }
                }
            }

            if (endereco != null) {
                if (endereco.getLogradouro() != null) {
                    logradouro = endereco.getLogradouro().trim() + ",";
                }

                if (endereco.getNumero() != null) {
                    logradouro += endereco.getNumero().trim() + ",";
                }

                logradouro = StringUtil.removeCaracteresEspeciais(logradouro);
                logradouro = StringUtil.removeEspacos(logradouro);

                bairro = endereco.getBairro();
                cep = endereco.getCep();
                cidade = endereco.getLocalidade();
                uf = endereco.getUf();
            }

            Calendar calendarDataAdmissao = Calendar.getInstance();
            calendarDataAdmissao.setTime(contrato.getDataAdmissao());

            String anoAdmissao = calendarDataAdmissao.get(Calendar.YEAR) + "";
            String mesAdmissao = calendarDataAdmissao.get(Calendar.MONTH) + "";
            String diaAdmissao = calendarDataAdmissao.get(Calendar.DAY_OF_MONTH) + "";

            if (mesAdmissao.length() == 1) {
                mesAdmissao = "0" + mesAdmissao;
            }

            if (diaAdmissao.length() == 1) {
                diaAdmissao = "0" + diaAdmissao;
            }

            String ordenacao = tipoInscricaoPJ + inscricaoDaEmpresa + pisPasepCI + anoAdmissao + mesAdmissao + diaAdmissao + StringUtil.cortaOuCompletaEsquerda(categoria, 2, "0");

            SefipRegistroTipo14 sefipRegistroTipo14 = new SefipRegistroTipo14(ordenacao,
                verificaNull(tipoInscricaoPJ),
                verificaNull(inscricaoDaEmpresa),
                "0",
                verificaNull(pisPasepCI),
                verificaNull(dataAdmissao),
                verificaNull(categoria),
                StringUtil.removeEspacos(verificaNull(pf.getNome())),
                verificaNull(numeroCTPS),
                verificaNull(serieCTPS),
                verificaNull(logradouro),
                verificaNull(bairro),
                verificaNull(cep),
                verificaNull(cidade),
                verificaNull(uf),
                "");

            listaRegistroTipo14.add(sefipRegistroTipo14.getRegistroTipo14());
//            }

        }

        Collections.sort(listaRegistroTipo14);

        for (String s : listaRegistroTipo14) {
            registrosTipo14 += s.substring(s.length() - 362);
        }


        return registrosTipo14;
    }

    public String registroTipo20() {
        Entidade entidade = selecionado.getEntidade();
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(entidade.getPessoaJuridica().getId());

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String inscricaoDaEmpresa = "";

        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }


        String logradouro = "";
        String bairro = "";
        String cep = "";
        String cidade = "";
        String uf = "";

        EnderecoCorreio endereco = pj.getEnderecoPrincipal();

//        for (EnderecoCorreio e : pj.getEnderecos()) {
//            if (e.getPrincipal()) {
//                endereco = e;
//                break;
//            }
//        }

        if (endereco == null) {
            for (EnderecoCorreio e : pj.getEnderecos()) {
                endereco = e;
                break;
            }
        }

        if (endereco != null) {
            logradouro = endereco.getLogradouro() + ", " + endereco.getNumero();
            bairro = endereco.getBairro();
            cep = endereco.getCep();
            cidade = endereco.getLocalidade();
            uf = endereco.getUf();
        }


        String codigoPagamentoGPS = "";

        if (Arrays.asList(130l, 135l, 155l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            if (entidade.getPagamentoDaGPS() != null) {
                codigoPagamentoGPS = entidade.getPagamentoDaGPS().getCodigo().toString();
            }
        }

        DecimalFormat df = new DecimalFormat("#,##0.00");
        df.setDecimalSeparatorAlwaysShown(false);

        String salarioFamilia = "0";
        BigDecimal valorSalarioFamilia = new BigDecimal(BigInteger.ZERO);
        if (selecionado.getMes() != 13) {
            if (Arrays.asList(150l, 155l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
                if (entidade.getCodigoFpas().compareTo(868) == 0) {
                    GrupoExportacao grupoExportacaoSalarioFamilia = grupoExportacaoFacade.recuperaGrupoExportacaoPorNomeReduzido(SefipNomeReduzido.SALARIO_FAMILIA);

                    if (grupoExportacaoSalarioFamilia != null) {
                        for (ItemGrupoExportacao i : grupoExportacaoSalarioFamilia.getItensGruposExportacoes()) {
                            BigDecimal valorRecuperado = folhaDePagamentoFacade.recuperaValorTotalDoEventoNaFolhaPorUnidade(MesCalendarioPagamento.getMesToInt(selecionado.getMes()), selecionado.getAno(), selecionado.getEntidade().getUnidadeOrganizacional(), i.getEventoFP());
                            if (i.getOperacaoFormula() == OperacaoFormula.ADICAO) {
                                valorSalarioFamilia = valorSalarioFamilia.add(valorRecuperado);
                            } else if (i.getOperacaoFormula() == OperacaoFormula.SUBTRACAO) {
                                valorSalarioFamilia = valorSalarioFamilia.subtract(valorRecuperado);
                            }
                        }
                        salarioFamilia = df.format(valorSalarioFamilia);
                    }
                }
            }
        }

        String ordenacao = "";

        SefipRegistroTipo20 sefipRegistroTipo20 = new SefipRegistroTipo20(ordenacao,
            verificaNull(tipoInscricaoPJ),
            verificaNull(inscricaoDaEmpresa),
            verificaNull(tipoInscricaoPJ),
            verificaNull(inscricaoDaEmpresa),
            verificaNull("0"),
            verificaNull(pj.getRazaoSocial()),
            verificaNull(logradouro),
            verificaNull(bairro),
            verificaNull(cep),
            verificaNull(cidade),
            verificaNull(uf),
            verificaNull(codigoPagamentoGPS),
            verificaNull(salarioFamilia),
            verificaNull("0"),
            verificaNull("0"),
            verificaNull("0"),
            verificaNull("0"),
            verificaNull("0"),
            verificaNull("0"),
            " ");


        return sefipRegistroTipo20.getRegistroTipo20();
    }

    public SefipRegistroTipo30 registroTipo30(ContratoFP contrato, Date dataOperacao) {
        Entidade entidade = selecionado.getEntidade();
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(entidade.getPessoaJuridica().getId());

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String inscricaoDaEmpresa = "";


        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "").replace("/", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }


        String tipoInscricaoTomador = "";
        String inscricaoTomador = "";

        if (Arrays.asList(130l, 135l, 211l, 150l, 155l, 317l, 337l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            tipoInscricaoTomador = tipoInscricaoPJ;
            inscricaoTomador = inscricaoDaEmpresa;
        }


        String registrosTipo30 = "";
        PessoaFisica pf = contrato.getMatriculaFP().getPessoa();

        pf = (PessoaFisica) pessoaFacade.recuperarPF(pf.getId());
        CarteiraTrabalho carteiraTrabalho = null;

        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
        String dataAdmissao = "";
        String categoria = "";

        if (contrato.getCategoriaSEFIP() != null) {
            categoria = contrato.getCategoriaSEFIP().getCodigo().toString();
            if (Arrays.asList(1l, 3l, 4l, 5l, 6l, 7l, 11l, 12l, 19l, 20l, 21l, 26l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
                if (contrato.getDataAdmissao() != null) {
                    dataAdmissao = sf.format(contrato.getDataAdmissao());
                }
            }
        }

        String numeroCTPS = "";
        String serieCTPS = "";

        for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
            if (doc instanceof CarteiraTrabalho) {
                CarteiraTrabalho c = (CarteiraTrabalho) doc;


                if (carteiraTrabalho == null) {
                    carteiraTrabalho = c;
                } else {
                    if (c.getDataEmissao() != null) {
                        Calendar calendarMesAno = Calendar.getInstance();
                        calendarMesAno.setTime(c.getDataEmissao());

                        if (c.getDataEmissao() != null && carteiraTrabalho.getDataEmissao() != null && (c.getDataEmissao().compareTo(carteiraTrabalho.getDataEmissao()) > 0)
                            && ((calendarMesAno.get(Calendar.MONTH) <= selecionado.getMes())
                            && ((calendarMesAno.get(Calendar.YEAR) <= selecionado.getAno().intValue())))) {
                            carteiraTrabalho = c;
                        }
                    }
                }
            }
        }

        if (carteiraTrabalho != null) {
            if (Arrays.asList("1", "3", "4", "6", "7", "26").contains(categoria)) {
                if (carteiraTrabalho.getNumero() != null) {
                    numeroCTPS = carteiraTrabalho.getNumero().trim();

                    if (!numeroCTPS.equals("")) {
                        numeroCTPS = StringUtil.cortaOuCompletaEsquerda(numeroCTPS, 7, "0");
                    }
                }

                if (carteiraTrabalho.getSerie() != null) {
                    serieCTPS = carteiraTrabalho.getSerie().trim();
                    serieCTPS = StringUtil.retornaApenasNumeros(serieCTPS);
                    if (serieCTPS.length() > 5) {
                        serieCTPS = serieCTPS.substring(serieCTPS.length() - 5, serieCTPS.length());
                    }
                    if (!serieCTPS.equals("")) {
                        serieCTPS = StringUtil.cortaOuCompletaEsquerda(serieCTPS, 5, "0");
                    }
                }
            }
        }

        String pisPasepCI = "";

        if (carteiraTrabalho != null) {
            pisPasepCI = carteiraTrabalho.getPisPasep();
        }

        String matricula = "";

        if (!Arrays.asList("6", "13", "14", "15", "16", "17", "18", "22", "23", "24", "25").contains(categoria)) {
            if (contrato.getMatriculaFP().getMatricula() != null) {
                matricula = contrato.getMatriculaFP().getMatricula().trim();

                if (!matricula.equals("")) {
                    matricula = StringUtil.cortaOuCompletaEsquerda(matricula, 11, "0");
                }
            }
        }

        String dataOpcaoFGTS = "";

        if (Arrays.asList("1", "3", "4", "5", "6", "7").contains(categoria)) {
            if (contrato.getDataOpcaoFGTS() != null) {
                dataOpcaoFGTS = sf.format(contrato.getDataOpcaoFGTS());
            }
            if (dataOpcaoFGTS.trim().length() <= 0) {
                dataOpcaoFGTS = sf.format(contrato.getDataAdmissao());
            }
        }

        String dataNascimento = "";

        if (Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 12l, 19l, 20l, 21l, 26l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
            if (pf.getDataNascimento() != null) {
                dataNascimento = sf.format(pf.getDataNascimento());
            }
        }

        String cbo = "";

        if (contrato.getCbo() != null) {
            cbo = contrato.getCbo().getCodigo().toString().trim();

            if (!cbo.equals("")) {

                if (cbo.length() >= 4) {
                    cbo = cbo.substring(0, 4);
                }

                cbo = StringUtil.cortaOuCompletaEsquerda(cbo, 5, "0");
            }
        }

        String remuneracaoSem13 = "0";
        BigDecimal valorRemuneracaoSem13 = BigDecimal.ZERO;
        if (selecionado.getMes() != 13) {
            if (doisOuMaisContratosAtivosNaPrefeitura(contrato)) {
                contrato.setOcorrenciaSEFIP(getOcorrenciaSefipDoTipoMultiplosVinculos());
                valorRemuneracaoSem13 = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_SEM_13_SALARIO);
            } else {
                valorRemuneracaoSem13 = getBaseDoEvento(selecionado, contrato, getEventosPorIdentificacao(IdentificacaoEventoFP.INSS));
                if (valorRemuneracaoSem13.compareTo(BigDecimal.ZERO) == 0 && contrato.isMultiploVinculo()) {
                    valorRemuneracaoSem13 = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_SEM_13_SALARIO);
                }
            }
            remuneracaoSem13 = getValorFormatadoParaArquivo(valorRemuneracaoSem13);
        }

        String remuneracao13 = "0";
        if (selecionado.getMes() != 13) {
            if (Arrays.asList("1", "2", "3", "4", "6", "7", "12", "19", "20", "21", "26").contains(categoria)) {
                BigDecimal valorRemuneracao13 = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_13);
                remuneracao13 = getValorFormatadoParaArquivo(valorRemuneracao13);
            }
        }

        BigDecimal valorDescontadoSegurado = BigDecimal.ZERO;
        if (Arrays.asList(5l, 6l, 7l, 8l).contains(contrato.getOcorrenciaSEFIP().getCodigo()) ||
            Arrays.asList(135l, 135l, 650l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            valorDescontadoSegurado = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO);
        }

        if (isDecimo13(dataOperacao)) {
            if (doisOuMaisContratosAtivosNaPrefeitura(contrato)) {
                contrato.setOcorrenciaSEFIP(getOcorrenciaSefipDoTipoMultiplosVinculos());
                valorDescontadoSegurado = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO);
            }
        }

        String descontadoSegurado = getValorFormatadoParaArquivo(valorDescontadoSegurado);

        String remuneracaoBaseCalculo = "0";
        BigDecimal valorRemuneracaoBaseCalculo = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_BASE_CALCULO_CONTRIBUICAO);
        remuneracaoBaseCalculo = getValorFormatadoParaArquivo(valorRemuneracaoBaseCalculo);

        String baseCalculo13Movimento = "0";
        if (isDecimo13(dataOperacao)) {
            BigDecimal valorBaseCalculo13Movimento = BigDecimal.ZERO;
            if (contrato.isMultiploVinculo()) {
                valorBaseCalculo13Movimento = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_SEM_13_SALARIO);
            } else {
                valorBaseCalculo13Movimento = getBaseDoEvento(selecionado, contrato, "899");
            }
            baseCalculo13Movimento = getValorFormatadoParaArquivo(valorBaseCalculo13Movimento);
        }

        String baseCalculo13GPS = "0";
        BigDecimal valorBaseCalculo13GPS = getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.BASE_CALCULO_13_SALARIO_PREVIDENCIA_REF_GPS_COMPETENCIA);
        baseCalculo13GPS = getValorFormatadoParaArquivo(valorBaseCalculo13GPS);

        Calendar calendarDataAdmissao = Calendar.getInstance();
        calendarDataAdmissao.setTime(contrato.getDataAdmissao());

        String anoAdmissao = calendarDataAdmissao.get(Calendar.YEAR) + "";
        String mesAdmissao = calendarDataAdmissao.get(Calendar.MONTH) + "";
        String diaAdmissao = calendarDataAdmissao.get(Calendar.DAY_OF_MONTH) + "";

        if (mesAdmissao.length() == 1) {
            mesAdmissao = "0" + mesAdmissao;
        }

        if (diaAdmissao.length() == 1) {
            diaAdmissao = "0" + diaAdmissao;
        }

        String ocorrencia = "";

        if (contrato.getOcorrenciaSEFIP() != null) {
            if (contrato.getOcorrenciaSEFIP().getCodigo() == 5) {
                ocorrencia = contrato.getOcorrenciaSEFIP().getCodigo().toString().trim();

                if (!ocorrencia.equals("")) {
                    ocorrencia = StringUtil.cortaOuCompletaEsquerda(ocorrencia, 2, "0");
                }
            } else if (!Arrays.asList("1", "3", "4", "5", "6", "7", "11", "12", "13", "15", "17", "18", "19", "20", "21", "24", "25").contains(categoria)) {
                ocorrencia = contrato.getOcorrenciaSEFIP().getCodigo().toString().trim();

                if (!ocorrencia.equals("")) {
                    ocorrencia = StringUtil.cortaOuCompletaEsquerda(ocorrencia, 2, "0");
                }
            }
        }

        String ordenacao = tipoInscricaoPJ + inscricaoDaEmpresa + tipoInscricaoTomador + inscricaoTomador + pisPasepCI + anoAdmissao + mesAdmissao + diaAdmissao + StringUtil.cortaOuCompletaEsquerda(categoria, 2, "0");

        String nome = pf.getNome();
        nome = StringUtil.removeCaracteresEspeciais(nome);
        nome = StringUtil.removeEspacos(nome);

        SefipRegistroTipo30 sefipRegistroTipo30 = new SefipRegistroTipo30(ordenacao,
            verificaNull(tipoInscricaoPJ),
            verificaNull(inscricaoDaEmpresa),
            verificaNull(tipoInscricaoTomador),
            verificaNull(inscricaoTomador),
            verificaNull(pisPasepCI),
            verificaNull(dataAdmissao),
            verificaNull(categoria),
            verificaNull(nome),
            verificaNull(matricula),
            verificaNull(numeroCTPS),
            verificaNull(serieCTPS),
            verificaNull(dataOpcaoFGTS),
            verificaNull(dataNascimento),
            verificaNull(cbo),
            verificaNull(remuneracaoSem13),
            verificaNull(remuneracao13),
            verificaNull(""),
            verificaNull(ocorrencia),
            verificaNull(descontadoSegurado),
            verificaNull(remuneracaoBaseCalculo),
            verificaNull(baseCalculo13Movimento),
            verificaNull(baseCalculo13GPS),
            " ");
        montarRelatorioConferenciaSefip(contrato);
        return sefipRegistroTipo30;
    }

    private BigDecimal getValorCalculadoQuandoTemAfastamento120Dias(BigDecimal valorRemuneracao, Afastamento afastamento) {
        if (afastamento.getInicio() != null) {
            Date dataAfastamento = DataUtil.dataSemHorario(afastamento.getInicio());
            Integer mesAfastamento = DataUtil.getMes(dataAfastamento);
            if (mesAfastamento.equals(selecionado.getMes())) {
                return getCalculoValorRemuneracaoLicencaMaternidade(valorRemuneracao, dataAfastamento, true);
            }
        }
        if (afastamento.getTermino() != null) {
            Date dataAfastamento = DataUtil.dataSemHorario(afastamento.getTermino());
            Integer mesAfastamento = DataUtil.getMes(dataAfastamento);
            if (mesAfastamento.equals(selecionado.getMes())) {
                return getCalculoValorRemuneracaoLicencaMaternidade(valorRemuneracao, dataAfastamento, false);
            }
        }
        return valorRemuneracao;
    }

    private BigDecimal getCalculoValorRemuneracaoLicencaMaternidade(BigDecimal valorRemuneracao, Date dataAfastamento, Boolean isDataInicial) {
        Integer mesSefip = selecionado.getMes();
        Integer anoSefip = selecionado.getAno();
        Date dataGeracaoSefip;
        if (isDataInicial) {
            dataGeracaoSefip = DataUtil.dataSemHorario(Util.criaDataUltimoDiaDoMesFP(mesSefip - 1, anoSefip));
        } else {
            dataGeracaoSefip = DataUtil.dataSemHorario(Util.criaDataPrimeiroDiaMesJoda(mesSefip, anoSefip).toDate());
        }
        BigDecimal diasMesAfastamento = new BigDecimal(DataUtil.getDiasNoMes(dataAfastamento));
        BigDecimal valorDiario = valorRemuneracao.divide(diasMesAfastamento, 8, RoundingMode.HALF_UP);
        BigDecimal diferencaDiasMes;
        if (isDataInicial) {
            diferencaDiasMes = new BigDecimal(DataUtil.diasEntreDate(dataAfastamento, dataGeracaoSefip));
        } else {
            diferencaDiasMes = new BigDecimal(DataUtil.diasEntreDate(dataGeracaoSefip, dataAfastamento));
        }
        if (valorDiario.compareTo(BigDecimal.ZERO) > 0 && diferencaDiasMes.compareTo(BigDecimal.ZERO) > 0) {
            return valorDiario.multiply(diferencaDiasMes);
        }
        return valorRemuneracao;
    }


    private String[] getEventosPorIdentificacao(IdentificacaoEventoFP identificacaoEventoFP) {
        String sql = " select * from eventofp where IdentificacaoEventoFP = :identificacao";
        Query q = em.createNativeQuery(sql, EventoFP.class);
        q.setParameter("identificacao", identificacaoEventoFP.name());
        if (!q.getResultList().isEmpty()) {
            List<String> codigos = Lists.newArrayList();
            for (Object o : q.getResultList()) {
                codigos.add(((EventoFP) o).getCodigo());
            }
            return codigos.toArray(new String[codigos.size()]);
        }
        return null;
    }

    private String getValorFormatadoParaArquivo(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        String r = nf.format(valor);
        r = StringUtil.retornaApenasNumeros(r);
        return r;
    }

    public String registroTipo32(ContratoFP contrato, String codigoMovimentacao, String dataMovimentacao, String
        indicativoRecolhimentoFGTS, Date dataOperacao) {
        Entidade entidade = selecionado.getEntidade();
        PessoaJuridica pj = (PessoaJuridica) pessoaFacade.recuperarPJ(entidade.getPessoaJuridica().getId());

        String tipoInscricaoPJ = "";

        if (pj.getTipoInscricao() != null) {
            tipoInscricaoPJ = pj.getTipoInscricao().getCodigo();
        }

        String inscricaoDaEmpresa = "";


        if (pj.getTipoInscricao() == TipoInscricao.CNPJ) {
            inscricaoDaEmpresa = pj.getCnpj().replace(".", "").replace("-", "").replace("/", "");
        } else if (pj.getTipoInscricao() == TipoInscricao.CEI) {
            inscricaoDaEmpresa = pj.getCei();
        }

        String tipoInscricaoTomador = "";
        String inscricaoTomador = "";

        if (Arrays.asList(130l, 135l, 211l, 150l, 155l, 317l, 337l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            tipoInscricaoTomador = tipoInscricaoPJ;
            inscricaoTomador = inscricaoDaEmpresa;
        }


        PessoaFisica pf = contrato.getMatriculaFP().getPessoa();

        pf = (PessoaFisica) pessoaFacade.recuperarPF(pf.getId());
        CarteiraTrabalho carteiraTrabalho = null;

        for (DocumentoPessoal doc : pf.getDocumentosPessoais()) {
            if (doc instanceof CarteiraTrabalho) {
                carteiraTrabalho = (CarteiraTrabalho) doc;
                break;
            }
        }

        String pisPasepCI = "";
        if (carteiraTrabalho != null) {
            pisPasepCI = carteiraTrabalho.getPisPasep();
        }

        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
        String dataAdmissao = "";
        String categoria = "";

        if (contrato.getCategoriaSEFIP() != null) {
            //if (Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
            categoria = contrato.getCategoriaSEFIP().getCodigo().toString();

            if (Arrays.asList(1l, 3l, 4l, 5l, 6l, 7l, 11l, 12l, 19l, 20l, 21l, 26l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
                if (contrato.getDataAdmissao() != null) {
                    dataAdmissao = sf.format(contrato.getDataAdmissao());
                }
            }
        }

        Calendar calendarDataAdmissao = Calendar.getInstance();
        calendarDataAdmissao.setTime(contrato.getDataAdmissao());

        String anoAdmissao = calendarDataAdmissao.get(Calendar.YEAR) + "";
        String mesAdmissao = calendarDataAdmissao.get(Calendar.MONTH) + "";
        String diaAdmissao = calendarDataAdmissao.get(Calendar.DAY_OF_MONTH) + "";

        if (mesAdmissao.length() == 1) {
            mesAdmissao = "0" + mesAdmissao;
        }

        if (diaAdmissao.length() == 1) {
            diaAdmissao = "0" + diaAdmissao;
        }

        if (!Arrays.asList("1", "3", "4", "5", "6", "7").contains(categoria)) {
            indicativoRecolhimentoFGTS = "N";
        }

        if ((!Arrays.asList("I1", "I2", "I3", "I4", "L").contains(codigoMovimentacao))
            || (isDecimo13(dataOperacao))) {
            indicativoRecolhimentoFGTS = "";
        }

        String nome = pf.getNome();
        nome = StringUtil.removeCaracteresEspeciais(nome);
        nome = StringUtil.removeEspacos(nome);

        String ordenacao = tipoInscricaoPJ + inscricaoDaEmpresa + tipoInscricaoTomador + inscricaoTomador + pisPasepCI + anoAdmissao + mesAdmissao + diaAdmissao + StringUtil.cortaOuCompletaEsquerda(categoria, 2, "0");
        SefipRegistroTipo32 sefipRegistroTipo32 = new SefipRegistroTipo32(ordenacao,
            verificaNull(tipoInscricaoPJ),
            verificaNull(inscricaoDaEmpresa),
            verificaNull(tipoInscricaoTomador),
            verificaNull(inscricaoTomador),
            verificaNull(pisPasepCI),
            verificaNull(dataAdmissao),
            verificaNull(categoria),
            verificaNull(nome),
            verificaNull(codigoMovimentacao),
            verificaNull(dataMovimentacao),
            verificaNull(indicativoRecolhimentoFGTS),
            " ");

        return sefipRegistroTipo32.getRegistroTipo32();
    }

    private boolean isDecimo13(Date dataOperacao) {
        ItemEntidadeDPContas item = getItemEntidadeDPContas(selecionado.getEntidade(), dataOperacao);
        return selecionado.getMes() == 13
            || (item != null && item.getGerarSefip13Dezembro() && hasFolha13());
    }

    private boolean hasFolha13() {
        for (SefipFolhaDePagamento sefipFolhaDePagamento : selecionado.getSefipFolhasDePagamento()) {
            if (TipoFolhaDePagamento.SALARIO_13.equals(sefipFolhaDePagamento.getFolhaDePagamento().getTipoFolhaDePagamento())) {
                return true;
            }
        }
        return false;
    }

    //tipo 90
    public String totalizadorArquivo() {
        return new SefipRegistroTipo90().getRegistroTipo90();
    }

    private void carregarTotalDeRegistros() {
        this.dependenciasDirf.setTotalCadastros(1);
    }

    private void apagarSefipExistente() {
        try {
            sefipAcompanhamentoFacade.jaExisteSefipGeradoMesmosParametros(selecionado);
        } catch (RuntimeException re) {
            sefipAcompanhamentoFacade.deleteSefip(selecionado);
        }
    }

    private void salvarNovoSefip() {
        if (selecionado.getId() == null) {
            sefipAcompanhamentoFacade.salvarNovo(selecionado);
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void gerarSefip(Sefip sefip, DependenciasDirf dd, Date dataOperacao, UsuarioSistema usuarioLogado) {
        relatorioConferenciaSefip = new ArrayList<>();
        selecionado = sefip;
        apagarSefipExistente();
        salvarNovoSefip();

        dependenciasDirf = dd;

        addLog("PREPARANDO ARQUIVO PARA GERAÇÃO DOS DADOS...");
        carregarTotalDeRegistros();
        String linhaArquivo = "";

        try {
            addLog("GERANDO HEADER (REGISTRO TIPO 00) DO ARQUIVO...");
            linhaArquivo = headerArquivo();
        } catch (Exception e) {
            e.printStackTrace();
            addLog("NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES.<br />DETALHES DO ERRO: " + e.getMessage());
            this.dependenciasDirf.pararProcessamento();
            return;
        }

        List<ContratoFP> contratoFPs = new ArrayList<>();

        List<HierarquiaOrganizacional> hos = buscarUnidadesParaDeclaracaoDoEstabelecimentoSelecionado();

        addLog("POR FAVOR AGUARDE, O SISTEMA ESTÁ RECUPERANDO OS SERVIDORES PARA GERAÇÃO DO ARQUIVO, ESTE PROCESSO PODE LEVAR ALGUNS INSTANTES...");
        for (HierarquiaOrganizacional ho : hos) {
            contratoFPs.addAll(getContratosComInss(selecionado, ho));
            contratoFPs.addAll(getContratosSemInss(selecionado, ho));
        }

        contratoFPs = new ArrayList(new HashSet(contratoFPs));

        addLog("GERANDO HEADER (REGISTRO TIPO 10) DO ARQUIVO...");
        linhaArquivo += registroTipo10();
        this.dependenciasDirf.setTotalCadastros(contratoFPs.size());

        //Obrigatório para os códigos de recolhimento 650 e 660
        if (Arrays.asList(650l, 660l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            linhaArquivo += registroTipo12();

        }

        if (selecionado.getAtualizaEnderecos() != null && selecionado.getAtualizaEnderecos()) {
            if (selecionado.getMes() != 13) {
                linhaArquivo += registroTipo14();
            }
        }

        //Obrigatório para os códigos de recolhimento:130,135,150,155,211,317,337,608
        if (Arrays.asList(130l, 135l, 150l, 155l, 211l, 317l, 337l, 608l).contains(selecionado.getRecolhimentoSEFIP().getCodigo())) {
            linhaArquivo += registroTipo20();
        }

        List<String> listaRegistros3032 = new ArrayList<>();
        //Registros tipo 30 e 32
        for (ContratoFP contrato : contratoFPs) {
            addLog(this.dependenciasDirf.getContadorProcessos() + 1 + " - GERANDO REGISTROS DE: " + contrato.toString().toUpperCase(), "<b>", "</b>");
            if (selecionado.getRecolhimentoSEFIP().getCodigo().compareTo(211l) == 0) {
                if (contrato.getCategoriaSEFIP() != null) {
                    if (!Arrays.asList(17l, 18l, 24l, 25l).contains(contrato.getCategoriaSEFIP().getCodigo())) {

                    }
                }
            }

            CategoriaSEFIP categoriaDaGeracaoSefip = getCategoriaSefipNoMesDoSefip(contrato);
            contrato.setCategoriaSEFIP(categoriaDaGeracaoSefip != null ? categoriaDaGeracaoSefip : contrato.getCategoriaSEFIP());

            SefipRegistroTipo30 reg30 = registroTipo30(contrato, dataOperacao);
            if (!reg30.isSalarioPositivo()) {
//                continue;
            }

            listaRegistros3032.add(reg30.getRegistroTipo30());
            int indice = listaRegistros3032.size() - 1;

            //linhaArquivo += registroTipo30(contrato);

            //Registro Tipo 32 não pode ser informado na competencia 13
//            if (selecionado.getMes() == 13) {
//                continue;
//            }

            //permitido o registro tipo 32 apenas para as categorias de trabalhador
            //01,02,03,04,05,06,,07,11,12,19,20,21 e 26
            if (contrato.getCategoriaSEFIP() != null) {
                if (!Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 7l, 11l, 12l, 19l, 20l, 21l, 26l).contains(contrato.getCategoriaSEFIP().getCodigo())) {
//                    continue;
                }
            }
            List<Afastamento> afastamentosFinal = Lists.newArrayList();
            if (selecionado.getMes() != 13) {
                afastamentosFinal = afastamentoFacade.servidorAfastadoEmPeriodoSEFIP(contrato, selecionado.getPrimeiroDiaDoMes(), selecionado.getUltimoDiaDoMes());
                for (Afastamento a : afastamentosFinal) {
                    listaRegistros3032.add(adicionarInicioDoAfastamento(contrato, a, dataOperacao));
                    if (a.getTermino() != null && a.getTermino().compareTo(selecionado.getPrimeiroDiaDoMes()) >= 0 && a.getTermino().compareTo(selecionado.getUltimoDiaDoMes()) <= 0) {
                        listaRegistros3032.add(adicionarFimDoAfastamento(contrato, a, dataOperacao));
                    }
                }

                SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
                String dataMovimento = "";
                String codigoMovimento = "";

                //rescisões
                for (ExoneracaoRescisao e : exoneracaoRescisaoFacade.listaExoneracaoRescisaoCompetenciaSEFIP(contrato, selecionado.getMes(), selecionado.getAno())) {
                    if (e.getDataRescisao() == null) {
                        dataMovimento = "";
                    } else {
                        dataMovimento = sf.format(e.getDataRescisao());
                    }

                    codigoMovimento = "";
                    if (e.getMotivoExoneracaoRescisao() != null) {
                        if (e.getMotivoExoneracaoRescisao().getMotivoDesligamentoFGTS() != null) {
                            codigoMovimento = e.getMotivoExoneracaoRescisao().getMotivoDesligamentoFGTS().getCodigo();
                        }
                    }

                    // Se o servidor trabalhou mais de 15 dias no ano em questão, deve ser informado um valor mínimo para movimento referente a base de cálculo
                    // caso o servidor trabalhou menos que 15 dias, não deve ser informado tal valor
                    if (DataUtil.diferencaDiasInteira(DataUtil.getPrimeiroDiaAno(selecionado.getAno()), e.getDataRescisao()) >= 15) {
                        BigDecimal valorRemuneracao13 = getBaseDoEvento(selecionado, contrato, "899");  //899 - I.N.S.S. 13S
                        String remuneracao13 = getValorFormatadoParaArquivo(valorRemuneracao13);
                        reg30.setRemuneracao13(remuneracao13);

                        BigDecimal valorBaseCalculo13Movimento = getBaseDoEvento(selecionado, contrato, "899");  //899 - I.N.S.S. 13S
                        String valorBaseCalculo13 = getValorFormatadoParaArquivo(valorBaseCalculo13Movimento);
                        reg30.setBaseDeCalculo13ReferenteCompetenciaMovimento(valorBaseCalculo13);

                        if (valorBaseCalculo13Movimento.compareTo(BigDecimal.ZERO) == 0 && !isExoneracaoDefinitiva(codigoMovimento)) {
                            String valor = getValorFormatadoParaArquivo(new BigDecimal("0.01"));
                            reg30.setBaseDeCalculo13ReferenteCompetenciaMovimento(valor);
                        }

                        String novaLinha = reg30.getRegistroTipo30();
                        listaRegistros3032.set(indice, novaLinha);
                    }

                    String indicativoRecolhimentoFGTS = "";

                    if (e.getRecolherFGTSGRRF() != null) {
                        if (e.getRecolherFGTSGRRF()) {
                            indicativoRecolhimentoFGTS = "S";
                        } else {
                            indicativoRecolhimentoFGTS = "N";
                        }
                    } else {
                        indicativoRecolhimentoFGTS = "N";
                    }

                    listaRegistros3032.add(registroTipo32(contrato, codigoMovimento, dataMovimento, indicativoRecolhimentoFGTS, dataOperacao));
                }
            }

            addLog("&nbsp;&bull; GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
            this.dependenciasDirf.incrementarContador();
        }

        this.dependenciasDirf.decrementarContador();

        addLog("&nbsp;&bull; CALCULANDO SALÁRIO FAMÍLIA E SALÁRIO MATERNIDADE...", "<b>", "</b>");

        BigDecimal valorSalarioFamilia = BigDecimal.ZERO;
        BigDecimal valorSalarioMaternidade = BigDecimal.ZERO;

        for (HierarquiaOrganizacional ho : hos) {
            valorSalarioFamilia = valorSalarioFamilia.add(getTodosValoresDaUnidadePorEvento(selecionado, ho, SefipNomeReduzido.SALARIO_FAMILIA));
            valorSalarioMaternidade = valorSalarioMaternidade.add(getTodosValoresDaUnidadePorEvento(selecionado, ho, SefipNomeReduzido.SALARIO_MATERNIDADE));
        }

        if (linhaArquivo.contains(CHAVE_SALARIO_FAMILIA)) {
            String salFam = getValorFormatadoParaArquivo(valorSalarioFamilia);
            salFam = StringUtil.cortaOuCompletaEsquerda(salFam, 15, "0");
            linhaArquivo = linhaArquivo.replace(CHAVE_SALARIO_FAMILIA, salFam);
        }

        if (linhaArquivo.contains(CHAVE_SALARIO_MATERNIDADE)) {
            String salMat = getValorFormatadoParaArquivo(valorSalarioMaternidade);
            salMat = StringUtil.cortaOuCompletaEsquerda(salMat, 15, "0");
            linhaArquivo = linhaArquivo.replace(CHAVE_SALARIO_MATERNIDADE, salMat);
        }

        Collections.sort(listaRegistros3032);

        for ( String s : listaRegistros3032) {
            linhaArquivo += s.substring(s.length() - 362);
        }

        try {
            linhaArquivo += totalizadorArquivo();

            addLog("&nbsp;&bull; CRIANDO ARQUIVO PARA DOWNLOAD, ISTO PODE LEVAR ALGUNS INSTANTES, POR FAVOR AGUARDE...", "<font style='color : blue;'><i>", "</i></font>");
            selecionado = gerarEntidadeArquivo(linhaArquivo, selecionado);
            addLog("&nbsp;&bull; ARQUIVO GERADO COM SUCESSO", "<font style='color : green;'><i>", "</i></font>");
            this.dependenciasDirf.incrementarContador();
            this.dependenciasDirf.setContadorProcessos(Integer.parseInt("" + this.dependenciasDirf.getTotalCadastros()));
        } catch (Exception e) {
            addLog("NÃO FOI POSSÍVEL GERAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e);
            logger.error("Erro ao tentar gerar arquivo SEFIP. ", e);
            this.dependenciasDirf.pararProcessamento();
        }

        try {
            addLog("SALVANDO ARQUIVO GERADO...");
            addLog("ARQUIVO GERADO E SALVO COM SUCESSO");
            gerarArquivoDoRelatorio(sefipAcompanhamentoFacade.gerarRelatoriArrayBytes(relatorioConferenciaSefip, selecionado, usuarioLogado));
            selecionado = (Sefip) sefipAcompanhamentoFacade.salvar(selecionado);
        } catch (Exception e) {
            addLog("NÃO FOI POSSÍVEL SALVAR O ARQUIVO. ENTRE EM CONTATO COM O SUPORTE PARA MAIORES INFORMAÇÕES. DETALHES DO ERRO: " + e);
            logger.error("Falha ao gerar aquivo SEFIP. ", e);
        }

        this.dependenciasDirf.pararProcessamento();
    }

    public void gerarArquivoDoRelatorio(byte[] bytes) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao("RELATORIO DE CONFERÊNCIA DO SEFIP");
        arquivo.setNome("relatorioConferenciaSefip.pdf");
        arquivo.setMimeType("application/pdf");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        arquivo.setTamanho(Long.valueOf(byteArrayInputStream.available()));
        selecionado.setRelatorioDeConferencia(arquivoFacade.novoArquivoMemoria(arquivo, byteArrayInputStream));
    }

    private String adicionarInicioDoAfastamento(ContratoFP contrato, Afastamento a, Date dataOperacao) {
        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
        String dataMovimento = "";
        String codigoMovimento = "";

        if (a.getInicio() == null) {
            dataMovimento = "";
        } else {
            Date inicio = a.getInicio();
            if (Util.getDataHoraMinutoSegundoZerado(inicio).compareTo(Util.getDataHoraMinutoSegundoZerado(contrato.getDataAdmissao())) != 0) {
                inicio = DataUtil.adicionaDias(inicio, -1);
            }
            dataMovimento = sf.format(inicio);
        }

        codigoMovimento = "";
        if (a.getTipoAfastamento() != null) {
            if (a.getTipoAfastamento().getMovimentoSEFIPAfastamento() != null) {
                codigoMovimento = a.getTipoAfastamento().getMovimentoSEFIPAfastamento().getCodigo();
            }
        }

        return registroTipo32(contrato, codigoMovimento, dataMovimento, "", dataOperacao);
    }

    private String adicionarFimDoAfastamento(ContratoFP contrato, Afastamento a, Date dataOperacao) {
        SimpleDateFormat sf = new SimpleDateFormat("ddMMyyyy");
        String dataMovimento = "";
        String codigoMovimento = "";
        if (a.getTermino() == null) {
            dataMovimento = "";
        } else {
            Date termino = a.getTermino();
            dataMovimento = sf.format(termino);
        }

        codigoMovimento = "";
        if (a.getTipoAfastamento() != null) {
            if (a.getTipoAfastamento().getMovimentoSEFIPRetorno() != null) {
                codigoMovimento = a.getTipoAfastamento().getMovimentoSEFIPRetorno().getCodigo();
            }
        }

        return registroTipo32(contrato, codigoMovimento, dataMovimento, "", dataOperacao);
    }

    private boolean isAfastamentoDefinitivo(Afastamento a) {
        return a.getTipoAfastamento().getMovimentoSEFIPAfastamento().getCodigo() == "J" ||
            a.getTipoAfastamento().getMovimentoSEFIPAfastamento().getCodigo().equals("S2");
    }

    private boolean isExoneracaoDefinitiva(String codigo) {
        return codigo.equals("H") ? true : false;
    }

    private void addLog(String valor, String abre, String fecha) {
        addLog(abre + valor + fecha);
    }

    private void addLog(String valor) {
        try {
            String agora = Util.dateHourToString(new Date());
            this.dependenciasDirf.getLog().add(agora + " - " + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (this.dependenciasDirf == null) {
                this.dependenciasDirf = new DependenciasDirf();
            }
            this.dependenciasDirf.setLog(new ArrayList<String>());
            addLog(valor);
        }
    }

    private Sefip gerarEntidadeArquivo(String conteudo, Sefip s) {
        InputStream is = new ByteArrayInputStream(conteudo.getBytes());
        Arquivo a = new Arquivo();
        a.setNome("SEFIP.RE");
        a.setMimeType("text/plain");
        a.setInputStream(is);
        try {
            arquivoFacade.novoArquivo(a, a.getInputStream());
        } catch (Exception ex) {
            logger.error("Erro:", ex);
        }

        s.setArquivo(a);
        s.setProcessadoEm(new Date());
        return s;
    }

    @Override
    public void remover(Sefip entity) {
        removerArquivoSefip(entity);
        super.remover(entity);
    }

    public void removerArquivoSefip(Sefip s) {
        if (s.getArquivo() != null) {
            removerRelacionamentoSefipArquivo(s);
            arquivoFacade.removerArquivo(s.getArquivo());
        }
    }

    private void removerRelacionamentoSefipArquivo(Sefip d) {
        try {
            String sql = "UPDATE Dirf SET arquivo_id = null WHERE id = :param";
            Query q = em.createNativeQuery(sql);
            q.setParameter("param", d.getId());
            q.executeUpdate();
        } catch (Exception e) {
        }
    }

    public List<Sefip> buscarSefipPorMesAndAno(Mes mes, Exercicio exercicio) throws NullPointerException {
        return em.createQuery(" from Sefip sp where sp.mes = :mes and sp.ano = :ano")
            .setParameter("mes", mes.getNumeroMes())
            .setParameter("ano", exercicio.getAno()).getResultList();
    }

    public ItemEntidadeDPContas getItemEntidadeDPContas(Entidade entidade, Date dataOperacao) {
        String sql = " select item.* from entidadedpcontas dp " +
            " inner join ItemEntidadeDPContas item on item.ENTIDADEDPCONTAS_ID = dp.id " +
            " inner join DECLARACAOPRESTACAOCONTAS declaracao on dp.DECLARACAOPRESTACAOCONTAS_ID = declaracao.id " +
            " where :dataAtual between trunc(dp.INICIOVIGENCIA) and coalesce(trunc(dp.FINALVIGENCIA), :dataAtual) " +
            " and item.entidade_id = :entidade and declaracao.CATEGORIADECLARACAO = :tipoCategoriaDeclaracao";
        Query q = em.createNativeQuery(sql, ItemEntidadeDPContas.class);
        q.setParameter("dataAtual", dataOperacao);
        q.setParameter("tipoCategoriaDeclaracao", CategoriaDeclaracaoPrestacaoContas.SEFIP.name());
        q.setParameter("entidade", entidade.getId());
        if (!q.getResultList().isEmpty()) {
            return (ItemEntidadeDPContas) q.getResultList().get(0);
        }
        return null;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public SefipAcompanhamentoFacade getSefipAcompanhamentoFacade() {
        return sefipAcompanhamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public RecolhimentoSEFIPFacade getRecolhimentoSEFIPFacade() {
        return recolhimentoSEFIPFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public DeclaracaoPrestacaoContasFacade getDeclaracaoPrestacaoContasFacade() {
        return declaracaoPrestacaoContasFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public List<ParametrosRelatorioConferenciaSefip> montarRelatorioConferenciaSefip (ContratoFP contrato) {


        String sql = "select " +
            "                MATRICULA.MATRICULA as matricula, " +
            "                vinculo.NUMERO as contrato, " +
            "                pf.NOME as servidor, " +
            "                ctps.PISPASEP as pispasep " +
            "from VINCULOFP vinculo " +
            "inner join CONTRATOFP contrato on vinculo.ID = contrato.ID " +
            "inner join matriculafp matricula on vinculo.MATRICULAFP_ID = matricula.ID " +
            "inner join PESSOAFISICA pf on matricula.PESSOA_ID = pf.ID " +
            "    inner join DOCUMENTOPESSOAL docpes on pf.ID = docpes.PESSOAFISICA_ID " +
            "inner join CARTEIRATRABALHO ctps on docpes.ID = ctps.ID where vinculo.id = :contratoId";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoId", contrato.getId());
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                ParametrosRelatorioConferenciaSefip relatorio = new ParametrosRelatorioConferenciaSefip();
                relatorio.setMatricula((String) obj[0]);
                relatorio.setContrato((String) obj[1]);
                relatorio.setServidor((String) obj[2]);
                relatorio.setPisPasep((String) obj[3]);

                relatorio.setRat(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.RAT));
                relatorio.setRat13(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.RAT13));

                relatorio.setBase(getSomaDosEventosRelatorio(selecionado, contrato, SefipNomeReduzido.REMUNERACAO_SEM_13_SALARIO));
                relatorio.setBase13(getSomaDosEventosRelatorio(selecionado, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO13));
                relatorio.setInss(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO));
                relatorio.setInss13(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_DESCONTADO_DO_SEGURADO13));
                relatorio.setValorPatronal(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_PATRONAL));
                relatorio.setValorPatronal13(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.VALOR_PATRONAL13));
                relatorio.setFgts13(getSomaDosEventos(selecionado, contrato, SefipNomeReduzido.FGTS13));
                relatorio.setFgts(buscarFGTS(selecionado, contrato.getId()));
                relatorioConferenciaSefip.add(relatorio);
            }
        }
        return relatorioConferenciaSefip;
    }

    public BigDecimal getSomaDosEventosRelatorio(Sefip s, ContratoFP c, SefipNomeReduzido nomeReduzido) {
        return getSomaDosEventosRelatorio(s, c, nomeReduzido, null);
    }


    public BigDecimal getSomaDosEventosRelatorio(Sefip s, ContratoFP c, SefipNomeReduzido nomeReduzido, TipoFolhaDePagamento tipo) {
        StringBuilder sql = new StringBuilder();
        sql.append("        SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                                 ");
        sql.append("        INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          ");
        sql.append("        INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            ");
        sql.append(tipo == null ? " INNER JOIN sefipfolhadepagamento            sfp ON sfp.folhadepagamento_id = fp.id         " : " ");
        sql.append("        INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    ");
        sql.append(" WHERE v.id          = :contratofp_id                                                                    ");
        sql.append("        AND v.numero      = :numero_contrato                                                                  ");
        sql.append(tipo == null ? "   AND sfp.sefip_id  = :sefip_id                                                  " : " ");
        sql.append(tipo != null ? "   AND fp.mes = :mes and fp.ano = :ano and   fp.tipoFolhaDePagamento = :tipo               " : "");
        sql.append("        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           ");
        sql.append("                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   ");
        sql.append("                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  ");
        sql.append("                                 WHERE me.descricao = :descricaoModulo                                ");
        sql.append("                                   AND ige.operacaoFormula = :adicao                                          ");
        sql.append("                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido))                        ");
        sql.append(" -                                                                                                            ");
        sql.append("    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                                         ");
        sql.append(" INNER JOIN fichafinanceirafp         ff ON ff.id = iff.fichafinanceirafp_id                                  ");
        sql.append(" INNER JOIN folhadepagamento          fp ON fp.id = ff.folhadepagamento_id                                    ");
        if (tipo == null) {
            sql.append(" INNER JOIN sefipfolhadepagamento            sfp ON sfp.folhadepagamento_id = fp.id                           ");
        }
        sql.append(" INNER JOIN vinculofp                  v ON v.id = ff.vinculofp_id                                            ");
        sql.append("      WHERE v.id           = :contratofp_id                                                                   ");
        sql.append("        AND v.numero      = :numero_contrato                                                                  ");
        if (tipo == null) {
            sql.append("       AND sfp.sefip_id  = :sefip_id                                                                         ");
        } else {
            sql.append("   AND fp.mes = :mes and fp.ano = :ano and   fp.tipoFolhaDePagamento = :tipo                               ");
        }
        sql.append("        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           ");
        sql.append("                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   ");
        sql.append("                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  ");
        sql.append("                                 WHERE me.descricao = :descricaoModulo                             ");
        sql.append("                                   AND ige.operacaoFormula = :subtracao                                       ");
        sql.append("                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido)) AS resultado FROM dual ");

        Query q = em.createNativeQuery(sql.toString());

        q.setParameter("contratofp_id", c.getId());
        q.setParameter("numero_contrato", c.getNumero());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido.name());
        if (tipo == null) {
            q.setParameter("sefip_id", s.getId());
        } else {
            q.setParameter("mes", Mes.getMesToInt(s.getMes() > 12 ? 12 : s.getMes()).getNumeroMesIniciandoEmZero());
            q.setParameter("ano", s.getAno());
            q.setParameter("tipo", tipo.name());
        }
        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();

            if (resultado == null) {
                return BigDecimal.ZERO.add(getSomaDosEventosFichaRPA(s, c, nomeReduzido));
            }
            return resultado.add(getSomaDosEventosFichaRPA(s, c, nomeReduzido));
        } catch (
            NoResultException nre) {
            return BigDecimal.ZERO;
        }

    }

    public BigDecimal getSomaDosEventosFichaRPA(Sefip s, ContratoFP c, SefipNomeReduzido nomeReduzido) {

        String sql = "SELECT (SELECT coalesce(sum(itemFicha.valor),0)  "
            + " FROM itemficharpa itemFicha "
            + "       INNER JOIN ficharpa ficha ON ficha.id = itemFicha.ficharpa_id "
            + "       INNER JOIN eventofp evento ON evento.id = itemFicha.eventofp_id "
            + " where ficha.prestadorservicos_id = :id "
            + "  and extract(month from ficha.geradoEm) = :mes "
            + "  and extract(year from ficha.geradoEm) = :ano "
            + "        AND itemFicha.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                     "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                             "
            + "                                   AND ige.operacaoFormula = :adicao                                          "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido))                        "
            + " -                                                                                                            "
            + "    (SELECT coalesce(sum(itemFicha.valor),0)  "
            + "             FROM itemficharpa itemFicha  "
            + "                   INNER JOIN ficharpa ficha ON ficha.id = itemFicha.ficharpa_id  "
            + "                   INNER JOIN eventofp evento ON evento.id = itemFicha.eventofp_id  "
            + "             where ficha.prestadorservicos_id = :id  "
            + "              and extract(month from ficha.geradoEm) = :mes  "
            + "              and extract(year from ficha.geradoEm) = :ano  "
            + "                   AND itemFicha.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige                           "
            + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id                   "
            + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id                  "
            + "                                 WHERE me.descricao = :descricaoModulo                             "
            + "                                   AND ige.operacaoFormula = :subtracao                                       "
            + "                                   AND ltrim(rtrim(ge.nomereduzido)) = :nomeReduzido)) AS resultado FROM dual ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("id", c.getId());
        q.setParameter("mes", s.getMes());
        q.setParameter("ano", s.getAno());
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        q.setParameter("nomeReduzido", nomeReduzido.name());


        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal buscarFGTS(Sefip s, Long contratoId) {

        String sql = "select coalesce(sum(item.VALOR),0)  " +
            "from ITEMFICHAFINANCEIRAFP item " +
            "         inner join eventofp evento on item.EVENTOFP_ID = evento.ID " +
            "         inner join FICHAFINANCEIRAFP ficha on item.FICHAFINANCEIRAFP_ID = ficha.ID " +
            "         inner join vinculofp vinculo on ficha.VINCULOFP_ID = vinculo.ID " +
            "         inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            "where evento.DESCRICAOREDUZIDA = :nomeReduzido and folha.mes = :mes and folha.ano = :ano and vinculo.id = :contratoId  ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("mes", s.getMes());
        q.setParameter("ano", s.getAno());
        q.setParameter("contratoId", contratoId);
        q.setParameter("nomeReduzido", SefipNomeReduzido.FGTS.name());

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }

    }

}
