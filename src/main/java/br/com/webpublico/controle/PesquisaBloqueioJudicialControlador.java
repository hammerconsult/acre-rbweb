package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioJudicial;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;

@ManagedBean
@ViewScoped
public class PesquisaBloqueioJudicialControlador extends ComponentePesquisaGenerico {
    @Override
    public void getCampos() {
        super.getCampos();
    }

    @Override
    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        if (atributo.getClasse().equals(BigDecimal.class.getName())) {
            return Util.formataValorSemCifrao(((BloqueioJudicial) objeto).getValorBloqueio());
        } else if (atributo.getField().getAnnotation(Tabelavel.class).protocolo()) {
            return ((BloqueioJudicial) objeto).getNumeroProtocolo() != null ? ((BloqueioJudicial) objeto).getNumeroProtocolo() + "/" + ((BloqueioJudicial) objeto).getAnoProtocolo() : null;
        }
        return super.preencherCampo(objeto, atributo);
    }
}
