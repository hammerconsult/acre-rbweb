package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class InfracaoSaud extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Ocorrência")
    private Date dataOcorrencia;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Infração")
    private TipoInfracaoSaud tipoInfracaoSaud;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Usuário")
    private UsuarioSaud usuarioSaud;
    @Obrigatorio
    @Etiqueta("Suspenção (em dias)")
    private Integer diasSuspensao;
    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao advertencias;


    public InfracaoSaud() {
        super();
        this.dataCadastro = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public UsuarioSaud getUsuarioSaud() {
        return usuarioSaud;
    }

    public void setUsuarioSaud(UsuarioSaud usuarioSaud) {
        this.usuarioSaud = usuarioSaud;
    }

    public Integer getDiasSuspensao() {
        return diasSuspensao;
    }

    public void setDiasSuspensao(Integer diasSuspensao) {
        this.diasSuspensao = diasSuspensao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoInfracaoSaud getTipoInfracaoSaud() {
        return tipoInfracaoSaud;
    }

    public void setTipoInfracaoSaud(TipoInfracaoSaud tipoInfracaoSaud) {
        this.tipoInfracaoSaud = tipoInfracaoSaud;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return advertencias;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        advertencias = detentorArquivoComposicao;
    }
}
