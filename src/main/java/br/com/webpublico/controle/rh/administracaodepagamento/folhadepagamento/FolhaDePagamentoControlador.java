package br.com.webpublico.controle.rh.administracaodepagamento.folhadepagamento;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static br.com.webpublico.enums.TipoFolhaDePagamento.*;

/**
 * @Author peixe on 30/01/2017  12:21.
 */
@ManagedBean(name = "folhaDePagamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaFolha", pattern = "/folha-de-pagamento/novo/", viewId = "/faces/rh/administracaodepagamento/folhapagamento/edita.xhtml"),
    @URLMapping(id = "editarFolha", pattern = "/folha-de-pagamento/editar/#{folhaDePagamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhapagamento/edita.xhtml"),
    @URLMapping(id = "listarFolhas", pattern = "/folha-de-pagamento/listar/", viewId = "/faces/rh/administracaodepagamento/folhapagamento/lista.xhtml"),
    @URLMapping(id = "verFolha", pattern = "/folha-de-pagamento/ver/#{folhaDePagamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhapagamento/visualizar.xhtml")
})
public class FolhaDePagamentoControlador extends PrettyControlador<FolhaDePagamento> implements Serializable, CRUD {

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private boolean podeConfigurarRetroacaoParaDecimo = false;

    @Override
    public String getCaminhoPadrao() {
        return "/folha-de-pagamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public FolhaDePagamentoFacade getFacede() {
        return folhaDePagamentoFacade;
    }

    public FolhaDePagamentoControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamento.class);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "novaFolha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        podeConfigurarRetroacaoParaDecimo = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_CONFIGURACAO_RETROACAO_DECIMO_TERCEIRO);
    }

    @URLAction(mappingId = "verFolha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarFolha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        podeConfigurarRetroacaoParaDecimo = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_CONFIGURACAO_RETROACAO_DECIMO_TERCEIRO);
    }

    public void efetivarFolha() {
        FolhaDePagamento f = folhaDePagamentoFacade.recuperarAlternativo(selecionado.getId());
        if (f == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento não encontrada!");
        } else if (f.folhaEfetivada()) {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + f.getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
        } else {
            selecionado = folhaDePagamentoFacade.efetivarFolhaDePagamento(f, new Date());
            processarServidoresPortalTransparencia(selecionado);
            FacesUtil.addOperacaoRealizada("Folha Efetivada com sucesso!");
        }
    }

    public void processarServidoresPortalTransparencia(FolhaDePagamento folhaDePagamento) {
        AssistenteBarraProgresso assistenteBarraProgresso = new AssistenteBarraProgresso();
        assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        assistenteBarraProgresso.setDataAtual(sistemaFacade.getDataOperacao());
        assistenteBarraProgresso.setDescricaoProcesso("Recuperando servidores para o portal transparência");

        CompletableFuture<AssistenteBarraProgresso> future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () ->
            folhaDePagamentoFacade.recuperarInformacoesPortalTransparencia(assistenteBarraProgresso, folhaDePagamento));

        future.thenAccept(assistente -> {
            assistente.setDescricaoProcesso("Salvando servidores para o portal transparência");
            AsyncExecutor.getInstance().execute(assistente,
                ()-> folhaDePagamentoFacade.salvarServidores(assistente));
        });
    }


    @Override
    public void salvar() {
        try {
            validarCampos();
            validaRegrasEspecificas();
            definirPropriedades();
            validaRegrasEspecificas();
            super.salvar();
        } catch (ValidacaoException val) {
            logger.error("Erro de validação. ", val);
            FacesUtil.printAllFacesMessages(val.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar", e);
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException val = new ValidacaoException();
        List<FolhaDePagamento> folhaDePagamentos = folhaDePagamentoFacade.buscarFolhasPorMesAnoTipoAndVersao(selecionado.getMes(), selecionado.getAno(), selecionado.getTipoFolhaDePagamento(), selecionado.getVersao());
        if (folhaDePagamentos != null && !folhaDePagamentos.isEmpty()) {
            for (FolhaDePagamento folhaDePagamento : folhaDePagamentos) {
                if (!folhaDePagamento.getId().equals(selecionado.getId())) {
                    val.adicionarMensagemDeOperacaoNaoPermitida("Já existe folha de pagamento com o parâmetros Mês: " + selecionado.getMes() + " Ano: " + selecionado.getAno() + " Tipo: " + selecionado.getTipoFolhaDePagamento().getDescricao() + " Versão: " + selecionado.getVersao());
                }
            }

        }
        val.lancarException();

        return true;
    }

    private void validarCampos() {
        ValidacaoException val = new ValidacaoException();
        if (selecionado.getCompetenciaFP() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo competência deve ser informado.");
        }
        if (selecionado.getVersao() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo versão deve ser informado.");
        }
        if (selecionado.getQtdeMesesRetroacao() == null) {
            val.adicionarMensagemDeCampoObrigatorio("O campo Quantidade de Meses de Retroação deve ser preenchido.");
        }
        val.lancarException();
    }

    private void definirPropriedades() {
        if (selecionado.getCompetenciaFP() != null) {
            selecionado.setAno(selecionado.getCompetenciaFP().getExercicio().getAno());
            selecionado.setMes(selecionado.getCompetenciaFP().getMes());
            selecionado.setTipoFolhaDePagamento(selecionado.getCompetenciaFP().getTipoFolhaDePagamento());
            if (hierarquiaOrganizacional != null) {
                selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            }
        }
    }

    public void atribuirQuantidadeMesesRetroacao() {
        if (selecionado.getCompetenciaFP() != null) {
            if (desabilitarConfiguracaoRetrocao()) {
                selecionado.setQtdeMesesRetroacao(0);
            } else {
                selecionado.setQtdeMesesRetroacao(1);
            }
        }

    }

    public void buscarUltimaVersao() {
        if (selecionado.getCompetenciaFP() != null) {
            Integer versao = folhaDePagamentoFacade.obterVersaoFolhaDePagamento(selecionado.getCompetenciaFP(), selecionado.getCompetenciaFP().getTipoFolhaDePagamento());
            selecionado.setVersao(versao);
        }
        definirPropriedades();
        atribuirQuantidadeMesesRetroacao();
    }

    public void abrirFolhaEfetivada() {
        if (selecionado == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento indisponível.");
            return;
        }
        selecionado = folhaDePagamentoFacade.recuperarAlternativo(selecionado.getId());
        if (StatusCompetencia.EFETIVADA.equals(selecionado.getCompetenciaFP().getStatusCompetencia())) {
            FacesUtil.addOperacaoNaoRealizada("A Folha de Pagamento do tipo '" + selecionado.getTipoFolhaDePagamento().getDescricao() + "' já está com a competência efetivada. Reabra a competência para prosseguir.");
        }
        selecionado.setEfetivadaEm(null);
        selecionado = folhaDePagamentoFacade.salvarRetornando(selecionado);
        FacesUtil.addOperacaoRealizada("Folha foi aberta com sucesso.");
        FacesUtil.redirecionamentoInterno("/folha-de-pagamento/ver/" + selecionado.getId());
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public boolean desabilitarConfiguracaoRetrocao() {
        if (selecionado != null && selecionado.getTipoFolhaDePagamento() != null) {
            if (COMPLEMENTAR.equals(selecionado.getTipoFolhaDePagamento()) || RESCISAO_COMPLEMENTAR.equals(selecionado.getTipoFolhaDePagamento())) {
                return true;
            }
            if ((SALARIO_13.equals(selecionado.getTipoFolhaDePagamento()) || ADIANTAMENTO_13_SALARIO.equals(selecionado.getTipoFolhaDePagamento())) && !podeConfigurarRetroacaoParaDecimo) {
                return true;
            }
        }
        return false;
    }
}
