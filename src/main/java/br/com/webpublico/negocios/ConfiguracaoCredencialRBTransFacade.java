package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConfiguracaoCredencialRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Date;

/**
 * Created by zaca on 06/01/17.
 */
@Stateless
public class ConfiguracaoCredencialRBTransFacade extends AbstractFacade<ConfiguracaoCredencialRBTrans> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoCredencialRBTransFacade() {
        super(ConfiguracaoCredencialRBTrans.class);
    }

    @Override
    public ConfiguracaoCredencialRBTrans recuperar(Object id) {
        ConfiguracaoCredencialRBTrans config = em.find(ConfiguracaoCredencialRBTrans.class, id);
        if (config.getChancelaRBTrans() != null) {
            if (config.getChancelaRBTrans().getArquivo() != null) {
                if (config.getChancelaRBTrans().getArquivo().getPartes() != null
                        && !config.getChancelaRBTrans().getArquivo().getPartes().isEmpty()) {
                    config.getChancelaRBTrans().getArquivo().getPartes().size();
                }
            }
        }
        return config;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public Boolean isExistsConfiguracaoValidaPorData(Date data) throws ValidacaoException {
        String hql = "select config from ConfiguracaoCredencialRBTrans  config " +
                " where config.inicioEm >= :data ";

        Query q = em.createQuery(hql);
        q.setParameter("data", data);
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public ConfiguracaoCredencialRBTrans buscarConfiguracaoCredencialRBTransVigente() throws ValidacaoException {
        String hql = "SELECT config.* " +
                "FROM CONFIGCREDENCIALRBTRANS config " +
                " WHERE config.finalEm is null ";

        Query q = em.createNativeQuery(hql, ConfiguracaoCredencialRBTrans.class);
        q.setMaxResults(1);
        try {
            if (!q.getResultList().isEmpty()) {
                ConfiguracaoCredencialRBTrans config = (ConfiguracaoCredencialRBTrans) q.getResultList().get(0);
                config.getChancelaRBTrans().getArquivo().getPartes().size();
                return config;
            }

        } catch (NoResultException nre) {
            return null;
        }
        return null;
    }

    public void definirConfiguracaoAnteriorToVigente() {
        String hql = " select config from ConfiguracaoCredencialRBTrans  config " +
                " where config.finalEm is not null " +
                " and config.finalEm = (select max(conf.finalEm) from ConfiguracaoCredencialRBTrans conf" +
                " where conf.id = config.id )";

        Query q = em.createQuery(hql);

        try {
            if (!q.getResultList().isEmpty()) {
                ConfiguracaoCredencialRBTrans config = (ConfiguracaoCredencialRBTrans) q.getResultList().get(0);
                config.setFinalEm(null);
                em.merge(config);
            }
        } catch (Exception ex) {
            logger.debug("erro.:", ex);
        }
    }

    public ConfiguracaoCredencialRBTrans salvarSelecionadoWithArquivo(ConfiguracaoCredencialRBTrans entity, FileUploadEvent file){
        try {
            Arquivo arquivo = getArquivo(file);

            entity.getChancelaRBTrans().setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, arquivo.getInputStream()));

            entity.getChancelaRBTrans().setArquivo(arquivoFacade.retornaArquivoSalvo(entity.getChancelaRBTrans().getArquivo(),
                    entity.getChancelaRBTrans().getArquivo().getInputStream()));

            ConfiguracaoCredencialRBTrans configuracaoAnterior = buscarConfiguracaoCredencialRBTransVigente();
            if (configuracaoAnterior != null) {
                Date inicioEm = entity.getInicioEm();
                Date finalConfigAnterior = DataUtil.adicionaDias(inicioEm, -1);
                configuracaoAnterior.setFinalEm(finalConfigAnterior);
                em.merge(configuracaoAnterior);
            }



            return em.merge(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Arquivo getArquivo(FileUploadEvent file) throws IOException {
        UploadedFile up = file.getFile();
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType(up.getContentType());
        arquivo.setNome(up.getFileName());
        arquivo.setTamanho(up.getSize());
        arquivo.setDescricao(up.getFileName());
        arquivo.setInputStream(up.getInputstream());
        return arquivo;
    }
}
