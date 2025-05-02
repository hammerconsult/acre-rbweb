package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.AcidenteTrabalho;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by israeleriston on 17/11/15.
 */
@ManagedBean(name = "relatorioAcidenteTrabalhoControlador")
@ViewScoped
public class RelatorioAcidenteTrabalhoControlador extends AbstractReport implements Serializable {

    private static final String ARQUIVO_JASPER = "RelatorioAcidenteTrabalho.jasper";


    public void emitir(AcidenteTrabalho acidenteTrabalho) throws JRException, SQLException {
        setGeraNoDialog(Boolean.TRUE);
        HashMap parameters = new HashMap();
        parameters.put("ID", acidenteTrabalho.getId());
        parameters.put("SECRETARIA", getSistemaControlador().getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente().toString().toUpperCase());
        parameters.put("USUARIO", getSistemaControlador().getSistemaFacade().getUsuarioCorrente().getNome());
        parameters.put("BRASAO", getCaminhoImagem());
        parameters.put("MODULO", "RECURSOS HUMANOS");
        try {
            gerarRelatorio(ARQUIVO_JASPER, parameters);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio("");
        }

    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }
}
