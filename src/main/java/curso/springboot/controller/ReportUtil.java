package curso.springboot.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	// Retorna nosso PDF em byte para dowload no navegador
	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext) throws Exception {

		// Cria a lista de dados para o relatorio com nossa lista de objetos para
		// imprimir
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);

		// Carregar o caminho do arquivo jasper compilado
		String caminhoJasper = servletContext.getRealPath("relatorio") + File.separator + relatorio + ".jasper";

		// Carrega o arquivo jasper passadno os dados
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap(), jrbcds);

		return JasperExportManager.exportReportToPdf(impressoraJasper);

	}
}
