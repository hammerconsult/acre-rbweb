package br.com.webpublico.controle.contabil.reinf;

/**
 * Created by Romanini on 07/07/2022.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "configuracaoReinfControlador")
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "configuracao-reinf", pattern = "/contabil/reinf/configuracao/", viewId = "/faces/financeiro/reinf/configuracao/edita.xhtml"),})
public class ConfiguracaoReinfControlador {
    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoReinfControlador.class);
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil selecionado;
    private ConfiguracaoContabilContaReinf configuracaoContabilContaReinf;
    private ConfiguracaoContabilTipoContaDespesaReinf configTipoContaDespesaReinf;
    private List<VoConfiguracaoContabilContaReinf> voContasReinf;

    @URLAction(mappingId = "configuracao-reinf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConfiguracao() {
        selecionado = configuracaoContabilFacade.configuracaoContabilVigente();
        selecionado = configuracaoContabilFacade.recuperar(selecionado.getId());
        montarVoContasReinf();
    }

    public ConfiguracaoContabil getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoContabil selecionado) {
        this.selecionado = selecionado;
    }

    public ConfiguracaoContabilContaReinf getConfiguracaoContabilContaReinf() {
        return configuracaoContabilContaReinf;
    }

    public void setConfiguracaoContabilContaReinf(ConfiguracaoContabilContaReinf configuracaoContabilContaReinf) {
        this.configuracaoContabilContaReinf = configuracaoContabilContaReinf;
    }

    public ConfiguracaoContabilTipoContaDespesaReinf getConfigTipoContaDespesaReinf() {
        return configTipoContaDespesaReinf;
    }

    public void setConfigTipoContaDespesaReinf(ConfiguracaoContabilTipoContaDespesaReinf configTipoContaDespesaReinf) {
        this.configTipoContaDespesaReinf = configTipoContaDespesaReinf;
    }

    public void salvar() {
        try {
            configuracaoContabilFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Configuração salvo com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e){
            FacesUtil.addMessageError("Erro ao salvar", "Detalhe do erro: " + e.getMessage());
        }
    }

    public void cancelarContaReinf() {
        configuracaoContabilContaReinf = null;
    }

    public void instanciarContaReinf() {
        configuracaoContabilContaReinf = new ConfiguracaoContabilContaReinf();
    }

    public void cancelarConfigTipoContaDespesa() {
        configTipoContaDespesaReinf = null;
    }

    public void instanciarConfigTipoContaDespesa() {
        configTipoContaDespesaReinf = new ConfiguracaoContabilTipoContaDespesaReinf();
    }

    public void removerConfigTipoContaDespesa(ConfiguracaoContabilTipoContaDespesaReinf config) {
        selecionado.getTiposContasDespesasReinf().remove(config);
    }

    public List<SelectItem> getTiposDeContaDeDespesa() {
        return Util.getListSelectItem(TipoContaDespesa.values());
    }

    public void adicionarConfigTipoContaDespesa() {
        try {
            validarTipoContaDespesa();
            configTipoContaDespesaReinf.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getTiposContasDespesasReinf(), configTipoContaDespesaReinf);
            cancelarConfigTipoContaDespesa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarTipoContaDespesa() {
        ValidacaoException ve = new ValidacaoException();
        if (configTipoContaDespesaReinf.getTipoContaDespesa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Conta de Despesa deve ser informado.");
        }
        ve.lancarException();
    }

    public List<ContaExtraorcamentaria> completarContaReinf(String filtro) {
        return configuracaoContabilFacade.getContaFacade().listaFiltrandoExtraorcamentario(filtro.trim(), configuracaoContabilFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void editarContaReinf(ConfiguracaoContabilContaReinf config) {
        this.configuracaoContabilContaReinf = (ConfiguracaoContabilContaReinf) Util.clonarObjeto(config);
    }

    public void removerContaReinf(ConfiguracaoContabilContaReinf config) {
        selecionado.getContasReinf().remove(config);
        VoConfiguracaoContabilContaReinf voVazio = null;
        for (VoConfiguracaoContabilContaReinf vo : voContasReinf) {
            if (vo.getTipo().equals(config.getTipoArquivoReinf())) {
                vo.getContas().remove(config);
                if (vo.getContas().isEmpty()) {
                    voVazio = vo;
                }
            }
        }
        voContasReinf.remove(voVazio);
    }

    public void adicionarContaReinf() {
        try {
            validarContaReinf();
            configuracaoContabilContaReinf.setConfiguracaoContabil(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getContasReinf(), configuracaoContabilContaReinf);
            adicionarContaReinfNoVo();
            cancelarContaReinf();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarContaReinfNoVo() {
        boolean novoTipoArquivoReinfNoVo = true;
        for (VoConfiguracaoContabilContaReinf vo : voContasReinf) {
            if (vo.getTipo().equals(configuracaoContabilContaReinf.getTipoArquivoReinf())) {
                Util.adicionarObjetoEmLista(vo.getContas(), configuracaoContabilContaReinf);
                novoTipoArquivoReinfNoVo = false;
            }
        }
        if (novoTipoArquivoReinfNoVo) {
            voContasReinf.add(new VoConfiguracaoContabilContaReinf(configuracaoContabilContaReinf.getTipoArquivoReinf(), Lists.newArrayList(configuracaoContabilContaReinf)));
        }
    }

    private void validarContaReinf() {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoContabilContaReinf.getTipoArquivoReinf() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Arquivo deve ser informado.");
        }
        if (configuracaoContabilContaReinf.getContaExtra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Conta Extra deve ser informado.");
        }
        ve.lancarException();
        for (ConfiguracaoContabilContaReinf config : selecionado.getContasReinf()) {
            if (!config.equals(configuracaoContabilContaReinf) &&
                config.getContaExtra().getCodigo().equals(configuracaoContabilContaReinf.getContaExtra().getCodigo()) &&
                config.getTipoArquivoReinf().equals(configuracaoContabilContaReinf.getTipoArquivoReinf())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Extra informada já foi adicionada para o Tipo de Arquivo " + configuracaoContabilContaReinf.getTipoArquivoReinf().getCodigo() + ".");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposArquivosReinf() {
        return Util.getListSelectItem(TipoArquivoReinf.values());
    }

    public void montarVoContasReinf() {
        voContasReinf = Lists.newArrayList();
        HashMap<TipoArquivoReinf, List<ConfiguracaoContabilContaReinf>> mapContasReinf = new HashMap<>();
        if (selecionado.getContasReinf() != null && !selecionado.getContasReinf().isEmpty()) {
            for (ConfiguracaoContabilContaReinf contabilContaReinf : selecionado.getContasReinf()) {
                if (!mapContasReinf.containsKey(contabilContaReinf.getTipoArquivoReinf())) {
                    mapContasReinf.put(contabilContaReinf.getTipoArquivoReinf(), Lists.newArrayList());
                }
                mapContasReinf.get(contabilContaReinf.getTipoArquivoReinf()).add(contabilContaReinf);
            }

            for (Map.Entry<TipoArquivoReinf, List<ConfiguracaoContabilContaReinf>> mapConta : mapContasReinf.entrySet()) {
                voContasReinf.add(new VoConfiguracaoContabilContaReinf(mapConta.getKey(), mapConta.getValue()));
            }
        }
    }

    public List<VoConfiguracaoContabilContaReinf> getVoContasReinf() {
        return voContasReinf;
    }

    public void setVoContasReinf(List<VoConfiguracaoContabilContaReinf> voContasReinf) {
        this.voContasReinf = voContasReinf;
    }

    public class VoConfiguracaoContabilContaReinf {
        private TipoArquivoReinf tipo;
        private List<ConfiguracaoContabilContaReinf> contas;

        public VoConfiguracaoContabilContaReinf(TipoArquivoReinf tipo, List<ConfiguracaoContabilContaReinf> contas) {
            this.tipo = tipo;
            this.contas = contas;
        }

        public TipoArquivoReinf getTipo() {
            return tipo;
        }

        public void setTipo(TipoArquivoReinf tipo) {
            this.tipo = tipo;
        }

        public List<ConfiguracaoContabilContaReinf> getContas() {
            return contas;
        }

        public void setContas(List<ConfiguracaoContabilContaReinf> contas) {
            this.contas = contas;
        }
    }

    public List<Pessoa> completarPessoa(String parte) {
        return configuracaoContabilFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ClasseCredor> completarClasseCredorInss(String parte) {
        return configuracaoContabilFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, selecionado.getPessoaInssPadraoDocLiq());
    }

    public List<ClasseCredor> completarClasseCredorIrrf(String parte) {
        return configuracaoContabilFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte, selecionado.getPessoaIrrfPadraoDocLiq());
    }
}

