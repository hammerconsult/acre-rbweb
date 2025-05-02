package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.ConfiguracaoStorage;
import br.com.webpublico.enums.Ambiente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.ArquivoUtil;
import br.com.webpublico.util.Util;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Stateless
public class ConfiguracaoStorageFacade extends AbstractFacade<ConfiguracaoStorage> {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoStorageFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;

    public ConfiguracaoStorageFacade() {
        super(ConfiguracaoStorage.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(ConfiguracaoStorage entity) {
        ConfiguracaoStorage ultimo = recuperarUltimo(entity.getAmbiente());
        if (ultimo != null && !ultimo.getId().equals(entity.getId())) {
            throw new ValidacaoException(String.format("Já existe uma configuração de storage para o ambiente %s", entity.getAmbiente().name()));
        }
        em.merge(entity);
    }

    public ConfiguracaoStorage recuperarUltimo(Ambiente ambiente) {
        String hql = "from ConfiguracaoStorage obj where obj.ambiente = :ambiente order by obj.id desc";
        Query q = getEntityManager()
            .createQuery(hql)
            .setParameter("ambiente", ambiente);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            ConfiguracaoStorage singleResult = (ConfiguracaoStorage) q.getResultList().get(0);
            return singleResult;
        }
        return null;
    }

    public String uploadFile(Arquivo arquivo) {
        SistemaFacade.PerfilApp perfilAplicacao = sistemaFacade.getPerfilAplicacao();
        String url = null;
        ConfiguracaoStorage configuracaoStorage = getConfiguracaoStorage(Ambiente.getPorPerfil(perfilAplicacao));

        try {

            List<ArquivoParte> partes = recuperarPartesOrdenadoPorId(arquivo);
            InputStream inputStream = montarInputStream(partes);

            File tempFile = ArquivoUtil.createTempFile(arquivo.getId() + "", inputStream);

            MinioClient minioClient = MinioClient.builder()
                .endpoint(configuracaoStorage.getHost())
                .credentials(configuracaoStorage.getAccessKey(), configuracaoStorage.getSecretKey())
                .build();

            minioClient.uploadObject(
                UploadObjectArgs.builder()
                    .bucket(configuracaoStorage.getBucket())
                    .contentType(arquivo.getMimeType() != null ? arquivo.getMimeType() : "application/pdf")
                    .object(arquivo.getId() + "-" + arquivo.getNome())
                    .filename(tempFile.getPath())
                    .build());

            url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(configuracaoStorage.getBucket())
                    .object(arquivo.getId() + "-" + arquivo.getNome())
                    .build());

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Não foi possível efeturar o upload do arquivo {} no bucket {}::{} ", arquivo.getDescricao(), configuracaoStorage.getHost(), configuracaoStorage.getBucket());
            logger.debug("Erro ao upload do arquivo para o Minio Storage. ", e);
        }
        return url;
    }

    private InputStream montarInputStream(List<ArquivoParte> partes) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : partes) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao recuperar o arquivo " + ex.getMessage());
            }
        }
        InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
        return inputStream;
    }

    private   List<ArquivoParte> recuperarPartesOrdenadoPorId(Arquivo arquivo) {
        return em.createQuery("select a from ArquivoParte a where a.arquivo.id = :id order by a.id")
            .setParameter("id", arquivo.getId())
            .getResultList();
    }

    public static InputStream getArquivoFromStorage(Arquivo arquivo) {
        SistemaFacade sistemaFacade = (SistemaFacade) Util.getFacadeViaLookup("java:module/SistemaFacade");
        SistemaFacade.PerfilApp perfilAplicacao = sistemaFacade.getPerfilAplicacao();
        ConfiguracaoStorage configuracaoStorage = getConfiguracaoStorage(Ambiente.getPorPerfil(perfilAplicacao));
        MinioClient minioClient =
            MinioClient.builder()
                .endpoint(configuracaoStorage.getHost())
                .credentials(configuracaoStorage.getAccessKey(), configuracaoStorage.getSecretKey())
                .build();
        try {
            minioClient.ignoreCertCheck();
            return minioClient.
                getObject(
                    GetObjectArgs.builder()
                        .bucket(configuracaoStorage.getBucket())
                        .object(arquivo.getId() + "-" + arquivo.getNome())
                        .build());
        } catch (Exception e) {
            logger.error("Não foi possivel recuperar o arquivo {} no bucket {}::{}", arquivo.getDescricao(), configuracaoStorage.getHost(), configuracaoStorage.getBucket());
            logger.debug("Erro ao recuperar o arquivo no bucket ", e);
        }
        return null;
    }

    private static ConfiguracaoStorage getConfiguracaoStorage(Ambiente ambiente) {
        ConfiguracaoStorageFacade facade = (ConfiguracaoStorageFacade) Util.getFacadeViaLookup("java:module/ConfiguracaoStorageFacade");
        return facade.recuperarUltimo(ambiente);
    }


}
