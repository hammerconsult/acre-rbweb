package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.comum.FormularioCampoOpcao;
import br.com.webpublico.entidades.comum.RespostaFormularioCampo;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
public class ComponenteRespostaFormularioControlador {

    @EJB
    private ArquivoFacade arquivoFacade;

    public ComponenteRespostaFormularioControlador() {
    }

    public List<SelectItem> getSelectItems(RespostaFormularioCampo respostaFormularioCampo, Boolean opcaoNula) {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (opcaoNula) toReturn.add(new SelectItem(null, ""));
        if (respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList() != null) {
            for (FormularioCampoOpcao opcao : respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList()) {
                toReturn.add(new SelectItem(opcao.getOpcao(), opcao.getOpcao()));
            }
        }
        return toReturn;
    }

    public StreamedContent getStreamContent(Arquivo arquivo) {
        try {
            if (arquivo != null) {
                return arquivoFacade.montarArquivoParaDownloadPorArquivo(arquivo);
            }
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível baixar o anexo. Detalhes: " + e.getMessage());
        }
        return null;
    }

    public String layoutSelectManyCheckbok(RespostaFormularioCampo respostaFormularioCampo) {
        if (respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList() != null) {
            for (FormularioCampoOpcao opcao : respostaFormularioCampo.getFormularioCampo().getFormularioCampoOpcaoList()) {
                if (opcao.getOpcao().length() > 30) {
                    return "pageDirection";
                }
            }
        }
        return "responsive";
    }
}
