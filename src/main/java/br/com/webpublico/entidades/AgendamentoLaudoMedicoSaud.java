package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class AgendamentoLaudoMedicoSaud extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Obrigatorio
    @Etiqueta("Usuário do SAUD")
    @ManyToOne
    private UsuarioSaud usuarioSaud;
    @Etiqueta("Data do Exame")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    private Date dataExame;
    @Obrigatorio
    @Etiqueta("Horário da Viagem (Horas)")
    private Integer horaExame;
    @Obrigatorio
    @Etiqueta("Horário da Viagem (Minutos)")
    private Integer minutoExame;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public AgendamentoLaudoMedicoSaud() {
        super();
        this.dataCadastro = new Date();
        this.horaExame = 0;
        this.minutoExame = 0;
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

    public Date getDataExame() {
        return dataExame;
    }

    public void setDataExame(Date dataExame) {
        this.dataExame = dataExame;
    }

    public Integer getHoraExame() {
        return horaExame;
    }

    public void setHoraExame(Integer horaExame) {
        this.horaExame = horaExame;
    }

    public Integer getMinutoExame() {
        return minutoExame;
    }

    public void setMinutoExame(Integer minutoExame) {
        this.minutoExame = minutoExame;
    }

    @Override
    public void realizarValidacoes() {
        super.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        Util.validarHoraMinuto(ve, horaExame, minutoExame, "Horário do exame");
        ve.lancarException();
    }

    @Override
    public String toString() {
        return "Usuário: " + usuarioSaud.getNome() + " (" + usuarioSaud.getCpf() + ") - " +
            "Data do exame: " + DataUtil.getDataFormatada(dataExame) + " - " +
            "Horário: " + DataUtil.formatarHoraMinuto(horaExame, minutoExame);
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
