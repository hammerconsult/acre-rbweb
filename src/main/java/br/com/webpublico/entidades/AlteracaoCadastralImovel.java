package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/06/14
 * Time: 08:42
 * To change this template use File | Settings | File Templates.
 */

@GrupoDiagrama(nome = "Cadastro Imobiliario")
@Entity
@Audited
@Etiqueta("Processo de Ativação/Inativação Cadastral de Imóvel")
public class AlteracaoCadastralImovel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Número de Registro")
    private Long registro;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 2)
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataRegistro;
    @Obrigatorio
    @Tabelavel(ordemApresentacao = 3)
    @Temporal(value = TemporalType.TIME)
    @Etiqueta("Hora")
    private Date horaRegistro;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 6)
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Processo")
    private TipoProcesso tipoProcesso;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Usuário Responsável")
    @ManyToOne
    private UsuarioSistema usuarioResponsavel;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 7)
    @Etiqueta("Cadastro Imobiliário")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    private String motivo;
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Processo")
    private String processo;
    @Transient
    private Long criadoEm;

    public AlteracaoCadastralImovel() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistro() {
        return registro;
    }

    public void setRegistro(Long registro) {
        this.registro = registro;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getHoraRegistro() {
        return horaRegistro;
    }

    public void setHoraRegistro(Date horaRegistro) {
        this.horaRegistro = horaRegistro;
    }

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getProcesso() {
        return processo;
    }

    public void setProcesso(String processo) {
        this.processo = processo;
    }

    public enum TipoProcesso {
        ATIVO("Ativação"), INATIVO("Inativação");

        private String descricao;

        TipoProcesso(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof AlteracaoCadastralImovel)) {
            return false;
        }
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
