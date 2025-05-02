package br.com.webpublico.singletons;

import br.com.webpublico.entidadesauxiliares.RelatorioRelacaoLancamentoTributario;
import com.google.common.collect.Lists;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wellington on 17/11/2015.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonRelatorioRelacaoLancamentoTributario implements Serializable {

    private List<RelatorioRelacaoLancamentoTributario> relatoriosEmMemoria;


    public void adicionarRelatorioEmMemoriaSemBytes(RelatorioRelacaoLancamentoTributario relatorioRelacaoLancamentoTributario) {
        if (relatoriosEmMemoria == null) {
            relatoriosEmMemoria = Lists.newArrayList();
        }
        relatoriosEmMemoria.add(relatorioRelacaoLancamentoTributario);
    }

    public void preencherBytesDoRelatorioEmMemoria(Long criadoEm, ByteArrayOutputStream bytes) {
        RelatorioRelacaoLancamentoTributario relatorioRelacaoLancamentoTributario = buscarRelatorioPorFiltro(criadoEm);
        relatorioRelacaoLancamentoTributario.setFinalizadoEm(new Date());
        relatorioRelacaoLancamentoTributario.setDados(bytes);
    }

    public List<RelatorioRelacaoLancamentoTributario> buscarRelatoriosEmMemoriaDoLancamento(RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario relacaoLancamentoTributario) {
        List<RelatorioRelacaoLancamentoTributario> relatorios = Lists.newArrayList();
        if (relatoriosEmMemoria != null) {
            for (RelatorioRelacaoLancamentoTributario relatorioRelacaoLancamentoTributario : relatoriosEmMemoria) {
                if (relatorioRelacaoLancamentoTributario.getRelacaoLancamentoTributario().equals(relacaoLancamentoTributario)) {
                    relatorios.add(relatorioRelacaoLancamentoTributario);
                }
            }
        }
        return relatorios;
    }

    public RelatorioRelacaoLancamentoTributario buscarRelatorioPorFiltro(Long criadoEm) {
        if (relatoriosEmMemoria != null) {
            for (RelatorioRelacaoLancamentoTributario relatorioRelacaoLancamentoTributario : relatoriosEmMemoria) {
                if (criadoEm.equals(relatorioRelacaoLancamentoTributario.getCriadoEm())) {
                    return relatorioRelacaoLancamentoTributario;
                }
            }
        }
        return null;
    }

    public void limparRelatoriosEmMemoriaDoTipoDeCalculo(RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario relatorioRelacaoLancamentoTributario) {
        if (relatoriosEmMemoria != null) {
            Iterator<RelatorioRelacaoLancamentoTributario> iterator = relatoriosEmMemoria.iterator();
            while (iterator.hasNext()) {
                RelatorioRelacaoLancamentoTributario relatorioDaVez = iterator.next();
                if (relatorioDaVez.getRelacaoLancamentoTributario().equals(relatorioRelacaoLancamentoTributario)) {
                    iterator.remove();
                }
            }
        }
    }
}
