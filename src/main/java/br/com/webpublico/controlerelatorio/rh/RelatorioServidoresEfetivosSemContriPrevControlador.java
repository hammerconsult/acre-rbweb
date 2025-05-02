package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidadesauxiliares.rh.RelatorioRH;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.relatorio.RelatorioServidoresEfetivosSemContriPrevFacade;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "rel-servidores-efetivos-sem-contri-prev",
        pattern = "/rel-servidores-efetivos-sem-contri-prev",
        viewId = "/faces/rh/relatorios/rel-servidores-efetivos-sem-contri-prev.xhtml")
})
public class RelatorioServidoresEfetivosSemContriPrevControlador extends RelatorioRH implements Serializable {

    @EJB
    private RelatorioServidoresEfetivosSemContriPrevFacade facade;


    public StreamedContent gerarArquivo() {
        try {
            List<String> titulos = Lists.newArrayList();
            titulos.add("Matrícula");
            titulos.add("Contrato");
            titulos.add("Nome");
            titulos.add("Cargo");
            titulos.add("Lotação Funcional");
            titulos.add("Descrição do Afastamento.");

            List<Object[]> objetos = Lists.newArrayList();
            List resultado = facade.buscarDados();
            objetos.addAll(resultado);
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcelXLSX("Servidores Efetivos sem Contribuição Previdenciária", "servidores-efetivos-sem-contribuição-previdenciária", titulos, objetos, null, true);
            return excel.fileDownload();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }


}
