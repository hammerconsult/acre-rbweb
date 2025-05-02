/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.enums.TipoContaExtraorcamentaria;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-receita-extra", pattern = "/relatorio/receita-extra/", viewId = "/faces/financeiro/relatorio/relatorioreceitaextra.xhtml")})
@ManagedBean
public class RelatorioReceitaExtraControlador extends RelatorioContabilSuperControlador {

    private SituacaoReceitaExtra situacaoReceitaExtra;
    private ContaExtraorcamentaria contaExtraorcamentaria;
    private TipoContaExtraorcamentaria tipoContaExtra;
    private TipoReceitaExtraorcamentaria tipoReceitaExtra;
    private Date dataConciliacao;
    private Conta contaDeDestinacao;

    private enum TipoReceitaExtraorcamentaria {
        TRANSPORTADAS("Transportadas"),
        RECEBIDAS_EXERCICIO("Recebidas no Exercício");

        TipoReceitaExtraorcamentaria(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    @URLAction(mappingId = "novo-relatorio-receita-extra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCamposGeral();
        dataInicial = DataUtil.montaData(1, 0, getSistemaFacade().getExercicioCorrente().getAno()).getTime();
        tipoContaExtra = null;
        contaExtraorcamentaria = null;
        dataConciliacao = null;
        numero = "";
        situacaoReceitaExtra = null;
        tipoReceitaExtra = null;
        contaDeDestinacao = null;
    }

    public List<SelectItem> tiposReceitasExtras() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todas"));
        for (TipoReceitaExtraorcamentaria tre : TipoReceitaExtraorcamentaria.values()) {
            toReturn.add(new SelectItem(tre, tre.getDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", getDescricaoSegundoEsferaDoPoder());
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaControlador().getExercicioCorrente().getAno().toString());
            dto.adicionarParametro("EXERCICIO", getSistemaControlador().getExercicioCorrente().getId());
            dto.adicionarParametro("APRESENTACAO", apresentacao.name());
            dto.adicionarParametro("apresentacaoRelatorioDTO", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTRO", getFiltrosAtualizados());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("contabil/receita-extra/");
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

    private String getFiltrosAtualizados() {
        filtros = getFiltrosPeriodo() + filtros;
        return atualizaFiltrosGerais();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        filtros = "";
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 0, true));
        if (numero != null && !numero.isEmpty()) {
            parametros.add(new ParametrosRelatorios(" REC.numero ", ":numero", null, OperacaoRelatorio.LIKE, numero, null, 1, false));
            filtros += " Número: " + numero + " -";
        }
        if (dataConciliacao != null) {
            parametros.add(new ParametrosRelatorios(" trunc(rec.dataConciliacao) ", ":DataConciliacao", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataConciliacao), null, 2, true));
            parametros.add(new ParametrosRelatorios(" trunc(ree.dataConciliacao) ", ":DataConciliacao", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataConciliacao), null, 3, true));
            filtros += " Data de Conciliação: " + DataUtil.getDataFormatada(dataConciliacao) + " -";
        }
        if (this.contaBancariaEntidade != null) {
            parametros.add(new ParametrosRelatorios(" cbe.id ", ":contaBancariaEntidade", null, OperacaoRelatorio.IGUAL, contaBancariaEntidade.getId(), null, 1, false));
            filtros += " Conta Bancária: " + contaBancariaEntidade.getNumeroCompleto() + " -";
        }
        if (this.contaFinanceira != null) {
            parametros.add(new ParametrosRelatorios(" sub.id ", ":subConta", null, OperacaoRelatorio.IGUAL, contaFinanceira.getId(), null, 1, false));
            filtros += " Conta Financeira: " + contaFinanceira.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" cdest.ID ", ":cdestId", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }
        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.ID ", ":fonteId", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recurso: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (this.contaExtraorcamentaria != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO ", ":contaCodigo", null, OperacaoRelatorio.LIKE, contaExtraorcamentaria.getCodigo(), null, 1, false));
            filtros += " Conta Extraorçamentária: " + contaExtraorcamentaria.getCodigo() + " -";
        }
        if (this.tipoContaExtra != null) {
            parametros.add(new ParametrosRelatorios(" CON.TIPOCONTAEXTRAORCAMENTARIA", ":tipoContaExtra", null, OperacaoRelatorio.LIKE, tipoContaExtra.name(), null, 1, false));
            filtros += " Tipo de Conta Extraorçamentária: " + tipoContaExtra.getDescricao() + " -";
        }
        if (this.eventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" eve.id", ":evento", null, OperacaoRelatorio.IGUAL, eventoContabil.getId(), null, 1, false));
            filtros += " Evento Contábil: " + eventoContabil.getCodigo() + " -";
        }
        if (pessoa != null) {
            parametros.add(new ParametrosRelatorios(" rec.pessoa_id ", ":fornecedor", null, OperacaoRelatorio.IGUAL, pessoa.getId(), null, 1, false));
            filtros += " Pessoa: " + pessoa.getNome() + " -";
        }
        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.id ", ":classe", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe: " + classeCredor.getCodigo() + " -";
        }
        if (this.tipoReceitaExtra != null) {
            parametros.add(new ParametrosRelatorios(" rec.TRANSPORTADO ", ":transportado", null, OperacaoRelatorio.IGUAL, TipoReceitaExtraorcamentaria.RECEBIDAS_EXERCICIO.equals(tipoReceitaExtra) ? "0" : "1", null, 1, false));
            filtros += " Tipo de Receita Extra: " + tipoReceitaExtra.getDescricao() + " -";
        }
        if (this.situacaoReceitaExtra != null) {
            parametros.add(new ParametrosRelatorios(" rec.SITUACAORECEITAEXTRA ", ":situacao", null, OperacaoRelatorio.LIKE, situacaoReceitaExtra.name(), null, 1, false));
            filtros += " Situação: " + situacaoReceitaExtra.getDescricao() + " -";
        }
        filtrosParametrosUnidade(parametros);
        adicionarExercicio(parametros);
        return parametros;
    }

    public void recuperarContaBancariaApartirDaContaFinanceira() {
        contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
    }

    public List<ContaExtraorcamentaria> completarContasExtraorcamentarias(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().listaFiltrandoExtraorcamentario(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<Conta> completarContasDeDestinacao(String parte) {
        return relatorioContabilSuperFacade.getContaFacade().buscarContasDeDestinacaoDeRecursos(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }

    public List<ClasseCredor> completarClassesCredor(String parte) {
        if (pessoa != null) {
            return relatorioContabilSuperFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), pessoa);
        } else {
            return relatorioContabilSuperFacade.getClasseCredorFacade().listaFiltrandoDescricao(parte.trim());
        }
    }

    public List<EventoContabil> completarEventosContabeis(String parte) {
        return relatorioContabilSuperFacade.getEventoContabilFacade().buscarEventosContabeisPorTipoEvento(parte.trim(), TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA);
    }

    public List<SelectItem> getTiposDeContaExtra() {
        return Util.getListSelectItem(TipoContaExtraorcamentaria.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoReceitaExtra.values());
    }

    public List<Pessoa> completarCredor(String parte) {
        return relatorioContabilSuperFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public TipoContaExtraorcamentaria getTipoContaExtra() {
        return tipoContaExtra;
    }

    public void setTipoContaExtra(TipoContaExtraorcamentaria tipoContaExtra) {
        this.tipoContaExtra = tipoContaExtra;
    }

    @Override
    public String getNomeRelatorio() {
        return "RECEITA-EXTRA";
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public SituacaoReceitaExtra getSituacaoReceitaExtra() {
        return situacaoReceitaExtra;
    }

    public void setSituacaoReceitaExtra(SituacaoReceitaExtra situacaoReceitaExtra) {
        this.situacaoReceitaExtra = situacaoReceitaExtra;
    }

    public TipoReceitaExtraorcamentaria getTipoReceitaExtra() {
        return tipoReceitaExtra;
    }

    public void setTipoReceitaExtra(TipoReceitaExtraorcamentaria tipoReceitaExtra) {
        this.tipoReceitaExtra = tipoReceitaExtra;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }
}
