package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoViagemSaud;
import br.com.webpublico.enums.tributario.MotivoViagemSaud;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Audited
public class AgendamentoViagemSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Obrigatorio
    @Etiqueta("Usuário do SAUD")
    @ManyToOne
    private UsuarioSaud usuarioSaud;
    @Obrigatorio
    @Etiqueta("Motivo da Viagem")
    @Enumerated(value = EnumType.STRING)
    private MotivoViagemSaud motivoViagem;
    @Obrigatorio
    @Etiqueta("Duração da Viagem (Horas)")
    private Integer duracaoViagemHora;
    @Obrigatorio
    @Etiqueta("Duração da Viagem (Minutos)")
    private Integer duracaoViagemMinuto;
    @Etiqueta("Endereço de Origem")
    @ManyToOne(cascade = CascadeType.ALL)
    private AgendamentoViagemSaudEndereco enderecoOrigem;
    @Etiqueta("Endereço de Destino")
    @ManyToOne(cascade = CascadeType.ALL)
    private AgendamentoViagemSaudEndereco enderecoDestino;
    @Etiqueta("Data da Viagem")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataViagem;
    @Obrigatorio
    @Etiqueta("Horário da Viagem (Horas)")
    private Integer horaViagem;
    @Obrigatorio
    @Etiqueta("Horário da Viagem (Minutos)")
    private Integer minutoViagem;
    @Enumerated(EnumType.STRING)
    private SituacaoViagemSaud situacao;
    @ManyToOne
    private UsuarioSistema usuarioAvaliacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAvaliacao;
    private String observacaoAvaliacao;
    @Obrigatorio
    @Etiqueta("Retorno da Viagem (Horas)")
    private Integer horaRetornoViagem;
    @Obrigatorio
    @Etiqueta("Retorno da Viagem (Minutos)")
    private Integer minutoRetornoViagem;

    public AgendamentoViagemSaud() {
        super();
        this.dataCadastro = new Date();
        this.enderecoOrigem = new AgendamentoViagemSaudEndereco();
        this.enderecoDestino = new AgendamentoViagemSaudEndereco();
        this.horaViagem = 0;
        this.minutoViagem = 0;
        this.horaRetornoViagem = 0;
        this.minutoRetornoViagem = 0;
        this.duracaoViagemHora = 0;
        this.duracaoViagemMinuto = 0;
        this.situacao = SituacaoViagemSaud.CADASTRADA;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AgendamentoViagemSaudEndereco getEnderecoOrigem() {
        return enderecoOrigem;
    }

    public void setEnderecoOrigem(AgendamentoViagemSaudEndereco enderecoOrigem) {
        this.enderecoOrigem = enderecoOrigem;
    }

    public AgendamentoViagemSaudEndereco getEnderecoDestino() {
        return enderecoDestino;
    }

    public void setEnderecoDestino(AgendamentoViagemSaudEndereco enderecoDestino) {
        this.enderecoDestino = enderecoDestino;
    }

    public Date getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(Date dataViagem) {
        this.dataViagem = dataViagem;
    }

    public MotivoViagemSaud getMotivoViagem() {
        return motivoViagem;
    }

    public void setMotivoViagem(MotivoViagemSaud motivoViagem) {
        this.motivoViagem = motivoViagem;
    }

    public Integer getDuracaoViagemHora() {
        return duracaoViagemHora;
    }

    public void setDuracaoViagemHora(Integer duracaoViagemHora) {
        this.duracaoViagemHora = duracaoViagemHora;
    }

    public Integer getDuracaoViagemMinuto() {
        return duracaoViagemMinuto;
    }

    public void setDuracaoViagemMinuto(Integer duracaoViagemMinuto) {
        this.duracaoViagemMinuto = duracaoViagemMinuto;
    }

    public Integer getHoraViagem() {
        return horaViagem;
    }

    public void setHoraViagem(Integer horaViagem) {
        this.horaViagem = horaViagem;
    }

    public Integer getMinutoViagem() {
        return minutoViagem;
    }

    public void setMinutoViagem(Integer minutoViagem) {
        this.minutoViagem = minutoViagem;
    }

    public SituacaoViagemSaud getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoViagemSaud situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getUsuarioAvaliacao() {
        return usuarioAvaliacao;
    }

    public void setUsuarioAvaliacao(UsuarioSistema usuarioAvaliacao) {
        this.usuarioAvaliacao = usuarioAvaliacao;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public String getObservacaoAvaliacao() {
        return observacaoAvaliacao;
    }

    public void setObservacaoAvaliacao(String observacaoAvaliacao) {
        this.observacaoAvaliacao = observacaoAvaliacao;
    }

    public Integer getHoraRetornoViagem() {
        return horaRetornoViagem;
    }

    public void setHoraRetornoViagem(Integer horaRetornoViagem) {
        this.horaRetornoViagem = horaRetornoViagem;
    }

    public Integer getMinutoRetornoViagem() {
        return minutoRetornoViagem;
    }

    public void setMinutoRetornoViagem(Integer minutoRetornoViagem) {
        this.minutoRetornoViagem = minutoRetornoViagem;
    }

    public Boolean isCadastrada() {
        return SituacaoViagemSaud.CADASTRADA.equals(this.situacao);
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        enderecoOrigem.realizarValidacoes(ve, "Endereço de Origem - ");
        enderecoDestino.realizarValidacoes(ve, "Endereço de Destino - ");
        Util.validarHoraMinuto(ve, horaViagem, minutoViagem, "Horário da Viagem");
        ve.lancarException();
    }

    @Override
    public String toString() {
        return "Usuário: " + usuarioSaud.getNome() + " (" + usuarioSaud.getCpf() + ") - " +
            "Data da viagem: " + DataUtil.getDataFormatada(dataViagem) + " - " +
            "Horário: " + DataUtil.formatarHoraMinuto(horaViagem, minutoViagem);
    }

    public void validarHorarioViagem(Integer horaAntecedenciaViagem) {
        if (horaAntecedenciaViagem != null && horaAntecedenciaViagem > 0) {
            LocalDateTime dataHoraViagem = LocalDateTime.of(dataViagem.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate(),
                LocalTime.of(horaViagem, minutoViagem));
            LocalDateTime dataMinimaAgendamento = LocalDateTime.now();
            dataMinimaAgendamento = dataMinimaAgendamento.plusHours(horaAntecedenciaViagem);
            if (dataHoraViagem.isBefore(dataMinimaAgendamento)) {
                throw new ValidacaoException("Você não pode agendar uma viagem com menos de " +
                    horaAntecedenciaViagem + " horas de antecedência. Por favor, escolha uma data que atenda a essa condição.");
            }
        }
    }
}
