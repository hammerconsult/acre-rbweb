/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.comum.ConfiguracaoStorageFacade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Arquivos")
@Entity

@Audited
public class Arquivo extends SuperEntidade {
    protected static final Logger logger = LoggerFactory.getLogger(Arquivo.class);

    private static final long serialVersionUID = 1L;
    @Id
    @Etiqueta("Código")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("nome")
    private String nome;
    private String mimeType;
    private Long tamanho;
    @NotAudited
    @OneToMany(mappedBy = "arquivo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "id")
    private List<ArquivoParte> partes;
    @Enumerated(value = EnumType.STRING)
    private Local local;
    @Transient
    private InputStream inputStream;
    @Transient
    private ArquivoParte parteMinio;

    public Arquivo() {
        partes = new ArrayList<>();
    }

    public static Arquivo toArquivo(Long id,
                                    String nome,
                                    String descricao,
                                    String mimeType,
                                    Long tamanho,
                                    String conteudo) {
        ArquivoDTO arquivoDTO = new ArquivoDTO();
        arquivoDTO.setId(id);
        arquivoDTO.setNome(nome);
        arquivoDTO.setDescricao(descricao);
        arquivoDTO.setMimeType(mimeType);
        arquivoDTO.setTamanho(tamanho);
        arquivoDTO.setConteudo(conteudo);
        return toArquivo(arquivoDTO);
    }

    public static Arquivo toArquivo(ArquivoDTO arquivoDTO) {
        Arquivo arquivo = new Arquivo();
        arquivo.setId(arquivoDTO.getId());
        arquivo.setNome(arquivoDTO.getNome());
        arquivo.setDescricao(arquivoDTO.getDescricao());
        arquivo.setMimeType(arquivoDTO.getMimeType());
        arquivo.setTamanho(arquivoDTO.getTamanho());
        try {
            dividirConteudo(arquivo, arquivoDTO.getConteudo());
        } catch (Exception e) {
            e.printStackTrace();
            arquivo.setPartes(new ArrayList());
        }
        return arquivo;
    }

    public static Arquivo clonar(Arquivo arquivo) {
        if (arquivo == null) return null;
        Arquivo clone = new Arquivo();
        clone.setCriadoEm(arquivo.getCriadoEm());
        clone.setNome(arquivo.getNome());
        clone.setDescricao(arquivo.getDescricao());
        clone.setTamanho(arquivo.getTamanho());
        clone.setMimeType(arquivo.getMimeType());
        clone.setInputStream(arquivo.getInputStream());

        List<ArquivoParte> partes = Lists.newArrayList();
        for (ArquivoParte parte : arquivo.getPartes()) {
            partes.add(ArquivoParte.clonar(parte, clone));
        }
        clone.setPartes(partes);
        return clone;
    }

    public static void dividirConteudo(Arquivo arquivo, String conteudo) throws IOException {
        traduzirConteudoParaInputStream(arquivo, conteudo);
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
    }

    private static void traduzirConteudoParaInputStream(Arquivo arquivo, String conteudo) {
        String data = "";
        try {
            String dataInfo = conteudo.split(";base64,")[0];
            data = conteudo.split("base64,")[1];
            arquivo.setMimeType(dataInfo.split(":")[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            data = conteudo;
        }

        Base64 decoder = new Base64();
        byte[] imgBytes = decoder.decode(data);
        arquivo.setInputStream(new ByteArrayInputStream(imgBytes));
    }

    public ArquivoDTO toArquivoDTO() {
        ArquivoDTO arquivoDTO = new ArquivoDTO();
        arquivoDTO.setId(this.id);
        arquivoDTO.setDescricao(this.descricao);
        arquivoDTO.setMimeType(this.mimeType);
        arquivoDTO.setTamanho(this.tamanho);
        arquivoDTO.setNome(this.nome);
        arquivoDTO.setConteudo(montarConteudo());
        return arquivoDTO;
    }

    public String montarConteudo() {
        try {
            if (getPartes() != null && !getPartes().isEmpty()) {
                ByteArrayOutputStream buffer = getByteArrayOutputStream();
                Base64 codec = new Base64();
                return "data:" + this.getMimeType() + ";base64," + new String(codec.encode(buffer.toByteArray()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    public String montarConteudoBase64() {
        try {
            if (getPartes() != null && !getPartes().isEmpty()) {
                ByteArrayOutputStream buffer = getByteArrayOutputStream();
                Base64 codec = new Base64();
                return new String(codec.encode(buffer.toByteArray()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }

    private ByteArrayOutputStream getByteArrayOutputStream() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte arquivoParte : getPartes()) {
            buffer.write(arquivoParte.getDados());
        }
        return buffer;
    }


    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ArquivoParte> getPartes() {
        if (Local.MINIO.equals(getLocal())) {
            if (parteMinio == null) {
                parteMinio = new ArquivoParte();
                try {
                    parteMinio.setDados(IOUtils.toByteArray(ConfiguracaoStorageFacade.getArquivoFromStorage(this)));
                } catch (Exception e) {
                    logger.debug("Erro ao recuperar o arquivo no bucket ");
                }
            }
            return Lists.newArrayList(parteMinio);
        }
        return partes;
    }

    public void setPartes(List<ArquivoParte> partes) {
        this.partes = partes;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Arquivo)) {
            return false;
        }
        Arquivo other = (Arquivo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public void montarImputStream() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : this.getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao recuperar o arquivo " + ex.getMessage());
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        this.setInputStream(is);
    }

    @JsonIgnore
    public byte[] getByteArrayDosDados() {
        try {
            ByteArrayOutputStream buffer = getByteArrayOutputStream();
            return buffer.toByteArray();
        } catch (Exception ex) {
            System.out.println("Erro ao recuperar o arquivo " + ex.getMessage());
        }
        return null;
    }

    public ArquivoDTO toArquivoDTO(String descricao) {
        ArquivoDTO arquivoDTO = new ArquivoDTO();
        arquivoDTO.setId(this.id);
        arquivoDTO.setNome(this.nome);
        arquivoDTO.setDescricao(descricao);
        arquivoDTO.setMimeType(this.mimeType);
        arquivoDTO.setTamanho(this.tamanho);
        arquivoDTO.setConteudo(montarConteudo());
        return arquivoDTO;
    }

    public static enum Local {
        SQL, MINIO
    }

    public String descricaoOuNomeDoArquivo(boolean nome) {
        if (this.getDescricao() != null && !nome) return this.getDescricao();
        if (this.getNome() != null) return this.getNome();
        return "";
    }
}
