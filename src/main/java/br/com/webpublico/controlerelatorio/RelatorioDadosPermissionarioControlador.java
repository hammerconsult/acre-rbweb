package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.PermissaoTransporte;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by israeleriston on 26/08/15.
 */
@ManagedBean(name = "relatorioDadosPermissionario")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio", pattern = "/permissao-de-transporte/ver/#{permissaoTransportePrettyControlador.id}/emitir-dados/", viewId = "")
})
public class RelatorioDadosPermissionarioControlador extends AbstractReport implements Serializable {


    public static final String ARQUIVO_JASPER = "RelatorioDadosPermissionario.jasper";

    public void imprimirRelatorio(PermissaoTransporte selecionado) throws IOException, JRException {
        HashMap parameter = new HashMap();
        parameter.put("BRASAO", super.getCaminhoImagem());
        parameter.put("USUARIO", getSistemaControlador().getUsuarioCorrente().getNome());
        parameter.put("IDPERMISSAO", selecionado.getId());
        String rg = selecionado.getPermissionarioVigente().getCadastroEconomico().getPessoa().getAsPessoaFisica().getRg() != null ? selecionado.getPermissionarioVigente().getCadastroEconomico().getPessoa().getAsPessoaFisica().getRg().getNumero() : "";
        parameter.put("RG", rg);
        try {
            gerarRelatorio(ARQUIVO_JASPER, parameter);
        } catch (JRException e) {
            FacesUtil.addErroAoGerarRelatorio(" " + e.getMessage());
        }

    }


    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }
}
