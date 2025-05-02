package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrdenaResultadoParcelaPorVencimento;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoCredorRestituicao;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoRestituicaoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by andregustavo on 19/05/2015.
 */
@ManagedBean(name = "processoRestituicaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoProcessoRestituicao", pattern = "/tributario/conta-corrente/processo-de-debitos/restituicao/novo/", viewId = "/faces/tributario/contacorrente/processodebitorestituicao/edita.xhtml"),
        @URLMapping(id = "editarProcessoRestituicao", pattern = "/tributario/conta-corrente/processo-de-debitos/restituicao/editar/#{processoRestituicaoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitorestituicao/edita.xhtml"),
        @URLMapping(id = "listarProcessoRestituicao", pattern = "/tributario/conta-corrente/processo-de-debitos/restituicao/listar/", viewId = "/faces/tributario/contacorrente/processodebitorestituicao/lista.xhtml"),
        @URLMapping(id = "verProcessoRestituicao", pattern = "/tributario/conta-corrente/processo-de-debitos/restituicao/ver/#{processoRestituicaoControlador.id}/", viewId = "/faces/tributario/contacorrente/processodebitorestituicao/visualizar.xhtml")
})
public class ProcessoRestituicaoControlador extends PrettyControlador<ProcessoRestituicao> implements Serializable, CRUD {
    @EJB
    private ProcessoRestituicaoFacade processoRestituicaoFacade;

    private PessoaRestituicao pessoaSelecionada;
    private RestituicaoPagamento pagamentoSelecionado;
    private List<ResultadoParcela> parcelasConsulta;
    private ObjetoConsultaParcela objetoConsultaParcela;


    public ProcessoRestituicaoControlador() {
        super(ProcessoRestituicao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return processoRestituicaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/conta-corrente/processo-de-debitos/restituicao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "verProcessoRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }


    @Override
    @URLAction(mappingId = "novoProcessoRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        instanciaDependencias();
        objetoConsultaParcela = new ObjetoConsultaParcela();
    }

    @Override
    @URLAction(mappingId = "editarProcessoRestituicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        instanciaDependencias();
        objetoConsultaParcela = new ObjetoConsultaParcela();
    }

    public void poeNaSessao() throws IllegalAccessException {
        Web.poeNaSessao(selecionado);
    }

    private void instanciaDependencias() {
        pessoaSelecionada = new PessoaRestituicao();
        pagamentoSelecionado = new RestituicaoPagamento();
        if (operacao.equals(Operacoes.NOVO)) {
            selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
            selecionado.setData(getSistemaControlador().getDataOperacao());
            selecionado.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
            selecionado.setCodigo(processoRestituicaoFacade.recuperaProximoCodigoPorExercicio(selecionado.getExercicio()));
        }
    }

    public void adicionarBeneficiario() {
        if (validaBeneficiario()) {
            pessoaSelecionada.setProcessoRestituicao(selecionado);
            pessoaSelecionada.setPessoa(processoRestituicaoFacade.getPessoaFacade().recuperar(pessoaSelecionada.getPessoa().getId()));
            selecionado.getPessoas().add(pessoaSelecionada);
            pessoaSelecionada = new PessoaRestituicao();
        }
    }

    public void adicionarCompensacao() {
        if (validaCompensacao()) {
            pagamentoSelecionado.setProcessoRestituicao(selecionado);
            selecionado.getPagamentos().add(pagamentoSelecionado);
            pagamentoSelecionado = new RestituicaoPagamento();
        }
    }

    public void removeBeneficiario(PessoaRestituicao p) {
        if (p.getPessoa().equals(selecionado.getRestituinte())) {
            selecionado.setRestituinte(null);
        }

        if (TipoCredorRestituicao.CONTRIBUINTE.equals(p.getTipoCredorRestituicao())
                && !selecionado.getParcelas().isEmpty()) {
            selecionado.setParcelas(new ArrayList<RestituicaoParcela>());
            selecionado.setPagamentos(new ArrayList<RestituicaoPagamento>());
        }

        selecionado.getPessoas().remove(p);
    }

    public void removerCompensacao(RestituicaoPagamento r) {
        selecionado.getPagamentos().remove(r);
    }

    public void removerPagamento(RestituicaoParcela rp) {
        selecionado.getParcelas().remove(rp);
    }

    public void novaConsultaParcela() {
        if (validaConsultaParcelas()) {
            objetoConsultaParcela = new ObjetoConsultaParcela(getContribuinte());
            parcelasConsulta = new ArrayList<>();
            FacesUtil.executaJavaScript("dialogParcelas.show()");
        }
    }

    public void adicionaParametrosConsulta(ConsultaParcela consulta, ObjetoConsultaParcela objeto) {

        if (objeto.getPessoa() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, objeto.getPessoa().getId());
        }

        if (objeto.getDivida() != null) {
            consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, objeto.getDivida().getId());
        }

        if (objeto.getSituacaoParcela() != null) {
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, objeto.getSituacaoParcela());
        }

    }

    public void consultarParcelas() {
        if (validaCamposConsultaParcelas()) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            adicionaParametrosConsulta(consultaParcela, objetoConsultaParcela);
            if(consultaParcela.getFiltros().isEmpty()){
                consultaParcela.executaConsulta();
                parcelasConsulta.addAll(consultaParcela.getResultados());
            }
            if (!parcelasConsulta.isEmpty()) {
                Collections.sort(parcelasConsulta, new OrdenaResultadoParcelaPorVencimento());
            }
        }

    }


    //-------------------------------------------VALIDATORS-------------------------------------------------------------


    private boolean validaSalvar() {
        boolean valida = true;
        if (selecionado.getProtocolo() == null) {
            FacesUtil.addCampoObrigatorio("O Protocolo é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getAtoLegal() == null) {
            FacesUtil.addCampoObrigatorio("O Ato Legal é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getMotivo() == null || selecionado.getMotivo().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Motivo é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getPessoas().isEmpty()) {
            FacesUtil.addCampoObrigatorio("É necessário identificar os envolvidos no processo na aba \" 1 - Dados do Contribuinte\".");
            valida = false;
        } else {
            if (getContribuinte() == null) {
                FacesUtil.addCampoObrigatorio("O processo de restituição necessita de um envolvido identificado como CONTRIBUINTE.");
                valida = false;
            }

            if (getRestituinte() == null) {
                FacesUtil.addCampoObrigatorio("O processo de restituição necessita de um envolvido identificado como RESTITUINTE.");
                valida = false;
            }
        }

        return valida;
    }

    private boolean validaBeneficiario() {
        boolean valida = true;
        if (pessoaSelecionada.getTipoCredorRestituicao() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A Opção de Credor é um campo obrigatório");
        }

        if (pessoaSelecionada.getPessoa() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A Pessoa é um campo obrigatório");
        }

        if (valida) {
            for (PessoaRestituicao pr : selecionado.getPessoas()) {
                if (pr.getTipoCredorRestituicao().equals(pessoaSelecionada.getTipoCredorRestituicao())) {
                    valida = false;
                    FacesUtil.addOperacaoNaoPermitida("Já existe um " + pessoaSelecionada.getTipoCredorRestituicao().getDescricao() + " inserido no processo.");
                }

                if (pr.getPessoa().equals(pessoaSelecionada.getPessoa())) {
                    valida = false;
                    FacesUtil.addOperacaoNaoPermitida("A pessoa " + pr.getPessoa().getNome() + " já foi inserida no processo como " + pr.getTipoCredorRestituicao().getDescricao());
                }
            }
        }
        return valida;
    }


    private boolean validaCompensacao() {
        boolean valida = true;

        if (pagamentoSelecionado.getPagamentoJudicial() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("A Compensação é um campo obrigatório.");
        } else {
            if (pagamentoSelecionado.getPagamentoJudicial().getSaldo().equals(BigDecimal.ZERO)) {
                FacesUtil.addOperacaoNaoPermitida("Não é posível inserir uma compensação não contenha saldo.");
                valida = false;
            }
            for (RestituicaoPagamento obj : selecionado.getPagamentos()) {
                if (pagamentoSelecionado.equals(obj.getPagamentoJudicial())) {
                    FacesUtil.addOperacaoNaoPermitida("A compensação selecionada já foi inserida ao processo.");
                    valida = false;
                }
            }
        }

        return valida;
    }

    private boolean validaCamposConsultaParcelas() {
        boolean valida = true;

        if (objetoConsultaParcela.getSituacaoParcela() == null) {
            FacesUtil.addCampoObrigatorio("A situação da Dívida é um campo obrigatório");
            valida = false;
        }

        return valida;
    }

    public boolean validaConsultaParcelas() {
        boolean valida = true;
        if (getContribuinte() == null) {
            FacesUtil.addCampoObrigatorio("É necessário identificar um contribuinte do Processo de Restituição.");
            valida = false;
        }
        return valida;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

//------------------------------------------COMPLETE METHODS------------------------------------------------------------

    public List<Processo> completeProcesso(String s) {
        if (s.charAt(0) == '/') {
            s = s.substring(1, s.length());
        }
        String parametros[] = s.trim().split("\\/");
        try {
            Integer numero = Integer.parseInt(parametros[0]);
            Integer ano = null;
            if (parametros.length == 2) {
                ano = Integer.parseInt(parametros[1]);
            }
            return processoRestituicaoFacade.getProcessoFacade().filtraProcesso(numero, ano);
        } catch (NumberFormatException nfe) {
            return new ArrayList<>();
        }
    }

    public List<PagamentoJudicial> completePagamentoJudicial(String s) {
        if (s.charAt(0) == '/') {
            s = s.substring(1, s.length());
        }
        String parametros[] = s.trim().split("\\/");
        try {
            Long codigo = Long.parseLong(parametros[0]);
            Integer ano = null;
            if (parametros.length == 2) {
                ano = Integer.parseInt(parametros[1]);
            }
            return processoRestituicaoFacade.getPagamentoJudicialFacade().filtraPagamentoJudicial(codigo, ano);
        } catch (NumberFormatException nfe) {
            return new ArrayList<>();
        }
    }


    public List<AtoLegal> completaAtoLegal(String parte) {
        return processoRestituicaoFacade.getAtoLegalFacade().listaFiltrando(parte.trim(), "nome");
    }

    //---------------------------------------ACTIVE TABS----------------------------------------------------------------


    //---------------------------------------GETTERS AND SETTERS--------------------------------------------------------

    public List<SelectItem> getRestituintes() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (!selecionado.getPessoas().isEmpty()) {
            toReturn.add(new SelectItem(null, "Selecione um Restituinte...."));
            for (PessoaRestituicao obj : selecionado.getPessoas()) {
                toReturn.add(new SelectItem(obj.getPessoa(), obj.getPessoa().toString()));
            }
        } else {
            toReturn.add(new SelectItem(null, "Identifique os envolvidos na aba 1 - Dados do Contribuinte."));
        }
        return toReturn;
    }

    public ObjetoConsultaParcela getObjetoConsultaParcela() {
        return objetoConsultaParcela;
    }

    public void setObjetoConsultaParcela(ObjetoConsultaParcela objetoConsultaParcela) {
        this.objetoConsultaParcela = objetoConsultaParcela;
    }

    public List<ResultadoParcela> getParcelasConsulta() {
        return parcelasConsulta;
    }

    public void setParcelasConsulta(List<ResultadoParcela> parcelasConsulta) {
        this.parcelasConsulta = parcelasConsulta;
    }

    public List<SelectItem> getCredoresRestituicao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (TipoCredorRestituicao obj : TipoCredorRestituicao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Divida d : processoRestituicaoFacade.getDividaFacade().lista()) {
            retorno.add(new SelectItem(d, d.getDescricao()));
        }
        return retorno;
    }

    public BigDecimal getTotalSaldoCompensacao() {
        BigDecimal total = new BigDecimal(0);
        for (RestituicaoPagamento obj : selecionado.getPagamentos()) {
            total = total.add(obj.getPagamentoJudicial().getSaldo());
        }
        return total;
    }

    public BigDecimal getTotalPagamentos() {
        BigDecimal total = new BigDecimal(0);
        for (RestituicaoParcela obj : selecionado.getParcelas()) {
            total = total.add(obj.getValorPago());
        }
        return total;
    }

    public BigDecimal getValorUFMDataPagamento(RestituicaoParcela obj) {
        try {
            return processoRestituicaoFacade.getMoedaFacade().converterToUFMComData(obj.getValorPago(), obj.getPagamentoParcela());
        } catch (UFMException e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar o valor do UFM vigente na data " + new SimpleDateFormat("dd/MM/yyyy").format(obj.getPagamentoParcela()) + ".");
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalPagamentosAtualizado() {
        BigDecimal totalUFM = new BigDecimal(0);
        BigDecimal totalReal = new BigDecimal(0);
        if (selecionado.getAtualizacaoMonetaria()) {
            for (RestituicaoParcela obj : selecionado.getParcelas()) {
                totalUFM = totalUFM.add(getValorUFMDataPagamento(obj));
            }
        }
        try {
            totalReal = processoRestituicaoFacade.getMoedaFacade().converterToReal(totalUFM);
            return totalReal;
        } catch (UFMException e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoPermitida("Não foi possível recuperar o valor do UFM vigente.");
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalRestituicao() {
        BigDecimal total = new BigDecimal(0);
        if (selecionado.getAtualizacaoMonetaria()) {
            total = getTotalPagamentosAtualizado().add(getTotalSaldoCompensacao());
        } else {
            total = getTotalPagamentos().add(getTotalSaldoCompensacao());
        }
        return total;
    }

    public BigDecimal getTotalCompensado() {
        BigDecimal total = new BigDecimal(0);
        for (RestituicaoPagamento obj : selecionado.getPagamentos()) {
            total = total.add(obj.getPagamentoJudicial().getValorCompensado());
        }
        return total;
    }

    public BigDecimal getTotalACompensar() {
        BigDecimal total = new BigDecimal(0);
        for (RestituicaoPagamento obj : selecionado.getPagamentos()) {
            total = total.add(obj.getPagamentoJudicial().getValorACompensar());
        }
        return total;
    }

    public PessoaRestituicao getPessoaSelecionada() {
        return pessoaSelecionada;
    }

    public void setPessoaSelecionada(PessoaRestituicao pessoaSelecionada) {
        this.pessoaSelecionada = pessoaSelecionada;
    }

    public RestituicaoPagamento getPagamentoSelecionado() {
        return pagamentoSelecionado;
    }

    public void setPagamentoSelecionado(RestituicaoPagamento pagamentoSelecionado) {
        this.pagamentoSelecionado = pagamentoSelecionado;
    }

    public Pessoa getContribuinte() {
        for (PessoaRestituicao pr : selecionado.getPessoas()) {
            if (TipoCredorRestituicao.CONTRIBUINTE.equals(pr.getTipoCredorRestituicao())) {
                return pr.getPessoa();
            }
        }
        return null;
    }

    public Pessoa getRestituinte() {
        for (PessoaRestituicao pr : selecionado.getPessoas()) {
            if (pr.getRestituinte()) {
                return pr.getPessoa();
            }
        }
        return null;
    }

    public void atribuiRestituinte(PessoaRestituicao p) {
        for (PessoaRestituicao pr : selecionado.getPessoas()) {
            if (!p.equals(pr)) {
                pr.setRestituinte(false);
            } else if (p.getRestituinte()) {
                selecionado.setRestituinte(p.getPessoa());
            } else {
                selecionado.setRestituinte(null);
            }
        }
    }

    public void addRestituicaoParcela(ResultadoParcela resultadoParcela) {
        RestituicaoParcela restituicaoParcela = new RestituicaoParcela();
        restituicaoParcela.setReferencia(resultadoParcela.getReferencia());
        restituicaoParcela.setTipoDebito(resultadoParcela.getTipoDeDebito());
        restituicaoParcela.setPagamentoParcela(resultadoParcela.getPagamento());
        restituicaoParcela.setDivida(resultadoParcela.getDivida());
        restituicaoParcela.setTipoCadastro(resultadoParcela.getTipoCadastro());
        restituicaoParcela.setSituacaoParcela(SituacaoParcela.fromDto(resultadoParcela.getSituacaoEnumValue()));
        restituicaoParcela.setProcessoRestituicao(selecionado);
        restituicaoParcela.setValorPago(resultadoParcela.getValorPago());
        restituicaoParcela.setParcelaValorDivida(processoRestituicaoFacade.recuperaParcelaValorDivida(resultadoParcela.getIdParcela()));
        selecionado.getParcelas().add(restituicaoParcela);
        parcelasConsulta.remove(resultadoParcela);
    }

    @Override
    public void salvar() {
        if (validaSalvar()) {
            selecionado.setValorRestituicao(getTotalRestituicao());
            selecionado = processoRestituicaoFacade.salvaProcessoRestituicao(selecionado);
            FacesUtil.addOperacaoRealizada("Salvo com sucesso");
            Web.navegacao(getUrlAtual(), new StringBuilder("/tributario/conta-corrente/processo-de-debitos/restituicao/ver/").append(selecionado.getId()).append("/").toString());
        }
    }

    public class ObjetoConsultaParcela {
        private Pessoa pessoa;
        private TipoCadastroTributario tipoCadastro;
        private Divida divida;
        private Date dataPagamento;
        private SituacaoParcela situacaoParcela;

        public ObjetoConsultaParcela() {
            tipoCadastro = TipoCadastroTributario.PESSOA;
        }

        public ObjetoConsultaParcela(Pessoa pessoa) {
            tipoCadastro = TipoCadastroTributario.PESSOA;
            this.pessoa = pessoa;
        }

        public Pessoa getPessoa() {
            return pessoa;
        }

        public void setPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
        }

        public TipoCadastroTributario getTipoCadastro() {
            return tipoCadastro;
        }

        public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
            this.tipoCadastro = tipoCadastro;
        }

        public Divida getDivida() {
            return divida;
        }

        public void setDivida(Divida divida) {
            this.divida = divida;
        }

        public Date getDataPagamento() {
            return dataPagamento;
        }

        public void setDataPagamento(Date dataPagamento) {
            this.dataPagamento = dataPagamento;
        }

        public List<SelectItem> getSituacoesPossiveisParcela() {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, "Selecione uma situação..."));
            for (SituacaoParcela situacao : SituacaoParcela.values()) {
                if (SituacaoParcela.BAIXADO.equals(situacao)
                        || SituacaoParcela.PAGO.equals(situacao)) {
                    toReturn.add(new SelectItem(situacao, situacao.getDescricao()));
                }
            }
            return toReturn;
        }

        public SituacaoParcela getSituacaoParcela() {
            return situacaoParcela;
        }

        public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
            this.situacaoParcela = situacaoParcela;
        }
    }
}
