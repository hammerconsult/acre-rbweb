/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.contabil.TipoUnidadeDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-transf-bens-moveis", pattern = "/relatorio/transf-bens-moveis/", viewId = "/faces/financeiro/relatorio/relatoriotransfbensmoveis.xhtml")
})
public class RelatorioTransfBensMoveisControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private GrupoBemFacade grupoBemFacade;
    private String numero;
    private GrupoBem grupoBemOrigem;
    private GrupoBem grupoBemDestino;
    private TipoGrupo tipoGrupoOrigem;
    private TipoGrupo tipoGrupoDestino;
    private TipoLancamento tipoLancamento;
    private TipoOperacaoBensMoveis operacaoOrigem;
    private TipoOperacaoBensMoveis operacaoDestino;
    private TipoUnidade tipoUnidade;

    public RelatorioTransfBensMoveisControlador() {
    }

    @URLAction(mappingId = "relatorio-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limparCamposGeral();
        numero = null;
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        tipoUnidade = TipoUnidade.ORIGEM;
        tipoLancamento = null;
        operacaoOrigem = null;
        operacaoDestino = null;
        tipoGrupoOrigem = null;
        tipoGrupoDestino = null;
        grupoBemOrigem = null;
        grupoBemDestino = null;
    }

    public void limparUnidades() {
        listaUnidades = Lists.newArrayList();
    }

    @Override
    public String getNomeRelatorio() {
        return "RELATORIO-TRANSF-BENS-MOVEIS";
    }

    public void gerarRelatorioTransfBensMoveis() {
        try {
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("MODULO", "Contábil");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("tipoUnidade", tipoUnidade.toDto);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("FILTRO", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/transf-bens-moveis/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        filtrosParametroDatas(parametros);
        adicionarExercicioParaApresentacao(parametros);
        filtrosParametrosUnidade(parametros);
        filtrosParametrosGerais(parametros);
        return parametros;
    }

    private void adicionarExercicioParaApresentacao(List<ParametrosRelatorios> parametros) {
        if (apresentacao.equals(ApresentacaoRelatorio.UNIDADE_GESTORA)) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        }
    }

    @Override
    public List<ParametrosRelatorios> filtrosParametrosUnidade(List<ParametrosRelatorios> parametros) {
        List<Long> idsUnidades = new ArrayList<>();
        if (!this.listaUnidades.isEmpty()) {
            montarFiltroUnidade(parametros, idsUnidades);
        } else if (this.unidadeGestora == null) {
            montarFiltroUnidadesUsuario(parametros, idsUnidades);
        } else {
            montarFiltroUnidadeGestora(parametros);
        }
        return parametros;
    }

    private void montarFiltroUnidadeGestora(List<ParametrosRelatorios> parametros) {
        if (TipoUnidade.ORIGEM.equals(tipoUnidade)) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
        } else {
            parametros.add(new ParametrosRelatorios(" ugdestino.id ", ":ugDestinoId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
        }
        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, buscarExercicioPelaDataFinal().getId(), null, 0, false));
        filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        filtroUG = unidadeGestora.getCodigo() + " - " + unidadeGestora.getDescricao();
    }

    private void montarFiltroUnidadesUsuario(List<ParametrosRelatorios> parametros, List<Long> idsUnidade) {
        List<HierarquiaOrganizacional> unidadesDoUsuario = relatorioContabilSuperFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), buscarExercicioPelaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
        for (HierarquiaOrganizacional lista : unidadesDoUsuario) {
            idsUnidade.add(lista.getSubordinada().getId());
        }
        if (!idsUnidade.isEmpty()) {
            if (TipoUnidade.ORIGEM.equals(tipoUnidade)) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidade, null, 1, false));
            } else {
                parametros.add(new ParametrosRelatorios(" vwdestino.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidade, null, 1, false));
            }
        }
    }

    private void montarFiltroUnidade(List<ParametrosRelatorios> parametros, List<Long> idsUnidades) {
        String unidades = "";
        for (HierarquiaOrganizacional lista : listaUnidades) {
            idsUnidades.add(lista.getSubordinada().getId());
            unidades += " " + lista.getCodigo().substring(3, 10) + " -";
        }
        if (TipoUnidade.ORIGEM.equals(tipoUnidade)) {
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            parametros.add(new ParametrosRelatorios(" vwdestino.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        }
        filtros += " Unidade(s): " + unidades;
    }

    private void filtrosParametroDatas(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" trunc(transf.datatransferencia) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + getDataInicialFormatada() + " a " + getDataFinalFormatada() + " -";
    }

    public List<ParametrosRelatorios> filtrosParametrosGerais(List<ParametrosRelatorios> parametros) {
        if (numero != null && !numero.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" transf.numero ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (tipoLancamento != null) {
            parametros.add(new ParametrosRelatorios(" transf.tipolancamento ", ":tipoLancamento", null, OperacaoRelatorio.IGUAL, tipoLancamento.name(), null, 1, false));
            filtros += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + " -";
        }
        if (tipoGrupoOrigem != null) {
            parametros.add(new ParametrosRelatorios(" transf.tipogrupoorigem ", ":tipoGrupoOrigem", null, OperacaoRelatorio.IGUAL, tipoGrupoOrigem.name(), null, 1, false));
            filtros += " Tipo Grupo: " + tipoGrupoOrigem.getDescricao() + " -";
        }
        if (tipoGrupoDestino != null) {
            parametros.add(new ParametrosRelatorios(" transf.tipogrupodestino ", ":tipoGrupoDestino", null, OperacaoRelatorio.IGUAL, tipoGrupoDestino.name(), null, 1, false));
            filtros += " Tipo Grupo: " + tipoGrupoDestino.getDescricao() + " -";
        }
        if (operacaoOrigem != null) {
            parametros.add(new ParametrosRelatorios(" transf.operacaoorigem ", ":operacaoOrigem", null, OperacaoRelatorio.IGUAL, operacaoOrigem.name(), null, 1, false));
            filtros += " Operação: " + operacaoOrigem.getDescricao() + " -";
        }
        if (operacaoDestino != null) {
            parametros.add(new ParametrosRelatorios(" transf.operacaodestino ", ":operacaoDestino", null, OperacaoRelatorio.IGUAL, operacaoDestino.name(), null, 1, false));
            filtros += " Operação: " + operacaoDestino.getDescricao() + " -";
        }
        if (grupoBemOrigem != null) {
            parametros.add(new ParametrosRelatorios(" gorigem.id  ", ":idGrupoBemOrigem", null, OperacaoRelatorio.IGUAL, grupoBemOrigem.getId(), null, 1, false));
            filtros += " Grupo Patrimonial: " + grupoBemOrigem.getCodigo() + " - " + grupoBemOrigem.getDescricao() + " -";
        }
        if (grupoBemDestino != null) {
            parametros.add(new ParametrosRelatorios(" gdestino.id  ", ":idGrupoBemDestino", null, OperacaoRelatorio.IGUAL, grupoBemDestino.getId(), null, 1, false));
            filtros += " Grupo Patrimonial: " + grupoBemDestino.getCodigo() + " - " + grupoBemDestino.getDescricao() + " -";
        }
        atualizaFiltrosGerais();
        return parametros;
    }

    public List<SelectItem> getOperacoesBensMoveisOrigem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaConcedida()) {
            toReturn.add(new SelectItem(tipoOperacaoBensMoveis, tipoOperacaoBensMoveis.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesBensMoveisDestino() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaRecebida()) {
            toReturn.add(new SelectItem(tipoOperacaoBensMoveis, tipoOperacaoBensMoveis.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposGrupo() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoGrupo tipo : TipoGrupo.values()) {
            if (tipo.getClasseDeUtilizacao().equals(BensMoveis.class))
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTiposLancamento() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, "Todos"));
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTipoDeUnidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoUnidade tipo : TipoUnidade.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<GrupoBem> completarGrupoPatrimonial(String parte) {
        return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
    }

    private enum TipoUnidade {
        ORIGEM("Origem", TipoUnidadeDTO.ORIGEM),
        DESTINO("Destino", TipoUnidadeDTO.DESTINO);
        private String descricao;
        private TipoUnidadeDTO toDto;

        TipoUnidade(String descricao, TipoUnidadeDTO toDto) {
            this.descricao = descricao;
            this.toDto = toDto;
        }

        public String getDescricao() {
            return descricao;
        }

        public TipoUnidadeDTO getToDto() {
            return toDto;
        }
    }

    @Override

    public String getNumero() {
        return numero;
    }

    @Override
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public GrupoBem getGrupoBemOrigem() {
        return grupoBemOrigem;
    }

    public void setGrupoBemOrigem(GrupoBem grupoBemOrigem) {
        this.grupoBemOrigem = grupoBemOrigem;
    }

    public GrupoBem getGrupoBemDestino() {
        return grupoBemDestino;
    }

    public void setGrupoBemDestino(GrupoBem grupoBemDestino) {
        this.grupoBemDestino = grupoBemDestino;
    }

    public TipoGrupo getTipoGrupoOrigem() {
        return tipoGrupoOrigem;
    }

    public void setTipoGrupoOrigem(TipoGrupo tipoGrupoOrigem) {
        this.tipoGrupoOrigem = tipoGrupoOrigem;
    }

    public TipoGrupo getTipoGrupoDestino() {
        return tipoGrupoDestino;
    }

    public void setTipoGrupoDestino(TipoGrupo tipoGrupoDestino) {
        this.tipoGrupoDestino = tipoGrupoDestino;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacaoBensMoveis getOperacaoOrigem() {
        return operacaoOrigem;
    }

    public void setOperacaoOrigem(TipoOperacaoBensMoveis operacaoOrigem) {
        this.operacaoOrigem = operacaoOrigem;
    }

    public TipoOperacaoBensMoveis getOperacaoDestino() {
        return operacaoDestino;
    }

    public void setOperacaoDestino(TipoOperacaoBensMoveis operacaoDestino) {
        this.operacaoDestino = operacaoDestino;
    }

    public TipoUnidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoUnidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }
}
