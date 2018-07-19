
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
Referencias
https://www.programcreek.com/java-api-examples/?api=org.apache.pdfbox.pdmodel.PDDocumentInformation
Atenção o PDFBox precisa do Apache Commons Logging
https://pdfbox.apache.org/2.0/dependencies.html
http://mail-archives.apache.org/mod_mbox/pdfbox-users/201005.mbox/%3C201005281141.o4SBf0Iu014793@post.webmailer.de%3E
http://commons.apache.org/proper/commons-logging/
http://java-source.net/open-source/pdf-libraries
* 
 * @author Dell E5410
 */
public class Documento {
    
    
    private PDDocumentInformation metaDadosPDF;
    private String textoExtraido;

       
    /*
    Extrai texto do documento PDF
    
    */ 
   public String extrairTextoPDF(String caminhoNomeArquivo) {
    
       PDDocument documentoPDF = null;
       try {
                      
           File arquivoEntrada = new File(caminhoNomeArquivo);
           
           documentoPDF = PDDocument.load(arquivoEntrada);
           metaDadosPDF = documentoPDF.getDocumentInformation();
           PDFTextStripper stripper = new PDFTextStripper();
           textoExtraido = stripper.getText(documentoPDF);
           
       } catch (IOException ex) {
           Logger.getLogger(Documento.class.getName()).log(Level.SEVERE, null, ex);
       }finally{ 
        if (documentoPDF != null) 
            documentoPDF.close();
       }
       return textoExtraido;
    }

    public PDDocumentInformation getMetaDadosPDF() {
        return metaDadosPDF;
    }

    public void setMetaDadosPDF(PDDocumentInformation metaDadosPDF) {
        this.metaDadosPDF = metaDadosPDF;
    }
  
  public String getTextoExtraido() {
        return textoExtraido;
    }

    public void setTextoExtraido(String textoExtraido) {
        this.textoExtraido = textoExtraido;
    }
   
    
}
