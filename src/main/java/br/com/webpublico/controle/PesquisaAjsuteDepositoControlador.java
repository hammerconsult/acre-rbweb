package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 29/08/2017.
 */
@ManagedBean
@ViewScoped
public class PesquisaAjsuteDepositoControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return " where obj.saldo > 0 "
            + "  and to_char(obj.dataAjuste, 'yyyy') = " + "'" + getSistemaControlador().getExercicioCorrente() + "'"
            + "  and obj.unidadeOrganizacional.id = " + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId()
            + "  and " + montaCondicao() + montaOrdenacao();

    }
}
