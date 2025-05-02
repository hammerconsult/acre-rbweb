package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoRenovacaoOTT;
import br.com.webpublico.ott.CertificadoRenovacaoOttDTO;
import br.com.webpublico.ott.RenovacaoVeiculoOttDTO;
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
@Etiqueta("Renovação Veiculo OTT")
public class RenovacaoVeiculoOTT extends SuperEntidade {

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
    @Etiqueta("Veiculo Operadora Tecnologia de Transporte")
    private VeiculoOperadoraTecnologiaTransporte veiculoOtt;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "renovacaoVeiculoOTT", orphanRemoval = true)
    private List<VeiculoRenovacaoDetentorArquivo> veiculoRenovacaoDetentorArquivos;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Certificado Renovação OTT")
    private CertificadoRenovacaoOTT certificadoRenovacaoOTT;

    @ManyToOne
    @Etiqueta("Exercício Renovação Veiculo")
    private Exercicio exercicioRenovacao;

    public RenovacaoVeiculoOTT() {
        veiculoRenovacaoDetentorArquivos = Lists.newArrayList();
    }

    public RenovacaoVeiculoOttDTO toDTO() {
        RenovacaoVeiculoOttDTO dto = new RenovacaoVeiculoOttDTO();
        dto.setId(id);
        dto.setDataRenovacao(dataRenovacao);
        dto.setSituacaoRenovacao(SituacaoRenovacaoDTO.valueOf(situacaoRenovacao.name()));
        dto.setVeiculoOttDTO(veiculoOtt.toDTO());
        dto.setUsuario(usuarioSistema != null ? usuarioSistema.getNome() : "");
        dto.setExercicioRenovacao(exercicioRenovacao.getAno());
        if (certificadoRenovacaoOTT != null) {
            CertificadoRenovacaoOttDTO certificadoDto = new CertificadoRenovacaoOttDTO();
            certificadoDto.setId(certificadoRenovacaoOTT.getId());
            certificadoDto.setVencimento(certificadoRenovacaoOTT.getVencimento());
            certificadoDto.setDataEmissao(certificadoRenovacaoOTT.getDataEmissao());
            dto.setCertificadoRenovacaoOttDTO(certificadoDto);
        }
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

    public List<VeiculoRenovacaoDetentorArquivo> getVeiculoRenovacaoDetentorArquivos() {
        return veiculoRenovacaoDetentorArquivos;
    }

    public void setVeiculoRenovacaoDetentorArquivos(List<VeiculoRenovacaoDetentorArquivo> veiculoRenovacaoDetentorArquivos) {
        this.veiculoRenovacaoDetentorArquivos = veiculoRenovacaoDetentorArquivos;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOtt() {
        return veiculoOtt;
    }

    public void setVeiculoOtt(VeiculoOperadoraTecnologiaTransporte veiculoOtt) {
        this.veiculoOtt = veiculoOtt;
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
