package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ImprimeDemonstrativoDebitos;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.entidadesauxiliares.TotalSituacao;
import br.com.webpublico.entidadesauxiliares.VOConsultaDebito;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CadastroFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by HardRock on 01/03/2017.
 */
@ManagedBean
@ViewScoped
public class ComponenteConsultaDebitos extends AbstractReport implements Serializable {

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    List<ResultadoParcela> resultadosParcela;
    private VOConsultaDebito consultaDebito;
    private ConsultaParcela consultaParcela;
    private Map<String, BigDecimal> totaisSituacao;

    public void novoComponente(TipoCadastroTributario tipoCadastroTributario) {
        resultadosParcela = Lists.newArrayList();
        consultaDebito = new VOConsultaDebito();
        totaisSituacao = new HashMap<>();
        consultaParcela = new ConsultaParcela();
        this.consultaDebito.setTipoCadastroTributario(tipoCadastroTributario);
    }

    public void consultarParcela(Long id, TipoCadastroTributario tipoCadastroTributario) {
        novoComponente(tipoCadastroTributario);
        adicionarParametroConsultaParcela(id);
        consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, getSituacoesParcela());
        consultaParcela.setFirtResult(0);
        consultaParcela.setMaxResult(51);
        resultadosParcela = consultaParcela.executaConsulta().getResultados();
        ajustarOrdemPadraoParcela(resultadosParcela);
    }

    private void ajustarOrdemPadraoParcela(List<ResultadoParcela> parcelas) {
        Collections.sort(parcelas, getComparatorByValuePadrao());
    }

    private Comparator<ResultadoParcela> getComparatorByValuePadrao() {
        return new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                int retorno;

                retorno = um.getOrdemApresentacao().compareTo(dois.getOrdemApresentacao());
                if (retorno == 0) {
                    retorno = um.getDivida().compareTo(dois.getDivida());
                }
                if (retorno == 0) {
                    retorno = um.getExercicio().compareTo(dois.getExercicio());
                }
                if (retorno == 0) {
                    retorno = um.getReferencia().compareTo(dois.getReferencia());
                }
                if (retorno == 0) {
                    retorno = um.getSd().compareTo(dois.getSd());
                }
                if (retorno == 0) {
                    retorno = dois.getCotaUnica().compareTo(um.getCotaUnica());
                }
                if (retorno == 0) {
                    retorno = um.getVencimento().compareTo(dois.getVencimento());
                }
                if (retorno == 0) {
                    retorno = um.getParcela().compareTo(dois.getParcela());
                }
                if (retorno == 0) {
                    retorno = um.getSituacao().compareTo(dois.getSituacao());
                }
                return retorno;
            }
        };
    }

    private void adicionarParametroConsultaParcela(Long id) {
        if (!consultaDebito.isContribuinteGeral()) {
            consultaDebito.setCadastro(cadastroFacade.recuperar(id));
            if (consultaDebito.getCadastro() != null) {
                consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, consultaDebito.getCadastro().getId());
            }
        } else {
            consultaDebito.setPessoa(pessoaFacade.recuperar(id));
            if (consultaDebito.getPessoa() != null) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, consultaDebito.getPessoa().getId());
            }
        }
    }

    public void adicionarConsultaDebitoNaSessao() {
        Web.poeNaSessao("CONSULTA", consultaDebito);
    }

    public void imprimirDemonstrativoDebitos() throws JRException, IOException {
        String filtros = getDescricaoObjeto();
        Pessoa pessoa = adicionarPessoaDoCadastro();
        try {
            validarPessoa(pessoa);
            EnderecoCorreio enderecoCorrespondencia = pessoa.getEnderecoDomicilioFiscal();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IN, getSituacoesParcela());
            consultaParcela.addComplementoDoWhere(" AND COALESCE(BCI.ATIVO, 0) = 1 ");

            montarMapaTotalPorParcela(resultadosParcela);

            setGeraNoDialog(true);
            ImprimeDemonstrativoDebitos imprime = new ImprimeDemonstrativoDebitos("RelatorioConsultaDebitos.jasper", new LinkedList<>(resultadosParcela));
            imprime.adicionarParametro("BRASAO", imprime.getCaminhoImagem());
            imprime.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            imprime.adicionarParametro("PESSOA", pessoa.getNome());
            imprime.adicionarParametro("CPF_CNPJ", pessoa.getCpf_Cnpj());
            if (enderecoCorrespondencia != null) {
                imprime.adicionarParametro("ENDERECO", enderecoCorrespondencia.getEnderecoCompleto());
            }
            imprime.adicionarParametro("FILTROS", filtros);
            imprime.adicionarParametro("TOTALPORSITUACAO", getTotalizadorPorSituacao());
            imprime.adicionarParametro("SUBREPORT_DIR", imprime.getCaminho());
            imprime.setGeraNoDialog(true);
            imprime.imprimeRelatorio();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private List<SituacaoParcela> getSituacoesParcela() {
        return Lists.newArrayList(SituacaoParcela.EM_ABERTO, SituacaoParcela.SUSPENSAO);
    }

    private List<TotalSituacao> getTotalizadorPorSituacao() {
        List<TotalSituacao> totalPorSituacao = Lists.newArrayList();
        for (String situacao : totaisSituacao.keySet()) {
            TotalSituacao total = new TotalSituacao();
            total.setSituacao(situacao);
            total.setValor(totaisSituacao.get(situacao));
            totalPorSituacao.add(total);
        }
        return totalPorSituacao;
    }

    private void validarPessoa(Pessoa pessoa) {
        ValidacaoException ve = new ValidacaoException();
        if (pessoa == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi possível recuperar a pessoa do " + consultaDebito.getTipoCadastroTributario().getDescricaoLonga() + ": " + consultaDebito.getCadastro().getNumeroCadastro());
        }
        ve.lancarException();
    }

    private Pessoa adicionarPessoaDoCadastro() {
        Pessoa pessoa;
        if (consultaDebito.getCadastro() != null) {
            List<Pessoa> pessoas = pessoaFacade.recuperaPessoasDoCadastro(consultaDebito.getCadastro());
            pessoa = pessoas.get(0);
        } else {
            pessoa = consultaDebito.getPessoa();
        }
        if (pessoa != null) {
            pessoa = pessoaFacade.recuperar(pessoa.getId());
        }
        return pessoa;
    }

    private void montarMapaTotalPorParcela(List<ResultadoParcela> resultadosParcela) {
        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();
        for (ResultadoParcela resultado : resultadosParcela) {
            resultado.setDebitoProtestado(consultaDebitoFacade.buscarProcessoAtivoDaParcela(
                resultado.getIdParcela()) == null ? Boolean.FALSE : Boolean.TRUE);
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }

        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (resultadoParcela.getCotaUnica()
                    && !resultadoParcela.getVencido()
                    && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())
                    || resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.PAGO.name()))
                    ) {
                    parcelasDaDivida.clear();
                    parcelasDaDivida.add(resultadoParcela);
                    break;
                } else {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }
        for (ResultadoParcela resultado : parcelasSoma) {
            BigDecimal total = BigDecimal.ZERO;
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(sistemaFacade.getDataOperacao());

            if (!totaisSituacao.containsKey(situacaoParaProcessamento)) {
                totaisSituacao.put(situacaoParaProcessamento, BigDecimal.ZERO);
            }
            if (resultado.isPago()) {
                total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorPago());
            } else {
                boolean soma = false;
                if ((!resultado.getVencido() && resultado.getCotaUnica())) {
                    soma = true;
                }
                if (!resultado.getCotaUnica()) {
                    soma = true;
                }
                if ((resultado.getCotaUnica() && (resultado.isInscrito() || resultado.isCancelado()))) {
                    soma = true;
                }
                if (soma) {
                    total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorTotal());
                    resultado.setSomaNoDemonstrativo(true);
                    totaisSituacao.put(situacaoParaProcessamento, total);
                }
            }
            totaisSituacao.put(situacaoParaProcessamento, total);
        }
    }

    public boolean verificarSeConsultaParcelaPossuiRegistros() {
        return resultadosParcela != null && !resultadosParcela.isEmpty();
    }

    public boolean renderizarMsgLimiteParcelas() {
        return !resultadosParcela.isEmpty() && resultadosParcela.size() > 50;
    }

    public String getDescricaoObjeto() {
        String descricao = "";
        if (!consultaDebito.isContribuinteGeral()) {
            descricao = consultaDebito.getTipoCadastroTributario().getDescricaoLonga() + ": " + consultaDebito.getCadastro().getNumeroCadastro();
        } else {
            descricao = "Contribuinte: " + consultaDebito.getPessoa();
        }
        return descricao;
    }

    public List<ResultadoParcela> getResultadosParcela() {
        return resultadosParcela;
    }

    public void setResultadosParcela(List<ResultadoParcela> resultadosParcela) {
        this.resultadosParcela = resultadosParcela;
    }
}
