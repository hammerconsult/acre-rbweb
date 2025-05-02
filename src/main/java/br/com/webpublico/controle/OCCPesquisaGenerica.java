package br.com.webpublico.controle;

import br.com.webpublico.entidades.OrigemContaContabil;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class OCCPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica itemPesquisaGenericaContaExtra = new ItemPesquisaGenerica();
        itemPesquisaGenericaContaExtra.setCondicao("obj.contaContabil.codigo");
        itemPesquisaGenericaContaExtra.setLabel("Código Conta Extra");
        itemPesquisaGenericaContaExtra.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericaContaExtra.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericaContaExtra);
        super.getItensOrdenacao().add(itemPesquisaGenericaContaExtra);

        ItemPesquisaGenerica itemPesquisaGenericacontaIntra = new ItemPesquisaGenerica();
        itemPesquisaGenericacontaIntra.setCondicao("obj.contaIntra.codigo");
        itemPesquisaGenericacontaIntra.setLabel("Código Conta Intra");
        itemPesquisaGenericacontaIntra.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericacontaIntra.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericacontaIntra);
        super.getItensOrdenacao().add(itemPesquisaGenericacontaIntra);

        ItemPesquisaGenerica itemPesquisaGenericacontaInterUniao = new ItemPesquisaGenerica();
        itemPesquisaGenericacontaInterUniao.setCondicao("obj.contaInterUniao.codigo");
        itemPesquisaGenericacontaInterUniao.setLabel("Código Conta Inter União");
        itemPesquisaGenericacontaInterUniao.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericacontaInterUniao.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericacontaInterUniao);
        super.getItensOrdenacao().add(itemPesquisaGenericacontaInterUniao);

        ItemPesquisaGenerica itemPesquisaGenericacontaInterEstado = new ItemPesquisaGenerica();
        itemPesquisaGenericacontaInterEstado.setCondicao("obj.contaInterEstado.codigo");
        itemPesquisaGenericacontaInterEstado.setLabel("Código Conta Inter Estado");
        itemPesquisaGenericacontaInterEstado.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericacontaInterEstado.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericacontaInterEstado);
        super.getItensOrdenacao().add(itemPesquisaGenericacontaInterEstado);

        ItemPesquisaGenerica itemPesquisaGenericacontaInterMunicipio = new ItemPesquisaGenerica();
        itemPesquisaGenericacontaInterMunicipio.setCondicao("obj.contaInterMunicipal.codigo");
        itemPesquisaGenericacontaInterMunicipio.setLabel("Código Conta Inter Município");
        itemPesquisaGenericacontaInterMunicipio.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericacontaInterMunicipio.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericacontaInterMunicipio);
        super.getItensOrdenacao().add(itemPesquisaGenericacontaInterMunicipio);

        ItemPesquisaGenerica itemPesquisaGenericaContaAuxiliarSiconfi = new ItemPesquisaGenerica();
        itemPesquisaGenericaContaAuxiliarSiconfi.setCondicao("obj.tipoContaAuxiliarSiconfi.codigo");
        itemPesquisaGenericaContaAuxiliarSiconfi.setLabel("Código Tipo Conta Auxiliar SICONFI");
        itemPesquisaGenericaContaAuxiliarSiconfi.setTipo(OrigemContaContabil.class);
        itemPesquisaGenericaContaAuxiliarSiconfi.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericaContaAuxiliarSiconfi);
        super.getItensOrdenacao().add(itemPesquisaGenericaContaAuxiliarSiconfi);
    }

    @Override
    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor, boolean isDate, boolean isStringExata) {
        if (nomeCampo.contains("obj.contaOrigem.codigo")) {
            String nomeDoCampo = "replace(obj.contaOrigem.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDoCampo + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }
        } else if (nomeCampo.contains("obj.contaContabil.codigo")) {
            String nomeDoCampoContaExtra = "replace(obj.contaContabil.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDoCampoContaExtra + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }
        } else if (nomeCampo.contains("obj.contaIntra.codigo")) {
            String nomeDaConta = "replace(obj.contaIntra.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDaConta + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }
        } else if (nomeCampo.contains("obj.contaInterUniao.codigo")) {
            String nomeDaConta = "replace(obj.contaInterUniao.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDaConta + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }

        } else if (nomeCampo.contains("obj.contaInterEstado.codigo")) {
            String nomeDaConta = "replace(obj.contaInterEstado.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDaConta + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }
        } else if (nomeCampo.contains("obj.contaInterMunicipal.codigo")) {
            String nomeDaConta = "replace(obj.contaInterMunicipal.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDaConta + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }
        } else {
            return super.processaIntervalo(nomeCampo, intervalo, isString, isValor, isDate, false);
        }
        return nomeCampo;
    }
}
