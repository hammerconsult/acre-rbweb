/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import net.sf.jasperreports.engine.JRException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author WebPublico07
 */
@ManagedBean
@SessionScoped
public class RelatorioSaldosDasContasDespesaControlador extends AbstractReport implements Serializable {

    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private Date dataCalculo;
    private Long teste;

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public String formataData(Date data) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        return formatador.format(data);
    }

    public void limpaCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        dataCalculo = null;

    }

    public void gerarRelatorioDeSaldos() throws JRException, IOException {
        if (hierarquiaOrganizacionalSelecionada != null) {
            String arquivoJasper = "RelatorioSaldosDasContasDespesa.jasper";
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            HashMap parameters = new HashMap();
            parameters.put("HI_SELECIONADA", Long.parseLong(hierarquiaOrganizacionalSelecionada.getSubordinada().getId() + ""));
            parameters.put("SUBREPORT_DIR", subReport);
            parameters.put("DATA", formataData(dataCalculo));
            parameters.put("USER", usuarioLogado().getLogin());
            parameters.put("ENTIDADE", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            //System.out.println("caminho imagem " + getCaminho());
            parameters.put("IMAGEM", getCaminhoImagem());
            gerarRelatorio(arquivoJasper, parameters);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " O Campo Unidade é Obrigatório!", " O Campo Unidade é Obrigatório!"));
        }
    }
//    @Override
//    protected Connection recuperaConexaoJDBC() {
//        Connection conn = super.recuperaConexaoJDBC();
//        try {
//            PreparedStatement ps = conn.prepareStatement(
//                "insert into rsaldocontasdespesa  " +
//                "select '55' as codigounidade,  " +
//                "       'SAOP' as descricaounidade,  " +
//                "       '033' as codigosubacao,  " +
//                "       'subação teste 033' as descricaosubacao,  " +
//                "       conta.codigo,  " +
//                "       fontedespesaorc.id,  " +
//                "       conta.descricao,  " +
//                "       saldofontedespesaorc.empenhado,  " +
//                "       (select saldofontedespesaorc.dotacao   " +
//                "          from saldofontedespesaorc   " +
//                "         where saldofontedespesaorc.dataSaldo = (select max(saldofontedespesaorc.dataSaldo)  " +
//                "                                                   from saldofontedespesaorc  " +
//                "                                             inner join fontedespesaorc on fontedespesaorc.id = saldofontedespesaorc.fontedespesaorc_id  " +
//                "                                                  where fontedespesaorc.despesaorc_id = 2010960 and saldofontedespesaorc.datasaldo <= '06/11/2011')   " +
//                "          and saldofontedespesaorc.fontedespesaorc_id = fontedespesaorc.id) as atualizado,  " +
//                "       (select saldofontedespesaorc.dotacao   " +
//                "         from saldofontedespesaorc   " +
//                "        where saldofontedespesaorc.dataSaldo = (select min(saldofontedespesaorc.dataSaldo)  " +
//                "                                                 from saldofontedespesaorc  " +
//                "                                           inner join fontedespesaorc on fontedespesaorc.id = saldofontedespesaorc.fontedespesaorc_id  " +
//                "                                                where fontedespesaorc.despesaorc_id = 2010960)  " +
//                "                                                  and saldofontedespesaorc.fontedespesaorc_id = fontedespesaorc.id)  as saldo  " +
//                "      from fontedespesaorc  " +
//                "inner join provisaoppafonte on provisaoppafonte.id = fontedespesaorc.provisaoppafonte_id  " +
//                "inner join conta on conta.id = provisaoppafonte.destinacaoderecursos_id  " +
//                "inner join saldofontedespesaorc on saldofontedespesaorc.fontedespesaorc_id = fontedespesaorc.id  " +
//                "     where fontedespesaorc.despesaorc_id = 2010960                  ");
//        } catch (SQLException ex) {
//            Logger.getLogger(RelatorioSaldosDasContasDespesaControlador.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return conn;
//    }
}
