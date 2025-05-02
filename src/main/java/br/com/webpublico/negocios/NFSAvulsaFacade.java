/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.BarCode;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroCancelamentoNFSa;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.tributario.EmissaoNotaAvulsaDTO;
import br.com.webpublico.tributario.GeraAliquotaNotaAvulsaDTO;
import br.com.webpublico.tributario.ItemNotaAvulsaDTO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.GeradorChaveAutenticacao;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSNFSAvulsa;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.codec.binary.Base64;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class NFSAvulsaFacade extends CalculoExecutorDepoisDePagar<NFSAvulsa> {

    private static final Logger logger = LoggerFactory.getLogger(NFSAvulsaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private GeraValorDividaNFSAvulsa geraDebito;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private DAMFacade DAMFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ConsultaAutenticidadeNFSAvulsaFacade consultaAutenticidadeNFSAvulsaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SingletonGeradorCodigoTributario singletonGeradorCodigoTributario;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;

    public NFSAvulsaFacade() {
        super(NFSAvulsa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GeraValorDividaNFSAvulsa getGeraDebito() {
        return geraDebito;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public DAMFacade getDAMFacade() {
        return DAMFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public ConsultaAutenticidadeNFSAvulsaFacade getConsultaAutenticidadeNFSAvulsaFacade() {
        return consultaAutenticidadeNFSAvulsaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public void setUsuarioSistemaFacade(UsuarioSistemaFacade usuarioSistemaFacade) {
        this.usuarioSistemaFacade = usuarioSistemaFacade;
    }

    @Override
    public NFSAvulsa recuperar(Object id) {
        NFSAvulsa nf = super.recuperar(id);
        nf.getItens().size();
        definirNumeroAutenticidade(nf);
        return nf;
    }


    public NFSAvulsa atualizaNFSAvulsa(NFSAvulsa nota) {
        return em.merge(nota);
    }


    public NFSAvulsa gerarNfsAvulsaAndDebito(NFSAvulsa nfsAvulsa) throws Exception {
        if (nfsAvulsa.getNumero() == null) {
            nfsAvulsa.setNumero(singletonGeradorCodigoTributario.getProximoNumero(nfsAvulsa.getExercicio(), NFSAvulsa.class, "numero", "exercicio_id"));
        }
        nfsAvulsa = isentarIss(nfsAvulsa);
        nfsAvulsa = em.merge(nfsAvulsa);
        if (nfsAvulsa.getValorTotalIss().compareTo(BigDecimal.ZERO) > 0) {
            geraDebito.geraDebito(criaCalculo(nfsAvulsa).getProcesso());
        }
        return nfsAvulsa;
    }

    private NFSAvulsa isentarIss(NFSAvulsa nfsAvulsa) {
        if (nfsAvulsa.getValorTotalIss().compareTo(BigDecimal.ZERO) > 0
            && TipoTomadorPrestadorNF.ECONOMICO.equals(nfsAvulsa.getTipoPrestadorNF())) {
            ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
            if (configuracaoTributario.getValidarIssqnFixo()
                && calculaISSFacade.hasCalculoIssFixoPago(nfsAvulsa.getExercicio(), nfsAvulsa.getCmcPrestador())) {
                nfsAvulsa.setValorTotalIss(BigDecimal.ZERO);
                nfsAvulsa.setSituacao(NFSAvulsa.Situacao.CONCLUIDA);
                return em.merge(nfsAvulsa);
            }
        }
        return nfsAvulsa;
    }

    public ValorDivida getValorDivida(NFSAvulsa selecionado) throws UFMException, ExcecaoNegocioGenerica {
        CalculoNFSAvulsa calculo = null;
        calculo = recuperaCalculoPelaNota(selecionado);
        ValorDivida vd = null;
        if (calculo != null) {
            vd = recuperaValorDivida(calculo);

        }
        return vd;
    }

    public CalculoNFSAvulsa recuperaCalculoPelaNota(NFSAvulsa selecionado) {
        Query q = em.createQuery("select calculo from CalculoNFSAvulsa calculo where calculo.nfsAvulsa = :nota");
        q.setParameter("nota", selecionado);
        if (q.getResultList().size() > 0) {
            return (CalculoNFSAvulsa) q.getResultList().get(0);
        }
        return null;
    }

    private ValorDivida recuperaValorDivida(CalculoNFSAvulsa calculo) {
        Query q2 = em.createQuery("select vd from ValorDivida vd where vd.calculo = :calculo");
        q2.setParameter("calculo", calculo);
        if (q2.getResultList().size() > 0) {
            ValorDivida vd = (ValorDivida) q2.getResultList().get(0);
            vd.getParcelaValorDividas().size();
            return vd;
        }
        return null;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public void setConfiguracaoTributarioFacade(ConfiguracaoTributarioFacade configuracaoTributarioFacade) {
        this.configuracaoTributarioFacade = configuracaoTributarioFacade;
    }

    private CalculoNFSAvulsa criaCalculo(NFSAvulsa selecionado) throws UFMException, ExcecaoNegocioGenerica {
        Tributo tributo = configuracaoTributarioFacade.retornaUltimo().getTributoNFSAvulsa();
        Divida divida;
        if (selecionado.getCmcPrestador() != null) {
            divida = configuracaoTributarioFacade.retornaUltimo().getDividaNFSAvulsa();
        } else {
            divida = configuracaoTributarioFacade.retornaUltimo().getDividaNFSAvulsaPessoa();
        }
        if (tributo == null || divida == null) {
            throw new ValidacaoException("Verifique a Configuração do Tributário. O Tributo ou a Dívida parametrizadas para a Nota Fiscal de Serviços Avulsa não foram informados.");
        }
        CalculoNFSAvulsa calculo = new CalculoNFSAvulsa();
        calculo.setCadastro(selecionado.getCmcPrestador());
        calculo.setDataCalculo(selecionado.getEmissao());
        calculo.setIsento(Boolean.FALSE);
        calculo.setNfsAvulsa(selecionado);
        calculo.setTributo(tributo);
        calculo.getPessoas().add(criaCalculoPessoa(selecionado.getPrestador(), calculo));
        calculo.setValorEfetivo(selecionado.getValorTotalIss());
        calculo.setValorReal(selecionado.getValorTotalIss());
        ProcessoCalculoNFSAvulsa processoCalculo = new ProcessoCalculoNFSAvulsa();
        processoCalculo.setCompleto(Boolean.TRUE);
        processoCalculo.setDataLancamento(selecionado.getEmissao());
        processoCalculo.setDescricao("Nota Fiscal de Serviços Avulsa");
        processoCalculo.setExercicio(selecionado.getExercicio());
        calculo.setProcesso(processoCalculo);
        processoCalculo.getCalculos().add(calculo);
        processoCalculo.setDivida(divida);
        processoCalculo = em.merge(processoCalculo);
        return processoCalculo.getCalculos().get(0);
    }

    private CalculoPessoa criaCalculoPessoa(Pessoa prestador, Calculo calculo) {
        CalculoPessoa calculoPessoa = new CalculoPessoa();
        calculoPessoa.setCalculo(calculo);
        if (prestador != null) {
            calculoPessoa.setPessoa(prestador);
        } else {
            calculoPessoa.setPessoa(((CadastroEconomico) calculo.getCadastro()).getPessoa());
        }

        return calculoPessoa;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public boolean anuidadeGeradaEPaga(CalculoISS calculo) {
        if (calculo != null) {
            boolean retorno = todasDividasParaOCalculoEstaoEmDia(calculo);
            return retorno;
        }
        return false;
    }

    public CalculoISS temCalculoISSFixoParaEsteAno(CadastroEconomico cmcPrestador) {
        String hql = "select calculo from CalculoISS calculo, ValorDivida vd " + " where calculo.cadastroEconomico = :cmc " + "   and calculo.tipoCalculoISS = :tipoISS " + "   and calculo.processoCalculoISS.exercicio = :ano " + "   and vd.calculo = calculo";
        Query q = em.createQuery(hql);
        q.setParameter("cmc", cmcPrestador);
        q.setParameter("tipoISS", TipoCalculoISS.FIXO);
        q.setParameter("ano", sistemaFacade.getExercicioCorrente());
        List<CalculoISS> listaCalculoISS = q.getResultList();
        if (listaCalculoISS.isEmpty()) {
            return null;
        }
        return listaCalculoISS.get(0);
    }

    public List<Long> recuperaIdsDividasDeParcelamento(Divida divida) {
        String sql = "select distinct param.dividaparcelamento_id from paramparcelamento param " +
            " inner join paramparcelamentodivida paramdiv on paramdiv.paramparcelamento_id = param.id " +
            " where paramdiv.divida_id in (:iddivida, :iddividaativa)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("iddivida", divida.getId());
        q.setParameter("iddividaativa", divida.getDivida().getId());
        return q.getResultList();
    }

    public Divida recuperaDividaCalculo(Calculo calculo) {
        Query q = em.createQuery(" select divida from ValorDivida vd " + " inner join vd.divida divida " + " where vd.calculo =:calculo").setParameter("calculo", calculo);

        return (Divida) q.getResultList().get(0);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public boolean todasDividasParaOCalculoEstaoEmDia(CalculoISS calculo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, calculo.getCadastroEconomico().getId());
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, montarIdsDividasDoCalculo(calculo));
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Operador.MENOR, sistemaFacade.getDataOperacao());
        consultaParcela.executaConsulta();
        return consultaParcela.getResultados().isEmpty();
    }

    private List<Long> montarIdsDividasDoCalculo(CalculoISS calculo) {
        Divida dividaCalculo = recuperaDividaCalculo(calculo);
        List<Long> idsDividas = recuperaIdsDividasDeParcelamento(dividaCalculo);
        idsDividas.add(dividaCalculo.getId());
        return idsDividas;
    }

    public boolean placaIgual(CadastroEconomico cmcPrestador, String placa) {
        String hql = "select placa from PlacaAutomovelCmc placa where placa.cadastroEconomico = :cmc and replace(placa.placa, '-', '') = :placa";
        Query q = em.createQuery(hql);
        q.setParameter("placa", placa.replace("-", ""));
        q.setParameter("cmc", cmcPrestador);
        return !q.getResultList().isEmpty();
    }

    public boolean temPlaca(CadastroEconomico cmcPrestador) {
        String hql = "select placa.id from PlacaAutomovelCmc placa where placa.cadastroEconomico = :cmc ";
        Query q = em.createQuery(hql);
        q.setParameter("cmc", cmcPrestador);
        return !q.getResultList().isEmpty();
    }

    public List filtrar(int inicio, int max, Pessoa filtroPrestador, Pessoa filtroTomador, String numeroInicial, String numeroFinal, Date emissaoInicial, Date emissaoFinal, Exercicio exercicioInicial, Exercicio exercicioFinal) {
        String hql = " select nota from NFSAvulsa nota " + "      inner join nota.exercicio ex" + "      inner join nota.prestador prestador" + "      inner join nota.tomador tomador";
        String juncao = " where";
        if (filtroPrestador != null) {
            hql += juncao += " prestador = :prestador";
            juncao = " and ";
        }
        if (filtroTomador != null) {
            hql += juncao += " tomador = :tomador";
            juncao = " and ";
        }
        if (emissaoInicial != null) {
            hql += juncao += " to_char(nota.emissao, 'dd/MM/yyyy') >= :emissaoInicial";
            juncao = " and ";
        }
        if (emissaoFinal != null) {
            hql += juncao += " to_char(nota.emissao, 'dd/MM/yyyy') <= :emissaoFinal";
            juncao = " and ";
        }
        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            hql += juncao += " to_char(nota.numero) >= :numeroInicial";
            juncao = " and ";
        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            hql += juncao += " to_char(nota.numero) <= :numeroFinal";
            juncao = " and ";
        }
        if (exercicioInicial != null) {
            hql += juncao += " exercicio.ano >= :exercicioInicial";
            juncao = " and ";
        }
        if (exercicioFinal != null) {
            hql += juncao += " exercicio.ano <= :exercicioFinal";
        }
        hql += " order by nota.numero, nota.emissao desc";
        Query q = em.createQuery(hql);
        q.setMaxResults(max + 10);
        q.setFirstResult(inicio);
        if (filtroPrestador != null) {
            q.setParameter("prestador", filtroPrestador);
        }
        if (filtroTomador != null) {

            q.setParameter("tomador", filtroTomador);
        }
        if (emissaoInicial != null) {
            SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
            q.setParameter("emissaoInicial", sp.format(emissaoInicial));
        }
        if (emissaoFinal != null) {
            SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
            q.setParameter("emissaoFinal", sp.format(emissaoFinal));
        }
        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            q.setParameter("numeroInicial", numeroInicial);

        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            q.setParameter("numeroFinal", numeroFinal);
        }
        if (exercicioInicial != null) {
            q.setParameter("exercicioInicial", exercicioInicial.getAno());
        }
        if (exercicioFinal != null) {
            q.setParameter("exercicioFinal", exercicioFinal.getAno());
        }
        //new Util().imprimeSQL(hql, q);
        return q.getResultList();
    }

    public String montaDescricaoLonga(NFSAvulsa selecionado) {
        DecimalFormat df = new DecimalFormat("###,###,##0.00");
        StringBuilder descricao = new StringBuilder(montaDescricaoCurta(selecionado));
        descricao.append(adicionaValor("TOTAL DA NOTA ....: ", selecionado.getValorServicos(), "", true)).append("\n");
        descricao.append(adicionaValor("ALÍQUOTA DO ISS ..: ", selecionado.getValorIss(), "", true)).append("\n");
        descricao.append(adicionaValor("VALOR DO ISS .....: ", selecionado.getValorTotalIss(), "", true)).append("\n");

        return descricao.toString();
    }

    public String montaDescricaoCurta(NFSAvulsa selecionado) {
        StringBuilder descricao = new StringBuilder("");
        descricao.append("TOMADOR:     ").append(selecionado.getTomador().getNome().toUpperCase()).append("\n");
        descricao.append("CPF/CNPJ:    ").append(selecionado.getTomador().getCpf_Cnpj()).append("\n");
        descricao.append("NATUREZA:    ").append("SERVIÇOS PRESTADOS").append("\n");
        descricao.append("NOTA FISCAL: ").append(selecionado.getNumeroCompleto()).append(" SÉRIE ÚNICA").append("\n\n");
        return descricao.toString();
    }

    private String adicionaValor(String texto, BigDecimal valor, String simbolo, boolean insereNoFinal) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < 10) {
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

    public List<NFSAvulsa> completaNota(String parte, NFSAvulsa.Situacao situacao) {
        String hql = "select n " + "       from NFSAvulsa n " + "      where n.fimVigencia is null " + "        and n.situacao != :situacao" + "        and n not in (select nfs.nfsAvulsa " + "                        from NFSAvulsaCancelamento nfs where nfs.nfsAvulsa = n)" + "      and (to_char(n.numero) like :parte " + "       or n.prestador in (select p from PessoaFisica p where lower(p.nome) like :parte)" + "       or n.prestador in (select p from PessoaJuridica p where lower(p.razaoSocial) like :parte))";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("situacao", situacao);
        return q.getResultList();
    }

    public List<NFSAvulsa> completaNFSAvulsaQueNaoEstejaCancelada(String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append("with nfs_de_prestador as ( ");
        sql.append("     select n.id ");
        sql.append("       from nfsavulsa n ");
        sql.append(" left join pessoafisica pf on pf.id = n.prestador_id ");
        sql.append("  inner join exercicio ex on n.exercicio_id = ex.id ");
        sql.append("      where pf.nome  like :parte or n.numero || '/' || ex.ano like :partenumero ");
        sql.append("      union ");

        sql.append(" select n.id ");
        sql.append("       from nfsavulsa n ");
        sql.append(" left join cadastroeconomico ce on ce.id = n.cmcprestador_id ");
        sql.append(" left join pessoafisica pf on ce.pessoa_id = pf.id");
        sql.append(" left join pessoajuridica pj on ce.pessoa_id = pj.id");
        sql.append("  where ce.inscricaocadastral  like :parte or pf.nome like :parte or pj.razaosocial like :parte ");

        sql.append("      union ");
        sql.append("     select n.id ");
        sql.append("       from nfsavulsa n    ");
        sql.append(" left join pessoajuridica pj on pj.id = n.prestador_id ");
        sql.append("  inner join exercicio ex on n.exercicio_id = ex.id ");
        sql.append("      where pj.razaosocial like :parte OR n.numero || '/' || ex.ano like :partenumero ");
        sql.append(")    select nfa.* ");
        sql.append("       from nfs_de_prestador n ");
        sql.append(" inner join nfsavulsa nfa on n.id = nfa.id ");
        sql.append("      where not exists (select c.id from nfsavulsacancelamento c where c.nfsavulsa_id = n.id)");
        sql.append("         order by nfa.numero, nfa.exercicio_id desc ");
        Query q = em.createNativeQuery(sql.toString(), NFSAvulsa.class);
        q.setParameter("parte", "%" + parte.toUpperCase() + "%");
        q.setParameter("partenumero", "%" + parte.toUpperCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<NFSAvulsa> completaNotaConcluida(String parte, Exercicio exercicio) {
        String sql = "select nfa.* " + "   from nfsavulsa nfa " + "  left join pessoajuridica pj on nfa.prestador_id = pj.id " + "  left join pessoafisica pf on nfa.prestador_id = pf.id " + "  inner join exercicio ex on ex.id = nfa.exercicio_id " + " where ex.ano = :exercicio and nfa.situacao = 'CONCLUIDA' " + " and (regexp_replace(coalesce(pf.cpf,pj.cnpj), '[^0-9]') like :parte " + " or (lower(pf.nome) like :parte or (replace(replace(pf.cpf,'.',''),'-','') like :parte)) " + " or ((lower(pj.razaosocial) like :parte) or (pj.cnpj like :parte) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)) " + " or  (to_char(nfa.numero) like :parte ))";


        Query q = em.createNativeQuery(sql, NFSAvulsa.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        if (exercicio != null) {
            q.setParameter("exercicio", exercicio.getAno());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<NFSAvulsa> completaNotaConcluidaOuAguardandoPagamento(String parte, Exercicio exercicio) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select nfa.*");
        sql.append(" from nfsavulsa nfa ");
        sql.append(" left join pessoajuridica pj on nfa.prestador_id = pj.id ");
        sql.append(" left join pessoafisica pf on nfa.prestador_id = pf.id ");
        sql.append(" inner join exercicio ex on ex.id = nfa.exercicio_id ");

//        sql.append(" left join cadastroeconomico ce on ce.id = nfa.cmcprestador_id");
//        sql.append(" left join pessoafisica pff on pff.id = ce.pessoa_id");
//        sql.append(" left join pessoajuridica pjj on pjj.id = ce.pessoa_id");


        sql.append(" where ex.ano = :exercicio");
        sql.append(" and (nfa.situacao in ('CONCLUIDA', 'EMITIDA', 'ABERTA'))");
        sql.append(" and (regexp_replace(coalesce(pf.cpf,pj.cnpj), '[^0-9]') like :parte ");
        sql.append(" or (lower(pf.nome) like :parte or (replace(replace(pf.cpf,'.',''),'-','') like :parte)) ");
        sql.append(" or ((lower(pj.razaosocial) like :parte) or (pj.cnpj like :parte) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :parte)) ");
        sql.append(" or  (to_char(nfa.numero) like :parte ))");


        Query q = em.createNativeQuery(sql.toString(), NFSAvulsa.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        if (exercicio != null) {
            q.setParameter("exercicio", exercicio.getAno());
        }
        q.setMaxResults(10);
        return q.getResultList();
    }

    public Boolean damPago(NFSAvulsa nfs) {
        if (nfs == null || nfs.getId() == null) {
            return false;
        }
        String sql = "select * from nfsavulsa na" + "         join calculonfsavulsa cna on na.id = cna.nfsavulsa_id" + "         join valordivida vd on cna.id = vd.calculo_id" + "         join parcelavalordivida pvd on vd.id = pvd.valordivida_id" + "         join situacaoparcelavalordivida spvd on pvd.id = spvd.parcela_id" + "              where spvd.situacaoparcela = 'PAGO' and na.id = :nota";
        Query q = em.createNativeQuery(sql);
        q.setParameter("nota", nfs.getId());
        return !q.getResultList().isEmpty();
    }

    public void cancelaParcelaValorDividaDaNFS(NFSAvulsa nfsAvulsa) {
        List<CalculoNFSAvulsa> calculosNFSAvulsa = buscarCalculosDaNFSAvulsa(nfsAvulsa);
        if (calculosNFSAvulsa != null && !calculosNFSAvulsa.isEmpty()) {
            List<ResultadoParcela> resultados = new ConsultaParcela().addParameter(ConsultaParcela.Campo.CALCULO_ID,
                    ConsultaParcela.Operador.IN, calculosNFSAvulsa.stream().map(CalculoNFSAvulsa::getId).collect(Collectors.toList()))
                .executaConsulta().getResultados();
            for (ResultadoParcela resultado : resultados) {
                if (resultado.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())) {
                    ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, resultado.getIdParcela());
                    SituacaoParcelaValorDivida spvd = new SituacaoParcelaValorDivida();
                    spvd.setDataLancamento(new Date());
                    spvd.setParcela(parcelaValorDivida);
                    spvd.setSituacaoParcela(SituacaoParcela.CANCELAMENTO);
                    spvd.setInconsistente(Boolean.FALSE);
                    spvd.setSaldo(this.consultaDebitoFacade.getUltimaSituacao(parcelaValorDivida).getSaldo());
                    this.em.persist(spvd);
                }
            }
        }
    }

    public List<NFSAvulsaItem> recuperItensNFS(NFSAvulsa nota) {
        Query consulta = em.createQuery("select item from NFSAvulsaItem item where NFSAvulsa = :nota");
        consulta.setParameter("nota", nota);
        return consulta.getResultList();
    }

    public AlteracaoNFSAvulsa salvarAlteracaoNFS(AlteracaoNFSAvulsa alteracaoNFSAvulsa) {
        alteracaoNFSAvulsa.getNFSAvulsa().setNFSAvulsa(em.merge(alteracaoNFSAvulsa.getNFSAvulsa().getNFSAvulsa()));
        alteracaoNFSAvulsa.setNFSAvulsa(em.merge(alteracaoNFSAvulsa.getNFSAvulsa()));

        CalculoNFSAvulsa calculo = recuperaCalculoDaNotaAntiga(alteracaoNFSAvulsa.getNFSAvulsa());
        if (calculo != null) {
            calculo.setNfsAvulsa(alteracaoNFSAvulsa.getNFSAvulsa().getNFSAvulsa());
            em.merge(calculo);
        }
        return em.merge(alteracaoNFSAvulsa);
    }

    public NFSAvulsa emitirNota(NFSAvulsa nota) throws JRException, IOException {
        nota = ajustaNotaParaImpresssao(nota);
        String arquivoJasper = "NFSAvulsa.jasper";
        AbstractReport report = AbstractReport.getAbstractReport();
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        String url = configuracaoTributario.getUrlPortalContribuinte() + "autenticidade-de-documentos/nota-avulsa/" + nota.getId() + "/";
        String qrCode = BarCode.generateBase64QRCodePng(url, 350, 350);

        HashMap parameters = new HashMap();
        parameters.put("BRASAO", report.getCaminhoImagem());
        parameters.put("SUBREPORT_DIR", report.getCaminhoSubReport());
        parameters.put("ENDERECOPRESTADOR", getEnderecoPrestador(nota));
        parameters.put("ENDERECOTOMADOR", getEnderecoTomador(nota));
        parameters.put("QRCODE", qrCode);
        parameters.put("NOTA_CANCELADA", NFSAvulsa.Situacao.CANCELADA.equals(nota.getSituacao()));

        new ByteArrayInputStream(Base64.decodeBase64(""));

        new ByteArrayInputStream(org.apache.commons.codec.binary.Base64.decodeBase64("$P{QRCODE}"));

        List<NFSAvulsa> aImprimir = new ArrayList<>();
        aImprimir.add(nota);
        for (NFSAvulsa nfs : aImprimir) {
            if (nfs.getCmcPrestador() != null) {
                nfs.setPrestador(nfs.getCmcPrestador().getPessoa());
            }
            if (nfs.getCmcTomador() != null) {
                nfs.setTomador(nfs.getCmcTomador().getPessoa());
            }
        }

        definirNumeroAutenticidade(nota);
        report.setGeraNoDialog(true);
        report.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, new JRBeanCollectionDataSource(aImprimir));
        return nota;
    }

    private NFSAvulsa ajustaNotaParaImpresssao(NFSAvulsa nota) {
        if (NFSAvulsa.Situacao.ABERTA.equals(nota.getSituacao()) || NFSAvulsa.Situacao.CONCLUIDA.equals(nota.getSituacao())) {
            if (nota.getEmissao() == null) {
                nota.setEmissao(new Date());
            }
            if (!NFSAvulsa.Situacao.CONCLUIDA.equals(nota.getSituacao())) nota.setSituacao(NFSAvulsa.Situacao.EMITIDA);
            nota = em.merge(nota);
        }
        return nota;
    }

    public void definirNumeroAutenticidade(NFSAvulsa nota) {
        if (nota.getEmissao() != null) {
            String cpfCnpjTomador = nota.getTomador() != null ? nota.getTomador().getCpf_Cnpj() : nota.getCmcTomador() != null ? nota.getCmcTomador().getPessoa().getCpf_Cnpj() : "";
            String chave = Util.dateToString(nota.getEmissao()) + nota.getNumero().toString() + StringUtil.retornaApenasNumeros(cpfCnpjTomador);
            nota.setAutenticidade(GeradorChaveAutenticacao.geraChaveDeAutenticacao(chave, GeradorChaveAutenticacao.TipoAutenticacao.NFSAVULSA));
        }
    }

    public String getCaminhoImagem() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/img/");
        caminho += "/";
        return caminho;
    }

    public String getEnderecoPrestador(NFSAvulsa nota) {
        if (nota.getCmcPrestador() != null) {
            EnderecoCorreio end = cadastroEconomicoFacade.recuperarEnderecoPessoaCMC(nota.getCmcPrestador().getId());
            return (end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + (end.getNumero() != null ? " Nº " + end.getNumero() : "") : "").replaceAll("null", " ");
        }
        if (nota.getPrestador() != null) {
            return getEndereco(nota.getPrestador()).replaceAll("null", " ");
        }
        return "";
    }


    public String getEnderecoTomador(NFSAvulsa nota) {
        if (nota.getCmcTomador() != null) {
            EnderecoCorreio end = cadastroEconomicoFacade.recuperarEnderecoPessoaCMC(nota.getCmcTomador().getId());
            return (end != null ? (end.getTipoLogradouro() != null ? end.getTipoLogradouro().getDescricao() + " " : "") + end.getLogradouro() + " " + end.getBairro() + (end.getNumero() != null ? " Nº " + end.getNumero() : "") : "").replaceAll("null", " ");
        }
        if (nota.getTomador() != null) {
            return getEndereco(nota.getTomador()).replaceAll("null", " ");
        }
        return "";
    }

    private String getEndereco(Pessoa p) {
        List<EnderecoCorreio> enderecos = getListaDeEnderecos(p);
        EnderecoCorreio enderecoCorreio = null;
        for (EnderecoCorreio end : enderecos) {
            if (end.getPrincipal()) {
                enderecoCorreio = end;
            }
            if (enderecoCorreio == null) {
                if (TipoEndereco.DOMICILIO_FISCAL.equals(end.getTipoEndereco())) {
                    enderecoCorreio = end;
                }
            }

            if (enderecoCorreio == null) {
                if (TipoEndereco.CORRESPONDENCIA.equals(end.getTipoEndereco())) {
                    return end.toString();
                }
            }
        }
        if (enderecoCorreio == null && enderecos.size() > 0) {
            enderecoCorreio = enderecos.get(0);
        }
        return enderecoCorreio != null ? enderecoCorreio.toString() : "";
    }


    public void setSituacaoCancelada(NFSAvulsa nfsAvulsa) {
        if (nfsAvulsa != null) {
            nfsAvulsa.setSituacao(NFSAvulsa.Situacao.CANCELADA);
            this.em.merge(nfsAvulsa);
        }
    }

    @Override
    public List<NFSAvulsa> lista() {
        Query consulta = em.createQuery("select n from NFSAvulsa n where n.fimVigencia is null order by numero desc");
        return consulta.getResultList();
    }

    private CalculoNFSAvulsa recuperaCalculoDaNotaAntiga(NFSAvulsa nfsAvulsa) {
        Query consulta = em.createQuery("select calc from CalculoNFSAvulsa calc where calc.nfsAvulsa = :nota");
        consulta.setParameter("nota", nfsAvulsa);
        if (consulta.getResultList().size() > 0) {
            return (CalculoNFSAvulsa) consulta.getResultList().get(0);
        } else {
            return null;
        }
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        NFSAvulsa nota = em.find(CalculoNFSAvulsa.class, calculo.getId()).getNfsAvulsa();
        if (nota != null) {
            nota.setSituacao(NFSAvulsa.Situacao.CONCLUIDA);
            em.merge(nota);
        }
    }

    public List<NFSAvulsa> buscarNotasDaPessoa(Pessoa pessoa, int inicio, int fim) {
        Query consulta = em.createQuery("select n from NFSAvulsa n " + " left join n.cmcPrestador cmcPrestador " + " left join n.cmcTomador cmcTomador" + " where n.tomador.id = :pessoa " + " or n.prestador.id = :pessoa " + " or cmcPrestador.pessoa.id = :pessoa " + " or cmcTomador.pessoa.id = :pessoa " + " order by n.dataNota desc");
        consulta.setParameter("pessoa", pessoa.getId());
        consulta.setFirstResult(inicio);
        consulta.setMaxResults(fim);
        return consulta.getResultList();
    }


    public Integer contarNotasDaPessoa(Pessoa pessoa) {
        Query consulta = em.createQuery("select count(n.id) from NFSAvulsa n " + " left join n.cmcPrestador cmcPrestador " + " left join n.cmcTomador cmcTomador" + " where n.tomador.id = :pessoa " + " or n.prestador.id = :pessoa " + " or cmcPrestador.pessoa.id = :pessoa " + " or cmcTomador.pessoa.id = :pessoa " + " order by n.numero desc, n.exercicio.ano desc");
        consulta.setParameter("pessoa", pessoa.getId());

        return ((Long) consulta.getSingleResult()).intValue();
    }

    public List<EnderecoCorreio> getListaDeEnderecos(Pessoa p) {
        return enderecoCorreioFacade.recuperaEnderecosPessoa(p);
    }

    private void definirPrestadorNFSAvulsa(NFSAvulsa nfsAvulsa, NFSAvulsaNfseDTO dto) {
        nfsAvulsa.setTipoPrestadorNF(TipoTomadorPrestadorNF.PESSOA);
        nfsAvulsa.setPrestador(pessoaFacade.createOrSave(dto.getPrestador()));
    }

    private void definirTomadorNFSAvulsa(NFSAvulsa nfsAvulsa, NFSAvulsaNfseDTO dto) {
        nfsAvulsa.setTipoTomadorNF(TipoTomadorPrestadorNF.PESSOA);
        nfsAvulsa.setTomador(pessoaFacade.createOrSave(dto.getTomador()));
    }

    private void criarItensNFSAvulsa(NFSAvulsa nfsAvulsa, NFSAvulsaNfseDTO dto) {
        for (NFSAvulsaItemNfseDTO itemNfseDTO : dto.getItens()) {
            NFSAvulsaItem nfsAvulsaItem = new NFSAvulsaItem();

            nfsAvulsaItem.setNFSAvulsa(nfsAvulsa);
            nfsAvulsaItem.setServico(new Servico(itemNfseDTO.getServico().getId()));
            nfsAvulsaItem.setAliquotaIss(itemNfseDTO.getAliquotaIss());
            nfsAvulsaItem.setDescricao(itemNfseDTO.getDescricao());
            nfsAvulsaItem.setQuantidade(itemNfseDTO.getQuantidade());
            nfsAvulsaItem.setValorUnitario(itemNfseDTO.getValorUnitario());
            nfsAvulsaItem.setValorIss(itemNfseDTO.getValorIss());

            nfsAvulsa.getItens().add(nfsAvulsaItem);
        }
    }

    public List<ResultadoParcela> buscarParcelasCalculo(CalculoNFSAvulsa calculoNFSAvulsa) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Campo.CALCULO_ID, br.com.webpublico.tributario.consultadebitos.ConsultaParcela.Operador.IGUAL, calculoNFSAvulsa.getId());
        return consultaParcela.executaConsulta().getResultados();
    }

    public boolean debitoEmAberto(List<ResultadoParcela> parcelas) {
        if (parcelas != null && !parcelas.isEmpty()) {
            for (ResultadoParcela parcela : parcelas) {
                if (SituacaoParcela.EM_ABERTO.name().equals(parcela.getSituacaoNameEnum())) return true;
            }
        }
        return false;
    }

    public byte[] gerarImpressaoNFSAvulsa(NFSAvulsa nota) throws JRException, IOException {
        nota = ajustaNotaParaImpresssao(nota);
        String arquivoJasper = "NFSAvulsa.jasper";
        AbstractReport report = AbstractReport.getAbstractReport();
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();

        String url = configuracaoTributario.getUrlPortalContribuinte() + "autenticidade-de-documentos/nota-avulsa/" + nota.getId() + "/";
        String qrCode = BarCode.generateBase64QRCodePng(url, 350, 350);

        String separator = System.getProperty("file.separator");
        String subReportDir = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String caminhoImg = Util.getApplicationPath("/img/") + separator;

        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "Rio Branco");
        parameters.put("BRASAO", caminhoImg);
        parameters.put("SUBREPORT_DIR", subReportDir);
        parameters.put("ENDERECOTOMADOR", getEnderecoTomador(nota));
        parameters.put("ENDERECOPRESTADOR", getEnderecoPrestador(nota));
        parameters.put("QRCODE", qrCode);
        parameters.put("NOTA_CANCELADA", NFSAvulsa.Situacao.CANCELADA.equals(nota.getSituacao()));

        List<NFSAvulsa> aImprimir = new ArrayList<>();
        aImprimir.add(nota);
        for (NFSAvulsa nfs : aImprimir) {
            if (nfs.getCmcPrestador() != null) {
                nfs.setPrestador(nfs.getCmcPrestador().getPessoa());
            }
            if (nfs.getCmcTomador() != null) {
                nfs.setTomador(nfs.getCmcTomador().getPessoa());
            }
        }
        definirNumeroAutenticidade(nota);
        report.setGeraNoDialog(true);
        ByteArrayOutputStream relatorio = report.gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(subReportDir, arquivoJasper, parameters, new JRBeanCollectionDataSource(aImprimir));
        return relatorio.toByteArray();
    }

    public List<NFSAvulsa> buscarNFSaCancelamento(FiltroCancelamentoNFSa filtro) {

        String sql = " select nfsa.* " + "          from nfsavulsa nfsa " + " left join cadastroeconomico cmc_prestador on nfsa.cmcprestador_id = cmc_prestador.id " + " left join pessoafisica pf on pf.id = nfsa.prestador_id " + " left join pessoajuridica pj on pj.id = nfsa.prestador_id " + " where not exists(select c.id from nfsavulsacancelamento c where c.nfsavulsa_id = nfsa.id) " + filtro.adicionarFiltros() + " order by nfsa.numero, nfsa.exercicio_id desc";
        Query q = em.createNativeQuery(sql, NFSAvulsa.class);
        filtro.adicionarParametros(q);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public WSNFSAvulsa gerarNotaAvulsaPortalContribuinte(EmissaoNotaAvulsaDTO dto) throws Exception {
        NFSAvulsa notaAvulsa = new NFSAvulsa();
        notaAvulsa.setOrigemNotaAvulsa(OrigemNotaAvulsa.PORTAL);

        Pessoa prestadorServico = pessoaFacade.buscarPessoaPorCpfOrCnpj(dto.getCpfPrestador());
        CadastroEconomico cmcPrestador = cadastroEconomicoFacade.buscarCadastroEconomicoAtivoPorPessoa(prestadorServico);
        Pessoa tomadorServico = pessoaFacade.buscarPessoaPorCpfOrCnpj(dto.getCpfCnpjTomador());
        if (tomadorServico != null) {
            CadastroEconomico cmcTomador = cadastroEconomicoFacade.buscarCadastroEconomicoAtivoPorPessoa(tomadorServico);
            notaAvulsa.setCmcTomador(cmcTomador);
            notaAvulsa.setTipoTomadorNF(cmcTomador != null ? TipoTomadorPrestadorNF.ECONOMICO : TipoTomadorPrestadorNF.PESSOA);
        } else {
            notaAvulsa.setTipoTomadorNF(TipoTomadorPrestadorNF.PESSOA);
            PessoaNfseDTO pessoaDto = instanciarDadosNovaPessoa(dto);
            tomadorServico = pessoaFacade.createOrSave(pessoaDto, SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO);
        }

        notaAvulsa.setPrestador(prestadorServico);
        notaAvulsa.setCmcPrestador(cmcPrestador);
        notaAvulsa.setTomador(tomadorServico);

        notaAvulsa.setValorServicos(dto.getTotalParcial());
        notaAvulsa.setEmissao(dto.getDataEmissao());
        notaAvulsa.setExercicio(exercicioFacade.getExercicioCorrente());
        notaAvulsa.setValorTotalIss(dto.getTotalIss());
        notaAvulsa.setSituacao(NFSAvulsa.Situacao.ABERTA);

        notaAvulsa.setDataNota(new Date());
        notaAvulsa.setUsuario(usuarioSistemaFacade.usuarioSistemaDaPessoa(prestadorServico));
        notaAvulsa.setInicioVigencia(new Date());
        notaAvulsa.setTipoPrestadorNF(TipoTomadorPrestadorNF.PESSOA);

        adicionarItensNotaAvulsaPortal(dto, notaAvulsa);
        BigDecimal total = BigDecimal.ZERO;
        for (NFSAvulsaItem it : notaAvulsa.getItens()) {
            total = total.add(it.getValorIss());
        }
        notaAvulsa.setValorTotalIss(total);
        if (notaAvulsa.getValorTotalIss() == null || notaAvulsa.getValorTotalIss().compareTo(BigDecimal.ZERO) == 0) {
            notaAvulsa.setSituacao(NFSAvulsa.Situacao.CONCLUIDA);
        }
        notaAvulsa = gerarNfsAvulsaAndDebito(notaAvulsa);
        return new WSNFSAvulsa(notaAvulsa);
    }

    private PessoaNfseDTO instanciarDadosNovaPessoa(EmissaoNotaAvulsaDTO dto) {
        PessoaNfseDTO pessoaDto = new PessoaNfseDTO();
        DadosPessoaisNfseDTO dadosPessoais = new DadosPessoaisNfseDTO();

        dadosPessoais.setCpfCnpj(dto.getCpfCnpjTomador());
        dadosPessoais.setNomeRazaoSocial(dto.getNovoTomador().getNomeRazaoSocial());
        dadosPessoais.setInscricaoMunicipal(dto.getNovoTomador().getInscricaoMunicipal());
        dadosPessoais.setEmail(dto.getNovoTomador().getEmail());
        dadosPessoais.setCep(dto.getNovoTomador().getCep());

        dadosPessoais.setLogradouro(dto.getNovoTomador().getLogradouro());
        dadosPessoais.setBairro(dto.getNovoTomador().getBairro());
        dadosPessoais.setNumero(dto.getNovoTomador().getNumero());
        dadosPessoais.setComplemento(dto.getNovoTomador().getComplemento());
        MunicipioNfseDTO municipioNfseDTO = new MunicipioNfseDTO();
        municipioNfseDTO.setNome(dto.getNovoTomador().getLocalidade());
        municipioNfseDTO.setEstado(dto.getNovoTomador().getUf());
        dadosPessoais.setMunicipio(municipioNfseDTO);
        dadosPessoais.setTelefone(dto.getNovoTomador().getTelefone());
        pessoaDto.setDadosPessoais(dadosPessoais);
        return pessoaDto;
    }

    private void adicionarItensNotaAvulsaPortal(EmissaoNotaAvulsaDTO notaAvulsaDTO, NFSAvulsa notaAvulsa) {
        for (ItemNotaAvulsaDTO itemDTO : notaAvulsaDTO.getItens()) {
            NFSAvulsaItem item = new NFSAvulsaItem();
            item.setNFSAvulsa(notaAvulsa);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setUnidade(itemDTO.getUnidade());
            item.setDescricao(itemDTO.getDescricao());
            item.setValorUnitario(itemDTO.getValorUnitario());
            item.setAliquotaIss(itemDTO.getAliquotaIss());
            item.setPlaca(itemDTO.getPlaca());
            defineAliquotaISS(item, notaAvulsa);
            BigDecimal valor = ((item.getAliquotaIss()).multiply(item.getValorTotal()))
                .divide(new BigDecimal("100"));
            item.setValorIss(valor);
            notaAvulsa.getItens().add(item);
        }
    }

    private boolean isPossuiPlacas(NFSAvulsa nota) {
        if (nota == null) {
            return false;
        }
        return temPlaca(nota.getCmcPrestador());
    }


    private boolean isNecessitaPlacas(CadastroEconomico economico) {
        if (economico != null) {
            return economico.getTipoAutonomo() != null && economico.getTipoAutonomo().getNecessitaPlacas();
        }
        return false;
    }

    public String defineAliquotaISS(NFSAvulsaItem item, NFSAvulsa selecionado) {
        String message = "";
        BigDecimal valorAliquota = configuracaoTributarioFacade.retornaUltimo().getAliquotaISSQN();
        CalculoISS calculo = null;
        if (selecionado.getCmcPrestador() != null) {
            if (selecionado.getCmcPrestador().getNaturezaJuridica() != null && selecionado.getCmcPrestador().getNaturezaJuridica().getAutonomo()) {
                if (selecionado.getCmcPrestador() != null && selecionado.getCmcPrestador().getTipoAutonomo() != null) {
                    calculo = temCalculoISSFixoParaEsteAno(selecionado.getCmcPrestador());
                }
                if (isNecessitaPlacas(selecionado.getCmcPrestador()) && isPossuiPlacas(selecionado)) {
                    if (placaIgual(selecionado.getCmcPrestador(), item.getPlaca())) {
                        if (anuidadeGeradaEPaga(calculo)) {
                            item.setAliquotaIss(BigDecimal.ZERO);
                        } else {
                            if (calculo == null) {
                                message = ("O prestador informado é um C.M.C. que está inadimplente, é necessário que o C.M.C tenha cálculo de ISS no ano corrente. O ISS deste serviço será lançado.");
                            } else {
                                message = ("O prestador informado é um C.M.C. que está inadimplente. O ISS deste serviço será lançado.");
                            }
                            item.setAliquotaIss(valorAliquota);
                        }
                    } else {
                        message = ("A placa do veículo informada não consta no cadastro do C.M.C. do prestador. O ISS deste serviço será lançado");
                        item.setAliquotaIss(valorAliquota);
                    }
                } else {
                    if (isNecessitaPlacas(selecionado.getCmcPrestador())) {
                        message = ("O prestador informado é um C.M.C. que não possui placa(s) informada no cadastro econômico, é necessário a atualização deste para que não haja cobrança de ISS. O ISS deste serviço será lançado.");
                        item.setAliquotaIss(valorAliquota);
                    } else {
                        if (anuidadeGeradaEPaga(calculo)) {
                            item.setAliquotaIss(BigDecimal.ZERO);
                        } else {
                            if (calculo == null) {
                                message = ("O prestador informado é um C.M.C. que está inadimplente, é necessário que o C.M.C tenha cálculo de ISS no ano corrente. O ISS deste serviço será lançado.");
                            } else {
                                message = ("O prestador informado é um C.M.C. que está inadimplente. O ISS deste serviço será lançado.");
                            }
                            item.setAliquotaIss(valorAliquota);
                        }
                    }
                }
            } else {
                message = ("O prestador informado é um C.M.C. que NÃO É AUTÔNOMO. O ISS deste serviço será lançado.");
                item.setAliquotaIss(valorAliquota);
            }
        } else {
            item.setAliquotaIss(valorAliquota);
        }
        return message;
    }

    public GeraAliquotaNotaAvulsaDTO definirAliquotaISS(String cpf) {
        GeraAliquotaNotaAvulsaDTO geraAliquota = new GeraAliquotaNotaAvulsaDTO();
        Pessoa prestadorServico = pessoaFacade.buscarPessoaPorCpfOrCnpj(cpf);
        CadastroEconomico economico = cadastroEconomicoFacade.buscarCadastroEconomicoAtivoPorPessoa(prestadorServico);
        geraAliquota.setAliquotaIss(configuracaoTributarioFacade.retornaUltimo().getAliquotaISSQN());
        if (economico != null) {
            geraAliquota.setPrestadorComCmc(true);
            geraAliquota.setCadastroEconomico(economico.getInscricaoCadastral());
            if (economico.getNaturezaJuridica() != null && economico.getNaturezaJuridica().getAutonomo()) {
                geraAliquota.setAutonomo(true);
                if (economico.getTipoAutonomo() != null) {
                    CalculoISS calculo = temCalculoISSFixoParaEsteAno(economico);
                    geraAliquota.setContemCalculo(calculo != null);
                    if (anuidadeGeradaEPaga(calculo)) {
                        geraAliquota.setAliquotaIss(BigDecimal.ZERO);
                        geraAliquota.setDividasEmDia(true);
                    }
                }
            }
        }
        return geraAliquota;
    }

    public List<CalculoNFSAvulsa> buscarCalculosDaNFSAvulsa(NFSAvulsa nfsAvulsa) {
        return em.createQuery(" from CalculoNFSAvulsa c " +
                " where c.nfsAvulsa = :nfsAvulsa " +
                " order by c.id desc ")
            .setParameter("nfsAvulsa", nfsAvulsa)
            .getResultList();
    }
}
