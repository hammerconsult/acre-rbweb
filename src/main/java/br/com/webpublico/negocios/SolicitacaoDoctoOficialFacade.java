/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.entidadesauxiliares.ValidacaoCertidaoDoctoOficial;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.viewobjects.DataTableSolicitacaoDoctoOficial;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author claudio
 */
@Stateless
public class SolicitacaoDoctoOficialFacade extends AbstractFacade<SolicitacaoDoctoOficial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private ConfiguracaoAcrescimosFacade configuracaoAcrescimosFacade;
    private List<ResultadoParcela> listaDebitos;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private FinalidadeFacade finalidadeFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private SingletonGeradorCodigoTributario singletonGeradorCodigoTributario;

    public SolicitacaoDoctoOficialFacade() {
        super(SolicitacaoDoctoOficial.class);
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public ConfiguracaoAcrescimosFacade getConfiguracaoAcrescimosFacade() {
        return configuracaoAcrescimosFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public FinalidadeFacade getFinalidadeFacade() {
        return finalidadeFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoDoctoOficial recuperar(Object id) {
        SolicitacaoDoctoOficial sdo = em.find(SolicitacaoDoctoOficial.class, id);
        sdo.getValoresAtributosSolicitacao().size();
        sdo.getImpressaoDoctoOficial().size();
        return sdo;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "SELECT * FROM SolicitacaoDoctoOficial WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql, SolicitacaoDoctoOficial.class);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM SolicitacaoDoctoOficial");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public List<ValorAtributoSolicitacao> recuperaAtributosPorSolicitacao(SolicitacaoDoctoOficial solicitacao) {
        String hql = "from ValorAtributoSolicitacao vas where vas.solicitacaoDoctoOficial = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);

        return q.getResultList();
    }

    public boolean verificaSeTemDocumentoGerado(DocumentoOficial documentoOficial) {
        String hql = "from SolicitacaoDoctoOficial  where documentoOficial = :doc";
        Query q = em.createQuery(hql);
        q.setParameter("doc", documentoOficial.getId());
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            return true;
        }
        return false;
    }

    public String recuperarNomeCadastroEconomico(SolicitacaoDoctoOficial solicitacao) {
        String sql = "SELECT coalesce(pf.nome, pj.razaosocial) || ' ' || coalesce(pf.cpf, pj.cnpj) AS nome FROM solicitacaodoctooficial sol "
            + "INNER JOIN cadastroeconomico ce ON ce.id = sol.cadastroeconomico_id "
            + "LEFT JOIN pessoafisica pf ON pf.id = ce.pessoa_id "
            + "LEFT JOIN pessoajuridica pj ON pj.id = ce.pessoa_id "
            + "WHERE sol.id = :solicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperarNomeCadastroImobiliario(SolicitacaoDoctoOficial solicitacao) {
        String sql = "SELECT ci.inscricaocadastral || ' Lote: ' || lo.codigolote || ' Quadra: ' || q.descricao || ' Setor: ' || s.nome AS nome FROM solicitacaodoctooficial sol "
            + "INNER JOIN cadastroimobiliario ci ON ci.id = sol.cadastroimobiliario_id "
            + "LEFT JOIN lote lo ON lo.id = ci.lote_id "
            + "LEFT JOIN quadra q ON q.id = lo.quadra_id "
            + "LEFT JOIN setor s ON s.id = lo.setor_id "
            + "WHERE sol.id = :solicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperarNomeCadastroRural(SolicitacaoDoctoOficial solicitacao) {
        String sql = "SELECT cr.nomepropriedade AS nome FROM solicitacaodoctooficial sol "
            + "INNER JOIN cadastrorural cr ON cr.id = sol.cadastrorural_id "
            + "WHERE sol.id = :solicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperarNomePessoaFisica(SolicitacaoDoctoOficial solicitacao) {
        String sql = "SELECT pf.nome || ' ' || pf.cpf AS nome FROM solicitacaodoctooficial sol "
            + "INNER JOIN pessoafisica pf ON pf.id = sol.pessoafisica_id "
            + "WHERE sol.id = :solicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperarNomePessoaJuridica(SolicitacaoDoctoOficial solicitacao) {
        String sql = "SELECT pj.razaosocial || ' ' || pj.cnpj AS nome FROM solicitacaodoctooficial sol "
            + "INNER JOIN pessoajuridica pj ON pj.id = sol.pessoajuridica_id "
            + "WHERE sol.id = :solicitacao";
        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public CadastroImobiliario recuperarCadastroImobiliario(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select ci from SolicitacaoDoctoOficial sol "
            + "inner join sol.cadastroImobiliario ci "
            + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CadastroImobiliario ci = (CadastroImobiliario) q.getResultList().get(0);
            ci.getPropriedade().size();
            return ci;
        }
        return null;
    }

    public CadastroEconomico recuperarCadastroEconomico(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select ce from SolicitacaoDoctoOficial sol "
            + "inner join sol.cadastroEconomico ce "
            + "where sol.id = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CadastroEconomico ce = (CadastroEconomico) q.getResultList().get(0);
            ce.getSituacaoCadastroEconomico().size();
            return ce;
        }
        return null;
    }

    public CadastroRural recuperarCadastroRural(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select cr from SolicitacaoDoctoOficial sol "
            + "inner join sol.cadastroRural cr "
            + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            CadastroRural cr = (CadastroRural) q.getResultList().get(0);
            cr.getPropriedade().size();
            return cr;
        }
        return null;
    }

    public PessoaFisica recuperarPessoaFisica(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select pf from SolicitacaoDoctoOficial sol "
            + "inner join sol.pessoaFisica pf "
            + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (PessoaFisica) q.getResultList().get(0);
        }
        return null;
    }

    public PessoaJuridica recuperarPessoaJuridica(SolicitacaoDoctoOficial solicitacao) {
        String hql = "select pj from SolicitacaoDoctoOficial sol "
            + "inner join sol.pessoaJuridica pj "
            + "where sol = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacao);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (PessoaJuridica) q.getResultList().get(0);
        }
        return null;
    }

    public ValidacaoCertidaoDoctoOficial documentoValido(SolicitacaoDoctoOficial solicitacaoDoctoOficial) throws UFMException {
        return permitirSolicitarDocumento(solicitacaoDoctoOficial);
    }

    public List<CalculoPessoa> geraPessoasCalculo(SolicitacaoDoctoOficial solicitacaoDoctoOficial, CalculoDoctoOficial calculo) {
        List<CalculoPessoa> pessoas = new ArrayList<>();

        if (solicitacaoDoctoOficial.getCadastroImobiliario() != null) {
            CadastroImobiliario cad = em.find(CadastroImobiliario.class, solicitacaoDoctoOficial.getCadastroImobiliario().getId());
            for (Propriedade prop : cad.getPropriedadeVigente()) {
                CalculoPessoa calculoPessoa = new CalculoPessoa();
                calculoPessoa.setCalculo(calculo);
                calculoPessoa.setPessoa(prop.getPessoa());
                pessoas.add(calculoPessoa);
            }
        } else if (solicitacaoDoctoOficial.getCadastroEconomico() != null) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculo);
            calculoPessoa.setPessoa(((CadastroEconomico) solicitacaoDoctoOficial.getCadastroEconomico()).getPessoa());
            pessoas.add(calculoPessoa);
        } else if (solicitacaoDoctoOficial.getCadastroRural() != null) {
            CadastroRural cad = em.find(CadastroRural.class, solicitacaoDoctoOficial.getCadastroRural().getId());
            for (PropriedadeRural prop : cad.getPropriedade()) {
                CalculoPessoa calculoPessoa = new CalculoPessoa();
                calculoPessoa.setCalculo(calculo);
                calculoPessoa.setPessoa(prop.getPessoa());
                pessoas.add(calculoPessoa);
            }
        } else if (solicitacaoDoctoOficial.getPessoaFisica() != null) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculo);
            calculoPessoa.setPessoa(solicitacaoDoctoOficial.getPessoaFisica());
            pessoas.add(calculoPessoa);
        } else if (solicitacaoDoctoOficial.getPessoaJuridica() != null) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setCalculo(calculo);
            calculoPessoa.setPessoa(solicitacaoDoctoOficial.getPessoaJuridica());
            pessoas.add(calculoPessoa);
        }
        return pessoas;
    }

    @Override
    public List<SolicitacaoDoctoOficial> lista() {
        return em.createQuery("select s from SolicitacaoDoctoOficial s ").getResultList();
    }

    @Override
    public List<SolicitacaoDoctoOficial> listaFiltrandoX(String s, int inicio, int max, Field... atributos) {
        String hql = "select s from SolicitacaoDoctoOficial s "
            + " left join s.cadastroImobiliario bci"
            + " left join s.cadastroEconomico cmc"
            + " left join s.cadastroRural bcr"
            + " left join s.pessoaFisica pf"
            + " left join s.pessoaJuridica pj"
            + " where "
            + " to_char(s.codigo) like :filtro"
            + " or to_char(s.dataSolicitacao) like :filtro"
            + " or bci.inscricaoCadastral like :filtro"
            + " or cmc.inscricaoCadastral like :filtro"
            + " or to_char(bcr.codigo) like :filtro"
            + " or lower(pf.nome) like :filtro"
            + " or lower(pj.razaoSocial) like :filtro"
            + " or lower(s.finalidade.descricao) like :filtro"
            + " ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<DataTableSolicitacaoDoctoOficial> listaComFiltros(int inicio, int max, String numeroInicial, String numeroFinal, Date dataInicial, Date dataFinal, TipoDoctoOficial tipoDoctoOficial, TipoCadastroTributario tipoCadastro, String cadastroInicial, String cadastroFinal, Pessoa pessoa) {

        String juncao = " where ";
        String hql = "select new br.com.webpublico.viewobjects.DataTableSolicitacaoDoctoOficial(s.id, s.codigo, s.dataSolicitacao, s.tipoDoctoOficial, s.tipoDoctoOficial.tipoCadastroDoctoOficial,"
            + " coalesce(cmc.inscricaoCadastral ,bci.inscricaoCadastral, to_char(bcr.codigo), pf.cpf, pj.cnpj), "
            + " coalesce(pf.nome, pj.razaoSocial, "
            + "coalesce((select pessoaf.nome from PessoaFisica pessoaf where pessoaf.id = prop.pessoa.id and rownum = 1),"
            + "(select pessoaj.razaoSocial from PessoaJuridica pessoaj where pessoaj.id = prop.pessoa.id and rownum = 1)),"
            + "coalesce((select pessoaf.nome from PessoaFisica pessoaf where pessoaf.id = cmc.pessoa.id and rownum = 1),"
            + "(select pessoaj.razaoSocial from PessoaJuridica pessoaj where pessoaj.id = cmc.pessoa.id and rownum = 1)),"
            + "coalesce((select pessoaf.nome from PessoaFisica pessoaf where pessoaf.id = propRural.pessoa.id and rownum = 1),"
            + "(select pessoaj.razaoSocial from PessoaJuridica pessoaj where pessoaj.id = propRural.pessoa.id and rownum = 1))), "
            + " s.tipoDoctoOficial.valor)"
            + " from SolicitacaoDoctoOficial s "
            + " left join s.cadastroImobiliario bci"
            + " left join bci.propriedade prop"
            + " left join s.cadastroEconomico cmc"
            + " left join s.cadastroRural bcr"
            + " left join bcr.propriedade propRural"
            + " left join s.pessoaFisica pf"
            + " left join s.pessoaJuridica pj";
        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            hql += juncao += " to_char(s.codigo) >= :codigoInicial ";
            juncao = " and ";
        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            hql += juncao += " to_char(s.codigo) <= :codigoFinal ";
            juncao = " and ";
        }
        if (dataInicial != null) {
            hql += juncao += " s.dataSolicitacao >= :dataInicial ";
            juncao = " and ";
        }
        if (dataFinal != null) {
            hql += juncao += " s.dataSolicitacao <= :dataFinal ";
            juncao = " and ";
        }
        if (tipoDoctoOficial != null) {
            hql += juncao += " s.tipoDoctoOficial = :tipoDoctoOficial ";
            juncao = " and ";
        }
        if (pessoa != null) {
            hql += juncao += " (s.pessoaFisica = :pessoa or s.pessoaJuridica = :pessoa)";
            juncao = " and ";
        }
        if (tipoCadastro != null && cadastroInicial != null && !cadastroInicial.isEmpty()) {
            if (cadastroInicial != null) {
                if (tipoCadastro.equals(TipoCadastroTributario.ECONOMICO)) {
                    hql += juncao += " (cmc.inscricaoCadastral >= :cadastroInicial)";
                    juncao = " and ";
                }
                if (tipoCadastro.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += juncao += " (bci.inscricaoCadastral >= :cadastroInicial)";
                    juncao = " and ";
                }
                if (tipoCadastro.equals(TipoCadastroTributario.RURAL)) {
                    hql += juncao += " (to_char(bcr.codigo) >= :cadastroInicial)";
                    juncao = " and ";
                }
            }
            if (tipoCadastro != null && cadastroFinal != null && !cadastroFinal.isEmpty()) {
                if (tipoCadastro.equals(TipoCadastroTributario.ECONOMICO)) {
                    hql += juncao += " (cmc.inscricaoCadastral <= :cadastroFinal)";
                    juncao = " and ";
                }
                if (tipoCadastro.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += juncao += " (bci.inscricaoCadastral <= :cadastroFinal)";
                    juncao = " and ";
                }
                if (tipoCadastro.equals(TipoCadastroTributario.RURAL)) {
                    hql += juncao += " (to_char(bcr.codigo) <= :cadastroFinal)";
                    juncao = " and ";
                }
            }
        }

        hql += " order by s.codigo";
        Query q = em.createQuery(hql);
        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            q.setParameter("codigoInicial", numeroInicial);
        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            q.setParameter("codigoFinal", numeroFinal);
        }
        if (dataInicial != null) {
            q.setParameter("dataInicial", dataInicial);
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", dataFinal);
        }
        if (tipoDoctoOficial != null) {
            q.setParameter("tipoDoctoOficial", tipoDoctoOficial);
        }
        if (pessoa != null) {
            q.setParameter("pessoa", pessoa);
        }
        if (tipoCadastro != null) {
            if (cadastroInicial != null && !cadastroInicial.isEmpty()) {
                q.setParameter("cadastroInicial", cadastroInicial);
            }
            if (cadastroFinal != null && !cadastroFinal.isEmpty()) {
                q.setParameter("cadastroFinal", cadastroFinal);
            }
        }
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public SituacaoSolicitacao calculaSituacaoSolicitacao(SolicitacaoDoctoOficial solicitacao) {
        SituacaoSolicitacao situacao;
        if (solicitacao.getTipoDoctoOficial() != null && solicitacao.getTipoDoctoOficial().getValor() != null && solicitacao.getTipoDoctoOficial().getValor().compareTo(BigDecimal.ZERO) > 0) {
            if (solicitacao.getId() != null && documentoOficialFacade.getCalculoDoctoOficialFacade().parcelaPaga(solicitacao)) {
                return SituacaoSolicitacao.PAGO;
            } else if (solicitacao.getId() != null && documentoOficialFacade.getCalculoDoctoOficialFacade().parcelaCancelada(solicitacao)) {
                return SituacaoSolicitacao.CANCELADO;
            } else if (solicitacao.getId() != null && documentoOficialFacade.getCalculoDoctoOficialFacade().parcelaEmAberto(solicitacao)) {
                return SituacaoSolicitacao.ABERTO;
            }
        }
        return SituacaoSolicitacao.NAONECESSITAPAGAMENTO;
    }

    private void adicionarOrdemPadrao(ConsultaParcela consultaParcela) {
        consultaParcela
            .addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.SD, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Ordem.TipoOrdem.DESC)

            .addOrdem(ConsultaParcela.Campo.VALOR_DIVIDA_ID, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.QUANTIDADE_PARCELA_VALOR_DIVIDA, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.PARCELA_NUMERO, ConsultaParcela.Ordem.TipoOrdem.ASC)

            .addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC)
            .addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
    }

    private List<ResultadoParcela> buscarParcelasReferenteASolicitacaoParaEmissao(SolicitacaoDoctoOficial solicitacaoDoctoOficial, List<SituacaoParcela> situacoes) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (solicitacaoDoctoOficial.getPessoaFisica() != null || solicitacaoDoctoOficial.getPessoaJuridica() != null) {
            Long idPessoa = solicitacaoDoctoOficial.getPessoaFisica() != null ? solicitacaoDoctoOficial.getPessoaFisica().getId() : solicitacaoDoctoOficial.getPessoaJuridica().getId();
            List<BigDecimal> cadastros = consultaDebitoFacade.getCadastroFacade().buscarIdsCadastrosDaPessoa(idPessoa, false, false);
            if (!cadastros.isEmpty()) {
                List<List<BigDecimal>> partes = Lists.partition(cadastros, 1000);
                for (List<BigDecimal> parteCadastros : partes) {
                    ConsultaParcela consultaParcela = new ConsultaParcela();
                    consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, parteCadastros);
                    consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);
                    adicionarOrdemPadrao(consultaParcela);
                    parcelas.addAll(consultaParcela.executaConsulta().getResultados());
                }
            }
            return parcelas;
        } else if (solicitacaoDoctoOficial.getCadastro() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, solicitacaoDoctoOficial.getCadastro().getId());
            consultaParcela.addComplementoDoWhere(" AND (BCI.ID IS NULL OR COALESCE(BCI.ATIVO,0) = 1 ) ");
            adicionarOrdemPadrao(consultaParcela);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    private List<ResultadoParcela> buscarParcelasReferenteASolicitacao(SolicitacaoDoctoOficial solicitacaoDoctoOficial, List<SituacaoParcela> situacoes) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (solicitacaoDoctoOficial.getPessoaFisica() != null || solicitacaoDoctoOficial.getPessoaJuridica() != null) {
            Long idPessoa = solicitacaoDoctoOficial.getPessoaFisica() != null ? solicitacaoDoctoOficial.getPessoaFisica().getId() : solicitacaoDoctoOficial.getPessoaJuridica().getId();
            List<BigDecimal> cadastros = consultaDebitoFacade.getCadastroFacade().buscarIdsCadastrosDaPessoa(idPessoa, false, false);
            if (!cadastros.isEmpty()) {
                List<List<BigDecimal>> partes = Lists.partition(cadastros, 1000);
                for (List<BigDecimal> parteCadastros : partes) {
                    parcelas.addAll(buscarParcelasPorSelect(parteCadastros, situacoes));
                }
            }
            return parcelas;
        } else if (solicitacaoDoctoOficial.getCadastro() != null) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);
            consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, solicitacaoDoctoOficial.getCadastro().getId());
            consultaParcela.addComplementoDoWhere(" AND (BCI.ID IS NULL OR COALESCE(BCI.ATIVO,0) = 1 ) ");
            adicionarOrdemPadrao(consultaParcela);
            return consultaParcela.executaConsulta().getResultados();
        }
        return Lists.newArrayList();
    }

    private List<ResultadoParcela> buscarParcelasPorSelect(List<BigDecimal> idsCadastros, List<SituacaoParcela> situacoes) {
        List<String> situacoesStr = Lists.newArrayList();
        for (SituacaoParcela situacao : situacoes) {
            situacoesStr.add(situacao.name());
        }
        String sql = "select pvd.id, pvd.vencimento, spvd.situacaoParcela, cal.tipoCalculo, op.promocional\n" +
            "from ParcelaValorDivida pvd\n" +
            "inner join ValorDivida vd on vd.id = pvd.valorDivida_id\n" +
            "inner join Calculo cal on cal.id = vd.calculo_id\n" +
            "inner join SituacaoParcelaValorDivida spvd on spvd.id = pvd.situacaoAtual_id\n" +
            "inner join OpcaoPagamento op on op.id = pvd.opcaopagamento_id\n" +
            "where spvd.situacaoParcela in (:situacoes)\n" +
            "  and cal.cadastro_id in (:idsCadastros)";
        List<ResultadoParcela> retorno = Lists.newArrayList();
        Query q = em.createNativeQuery(sql);
        q.setParameter("idsCadastros", idsCadastros);
        q.setParameter("situacoes", situacoesStr);

        List<Object[]> lista = q.getResultList();
        for (Object[] obj : lista) {
            ResultadoParcela rp = new ResultadoParcela();
            rp.setIdParcela(((BigDecimal) obj[0]).longValue());
            rp.setVencimento((Date) obj[1]);
            rp.setSituacao((String) obj[2]);
            rp.setTipoCalculo((String) obj[3]);
            rp.setCotaUnica(((BigDecimal)obj[4]).compareTo(BigDecimal.valueOf(1)) == 0);
            retorno.add(rp);
        }
        return retorno;
    }

    private List<ResultadoParcela> buscarParcelasSomenteDaPessoa(SolicitacaoDoctoOficial solicitacaoDoctoOficial, List<SituacaoParcela> situacoes) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (solicitacaoDoctoOficial.getPessoaFisica() != null || solicitacaoDoctoOficial.getPessoaJuridica() != null) {
            Long idPessoa = solicitacaoDoctoOficial.getPessoaFisica() != null ? solicitacaoDoctoOficial.getPessoaFisica().getId() : solicitacaoDoctoOficial.getPessoaJuridica().getId();
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, idPessoa);
            consultaParcela.addComplementoDoWhere(" and vw.cadastro_id is null");
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, situacoes);
            adicionarOrdemPadrao(consultaParcela);
            parcelas = consultaParcela.executaConsulta().getResultados();
        }
        return parcelas;
    }

    public List<ResultadoParcela> buscarParcelaParaSolicitacaoDeCertidaoParaEmissao(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        List<SituacaoParcela> situacoes = Lists.newArrayList(SituacaoParcela.EM_ABERTO,
            SituacaoParcela.SUSPENSAO
        );

        parcelas.addAll(buscarParcelasReferenteASolicitacaoParaEmissao(solicitacaoDoctoOficial, situacoes));
        parcelas.addAll(buscarParcelasSomenteDaPessoa(solicitacaoDoctoOficial, situacoes));

        return parcelas;
    }

    public List<ResultadoParcela> buscarParcelaParaSolicitacaoDeCertidao(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        List<SituacaoParcela> situacoes = Lists.newArrayList(SituacaoParcela.EM_ABERTO,
            SituacaoParcela.SUSPENSAO
        );
        parcelas.addAll(buscarParcelasReferenteASolicitacao(solicitacaoDoctoOficial, situacoes));
        parcelas.addAll(buscarParcelasSomenteDaPessoa(solicitacaoDoctoOficial, situacoes));

        return parcelas;
    }

    private List<ResultadoParcela> buscarParcelaParaSolicitacaoDeCertidaoPositivaComEfeitoNegativo(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        List<SituacaoParcela> situacoes = Lists.newArrayList(SituacaoParcela.EM_ABERTO,
            SituacaoParcela.PAGO,
            SituacaoParcela.BAIXADO,
            SituacaoParcela.PAGO_PARCELAMENTO,
            SituacaoParcela.PAGO_SUBVENCAO,
            SituacaoParcela.COMPENSACAO,
            SituacaoParcela.SUSPENSAO
        );

        parcelas.addAll(buscarParcelasReferenteASolicitacao(solicitacaoDoctoOficial, situacoes));
        parcelas.addAll(buscarParcelasSomenteDaPessoa(solicitacaoDoctoOficial, situacoes));

        return parcelas;
    }

    public ValidacaoCertidaoDoctoOficial permitirSolicitarDocumento(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        ValidacaoCertidaoDoctoOficial validacao = new ValidacaoCertidaoDoctoOficial();
        TipoValidacaoDoctoOficial tipoValidacao = solicitacaoDoctoOficial.getTipoDoctoOficial().getTipoValidacaoDoctoOficial();
        List<ResultadoParcela> parcelas;
        List<ResultadoParcela> vencidos = Lists.newArrayList();
        List<ResultadoParcela> aVencer = Lists.newArrayList();

        montarMensagemValidacao(tipoValidacao, solicitacaoDoctoOficial.getTipoDoctoOficial().getDescricao(), solicitacaoDoctoOficial.getTipoCadastroDoctoOficialEnumValue(), validacao);

        switch (tipoValidacao) {
            case CERTIDAOPOSITIVA:
                parcelas = buscarParcelaParaSolicitacaoDeCertidao(solicitacaoDoctoOficial);
                for (ResultadoParcela p : parcelas) {
                    if (p.getVencido() || Calculo.TipoCalculo.PARCELAMENTO.equals(p.getTipoCalculoEnumValue())) {
                        vencidos.add(p);
                    }
                }
                validacao.setValido(!vencidos.isEmpty());
                return validacao;
            case CERTIDAONEGATIVA:
                parcelas = buscarParcelaParaSolicitacaoDeCertidao(solicitacaoDoctoOficial);
                for (ResultadoParcela p : parcelas) {
                    if (p.getVencido() && (!Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())
                        && !Calculo.TipoCalculo.NFS_AVULSA.equals(p.getTipoCalculoEnumValue())
                        && !Calculo.TipoCalculo.DOCTO_OFICIAL.equals(p.getTipoCalculoEnumValue()))) {
                        validacao.setMensagem("Não é possível efetuar a solicitação de " + solicitacaoDoctoOficial.getTipoDoctoOficial().getDescricao() +
                            ". O(a) " + solicitacaoDoctoOficial.getTipoCadastroDoctoOficialEnumValue().getDescricao() + " possui débitos vencidos!");
                        vencidos.add(p);
                    } else if (p.isEmAberto() && Calculo.TipoCalculo.PARCELAMENTO.equals(p.getTipoCalculoEnumValue())) {
                        validacao.setMensagem("Não é possível efetuar a solicitação de " + solicitacaoDoctoOficial.getTipoDoctoOficial().getDescricao() +
                            ". O(a) " + solicitacaoDoctoOficial.getTipoCadastroDoctoOficialEnumValue().getDescricao() + " possui débitos parcelados! " +
                            " Favor solicitar a Certidão Positiva com efeito Negativo!");
                        vencidos.add(p);
                    } else if (p.getVencido() && Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())) {
                        if (tributoTaxasDividasDiversasFacade.parcelaComTaxaBloqueada(p.getIdParcela())) {
                            validacao.setMensagem("Não é possível efetuar a solicitação de " + solicitacaoDoctoOficial.getTipoDoctoOficial().getDescricao() +
                                ". O(a) " + solicitacaoDoctoOficial.getTipoCadastroDoctoOficialEnumValue().getDescricao() + " possui débitos vencidos!");
                            vencidos.add(p);
                        }
                    }
                }
                validacao.setValido(vencidos.isEmpty());
                return validacao;
            case CERTIDAOPOSITIVA_EFEITONEGATIVA:
                parcelas = buscarParcelaParaSolicitacaoDeCertidaoPositivaComEfeitoNegativo(solicitacaoDoctoOficial);
                Map<Long, List<ResultadoParcela>> mapaPorIdCalculo = Maps.newHashMap();
                for (ResultadoParcela p : parcelas) {
                    if (SituacaoParcela.SUSPENSAO.equals(p.getSituacaoEnumValue())) {
                        aVencer.add(p);
                    } else if (SituacaoParcela.EM_ABERTO.equals(p.getSituacaoEnumValue()) && (p.getVencido()
                        && !Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue())
                        && !Calculo.TipoCalculo.NFS_AVULSA.equals(p.getTipoCalculoEnumValue())
                        && !Calculo.TipoCalculo.DOCTO_OFICIAL.equals(p.getTipoCalculoEnumValue())
                        && !Calculo.TipoCalculo.PARCELAMENTO.equals(p.getTipoCalculoEnumValue())
                    )) {
                        vencidos.add(p);
                    } else if (SituacaoParcela.EM_ABERTO.equals(p.getSituacaoEnumValue()) &&
                        (p.getVencido() && Calculo.TipoCalculo.TAXAS_DIVERSAS.equals(p.getTipoCalculoEnumValue()))) {
                        if (tributoTaxasDividasDiversasFacade.parcelaComTaxaBloqueada(p.getIdParcela())) {
                            vencidos.add(p);
                        } else {
                            aVencer.add(p);
                        }
                    } else if (Calculo.TipoCalculo.PARCELAMENTO.equals(p.getTipoCalculoEnumValue())) {
                        if (mapaPorIdCalculo.containsKey(p.getIdCalculo())) {
                            List<ResultadoParcela> parcelasDoCalculo = mapaPorIdCalculo.get(p.getIdCalculo());
                            parcelasDoCalculo.add(p);
                            mapaPorIdCalculo.put(p.getIdCalculo(), parcelasDoCalculo);
                        } else {
                            mapaPorIdCalculo.put(p.getIdCalculo(), Lists.newArrayList(p));
                        }
                    } else if (SituacaoParcela.EM_ABERTO.equals(p.getSituacaoEnumValue()) &&
                        Calculo.TipoCalculo.DOCTO_OFICIAL.equals(p.getTipoCalculoEnumValue())) {
                        aVencer.add(p);
                    }
                }
                if (!vencidos.isEmpty()) {
                    validacao.setValido(false);
                    return validacao;
                }
                if (!aVencer.isEmpty() || !mapaPorIdCalculo.isEmpty()) {
                    Map<Long, Boolean> mapaIdCalculoPorSolicitar = Maps.newHashMap();
                    for (Long idCalculo : mapaPorIdCalculo.keySet()) {
                        boolean umaParcelaPaga = false;
                        boolean umaParcelaAberta = false;
                        boolean umaParcelaAbertaVencida = false;
                        boolean todasParcelasPagas = true;
                        for (ResultadoParcela p : mapaPorIdCalculo.get(idCalculo)) {
                            if (p.isPago()) {
                                umaParcelaPaga = true;
                            } else {
                                todasParcelasPagas = false;
                                if (!p.getVencido()) {
                                    umaParcelaAberta = true;
                                } else if (p.isEmAberto()){
                                    umaParcelaAbertaVencida = true;
                                }
                            }
                        }
                        if (mapaPorIdCalculo.get(idCalculo).size() > 1) {
                            mapaIdCalculoPorSolicitar.put(idCalculo, (umaParcelaPaga && umaParcelaAberta && !umaParcelaAbertaVencida) || todasParcelasPagas);
                        } else {
                            mapaIdCalculoPorSolicitar.put(idCalculo, (umaParcelaPaga || umaParcelaAberta) && !umaParcelaAbertaVencida);
                        }
                    }
                    boolean todosCaculosPodemSolicitar = true;
                    for (Long idCalculo : mapaIdCalculoPorSolicitar.keySet()) {
                        if (!mapaIdCalculoPorSolicitar.get(idCalculo)) {
                            todosCaculosPodemSolicitar = false;
                        }
                    }
                    validacao.setValido(todosCaculosPodemSolicitar);
                    return validacao;
                } else {
                    validacao.setValido(false);
                    return validacao;
                }
            case CERTIDAO_POSITIVA_BENS_IMOVEIS:
                validacao.setValido(!buscarCadastrosImobiliarioDaPessoa(solicitacaoDoctoOficial).isEmpty());
                return validacao;
            case CERTIDAO_NEGATIVA_BENS_IMOVEIS:
                validacao.setValido(buscarCadastrosImobiliarioDaPessoa(solicitacaoDoctoOficial).isEmpty());
                return validacao;
        }
        validacao.setValido(true);
        return validacao;
    }

    public ValidacaoCertidaoDoctoOficial montarMensagemValidacao(TipoValidacaoDoctoOficial tipoValidacaoDoctoOficial, String nomeDocumentoOficial, TipoCadastroDoctoOficial tipoCadastro, ValidacaoCertidaoDoctoOficial validacaoCertidao) {
        if (tipoValidacaoDoctoOficial != null) {
            String inicioMensagem = "Não é possível efetuar a solicitação de ";
            if (TipoValidacaoDoctoOficial.CERTIDAONEGATIVA.equals(tipoValidacaoDoctoOficial)) {
                validacaoCertidao.setMensagem(inicioMensagem + nomeDocumentoOficial + ". O(a) "
                    + tipoCadastro.getDescricao() + " possui débitos que não se enquadram a solicitação!");
            } else if (TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA.equals(tipoValidacaoDoctoOficial)) {
                validacaoCertidao.setMensagem(inicioMensagem + nomeDocumentoOficial + ". O(a) "
                    + tipoCadastro.getDescricao() + " não possui débitos!");
            } else if (TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA_EFEITONEGATIVA.equals(tipoValidacaoDoctoOficial)) {
                validacaoCertidao.setMensagem(inicioMensagem + nomeDocumentoOficial + ". O(a) "
                    + tipoCadastro.getDescricao() + " possui débitos vencidos ou não possui débitos em um processo de parcelamento que possui pelo menos uma parcela paga!");
            } else if (TipoValidacaoDoctoOficial.CERTIDAO_POSITIVA_BENS_IMOVEIS.equals(tipoValidacaoDoctoOficial)) {
                validacaoCertidao.setMensagem(inicioMensagem + nomeDocumentoOficial + ". O(a) "
                    + tipoCadastro.getDescricao() + " não possui imóveis cadastrado no município!");
            } else if (TipoValidacaoDoctoOficial.CERTIDAO_NEGATIVA_BENS_IMOVEIS.equals(tipoValidacaoDoctoOficial)) {
                validacaoCertidao.setMensagem(inicioMensagem + nomeDocumentoOficial + ". O(a) "
                    + tipoCadastro.getDescricao() + " possui imóveis cadastrado no município!");
            } else {
                validacaoCertidao.setMensagem("");
            }
        }
        return validacaoCertidao;
    }

    private List<CadastroImobiliario> buscarCadastrosImobiliarioDaPessoa(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        return cadastroImobiliarioFacade.buscarCadastrosAtivosDaPessoa(solicitacaoDoctoOficial.getPessoa());
    }

    public void calculaValorCertidao(SolicitacaoDoctoOficial selecionado) {
        selecionado = em.find(SolicitacaoDoctoOficial.class, selecionado.getId());
        if (selecionado.getTipoDoctoOficial().getValor() != null && selecionado.getTipoDoctoOficial().getValor().compareTo(BigDecimal.ZERO) > 0) {
            CalculoDoctoOficial calculo = new CalculoDoctoOficial();
            //calculo já efetuado em UFM.
            calculo.setValorEfetivo(selecionado.getTipoDoctoOficial().getValor());
            calculo.setValorReal(calculo.getValorEfetivo());
            calculo.setSimulacao(false);

            if (TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
                calculo.setCadastro(selecionado.getCadastroEconomico());
            } else if (TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
                calculo.setCadastro(selecionado.getCadastroImobiliario());
            } else if (TipoCadastroDoctoOficial.CADASTRORURAL.equals(selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial())) {
                calculo.setCadastro(selecionado.getCadastroRural());
            } else {
                calculo.setCadastro(null);
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, selecionado.getTipoDoctoOficial().getValidadeDam());
            while (DataUtil.ehDiaNaoUtil(cal.getTime(), getFeriadoFacade())) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            calculo.setVencimento(cal.getTime());
            calculo.setSolicitacaoDoctoOficial(selecionado);
            calculo.setDataCalculo(new Date());
            calculo.setExercicio(sistemaFacade.getExercicioCorrente());
            calculo.setTributo(selecionado.getTipoDoctoOficial().getTributo());
            calculo.setPessoas(geraPessoasCalculo(selecionado, calculo));
            calculo.setSolicitacaoDoctoOficial(selecionado);
            getDocumentoOficialFacade().getCalculoDoctoOficialFacade().salvar(calculo, selecionado.getTipoDoctoOficial().getTipoCadastroDoctoOficial());
        }
    }

    public boolean solicitacaoPaga(Long idSolicitacao) {
        String sql = "select pvd.id from solicitacaodoctooficial sol " +
            "inner join CalculoDoctoOficial cd on cd.solicitacaoDoctoOficial_id = sol.id " +
            "inner join valordivida vd on vd.calculo_id = cd.id " +
            "inner join parcelavalordivida pvd on pvd.VALORDIVIDA_ID = vd.id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "where spvd.situacaoparcela = 'EM_ABERTO' " +
            "and sol.id = :id";
        Query q = em.createNativeQuery(sql).setParameter("id", idSolicitacao);
        return q.getResultList().isEmpty();
    }

    public CalculoDoctoOficial retornaCalculoDaSolicitacao(SolicitacaoDoctoOficial sol) {
        Query q = em.createQuery("from CalculoDoctoOficial calc where calc.solicitacaoDoctoOficial = :sol")
            .setParameter("sol", sol);
        if (!q.getResultList().isEmpty()) {
            return (CalculoDoctoOficial) q.getResultList().get(0);
        }
        return null;
    }


    public SolicitacaoDoctoOficial salvarSolicitacao(SolicitacaoDoctoOficial solicitacao) {
        solicitacao.setCodigo(singletonGeradorCodigoTributario.getProximoNumero(sistemaFacade.getExercicioCorrente(), SolicitacaoDoctoOficial.class, "codigo", 5));
        return em.merge(solicitacao);
    }

    public boolean existeCertidaoVigente(SolicitacaoDoctoOficial solicitacao) {
        Long idCadastro = solicitacao.getCadastro() != null ? solicitacao.getCadastro().getId() : solicitacao.getPessoa().getId();
        return existeCertidaoVigente(solicitacao.getTipoDoctoOficial().getId(), solicitacao.getTipoDoctoOficial().getTipoCadastroDoctoOficial(), idCadastro);
    }

    public boolean existeCertidaoVigente(Long idTipoDoctoOficial, TipoCadastroDoctoOficial tipoCadastroDoctoOficial, Long idCadastro) {
        SolicitacaoDoctoOficial sol = buscarSolicitacaoCertidaoVigente(idTipoDoctoOficial, tipoCadastroDoctoOficial, idCadastro);
        return sol != null;
    }

    public SolicitacaoDoctoOficial buscarSolicitacaoCertidaoVigente(Long idTipoDoctoOficial, TipoCadastroDoctoOficial tipoCadastroDoctoOficial, Long idCadastro) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select sdo.* from solicitacaoDoctoOficial sdo ");
        sql.append(" inner join  tipodoctooficial tipo on tipo.id = sdo.tipodoctooficial_id and  tipo.id = :TIPODOCUMENTO_ID");
        sql.append(" inner join documentoOficial do on do.id = sdo.documentoOficial_id ");

        if (TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(tipoCadastroDoctoOficial)) {
            sql.append(" inner join cadastroEconomico cadastro on cadastro.id = sdo.cadastroeconomico_id and cadastro.id = :CADASTRO_ID ");
        } else if (TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(tipoCadastroDoctoOficial)) {
            sql.append(" inner join cadastroImobiliario cadastro on cadastro.id = sdo.cadastroimobiliario_id and cadastro.id = :CADASTRO_ID ");
        } else if (TipoCadastroDoctoOficial.CADASTRORURAL.equals(tipoCadastroDoctoOficial)) {
            sql.append(" inner join cadastroRural cadastro on cadastro.id = sdo.cadastrorural_id and cadastro.id = :CADASTRO_ID ");
        } else if (TipoCadastroDoctoOficial.PESSOAFISICA.equals(tipoCadastroDoctoOficial)) {
            sql.append(" inner join pessoaFisica cadastro on cadastro.id = sdo.pessoaFisica_id and cadastro.id = :CADASTRO_ID ");
        } else if (TipoCadastroDoctoOficial.PESSOAJURIDICA.equals(tipoCadastroDoctoOficial)) {
            sql.append(" inner join pessoaJuridica cadastro on cadastro.id = sdo.pessoaJuridica_id and cadastro.id = :CADASTRO_ID ");
        }

        sql.append(" where do.validade >= :DATAATUAL and sdo.situacaosolicitacao <> 'CANCELADO'");

        Query q = em.createNativeQuery(sql.toString(), SolicitacaoDoctoOficial.class);

        q.setParameter("DATAATUAL", sistemaFacade.getDataOperacao());
        q.setParameter("TIPODOCUMENTO_ID", idTipoDoctoOficial);
        q.setParameter("CADASTRO_ID", idCadastro);

        return q.getResultList() != null && !q.getResultList().isEmpty() ? (SolicitacaoDoctoOficial)q.getResultList().get(0) : null;
    }

    public List<SolicitacaoDoctoOficial> buscarSolicitacoesPorCodigo(Long codigo) {
        String hql = "select s from SolicitacaoDoctoOficial s where s.codigo = :codigo and s.situacaoSolicitacao <> :cancelado order by s.dataSolicitacao";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("codigo", codigo);
        q.setParameter("cancelado", SituacaoSolicitacao.CANCELADO);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public void cancelarParcela(List<ResultadoParcela> parcelas) {
        for (ResultadoParcela resultadoParcela : parcelas) {
            ParcelaValorDivida parcela = recuperarParcela(resultadoParcela);
            SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
            situacao.setSaldo(parcela.getUltimaSituacao().getSaldo());
            situacao.setDataLancamento(new Date());
            situacao.setParcela(parcela);
            situacao.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);
            em.persist(situacao);
        }
    }

    private ParcelaValorDivida recuperarParcela(ResultadoParcela parcela) {
        String hql = "select p from ParcelaValorDivida p where p.id = :idParcela";
        Query q = em.createQuery(hql);
        q.setParameter("idParcela", parcela.getIdParcela());
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

    public void salvar(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        em.merge(solicitacaoDoctoOficial);
    }

    public SolicitacaoDoctoOficial salvarRetornando(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        return em.merge(solicitacaoDoctoOficial);
    }

}
