package br.com.webpublico.controle.rh.administracaodepagamento.folhadepagamento;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.rh.administracaodepagamento.FolhaDePagamentoSimulacao;
import br.com.webpublico.enums.StatusCompetencia;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.FolhaDePagamentoSimulacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

/**
 * @Author peixe on 30/01/2017  12:21.
 */
@ManagedBean(name = "folhaDePagamentoSimulacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-folha-simulacao", pattern = "/folha-de-pagamento-simulacao/novo/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacaocadastro/edita.xhtml"),
    @URLMapping(id = "editar-folha-simulacao", pattern = "/folha-de-pagamento-simulacao/editar/#{folhaDePagamentoSimulacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacaocadastro/edita.xhtml"),
    @URLMapping(id = "listar-folha-simulacao", pattern = "/folha-de-pagamento-simulacao/listar/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacaocadastro/lista.xhtml"),
    @URLMapping(id = "ver-folha-simulacao", pattern = "/folha-de-pagamento-simulacao/ver/#{folhaDePagamentoSimulacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/folhadepagamentosimulacaocadastro/visualizar.xhtml")
})
public class FolhaDePagamentoSimulacaoControlador extends PrettyControlador<FolhaDePagamentoSimulacao> implements Serializable, CRUD {

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FolhaDePagamentoSimulacaoFacade folhaDePagamentoSimulacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/folha-de-pagamento-simulacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public FolhaDePagamentoSimulacaoFacade getFacede() {
        return folhaDePagamentoSimulacaoFacade;
    }

    public FolhaDePagamentoSimulacaoControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamentoSimulacao.class);
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

    @URLAction(mappingId = "nova-folha-simulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-folha-simulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-folha-simulacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void efetivarFolha() {
        FolhaDePagamentoSimulacao f = folhaDePagamentoSimulacaoFacade.recuperar(selecionado.getId());
        if (f == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento não encontrada!");
        } else if (f.folhaEfetivada()) {
            FacesUtil.addError("Atenção", "A Folha de Pagamento do tipo '" + f.getTipoFolhaDePagamento().getDescricao() + "' deste mês de competência já está efetivado! Entre em contato com o administrador do sistema para reabrir a competência.");
        } else {
            folhaDePagamentoSimulacaoFacade.efetivarFolhaDePagamento(f, sistemaFacade.getDataOperacao());
            FacesUtil.addOperacaoRealizada("Efetivada com sucesso");
            FacesUtil.redirecionamentoInterno("/folha-de-pagamento-simulacao/listar/");
        }

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
        List<FolhaDePagamentoSimulacao> folhaDePagamentos = folhaDePagamentoSimulacaoFacade.buscarFolhasPorMesAnoTipoAndVersao(selecionado.getMes(), selecionado.getAno(), selecionado.getTipoFolhaDePagamento());
        if (folhaDePagamentos != null && !folhaDePagamentos.isEmpty()) {
            for (FolhaDePagamentoSimulacao folhaDePagamento : folhaDePagamentos) {
                if (!folhaDePagamento.getId().equals(selecionado.getId())) {
                    val.adicionarMensagemDeOperacaoNaoPermitida("Já existe folha de pagamento com o parâmetros Mês: " + selecionado.getMes() + " Ano: " + selecionado.getAno() + " Tipo: " + selecionado.getTipoFolhaDePagamento().getDescricao());
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
            if (TipoFolhaDePagamento.isFolhaRescisao(selecionado.getCompetenciaFP().getTipoFolhaDePagamento()) || TipoFolhaDePagamento.isFolhaComplementar(selecionado.getCompetenciaFP().getTipoFolhaDePagamento())) {
                selecionado.setQtdeMesesRetroacao(0);
            } else {
                selecionado.setQtdeMesesRetroacao(1);
            }
        }

    }


    public void abrirFolhaEfetivada() {
        if (selecionado == null) {
            FacesUtil.addError("Atenção", "Folha de pagamento indisponível.");
            return;
        }
        selecionado = folhaDePagamentoSimulacaoFacade.recuperar(selecionado.getId());
        if (StatusCompetencia.EFETIVADA.equals(selecionado.getCompetenciaFP().getStatusCompetencia())) {
            FacesUtil.addOperacaoNaoRealizada("A Folha de Pagamento do tipo '" + selecionado.getTipoFolhaDePagamento().getDescricao() + "' já está com a competência efetivada. Reabra a competência para prosseguir.");
        }
        selecionado.setEfetivadaEm(null);
        folhaDePagamentoSimulacaoFacade.salvar(selecionado);
        FacesUtil.addOperacaoRealizada("Folha foi aberta com sucesso.");
        FacesUtil.redirecionamentoInterno("/folha-de-pagamento/listar/");

    }
}
