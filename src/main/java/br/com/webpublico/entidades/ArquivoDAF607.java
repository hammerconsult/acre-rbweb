package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ArquivoBancarioTributario;
import br.com.webpublico.entidadesauxiliares.HeaderDAF607;
import br.com.webpublico.entidadesauxiliares.RegistroDAF607;
import br.com.webpublico.entidadesauxiliares.TraillerDAF607;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class ArquivoDAF607 extends ArquivoBancarioTributario {

    private Date dataCreditoConta;
    private HeaderDAF607 headerDAF607;
    private List<RegistroDAF607> registrosDAF607;
    private TraillerDAF607 traillerDAF607;

    @Override
    public int getQuantidadeRegistros() {
        return this.getRegistrosDAF607() != null ? this.getRegistrosDAF607().size() : 0;
    }

    @Override
    public Long getId() {
        return null;
    }

    public HeaderDAF607 getHeaderDAF607() {
        return headerDAF607;
    }

    public void setHeaderDAF607(HeaderDAF607 headerDAF607) {
        this.headerDAF607 = headerDAF607;
    }

    public List<RegistroDAF607> getRegistrosDAF607() {
        return registrosDAF607;
    }

    public void setRegistrosDAF607(List<RegistroDAF607> registrosDAF607) {
        this.registrosDAF607 = registrosDAF607;
    }

    public TraillerDAF607 getTraillerDAF607() {
        return traillerDAF607;
    }

    public void setTraillerDAF607(TraillerDAF607 traillerDAF607) {
        this.traillerDAF607 = traillerDAF607;
    }

    public void adicionarRegisto(RegistroDAF607 registroDAF607) {
        if (registrosDAF607 == null) {
            registrosDAF607 = Lists.newArrayList();
        }
        registrosDAF607.add(registroDAF607);
    }

    public Date getDataCreditoConta() {
        return dataCreditoConta;
    }

    public void setDataCreditoConta(Date dataCreditoConta) {
        this.dataCreditoConta = dataCreditoConta;
    }
}
