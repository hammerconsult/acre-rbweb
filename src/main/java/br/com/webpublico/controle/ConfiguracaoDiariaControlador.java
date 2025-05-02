/*
 * Codigo gerado automaticamente em Wed Mar 14 18:54:58 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.ClasseDiaria;
import br.com.webpublico.entidades.ConfiguracaoDiaria;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoDiariaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoconfiguracaodiaria", pattern = "/tabela-de-diaria/novo/", viewId = "/faces/financeiro/concessaodediarias/tabeladediaria/edita.xhtml"),
    @URLMapping(id = "editarconfiguracaodiaria", pattern = "/tabela-de-diaria/editar/#{configuracaoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/tabeladediaria/edita.xhtml"),
    @URLMapping(id = "verconfiguracaodiaria", pattern = "/tabela-de-diaria/ver/#{configuracaoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/tabeladediaria/visualizar.xhtml"),
    @URLMapping(id = "listarconfiguracaodiaria", pattern = "/tabela-de-diaria/listar/", viewId = "/faces/financeiro/concessaodediarias/tabeladediaria/lista.xhtml")
})
public class ConfiguracaoDiariaControlador extends PrettyControlador<ConfiguracaoDiaria> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoDiariaFacade configuracaoDiariaFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private MoneyConverter moneyConverter;
    private DotConverter dotConverter;
    private ConverterAutoComplete converterAutoCompleteAtoLegal;
    private ClasseDiaria classeDiaria;

    public ConfiguracaoDiariaControlador() {
        super(ConfiguracaoDiaria.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @Override
    public AbstractFacade getFacede() {
        return configuracaoDiariaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tabela-de-diaria/";
    }

    @URLAction(mappingId = "novoconfiguracaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        classeDiaria = new ClasseDiaria();
        classeDiaria.setCodigo("1");
        selecionado.setInicioVigencia(sistemaControlador.getDataOperacao());
    }

    @URLAction(mappingId = "verconfiguracaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        classeDiaria = new ClasseDiaria();
    }

    @URLAction(mappingId = "editarconfiguracaodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        int tamanhoLista;
        classeDiaria = new ClasseDiaria();
        tamanhoLista = selecionado.getClasseDiarias().size();
        classeDiaria.setCodigo(tamanhoLista + 1 + "");
    }

    public boolean validarCampos() {
        boolean controle = true;
        controle = Util.validaCampos(selecionado);
        if (selecionado.getClasseDiarias().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida(" Favor adicionar pelo menos uma classe de diária");
            controle = false;
        }
        if (selecionado.getFimVigencia() != null) {
            if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida(" A data Fim de Vigência deve ser marior que a data de início de vigência.");
                controle = false;
            }
        }
        return controle;
    }

    @Override
    public void salvar() {
        if (validarCampos()) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    configuracaoDiariaFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
                } else {
                    configuracaoDiariaFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("Registro alterado com sucesso.");
                }
                redireciona();
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }


    public Boolean validarAdicionarClasse() {
        if (classeDiaria.getCargo().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio(" O campo Cargo/Função deve ser informado para adicionar.");
            return false;
        }
        return true;
    }

    public void adicionar() {
        if (validarAdicionarClasse()) {
            classeDiaria.setConfiguracaoDiaria(selecionado);
            selecionado.setClasseDiarias(Util.adicionarObjetoEmLista(selecionado.getClasseDiarias(), classeDiaria));
//            selecionado.getClasseDiarias().add(classeDiaria);
            classeDiaria = new ClasseDiaria();
            classeDiaria.setCodigo(selecionado.getClasseDiarias().size() + 1 + "");
        }
    }

    public void removerClasseDiaria(ActionEvent evento) {
        ClasseDiaria c = (ClasseDiaria) evento.getComponent().getAttributes().get("removeClasse");
        selecionado.getClasseDiarias().remove(c);
        classeDiaria.setCodigo(selecionado.getClasseDiarias().size() + 1 + "");
    }

    public void editarClasseDiaria(ActionEvent event) {
        ClasseDiaria c = (ClasseDiaria) event.getComponent().getAttributes().get("editarClasse");
        classeDiaria = c;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return configuracaoDiariaFacade.getAtoLegalFacade().listaFiltrando(parte, "nome");
    }

    public DotConverter getDotConverter() {
        if (dotConverter == null) {
            dotConverter = new DotConverter();
        }
        return dotConverter;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterAutoCompleteAtoLegal() {
        if (converterAutoCompleteAtoLegal == null) {
            converterAutoCompleteAtoLegal = new ConverterAutoComplete(AtoLegal.class, configuracaoDiariaFacade.getAtoLegalFacade());
        }
        return converterAutoCompleteAtoLegal;
    }

    public ClasseDiaria getClasseDiaria() {
        return classeDiaria;
    }

    public void setClasseDiaria(ClasseDiaria classeDiaria) {
        this.classeDiaria = classeDiaria;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
