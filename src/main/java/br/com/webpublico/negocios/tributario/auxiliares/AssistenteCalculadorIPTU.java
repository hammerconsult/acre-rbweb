package br.com.webpublico.negocios.tributario.auxiliares;

import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

public class AssistenteCalculadorIPTU extends AssistenteBarraProgresso {
    private final JdbcCalculoIptuDAO calculoDAO;
    private ProcessoCalculoIPTU processoCalculoIPTU;
    private boolean persisteCalculo;

    public AssistenteCalculadorIPTU(ProcessoCalculoIPTU processoCalculoIPTU, Integer total) {
        setDescricaoProcesso("Cálculo de IPTU ");
        setUsuarioSistema(processoCalculoIPTU.getUsuarioSistema());
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        calculoDAO = (JdbcCalculoIptuDAO) ap.getBean("jdbcCalculoIptuDAO");
        this.processoCalculoIPTU = processoCalculoIPTU;
        super.setTotal(total);
        super.setCalculados(0);
        persisteCalculo = true;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public boolean isPersisteCalculo() {
        return persisteCalculo;
    }

    public void setPersisteCalculo(boolean persisteCalculo) {
        this.persisteCalculo = persisteCalculo;
    }
}
