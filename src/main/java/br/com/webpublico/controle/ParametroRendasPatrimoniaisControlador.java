package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroRendasPatrimoniaisFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "parametroRendasPatrimoniaisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroRendas", pattern = "/parametro-rendas-patrimoniais/novo/", viewId = "/faces/tributario/rendaspatrimoniais/parametrosrendas/edita.xhtml"),
    @URLMapping(id = "editarParametroRendas", pattern = "/parametro-rendas-patrimoniais/editar/#{parametroRendasPatrimoniaisControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/parametrosrendas/edita.xhtml"),
    @URLMapping(id = "listarParametroRendas", pattern = "/parametro-rendas-patrimoniais/listar/", viewId = "/faces/tributario/rendaspatrimoniais/parametrosrendas/lista.xhtml"),
    @URLMapping(id = "verParametroRendas", pattern = "/parametro-rendas-patrimoniais/ver/#{parametroRendasPatrimoniaisControlador.id}/", viewId = "/faces/tributario/rendaspatrimoniais/parametrosrendas/visualizar.xhtml")
})
public class ParametroRendasPatrimoniaisControlador extends PrettyControlador<ParametroRendas> implements Serializable, CRUD {

    @EJB
    private ParametroRendasPatrimoniaisFacade parametroRendasPatrimoniaisFacade;
    private ConverterGenerico converterExercicio;
    private ConverterAutoComplete converterIndiceEconomico;
    private ConverterGenerico converterLotacaoVistoriadora;
    private ServicoRateioCeasa servicoRateioCeasa;
    private boolean bloqueiaLotacao = false;

    public ParametroRendasPatrimoniaisControlador() {
        super(ParametroRendas.class);
        servicoRateioCeasa = new ServicoRateioCeasa();
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, parametroRendasPatrimoniaisFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getConverterIndiceEconomico() {
        if (converterIndiceEconomico == null) {
            converterIndiceEconomico = new ConverterAutoComplete(IndiceEconomico.class, parametroRendasPatrimoniaisFacade.getIndiceEconomicoFacade());
        }
        return converterIndiceEconomico;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, " "));
        for (Exercicio ex : parametroRendasPatrimoniaisFacade.getExercicioFacade().listaExerciciosAtualFuturos()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public void adicionarServico() {
        if (podeAdicionarServico()) {
            bloqueiaLotacao = true;
            selecionado.getListaServicoRateioCeasa().add(servicoRateioCeasa);
            servicoRateioCeasa.setParametroCeasa(selecionado);
            servicoRateioCeasa = new ServicoRateioCeasa();
        }
    }

    public Boolean podeAdicionarServico() {
        boolean retorno = true;
        if (servicoRateioCeasa.getCodigo() == null) {
            FacesUtil.addWarn("Campo Obrigatório", "O campo código é obrigatório");
            retorno = false;
        }
        if (servicoRateioCeasa.getDescricao().trim().equals("")) {
            FacesUtil.addWarn("Campo Obrigatório", "O campo descrição é obrigatório");
            retorno = false;
        }
        if (servicoRateioCeasa.getValor() == null) {
            FacesUtil.addWarn("Campo Obrigatório", "O campo valor é obrigatório");
            retorno = false;
        }
        return retorno;
    }

    public void removerServico(ServicoRateioCeasa servico) {
        selecionado.getListaServicoRateioCeasa().remove(servico);
        if (selecionado.getListaServicoRateioCeasa().size() == 0) {
            bloqueiaLotacao = false;
        }
    }

    public ServicoRateioCeasa getServicoRateioCeasa() {
        return servicoRateioCeasa;
    }

    public void setServicoRateioCeasa(ServicoRateioCeasa servicoRateioCeasa) {
        this.servicoRateioCeasa = servicoRateioCeasa;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return parametroRendasPatrimoniaisFacade.getExercicioFacade().listaFiltrandoEspecial(parte);
    }

    public List<IndiceEconomico> completaIndiceEconomico(String parte) {
        return parametroRendasPatrimoniaisFacade.getIndiceEconomicoFacade().listaFiltrando(parte.toLowerCase().trim(), "descricao");
    }

    public boolean isBloqueiaLotacao() {
        return bloqueiaLotacao;
    }

    public void setBloqueiaLotacao(boolean bloqueiaLotacao) {
        this.bloqueiaLotacao = bloqueiaLotacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-rendas-patrimoniais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroRendasPatrimoniaisFacade;
    }

    @URLAction(mappingId = "novoParametroRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarParametroRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verParametroRendas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validaCampos();
            super.salvar();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        }
    }

    private void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício é obrigatório.");
        }
        if (selecionado.getLotacaoVistoriadora() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo lotação deve ser informada.");
        }
        if (selecionado.getDataInicioContrato() == null || selecionado.getDataFimContrato() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos de início e fim de contrato são obrigatórios.");
        } else if (selecionado.getDataInicioContrato().after(selecionado.getDataFimContrato())) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de início do contrato deve ser menor que a data de fim do contrato");
        } else if (selecionado.getExercicio() != null && LocalDate.fromDateFields(selecionado.getDataInicioContrato()).getYear() != selecionado.getExercicio().getAno()) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de início do contrato deve pertencer ao exercicio de " + selecionado.getExercicio().getAno());
        } else if (selecionado.getExercicio() != null && LocalDate.fromDateFields(selecionado.getDataFimContrato()).getYear() != selecionado.getExercicio().getAno()) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de fim do contrato deve pertencer ao exercicio de " + selecionado.getExercicio().getAno());
        }
        if (parametroRendasPatrimoniaisFacade.jaExiste(selecionado)) {
            ve.adicionarMensagemDeCampoObrigatorio("Já existe um parâmetro cadastrado para o Exercício e Lotação informados.");
        }
        if (selecionado.getIndiceEconomico() == null || selecionado.getIndiceEconomico().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Índice Econômico é obrigatório.");
        }
        if (selecionado.getDiaVencimentoParcelas() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dia de vencimento das parcelas é obrigatório.");
        } else if (selecionado.getDiaVencimentoParcelas().intValue() <= 0 || selecionado.getDiaVencimentoParcelas().intValue() > 30) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo dia de vencimento das parcelas é obrigatório.");
        }
        if (renderedizaDeAcordoComAlotacao()) {
            if (selecionado.getListaServicoRateioCeasa().size() == 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um Tipos de Serviços do Rateio.");
            }
        }
        if (renderedizaDeAcordoComAlotacao()) {
            if (selecionado.getAreaTotal() == null || selecionado.getAreaTotal().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo área total (m2) é obrigatório e deve ser maior que zero.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getLotacaoVistoriadoras() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));

        ConfiguracaoTributario configuracao = parametroRendasPatrimoniaisFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        for (LotacaoVistoriadoraConfigTribRendas rendasLotacaoVistoriadora : configuracao.getRendasLotacoesVistoriadoras()) {
            toReturn.add(new SelectItem(rendasLotacaoVistoriadora.getLotacaoVistoriadora(), rendasLotacaoVistoriadora.getLotacaoVistoriadora().toString()));
        }

        if (configuracao.getCeasaLotacaoVistoriadora() != null) {
            toReturn.add(new SelectItem(configuracao.getCeasaLotacaoVistoriadora(), configuracao.getCeasaLotacaoVistoriadora().toString()));
        }

        return toReturn;
    }

    public Boolean renderedizaDeAcordoComAlotacao() {
        ConfiguracaoTributario configuracao = parametroRendasPatrimoniaisFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (selecionado.getLotacaoVistoriadora() != null && selecionado.getLotacaoVistoriadora().getId().equals(configuracao.getCeasaLotacaoVistoriadora().getId())) {
            return true;
        }
        selecionado.setAreaTotal(null);
        return false;
    }


    public String totalServicoFormatado() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado.getListaServicoRateioCeasa() != null && !selecionado.getListaServicoRateioCeasa().isEmpty()) {
            for (ServicoRateioCeasa servico : selecionado.getListaServicoRateioCeasa()) {
                total = total.add(servico.getValor());
            }
        }
        return Util.formataValor(total);
    }


    public ConverterGenerico getConverterLotacaoVistoriadora() {
        if (converterLotacaoVistoriadora == null) {
            converterLotacaoVistoriadora = new ConverterGenerico(LotacaoVistoriadora.class, parametroRendasPatrimoniaisFacade.getLotacaoVistoriadoraFacade());
        }
        return converterLotacaoVistoriadora;
    }
}
