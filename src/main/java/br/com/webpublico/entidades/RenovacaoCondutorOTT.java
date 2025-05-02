package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRenovacaoOTT;
import br.com.webpublico.ott.RenovacaoCondutorOttDTO;
import br.com.webpublico.ott.enums.SituacaoRenovacaoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.beust.jcommander.internal.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Renovação Condutor OTT")
public class RenovacaoCondutorOTT extends SuperEntidade {

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

    @ManyToOne
    @Etiqueta("Condutor Operadora Tecnologia de Transporte")
    private CondutorOperadoraTecnologiaTransporte condutorOtt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "renovacaoCondutorOTT", orphanRemoval = true)
    private List<CondutorRenovacaoDetentorArquivo> condutorRenovacaoDetentorArquivos;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Certificado Renovação OTT")
    private CertificadoRenovacaoOTT certificadoRenovacaoOTT;

    @ManyToOne
    @Etiqueta("Exercício Renovação Condutor")
    private Exercicio exercicioRenovacao;

    public RenovacaoCondutorOTT() {
        condutorRenovacaoDetentorArquivos = Lists.newArrayList();
    }

    public RenovacaoCondutorOttDTO toDTO() {
        RenovacaoCondutorOttDTO dto = new RenovacaoCondutorOttDTO();
        dto.setId(id);
        dto.setDataRenovacao(dataRenovacao);
        dto.setSituacaoRenovacao(SituacaoRenovacaoDTO.valueOf(situacaoRenovacao.name()));
        dto.setCondutorOttDTO(condutorOtt.toDTO());
        dto.setUsuario(usuarioSistema != null ? usuarioSistema.getNome() : "");
        dto.setExercicioRenovacao(exercicioRenovacao != null ? exercicioRenovacao.getAno() : null);
        return dto;
    }

    public RenovacaoCondutorOTT(Long id) {
        this.id = id;
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

    public CondutorOperadoraTecnologiaTransporte getCondutorOtt() {
        return condutorOtt;
    }

    public void setCondutorOtt(CondutorOperadoraTecnologiaTransporte condutorOtt) {
        this.condutorOtt = condutorOtt;
    }

    public List<CondutorRenovacaoDetentorArquivo> getCondutorRenovacaoDetentorArquivos() {
        return condutorRenovacaoDetentorArquivos;
    }

    public void setCondutorRenovacaoDetentorArquivos(List<CondutorRenovacaoDetentorArquivo> condutorRenovacaoDetentorArquivos) {
        this.condutorRenovacaoDetentorArquivos = condutorRenovacaoDetentorArquivos;
    }

    public CertificadoRenovacaoOTT getCertificadoRenovacaoOTT() {
        return certificadoRenovacaoOTT;
    }

    public void setCertificadoRenovacaoOTT(CertificadoRenovacaoOTT certificadoRenovacaoOTT) {
        this.certificadoRenovacaoOTT = certificadoRenovacaoOTT;
    }

    public Exercicio getExercicioRenovacao() {
        return exercicioRenovacao;
    }

    public void setExercicioRenovacao(Exercicio exercicioRenovacao) {
        this.exercicioRenovacao = exercicioRenovacao;
    }
}
