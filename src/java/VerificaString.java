
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
    private static String query = "As diferentes formas de plágio praticadas por plagiários podem ser\n"
            + "detectadas através da implementação de cada um dos métodos de detecção,\n"
            + "citados na seção 2.1. Desta forma cada método poderia gerar um novo trabalho, ou\n"
            + "então seria possível também desenvolver um trabalho que utilizasse várias destas\n"
            + "técnicas. A biblioteca Lucene possui vários recursos que podem ser úteis, por\n"
            + "exemplo, a opção de reduzir as palavras na sua raiz, a utilização";

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
        System.out.println("URL encontrada: ".concat(url));
        String dominio = "";
        matcher = padraoNomeDominio.matcher(url);
        if (matcher.find()) {
            dominio = matcher.group(0).toLowerCase().trim();
        }
        return dominio;

    }

    private void getDadosGoogle(String query) {

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
            Logger.getLogger(VerificaString.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (motorBuscaEscolhido.equals("G")) {
            pegaLinksGoogle(doc);
        }
        if (motorBuscaEscolhido.equals("GS")) {
            pegaLinksGoogleScholar(doc);
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
                    resultado.add(getNomeDominio(href));
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

}
