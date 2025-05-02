package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContratoRendasPatrimoniais;
import br.com.webpublico.entidades.ParametroRendas;
import br.com.webpublico.entidades.ProcessoCalculoRendas;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.beust.jcommander.internal.Lists;

import java.util.List;

public class AssistenteRendasPatrimoniais extends AssistenteBarraProgresso {
    private String motivo;
    private ParametroRendas parametroRendas;
    private List<ContratoRendasPatrimoniais> contratoRendasPatrimoniais;
    private List<ContratoRendasPatrimoniais> renovados;
    private List<ProcessoCalculoRendas> processos;

    public AssistenteRendasPatrimoniais() {
        super();
        this.contratoRendasPatrimoniais = Lists.newArrayList();
        this.renovados = Lists.newArrayList();
        this.processos = Lists.newArrayList();
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ParametroRendas getParametroRendas() {
        return parametroRendas;
    }

    public void setParametroRendas(ParametroRendas parametroRendas) {
        this.parametroRendas = parametroRendas;
    }

    public List<ContratoRendasPatrimoniais> getContratoRendasPatrimoniais() {
        return contratoRendasPatrimoniais;
    }

    public void setContratoRendasPatrimoniais(List<ContratoRendasPatrimoniais> contratoRendasPatrimoniais) {
        this.contratoRendasPatrimoniais = contratoRendasPatrimoniais;
    }

    public List<ContratoRendasPatrimoniais> getRenovados() {
        return renovados;
    }

    public void setRenovados(List<ContratoRendasPatrimoniais> renovados) {
        this.renovados = renovados;
    }

    public List<ProcessoCalculoRendas> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcessoCalculoRendas> processos) {
        this.processos = processos;
    }
}
