package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 27/10/14
 * Time: 08:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class SuperFacadeContabil<T> extends AbstractFacade<T> implements Serializable {

    public SuperFacadeContabil(Class<T> classe) {
        super(classe);
    }

    public abstract void contabilizarReprocessamento(EntidadeContabil entidadeContabil);

    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) { return Lists.newArrayList();}
}
