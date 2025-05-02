package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta(value = "Bloqueio de Transferência de Proprietários")
public class BloqueioTransferenciaProprietario extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long codigo;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private UsuarioSistema usuarioLancamento;
    @Obrigatorio
    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Obrigatorio
    @Etiqueta("Data Inicial")
    @Temporal(TemporalType.DATE)
    private Date dataInicial;
    @ManyToOne
    private UsuarioSistema usuarioFinalizacao;
    @Etiqueta("Data Final")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;
    @Obrigatorio
    @Etiqueta("Cadastro Imobiliário")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Obrigatorio
    @Etiqueta("Motivo")
    private String motivo;
    private String motivoFinalizacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioLancamento() {
        return usuarioLancamento;
    }

    public void setUsuarioLancamento(UsuarioSistema usuarioLancamento) {
        this.usuarioLancamento = usuarioLancamento;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public UsuarioSistema getUsuarioFinalizacao() {
        return usuarioFinalizacao;
    }

    public void setUsuarioFinalizacao(UsuarioSistema usuarioFinalizacao) {
        this.usuarioFinalizacao = usuarioFinalizacao;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
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

    public String getMotivoFinalizacao() {
        return motivoFinalizacao;
    }

    public void setMotivoFinalizacao(String motivoFinalizacao) {
        this.motivoFinalizacao = motivoFinalizacao;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (dataFinal != null) {
            if (Strings.isNullOrEmpty(motivoFinalizacao)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo de Finalização deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public void validarFinalizacao() {
        ValidacaoException ve = new ValidacaoException();
        ve.lancarException();
    }
}
