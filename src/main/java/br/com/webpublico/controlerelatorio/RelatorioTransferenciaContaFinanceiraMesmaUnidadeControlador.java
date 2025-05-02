/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoTransferenciaMesmaUnidade;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SubContaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */

@ManagedBean(name = "relatorioTransferenciaContaFinanceiraMesmaUnidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "transferencia-conta-financeira-mesma-unidade", pattern = "/relatorio/contabil/transf-conta-financeira-mesma-unidade", viewId = "/faces/financeiro/relatorio/relatoriotransferenciacontafinanceiramesmaunidade.xhtml"),
})

public class RelatorioTransferenciaContaFinanceiraMesmaUnidadeControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SubContaFacade contaFinanceiraFacade;
    private String numero;
    private Date dataInicial;
    private Date dataFinal;
    private SubConta contaFinanceiraRetirada;
    private SubConta contaFinanceiraDeposito;
    private FonteDeRecursos fonteDeRecursosRetirada;
    private FonteDeRecursos fonteDeRecursosDeposito;
    private TipoTransferenciaMesmaUnidade tipoTransferenciaFinanceiraMesmaConta;
    private List<HierarquiaOrganizacional> listaUnidades;
    private String filtro;
    private ApresentacaoRelatorio apresentacao;
    private UnidadeGestora unidadeGestora;

    @URLAction(mappingId = "transferencia-conta-financeira-mesma-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        dataInicial = new Date();
        listaUnidades = new ArrayList<>();
        dataFinal = new Date();
        contaFinanceiraDeposito = null;
        contaFinanceiraRetirada = null;
        tipoTransferenciaFinanceiraMesmaConta = null;
        fonteDeRecursosRetirada = null;
        numero = "";
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        fonteDeRecursosDeposito = null;
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values());
    }

    public List<SelectItem> getTiposTransferenciasFinanceiras() {
        return Util.getListSelectItem(TipoTransferenciaMesmaUnidade.values());
    }

    public List<FonteDeRecursos> completarFontesRecursosRetiradas(String parte) {
        if (contaFinanceiraRetirada != null) {
            return contaFinanceiraFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceira(parte, contaFinanceiraRetirada);
        }
        return null;
    }

    public List<FonteDeRecursos> completarFontesRecursosDepositos(String parte) {
        if (contaFinanceiraDeposito != null) {
            return contaFinanceiraFacade.getFonteDeRecursosFacade().listaFiltrandoPorContaFinanceira(parte, contaFinanceiraDeposito);
        }
        return null;
    }

    public List<SubConta> completarSubContas(String parte) {
        return contaFinanceiraFacade.listaFiltrando(parte, "codigo", "descricao");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("Transferência de Saldo na Conta Financeira Mesma Unidade");
            dto.setApi("contabil/transferencia-conta-financeira-mesma-unidade/");
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

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        parametros.add(new ParametrosRelatorios(" trunc(A.DATATRANSFERENCIA) ", ":dataInicial", ":dataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtro = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";

        if (this.tipoTransferenciaFinanceiraMesmaConta != null) {
            parametros.add(new ParametrosRelatorios(" a.TIPOTRANSFERENCIA ", ":tipoTransferencia", null, OperacaoRelatorio.IGUAL, tipoTransferenciaFinanceiraMesmaConta.name(), null, 1, false));
            filtro += " Tipo de Transferência: " + tipoTransferenciaFinanceiraMesmaConta.getDescricao() + " -";
        }
        if (this.contaFinanceiraDeposito != null) {
            parametros.add(new ParametrosRelatorios(" a.SUBCONTADEPOSITO_ID ", ":subContaDeposito", null, OperacaoRelatorio.IGUAL, contaFinanceiraDeposito.getId(), null, 1, false));
            filtro += " Conta Financeira Depósito: " + contaFinanceiraDeposito.getCodigo() + " -";
        }
        if (this.contaFinanceiraRetirada != null) {
            parametros.add(new ParametrosRelatorios(" a.SUBCONTARETIRADA_ID ", ":subContaRetirada", null, OperacaoRelatorio.IGUAL, contaFinanceiraRetirada.getId(), null, 1, false));
            filtro += " Conta Financeira Retirada: " + contaFinanceiraDeposito.getCodigo() + " -";
        }
        if (this.fonteDeRecursosDeposito != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOSDEPOSITO_ID ", ":fonteDeposito", null, OperacaoRelatorio.IGUAL, fonteDeRecursosDeposito.getId(), null, 1, false));
            filtro += " Fonte de Recurso Depósito: " + fonteDeRecursosDeposito.getCodigo() + " -";
        }
        if (this.fonteDeRecursosRetirada != null) {
            parametros.add(new ParametrosRelatorios(" a.FONTEDERECURSOSRETIRADA_ID ", ":fonteRetirada", null, OperacaoRelatorio.IGUAL, fonteDeRecursosRetirada.getId(), null, 1, false));
            filtro += " Fonte de Recurso Retirada: " + fonteDeRecursosRetirada.getCodigo() + " -";
        }

        if (this.numero != null && !this.numero.trim().equals("")) {
            parametros.add(new ParametrosRelatorios(" a.NUMERO ", ":numero", null, OperacaoRelatorio.IGUAL, numero, null, 1, false));
            filtro += " Número: " + numero + " -";
        }

        if (this.listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            List<Long> listaIdsUnidades = new ArrayList<>();
            listaUndsUsuarios = contaFinanceiraFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), getExercicio(), getDataReferencia(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }

        if (this.unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (this.unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicio().getId(), null, 1, false));
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return parametros;
    }

    public Exercicio getExercicio() {
        return dataFinal != null ? exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal)) : sistemaFacade.getExercicioCorrente();
    }

    public Date getDataReferencia() {
        return dataFinal != null ? dataFinal : sistemaFacade.getDataOperacao();
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial deve ser menor que a Data Final");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public SubConta getContaFinanceiraDeposito() {
        return contaFinanceiraDeposito;
    }

    public void setContaFinanceiraDeposito(SubConta contaFinanceiraDeposito) {
        this.contaFinanceiraDeposito = contaFinanceiraDeposito;
    }

    public SubConta getContaFinanceiraRetirada() {
        return contaFinanceiraRetirada;
    }

    public void setContaFinanceiraRetirada(SubConta contaFinanceiraRetirada) {
        this.contaFinanceiraRetirada = contaFinanceiraRetirada;
    }

    public TipoTransferenciaMesmaUnidade getTipoTransferenciaFinanceiraMesmaConta() {
        return tipoTransferenciaFinanceiraMesmaConta;
    }

    public void setTipoTransferenciaFinanceiraMesmaConta(TipoTransferenciaMesmaUnidade tipoTransferenciaFinanceiraMesmaConta) {
        this.tipoTransferenciaFinanceiraMesmaConta = tipoTransferenciaFinanceiraMesmaConta;
    }

    public FonteDeRecursos getFonteDeRecursosDeposito() {
        return fonteDeRecursosDeposito;
    }

    public void setFonteDeRecursosDeposito(FonteDeRecursos fonteDeRecursosDeposito) {
        this.fonteDeRecursosDeposito = fonteDeRecursosDeposito;
    }

    public FonteDeRecursos getFonteDeRecursosRetirada() {
        return fonteDeRecursosRetirada;
    }

    public void setFonteDeRecursosRetirada(FonteDeRecursos fonteDeRecursosRetirada) {
        this.fonteDeRecursosRetirada = fonteDeRecursosRetirada;
    }

    public void setaContaFinanceiraRetirada(SelectEvent evento) {
        contaFinanceiraRetirada = (SubConta) evento.getObject();
    }

    public void setaContaFinanceiraDeposito(SelectEvent evento) {
        contaFinanceiraRetirada = (SubConta) evento.getObject();
    }

    public List<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(List<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }
}
