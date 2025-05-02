package br.com.webpublico.negocios.rh.cbo;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.ImportacaoArquivoCBO;
import br.com.webpublico.entidadesauxiliares.rh.cbo.ConteudoCBO;
import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImportacaoArquivoCBOExecutor implements Callable<AssistenteBarraProgresso> {

    private final Logger logger = LoggerFactory.getLogger(ImportacaoArquivoCBOExecutor.class);

    private CBOFacade cboFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private ImportacaoArquivoCBO importacaoArquivoCBO;
    private List<ConteudoCBO> cbosImportacao;
    private List<CBO> cbosBase;
    private Map<Long, CBO> mapCbo;

    public ImportacaoArquivoCBOExecutor(CBOFacade cboFacade) {
        this.cboFacade = cboFacade;
    }

    public Future<AssistenteBarraProgresso> execute(ImportacaoArquivoCBO importacaoArquivoCBO, List<ConteudoCBO> cbosImportacao, AssistenteBarraProgresso assistenteBarraProgresso) {
        this.importacaoArquivoCBO = importacaoArquivoCBO;
        this.cbosImportacao = cbosImportacao;
        this.assistenteBarraProgresso = assistenteBarraProgresso;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<AssistenteBarraProgresso> submit = executorService.submit(this);
        executorService.shutdown();
        return submit;
    }

    public AssistenteBarraProgresso call() throws Exception {
        try {
            if (TipoCBO.SINONIMO.equals(importacaoArquivoCBO.getTipoCBO())) {
                cboFacade.deletarCbosSinonimos();
            } else {
                cbosBase = cboFacade.buscarCbosPorTipo(importacaoArquivoCBO.getTipoCBO());
                mapCbo = Maps.newHashMap();
                cboFacade.updateInativarCbosPorTipo(importacaoArquivoCBO.getTipoCBO());
                for (CBO cbo : cbosBase) {
                    mapCbo.put(cbo.getCodigo(), cbo);
                }
            }
            for (ConteudoCBO conteudoCBO : cbosImportacao) {
                if (TipoCBO.OCUPACAO.equals(importacaoArquivoCBO.getTipoCBO())) {
                    CBO cbo = mapCbo.get(conteudoCBO.getCodigo());
                    if (cbo != null) {
                        cbo.setDescricao(conteudoCBO.getDescricao());
                        cbo.setTipoCBO(conteudoCBO.getTipoCBO());
                        cbo.setAtivo(Boolean.TRUE);
                        cboFacade.salvar(cbo);
                    } else {
                        cboFacade.salvar(new CBO(conteudoCBO.getDescricao(), conteudoCBO.getCodigo(), conteudoCBO.getTipoCBO(), Boolean.TRUE));
                    }
                } else {
                    cboFacade.salvar(new CBO(conteudoCBO.getDescricao(), conteudoCBO.getCodigo(), conteudoCBO.getTipoCBO(), Boolean.TRUE));
                }
                assistenteBarraProgresso.conta();
            }
            return assistenteBarraProgresso;
        } catch (Exception ex) {
            logger.error("Erro ao atualizar CBO{}", ex);
        }
        return null;
    }

}
