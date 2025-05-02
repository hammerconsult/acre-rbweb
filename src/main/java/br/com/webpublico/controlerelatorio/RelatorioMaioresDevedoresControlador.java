/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.FiltroMaioresDevedores;
import br.com.webpublico.enums.Ordenacao;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean(name = "relatorioMaioresDevedoresControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioMaioresDevedores",
        pattern = "/tributario/arrecadacao/relatorios/maiores-devedores-por-contribuinte-geral/",
        viewId = "/faces/tributario/arrecadacao/relatorios/maioresdevedores.xhtml"),
    @URLMapping(id = "novoRelatorioMaioresDevedoresParcelamento",
        pattern = "/tributario/arrecadacao/relatorios/maiores-devedores-de-parcelamento/",
        viewId = "/faces/tributario/arrecadacao/relatorios/maioresdevedoresparcelamento.xhtml"),
    @URLMapping(id = "novoRelatorioMaioresDevedoresImobiliario",
        pattern = "/tributario/arrecadacao/relatorios/maiores-devedores-por-cadastro-imobiliario/",
        viewId = "/faces/tributario/arrecadacao/relatorios/maioresdevedoresimobiliario.xhtml")
})
public class RelatorioMaioresDevedoresControlador implements Serializable {

    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroMaioresDevedores filtro;
    private Divida divida;
    private Pessoa pessoa;
    private ConverterAutoComplete converterPessoa;


    public FiltroMaioresDevedores getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroMaioresDevedores filtro) {
        this.filtro = filtro;
    }


    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SelectItem> getOrdens() {
        List<SelectItem> retorno = new ArrayList<>();
        for (Ordenacao ordem : Ordenacao.values()) {
            retorno.add(new SelectItem(ordem, ordem.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoesCadastro() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Ambos"));
        retorno.add(new SelectItem(true, "Ativo"));
        retorno.add(new SelectItem(false, "Inativo"));
        return retorno;
    }

    public List<SelectItem> getTiposPessoa() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Ambos"));
        for (TipoPessoa tipo : TipoPessoa.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void limparPessoa() {
        this.pessoa = null;
    }

    public List<Pessoa> completarPessoas(String parte) {
        if (TipoPessoa.FISICA.equals(filtro.getTipoPessoa())) {
            return pessoaFacade.listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO, SituacaoCadastralPessoa.INATIVO, SituacaoCadastralPessoa.BAIXADO);
        } else if (TipoPessoa.JURIDICA.equals(filtro.getTipoPessoa())) {
            return pessoaFacade.listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO, SituacaoCadastralPessoa.INATIVO, SituacaoCadastralPessoa.BAIXADO);
        }
        return new ArrayList<>();
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public void removeDivida(Divida divida) {
        if (filtro.getListaDividas().contains(divida)) {
            filtro.getListaDividas().remove(divida);
        }
    }

    public void removerPessoa(Pessoa pessoa) {
        if (filtro.getPessoas().contains(pessoa)) {
            filtro.getPessoas().remove(pessoa);
        }
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<Divida> dividas;
        if (filtro.getParcelamento()) {
            dividas = dividaFacade.listaDividasDeParcelamentoOrdenadoPorDescricao();
        } else if (filtro.getCadastroImobiliario()) {
            dividas = dividaFacade.listaDividasDoTipoCadastro("", TipoCadastroTributario.IMOBILIARIO);
        } else {
            dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
        }
        for (Divida t : dividas) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public void addDivida() {
        if (validaDivida()) {
            filtro.getListaDividas().add(divida);
            divida = new Divida();
        }
    }

    public void addPessoa() {
        if (validarPessoa()) {
            filtro.getPessoas().add(pessoa);
            pessoa = null;
        }
    }

    private boolean validaDivida() {
        boolean valida = true;
        if (divida == null || divida.getId() == null) {
            FacesUtil.addError("Atenção", "Selecione uma Dívida para adicionar");
            valida = false;
        } else if (filtro.getListaDividas().contains(divida)) {
            FacesUtil.addError("Atenção", "Essa Dívida já foi selecionada.");
            valida = false;
        }
        return valida;
    }

    private boolean validarPessoa() {
        boolean valida = true;
        if (pessoa == null || pessoa.getId() == null) {
            FacesUtil.addError("Atenção", "Selecione uma Pessoa para adicionar");
            valida = false;
        } else if (filtro.getPessoas().contains(pessoa)) {
            FacesUtil.addError("Atenção", "Essa Pessoa já foi selecionada.");
            valida = false;
        }
        return valida;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getVencimentoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de vencimento inicial");
        } else {
            filtro.setVencimentoInicial(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoInicial()));
        }
        if (filtro.getVencimentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de vencimento final");
        } else {
            filtro.setVencimentoFinal(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoFinal()));
        }
        if (filtro.getVencimentoInicial() != null && filtro.getVencimentoFinal() != null) {
            if (filtro.getVencimentoInicial().after(filtro.getVencimentoFinal())) {
                ve.adicionarMensagemDeCampoObrigatorio("A data de vencimento final deve ser superior a data de vencimento inicial");
            }
        }
        if (filtro.getQtdeDevedores() == null || filtro.getQtdeDevedores() < 1 || filtro.getQtdeDevedores() > 999) {
            ve.adicionarMensagemDeCampoObrigatorio("A quantidade de devedores deve estar entre 1 e 999");
        }
        ve.lancarException();
    }

    private void trataDividasSelecionadas() {
        if (divida != null) {
            if (!filtro.getListaDividas().contains(divida)) {
                filtro.getListaDividas().add(divida);
            }
        }
        divida = null;
    }

    @URLAction(mappingId = "novoRelatorioMaioresDevedoresParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParcelamento() {
        novo();
        filtro.setParcelamento(true);
    }

    @URLAction(mappingId = "novoRelatorioMaioresDevedoresImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoImobiliario() {
        novo();
        filtro.setCadastroImobiliario(true);
    }

    @URLAction(mappingId = "novoRelatorioMaioresDevedores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroMaioresDevedores(sistemaFacade.getUsuarioCorrente());
        DateTime hoje = DateTime.now();
        filtro.setVencimentoInicial(hoje.minusDays(30).toDate());
        filtro.setVencimentoFinal(hoje.toDate());
        divida = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            trataDividasSelecionadas();
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_PREFEITURA", "PREFEITURA MUNICIPAL DE RIO BRANCO");
            dto.adicionarParametro("NOME_ESTADO", "ESTADO DO ACRE");
            dto.adicionarParametro("NOME_ORGAO", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("IP", SistemaFacade.obtemIp());
            dto.adicionarParametro("FILTROS", filtro.getFiltro());
            dto.adicionarParametro("NOME_RELATORIO", getNomeRelatorio());
            dto.adicionarParametro("DETALHADO", filtro.getDetalhado());
            dto.adicionarParametro("CADASTROIMOBILIARIO", filtro.getCadastroImobiliario());
            enviarFiltroMaioresDevedores(dto);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("tributario/maiores-devedores/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void enviarFiltroMaioresDevedores(RelatorioDTO dto) {
        dto.adicionarParametro("filtrouTipoPessoa", filtro.getTipoPessoa() != null);
        if (filtro.getTipoPessoa() != null)
            dto.adicionarParametro("tipoPessoa", filtro.getTipoPessoa().getToDto());
        dto.adicionarParametro("filtrouPessoa", !filtro.getPessoas().isEmpty());
        if (!filtro.getPessoas().isEmpty())
            dto.adicionarParametro("idsPessoas", filtro.getListaIdPessoas());

        dto.adicionarParametro("vencimentoInicial", DataUtil.getDataFormatada(filtro.getVencimentoInicial()));
        dto.adicionarParametro("vencimentoFinal", DataUtil.getDataFormatada(filtro.getVencimentoFinal()));
        dto.adicionarParametro("qtdeDevedores", filtro.getQtdeDevedores());
        dto.adicionarParametro("ordenacao", filtro.getOrdenacao().getToDto());
        dto.adicionarParametro("idsDividas", filtro.getListaIdDividas());
        dto.adicionarParametro("parcelamento", filtro.getParcelamento());
        dto.adicionarParametro("cadastroImobiliario", filtro.getCadastroImobiliario());
        dto.adicionarParametro("inscricaoInicial", filtro.getInscricaoInicial());
        dto.adicionarParametro("inscricaoFinal", filtro.getInscricaoFinal());
        dto.adicionarParametro("tipoDebitoExercicio", filtro.getTipoDebitoExercicio());
        dto.adicionarParametro("tipoDebitoDividaAtiva", filtro.getTipoDebitoDividaAtiva());
        dto.adicionarParametro("tipoDebitoDividaAtivaAjuizada", filtro.getTipoDebitoDividaAtivaAjuizada());
        dto.adicionarParametro("tipoDebitoDividaAtivaProtestada", filtro.getTipoDebitoDividaAtivaProtestada());
        dto.adicionarParametro("situacaoCadastro", filtro.getSituacaoCadastro());
    }

    private String getNomeRelatorio() {
        return "Relatório de Maiores Devedores por" + (filtro.getCadastroImobiliario() ? " Cadastro Imobiliário " : " Contribuinte Geral ")
            + (filtro.getParcelamento() ? "de Parcelamento " : " ")
            + (filtro.getDetalhado() ? "(Detalhado)" : "(Resumido)");

    }
}
