package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BancoContaConfTributario;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.LoteBaixaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoTotalizadorDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean(name = "relatorioReceitaDiariaControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioReceitaDiariaControlador",
        pattern = "/tributario/arrecadacao/relatorios/receita-diaria/",
        viewId = "/faces/tributario/arrecadacao/relatorios/receitadiaria.xhtml")
})
public class RelatorioReceitaDiariaControlador {

    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioReceitaDiaria filtro;
    private BancoContaConfTributario[] contasSelecionadas;
    private List<Divida> dividasSelecionadas;
    private Divida divida;
    private TipoTotalizador tipoTotalizador;

    public List<Divida> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public void removeDivida(Divida divida) {
        dividasSelecionadas.remove(divida);
    }

    public TipoTotalizador getTipoTotalizador() {
        return tipoTotalizador;
    }

    public void setTipoTotalizador(TipoTotalizador tipoTotalizador) {
        this.tipoTotalizador = tipoTotalizador;
    }

    public void gerarRelatorio(String extencaoRelatorio) {
        try {
            validarCampos();
            adicionarSubContasDasContasSelecionadas();
            RelatorioDTO dto = new RelatorioDTO();
            TipoRelatorioDTO tipoRelatorio = TipoRelatorioDTO.valueOf(extencaoRelatorio);
            dto.setTipoRelatorio(tipoRelatorio);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(filtro.getDataInicio()));
            dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(filtro.getDataFinal()));
            dto.adicionarParametro("TIPOTOTALIZADOR", tipoTotalizador.name());
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("NOME_RELATORIO", "RECEITA DIÁRIA DA ARRECADAÇÃO");
            dto.adicionarParametro("idsSubContas", Util.montarIdsList(filtro.getSubContas()));
            dto.adicionarParametro("idsDividas", getIdsDividas());
            dto.setNomeRelatorio("Relatório de Receita Diária de Arrecadação");
            dto.setApi("tributario/receita-diaria/" + (TipoRelatorioDTO.XLS.equals(tipoRelatorio) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDataInicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Pagamento Inicial deve ser informado.");
        }
        if (filtro.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Pagamento Final deve ser Informado.");
        }
        ve.lancarException();
        if (filtro.getDataInicio().after(filtro.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de pagamento inicial deve ser menor que a data final.");
        }
        ve.lancarException();
    }

    private void adicionarSubContasDasContasSelecionadas() {
        filtro.getSubContas().clear();
        for (BancoContaConfTributario bancoConta : contasSelecionadas) {
            filtro.getSubContas().add(bancoConta.getSubConta());
        }
    }

    public List<SelectItem> getTiposTotalizador() {
        return Util.getListSelectItemSemCampoVazio(TipoTotalizador.values());
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<Divida> dividas = dividaFacade.listaDividasOrdenadoPorDescricao();
        for (Divida t : dividas) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public void addDivida() {
        try {
            validarDivida();
            Util.adicionarObjetoEmLista(dividasSelecionadas, divida);
            divida = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null || divida.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado.");
        } else if (dividasSelecionadas.contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida já foi selecionada.");
        }
        ve.lancarException();
    }

    public FiltroRelatorioReceitaDiaria getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioReceitaDiaria filtro) {
        this.filtro = filtro;
    }

    public BancoContaConfTributario[] getContasSelecionadas() {
        return contasSelecionadas;
    }

    public void setContasSelecionadas(BancoContaConfTributario[] contasSelecionadas) {
        this.contasSelecionadas = contasSelecionadas;
    }

    @URLAction(mappingId = "novoRelatorioReceitaDiariaControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioReceitaDiaria();
        dividasSelecionadas = Lists.newArrayList();
        contasSelecionadas = new BancoContaConfTributario[0];
        divida = null;
    }

    public List<BancoContaConfTributario> getContas() {
        List<BancoContaConfTributario> retorno = loteBaixaFacade.recuperaContasConfiguracao();
        retorno.sort(new Comparator<BancoContaConfTributario>() {
            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return retorno;
    }

    private List<Long> getIdsDividas() {
        if (divida != null && !dividasSelecionadas.contains(divida)) {
            Util.adicionarObjetoEmLista(dividasSelecionadas, divida);
            divida = null;
        }
        return Util.montarIdsList(dividasSelecionadas);
    }

    public enum TipoTotalizador implements EnumComDescricao {
        POR_PERIODO("Por Período", TipoTotalizadorDTO.POR_PERIODO),
        POR_MES("Por Mês", TipoTotalizadorDTO.POR_MES),
        ANUAL("Anual", TipoTotalizadorDTO.ANUAL);
        private final String descricao;

        TipoTotalizador(String descricao, TipoTotalizadorDTO tipo) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public static class FiltroRelatorioReceitaDiaria {
        private Date dataInicio;
        private Date dataFinal;
        private Divida divida;
        private List<SubConta> subContas;

        public FiltroRelatorioReceitaDiaria() {
            this.subContas = Lists.newArrayList();
        }

        public Date getDataInicio() {
            return dataInicio;
        }

        public void setDataInicio(Date dataInicio) {
            this.dataInicio = dataInicio;
        }

        public Date getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(Date dataFinal) {
            this.dataFinal = dataFinal;
        }

        public Divida getDivida() {
            return divida;
        }

        public void setDivida(Divida divida) {
            this.divida = divida;
        }

        public List<SubConta> getSubContas() {
            return subContas;
        }

        public void setSubContas(List<SubConta> subContas) {
            this.subContas = subContas;
        }
    }
}
