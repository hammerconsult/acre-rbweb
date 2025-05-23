package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.ProcessoAdministrativoJudicialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mateus on 18/06/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-processo-administrativo-judicial", pattern = "/rh/e-social/processo-administrativo-judicial/novo/", viewId = "/faces/rh/esocial/processo-administrativo-judicial/edita.xhtml"),
    @URLMapping(id = "editar-processo-administrativo-judicial", pattern = "/rh/e-social/processo-administrativo-judicial/editar/#{processoAdministrativoJudicialControlador.id}/", viewId = "/faces/rh/esocial/processo-administrativo-judicial/edita.xhtml"),
    @URLMapping(id = "ver-processo-administrativo-judicial", pattern = "/rh/e-social/processo-administrativo-judicial/ver/#{processoAdministrativoJudicialControlador.id}/", viewId = "/faces/rh/esocial/processo-administrativo-judicial/visualizar.xhtml"),
    @URLMapping(id = "listar-processo-administrativo-judicial", pattern = "/rh/e-social/processo-administrativo-judicial/listar/", viewId = "/faces/rh/esocial/processo-administrativo-judicial/lista.xhtml")
})
public class ProcessoAdministrativoJudicialControlador extends PrettyControlador<ProcessoAdministrativoJudicial> implements Serializable, CRUD {

    @EJB
    private ProcessoAdministrativoJudicialFacade facade;

    public ProcessoAdministrativoJudicialControlador() {
        super(ProcessoAdministrativoJudicial.class);
    }

    @URLAction(mappingId = "novo-processo-administrativo-judicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-processo-administrativo-judicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-processo-administrativo-judicial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/e-social/processo-administrativo-judicial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> buscarTiposDeProcesso() {
        return Util.getListSelectItem(TipoProcessoAdministrativoJudicial.values(), false);
    }

    public List<SelectItem> buscarIndicativosDeAutoria() {
        return Util.getListSelectItem(IndicativoAutoria.values(), false);
    }

    public List<SelectItem> buscarTipoIntegracao() {
        return Util.getListSelectItem(TipoIntegracaoEsocial.values(), false);
    }


    public List<IndicativoMateriaProcesso> buscarIndicativosDeMateria() {
        return IndicativoMateriaProcesso.buscarIndicativosPeloTipoProcesso(selecionado.getTipoProcesso());
    }

    public List<IndicativoSuspensaoExigibilidade> buscarIndicativosDeSuspensao() {
        return IndicativoSuspensaoExigibilidade.buscarIndicativosPeloTipoProcesso(selecionado.getTipoProcesso());
    }

    public Boolean desabilitarDeposito() {
        return !IndicativoSuspensaoExigibilidade.LIMINAR_EM_MANDADO_DE_SEGURANCA.equals(selecionado.getIndicativoSuspensaoExigib());
    }

    public List<UF> completarUFs(String filtro) {
        return facade.getUfFacade().listaFiltrando(filtro.trim().toUpperCase(), "uf", "nome");
    }

    public List<Cidade> completarCidades(String filtro) {
        if (selecionado.getUfVara() != null) {
            return facade.getCidadeFacade().consultaCidade(selecionado.getUfVara().getUf(), filtro);
        }
        return Lists.newArrayList();
    }

    public Integer maxLengthNumeroProcesso() {
        if (selecionado.getTipoProcesso() == null) {
            return 0;
        }
        switch (selecionado.getTipoProcesso()) {
            case ADMINISTRATIVO:
                return 21;
            case JUDICIAL:
                return 20;
            case NUMERO_DE_BENEFICIO:
                return 10;
            case PROCESSO_FAP:
                return 16;
            default:
                return 0;
        }
    }

    public void atualizarDepositoMontanteIntegral() {
        switch (selecionado.getIndicativoSuspensaoExigib()) {
            case LIMINAR_EM_MANDADO_DE_SEGURANCA:
                break;
            case DEPOSITO_JUDICIAL_DO_MONTANTE_INTEGRAL:
            case DEPOSITO_ADMINISTRATIVO_DO_MONTANTE_INTEGRAL:
                selecionado.setDepositoMontanteIntegral(true);
                break;
            default:
                selecionado.setDepositoMontanteIntegral(false);
                break;
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataEncerramento() != null && selecionado.getDataEncerramento().before(selecionado.getDataAbertura())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data de Encerramento deve ser igual ou posterior à Data de Abertura.");
        }
        switch (selecionado.getTipoProcesso()) {
            case ADMINISTRATIVO:
                if (selecionado.getNumeroProcesso().length() != 17 && selecionado.getNumeroProcesso().length() != 21) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número do processo deve possuir 17 (dezessete) ou 21 (vinte e um) algarismos.");
                }
                break;
            case JUDICIAL:
                if (selecionado.getNumeroProcesso().length() != 20) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número do processo deve possuir 20 (vinte) algarismos.");
                }
                if (selecionado.getIndicativoAutoria() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Indicativo de Autoria deve ser informado.");
                }
                break;
            case NUMERO_DE_BENEFICIO:
                if (selecionado.getNumeroProcesso().length() != 10) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número do processo deve possuir 10 (dez) algarismos.");
                }
                if (!IndicativoMateriaProcesso.CONVERSAO_LICENCA_SAUDE_EM_ACIDENTE_DE_TRABALHO.equals(selecionado.getIndicativoMateriaProcesso())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Indicativo da materia do processo ou alvará judicial deve ser '6 - Conversão de licença saúde em Acidente de Trabalho ' " +
                        "quando o Tipo de Processo for '3 - Número de Benefício (NB) do INSS'");
                }
                break;
            case PROCESSO_FAP:
                if (selecionado.getNumeroProcesso().length() != 16) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número do processo deve possuir 16 (dezesseis) algarismos.");
                }
                break;
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<SelectItem> getConfiguracaoEmpregador() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial configuracao : facade.getConfiguracaoEmpregadorESocialFacade().lista()) {
            toReturn.add(new SelectItem(configuracao, configuracao.getEntidade().getPessoaJuridica().getCnpj() + " - " + configuracao.getEntidade().getNome()));
        }
        return toReturn;
    }
}
