package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidadesauxiliares.ImpressaoAlvara;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.negocios.AlvaraFacade;
import br.com.webpublico.negocios.CalculoAlvaraFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
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
 * Created by fox_m on 27/04/2016.
 */

@ManagedBean(name = "relatorioAlvaraEmLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "reemissaoAlvaraEmLote", pattern = "/reemissao-alvara-em-lote/", viewId = "/faces/tributario/alvara/relatorios/relatorioreemissaoalvaraemlote.xhtml")
})
public class RelatorioAlvaraEmLoteControlador implements Serializable {

    private TipoAlvara tipoAlvara;
    private Integer exercicio;
    private String inscricaoInicial;
    private String inscricaoFinal;
    @EJB
    private AlvaraFacade alvaraFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    private List<ImpressaoAlvara> impressoes;
    private ImpressaoAlvara[] impressoesSelecionadas;

    public List<SelectItem> getTiposDeAlvara() {
        List<SelectItem> tipos = Lists.newArrayList();
        tipos.add(new SelectItem(null, "     "));
        tipos.add(new SelectItem(TipoAlvara.LOCALIZACAO, TipoAlvara.LOCALIZACAO.getDescricaoSimples()));
        tipos.add(new SelectItem(TipoAlvara.FUNCIONAMENTO, TipoAlvara.FUNCIONAMENTO.getDescricaoSimples()));
        tipos.add(new SelectItem(TipoAlvara.SANITARIO, TipoAlvara.SANITARIO.getDescricaoSimples()));
        return tipos;
    }

    @URLAction(mappingId = "reemissaoAlvaraEmLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        tipoAlvara = null;
        exercicio = null;
        inscricaoInicial = null;
        inscricaoFinal = null;
        impressoes = Lists.newArrayList();
    }

    public void buscarAlvaras() {
        if (validarCampos()) {
            impressoes = alvaraFacade.buscarAlvarasParaImpressaoEmLote(tipoAlvara, exercicio, inscricaoInicial, inscricaoFinal);
            if (impressoes.isEmpty()) {
                FacesUtil.addError("Não é possível imprimir!", "Nenhum alvará encontrado!");
            }
        }
    }

    public void imprimirTodos() {
        if (!impressoes.isEmpty()) {
            gerarRelatorio(impressoes);
        } else {
            FacesUtil.addError("Não é possível imprimir!", "Nenhum alvará encontrado!");
        }
    }

    public void imprimirSelecionados() {
        if (impressoesSelecionadas.length != 0) {
            gerarRelatorio(Lists.newArrayList(impressoesSelecionadas));
        } else {
            FacesUtil.addError("Não é possível imprimir!", "Nenhum alvará encontrado!");
        }
    }

    private void gerarRelatorio(List<ImpressaoAlvara> impressoes) {
        try {
            alvaraFacade.imprimirAlvara(impressoes, tipoAlvara);
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private Boolean validarCampos() {
        Boolean retorno = true;
        if (exercicio == null || exercicio.toString().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo exercício é obrigatório");
            retorno = false;
        }
        if (inscricaoInicial == null || inscricaoInicial.trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo CMC Inicial é obrigatório");
            retorno = false;
        }
        if (inscricaoFinal == null || inscricaoFinal.trim().equals("")) {
            FacesUtil.addCampoObrigatorio("O campo CMC Final é obrigatório");
            retorno = false;
        }
        if (tipoAlvara == null) {
            FacesUtil.addCampoObrigatorio("O campo Tipo de Alvará é obrigatório");
            retorno = false;
        }
        return retorno;
    }

    public RelatorioAlvaraEmLoteControlador() {
    }

    public TipoAlvara getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(TipoAlvara tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public List<ImpressaoAlvara> getImpressoes() {
        return impressoes;
    }

    public void setImpressoes(List<ImpressaoAlvara> impressoes) {
        this.impressoes = impressoes;
    }

    public ImpressaoAlvara[] getImpressoesSelecionadas() {
        return impressoesSelecionadas;
    }

    public void setImpressoesSelecionadas(ImpressaoAlvara[] impressoesSelecionadas) {
        this.impressoesSelecionadas = impressoesSelecionadas;
    }
}
