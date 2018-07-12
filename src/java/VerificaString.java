
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Emmanuel D'Abruzzo
 */
public class VerificaString {

    private static Set<String> resultado = new HashSet<String>();
    private static Pattern padraoNomeDominio;

    private Matcher matcher;
    private static final String PADRAO_DOMINIO
            = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}";
    String motorBuscaEscolhido = "";
    String buscaGoogle = "https://www.google.com/search?q=";
    String buscaBing = "https://www.bing.com/search?q=";
    String buscaGoogleScholar = "https://scholar.google.com.br/scholar?q=";
            
    String quantidadeResultados = null; 
    private static String query = "emmanuel d'abruzzo";

    private static String agenteGoogle = "Mozilla/5.0 (compatible; Googlebot/2.1; "
            + "+http://www.google.com/bot.html)";

    private static String agente = "Mozilla/5.0";

    static {
        padraoNomeDominio = Pattern.compile(PADRAO_DOMINIO);
    }

    public static void main(String[] args) throws Exception {

        VerificaString teste = new VerificaString();
        String textoPesquisado = tratarQuery(query);
        teste.getDadosGoogle(textoPesquisado);

        for (Iterator<String> it = resultado.iterator(); it.hasNext();) {
            String temp = it.next();
            System.out.println(temp);
        }
        System.out.println("RESULTADOS OBTIDOS : " + resultado.size());
    }

    public String getNomeDominio(String url) {

        String domainName = "";
        matcher = padraoNomeDominio.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;

    }

    private void getDadosGoogle(String query) {

        //String request = endereco.concat(query).concat("&num=").concat(quantidadeResultados);
        String request = null;

        if (motorBuscaEscolhido.equals("G")) {

            request = buscaGoogle.concat(query);
            efetuarRequest(request);
            
          }else if (motorBuscaEscolhido.equals("GS")) {

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
            request = buscaGoogle.concat(query);
            efetuarRequest(request);
            
            motorBuscaEscolhido = "B";
            request = buscaBing.concat(query);
            efetuarRequest(request);

        }

    }

    private void efetuarRequest(String request) {

        System.out.println("Enviando request para o seguinte motor de busca..." + request);
        String agenteUsado = agente;

        if (motorBuscaEscolhido.equals("G") || motorBuscaEscolhido.equals("GS") ) {
            agenteUsado = agenteGoogle;
        }

        try {

            // need http protocol, set this as a Google bot agent :)
            Document doc = Jsoup
                    .connect(request)
                    .userAgent(agenteUsado)
                    .timeout(5000).get();

            // get all links
            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String temp = link.attr("href");
                if (temp.startsWith("/url?q=")) {
                    //use regex to get domain name
                    resultado.add(getNomeDominio(temp));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
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

}
