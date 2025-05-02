/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AutorizacaoTributario;
import br.com.webpublico.enums.SituacaoCalculoDividaDiversa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wellington
 */
@Stateless
public class CalculoDividaDiversaFacade extends AbstractFacade<CalculoDividaDiversa> {

    private static final int TAMANHO_DESCRICAO_VALOR = 60;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GeraValorDividaDividasDiversas geraDebito;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DAMFacade dAMFacade;
    @EJB
    private TipoDividaDiversaFacade tipoDividaDiversaFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private TributoTaxasDividasDiversasFacade tributoTaxasDividasDiversasFacade;
    @EJB
    private ParametroTipoDividaDiversaFacade parametroTipoDividaDiversaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public CalculoDividaDiversaFacade() {
        super(CalculoDividaDiversa.class);
    }

    public Integer retornoProximoNumero(Exercicio exercicio) {
        Query q = em.createNativeQuery(" SELECT coalesce(max(l.numerolancamento),0) + 1 FROM calculodividadiversa l "
            + " WHERE l.exercicio_id =:exercicio_id ");
        q.setParameter("exercicio_id", exercicio.getId());
        if (q.getSingleResult() == null) {
            return 1;
        } else {
            BigDecimal retorno = (BigDecimal) q.getSingleResult();
            return retorno.intValueExact();
        }
    }

    public String montaDescricaoCadastro(CalculoDividaDiversa calculoDividaDiversa) {
        String retorno = "";
        Query q = em.createNativeQuery(" SELECT CASE ldd.tipocadastrotributario "
            + "           WHEN 'IMOBILIARIO' THEN ci.inscricaocadastral||' (Cadastro Imobiliário) ' "
            + "           WHEN 'RURAL' THEN cr.codigo||' (Cadastro Rural) ' "
            + "           WHEN 'ECONOMICO' THEN ce.inscricaocadastral||' (Cadastro Mobiliário) ' "
            + "           WHEN 'PESSOA' THEN coalesce(pf.nome,pj.razaosocial)||' (Contribuinte Geral) ' "
            + "        END AS retorno "
            + "    FROM calculodividadiversa ldd "
            + "   INNER JOIN Calculo cal on cal.id = ldd.id "
            + "   LEFT JOIN pessoa p ON ldd.pessoa_id = p.id "
            + "   LEFT JOIN pessoafisica pf ON p.id = pf.id "
            + "   LEFT JOIN pessoajuridica pj ON p.id = pj.id "
            + "   LEFT JOIN cadastroimobiliario ci ON cal.cadastro_id = ci.id "
            + "   LEFT JOIN cadastroeconomico ce ON cal.cadastro_id = ce.id "
            + "   LEFT JOIN cadastrorural cr ON cal.cadastro_id = cr.id "
            + " WHERE ldd.id = :id");
        q.setParameter("id", calculoDividaDiversa.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            retorno = resultList.get(0).toString();
        }
        return retorno;
    }

    public ProcessoCalcDivDiversas processaCalculo(CalculoDividaDiversa calculo) throws ExcecaoNegocioGenerica {
        ProcessoCalcDivDiversas processo;
        if (calculo.getSituacao().equals(SituacaoCalculoDividaDiversa.NOVO)) {
            processo = criaProcesso(calculo);
            calculo.setSituacao(SituacaoCalculoDividaDiversa.EM_ABERTO);
            processo = em.merge(processo);
        } else {
            calculo = em.merge(calculo);
            processo = recuperarProcesso(calculo.getProcessoCalcDivDiversas().getId());
        }
        return processo;
    }

    private ProcessoCalcDivDiversas criaProcesso(CalculoDividaDiversa calculo) throws ExcecaoNegocioGenerica {
        ProcessoCalcDivDiversas processo = new ProcessoCalcDivDiversas();
        processo.setDataLancamento(calculo.getDataCalculo());
        processo.setExercicio(calculo.getExercicioDoDebito() != null ? calculo.getExercicioDoDebito() : calculo.getExercicio());
        if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
            processo.setDescricao("Dívidas Diversas - Cadastro Imobiliário: " + ((CadastroImobiliario) calculo.getCadastro()).getInscricaoCadastral());
            processo.setDivida(calculo.getTipoDividaDiversa().getDividaCadastroImobiliario());
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
            processo.setDescricao("Dívidas Diversas - Cadastro Mobiliário: " + ((CadastroEconomico) calculo.getCadastro()).getInscricaoCadastral());
            processo.setDivida(calculo.getTipoDividaDiversa().getDividaCadastroEconomico());
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            processo.setDescricao("Dívidas Diversas - Cadastro Rural: " + ((CadastroRural) calculo.getCadastro()).getNumeroCadastro());
            processo.setDivida(calculo.getTipoDividaDiversa().getDividaCadastroRural());
        } else {
            processo.setDescricao("Dívidas Diversas - Contribuinte Geral: " + calculo.getPessoa().getNome());
            processo.setDivida(calculo.getTipoDividaDiversa().getDividaContribuinteGeral());
        }
        if (processo.getDivida() == null) {
            throw new ExcecaoNegocioGenerica("A Dívida de " + calculo.getTipoCadastroTributario().getDescricaoLonga() + " no Tipo de Dívida Diversa não foi configurada.");
        }
        if (processo.getDescricao().length() > 70) {
            processo.setDescricao(processo.getDescricao().substring(0, 69));
        }
        processo.setCalculos(new ArrayList<CalculoDividaDiversa>());
        calculo.setProcessoCalcDivDiversas(processo);
        if (calculo.getNumeroLancamento() == null) {
            calculo.setNumeroLancamento(singletonGeradorCodigo.getProximoCodigo(CalculoDividaDiversa.class, "numeroLancamento").intValue());
        }
        processo.getCalculos().add(calculo);
        return processo;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public CalculoDividaDiversa recuperar(Object id) {
        CalculoDividaDiversa l = em.find(CalculoDividaDiversa.class, id);
        l.getItens().size();
        l.getPessoas().size();
        l.getArquivoCalcDividaDiversas().size();
        return l;
    }

    public ProcessoCalcDivDiversas recuperarProcesso(Object id) {
        ProcessoCalcDivDiversas p = em.find(ProcessoCalcDivDiversas.class, id);
        p.getCalculos().size();
        return p;
    }

    @Override
    public List<CalculoDividaDiversa> lista() {
        String hql = "from CalculoDividaDiversa obj order by obj.exercicio.ano desc, obj.numeroLancamento desc ";
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();
    }

    public CalculoDividaDiversa salva(CalculoDividaDiversa entity) {
        CalculoDividaDiversa c = em.merge(entity);
        c.getItens().size();
        return c;
    }

    public ProcessoCalcDivDiversas recuperaProcesso(ProcessoCalcDivDiversas processo) {
        ProcessoCalcDivDiversas p = em.find(ProcessoCalcDivDiversas.class, processo.getId());
        p.getCalculos().size();
        for (CalculoDividaDiversa calculo : p.getCalculos()) {
            p.getCalculos().set(p.getCalculos().indexOf(calculo), super.recuperar(calculo.getId()));
        }
        return p;
    }

    public ValorDivida retornaValorDividaDoCalculo(CalculoDividaDiversa calculo) {
        ValorDivida retorno = null;
        Query q = em.createQuery(" from ValorDivida vd where vd.calculo.id = :calculo");
        q.setParameter("calculo", calculo.getId());
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = (ValorDivida) q.getResultList().get(0);
            q = em.createQuery("from Calculo c where c.id = :calculo_id");
            q.setParameter("calculo_id", calculo.getId());
            if (q.getResultList() != null && q.getResultList().size() > 0) {
                retorno.setCalculo((Calculo) q.getResultList().get(0));
            }
        }

        retorno.getParcelaValorDividas().size();
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
        String valorString = Util.formataNumero(valor);
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
        sb.append(" ");
        return sb.toString();
    }

    public String montaDescricaoLonga(ValorDivida aImprimir) throws UFMException {
        StringBuilder descricao = new StringBuilder();
        //DecimalFormat formatNumber = new DecimalFormat("#,###,##0.00");
        CalculoDividaDiversa calculo = em.find(CalculoDividaDiversa.class, aImprimir.getCalculo().getId());

        descricao.append(
            "TRIBUTOS: \n\n");
        for (ItemCalculoDivDiversa item : calculo.getItens()) {
            descricao.append(adicionaValor((item.getTributoTaxaDividasDiversas().getTributo().getDescricao().toUpperCase()), item.getValorReal(), "R$", false)).append("\n");
        }
        descricao.append("\n");
        descricao.append(adicionaValor("VALOR TOTAL", calculo.getValorReal(), "R$", false)).append("\n");
        descricao.append("\nOBSERVAÇÃO: ");
        descricao.append("\n\n");
        if (calculo.getObservacao() != null) {
            descricao.append(calculo.getObservacao());
        }
        return descricao.toString();
    }

    public String montaDescricaoCurta(ValorDivida aImprimir) throws UFMException {
        DecimalFormat formatNumber = new DecimalFormat("#,###,##0.00");
        StringBuilder descricao = new StringBuilder();
        CalculoDividaDiversa calculo = em.find(CalculoDividaDiversa.class, aImprimir.getCalculo().getId());
        descricao.append("\nVALOR TOTAL R$").append(formatNumber.format(calculo.getValorReal()));
        return descricao.toString();
    }

    public Pessoa retornaPessoaCalculo(CalculoDividaDiversa calculo) {
        Pessoa retorno = null;
        String hql = "";
        if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
            retorno = ((CadastroEconomico) calculo.getCadastro()).getPessoa();
        } else if (calculo.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            retorno = calculo.getPessoa();
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

    public List<CalculoDividaDiversa> listaCalculosDisponiveisParaCancelamento() {
        String hql = " from CalculoDividaDiversa calc "
            + " where calc.situacao = 'EFETIVADO' order by calc.id desc ";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        return q.getResultList();
    }

    private ValorDivida recuperarValorDivida(ValorDivida valorDivida) {
        valorDivida = this.em.find(ValorDivida.class, valorDivida.getId());
        valorDivida.getParcelaValorDividas().size();
        return valorDivida;
    }

    public void cancelaParcelasValorDivida(ValorDivida valorDivida) {
        valorDivida = recuperarValorDivida(valorDivida);
        List<ParcelaValorDivida> listaParcelas = valorDivida.getParcelaValorDividas();
        for (ParcelaValorDivida parcelaValorDivida : listaParcelas) {
            parcelaValorDivida.getSituacoes().add(new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcelaValorDivida, parcelaValorDivida.getValor()));
            em.persist(parcelaValorDivida);
        }
    }

    public boolean permitirCancelamento(ValorDivida valorDivida) {
        valorDivida = recuperarValorDivida(valorDivida);
        List<ParcelaValorDivida> listaParcelas = valorDivida.getParcelaValorDividas();
        if (!listaParcelas.isEmpty()) {
            for (ParcelaValorDivida parcelaValorDivida : listaParcelas) {
                SituacaoParcelaValorDivida ultimaSituacao = parcelaValorDivida.getUltimaSituacao();
                if (SituacaoParcela.PAGO.equals(ultimaSituacao.getSituacaoParcela())
                    || SituacaoParcela.PAGO_PARCELAMENTO.equals(ultimaSituacao.getSituacaoParcela())
                    || SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.equals(ultimaSituacao.getSituacaoParcela())
                    || SituacaoParcela.PAGO_SUBVENCAO.equals(ultimaSituacao.getSituacaoParcela())) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<ValorDivida> getValorDividaPorCalculo(CalculoDividaDiversa calculoDividaDiversa) {
        String hql = "select vd from ValorDivida vd where vd.calculo = :calculoDividaDiversa";
        Query q = em.createQuery(hql);
        q.setParameter("calculoDividaDiversa", calculoDividaDiversa);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<ValorDivida>();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public GeraValorDividaDividasDiversas getGeraDebito() {
        return geraDebito;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public DAMFacade getdAMFacade() {
        return dAMFacade;
    }

    public TipoDividaDiversaFacade getTipoDividaDiversaFacade() {
        return tipoDividaDiversaFacade;
    }

    public CadastroFacade getCadastroFacade() {
        return cadastroFacade;
    }

    public TributoTaxasDividasDiversasFacade getTributoTaxasDividasDiversasFacade() {
        return tributoTaxasDividasDiversasFacade;
    }

    public ParametroTipoDividaDiversaFacade getParametroTipoDividaDiversaFacade() {
        return parametroTipoDividaDiversaFacade;
    }

    public List buscarListaDeCalculoDividaDiversaParaCancelamentoPorPesquisaDivida(PesquisaDividas pesquisa) {
        String juncao = " where ";
        String hql = "  select calculo from CalculoDividaDiversa calculo ";

        if (pesquisa.getCadastro() != null && !pesquisa.getCadastro().isEmpty()) {
            hql += juncao + "("
                + "( calculo.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or calculo.pessoa in "
                + "( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + ")"
                + " or calculo.cadastro in "
                + "("
                + " select c from CadastroImobiliario c left join c.propriedade p  "
                + " where lower(c.inscricaoCadastral) like :cadastro"
                + " or p.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or p.pessoa in "
                + "( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + ")"
                + " or calculo.cadastro in "
                + "("
                + " select c from CadastroRural c left join c.propriedade p  where to_char(c.codigo) like :cadastro "
                + " or p.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or p.pessoa in "
                + "( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + ")"
                + " or calculo.cadastro in "
                + "("
                + " select c from CadastroEconomico c where c.inscricaoCadastral like :cadastro"
                + " or c.pessoa in "
                + " ( select obj"
                + " from PessoaFisica obj "
                + " where lower(obj.nome) like :cadastro "
                + " or (replace(replace(replace(obj.cpf,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + " or c.pessoa in "
                + "( select obj"
                + " from PessoaJuridica obj "
                + " where lower(obj.razaoSocial) like :cadastro "
                + " or (replace(replace(replace(obj.cnpj,'.',''),'-',''),'/','') like :cadastro) "
                + " )"
                + ")"
                + ")";
            juncao = " and ";
        }
        if (pesquisa.getDataLancamento() != null) {
            hql += juncao + "( to_char(calculo.dataLancamento,'dd/MM/yyyy') = to_char(:lancamento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getDataVencimento() != null) {
            hql += juncao + "( to_char(calculo.dataVencimento,'dd/MM/yyyy') = to_char(:vencimento,'dd/MM/yyyy'))";
            juncao = " and ";
        }
        if (pesquisa.getExercicio() != null) {
            hql += juncao + " calculo.exercicio.ano = :exercicio ";
            juncao = " and ";
        }
        if (pesquisa.getNumero() != null) {
            hql += juncao + " calculo.numeroLancamento = :numeroProcessoProtocolo ";
            juncao = " and ";
        }
        if (pesquisa.getTipoDivida() != null) {
            hql += juncao + " calculo.tipoDividaDiversa = :tipoDivida ";
            juncao = " and ";
        }
        if (pesquisa.getSituacaoCalculoDividaDiversa() != null) {
            hql += juncao + "( calculo.situacao not in ('ESTORNADO', 'CANCELADO'))";
            juncao = " and ";
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            hql += juncao + "( calculo.tipoCadastroTributario = :tipoCadastro)";
        }
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
        if (pesquisa.getExercicio() != null) {
            q.setParameter("exercicio", pesquisa.getExercicio());
        }
        if (pesquisa.getTipoDivida() != null) {
            q.setParameter("tipoDivida", pesquisa.getTipoDivida());
        }
        if (pesquisa.getNumero() != null) {
            q.setParameter("numeroProcessoProtocolo", pesquisa.getNumero());
        }
        if (pesquisa.getTipoCadastroTributario() != null) {
            q.setParameter("tipoCadastro", pesquisa.getTipoCadastroTributario());
        }

        List toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return buscarCalculosSemParcelasPagas(toReturn);
        }
        return toReturn;
    }

    public List buscarCalculosSemParcelasPagas(List<Calculo> calculos) {
        List toReturn = new ArrayList<>(calculos);
        List<Long> ids = new ArrayList<>();
        for (Calculo calc : calculos) {
            ids.add(calc.getId());
        }

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IN, ids);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, Lists.newArrayList(SituacaoParcela.PAGO, SituacaoParcela.PAGO_SUBVENCAO));
        consulta.executaConsulta();

        List<ResultadoParcela> resultado = consulta.getResultados();
        if (!resultado.isEmpty()) {
            for (ResultadoParcela res : resultado) {
                for (Calculo calc : calculos) {
                    if (res.getIdCalculo().equals(calc.getId())) {
                        toReturn.remove(calc);
                    }
                }
            }
        }
        return toReturn;
    }


    public CalculoDividaDiversa efetiva(CalculoDividaDiversa calculoDividaDiversa) throws Exception {
        calculoDividaDiversa = salva(calculoDividaDiversa);

        calculoDividaDiversa.setUsuarioEfetivacao(sistemaFacade.getUsuarioCorrente());
        calculoDividaDiversa.setDataEfetivacao(new Date());
        calculoDividaDiversa.setSituacao(SituacaoCalculoDividaDiversa.EFETIVADO);
        return salva(calculoDividaDiversa);
    }

    public List<DAM> recuperaDAM(Long id) {
        return em.createQuery("select dam " +
            "                  from ItemDAM " +
            "               item join item.DAM dam " +
            "               where item.parcela.id = :id ").setParameter("id", id).getResultList();
    }

    public List<CalculoDividaDiversa> buscarCalculoDividaDiversaPorNumero(String parte) {
        if (StringUtil.retornaApenasNumeros(parte).isEmpty()) {
            return Lists.newArrayList();
        }
        String hql = "select divida \n" +
            "   from CalculoDividaDiversa divida " +
            "where divida.numeroLancamento = :numero " +
            "order by divida.exercicio.ano desc, divida.numeroLancamento desc ";

        Query q = em.createQuery(hql);
        q.setParameter("numero", Integer.valueOf(StringUtil.retornaApenasNumeros(parte)));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public boolean hasPermissaoParaLancarDividaDiversaExercicioAnterior() {
        return usuarioSistemaFacade.validaAutorizacaoUsuario(
            sistemaFacade.getUsuarioCorrente(),
            AutorizacaoTributario.PERMITIR_LANCAMENTO_DIVIDA_DIVERSA_EXERCICIOS_ANTERIORES);
    }

    public List<Exercicio> listarFiltrandoExerciciosAtualPassados(String parte) {
        return exercicioFacade.listaFiltrandoExerciciosAtualPassados(parte);
    }
}
