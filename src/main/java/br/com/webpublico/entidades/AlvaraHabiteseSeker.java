/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Table(name = "ALVARAHABITESESEKER")
@Audited
public class AlvaraHabiteseSeker implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    Long idAlvaraHabitese;
    @Column(name="tb_sek_tipouso_construcao")
    Long tbSekTipoUsoConstrucao;
    @Column(name="tb_sek_construcaoexistente")
    Long tbSekConstrucaoExistente;
    @Column(name="tb_sek_diretorsecretario")
    Long tbSekDiretorSecretario;
    @Column(name="tb_usuario_idusuario")
    Long tbUsuarioIdusuario;
    @Column(name="tb_sek_tipodocumento")
    Long tbSekTipoDocumento;
    @Column(name="tb_sek_responsaveltecnico")
    Long tbSekResponsavelTecnico;
    String numero;
    String validade;
    String numProtocolo;
    String contribuinte;
    Date dataAtual;
    String areaTerreno;
    String rua;
    String quadra;
    String loteCadastro;
    Long bairro;
    Long nunPavimentos;
    String construExistente;
    String aConstruir;
    String aDemolir;
    String areaTotal;
    String artCrea;
    String nunMatInss;
    String numProcessoAdmin;
    Long numCasa;
    Date dataVistoria;
    String observacao;
    String cpfRequerente;
    String cnpjRequerente;
    String numAlvara;
    Long cancelado;
    String tipoAlvara;
    @Column(name="tb_sek_responsaveltecnicoexerc")
    Long tbSekResponsavelTecnicoExerc;
    String artCreaExe;

    public Long getIdAlvaraHabitese() {
        return idAlvaraHabitese;
    }

    public void setIdAlvaraHabitese(Long idAlvaraHabitese) {
        this.idAlvaraHabitese = idAlvaraHabitese;
    }

    public Long getTbSekTipoUsoConstrucao() {
        return tbSekTipoUsoConstrucao;
    }

    public void setTbSekTipoUsoConstrucao(Long tbSekTipoUsoConstrucao) {
        this.tbSekTipoUsoConstrucao = tbSekTipoUsoConstrucao;
    }

    public Long getTbSekConstrucaoExistente() {
        return tbSekConstrucaoExistente;
    }

    public void setTbSekConstrucaoExistente(Long tbSekConstrucaoExistente) {
        this.tbSekConstrucaoExistente = tbSekConstrucaoExistente;
    }

    public Long getTbSekDiretorSecretario() {
        return tbSekDiretorSecretario;
    }

    public void setTbSekDiretorSecretario(Long tbSekDiretorSecretario) {
        this.tbSekDiretorSecretario = tbSekDiretorSecretario;
    }

    public Long getTbUsuarioIdusuario() {
        return tbUsuarioIdusuario;
    }

    public void setTbUsuarioIdusuario(Long tbUsuarioIdusuario) {
        this.tbUsuarioIdusuario = tbUsuarioIdusuario;
    }

    public Long getTbSekTipoDocumento() {
        return tbSekTipoDocumento;
    }

    public void setTbSekTipoDocumento(Long tbSekTipoDocumento) {
        this.tbSekTipoDocumento = tbSekTipoDocumento;
    }

    public Long getTbSekResponsavelTecnico() {
        return tbSekResponsavelTecnico;
    }

    public void setTbSekResponsavelTecnico(Long tbSekResponsavelTecnico) {
        this.tbSekResponsavelTecnico = tbSekResponsavelTecnico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getNumProtocolo() {
        return numProtocolo;
    }

    public void setNumProtocolo(String numProtocolo) {
        this.numProtocolo = numProtocolo;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public Date getDataAtual() {
        return dataAtual;
    }

    public void setDataAtual(Date dataAtual) {
        this.dataAtual = dataAtual;
    }

    public String getAreaTerreno() {
        return areaTerreno != null ? areaTerreno.replace(".","").replace(",", ".") : "0";
    }

    public void setAreaTerreno(String areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLoteCadastro() {
        return loteCadastro;
    }

    public void setLoteCadastro(String loteCadastro) {
        this.loteCadastro = loteCadastro;
    }

    public Long getBairro() {
        return bairro;
    }

    public void setBairro(Long bairro) {
        this.bairro = bairro;
    }

    public Long getNunPavimentos() {
        return nunPavimentos != null ? nunPavimentos : 0L;
    }

    public void setNunPavimentos(Long nunPavimentos) {
        this.nunPavimentos = nunPavimentos;
    }

    public String getConstruExistente() {
        return construExistente != null ? construExistente.replace(".","").replace(",", ".") : "0";
    }

    public void setConstruExistente(String construExistente) {
        this.construExistente = construExistente;
    }

    public String getaConstruir() {
        return aConstruir;
    }

    public void setaConstruir(String aConstruir) {
        this.aConstruir = aConstruir;
    }

    public String getaDemolir() {
        return aDemolir;
    }

    public void setaDemolir(String aDemolir) {
        this.aDemolir = aDemolir;
    }

    public String getAreaTotal() {
        return areaTotal != null ? areaTotal.replace(".","").replace(",", ".") : "0";
    }

    public void setAreaTotal(String areaTotal) {
        this.areaTotal = areaTotal;
    }

    public String getArtCrea() {
        return artCrea;
    }

    public void setArtCrea(String artCrea) {
        this.artCrea = artCrea;
    }

    public String getNunMatInss() {
        return nunMatInss;
    }

    public void setNunMatInss(String nunMatInss) {
        this.nunMatInss = nunMatInss;
    }

    public String getNumProcessoAdmin() {
        return numProcessoAdmin;
    }

    public void setNumProcessoAdmin(String numProcessoAdmin) {
        this.numProcessoAdmin = numProcessoAdmin;
    }

    public Long getNumCasa() {
        return numCasa;
    }

    public void setNumCasa(Long numCasa) {
        this.numCasa = numCasa;
    }

    public Date getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(Date dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCpfRequerente() {
        return cpfRequerente;
    }

    public void setCpfRequerente(String cpfRequerente) {
        this.cpfRequerente = cpfRequerente;
    }

    public String getCnpjRequerente() {
        return cnpjRequerente;
    }

    public void setCnpjRequerente(String cnpjRequerente) {
        this.cnpjRequerente = cnpjRequerente;
    }

    public String getNumAlvara() {
        return numAlvara;
    }

    public void setNumAlvara(String numAlvara) {
        this.numAlvara = numAlvara;
    }

    public Long getCancelado() {
        return cancelado != null ? cancelado : 0;
    }

    public void setCancelado(Long cancelado) {
        this.cancelado = cancelado;
    }

    public String getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(String tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public Long getTbSekResponsavelTecnicoExerc() {
        return tbSekResponsavelTecnicoExerc;
    }

    public void setTbSekResponsavelTecnicoExerc(Long tbSekResponsavelTecnicoExerc) {
        this.tbSekResponsavelTecnicoExerc = tbSekResponsavelTecnicoExerc;
    }

    public String getArtCreaExe() {
        return artCreaExe;
    }

    public void setArtCreaExe(String artCreaExe) {
        this.artCreaExe = artCreaExe;
    }
}
