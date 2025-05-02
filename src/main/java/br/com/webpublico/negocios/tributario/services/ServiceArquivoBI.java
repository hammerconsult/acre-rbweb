package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidadesauxiliares.bi.TipoExportacaoArquivoBI;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.negocios.contabil.ExportacaoArquivoBIFacade;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class ServiceArquivoBI {

    public static final Logger logger = LoggerFactory.getLogger(ServiceArquivoBI.class);
    @PersistenceContext
    protected transient EntityManager em;
    public ExportacaoArquivoBIFacade exportacaoArquivoBIFacade;

    @PostConstruct
    private void init() {
        try {
            exportacaoArquivoBIFacade = (ExportacaoArquivoBIFacade) new InitialContext().lookup("java:module/ExportacaoArquivoBIFacade");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarArquivosTributarios() {
        enviarArquivosBI(ModuloSistema.TRIBUTARIO, TipoWebService.ARQUIVO_BI_TRIBUTARIO);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarArquivoDebitosGeraisTributarios() {
        enviarArquivosBIDebitosGeralTributario(TipoWebService.ARQUIVO_BI_TRIBUTARIO);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarArquivosContabeis() {
        enviarArquivosBI(ModuloSistema.CONTABIL, TipoWebService.ARQUIVO_BI_CONTABIL);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarArquivosBIDebitosGeralTributario(TipoWebService tipoWebService) {
        exportacaoArquivoBIFacade.enviarArquivosBIDebitosGeralTributario(tipoWebService);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void enviarArquivosBI(ModuloSistema moduloSistema, TipoWebService tipoWebService) {
        List<TipoExportacaoArquivoBI> tipos = TipoExportacaoArquivoBI.getTiposArquivosBiPorModulo(moduloSistema);
        for (TipoExportacaoArquivoBI tipo : tipos) {
            if (TipoExportacaoArquivoBI.DEBITOS_GERAL.equals(tipo)) continue;
            try {
                String lineSeparator = System.getProperty("line.separator");
                System.setProperty("line.separator", "\r\n");
                logger.debug("Vai gerar o arquivo com " + tipo.getNomeArquivo());
                StreamedContent arquivo = exportacaoArquivoBIFacade.gerarEnviarAquivosBI(tipo);
                logger.debug("Vai enviar o Arquivo via FTP");
                exportacaoArquivoBIFacade.enviarArquivoParaFTP(arquivo, tipoWebService);
                logger.debug("Enviou o Arquivo");
                System.setProperty("line.separator", lineSeparator);
            } catch (Exception e) {
                logger.error("{}", e);
            }
        }
    }
}
