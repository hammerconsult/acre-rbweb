package br.com.webpublico.controlerelatorio;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import br.com.webpublico.relatoriofacade.RelatorioProtocoloFacade;
import br.com.webpublico.util.ScannerEntidades;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author terminal1
 */
@ManagedBean
@SessionScoped
public class RelatorioGenerico extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioGenerico.class);

    @EJB
    RelatorioProtocoloFacade relatorioProtocoloFacade;
    private ScannerEntidades entidades;
    private List<Class> disponiveis;
    private List<Class> selecionados;
    private Class classSelecionada;
    private List<Field> campos;
    private transient Field[] camposSelecionados;

    public RelatorioGenerico() {
        entidades = new ScannerEntidades();
        disponiveis = entidades.getClassesEntidade();
        selecionados = new ArrayList<Class>();
        campos = new ArrayList<Field>();
    }

    public void gerarReport(Class classe, Field[] camp) {

        List dados = relatorioProtocoloFacade.consultaClasses(classe);
        try {
            FastReportBuilder drb = new FastReportBuilder();

            drb.setPrintBackgroundOnOddRows(true);
            drb.setUseFullPageWidth(true);
            drb.setTitle("Relatório de " + classe.getSimpleName());
            drb.setPageSizeAndOrientation(Page.Page_A4_Landscape());
            drb.setPrintColumnNames(true);
            String subTitulo = "Usuario...:" + usuarioLogado().getPessoaFisica().getNome() + "    Data Impressão...: " + new Date().toLocaleString();
            drb.setSubtitle(subTitulo);
            for (Field f : camp) {
                drb.addColumn(f.getName(), f.getName(), String.class.getName(), 70);
            }

            DynamicReport dr = drb.build();

            JRDataSource ds = new JRBeanCollectionDataSource(dados);

            JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), ds);
            gerarRelatorio(jp);

        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        } catch (JRException ex) {
            logger.error("Erro: ", ex);
        } catch (ColumnBuilderException ex) {
            logger.error("Erro: ", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("Erro: ", ex);
        }
    }

    public Class getClassSelecionada() {
        return classSelecionada;
    }

    public void setClassSelecionada(Class classSelecionada) {
        this.classSelecionada = classSelecionada;
    }

    public List<Class> getDisponiveis() {
        return disponiveis;
    }

    public void setDisponiveis(List<Class> disponiveis) {
        this.disponiveis = disponiveis;
    }

    public List<Class> getSelecionados() {
        return selecionados;
    }

    public void setSelecionados(List<Class> selecionados) {
        this.selecionados = selecionados;
    }

    public List<Field> getCampos() {
        return campos;
    }

    public void setCampos(List<Field> campos) {
        this.campos = campos;
    }

    public Field[] getCamposSelecionados() {
        return camposSelecionados;
    }

    public void setCamposSelecionados(Field[] camposSelecionados) {
        this.camposSelecionados = camposSelecionados;
    }

    public void selecionarEntidade() {

        if (classSelecionada != null) {
            camposSelecionados = null;
            campos = new ArrayList<Field>();
            campos = java.util.Arrays.asList(classSelecionada.getDeclaredFields());
            //System.out.println("entrou");
        }
    }


    public void imprimiRealtorio() {
        if (camposSelecionados != null) {
            gerarReport(classSelecionada, camposSelecionados);
        }
    }
}
