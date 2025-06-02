package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoMalaDireta;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "parametroMonitoramentoFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroMonitoramentoFiscal", pattern = "/parametro-monitoramento-fiscal/novo/",
                viewId = "/faces/tributario/monitoramentofiscal/parametro/edita.xhtml"),
    @URLMapping(id = "editarParametroMonitoramentoFiscal", pattern = "/parametro-monitoramento-fiscal/editar/#{parametroMonitoramentoFiscalControlador.id}/",
                viewId = "/faces/tributario/monitoramentofiscal/parametro/edita.xhtml"),
    @URLMapping(id = "listarParametroMonitoramentoFiscal", pattern = "/parametro-monitoramento-fiscal/listar/",
                viewId = "/faces/tributario/monitoramentofiscal/parametro/lista.xhtml"),
    @URLMapping(id = "verParametroMonitoramentoFiscal", pattern = "/parametro-monitoramento-fiscal/ver/#{parametroMonitoramentoFiscalControlador.id}/",
                viewId = "/faces/tributario/monitoramentofiscal/parametro/visualizar.xhtml")
})
public class ParametroMonitoramentoFiscalControlador extends PrettyControlador<ParametroMonitoramentoFiscal> implements Serializable, CRUD {

    @EJB
    private ParametroMonitoramentoFiscalFacade parametroMonitoramentoFiscalFacade;
    @EJB
    private SecretariaFiscalizacaoFacade secretariaFiscalizacaoFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ParametroMalaDiretaFacade parametroMalaDiretaFacade;
    private ConverterAutoComplete converterPessoa;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ParametroMonitoramentoFiscalControlador() {
        super(ParametroMonitoramentoFiscal.class);
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @URLAction(mappingId = "novoParametroMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        hierarquiaOrganizacional = null;
    }

    @URLAction(mappingId = "verParametroMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroMonitoramentoFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getSecretaria() != null) {
            hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                DataUtil.getPrimeiroDiaAno(selecionado.getExercicio().getAno()),
                selecionado.getSecretaria(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }
    }

    @Override
    public void salvar() {
        if (hierarquiaOrganizacional != null) {
            selecionado.setSecretaria(hierarquiaOrganizacional.getSubordinada());
        }
        super.salvar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-monitoramento-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroMonitoramentoFiscalFacade;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaOrganizacionalTipo(parte, TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), sistemaFacade.getDataOperacao());
    }

    public List<TipoDoctoOficial> completarTipoDocOficial(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModulo(parte, ModuloTipoDoctoOficial.MONITORAMENTO_FISCAL);
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFisicaFacade);
        }
        return converterPessoa;
    }

    public List<PessoaFisica> completarPessoasComContratosFP(String parte) {
        return contratoFPFacade.listaPessoasComContratos(parte.trim());
    }

    public List<ParametroMalaDireta> completarParametroPorTipo(String parte) {
        return parametroMalaDiretaFacade.completarParametroPorTipo(parte, TipoMalaDireta.NOTIFICACAO);
    }
}
