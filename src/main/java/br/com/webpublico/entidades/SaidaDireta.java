package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/04/14
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Saída Direta de Materiais")
public class SaidaDireta extends SaidaMaterial {

    @Pesquisavel
    @Etiqueta("CRM")
    private String crm;

    @Pesquisavel
    @Etiqueta("CNS")
    private String cns;

    @Etiqueta("Observação")
    private String observacao;

    public String getCrm() {
        return crm;
    }

    public void setCrm(String crm) {
        this.crm = crm;
    }

    public String getCns() {
        return cns;
    }

    public void setCns(String cns) {
        this.cns = cns;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public TipoSaidaMaterial getTipoDestaSaida() {
        return TipoSaidaMaterial.DIRETA;
    }

    public Boolean temMaterialMedicoHospitalar() {
        for (ItemSaidaMaterial ism : getListaDeItemSaidaMaterial()) {
            if (ism.getMaterial().getMedicoHospitalar()) {
                return true;
            }
        }

        return false;
    }

    /*Validação obtida em http://www.yanaga.com.br/2012/06/validacao-do-cns-cartao-nacional-de.html*/
    public boolean cnsEhValida() {
        /*Para a validação são considerados somente os 15 primeiros números. O 16º número indica a via do cartão e pode ser desconsiderado.*/
        String cnsParaValidar = cns.length() == 16 ? cns.substring(0, 15) : cns;

        if (cnsParaValidar.matches("[1-2]\\d{10}00[0-1]\\d") || cnsParaValidar.matches("[7-9]\\d{14}")) {
            return somaPonderada(cnsParaValidar) % 11 == 0;
        }

        return false;
    }

    private int somaPonderada(String s) {
        char[] cs = s.toCharArray();
        int soma = 0;

        for (int i = 0; i < cs.length; i++) {
            soma += Character.digit(cs[i], 10) * (15 - i);
        }

        return soma;
    }

    public boolean crmEhValido() {
        return true;
    }
}
