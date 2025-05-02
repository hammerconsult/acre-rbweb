package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.ProcessoDeCompra;
import br.com.webpublico.entidades.SolicitacaoMaterial;
import br.com.webpublico.enums.SubTipoObjetoCompra;

import java.io.Serializable;

public interface ObjetoLicitatorioContrato extends Serializable {

    public boolean isRegistroDePrecos();

    public void transferirObjetoLicitatorioParaContrato();

    public String getLocalDeEntrega();

    public ProcessoDeCompra getProcessoDeCompra();

    public SolicitacaoMaterial getSolicitacaoMaterial();

    public boolean isObras();

    public SubTipoObjetoCompra getSubTipoObjetoCompra();
}
