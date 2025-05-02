package br.com.webpublico.negocios.contabil.singleton;

import br.com.webpublico.entidades.comum.FechamentoMensal;
import br.com.webpublico.entidades.comum.FechamentoMensalMes;
import br.com.webpublico.enums.SituacaoFechamentoMensal;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.comum.FechamentoMensalFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonFechamentoMensal implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SingletonFechamentoMensal.class);
    private HashMap<String, FechamentoMensalMes> fechamentosMensais = Maps.newHashMap();
    private FechamentoMensalFacade fechamentoMensalFacade;

    @PostConstruct
    private void init() {
        try {
            fechamentoMensalFacade = (FechamentoMensalFacade) new InitialContext().lookup("java:module/FechamentoMensalFacade");
            limparAndPopularFechamentos();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Lock(LockType.WRITE)
    public FechamentoMensalMes buscarFechamentoMensalNoMap(Integer mesInt, Integer anoInt) {
        if (fechamentosMensais == null) {
            fechamentosMensais = Maps.newHashMap();
        }
        for (Map.Entry<String, FechamentoMensalMes> entry : fechamentosMensais.entrySet()) {
            if (entry.getKey().equals(mesInt.toString() + anoInt.toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void limparAndPopularFechamentos() {
        fechamentosMensais = Maps.newHashMap();
        List<FechamentoMensal> fechamentos = fechamentoMensalFacade.buscarFechamentosMensais();
        if (!fechamentos.isEmpty()) {
            for (FechamentoMensal fechamento : fechamentos) {
                for (FechamentoMensalMes fechamentoMensalMes : fechamento.getMeses()) {
                    adicionarFechamentoMensal(fechamentoMensalMes);
                }
            }
        }
    }

    public void validarMesContabil(Date dataSaldo) {
        FechamentoMensalMes fechamentoMensalMes = buscarFechamentoMensalNoMap(DataUtil.getMes(dataSaldo), DataUtil.getAno(dataSaldo));
        if (fechamentoMensalMes != null && SituacaoFechamentoMensal.FECHADO.equals(fechamentoMensalMes.getSituacaoContabil())) {
            throw new ExcecaoNegocioGenerica("O mês <b> " + fechamentoMensalMes.getMes().getDescricao() + " </b> se encontra com situação <b> Fechado </b> para o Exercício <b> " + fechamentoMensalMes.getFechamentoMensal().getExercicio().getAno() + " </b>.");
        }
    }

    @Lock(LockType.WRITE)
    public void adicionarFechamentoMensal(FechamentoMensalMes fechamentoMensalMes) {
        if (fechamentosMensais == null) {
            fechamentosMensais = Maps.newHashMap();
        }
        if (buscarFechamentoMensalNoMap(fechamentoMensalMes.getMes().getNumeroMes(), fechamentoMensalMes.getFechamentoMensal().getExercicio().getAno()) == null) {
            fechamentosMensais.put(fechamentoMensalMes.getMes().getNumeroMes().toString() + fechamentoMensalMes.getFechamentoMensal().getExercicio().getAno().toString(), fechamentoMensalMes);
        }
    }
}
