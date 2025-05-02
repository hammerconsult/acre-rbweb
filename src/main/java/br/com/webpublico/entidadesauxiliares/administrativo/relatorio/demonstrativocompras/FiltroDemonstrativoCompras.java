package br.com.webpublico.entidadesauxiliares.administrativo.relatorio.demonstrativocompras;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ProcessoDeCompra;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wellington on 07/04/2016.
 */
public class FiltroDemonstrativoCompras implements Serializable {

    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private List<ProcessoDeCompra> processosDeCompra;
    private List<ProcessoDeCompra> processoDeComprasSelecionados;

    public FiltroDemonstrativoCompras() {
        processosDeCompra = Lists.newArrayList();
        processoDeComprasSelecionados = Lists.newArrayList();
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public List<ProcessoDeCompra> getProcessosDeCompra() {
        return processosDeCompra;
    }

    public void setProcessosDeCompra(List<ProcessoDeCompra> processosDeCompra) {
        this.processosDeCompra = processosDeCompra;
    }

    public List<ProcessoDeCompra> getProcessoDeComprasSelecionados() {
        return processoDeComprasSelecionados;
    }

    public void setProcessoDeComprasSelecionados(List<ProcessoDeCompra> processoDeComprasSelecionados) {
        this.processoDeComprasSelecionados = processoDeComprasSelecionados;
    }

    public List<String> getIdsProcessosDeCompraSelecionados() {
        List<String> toReturn = Lists.newArrayList();
        if (processoDeComprasSelecionados != null && processoDeComprasSelecionados.size() > 0) {
            for (ProcessoDeCompra processoDeCompra : processoDeComprasSelecionados) {
                toReturn.add(processoDeCompra.getId().toString());
            }
        }
        return toReturn;
    }
}
