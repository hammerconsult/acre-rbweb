package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoPatrimonioLiquido;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PatrimonioLiquidoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mga on 18/10/2017.
 */

@ManagedBean(name = "patrimonioLiquidoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-patrimonio-liquido", pattern = "/patrimonio-liquido/novo/", viewId = "/faces/financeiro/orcamentario/patrimonioliquido/edita.xhtml"),
    @URLMapping(id = "editar-patrimonio-liquido", pattern = "/patrimonio-liquido/editar/#{patrimonioLiquidoControlador.id}/", viewId = "/faces/financeiro/orcamentario/patrimonioliquido/edita.xhtml"),
    @URLMapping(id = "ver-patrimonio-liquido", pattern = "/patrimonio-liquido/ver/#{patrimonioLiquidoControlador.id}/", viewId = "/faces/financeiro/orcamentario/patrimonioliquido/visualizar.xhtml"),
    @URLMapping(id = "listar-patrimonio-liquido", pattern = "/patrimonio-liquido/listar/", viewId = "/faces/financeiro/orcamentario/patrimonioliquido/lista.xhtml"),
})
public class PatrimonioLiquidoControlador extends PrettyControlador<PatrimonioLiquido> implements Serializable, CRUD {

    @EJB
    private PatrimonioLiquidoFacade facade;

    public PatrimonioLiquidoControlador() {
        super(PatrimonioLiquido.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/patrimonio-liquido/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novo-patrimonio-liquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        parametrosIniciais();
    }

    @URLAction(mappingId = "editar-patrimonio-liquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-patrimonio-liquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    private void parametrosIniciais() {
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        if (facade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso!", facade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    public void definirEventoContabil() {
        try {
            selecionado.setEventoContabil(null);
            if (selecionado.getTipoLancamento() != null && selecionado.getOperacaoPatrimonioLiquido() != null) {
                EventoContabil eventoContabil = facade.buscarEventoContabil(selecionado);
                if (eventoContabil != null) {
                    selecionado.setEventoContabil(eventoContabil);
                }
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<TipoLancamento> getListaTipoLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        lista.addAll(Arrays.asList(TipoLancamento.values()));
        return lista;
    }

    public List<SelectItem> getOperacoes() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem("", null));
        for (OperacaoPatrimonioLiquido tipo : OperacaoPatrimonioLiquido.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }


    public List<Pessoa> completarPessoa(String parte) {
        return facade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
    }

    public List<SelectItem> getClassesCredor() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        list.add(new SelectItem(null, ""));
        if (selecionado.getPessoa() != null) {
            List<ClasseCredor> listClasse = facade.getClasseCredorFacade().buscarClassesPorPessoa("", selecionado.getPessoa());
            for (ClasseCredor classe : listClasse) {
                list.add(new SelectItem(classe, classe.toString()));
            }
        }
        return list;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                selecionado = facade.salvarPatrimonio(selecionado);
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                facade.salvar(selecionado);
                redireciona();
            }
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getValor() != null && selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero.");
        }
        ve.lancarException();
    }


    public Boolean isRegistroEditavel() {
        return selecionado != null && selecionado.getId() != null;
    }

    public void selecionarPessoa(ActionEvent evento) {
        Pessoa pessoa = (Pessoa) evento.getComponent().getAttributes().get("objeto");
        if (pessoa != null) {
            selecionado.setPessoa(pessoa);
        }
    }

    public ParametroEvento getParametroEvento() {
        try {
            ParametroEvento parametroEvento = facade.criarParametroEvento(selecionado);
            if (parametroEvento != null) {
                return parametroEvento;
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
        throw new ExcecaoNegocioGenerica("Parametro evento não encontrado para visualizar o evento contábil.");
    }

    public void atribuirNullParaClasseAndPessoa() {
        selecionado.setPessoa(null);
        selecionado.setClasse(null);
    }
}
