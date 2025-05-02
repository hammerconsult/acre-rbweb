package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
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
        @URLMapping(id = "relatorio-transferencia-permissao", pattern = "/relatorio/transferencia-permissao", viewId = "/faces/tributario/relatorio/relatoriotransferencia.xhtml")
})
@ManagedBean
public class RelatorioTransferenciaPermissaoControlador extends AbstractReport implements Serializable {

    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissaoRBTrans;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public RelatorioTransferenciaPermissaoControlador() {
    }

    public List<SelectItem> getListaTipoPermissao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, null));
        for (TipoPermissaoRBTrans tp : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-transferencia-permissao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        tipoPermissaoRBTrans = null;
    }

    public void gerarRelatorio() {
        HashMap parametros = new HashMap();
        String nomeRelatorio = "RelatorioTransferenciaPermissao.jasper";
        try {
            parametros.put("SECRETARIA", sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao());
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            parametros.put("ANO", sistemaControlador.getExercicioCorrente().getAno());
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("WHERE", tipoPermissaoRBTrans == null ? "" : " and pt.TIPOPERMISSAORBTRANS = '" + tipoPermissaoRBTrans.name() + "'");
            gerarRelatorio(nomeRelatorio, parametros);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public TipoPermissaoRBTrans getTipoPermissaoRBTrans() {

        return tipoPermissaoRBTrans;
    }

    public void setTipoPermissaoRBTrans(TipoPermissaoRBTrans tipoPermissaoRBTrans) {
        this.tipoPermissaoRBTrans = tipoPermissaoRBTrans;
    }
}
