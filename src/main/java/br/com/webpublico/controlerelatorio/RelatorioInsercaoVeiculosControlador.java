package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mateus on 21/01/2015.
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-insercao-veiculo", pattern = "/relatorio/insercao-veiculo", viewId = "/faces/tributario/relatorio/relatorioinsercaoveiculo.xhtml")
})
@ManagedBean
public class RelatorioInsercaoVeiculosControlador extends AbstractReport implements Serializable {
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public RelatorioInsercaoVeiculosControlador() {
    }

    public List<SelectItem> getListaTipoPermissao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, null));
        for (TipoPermissaoRBTrans tp : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }


    @URLAction(mappingId = "relatorio-insercao-veiculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        exercicio = null;
        tipoPermissaoRBTrans = null;
    }

    public void gerarRelatorio() {
        HashMap parametros = new HashMap();
        String nomeRelatorio = "RelatorioInsercaoVeiculos.jasper";
        try {
            parametros.put("SECRETARIA", sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao());
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            parametros.put("ANO", sistemaControlador.getExercicioCorrente().getAno());
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("WHERE", tipoPermissaoRBTrans == null ? "" : " and pt.TIPOPERMISSAORBTRANS = '" + tipoPermissaoRBTrans.name() + "'");
            gerarRelatorio(nomeRelatorio, parametros);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {
        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
