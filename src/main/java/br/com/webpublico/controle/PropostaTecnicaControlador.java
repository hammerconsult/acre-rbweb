package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PropostaTecnicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.LazyInitializationException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/09/14
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-proposta-tecnica", pattern = "/licitacao/proposta-tecnica/novo/", viewId = "/faces/administrativo/licitacao/propostatecnica/edita.xhtml"),
    @URLMapping(id = "editar-proposta-tecnica", pattern = "/licitacao/proposta-tecnica/editar/#{propostaTecnicaControlador.id}/", viewId = "/faces/administrativo/licitacao/propostatecnica/edita.xhtml"),
    @URLMapping(id = "listar-proposta-tecnica", pattern = "/licitacao/proposta-tecnica/listar/", viewId = "/faces/administrativo/licitacao/propostatecnica/lista.xhtml"),
    @URLMapping(id = "ver-proposta-tecnica", pattern = "/licitacao/proposta-tecnica/ver/#{propostaTecnicaControlador.id}/", viewId = "/faces/administrativo/licitacao/propostatecnica/visualizar.xhtml")
})
public class PropostaTecnicaControlador extends PrettyControlador<PropostaTecnica> implements Serializable, CRUD {

    @EJB
    private PropostaTecnicaFacade propostaTecnicaFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterRepresentante;
    private LicitacaoFornecedor licitacaoFornecedorSelecionado;

    public PropostaTecnicaControlador() {
        super(PropostaTecnica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return propostaTecnicaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/proposta-tecnica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-proposta-tecnica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarDados();
    }

    private void inicializarDados() {
        selecionado.setData(propostaTecnicaFacade.getSistemaFacade().getDataOperacao());
        instanciarLicitacaoFornecedorSelecionado();
    }

    @Override
    @URLAction(mappingId = "ver-proposta-tecnica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @Override
    @URLAction(mappingId = "editar-proposta-tecnica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarListasDaLicitacaoSelecionada();
        recuperaDados();
        try {
            propostaTecnicaFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    private void recuperaDados() {
        instanciarLicitacaoFornecedorSelecionado();
        recuperaLicitacaoFornecedorDaPessoa();
    }

    @Override
    public void salvar() {
        try {
            validarProposta();
            propostaTecnicaFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            propostaTecnicaFacade.salvarPropostaEFornecedorClassificado(selecionado, licitacaoFornecedorSelecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (StatusLicitacaoException se) {
            redireciona();
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        propostaTecnicaFacade.inabilitarFornecedor(licitacaoFornecedorSelecionado);
        super.excluir();
    }

    private void validarProposta() {
        ValidacaoException ve = new ValidacaoException();
        validarConfirmacao(selecionado, ve);
        validarRepresentante(ve);
        if (ve.temMensagens()) throw ve;
    }

    public boolean isMapaGerado() {
        if (selecionado.getLicitacao() != null) {
            return propostaTecnicaFacade.getTecnicaPrecoFacade().temMapaComparativo(selecionado.getLicitacao());
        } else {
            return false;
        }
    }

    private void validarRepresentante(ValidacaoException ve) {
        if (selecionado.getFornecedor().isPessoaJuridica() && selecionado.getRepresentante() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar o representante do fornecedor. Fornecedor informado é uma pessoa jurídica.");
        }
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, propostaTecnicaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterRepresentante() {
        if (converterRepresentante == null) {
            converterRepresentante = new ConverterAutoComplete(RepresentanteLicitacao.class, propostaTecnicaFacade.getLicitacaoFacade().getRepresentanteLicitacaoFacade());
        }
        return converterRepresentante;
    }

    public LicitacaoFornecedor getLicitacaoFornecedorSelecionado() {
        return licitacaoFornecedorSelecionado;
    }

    public void setLicitacaoFornecedorSelecionado(LicitacaoFornecedor licitacaoFornecedorSelecionado) {
        this.licitacaoFornecedorSelecionado = licitacaoFornecedorSelecionado;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return propostaTecnicaFacade.getLicitacaoFacade().buscarLicitacaoPorTipoAvaliacaoAndNumeroOrDescricaoOrExercico(parte.trim(), TipoAvaliacaoLicitacao.TECNICA_PRECO);
    }

    public List<Pessoa> completaPessoa(String parte) {
        instanciarLicitacaoFornecedorSelecionado();
        List<Pessoa> retorno = new ArrayList<>();
        if (selecionado.getLicitacao() == null) {
            FacesUtil.addOperacaoNaoRealizada("É obrigatório informar a licitação primeiro.");
            return retorno;
        }
        carregarListasDaLicitacaoSelecionada();
        for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
            if (selecionado.getLicitacao().getModalidadeLicitacao().isConcorrencia() || selecionado.getLicitacao().getModalidadeLicitacao().isTomadaPrecos()){
                if (isFornecedorHabilitadoOuHabilitadoComRessalva(licitacaoFornecedor)) {
                    retorno.add(licitacaoFornecedor.getEmpresa());
                }
            } else {
                retorno.add(licitacaoFornecedor.getEmpresa());
            }
        }
        removerFornecedoresQueJaFizeramPropostaParaALicitacao(selecionado.getLicitacao(), retorno);
        informarUsuarioTodosFornecedoresComPropostaRealizada(retorno);
        return retorno;
    }

    private boolean isFornecedorHabilitadoOuHabilitadoComRessalva(LicitacaoFornecedor licitacaoFornecedor) {
        return TipoClassificacaoFornecedor.HABILITADO.equals(licitacaoFornecedor.getTipoClassificacaoFornecedor()) || TipoClassificacaoFornecedor.HABILITADOCOMRESSALVA.equals(licitacaoFornecedor.getTipoClassificacaoFornecedor());
    }

    private void removerFornecedoresQueJaFizeramPropostaParaALicitacao(Licitacao licitacao, List<Pessoa> fornecedores) {
        List<PropostaTecnica> propostasDaLicitacao = propostaTecnicaFacade.buscarPorLicitacao(licitacao);
        List<Pessoa> fornecedoresDasPropostas = getFornecedoresDasPropostas(propostasDaLicitacao);
        for (Pessoa fornecedor : fornecedoresDasPropostas) {
            if (fornecedores.contains(fornecedor)) {
                fornecedores.remove(fornecedor);
            }
        }
    }

    private void informarUsuarioTodosFornecedoresComPropostaRealizada(List<Pessoa> fornecedores) {
        if (fornecedores.isEmpty()) {
            FacesUtil.addAtencao("Todos Fornecedores desta licitação já fizeram proposta técnica.");
        }
    }

    private List<Pessoa> getFornecedoresDasPropostas(List<PropostaTecnica> propostasDaLicitacao) {
        List<Pessoa> fornecedoresDasPropostas = new ArrayList<>();
        for (PropostaTecnica propostaTecnica : propostasDaLicitacao) {
            fornecedoresDasPropostas.add(propostaTecnica.getFornecedor());
        }
        return fornecedoresDasPropostas;
    }

    public List<Pessoa> completaPessoaFisica(String parte) {
        return propostaTecnicaFacade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
    }

    public List<RepresentanteLicitacao> completarRepresentante(String parte) {
        return propostaTecnicaFacade.getLicitacaoFacade().getRepresentanteLicitacaoFacade().buscarRepresentanteLicitacao(parte.trim());
    }

    public void validaLicitacao() {
        if (selecionado.getLicitacao() != null) {
            limparItensDaProposta();
            carregarListasDaLicitacaoSelecionada();
            if (licitacaoSemFornecedor()) {
                cancelarLicitacaoSelecionada();
                FacesUtil.addOperacaoNaoRealizada("Adicione (o)s fornecedor(es) antes de fazer a proposta técnica.");
                return;
            }
            if (licitacaoComTodosFornecedoresInabilitados()) {
                cancelarLicitacaoSelecionada();
                FacesUtil.addOperacaoNaoRealizada("A licitação selecionada está com todos os fornecedores inabilitados.");
                return;
            }
            if (isMapaGerado()) {
                cancelarLicitacaoSelecionada();
                FacesUtil.addOperacaoNaoRealizada("A licitação selecionada já possui Mapa Comparativo Técnica e Preço gerado.");
                return;
            }
            criarItensDaPropostaBuscandoItensDaSolicitacaoMaterial();
            selecionado.ordenarListas();
            selecionado.setNotaTecnica(BigDecimal.ZERO);
        }
    }

    private void limparItensDaProposta() {
        selecionado.setItens(new ArrayList<ItemPropostaTecnica>());
    }

    private void instanciarLicitacaoFornecedorSelecionado() {
        licitacaoFornecedorSelecionado = new LicitacaoFornecedor();
    }

    private void criarItensDaPropostaBuscandoItensDaSolicitacaoMaterial() {
        List<ItemCriterioTecnica> itensCriterioTecnicaRecuperadosDaSolicitacao = recuperaItensCriterioTecnicaDaSolicitacaoMaterial();

        for (ItemCriterioTecnica itemCriterioTecnica : itensCriterioTecnicaRecuperadosDaSolicitacao) {
            ItemPropostaTecnica itemPropostaTecnica = populaItemPropostaTecnica(itemCriterioTecnica);
            selecionado.setItens(Util.adicionarObjetoEmLista(selecionado.getItens(), itemPropostaTecnica));
        }
    }

    private ItemPropostaTecnica populaItemPropostaTecnica(ItemCriterioTecnica itemCriterioTecnica) {
        ItemPropostaTecnica itemPropostaTecnica = new ItemPropostaTecnica();
        itemPropostaTecnica.setPropostaTecnica(selecionado);
        itemPropostaTecnica.setItemCriterioTecnica(itemCriterioTecnica);
        return itemPropostaTecnica;
    }

    private List<ItemCriterioTecnica> recuperaItensCriterioTecnicaDaSolicitacaoMaterial() {
        List<ItemCriterioTecnica> itensCriterioTecnica = new ArrayList<>();
        for (LoteProcessoDeCompra loteProcessoDeCompra : selecionado.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
            for (ItemProcessoDeCompra itemProcessoDeCompra : loteProcessoDeCompra.getItensProcessoDeCompra()) {
                SolicitacaoMaterial solicitacaoMaterial = itemProcessoDeCompra.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial();
                solicitacaoMaterial = propostaTecnicaFacade.getSolicitacaoMaterialFacade().recuperar(solicitacaoMaterial.getId());
                itensCriterioTecnica = solicitacaoMaterial.getCriterioTecnicaSolicitacao().getItens();
            }
        }
        return itensCriterioTecnica;
    }

    private boolean licitacaoComTodosFornecedoresInabilitados() {
        if (!selecionado.getLicitacao().getModalidadeLicitacao().isConcorrencia() && !selecionado.getLicitacao().getModalidadeLicitacao().isTomadaPrecos()) {
            return false;
        }
        for (LicitacaoFornecedor fornecedor : selecionado.getLicitacao().getFornecedores()) {
            if (fornecedor.isFornecedorHabilitado()) {
                return false;
            }
        }
        return true;
    }

    private void cancelarLicitacaoSelecionada() {
        selecionado.setLicitacao(null);
        FacesUtil.atualizarComponente("Formulario:tab-view-geral:licitacao");
    }

    private void carregarListasDaLicitacaoSelecionada() {
        try {
            selecionado.getLicitacao().getFornecedores().size();
            selecionado.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra().size();
        } catch (LazyInitializationException lie) {
            selecionado.setLicitacao(propostaTecnicaFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
        }
    }

    private boolean licitacaoSemFornecedor() {
        return selecionado.getLicitacao().getFornecedores() == null || selecionado.getLicitacao().getFornecedores().isEmpty();
    }

    public boolean isUmFornecedorInabilitado(LicitacaoFornecedor lf) {
        return lf.isFornecedorInabilitado();
    }

    public void setaRepresentanteDoFornecedorSelecionado() {
        licitacaoFornecedorSelecionado = selecionado.getLicitacao().getLicitacaoFornecedorPorEmpresa(licitacaoFornecedorSelecionado.getEmpresa());
        if (!selecionado.getLicitacao().isRDCMapaComparativo()) {
            if (isUmFornecedorInabilitado(licitacaoFornecedorSelecionado)) {
                FacesUtil.addOperacaoNaoPermitida("O Fornecedor " + licitacaoFornecedorSelecionado.getEmpresa() + " está inabilitado referente a documentação.");
                setLicitacaoFornecedorSelecionado(new LicitacaoFornecedor());
                selecionado.setRepresentante(null);
                return;
            }
        }
        if (isUmFornecedorQueJaFezSuaProposta()) {
            selecionado.setFornecedor(licitacaoFornecedorSelecionado.getEmpresa());
            selecionado.setRepresentante(licitacaoFornecedorSelecionado.getRepresentante());
        }
    }

    public void cancelarFornecedorSelecionado() {
        licitacaoFornecedorSelecionado.setEmpresa(null);
    }

    private boolean isUmFornecedorQueJaFezSuaProposta() {
        if (propostaTecnicaFacade.fornecedorJaFezPropostaTecnica(licitacaoFornecedorSelecionado.getEmpresa(), selecionado.getLicitacao())) {
            return true;
        }
        FacesUtil.atualizarComponente("form-proposta-fornecedor");
        FacesUtil.executaJavaScript("propostaFornecedorDialog.show()");
        return false;
    }

    public void visualizarProposta() {
        PropostaTecnica propostaRecuperada = propostaTecnicaFacade.recuperarPropostaDoFornecedorPorLicitacao(licitacaoFornecedorSelecionado.getEmpresa(), selecionado.getLicitacao());
        if (propostaRecuperada != null) {
            selecionado = propostaRecuperada;
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + propostaRecuperada.getId() + "/");
        }
        cancelarFornecedorSelecionado();
    }

    public void calcularPontuacao(ItemPropostaTecnica item) {
        try {
            validarItemPropostaTecnica(item);
            if (item.getItemCriterioTecnica().getTipoItemCriterioTecnica().equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUALITATIVO)) {
                item.getItemCriterioTecnica().getPontuacaoTecnicaQualitativaReferenteQuantidadeInformada(item.getQuantidade());
                item.setPonto(item.getQuantidade());
            }

            if (item.getItemCriterioTecnica().getTipoItemCriterioTecnica().equals(ItemCriterioTecnica.TipoItemCriterioTecnica.QUANTITATIVO)) {
                PontuacaoTecnica pontuacao = item.getItemCriterioTecnica().getPontuacaoTecnicaReferenteQuantidadeInformada(item.getQuantidade());
                item.setPonto(pontuacao.getPonto());
            }
        } catch (Exception ex) {
            cancelarQuantidadeEPontoDosItensPropostaTecnica(item);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
        processaClassificacaoTecnica();
    }

    private void cancelarQuantidadeEPontoDosItensPropostaTecnica(ItemPropostaTecnica item) {
        item.setQuantidade(null);
        item.setPonto(null);
    }

    private void validarItemPropostaTecnica(ItemPropostaTecnica item) {
        if (selecionado.getFornecedor() == null) {
            throw new ValidacaoException("É obrigatório informar o fornecedor antes de realizar o lançamento do critério técnico.");
        }
        validarQuantidadeInformada(item);
    }

    private void validarQuantidadeInformada(ItemPropostaTecnica item) {
        if (item.getQuantidade() == null) {
            throw new ValidacaoException("Quantidade não informada.");
        } else {
            if (!item.isQuantidadeValida()) {
                throw new ValidacaoException("A quantidade informada não foi encontrada em nenhum intervalo deste item.");
            }
        }
    }

    private void processaClassificacaoTecnica() {
        if (selecionado.getTotalPontos().compareTo(getValorParaHabilitacao()) >= 0) {
            licitacaoFornecedorSelecionado.setClassificacaoTecnica(TipoClassificacaoFornecedor.HABILITADO);
        } else {
            licitacaoFornecedorSelecionado.setClassificacaoTecnica(TipoClassificacaoFornecedor.INABILITADO);
        }
    }

    private void recuperaLicitacaoFornecedorDaPessoa() {
        for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
            if (licitacaoFornecedor.getEmpresa().equals(selecionado.getFornecedor())) {
                licitacaoFornecedorSelecionado = licitacaoFornecedor;
            }
        }
    }

    public BigDecimal getValorParaHabilitacao() {
        BigDecimal valorHabilitacao = BigDecimal.ZERO;
        try {
            carregarListasDaLicitacaoSelecionada();
            for (LoteProcessoDeCompra loteProcessoDeCompra : selecionado.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
                for (ItemProcessoDeCompra itemProcessoDeCompra : loteProcessoDeCompra.getItensProcessoDeCompra()) {
                    SolicitacaoMaterial solicitacaoMaterial = itemProcessoDeCompra.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial();
                    solicitacaoMaterial = propostaTecnicaFacade.getSolicitacaoMaterialFacade().recuperar(solicitacaoMaterial.getId());
                    valorHabilitacao = solicitacaoMaterial.getCriterioTecnicaSolicitacao().getValorHabilitacao();
                }
            }
            return valorHabilitacao;
        } catch (NullPointerException npe) {
            return valorHabilitacao;
        }
    }

    public String getCorDaDescricao() {
        try {
            if ("Habilitado".equalsIgnoreCase(licitacaoFornecedorSelecionado.getClassificacaoTecnica().getDescricao())) {
                return "green";
            }
            return "#cd0a0a";
        } catch (NullPointerException npe) {
            return "#cd0a0a";
        }
    }

    private boolean validarConfirmacao(ValidadorEntidade obj, ValidacaoException ve) {
        if (!Util.validaCampos(obj)) {
            throw ve;
        }

        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
            return false;
        }
        return true;
    }

}
