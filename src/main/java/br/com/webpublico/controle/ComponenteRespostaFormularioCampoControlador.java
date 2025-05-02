package br.com.webpublico.controle;

import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.comum.FormularioCampoOpcao;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
public class ComponenteRespostaFormularioCampoControlador {

    public ComponenteRespostaFormularioCampoControlador() {
    }

    public List<SelectItem> getSelectItems(FormularioCampo formularioCampo, Boolean opcaoNulo) {
        List<SelectItem> toReturn = Lists.newArrayList();
        if (opcaoNulo) {
            toReturn.add(new SelectItem(null, ""));
        }
        for (FormularioCampoOpcao formularioCampoOpcao : formularioCampo.getFormularioCampoOpcaoList()) {
            toReturn.add(new SelectItem(formularioCampoOpcao.getOpcao(), formularioCampoOpcao.getOpcao()));
        }
        return toReturn;
    }
}
