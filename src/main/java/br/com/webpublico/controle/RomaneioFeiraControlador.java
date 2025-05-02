package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoRomaneioFeira;
import br.com.webpublico.enums.administrativo.TipoExibicaoRelatorioRomaneio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RomaneioFeiraFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-romaneio", pattern = "/romaneio-feira/novo/", viewId = "/faces/administrativo/feiras/romaneio/edita.xhtml"),
    @URLMapping(id = "editar-romaneio", pattern = "/romaneio-feira/editar/#{romaneioFeiraControlador.id}/", viewId = "/faces/administrativo/feiras/romaneio/edita.xhtml"),
    @URLMapping(id = "listar-romaneio", pattern = "/romaneio-feira/listar/", viewId = "/faces/administrativo/feiras/romaneio/lista.xhtml"),
    @URLMapping(id = "ver-romaneio", pattern = "/romaneio-feira/ver/#{romaneioFeiraControlador.id}/", viewId = "/faces/administrativo/feiras/romaneio/visualizar.xhtml"),
})
public class RomaneioFeiraControlador extends PrettyControlador<RomaneioFeira> implements Serializable, CRUD {

    @EJB
    private RomaneioFeiraFacade facade;
    private RomaneioFeiraFeiranteProduto romaneioProduto;
    private RomaneioFeiraFeirante romaneioFeirante;
    private TipoExibicaoRelatorioRomaneio tipoExibicaoRelatorio;

    public RomaneioFeiraControlador() {
        super(RomaneioFeira.class);
    }

    @Override
    @URLAction(mappingId = "novo-romaneio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoRomaneioFeira.EM_ABERTO);
        selecionado.setDataInicial(facade.getSistemaFacade().getDataOperacao());
        novoFeirante();
    }

    @Override
    @URLAction(mappingId = "ver-romaneio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();

    }

    @Override
    @URLAction(mappingId = "editar-romaneio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        if (isOperacaoEditar() && selecionado.getSituacao().isFinalizado()) {
            FacesUtil.addOperacaoNaoPermitida("Não é permitido realizar alterações em um romaneio já finalizado.");
            redirecionarParaVer();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/romaneio-feira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarCamposSalvar();
        return super.validaRegrasEspecificas();
    }

    public List<Feirante> completarFeirante(String parte) {
        if (hasFeiraSelecionada()) {
            return facade.getFeiranteFacade().buscarFeirante(selecionado.getFeira(), parte.trim());
        }
        return Collections.emptyList();
    }

    public List<ProdutoFeira> completarProduto(String parte) {
        return facade.buscarProdutos(parte.trim());
    }

    public List<Feira> completarFeira(String parte) {
        return facade.getFeiraFacade().buscarFeira(parte.trim());
    }

    public void limparCamposFeira() {
        selecionado.setFeira(null);
        setRomaneioFeirante(null);
    }

    public void limparCamposFeirante() {
        romaneioFeirante.setFeirante(null);
    }

    public void adicionarProduto() {
        try {
            validarProduto();
            Util.adicionarObjetoEmLista(romaneioFeirante.getProdutos(), romaneioProduto);
            novoProduto();
            FacesUtil.executaJavaScript("setaFoco('formDlgProdutos:produto_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void novoProduto() {
        romaneioProduto = new RomaneioFeiraFeiranteProduto();
        romaneioProduto.setRomaneioFeiraFeirante(romaneioFeirante);
    }

    public void removerProduto(RomaneioFeiraFeiranteProduto produto) {
        romaneioFeirante.getProdutos().remove(produto);
    }

    public void editarProduto(RomaneioFeiraFeiranteProduto produto) {
        romaneioProduto = produto;
    }

    public void removerFeirante(RomaneioFeiraFeirante romaneioFeiraFeirante) {
        selecionado.getFeirantes().remove(romaneioFeiraFeirante);
    }

    public void validarProduto() {
        Util.validarCampos(romaneioProduto);
        ValidacaoException ve = new ValidacaoException();
        if (romaneioProduto.getQuantidade().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Quantidade deve ser maior que zero(0).");
        }
        romaneioFeirante.getProdutos().stream()
            .filter(prodList -> prodList.getProdutoFeira().equals(romaneioProduto.getProdutoFeira()))
            .filter(prodList -> !prodList.equals(romaneioProduto))
            .map(prodMap -> "Produto já adicionado na lista.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    public void validarFeirante() {
        Util.validarCampos(romaneioFeirante);

        ValidacaoException ve = new ValidacaoException();
        selecionado.getFeirantes().stream()
            .filter(feiraNaLista -> feiraNaLista.getFeirante().getId().equals(romaneioFeirante.getFeirante().getId()))
            .map(feiraNaLista -> "Feirante já adicionada na lista.")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    public void validarCamposSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFeirantes() == null || selecionado.getFeirantes().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione um feirante na lista para continuar.");
        }
        ve.lancarException();
    }

    public void validarQuantidadeProduto() {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isListNullOrEmpty(romaneioFeirante.getProdutos())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Feirante deve conter produtos para continuar.");
        }
        ve.lancarException();
        romaneioFeirante.getProdutos().stream()
            .filter(prod -> prod.getQuantidade().compareTo(BigDecimal.ZERO) <= 0)
            .map(prod -> "Quantidade deve ser maior que zero(0).")
            .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        ve.lancarException();
    }

    public void novoFeirante() {
        romaneioFeirante = new RomaneioFeiraFeirante();
        romaneioFeirante.setRomaneioFeira(selecionado);
    }

    public void adicionarFeirante() {
        try {
            validarFeirante();
            novoProduto();
            FacesUtil.executaJavaScript("dlgProdutos.show()");
            FacesUtil.atualizarComponente("Formulario:gridDadosGerais");
            FacesUtil.atualizarComponente("formDlgProdutos");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void editarFeirante(RomaneioFeiraFeirante romaneioFeiraFeirante) {
        try {
            this.romaneioFeirante = romaneioFeiraFeirante;
            novoProduto();
            FacesUtil.executaJavaScript("dlgProdutos.show()");
            FacesUtil.atualizarComponente("formDlgProdutos");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void confirmarProduto() {
        try {
            validarQuantidadeProduto();
            Util.adicionarObjetoEmLista(selecionado.getFeirantes(), romaneioFeirante);
            cancelarProduto();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public void cancelarProduto() {
        try {
            setRomaneioProduto(null);
            novoFeirante();
            FacesUtil.executaJavaScript("dlgProdutos.hide()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public boolean hasFeirantes() {
        return !Util.isListNullOrEmpty(selecionado.getFeirantes());
    }

    public boolean hasFeiraSelecionada() {
        return selecionado.getFeira() != null;
    }

    public void finalizar() {
        try {
            validaRegrasEspecificas();
            selecionado = facade.finalizarRomaneio(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redirecionarParaVer();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            descobrirETratarException(ex);
        }
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarGerarRelatorio();
            String nomeRelatorio = "Relatório de Romaneio de Produtos";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONOMICO, TURISMO, TECNOLOGIA E INOVAÇÃO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("tipoExibicaoRelatorio", tipoExibicaoRelatorio.name());
            dto.adicionarParametro("idRomaneio", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/romaneio-produto-feira/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
            FacesUtil.executaJavaScript("dialogRelatorio.hide()");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório de romaneio: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarGerarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoExibicaoRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Relatório deve ser informado.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposExibicoesRelatorio() {
        return Util.getListSelectItemSemCampoVazio(TipoExibicaoRelatorioRomaneio.values());
    }

    public TipoExibicaoRelatorioRomaneio getTipoExibicaoRelatorio() {
        return tipoExibicaoRelatorio;
    }

    public void setTipoExibicaoRelatorio(TipoExibicaoRelatorioRomaneio tipoExibicaoRelatorio) {
        this.tipoExibicaoRelatorio = tipoExibicaoRelatorio;
    }

    public RomaneioFeiraFeiranteProduto getRomaneioProduto() {
        return romaneioProduto;
    }

    public void setRomaneioProduto(RomaneioFeiraFeiranteProduto romaneioProduto) {
        this.romaneioProduto = romaneioProduto;
    }

    public RomaneioFeiraFeirante getRomaneioFeirante() {
        return romaneioFeirante;
    }

    public void setRomaneioFeirante(RomaneioFeiraFeirante romaneioFeirante) {
        this.romaneioFeirante = romaneioFeirante;
    }
}
