package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigContaBancariaPessoaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configContaBancariaPessoaControlador")
@ViewScoped

@URLMappings(mappings = {
        @URLMapping(id = "nova-config-contabancaria-pessoa",    pattern = "/configuracao-contabancaria-pessoa/novo/",                                              viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contabancaria-pessoa/edita.xhtml"),
        @URLMapping(id = "editar-config-contabancaria-pessoa",  pattern = "/configuracao-contabancaria-pessoa/editar/#{configContaBancariaPessoaControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contabancaria-pessoa/edita.xhtml"),
        @URLMapping(id = "ver-config-contabancaria-pessoa",     pattern = "/configuracao-contabancaria-pessoa/ver/#{configContaBancariaPessoaControlador.id}/",    viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contabancaria-pessoa/visualizar.xhtml"),
        @URLMapping(id = "listar-config-contabancaria-pessoa",  pattern = "/configuracao-contabancaria-pessoa/listar/",                                            viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contabancaria-pessoa/lista.xhtml")
})

public class ConfigContaBancariaPessoaControlador extends PrettyControlador<ConfigContaBancariaPessoa> implements Serializable, CRUD {

    @EJB
    private ConfigContaBancariaPessoaFacade configContaBancariaPessoaFacade;
    private ConverterAutoComplete converterContaBancaria;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterClasse;

    public ConfigContaBancariaPessoaControlador() {
        super(ConfigContaBancariaPessoa.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configContaBancariaPessoaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-contabancaria-pessoa/";
    }

    @Override
    @URLAction(mappingId = "nova-config-contabancaria-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-config-contabancaria-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-config-contabancaria-pessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado)) {
                if (operacao.equals(Operacoes.NOVO)) {
                    configContaBancariaPessoaFacade.salvarNovo(selecionado);
                } else {
                    configContaBancariaPessoaFacade.salvar(selecionado);
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
                redireciona();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), ex.getMessage());

        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
        }
    }

    public ConverterAutoComplete getConverterContaBancaria() {
        if (converterContaBancaria == null) {
            converterContaBancaria = new ConverterAutoComplete(ContaCorrenteBancaria.class, configContaBancariaPessoaFacade.getContaCorrenteBancariaFacade());
        }
        return converterContaBancaria;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, configContaBancariaPessoaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterClasse() {
        if (converterClasse == null) {
            converterClasse = new ConverterAutoComplete(ClasseCredor.class, configContaBancariaPessoaFacade.getClasseCredorFacade());
        }
        return converterClasse;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return configContaBancariaPessoaFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        if (selecionado.getPessoa() != null) {
            return configContaBancariaPessoaFacade.getClasseCredorFacade().buscarClassesPorPessoa(parte.trim(), selecionado.getPessoa());
        } else {
            return new ArrayList<>();
        }
    }

    public List<ContaCorrenteBancaria> completaContaCorrenteBancaria(String parte) {
        if (selecionado.getPessoa() != null) {
            return configContaBancariaPessoaFacade.getContaCorrenteBancariaFacade().listaContasPorPessoaFiltrandoPorCodigo(selecionado.getPessoa(),parte.trim());
        } else {
            return new ArrayList<>();
        }
    }
}
