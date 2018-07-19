
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * Referencias:
 * https://www.programcreek.com/2012/12/how-to-make-a-web-crawler-using-java/
 
 * @author Emmanuel D'Abruzzo
 */
public class BuscaWeb {

    private static Set<String> resultado = new HashSet<String>();
    private static Pattern padraoNomeDominio;

    private Matcher matcher;
    private static final String PADRAO_DOMINIO
            //     = "(https?://[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
            = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
    String motorBuscaEscolhido = "";
    String buscaGoogle = "https://www.google.com/search?q=";
    String buscaBing = "https://www.bing.com/search?q=";
    String buscaGoogleScholar = "https://scholar.google.com.br/scholar?q=";

    String quantidadeResultados = null;

    private static String query;

    //unico agente que devolve a URL limpa
    private static String agenteGoogle = ""
            + "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/61.0.3163.100 Safari/537.36 ";

    private static String agente = "Mozilla/5.0";

    static {
        padraoNomeDominio = Pattern.compile(PADRAO_DOMINIO);
    }

    public static void main(String[] args) throws Exception {

        BuscaWeb teste = new BuscaWeb();
        Documento documento = new Documento();
        UploadDocumentoView docUploaded = new UploadDocumentoView();
        //String caminho = FacesContext.getCurrentInstance().getExternalContext().getRealPath();
        String caminho = System.getProperty("user.dir");
        //String nomeArquivo = docUploaded.getFile().getFileName();
        String nomeArquivo = "Plagio_Testes_PDF.pdf";
        String conteudoPDF = documento.extrairTextoPDF(caminho.concat("/").concat(nomeArquivo));
        System.out.println("Texto pesquisado: ");
        System.out.println("------------------");
        System.out.println(conteudoPDF);
        System.out.println("------------------");
        /*
        String textoPesquisado = tratarQuery(query);
        teste.getDados(textoPesquisado);

        for (Iterator<String> it = resultado.iterator(); it.hasNext();) {
            String temp = it.next();
            System.out.println(temp);
            }
        System.out.println("RESULTADOS OBTIDOS : " + resultado.size());
         */
    }

    
    public String extrairDominio(String url) {

        System.out.println("href completo encontrado - getNomeDominio(): ".concat(url));
        String dominio = "";
        matcher = padraoNomeDominio.matcher(url);
        if (matcher.find()) {
            dominio = matcher.group(0).toLowerCase().trim();
        }
        System.out.println("Domínio extraído: ".concat(dominio));

        return dominio;

    }

    public String extrairUrlGoogle(String href) {

        System.out.println("*********************");
        System.out.println("URL encontrada: ".concat(href));
        href = href.replace("/url?q=http", "§").replace("&sa=", "@").trim().toLowerCase();
        href = href.substring(href.indexOf("§") + 1, href.indexOf("@"));
        String url = "http".concat(href);
        System.out.println("URL extraída: ".concat(url));
        System.out.println("*********************");

        return url;

    }

    private void getDados(String query) {

        //String request = endereco.concat(query).concat("&num=").concat(quantidadeResultados);
        String request = null;

        if (motorBuscaEscolhido.equals("G")) {

            request = buscaGoogle.concat(query);
            efetuarRequest(request);

        } else if (motorBuscaEscolhido.equals("GS")) {

            request = buscaGoogleScholar.concat(query);
            efetuarRequest(request);

        } else if (motorBuscaEscolhido.equals("B")) {
            request = buscaBing.concat(query);
            efetuarRequest(request);

        } else if (motorBuscaEscolhido.isEmpty()) {

            motorBuscaEscolhido = "G";
            request = buscaGoogle.concat(query);
            efetuarRequest(request);

            motorBuscaEscolhido = "GS";
            request = buscaGoogleScholar.concat(query);
            efetuarRequest(request);

            motorBuscaEscolhido = "B";
            request = buscaBing.concat(query);
            efetuarRequest(request);

        }

    }

    private void efetuarRequest(String request) {

        System.out.println("Enviando request para o seguinte motor de busca..." + request);
        String agenteUsado = agente;

        if (motorBuscaEscolhido.equals("G")) {
            agenteUsado = agenteGoogle;
        }

        Document doc = null;
        try {
            doc = Jsoup
                    .connect(request)
                    .userAgent(agenteUsado)
                    .timeout(5000).get();
        } catch (IOException ex) {
            Logger.getLogger(BuscaWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (motorBuscaEscolhido.equals("G")) {
            pegaLinksGoogle(doc);
        }
        if (motorBuscaEscolhido.equals("GS")) {
            pegaLinksGoogleScholar(doc);
        }

        if (motorBuscaEscolhido.equals("B")) {
            pegaLinksBing(doc);
        }

    }

    private static String tratarQuery(String query) {

        query = query.replace("\n", "");
        query = query.replace("{", "");
        query = query.replace("}", "");
        query = query.replace(";", "");
        query = query.replace(">", "");
        query = query.replace("<", "");
        query = query.replace("=", "");
        query = query.replace("\\\\", "");

        return query;
    }

    private void pegaLinksGoogle(Document doc) {

        Elements links = doc.select("a[href]");

        for (Element link : links) {

            String href = link.attr("href");
            if (href.startsWith("/url?q=")) {
                //use regex to get domain name
                if (motorBuscaEscolhido.equals("G")) {
                    resultado.add(extrairDominio(href));
                    resultado.add(extrairUrlGoogle(href));
                } else {
                    resultado.add(href);
                }

            }

        }

    }

    private void pegaLinksGoogleScholar(Document doc) {

        Elements h3 = doc.getElementsByClass("gs_rt");

        for (Element elemento : h3) {

            Elements elementos = elemento.getElementsByTag("a");

            for (Element a : elementos) {

                String href = a.attr("href");
                resultado.add(href);

            }

        }

    }

    private void pegaLinksBing(Document doc) {

        Elements elementos = doc.getElementsByTag("cite");

        for (Element link : elementos) {

            System.out.println("Link Bing : ");
//                resultado.add(link);

        }

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
