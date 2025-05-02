/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.CalculoTaxasDiversasControlador;
import br.com.webpublico.controle.CancelamentoTaxasDiversasControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoTaxasDiversas;
import br.com.webpublico.entidadesauxiliares.RelacaoTaxasDiversas;
import br.com.webpublico.enums.SituacaoCalculo;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class CalculoTaxasDiversasFacade extends CalculoExecutorDepoisDePagar<CalculoTaxasDiversas>  {

    private static final int TAMANHO_LINHA = 89;
    private static final int QUEBRA_LINHA = 1;
    private static final int CONCATENA_LINHA = 2;
    private static final int TAMANHO_DESCRICAO_VALOR = 60;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private GeraValorDividaTaxasDiversas geraDebito;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade DAMFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private ConfiguracaoDividaFacade configuracaoDividaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public CalculoTaxasDiversasFacade() {
        super(CalculoTaxasDiversas.class);
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public CadastroFacade getCadastroFacade() {
        return cadastroFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public TributoTaxasDividasDiversasFacade getTributoTaxasDividasDiversasFacade() {
        return tributoTaxasDividasDiversasFacade;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public GeraValorDividaTaxasDiversas getGeraDebito() {
        return geraDebito;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DAMFacade getDAMFacade() {
        return DAMFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public ProcessoCalcTaxasDiversas processarCalculoAndGerarDebito(CalculoTaxasDiversas calculo) throws Exception {
        ProcessoCalcTaxasDiversas processo = processaCalculo(calculo);
        geraDebito.geraDebito(processo);
        processo.getCalculos().get(0).setSituacao(SituacaoCalculo.EMITIDO);
        return em.merge(processo);
    }

    public ProcessoCalcTaxasDiversas processaCalculo(CalculoTaxasDiversas calculo) throws Exception {
        if (calculo.getNumero() == null) {
            calculo.setNumero(retornoProximoNumero(calculo.getExercicio()));
        }
        ProcessoCalcTaxasDiversas processo = criaProcesso(calculo);
        return em.merge(processo);
    }

    public Long retornoProximoNumero(Exercicio exercicio) {
        Query q = em.createNativeQuery(" select coalesce(max(l.numero),0) + 1 from calculotaxasdiversas l "
            + " where l.exercicio_id =:exercicio_id ");
        q.setParameter("exercicio_id", exercicio.getId());
        if (q.getSingleResult() == null) {
            return 1l;
        } else {
            BigDecimal retorno = (BigDecimal) q.getSingleResult();
            return retorno.longValueExact();
        }
    }

    private ProcessoCalcTaxasDiversas criaProcesso(CalculoTaxasDiversas calculo) {
        ProcessoCalcTaxasDiversas processo = new ProcessoCalcTaxasDiversas();
        processo.setDataLancamento(calculo.getDataCalculo());
        processo.setExercicio(calculo.getExercicio());
        definirDescricaoProcessoCalculo(processo, calculo);
        definirDividaProcessoCalculo(processo, calculo);
        processo.setCalculos(Lists.newArrayList());
        processo.getCalculos().add(calculo);
        calculo.setProcessoCalcTaxasDiversas(processo);
        return processo;
    }

    private void definirDividaProcessoCalculo(ProcessoCalcTaxasDiversas processo, CalculoTaxasDiversas calculo) {
        ItemCalculoTaxasDiversas itemCalculoTaxasDiversas = calculo.getItens().stream().findFirst().get();
        Entidade entidade = itemCalculoTaxasDiversas.getTributoTaxaDividasDiversas().getTributo().getEntidade();
        ConfiguracaoDivida configuracaoDivida = configuracaoDividaFacade.getConfiguracaoDivida(calculo.getTipoCalculo(),
            entidade, calculo.getTipoCadastroTributario());
        if (configuracaoDivida == null) {
            throw new ValidacaoException("Nenhuma configuração de dívida encontrada para o cálculo de " + calculo.getTipoCalculo().getDescricao() +
                " na entidade " + entidade.getNome() + " e tipo de cadastro tributário " + calculo.getTipoCadastroTributario().getDescricao());
        } else {
            processo.setDivida(configuracaoDivida.getDivida());
        }
    }

    private void definirDescricaoProcessoCalculo(ProcessoCalcTaxasDiversas processo, CalculoTaxasDiversas calculo) {
        if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
            processo.setDescricao("Taxas Diversas - Cadastro Imobiliário: " + ((CadastroImobiliario) calculo.getCadastro()).getInscricaoCadastral());
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
            processo.setDescricao("Taxas Diversas - Cadastro Mobiliário: " + ((CadastroEconomico) calculo.getCadastro()).getInscricaoCadastral());
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            processo.setDescricao("Taxas Diversas - Cadastro Rural: " + ((CadastroRural) calculo.getCadastro()).getNumeroCadastro());
        } else {
            processo.setDescricao("Taxas Diversas - Contribuinte Geral: " + calculo.getContribuinte().getNome());
        }
    }

    public ItemCalculoTaxasDiversas calculaValoresUFMRBItem(ItemCalculoTaxasDiversas item) {
        try {
            IndiceEconomico UFMRB = indiceEconomicoFacade.recuperaPorDescricao("UFM");
            Moeda moeda = moedaFacade.getMoedaPorIndiceData(UFMRB, item.getCalculoTaxasDiversas().getDataCalculo());
            item.setValorReal(item.getValorUFM().multiply(moeda.getValor()).setScale(6, RoundingMode.HALF_UP));
            return item;
        } catch (Exception ex) {
            logger.error("Erro ao calcular UFM");
            item.setValorReal(BigDecimal.ZERO);
            return item;
        }
    }

    public ValorDivida retornaValorDividaDoCalculo(ProcessoCalcTaxasDiversas calculo) {

        ValorDivida retorno = null;
        Query q = em.createQuery(" from ValorDivida vd where vd.calculo = (select c from CalculoTaxasDiversas c where c.processoCalcTaxasDiversas = :calculo)");
        q.setParameter("calculo", calculo);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = (ValorDivida) q.getResultList().get(0);
            q = em.createQuery("from Calculo c where c = (select c from CalculoTaxasDiversas c where c.processoCalcTaxasDiversas = :calculo)");
            q.setParameter("calculo", calculo);
            if (q.getResultList() != null && q.getResultList().size() > 0) {
                retorno.setCalculo((Calculo) q.getResultList().get(0));
                retorno.getParcelaValorDividas().size();
            }
        }
        return retorno;
    }

    public String montaDescricaoLonga(ValorDivida aImprimir) throws UFMException {
        StringBuilder descricao = new StringBuilder();
        DecimalFormat formatNumber = new DecimalFormat("#,###,##0.00");
        CalculoTaxasDiversas calculo = em.find(CalculoTaxasDiversas.class, aImprimir.getCalculo().getId());
        descricao.append(incluiTexto("DESCRIÇÃO", 38, true));
        descricao.append(incluiTexto("QUANTIDADE", 17, true));
        descricao.append(incluiTexto("VALOR UNIT", 10, false));
        descricao.append(incluiTexto("VALOR TOTAL\n", 25, false));
        descricao.append("__________________________________________________________________________________________\n");

        for (ItemCalculoTaxasDiversas item : calculo.getItens()) {
            BigDecimal quantidade = new BigDecimal(item.getQuantidadeTributoTaxas());
            BigDecimal valorUnitario = item.getValorReal();
            BigDecimal valorTotal = quantidade.multiply(valorUnitario);
            String descricaoDaTaxa = item.getTributoTaxaDividasDiversas().getTributo().getDescricao().toUpperCase();

            descricao.append(incluiTexto(descricaoDaTaxa, 44, true));
            descricao.append(incluiTexto(String.valueOf(quantidade), 10, true));
            descricao.append(incluiTexto(formatNumber.format(valorUnitario), 10, false));
            descricao.append(incluiTexto(formatNumber.format(valorTotal), 25, false));

//            descricao.append(adicionaValor((item.getTributoTaxaDividasDiversas().getTributo().getDescricao().toUpperCase()), quantidade.multiply(item.getValorReal()), "R$", true));
        }

        descricao.append("\n\n");
//        descricao.append(adicionaValor("VALOR TOTAL", calculo.getValorReal(), "R$", false)).append("\n");
        descricao.append(incluiTexto("VALOR TOTAL: ", 64, false));
        descricao.append(incluiTexto(formatNumber.format(calculo.getValorReal()), 25, false));
        //descricao.append("\nVALOR TOTAL R$").append(formatNumber.format(calculo.getValorReal()));

        descricao.append("\n\n\n\n\n\nPROCESSO: ");
        descricao.append(calculo.getExercicio()).append(calculo.getNumeroFormatado());
        descricao.append("\nOBSERVAÇÃO: ");
        descricao.append("\n\n").append(calculo.getObservacao() == null ? " " : calculo.getObservacao());
        return descricao.toString();
    }

    public String montaDescricaoCurta(ValorDivida aImprimir) throws UFMException {
        DecimalFormat formatNumber = new DecimalFormat("#,###,##0.00");
        StringBuilder descricao = new StringBuilder();
        CalculoTaxasDiversas calculo = em.find(CalculoTaxasDiversas.class, aImprimir.getCalculo().getId());
        descricao.append("PROCESSO: ");
        descricao.append(calculo.getExercicio()).append(calculo.getNumeroFormatado());
        descricao.append("\nVALOR TOTAL R$").append(formatNumber.format(calculo.getValorReal()));
        return descricao.toString();
    }

    private String adicionaLinha(String texto, int operacao) {
        String retorno;
        if (operacao == CONCATENA_LINHA) {
            retorno = texto.substring(0, TAMANHO_LINHA > texto.length() ? texto.length() : TAMANHO_LINHA);
        } else {
            int tamanhoLinhaAtual = 0;
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < texto.length(); i++) {
                if (i >= texto.length()) {
                    break;
                }
                sb.append(texto.charAt(i));
                tamanhoLinhaAtual++;
                if (tamanhoLinhaAtual >= TAMANHO_LINHA) {
                    sb.append("\n");
                    tamanhoLinhaAtual = 0;
                }
            }
            retorno = sb.toString();
        }
        return retorno;
    }

    private String adicionaValor(String texto, BigDecimal valor, String simbolo, boolean insereNoFinal) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < TAMANHO_DESCRICAO_VALOR) {
            sb.append(" ");
        }
        //espaço para completar a coluna com 44 caracteres
        int tamanhoEspaco = 9;
        for (int i = 0; i < (tamanhoEspaco - simbolo.length()); i++) {
            sb.append(" ");
        }
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        String valorString = df.format(valor);
        int espacos = 14 - valorString.length();
        if (espacos > 0) {
            for (int i = 0; i < espacos; i++) {
                valorString = " " + valorString;
            }
        }
        if (insereNoFinal) {
            sb.append(valorString).append(simbolo);
        } else {
            sb.append(simbolo).append(valorString);
        }
        //sb.append(valorString);
        sb.append(" ");
        return sb.toString();
    }

    @Override
    public CalculoTaxasDiversas recuperar(Object id) {
        CalculoTaxasDiversas calculo = em.find(CalculoTaxasDiversas.class, id);
        calculo.getItens().size();
        return calculo;
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        CalculoTaxasDiversas taxa = recuperar(calculo.getId());
        taxa.setSituacao(SituacaoCalculo.PAGO);
        em.merge(calculo);
    }

    public ProcessoCalcTaxasDiversas recuperaProcesso(ProcessoCalcTaxasDiversas proc) {
        if (proc != null || proc.getId() != null) {
            ProcessoCalcTaxasDiversas processo = em.find(ProcessoCalcTaxasDiversas.class, proc.getId());
            processo.getCalculos().size();
            for (CalculoTaxasDiversas calculo : processo.getCalculos()) {
                processo.getCalculos().set(processo.getCalculos().indexOf(calculo), recuperar(calculo.getId()));
            }
            return processo;
        } else {
            return null;
        }
    }

    @Override
    public List<CalculoTaxasDiversas> lista() {
        return em.createQuery("From CalculoTaxasDiversas c order by c.vencimento desc, c.situacao").getResultList();
    }

    public CalculoTaxasDiversas salva(CalculoTaxasDiversas entity) {
        CalculoTaxasDiversas c = em.merge(entity);
        c.getItens().size();
        return c;
    }

    public List<CalculoTaxasDiversas> listaCalculoTaxasDiversasParaCancelamento() {
        String hql = "select calculo from CalculoTaxasDiversas calculo "
            + " where calculo.situacao = '" + SituacaoCalculo.EMITIDO.name() + "' ";
        Query q = em.createQuery(hql);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<CalculoTaxasDiversas>();
    }

    public List<ValorDivida> getValorDividaPorCalculo(CalculoTaxasDiversas calculoTaxasDiversas) {
        String hql = "select vd from ValorDivida vd where vd.calculo = :calculoTaxasDiversas";
        Query q = em.createQuery(hql);
        q.setParameter("calculoTaxasDiversas", calculoTaxasDiversas);
        //System.out.println("q.getResultlist() valordivida = " + q.getResultList().size());
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<ValorDivida>();
    }

    public void cancelaParcelasValorDivida(ValorDivida valorDivida) {
        valorDivida = recuperarValorDivida(valorDivida);
        List<ParcelaValorDivida> listaParcelas = valorDivida.getParcelaValorDividas();
        for (ParcelaValorDivida parcelaValorDivida : listaParcelas) {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcelaValorDivida, parcelaValorDivida.getValor()));
            em.persist(parcelaValorDivida);
        }
    }

    private ValorDivida recuperarValorDivida(ValorDivida valorDivida) {
        valorDivida = this.em.find(ValorDivida.class, valorDivida.getId());
        valorDivida.getParcelaValorDividas().size();
        return valorDivida;
    }

    public boolean permiteCancelamento(ValorDivida valorDivida) {
        valorDivida = recuperarValorDivida(valorDivida);
        List<ParcelaValorDivida> listaParcelas = valorDivida.getParcelaValorDividas();
        for (ParcelaValorDivida parcelaValorDivida : listaParcelas) {
            SituacaoParcelaValorDivida ultimaSituacao = parcelaValorDivida.getUltimaSituacao();
            //System.out.println("ultimasituacao = " + ultimaSituacao.getSituacaoParcela());
            if (!ultimaSituacao.getSituacaoParcela().equals(SituacaoParcela.AGUARDANDO) && !ultimaSituacao.getSituacaoParcela().equals(SituacaoParcela.EM_ABERTO)) {
                return false;
            }
        }
        return true;
    }

    public Pessoa retornaPessoaCalculo(CalculoTaxasDiversas calculo) {
        Pessoa retorno = null;
        String hql = "";
        if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
            retorno = ((CadastroEconomico) calculo.getCadastro()).getPessoa();
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            retorno = calculo.getContribuinte();
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
            hql = " select  prop.pessoa               "
                + "    from CadastroImobiliario ci    "
                + "   inner join ci.propriedade prop  "
                + " where ci = :cadastro              "
                + " order by prop.proporcao desc      ";
            Query q = em.createQuery(hql);
            List<Propriedade> listaPropriedade = cadastroImobiliarioFacade.recuperarProprietariosVigentes((CadastroImobiliario) calculo.getCadastro());
            if (listaPropriedade != null) {
                retorno = listaPropriedade.get(0).getPessoa();
            }
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            hql = " select  pr.pessoa                  "
                + "    from CadastroRural cr       "
                + "   inner join cr.propriedade pr "
                + " where cr = :cadastro           "
                + " order by pr.proporcao desc     ";
            Query q = em.createQuery(hql);
            List<PropriedadeRural> listaPropriedadeRural = cadastroRuralFacade.recuperarPropriedadesVigentes((CadastroRural) calculo.getCadastro());
            if (listaPropriedadeRural != null) {
                retorno = listaPropriedadeRural.get(0).getPessoa();
            }
        }
        if (retorno != null) {
            retorno = em.find(Pessoa.class, retorno.getId());
        }
        return retorno;
    }

    public String incluiTexto(String texto, int tamanho, boolean alinhaEsquerda) {
        String retorno = null;
        if (texto == null || tamanho < 0) {
            return "";
        }
        if (texto.length() >= tamanho) {
            return texto.substring(0, tamanho);
        } else {
            int diferenca = tamanho - texto.length();
            String espacos = "";
            for (int i = 0; i < diferenca; i++) {
                espacos += " ";
            }
            if (alinhaEsquerda) {
                retorno = texto + espacos;
            } else {
                retorno = espacos + texto;
            }
        }
        return retorno;
    }

    public List<CalculoTaxasDiversas> filtrarPorPessoa(String filtro) {
        String hql = "  select calculo from CalculoTaxasDiversas calculo "
            + " join calculo.pessoas cp"
            + " where cp.pessoa in "
            + " ( select obj"
            + " from PessoaFisica obj "
            + " where lower(obj.nome) like :filtro "
            + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :filtro) "
            + ")"
            + " or cp.pessoa in ( select obj"
            + " from PessoaJuridica obj "
            + " where lower(obj.razaoSocial) like :filtro "
            + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :filtro) "
            + ")"
            + "or calculo.cadastro in (select c from CadastroImobiliario c where c.inscricaoCadastral like :filtro)"
            + "or calculo.cadastro in (select c from CadastroRural c where to_char(c.codigo) like :filtro)"
            + "or calculo.cadastro in (select c from CadastroEconomico c where c.inscricaoCadastral like :filtro)";
        List resultList = null;
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        resultList = q.getResultList();
        return new ArrayList<>(new HashSet<CalculoTaxasDiversas>(resultList));
    }

    public List<CalculoTaxasDiversas> listaPorFiltro(CalculoTaxasDiversasControlador.PesquisaTaxas pesquisa) {
        String juncao = " where ";
        String hql = "  select calculo from CalculoTaxasDiversas calculo "
            + " join calculo.pessoas cp"
            + " join calculo.exercicio ex";
        if (pesquisa.getCadastro() != null && !pesquisa.getCadastro().isEmpty()) {
            hql += juncao + "( cp.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or cp.pessoa in ( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or calculo.cadastro in (select c from CadastroImobiliario c where c.inscricaoCadastral like :cadastro)"
                + " or calculo.cadastro in (select c from CadastroRural c where to_char(c.codigo) like :cadastro)"
                + " or calculo.cadastro in (select c from CadastroEconomico c where c.inscricaoCadastral like :cadastro))";
            juncao = " and ";
        }
        if (pesquisa.getDataLancamento() != null) {
            hql += juncao + "( to_char(calculo.dataHoraLancamento,'dd/MM/yyyy') = to_char(:lancamento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getDataVencimento() != null) {
            hql += juncao + "( to_char(calculo.vencimento,'dd/MM/yyyy') = to_char(:vencimento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getSituacaoCalculo() != null) {
            hql += juncao + "( calculo.situacao = :situacao)";
            juncao = " and ";
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            hql += juncao + "( calculo.tipoCadastroTributario = :tipoCadastro)";
            juncao = " and ";
        }
        if (pesquisa.getExercicio() != null && pesquisa.getExercicio() > 0) {
            hql += juncao + "( ex.ano = :exercicio)";
            juncao = " and ";
        }
        if (pesquisa.getNumero() != null && pesquisa.getNumero() > 0) {
            hql += juncao + "( calculo.numero = :numero)";
        }

        hql += " order by calculo.dataHoraLancamento desc ex.ano , calculo.numero";
        Query q = em.createQuery(hql);
        if (pesquisa.getCadastro() != null && !pesquisa.getCadastro().isEmpty()) {
            q.setParameter("cadastro", "%" + pesquisa.getCadastro().toLowerCase().trim() + "%");
        }
        if (pesquisa.getDataLancamento() != null) {
            q.setParameter("lancamento", Util.getDataHoraMinutoSegundoZerado(pesquisa.getDataLancamento()));
        }
        if (pesquisa.getDataVencimento() != null) {
            q.setParameter("vencimento", Util.getDataHoraMinutoSegundoZerado(pesquisa.getDataVencimento()));
        }
        if (pesquisa.getSituacaoCalculo() != null) {
            q.setParameter("situacao", pesquisa.getSituacaoCalculo());
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            q.setParameter("tipoCadastro", pesquisa.getTipoCadastroTributario());
        }
        if (pesquisa.getExercicio() != null && pesquisa.getExercicio() > 0) {
            q.setParameter("exercicio", pesquisa.getExercicio());
        }
        if (pesquisa.getNumero() != null && pesquisa.getNumero() > 0) {
            q.setParameter("numero", pesquisa.getNumero());
        }
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> listaPorFiltro(CancelamentoTaxasDiversasControlador.PesquisaTaxas pesquisa) {
        String juncao = " where ";
        String hql = "  select calculo from CalculoTaxasDiversas calculo "
            + " join calculo.pessoas cp"
            + " join calculo.exercicio ex";
        if (pesquisa.getCadastro() != null && !pesquisa.getCadastro().isEmpty()) {
            hql += juncao + "( cp.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or cp.pessoa in ( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or calculo.cadastro in (select c from CadastroImobiliario c where c.inscricaoCadastral like :cadastro)"
                + " or calculo.cadastro in (select c from CadastroRural c where to_char(c.codigo) like :cadastro)"
                + " or calculo.cadastro in (select c from CadastroEconomico c where c.inscricaoCadastral like :cadastro))";
            juncao = " and ";
        }
        if (pesquisa.getDataLancamento() != null) {
            hql += juncao + "( to_char(calculo.dataHoraLancamento,'dd/MM/yyyy') = to_char(:lancamento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getDataVencimento() != null) {
            hql += juncao + "( to_char(calculo.vencimento,'dd/MM/yyyy') = to_char(:vencimento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getSituacaoCalculo() != null) {
            hql += juncao + "( calculo.situacao = :situacao)";
            juncao = " and ";
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            hql += juncao + "( calculo.tipoCadastroTributario = :tipoCadastro)";
            juncao = " and ";
        }
        if (pesquisa.getExercicio() != null && pesquisa.getExercicio() > 0) {
            hql += juncao + "( ex.ano = :exercicio)";
            juncao = " and ";
        }
        if (pesquisa.getNumero() != null && pesquisa.getNumero() > 0) {
            hql += juncao + "( calculo.numero = :numero)";
        }

        hql += " order by calculo.dataHoraLancamento desc ex.ano , calculo.numero";
        Query q = em.createQuery(hql);
        q.setMaxResults(1000);
        if (pesquisa.getCadastro() != null && !pesquisa.getCadastro().isEmpty()) {
            q.setParameter("cadastro", "%" + pesquisa.getCadastro().toLowerCase().trim() + "%");
        }
        if (pesquisa.getDataLancamento() != null) {
            q.setParameter("lancamento", Util.getDataHoraMinutoSegundoZerado(pesquisa.getDataLancamento()));
        }
        if (pesquisa.getDataVencimento() != null) {
            q.setParameter("vencimento", Util.getDataHoraMinutoSegundoZerado(pesquisa.getDataVencimento()));
        }
        if (pesquisa.getSituacaoCalculo() != null) {
            q.setParameter("situacao", pesquisa.getSituacaoCalculo());
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            q.setParameter("tipoCadastro", pesquisa.getTipoCadastroTributario());
        }
        if (pesquisa.getExercicio() != null && pesquisa.getExercicio() > 0) {
            q.setParameter("exercicio", pesquisa.getExercicio());
        }
        if (pesquisa.getNumero() != null && pesquisa.getNumero() > 0) {
            q.setParameter("numero", pesquisa.getNumero());
        }
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorSituacao(SituacaoCalculo filtro) {
        String hql = " select obj from CalculoTaxasDiversas obj where obj.situacao = :filtro order by obj.dataHoraLancamento desc ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", filtro);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorVencimento(Date filtrodata) {
        String hql = " select obj from CalculoTaxasDiversas obj where  obj.vencimento = :filtro order by obj.dataHoraLancamento desc ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", filtrodata);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorDataLancamento(Date filtrodata) {
        String hql = " select obj from CalculoTaxasDiversas obj where  obj.dataHoraLancamento = :filtro order by obj.dataHoraLancamento desc ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", filtrodata);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorTipoDeCadastro(TipoCadastroTributario cadastroTributario) {
        String hql = " select obj from CalculoTaxasDiversas obj where  obj.tipoCadastroTributario = :filtro order by obj.dataHoraLancamento desc ";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", cadastroTributario);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorValoReal(BigDecimal filtro) {
        String sql = "from CalculoTaxasDiversas ctd where ctd.valorReal = :filtro";
        Query q = em.createQuery(sql);
        q.setParameter("filtro", filtro);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> filtrarCalculoTaxasDiversasPorValoUFM(BigDecimal filtro) {
        BigDecimal valorUFMVigente = moedaFacade.recuperaValorVigenteUFM();
        //System.out.println("ufm----------" + valorUFMVigente);
        String hql = "from CalculoTaxasDiversas ctd where ctd.valorReal / :moeda = :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", filtro);
        q.setParameter("moeda", valorUFMVigente);
        return q.getResultList();
    }

    public List<CalculoTaxasDiversas> listaCalculoTaxasDiversas() {
        String hql = "from CalculoTaxasDiversas ";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public ProcessoCalcTaxasDiversas salva(ProcessoCalcTaxasDiversas processo) {
        return em.merge(processo);
    }

    private boolean temPagamento(CalculoTaxasDiversas calculo) {
        Query q = em.createQuery("select i.id " +
                " from ItemLoteBaixaParcela i " +
                " where i.itemDam.parcela.valorDivida.calculo = :calculo " +
                " and (i.itemLoteBaixa.loteBaixa.situacaoLoteBaixa = 'BAIXADO' or i.itemLoteBaixa.loteBaixa.situacaoLoteBaixa = ' BAIXADO_INCONSITENTE')")
            .setParameter("calculo", calculo);
        return !q.getResultList().isEmpty();
    }

    public List<DAM> recuperaDAM(Long id) {
        return em.createQuery("select dam " +
            "                  from ItemDAM " +
            "               item join item.DAM dam " +
            "               where item.parcela.id = :id ").setParameter("id", id).getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<RelacaoTaxasDiversas> buscarRelacaoTaxasDiversas(FiltroRelacaoTaxasDiversas filtro) {
        String sql = "select distinct taxa.id, taxa.numero, " +
            "       e.ano exercicio, " +
            "       taxa.datahoralancamento datarequerimento, " +
            "       d.vencimento datavencimentodam, " +
            "       coalesce(ci.inscricaoCadastral, coalesce(ce.inscricaoCadastral, coalesce(pf.cpf, pj.cnpj))) cadastro, " +
            "       coalesce(pf.nome, pj.razaosocial) requerente, " +
            "       taxa.situacao situacaotaxa, " +
            "       d.situacao situacaodam, " +
            "       us_taxa.login usuariotaxa, " +
            "       us_hdam.login usuariodam, " +
            "       vd.valor totaltaxa " +
            "   FROM CalculoTaxasDiversas taxa " +
            "  INNER JOIN Calculo cal on cal.id = taxa.id " +
            "  INNER join CalculoPessoa cp on cal.id = cp.calculo_id " +
            "  INNER join ItemCalculoTaxasDiversas item ON item.calculoTaxasDiversas_id = taxa.id " +
            "  INNER join TributoTaxaDividasDiversas tributo ON item.tributoTaxaDividasDiversas_id = tributo.id " +
            "  LEFT join PessoaFisica pf on cp.pessoa_id = pf.id " +
            "  LEFT join PessoaJuridica pj on cp.pessoa_id = pj.id " +
            "  LEFT join CadastroEconomico ce on cal.cadastro_id = ce.id " +
            "  LEFT join CadastroImobiliario ci on cal.cadastro_id = ci.id " +
            "  INNER join Exercicio e on taxa.exercicio_id = e.id " +
            "  LEFT join UsuarioSistema us_taxa on us_taxa.id = taxa.usuarioLancamento_id " +
            "  INNER join ValorDivida vd on taxa.id = vd.calculo_id " +
            "  INNER join ParcelaValorDivida pvd on vd.id = pvd.valorDivida_id " +
            "  INNER join ItemDam idam on pvd.id = idam.parcela_id " +
            "  INNER join Dam d on d.id = idam.dam_id " +
            "  INNER join HistoricoImpressaoDam hd on hd.id = (select max(s_hd.id) from HistoricoImpressaoDam s_hd where s_hd.dam_id = d.id) " +
            "  LEFT join UsuarioSistema us_hdam on us_hdam.id = hd.usuariosistema_id " +
            " WHERE d.id = (select max(i.dam_id) from ItemDam i where i.parcela_id = pvd.id)";

        if (filtro.getDataLancamentoInicial() != null) {
            sql += " and trunc(taxa.dataHoraLancamento) >= :dataLancamentoInicial ";
        }

        if (filtro.getDataLancamentoFinal() != null) {
            sql += " and trunc(taxa.dataHoraLancamento) <= :dataLancamentoFinal ";
        }

        if (filtro.getDataVencimentoInicial() != null) {
            sql += " and trunc(d.vencimento) >= :vencimentoInicial ";
        }

        if (filtro.getDataVencimentoFinal() != null) {
            sql += " and trunc(d.vencimento) <= :vencimentoFinal ";
        }

        if (filtro.getTipoCadastroTributario() != null) {
            sql += " and taxa.tipoCadastroTributario = :tipoCadastro ";

            if (filtro.getCadastroInicial() != null && !filtro.getCadastroInicial().trim().isEmpty()) {
                sql += " and coalesce(ci.inscricaoCadastral, coalesce(ce.inscricaoCadastral, coalesce(pf.cpf, pj.cnpj))) >= :cadastroInicial ";
            }

            if (filtro.getCadastroFinal() != null && !filtro.getCadastroFinal().trim().isEmpty()) {
                sql += " and coalesce(ci.inscricaoCadastral, coalesce(ce.inscricaoCadastral, coalesce(pf.cpf, pj.cnpj))) <= :cadastroFinal ";
            }
        }

        if (filtro.getTributoTaxaDividasDiversas() != null) {
            sql += " and tributo.id = :tributoTaxa ";
        }

        if (filtro.getNomeInicial() != null && !filtro.getNomeInicial().trim().isEmpty()) {
            sql += " and coalesce(pf.nome, pj.razaoSocial) <= :nomeInicial ";
        }

        if (filtro.getNomeFinal() != null && !filtro.getNomeFinal().trim().isEmpty()) {
            sql += " and coalesce(pf.nome, pj.razaoSocial) >= :nomeFinal ";
        }

        if (filtro.getSituacaoCalculo() != null) {
            sql += " and taxa.situacao = :situacaoCalculo ";
        }

        if (filtro.getSituacaoDAM() != null) {
            sql += " and d.situacao = :situacaoDam ";
        }

        if (filtro.getUsuarioTaxa() != null && filtro.getUsuarioTaxa().getId() != null) {
            sql += " and us_taxa.id = :usuarioTaxa ";
        }

        if (filtro.getUsuarioDAM() != null) {
            sql += " and us_hdam.id = :usuarioDam ";
        }

        if (filtro.getNumeroTaxaInicial() != null && !filtro.getNumeroTaxaInicial().trim().isEmpty()) {
            sql += " and taxa.numero >= :numeroInicial ";
        }

        if (filtro.getNumeroTaxaFinal() != null && !filtro.getNumeroTaxaFinal().trim().isEmpty()) {
            sql += " and taxa.numero <= :numeroFinal ";
        }

        Query q = em.createNativeQuery(sql);
        if (filtro.getDataLancamentoInicial() != null) {
            q.setParameter("dataLancamentoInicial", filtro.getDataLancamentoInicial());
        }

        if (filtro.getDataLancamentoFinal() != null) {
            q.setParameter("dataLancamentoFinal", filtro.getDataLancamentoFinal());
        }

        if (filtro.getDataVencimentoInicial() != null) {
            q.setParameter("vencimentoInicial", filtro.getDataVencimentoInicial());
        }

        if (filtro.getDataVencimentoFinal() != null) {
            q.setParameter("vencimentoFinal", filtro.getDataVencimentoFinal());
        }

        if (filtro.getTipoCadastroTributario() != null) {
            q.setParameter("tipoCadastro", filtro.getTipoCadastroTributario().name());

            if (filtro.getCadastroInicial() != null && !filtro.getCadastroInicial().trim().isEmpty()) {
                q.setParameter("cadastroInicial", filtro.getCadastroInicial());
            }
            if (filtro.getCadastroFinal() != null && !filtro.getCadastroFinal().trim().isEmpty()) {
                q.setParameter("cadastroFinal", filtro.getCadastroFinal());
            }
        }

        if (filtro.getTributoTaxaDividasDiversas() != null) {
            q.setParameter("tributoTaxa", filtro.getTributoTaxaDividasDiversas().getId());
        }

        if (filtro.getNomeInicial() != null && !filtro.getNomeInicial().trim().isEmpty()) {
            q.setParameter("nomeInicial", filtro.getNomeInicial());
        }

        if (filtro.getNomeFinal() != null && !filtro.getNomeFinal().trim().isEmpty()) {
            q.setParameter("nomeFinal", filtro.getNomeFinal());
        }

        if (filtro.getSituacaoCalculo() != null) {
            q.setParameter("situacaoCalculo", filtro.getSituacaoCalculo().name());
        }

        if (filtro.getSituacaoDAM() != null) {
            q.setParameter("situacaoDam", filtro.getSituacaoDAM().name());
        }

        if (filtro.getUsuarioTaxa() != null && filtro.getUsuarioTaxa().getId() != null) {
            q.setParameter("usuarioTaxa", filtro.getUsuarioTaxa().getId());
        }

        if (filtro.getUsuarioDAM() != null) {
            q.setParameter("situacaoDam", filtro.getUsuarioDAM().getId());
        }

        if (filtro.getNumeroTaxaInicial() != null && !filtro.getNumeroTaxaInicial().trim().isEmpty()) {
            q.setParameter("numeroInicial", filtro.getNumeroTaxaInicial());
        }

        if (filtro.getNumeroTaxaFinal() != null && !filtro.getNumeroTaxaFinal().trim().isEmpty()) {
            q.setParameter("numeroFinal", filtro.getNumeroTaxaFinal());
        }
        List<Object[]> lista = q.getResultList();
        List<RelacaoTaxasDiversas> relacao = Lists.newArrayList();
        for (Object[] obj : lista) {
            RelacaoTaxasDiversas rel = new RelacaoTaxasDiversas();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setNumero(((BigDecimal) obj[1]).longValue());
            rel.setExercicio(((BigDecimal) obj[2]).intValue());
            rel.setDataRequerimento((Date) obj[3]);
            rel.setDataVencimentoDam((Date) obj[4]);
            rel.setCadastro((String) obj[5]);
            rel.setRequerente((String) obj[6]);
            rel.setSituacaoTaxa(SituacaoCalculo.valueOf((String) obj[7]).getDescricao());
            rel.setSituacaoDam(DAM.Situacao.valueOf((String) obj[8]).getDescricao());
            rel.setUsuarioTaxa((String) obj[9]);
            rel.setUsuarioDam((String) obj[10]);
            rel.setTotalTaxa((BigDecimal) obj[11]);
            rel.setTributos(buscarItensRelacaoTaxasDiversas(rel.getId()));
            relacao.add(rel);
        }
        return relacao;
    }

    private List<RelacaoTaxasDiversas.ItemRelacaoTaxasDiversas> buscarItensRelacaoTaxasDiversas(Long id) {
        String sql = "SELECT TRIBUTOTAXA.id, TRIBUTO.DESCRICAO FROM CALCULOTAXASDIVERSAS CALCULO " +
            "  INNER JOIN ITEMCALCULOTAXASDIVERSAS ITEMCALCULO ON ITEMCALCULO.CALCULOTAXASDIVERSAS_ID = CALCULO.ID " +
            "  INNER JOIN TRIBUTOTAXADIVIDASDIVERSAS TRIBUTOTAXA ON ITEMCALCULO.TRIBUTOTAXADIVIDASDIVERSAS_ID = TRIBUTOTAXA.ID " +
            "  INNER JOIN TRIBUTO ON TRIBUTOTAXA.TRIBUTO_ID = TRIBUTO.ID " +
            "  where calculo.id = :idCalculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCalculo", id);
        List<Object[]> lista = q.getResultList();
        List<RelacaoTaxasDiversas.ItemRelacaoTaxasDiversas> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            RelacaoTaxasDiversas.ItemRelacaoTaxasDiversas item = new RelacaoTaxasDiversas.ItemRelacaoTaxasDiversas();
            item.setDescricao((String) obj[1]);
            retorno.add(item);
        }
        return retorno;
    }

    public Date calcularVencimento(Date hoje) {
        Calendar c = Calendar.getInstance();
        c.setTime(hoje);
        c.add(Calendar.DATE, 1);
        if (DataUtil.ehDiaNaoUtil(c.getTime(), feriadoFacade)) {
            calcularVencimento(c.getTime());
        }
        return c.getTime();
    }

}
