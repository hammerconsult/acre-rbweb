package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SubConta;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubConta;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaDestino;
import br.com.webpublico.entidades.contabil.ConfiguracaoTransporteSaldoSubContaOrigem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.execucao.ConfiguracaoTransporteSaldoSubContaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConfiguracaoTransporteSubConta", pattern = "/contabil/configuracao-transporte-saldo-conta-financeira/novo/", viewId = "/faces/financeiro/configuracao-transporte-conta-financeira/edita.xhtml"),
    @URLMapping(id = "editarConfiguracaoTransporteSubConta", pattern = "/contabil/configuracao-transporte-saldo-conta-financeira/editar/#{configuracaoTransporteSaldoSubContaControlador.id}/", viewId = "/faces/financeiro/configuracao-transporte-conta-financeira/edita.xhtml"),
    @URLMapping(id = "listarConfiguracaoTransporteSubConta", pattern = "/contabil/configuracao-transporte-saldo-conta-financeira/listar/", viewId = "/faces/financeiro/configuracao-transporte-conta-financeira/lista.xhtml"),
    @URLMapping(id = "verConfiguracaoTransporteSubConta", pattern = "/contabil/configuracao-transporte-saldo-conta-financeira/ver/#{configuracaoTransporteSaldoSubContaControlador.id}/", viewId = "/faces/financeiro/configuracao-transporte-conta-financeira/visualizar.xhtml")
})
public class ConfiguracaoTransporteSaldoSubContaControlador extends PrettyControlador<ConfiguracaoTransporteSaldoSubConta> implements Serializable, CRUD {
    @EJB
    private ConfiguracaoTransporteSaldoSubContaFacade facade;
    private List<ConfiguracaoTransporteSaldoSubContaOrigem> origensFiltradas;
    private ConfiguracaoTransporteSaldoSubContaOrigem origem;
    private ConfiguracaoTransporteSaldoSubContaDestino destino;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ConfiguracaoTransporteSaldoSubContaControlador() {
        super(ConfiguracaoTransporteSaldoSubConta.class);
    }

    @URLAction(mappingId = "novoConfiguracaoTransporteSubConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicioOrigem(facade.getExercicioCorrente());
        selecionado.setExercicioDestino(facade.getExercicioPorAno(selecionado.getExercicioOrigem().getAno() + 1));
        facade.buscarOrigens(selecionado);
    }

    @URLAction(mappingId = "editarConfiguracaoTransporteSubConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        facade.recuperarHierarquias(selecionado);
        ordenarOrigens();
    }

    @URLAction(mappingId = "verConfiguracaoTransporteSubConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        facade.recuperarHierarquias(selecionado);
        ordenarOrigens();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/configuracao-transporte-saldo-conta-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void buscarOrigens() {
        try {
            Util.validarCampos(selecionado);
            facade.buscarOrigens(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public void atualizarParaGrupoDois() {
        try {
            List<ConfiguracaoTransporteSaldoSubContaOrigem> origensSelecionadas;
            if (origensFiltradas != null) {
                origensSelecionadas = getOrigensSelecionadas(origensFiltradas);
            } else {
                origensSelecionadas = getOrigensSelecionadas(selecionado.getOrigens());
            }
            if (origensSelecionadas == null || origensSelecionadas.isEmpty()) {
                throw new ValidacaoException("Deve ser selecionada uma ou mais origens.");
            }
            origensSelecionadas.forEach(ori -> {
                ori.setDestinos(Lists.newArrayList());
                ConfiguracaoTransporteSaldoSubContaDestino destino = facade.criarNovoDestinoPadrao(ori, selecionado.getExercicioDestino(), true);
                validarDestino(destino);
                ori.getDestinos().add(destino);
                ori.atualizarStyleText();
                Util.adicionarObjetoEmLista(selecionado.getOrigens(), ori);
                if (origensFiltradas != null) {
                    Util.adicionarObjetoEmLista(origensFiltradas, ori);
                }
            });
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarDestino(ConfiguracaoTransporteSaldoSubContaDestino destino) {
        ValidacaoException ve = new ValidacaoException();
        if (destino.getContaDeDestinacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi encontrado Fonte 2" + destino.getConfiguracaoOrigem().getContaDeDestinacao().getFonteDeRecursos().getCodigo().substring(1) + " em " + selecionado.getExercicioDestino().getAno() + ".");
        }
        if (destino.getUnidadeOrganizacional() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Não foi encontrado Unidade vigênte em 01/01/" + selecionado.getExercicioDestino() + " para a Unidade de origem " + destino.getConfiguracaoOrigem().getHierarquiaOrganizacional().toString());
        }
        ve.lancarException();
    }

    private List<ConfiguracaoTransporteSaldoSubContaOrigem> getOrigensSelecionadas(List<ConfiguracaoTransporteSaldoSubContaOrigem> origens) {
        return origens.stream().filter(ConfiguracaoTransporteSaldoSubContaOrigem::getSelecionado).collect(Collectors.toList());
    }

    public void atualizarOrigem() {
        try {
            validarAtualizarOrigem();
            origem.atualizarStyleText();
            Util.adicionarObjetoEmLista(selecionado.getOrigens(), origem);
            limparOrigem();
            FacesUtil.executaJavaScript("dialogOrigem.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao atualizar origens: ", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarAtualizarOrigem() {
        Util.validarCampos(origem);
        BigDecimal totalDosDestinos = origem.getValorTotalDestinos();
        ValidacaoException ve = new ValidacaoException();
        if (origem.getValor().compareTo(totalDosDestinos) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor total dos destinos <b>(" + Util.formatarValor(totalDosDestinos) +
                ")</b> deve ser igual ao valor da origem <b>(" + Util.formatarValor(origem.getValor()) + ")</b>");
        }
        ve.lancarException();
    }

    public void limparOrigem() {
        origem = null;
    }

    public void editarOrigem(ConfiguracaoTransporteSaldoSubContaOrigem configOrigem) {
        origem = (ConfiguracaoTransporteSaldoSubContaOrigem) Util.clonarObjeto(configOrigem);
        novoDestino();
    }

    public void adicionarDestino() {
        try {
            destino.setUnidadeOrganizacional(destino.getHierarquiaOrganizacional().getSubordinada());
            destino.setConfiguracaoOrigem(origem);
            validarAdicionarDestino();
            origem.atualizarStyleText();
            Util.adicionarObjetoEmLista(origem.getDestinos(), destino);
            novoDestino();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao adicionar destino: ", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarAdicionarDestino() {
        Util.validarCampos(destino);
        ValidacaoException ve = new ValidacaoException();
        BigDecimal totalAdicionado = origem.getValorTotalDestinos(destino);
        if (destino.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor deve ser superior a 0");
        }
        if (totalAdicionado.add(destino.getValor()).compareTo(origem.getValor()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor <b>(" + Util.formatarValor(destino.getValor()) + ")</b> somado com o valor já informado nos destinos <b>("
                + Util.formatarValor(totalAdicionado) + ")</b> deve ser igual ou inferior ao valor da origem <b>(" + Util.formatarValor(origem.getValor()) + ")</b>");
        }
        ve.lancarException();
    }

    public void novoDestino() {
        destino = new ConfiguracaoTransporteSaldoSubContaDestino();
        if (origem != null) {
            destino.setConfiguracaoOrigem(origem);
            destino.setValor(origem.getValorDisponivel());
        }
    }

    public void removerDestino(ConfiguracaoTransporteSaldoSubContaDestino destino) {
        origem.getDestinos().remove(destino);
    }

    public void editarDestino(ConfiguracaoTransporteSaldoSubContaDestino destino) {
        this.destino = (ConfiguracaoTransporteSaldoSubContaDestino) Util.clonarObjeto(destino);
    }

    public List<ContaDeDestinacao> completarContasDeDestinacaoDeRecursos(String parte) {
        if (selecionado.getExercicioDestino() != null) {
            return facade.buscarContasDeDestinacaoDeRecursos(parte, selecionado.getExercicioDestino());
        }
        return Lists.newArrayList();
    }

    public List<HierarquiaOrganizacional> completarHierarquiasOrcamentarias(String parte) {
        return facade.buscarHierarquiasOrganizacionais(parte, DataUtil.montaData(1, 1, selecionado.getExercicioDestino().getAno()).getTime());
    }

    public List<SubConta> completarContasFinanceiras(String parte) {
        if (selecionado.getExercicioDestino() != null) {
            return facade.buscarContasFinanceiras(parte, selecionado.getExercicioOrigem());
        }
        return Lists.newArrayList();
    }

    public void selecionarOuDesselecionarTodasOrigens(boolean selecionarDesselecionar) {
        if (origensFiltradas != null) {
            for (ConfiguracaoTransporteSaldoSubContaOrigem configOrigemFiltrada : origensFiltradas) {
                configOrigemFiltrada.setSelecionado(selecionarDesselecionar);
                Util.adicionarObjetoEmLista(selecionado.getOrigens(), configOrigemFiltrada);
            }
        } else {
            for (ConfiguracaoTransporteSaldoSubContaOrigem configOrigem : selecionado.getOrigens()) {
                configOrigem.setSelecionado(selecionarDesselecionar);
            }
        }
    }

    public boolean hasOrigemNaoSelecionada() {
        if (origensFiltradas != null) {
            for (ConfiguracaoTransporteSaldoSubContaOrigem configOrigemFiltrada : origensFiltradas) {
                if (!configOrigemFiltrada.getSelecionado()) {
                    return true;
                }
            }
        } else {
            for (ConfiguracaoTransporteSaldoSubContaOrigem configOrigem : selecionado.getOrigens()) {
                if (!configOrigem.getSelecionado()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void atualizarAoSelecionarOrigem(ConfiguracaoTransporteSaldoSubContaOrigem origemSelecionada, boolean selecionarDesselecionar) {
        origemSelecionada.setSelecionado(selecionarDesselecionar);
        if (origensFiltradas != null) {
            Util.adicionarObjetoEmLista(selecionado.getOrigens(), origemSelecionada);
        }
    }

    private void ordenarOrigens() {
        selecionado.getOrigens().sort((o1, o2) -> ComparisonChain.start()
            .compare(o1.getSubConta().getCodigo(), o2.getSubConta().getCodigo())
            .compare(o1.getHierarquiaOrganizacional().getCodigo(), o2.getHierarquiaOrganizacional().getCodigo())
            .compare(o1.getContaDeDestinacao().getFonteDeRecursos().getCodigo(), o2.getContaDeDestinacao().getFonteDeRecursos().getCodigo())
            .result());
    }

    public void gerarRelatorio(String tipoExtensao) {
        if (selecionado.getId() != null) {
            try {
                RelatorioDTO dto = new RelatorioDTO();
                dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensao));
                dto.adicionarParametro("USER", facade.getUsuarioCorrente().getNome(), false);
                dto.setNomeParametroBrasao("IMAGEM");
                dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
                dto.adicionarParametro("idConfiguracao", selecionado.getId());
                dto.setNomeRelatorio("Relatório Configuração Transporte do Saldo da Conta Financeira de  " + selecionado.getExercicioOrigem().getAno() + " para " + selecionado.getExercicioDestino().getAno());
                dto.setApi("contabil/configuracao-transporte-saldo-subconta/");
                ReportService.getInstance().gerarRelatorio(facade.getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            } catch (WebReportRelatorioExistenteException e) {
                ReportService.getInstance().abrirDialogConfirmar(e);
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            }
        }
    }

    public List<ConfiguracaoTransporteSaldoSubContaOrigem> getOrigensFiltradas() {
        return origensFiltradas;
    }

    public void setOrigensFiltradas(List<ConfiguracaoTransporteSaldoSubContaOrigem> origensFiltradas) {
        this.origensFiltradas = origensFiltradas;
    }

    public ConfiguracaoTransporteSaldoSubContaOrigem getOrigem() {
        return origem;
    }

    public void setOrigem(ConfiguracaoTransporteSaldoSubContaOrigem origem) {
        this.origem = origem;
    }

    public ConfiguracaoTransporteSaldoSubContaDestino getDestino() {
        return destino;
    }

    public void setDestino(ConfiguracaoTransporteSaldoSubContaDestino destino) {
        this.destino = destino;
    }
}
