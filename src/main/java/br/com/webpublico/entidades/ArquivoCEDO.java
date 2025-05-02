package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.ArquivoUtil;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ArquivoCEDO extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataGeracao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataImportacao;
    @ManyToOne
    private UsuarioSistema usuarioImportacao;
    @OneToMany(mappedBy = "arquivoCEDO")
    private List<DetalheArquivoCEDO> detalhes;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private String hashMd5;
    private boolean processado;

    public ArquivoCEDO() {
        super();
        this.detalhes = Lists.newArrayList();
        this.processado = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Date getDataImportacao() {
        return dataImportacao;
    }

    public void setDataImportacao(Date dataImportacao) {
        this.dataImportacao = dataImportacao;
    }

    public UsuarioSistema getUsuarioImportacao() {
        return usuarioImportacao;
    }

    public void setUsuarioImportacao(UsuarioSistema usuarioImportacao) {
        this.usuarioImportacao = usuarioImportacao;
    }

    public List<DetalheArquivoCEDO> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<DetalheArquivoCEDO> detalhes) {
        this.detalhes = detalhes;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getHashMd5() {
        return hashMd5;
    }

    public void setHashMd5(String hashMd5) {
        this.hashMd5 = hashMd5;
    }

    public boolean isProcessado() {
        return processado;
    }

    public void setProcessado(boolean processado) {
        this.processado = processado;
    }

    @Override
    public void realizarValidacoes() {
        if (detentorArquivoComposicao == null || detentorArquivoComposicao.getArquivoComposicao() == null) {
            throw new ValidacaoException("O Arquivo deve ser informado.");
        }
    }

    public List<String> getLinhasArquivo() throws IOException {
        Arquivo arquivo = detentorArquivoComposicao.getArquivoComposicao().getArquivo();
        arquivo.montarImputStream();
        InputStream inputStream = arquivo.getInputStream();
        return IOUtils.readLines(inputStream);
    }

    public void gerarHashMd5() {
        try {
            String conteudo = StringUtils.join(getLinhasArquivo(), ",");
            hashMd5 = ArquivoUtil.stringHexa(ArquivoUtil.gerarHashMD5(conteudo));
        } catch (IOException e) {
            throw new ValidacaoException("Erro inesperado ao gerar o hash do arquivo CEDO.");
        }
    }
}
