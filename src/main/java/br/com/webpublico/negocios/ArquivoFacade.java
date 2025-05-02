/*
 * Codigo gerado automaticamente em Tue Feb 22 11:01:28 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.comum.ConfiguracaoStorageFacade;
import br.com.webpublico.nfse.domain.dtos.NfseArquivo;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.hibernate.Hibernate;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateless
public class ArquivoFacade implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ArquivoFacade.class);
    private static String todosTipos = ".ai application/postscript .aif, audio/x-aiff .aifc, audio/x-aiff .aiff, audio/x-aiff .asc, text/plain .atom, application/atom+xml .au, audio/basic .avi video/x-msvideo .bcpio application/x-bcpio .bin application/octet-stream .bmp image/bmp .cdf application/x-netcdf .cgm image/cgm .class application/octet-stream .cpio application/x-cpio .cpt application/mac-compactpro .csh application/x-csh .css text/css .dcr application/x-director .dif video/x-dv .dir application/x-director .djv image/vnd.djvu .djvu image/vnd.djvu .dll application/octet-stream .dmg application/octet-stream .dms application/octet-stream .doc application/msword .dtd application/xml-dtd .dv video/x-dv .dvi application/x-dvi .dxr application/x-director .eps application/postscript .etx text/x-setext .exe application/octet-stream .ez application/andrew-inset .flv video/x-flv .gif image/gif .gram application/srgs .grxml application/srgs+xml .gtar application/x-gtar .hdf application/x-hdf .hqx application/mac-binhex40 .htm text/html .html text/html .ice x-conference/x-cooltalk .ico image/x-icon .ics text/calendar .ief image/ief .ifb text/calendar .iges model/iges .igs model/iges .jnlp application/x-java-jnlp-file .jp2 image/jp2 .jpe image/jpeg .jpeg image/jpeg .jpg image/jpeg .js application/x-javascript .kar audio/midi .latex application/x-latex .lha application/octet-stream .lzh application/octet-stream .m3u audio/x-mpegurl .m4a audio/mp4a-latm .m4b audio/mp4a-latm .m4p audio/mp4a-latm .m4u video/vnd.mpegurl .m4v video/x-m4v .mac image/x-macpaint .man application/x-troff-man .mathml application/mathml+xml .me application/x-troff-me .mesh model/mesh .mid audio/midi .midi audio/midi .mif application/vnd.mif .mov video/quicktime .movie video/x-sgi-movie .mp2 audio/mpeg .mp3 audio/mpeg .mp4 video/mp4 .mpe video/mpeg .mpeg video/mpeg .mpg video/mpeg .mpga audio/mpeg .ms application/x-troff-ms .msh model/mesh .mxu video/vnd.mpegurl .nc application/x-netcdf .oda application/oda .ogg application/ogg .pbm image/x-portable-bitmap .pct image/pict .pdb chemical/x-pdb .pdf application/pdf .pgm image/x-portable-graymap .pgn application/x-chess-pgn .pic image/pict .pict image/pict .png image/png .pnm image/x-portable-anymap .pnt image/x-macpaint .pntg image/x-macpaint .ppm image/x-portable-pixmap .ppt application/vnd.ms-powerpoint .ps application/postscript .qt video/quicktime .qti image/x-quicktime .qtif image/x-quicktime .ra audio/x-pn-realaudio .ram audio/x-pn-realaudio .ras image/x-cmu-raster .rdf application/rdf+xml .rgb image/x-rgb .rm application/vnd.rn-realmedia .roff application/x-troff .rtf text/rtf .rtx text/richtext .sgm text/sgml .sgml text/sgml .sh application/x-sh .shar application/x-shar .silo model/mesh .sit application/x-stuffit .skd application/x-koan .skm application/x-koan .skp application/x-koan .skt application/x-koan .smi application/smil .smil application/smil .snd audio/basic .so application/octet-stream .spl application/x-futuresplash .src application/x-wais-source .sv4cpio application/x-sv4cpio .sv4crc application/x-sv4crc .svg image/svg+xml .swf application/x-shockwave-flash .t application/x-troff .tar application/x-tar .tcl application/x-tcl .tex application/x-tex .texi application/x-texinfo .texinfo application/x-texinfo .tif image/tiff .tiff image/tiff .tr application/x-troff .tsv text/tab-separated-values .txt text/plain .ustar application/x-ustar .vcd application/x-cdlink .vrml model/vrml .vxml application/voicexml+xml .wav audio/x-wav .wbmp image/vnd.wap.wbmp .wbmxl application/vnd.wap.wbxml .wml text/vnd.wap.wml .wmlc application/vnd.wap.wmlc .wmls text/vnd.wap.wmlscript .wmlsc application/vnd.wap.wmlscriptc .wrl model/vrml .xbm image/x-xbitmap .xht application/xhtml+xml .xhtml application/xhtml+xml .xls application/vnd.ms-excel .xml application/xml .xpm image/x-xpixmap .xsl application/xml .xslt application/xslt+xml .xul application/vnd.mozilla.xul+xml .xwd image/x-xwindowdump .xyz chemical/x-xyz .zip application/zip";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoStorageFacade configuracaoStorageFacade;


    public ArquivoFacade() {
    }


    protected EntityManager getEntityManager() {
        return em;
    }

    public Arquivo recupera(Long id) {
        try {
            return em.find(Arquivo.class, id);
        } catch (Exception e) {
            return null;
        }

    }

    public Arquivo recuperaDependencias(Long id) {
        try {
            Arquivo a = em.find(Arquivo.class, id);
            Hibernate.initialize(a.getPartes());
            return a;
        } catch (Exception e) {
            return null;
        }

    }

    public void novoArquivo(Arquivo arquivo, InputStream is) throws Exception {
        int bytesLidos = 0;
        while (true) {
            int restante = is.available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = is.read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            em.persist(arquivoParte);
        }
        em.persist(arquivo);
    }

    public Arquivo retornaArquivoSalvo(Arquivo arquivo, InputStream is) throws Exception {
        int bytesLidos = 0;
        arquivo = em.merge(arquivo);
        while (true) {
            int restante = is.available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = is.read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            em.merge(arquivoParte);
        }
        return arquivo;
    }

    public Arquivo novoArquivoMemoria(Arquivo arquivo) throws Exception {
        int bytesLidos = 0;
        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }

        return arquivo;
    }

    public Arquivo novoArquivoMemoria(Arquivo arquivo, InputStream is) throws Exception {
        int bytesLidos = 0;
        while (true) {
            int restante = is.available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = is.read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
//            em.persist(arquivoParte);
        }
        //        em.persist(arquivo);
        return arquivo;
    }

    public List<Arquivo> listaArquivos() {
        Query q = em.createQuery("from Arquivo");
        return q.getResultList();
    }

    public void recupera(Long id, OutputStream out) throws Exception {
        if (id != null) {
            Arquivo arquivo = em.find(Arquivo.class, id);
            if (arquivo != null) {
                for (ArquivoParte arquivoParte : arquivo.getPartes()) {
                    out.write(arquivoParte.getDados());
                }
            }
        }
    }

    @Deprecated
    public String getMimeType(String nomeArquivo) {
        String tipos[] = todosTipos.split(" ");
        for (int i = 0; i < tipos.length; i += 2) {
            if (nomeArquivo.toLowerCase().endsWith(tipos[i])) {
                return tipos[i + 1];
            }
        }
        return "application/octet-stream";
    }

    public String getMimeType(InputStream is) {
        TikaConfig tika = null;
        try {
            tika = new TikaConfig();
            MediaType mimetype = tika.getDetector().detect(TikaInputStream.get(is), new Metadata());
            return mimetype.getType() + "/" + mimetype.getSubtype();
        } catch (TikaException e) {
            log.error("Não foi possível gerar o mimeType: " + e);
        } catch (IOException e) {
            log.error("Não foi possível gerar o mimeType: " + e);
        }
        return "application/octet-stream";
    }

    public String getExtension(String mimeType) {
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType extension = allTypes.forName(mimeType);
            return extension.getExtension();
        } catch (MimeTypeException e) {
            log.error("Não foi possível recuperar a extensão do arquivo: " + e);
        }
        return "";
    }

    public void removerArquivo(Arquivo a) {
        try {
            Arquivo arq = em.find(Arquivo.class, a.getId());
            em.remove(arq);
        } catch (NullPointerException npe) {
        }
    }

    public Arquivo recuperaUltimaLogo() {
        Query q = em.createQuery("select a from Arquivo a where a.id = (select max(b.id) from Arquivo b where b.descricao = 'Logo')");
        q.setMaxResults(1);
        if (q.getResultList().size() > 0) {
            Arquivo arquivo = (Arquivo) q.getSingleResult();
            Hibernate.initialize(arquivo.getPartes());
            return arquivo;
        } else {
            return new Arquivo();
        }
    }

    public Arquivo recuperaUltimaLogoTopo() {
        Query q = em.createQuery("select a from Arquivo a where a.id = (select max(b.id) from Arquivo b where b.descricao = 'LogoTopo')");
        q.setMaxResults(1);
        try {
            Arquivo arquivo = (Arquivo) q.getSingleResult();
            Hibernate.initialize(arquivo.getPartes());
            return arquivo;
        } catch (NoResultException nre) {
            return new Arquivo();
        }
    }

    public Arquivo recuperarArquivoDaPessoa(Pessoa pessoa) {
        String sql = " select arq.* from pessoa pes " + " inner join arquivo arq on pes.arquivo_id = arq.id " + " where pes.id = :idPessoa ";

        Query q = em.createNativeQuery(sql, Arquivo.class);
        q.setParameter("idPessoa", pessoa.getId());

        List<Arquivo> arquivos = q.getResultList();

        if (arquivos != null && !arquivos.isEmpty()) {
            Arquivo arquivo = arquivos.get(0);
            Hibernate.initialize(arquivo.getPartes());

            return arquivo;
        }
        return null;
    }

    public void salvar(Arquivo arquivo, FileUploadEvent fileUploadEvent) {

        try {
            Preconditions.checkNotNull(fileUploadEvent, "Evento de upload do arquivo esta vazio entre em contato com o Suporte!");
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arquivo.setNome(arquivoRecebido.getFileName());
            arquivo.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arquivo.setTamanho(arquivoRecebido.getSize());
            novoArquivo(arquivo, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
            return false;
        }
        try {
            ar = recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }

    public StreamedContent montarArquivoParaDownloadPorArquivoComposicao(ArquivoComposicao arq) {
        if (arq.getArquivo().getId() != null) {
            arq.setArquivo(recuperaDependencias(arq.getArquivo().getId()));
        }
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arq.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arq.getArquivo().getMimeType(), arq.getArquivo().getNome());
        return s;
    }

    public StreamedContent montarArquivoParaDownloadPorArquivo(Arquivo arq) {
        if (arq.getId() != null) {
            arq = recuperaDependencias(arq.getId());
        }
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arq.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, arq.getMimeType(), arq.getNome());
        return s;
    }

    public Page<Arquivo> recuperarArquivosPaginado(Pageable pageable, String query) {
        String hql = "from Arquivo";
        hql += PesquisaGenericaNfseUtil.montarWhere("nome, descricao", query);
        Query q = em.createQuery(hql);
        q = PesquisaGenericaNfseUtil.atribuirParametroDeBusca(q, query);
        int resultCount = q.getResultList().size();
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);

        List<Arquivo> arquivos = q.getResultList();

        ArrayList<NfseArquivo> l = Lists.newArrayList(new NfseArquivo());

        List<NfseArquivo> arquivoDTOS = Lists.newArrayList();
        for (Arquivo arq : arquivos) {
            arquivoDTOS.add(new NfseArquivo(arq));
        }

        return new PageImpl(arquivoDTOS, pageable, resultCount);
    }

    public byte[] montarArquivoParaDownload(Arquivo arquivo) {
        if (arquivo.getId() != null) {
            arquivo = recuperaDependencias(arquivo.getId());
        }
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : arquivo.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] byteFile = new byte[buffer.size()];
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        try {
            is.read(byteFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteFile;
    }

    public Arquivo criarArquivo(UploadedFile file) throws IOException {
        Arquivo arquivo = new Arquivo();

        arquivo.setDescricao(file.getFileName());
        arquivo.setNome(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo.setMimeType(getMimeType(file.getInputstream()));
        arquivo = criarPartesDoArquivo(arquivo);

        return arquivo;
    }

    public Arquivo criarPartesDoArquivo(Arquivo arquivo) throws IOException {
        int bytesLidos = 0;

        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }

        return arquivo;
    }

    public DefaultStreamedContent ziparArquivos(String nomeArquivo, List<Arquivo> arquivos) throws IOException {
        List<StreamedContent> streamedContents = Lists.newArrayList();
        List<String> filesZipados = Lists.newArrayList();
        for (Arquivo arquivo : arquivos) {
            if (filesZipados.contains(arquivo.getNome())) continue;
            filesZipados.add(arquivo.getNome());
            streamedContents.add(montarArquivoParaDownloadPorArquivo(arquivo));
        }
        File zip = File.createTempFile(nomeArquivo, "zip");
        byte[] buffer = new byte[1024];
        FileOutputStream fout = new FileOutputStream(zip);
        ZipOutputStream zout = new ZipOutputStream(fout);
        for (StreamedContent streamedContent : streamedContents) {
            InputStream fin = streamedContent.getStream();
            zout.putNextEntry(new ZipEntry(streamedContent.getName()));
            int length;
            while ((length = fin.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            fin.close();
        }
        zout.close();
        FileInputStream fis = new FileInputStream(zip);
        return new DefaultStreamedContent(fis, "application/zip", nomeArquivo + ".zip");
    }


    public String enviarArquivoParaStorage(Arquivo arquivo) {
        if (arquivo != null) {
            arquivo = recupera(arquivo.getId());
            String s = configuracaoStorageFacade.uploadFile(arquivo);
            if (s != null) {
                arquivo.setLocal(Arquivo.Local.MINIO);
                arquivo.getPartes().clear();
                em.merge(arquivo);
                return s;
            }

        }

        return "";
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Arquivo montarImputStream(Arquivo arquivo) {
        arquivo = recupera(arquivo.getId());
        arquivo.montarImputStream();
        return arquivo;
    }

    public Arquivo salvarArquivoJaRecuperadoRetornando(Arquivo arquivo) {
        arquivo = em.merge(arquivo);
        for (ArquivoParte parte : arquivo.getPartes()) {
            em.merge(parte);
        }
        return arquivo;
    }
}
