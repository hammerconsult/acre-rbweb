package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.ProcessoDebito;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ServiceProcessoDebitos {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProcessoDebitos.class);
    @PersistenceContext
    protected transient EntityManager em;
    @Autowired
    private ServiceItemProcessoDebito serviceItemProcessoDebito;

    public void cancelarProcessosVencidos() {
        try {
            List<ProcessoDebito> processos = serviceItemProcessoDebito.buscarTodosProcessosFinalizadosVencidos();

            int quantidade = processos.size() > 5 ? processos.size() / 5 : 1;

            List<List<ProcessoDebito>> partition = Lists.partition(processos, quantidade);

            for (List<ProcessoDebito> processo : partition) {
                serviceItemProcessoDebito.estornarProcessos(processo);
            }

            List<ParcelaValorDivida> parcelas = serviceItemProcessoDebito.buscarParcelasEmAbertoParaProcessosFinalizadosNaoVencidos();

            quantidade = parcelas.size() > 5 ? parcelas.size() / 5 : 1;

            List<List<ParcelaValorDivida>> partition2 = Lists.partition(parcelas, quantidade);

            for (List<ParcelaValorDivida> parcela : partition2) {
                serviceItemProcessoDebito.suspenderParcelas(parcela);
            }

        } catch (Exception ex) {
        }
    }

}
