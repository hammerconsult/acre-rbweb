package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContratoCEASA;
import br.com.webpublico.entidades.ParametroRendas;
import br.com.webpublico.entidades.ProcessoCalculoCEASA;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.beust.jcommander.internal.Lists;

import java.util.List;

public class AssistenteContratoCeasa extends AssistenteBarraProgresso {
    private String motivo;
    private ParametroRendas parametroRendas;
    private List<ContratoCEASA> contratos;
    private List<ContratoCEASA> contratosRenovados;
    private List<ProcessoCalculoCEASA> processosDeCalculo;

    public AssistenteContratoCeasa() {
        super();
        this.contratos = Lists.newArrayList();
        this.contratosRenovados = Lists.newArrayList();
        this.processosDeCalculo = Lists.newArrayList();
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

    public List<ContratoCEASA> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoCEASA> contratos) {
        this.contratos = contratos;
    }

    public List<ContratoCEASA> getContratosRenovados() {
        return contratosRenovados;
    }

    public void setContratosRenovados(List<ContratoCEASA> contratosRenovados) {
        this.contratosRenovados = contratosRenovados;
    }

    public List<ProcessoCalculoCEASA> getProcessosDeCalculo() {
        return processosDeCalculo;
    }

    public void setProcessosDeCalculo(List<ProcessoCalculoCEASA> processosDeCalculo) {
        this.processosDeCalculo = processosDeCalculo;
    }
}
