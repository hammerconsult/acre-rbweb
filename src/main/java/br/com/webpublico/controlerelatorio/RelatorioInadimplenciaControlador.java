package br.com.webpublico.controlerelatorio;

/**
 * Created by HardRock on 12/05/2017.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRelatorioInadimplencia;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioInadimplencia;
import br.com.webpublico.entidadesauxiliares.Inadimplencia;
import br.com.webpublico.entidadesauxiliares.TotalizadorInadimplenciaPorDivida;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.RelatorioInadimplenciaFacade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ViewScoped
@ManagedBean(name = "relatorioInadimplenciaControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioInadimplencia",
        pattern = "/tributario/relatorio-inadimplencia/",
        viewId = "/faces/tributario/relatorio/relatorioinadimplencia.xhtml")
})
public class RelatorioInadimplenciaControlador implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(RelatorioInadimplenciaControlador.class);
    @EJB
    private RelatorioInadimplenciaFacade facade;
    private FiltroRelatorioInadimplencia filtro;
    private List<Future<AssistenteRelatorioInadimplencia>> futures;
    private List<Inadimplencia> inadimplencias;
    private List<TotalizadorInadimplenciaPorDivida> totalizadorDivida;
    private AssistenteBarraProgresso assistenteBarraProgresso;

    @URLAction(mappingId = "novoRelatorioInadimplencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioInadimplencia();
        filtro.novoRelatorio();
        iniciarFuture();
        inadimplencias = Lists.newArrayList();
        iniciarAssistente();
    }

    public void novoRelatorio() {
        filtro.setTexto("");
    }

    public void iniciarFuture() {
        futures = Lists.newArrayList();
    }

    public void iniciarAssistente() {
        assistenteBarraProgresso = new AssistenteBarraProgresso(0);
    }

    public void consultaAndamentoEmissaoRelatorio() {
        if (isFutureDadosRelatorioConcluida()) {
            FacesUtil.executaJavaScript("terminaRelatorio()");
        }
    }

    public boolean isFutureDadosRelatorioConcluida() {
        if (futures == null) {
            return false;
        }
        for (Future<AssistenteRelatorioInadimplencia> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

    public List<SelectItem> getOrdenacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        for (Ordenacao ordem : Ordenacao.values()) {
            retorno.add(new SelectItem(ordem, ordem.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getOrdensInadimplencias() {
        List<SelectItem> retorno = new ArrayList<>();
        for (FiltroRelatorioInadimplencia.OrdenacaoInadimplencia ordem : FiltroRelatorioInadimplencia.OrdenacaoInadimplencia.values()) {
            retorno.add(new SelectItem(ordem, ordem.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposDebitos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todos"));
        for (FiltroRelatorioInadimplencia.TipoDebito tipo : FiltroRelatorioInadimplencia.TipoDebito.values()) {
            if (tipo.isVisivel()) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
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

    public void definirNullParaPessoa() {
        filtro.setPessoa(null);
    }

    public void limparCampos() {
        if (filtro.getListaDividas() != null) {
            filtro.getListaDividas().clear();
        }
        filtro.iniciarVariaveisCadastroRural();
        filtro.iniciarVariaveisCadastroContribuinte();
        filtro.iniciarVariaveisCadastroImobiliario();
        filtro.iniciarVariaveisCadastroEconomico();
    }

    public List<Pessoa> completarPessoas(String parte) {
        if (filtro.isPessoaFisica()) {
            return facade.getPessoaFacade().listaPessoasFisicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
        } else if (filtro.isPessoaJuridica()) {
            return facade.getPessoaFacade().listaPessoasJuridicas(parte.trim(), SituacaoCadastralPessoa.ATIVO);
        }
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public void removerDivida(Divida divida) {
        if (filtro.getListaDividas().contains(divida)) {
            filtro.getListaDividas().remove(divida);
        }
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        List<Divida> dividas = Lists.newArrayList();
        if (filtro.isCadastroContribuinte()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.PESSOA);

        } else if (filtro.isCadastroImobiliario()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.IMOBILIARIO);

        } else if (filtro.isCadastroEconomico()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.ECONOMICO);

        } else if (filtro.isCadastroRural()) {
            dividas = facade.getDividaFacade().listaDividasPorTipoCadastro(TipoCadastroTributario.RURAL);
        } else {
            toReturn.add(new SelectItem(null, "Selecione um tipo de cadastro para filtrar as dívidas"));
        }
        for (Divida t : dividas) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            filtro.getListaDividas().add(filtro.getDivida());
            filtro.setDivida(new Divida());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getDivida() == null || filtro.getDivida().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dívida deve ser informado.");
        } else if (filtro.getListaDividas().contains(filtro.getDivida())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida já foi selecionada.");
        }
        ve.lancarException();
    }

    private void validarCampo() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getVencimentoInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de vencimento inicial deve ser informado.");
        } else {
            filtro.setVencimentoInicial(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoInicial()));
        }
        if (filtro.getVencimentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de vencimento deve ser informado.");
        } else {
            filtro.setVencimentoFinal(Util.getDataHoraMinutoSegundoZerado(filtro.getVencimentoFinal()));
        }
        if (filtro.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de cadastro deve ser informado.");
        }
        ve.lancarException();
        if (filtro.getVencimentoInicial().after(filtro.getVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento inicial deve ser inferior ou igual a data de vencimento final.");
        }
        if (filtro.getLancamentoInicial() != null && filtro.getLancamentoFinal() != null) {
            if (filtro.getLancamentoInicial().after(filtro.getLancamentoFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de lançamento inicial deve ser inferior ou igual a data de lançamento final.");
            }
        }
        ve.lancarException();
    }

    private void iniciarApresentacaoFiltros() {
        filtro.setFiltros("");
        if (filtro.isCadastroImobiliario()) {
            filtro.setFiltros("Cadastro Imobiliário: ");
        }
        if (filtro.isCadastroEconomico()) {
            filtro.setFiltros("Cadastro Econômico: ");
        }
        if (filtro.isCadastroRural()) {
            filtro.setFiltros("Cadastro Rural: ");
        }
    }

    public void iniciarConsulta() {
        try {
            validarCampo();
            iniciarAssistente();
            iniciarApresentacaoFiltros();
            iniciarFuture();
            tratarDividasSelecionadas();
            filtro.setTexto("Executando Consulta");
            FacesUtil.executaJavaScript("myArguarde.show()");
            FacesUtil.atualizarComponente("myAguardeForm");
            executarConsulta();
            FacesUtil.executaJavaScript("acompanhaConsulta()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("myArguarde.hide()");
        }
    }

    public void executarConsulta() {
        if (filtro.isCadastroContribuinte()) {
            futures.add(facade.buscarInadimplenciasContribuinte(filtro, montarCondicaoCadastroContribuinte().toString()));
        }
        if (filtro.isCadastroImobiliario()) {
            futures.add(facade.buscarInadimplenciasCadastroImobiliario(filtro, montarCondicaoCadastroImobiliario().toString()));
        }
        if (filtro.isCadastroEconomico()) {
            futures.add(facade.buscarInadimplenciasCadastroEconomico(filtro, montarCondicaoCadastroEconomico().toString()));
        }
        if (filtro.isCadastroRural()) {
            futures.add(facade.buscarInadimplenciasCadastroRural(filtro, montarCondicaoCadastroRural().toString()));
        }
    }

    public void iniciarAtualizacaoValor() {
        try {
            inadimplencias = getInadimplencias();
            iniciarFuture();
            if (!inadimplencias.isEmpty()) {
                assistenteBarraProgresso = new AssistenteBarraProgresso(inadimplencias.size());
                futures.add(facade.atualizarValoresDevidos(filtro, assistenteBarraProgresso, inadimplencias));
            }
        } catch (Exception e) {
            log.error("Não foi possível atualizar os valores para o relatório {} ", e);
            FacesUtil.addError("Atenção!", "Não foi possível atualizar os valores para o relatório! (ERROR:" + e.getMessage() + ")");
        }
        if (futures.isEmpty()) {
            FacesUtil.executaJavaScript("myArguarde.hide()");
        }
    }

    public void consultaAndamentoQuery() {
        if (futures != null) {
            for (Future<AssistenteRelatorioInadimplencia> future : futures) {
                if (future == null || !future.isDone()) {
                    return;
                }
            }
            iniciarAtualizacaoValor();
            filtro.setTexto("Atualizando Valores");
            FacesUtil.atualizarComponente("myAguardeForm");
            FacesUtil.executaJavaScript("acompanhaAtualizacao()");
        }
    }

    private void tratarDividasSelecionadas() {
        if (filtro.getDivida() != null) {
            if (!filtro.getListaDividas().contains(filtro.getDivida())) {
                filtro.getListaDividas().add(filtro.getDivida());
            }
        }
        filtro.setDivida(null);
    }

    public void imprimir() throws ExecutionException, InterruptedException, IOException, JRException {
        String tituloRelatorio = getTituloRelatorio();
        totalizadorDivida = facade.calcularTotalizadorDivida(inadimplencias);
        facade.gerarRelatorio(filtro, inadimplencias, totalizadorDivida, tituloRelatorio);
    }

    public String getTituloRelatorio() {
        String titulo = "Relatório de Inadimplências por ";
        if (filtro.isCadastroContribuinte()) {
            titulo += "Contribuinte Geral";
        }
        if (filtro.isCadastroImobiliario()) {
            titulo += "Cadastro Imobiliário";
        }
        if (filtro.isCadastroEconomico()) {
            titulo += "Cadastro Econômico";
        }
        if (filtro.isCadastroRural()) {
            titulo += "Cadastro Rural";
        }
        titulo += filtro.getDetalhado() ? " (Detalhado)" : " (Resumido)";
        return titulo;
    }

    private String getIdsDividaAsString() {
        String idDivida = "";
        if (filtro.getListaDividas().size() > 0) {
            for (Divida divida : filtro.getListaDividas()) {
                idDivida += divida.getId() + ",";
            }
        }
        idDivida = (idDivida.substring(0, idDivida.length() - 1));
        return idDivida;
    }

    private List<Inadimplencia> getInadimplencias() throws InterruptedException, ExecutionException {
        HashSet<Inadimplencia> setInadimplencias = Sets.newHashSet();
        for (Future<AssistenteRelatorioInadimplencia> future : futures) {
            setInadimplencias.addAll(future.get().getResultado());
        }
        return Lists.newLinkedList(setInadimplencias);
    }

    public FiltroRelatorioInadimplencia getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioInadimplencia filtro) {
        this.filtro = filtro;
    }

    public List<Logradouro> completarLogradouro(String parte) {
        Bairro bairro = null;
        if (filtro.isCadastroEconomico() && filtro.getBairroCmc() != null) {
            bairro = filtro.getBairroCmc();
        }
        if (filtro.isCadastroImobiliario() && filtro.getBairroBci() != null) {
            bairro = filtro.getBairroBci();
        }
        if (bairro != null) {
            return facade.getLogradouroFacade().completaLogradourosPorBairro(parte.trim(), bairro);
        }
        return facade.getLogradouroFacade().listaFiltrando(parte.trim(), "nome", "nomeUsual", "nomeAntigo");
    }

    public List<Bairro> completarBairro(String parte) {
        return facade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<TipoAutonomo> completarTipoAutonomo(String parte) {
        return facade.getTipoAutonomoFacade().completaTipoAutonomo(parte.trim());
    }

    public List<SelectItem> getClassificacoesAtividades() {
        return Util.getListSelectItem(ClassificacaoAtividade.values());
    }

    public List<SelectItem> getGrausDeRisco() {
        return Util.getListSelectItem(GrauDeRiscoDTO.values());
    }

    public List<SelectItem> getNaturezasJuridica() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, " "));
        for (NaturezaJuridica natureza : facade.getNaturezaJuridicaFacade().buscarNaturezasJuridicasAtivas()) {
            toReturn.add(new SelectItem(natureza, natureza.getCodigo() + " - " + natureza.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCadastro() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricaoLonga()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposImoveis() {
        return Util.getListSelectItem(TipoImovel.values());
    }

    public StringBuilder montarCondicaoCadastroContribuinte() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getPessoa() != null) {
            condicao.append(" and p.id = ").append(filtro.getPessoa().getId());
            filtros.append(" Pessoa: ").append(filtro.getPessoa()).append(", ");
        }
        if (filtro.getTipoPessoa() != null) {
            filtros.append(" Tipo de pessoa: ").append(filtro.getTipoPessoa().getDescricao()).append(", ");
        } else {
            filtros.append(" Tipo de pessoa: Física e Jurídica" ).append(", ");
        }
        filtro.setFiltros(filtro.getFiltros() + filtros.toString());
        return condicao;
    }

    public StringBuilder montarCondicaoCadastroImobiliario() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getInscricaoBciInicial() != null && !filtro.getInscricaoBciInicial().isEmpty()) {
            condicao.append(" and imo.inscricaocadastral >= ").append("'" + filtro.getInscricaoBciInicial() + "'");
            filtros.append("Inscrição inicial: ").append(filtro.getInscricaoBciInicial()).append(", ");
        }
        if (filtro.getInscricaoBciFinal() != null && !filtro.getInscricaoBciFinal().isEmpty()) {
            condicao.append(" and imo.inscricaocadastral <= ").append("'" + filtro.getInscricaoBciFinal() + "'");
            filtros.append("Inscrição final: ").append(filtro.getInscricaoBciFinal()).append(", ");
        }
        if (filtro.getTipoImovelBci() != null && TipoImovel.PREDIAL.equals(filtro.getTipoImovelBci())) {
            condicao.append(" and exists (select c.id from construcao c where imovel_id = imo.id and coalesce(c.cancelada, 0) = 0 and rownum = 1) ");
            filtros.append("Tipo de imóvel: ").append(filtro.getTipoImovelBci().getDescricao()).append(", ");
        }
        if (filtro.getTipoImovelBci() != null && TipoImovel.TERRITORIAL.equals(filtro.getTipoImovelBci())) {
            condicao.append(" and not exists (select c.id from construcao c where imovel_id = imo.id and coalesce(c.cancelada, 0) = 0 and rownum = 1) ");
            filtros.append("Tipo de imóvel: ").append(filtro.getTipoImovelBci().getDescricao()).append(", ");
        }
        if (filtro.getBairroBci() != null) {
            condicao.append(" and bairro.id = ").append(filtro.getBairroBci().getId());
            filtros.append("Bairro: ").append(filtro.getBairroBci()).append(", ");
        }
        if (filtro.getLogradouroBci() != null) {
            condicao.append(" and log.id = ").append(filtro.getLogradouroBci().getId());
            filtros.append("Logradouro: ").append(filtro.getLogradouroBci()).append(", ");
        }
        if (filtro.getSetorBciInicial() != null && !filtro.getSetorBciInicial().isEmpty()) {
            condicao.append(" and setor.codigo >= '" + filtro.getSetorBciInicial() + "'");
            filtros.append("Setor inicial: ").append(filtro.getSetorBciInicial()).append(", ");
        }
        if (filtro.getSetorBciFinal() != null && !filtro.getSetorBciFinal().isEmpty()) {
            condicao.append(" and setor.codigo <= '" + filtro.getSetorBciFinal() + "'");
            filtros.append("Setor inicial: ").append(filtro.getSetorBciFinal()).append(", ");
        }
        if (filtro.getQuadraBciInicial() != null && !filtro.getQuadraBciInicial().isEmpty()) {
            condicao.append(" and quadra.codigo >= ").append("'" + filtro.getQuadraBciInicial() + "'");
            filtros.append("Quadra inicial: ").append(filtro.getQuadraBciInicial()).append(", ");
        }
        if (filtro.getQuadraBciFinal() != null && !filtro.getQuadraBciFinal().isEmpty()) {
            condicao.append(" and quadra.codigo <= ").append("'" + filtro.getQuadraBciFinal() + "'");
            filtros.append("Quadra inicial: ").append(filtro.getQuadraBciFinal()).append(", ");
        }
        if (filtro.getLoteBciInicial() != null && !filtro.getLoteBciInicial().isEmpty()) {
            condicao.append(" and lote.codigoLote >= ").append("'" + filtro.getLoteBciInicial() + "'");
            filtros.append("Lote inicial: ").append(filtro.getLoteBciInicial()).append(", ");
        }
        if (filtro.getLoteBciFinal() != null && !filtro.getLoteBciFinal().isEmpty()) {
            condicao.append(" and lote.codigoLote <= ").append("'" + filtro.getLoteBciFinal() + "'");
            filtros.append("Lote inicial: ").append(filtro.getLoteBciFinal()).append(", ");
        }
        filtro.setFiltros(filtro.getFiltros() + filtros);
        return condicao;
    }

    public StringBuilder montarCondicaoCadastroEconomico() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getCmcInicial() != null && !filtro.getCmcInicial().isEmpty()) {
            condicao.append(" and cmc.inscricaocadastral >= ").append("'" + filtro.getCmcInicial() + "'");
            filtros.append("C.M.C. inicial: ").append(filtro.getCmcInicial()).append(", ");
        }
        if (filtro.getCmcFinal() != null && !filtro.getCmcFinal().isEmpty()) {
            condicao.append(" and cmc.inscricaocadastral <= ").append("'" + filtro.getCmcFinal() + "'");
            filtros.append("C.M.C. final: ").append(filtro.getCmcFinal()).append(", ");
        }
        if (filtro.getBairroCmc() != null) {
            condicao.append(" and bairro.id = ").append(filtro.getBairroCmc().getId());
            filtros.append("Bairro: ").append(filtro.getBairroCmc()).append(", ");
        }
        if (filtro.getLogradouroCmc() != null) {
            condicao.append(" and log.id = ").append(filtro.getLogradouroCmc().getId());
            filtros.append("Logradouro: ").append(filtro.getLogradouroCmc()).append(", ");
        }
        if (filtro.getClassificacaoAtividade() != null) {
            condicao.append(" and cmc.classificacaoAtividade = ").append("'" + filtro.getClassificacaoAtividade().name() + "'");
            filtros.append("Classificação atividade: ").append(filtro.getClassificacaoAtividade().getDescricao()).append(", ");
        }
        if (filtro.getNaturezaJuridica() != null) {
            condicao.append(" and cmc.naturezajuridica_id = ").append(filtro.getNaturezaJuridica().getId());
            filtros.append("Natureza jurídica: ").append(filtro.getNaturezaJuridica().getDescricao()).append(", ");
        }
        if (filtro.getGrauDeRisco() != null) {
            condicao.append(" and cnae.grauDeRisco = ").append("'" + filtro.getGrauDeRisco().name() + "'");
            filtros.append("Grau de risco: ").append(filtro.getGrauDeRisco()).append(", ");
        }
        if (filtro.getTipoAutonomo() != null) {
            condicao.append(" and cmc.tipoAutonomo_id = ").append(filtro.getTipoAutonomo().getId());
            filtros.append("Tipo autonomo: ").append(filtro.getTipoAutonomo()).append(", ");
        }
        filtro.setFiltros(filtro.getFiltros() + filtros);
        return condicao;
    }

    public StringBuilder montarCondicaoCadastroRural() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        condicao.append(montarCondicaoGeral());

        if (filtro.getInscricaoRuralInicial() != null && !filtro.getInscricaoRuralInicial().isEmpty()) {
            condicao.append(" and rural.codigo >= ").append("'" + filtro.getInscricaoRuralInicial() + "'");
            filtros.append("Inscrição inicial: ").append(filtro.getInscricaoRuralInicial() + ", ");
        }
        if (filtro.getInscricaoRuralFinal() != null && !filtro.getInscricaoRuralFinal().isEmpty()) {
            condicao.append(" and rural.codigo <= ").append("'" + filtro.getInscricaoRuralFinal() + "'");
            filtros.append("Inscrição final: ").append(filtro.getInscricaoBciFinal() + ", ");
        }
        if (filtro.getLocalizacaoLote() != null && !filtro.getLocalizacaoLote().isEmpty()) {
            condicao.append(" and rural.localizacaoLote like ").append("'" + filtro.getLocalizacaoLote() + "'");
            filtros.append("Localização: ").append(filtro.getLocalizacaoLote() + ", ");
        }
        if (filtro.getNumeroIncra() != null && !filtro.getNumeroIncra().isEmpty()) {
            condicao.append(" and rural.numeroIncra like ").append("'" + filtro.getNumeroIncra() + "'");
            filtros.append("Número do incra: ").append(filtro.getNumeroIncra() + ", ");
        }
        filtro.setFiltros(filtro.getFiltros() + filtros);
        return condicao;
    }

    public StringBuilder montarCondicaoGeral() {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        if (filtro.getVencimentoInicial() != null && filtro.getVencimentoFinal() != null) {
            condicao.append(" where pvd.vencimento between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getVencimentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getVencimentoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Vencimento inicial: ").append(DataUtil.getDataFormatada(filtro.getVencimentoInicial())).append(", ");
            filtros.append("Vencimento final: ").append(DataUtil.getDataFormatada(filtro.getVencimentoFinal())).append(", ");
        }
        if (filtro.getLancamentoInicial() != null && filtro.getLancamentoFinal() != null) {
            condicao.append(" and calc.dataCalculo between ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getLancamentoInicial()) + "', 'dd/MM/yyyy') and ")
                .append(" to_date('" + DataUtil.getDataFormatada(filtro.getLancamentoFinal()) + "', 'dd/MM/yyyy')");

            filtros.append("Lançamento inicial: ").append(DataUtil.getDataFormatada(filtro.getLancamentoInicial())).append(", ");
            filtros.append("Lançamento final: ").append(DataUtil.getDataFormatada(filtro.getLancamentoFinal())).append(", ");
        }

        condicao.append(" and spvd.situacaoparcela = ").append("'" + SituacaoParcela.EM_ABERTO.name() + "'");

        if (filtro.getExercicioDivida() != null) {
            condicao.append(" and vd.exercicio_id = ").append(filtro.getExercicioDivida().getId());
            filtros.append("Exercício da dívida = ").append(filtro.getExercicioDivida().getAno());
        }
        if (filtro.getTipoDebito() != null) {
            if (FiltroRelatorioInadimplencia.TipoDebito.DO_EXERCICIO.equals(filtro.getTipoDebito())) {
                condicao.append(" and (coalesce(pvd.dividaAtiva,0) = 0 and coalesce(pvd.dividaAtivaAjuizada,0) = 0) ");
            } else if (FiltroRelatorioInadimplencia.TipoDebito.DIVIDA_ATIVA.equals(filtro.getTipoDebito())) {
                condicao.append(" and coalesce(pvd.dividaAtiva,0) = 1 ");
            } else if (FiltroRelatorioInadimplencia.TipoDebito.DIVIDA_ATIVA_AJUIZADA.equals(filtro.getTipoDebito())) {
                condicao.append(" and coalesce(pvd.dividaAtivaAjuizada,0) = 1 ");
            }
            filtros.append(" Tipo de débito: ").append(filtro.getTipoDebito().getDescricao()).append(", ");
        }
        if (!filtro.getListaDividas().isEmpty()) {
            condicao.append(" and vd.divida_id in (").append(getIdsDividaAsString()).append(")");
            for (Divida dv : filtro.getListaDividas()) {
                filtros.append("Dívidas: ").append(dv.getDescricao() + ", ");
            }
        } else {
            filtros.append("Todas Dívidas").append(", ");
        }
        if (filtro.getQuantidadeInadimplencias() > 0) {
            filtros.append(" Quantidade de inadimplências: " + filtro.getQuantidadeInadimplencias()).append(", ");
        }
        if (filtro.getDetalhado()) {
            filtros.append("(Relatório Detalhado) ").append(", ");
        } else {
            filtros.append("(Relatório Resumido) ").append(", ");
        }
        filtro.setFiltros(filtro.getFiltros() + filtros);
        return condicao;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
