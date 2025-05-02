/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.StatusAidf;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.viewobjects.FiltroAIDF;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author leonardo
 */
@ManagedBean(name = "cadastroAidfControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCadastroAidf", pattern = "/aidf/novo/", viewId = "/faces/tributario/cadastromunicipal/cadastroaidf/edita.xhtml"),
    @URLMapping(id = "editarCadastroAidf", pattern = "/aidf/editar/#{cadastroAidfControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroaidf/edita.xhtml"),
    @URLMapping(id = "listarCadastroAidf", pattern = "/aidf/listar/", viewId = "/faces/tributario/cadastromunicipal/cadastroaidf/lista.xhtml"),
    @URLMapping(id = "cancelarCadastroAidf", pattern = "/aidf/cancelar/#{cadastroAidfControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroaidf/cancela.xhtml"),
    @URLMapping(id = "verCadastroAidf", pattern = "/aidf/ver/#{cadastroAidfControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cadastroaidf/visualizar.xhtml")
})
public class CadastroAidfControlador extends PrettyControlador<CadastroAidf> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(CadastroAidfControlador.class);

    @EJB
    private CadastroAidfFacade cadastroAidfFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private NumeroSerieFacade numeroSerieFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GraficaFacade graficaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private EnderecoFacade enderecoFacade;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterNumeroSerie;
    private ConverterAutoComplete converterGrafica;
    private String caminho;
    private List<EnderecoCorreio> enderecos;
    private List<Telefone> telefones;
    private FiltroAIDF filtros;
    private List<OrdenarPor> ordenarPor;
    private List<CadastroAidf> lista;
    private int inicio = 0;
    private int maximoRegistrosTabela;
    private RelatoriosAidf relatoriosAidf;

    public CadastroAidfControlador() {
        super(CadastroAidf.class);
        relatoriosAidf = new RelatoriosAidf();
    }

    public List<OrdenarPor> getOrdenarPor() {
        return ordenarPor;
    }

    public void setOrdenarPor(List<OrdenarPor> ordenarPor) {
        this.ordenarPor = ordenarPor;
    }

    public FiltroAIDF getFiltros() {
        if (filtros == null) {
            filtros = new FiltroAIDF();
        }
        return filtros;
    }

    public void setFiltros(FiltroAIDF filtros) {
        this.filtros = filtros;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aidf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return cadastroAidfFacade;
    }

    @Override
    @URLAction(mappingId = "novoCadastroAidf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        this.selecionado.setNotaFiscalInicialAutorizado("0");
        this.selecionado.setNumeroAidf(cadastroAidfFacade.getProximoNumeroAIDF());
    }

    @URLAction(mappingId = "cancelarCadastroAidf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void cancelarAidf() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "verCadastroAidf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        telefones = pessoaFacade.telefonePorPessoa(selecionado.getCadastroEconomico().getPessoa());
        enderecos = enderecoFacade.retornaEnderecoCorreioDaPessoa(selecionado.getCadastroEconomico().getPessoa());
    }

    @Override
    @URLAction(mappingId = "editarCadastroAidf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        telefones = pessoaFacade.telefonePorPessoa(selecionado.getCadastroEconomico().getPessoa());
        enderecos = enderecoFacade.retornaEnderecoCorreioDaPessoa(selecionado.getCadastroEconomico().getPessoa());
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return this.cadastroEconomicoFacade.listaCadastroEconomicoPorPessoa(parte.trim());
    }

    public Converter getConverterCadastroEconomico() {
        if (this.converterCadastroEconomico == null) {
            this.converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return this.converterCadastroEconomico;
    }

    public List<NumeroSerie> completaNumeroSerie(String parte) {
        return this.numeroSerieFacade.listaFiltrando(parte.trim(), "descricao");

    }

    public Converter getConverterNumeroSerie() {
        if (this.converterNumeroSerie == null) {
            this.converterNumeroSerie = new ConverterAutoComplete(NumeroSerie.class, numeroSerieFacade);
        }
        return this.converterNumeroSerie;
    }

    public List<Grafica> completaGrafica(String parte) {
        return this.graficaFacade.listaPorPessoa(parte.trim());
    }

    public Converter getConverterGrafica() {

        if (this.converterGrafica == null) {
            this.converterGrafica = new ConverterAutoComplete(Grafica.class, graficaFacade);
        }
        return this.converterGrafica;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (this.selecionado.getCadastroEconomico() == null) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o C.M.C.");
        }

        if (this.selecionado.getNumeroSerie() == null || this.selecionado.getNumeroSerie().getDescricao().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número de série.");
        }

        if (this.selecionado.getData() == null || this.selecionado.getData().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe a data.");
        }

        if (this.selecionado.getNumeroAidf() == null || this.selecionado.getNumeroAidf().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número da AIDF.");
        } else if (cadastroAidfFacade.existeEsteNumeroAIDF(selecionado)) {
            retorno = false;
            FacesUtil.addError("Atenção!", "O número da AIDF informado já existe.");
        }

        if (this.selecionado.getNotaFiscalInicial() == null || this.selecionado.getNotaFiscalInicial().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número inicial da nota fiscal solicitada.");
        }

        if (this.selecionado.getNotaFiscalFinal() == null || this.selecionado.getNotaFiscalFinal().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número final da nota fiscal solicitada.");
        } else if (Long.parseLong(this.selecionado.getNotaFiscalFinal()) <= Long.parseLong(this.selecionado.getNotaFiscalInicial())) {
            retorno = false;
            FacesUtil.addError("Atenção!", "O número final da nota fiscal deve ser maior que o número inicial.");
        }

        if (this.selecionado.getGrafica() == null || this.selecionado.getGrafica().getId() == null) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe a gráfica.");
        }

        if (this.selecionado.getNotaFiscalInicialAutorizado().equals("0")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe selecione o C.M.C. desejado e o número de série da nota fiscal.");
        }

        if (this.selecionado.getNotaFiscalFinalAutorizado() == null || this.selecionado.getNotaFiscalFinalAutorizado().equals("")) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número final da nota fiscal autorizada.");
        } else if (Long.parseLong(
            this.selecionado.getNotaFiscalFinalAutorizado()) <= Long.parseLong(this.selecionado.getNotaFiscalInicialAutorizado())) {
            retorno = false;
            FacesUtil.addError("Atenção!", " O número final da nota fiscal deve ser maior que o número inicial.");
        }

        if (this.selecionado.getValidadeNotaFiscal() == null) {
            retorno = false;
            FacesUtil.addError("Atenção!", "A Data de Validade das Notas Fiscais é obrigatório!");
        }
        return retorno;
    }

    public List<CadastroAidf> getLista() {
        return lista;
    }

    @URLAction(mappingId = "listarCadastroAidf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void iniciaPesquisa() {
        ordenarPor = Lists.newArrayList();
        filtros = new FiltroAIDF();
        lista = Lists.newArrayList();
    }

    public void filtrar() {
        try {
            lista = cadastroAidfFacade.filtrar(ordenarPor, filtros, maximoRegistrosTabela, inicio);
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
    }

    public void selecionarUltimaNotaFiscal(SelectEvent event) {
        if (event.getObject() instanceof CadastroEconomico) {
            selecionado.setCadastroEconomico((CadastroEconomico) event.getObject());
        } else {
            selecionado.setNumeroSerie((NumeroSerie) event.getObject());
        }
        selecionado.setNotaFiscalInicialAutorizado(cadastroAidfFacade.ultimoMaisUmNotaFiscalInicial(selecionado.getCadastroEconomico(), selecionado.getNumeroSerie()));
    }

    public boolean verificaUltimoAidfPorPessoa() {
        if (selecionado.getId().compareTo(cadastroAidfFacade.verificaUltimoAidfPorPessoa(selecionado)) == 0) {
            return true;
        }
        return false;
    }

    public void efetuarCancelamentoAidf() {
        if (validaCamposCancelamento()) {
            try {
                selecionado.setUsuario(sistemaFacade.getUsuarioCorrente());
                selecionado.setStatusAidf(StatusAidf.CANCELADO);
                cadastroAidfFacade.salvar(selecionado);

                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Cancelado com sucesso!");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addFatal("Exceção do sistema!", "Não foi possível cancelar a AIDF");
            }
        }
    }

    public List<SelectItem> getQuantidadesRegistros() {
        List<SelectItem> retorno = new ArrayList<>();
        for (QuantidadeRegistros qnt : QuantidadeRegistros.values()) {
            retorno.add(new SelectItem(qnt, qnt.getValor().toString()));
        }
        return retorno;
    }

    public List<SelectItem> getOrdenacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        for (OrdenarPor ord : OrdenarPor.values()) {
            retorno.add(new SelectItem(ord, ord.getDescricao()));
        }
        return retorno;
    }

    public boolean validaCamposCancelamento() {
        boolean retorno = true;

        if (this.selecionado.getNumeroProcesso().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o número do processo.");
        }
        if (this.selecionado.getMotivoCancelamento().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Atenção!", "Informe o motivo do cancelamento.");
        }

        return retorno;
    }

    public boolean isCancelado() {
        return selecionado.getStatusAidf().equals(StatusAidf.CANCELADO);
    }

    public boolean isFisica() {
        if (selecionado.getCadastroEconomico() == null) {
            return false;
        }
        if ((selecionado.getCadastroEconomico().getPessoa() instanceof PessoaFisica)) {
            return true;

        } else {
            return false;
        }
    }

    public boolean isJuridica() {
        if (selecionado.getCadastroEconomico() == null) {
            return false;
        }
        if ((selecionado.getCadastroEconomico().getPessoa() instanceof PessoaJuridica)) {
            return true;

        } else {
            return false;
        }
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecos = enderecos;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        return new SituacaoCadastroEconomico();
    }

    public List<Pessoa> recuperaPessoasCadastro() {
        return pessoaFacade.recuperaPessoasDoCadastro(selecionado.getCadastroEconomico());
    }

    public enum QuantidadeRegistros {

        DEZ(10, "Dez"), VINTECINCO(25, "Vinte e Cinco"), CINQUENTA(50, "Cinquenta"), CEM(100, "Cem"), DUZENTOSCINQUENTA(250, "Duzentos e Cinquenta"), QUINHENTOS(550, "Quinhentos");
        private Integer valor;
        private String descricao;

        public Integer getValor() {
            return valor;
        }

        public String getDescricao() {
            return descricao;
        }

        private QuantidadeRegistros(Integer valor, String descricao) {
            this.valor = valor;
            this.descricao = descricao;
        }
    }

    public int getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    public void uploadArquivos(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(cadastroAidfFacade.getArquivoFacade().getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            ArquivoAidf arquivoAidf = new ArquivoAidf();
            arquivoAidf.setArquivo(cadastroAidfFacade.getArquivoFacade().novoArquivoMemoria(arq, file.getFile().getInputstream()));
            arquivoAidf.setCadastroAidf(selecionado);

            selecionado.getArquivos().add(arquivoAidf);
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
    }

    public void removerArquivo(ActionEvent evento) {
        ArquivoAidf arq = (ArquivoAidf) evento.getComponent().getAttributes().get("objeto");
        selecionado.getArquivos().remove(arq);
    }

    public Converter getConverterOrdenacao() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                for (OrdenarPor ordem : OrdenarPor.values()) {
                    if (ordem.getDescricao().equals(string)) {
                        return ordem;
                    }
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                return ((OrdenarPor) o).getDescricao();
            }
        };
    }

    public List<SituacaoCadastralCadastroEconomico> getSituacoesDisponiveis() {
        List<SituacaoCadastralCadastroEconomico> situacoes = new ArrayList<>();
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacoes.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
        return situacoes;
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setCadastroEconomico((CadastroEconomico) obj);
    }

    public ComponentePesquisaGenerico getComponentePesquisa() {
        PesquisaCadastroEconomicoControlador componente = (PesquisaCadastroEconomicoControlador) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
        List<SituacaoCadastralCadastroEconomico> situacao = Lists.newArrayList();
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
        situacao.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);

        componente.setSituacao(situacao);
        return (ComponentePesquisaGenerico) Util.getControladorPeloNome("pesquisaCadastroEconomicoControlador");
    }

    public enum OrdenarPor {

        numeroAidf("Nº AIDF", "cast(c.numeroAidf as int)"),
        NUMSERIE("Nº de Série", "c.numeroSerie.numeroSerie"),
        CMC("C.M.C.", "c.cadastroEconomico.inscricaoCadastral"),
        DATA("Data", "c.data"),
        RSOCIAL("Nome/Razão Social", "c.cadastroEconomico.pessoa"),
        GRAFICA("Gráfica", "c.grafica");
        private String descricao;
        private String ordenaPor;

        public String getOrdenaPor() {
            return ordenaPor;
        }

        public void setOrdenaPor(String ordenaPor) {
            this.ordenaPor = ordenaPor;
        }

        private OrdenarPor(String ordenaPor) {
            this.ordenaPor = ordenaPor;
        }

        private OrdenarPor(String descricao, String ordenaPor) {
            this.descricao = descricao;
            this.ordenaPor = ordenaPor;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return ordenaPor;
        }
    }

    public void limparFiltrosETabela() {
        this.filtros = new FiltroAIDF();
        this.lista = new ArrayList<>();
    }

    public void iniciarPesquisa() {
        inicio = 0;
        filtrar();
    }

    public boolean isTemMaisResultados() {
        if (lista == null) {
            filtrar();
        }

        return lista == null ? false : lista.size() > maximoRegistrosTabela;
    }

    public boolean isTemAnterior() {
        return inicio > 0;
    }

    public void acaoBotaoFiltrar() {
        inicio = 0;
        filtrar();
    }

    public void proximos() {
        inicio += maximoRegistrosTabela;
        filtrar();
    }

    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        filtrar();
    }

    public void navegar(String destino) {
        String origem = getCaminhoPadrao();
        if (selecionado.getId() == null) {
            origem += "novo/";
        } else {
            origem += "editar/" + getUrlKeyValue() + "/";
        }
        Web.navegacao(origem, destino, selecionado);
    }

    public void novoCadastroEconomico() {
        navegar("/tributario/cadastroeconomico/novo/");
    }

    public void novoNumeroDeSerie() {
        navegar("/numero-de-serie/novo/");
    }

    public void novaGrafica() {
        navegar("/grafica/novo/");
    }

    public RelatoriosAidf getRelatoriosAidf() {
        return relatoriosAidf;
    }

    public class RelatoriosAidf extends AbstractReport {


        public RelatoriosAidf() {
        }


        public void imprimirDetalhamento() {
            HashMap parameters = new HashMap();
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("ID_AIDF", selecionado.getId());
            this.setGeraNoDialog(true);
            try {
                gerarRelatorio("RelatorioDetalhamentoAidf.jasper", parameters);
            } catch (JRException ex) {
                logger.error("{}", ex);
            } catch (IOException ex) {
                logger.error("{}", ex);
            }
        }

    }
}
