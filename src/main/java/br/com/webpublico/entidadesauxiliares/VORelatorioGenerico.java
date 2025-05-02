package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Maps;
import com.javax0.intern.InternPool;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class VORelatorioGenerico implements Serializable {

    private Map<String, Object> campos;
    private Map<String, List<VORelatorioGenerico>> subReports;
    private ExportPDFMonitor exportPDFMonitor;

    public VORelatorioGenerico() {
        subReports = Maps.newHashMap();
        campos = Maps.newHashMap();
    }

    public VORelatorioGenerico(ResultSet resultSet, InternPool pool) throws SQLException {
        this();
        preencherComResultSet(resultSet, pool);
    }

    private void preencherComResultSet(ResultSet resultSet, InternPool pool) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        for (int x = 1; x <= resultSetMetaData.getColumnCount(); x++) {
            campos.put((String) pool.intern(resultSetMetaData.getColumnLabel(x).toUpperCase()), pool.intern(resultSet.getObject(x)));
        }
    }

    public void adicionarSubReport(String nome, List<VORelatorioGenerico> dados) {
        subReports.put(nome.intern(), dados);
    }

    public void setExportPDFMonitor(ExportPDFMonitor exportPDFMonitor) {
        this.exportPDFMonitor = exportPDFMonitor;
    }

    public <T> T getCampo(String campo) {
        if (campos.containsKey(campo)) {
            return (T) campos.get(campo);
        }
        return null;
    }

    public Map<String, Object> getCampos() {
        if (exportPDFMonitor != null) {
            exportPDFMonitor.gerouLinha();
        }
        return campos;
    }

    public Map<String, List<VORelatorioGenerico>> getSubReports() {
        return subReports;
    }
}
