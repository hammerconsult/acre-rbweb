package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.webreportdto.dto.rh.RelatorioDescontoPrevidenciarioDTO;

import java.util.Calendar;

public class RelatorioDescontoPrevidenciarioVO {

    private Integer mes;
    private Integer ano;
    private String primeiro;
    private String segundo;
    private String terceiro;
    private String quarto;
    private String quinto;
    private String sexto;
    private String setimo;
    private String oitavo;
    private String nono;
    private String decimo;
    private String decimoPrimeiro;
    private String decimoSegundo;

    public RelatorioDescontoPrevidenciarioVO() {
    }

    public static RelatorioDescontoPrevidenciarioDTO descontoPrevidenciarioToDto(RelatorioDescontoPrevidenciarioVO parametrosRelatorio) {
        RelatorioDescontoPrevidenciarioDTO parametro = new RelatorioDescontoPrevidenciarioDTO();
        parametro.setPrimeiro(parametrosRelatorio.getPrimeiro());
        parametro.setSegundo(parametrosRelatorio.getSegundo());
        parametro.setTerceiro(parametrosRelatorio.getTerceiro());
        parametro.setQuarto(parametrosRelatorio.getQuarto());
        parametro.setQuinto(parametrosRelatorio.getQuinto());
        parametro.setSexto(parametrosRelatorio.getSexto());
        parametro.setSetimo(parametrosRelatorio.getSetimo());
        parametro.setOitavo(parametrosRelatorio.getOitavo());
        parametro.setNono(parametrosRelatorio.getNono());
        parametro.setDecimo(parametrosRelatorio.getDecimo());
        parametro.setDecimoPrimeiro(parametrosRelatorio.getDecimoPrimeiro());
        parametro.setDecimoSegundo(parametrosRelatorio.getDecimoSegundo());
        return parametro;
    }

    public void montaParametrosMeses() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, mes - 1);
        cal.set(Calendar.YEAR, ano);

        decimoSegundo = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        decimoPrimeiro = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        decimo = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        nono = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        oitavo = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        setimo = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        sexto = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        quinto = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        quarto = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        terceiro = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        segundo = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);

        cal.add(Calendar.MONTH, -1);
        primeiro = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.YEAR);
    }

    public String getPrimeiro() {
        return primeiro;
    }

    public void setPrimeiro(String primeiro) {
        this.primeiro = primeiro;
    }

    public String getSegundo() {
        return segundo;
    }

    public void setSegundo(String segundo) {
        this.segundo = segundo;
    }

    public String getTerceiro() {
        return terceiro;
    }

    public void setTerceiro(String terceiro) {
        this.terceiro = terceiro;
    }

    public String getQuarto() {
        return quarto;
    }

    public void setQuarto(String quarto) {
        this.quarto = quarto;
    }

    public String getQuinto() {
        return quinto;
    }

    public void setQuinto(String quinto) {
        this.quinto = quinto;
    }

    public String getSexto() {
        return sexto;
    }

    public void setSexto(String sexto) {
        this.sexto = sexto;
    }

    public String getSetimo() {
        return setimo;
    }

    public void setSetimo(String setimo) {
        this.setimo = setimo;
    }

    public String getOitavo() {
        return oitavo;
    }

    public void setOitavo(String oitavo) {
        this.oitavo = oitavo;
    }

    public String getNono() {
        return nono;
    }

    public void setNono(String nono) {
        this.nono = nono;
    }

    public String getDecimo() {
        return decimo;
    }

    public void setDecimo(String decimo) {
        this.decimo = decimo;
    }

    public String getDecimoPrimeiro() {
        return decimoPrimeiro;
    }

    public void setDecimoPrimeiro(String decimoPrimeiro) {
        this.decimoPrimeiro = decimoPrimeiro;
    }

    public String getDecimoSegundo() {
        return decimoSegundo;
    }

    public void setDecimoSegundo(String decimoSegundo) {
        this.decimoSegundo = decimoSegundo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
