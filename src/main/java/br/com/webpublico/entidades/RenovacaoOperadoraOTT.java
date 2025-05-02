package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRenovacaoOTT;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.ott.RenovacaoOperadoraOttDTO;
import br.com.webpublico.ott.enums.SituacaoRenovacaoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Renovação Operadora Tecnologia de Transporte")
public class RenovacaoOperadoraOTT extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoRenovacaoOTT situacaoRenovacao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Renovação")
    private Date dataRenovacao;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "renovacaoOperadoraOTT", orphanRemoval = true)
    private List<OperadoraRenovacaoDetentorArquivo> operadoraRenovacaoDetentorArquivos;

    @ManyToOne
    @Etiqueta("Operadora Tecnologia de Transporte")
    private OperadoraTecnologiaTransporte operadora;

    @ManyToOne
    @Etiqueta("Exercício Renovação Operadora")
    private Exercicio exercicioRenovacao;

    public RenovacaoOperadoraOTT() {
        operadoraRenovacaoDetentorArquivos = Lists.newArrayList();
    }

    public RenovacaoOperadoraOttDTO toDTO() {
        RenovacaoOperadoraOttDTO dto = new RenovacaoOperadoraOttDTO();
        dto.setId(id);
        dto.setDataRenovacao(dataRenovacao);
        dto.setSituacaoRenovacao(SituacaoRenovacaoDTO.valueOf(situacaoRenovacao.name()));
        dto.setOperadoraOttDTO(operadora.toDTO());
        dto.setUsuario(usuarioSistema != null ? usuarioSistema.getNome() : "");
        dto.setExercicioRenovacao(exercicioRenovacao != null ? exercicioRenovacao.getAno() : null);
        return dto;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoRenovacaoOTT getSituacaoRenovacao() {
        return situacaoRenovacao;
    }

    public void setSituacaoRenovacao(SituacaoRenovacaoOTT situacaoRenovacao) {
        this.situacaoRenovacao = situacaoRenovacao;
    }

    public Date getDataRenovacao() {
        return dataRenovacao;
    }

    public void setDataRenovacao(Date dataRenovacao) {
        this.dataRenovacao = dataRenovacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public OperadoraTecnologiaTransporte getOperadora() {
        return operadora;
    }

    public void setOperadora(OperadoraTecnologiaTransporte operadora) {
        this.operadora = operadora;
    }

    public Exercicio getExercicioRenovacao() {
        return exercicioRenovacao;
    }

    public void setExercicioRenovacao(Exercicio exercicioRenovacao) {
        this.exercicioRenovacao = exercicioRenovacao;
    }

    public List<OperadoraRenovacaoDetentorArquivo> getOperadoraRenovacaoDetentorArquivos() {
        return operadoraRenovacaoDetentorArquivos;
    }

    public void setOperadoraRenovacaoDetentorArquivos(List<OperadoraRenovacaoDetentorArquivo> operadoraRenovacaoDetentorArquivos) {
        this.operadoraRenovacaoDetentorArquivos = operadoraRenovacaoDetentorArquivos;
    }

    @Override
    public void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        for (OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo : operadoraRenovacaoDetentorArquivos) {
            if (operadoraRenovacaoDetentorArquivo.getObrigatorio()
                && operadoraRenovacaoDetentorArquivo.getDetentorArquivoComp() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O documento " +
                    operadoraRenovacaoDetentorArquivo.getDescricaoDocumento() +
                    " deve ser anexado.");
            }
        }
        ve.lancarException();
    }
}
